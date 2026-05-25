/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.server;

import com.toystory.server.impl.ToyStoryGame;
import com.toystory.server.type.Command;
import com.toystory.server.type.CommandType;

/**
 * L'Engine è il controllore centrale del gioco sul Server.
 * Estende GameObservable assumendo l'unico compito di fare da "banditore":
 * riceve il comando dalla rete e lo notifica alla lista degli ascoltatori (Observers),
 * disaccoppiando completamente il motore dalla logica dei singoli comandi.
 */
public class Engine extends GameObservable {

    private final ToyStoryGame game;

    /**
     * Costruttore dell'Engine.
     * @param game L'istanza corrente della partita.
     */
    public Engine(ToyStoryGame game) {
        this.game = game;
        // All'avvio del server, qui registreremo tutti gli osservatori specializzati.
        // Esempio:
        // this.addObserver(new com.toystory.server.impl.LookAtObserver());
        // this.addObserver(new com.toystory.server.impl.PickUpObserver());
        
        // ISCRIVIAMO GLI OBSERVER!
        this.addObserver(new com.toystory.server.impl.LookAtObserver());
        this.addObserver(new com.toystory.server.impl.PickUpObserver());
        this.addObserver(new com.toystory.server.impl.UseObserver());
        this.addObserver(new com.toystory.server.impl.CallObserver());
        this.addObserver(new com.toystory.server.impl.OpenObserver());
        this.addObserver(new com.toystory.server.impl.TalkObserver()); 
        this.addObserver(new com.toystory.server.impl.GiveObserver());
    }

    /**
     * Il metodo core dell'Engine. Riceve l'azione e delega l'esecuzione agli Observer iscritti.
     * @param type Il tipo di comando cliccato (es. GUARDA, PRENDI, VAI_A, CHIAMA).
     * @param targetName Il nome dell'oggetto o del varco su cui si è cliccato (può essere null).
     * @return La stringa di risposta elaborata dall'Observer specifico da inviare al Client.
     */
    public String executeAction(CommandType type, String targetName) {
        if (type == null) {
            return "TESTO|Azione non valida.";
        }

        // 1. Incapsuliamo i dati ricevuti in un oggetto Command (come richiesto dal metodo update)
        Command comando = new Command(type, targetName);

        // 2. Notifichiamo gli osservatori. Il primo che riconosce il CommandType 
        // prenderà in carico l'azione, eseguirà la logica e restituirà il testo.
        return this.notifyObservers(comando, game);
    }
}