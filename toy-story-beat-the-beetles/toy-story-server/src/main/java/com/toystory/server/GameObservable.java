package com.toystory.server;

import com.toystory.server.type.Command;
import java.util.ArrayList;
import java.util.List;

public class GameObservable<T> {

    private final List<GameObserver<T>> observers = new ArrayList<>();

    public void addObserver(GameObserver<T> observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(GameObserver<T> observer) {
        observers.remove(observer);
    }

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