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
                state.saveFlag("BAULE_APERTO", true);

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
        
        // 2. LOGICA DELLA PORTA DELLA CUCINA
        if (target.equalsIgnoreCase("porta_cucina")) {
            
            boolean PortaSbloccata = state.getFlags().getOrDefault("PORTA_SBLOCCATA", false);
            // Controlliamo se l'eroe attivo ha la chiave nello zaino
            boolean haLaPallina = attivo.getPocket().stream()
                    .anyMatch(obj -> obj.getName().equalsIgnoreCase("pallina"));

            if (!haLaPallina) {
                // L'eroe non ha la pallina
                return "TESTO|La porta è bloccata da Buster, non hai niente per distrarlo";
            } else {
                // L'eroe ha la pallina! 
                
                // 1. Troviamo l'oggetto "pallina" esatto nell'inventario
                com.toystory.server.type.PickupableObject pallinaUsata = (com.toystory.server.type.PickupableObject) attivo.getPocket().stream()
                        .filter(obj -> obj.getName().equalsIgnoreCase("pallina"))
                        .findFirst()
                        .orElse(null);

                if (pallinaUsata != null) {
                    // 2. RIMOZIONE DALLA MEMORIA
                    attivo.getPocket().remove(pallinaUsata);

                    // 3. RIMOZIONE DAL DATABASE (Non è automatico!)
                    try {
                        // Assicurati di avere un metodo del genere nel tuo gestore DB
                        state.getDb().consumeItem(attivo.getName(), pallinaUsata.getId());
                    } catch (Exception e) {
                        System.err.println("[UseObserver] Errore DB durante rimozione: " + e.getMessage());
                    }
                }
                
                // 4. Impostiamo il flag per il baule aperto
                state.saveFlag("PORTA_SBLOCCATA", true);

                // 5. COSTRUZIONE DELLA RISPOSTA 
                String testoNarrativo = Dialoghi.getDialogoPortaCucinaAperta();
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
        
        //CANCELLO FOGNA
        if (target.equalsIgnoreCase("cancello")) {
            
            boolean CancelloSbloccato = state.getFlags().getOrDefault("CANCELLO_SBLOCCATO", false);
            // Controlliamo se l'eroe attivo ha la chiave nello zaino
            boolean haLaForcina = attivo.getPocket().stream()
                    .anyMatch(obj -> obj.getName().equalsIgnoreCase("forcina"));

            if (!haLaForcina) {
                // L'eroe non ha la forcina
                return "TESTO| Il cancello è bloccato da un pesante lucchetto..ti servirebbe qualcosa per scassinarlo";
            } else {
                // L'eroe ha la forcina! 
                
                // 1. Troviamo l'oggetto "forcina" esatto nell'inventario
                com.toystory.server.type.PickupableObject forcinaUsata = (com.toystory.server.type.PickupableObject) attivo.getPocket().stream()
                        .filter(obj -> obj.getName().equalsIgnoreCase("forcina"))
                        .findFirst()
                        .orElse(null);

                if (forcinaUsata != null) {
                    // 2. RIMOZIONE DALLA MEMORIA
                    attivo.getPocket().remove(forcinaUsata);

                    // 3. RIMOZIONE DAL DATABASE (Non è automatico!)
                    try {
                        // Assicurati di avere un metodo del genere nel tuo gestore DB
                        state.getDb().consumeItem(attivo.getName(), forcinaUsata.getId());
                    } catch (Exception e) {
                        System.err.println("[UseObserver] Errore DB durante rimozione: " + e.getMessage());
                    }
                }
                
                // 4. Impostiamo il flag per il baule aperto
                state.saveFlag("CANCELLO_SBLOCCATO", true);

                // 5. COSTRUZIONE DELLA RISPOSTA 
                String testoNarrativo = Dialoghi.getDialogoCancelloAperto();
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