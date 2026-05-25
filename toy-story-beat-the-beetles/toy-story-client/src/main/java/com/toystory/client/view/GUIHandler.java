/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.client.view;

import com.toystory.client.view.GameWindow;

/**
 * Interpreta i messaggi stringa del Server e aggiorna la finestra di gioco.
 */
public class GUIHandler {
    
    private final GameWindow finestra; // Riferimento alla tua GUI di Matisse

    public GUIHandler(GameWindow finestra) {
        this.finestra = finestra;
    }

    /**
     * Prende la stringa (es: "TESTO|Ciao!|CAMBIA_SFONDO|2") e muove la grafica
     */
    public void processaComando(String messaggioServer) {
        if (messaggioServer == null || messaggioServer.isEmpty()) return;

        String[] tokens = messaggioServer.split("\\|");
        
        for (int i = 0; i < tokens.length; i += 2) {
            if (i + 1 >= tokens.length) break;
            
            String comando = tokens[i].toUpperCase();
            String valore = tokens[i + 1];

            switch (comando) {
                case "TESTO":
                    // Accede ai metodi pubblici della tua finestra per scrivere il testo
                    finestra.scriviNelLog(valore);
                    break;
                    
                case "CAMBIA_SFONDO":
                    // Cambia lo sfondo della stanza nella finestra
                    finestra.aggiornaSfondoScenario(valore);
                    break;
                    
                case "SWITCH_AVATAR":
                    // Cambia la faccia del personaggio
                    finestra.cambiaIconaAvatar(valore);
                    break;
                    
                case "ABILITA":
                    // Verifichiamo se c'è un token successivo per l'icona, altrimenti passiamo una stringa vuota
                    String icona = "";
                    if (i + 2 < tokens.length) {
                        icona = tokens[i + 2];
                    }
                    
                    // Chiamiamo il metodo sulla finestra passandogli le due stringhe pulite
                    finestra.aggiornaSlotAbilita(valore, icona);
                    
                    // Se c'era l'icona, avanziamo l'indice del ciclo per saltare il token extra
                    if (!valore.equalsIgnoreCase("Nessuna")) {
                        i++; 
                    }
                    break;
            }
        }
    }
}