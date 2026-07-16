package com.toystory.server;

import com.toystory.server.impl.ToyStoryGame;
import com.toystory.server.type.Command;
import com.toystory.server.type.CommandType;

/**
 * Motore logico principale della sessione di gioco.
 * <p>
 * Agisce come orchestratore centrale: estende {@link GameObservable} per gestire 
 * la registrazione dei vari Observer (azioni) e intercetta i comandi in ingresso 
 * per distribuirli. Fornisce anche metodi di utilità per la sincronizzazione del client.
 * </p>
 */
public class Engine extends GameObservable<String>  {

    private final ToyStoryGame game;
    
    /**
     * Inizializza il motore logico e iscrive tutti gli Observer necessari 
     * al corretto funzionamento delle interazioni di base.
     * 
     * @param game L'istanza principale della partita contenente la mappa e le regole.
     */
    public Engine(ToyStoryGame game) {
        this.game = game;
        this.addObserver(new com.toystory.server.impl.LookAtObserver());
        this.addObserver(new com.toystory.server.impl.PickUpObserver());
        this.addObserver(new com.toystory.server.impl.UseObserver());
        this.addObserver(new com.toystory.server.impl.MoveObserver());
        this.addObserver(new com.toystory.server.impl.CallObserver());
        this.addObserver(new com.toystory.server.impl.TalkObserver());
    }
    
    /**
     * Confeziona il comando e lo smista agli observer registrati.
     * 
     * @param type La tipologia dell'azione (es. GUARDA, PRENDI).
     * @param targetName Il nome logico del bersaglio dell'azione.
     * @param client Lo stato del giocatore che ha richiesto l'azione.
     * @param session La sessione a cui il giocatore appartiene.
     * @return La stringa di risposta elaborata dalla logica di gioco, pronta per essere inviata via rete.
     */
    public String executeAction(CommandType type, String targetName, ClientState client, GameSession session) {
        if (type == null) {
            return "TESTO|Azione non valida.";
        }
        Command comando = new Command(type, targetName);
        return this.notifyObservers(comando, game, client, session, "TESTO|Non succede nulla. Non è un'azione valida in questo momento.");
    }

    /**
     * Costruisce il pacchetto di rete per sincronizzare l'interfaccia grafica del client 
     * con il reale stato del server (sfondo, avatar, abilità, inventario).
     * <p>
     * Particolarmente utile alla connessione iniziale o dopo un ricollegamento.
     * </p>
     * 
     * @param client Lo stato del client da sincronizzare.
     * @return La stringa codificata con le istruzioni per l'interfaccia, o null se non c'è nulla da sincronizzare.
     */
    public String buildResumeSyncMessage(ClientState client) {
       StringBuilder sb = new StringBuilder();
       if (client.getCurrentRoom() != null) {
           String idStanza = client.getCurrentRoom().getName().toUpperCase().replace(" ", "_");
           sb.append("CAMBIA_SFONDO|").append(idStanza);
       }
       String characterFragment = game.buildCharacterStatusFragment(client.getCurrentCharacter());
       if (!characterFragment.isEmpty()) {
           if (sb.length() > 0) sb.append("|");
           sb.append(characterFragment);
       }
       return sb.length() > 0 ? sb.toString() : null;
    }
}