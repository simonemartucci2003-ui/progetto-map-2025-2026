package com.toystory.server;

import com.toystory.server.type.Command;

/**
 * Interfaccia generica per i listener delle azioni di gioco (Pattern Observer).
 * <p>
 * Definisce il contratto per tutte le classi ("Observer") che devono reagire 
 * ai comandi inviati dai client. Utilizza i generics per specificare il tipo 
 * di dato restituito dopo l'elaborazione del comando.
 * </p>
 *
 * @param <T> Il tipo di dato restituito dal metodo update (es. String per messaggi testuali).
 */
public interface GameObserver<T> {
    
    /**
     * Valuta ed eventualmente elabora il comando ricevuto dal client.
     * 
     * @param command Il comando inviato dal client, contenente tipo e target.
     * @param state Lo stato globale della partita (stanze, flag, inventari).
     * @param client Lo stato specifico del client che ha inviato il comando (personaggio, posizione).
     * @param session La sessione di gioco (stanza multiplayer) corrente.
     * @return La risposta generata dall'azione (di tipo T), oppure null se il comando non è di competenza di questo observer.
     */
    T update(Command command, GameDescription state, ClientState client, GameSession session);
}