/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.server.type;

/**
 *
 * @author simon
 */
public enum CommandType {
    GUARDA,     // Ispeziona la stanza o un oggetto
    PRENDI,     // Raccoglie un oggetto e lo mette nelle tasche del personaggio
    USA,        // Usa un oggetto con un altro (es. usa lazo con maniglia, o usa oggetto con Hamm)
    CHIAMA,     // Cambia il personaggio attivo (es. passa da Woody a Buzz)
    VAI,         // Clicca su una porta/varco per cambiare stanza
    INDIETRO,   // Torna alla stanza precedente
    FINE,        // Chiude il gioco
    APRI,
    PARLA,
    DAI
    
}
