/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.server;

import com.toystory.server.impl.ToyStoryGame;
import com.toystory.server.type.CommandType;
import com.toystory.server.type.Room;
import com.toystory.server.type.AdvObject;
import com.toystory.server.type.PickupableObject;
import com.toystory.server.type.ContainerObject; 
import java.util.stream.Collectors;               
/**
 * L'Engine è il controllore centrale del gioco sul Server.
 * Riceve i comandi inviati dai click del Client, interroga lo stato del gioco (ToyStoryGame)
 * ed esegue materialmente le azioni, restituendo la risposta testuale per l'utente.
 */
public class Engine {

    private final ToyStoryGame game;

    /**
     * Costruttore dell'Engine.
     * @param game L'istanza corrente della partita.
     */
    public Engine(ToyStoryGame game) {
        this.game = game;
    }

    /**
     * Il metodo core dell'Engine. Riceve l'azione della GUI e la mappa sullo stato del gioco.
     * @param type Il tipo di comando cliccato (es. GUARDA, PRENDI, VAI_A, CHIAMA).
     * @param targetName Il nome dell'oggetto o del varco su cui si è cliccato (può essere null).
     * @return La stringa di risposta da inviare al Client per aggiornare l'interfaccia.
     */
    public String executeAction(CommandType type, String targetName) {
        if (type == null) {
            return "Azione non valida.";
        }

        switch (type) {
            case GUARDA:
                return handleLook(targetName);
                
            case PRENDI:
                return handlePickUp(targetName);
                
            case CHIAMA:
                return handleSwitchCharacter(targetName);
                
            case VAI_A:
                return handleMove(targetName);

            default:
                return "Azione non ancora implementata!";
        }
    }

    /** Gestisce il click sul tasto GUARDA con le Lambda Expression */
    private String handleLook(String targetName) {
        if (targetName == null || targetName.isEmpty()) {
            return game.getCurrentRoom().getDescription();
        }
        
        // Uso degli Stream + Lambda per cercare l'oggetto nella stanza
        return game.getCurrentRoom().getObjects().stream()
                .filter(obj -> obj.getName().equalsIgnoreCase(targetName))
                .map(AdvObject::getDescription) // Espressione Lambda contratta (Method Reference)
                .findFirst()
                .orElse("Non vedi quell'oggetto qui intorno.");
    }

    /** Gestisce il click sul tasto PRENDI con controllo del tipo ed ereditarietà */
    private String handlePickUp(String targetName) {
        if (targetName == null || targetName.isEmpty()) {
            return "Cosa vorresti raccogliere?";
        }

        // Cerchiamo l'oggetto nella stanza usando le Lambda
        AdvObject targetObj = game.getCurrentRoom().getObjects().stream()
                .filter(obj -> obj.getName().equalsIgnoreCase(targetName))
                .findFirst()
                .orElse(null);

        if (targetObj == null) {
            return "Questo oggetto non è presente nella stanza.";
        }

        // CONTROLLO DI SICUREZZA CON INSTANCEOF (Polimorfismo/Ereditarietà)
        // Se il giocatore prova a prendere il Baule, il gioco lo blocca perché non è un PickupableObject!
        if (!(targetObj instanceof PickupableObject)) {
            return "Non puoi raccogliere " + targetObj.getName() + ", è troppo pesante o fisso!";
        }

        PickupableObject oggettoRaccoglibile = (PickupableObject) targetObj;

        // Controllo delle tasche (Max 2 oggetti)
        if (game.getActiveCharacter().addTemplateObject(oggettoRaccoglibile)) {
            game.getCurrentRoom().removeObject(oggettoRaccoglibile);
            return game.getActiveCharacter().getName() + " ha messo " + oggettoRaccoglibile.getName() + " in tasca!";
        } else {
            return "Le tasche di " + game.getActiveCharacter().getName() + " sono piene!";
        }
    }

    /** Gestisce il cambio personaggio (Click su CHIAMA) */
    private String handleSwitchCharacter(String targetName) {
        if (targetName == null || targetName.isEmpty()) {
            return "Chi vuoi chiamare?";
        }
        
        String vecchioEroe = game.getActiveCharacter().getName();
        if (vecchioEroe.equalsIgnoreCase(targetName)) {
            return targetName.toUpperCase() + " è già in azione!";
        }
        
        game.switchCharacter(targetName);
        
        // Verifica se il cambio è andato a buon fine
        if (game.getActiveCharacter().getName().equalsIgnoreCase(targetName)) {
            return vecchioEroe + " si fa da parte. Ora stai controllando " + game.getActiveCharacter().getName() + "!";
        }
        return "Quel personaggio non è disponibile al momento.";
    }

    /** Gestisce lo spostamento (Click su un varco tipo la Porta) */
    private String handleMove(String targetName) {
        if (targetName == null || targetName.isEmpty()) {
            return "Dove vuoi andare?";
        }

        // Controllo speciale per il tutorial: la porta della camera è bloccata se non si usa l'ingegno!
        if (game.getCurrentRoom().getId() == 1 && targetName.equalsIgnoreCase("Porta")) {
            return "Provi a spingere la porta, ma la maniglia è troppo in alto per te. Serve un modo per agganciarla e fare leva con molto peso!";
        }

        Room successiva = game.getCurrentRoom().getExit(targetName);
        if (successiva != null) {
            game.setCurrentRoom(successiva);
            return "Ti sposti in: " + successiva.getName() + ".\n" + successiva.getDescription();
        }
        return "Non puoi andare in quella direzione.";
    }
}
