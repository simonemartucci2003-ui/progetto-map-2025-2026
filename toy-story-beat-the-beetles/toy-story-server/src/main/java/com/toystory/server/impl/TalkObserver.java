/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.server.impl;

import com.toystory.server.GameDescription;
import com.toystory.server.GameObserver;
import com.toystory.server.type.Command;
import com.toystory.server.type.CommandType;
import com.toystory.server.type.Room;

/**
 * Gestore dei dialoghi con i personaggi non giocanti (Verbo PARLA).
 * Fornisce indizi contestuali in base allo stato di avanzamento del gioco.
 */
public class TalkObserver implements GameObserver {

    @Override
    public String update(Command command, GameDescription state) {
        // 1. Controllo di competenza: si attiva SOLO per il comando PARLA
        if (command.getType() != CommandType.PARLA) {
            return null;
        }

        String target = command.getTargetName();
        Room currentRoom = state.getCurrentRoom();

        if (target == null || target.isEmpty()) {
            return "TESTO|Con chi vorresti parlare?";
        }

        // 2. LOGICA NELLA CAMERA DI ANDY (ID = 1)
        if (currentRoom.getId() == 1) {
            
            // Verifichiamo prima di tutto se il baule è stato aperto
            boolean bauleAperto = state.getFlags().getOrDefault("CHEST_OPENED", false);
            if (!bauleAperto) {
                return "TESTO|Non c'è nessuno con cui parlare qui, a parte i muri... o almeno finché non trovi il modo di liberare gli altri.";
            }

            // CASO A: Il giocatore PARLA con REX
            if (target.equalsIgnoreCase("rex")) {
                boolean lazoPreso = state.getCurrentPlayer().getPocket().stream()
                        .anyMatch(obj -> obj.getName().equalsIgnoreCase("lazo"));
                
                if (lazoPreso) {
                    return "TESTO|REX: 'Woody, hai preso il lazo! Sapevo che avresti trovato una soluzione. Ora vedi se riesci ad agganciare la maniglia della porta!'";
                } else {
                    return "TESTO|REX: 'Che paura là dentro, Woody! Meno male che ci hai salvati. Slinky dice che c'è qualcosa sotto il letto, ma ha troppa paura del buio per andare a vedere!'";
                }
            }

            // CASO B: Il giocatore PARLA con MR. POTATO
            if (target.equalsIgnoreCase("potato") || target.equalsIgnoreCase("mr potato")) {
                boolean laserUsato = state.getFlags().getOrDefault("LASER_USED", false);
                
                if (laserUsato) {
                    return "TESTO|MR. POTATO: 'Ah! Il laser spaziale di Buzz ha illuminato il pezzo mancante! Sbrigati a prenderlo, Sceriffo!'";
                } else {
                    return "TESTO|MR. POTATO: 'Mi manca ancora un orecchio, ed è sicuramente finito sotto quel maledetto letto. È troppo buio per cercarlo a tentoni, serve una luce forte!'";
                }
            }
        }

        // Risposta standard se il PNG non è presente o non vuole parlare
        return "TESTO|" + target + " non sembra intenzionato a risponderti al momento.";
    }
}