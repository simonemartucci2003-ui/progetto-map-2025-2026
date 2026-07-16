package com.toystory.server.impl;

import com.toystory.server.GameObserver;
import com.toystory.server.GameDescription;
import com.toystory.server.type.Command;
import com.toystory.server.type.CommandType;
import com.toystory.server.ClientState;
import com.toystory.server.GameSession;

/**
 * Osservatore dedicato alla gestione del comando PARLA.
 * <p>
 * Questa classe intercetta i comandi di tipo {@link CommandType#PARLA} e fornisce 
 * la corretta linea di dialogo in base al personaggio o all'entità (target) con 
 * cui il giocatore tenta di comunicare. Supporta sia dialoghi statici (che rimangono 
 * invariati) sia dialoghi dinamici (che evolvono in base alla risoluzione di specifici 
 * enigmi, verificando i flag di stato del gioco).
 * </p>
 */
public class TalkObserver implements GameObserver<String> {

    /**
     * Elabora il comando "PARLA" inviato dal giocatore.
     * 
     * <p>Il metodo esegue le seguenti operazioni:</p>
     * <ul>
     * <li>Verifica che il comando sia di tipo PARLA, altrimenti viene ignorato.</li>
     * <li>Controlla la presenza di un bersaglio valido.</li>
     * <li>Recupera il testo del dialogo corrispondente tramite la classe {@link Dialoghi}.</li>
     * <li>Se l'interlocutore ha un dialogo dipendente dallo stato del gioco (es. Topo, Scarafaggio),
     * delega la costruzione della risposta ai metodi helper privati.</li>
     * </ul>
     *
     * @param command Il comando inviato dal giocatore, contenente il nome del bersaglio.
     * @param state   Lo stato globale della partita (usato per leggere i flag necessari per i dialoghi dinamici).
     * @param client  Lo stato specifico del client che ha richiesto l'azione.
     * @param session La sessione di gioco multiplayer.
     * @return Una stringa formattata contenente il dialogo (es. "TESTO|Ciao!"), 
     *         oppure {@code null} se il comando non è di competenza di questo observer.
     */
    @Override
    public String update(Command command, GameDescription state, ClientState client, GameSession session) {
        // Il TalkObserver si attiva solo per il comando PARLA
        if (command.getType() != CommandType.PARLA) {
            return null;
        }

        String target = command.getTargetName();
        if (target == null || target.isEmpty()) {
            return "TESTO|Con chi vorresti parlare?";
        }

        String testoDialogo;
        // Switch case per gestire i dialoghi in base al target
        switch (target.toLowerCase()) {
            case "bo_peep":
                testoDialogo = Dialoghi.getDialogoBoPeep();
                return "TESTO|" + testoDialogo;
                
            case "scarafaggi":
                testoDialogo = Dialoghi.getDialogoScarafaggi();
                return "TESTO|" + testoDialogo;
            
            case "topo":
                return gestisciDialogoTopo(state);
                
            case "varco":
                return gestisciDialogoScarafaggio(state);
            
            default:
                return "TESTO|Non c'è nessuna risposta da " + target + ".";
        }
    }
    
    /**
     * Metodo privato per gestire il dialogo dinamico con il Topo.
     * <p>
     * Se il generatore della casa del Topo è stato riattivato (flag GENERATORE_ACCESO), 
     * il Topo ringrazierà il giocatore. Altrimenti, reciterà il dialogo iniziale 
     * in cui richiede aiuto per la mancanza di corrente.
     * </p>
     *
     * @param state Lo stato del gioco da cui leggere i flag.
     * @return La stringa di risposta formattata contenente il dialogo corretto.
     */
    private String gestisciDialogoTopo(GameDescription state) {
        // Leggiamo lo stato del generatore
        boolean generatoreAcceso = state.getFlags().getOrDefault("GENERATORE_ACCESO", false);

        if (generatoreAcceso) {
            // DIALOGO POST-GENERATORE (Successo)
            return "TESTO|" + Dialoghi.getDialogoTopoRingraziamento();
        } else {
            // DIALOGO PRE-GENERATORE (Iniziale)
            return "TESTO|" + Dialoghi.getDialogoTopo();
        }
    }
    
    /**
     * Metodo privato per gestire il dialogo dinamico con lo Scarafaggio gigante al varco.
     * <p>
     * Se lo scarafaggio è stato distratto con il cibo (flag MELA_DATA), 
     * restituirà un dialogo (è impegnato a mangiare). 
     * Altrimenti, mostrerà un atteggiamento ostile e impedirà il passaggio.
     * </p>
     *
     * @param state Lo stato del gioco da cui leggere i flag.
     * @return La stringa di risposta formattata contenente il dialogo corretto.
     */
    private String gestisciDialogoScarafaggio(GameDescription state) {
        boolean melaData = state.getFlags().getOrDefault("MELA_DATA", false);

        if (melaData) {
            // DIALOGO POST-MELA (Successo)
            return "TESTO|" + Dialoghi.getDialogoScarafaggioCorotto();
        } else {
            // DIALOGO PRE-MELA (Iniziale)
            return "TESTO|" + Dialoghi.getDialogoScarafaggioRetto();
        }
    }
    
}