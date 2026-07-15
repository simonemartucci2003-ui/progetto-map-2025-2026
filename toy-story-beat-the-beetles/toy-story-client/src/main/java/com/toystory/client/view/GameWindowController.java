package com.toystory.client.view;

import com.toystory.client.GameClient;

/**
 * Controller principale per la gestione del flusso di gioco nella {@link GameWindow}.
 * <p>
 * Questa classe implementa il ruolo di Controller nel pattern MVC. Gestisce la comunicazione 
 * bidirezionale:
 * <ul>
 *   <li>Riceve gli input dall'interfaccia utente (es. click, selezione azioni).</li>
 *   <li>Invia i comandi al server tramite {@link GameClient}.</li>
 *   <li>Aggiorna lo stato della vista e della logica interna ({@link MappaScenario}) 
 *       in risposta ai messaggi del server.</li>
 * </ul>
 * </p>
 * 
 */
public class GameWindowController {
    
    private final GameWindow finestra;
    private final GameClient clientRete;
    private final GUIHandler handlerGrafico;
    private String azioneSelezionata = "GUARDA";
    
    private final MappaScenario mappaScenario = new MappaScenario();
    
    /**
     * Inizializza il controller, collegando la vista principale e configurando il gestore di rete.
     * 
     * @param finestra La {@link GameWindow} associata al controller.
     */
    public GameWindowController(GameWindow finestra) {
        this.finestra = finestra;
        this.handlerGrafico = new GUIHandler(finestra, this);
        
        this.clientRete = new GameClient(messaggioServer -> {
            javax.swing.SwingUtilities.invokeLater(() -> {
                handlerGrafico.processaComando(messaggioServer);
            });
        });
    }
    
    /**
     * Avvia la logica di inizializzazione mostrando il menu di avvio.
     */
    public void avviaFlussoIniziale() {
        int scelta = GameDialogs.mostraMenuAvvio(finestra);

        if (scelta == 0) { 
            avviaNuovaPartita();
        } else if (scelta == 1 || scelta == 2) { 
            uniscitiPartita();
        } else {
            System.exit(0);
        }
    }
    
    /**
     * Avvia una nuova partita.
     */
    private void avviaNuovaPartita() {
        String gameId = clientRete.connectAndHandshake(true, "");
        if (gameId.equals("ERROR")) {
            GameDialogs.mostraErrore(finestra, "Errore server.");
            System.exit(0);
        } else {
            GameDialogs.mostraSuccessoCreazione(finestra, gameId);
            finestra.setTitle("Toy Story - Partita ID: " + gameId);
            finestra.setVisible(true); 
        }
    }
    
    /**
     * Permette di uniscri ad una partita già creata
     */
    private void uniscitiPartita() {
        String gameId = GameDialogs.chiediIdPartita(finestra);
        if (gameId != null && !gameId.trim().isEmpty()) {
            eseguiJoin(gameId.trim().toUpperCase());
        } else {
            System.exit(0);
        }
    }
    
    /**
     * Aggiorna lo scenario di gioco, cambiando la stanza corrente e aggiornando la grafica.
     * 
     * @param idStanza L'identificativo della nuova stanza da caricare.
     */
    public void cambiaStanzaMappa(String idStanza) {
        // 1. Aggiorna la logica interna
        mappaScenario.setStanzaCorrenteId(idStanza);
        finestra.scriviNelLog("[Cambio Scenario]: Ti sposti nella stanza ID " + idStanza);
        
        // 2. Chiede alla mappa quale immagine usare
        String percorsoImmagine = mappaScenario.getPercorsoImmagine(idStanza);
        
        // 3. Dice alla finestra di disegnare quell'immagine
        finestra.impostaSfondo(percorsoImmagine);
    }
    
    /**
     * Tenta di unire il client a una partita esistente tramite il suo identificativo.
     * <p>
     * Effettua l'handshake con il server; in caso di successo, salva il progresso
     * della partita localmente, aggiorna il titolo della finestra e rende visibile 
     * l'interfaccia di gioco. In caso di fallimento, notifica l'utente tramite 
     * un dialogo di errore e chiude l'applicazione.
     * </p>
     * 
     * @param gameId L'identificativo univoco della partita a cui connettersi.
     */
    private void eseguiJoin(String gameId) {
        String risultato = clientRete.connectAndHandshake(false, gameId);
        if (risultato.equals("SUCCESS")) {
            finestra.setTitle("Toy Story - Partita ID: " + gameId);
            finestra.setVisible(true);
        } else {
            GameDialogs.mostraErrore(finestra, "Partita non trovata.");
            System.exit(0);
        }
    }

    // --- AZIONI IN GIOCO ---
    
    /**
     * Imposta l'azione che il giocatore desidera compiere (es. "PRENDI", "USA").
     * 
     * @param verbo Il comando scelto dal giocatore.
     */
    public void cambiaAzione(String verbo) {
        this.azioneSelezionata = verbo;
        finestra.scriviNelLog("[Sistema]: Hai selezionato l'azione " + verbo + ". Ora clicca su un bersaglio.");
    }
    
    /**
     * Invia al server il comando di selezione di un personaggio.
     * 
     * @param nomePersonaggio Il nome del personaggio da selezionare.
     */
    public void selezionaPersonaggio(String nomePersonaggio) {
        clientRete.sendCommand("CHIAMA", nomePersonaggio);
        finestra.aggiornaBordiPersonaggi();
    }
    
    /**
     * Elabora il click dell'utente sullo scenario grafico, traducendo le coordinate 
     * in una richiesta di interazione con un oggetto o un'area.
     * 
     * @param x Coordinata X del mouse.
     * @param y Coordinata Y del mouse.
     * @param width Larghezza del pannello.
     * @param height Altezza del pannello.
     */
    public void elaboraClickScenario(int x, int y, int width, int height) {
        String target = mappaScenario.cercaTarget(x, y, width, height);
        if (target != null) {
            clientRete.sendCommand(azioneSelezionata, target);
        } else {
            finestra.scriviNelLog("[Sistema]: Lì non c'è nulla di interessante.");
        }
    }
    /**
     * 
     * Gestisce la disconnessione dal server e il ritorno al menu principale.
     */
    public void tornaAlMenu() {
        if (GameDialogs.confermaRitornoMenu(finestra)) {
            clientRete.disconnect();
            finestra.dispose();
            java.awt.EventQueue.invokeLater(() -> new GameWindow().setVisible(true));
        }
    }
}