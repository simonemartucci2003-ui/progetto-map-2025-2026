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

        // 2. LOGICA DI SWITCH LOGICO (Cerchiamo il personaggio nel database/mappa del gioco)
        // Nota: Nel Passo 1 abbiamo registrato le istanze. Qui facciamo lo switch simulato 
        // andando a scambiare il puntatore currentPlayer in GameDescription.
        if (targetName.equalsIgnoreCase("Buzz Lightyear") || targetName.equalsIgnoreCase("Buzz")) {
            
            // Creiamo o recuperiamo l'istanza corretta. Per sicurezza, reimpostiamo il player.
            // In un'architettura definitiva, ToyStoryGame esporrà una lista di eroi.
            // Per ora, facciamo lo switch diretto modificando lo stato.
            PlayableCharacter buzz = new PlayableCharacter("Buzz Lightyear");
            // Re-importiamo la sua abilità per non perderla nel passaggio di stato
            buzz.setAbility(new com.toystory.server.type.Ability("Laser", "/images/skills/laser.png"));
            
            state.setCurrentPlayer(buzz);

            // PROTOCOLLO DI RETE ESTESO: "TESTO|...|SWITCH_AVATAR|percorso_immagine|ABILITA|nome_abilita|percorso_icona"
            return "TESTO|Woody fa un passo indietro. Ora controlli Buzz Lightyear! 'Verso l'infinito e oltre!'"
                    + "|SWITCH_AVATAR|/images/avatars/buzz.png"
                    + "|ABILITA|Laser|/images/skills/laser.png";
            
        } else if (targetName.equalsIgnoreCase("Woody")) {
            
            PlayableCharacter woody = new PlayableCharacter("Woody");
            
            // Verifichiamo se Woody ha già sbloccato il lazo tramite i flag di progressione
            boolean lazoSbloccato = state.getFlags().getOrDefault("LAZO_UNLOCKED", false);
            if (lazoSbloccato) {
                woody.setAbility(new com.toystory.server.type.Ability("Lazo", "/images/skills/lazo.png"));
            } else {
                woody.setAbility(null);
            }
            
            state.setCurrentPlayer(woody);

            String risposta = "TESTO|Buzz si mette in posizione di guardia. Ora controlli lo Sceriffo Woody! 'C'è un serpente nel mio stivale!'|SWITCH_AVATAR|/images/avatars/woody.png";
            
            // Se Woody ha il lazo, aggiorna anche la GUI con la sua abilità
            if (lazoSbloccato) {
                risposta += "|ABILITA|Lazo|/images/skills/lazo.png";
            } else {
                risposta += "|ABILITA|Nessuna|vuoto";
            }
            
            return risposta;
        }

        return "TESTO|Quel personaggio non fa parte della squadra o non è raggiungibile al momento.";
    }
}