/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.server;

import com.toystory.server.impl.ToyStoryGame;
import com.toystory.server.type.CommandType;
import java.util.Scanner;

/**
 * Classe di test locale per verificare la logica di gioco, l'Engine 
 * e tutti gli Observer funzionanti in sinergia tramite console.
 */
public class GiocoTestConsole {

    public static void main(String[] args) throws Exception {
        // 1. Inizializziamo lo stato del gioco (Passo 1)
        ToyStoryGame gioco = new ToyStoryGame();
        gioco.init(); // Crea la stanza, Woody, Buzz e gli oggetti

        // 2. Inizializziamo l'Engine (che registrerà i 7 Observer nel suo costruttore)
        Engine engine = new Engine(gioco);

        // 3. Setup della lettura da tastiera
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=================================================");
        System.out.println("    TOY STORY GAME - TEST CONSOLE ARCHITETTURA   ");
        System.out.println("=================================================");
        System.out.println("Benvenuto nella " + gioco.getCurrentRoom().getName() + "!");
        System.out.println(gioco.getCurrentRoom().getDescription());
        System.out.println("-------------------------------------------------");
        System.out.println("Comandi disponibili per il test (scrivi esatto):");
        System.out.println("-> GUARDA [bersaglio] (es: GUARDA libreria, GUARDA baule)");
        System.out.println("-> PRENDI [bersaglio] (es: PRENDI chiave)");
        System.out.println("-> APRI [bersaglio]   (es: APRI baule)");
        System.out.println("-> USA [combinazione] (es: USA chiave CON baule)");
        System.out.println("-> CHIAMA [eroe]      (es: CHIAMA Buzz)");
        System.out.println("-> Scrivi 'ESCI' per chiudere il test.");
        System.out.println("=================================================\n");

        while (true) {
            System.out.print("\nCosa vuoi fare? > ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("esci")) {
                System.out.println("Test terminato. Alla prossima avventura!");
                break;
            }

            // 4. Semplice Parser dell'input per mappare i nostri CommandType
            CommandType tipoScelto = null;
            String target = "";

            if (input.toUpperCase().startsWith("GUARDA")) {
                tipoScelto = CommandType.GUARDA;
                target = input.substring(6).trim(); // Prende tutto ciò che c'è dopo "GUARDA"
            } else if (input.toUpperCase().startsWith("PRENDI")) {
                tipoScelto = CommandType.PRENDI;
                target = input.substring(6).trim();
            } else if (input.toUpperCase().startsWith("APRI")) {
                tipoScelto = CommandType.APRI;
                target = input.substring(4).trim();
            } else if (input.toUpperCase().startsWith("USA")) {
                tipoScelto = CommandType.USA;
                target = input.substring(3).trim();
            } else if (input.toUpperCase().startsWith("CHIAMA")) {
                tipoScelto = CommandType.CHIAMA;
                target = input.substring(6).trim();
            } else if (input.toUpperCase().startsWith("PARLA")) {
                tipoScelto = CommandType.PARLA;
                target = input.substring(5).trim();
            } else if (input.toUpperCase().startsWith("DAI")) {
                tipoScelto = CommandType.DAI;
                target = input.substring(3).trim();
            }

            // 5. Inoltro del comando all'Engine e stampa del responso
            if (tipoScelto != null) {
                // Eseguiamo l'azione tramite l'architettura ad Observer
                String rispostaServer = engine.executeAction(tipoScelto, target);
                
                // Puliamo la risposta per la console (togliamo i token grafici come |ABILITA per renderlo leggibile)
                String rispostaPulita = rispostaServer.replace("TESTO|", "")
                                                      .replaceAll("\\|[A-Z_]+\\|.*", ""); // Rimuove i comandi GUI di coda
                
                System.out.println("\n[RISPOSTA SERVER]:\n" + rispostaPulita);
            } else {
                System.out.println("\n[ERRORE]: Comando non riconosciuto per questo test.");
            }
        }
        scanner.close();
    }
}