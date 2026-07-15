package com.toystory.server.impl;

import com.toystory.server.GameDescription;
import com.toystory.server.GameObserver;
import com.toystory.server.type.Command;
import com.toystory.server.type.CommandType;
import com.toystory.server.type.Room;
import com.toystory.server.type.PlayableCharacter;

public class MoveObserver implements GameObserver {

    @Override
    public String update(Command command, GameDescription state) {
        
        // 1. Controllo: si attiva solo per il comando VAI
        if (command.getType() != CommandType.VAI) {
            return null;
        }

        String target = command.getTargetName();
        if (target == null || target.isEmpty()) {
            return "TESTO|Dove vorresti andare?";
        }

        PlayableCharacter attivo = state.getCurrentPlayer();
        Room currentRoom = state.getCurrentRoom();

        // 2. SWITCH CASE: Smista l'azione in base alla destinazione
        switch (target.toLowerCase()) {
            
            case "porta":
                // L'enigma del tutorial con il lazo
                return gestisciPorta(state, attivo, currentRoom);
                
            case "porta_cucina":
                return gestisciPortaCucina(state, attivo, currentRoom);
                
            case "cancello":
                return gestisciCancello(state, attivo, currentRoom);
                
            case "tubo_buio":
                return gestisciTuboBuio(state, attivo, currentRoom);
                
            case "porticina": 
                return gestisciCasaTopo(state, attivo, currentRoom);
                
            case "buco_stretto"  :
                return gestisciBuco(state, attivo, currentRoom);
                
            case "varco"  :
                return gestisciVarco(state, attivo, currentRoom);

            
                
            default:
                // Logica di movimento standard per le stanze libere
                return eseguiMovimentoGenerico(target, state, currentRoom);
        }
    }

    /**
     * Metodo privato per gestire l'enigma della porta col Lazo
     */
    private String gestisciPorta(GameDescription state, PlayableCharacter attivo, Room currentRoom) {
        // 1. Controlliamo se la porta è GIÀ stata sbloccata in precedenza
        boolean portaAperta = state.getFlags().getOrDefault("PORTA_APERTA", false);
        
        // Se è già aperta, saltiamo tutto l'enigma e usiamo il movimento normale!
        if (portaAperta) {
            return eseguiMovimentoGenerico("porta", state, currentRoom);
        }
        
        
        boolean lazoSbloccato = state.getFlags().getOrDefault("LAZO_UNLOCKED", false);

        // CASO 1: Nessuno ha il lazo
        if (!lazoSbloccato) {
            String testoDialogo = Dialoghi.getManigliaTroppoInAlto(); 
            return "TESTO|" + testoDialogo;
           
        }

        // CASO 2: Abbiamo il lazo, ma stiamo usando il personaggio sbagliato
        if (!attivo.getName().equalsIgnoreCase("Woody")) {
            
            String testoDialogo = Dialoghi.getPortaNonWoody(); 
            return "TESTO|" + testoDialogo;
            
        }

        // CASO 3: Abbiamo il lazo e stiamo usando Woody (SUCCESSO!)
        Room prossimaStanza = currentRoom.getExit("porta");
        
        if (prossimaStanza != null) {
            state.saveFlag("PORTA_APERTA", true);
            
            // 1. Aggiorniamo la stanza corrente nel Server
            state.setCurrentRoom(prossimaStanza);
            
            // 2. Generiamo l'ID logico in modo automatico (come nell'altro metodo!)
            String idStanza = prossimaStanza.getName().toUpperCase().replace(" ", "_");
            
            String testoDialogo = Dialoghi.getPortaConWoody(); 
            return "TESTO|" + testoDialogo + "|CAMBIA_SFONDO|" + idStanza;
            
        } else {
            return "TESTO|Hai aperto la porta, ma oltre c'è solo un muro nero. (Errore: Stanza di destinazione non configurata nella mappa di Room!)";
        }
    }
    
    private String gestisciPortaCucina(GameDescription state, PlayableCharacter attivo, Room currentRoom) {
        boolean portaSbloccata = state.getFlags().getOrDefault("PORTA_SBLOCCATA", false);

        if (!portaSbloccata) {
            return "TESTO|La porta è bloccata da Buster, hai bisogno di qualcosa per distrarlo";
        }

        Room prossimaStanza = currentRoom.getExit("porta_cucina");

        if (prossimaStanza != null) {
            state.setCurrentRoom(prossimaStanza);
            String idStanza = prossimaStanza.getName().toUpperCase().replace(" ", "_");

            return "TESTO|Buster è troppo distratto dalla sua pallina per accorgersi di te. Sgattaioli oltre la porta.|CAMBIA_SFONDO|" + idStanza;
        } else {
            return "TESTO|La porta è aperta, ma oltre c'è solo un muro nero. (Errore: Stanza di destinazione non configurata!)";
        }
    }
    
    private String gestisciCancello(GameDescription state, PlayableCharacter attivo, Room currentRoom) {
        boolean CancelloSbloccato = state.getFlags().getOrDefault("CANCELLO_SBLOCCATO", false);

        if (!CancelloSbloccato) {
            return "TESTO|Il cancello è bloccato da un lucchetto, dobbiamo sapere cosa c'è li dentro.";
        }

        Room prossimaStanza = currentRoom.getExit("cancello");

        if (prossimaStanza != null) {
            state.saveFlag("CANCELLO_SBLOCCATO", true);
            
            // 1. Aggiorniamo la stanza corrente nel Server
            state.setCurrentRoom(prossimaStanza);
            
            // 2. Generiamo l'ID logico in modo automatico
            String idStanza = prossimaStanza.getName().toUpperCase().replace(" ", "_");
            
            String testoDialogo = Dialoghi.getCancelloAperto(); 
            return "TESTO|" + testoDialogo + "|CAMBIA_SFONDO|" + idStanza;
            
        } else {
            return "TESTO|Hai aperto la porta, ma oltre c'è solo un muro nero. (Errore: Stanza di destinazione non configurata nella mappa di Room!)";
        }
    }
    
    private String gestisciTuboBuio(GameDescription state, PlayableCharacter attivo, Room currentRoom) {
        
        // 1. Controllo Personaggio: Solo Buzz può entrare nel tubo buio
        if (!attivo.getName().equalsIgnoreCase("Buzz Lightyear")) {
            return "TESTO|È troppo buio lì dentro! Woody e gli altri non vedrebbero nulla. Serve qualcuno con una tecnologia avanzata per illuminare quel tunnel.";
        }

        // 2. Spostamento
        Room prossimaStanza = currentRoom.getExit("tubo_buio");

        if (prossimaStanza != null) {
            state.setCurrentRoom(prossimaStanza);
            String idStanza = prossimaStanza.getName().toUpperCase().replace(" ", "_");

            return "TESTO|Grazie al laser della mia tuta, riesco a scorgere il sentiero tra l'oscurità. Il tubo è stretto, ma ce la farò!|CAMBIA_SFONDO|" + idStanza;
        } else {
            return "TESTO|Il passaggio sembra esserci, ma è bloccato da detriti. (Errore: Stanza di destinazione non configurata!)";
        }
    }
    
    /**
     * Metodo privato per gestire l'ingresso nella casa del topo.
     * Richiede che il generatore sia stato riattivato.
     */
    private String gestisciCasaTopo(GameDescription state, PlayableCharacter attivo, Room currentRoom) {
        
        // Controlliamo se il generatore è acceso
        boolean generatoreAcceso = state.getFlags().getOrDefault("GENERATORE_ACCESO", false);
        
        if (!generatoreAcceso) {
            return "TESTO|La porta tecnologica è bloccata e le luci sono spente. Non puoi entrare finché non riattivi la corrente nella zona!";
        }

        // Se c'è corrente, procediamo con lo spostamento
        Room prossimaStanza = currentRoom.getExit("porticina");

        if (prossimaStanza != null) {
            state.setCurrentRoom(prossimaStanza);
            String idStanza = prossimaStanza.getName().toUpperCase().replace(" ", "_");

            return "TESTO|La porta tecnologica si apre con un ronzio meccanico e le luci dei monitor ti accolgono. Entri nella casa del topo.|CAMBIA_SFONDO|" + idStanza;
        } else {
            return "TESTO|Errore: Stanza di destinazione non configurata!";
        }
    }
    
    private String gestisciBuco(GameDescription state, PlayableCharacter attivo, Room currentRoom) {
        
        // 1. Controllo Personaggio: Solo Jessie può entrare nel tubo buio
        if (!attivo.getName().equalsIgnoreCase("Jessie")) {
            return "TESTO|È troppo stretto lì su! I giocattoli non riescono a passare. Serve qualcuno abbastanza agile per infilarsi in quel passaggio.";
        }

        // 2. Spostamento
        Room prossimaStanza = currentRoom.getExit("buco_stretto");

        if (prossimaStanza != null) {
            state.setCurrentRoom(prossimaStanza);
            String idStanza = prossimaStanza.getName().toUpperCase().replace(" ", "_");

            return "TESTO|Questo sembra un lavoro per Jessie ragazzi! Fate spazio, ora vi farò vedere cosa sa fare un avera cowgirl!|CAMBIA_SFONDO|" + idStanza;
        } else {
            return "TESTO|Il passaggio sembra esserci, ma è bloccato da detriti. (Errore: Stanza di destinazione non configurata!)";
        }
    }
    
    private String gestisciVarco(GameDescription state, PlayableCharacter attivo, Room currentRoom) {
        // 1. Controlliamo se lo scarafaggio è già stato distratto con la mela
        boolean melaData = state.getFlags().getOrDefault("MELA_DATA", false);

        // Se non è ancora stato distratto, il passaggio resta bloccato
        if (!melaData) {
            return "TESTO|Lo scarafaggio gigante blocca completamente il passaggio. Dovresti trovare un modo per distrarlo.";
        }

        // 2. Lo scarafaggio è distratto: il passaggio è libero, usiamo il movimento normale
        return eseguiMovimentoGenerico("varco", state, currentRoom);

    }

    /**
     * Metodo privato per i movimenti standard senza enigmi particolari.
     * Cerca automaticamente se il target cliccato è un'uscita valida per la stanza.
     */
    private String eseguiMovimentoGenerico(String target, GameDescription state, Room currentRoom) {
        // Controlliamo se la stanza attuale ha un'uscita corrispondente a dove abbiamo cliccato
        Room prossimaStanza = currentRoom.getExit(target);
        
        if (prossimaStanza != null) {
            // Spostiamo il giocatore nel Server
            state.setCurrentRoom(prossimaStanza);
            
            // Assumendo che getId() o getName() restituisca stringhe come "STANZA_MOLLY" o "STANZA_ANDY"
            // Se la tua classe Room non ha getId(), puoi usare un formato standardizzato basato sul nome, 
            // ma l'ideale è usare l'ID della stanza se lo hai configurato.
            String idStanza = prossimaStanza.getName().toUpperCase().replace(" ", "_");
            
            return "TESTO|Attraversi il passaggio...<PAUSA>Sei arrivato in: " + prossimaStanza.getName() + ".|CAMBIA_SFONDO|" + idStanza;
        } else {
            return "TESTO|Non sembra esserci un passaggio in quella direzione.";
        }
    }
}