/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.server;

import com.toystory.server.type.CommandType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import com.toystory.server.database.DatabaseManager;

/**
 * Thread dedicato alla gestione del flusso di comunicazione con un singolo client connesso.
 * Implementa il protocollo ad eventi via stringhe separate dal carattere pipe ('|'),
 * riceve gli input inviati dai bottoni della GUI del Client e rimanda le risposte
 * a tutti i giocatori attivi sfruttando la logica di Broadcast.
 * * @author Il Tuo Nome / Gruppo
 */
public class ServerThread extends Thread {

    /** Il canale socket per parlare con il client specifico */
    private final Socket socket;
    
    /** Riferimento all'Engine di gioco condiviso */
    private final Engine engine;
    
    /** Canale di output per inviare stringhe di testo verso il rispettivo Client */
    private PrintWriter out;

    /** NUOVO: La sessione (stanza) a cui questo giocatore appartiene */
    private GameSession session;

    /**
     * Costruttore del thread di gestione client.
     * * @param socket Il socket generato dal metodo accept() del ServerMain.
     * @param engine L'istanza dell'Engine di gioco centralizzata.
     */
    public ServerThread(Socket socket, Engine engine) {
        this.socket = socket;
        this.engine = engine;
    }

    /**
     * NUOVO METODO: Assegna questo thread a una specifica stanza di gioco.
     */
    public void setSession(GameSession session) {
        this.session = session;
    }
    
    /**
     * Ciclo di esecuzione del Thread. Gestisce l'apertura dei flussi I/O, 
     * la lettura dei comandi in arrivo dai bottoni e l'invio in broadcast.
     */
    @Override
    public void run() {
        // Apriamo il flusso di input per leggere cosa invia il Client (click dei bottoni)
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            // Inizializziamo il canale di output con l'autoflush attivo (invia subito i dati senza bufferizzare)
            this.out = new PrintWriter(socket.getOutputStream(), true);
            
            // ========================================================
            // FASE 1: HANDSHAKE (Creazione o Unione alla Partita)
            // ========================================================
            String initialCommand = in.readLine();
            
            if (initialCommand != null) {
                if (initialCommand.equals("CREA_PARTITA")) {
                   try {
                        // 1. Generiamo un ID casuale di 6 caratteri
                        String newGameId = java.util.UUID.randomUUID().toString().substring(0, 6).toUpperCase();
                        
                        // 2. Creiamo la nuova sessione (Ora in un blocco protetto!)
                        GameSession nuovaSessione = new GameSession(newGameId);
                        ServerMain.activeSessions.put(newGameId, nuovaSessione);
                        
                        // 3. Inseriamo questo giocatore nella sessione
                        this.setSession(nuovaSessione);
                        nuovaSessione.addPlayer(this);
                        
                        // 4. Rispondiamo al Client con il codice generato
                        out.println("PARTITA_CREATA|" + newGameId);
                        System.out.println("[Server] Nuova partita creata con ID: " + newGameId);
                        
                    } catch (Exception e) {
                        // Se la creazione del DB fallisce, avvisiamo il client invece di far crashare il server
                        out.println("ERRORE|Impossibile creare la stanza sul server.");
                        System.err.println("[Server] Errore creazione sessione: " + e.getMessage());
                    }

                } else if (initialCommand.startsWith("UNISCITI_PARTITA|")) {
                    String gameId = initialCommand.split("\\|")[1];
                    
                    // Controlliamo se la partita esiste nel registro globale del ServerMain
                    if (ServerMain.activeSessions.containsKey(gameId)) {
                        GameSession sessioneEsistente = ServerMain.activeSessions.get(gameId);
                        
                        this.setSession(sessioneEsistente);
                        sessioneEsistente.addPlayer(this);
                        
                        out.println("CONNESSIONE_SUCCESSO|");
                        System.out.println("[Server] Un giocatore si è unito alla partita: " + gameId);
                    } else {
                        // Partita non trovata: avvisiamo il client e chiudiamo la connessione
                        out.println("ERRORE|Partita non trovata");
                        socket.close();
                        return; // Terminiamo il thread
                    }
                }
            }

            // ========================================================
            // FASE 2: GIOCO VERO E PROPRIO (Gameplay)
            // ========================================================

            String inputLine;
            
            // Restiamo in ascolto continuo dei messaggi stringa inviati dal Client attraverso la rete
            while ((inputLine = in.readLine()) != null) {
                System.out.println("[Multiplayer - Log] Ricevuto dal client: " + inputLine);

                // Protocollo strutturato: spacchiamo la stringa usando il carattere '|' come separatore
                // Usiamo il limite -1 per non perdere i campi vuoti alla fine (es. "CHIAMA|")
                String[] parts = inputLine.split("\\|", -1);
                if (parts.length < 1 || parts[0].isEmpty()) {
                    continue; // Stringa non valida, passiamo al prossimo ciclo
                }

                try {
                    // 1. Mappiamo la stringa inviata dal bottone direttamente nella costante dell'enum CommandType
                    CommandType tipoComando = CommandType.valueOf(parts[0].toUpperCase());
                    
                    // 2. Estraiamo il target (l'oggetto o stanza cliccata), se non c'è passiamo una stringa vuota
                    String target = (parts.length > 1) ? parts[1] : "";

                    // Usiamo il database specifico della sessione di questo thread
                    DatabaseManager db = this.session.getDb();

                    synchronized (engine) {
                        try {
                            // --- MODIFICA DATABASE: transazione sulla sessione corrente ---
                            db.startTransaction();

                            // 2. Interroghiamo l'Engine
                            String rispostaServer = engine.executeAction(tipoComando, target);

                            // 3. Inviamo il risultato o annulliamo
                            if (rispostaServer != null) {
                                db.commitTransaction();
                                // Inviamo l'aggiornamento a tutti i giocatori della STESSA sessione
                                this.session.broadcast(rispostaServer);
                            } else {
                                db.rollbackTransaction();
                            }
                        } catch (Exception e) {
                           System.err.println("[Server] Errore critico durante la transazione: " + e.getMessage());
                            
                            // Proteggiamo il rollback nel caso in cui il DB sia completamente irraggiungibile
                            try {
                                if (db != null) {
                                    db.rollbackTransaction();
                                }
                            } catch (java.sql.SQLException ex) {
                                System.err.println("[Server] Fallimento critico: Impossibile eseguire il rollback. " + ex.getMessage());
                            }
                        }
                    }

                } catch (IllegalArgumentException e) {
                    out.println("TESTO|Comando sconosciuto o non riconosciuto dal sistema.");
                }
            }
            
        } catch (IOException e) {
            System.out.println("[Server] Connessione chiusa bruscamente o persa con un giocatore.");
        } finally {
            // Rimuoviamo il thread dal registro multiplayer
            ServerMain.clientThreads.remove(this);
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Errore durante la chiusura della socket: " + e.getMessage());
            }
        }
    }

    /**
     * Metodo di servizio per spedire un messaggio testuale privato a questo specifico client.
     * * @param msg Il messaggio stringa da trasmettere sulla rete.
     */
    public void sendMessage(String msg) {
        if (out != null) {
            out.println(msg);
        }
    }

    /**
     * Invia un messaggio in Broadcast a TUTTI i client attualmente connessi al server.
     * Utilizza un blocco di sincronizzazione sulla lista condivisa per evitare 
     * problemi di concorrenza (es. se un client si disconnette mentre stiamo inviando dati).
     * * @param msg Il messaggio risultante dall'azione di gioco da notificare a tutti.
     */
    private void sendToAllPlayers(String msg) {
        synchronized (ServerMain.clientThreads) {
            // Cicliamo su tutti i thread dei giocatori registrati nel ServerMain
            for (ServerThread thread : ServerMain.clientThreads) {
                thread.sendMessage(msg); // Spediamo l'aggiornamento
            }
        }
    }
}