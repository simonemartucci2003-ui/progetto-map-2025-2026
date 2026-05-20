/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.server.type;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta un personaggio controllabile dal giocatore (es. Woody, Buzz, Jessie).
 * Eredita il nome dalla classe madre e gestisce l'inventario tascabile limitato a 2 slot.
 */
public class PlayableCharacter extends GameCharacter {
    
    private final List<PickupableObject> pocket = new ArrayList<>();
    private static final int MAX_POCKET_SIZE = 2;

    public PlayableCharacter(String name) {
        super(name); // Passa il nome al costruttore della classe madre
    }

    public List<PickupableObject> getPocket() { 
        return pocket; 
    }

    public boolean canPickUp() {
        return pocket.size() < MAX_POCKET_SIZE;
    }

    public boolean addTemplateObject(PickupableObject obj) {
        if (canPickUp()) {
            pocket.add(obj);
            return true;
        }
        return false;
    }

    public void removeObject(PickupableObject obj) {
        pocket.remove(obj);
    }
}