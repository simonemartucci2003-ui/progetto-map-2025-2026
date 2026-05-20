/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * Gestisce l'infrastruttura di rete lato Client (Socket e I/O).
 * È completamente disaccoppiata dalla visualizzazione grazie all'uso 
 * delle Interfacce Funzionali (Lambda Expression).
 * * @author Il Tuo Nome / Gruppo
 */
public class GameClient {

    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 6666;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean running = false;

    /** Interfaccia funzionale (Callback) per notificare l'arrivo di stringhe dal server */
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

            // Facciamo partire il thread in background per evitare il congelamento (freeze) del programma principal/GUI
            new NetworkListenerThread().start();

        } catch (IOException e) {
            // Se il server è spento, notifichiamo l'errore tramite la lambda
            onMessageReceived.accept("Impossibile connettersi al Server. Verifica che sia attivo!");
        }
    }

    /**
     * Invia un comando formattato ("AZIONE|TARGET") generato dall'interazione dell'utente.
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
     * Chiude in sicurezza tutte le risorse di rete aperte.
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
     * Thread interno (Inner Class) adibito al monitoraggio costante dei messaggi 
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
                    
                    // UTILIZZO DELLA LAMBDA: Passiamo la stringa all'esterno. 
                    // Sarà chi ha istanziato GameClient a decidere dove mostrarla!
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