package com.toystory.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Classe entry-point principale per l'esecuzione del Server di gioco.
 * <p>
 * Ha la responsabilità di avviare l'infrastruttura di rete mettendosi in ascolto 
 * delle richieste in ingresso dai client. Gestisce inoltre il registro globale 
 * delle sessioni di gioco attive e dei thread dei singoli giocatori, garantendo 
 * che le risorse (come le connessioni al database) vengano rilasciate correttamente 
 * alla chiusura dell'applicazione.
 * </p>
 */
public class ServerMain {

    /** 
     * Porta TCP standard sulla quale il server rimarrà in ascolto per le connessioni in ingresso. 
     */
    public static final int PORT = 6666; // Messa public così anche il Client può leggerla se serve
    
    /** 
     * Lista thread-safe globale che contiene tutti i {@link ServerThread} attualmente attivi.
     * <p>
     * Utilizzata per monitorare i giocatori connessi e per permettere, in caso di necessità, 
     * l'invio di messaggi broadcast a livello globale (attraverso tutte le sessioni).
     * </p>
     */
    public static final List<ServerThread> clientThreads = Collections.synchronizedList(new ArrayList<>());

    /** 
     * Registro globale di tutte le stanze (o partite) attive nel server.
     * <p>
     * Mappa l'ID alfanumerico univoco della partita (es. "AB1234") all'istanza 
     * della relativa {@link GameSession}, permettendo ai nuovi client di unirsi 
     * in tempo reale a una partita esistente in modo thread-safe.
     * </p>
     */
    public static final Map<String, GameSession> activeSessions = new ConcurrentHashMap<>();
    
    /**
     * Punto di avvio dell'applicazione Server.
     * <p>
     * Esegue le seguenti operazioni sequenziali:
     * <ol>
     *   <li>Registra un <i>Shutdown Hook</i> per chiudere in sicurezza tutti i database all'arresto.</li>
     *   <li>Apre un {@link ServerSocket} sulla porta predefinita.</li>
     *   <li>Entra in un loop infinito in cui accetta le connessioni dei client e delega la gestione a nuovi {@link ServerThread}.</li>
     * </ol>
     * </p>
     * 
     * @param args Argomenti passati da riga di comando (attualmente non utilizzati).
     */
    public static void main(String[] args) {
        // Registrazione dello Shutdown Hook per garantire la chiusura sicura dei database
        // quando il processo del server viene interrotto (es. CTRL+C o chiusura forzata).
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("[Server] Spegnimento in corso, salvataggio dei database...");
            for (GameSession session : activeSessions.values()) {
                try {
                    if (session.getDb() != null) {
                        session.getDb().closeConnection();
                        System.out.println("[Server] Salvato DB per la partita: " + session.getGameId());
                    }
                } catch (SQLException e) {
                    System.err.println("[Server] Errore chiusura DB per la partita " + session.getGameId() + ": " + e.getMessage());
                }
            }
        }));
        
        System.out.println("==========================================");
        System.out.println("   TOY STORY MULTIPLAYER SERVER STARTED   ");
        System.out.println("==========================================");

        try {
            

            // Apertura del socket del server protetto dal costrutto try-with-resources
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                System.out.println("[Server] In ascolto dei client sulla porta " + PORT + "...");

                // Ciclo infinito di ascolto: il server accetta continuamente nuove connessioni
                while (true) {
                    // Il thread principale si blocca (listen) finché un client non si connette
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("[Server] Nuovo giocatore connesso dal Client: " + clientSocket.getRemoteSocketAddress());

                    // Istanziamo un Thread dedicato a gestire la comunicazione bidirezionale con questo specifico client
                    ServerThread thread = new ServerThread(clientSocket);
                    
                    // Registriamo il thread appena creato nella lista globale prima di avviarlo
                    clientThreads.add(thread);
                    
                    // Avviamo il thread, che eseguirà il proprio metodo run() in parallelo
                    thread.start();
                }
                
            } catch (IOException e) {
                System.err.println("[Server] Errore critico di rete nella ServerSocket: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("[Server] Errore fatale durante l'inizializzazione del gioco: " + e.getMessage());
        }
    }
}