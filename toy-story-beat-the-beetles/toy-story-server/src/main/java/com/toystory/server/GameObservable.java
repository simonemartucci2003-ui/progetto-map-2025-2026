/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.server;

import com.toystory.server.type.Command;
import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta il "Soggetto" (o Publisher) nel Pattern Observer.
 * Mantiene un registro di tutti i GameObserver iscritti e si occupa 
 * di smistare a ognuno di essi i comandi di gioco.
 */
public class GameObservable {

    // La lista dei listener iscritti al motore di gioco
    private final List<GameObserver> observers = new ArrayList<>();

    /**
     * Registra un nuovo osservatore nel sistema (es. LookAtObserver, UseObserver).
     * @param observer Il controller da iscrivere.
     */
    public void addObserver(GameObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * Rimuove un osservatore dal registro.
     * @param observer Il controller da disiscrivere.
     */
    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notifica tutti gli osservatori iscritti che è arrivato un nuovo comando.
     * Il primo osservatore che valida ed elabora il comando restituirà la stringa 
     * di risposta, interrompendo la catena (Catena di Responsabilità combinata).
     * * @param command Il comando da elaborare.
     * @param state Lo stato corrente del gioco.
     * @return La stringa di risposta da inviare al client, o un messaggio di errore standard se nessuno ha gestito il comando.
     */
    public String notifyObservers(Command command, GameDescription state, ClientState client, GameSession session) {
        for (GameObserver observer : observers) {
            // Risveglia l'osservatore
            String response = observer.update(command, state, client, session);
            
            // Se l'osservatore ha preso in carico il comando e ha restituito una risposta valida,
            // la rimandiamo indietro fermando il ciclo.
            if (response != null) {
                return response;
            }
        }
        
        // Se nessun observer si è attivato per questo comando
        return "TESTO|Non succede nulla. Non è un'azione valida in questo momento.";
    }
}