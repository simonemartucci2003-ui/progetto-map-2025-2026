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
    
    public static String getDescrizoneLettoBauleApertoLazoSbloccato(){
        return "Hai gia guardato qui sotto, hai trovato il lazo di Woody";
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
    
    public static String getBuzzSottoAlLettoMolly() {
        return "Buzz si stende a terra, attivando la luce tattica del suo laser e scrutando nell'oscurità.<PAUSA>" +
               "Buzz: Comando Stellare, ho individuato un artefatto terrestre sotto le reti nemiche. Sembra essere... una forcina per capelli di Molly!<PAUSA>" +
               "Woody: Ottimo lavoro, Buzz! Potrebbe tornarci utile per forzare qualche serratura. Mettiamola via!<PAUSA>";
    }
    
    public static String getDescrizoneLettoMollyBloccato() {
        return "Guardi sotto il letto di Molly... È avvolto in un'oscurità minacciosa.<PAUSA>" +
               "Rex (tremando): O-oh... è così buio lì sotto! E se ci fosse un mostro mangia-dinosauri?!<PAUSA>" +
               "Buzz: Niente panico, Rex! Questo è un lavoro per uno Space Ranger! Con l'illuminazione del mio laser posso perlustrare il perimetro e PRENDERE qualsiasi cosa senza alcun rischio.<PAUSA>";
    }
    
    public static String getDescrizoneLettoMollySbloccato() {
        return "Hai già esplorato questo quadrante. Sotto il letto di Molly c'è solo un po' di polvere, adesso che hai recuperato la forcina.";
    }
    
    public static String getDescrizonePortaAndy() {
        return "La porta della camera di Andy. Per fortuna siamo riusciti ad aprirla ed è rimasta socchiusa. La via per il corridoio è libera!";
    }
    
    public static String getDescrizonePortaMolly() {
        return "È la porta della camera di Molly. Forse dovremmo dare un'occhiata lì dentro, chissà che i suoi giocattoli non abbiano visto qualcosa di utile.";
    }
    
    public static String getDescrizoneScale() {
        return "Le scale per il piano di sotto. Per un giocattolo sembrano un'immensa montagna da scalare, gradino dopo gradino... ma non possiamo tirarci indietro ora!";
    }
    
    public static String getDescrizoneScalePianoTerra() {
        return "Le scale che portano al piano di sopra. Sarà una faticaccia risalire tutta questa montagna di legno, ma dobbiamo tornare in camera!";
    }
            
    public static String getDescrizonePortaCucuna() {
        return "Guardi la porta della cucina. Dei rumori sospetti provengono proprio da lì dentro...<PAUSA>" +
               "Rex (sussurrando terrorizzato): Ra... Ragazzi... non mi piacciono per niente quei suoni...<PAUSA>" +
               "Jessie: Niente paura! Dobbiamo entrare e scoprire cosa sta succedendo. La porta è socchiusa, spingiamo tutti insieme!<PAUSA>" +
               "-- OISSA... OISSA... --<PAUSA>" +
               "Woody: Accidenti, non si muove di un millimetro! Aspettate... c'è qualcosa di pesante che la blocca dall'altra parte.<PAUSA>" +
               "Buzz: Per i crateri di Marte... è Buster! Sta dormendo proprio davanti alla fessura. Dobbiamo trovare un'esca per distrarlo e liberare il passaggio.<PAUSA>";
    }
    
    public static String getDescrizionePortaCucunaSbloccata(){
        return "Fortunatamente siamo riusciti a spostare Buster, ora la via per la cucina è libera";
    }
    
    public static String getDialogoPortaCucinaAperta() {
        return "Stringi la pallina presa dalle tue tasche... *SQUEAK SQUEAK*!<PAUSA>" +
               "Buster: Wof! Wof! Wof!<PAUSA>" +
               "-- Buster balza in piedi all'istante, completamente ipnotizzato dalla sua pallina preferita, e corre via per inseguirla! --<PAUSA>" +
               "Jessie: Yee-haw!! Ce l'abbiamo fatta ragazzi! Sapevo che quel cagnolone non avrebbe resistito!<PAUSA>" +
               "Buzz: Ottima mossa diversiva. Il perimetro è libero, squadra, muoviamoci!";
    }
            
    public static String getDescrizonePorticina() {
        return "La porticina basculante di Buster. È la nostra via di fuga perfetta per entrare e uscire da casa senza farci scoprire dagli umani!";
    }
    
    public static String getDescrizonePortaInternaCucina() {
        return "Buster sta ancora sgranocchiando felice la sua pallina in salotto. L'ingresso per la cucina adesso è completamente libero.";
    }
    
    public static String getDescrizioneScarafaggi() {
        return "Woody: Ehi, ma chi c'è lassù sul tavolo? Degli scarafaggi... Bleah! Però aspettate, forse loro sanno cos'è successo alla torta di Andy, proviamo a parlarci.";
    }
    
    public static String getDialogoScarafaggi() {
        return "Stronzi! Levatevi di torno, miseri pezzi di plastica! Vi conviene non impicciarvi negli affari nostri!";
    }
    
    public static String getDescrizioneTombino() {
        return "Un vecchio tombino metallico sul ciglio della strada. Ci sono delle tracce di scarafaggi lì vicino... siamo decisamente sulla pista giusta.";
    }
    
    public static String getDescrizioneSacchi() {
        return "Due giganteschi sacchi neri della spazzatura. Emanano un odore terribile e sembrano piuttosto viscidi... ma a mali estremi, estremi rimedi.";
    }
    
    public static String getDescrizioneAlbero() {
        return "Un albero imponente che svetta nel giardino. Tra le foglie più basse è rimasto impigliato un robusto ramo spezzato. È un'arrampicata rischiosa: servirà molta agilità per recuperarlo.";
    }
    
    public static String getJessieAlbero() {
        return "Jessie si piega sulle ginocchia e si slancia verso l'alto con tutta la sua forza, volteggiando da un ramo all'altro dell'albero senza un briciolo di paura!<PAUSA>" + 
               "Jessie: Yee-haw!! È fantastico quassù, dovreste provare anche voi, ragazzi!<PAUSA>" +
               "Buzz: Ottima agilità, cowgirl! Hai individuato qualcosa di utile da quell'altitudine?<PAUSA>" +
               "Jessie: Ho trovato questo ramo spezzato! Per poco non mi scuciva uno stivale, ma direi che fa proprio al caso nostro!";
    }
    
    public static String getWoodyAlbero() {
        return "Woody si allunga il più possibile, cercando di fare presa con le sue mani di pezza sulla corteccia ruvida dell'albero... ma scivola inesorabilmente giù.<PAUSA>" +
               "Woody: Accidenti! Le mie braccia non reggono. Credo proprio che fare la scimmia non sia il lavoro adatto a un povero sceriffo.";
    }
    
    public static String getBuzzAlbero() {
        return "Buzz analizza il perimetro dell'albero cercando un appiglio tattico, ma la sua armatura di plastica scivola sulla corteccia.<PAUSA>" +
               "Buzz: Rapporto danni: niente da fare, Sceriffo. Senza il mio jetpack funzionante, questo ostacolo è insuperabile anche per uno Space Ranger.<PAUSA>";
    }
    
    public static String getDescrizoneGrata() {
        return "La grata ci offre una via di fuga da questo luogo sudicio.";
    }
    
    public static String getDescrizioneTunnel() {
        return "Questo tunnel non sembra per nulla invitanete, ma dobbiamo fare il possibile per salvare il compleanno di Andy.";
    }
    
    public static String getDescrizioneCancello() {
        return "Un cancello chiuso a chiave..qui nelle fogne..Chissa cosa avranno da nascondere questi insetti...";
    }

    
    public static String getDialogoCancelloAperto(){
        return "Prendi la forcina dalle tue tasche e con maestria le infili nella toppa della serratura<PAUSA>" + 
               " Woody: Forza! Non c'è tempo da perdere, tra poco arriveranno tutti per il compleanno di Andy..<PAUSA>" +
               "..*Click*! Il pesante lucchetto cade a terra. Con pazienza e destrezza sei riuscito a sbloccare il lucchetto<PAUSA>" +
               "Buzz: Il perimetro è libero, squadra, muoviamoci!";
    }
    
    public static String getCancelloAperto(){
        return "Il cancello è aperto grazie alla tua destria da scassinatore";
    }
    
     public static String getDescrizonetunnelRitorno() {
        return "Questo tunnel è spaventoso, ma almeno sappiamo dove porta";
    }
    
    public static String getDescrizioneTopo() {
        return "ehi, ma li c'è uno strano topino..chissa se è un alleato di quegli insetti.";
    }
    
    public static String getDescrizioneporticina() {
        return "Una piccola porticina, probabilmente sara la tana di quel topolino.";
    }
    
    public static String getDescrizioneTuboBuio() {
        return "Questo passaggio è cosi buio ..Servirebbe un po di luce per vederci attraverso.";
    }

    // In futuro potrai aggiungere qui altri dialoghi, es:
    // public static String getDialogoIncontroBuzz() { ... }
}