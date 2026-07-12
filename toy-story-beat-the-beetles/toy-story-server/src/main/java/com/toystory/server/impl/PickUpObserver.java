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
import com.toystory.server.type.PickupableObject;
import com.toystory.server.type.Room;

/**
 * Gestore della raccolta degli item (Verbo PRENDI).
 * Controlla il tipo di oggetto, la capienza delle tasche e aggiorna il modello.
 */
public class PickUpObserver implements GameObserver {

    @Override
    public String update(Command command, GameDescription state) {
        // 1. Controllo di competenza: si attiva SOLO per il comando PRENDI
        if (command.getType() != CommandType.PRENDI) {
            return null; 
        }

        String target = command.getTargetName();
        Room currentRoom = state.getCurrentRoom();

        if (target == null || target.isEmpty()) {
            return "TESTO|Cosa vorresti raccogliere?";
        }

        // 2. Cerchiamo l'oggetto nella stanza usando gli Stream e le Lambda
        AdvObject targetObj = currentRoom.getObjects().stream()
                .filter(obj -> obj.getName().equalsIgnoreCase(target))
                .findFirst()
                .orElse(null);

        if (targetObj == null) {
            return "TESTO|Questo oggetto non è presente nella stanza.";
        }

        // 3. CONTROLLO DI SICUREZZA POLIMORFICO (Il tuo codice originale)
        if (!(targetObj instanceof PickupableObject)) {
            return "TESTO|Non puoi raccogliere " + targetObj.getName() + ", è un elemento fisso dello scenario!";
        }

        PickupableObject oggettoRaccoglibile = (PickupableObject) targetObj;

        // 4. CONTROLLO DELLE TASCHE (Max 2 slot - Gestito dal PlayableCharacter attivo)
        if (state.getCurrentPlayer().addTemplateObject(oggettoRaccoglibile)) {
            
            // Rimuoviamo l'oggetto dalla stanza fisica
            currentRoom.getObjects().remove(oggettoRaccoglibile);

            // --- INIZIO AGGIUNTA DATABASE ---
            try {
                // Rimuove l'ID della stanza (NULL) e lo lega al personaggio
                state.getDb().addToInventory(state.getCurrentPlayer().getName(), oggettoRaccoglibile.getId());
            } catch (Exception e) {
                System.err.println("[PickUpObserver] Errore salvataggio DB: " + e.getMessage());
            }
            // --- FINE AGGIUNTA DATABASE ---
            
            // LOGICA SPECIALE DEL TUTORIAL PER NOTIFICARE LA GUI
            // Se prendiamo la chiave o il lazo, mandiamo un comando speciale alla GUI per aggiornare gli slot!
            String risposta = "TESTO|" + state.getCurrentPlayer().getName() + " ha messo " + oggettoRaccoglibile.getName() + " in tasca!";
            
            // Protocollo di rete per l'inventario: es. "INVENTARIO|chiave"
            risposta += "|INVENTARIO|" + oggettoRaccoglibile.getName(); 
            
            return risposta;
        } else {
            return "TESTO|Le tasche di " + state.getCurrentPlayer().getName() + " sono piene! Non puoi portare più di 2 oggetti.";
        }
    }
}