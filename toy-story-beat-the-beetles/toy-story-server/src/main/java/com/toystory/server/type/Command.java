/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.server.type;

/**
 * Struttura che incapsula i dati di un comando inviato dal giocatore.
 */
public class Command {

    private final CommandType type;
    private final String targetName;

    /**
     * NUOVO COSTRUTTORE: Permette all'Engine di impacchettare i dati 
     * ricevuti dalla rete prima di smistarli agli Observer.
     * * @param type Il tipo di azione (es. GUARDA, PRENDI).
     * @param targetName Il nome dell'oggetto coinvolto (es. "chiave", "baule").
     */
    public Command(CommandType type, String targetName) {
        this.type = type;
        this.targetName = targetName;
    }

    // GETTER per permettere agli Observer di leggere i dati del comando
    public CommandType getType() {
        return type;
    }

    public String getTargetName() {
        return targetName;
    }
}