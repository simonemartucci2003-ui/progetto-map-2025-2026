/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.server.impl;

import com.toystory.server.GameDescription;
import com.toystory.server.GameObserver;
import com.toystory.server.type.Command;
import com.toystory.server.type.CommandType;
import com.toystory.server.type.PlayableCharacter;
import com.toystory.server.type.Ability;

/**
 * Gestore del cambio di personaggio attivo (Verbo CHIAMA).
 * Permette lo switch in tempo reale tra Woody e Buzz Lightyear, notificandone 
 * i cambiamenti di stato e le icone alla GUI del Client.
 */
public class CallObserver implements GameObserver {

    @Override
    public String update(Command command, GameDescription state) {
        // 1. Controllo di competenza: si attiva SOLO per il comando CHIAMA
        if (command.getType() != CommandType.CHIAMA) {
            return null;
        }

        String targetName = command.getTargetName();

        if (targetName == null || targetName.isEmpty()) {
            return "TESTO|Chi vorresti chiamare in azione?";
        }

        // Recuperiamo il nome del personaggio attualmente attivo
        String currentHeroName = state.getCurrentPlayer().getName();

        // Se il giocatore prova a chiamare il personaggio che sta già controllando
        if (currentHeroName.equalsIgnoreCase(targetName)) {
            return "TESTO|" + targetName.toUpperCase() + " è già sul posto e pronto a ricevere ordini!";
        }

        // 2. LOGICA DI SWITCH LOGICO (Cerchiamo il personaggio nella memoria del gioco)
        PlayableCharacter nuovoEroe = null;
        
        // Scorriamo tutti i personaggi registrati in ToyStoryGame.java all'avvio
        for (PlayableCharacter p : state.getPlayers()) {
            // Usiamo equalsIgnoreCase per match esatti
            // Aggiungiamo un'eccezione morbida nel caso arrivi solo "Buzz" dal client
            if (p.getName().equalsIgnoreCase(targetName) || 
               (targetName.equalsIgnoreCase("Buzz") && p.getName().equalsIgnoreCase("Buzz Lightyear"))) {
                nuovoEroe = p;
                break; // Trovato! Usciamo dal ciclo
            }
        }
        
        // Se il personaggio è stato trovato tra quelli disponibili
        if (nuovoEroe != null){
            
            // Facciamo lo switch "fisico" impostandolo come giocatore corrente
            state.setCurrentPlayer(nuovoEroe);

            // Costruiamo la risposta per la grafica (TESTO + PROTOCOLLI GUI)
            String risposta = "TESTO|" + currentHeroName + " fa un passo indietro. Ora controlli " + nuovoEroe.getName() + "!";

            if (nuovoEroe.getName().equalsIgnoreCase("Buzz Lightyear")) {
                risposta += " 'Verso l'infinito e oltre!'";
                risposta += "|SWITCH_AVATAR|/images/avatars/buzz.png";
                risposta += "|ABILITA|Laser|/images/skills/laser.png";
                
            } else if (nuovoEroe.getName().equalsIgnoreCase("Woody")) {
                risposta += " 'C'è un serpente nel mio stivale!'";
                risposta += "|SWITCH_AVATAR|/images/avatars/woody.png";
                
                // Verifichiamo se Woody ha già sbloccato il lazo tramite i flag
                boolean lazoSbloccato = state.getFlags().getOrDefault("LAZO_UNLOCKED", false);
                if (lazoSbloccato) {
                    risposta += "|ABILITA|Lazo|/images/skills/lazo.png";
                } else {
                    risposta += "|ABILITA|Nessuna|vuoto";
                }
                
            } else if (nuovoEroe.getName().equalsIgnoreCase("Jessie")) {
                // Aggiunta la terza eroina: Jessie!
                risposta += " 'Yee-haw!'";
                risposta += "|SWITCH_AVATAR|/images/avatars/jessie.png";
                risposta += "|ABILITA|Destrezza|/images/skills/destrezza.png";
            }
            
            // 1. Ordiniamo al client di svuotare graficamente le tasche del vecchio eroe.
            // Nota: aggiungiamo "|OK" alla fine per non rompere il tuo ciclo for nel GUIHandler (che legge a coppie!)
            risposta += "|CLEAR_INVENTORY|OK";
            
            // 2. Controlliamo se il nuovo eroe ha già oggetti e li mandiamo alla grafica
            // (Assicurati che PlayableCharacter abbia un metodo getInventory() che restituisce la lista!)
            if (nuovoEroe.getPocket() != null && !nuovoEroe.getPocket().isEmpty()) {
                for (com.toystory.server.type.PickupableObject obj : nuovoEroe.getPocket()) {
                    risposta += "|INVENTARIO|" + obj.getName() + "|" + obj.getIcona();
                }
            }
            
            return risposta;
        }

        // Se il nome cercato non è nella lista dei giocatori validi
        return "TESTO|Quel personaggio non fa parte della squadra o non è raggiungibile al momento.";
    }
}