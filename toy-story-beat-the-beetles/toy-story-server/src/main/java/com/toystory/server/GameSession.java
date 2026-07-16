package com.toystory.server;

import com.toystory.server.database.DatabaseManager;
import com.toystory.server.impl.ToyStoryGame;
import com.toystory.server.type.PlayableCharacter;
import com.toystory.server.type.Room;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rappresenta un'istanza isolata di una partita multiplayer (una "Stanza" o "Lobby").
 * <p>
 * Ogni GameSession raggruppa tutti i componenti necessari per far funzionare una 
 * partita indipendente: un ID univoco, un database dedicato ({@link DatabaseManager}), 
 * l'istanza del mondo di gioco ({@link ToyStoryGame}), il motore degli eventi ({@link Engine}) 
 * e la lista dei client connessi ({@link ServerThread}).
 * </p>
 * <p>
 * Gestisce in modo thread-safe l'assegnazione esclusiva dei personaggi giocabili 
 * ai vari client, impedendo che due giocatori controllino lo stesso avatar contemporaneamente.
 * </p>
 */
public class GameSession {
    private final String gameId;
    private final DatabaseManager db;
    private final List<ServerThread> players;
    private final ToyStoryGame game;
    private final Engine engine;

    /** Mappa thread-safe per tracciare quale client controlla attualmente quale personaggio (la "sedia occupata") */
    private final Map<PlayableCharacter, ClientState> characterAssignments = new ConcurrentHashMap<>();
    
    /**
     * Inizializza una nuova sessione di gioco isolata.
     * 
     * @param gameId Il codice alfanumerico univoco che identifica questa partita.
     * @throws Exception Se si verifica un errore critico durante l'inizializzazione del database o del mondo di gioco.
     */
    public GameSession(String gameId) throws Exception {
        this.gameId = gameId;
        this.db = DatabaseManager.getInstance(gameId);
        this.players = new ArrayList<>();
        this.game = new ToyStoryGame();
        this.game.setDb(this.db);
        this.game.init();
        this.engine = new Engine(this.game);
    }
    
    /**
     * Aggiunge un nuovo giocatore (Thread) a questa sessione.
     * 
     * @param player Il thread di rete associato al client che si è appena unito.
     */
    public synchronized void addPlayer(ServerThread player) {
        players.add(player);
    }
    
    /**
     * Rimuove un giocatore dalla sessione (es. in caso di disconnessione).
     * 
     * @param player Il thread di rete da rimuovere.
     */
    public synchronized void removePlayer(ServerThread player) {
        players.remove(player);
    }

    /**
     * Assegna automaticamente al client il primo personaggio libero disponibile.
     * <p>
     * Viene invocato al momento dell'ingresso in partita per garantire che il 
     * giocatore abbia subito un avatar da controllare.
     * </p>
     * 
     * @param client Lo stato del client a cui assegnare il personaggio.
     * @return Il {@link PlayableCharacter} assegnato, oppure null se la partita è piena.
     */
    public synchronized PlayableCharacter assignInitialCharacter(ClientState client) {
        for (PlayableCharacter candidato : game.getPlayers()) {
            if (!characterAssignments.containsKey(candidato)) {
                characterAssignments.put(candidato, client);
                client.setCurrentCharacter(candidato);
                client.setCurrentRoom(risolviStanzaIniziale(candidato));
                return candidato;
            }
        }
        return null; // partita piena (tutti i personaggi già occupati)
    }

    /**
     * Gestisce la richiesta di un client di cambiare il personaggio controllato.
     * <p>
     * L'operazione fallisce esclusivamente se il personaggio richiesto è già 
     * attivamente controllato da un altro client in questa sessione.
     * </p>
     * 
     * @param want Il personaggio di cui il client desidera prendere il controllo.
     * @param client Lo stato del client che fa la richiesta.
     * @return true se il cambio è avvenuto con successo, false se il personaggio è occupato.
     */
    public synchronized boolean switchCharacter(PlayableCharacter want, ClientState client) {
        ClientState occupante = characterAssignments.get(want);
        if (occupante != null && occupante != client) {
            return false; // occupato da un altro giocatore: rifiuta
        }

        // libera la sedia che stava occupando prima, e si siede su quella nuova
        characterAssignments.remove(client.getCurrentCharacter());
        characterAssignments.put(want, client);
        client.setCurrentCharacter(want);
        client.setCurrentRoom(risolviStanzaIniziale(want));
        return true;
    }

    /**
     * Libera tutti i personaggi attualmente controllati da un client specifico.
     * 
     * @param client Il client che si sta disconnettendo o che rilascia i controlli.
     */
    public synchronized void releaseClient(ClientState client) {
        characterAssignments.entrySet().removeIf(e -> e.getValue() == client);
    }
    
    /**
     * Determina in quale stanza deve comparire il personaggio (spawn logic).
     * 
     * @param personaggio Il personaggio di cui risolvere la posizione.
     * @return La {@link Room} salvata in precedenza, o la stanza iniziale di default.
     */
    private Room risolviStanzaIniziale(PlayableCharacter personaggio) {
        Room stanzaSalvata = game.loadCharacterRoom(personaggio);
        if (stanzaSalvata != null) return stanzaSalvata;
        List<Room> tutte = game.getRooms();
        return tutte.isEmpty() ? null : tutte.get(0);
    }
    
    /**
     * Invia un messaggio testuale a tutti i giocatori attualmente connessi a questa sessione.
     * 
     * @param message Il messaggio stringa da inoltrare in broadcast.
     */
    public synchronized void broadcast(String message) {
        for (ServerThread p : players) {
            p.sendMessage(message);
        }
    }
    
    /**
     * @return L'identificativo univoco di questa partita.
     */
    public String getGameId() { return gameId; }
    
    /**
     * @return L'istanza del DatabaseManager isolata per questa partita.
     */
    public DatabaseManager getDb() { return db; }
    
    /**
     * @return Il motore logico incaricato di elaborare le azioni per questa partita.
     */
    public Engine getEngine() { return engine; }
}