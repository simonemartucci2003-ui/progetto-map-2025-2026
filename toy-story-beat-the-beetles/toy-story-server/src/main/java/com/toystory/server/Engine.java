package com.toystory.server;

import com.toystory.server.impl.ToyStoryGame;
import com.toystory.server.type.Command;
import com.toystory.server.type.CommandType;

public class Engine extends GameObservable<String>  {

    private final ToyStoryGame game;

    public Engine(ToyStoryGame game) {
        this.game = game;
        this.addObserver(new com.toystory.server.impl.LookAtObserver());
        this.addObserver(new com.toystory.server.impl.PickUpObserver());
        this.addObserver(new com.toystory.server.impl.UseObserver());
        this.addObserver(new com.toystory.server.impl.MoveObserver());
        this.addObserver(new com.toystory.server.impl.CallObserver());
        this.addObserver(new com.toystory.server.impl.TalkObserver());
    }

    public String executeAction(CommandType type, String targetName, ClientState client, GameSession session) {
        if (type == null) {
            return "TESTO|Azione non valida.";
        }
        Command comando = new Command(type, targetName);
        return this.notifyObservers(comando, game, client, session, "TESTO|Non succede nulla. Non è un'azione valida in questo momento.");
    }

    public String buildResumeSyncMessage(ClientState client) {
       StringBuilder sb = new StringBuilder();
       if (client.getCurrentRoom() != null) {
           String idStanza = client.getCurrentRoom().getName().toUpperCase().replace(" ", "_");
           sb.append("CAMBIA_SFONDO|").append(idStanza);
       }
       String characterFragment = game.buildCharacterStatusFragment(client.getCurrentCharacter());
       if (!characterFragment.isEmpty()) {
           if (sb.length() > 0) sb.append("|");
           sb.append(characterFragment);
       }
       return sb.length() > 0 ? sb.toString() : null;
    }
}