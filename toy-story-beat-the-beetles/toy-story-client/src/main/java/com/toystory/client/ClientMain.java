/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.client;

import java.util.Scanner;

/**
 * Classe principale del Client (Modulo toy-story-client).
 * Avvia i motori di rete e centralizza il flusso del programma senza appesantirsi.
 * * @author Il Tuo Nome / Gruppo
 */
public class ClientMain {

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("     TOY STORY GAME CLIENT IS RUNNING     ");
        System.out.println("==========================================");

        /* * Creiamo l'infrastruttura di rete passando una LAMBDA EXPRESSION (Consumer<String>).
         * La stringa 'messaggio' che arriva dal server viene passata direttamente 
         * al blocco di codice successivo. Per adesso stampa in console, ma domani 
         * basterà scrivere: messaggio -> areaTestoGUI.append(messaggio)
         */
        GameClient client = new GameClient(messaggio -> {
            System.out.println("\n[AGGIORNAMENTO GIOCO] " + messaggio);
            System.out.print("> Scegli opzione (o attendi mosse altrui): ");
        });

        // Avviamo la connessione di rete verso il server
        client.connect();

        // Scanner temporaneo in console per fare i test sul funzionamento dei bottoni simulati
        Scanner scanner = new Scanner(System.in);
        System.out.println("[Info] Digita 'LOOK' per simulare il click su un baule, 'TAKE' per il lazo, o 'ESCI'.");
        
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            
            if (input.equalsIgnoreCase("ESCI")) {
                client.disconnect();
                break;
            } else if (input.equalsIgnoreCase("LOOK")) {
                // Simulazione di un click sul bottone "Guarda" avendo selezionato l'oggetto "Baule"
                client.sendCommand("GUARDA", "Baule");
            } else if (input.equalsIgnoreCase("TAKE")) {
                // Simulazione di un click sul bottone "Prendi" avendo selezionato l'oggetto "Lazo"
                client.sendCommand("PRENDI", "Lazo");
            } else {
                System.out.println("Comando di test non valido. Usa 'LOOK', 'TAKE' o 'ESCI'.");
            }
        }
        
        System.out.println("[Client] Applicazione terminata.");
        scanner.close();
    }
}