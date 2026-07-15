package com.toystory.server;

import com.toystory.server.type.PlayableCharacter;
import com.toystory.server.type.Room;

public class ClientState {
    private PlayableCharacter currentCharacter;
    private Room currentRoom;

    public PlayableCharacter getCurrentCharacter() { return currentCharacter; }
    public void setCurrentCharacter(PlayableCharacter currentCharacter) { this.currentCharacter = currentCharacter; }

    public Room getCurrentRoom() { return currentRoom; }
    public void setCurrentRoom(Room currentRoom) { this.currentRoom = currentRoom; }
}