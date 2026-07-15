package com.toystory.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * Gestisce l'infrastruttura di rete lato Client (Socket e I/O).
 * <p>
 * Questa classe è responsabile della gestione della connessione TCP con il server,
 * dell'invio dei comandi e dell'ascolto asincrono dei messaggi in arrivo tramite 
 * un thread dedicato ({@link NetworkListenerThread}).
 * </p>
 * <p>
 * L'architettura è completamente disaccoppiata dalla visualizzazione grafica grazie 
 * all'uso di un'interfaccia funzionale ({@link Consumer}), che permette di delegare 
 * la gestione dei messaggi ricevuti a qualsiasi classe esterna (es. il Controller).
 * </p>
 * 
 * @author simon
 */
public class GameClient {

    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 6666;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    /** Flag volatile per garantire la sincronizzazione tra i thread in fase di chiusura */
    private volatile boolean running = false;

   /**
     * Callback funzionale che definisce l'azione da intraprendere alla ricezione di ogni messaggio dal server.
     * <p>
     * L'uso di {@link Consumer} permette di disaccoppiare la logica di rete dalla gestione dell'interfaccia 
     * utente: il client si limita a "recapitare" il messaggio ricevuto, delegando totalmente al chiamante 
     * (es. il Controller) la decisione su come processarlo o visualizzarlo.
     * </p>
     */
    private final Consumer<String> onMessageReceived;

    /**
     * Costruttore del client di rete.
     * @param onMessageReceived Espressione lambda che definisce l'azione da eseguire 
     * quando viene intercettato un messaggio in broadcast dal server.
     */
    public GameClient(Consumer<String> onMessageReceived) {
        this.onMessageReceived = onMessageReceived;
    }
    
    /**
     * Stabilisce la connessione con il Server e avvia il thread di ascolto in background.
     */
    public void connect() {
        try {
            System.out.println("[Client-Rete] Tentativo di connessione a " + SERVER_IP + ":" + SERVER_PORT + "...");
            this.socket = new Socket(SERVER_IP, SERVER_PORT);
            
            // Flussi per inviare e ricevere stringhe
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            this.running = true;
            System.out.println("[Client-Rete] Connessione completata con successo.");

            // Facciamo partire il thread in background per evitare il freeze del programma principale/GUI
            new NetworkListenerThread().start();

        } catch (IOException e) {
            // Se il server è spento, notifichiamo l'errore tramite la lambda
            onMessageReceived.accept("Impossibile connettersi al Server. Verifica che sia attivo!");
        }
    }
    
    /**
     * Stabilisce la connessione con il Server e gestisce la fase di handshake 
     * per la creazione o l'unione a una partita.
     * 
     * @param isHost true se l'utente richiede la creazione di una nuova partita, 
     *               false se intende unirsi a una esistente.
     * @param gameId L'identificativo della partita (richiesto se {@code isHost} è false).
     * @return L'ID della partita creata, "SUCCESS" se l'unione è riuscita, o "ERROR" in caso di fallimento.
     */
    public String connectAndHandshake(boolean isHost, String gameId) {
        try {
            System.out.println("[Client-Rete] Tentativo di connessione a " + SERVER_IP + ":" + SERVER_PORT + "...");
            // Apriamo la connessione base
            this.socket = new Socket(SERVER_IP, SERVER_PORT);
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // 1. Inviamo la nostra scelta al Server PRIMA di fare qualsiasi altra cosa
            if (isHost) {
                out.println("CREA_PARTITA");
            } else {
                out.println("UNISCITI_PARTITA|" + gameId);
            }

            // 2. Aspettiamo la risposta del Server
            String response = in.readLine();
            
            if (response != null && response.startsWith("PARTITA_CREATA|")) {
                this.running = true;
                new NetworkListenerThread().start(); // Tutto ok, avviamo l'ascolto continuo
                return response.split("\\|")[1]; // Restituiamo l'ID generato dal server

            } else if (response != null && response.startsWith("CONNESSIONE_SUCCESSO")) {
                this.running = true;
                new NetworkListenerThread().start(); // Tutto ok, ci siamo uniti!
                return "SUCCESS";

            } else {
                disconnect(); // Qualcosa è andato storto (es. codice errato)
                return "ERROR";
            }
        } catch (IOException e) {
            return "ERROR"; // Server spento o irraggiungibile
        }
    }

    /**
     * Invia un comando formattato (es: "AZIONE|TARGET") generato dall'interazione dell'utente.
     * @param action Il tipo di comando (es. "GUARDA", "PRENDI")
     * @param target L'oggetto o la stanza bersaglio dell'azione
     */
    public void sendCommand(String action, String target) {
        if (out != null) {
            // Protocollo basato su pipe concordato con il ServerThread
            out.println(action + "|" + target);
        } else {
            onMessageReceived.accept("[Errore] Sei disconnesso dal server. Azione annullata.");
        }
    }

    /**
     * Chiude in sicurezza il socket e i flussi di comunicazione.
     */
    public void disconnect() {
        this.running = false;
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
            System.out.println("[Client-Rete] Flussi e socket chiusi correttamente.");
        } catch (IOException ignored) {}
    }

    /**
     * Thread interno adibito al monitoraggio costante dei messaggi
     * provenienti dal Server. Cattura i dati in broadcast (multiplayer) in tempo reale.
     */
    private class NetworkListenerThread extends Thread {
        @Override
        public void run() {
            try {
                String serverMessage;
                // Resta in ascolto finché il canale è aperto e la stringa ricevuta non è nulla
                while (running && (serverMessage = in.readLine()) != null) {
                    
                    // Se intercettiamo il messaggio di benvenuto della connessione, puliamo il token
                    if (serverMessage.startsWith("CONNESSIONE_STABILITA")) {
                        serverMessage = serverMessage.split("\\|")[1];
                    }
                    
                    // Notifica l'observer esterno tramite la callback definita al momento dell'istanziazione,
                    // mantenendo il client indipendente dalla logica di visualizzazione.
                    onMessageReceived.accept(serverMessage);
                }
            } catch (IOException e) {
                if (running) {
                    onMessageReceived.accept("Connessione con il server interrotta bruscamente.");
                }
            } finally {
                disconnect();
            }
        }
    }
}