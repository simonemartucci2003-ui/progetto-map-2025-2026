/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.server.impl;

import com.toystory.server.GameDescription;
import com.toystory.server.GameObserver;
import com.toystory.server.type.Command;
import com.toystory.server.type.CommandType;
import com.toystory.server.type.AdvObject;
import com.toystory.server.type.Room;

/**
 * Gestore dell'ispezione visiva di stanze e oggetti (Verbo GUARDA).
 * Se l'azione non è di sua competenza, restituisce null per passare il testimone.
 */
public class LookAtObserver implements GameObserver {

    @Override
    public String update(Command command, GameDescription state) {
        // 1. Controllo di competenza: questo observer si sveglia SOLO per il comando GUARDA
        if (command.getType() != CommandType.GUARDA) {
            return null; // Passa al prossimo observer nella catena
        }

        String target = command.getTargetName();
        Room currentRoom = state.getCurrentRoom();

        // 2. CASO GENERALE: Il giocatore clicca su "GUARDA" senza selezionare un oggetto specifico
        if (target == null || target.isEmpty() || target.equalsIgnoreCase("stanza")) {
            return "TESTO|" + currentRoom.getDescription();
        }

        // 3. LOGICA SPECIALE DEL TUTORIAL: Camera di Andy (ID Stanza = 1)
        if (currentRoom.getId() == 1) {
            
            if (target.equalsIgnoreCase("libreria")) {
                // Se la chiave è ancora nella stanza, significa che non è stata presa
                boolean haChiave = currentRoom.getObjects().stream().anyMatch(o -> o.getName().equalsIgnoreCase("chiave"));
                if (haChiave) {
                    return "TESTO|Una libreria in legno piena di fumetti. Noti un riflesso metallico luccicare proprio dietro la copertina di un vecchio libro di cowboy!";
                } else {
                    return "TESTO|Una libreria in legno piena di fumetti. Il posto in cui si trovava la chiave ora è vuoto.";
                }
            }

            if (target.equalsIgnoreCase("baule")) {
                // Recuperiamo lo stato del baule dai flag di progressione
                boolean giaAperto = state.getFlags().getOrDefault("CHEST_OPENED", false);
                if (giaAperto) {
                    return "TESTO|Il baule dei giocattoli è spalancato. Dentro è vuoto, a parte Rex e Mr. Potato che imprecano.";
                } else {
                    return "TESTO|Il baule è bloccato da un pesante lucchetto a scatto dall'esterno. È troppo stretto per le tue dita di stoffa di Woody. Serve una chiave.";
                }
            }

            if (target.equalsIgnoreCase("letto")) {
                boolean laserUsato = state.getFlags().getOrDefault("LASER_USED", false);
                if (laserUsato) {
                    return "TESTO|Grazie alla luce del laser di Buzz, vedi chiaramente un lazo impigliato vicino a una vecchia scarpa da ginnastica sotto il letto!";
                } else {
                    return "TESTO|Sotto il letto è davvero troppo buio. Slinky Dog dice di sentire l'odore di qualcosa di utile, ma non si vede a un palmo dal naso. Serve una forte fonte di luce.";
                }
            }
        }

        // 4. COMPORTAMENTO STANDARD (Il tuo codice originale con Stream + Lambda)
        // Se non siamo nel tutorial o l'oggetto non ha una logica speciale, cerca la descrizione base nel Model
        return currentRoom.getObjects().stream()
                .filter(obj -> obj.getName().equalsIgnoreCase(target))
                .map(obj -> "TESTO|" + obj.getDescription()) // Prepariamo il flag per il Client
                .findFirst()
                .orElse("TESTO|Non vedi quell'oggetto qui intorno.");
    }
}