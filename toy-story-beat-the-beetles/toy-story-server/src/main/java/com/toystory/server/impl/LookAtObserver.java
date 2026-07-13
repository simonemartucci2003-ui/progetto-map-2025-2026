package com.toystory.server.impl;

import com.toystory.server.GameDescription;
import com.toystory.server.GameObserver;
import com.toystory.server.type.Command;
import com.toystory.server.type.CommandType;

public class LookAtObserver implements GameObserver {

    @Override
    public String update(Command command, GameDescription state) {
        // 1. Si attiva solo per il comando GUARDA
        if (command.getType() != CommandType.GUARDA) {
            return null;
        }

        String target = command.getTargetName();
        if (target == null) {
            return "TESTO|Cosa vorresti guardare?";
        }

        
       String descrizione;

        switch (target.toLowerCase()) {
            case "libreria":
                descrizione = Dialoghi.getDescrizoneLibreria();
                return "TESTO|" + descrizione;
                
            case "baule":
                boolean bauleAperto = state.getFlags().getOrDefault("BAULE_APERTO", false);
                if (!bauleAperto) {
                    // IL BAULE È CHIUSO: I giocattoli sono intrappolati e parlano.
                    String testoDialogo = Dialoghi.getDescrizoneBauleChiuso(); 
                
                    return "TESTO|" + testoDialogo;
                
                } else {
                    String testoDialogo = Dialoghi.getDescrizoneBauleAperto(); 
                    return "TESTO|" + testoDialogo;
                }
            case "letto":
                // 1. Controlliamo la memoria del gioco: il baule è aperto o chiuso?
                // Se il flag non esiste ancora, getOrDefault restituisce 'false' (baule chiuso).
                bauleAperto = state.getFlags().getOrDefault("BAULE_APERTO", false);

                if (!bauleAperto) {
                    // IL BAULE È CHIUSO: I giocattoli sono intrappolati e parlano.
                    String testoDialogo = Dialoghi.getDescrizoneLettoBauleChiuso(); 
                    // (Se Dialoghi è nello stesso package, togli "com.toystory.server.utils." e lascia solo Dialoghi.get...)
                
                    return "TESTO|" + testoDialogo;
                
                } else {
                    String testoDialogo = Dialoghi.getDescrizoneLettoBauleAperto(); 
                    return "TESTO|" + testoDialogo;
                }
 
                
            case "porta":
                descrizione = "La porta della camera. La maniglia è troppo in alto per un giocattolo...";
                break;
            default:
                descrizione = "Lì non c'è nulla di interessante. ";
                break;
        }

        return "TESTO|" + descrizione;
    }
}