/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.server;

import com.toystory.server.type.AdvObject;
import com.toystory.server.type.ContainerObject;
import com.toystory.server.type.PlayableCharacter;
import com.toystory.server.type.PickupableObject;
import com.toystory.server.type.Room;
import com.toystory.server.database.DatabaseManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe astratta fondamentale che modella i requisiti minimi di una partita.
 * Fornisce le strutture dati per la mappa (stanze), i personaggi giocabili,
 * il tracciamento del giocatore attivo e i flag di progressione della trama.
 */
public abstract class GameDescription {
    
    // =========================================================================
    // VARIABILI DI STATO DEL GIOCO
    // =========================================================================

    // 1. La lista che conterrà tutti i personaggi sbloccati/disponibili
    private List<PlayableCharacter> players = new ArrayList<>();
    
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
     */
    public GameDescription() {
        // Inizializzazione di base della struttura astratta
    }

    // =========================================================================
    // GESTIONE DATABASE E SINCRONIZZAZIONE
    // =========================================================================

    public void setDb(DatabaseManager db) {
        this.db = db;
    }

    public DatabaseManager getDb() {
        return this.db;
    }
    
    /** 
     * Salva un flag di progressione sia nella memoria locale (Map)
     * che nel database per garantire la persistenza.
     */
    public void saveFlag(String key, boolean value) {
        this.getFlags().put(key, value);
        try {
            // Sostituito il vecchio getInstance() con il DB di questa sessione
                this.db.saveFlag(key, String.valueOf(value));
        } catch (Exception e) {
            System.err.println("[GameDescription] Errore nel salvataggio del flag '" + key + "': " + e.getMessage());
        }
    }

    /**
     * Sincronizza il mondo con il database.
     */
    public void syncWorldWithDatabase() {
        if (this.db == null) return; // Sicurezza: evitiamo crash se il DB non è ancora stato impostato
        try {
            if (db.isDatabaseEmpty()) {
                System.out.println("[GameDescription] Database vuoto: popolamento iniziale...");
                for (Room r : this.rooms) {
                    db.insertRoom(r);
                    for (AdvObject obj : r.getObjects()) {
                        db.insertObject(obj, r.getId());
                    }
                }
            } else {
                System.out.println("[GameDescription] Caricamento stato da database...");
                loadGameFromDatabase();
            }
        } catch (Exception e) {
            System.err.println("[GameDescription] Errore critico sincronizzazione: " + e.getMessage());
        }
    }

    private void loadGameFromDatabase() {
        if (this.db == null) return;
        
        try {
            // 1. Ripristino dei Flag 
            this.flags.putAll(db.getAllFlags()); 

            // 2. Ripristino delle posizioni degli oggetti
            for (Room room : this.rooms) {
                room.getObjects().clear();
                List<Integer> objectIds = db.getObjectIdsInRoom(room.getId());
            
                for (Integer id : objectIds) {
                    AdvObject obj = findObjectById(id); 
                    if (obj != null) {
                        room.addObject(obj);
                        // 3. Ripristino stato contenitori 
                        if (obj instanceof ContainerObject) {
                            ((ContainerObject) obj).setOpen(db.isObjectOpen(id));
                            ((ContainerObject) obj).setLocked(db.isObjectLocked(id));
                        }
                    }
                }
            }

            // 4. Ripristino Inventario Personaggio
            if (this.currentPlayer != null) {
                List<Integer> inventoryIds = db.getInventory(this.currentPlayer.getName());
                this.currentPlayer.getPocket().clear();
                for (Integer id : inventoryIds) {
                    AdvObject obj = findObjectById(id);
                    if (obj != null && obj instanceof PickupableObject) {
                        this.currentPlayer.getPocket().add((PickupableObject) obj);
                    }
                }
            }
        } catch (Exception e) {
                System.err.println("[GameDescription] Errore nel caricamento del mondo: " + e.getMessage());
        }
    }

    /**
     * Cerca un oggetto in memoria basandosi sul suo ID.
     */
    protected AdvObject findObjectById(int id) {
        for (Room room : this.rooms) {
            for (AdvObject obj : room.getObjects()) {
                if (obj.getId() == id) return obj;
            }
        }
        if (currentPlayer != null) {
            for (AdvObject obj : currentPlayer.getPocket()) {
                if (obj.getId() == id) return obj;
            }
        }
        return null;
    }

    private void saveCurrentRoomToDatabase(int roomId) {
        if (this.db != null) {
            try {
                this.db.saveFlag("CURRENT_ROOM_ID", String.valueOf(roomId));
            } catch (Exception e) {
                System.err.println("[GameDescription] Errore nel salvataggio della stanza corrente: " + e.getMessage());
            }
        }
    }

    /**
     * Metodo astratto che deve essere implementato dalle sottoclassi.
     */
    public abstract void init() throws Exception;

    // =========================================================================
    // GETTER AND SETTER 
    // =========================================================================

    public List<PlayableCharacter> getPlayers() {
        return players;
    }

    public PlayableCharacter getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(PlayableCharacter currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public Map<String, Boolean> getFlags() {
        return flags;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
        if (this.currentRoom != null) {
            saveCurrentRoomToDatabase(this.currentRoom.getId());
        }
    }
}