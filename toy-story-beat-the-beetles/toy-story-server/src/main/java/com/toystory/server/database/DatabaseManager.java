package com.toystory.server.database;

import java.sql.*;
import com.toystory.server.type.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap; // Aggiunto per thread-safety


/**
 * Gestore centralizzato per le operazioni di persistenza su database H2.
 * <p>
 * Questa classe implementa un pattern Factory/Singleton thread-safe per gestire
 * istanze separate di database in base all'identificativo della partita (gameId).
 * Fornisce metodi per la gestione del ciclo di vita del database, la persistenza
 * dello stato dei flag di gioco, degli inventari dei personaggi e della posizione
 * degli oggetti nel mondo.
 * </p>
 */
public class DatabaseManager {
    /**
     * Registro globale delle istanze attive, una per ogni partita (gameId).
     * Una {@link ConcurrentHashMap} evita che due partite create nello stesso
     * istante generino istanze duplicate dello stesso database.
     */
    private static final Map<String, DatabaseManager> instances = new ConcurrentHashMap<>();
    
     /** Connessione JDBC dedicata al database H2 di questa singola partita. */
    private final Connection conn;

    /**
     * Costruttore privato: nessuna classe esterna può istanziare direttamente
     * un {@code DatabaseManager}, che deve sempre essere ottenuto tramite
     * {@link #getInstance(String)}.
     *
     * @param gameId identificativo univoco della partita, usato per determinare
     *               il percorso del file fisico del database H2.
     * @throws SQLException se la connessione al database non può essere aperta.
     */
    private DatabaseManager(String gameId) throws SQLException {
        String dbPath = "jdbc:h2:./saves/toystory_" + gameId;
        this.conn = DriverManager.getConnection(dbPath, "sa", "");
        initDatabase();
    }

    /**
     * Metodo Factory: restituisce l'istanza di {@code DatabaseManager} associata
     * a una specifica partita, creandola al primo utilizzo.
     * <p>
     * L'operazione è atomica grazie a {@link ConcurrentHashMap#computeIfAbsent},
     * che garantisce che, anche in presenza di più thread concorrenti (es. due
     * giocatori che creano una partita nello stesso istante), venga creata una
     * sola istanza per ogni {@code gameId}.
     * </p>
     *
     * @param gameId identificativo della partita.
     * @return l'istanza di {@code DatabaseManager} dedicata a quella partita.
     * @throws SQLException se la creazione del database fallisce.
     */
    public static DatabaseManager getInstance(String gameId) throws SQLException {
        // double-checked locking, in una ConcurrentHashMap
        instances.computeIfAbsent(gameId, id -> {
            try {
                return new DatabaseManager(id);
            } catch (SQLException e) {
                throw new RuntimeException("Errore critico creazione DB per " + id, e);
            }
        });
        return instances.get(gameId);
    }
    
    /**
     * Crea, se non già esistenti, le tabelle necessarie alla persistenza dello
     * stato di gioco: {@code game_flags} (flag di progressione della trama),
     * {@code rooms} (catalogo delle stanze), {@code game_objects} (posizione
     * degli oggetti) e {@code inventory} (associazione oggetto-personaggio).
     * Viene invocato una sola volta, dal costruttore.
     *
     * @throws SQLException se una delle istruzioni DDL fallisce.
     */
    private void initDatabase() throws SQLException {
    try (Statement stm = conn.createStatement()) {
        
        // Tabella Flag: mantiene i progressi della trama
        stm.executeUpdate("CREATE TABLE IF NOT EXISTS game_flags (" +
                          "key VARCHAR(255) PRIMARY KEY, " +
                          "value VARCHAR(255))");

        // Tabella Stanze: elenca le stanze esistenti
        stm.executeUpdate("CREATE TABLE IF NOT EXISTS rooms (" +
                          "id INT PRIMARY KEY, " +
                          "name VARCHAR(255), " +
                          "description VARCHAR(1024))");

        // Tabella Oggetti: il "catalogo" di tutti gli oggetti del gioco
        // Definisce l'oggetto, dove si trova
        stm.executeUpdate("CREATE TABLE IF NOT EXISTS game_objects (" +
                          "id INT PRIMARY KEY, " +
                          "name VARCHAR(255), " +
                          "room_id INT)"); // NULL se l'oggetto è in inventario

        // Tabella Inventario: lega il giocatore all'oggetto
        stm.executeUpdate("CREATE TABLE IF NOT EXISTS inventory (" +
                          "character_name VARCHAR(255), " +
                          "item_id INT, " +
                          "PRIMARY KEY (character_name, item_id))");
        }
    }


    /**
     * Verifica se il database è vuoto controllando se la tabella {@code rooms}
     * non ha record. Viene usato all'avvio per decidere se popolare il mondo di
     * gioco da zero oppure ricaricare una partita già esistente.
     *
     * @return {@code true} se il database è vuoto (nessuna stanza salvata),
     *         {@code false} altrimenti.
     * @throws SQLException se la query fallisce.
     */
    public boolean isDatabaseEmpty() throws SQLException {
        String sql = "SELECT COUNT(*) FROM rooms";
        try (Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(sql)) {
            if (rs.next()) {
                // Se il conteggio è 0, il database è vuoto
                return rs.getInt(1) == 0;
            }
        }
        return true; // Per sicurezza, se c'è un errore assumiamo sia vuoto
    }

    /**
     * Inserisce una nuova stanza nel database. Usato in fase di popolamento
     * iniziale del mondo di gioco (database vuoto).
     *
     * @param room la stanza da salvare, con id, nome e descrizione.
     * @throws SQLException se l'inserimento fallisce (es. id duplicato).
     */
    public void insertRoom(Room room) throws SQLException {
        String sql = "INSERT INTO rooms (id, name, description) VALUES (?, ?, ?)";
        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setInt(1, room.getId());
            pstm.setString(2, room.getName());
            pstm.setString(3, room.getDescription());
            pstm.executeUpdate();
        }
    }

    /**
     * Inserisce un oggetto nel catalogo {@code game_objects}, associandolo alla
     * stanza in cui si trova inizialmente. Usato in fase di popolamento
     * iniziale del mondo di gioco (database vuoto).
     *
     * @param obj    l'oggetto di gioco da registrare.
     * @param roomId l'id della stanza in cui l'oggetto viene collocato.
     * @throws SQLException se l'inserimento fallisce (es. id duplicato).
     */
    public void insertObject(AdvObject obj, int roomId) throws SQLException {
        String sql = "INSERT INTO game_objects (id, name, room_id) VALUES (?, ?, ?)";
        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setInt(1, obj.getId());
            pstm.setString(2, obj.getName());
            pstm.setInt(3, roomId);
            pstm.executeUpdate();
        }
    }

    /**
     * Aggiunge un oggetto all'inventario di un personaggio e, contestualmente,
     * lo rimuove dalla stanza in cui si trovava (impostando {@code room_id} a
     * {@code NULL} in {@code game_objects}), così che non compaia più a terra.
     *
     * @param characterName nome del personaggio che raccoglie l'oggetto.
     * @param itemId        id dell'oggetto raccolto.
     * @throws SQLException se una delle due operazioni SQL fallisce.
     */
    public void addToInventory(String characterName, int itemId) throws SQLException {
        String sql = "INSERT INTO inventory (character_name, item_id) VALUES (?, ?)";
        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, characterName);
            pstm.setInt(2, itemId);
            pstm.executeUpdate();
        
            // Importante: rimuoviamo l'oggetto dalla stanza (room_id diventa NULL)
            String sqlUpdateRoom = "UPDATE game_objects SET room_id = NULL WHERE id = ?";
            try (PreparedStatement pstmUpdate = conn.prepareStatement(sqlUpdateRoom)) {
                pstmUpdate.setInt(1, itemId);
                pstmUpdate.executeUpdate();
            }
        }
    }

    // Metodo non utilizzato, lasciato per un futuro ampliamento del gioco 
    public void removeFromInventory(String characterName, int itemId, int roomId) throws SQLException {
    String sql = "DELETE FROM inventory WHERE character_name = ? AND item_id = ?";
    try (PreparedStatement pstm = conn.prepareStatement(sql)) {
        pstm.setString(1, characterName);
        pstm.setInt(2, itemId);
        pstm.executeUpdate();
        
        // Aggiorniamo la posizione dell'oggetto
        String sqlUpdateRoom = "UPDATE game_objects SET room_id = ? WHERE id = ?";
        try (PreparedStatement pstmUpdate = conn.prepareStatement(sqlUpdateRoom)) {
            pstmUpdate.setInt(1, roomId);
            pstmUpdate.setInt(2, itemId);
            pstmUpdate.executeUpdate();
            }
        }
    }

    /**
     * Salva (o aggiorna) lo stato di un flag di progressione della trama,
     * ad esempio l'esito di un enigma o di un evento di gioco.
     * <p>
     * Usa l'istruzione {@code MERGE INTO}, una variante SQL specifica di H2
     * che esegue un "upsert": se la chiave esiste già ne aggiorna il valore,
     * altrimenti inserisce una nuova riga, evitando duplicati.
     * </p>
     *
     * @param key   il nome del flag (es. {@code "BAULE_APERTO"}).
     * @param value il valore da salvare, in forma di stringa.
     * @throws SQLException se l'operazione di upsert fallisce.
     */
    public void saveFlag(String key, String value) throws SQLException {
        // MERGE INTO È una variante SQL specifica di H2 (molto utile) che esegue un 
        // "UPSERT": se la chiave esiste già, aggiorna il valore, 
        // altrimenti inserisce una nuova riga
        //  È perfetto per evitare duplicati in database di gioco
        String sql = "MERGE INTO game_flags (key, value) VALUES (?, ?)";
        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, key);
            pstm.setString(2, value);
            pstm.executeUpdate(); //esegue agiornamento
        }
    }
   

    /**
     * Metodo generico: recupera un flag dal database e lo converte
     * automaticamente nel tipo richiesto ({@link Boolean}, {@link Integer},
     * {@link String}, ...).
     * <p>
     * Il parametro {@code Class<T>} serve a "portare" l'informazione di tipo
     * a runtime, altrimenti persa a causa della cancellazione dei generics
     * (type erasure) di Java.
     * </p>
     *
     * @param <T>  il tipo nel quale convertire il valore letto.
     * @param key  il nome del flag da leggere.
     * @param type la classe del tipo di destinazione (es. {@code Boolean.class}).
     * @return il valore del flag convertito nel tipo {@code T}, oppure
     *         {@code null} se il flag non esiste.
     * @throws SQLException             se la lettura dal database fallisce.
     * @throws IllegalArgumentException se {@code type} non è tra quelli supportati
     *                                   ({@link Boolean}, {@link Integer}, {@link String}).
     */
    public <T> T getFlagAs(String key, Class<T> type) throws SQLException {
        String value = getFlagAsString(key);
        if (value == null) {
            return null;
        }

        if (type == Boolean.class) {
            return type.cast(Boolean.valueOf(value));
        } else if (type == Integer.class) {
            return type.cast(Integer.valueOf(value));
        } else if (type == String.class) {
            return type.cast(value);
        }

        throw new IllegalArgumentException("Tipo non supportato per getFlagAs: " + type.getSimpleName());
    }

    /**
     * Recupera gli id di tutti gli oggetti attualmente collocati in una
     * determinata stanza. Usato per ricostruire lo stato del mondo all'avvio
     * (o alla ripresa) di una partita.
     *
     * @param roomId l'id della stanza da interrogare.
     * @return la lista degli id degli oggetti in quella stanza
     *         (lista vuota se non ce ne sono).
     * @throws SQLException se la query fallisce.
     */
    public List<Integer> getObjectIdsInRoom(int roomId) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT id FROM game_objects WHERE room_id = ?";
        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setInt(1, roomId);
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("id"));
                }
            }
        }
        return ids;
    }

    /**
     * Recupera gli id di tutti gli oggetti attualmente nell'inventario di un
     * personaggio. Usato per ricostruire la "tasca" del personaggio all'avvio
     * (o alla ripresa) di una partita.
     *
     * @param characterName il nome del personaggio da interrogare.
     * @return la lista degli id degli oggetti in possesso del personaggio
     *         (lista vuota se non ne possiede nessuno).
     * @throws SQLException se la query fallisce.
     */
    public List<Integer> getInventory(String characterName) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT item_id FROM inventory WHERE character_name = ?";
        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, characterName);
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                 ids.add(rs.getInt("item_id"));
                }
             }
        }
        return ids;
    }

    /**
     * Recupera tutti i flag di progressione salvati nel database, per
     * ripristinare in memoria la mappa dei flag in {@code GameDescription}.
     *
     * @return una mappa nome-flag → valore booleano, con tutti i flag salvati
     *         (mappa vuota se non ne è stato salvato nessuno).
     * @throws SQLException se la query fallisce.
     */
    public Map<String, Boolean> getAllFlags() throws SQLException {
        Map<String, Boolean> flags = new HashMap<>();
        String sql = "SELECT key, value FROM game_flags";
        try (Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery(sql)) {
            while (rs.next()) {
                flags.put(rs.getString("key"), rs.getBoolean("value"));
            }
        }
        return flags;
    }
    
    /**
     * Recupera il valore grezzo (non convertito) di un singolo flag dal
     * database. È il metodo di base su cui si appoggia {@link #getFlagAs}.
     *
     * @param key il nome del flag da leggere.
     * @return il valore del flag come stringa, oppure {@code null} se il flag
     *         non è mai stato salvato.
     * @throws SQLException se la query fallisce.
     */
    public String getFlagAsString(String key) throws SQLException {
        String sql = "SELECT value FROM game_flags WHERE key = ?";
        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, key);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) return rs.getString("value");
            }
        }
        return null;
    }

    /**
     * Rimuove definitivamente un oggetto dall'inventario di un personaggio,
     * senza rimetterlo in nessuna stanza (a differenza di
     * {@link #removeFromInventory}). Usato per gli oggetti "consumati" durante
     * un enigma (es. una chiave usata per aprire un baule).
     * <p>
     * Lasciando {@code room_id} a {@code NULL} nella tabella
     * {@code game_objects}, l'oggetto sparisce definitivamente dal mondo di
     * gioco.
     * </p>
     *
     * @param characterName il nome del personaggio che consuma l'oggetto.
     * @param itemId         l'id dell'oggetto da consumare.
     * @throws SQLException se l'operazione di cancellazione fallisce.
     */
    public void consumeItem(String characterName, int itemId) throws SQLException {
        String sql = "DELETE FROM inventory WHERE character_name = ? AND item_id = ?";
        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, characterName);
            pstm.setInt(2, itemId);
            pstm.executeUpdate();
        }
        // Lasciando il room_id a NULL nella tabella game_objects, l'oggetto "sparisce" dal mondo di gioco.
    }

   // --- Transazioni ---
    // Sincronizzate per garantire la thread-safety
    
    /**
     * Avvia una transazione esplicita disattivando l'auto-commit sulla
     * connessione. Chiamato da {@code ServerThread} prima di eseguire
     * un'azione di gioco, in modo che le eventuali scritture sul database
     * possano essere confermate o annullate atomicamente.
     *
     * @throws SQLException se la connessione non può passare in modalità
     *                       manuale.
     */
    public synchronized void startTransaction() throws SQLException { conn.setAutoCommit(false); }
    
    /**
     * Conferma (commit) la transazione corrente e ripristina l'auto-commit.
     *
     * @throws SQLException se il commit fallisce.
     */
    public synchronized void commitTransaction() throws SQLException { conn.commit(); conn.setAutoCommit(true); }
    
     /**
     * Annulla (rollback) la transazione corrente e ripristina l'auto-commit.
     * Chiamato quando un'azione di gioco solleva un'eccezione, per non
     * lasciare il database in uno stato incoerente.
     *
     * @throws SQLException se il rollback fallisce.
     */
    public synchronized void rollbackTransaction() throws SQLException { conn.rollback(); conn.setAutoCommit(true); }

    /**
     * Chiude la connessione al database. Invocato allo spegnimento del server
     * (shutdown hook in {@code ServerMain}) per rilasciare correttamente le
     * risorse di ogni partita attiva.
     *
     * @throws SQLException se la chiusura della connessione fallisce.
     */
    public void closeConnection() throws SQLException {
        if (conn != null) conn.close();
    }

    
    // Svuota completamente le tabelle. Non usata, lasciata per aggiornamemti futuri.
    public void resetGame() throws SQLException {
        try (Statement stm = conn.createStatement()) {
            stm.executeUpdate("DELETE FROM inventory");
            stm.executeUpdate("DELETE FROM game_objects");
            stm.executeUpdate("DELETE FROM rooms");
            stm.executeUpdate("DELETE FROM game_flags");
        }
    }

}