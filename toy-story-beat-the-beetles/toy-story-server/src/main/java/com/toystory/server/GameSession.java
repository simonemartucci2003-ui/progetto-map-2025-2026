package com.toystory.server;

import com.toystory.server.database.DatabaseManager;
import com.toystory.server.impl.ToyStoryGame;
import java.util.ArrayList;
import java.util.List;

public class GameSession {
    private final String gameId;
    private final DatabaseManager db;
    private final List<ServerThread> players;
    
    // Aggiungiamo l'Engine e il Game specifici per questa sessione
    private final ToyStoryGame game;
    private final Engine engine;

    public GameSession(String gameId) throws Exception {
        this.gameId = gameId;
        this.db = DatabaseManager.getInstance(gameId);
        this.players = new ArrayList<>();

        // 1. Creiamo un mondo di gioco totalmente isolato per questa partita
        this.game = new ToyStoryGame();
        
        // 2. IMPORTANTISSIMO: Diamo il DB al gioco PRIMA di fare init() 
        // affinché syncWorldWithDatabase() funzioni correttamente
        this.game.setDb(this.db);
        this.game.init();

        // 3. Creiamo un motore isolato che gestirà solo questo specifico mondo
        this.engine = new Engine(this.game);
    }

    public synchronized void addPlayer(ServerThread player) {
        players.add(player);
    }

    public synchronized void removePlayer(ServerThread player) {
        players.remove(player);
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