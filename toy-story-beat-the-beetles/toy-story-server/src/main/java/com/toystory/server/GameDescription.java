package com.toystory.server;

import com.toystory.server.type.AdvObject;

import com.toystory.server.type.PlayableCharacter;
import com.toystory.server.type.PickupableObject;
import com.toystory.server.type.Room;
import com.toystory.server.database.DatabaseManager;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe astratta fondamentale che modella i requisiti minimi di una partita.
 * <p>
 * Fornisce le strutture dati principali per la mappa (stanze), i personaggi giocabili,
 * il tracciamento del giocatore attivo e i flag di progressione della trama.
 * Gestisce inoltre la sincronizzazione trasparente dello stato di gioco con il database.
 * </p>
 */
public abstract class GameDescription {
    
    // =========================================================================
    // VARIABILI DI STATO DEL GIOCO
    // =========================================================================

    // 1. La lista che conterrà tutti i personaggi sbloccati/disponibili
    private final List<PlayableCharacter> players = new ArrayList<>();
    
    // 2. Il personaggio attualmente controllato dal giocatore
    private PlayableCharacter currentPlayer;

    // 3. Lista globale di tutte le stanze che compongono la mappa del gioco
    private final List<Room> rooms = new ArrayList<>();

    // 4. Mappa dei flag di progressione: associa un ID/Stringa a un valore booleano
    private final Map<String, Boolean> flags = new HashMap<>();

    // 5. Riferimento alla stanza in cui si trova attualmente il giocatore
    private Room currentRoom;
    
    private DatabaseManager db;


    /**
     * Costruttore base di GameDescription.
     * Inizializza la struttura astratta della partita.
     */
    public GameDescription() {
        // Inizializzazione di base della struttura astratta
    }
    
    /**
     * Imposta il gestore del database associato a questa specifica sessione di gioco.
     * 
     * @param db L'istanza di {@link DatabaseManager} da utilizzare per la persistenza.
     */
    public void setDb(DatabaseManager db) {
        this.db = db;
    }
    
    /**
     * Restituisce il gestore del database attualmente in uso.
     * 
     * @return L'istanza di {@link DatabaseManager}.
     */
    public DatabaseManager getDb() {
        return this.db;
    }
    
    /** 
     * Salva un flag di progressione sia nella memoria locale (Map)
     * che nel database per garantire la persistenza.
     * 
     * @param key L'identificativo univoco del flag (es. "BAULE_APERTO").
     * @param value Il valore booleano da assegnare.
     */
    public void saveFlag(String key, boolean value) {
        this.getFlags().put(key, value);
        try {
            // Sostituito il vecchio getInstance() con il DB di questa sessione
                this.db.saveFlag(key, String.valueOf(value));
        } catch (SQLException e) {
            System.err.println("[GameDescription] Errore nel salvataggio del flag '" + key + "': " + e.getMessage());
        }
    }

    /**
     * Sincronizza il mondo di gioco in memoria con il database.
     * <p>
     * Se il database è vuoto, effettua un popolamento iniziale salvando stanze e oggetti.
     * Se il database contiene già dei dati, ripristina lo stato salvato in precedenza.
     * </p>
     */
    public void syncWorldWithDatabase() {
        if (this.db == null) return; // Sicurezza: evitiamo crash se il DB non è ancora stato impostato
        try {
            if (db.isDatabaseEmpty()) {
                System.out.println("[GameDescription] Database vuoto: popolamento iniziale...");
                 for (Room r : this.rooms) {
                     try {
                        db.insertRoom(r);
                        for (AdvObject obj : r.getObjects()) {
                            db.insertObject(obj, r.getId());
                        }
                     } catch (SQLException e) {
                        System.err.println("[GameDescription] Errore inserimento stanza '" + r.getName() + "': " + e.getMessage());
                        }
                    }
            } else {
                System.out.println("[GameDescription] Caricamento stato da database...");
                loadGameFromDatabase();
            }
        } catch (SQLException e) {
            System.err.println("[GameDescription] Errore critico sincronizzazione: " + e.getMessage());
        }
    }
    
    /**
     * Ripristina integralmente lo stato della partita leggendo i dati dal database,
     * inclusi flag di progressione, posizione degli oggetti nelle stanze e inventari.
     */
    private void loadGameFromDatabase() {
    if (this.db == null) return;

        try {
            // 1. Ripristino dei Flag 
            this.flags.putAll(db.getAllFlags()); 

            for (java.util.Map.Entry<String, Boolean> e : db.getAllFlags().entrySet()) {
                if (!e.getKey().startsWith("ROOM_OF_")) {
                    this.flags.put(e.getKey(), e.getValue());
                }
            }

            // Costruiamo un catalogo di TUTTI gli oggetti creati da init(),
            // PRIMA di svuotare qualunque stanza.
            Map<Integer, AdvObject> catalogoOggetti = new HashMap<>();
            for (Room room : this.rooms) {
                for (AdvObject obj : room.getObjects()) {
                    catalogoOggetti.put(obj.getId(), obj);
                }
            }

            //2. Ripristino delle posizioni degli oggetti
            for (Room room : this.rooms) {
                room.getObjects().clear();
                List<Integer> objectIds = db.getObjectIdsInRoom(room.getId());

                for (Integer id : objectIds) {
                    AdvObject obj = catalogoOggetti.get(id);
                    if (obj != null) {
                        room.addObject(obj);
                    }
                }
            }

            // 3. Ripristino Inventario per TUTTI i personaggi
            for (PlayableCharacter player : this.players) {
                List<Integer> inventoryIds = db.getInventory(player.getName());
                player.getPocket().clear();
                for (Integer id : inventoryIds) {
                    AdvObject obj = catalogoOggetti.get(id);
                    if (obj != null && obj instanceof PickupableObject) {
                        player.getPocket().add((PickupableObject) obj);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("[GameDescription] Errore nel caricamento del mondo: " + e.getMessage());
        }
    }

    /**
     * Cerca un oggetto in memoria basandosi sul suo ID, scansionando sia le stanze
     * che l'inventario del personaggio specificato.
     * 
     * @param id L'identificativo numerico dell'oggetto da cercare.
     * @param attore Il personaggio di cui controllare l'inventario.
     * @return L'oggetto {@link AdvObject} trovato, o null se inesistente.
     */
    protected AdvObject findObjectById(int id, PlayableCharacter attore) {
        for (Room room : this.rooms) {
            for (AdvObject obj : room.getObjects()) {
                if (obj.getId() == id) return obj;
            }
        }
        if (attore != null) {
            for (AdvObject obj : attore.getPocket()) {
                if (obj.getId() == id) return obj;
            }
        }
        return null;
    }
    
    /**
     * Salva l'ID della stanza corrente nel database di gioco.
     * 
     * @param roomId L'ID della stanza in cui si trova attualmente il giocatore.
     */
    private void saveCurrentRoomToDatabase(int roomId) {
        if (this.db != null) {
            try {
                this.db.saveFlag("CURRENT_ROOM_ID", String.valueOf(roomId));
            } catch (SQLException e) {
                System.err.println("[GameDescription] Errore nel salvataggio della stanza corrente: " + e.getMessage());
            }
        }
    }

    /**
     * Metodo astratto che deve essere implementato dalle classi figlie per 
     * costruire concretamente la mappa, gli oggetti e i personaggi del gioco.
     * 
     * @throws Exception In caso di errori durante l'inizializzazione del mondo.
     */
    public abstract void init() throws Exception;

    // GETTER AND SETTER
    /**
     * @return La lista dei personaggi giocabili disponibili.
     */
    public List<PlayableCharacter> getPlayers() {
        return players;
    }
    /**
     * @return Il personaggio attualmente sotto il controllo del giocatore principale.
     */
    public PlayableCharacter getCurrentPlayer() {
        return currentPlayer;
    }
    /**
     * @param currentPlayer Il personaggio da assegnare al controllo del giocatore.
     */
    public void setCurrentPlayer(PlayableCharacter currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
    /**
     * @return La lista globale di tutte le stanze caricate in memoria.
     */
    public List<Room> getRooms() {
        return rooms;
    }
    /**
     * @return La mappa contenente tutti i flag di progressione della storia.
     */
    public Map<String, Boolean> getFlags() {
        return flags;
    }
    /**
     * @return La stanza attualmente esplorata dal giocatore principale.
     */
    public Room getCurrentRoom() {
        return currentRoom;
    }
    
    /**
     * Costruisce il frammento di messaggio con avatar, abilità e inventario 
     * del personaggio attualmente attivo.
     * <p>
     * Questo metodo viene riusato sia in fase di cambio personaggio manuale 
     * sia per la sincronizzazione del client al momento del resume.
     * </p>
     * 
     * @param attore Il personaggio giocabile di cui costruire lo stato.
     * @return La stringa formattata secondo il protocollo di rete, o stringa vuota se l'attore è null.
     */
    public String buildCharacterStatusFragment(PlayableCharacter attore) {
        if (attore == null) return "";

        StringBuilder sb = new StringBuilder();
        String nome = attore.getName();

        sb.append("PERSONAGGIO_ATTIVO|").append(nome);

        if (nome.equalsIgnoreCase("Buzz Lightyear") || nome.equalsIgnoreCase("Buzz")) {
            sb.append("|SWITCH_AVATAR|/avatars/buzz.png|");
            sb.append("ABILITA|Laser|/Laser.png|");
        } else if (nome.equalsIgnoreCase("Woody")) {
            sb.append("|SWITCH_AVATAR|/avatars/woody.png|");
            boolean lazoSbloccato = this.flags.getOrDefault("LAZO_UNLOCKED", false);
            if (lazoSbloccato) {
                sb.append("ABILITA|Lazo|/Lazo.png|");
            } else {
                sb.append("ABILITA|Nessuna|vuoto|");
            }
        } else if (nome.equalsIgnoreCase("Jessie")) {
            sb.append("|SWITCH_AVATAR|/avatars/jessie.png|");
            sb.append("ABILITA|Destrezza|/Destrezza.png|");
        }

        sb.append("CLEAR_INVENTORY|OK");
        if (attore.getPocket() != null) {
            for (com.toystory.server.type.PickupableObject obj : attore.getPocket()) {
                sb.append("|INVENTARIO|").append(obj.getName()).append("|").append(obj.getIcona());
            }
        }
        return sb.toString();
     }
    
    /**
     * Aggiorna la stanza corrente in memoria e innesca il salvataggio sul database.
     * 
     * @param currentRoom La nuova stanza in cui impostare la visuale.
     */
    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
        if (this.currentRoom != null) {
            saveCurrentRoomToDatabase(this.currentRoom.getId());
        }
    }
    
    /**
     * Salva nel database la stanza specifica in cui si trova attualmente un dato personaggio.
     * 
     * @param character Il personaggio giocabile da tracciare.
     * @param room La stanza in cui il personaggio è appena entrato.
     */
    public void saveCharacterRoom(PlayableCharacter character, Room room) {
        if (this.db == null || character == null || room == null) return;
        try {
            this.db.saveFlag("ROOM_OF_" + character.getId(), String.valueOf(room.getId()));
        } catch (SQLException e) {
            System.err.println("[GameDescription] Errore salvataggio stanza per '" + character.getName() + "': " + e.getMessage());
        }
    }
    
    /**
     * Recupera dal database l'ultima stanza in cui è stato registrato un determinato personaggio.
     * 
     * @param character Il personaggio giocabile di cui recuperare la posizione.
     * @return L'oggetto {@link Room} in cui si trovava, o null in caso di fallimento o assenza di dati.
     */
    public Room loadCharacterRoom(PlayableCharacter character) {
        if (this.db == null || character == null) return null;
        try {
            Integer savedRoomId = this.db.getFlagAs("ROOM_OF_" + character.getId(), Integer.class);
            if (savedRoomId != null) {
                for (Room r : this.rooms) {
                    if (r.getId() == savedRoomId) return r;
                }
            }
        } catch (SQLException e) {
            System.err.println("[GameDescription] Errore caricamento stanza per '" + character.getName() + "': " + e.getMessage());
        }
        return null;
    }
}