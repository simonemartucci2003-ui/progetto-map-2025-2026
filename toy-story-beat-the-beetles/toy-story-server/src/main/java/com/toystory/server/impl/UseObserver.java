package com.toystory.server.impl;

import com.toystory.server.GameDescription;
import com.toystory.server.GameObserver;
import com.toystory.server.type.Command;
import com.toystory.server.type.CommandType;
import com.toystory.server.type.PlayableCharacter;
import com.toystory.server.ClientState;
import com.toystory.server.GameSession;

/**
 * Osservatore dedicato alla gestione del comando USA.
 * <p>
 * Questa classe intercetta i comandi di tipo {@link CommandType#USA} e gestisce
 * l'interazione tra gli oggetti presenti nell'inventario del personaggio e gli elementi 
 * dell'ambiente circostante. Risolve gli enigmi verificando che il giocatore possieda 
 * l'oggetto corretto, aggiorna i flag di stato della partita e consuma l'oggetto 
 * rimuovendolo dall'inventario e dal database.
 * </p>
 */
public class UseObserver implements GameObserver<String> {

    /**
     * Elabora il comando "USA" inviato dal giocatore per interagire con un elemento dell'ambiente.
     * 
     * <p>Il metodo esegue i seguenti passaggi:</p>
     * <ul>
     * <li>Verifica che il comando sia di tipo USA.</li>
     * <li>Controlla che sia stato specificato un bersaglio valido.</li>
     * <li>Smista la richiesta al metodo specifico per l'oggetto ambientale indicato tramite un blocco switch.</li>
     * </ul>
     *
     * @param command Il comando inviato dal giocatore, contenente il bersaglio (target) dell'azione.
     * @param state   Lo stato globale della partita (per controllare e salvare i flag degli enigmi risolti).
     * @param client  Lo stato del client, utile per recuperare il personaggio attivo e il suo inventario.
     * @param session La sessione di gioco multiplayer attuale.
     * @return Una stringa formattata contenente il testo narrativo ed eventuali comandi di aggiornamento 
     *         dell'interfaccia (es. aggiornamento dell'inventario grafico), oppure {@code null} se ignorato.
     */
    @Override
    public String update(Command command, GameDescription state, ClientState client, GameSession session) {
        // 1. Controllo: si attiva solo per il comando USA
        if (command.getType() != CommandType.USA) {
            return null;
        }

        String target = command.getTargetName();
        PlayableCharacter attivo = client.getCurrentCharacter();

        if (target == null || target.isEmpty()) {
            return "TESTO|Cosa vorresti usare?";
        }

        // 2. SWITCH CASE snello e pulito
        switch (target.toLowerCase()) {
            case "baule":
                return gestisciBaule(state, attivo);
                
            case "porta_cucina":
                return gestisciPortaCucina(state, attivo);
                
            case "cancello":
                return gestisciCancello(state, attivo);
                
            case "generatore":
                return gestisciGeneratore(state);
                
             case "leva":
                return gestisciLeva(state, attivo);
                
             case "varco":    
                 return gestisciScarafaggio(state, attivo);
                
            default:
                return "TESTO|Non succede nulla. Forse non è il modo giusto di usarlo.";
        }
    }

    // ===================================================================================
    // METODI PRIVATI PER LA LOGICA DEI SINGOLI OGGETTI
    // ===================================================================================

    /**
     * Gestisce l'interazione con il baule nella stanza di Andy.
     * Richiede la "chiave" nell'inventario per essere aperto.
     */
    private String gestisciBaule(GameDescription state, PlayableCharacter attivo) {
        boolean bauleAperto = state.getFlags().getOrDefault("BAULE_APERTO", false);
        if (bauleAperto) {
            return "TESTO|Il baule è già aperto. Hai già preso ciò che c'era dentro.";
        }

        boolean haLaChiave = attivo.getPocket().stream()
                .anyMatch(obj -> obj.getName().equalsIgnoreCase("chiave"));

        if (!haLaChiave) {
            return "TESTO|Il baule è chiuso a chiave. Sembra servire una piccola chiave dorata per aprirlo.";
        } 

        // Consuma l'oggetto e aggiorna stato
        consumaOggetto("chiave", attivo, state);
        state.saveFlag("BAULE_APERTO", true);

        return aggiornaInventarioDopoUso(Dialoghi.getDialogoBauleAperto(), attivo);
    }

    /**
     * Gestisce l'interazione con la porta della cucina.
     * Richiede la "pallina" nell'inventario per distrarre Buster.
     */
    private String gestisciPortaCucina(GameDescription state, PlayableCharacter attivo) {
        boolean portaSbloccata = state.getFlags().getOrDefault("PORTA_SBLOCCATA", false);
        if (portaSbloccata) {
            return "TESTO|La porta della cucina è già sbloccata, Buster sta ancora giocando.";
        }

        boolean haLaPallina = attivo.getPocket().stream()
                .anyMatch(obj -> obj.getName().equalsIgnoreCase("pallina"));

        if (!haLaPallina) {
            return "TESTO|La porta è bloccata da Buster, non hai niente per distrarlo.";
        } 

        // Consuma l'oggetto e aggiorna stato
        consumaOggetto("pallina", attivo, state);
        state.saveFlag("PORTA_SBLOCCATA", true);

        return aggiornaInventarioDopoUso(Dialoghi.getDialogoPortaCucinaAperta(), attivo);
    }

    /**
     * Gestisce l'apertura del cancello per le fogne.
     * Richiede la "forcina" nell'inventario per scassinare il lucchetto.
     */
    private String gestisciCancello(GameDescription state, PlayableCharacter attivo) {
        boolean cancelloSbloccato = state.getFlags().getOrDefault("CANCELLO_SBLOCCATO", false);
        if (cancelloSbloccato) {
            return "TESTO|Il cancello è già stato sbloccato.";
        }

        boolean haLaForcina = attivo.getPocket().stream()
                .anyMatch(obj -> obj.getName().equalsIgnoreCase("forcina"));

        if (!haLaForcina) {
            return "TESTO|Il cancello è bloccato da un pesante lucchetto..ti servirebbe qualcosa per scassinarlo.";
        } 

        // Consuma l'oggetto e aggiorna stato
        consumaOggetto("forcina", attivo, state);
        state.saveFlag("CANCELLO_SBLOCCATO", true);

        return aggiornaInventarioDopoUso(Dialoghi.getDialogoCancelloAperto(), attivo);
    }

    /**
     * Gestisce l'accensione del generatore.
     * Non richiede oggetti specifici nell'inventario, basta interagire con l'elemento.
     */
    private String gestisciGeneratore(GameDescription state) {
        boolean generatoreAcceso = state.getFlags().getOrDefault("GENERATORE_ACCESO", false);
        
        if (generatoreAcceso) {
            return "TESTO|Il generatore è già in funzione. Il suo ronzio riempie la stanza e la corrente è ripristinata.";
        } 
        
        state.saveFlag("GENERATORE_ACCESO", true);
        return "TESTO|Hai azionato la leva del generatore! I macchinari si riattivano con un ronzio metallico e la corrente elettrica torna a fluire." +
            "Buzz: Missione compiuta ragazzi!";
    }
    
    /**
     * Gestisce l'interazione con la leva difettosa.
     * Richiede il "rametto" nell'inventario per effettuare la riparazione.
     */
    private String gestisciLeva(GameDescription state, PlayableCharacter attivo) {
        boolean LevaAggiustata = state.getFlags().getOrDefault("LEVA_AGGIUSTATA", false);
        
        if (LevaAggiustata) {
            return "TESTO.La leva è stata riparata e lo scolo dell acqua e nuovamente funzionante";
        } 
        
        boolean haIlRametto = attivo.getPocket().stream()
                .anyMatch(obj -> obj.getName().equalsIgnoreCase("rametto"));

        if (!haIlRametto) {
            return "TESTO|Il cancello è bloccato da un pesante lucchetto..ti servirebbe qualcosa per scassinarlo.";
        } 

        // Consuma l'oggetto e aggiorna stato
        consumaOggetto("rametto", attivo, state);
        state.saveFlag("LEVA_AGGIUSTATA", true);

        return aggiornaInventarioDopoUso(Dialoghi.getDialogoLevaAggiustata(), attivo);
    }
    
    /**
     * Gestisce l'interazione con lo scarafaggio gigante che blocca il varco.
     * Richiede il "torsolo" di mela nell'inventario per essere distratto.
     */
    private String gestisciScarafaggio(GameDescription state, PlayableCharacter attivo) {
        boolean melaData = state.getFlags().getOrDefault("MELA_DATA", false);
        
        if (melaData) {
            return "TESTO.Lo scarafaggio è totalemnte intento a mangiare il suo torsolo di mela,";
        } 
        
        boolean haIlTorsolo = attivo.getPocket().stream()
                .anyMatch(obj -> obj.getName().equalsIgnoreCase("torsolo"));

        if (!haIlTorsolo) {
            return "TESTO|Purtroppo non hai nulla per distrarre questo bestione.";
        } 

        // Consuma l'oggetto e aggiorna stato
        consumaOggetto("torsolo", attivo, state);
        state.saveFlag("MELA_DATA", true);

        return aggiornaInventarioDopoUso(Dialoghi.getDialogoUsoMela(), attivo);
    }


    // ===================================================================================
    // METODI DI SUPPORTO (HELPER)
    // ===================================================================================

    /**
     * Rimuove un oggetto consumabile dalla memoria dell'inventario del personaggio 
     * e aggiorna la situazione nel Database.
     *
     * @param nomeOggetto Il nome dell'oggetto da consumare.
     * @param attivo      Il personaggio che sta utilizzando l'oggetto.
     * @param state       Lo stato globale che fornisce l'accesso al database.
     */
    private void consumaOggetto(String nomeOggetto, PlayableCharacter attivo, GameDescription state) {
        com.toystory.server.type.PickupableObject oggettoDaUsare = (com.toystory.server.type.PickupableObject) attivo.getPocket().stream()
                .filter(obj -> obj.getName().equalsIgnoreCase(nomeOggetto))
                .findFirst()
                .orElse(null);

        if (oggettoDaUsare != null) {
            attivo.getPocket().remove(oggettoDaUsare);
            try {
                state.getDb().consumeItem(attivo.getName(), oggettoDaUsare.getId());
            } catch (Exception e) {
                System.err.println("[UseObserver] Errore DB durante rimozione: " + e.getMessage());
            }
        }
    }

    /**
     * Costruisce la stringa di risposta finale da inviare al client, accodando 
     * i comandi necessari per svuotare l'interfaccia grafica dell'inventario 
     * in modo che rifletta l'avvenuta rimozione dell'oggetto usato.
     *
     * @param testoNarrativo Il dialogo o testo di descrizione generato dall'azione.
     * @param attivo         Il personaggio per cui ricalcolare gli elementi dell'inventario.
     * @return Una stringa formattata pronta per il network (es. "TESTO|...|CLEAR_INVENTORY|OK|INVENTARIO|...").
     */
    private String aggiornaInventarioDopoUso(String testoNarrativo, PlayableCharacter attivo) {
        String risposta = "TESTO|" + testoNarrativo + "|CLEAR_INVENTORY|OK";
        
        if (attivo.getPocket() != null && !attivo.getPocket().isEmpty()) {
            for (com.toystory.server.type.PickupableObject obj : attivo.getPocket()) {
                risposta += "|INVENTARIO|" + obj.getName() + "|" + obj.getIcona();
            }
        }
        return risposta;
    }
}