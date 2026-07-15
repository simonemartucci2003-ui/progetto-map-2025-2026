package com.toystory.server;

import com.toystory.server.database.DatabaseManager;
import com.toystory.server.impl.ToyStoryGame;
import com.toystory.server.type.PlayableCharacter;
import com.toystory.server.type.Room;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameSession {
    private final String gameId;
    private final DatabaseManager db;
    private final List<ServerThread> players;
    private final ToyStoryGame game;
    private final Engine engine;

    // Chi controlla ciascun personaggio in questo momento (la "sedia occupata")
    private final Map<PlayableCharacter, ClientState> characterAssignments = new ConcurrentHashMap<>();

    public GameSession(String gameId) throws Exception {
        this.gameId = gameId;
        this.db = DatabaseManager.getInstance(gameId);
        this.players = new ArrayList<>();
        this.game = new ToyStoryGame();
        this.game.setDb(this.db);
        this.game.init();
        this.engine = new Engine(this.game);
    }

    public synchronized void addPlayer(ServerThread player) {
        players.add(player);
    }

    public synchronized void removePlayer(ServerThread player) {
        players.remove(player);
    }

    /**
     * Assegna al client il primo personaggio libero, al momento dell'ingresso in partita.
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
     * Il cuore della richiesta: prova a far passare 'client' al controllo di 'want'.
     * Fallisce SOLO se 'want' è controllato da un client diverso da quello richiedente.
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

    /** Alla disconnessione, libera tutti i personaggi che quel client stava controllando. */
    public synchronized void releaseClient(ClientState client) {
        characterAssignments.entrySet().removeIf(e -> e.getValue() == client);
    }

    private Room risolviStanzaIniziale(PlayableCharacter personaggio) {
        Room stanzaSalvata = game.loadCharacterRoom(personaggio);
        if (stanzaSalvata != null) return stanzaSalvata;
        List<Room> tutte = game.getRooms();
        return tutte.isEmpty() ? null : tutte.get(0);
    }

    public synchronized void broadcast(String message) {
        for (ServerThread p : players) {
            p.sendMessage(message);
        }
    }

    public String getGameId() { return gameId; }
    public DatabaseManager getDb() { return db; }
    public Engine getEngine() { return engine; }
}