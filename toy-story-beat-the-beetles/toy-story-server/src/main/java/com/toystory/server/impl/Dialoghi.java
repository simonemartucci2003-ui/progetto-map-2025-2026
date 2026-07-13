/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.server.impl;

/**
 * Raccoglie tutti i testi e i dialoghi narrativi del gioco.
 * Mantiene puliti gli Observer separando la logica dalla narrazione.
 */
public class Dialoghi {

    // Metodo che restituisce il dialogo post-apertura del baule
    public static String getDialogoBauleAperto() {
        return "Hai inserito la chiave nella toppa... *Click*! Il baule si apre.<PAUSA>" +
               "--- I GIOCATTOLI SONO LIBERI ---<PAUSA>" +
               "Rex: Oh, grazie al cielo! Pensavo saremmo rimasti lì dentro per sempre!<PAUSA>" +
               "Slinky: Ottimo lavoro, sceriffo! Ora però dobbiamo sbrigarci.<PAUSA>" +
               "Mr.Potato: Esatto! Dobbiamo uscire dalla cameretta e controllare i preparativi per la festa di Andy!<PAUSA>"+
               "Rex: EH SE... EH SE RICEVESSE UN NUOVO DINOSAURO COME REGALO?!" ;
       
    }
    public static String getDescrizoneBauleChiuso() {
        return "E' il Baule dei Giocattoli di Andy, purtroppo è chiuso a chiave...";
            
    }
    public static String getDescrizoneBauleAperto() {
        return "E'il Baule dei Giocattoli di Andy";
            
    }
    public static String getDescrizoneLibreria() {
        return "La libreria di Andy piena di fumetti, dove conserva di solito la chiave per il baule";
            
    }
    
    public static String getDescrizoneLettoBauleChiuso() {
        return "Guardi sotto il letto di Andy... senti delle voci provenire dal baule di fianco!<PAUSA>" +
               "Rex (sussurrando): C'è nessuno fuori? Ti prego, tiraci fuori da questo baule!<PAUSA>" +
               "Slinky: Molla l'osso, dinosauro, non farti prendere dal panico. Sceriffo, sei tu?<PAUSA>" +
               "Woody: Sono qui! Tenete duro ragazzi, devo solo trovare la chiave per aprire la serratura del baule.";
    }
    
    public static String getDescrizoneLettoBauleAperto() {
        return "Guardi sotto il letto di Andy... E' molto buoio come al solito<PAUSA>" +
               "Slinky: Hey Woody! SNIFF SNIFF, sento l'odore del tuo lazo sotto al letto, ma è troppo buio, non lo vedo..<PAUSA>" +
               "Buzz: Ma questo è proprio un lavoro da BUZZ LIGHTEAR, con la mia pistola laser riesco a PRENDERE il tuo lazo senza problemi!<PAUSA>" +
               "Woody: Con il mio lazo riuscirò ad aprire la porta! Fai presto Buzz!";
    }
    public static String getJessieSottoAlLetto() {
        return "Jessie si butta a terra e allunga il braccio sotto il letto.<PAUSA>" + 
                "Jessie: Niente da fare, Woody. E' troppo buio, qui serve BUZZ";
    }
    public static String getWoodySottoAlLetto() {
        return "Woody si allunga sotto il letto il più possibile, ma e'troppo buio, qui serve la luce di BUZZ!<PAUSA>" +
                "Woody: Accidenti, non risco a prendere il mio lazo, e' tropo buio...";
    }
    
    public static String getBuzzSottoAlLetto() {
        return "Buzz si stende a terra e s fa luce con il laser. Riesce a prendere il lazo!<PAUSA>" +
                "Buzz: Sceriffo, credo che questo ti appartenga!<PAUSA>" +
                "Woody: Il mio lazo! Grazie, Space Ranger! Ora posso aprire la porta!<PAUSA>" +
                "*** Woody ha sbloccato l'abilità LAZO! Cambia personaggio per equipaggiarla! ***";
    }
    
    public static String getManigliaTroppoInAlto() {
         return "La maniglia è troppo in alto! Nessuno di voi riesce ad arrivarci...<PAUSA>"+
                 "Ci vorrebbe qualcosa per agganciarla e tirarla giù.";
    }
    
    public static String getPortaNonWoody() {
         return "La maniglia è troppo in alto...<PAUSA>" +
                "Woody, usa il tuo lazo per aprirla!<PAUSA>" +
                 "*** Cambia personaggio selezionando Woody per usare il Lazo! ***";
    }
    
    public static String getPortaConWoody() {
         return "Woody fa roteare il suo fido lazo... SWISH! Il lazo si aggancia perfettamente alla maniglia!<PAUSA>" +
                "Con un colpo secco la tira in giù e... *SCREEEAK*, la porta si apre!<PAUSA>" +
                "--- I GIOCATTOLI SONO USCITI DALLA STANZA ---" ;
               
    }
    
    public static String getDialogoBoPeep() {
         return "Bo Peep: Ragazzi, Woody! Grazie al cielo siete qui! Ho sentito dei terribili rumori provenire dal piano di sotto!<PAUSA> " +
                 "Woody: Cosa?! Che tipo di rumori?<PAUSA>" +
                 "Bo Peep: Ho sentito voci e passi, come zampe di insetto, provenivano dalla cucina!<PAUSA>" +
                 "Jessie: Dalla cucina hai detto, su ragazzi andiamo a controllare, presto!";
               
    }
    public static String getDialogoScarafaggi() {
         return "Stronzi!";
               
    }
    
    

    // In futuro potrai aggiungere qui altri dialoghi, es:
    // public static String getDialogoIncontroBuzz() { ... }
}