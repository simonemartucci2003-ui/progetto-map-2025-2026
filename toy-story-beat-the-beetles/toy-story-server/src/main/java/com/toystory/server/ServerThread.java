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
    
    /** Riferimento all'Engine di gioco condiviso (stato unico del Server) */
    private final Engine engine;
    
    /** Canale di output per inviare stringhe di testo verso il rispettivo Client */
    private PrintWriter out;

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
            
            // Inviamo un token di benvenuto al client per confermare che la connessione è riuscita
            out.println("CONNESSIONE_STABILITA|Benvenuto nell'avventura di Toy Story!");

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

                    // Creiamo un blocco sincronizzato sull'Engine (che è unico per tutti)
                    // In questo modo, il server gestirà rigorosamente UN comando alla volta.
                    synchronized (engine) {
                        try {
                            // --- MODIFICA DATABASE ---
                            com.toystory.server.database.DatabaseManager.getInstance().startTransaction();

                            // 2. Interroghiamo l'Engine
                            String rispostaServer = engine.executeAction(tipoComando, target);

                            // 3. Inviamo il risultato o annulliamo
                            if (rispostaServer != null) {
                                com.toystory.server.database.DatabaseManager.getInstance().commitTransaction();
                                sendToAllPlayers(rispostaServer);
                            } else {
                                com.toystory.server.database.DatabaseManager.getInstance().rollbackTransaction();
                            }
                        } catch (Exception e) {
                            com.toystory.server.database.DatabaseManager.getInstance().rollbackTransaction();
                            System.err.println("[Server] Errore critico durante la transazione: " + e.getMessage());
                        }
                    }

                } catch (IllegalArgumentException e) {
                    // Allineiamo l'errore al protocollo del client aggiungendo "TESTO|"
                    out.println("TESTO|Comando sconosciuto o non riconosciuto dal sistema.");
                }
            }
            
        } catch (IOException e) {
            System.out.println("[Server] Connessione chiusa bruscamente o persa con un giocatore.");
        } finally {
            // Creiamo un riferimento esplicito all'istanza corrente del ServerThread
            ServerThread currentThread = ServerThread.this;
            
            // Rimuoviamo il thread dal registro multiplayer usando il riferimento sicuro
            ServerMain.clientThreads.remove(currentThread);
            try {
                socket.close();
                System.out.println("[Server] Risorse della socket rilasciate correttamente.");
            } catch (IOException e) {
                System.err.println("Errore durante la chiusura forzata della socket: " + e.getMessage());
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