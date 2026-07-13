package com.toystory.server.impl;

import com.toystory.server.GameDescription;
import com.toystory.server.GameObserver;
import com.toystory.server.type.Command;
import com.toystory.server.type.CommandType;
import com.toystory.server.type.PlayableCharacter;


public class UseObserver implements GameObserver {

    @Override
    public String update(Command command, GameDescription state) {
        // 1. Controllo: si attiva solo per il comando USA
        if (command.getType() != CommandType.USA) {
            return null;
        }

        String target = command.getTargetName();
        PlayableCharacter attivo = state.getCurrentPlayer();

        if (target == null || target.isEmpty()) {
            return "TESTO|Cosa vorresti usare?";
        }

        // 2. LOGICA DEL BAULE
        if (target.equalsIgnoreCase("baule")) {
            
            // Usiamo i Flags per ricordarci se il baule è già stato aperto
            boolean bauleAperto = state.getFlags().getOrDefault("BAULE_APERTO", false);
            if (bauleAperto) {
                return "TESTO|Il baule è già aperto. Hai già preso ciò che c'era dentro.";
            }

            // Controlliamo se l'eroe attivo ha la chiave nello zaino
            boolean haLaChiave = attivo.getPocket().stream()
                    .anyMatch(obj -> obj.getName().equalsIgnoreCase("chiave"));

            if (!haLaChiave) {
                // L'eroe non ha la chiave
                return "TESTO|Il baule è chiuso a chiave. Sembra servire una piccola chiave dorata per aprirlo.";
            } else {
                // L'eroe ha la chiave! 
                
                // 1. Troviamo l'oggetto "chiave" esatto nell'inventario
                com.toystory.server.type.PickupableObject chiaveUsata = (com.toystory.server.type.PickupableObject) attivo.getPocket().stream()
                        .filter(obj -> obj.getName().equalsIgnoreCase("chiave"))
                        .findFirst()
                        .orElse(null);

                if (chiaveUsata != null) {
                    // 2. RIMOZIONE DALLA MEMORIA
                    attivo.getPocket().remove(chiaveUsata);

                    // 3. RIMOZIONE DAL DATABASE (Non è automatico!)
                    try {
                        // Assicurati di avere un metodo del genere nel tuo gestore DB
                        state.getDb().consumeItem(attivo.getName(), chiaveUsata.getId());
                    } catch (Exception e) {
                        System.err.println("[UseObserver] Errore DB durante rimozione: " + e.getMessage());
                    }
                }

                // 4. Impostiamo il flag per il baule aperto
                state.getFlags().put("BAULE_APERTO", true);

                // 5. COSTRUZIONE DELLA RISPOSTA 
                String testoNarrativo = Dialoghi.getDialogoBauleAperto();
                String risposta = "TESTO|" + testoNarrativo;
                
                // Diciamo al client di pulire la grafica...
                risposta += "|CLEAR_INVENTORY|OK";
                
                // ...e rimandiamo solo gli oggetti RIMASTI in tasca (se ce ne sono)
                if (attivo.getPocket() != null && !attivo.getPocket().isEmpty()) {
                    for (com.toystory.server.type.PickupableObject obj : attivo.getPocket()) {
                        risposta += "|INVENTARIO|" + obj.getName() + "|" + obj.getIcona();
                    }
                }

                return risposta;
            
            }
        }

        // Se l'utente clicca USA su qualcos'altro che non abbiamo programmato
        return "TESTO|Non succede nulla. Forse non è il modo giusto di usarlo.";
    }
}