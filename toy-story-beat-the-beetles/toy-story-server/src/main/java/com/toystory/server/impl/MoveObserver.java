package com.toystory.server.impl;

import com.toystory.server.ClientState;
import com.toystory.server.GameDescription;
import com.toystory.server.GameObserver;
import com.toystory.server.GameSession;
import com.toystory.server.type.Command;
import com.toystory.server.type.CommandType;
import com.toystory.server.type.Room;
import com.toystory.server.type.PlayableCharacter;

public class MoveObserver implements GameObserver {

    @Override
    public String update(Command command, GameDescription state, ClientState client, GameSession session) {
        if (command.getType() != CommandType.VAI) {
            return null;
        }

        String target = command.getTargetName();
        if (target == null || target.isEmpty()) {
            return "TESTO|Dove vorresti andare?";
        }

        PlayableCharacter attivo = client.getCurrentCharacter();
        Room currentRoom = client.getCurrentRoom();

        switch (target.toLowerCase()) {
            case "porta":
                return gestisciPorta(state, client, attivo, currentRoom);
            case "porta_cucina":
                return gestisciPortaCucina(state, client, currentRoom);
            case "cancello":
                return gestisciCancello(state, client, currentRoom);
            case "tubo_buio":
                return gestisciTuboBuio(state, client, attivo, currentRoom);
            case "porticina":
                return gestisciCasaTopo(state, client, currentRoom);
            case "buco_stretto":
                return gestisciBuco(state, client, attivo, currentRoom);
            case "varco":
                return gestisciVarco(state, client, currentRoom);
            case "botola_sbloccata":
                return gestisciBotola(state, client, currentRoom);
            default:
                return eseguiMovimentoGenerico(target, state, client, currentRoom);
        }
    }

    /** Sposta questo client nella nuova stanza, e ricorda dove si trova ora quel personaggio. */
    private void spostaGiocatore(GameDescription state, ClientState client, Room nuovaStanza) {
        client.setCurrentRoom(nuovaStanza);
        state.saveCharacterRoom(client.getCurrentCharacter(), nuovaStanza);
    }

    private String gestisciPorta(GameDescription state, ClientState client, PlayableCharacter attivo, Room currentRoom) {
        boolean portaAperta = state.getFlags().getOrDefault("PORTA_APERTA", false);
        if (portaAperta) {
            return eseguiMovimentoGenerico("porta", state, client, currentRoom);
        }
        boolean lazoSbloccato = state.getFlags().getOrDefault("LAZO_UNLOCKED", false);
        if (!lazoSbloccato) {
            return "TESTO|" + Dialoghi.getManigliaTroppoInAlto();
        }
        if (!attivo.getName().equalsIgnoreCase("Woody")) {
            return "TESTO|" + Dialoghi.getPortaNonWoody();
        }
        Room prossimaStanza = currentRoom.getExit("porta");
        if (prossimaStanza != null) {
            state.saveFlag("PORTA_APERTA", true);
            spostaGiocatore(state, client, prossimaStanza);
            String idStanza = prossimaStanza.getName().toUpperCase().replace(" ", "_");
            return "TESTO|" + Dialoghi.getPortaConWoody() + "|CAMBIA_SFONDO|" + idStanza;
        } else {
            return "TESTO|Hai aperto la porta, ma oltre c'è solo un muro nero. (Errore: Stanza di destinazione non configurata nella mappa di Room!)";
        }
    }

    private String gestisciPortaCucina(GameDescription state, ClientState client, Room currentRoom) {
        boolean portaSbloccata = state.getFlags().getOrDefault("PORTA_SBLOCCATA", false);
        if (!portaSbloccata) {
            return "TESTO|La porta è bloccata da Buster, hai bisogno di qualcosa per distrarlo";
        }
        Room prossimaStanza = currentRoom.getExit("porta_cucina");
        if (prossimaStanza != null) {
            spostaGiocatore(state, client, prossimaStanza);
            String idStanza = prossimaStanza.getName().toUpperCase().replace(" ", "_");
            return "TESTO|Buster è troppo distratto dalla sua pallina per accorgersi di te. Sgattaioli oltre la porta.|CAMBIA_SFONDO|" + idStanza;
        } else {
            return "TESTO|La porta è aperta, ma oltre c'è solo un muro nero. (Errore: Stanza di destinazione non configurata!)";
        }
    }

    private String gestisciCancello(GameDescription state, ClientState client, Room currentRoom) {
        boolean cancelloSbloccato = state.getFlags().getOrDefault("CANCELLO_SBLOCCATO", false);
        if (!cancelloSbloccato) {
            return "TESTO|Il cancello è bloccato da un lucchetto, dobbiamo sapere cosa c'è li dentro.";
        }
        Room prossimaStanza = currentRoom.getExit("cancello");
        if (prossimaStanza != null) {
            state.saveFlag("CANCELLO_SBLOCCATO", true);
            spostaGiocatore(state, client, prossimaStanza);
            String idStanza = prossimaStanza.getName().toUpperCase().replace(" ", "_");
            return "TESTO|" + Dialoghi.getCancelloAperto() + "|CAMBIA_SFONDO|" + idStanza;
        } else {
            return "TESTO|Hai aperto la porta, ma oltre c'è solo un muro nero. (Errore: Stanza di destinazione non configurata nella mappa di Room!)";
        }
    }

    private String gestisciTuboBuio(GameDescription state, ClientState client, PlayableCharacter attivo, Room currentRoom) {
        if (!attivo.getName().equalsIgnoreCase("Buzz Lightyear")) {
            return "TESTO|È troppo buio lì dentro! Woody e gli altri non vedrebbero nulla. Serve qualcuno con una tecnologia avanzata per illuminare quel tunnel.";
        }
        Room prossimaStanza = currentRoom.getExit("tubo_buio");
        if (prossimaStanza != null) {
            spostaGiocatore(state, client, prossimaStanza);
            String idStanza = prossimaStanza.getName().toUpperCase().replace(" ", "_");
            return "TESTO|Grazie al laser della mia tuta, riesco a scorgere il sentiero tra l'oscurità. Il tubo è stretto, ma ce la farò!|CAMBIA_SFONDO|" + idStanza;
        } else {
            return "TESTO|Il passaggio sembra esserci, ma è bloccato da detriti. (Errore: Stanza di destinazione non configurata!)";
        }
    }

    private String gestisciCasaTopo(GameDescription state, ClientState client, Room currentRoom) {
        boolean generatoreAcceso = state.getFlags().getOrDefault("GENERATORE_ACCESO", false);
        if (!generatoreAcceso) {
            return "TESTO|La porta tecnologica è bloccata e le luci sono spente. Non puoi entrare finché non riattivi la corrente nella zona!";
        }
        Room prossimaStanza = currentRoom.getExit("porticina");
        if (prossimaStanza != null) {
            spostaGiocatore(state, client, prossimaStanza);
            String idStanza = prossimaStanza.getName().toUpperCase().replace(" ", "_");
            return "TESTO|La porta tecnologica si apre con un ronzio meccanico e le luci dei monitor ti accolgono. Entri nella casa del topo.|CAMBIA_SFONDO|" + idStanza;
        } else {
            return "TESTO|Errore: Stanza di destinazione non configurata!";
        }
    }

    private String gestisciBuco(GameDescription state, ClientState client, PlayableCharacter attivo, Room currentRoom) {
        if (!attivo.getName().equalsIgnoreCase("Jessie")) {
            return "TESTO|È troppo stretto lì su! I giocattoli non riescono a passare. Serve qualcuno abbastanza agile per infilarsi in quel passaggio.";
        }
        Room prossimaStanza = currentRoom.getExit("buco_stretto");
        if (prossimaStanza != null) {
            spostaGiocatore(state, client, prossimaStanza);
            String idStanza = prossimaStanza.getName().toUpperCase().replace(" ", "_");
            return "TESTO|Questo sembra un lavoro per Jessie ragazzi! Fate spazio, ora vi farò vedere cosa sa fare un avera cowgirl!|CAMBIA_SFONDO|" + idStanza;
        } else {
            return "TESTO|Il passaggio sembra esserci, ma è bloccato da detriti. (Errore: Stanza di destinazione non configurata!)";
        }
    }
    
 
    private String gestisciVarco(GameDescription state, ClientState client, Room currentRoom) {
        // 1. Controlliamo se lo scarafaggio si è spostato
        boolean melaData = state.getFlags().getOrDefault("MELA_DATA", false);
        if (!melaData) {
            return "TESTO|Lo scarafaggio gigante blocca completamente il passaggio. Dovresti trovare un modo per distrarlo.";
        }

        // 2. LOGICA ACQUA: La leva è stata tirata da Jessie?
        boolean levaAggiustata = state.getFlags().getOrDefault("LEVA_AGGIUSTATA", false);
        Room destinazione = null;

        if (levaAggiustata) {
            // Se la leva è abbassata, il server cerca manualmente la Stanza Senza Acqua per mandarci il giocatore
            for (Room r : state.getRooms()) {
                if (r.getName().equalsIgnoreCase("Stanza Senza Acqua")) {
                    destinazione = r;
                    break;
                }
            }
        } else {
            // Altrimenti va nella destinazione standard (la Stanza con Acqua)
            destinazione = currentRoom.getExit("varco");
        }

        // 3. Esegue il movimento
        if (destinazione != null) {
            spostaGiocatore(state, client, destinazione);
            String idStanza = destinazione.getName().toUpperCase().replace(" ", "_");
            return "TESTO|Oltrepassi lo scarafaggio intento a mangiare la mela...<PAUSA>Sei arrivato in: " + destinazione.getName() + ".|CAMBIA_SFONDO|" + idStanza;
        } else {
            return "TESTO|Non sembra esserci un passaggio in quella direzione.";
        }
    }

    private String gestisciBotola(GameDescription state, ClientState client, Room currentRoom) {
        boolean levaAggiustata = state.getFlags().getOrDefault("LEVA_AGGIUSTATA", false);
        
        if (!levaAggiustata) {
            // L'acqua c'è ancora, vietato scendere!
            return "TESTO|La botola è completamente sommersa dall'acqua putrida!";
        }
        Room prossimaStanza = currentRoom.getExit("botola_sbloccata");
        spostaGiocatore(state, client, prossimaStanza);
        String idStanza = prossimaStanza.getName().toUpperCase().replace(" ", "_");
        // Se la leva è aggiustata, li lasciamo passare e sfidare il boss!
        return "TESTO|" + Dialoghi.getDialogoBossFinale() + "|CAMBIA_SFONDO|"+ idStanza;
    }

    private String eseguiMovimentoGenerico(String target, GameDescription state, ClientState client, Room currentRoom) {
        Room prossimaStanza = currentRoom.getExit(target);
        if (prossimaStanza != null) {
            spostaGiocatore(state, client, prossimaStanza);
            String idStanza = prossimaStanza.getName().toUpperCase().replace(" ", "_");
            return "TESTO|Attraversi il passaggio...<PAUSA>Sei arrivato in: " + prossimaStanza.getName() + ".|CAMBIA_SFONDO|" + idStanza;
        } else {
            return "TESTO|Non sembra esserci un passaggio in quella direzione.";
        }
    }
}