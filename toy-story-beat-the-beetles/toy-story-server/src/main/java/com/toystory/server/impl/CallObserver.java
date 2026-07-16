package com.toystory.server.impl;

import com.toystory.server.ClientState;
import com.toystory.server.GameDescription;
import com.toystory.server.GameObserver;
import com.toystory.server.GameSession;
import com.toystory.server.type.Command;
import com.toystory.server.type.CommandType;
import com.toystory.server.type.PlayableCharacter;

/**
 * Osservatore dedicato alla gestione del comando per richiamare o cambiare il personaggio giocante.
 * <p>
 * Questa classe implementa l'interfaccia {@link GameObserver} e intercetta i comandi di tipo 
 * {@link CommandType#CHIAMA}. Gestisce la transizione di un client da un personaggio all'altro,
 * verificando la disponibilità del personaggio richiesto (sia in modalità singola che multiplayer)
 * e formulando la corretta risposta testuale e visiva da inviare al client.
 * </p>
 */
public class CallObserver implements GameObserver<String> {
    
    /**
     * Elabora il comando inviato dal client per prendere il controllo di un nuovo personaggio.
     * 
     * <p>Il metodo esegue i seguenti controlli:</p>
     * <ul>
     * <li>Verifica che il comando sia di tipo CHIAMA, altrimenti viene ignorato.</li>
     * <li>Verifica se il bersaglio è valido, se fa parte della squadra e se non è già il personaggio attuale.</li>
     * <li>Gestisce il passaggio di controllo all'interno della {@link GameSession}, bloccandolo se 
     * il personaggio è già controllato da un altro utente.</li>
     * <li>Genera una stringa di risposta che aggiorna il testo della chat, lo sfondo della stanza 
     * e le statistiche del nuovo personaggio.</li>
     * </ul>
     *
     * @param command Il comando inviato dal giocatore.
     * @param state   L'oggetto che descrive lo stato generale del gioco (es. lista di tutti i personaggi disponibili).
     * @param client  Lo stato specifico del client che ha effettuato la richiesta.
     * @param session La sessione di gioco multiplayer attuale.
     * @return Una stringa formattata contenente i dati di risposta da inviare al client, 
     *         oppure {@code null} se il comando non è di competenza di questo observer.
     */
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