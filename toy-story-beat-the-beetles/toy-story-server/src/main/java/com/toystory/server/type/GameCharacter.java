/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.server.type;

// Aggiungendo "extends AdvObject", diciamo a Java che un GameCharacter 
// È un AdvObject, e può essere inserito nella lista getObjects() delle stanze!
public class GameCharacter extends AdvObject {

    public GameCharacter(int id, String name, String description) {
        // Passiamo i dati direttamente al costruttore della classe madre (AdvObject)
        super(id, name, description);
    }

    // NON c'è più bisogno di dichiarare id, name e description qui dentro,
    // né di scrivere i metodi getId() o getName(). 
    // Ora GameCharacter eredita tutto automaticamente da AdvObject in modo trasparente!
}