/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.server.database;

import java.sql.*;
import java.util.Properties;
import com.toystory.server.type.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap; // Aggiunto per thread-safety

public class DatabaseManager {
    // Usiamo una ConcurrentHashMap per evitare problemi se due Host creano partite nello stesso millisecondo
    private static final Map<String, DatabaseManager> instances = new ConcurrentHashMap<>();
    private Connection conn;

    // Costruttore privato (nessuno fa new DatabaseManager() fuori dalla classe)
    private DatabaseManager(String gameId) throws SQLException {
        String dbPath = "jdbc:h2:./saves/toystory_" + gameId;
        this.conn = DriverManager.getConnection(dbPath, "sa", "");
        initDatabase();
    }

    // Metodo Factory: restituisce il DB specifico per quella partita
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
    private void initDatabase() throws SQLException {
    try (Statement stm = conn.createStatement()) {
        
        // 1. Tabella Flag: mantiene i progressi della trama
        stm.executeUpdate("CREATE TABLE IF NOT EXISTS game_flags (" +
                          "key VARCHAR(255) PRIMARY KEY, " +
                          "value VARCHAR(255))");

        // 2. Tabella Stanze: elenca le stanze esistenti
        stm.executeUpdate("CREATE TABLE IF NOT EXISTS rooms (" +
                          "id INT PRIMARY KEY, " +
                          "name VARCHAR(255), " +
                          "description VARCHAR(1024))");

        // 3. Tabella Oggetti: il "catalogo" di tutti gli oggetti del gioco
        // Definisce l'oggetto, dove si trova, e se è aperto/bloccato
        stm.executeUpdate("CREATE TABLE IF NOT EXISTS game_objects (" +
                          "id INT PRIMARY KEY, " +
                          "name VARCHAR(255), " +
                          "room_id INT, " + // NULL se l'oggetto è in inventario
                          "is_open BOOLEAN DEFAULT FALSE, " +
                          "is_locked BOOLEAN DEFAULT FALSE)");

        // 4. Tabella Inventario: lega il giocatore all'oggetto
        stm.executeUpdate("CREATE TABLE IF NOT EXISTS inventory (" +
                          "character_name VARCHAR(255), " +
                          "item_id INT, " +
                          "PRIMARY KEY (character_name, item_id))");
        }
    }


    /**
    * Verifica se il database è vuoto controllando se la tabella 'rooms' non ha record.
    * @return true se il database è vuoto, false altrimenti.
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

    //popola la tabella rooms
    public void insertRoom(Room room) throws SQLException {
        String sql = "INSERT INTO rooms (id, name, description) VALUES (?, ?, ?)";
        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setInt(1, room.getId());
            pstm.setString(2, room.getName());
            pstm.setString(3, room.getDescription());
            pstm.executeUpdate();
        }
    }

    // Inserisce un oggetto nel database
    public void insertObject(AdvObject obj, int roomId) throws SQLException {
        String sql = "INSERT INTO game_objects (id, name, room_id, is_open, is_locked) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setInt(1, obj.getId());
            pstm.setString(2, obj.getName());
            pstm.setInt(3, roomId);
        
            // Gestiamo lo stato se è un contenitore
            boolean isOpen = (obj instanceof ContainerObject) ? ((ContainerObject) obj).isOpen() : false;
            boolean isLocked = (obj instanceof ContainerObject) ? ((ContainerObject) obj).isLocked() : false;
        
            pstm.setBoolean(4, isOpen);
            pstm.setBoolean(5, isLocked);
            pstm.executeUpdate();
        }
    }

    // Aggiunge un oggetto all'inventario di un personaggio
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

    // Rimuove un oggetto dall'inventario e lo mette in una stanza
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

    // Salva lo stato di un enigma o di un evento nel gioco. 
    // Usa PreparedStatement per mappare i valori in modo sicuro
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
   
    // Interroga il database per recuperare il valore attuale 
    // di un flag tramite una SELECT
    public boolean getFlag(String key) throws SQLException { //Recupera uno stato dal database
        String sql = "SELECT value FROM game_flags WHERE key = ?"; //cercare il valore associato a una specifica chiave
        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, key);
            try (ResultSet rs = pstm.executeQuery()) { //Il risultato viene memorizzato in un ResultSet
                if (rs.next()) {
                   // CORREZIONE: Convertiamo la stringa "true"/"false" del DB in un vero boolean Java
                    return Boolean.parseBoolean(rs.getString("value"));
                }
            }
        }
        return false;
    }

    // Recupera gli oggetti in una stanza (per ricostruire la stanza all'avvio)
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

    // Recupera l'inventario di un giocatore
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

    // Recupera tutti i flag salvati per ripristinare la mappa in GameDescription
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

    // Controlla se un contenitore è aperto
    public boolean isObjectOpen(int objectId) throws SQLException {
        String sql = "SELECT is_open FROM game_objects WHERE id = ?";
        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setInt(1, objectId);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) return rs.getBoolean("is_open");
            }
        }
        return false;
    }

    // Controlla se un contenitore è bloccato a chiave
    public boolean isObjectLocked(int objectId) throws SQLException {
        String sql = "SELECT is_locked FROM game_objects WHERE id = ?";
        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setInt(1, objectId);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) return rs.getBoolean("is_locked");
            }
        }
        return false;
    }

    // Rimuove un oggetto dall'inventario distruggendolo definitivamente (es. chiavi usate)
    public void consumeItem(String characterName, int itemId) throws SQLException {
        String sql = "DELETE FROM inventory WHERE character_name = ? AND item_id = ?";
        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, characterName);
            pstm.setInt(2, itemId);
            pstm.executeUpdate();
        }
        // Lasciando il room_id a NULL nella tabella game_objects, l'oggetto "sparisce" dal mondo di gioco.
    }

    // Aggiorna lo stato di un contenitore (se è aperto o chiuso, bloccato o sbloccato)
    public void updateContainerState(int objectId, boolean isOpen, boolean isLocked) throws SQLException {
        String sql = "UPDATE game_objects SET is_open = ?, is_locked = ? WHERE id = ?";
        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setBoolean(1, isOpen);
            pstm.setBoolean(2, isLocked);
            pstm.setInt(3, objectId);
            pstm.executeUpdate();
        }
    }


   // --- Transazioni ---
    // Sincronizzate per garantire la thread-safety
    public synchronized void startTransaction() throws SQLException { conn.setAutoCommit(false); }
    public synchronized void commitTransaction() throws SQLException { conn.commit(); conn.setAutoCommit(true); }
    public synchronized void rollbackTransaction() throws SQLException { conn.rollback(); conn.setAutoCommit(true); }

    // Chiude il collegamento col database
    public void closeConnection() throws SQLException {
        if (conn != null) conn.close();
    }

    
    // Svuota completamente le tabelle per permettere di iniziare una Nuova Partita
    public void resetGame() throws SQLException {
        try (Statement stm = conn.createStatement()) {
            stm.executeUpdate("DELETE FROM inventory");
            stm.executeUpdate("DELETE FROM game_objects");
            stm.executeUpdate("DELETE FROM rooms");
            stm.executeUpdate("DELETE FROM game_flags");
        }
    }

}