/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.server.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Modella una stanza o scenario del gioco (es. Camera di Andy, Cucina, Fogne).
 * Non usa più i punti cardinali (Nord/Sud), ma gestisce le uscite tramite una mappa di varchi cliccabili
 * e tiene traccia degli oggetti posati sul pavimento.
 */
public class Room {
    /** ID numerico univoco della stanza (utile per la persistenza nel Database). */
    private final int id;
    /** Nome della stanza da mostrare nell'interfaccia grafica (es. "La Camera di Andy"). */
    private final String name;
    /** Descrizione testuale dell'ambiente circostante. */
    private final String description;
    
    /** * Mappa delle uscite "Punta e Clicca". 
     * Associa il nome di un varco grafico (chiave, es: "porta", "botola") alla Stanza di destinazione (valore).
     */
    private final Map<String, Room> exits = new HashMap<>();
    
    /** Lista degli oggetti fisici posati a terra in questa specifica stanza e visibili al giocatore. */
    private final List<AdvObject> objects = new ArrayList<>();

    /**
     * Costruttore per istanziare una nuova stanza di gioco.
     * @param id Identificativo numerico.
     * @param name Nome della stanza.
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
     * Aggiunge un oggetto nella stanza (es. a inizio gioco o se un personaggio lo posa a terra).
     * @param obj L'oggetto da piazzare nella stanza.
     */
    public void addObject(AdvObject obj) { this.objects.add(obj); }
    /**
     * Rimuove un oggetto dalla stanza (es. quando un personaggio lo raccoglie).
     * @param obj L'oggetto da rimuovere dal pavimento.
     */
    public void removeObject(AdvObject obj) { this.objects.remove(obj); }

    /**
     * Registra un'uscita cliccabile per questa stanza (Configurazione della mappa).
     * Trasforma la chiave in minuscolo (.toLowerCase()) per evitare bug di battitura nel backend.
     * @param nameVarco Il nome del portale grafico (es. "Porta", "Tombino").
     * @param destination La stanza a cui si accede attraversando quel varco.
     */
    public void addExit(String nameVarco, Room destination) {
        exits.put(nameVarco.toLowerCase(), destination);
    }
    /**
     * Restituisce la stanza di destinazione associata al varco cliccato dall'utente.
     * @param nameVarco Il nome del varco su cui l'utente ha cliccato nella GUI.
     * @return L'oggetto Room di destinazione, oppure null se quel varco non esiste.
     */
    public Room getExit(String nameVarco) {
        return exits.get(nameVarco.toLowerCase());
    }
    
    /**
     * Restituisce l'intera mappa delle uscite della stanza.
     * Utile al Client per sapere quali bottoni di transizione deve generare a schermo.
     * @return La mappa completa dei collegamenti.
     */
    public Map<String, Room> getExits() { return exits; }
    
}
