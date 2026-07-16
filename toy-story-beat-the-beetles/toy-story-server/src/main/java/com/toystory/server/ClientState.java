package com.toystory.server;

import com.toystory.server.type.PlayableCharacter;
import com.toystory.server.type.Room;

/**
 * Rappresenta lo stato corrente di un singolo client (giocatore) connesso al server.
 * <p>
 * Mantiene in memoria le informazioni specifiche e volatili della sessione utente,
 * come il personaggio attualmente controllato e la stanza in cui si trova. 
 * Questo permette al server di gestire connessioni multiple mantenendo i progressi 
 * e le posizioni individuali separati per ciascun client.
 * </p>
 */
public class ClientState {
    private PlayableCharacter currentCharacter;
    private Room currentRoom;
    
    /**
     * Recupera il personaggio attualmente controllato da questo client.
     * 
     * @return Il personaggio giocabile attivo per questo client.
     */
    public PlayableCharacter getCurrentCharacter() { return currentCharacter; }
    
    /**
     * Imposta il personaggio che il client andrà a controllare.
     * 
     * @param currentCharacter Il nuovo personaggio da assegnare al client.
     */
    public void setCurrentCharacter(PlayableCharacter currentCharacter) { this.currentCharacter = currentCharacter; }
    
    /**
     * Recupera la stanza in cui si trova attualmente il personaggio del client.
     * 
     * @return L'oggetto Room che rappresenta la stanza corrente.
     */
    public Room getCurrentRoom() { return currentRoom; }
    
    /**
     * Aggiorna la posizione del client impostando una nuova stanza corrente.
     * 
     * @param currentRoom La nuova stanza in cui il personaggio si è spostato.
     */
    public void setCurrentRoom(Room currentRoom) { this.currentRoom = currentRoom; }
}