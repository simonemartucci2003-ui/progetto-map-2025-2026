package com.toystory.client.view;

import com.toystory.client.GameClient;
import com.toystory.client.SaveManager;

public class GameWindowController {
    
    private final GameWindow finestra;
    private final GameClient clientRete;
    private final GUIHandler handlerGrafico;
    private String azioneSelezionata = "GUARDA";
    
    private final MappaScenario mappaScenario = new MappaScenario();

    public GameWindowController(GameWindow finestra) {
        this.finestra = finestra;
        this.handlerGrafico = new GUIHandler(finestra, this);
        
        this.clientRete = new GameClient(messaggioServer -> {
            javax.swing.SwingUtilities.invokeLater(() -> {
                handlerGrafico.processaComando(messaggioServer);
            });
        });
    }

    public void avviaFlussoIniziale() {
        java.util.Set<String> partite = SaveManager.leggiTutteLePartite();
        int scelta = GameDialogs.mostraMenuAvvio(finestra, !partite.isEmpty());

        if (scelta == 0) { 
            avviaNuovaPartita();
        } else if (scelta == 1 || scelta == 2) { 
            uniscitiPartita();
        } else {
            System.exit(0);
        }
    }

    private void avviaNuovaPartita() {
        String gameId = clientRete.connectAndHandshake(true, "");
        if (gameId.equals("ERROR")) {
            GameDialogs.mostraErrore(finestra, "Errore server.");
            System.exit(0);
        } else {
            SaveManager.salvaPartita(gameId);
            GameDialogs.mostraSuccessoCreazione(finestra, gameId);
            finestra.setTitle("Toy Story - Partita ID: " + gameId);
            
            finestra.setVisible(true); 
        }
    }

    private void uniscitiPartita() {
        String gameId = GameDialogs.chiediIdPartita(finestra);
        if (gameId != null && !gameId.trim().isEmpty()) {
            eseguiJoin(gameId.trim().toUpperCase());
        } else {
            System.exit(0);
        }
    }
    
    public void cambiaStanzaMappa(String idStanza) {
        mappaScenario.setStanzaCorrenteId(idStanza);
    }

    private void eseguiJoin(String gameId) {
        String risultato = clientRete.connectAndHandshake(false, gameId);
        if (risultato.equals("SUCCESS")) {
            SaveManager.salvaPartita(gameId);
            finestra.setTitle("Toy Story - Partita ID: " + gameId);
            finestra.setVisible(true);
        } else {
            GameDialogs.mostraErrore(finestra, "Partita non trovata.");
            System.exit(0);
        }
    }

    // --- AZIONI IN GIOCO ---
    
    public void cambiaAzione(String verbo) {
        this.azioneSelezionata = verbo;
        finestra.scriviNelLog("[Sistema]: Hai selezionato l'azione " + verbo + ". Ora clicca su un bersaglio.");
    }

    public void selezionaPersonaggio(String nomePersonaggio) {
        clientRete.sendCommand("CHIAMA", nomePersonaggio);
        finestra.aggiornaBordiPersonaggi();
    }

    public void elaboraClickScenario(int x, int y, int width, int height) {
        String target = mappaScenario.cercaTarget(x, y, width, height);
        if (target != null) {
            clientRete.sendCommand(azioneSelezionata, target);
        } else {
            finestra.scriviNelLog("[Sistema]: Lì non c'è nulla di interessante.");
        }
    }

    public void tornaAlMenu() {
        if (GameDialogs.confermaRitornoMenu(finestra)) {
            clientRete.disconnect();
            finestra.dispose();
            java.awt.EventQueue.invokeLater(() -> new GameWindow().setVisible(true));
        }
    }
}