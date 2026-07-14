package com.toystory.server.impl;

import com.toystory.server.GameDescription;
import com.toystory.server.GameObserver;
import com.toystory.server.type.*;

public class PickUpObserver implements GameObserver {

    @Override
    public String update(Command command, GameDescription state) {
        // 1. Controllo: si attiva solo per il comando PRENDI
        if (command.getType() != CommandType.PRENDI) {
            return null;
        }

        String target = command.getTargetName();
        if (target == null || target.isEmpty()) {
            return "TESTO|Cosa vorresti raccogliere?";
        }

        PlayableCharacter attivo = state.getCurrentPlayer();
        Room currentRoom = state.getCurrentRoom();

        // 2. SWITCH CASE: Smista l'azione in base a cosa abbiamo cliccato
        switch (target.toLowerCase()) {
            
            //-- STANZA ANDY --
            case "libreria":
                // Se clicchiamo la libreria, deviamo la logica cercando la "chiave"
                return eseguiRaccolta("chiave", target, state, attivo, currentRoom);

            case "letto":
     
                // 1. Controlliamo lo stato del gioco tramite i Flags
                boolean bauleAperto = state.getFlags().getOrDefault("BAULE_APERTO", false);
                boolean lazoSbloccato = state.getFlags().getOrDefault("LAZO_UNLOCKED", false);

                // Se il baule è chiuso, nessuno può passare
                if (!bauleAperto) {
                    return "TESTO| Sotto al letto non si riesce a vedere niente, e' tropppo buio...";
                }

                // Se abbiamo già preso il lazo
                if (lazoSbloccato) {
                    return "TESTO|Sotto il letto ormai c'è solo polvere. Hai già recuperato tutto.";
                }

                // Se arriviamo qui: il baule è aperto ma il lazo è ancora lì sotto.
                String nomeEroe = attivo.getName();

                if (nomeEroe.equalsIgnoreCase("Woody")) {
                    // WOODY FALLISCE (braccia di pezza)
                    String testoDialogo = Dialoghi.getWoodySottoAlLetto();
                    return "TESTO|" + testoDialogo;

                } else if (nomeEroe.equalsIgnoreCase("Jessie")) {
                    // JESSIE FALLISCE (braccia di pezza)
                    String testoDialogo = Dialoghi.getJessieSottoAlLetto();
                    return "TESTO|" + testoDialogo;

                } else if (nomeEroe.equalsIgnoreCase("Buzz Lightyear") || nomeEroe.equalsIgnoreCase("Buzz")) {
                    // SOLO BUZZ HA SUCCESSO
                    state.saveFlag("LAZO_UNLOCKED", true); // Sblocchiamo l'abilità per Woody
                    
                    // Buzz trova il Lazo. Non aggiorniamo la GUI del Lazo ora perché siamo su Buzz, 
                    // ma avvisiamo il giocatore che Woody lo ha ottenuto!
                    String testoDialogo = Dialoghi.getBuzzSottoAlLetto();
                    return "TESTO|" + testoDialogo;
                }
                
                return "TESTO|Non riesci a raggiungere niente.";
                
            // -- STANZA MOLLY --
            case "baule_molly":
                // Se clicchiamo il baule di molly, deviamo la logica cercando la "pallina"
                return eseguiRaccolta("pallina", target, state, attivo, currentRoom);
                
            case "letto_molly":
                // Se clicchiamo il baule di molly, deviamo la logica cercando la "pallina"
                //return eseguiRaccolta("forcina", target, state, attivo, currentRoom);
                
                // 1. Controlliamo lo stato del gioco tramite i Flags               
                boolean forcinaSbloccata = state.getFlags().getOrDefault("FORCINA_UNLOCKED", false);

                // Se abbiamo già preso il lazo
                if (forcinaSbloccata) {
                    return "TESTO|Sotto il letto ormai c'è solo polvere. Hai già recuperato tutto.";
                }

                nomeEroe = attivo.getName();

                if (nomeEroe.equalsIgnoreCase("Woody")) {
                    // WOODY FALLISCE (braccia di pezza)
                    String testoDialogo = Dialoghi.getWoodySottoAlLetto();
                    return "TESTO|" + testoDialogo;

                } else if (nomeEroe.equalsIgnoreCase("Jessie")) {
                    // JESSIE FALLISCE (braccia di pezza)
                    String testoDialogo = Dialoghi.getJessieSottoAlLetto();
                    return "TESTO|" + testoDialogo;

                } else if (nomeEroe.equalsIgnoreCase("Buzz Lightyear") || nomeEroe.equalsIgnoreCase("Buzz")) {
                    // SOLO BUZZ HA SUCCESSO
                   
                    state.saveFlag("FORCINA_UNLOCKED", true); // raccogliamo la forcina
                    String testoDialogo = Dialoghi.getBuzzSottoAlLettoMolly();
                    
                    return eseguiRaccolta("forcina", target, state, attivo, currentRoom);
                }
                
                
             //GIARDINO
            case "sacchi_neri":
                // Se clicchiamo l albero, deviamo la logica cercando "torsolo"
                return eseguiRaccolta("torsolo", target, state, attivo, currentRoom);
                
            case "albero":
                // 1. Controlliamo lo stato del gioco tramite i Flags               
                boolean RamettoSbloccato = state.getFlags().getOrDefault("RAMETTO_UNLOCKED", false);

                if (RamettoSbloccato) {
                    return "TESTO|Hai già controllato qui su. Hai preso tutto quello che c'era.";
                }

                nomeEroe = attivo.getName();

                if (nomeEroe.equalsIgnoreCase("Woody")) {
                    // WOODY FALLISCE (braccia di pezza)
                    String testoDialogo = Dialoghi.getWoodyAlbero();
                    return "TESTO|" + testoDialogo;

                } else if (nomeEroe.equalsIgnoreCase("Jessie")) {
                    // SOLO JESSIE RIESCIE A RAGGIUNGERE IL RAMETTO GRAZIE ALLA SUA DESTREZZA
                    state.saveFlag("RAMETTO_UNLOCKED", true); // raccogliamo la forcina
                    return eseguiRaccolta("rametto", target, state, attivo, currentRoom);

                } else if (nomeEroe.equalsIgnoreCase("Buzz Lightyear") || nomeEroe.equalsIgnoreCase("Buzz")) {
                    // BUZZ FALLISCE
                    String testoDialogo = Dialoghi.getBuzzAlbero();
                    return "TESTO|" + testoDialogo;
                }
                
            //INGRESSO FOGNA
            
                
                
            default:
                // Per tutti gli altri oggetti generici sparsi nella stanza
                return eseguiRaccolta(target, target, state, attivo, currentRoom);
        }
    }

    
    
    /**
     * Metodo privato di supporto che contiene tutta la logica di raccolta standard.
     * Evita di dover riscrivere i controlli di Database e Inventario per ogni singolo oggetto!
     */
    private String eseguiRaccolta(String nomeDaCercare, String targetOriginale, GameDescription state, PlayableCharacter attivo, Room currentRoom) {
        
        // 3. Cerchiamo l'oggetto nella stanza
        AdvObject targetObj = currentRoom.getObjects().stream()
                .filter(obj -> obj.getName().equalsIgnoreCase(nomeDaCercare))
                .findFirst()
                .orElse(null);

        // 4. Feedback se l'oggetto non esiste
        if (targetObj == null) {
            if (targetOriginale.equalsIgnoreCase("libreria")) {
                return "TESTO|Hai già rovistato qui e hai preso tutto quello che c'era.";
            } else {
                return "TESTO|Non vedi niente di interessante da prendere.";
            }
        }

        // 5. Controllo se l'oggetto è raccoglibile
        if (!(targetObj instanceof PickupableObject)) {
            return "TESTO|Non puoi raccogliere " + targetObj.getName() + ", è un elemento fisso dello scenario.";
        }

        PickupableObject oggettoRaccoglibile = (PickupableObject) targetObj;

        // BLOCCO ANTI-DUPLICAZIONE (Controlla le tasche)
        boolean giaInTasca = attivo.getPocket().stream()
                .anyMatch(obj -> obj.getName().equalsIgnoreCase(oggettoRaccoglibile.getName()));

        if (giaInTasca) {
            return "TESTO|Hai già questo oggetto nello zaino!";
        }

        // 6. Tenta di aggiungere all'inventario
        if (attivo.addToInventory(oggettoRaccoglibile)) { 
            
            // Rimuoviamo dalla stanza
            currentRoom.removeObject(oggettoRaccoglibile);

            // Aggiornamento Database
            try {
                state.getDb().addToInventory(attivo.getName(), oggettoRaccoglibile.getId());
            } catch (Exception e) {
                System.err.println("[PickUpObserver] Errore DB: " + e.getMessage());
            }
            
            String nomeFileIcona = oggettoRaccoglibile.getIcona();
            
            // Risposta dinamica: il nome dell'oggetto e la sua icona
            return "TESTO|Hai dato " + oggettoRaccoglibile.getName() + " a " + attivo.getName() + 
                   "!|INVENTARIO|" + oggettoRaccoglibile.getName() + "|" + nomeFileIcona;
                   
        } else {
            return "TESTO|Le tasche di " + attivo.getName() + " sono piene!";
        }
    }
}