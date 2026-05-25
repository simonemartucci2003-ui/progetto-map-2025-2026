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

    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }

    public PlayableCharacter getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(PlayableCharacter currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
}