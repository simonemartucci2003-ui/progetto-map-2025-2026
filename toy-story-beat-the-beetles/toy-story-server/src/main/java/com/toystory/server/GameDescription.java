/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.server;

import com.toystory.server.type.Room;
import com.toystory.server.type.PlayableCharacter;
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

    // Lista globale di tutte le stanze che compongono la mappa del gioco
    private final List<Room> rooms = new ArrayList<>();

    // Mappa dei flag di progressione: associa un ID/Stringa a un valore booleano
    // Fondamentale per salvare gli stati degli enigmi (es. "CHEST_OPENED" -> true/false)
    private final Map<String, Boolean> flags = new HashMap<>();

    // Riferimento alla stanza in cui si trova attualmente il giocatore
    private Room currentRoom;

    // Riferimento al personaggio (giocattolo) correntemente controllato dall'utente
    private PlayableCharacter currentPlayer;

    /**
     * Costruttore base di GameDescription.
     */
    public GameDescription() {
        // Inizializzazione di base della struttura astratta
    }

    /** AGGIUNTA DATABASE
    * Salva un flag di progressione sia nella memoria locale (Map)
    * che nel database per garantire la persistenza.
    */
    public void saveFlag(String key, boolean value) {
        // 1. Aggiorniamo la mappa in memoria (per l'uso immediato durante il gioco)
        this.getFlags().put(key, value);
    
        // 2. Salviamo nel Database (per la persistenza permanente)
        try {
            // Convertiamo il booleano in stringa prima di mandarlo al DB
            DatabaseManager.getInstance().saveFlag(key, String.valueOf(value));
        } catch (Exception e) {
            System.err.println("[GameDescription] Errore nel salvataggio del flag '" + key + "': " + e.getMessage());
        }
    }
    /**
     * Sincronizza il mondo con il database:
     * - Se vuoto, lo popola con la configurazione attuale (RAM).
     * - Se pieno, carica lo stato salvato nel database (RAM).
     */
    public void syncWorldWithDatabase() {
        try {
            com.toystory.server.database.DatabaseManager db = com.toystory.server.database.DatabaseManager.getInstance();
            
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
        try {
            com.toystory.server.database.DatabaseManager db = com.toystory.server.database.DatabaseManager.getInstance();

            // 1. Ripristino dei Flag (i progressi narrativi)
            // Recuperiamo tutti i flag dal DB e aggiorniamo la nostra mappa
            this.flags.putAll(db.getAllFlags()); // Assicurati di avere questo metodo nel DBManager

            // 2. Ripristino delle posizioni degli oggetti
            // Iteriamo su tutte le stanze che abbiamo in memoria
            for (Room room : this.rooms) {
                // Puliamo la lista corrente per evitare duplicati
                room.getObjects().clear();
            
                // Chiediamo al DB quali ID sono in questa stanza
                List<Integer> objectIds = db.getObjectIdsInRoom(room.getId());
            
                for (Integer id : objectIds) {
                    // Ricostruiamo l'oggetto (assumendo tu abbia un metodo di ricerca)
                    AdvObject obj = findObjectById(id); 
                    if (obj != null) {
                        room.addObject(obj);
                        // 3. Ripristino stato contenitori (aperto/chiuso)
                        if (obj instanceof ContainerObject) {
                            ((ContainerObject) obj).setOpen(db.isObjectOpen(id));
                            ((ContainerObject) obj).setLocked(db.isObjectLocked(id));
                        }
                    }
                }
            }

            // 4. Ripristino Inventario Personaggio
            List<Integer> inventoryIds = db.getInventory(this.currentPlayer.getName());
            this.currentPlayer.getPocket().clear();
            for (Integer id : inventoryIds) {
                AdvObject obj = findObjectById(id);
                if (obj != null && obj instanceof com.toystory.server.type.PickupableObject) {
                    this.currentPlayer.getPocket().add((com.toystory.server.type.PickupableObject) obj);
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
        // Cerca nelle stanze
        for (Room room : this.rooms) {
            for (AdvObject obj : room.getObjects()) {
                if (obj.getId() == id) return obj;
            }
        }
        // Cerca nell'inventario del giocatore
        if (currentPlayer != null) {
            for (AdvObject obj : currentPlayer.getPocket()) {
                if (obj.getId() == id) return obj;
            }
        }
        return null;
    }

    /**
     * Salva l'ID della stanza corrente nel database per permettere
     * al giocatore di riprendere la partita esattamente da dove l'ha lasciata.
     */
    private void saveCurrentRoomToDatabase(int roomId) {
        try {
            // Usa il DatabaseManager per salvare l'ID convertito in stringa nel Key-Value store
            com.toystory.server.database.DatabaseManager.getInstance().saveFlag("CURRENT_ROOM_ID", String.valueOf(roomId));
        } catch (Exception e) {
            System.err.println("[GameDescription] Errore nel salvataggio della stanza corrente: " + e.getMessage());
        }
    }



    /**
     * Metodo astratto che deve essere implementato dalle sottoclassi (es. ToyStoryGame).
     * Si occupa di istanziare concretamente stanze, oggetti, collegamenti e trama.
     * * @throws Exception Se si verificano errori nel caricamento dei dati o dei file.
     */
    public abstract void init() throws Exception;

    // =========================================================================
    // GETTER AND SETTER (Metodi di accesso per il motore di gioco e gli Observer)
    // =========================================================================

    public List<Room> getRooms() {
        return rooms;
    }

    public Map<String, Boolean> getFlags() {
        return flags;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    /**
     * Imposta la nuova stanza in memoria e aggiorna automaticamente il database.
     */
    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom; //[cite: 13]
        
        // Sincronizzazione automatica della posizione
        if (this.currentRoom != null) {
            saveCurrentRoomToDatabase(this.currentRoom.getId());
        }
    }

    /*quando in futuro aggiungerai un MoveObserver per permettere 
    ai personaggi di camminare tra le stanze, 
    ti basterà fargli chiamare state.setCurrentRoom(nuovaStanza). 
    Il gioco aggiornerà la RAM e il database in un colpo solo 
    in modo del tutto invisibile e automatico.*/

    public PlayableCharacter getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(PlayableCharacter currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
}