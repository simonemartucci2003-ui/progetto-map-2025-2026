package com.toystory.server.impl;

import com.toystory.server.GameObserver;
import com.toystory.server.GameDescription;
import com.toystory.server.type.Command;
import com.toystory.server.type.CommandType;

public class TalkObserver implements GameObserver {

    @Override
    public String update(Command command, GameDescription state) {
        // 1. Il TalkObserver si attiva solo per il comando PARLA
        if (command.getType() != CommandType.PARLA) {
            return null;
        }

        String target = command.getTargetName();
        if (target == null || target.isEmpty()) {
            return "TESTO|Con chi vorresti parlare?";
        }

        String testoDialogo;
        // 2. Switch case per gestire i dialoghi in base al target
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
     * Metodo privato per gestire il dialogo dinamico con il Topo.
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