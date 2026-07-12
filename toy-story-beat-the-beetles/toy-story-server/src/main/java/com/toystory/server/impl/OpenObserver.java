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
import com.toystory.server.type.AdvObject;
import com.toystory.server.type.ContainerObject;
import com.toystory.server.database.DatabaseManager;

/**
 * Gestore dell'azione APRI (CommandType.APRI).
 * Si occupa ESCLUSIVAMENTE di spalancare i contenitori sbloccati.
 */
public class OpenObserver implements GameObserver {

    @Override
    public String update(Command command, GameDescription state) {
        if (command.getType() != CommandType.APRI) {
            return null;
        }

        String target = command.getTargetName();
        Room currentRoom = state.getCurrentRoom();

        if (target == null || target.isEmpty()) {
            return "TESTO|Cosa vorresti tentare di aprire?";
        }

        // LOGICA CAMERA DI ANDY (ID = 1)
        if (currentRoom.getId() == 1) {

            if (target.equalsIgnoreCase("baule")) {
                AdvObject bauleObj = currentRoom.getObjects().stream()
                        .filter(o -> o.getName().equalsIgnoreCase("baule"))
                        .findFirst().orElse(null);

                if (bauleObj instanceof ContainerObject) {
                    ContainerObject baule = (ContainerObject) bauleObj;

                    // CASO 1: Il baule ha ancora il lucchetto
                    if (baule.isLocked()) {
                        return "TESTO|Provi a tirare su il coperchio, ma il pesante lucchetto lo tiene saldamente chiuso."
                                + "\nWOODY: 'È sprangato. Inutile insistere, serve una chiave!'";
                    }

                    // CASO 2: Il baule è stato sbloccato (con la chiave in UseObserver) ma non ancora aperto
                    if (!baule.isOpen()) {
                        baule.setOpen(true);
                        // Usa il nuovo metodo di GameDescription per salvare in RAM e su DB
                        state.saveFlag("CHEST_OPENED", true);
                        
                        try {
                            // Aggiorna la riga del contenitore (is_open = true)
                           state.getDb().updateContainerState(baule.getId(), true, baule.isLocked());
                        } catch (Exception e) {
                            System.err.println("[OpenObserver] Errore salvataggio stato contenitore: " + e.getMessage());
                        }

                        return "TESTO|Afferri il bordo e sollevi il pesante coperchio di legno. Il baule si spalanca!"
                                + "\nREX: 'WOODY! Meno male, stavamo soffocando qui dentro!' "
                                + "\nMR. POTATO: 'Qualcuno ha visto il mio orecchio sinistro? Scommetto che è finito sotto il letto di Andy!'";
                    } else {
                        // CASO 3: È già aperto
                        return "TESTO|Il baule è già completamente aperto e vuoto.";
                    }
                }
            }
            
            if (target.equalsIgnoreCase("porta")) {
                return "TESTO|La porta non si apre semplicemente spingendola. La maniglia è troppo in alto per un giocattolo.";
            }
        }

        return "TESTO|Non puoi aprire questo elemento.";
    }
}