/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.server.type;

/**
 * Rappresenta un personaggio non giocabile (NPC) generico all'interno del mondo di gioco.
 * Estende GameCharacter ereditando il nome.
 */
public class NonPlayableCharacter extends GameCharacter {

    public NonPlayableCharacter(String name) {
        super(name);
    }
}