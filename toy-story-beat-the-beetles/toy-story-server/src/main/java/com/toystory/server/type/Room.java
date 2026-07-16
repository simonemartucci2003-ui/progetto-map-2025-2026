package com.toystory.server.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Modella una stanza o uno scenario all'interno del mondo di gioco (es. Camera di Andy, Cucina, Fogne).
 * <p>
 * Questa classe gestisce la topologia della mappa attraverso un sistema di varchi 
 * "punta e clicca" invece dei tradizionali punti cardinali, e mantiene traccia 
 * degli oggetti fisici attualmente presenti nell'ambiente (visibili sul pavimento).
 * </p>
 */
public class Room {
    /** ID numerico univoco della stanza, essenziale per il salvataggio e la persistenza nel Database. */
    private final int id;
    
   /** Nome della stanza visualizzato nell'interfaccia grafica (es. "La Camera di Andy"). */
    private final String name;
    
    /** Descrizione testuale dell'ambiente circostante. */
    private final String description;
    
    /** 
     * Mappa delle uscite "punta e clicca". 
     * <p>
     * Questa mappa gestisce la connettività logica tra le stanze del gioco. 
     * A differenza dei sistemi MUD (Multi-User Dungeon) classici che usano punti 
     * cardinali (Nord, Sud, ecc.), qui ogni uscita è identificata da un <b>nome logico</b> 
     * (es. "porta", "botola").
     * </p>
     * <p>
     * Questo approccio disaccoppia la logica dal client: il Server non conosce le 
     * coordinate (x, y) dei bottoni definiti nella {@code MappaScenario} del client, 
     * ma riceve solo il comando "VAI" seguito dal nome del varco. Sarà il client 
     * ad occuparsi di mappare il click del mouse su una specifica coordinata 
     * grafica verso il nome logico che il Server utilizzerà per cercare la 
     * stanza di destinazione in questa mappa.
     * </p>
     */
    private final Map<String, Room> exits = new HashMap<>();
    
    /** Lista degli oggetti fisici posati a terra in questa specifica stanza. */
    private final List<AdvObject> objects = new ArrayList<>();

    /**
     * Crea una nuova istanza di stanza.
     * 
     * @param id Identificativo numerico univoco.
     * @param name Nome leggibile della stanza.
     * @param description Testo descrittivo del luogo.
     */
    public Room(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    /** @return L'ID della stanza. */
    public int getId() { return id; }
    
    /** @return Il nome della stanza. */
    public String getName() { return name; }
    
    /** @return La descrizione ambientale della stanza. */
    public String getDescription() { return description; }
    
   /** @return La lista degli oggetti presenti sul pavimento della stanza. */
    public List<AdvObject> getObjects() { return objects; }
    
    /**
     * Aggiunge un oggetto all'elenco di quelli presenti nella stanza.
     * 
     * @param obj L'oggetto {@link AdvObject} da piazzare nell'ambiente.
     */
    public void addObject(AdvObject obj) { this.objects.add(obj); }
    
    /**
     * Rimuove un oggetto dalla stanza in modo sicuro.
     * <p>
     * Utilizza un confronto case-insensitive basato sul nome per garantire la 
     * corretta rimozione, prevenendo errori comuni di mancata corrispondenza testuale.
     * </p>
     * 
     * @param obj L'oggetto {@link AdvObject} da rimuovere (es. perché raccolto).
     */
    public void removeObject(AdvObject obj) { 
        // Rimuove l'oggetto dalla lista se il nome corrisponde esattamente. 
        // Questo evita i bug di mancata cancellazione di Java!
        this.objects.removeIf(o -> o.getName().equalsIgnoreCase(obj.getName())); 
    }

    /**
     * Registra un'uscita cliccabile per questa stanza, configurando i collegamenti della mappa.
     * La chiave viene normalizzata in minuscolo per evitare errori dovuti a discrepanze di battitura.
     * 
     * @param nameVarco Il nome del portale grafico (es. "Porta", "Tombino").
     * @param destination La stanza {@link Room} a cui si accede tramite questo varco.
     */
    public void addExit(String nameVarco, Room destination) {
        exits.put(nameVarco.toLowerCase(), destination);
    }
    
    /**
     * Recupera la stanza di destinazione associata al varco cliccato dall'utente.
     * 
     * @param nameVarco Il nome del varco su cui l'utente ha cliccato nella GUI.
     * @return La {@link Room} di destinazione, oppure null se quel varco non è definito.
     */
    public Room getExit(String nameVarco) {
        return exits.get(nameVarco.toLowerCase());
    }
    
    /**
     * @return L'intera mappa dei collegamenti (uscite) definiti per questa stanza.
     */
    public Map<String, Room> getExits() { return exits; }
    
}
