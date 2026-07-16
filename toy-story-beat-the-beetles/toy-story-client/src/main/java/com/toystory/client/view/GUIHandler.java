package com.toystory.client.view;

/**
 * Gestisce l'interpretazione dei messaggi inviati dal server, traducendoli in
 * aggiornamenti grafici sulla {@link GameWindow}.
 * <p>
 * Questa classe funge da interprete tra il protocollo di rete e l'interfaccia 
 * utente, garantendo che i comandi ricevuti dal server siano processati in modo 
 * sicuro e ordinato.
 * </p>
 * 
 */
public class GUIHandler {
    
    private final GameWindow finestra; // Riferimento alla tua GUI di Matisse
    private final GameWindowController controller;
    
    /**
     * Costruisce un nuovo interprete di comandi grafici.
     * 
     * @param finestra La finestra di gioco principale da aggiornare.
     * @param controller Il controller che gestisce la logica di gioco.
     */
    public GUIHandler(GameWindow finestra, GameWindowController controller) {
        this.finestra = finestra;
        this.controller = controller;
    }

    /**
     * Processa un messaggio testuale proveniente dal server, suddividendolo in comandi
     * e parametri basati sul separatore '|'.
     * <p>
     * Esegue il parsing ciclico dei comandi e aggiorna la UI tramite chiamate ai metodi 
     * appropriati della {@link GameWindow} o del {@link GameWindowController}.
     * </p>
     * 
     * @param messaggioServer La stringa di comando grezza inviata dal server (es. "TESTO|Ciao!|CAMBIA_SFONDO|2").
     */
    public void processaComando(String messaggioServer) {
        if (messaggioServer == null || messaggioServer.isEmpty()) return;

        String[] tokens = messaggioServer.split("\\|");
        int i = 0;
        
        while (i < tokens.length) {
            String comando = tokens[i].toUpperCase();
            
            if (i + 1 >= tokens.length) {
                break;
            }
            
            String valore = tokens[i + 1];

            switch (comando) {
                case "TESTO":
                    // Accede ai metodi pubblici della tua finestra per scrivere il testo
                    finestra.stampaTestoConPausa(valore);
                    i += 2; // Consuma 2 pezzi: (COMANDO | TESTO)
                    break;
                    
                case "CAMBIA_SFONDO":
                    // Cambia lo sfondo della stanza nella finestra
                    // Delega al controller l'aggiornamento della mappa e della vista
                    controller.cambiaStanzaMappa(valore);
                    i += 2;
                    break;
                    
                case "SWITCH_AVATAR":
                    i += 2;
                    break;
                    
                case "ABILITA":
                    String iconaAbilita = "";
                    // Controlliamo se c'è un terzo pezzo (l'icona) senza andare fuori dai limiti
                    if (i + 2 < tokens.length) {
                        iconaAbilita = tokens[i + 2];
                        i += 3; // Consuma 3 pezzi: (ABILITA | NOME | ICONA)
                    } else {
                        i += 2; // Fallback se manca l'icona
                    }
                    finestra.aggiornaSlotAbilita(valore, iconaAbilita);
                    break;
                    
                case "INVENTARIO":
                    String iconaOggetto = "";
                    if (i + 2 < tokens.length) {
                        iconaOggetto = tokens[i + 2];
                        i += 3; // Consuma 3 pezzi: (INVENTARIO | NOME | ICONA)
                    } else {
                        i += 2; // Fallback di emergenza
                    }
                    finestra.aggiungiAllInventario(valore, iconaOggetto);
                    break;
                    
                case "CLEAR_INVENTORY":
                    finestra.svuotaInventario();
                    i += 2; 
                    break;
                    
                case "PERSONAGGIO_ATTIVO":
                    finestra.aggiornaBordiPersonaggi(valore);
                    i += 2;
                    break;
                
                default:
                    System.err.println("[GUIHandler] Comando sconosciuto o non supportato: " + comando);
                    i += 2; // Avanzamento di sicurezza per non bloccare il parsing
                    break;
            }
        }
    }
}