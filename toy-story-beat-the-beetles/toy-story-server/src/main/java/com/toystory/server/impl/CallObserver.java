package com.toystory.server.impl;

import com.toystory.server.ClientState;
import com.toystory.server.GameDescription;
import com.toystory.server.GameObserver;
import com.toystory.server.GameSession;
import com.toystory.server.type.Command;
import com.toystory.server.type.CommandType;
import com.toystory.server.type.PlayableCharacter;

public class CallObserver implements GameObserver {

    @Override
    public String update(Command command, GameDescription state, ClientState client, GameSession session) {
        if (command.getType() != CommandType.CHIAMA) {
            return null;
        }

        String targetName = command.getTargetName();
        if (targetName == null || targetName.isEmpty()) {
            return "TESTO|Chi vorresti chiamare in azione?";
        }

        PlayableCharacter attuale = client.getCurrentCharacter();
        String nomeAttuale = attuale != null ? attuale.getName() : null;

        if (nomeAttuale != null && nomeAttuale.equalsIgnoreCase(targetName)) {
            return "TESTO|" + targetName.toUpperCase() + " è già sul posto e pronto a ricevere ordini!";
        }

        PlayableCharacter nuovoEroe = null;
        for (PlayableCharacter p : state.getPlayers()) {
            if (p.getName().equalsIgnoreCase(targetName) ||
               (targetName.equalsIgnoreCase("Buzz") && p.getName().equalsIgnoreCase("Buzz Lightyear"))) {
                nuovoEroe = p;
                break;
            }
        }

        if (nuovoEroe == null) {
            return "TESTO|Quel personaggio non fa parte della squadra o non è raggiungibile al momento.";
        }

        if (session == null) {
            // Modalità test locale offline: nessun altro giocatore, switch sempre libero
            client.setCurrentCharacter(nuovoEroe);
        } else {
            boolean ok = session.switchCharacter(nuovoEroe, client);
            if (!ok) {
                return "TESTO|" + nuovoEroe.getName() + " è già controllato da un altro giocatore in questa partita! Scegline un altro.";
            }
        }

        String risposta = "TESTO|" + (nomeAttuale != null ? nomeAttuale : "Il gruppo")
                + " fa un passo indietro. Ora controlli " + nuovoEroe.getName() + "!";

        if (nuovoEroe.getName().equalsIgnoreCase("Buzz Lightyear")) {
            risposta += " 'Verso l'infinito e oltre!'";
        } else if (nuovoEroe.getName().equalsIgnoreCase("Woody")) {
            risposta += " 'C'è un serpente nel mio stivale!'";
        } else if (nuovoEroe.getName().equalsIgnoreCase("Jessie")) {
            risposta += " 'Yee-haw!'";
        }

        // Il personaggio richiamato potrebbe trovarsi in una stanza diversa da dove eri tu:
        // aggiorniamo lo sfondo per riflettere la SUA posizione salvata.
        if (client.getCurrentRoom() != null) {
            String idStanza = client.getCurrentRoom().getName().toUpperCase().replace(" ", "_");
            risposta += "|CAMBIA_SFONDO|" + idStanza;
        }

        risposta += "|" + state.buildCharacterStatusFragment(nuovoEroe);

        return risposta;
    }
}