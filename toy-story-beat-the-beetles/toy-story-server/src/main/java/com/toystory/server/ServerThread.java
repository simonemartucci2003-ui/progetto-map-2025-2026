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
import com.toystory.server.type.PlayableCharacter;

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
    
    
    /** Canale di output per inviare stringhe di testo verso il rispettivo Client */
    private PrintWriter out;

    /** NUOVO: La sessione (stanza) a cui questo giocatore appartiene */
    private GameSession session;
    
    private final ClientState clientState = new ClientState();

    /**
     * Costruttore del thread di gestione client.
     * * @param socket Il socket generato dal metodo accept() del ServerMain.
     * @param socket
     */
    public ServerThread(Socket socket) {
        this.socket = socket;
        
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
                        String newGameId = java.util.UUID.randomUUID().toString().substring(0, 6).toUpperCase();
                        GameSession nuovaSessione = new GameSession(newGameId);
                        ServerMain.activeSessions.put(newGameId, nuovaSessione);

                        this.setSession(nuovaSessione);
                        nuovaSessione.addPlayer(this);

                        PlayableCharacter personaggio = nuovaSessione.assignInitialCharacter(clientState);
                        if (personaggio == null) {
                            out.println("ERRORE|Impossibile assegnare un personaggio.");
                            socket.close();
                            return;
                        }

                        out.println("PARTITA_CREATA|" + newGameId + "|" + personaggio.getName());
                        System.out.println("[Server] Nuova partita creata con ID: " + newGameId
                                + " - Host controlla: " + personaggio.getName());

                        String syncMsg = nuovaSessione.getEngine().buildResumeSyncMessage(clientState);
                        if (syncMsg != null && !syncMsg.isEmpty()) {
                            out.println(syncMsg);
                        }

                    } catch (Exception e) {
                        out.println("ERRORE|Impossibile creare la stanza sul server.");
                        System.err.println("[Server] Errore creazione sessione: " + e.getMessage());
                        if (e.getCause() != null) {
                            System.err.println("[Server] Causa reale: " + e.getCause());
                        }
                        e.printStackTrace();
                    }

                } else if (initialCommand.startsWith("UNISCITI_PARTITA|")) {
                    String gameId = initialCommand.split("\\|")[1].toUpperCase().trim();

                    try {
                        // computeIfAbsent = operazione atomica: evita che due client, unendosi
                        // nello stesso istante, creino due sessioni diverse per la stessa partita
                        GameSession sessione = ServerMain.activeSessions.computeIfAbsent(gameId, id -> {
                            java.io.File dbFile = new java.io.File("./saves/toystory_" + id + ".mv.db");
                            if (dbFile.exists()) {
                                try {
                                    System.out.println("[Server] Partita " + id + " trovata su disco, ricarico la sessione...");
                                    return new GameSession(id);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            return null;
                        });

                        if (sessione != null) {
                            this.setSession(sessione);
                            sessione.addPlayer(this);

                            PlayableCharacter personaggio = sessione.assignInitialCharacter(clientState);
                            if (personaggio == null) {
                                out.println("ERRORE|Partita piena, tutti i personaggi sono già controllati.");
                                sessione.removePlayer(this);
                                socket.close();
                                return;
                            }

                            out.println("CONNESSIONE_SUCCESSO|" + personaggio.getName());
                            System.out.println("[Server] Un giocatore si è unito alla partita: " + gameId
                                    + " - controlla: " + personaggio.getName());

                            String syncMsg = sessione.getEngine().buildResumeSyncMessage(clientState);
                            if (syncMsg != null && !syncMsg.isEmpty()) {
                                out.println(syncMsg);
                            }
                        } else {
                            out.println("ERRORE|Partita non trovata");
                            socket.close();
                            return;
                        }

                    } catch (Exception e) {
                        System.err.println("[Server] Errore nel ripristino della sessione " + gameId + ": " + e.getMessage());
                        out.println("ERRORE|Impossibile caricare la partita.");
                        socket.close();
                        return;
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
                    Engine sessionEngine = this.session.getEngine(); // Usiamo l'engine della sessione!

                    // Il blocco synchronized ora protegge SOLO i giocatori della stessa partita.
                    // Questo serializza perfettamente le operazioni CRUD sul database, 
                    // prevenendo qualsiasi problema di concorrenza su H2!

                    synchronized (sessionEngine) {
                        try {
                            db.startTransaction();
                            String rispostaServer = sessionEngine.executeAction(tipoComando, target, clientState, this.session);
                            if (rispostaServer != null) {
                                db.commitTransaction();
                                this.sendMessage(rispostaServer); // risposta privata, solo a chi ha agito
                            } else {
                                db.rollbackTransaction();
                            }
                        } catch (Exception e) {
                            System.err.println("[Server] Errore critico durante la transazione: " + e.getMessage());
                            try {
                                if (db != null) db.rollbackTransaction();
                            } catch (java.sql.SQLException ex) {
                                System.err.println("[Server] Fallimento critico di rollback. " + ex.getMessage());
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
             if (this.session != null) {
                this.session.removePlayer(this);
                this.session.releaseClient(clientState);   // libera il personaggio che stava controllando
            }
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