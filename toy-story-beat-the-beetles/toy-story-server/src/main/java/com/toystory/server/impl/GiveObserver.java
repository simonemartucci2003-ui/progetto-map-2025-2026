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
import com.toystory.server.type.PickupableObject;

/**
 * Gestore dello scambio di oggetti con i personaggi (Verbo DAI).
 * Risolve gli enigmi basati sulla consegna di item specifici ai PNG.
 */
public class GiveObserver implements GameObserver {

    @Override
    public String update(Command command, GameDescription state) {
        // 1. Controllo di competenza: si attiva SOLO per il comando DAI
        if (command.getType() != CommandType.DAI) {
            return null;
        }

        String target = command.getTargetName(); // Es. "orecchio A potato"
        Room currentRoom = state.getCurrentRoom();

        if (target == null || target.isEmpty() || !target.toLowerCase().contains(" a ")) {
            return "TESTO|Usa il formato corretto, ad esempio: DAI [oggetto] A [personaggio]";
        }

        // 2. LOGICA NELLA CAMERA DI ANDY (ID = 1)
        if (currentRoom.getId() == 1) {
            
            // Esempio di enigma (estensione narrativa): Consegna dell'orecchio a Mr. Potato
            if (target.toLowerCase().contains("orecchio") && target.toLowerCase().contains("potato")) {
                
                // Controlliamo se l'oggetto "orecchio" è nell'inventario del giocatore
                boolean haOrecchio = state.getCurrentPlayer().getPocket().stream()
                        .anyMatch(obj -> obj.getName().equalsIgnoreCase("orecchio"));
                
                if (!haOrecchio) {
                    return "TESTO|Non hai nessun 'orecchio' nelle tue tasche da poter dare!";
                }
                
                // Se ce l'ha, risolve l'enigma secondario
                PickupableObject orecchio = state.getCurrentPlayer().getPocket().stream()
                        .filter(obj -> obj.getName().equalsIgnoreCase("orecchio"))
                        .findFirst().orElse(null);
                
                state.getCurrentPlayer().removeObject(orecchio); // Rimuove l'oggetto dalle tasche
                state.getFlags().put("POTATO_EAR_RESTORED", true); // Imposta un flag di bonus
                
                return "TESTO|Consegni l'orecchio sinistro a Mr. Potato, che se lo riattacca felice!"
                        + "\nMR. POTATO: 'Ah, finalmente ci sento da entrambi i lati! Grazie Sceriffo, sei un vero amico!'"
                        + "|RIMUOVI_INVENTARIO|orecchio";
            }
        }

        return "TESTO|Non sembra una buona idea, o il personaggio non vuole questo oggetto.";
    }
}