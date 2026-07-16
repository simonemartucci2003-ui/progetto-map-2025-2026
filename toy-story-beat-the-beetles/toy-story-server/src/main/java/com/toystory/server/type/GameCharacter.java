package com.toystory.server.type;

// Aggiungendo "extends AdvObject", diciamo a Java che un GameCharacter 
// È un AdvObject, e può essere inserito nella lista getObjects() delle stanze!
public class GameCharacter extends AdvObject {

    public GameCharacter(int id, String name, String description) {
        // Passiamo i dati direttamente al costruttore della classe madre (AdvObject)
        super(id, name, description);
    }
}