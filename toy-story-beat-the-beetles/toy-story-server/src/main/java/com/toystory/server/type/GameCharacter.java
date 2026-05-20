/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.server.type;

/**
 * Classe astratta che fa da madre a tutti i personaggi del gioco (Giocabili e NPC).
 * Dimostra l'uso dell'ereditarietà e del polimorfismo per il progetto.
 */
public abstract class GameCharacter {
    
    private final String name;

    public GameCharacter(String name) {
        this.name = name;
    }

    /** @return Il nome del personaggio. */
    public String getName() { 
        return name; 
    }
}