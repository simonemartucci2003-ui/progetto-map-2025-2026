package com.toystory.server.impl;

import com.toystory.server.GameDescription;
import com.toystory.server.GameObserver;
import com.toystory.server.type.*;
import com.toystory.server.ClientState;
import com.toystory.server.GameSession;

/**
 * Osservatore dedicato alla gestione del comando di raccolta degli oggetti (PRENDI).
 * <p>
 * Questa classe intercetta i comandi di tipo {@link CommandType#PRENDI} e gestisce
 * l'aggiunta di oggetti collezionabili ({@link PickupableObject}) all'inventario del
 * personaggio attivo. Inoltre, implementa le logiche degli enigmi, verificando
 * che vengano rispettate condizioni specifiche (es. l'oggetto è raggiungibile solo da
 * un determinato personaggio, come Buzz per i posti bui o Jessie per i posti alti).
 * </p>
 */
public class PickUpObserver implements GameObserver<String> {

    /**
     * Elabora il comando "PRENDI" inviato dal giocatore per raccogliere un oggetto.
     * 
     * <p>Il metodo esegue le seguenti operazioni:</p>
     * <ul>
     * <li>Verifica che il comando sia di tipo PRENDI.</li>
     * <li>Analizza il bersaglio (target) dell'azione. Se il giocatore indica un elemento
     * dell'ambiente, la logica valuta se il personaggio attivo ha
     * i requisiti per estrarre l'oggetto nascosto in quel luogo.</li>
     * <li>Aggiorna i flag globali del gioco quando un oggetto importante viene scoperto.</li>
     * <li>Delega l'effettiva raccolta ai metodi helper per aggiornare la stanza e il database.</li>
     * </ul>
     *
     * @param command Il comando inviato dal giocatore, contenente l'oggetto o il luogo da cui raccogliere.
     * @param state   Lo stato globale della partita (utilizzato per i flag degli eventi).
     * @param client  Lo stato del client, per recuperare il personaggio attivo e la stanza corrente.
     * @param session La sessione di gioco multiplayer attuale.
     * @return Una stringa formattata contenente l'esito testuale dell'azione e, in caso di successo,
     *         i comandi di aggiornamento dell'interfaccia dell'inventario.
     */
    @Override
    public String update(Command command, GameDescription state, ClientState client, GameSession session) {
        // si attiva solo per il comando PRENDI
        if (command.getType() != CommandType.PRENDI) {
            return null;
        }

        String target = command.getTargetName();
        if (target == null || target.isEmpty()) {
            return "TESTO|Cosa vorresti raccogliere?";
        }

        PlayableCharacter attivo = client.getCurrentCharacter();  
        Room currentRoom = client.getCurrentRoom();               

        // Smista l'azione in base a cosa abbiamo cliccato
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
                    return "TESTO| Sotto al letto non si riesce a vedere niente, e' troppo buio...";
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
                    
                    return eseguiRaccolta("forcina", target, state, attivo, currentRoom);
                }
                
                
             //GIARDINO
            case "sacchi_neri":
                // Se clicchiamo i sacchi, deviamo la logica cercando "torsolo"
                return eseguiRaccolta("torsolo", target, state, attivo, currentRoom);
                
            case "albero":
                // Controlliamo lo stato del gioco tramite i Flags               
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
  
            default:
                // Per tutti gli altri oggetti generici sparsi nella stanza
                return eseguiRaccolta(target, target, state, attivo, currentRoom);
        }
    }

    
    
   /**
     * Converte l'esito logico della raccolta in una stringa compatibile con il protocollo di comunicazione.
     * <p>
     * Se la raccolta ha successo, appende i comandi necessari ad aggiornare l'interfaccia 
     * dell'inventario del giocatore (es. mostrare l'icona del nuovo oggetto).
     * </p>
     *
     * @param nomeDaCercare   Il nome reale dell'oggetto da cercare tra gli elementi della stanza.
     * @param targetOriginale La stringa cliccata in origine dal giocatore.
     * @param state           Lo stato globale del gioco.
     * @param attivo          Il personaggio che sta tentando la raccolta.
     * @param currentRoom     La stanza attuale in cui effettuare la ricerca.
     * @return Una stringa di rete formattata (es. "TESTO|...|INVENTARIO|nome|icona").
     */
    private String eseguiRaccolta(String nomeDaCercare, String targetOriginale, GameDescription state, PlayableCharacter attivo, Room currentRoom) {
        ActionResult<PickupableObject> risultato = tentaRaccolta(nomeDaCercare, targetOriginale, state, attivo, currentRoom);

        if (!risultato.isSuccess()) {
            return "TESTO|" + risultato.getMessage();
        }

        PickupableObject obj = risultato.getPayload();
        return "TESTO|" + risultato.getMessage() + "|INVENTARIO|" + obj.getName() + "|" + obj.getIcona();
    }

    
    /**
     * Tenta fisicamente di raccogliere l'oggetto, eseguendo tutti i controlli di validazione e salvataggio.
     * <p>
     * Effettua i seguenti controlli:
     * <ul>
     * <li>L'oggetto esiste nella stanza attuale?</li>
     * <li>L'oggetto è di tipo {@link PickupableObject}</li>
     * <li>L'oggetto è già presente nell'inventario del personaggio?</li>
     * <li>C'è spazio a sufficienza nell'inventario del personaggio?</li>
     * </ul>
     * Se tutti i controlli vengono superati, l'oggetto viene rimosso dalla stanza, aggiunto 
     * all'inventario del personaggio e la transazione viene salvata sul database.
     * </p>
     *
     * @param nomeDaCercare   Il nome dell'oggetto da cercare e raccogliere.
     * @param targetOriginale La stringa originariamente richiesta dall'utente (usata per differenziare i messaggi d'errore).
     * @param state           Lo stato globale del gioco e del database.
     * @param attivo          Il personaggio che esegue la raccolta.
     * @param currentRoom     La stanza dove risiede l'oggetto.
     * @return Un oggetto {@link ActionResult} contenente l'esito dell'operazione e, in caso di successo, l'oggetto raccolto.
     */
    private ActionResult<PickupableObject> tentaRaccolta(String nomeDaCercare, String targetOriginale, GameDescription state, PlayableCharacter attivo, Room currentRoom) {
        AdvObject targetObj = currentRoom.getObjects().stream()
                .filter(obj -> obj.getName().equalsIgnoreCase(nomeDaCercare))
                .findFirst()
                .orElse(null);

        if (targetObj == null) {
            String msg = targetOriginale.equalsIgnoreCase("libreria")
                    ? "Hai già rovistato qui e hai preso tutto quello che c'era."
                    : "Non vedi niente di interessante da prendere.";
            return ActionResult.fail(msg);
        }

        if (!(targetObj instanceof PickupableObject)) {
            return ActionResult.fail("Non puoi raccogliere " + targetObj.getName() + ", è un elemento fisso dello scenario.");
        }

        PickupableObject oggettoRaccoglibile = (PickupableObject) targetObj;

        boolean giaInTasca = attivo.getPocket().stream()
                .anyMatch(obj -> obj.getName().equalsIgnoreCase(oggettoRaccoglibile.getName()));
        if (giaInTasca) {
            return ActionResult.fail("Hai già questo oggetto nello zaino!");
        }

        if (!attivo.addToInventory(oggettoRaccoglibile)) {
            return ActionResult.fail("Le tasche di " + attivo.getName() + " sono piene!");
        }

        currentRoom.removeObject(oggettoRaccoglibile);
        try {
            state.getDb().addToInventory(attivo.getName(), oggettoRaccoglibile.getId());
        } catch (Exception e) {
            System.err.println("[PickUpObserver] Errore DB: " + e.getMessage());
        }

        return ActionResult.ok("Hai dato " + oggettoRaccoglibile.getName() + " a " + attivo.getName() + "!", oggettoRaccoglibile);
    }
}