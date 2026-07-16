package com.toystory.server;

import com.toystory.server.type.CommandType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import com.toystory.server.database.DatabaseManager;
import com.toystory.server.type.PlayableCharacter;
import java.sql.SQLException;

/**
 * Thread dedicato alla gestione della comunicazione di rete con un singolo client connesso.
 * <p>
 * Implementa un ciclo di ascolto continuo che riceve gli input (comandi) inviati 
 * dalla GUI del client, li mappa nel protocollo di gioco (separato da pipe '|') 
 * e li inoltra all'{@link Engine} della rispettiva {@link GameSession}. 
 * Si occupa inoltre di recapitare al client le risposte testuali generate dal server.
 * </p>
 */
public class ServerThread extends Thread {

    /** Il canale socket per comunicare direttamente con lo specifico client. */
    private final Socket socket;
    
    /** Canale di output (stream) per inviare stringhe di testo verso il client. */
    private PrintWriter out;

    /** La sessione (stanza) isolata a cui questo giocatore appartiene. */
    private GameSession session;
    
    /** Lo stato locale del client (personaggio controllato, stanza attuale). */
    private final ClientState clientState = new ClientState();

    /**
     * Inizializza il thread di gestione per una nuova connessione client.
     * 
     * @param socket Il socket generato dal metodo accept() di {@link ServerMain}.
     */
    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    /**
     * Associa questo thread (e quindi il giocatore) a una specifica stanza di gioco.
     * 
     * @param session L'istanza della partita a cui il giocatore si è unito o che ha creato.
     */
    public void setSession(GameSession session) {
        this.session = session;
    }
    
    /**
     * Ciclo di vita principale del Thread.
     * <p>
     * Gestisce le due fasi fondamentali della connessione:
     * <ol>
     *   <li><b>Handshake:</b> Ricezione del comando iniziale per creare o unirsi a una partita.</li>
     *   <li><b>Gameplay:</b> Ascolto continuo dei comandi (es. PRENDI, USA, GUARDA), 
     *       esecuzione tramite l'Engine e restituzione dell'esito.</li>
     * </ol>
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
                            System.err.println("[Server] Causa reale: " + e.getCause().getMessage());
                        }
  
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

                    } catch (IOException e) {
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

                    // Il blocco synchronized protegge SOLO i giocatori della stessa partita.
                    // Questo serializza perfettamente le operazioni CRUD sul database.
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
                        } catch (SQLException e) {
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
            // Rimuoviamo il thread dal registro multiplayer alla disconnessione
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
     * Metodo di servizio per spedire un messaggio testuale (risposta di gioco) a questo specifico client.
     * 
     * @param msg La stringa formattata secondo il protocollo da trasmettere sulla rete.
     */
    public void sendMessage(String msg) {
        if (out != null) {
            out.println(msg);
        }
    }
}