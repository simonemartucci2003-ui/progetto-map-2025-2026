package com.toystory.server;

import com.toystory.server.type.Command;

public interface GameObserver<T> {
    T update(Command command, GameDescription state, ClientState client, GameSession session);
}