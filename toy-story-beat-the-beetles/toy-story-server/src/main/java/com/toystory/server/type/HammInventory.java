/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.server.type;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe specifica per il personaggio di Hamm.
 * Estende NonPlayableCharacter (ereditando le proprietà degli NPC) e introduce 
 * la capacità esclusiva di fare da Inventario Comune di squadra (max 5 oggetti).
 */
public class HammInventory extends NonPlayableCharacter {
    
    /** La pancia del salvadanaio: l'lista degli oggetti condivisi tra i giocatori. */
    private final List<PickupableObject> inventory = new ArrayList<>();
    
    /** Limite massimo di oggetti stivabili dentro Hamm. */
    private static final int MAX_INV_SIZE = 5;

    /**
     * Costruttore che passa il nome predefinito alla classe madre degli NPC.
     * @param name Il nome del personaggio (es. "HAMM").
     */
    public HammInventory(int id, String name, String description) {
        super(id, name, description);
    }

    /** @return La lista degli oggetti attualmente custoditi dentro Hamm. */
    public List<PickupableObject> getObjects() {
        return inventory;
    }

    /** @return true se l'inventario globale è pieno (5 oggetti), false altrimenti. */
    public boolean isFull() {
        return inventory.size() >= MAX_INV_SIZE;
    }

    /**
     * Deposita un oggetto raccoglibile nella pancia di Hamm.
     * @param obj L'oggetto da depositare.
     * @return true se l'operazione riesce, false se l'inventario è pieno.
     */
    public boolean deposit(PickupableObject obj) {
        if (!isFull()) {
            inventory.add(obj);
            return true;
        }
        return false;
    }

    /**
     * Preleva un oggetto da Hamm per passarlo a un personaggio giocabile.
     * @param obj L'oggetto da estrarre.
     * @return true se l'oggetto è stato trovato e rimosso con successo.
     */
    public boolean withdraw(PickupableObject obj) {
        return inventory.remove(obj);
    }
}