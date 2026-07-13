package com.toystory.server.impl;

import com.toystory.server.GameDescription;
import com.toystory.server.GameObserver;
import com.toystory.server.type.Command;
import com.toystory.server.type.CommandType;
import com.toystory.server.type.Room;
import com.toystory.server.type.PlayableCharacter;

public class MoveObserver implements GameObserver {

    @Override
    public String update(Command command, GameDescription state) {
        
        // 1. Controllo: si attiva solo per il comando VAI
        if (command.getType() != CommandType.VAI) {
            return null;
        }

        String target = command.getTargetName();
        if (target == null || target.isEmpty()) {
            return "TESTO|Dove vorresti andare?";
        }

        PlayableCharacter attivo = state.getCurrentPlayer();
        Room currentRoom = state.getCurrentRoom();

        // 2. SWITCH CASE: Smista l'azione in base alla destinazione
        switch (target.toLowerCase()) {
            
            case "porta":
                // L'enigma del tutorial con il lazo
                return gestisciPorta(state, attivo, currentRoom);

            // QUI POTRAI AGGIUNGERE ALTRE PORTE O PASSAGGI IN FUTURO!
            // case "finestra":
            //     return gestisciFinestra(state, attivo, currentRoom);
                
            default:
                // Logica di movimento standard per le stanze libere
                return eseguiMovimentoGenerico(target, state, currentRoom);
        }
    }

    /**
     * Metodo privato per gestire l'enigma della porta col Lazo
     */
    private String gestisciPorta(GameDescription state, PlayableCharacter attivo, Room currentRoom) {
        // 1. Controlliamo se la porta è GIÀ stata sbloccata in precedenza
        boolean portaAperta = state.getFlags().getOrDefault("PORTA_APERTA", false);
        
        // Se è già aperta, saltiamo tutto l'enigma e usiamo il movimento normale!
        if (portaAperta) {
            return eseguiMovimentoGenerico("porta", state, currentRoom);
        }
        
        
        boolean lazoSbloccato = state.getFlags().getOrDefault("LAZO_UNLOCKED", false);

        // CASO 1: Nessuno ha il lazo
        if (!lazoSbloccato) {
            String testoDialogo = Dialoghi.getManigliaTroppoInAlto(); 
            return "TESTO|" + testoDialogo;
           
        }

        // CASO 2: Abbiamo il lazo, ma stiamo usando il personaggio sbagliato
        if (!attivo.getName().equalsIgnoreCase("Woody")) {
            
            String testoDialogo = Dialoghi.getPortaNonWoody(); 
            return "TESTO|" + testoDialogo;
            
        }

        // CASO 3: Abbiamo il lazo e stiamo usando Woody (SUCCESSO!)
        Room prossimaStanza = currentRoom.getExit("porta");
        
        if (prossimaStanza != null) {
            state.saveFlag("PORTA_APERTA", true);
            
            // 1. Aggiorniamo la stanza corrente nel Server
            state.setCurrentRoom(prossimaStanza);
            
            // 2. Generiamo l'ID logico in modo automatico (come nell'altro metodo!)
            String idStanza = prossimaStanza.getName().toUpperCase().replace(" ", "_");
            
            String testoDialogo = Dialoghi.getPortaConWoody(); 
            return "TESTO|" + testoDialogo + "|CAMBIA_SFONDO|" + idStanza;
            
        } else {
            return "TESTO|Hai aperto la porta, ma oltre c'è solo un muro nero. (Errore: Stanza di destinazione non configurata nella mappa di Room!)";
        }
    }

    /**
     * Metodo privato per i movimenti standard senza enigmi particolari.
     * Cerca automaticamente se il target cliccato è un'uscita valida per la stanza.
     */
    private String eseguiMovimentoGenerico(String target, GameDescription state, Room currentRoom) {
        // Controlliamo se la stanza attuale ha un'uscita corrispondente a dove abbiamo cliccato
        Room prossimaStanza = currentRoom.getExit(target);
        
        if (prossimaStanza != null) {
            // Spostiamo il giocatore nel Server
            state.setCurrentRoom(prossimaStanza);
            
            // Assumendo che getId() o getName() restituisca stringhe come "STANZA_MOLLY" o "STANZA_ANDY"
            // Se la tua classe Room non ha getId(), puoi usare un formato standardizzato basato sul nome, 
            // ma l'ideale è usare l'ID della stanza se lo hai configurato.
            String idStanza = prossimaStanza.getName().toUpperCase().replace(" ", "_");
            
            return "TESTO|Attraversi il passaggio...<PAUSA>Sei arrivato in: " + prossimaStanza.getName() + ".|CAMBIA_SFONDO|" + idStanza;
        } else {
            return "TESTO|Non sembra esserci un passaggio in quella direzione.";
        }
    }
}