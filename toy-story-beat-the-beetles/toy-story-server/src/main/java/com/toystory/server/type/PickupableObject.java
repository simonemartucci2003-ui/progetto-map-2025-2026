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

   private String icona;

   public PickupableObject(int id, String name, String description, String icona) {
        super(id, name, description);
        this.icona = icona;
    }

    public String getIcona() { return icona; }
    
 }
