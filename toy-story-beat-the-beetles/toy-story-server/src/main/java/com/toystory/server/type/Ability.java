/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.server.type;

/**
 * Rappresenta l'abilità speciale di un personaggio (es. Lazo, Laser).
 */
public class Ability {
    private final String name;
    private final String iconPath; // Percorso dell'immagine nei file della GUI

    public Ability(String name, String iconPath) {
        this.name = name;
        this.iconPath = iconPath;
    }

    public String getName() {
        return name;
    }

    public String getIconPath() {
        return iconPath;
    }
}
