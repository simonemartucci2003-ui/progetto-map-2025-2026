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
        return "Woody: Fermi tutti! Siete voi che avete fatto sparire la torta di compleanno di Andy?!<PAUSA> " +
        "BeatleJohn: Ehi, calmi con le accuse sceriffo! Noi stiamo solo dando una pulita alle briciole.<PAUSA>" +
        "BeatlePaul: Esatto! La torta non è qui... è già stata portata via dal nostro Capo nelle fogne!<PAUSA>" +
        "Jessie: Il vostro Capo? Perché mai dovrebbe rubare una torta di compleanno?<PAUSA>" +
        "BeatleJohn: È la sua ossessione! Ruba ogni singola torta di compleanno che trova in questa città.<PAUSA>" +
        "BeatlePaul: Ormai è laggiù nei tunnel. Se volete recuperarla, dovrete vedervela con lui... e buona fortuna, perché non ama gli ospiti!<PAUSA>" +
        "Jessie: Alle fogne quindi! Non lasceremo che quel ladro rovini la festa di Andy, andiamo ragazzi!";
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
        return "Questo tunnel non sembra molto invitanete, ma dobbiamo fare qualcosa per salvare il compleanno di Andy.";
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
        return "Un topo con degli spessi occhiali da vista e una giacchetta rossa logora. Se ne sta seduto comodamente su una piattaforma, bloccando l'accesso alla sua porticina. Dobbiamo parlargli per convincerlo a farci passare.";
    }
    
    public static String getDescrizioneporticina() {
        return "Una minuscola porta di legno rinforzata con dei cardini di metallo.";
    }
    
    public static String getDescrizioneTuboBuio() {
        return "Un gigantesco tubo di scarico circolare sulla parete sinistra. Lì dentro è completamente buio, non si vede a un palmo di naso..";
    }

   public static String getDialogoTopo(){
      return "Topo: Ehi voi! Fermi lì! Non avvicinatevi troppo, c'è un casino infernale qui davanti.<PAUSA>" +
        "Woody: Ehi, calmati piccolo amico, siamo solo di passaggio. Che cosa è successo alla tua porta?<PAUSA>" +
        "Topo: Quei maledetti scarafaggi! Hanno fatto saltare la corrente principale, ora la mia porta tecnologica è bloccata!<PAUSA>" +
        "Topo: Devo riattivare il generatore che si trova in fondo a quel tunnel buio, ma non ci vedo una cippa! Troppi anni passati a fissare i monitor dei miei computer mi hanno rovinato la vista.<PAUSA>" +
        "Buzz: La tua vista è compromessa, ma la mia tecnologia è intatta. Il mio laser spaziale illuminerà a giorno quel tunnel.<PAUSA>" +
        "Buzz: Restate qui, vado io a riaccendere il generatore. Nessun topo rimarrà al buio sotto il mio comando!";
   }
   
   public static String getDialogoTopoRingraziamento(){
      return "Topo: Ragazzi, siete fantastici! Sento di nuovo il ronzio dei miei amati server e la porta tecnologica è sbloccata.<PAUSA>" +
             "Topo: Prego, entrate pure a casa mia, siete i benvenuti e fate come foste a casa vostra!<PAUSA>" +
             "Woody: Ottimo lavoro di squadra. Andiamo a dare un'occhiata lì dentro.";
   }
   
    public static String  getDescrizoneTuboRitorno(){
        return "L'oscuro tunnel da cui siamo venuti. Senza la fonte di luce di Buzz, saremmo rimasti bloccati lì dentro al buio totale. Meglio non allontanarsi troppo.";
    }
    
    public static String  getDescrizioneGeneratore(){
        return "Un massiccio pannello metallico con la scritta 'GENERATORE AUSILIARIO'. Al centro c'è una grossa leva. Se USIAMO le nostre forze di plastica per abbassarla, potremmo riattivare la corrente per quel topo e farci aprire la porta!";
    }

    public static String  getDescrizioneGeneratoreAcceso(){
        return "Ora il generatore è acceso, il nodtro nuovo amico ne sara felice ";
    }
    
   
    public static String  getDescrizoneTopoCasa(){
        return "Il topo smanettone! Ora che gli abbiamo ridato la corrente, sta digitando furiosamente sulla tastiera del suo computer.";
    }
    
    public static String  getDescrizionePorticinaRitorno(){
        return "Il retro della porta da cui siamo entrati è un groviglio di cavi, schermi di stato e persino dei controller arcade incastrati nel legno.";
    }

    public static String  getDescrizioneBuco(){
        return "In alto a destra, tra i mattoni, c'è uno stretto condotto d'aerazione buio. Serve un giocattolo molto agile, snodato e scattante per infilarcisi. ";
    }
    
    public static String  getDescrizioneBucoRitorno(){
        return "L'apertura da cui sono sbucata. È davvero stretta, Woody o Buzz si sarebbero incastrati al primo metro.";
    }

    public static String  getDescrizioneLeva(){
        return "Il meccanismo di controllo principale per il deflusso dell'acqua. Il manico di metallo è completamente spezzato alla base! La fessura è larga, ci vorrebbe qualcosa da incastrare per fare leva e sbloccare le valvole.";
    }
    
    public static String  getDescrizioneLevaAggiustata(){
        return "La leva finalmente è stata riparata.";
    }
    
     public static String  getDialogoLevaAggiustata(){
        return "Jessie: Ragazzi ci siamo. Adesso l'acqua non sara un problema per noi. Salveremo il compleanno di Andy.";
    }

    public static String  getDescrizioneCancelloAperto(){
        return "Il pesante cancello di ferro a sbarre. Per fortuna siamo riusciti a forzarlo, la via per tornare all'ingresso è libera.";
    }
    
    public static String  getDescrizioneScarafaggio(){
        return "Un enorme scarafaggio incredibilmente grasso. Se ne sta seduto a bloccare completamente l'arco del tunnel e si sfrega le zampe sulla pancia. Ha l'aria di chi non si sposterà di un millimetro...Proviamo a parlarci";
    }
    
    public static String  getDescrizioneScarafaggioDopo(){
        return "Lo scrafaggio è intento a mangiare la sua mela. Fortunatamente si è liberato uno spazio per passare";
    }
    
    public static String  getDialogoScarafaggioCorotto(){
        return "Scarafaggio: (masticando rumorosamente) Oh, questa mela è squisita! Molto meglio degli ordini del capo.<PAUSA> " +
           "Woody: Allora, siamo amici adesso? Possiamo passare? <PAUSA>" +
           "Scarafaggio: Certo, certo! Potete andare. Anzi, se ne avete un'altra, vi mostro anche una scorciatoia! <PAUSA>";
    }
    
    public static String  getDialogoScarafaggioRetto(){
        return "Buzz: Fermatevi! Quello scarafaggio blocca il passaggio per il condotto dell'acqua.<PAUSA>" +
               "Scarafaggio: Non si passa! Il mio capo, il Grande Coleottero, ha detto che nessuno deve passare di qui!<PAUSA>" +
               "Woody: Ehi amico, senti, è un'emergenza. Dobbiamo salvare il compleanno di Andy! <PAUSA>" +
               "Scarafaggio: (con voce ingenua) Il capo ha detto che gli umani sono cattivi. E io ho una fame terribile, il mio pancione brontola da ore... <PAUSA>" +
               "Jessie: (sussurrando) Ragazzi, avete sentito? Ha una fame da lupi. Se troviamo qualcosa da mangiare, forse ci lascerà passare.<PAUSA>";
    }
    
    public static String  getDialogoUsoMela(){
        return "Woody: Ehi, ciccione, guarda un po' cosa abbiamo qui. Un torsolo di mela freschissimo, appena arrivato dalla spazzatura! <PAUSA> " +
           "Scarafaggio: (gli occhi si spalancano) Una... una mela? Per me? <PAUSA> " +
           "Jessie: Sì, tutta tua! È succosa e croccante. Basta che ti sposti un pochino.<PAUSA>  " +
           "Scarafaggio: (afferra il torsolo con voracità) Oh, che meraviglia! Sapete, il capo non mi offre mai nulla di così buono.<PAUSA>  " +
           "Buzz: Ottimo lavoro. Mentre è distratto a mangiare, sgusciamo via da questa parte!<PAUSA> ";
    }
    
    public static String getDialogoBossFinale() {
        return "Woody: Fermi tutti! Abbiamo trovato la torta... e anche il ladro!<PAUSA>" +
               "Buzz: Per tutti i crateri marziani... sei tu il capo degli scarafaggi? Un pupazzo da ventriloquo?<PAUSA>" +
               "Boss: Esatto, Space Ranger. Benvenuti nel mio oscuro regno! Nessuno toccherà quel dolce!<PAUSA>" +
               "Jessie: Ma perché rubare la torta di compleanno di Andy? Che male ti ha fatto quel bambino?!<PAUSA>" +
               "Boss: I compleanni... (abbassa lo sguardo di scatto). Io sono stato il regalo di un bambino, tanto tempo fa. Ma appena ha strappato la carta da regalo, ha urlato.<PAUSA>" +
               "Boss: Ha detto che ero 'troppo brutto' e inquietante! Mi ha gettato via subito, il giorno stesso del suo compleanno!<PAUSA>" +
               "Boss: Da quel momento ho giurato a me stesso che nessun bambino avrebbe più sorriso davanti a una torta! Le rovinerò tutte, per sempre!<PAUSA>" +
               "Woody: Ascoltami bene, amico. Capisco il tuo dolore, l'abbandono è la cosa peggiore che possa capitare a un giocattolo. Ma Andy non è così!<PAUSA>" +
               "Buzz: Il nostro bambino ha un cuore grande. Tratta ogni giocattolo con rispetto, inventando avventure incredibili per tutti noi!<PAUSA>" +
               "Jessie: Ha ragione! Andy amerebbe un pupazzo come te! Saresti un perfetto cattivo del selvaggio West, oppure un misterioso alieno spaziale! Vieni con noi, ti accoglierà a braccia aperte!<PAUSA>" +
               "Boss: Voi... voi dite davvero? Credete che potrei avere un'altra occasione? Che un bambino vorrebbe giocare con me dopo tutto questo tempo?<PAUSA>" +
               "Woody: Hai la parola di uno Sceriffo. Ma devi lasciarci riportare la torta a casa, la festa sta per cominciare.<PAUSA>" +
               "Boss: E va bene... (si alza lentamente, spolverandosi il vestito). Aiutatemi a portarla su. Andiamo a conoscere questo Andy...<PAUSA>" +
               "--- IL BOSS SI UNISCE ALLA SQUADRA E LA TORTA È SALVA ---";
    }
}