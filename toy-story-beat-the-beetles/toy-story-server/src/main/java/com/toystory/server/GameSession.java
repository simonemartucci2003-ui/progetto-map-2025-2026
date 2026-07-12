/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.server;

import com.toystory.server.database.DatabaseManager;
import java.util.ArrayList;
import java.util.List;

public class GameSession {
    private final String gameId;
    private final DatabaseManager db;
    private final List<ServerThread> players;

    // Quando creiamo una sessione, le diamo un ID e apriamo il suo database dedicato
    public GameSession(String gameId) throws Exception {
        this.gameId = gameId;
        // Modificheremo presto il DatabaseManager per accettare questo parametro
        this.db = DatabaseManager.getInstance(gameId); 
        this.players = new ArrayList<>();
    }

    // Aggiunge un giocatore alla stanza
    public synchronized void addPlayer(ServerThread player) {
        players.add(player);
    }

    // Rimuove un giocatore (es. se si disconnette)
    public synchronized void removePlayer(ServerThread player) {
        players.remove(player);
    }

    // Invia un messaggio SOLO ai giocatori di questa specifica stanza
    public synchronized void broadcast(String message) {
        for (ServerThread p : players) {
            p.sendMessage(message);
        }
    }

    public String getGameId() {
        return gameId;
    }

    public DatabaseManager getDb() {
        return db;
    }
}