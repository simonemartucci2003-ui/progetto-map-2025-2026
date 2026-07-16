package com.toystory.server;

import com.toystory.server.type.Command;
import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta il "Soggetto" (o Publisher) nel Pattern Observer con supporto ai Generics.
 * <p>
 * Mantiene un registro di tutti gli observer iscritti (i vari gestori di comandi) 
 * e si occupa di smistare a ognuno di essi i comandi in arrivo, implementando 
 * una logica simile a una Chain of Responsibility.
 * </p>
 *
 * @param <T> Il tipo di dato atteso come risposta dalle notifiche agli observer.
 */
public class GameObservable<T> {

    private final List<GameObserver<T>> observers = new ArrayList<>();
    
    /**
     * Registra un nuovo osservatore nel sistema.
     * 
     * @param observer L'istanza dell'osservatore da aggiungere alla lista.
     */
    public void addObserver(GameObserver<T> observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    /**
     * Rimuove un osservatore precedentemente registrato.
     * 
     * @param observer L'istanza dell'osservatore da rimuovere.
     */
    public void removeObserver(GameObserver<T> observer) {
        observers.remove(observer);
    }
    
    /**
     * Notifica tutti gli osservatori iscritti della ricezione di un nuovo comando.
     * <p>
     * Interroga ciclicamente gli observer: il primo che restituisce una risposta 
     * diversa da null interrompe il ciclo, assumendosi la responsabilità del comando.
     * </p>
     * 
     * @param command Il comando da elaborare.
     * @param state Lo stato globale della partita.
     * @param client Lo stato specifico del client chiamante.
     * @param session La sessione multiplayer attuale.
     * @param defaultResponse La risposta di fallback da restituire se nessun observer gestisce il comando.
     * @return La risposta fornita dall'observer che ha gestito il comando, o quella di default.
     */
    public T notifyObservers(Command command, GameDescription state, ClientState client, GameSession session, T defaultResponse) {
        for (GameObserver<T> observer : observers) {
            T response = observer.update(command, state, client, session);
            if (response != null) {
                return response;
            }
        }
        return defaultResponse;
    }
}