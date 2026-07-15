/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.server;

import com.toystory.server.type.Command;

/**
 * Interfaccia base per tutti i listener delle azioni di gioco (Pattern Observer).
 * Ogni classe che implementa questa interfaccia rappresenta un "controller" 
 * specializzato nel gestire un tipo specifico di azione (es. PRENDI, USA, GUARDA).
 */
public interface GameObserver {

    /**
     * Metodo standard invocato dall'Engine quando viene ricevuto un comando dalla rete.
     * * @param command Il comando inviato dal client, già parsato dal server.
     * @param state Lo stato attuale del mondo di gioco (stanze, player, inventario).
     * @return La stringa di risposta del Server da inviare al Client (es. "TESTO|Hai preso la chiave"), 
     * oppure null se l'observer ignora il comando perché non di sua competenza.
     */
    String update(Command command, GameDescription state, ClientState client, GameSession session);
}
