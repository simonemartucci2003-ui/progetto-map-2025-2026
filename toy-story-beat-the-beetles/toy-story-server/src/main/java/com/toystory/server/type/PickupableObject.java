/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.server.type;

/**
 * Rappresenta un oggetto che può essere raccolto e trasportato dai personaggi.
 * Estende la classe base AdvObject ereditando ID, Nome e Descrizione.
 */
public class PickupableObject extends AdvObject {

    /**
     * Costruttore che richiama il costruttore della classe madre tramite 'super'.
     */
    public PickupableObject(int id, String name, String description) {
        super(id, name, description);
    }
}
