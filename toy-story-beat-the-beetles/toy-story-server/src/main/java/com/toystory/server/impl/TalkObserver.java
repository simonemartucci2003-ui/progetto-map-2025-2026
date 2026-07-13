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
            
            
            default:
                return "TESTO|Non c'è nessuna risposta da " + target + ".";
        }
    }
}