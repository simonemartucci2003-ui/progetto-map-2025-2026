package com.toystory.server.impl;

import com.toystory.server.GameDescription;
import com.toystory.server.GameObserver;
import com.toystory.server.type.Command;
import com.toystory.server.type.CommandType;
import com.toystory.server.type.PlayableCharacter;
import com.toystory.server.ClientState;
import com.toystory.server.GameSession;

public class UseObserver implements GameObserver {

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


    private String gestisciGeneratore(GameDescription state) {
        boolean generatoreAcceso = state.getFlags().getOrDefault("GENERATORE_ACCESO", false);
        
        if (generatoreAcceso) {
            return "TESTO|Il generatore è già in funzione. Il suo ronzio riempie la stanza e la corrente è ripristinata.";
        } 
        
        state.saveFlag("GENERATORE_ACCESO", true);
        return "TESTO|Hai azionato la leva del generatore! I macchinari si riattivano con un ronzio metallico e la corrente elettrica torna a fluire." +
            "Buzz: Missione compiuta ragazzi!";
    }
    
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
     * Rimuove l'oggetto dalla memoria dell'inventario e dal Database.
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
     * Costruisce la stringa di risposta finale, formattando il testo e accodando 
     * i comandi necessari per rinfrescare l'interfaccia grafica dell'inventario.
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