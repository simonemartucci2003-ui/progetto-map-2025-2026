/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.server.impl;

/**
 *
 * @author simon
 */
import com.toystory.server.GameDescription;
import com.toystory.server.GameObserver;
import com.toystory.server.type.Command;
import com.toystory.server.type.CommandType;
import com.toystory.server.type.Room;

public class MoveObserver implements GameObserver {
    @Override
    public String update(Command command, GameDescription state) {
        if (command.getType() != CommandType.VAI) return null;

        String targetExit = command.getTargetName();
        Room currentRoom = state.getCurrentRoom();
        Room nextRoom = currentRoom.getExit(targetExit);

        if (nextRoom != null) {
            // Questo setter aggiorna la RAM e salva l'ID nel database (flag: CURRENT_ROOM_ID)
            state.setCurrentRoom(nextRoom); 
            
            return "TESTO|Ti sposti verso " + targetExit + ".|CAMBIA_SFONDO|" + nextRoom.getId();
        }
        return "TESTO|Non puoi andare di lì.";
    }
}