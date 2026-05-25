/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 *//*
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
import com.toystory.server.type.ContainerObject;
import com.toystory.server.type.AdvObject;
import com.toystory.server.type.Ability;

/**
 * Risolutore degli enigmi combinati (Verbo USA).
 * Gestisce l'interazione tra oggetti dell'inventario e lo scenario,
 * o l'attivazione delle abilità speciali (Laser, Lazo).
 */
public class UseObserver implements GameObserver {

    @Override
    public String update(Command command, GameDescription state) {
        // 1. Controllo di competenza: si attiva SOLO per il comando USA
        if (command.getType() != CommandType.USA) {
            return null;
        }

        String target = command.getTargetName(); // Il nome dell'azione/oggetto (es. "chiave CON baule" o "laser")
        Room currentRoom = state.getCurrentRoom();

        if (target == null || target.isEmpty()) {
            return "TESTO|Cosa vorresti usare?";
        }

        // 2. LOGICA TUTORIAL: CAMERA DI ANDY (ID = 1)
        if (currentRoom.getId() == 1) {

            // =================================================================
            // ENIGMA 1: USA CHIAVE CON BAULE (Oggetto Consumabile)
            // =================================================================
            if (target.toLowerCase().contains("chiave") && target.toLowerCase().contains("baule")) {
                
                // Controlliamo se il giocatore ha effettivamente la chiave in tasca
                boolean haChiave = state.getCurrentPlayer().getPocket().stream()
                        .anyMatch(obj -> obj.getName().equalsIgnoreCase("chiave"));

                if (!haChiave) {
                    return "TESTO|Vorresti usare la chiave, ma non ce l'hai in tasca! Forse dovresti cercarla meglio...";
                }

                boolean giaAperto = state.getFlags().getOrDefault("CHEST_OPENED", false);
                if (giaAperto) {
                    return "TESTO|Il baule è già spalancato.";
                }

                // Cerchiamo il baule nella stanza per aggiornarne lo stato polimorfico
                AdvObject bauleObj = currentRoom.getObjects().stream()
                        .filter(o -> o.getName().equalsIgnoreCase("baule"))
                        .findFirst().orElse(null);

                if (bauleObj instanceof ContainerObject) {
                    ContainerObject baule = (ContainerObject) bauleObj;
                    baule.setLocked(false);
                    baule.setOpen(true);
                }

                // Aggiorniamo il flag di progressione nella GameDescription
                state.getFlags().put("CHEST_OPENED", true);

                // Rimuoviamo la chiave dalle tasche del giocatore (consumata)
                PickupableObject chiaveInTasca = state.getCurrentPlayer().getPocket().stream()
                        .filter(obj -> obj.getName().equalsIgnoreCase("chiave"))
                        .findFirst().orElse(null);
                state.getCurrentPlayer().removeObject(chiaveInTasca);

                // Dialogo narrativo di sblocco (Phase 2 e 3)
                return "TESTO|*CLACK!* Il lucchetto salta! Spalanchi il baule e... "
                        + "\nREX: 'WOODY! Meno male, stavamo soffocando qui dentro!' "
                        + "\nMR. POTATO: 'Qualcuno ha visto il mio orecchio sinistro? Scommetto che è finito sotto il letto di Andy!' "
                        + "\n[OBIETTIVO AGGIORNATO]: Ispeziona lo spazio sotto il letto."
                        + "|RIMUOVI_INVENTARIO|chiave"; // Protocollo di rete per far sparire l'oggetto dalla GUI
            }

            // =================================================================
            // ENIGMA 2: USA LASER (Abilità Speciale di Buzz) CON IL LETTO
            // =================================================================
            if (target.toLowerCase().contains("laser") || target.toLowerCase().contains("luce")) {
                
                // Controllo se il personaggio corrente ha l'abilità del laser
                if (state.getCurrentPlayer().getAbility() == null || 
                    !state.getCurrentPlayer().getAbility().getName().equalsIgnoreCase("Laser")) {
                    return "TESTO|Solo Buzz Lightyear può usare il puntatore laser! Cambia personaggio con il tasto CHIAMA.";
                }

                // Controlliamo se siamo vicini al letto o se l'utente punta al letto
                if (target.toLowerCase().contains("letto")) {
                    state.getFlags().put("LASER_USED", true);
                    
                    // Creiamo il lazo come oggetto reale e lo piazziamo nella stanza
                    PickupableObject lazo = new PickupableObject(102, "lazo", "Una corda di canapa robusta intrecciata a cappio.");
                    currentRoom.getObjects().add(lazo);

                    return "TESTO|Buzz attiva il suo laser da polso ed emette un potente fascio di luce rossa sotto il letto..."
                            + "\nBUZZ: 'La zona oscura è stata illuminata, Sceriffo! C'è qualcosa che brilla laggiù!'"
                            + "\n[LOG]: Ora puoi VEDERE e PRENDERE il 'lazo' sotto il letto!";
                } else {
                    return "TESTO|Attivi il laser per fare scena, ma non stai illuminando il punto giusto. Prova a usarlo sul 'letto'.";
                }
            }

            // =================================================================
            // ENIGMA 3 (FINALE): USA LAZO CON PORTA (Sblocco Abilità Permanente)
            // =================================================================
            if (target.toLowerCase().contains("lazo") && target.toLowerCase().contains("porta")) {
                
                // Verifichiamo che il giocatore (Woody) abbia preso il lazo da sotto il letto
                boolean haLazo = state.getCurrentPlayer().getPocket().stream()
                        .anyMatch(obj -> obj.getName().equalsIgnoreCase("lazo"));

                if (!haLazo) {
                    return "TESTO|Vorresti usare il lazo, ma non ce l'hai in tasca! Cerca meglio sotto il letto.";
                }

                // Attiviamo i flag di progressione: l'enigma della porta è risolto!
                state.getFlags().put("LAZO_USED", true);
                state.getFlags().put("LAZO_UNLOCKED", true);

                // IMPORTANTE: Il lazo diventa un'abilità permanente di Woody!
                // Non lo rimuoviamo dalle tasche (non usiamo removeObject), ma lo registriamo come SpecialAbility
                state.getCurrentPlayer().setAbility(
                    new Ability("Lazo", "/images/skills/lazo.png")
                );

                // Restituiamo il successo e diciamo alla GUI di illuminare lo slot abilità permanente
                return "TESTO|Lanci il lazo con precisione millimetrica! Il cappio stringe perfettamente il pomello della porta."
                        + "\nWOODY: 'Presa! Ora l'abilità Lazo è sbloccata per sempre e fa leva sulla maniglia!'"
                        + "\n[OBIETTIVO COMPLETATO]: Clicca sulla PORTA (o usa GUARDA porta) per aprire ed uscire finalmente nel Corridoio!"
                        + "|ABILITA|Lazo|/images/skills/lazo.png";
            }
        }

        // Caduta standard
        return "TESTO|Non sai come usare questo oggetto in questo modo.";
    }
}