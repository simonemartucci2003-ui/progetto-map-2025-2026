/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.server;

import com.toystory.server.impl.ToyStoryGame;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Classe Main del Server per l'applicazione Toy Story.
 * Si occupa di inizializzare lo stato univoco del gioco, configurare il motore 
 * logico (Engine) e gestire l'accettazione dei client tramite una ServerSocket.
 * Mantiene inoltre il registro globale dei thread per la funzionalità multiplayer.
 * * @author Il Tuo Nome / Gruppo
 */
public class ServerMain {

    /** Porta standard sulla quale il server rimarrà in ascolto */
    private static final int PORT = 6666;
    
    /** * Lista thread-safe globale che contiene tutti i ServerThread attivi.
     * Utilizzata per inviare i messaggi in broadcast (multiplayer cooperativo) 
     * in modo che ogni modifica al mondo di gioco sia istantaneamente visibile a tutti.
     */
    public static final List<ServerThread> clientThreads = Collections.synchronizedList(new ArrayList<>());

    /**
     * Punto di ingresso principale del Server.
     * * @param args Argomenti della riga di comando (non utilizzati).
     */
    public static void main(String[] args) {
        // 1. Inizializziamo l'istanza unica del gioco e carichiamo le stanze/personaggi
        ToyStoryGame game = new ToyStoryGame();
        game.init();
        
        // 2. Inizializziamo il motore logico che elaborerà i comandi ad eventi (senza parser)
        Engine engine = new Engine(game);

        System.out.println("==========================================");
        System.out.println("   TOY STORY MULTIPLAYER SERVER STARTED   ");
        System.out.println("==========================================");
        System.out.println("[Server] Logica di gioco caricata ed Engine attivo.");

        // 3. Apertura della ServerSocket protetta da un blocco try-with-resources
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("[Server] In ascolto dei client sulla porta " + PORT + "...");

            // Ciclo infinito di ascolto: il server non muore mai e accetta continuamente connessioni
            while (true) {
                // Il server si blocca qui (metodo bloccante) finché un modulo client non effettua il "connect"
                Socket clientSocket = serverSocket.accept();
                System.out.println("[Server] Nuovo giocatore connesso dal Client: " + clientSocket.getRemoteSocketAddress());

                // 4. Istanziamo un Thread dedicato a gestire la comunicazione con questo specifico client
                ServerThread thread = new ServerThread(clientSocket, engine);
                
                // Registriamo il thread appena creato nella lista globale prima di avviarlo
                clientThreads.add(thread);
                
                // Avviamo il thread (invocando internamente il metodo run())
                thread.start();
            }
            
        } catch (IOException e) {
            System.err.println("[Server] Errore critico di rete nella ServerSocket: " + e.getMessage());
        }
    }
}