/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.server.impl;

import com.toystory.server.GameDescription;
import com.toystory.server.type.Room;
import com.toystory.server.type.AdvObject;
import com.toystory.server.type.PickupableObject;
import com.toystory.server.type.PlayableCharacter;
import com.toystory.server.type.Ability;

/**
 * Istanzia concretamente l'universo di gioco: modella la planimetria della casa di Andy 
 * e delle fogne, colloca i passaggi segreti, definisce gli scarafaggi nemici 
 * e setta le condizioni di vittoria della storia.
 */
public class ToyStoryGame extends GameDescription {
    // Definiamo i riferimenti alle stanze come campi della classe
    //Casa - Piano Superiore
    private Room cameraAndy;
    private Room corridoioPrimoPiano; // corrisponde a configureCorridoioPrimoiano
    private Room corridoioPianoTerra;
    private Room cameraMolly;

    // Casa - Piano Inferiore
    private Room cucina;
    private Room giardino;

    // Zona Fogne 
    private Room ingressoFogna;
    private Room fognaPrimaStanza;
    private Room stanzaBuia;
    private Room casaTopo;
    private Room stanzaLeva;
    private Room fognaSecondaStanza;
    private Room stanzaAcqua;
    private Room stanzaSenzaAcqua;
    private Room bossFinale;
    private Room scenaFinale;

    /**
     * Costruttore di ToyStoryGame. Richiama il costruttore della classe madre.
     */
    public ToyStoryGame() {
        super();
    }

    private Room registerRoom(Room room) {
        this.getRooms().add(room);
        return room;
    }
    
    /**
     * Inizializza tutti i personaggi giocabili e le loro abilità speciali.
     * Poiché sono disponibili fin dall'inizio, vengono caricati globalmente.
     */
    private void initPlayableCharacters() {
        // 1. WOODY
        PlayableCharacter woody = new PlayableCharacter(1, "Woody", "Il coraggioso sceriffo Woody.");
        // Woody all'inizio non ha il lazo (o puoi assegnargli l'abilità se ce l'ha già)
        woody.setAbility(null); 

        // 2. BUZZ LIGHTYEAR
        PlayableCharacter buzz = new PlayableCharacter(2, "Buzz Lightyear", "Lo space ranger Buzz Lightyear. Verso l'infinito e oltre.");
        Ability laser = new Ability("Laser", "/Laser.png");
        buzz.setAbility(laser);

        // 3. JESSIE
        PlayableCharacter jessie = new PlayableCharacter(3, "Jessie", "La scattante cowgirl Jessie. Yodel-le-hi-hoo!");
        Ability agilita = new Ability("Destrezza", "/Destrezza.png");
        jessie.setAbility(agilita);

        // 4. SALVATAGGIO NELLA LISTA GLOBALE
        // Salva i personaggi nella classe madre GameDescription
        this.getPlayers().add(woody);
        this.getPlayers().add(buzz);
        this.getPlayers().add(jessie);

        
    }
    
    /**
     * Implementazione del metodo astratto init(). Viene invocato dall'Engine 
     * all'avvio del server per generare il mondo di gioco.
     */
    @Override
    public void init() throws Exception {
        
        // Inizializzazione e registrazione simultanea
        cameraAndy = registerRoom(new Room(1, "Camera di Andy", 
                "La camera di Andy, piena di giocattoli."));
        corridoioPrimoPiano = registerRoom(new Room(2, "Corridoio Primo Piano", 
                "Il corridoio sembra tranquillo, ma un rumore strano sembra provenire dal piano inferiore."));
        corridoioPianoTerra = registerRoom(new Room(3, "Corridoio Piano Terra", 
                "Il corridoio è illuminato dalla luce che proviene da fuori; sembra tutto tranquillo, ma il rumore è sempre più forte."));
        cameraMolly = registerRoom(new Room(4, "Camera di Molly", 
                "Questa è la stanza di Molly, è molto diversa da quella di Andy, ma ci sono molti amici qui."));
        cucina = registerRoom(new Room(5, "Cucina", 
                "Il cuore della casa, l'odore della torta per il compleanno di Andy riempie ancora la stanza; il rumore ora è davvero forte."));
        giardino = registerRoom(new Room(6, "Giardino", 
                "L'aria fresca del giardino ti accoglie, ma il frastuono proveniente dal tombino ti inquieta."));
        ingressoFogna = registerRoom(new Room(7, "Ingresso Fognature", 
                "Le fogne sono molto più sporche di casa nostra, non è decisamente un posto adatto ai giocattoli."));
        fognaPrimaStanza = registerRoom(new Room(8, "Fogne Prima Stanza", 
                "Il tunnel era sporco e umido, ma finalmente ne siamo usciti. C'è una strana figura qui... un topino, forse?"));
        stanzaBuia = registerRoom(new Room(9, "Stanza Buia", 
                "Non si vede assolutamente nulla; è necessaria una fonte di luce per procedere."));
        casaTopo = registerRoom(new Room(10, "Casa del Topo", 
                "La piccola tana del nostro nuovo amico. Si vede che è un nerd: guarda quanti vecchi apparecchi tecnologici rari!"));
        stanzaLeva = registerRoom(new Room(11, "Stanza della Leva", 
                "Questa stanza segreta ospita una grande leva al centro, ma sembra essere rotta."));
        fognaSecondaStanza = registerRoom(new Room(12, "Fogne Seconda Stanza", 
                "Questa stanza è ancora più umida e sporca della precedente."));
        stanzaAcqua = registerRoom(new Room(13, "Stanza con Acqua", 
                "Una grande vasca di raccolta; l'acqua è troppo alta per poter passare."));
        stanzaSenzaAcqua = registerRoom(new Room(14, "Stanza Senza Acqua", 
                "La vasca ora è vuota; ecco cosa nascondeva il livello dell'acqua!"));
        bossFinale = registerRoom(new Room(15, "Boss", 
                "L'antro del boss. Il pericolo è imminente, bisogna fare attenzione!"));
        scenaFinale = registerRoom(new Room(16, "scenaFinale", ""));
    
        // ---------------------------------------------------------------------
        // INIZIALIZZAZIONE PERSONAGGI GIOCABILI GLOBALI
        // ---------------------------------------------------------------------
        initPlayableCharacters();
    
        // ---------------------------------------------------------------------
        // INIZIALIZZAZIONE DEI FLAG DI PROGRESSIONE (TRAMA)
        // ---------------------------------------------------------------------
        // Questi flag ereditati dalla classe madre verranno letti e modificati 
        // dagli Observer (es. UseObserver, OpenObserver) per far avanzare la storia.
        this.getFlags().put("TUTORIAL_START", true); // Il gioco è appena iniziato
        this.getFlags().put("CHEST_OPENED", false);   // Il baule parte chiuso
        this.getFlags().put("LASER_USED", false);     // Buzz non ha ancora illuminato il letto
        this.getFlags().put("LAZO_UNLOCKED", false);  // Woody non ha ancora ottenuto il lazo

        // ---------------------------------------------------------------------
        // AVVIO CONFIGURAZIONE DELLE STANZE
        // ---------------------------------------------------------------------
        // Configura
        configureCameraAndy();
        configureCorridoioPrimoPiano();
        configureCorridoioPianoTerra();
        configureCameraMolly();
        configureCucina();
        configureGiardino();
        configureIngressoFogna();
        configureFogniaPrimaStanza();
        configureStanzaBuia();
        configureCasaTopo();
        configureStanzaLeva();
        configureFognaSecondaStanza();
        configureStanzaAcqua();
        configureStanzaSenzaAcqua();
        configureBossFinale();
        
        //DATABASE
        // 2. Chiediamo alla classe base di occuparsi del database
        // ToyStoryGame non sa NIENTE del DB, sa solo che il mondo deve essere sincronizzato.
        this.syncWorldWithDatabase();
        
    }

    /**
     * Genera la mappa, gli oggetti interattivi, le hitbox logiche e i personaggi
     */
    private void configureCameraAndy() {
        // 1. Uscite (Collegamenti logici)
        cameraAndy.addExit("porta", corridoioPrimoPiano);
        
        // 2. CREAZIONE OGGETTI
        // Chiave: l'oggetto che vogliamo raccogliere
        PickupableObject chiave = new PickupableObject(101, "chiave", "Una piccola chiave dorata.", "chiave.png");
        
        // Oggetti di scenario: lasciamo la descrizione vuota o minima, 
        // così la gestione narrativa rimane centralizzata nel LookAtObserver.
        AdvObject libreria = new AdvObject(201, "libreria", "") {};
        
        AdvObject baule = new AdvObject(202, "baule", "") {};

        AdvObject letto = new AdvObject(203, "letto", "") {};
        AdvObject porta = new AdvObject(204, "porta", "") {};

        // 3. COLLOCAMENTO
        cameraAndy.getObjects().add(chiave);
        cameraAndy.getObjects().add(libreria);
        cameraAndy.getObjects().add(baule);
        cameraAndy.getObjects().add(letto);
        cameraAndy.getObjects().add(porta);
    }
    
    private void configureCorridoioPrimoPiano() {
        // TODO: Inserisci qui addExit e aggiunta oggetti
        corridoioPrimoPiano.addExit("porta_camera_andy", cameraAndy);
        corridoioPrimoPiano.addExit("porta_camera_molly", cameraMolly);
        corridoioPrimoPiano.addExit("scale_giu",corridoioPianoTerra);
        
        // OGGETTI DI SCENARIO (Fissi, non si possono raccogliere)
        // Usiamo ID univoci a partire da 205 per non sovrapporci agli oggetti della Camera di Andy (che arrivavano a 204).
        AdvObject portaAndyObj = new AdvObject(205, "porta_camera_andy", "");
        AdvObject portaMollyObj = new AdvObject(206, "porta_camera_molly", "");
        AdvObject scaleObj = new AdvObject(207, "scale_giu", "");

        // 3. OGGETTI DI SCENARIO EXTRA (Non raccoglibili, solo per immersione) DA AGGIUNGERE ALLA FINE SE ABBIAMO TEMPO
        AdvObject finestra = new AdvObject(208, "finestra", "Dalla finestra si vede il tranquillo vicinato. È una bella giornata, ma noi abbiamo una missione da compiere!");
        AdvObject quadro = new AdvObject(209, "quadro", "Un piccolo quadro che raffigura delle verdi colline. Molto rilassante.");
        AdvObject interruttore = new AdvObject(211, "interruttore", "L'interruttore della luce del corridoio. Decisamente troppo in alto per le braccia di un giocattolo.");
        AdvObject soldatino = new AdvObject(212, "soldatino", "Un soldatino verde di plastica. Sembra stia coraggiosamente facendo la guardia al pavimento, ma non risponde.");
        AdvObject cesto = new AdvObject(214, "cesto", "Un cesto di vimini in un angolo, sembra pieno di roba da buttare o vecchi ombrelli.");
        AdvObject foglietto = new AdvObject(215, "foglietto", "Un foglietto di carta attaccato al muro giù per le scale. Chissà chi ce lo ha messo...");

        // 4. AGGIUNTA DI TUTTI GLI OGGETTI ALLA STANZA
        corridoioPrimoPiano.getObjects().add(portaAndyObj);
        corridoioPrimoPiano.getObjects().add(portaMollyObj);
        corridoioPrimoPiano.getObjects().add(scaleObj);
        corridoioPrimoPiano.getObjects().add(finestra);
        corridoioPrimoPiano.getObjects().add(quadro);
        corridoioPrimoPiano.getObjects().add(interruttore);
        corridoioPrimoPiano.getObjects().add(soldatino);
        corridoioPrimoPiano.getObjects().add(cesto);
        corridoioPrimoPiano.getObjects().add(foglietto);
    }

    private void configureCorridoioPianoTerra() {
        corridoioPianoTerra.addExit("scale_su",corridoioPrimoPiano);
        corridoioPianoTerra.addExit("porta_cucina",cucina);
        corridoioPianoTerra.addExit("porticina_cane",giardino);
        
        AdvObject portaCucinaObj = new AdvObject(302, "porta_cucina", "");
        AdvObject porticina = new AdvObject(310, "porticina_cane", "");
        AdvObject scaleObj = new AdvObject(304, "scale_su", "");
         
       

        // 4. AGGIUNTA DI TUTTI GLI OGGETTI ALLA STANZA
        corridoioPianoTerra.getObjects().add(portaCucinaObj);
        corridoioPianoTerra.getObjects().add(scaleObj);
        corridoioPianoTerra.getObjects().add(porticina);
        
       

    }

    private void configureCameraMolly() {
        // 1. USCITE (I collegamenti bidirezionali)
        cameraMolly.addExit("porta_molly", corridoioPrimoPiano);
        
        // 2. Oggetti: la pallina inizia "nascosta" (non nella stanza)
        PickupableObject pallina = new PickupableObject(403, "pallina", "La pallina di Buster!", "pallina.png");
        PickupableObject forcina = new PickupableObject(405, "forcina", "Un ferretto per capelli di Molly. ", "forcina.png");
        
        // Oggetto baule (solo scenario)
        AdvObject bauleMolly = new AdvObject(404, "baule_molly", "Il baule dei giocattoli. Sembra socchiuso.");
        AdvObject letto = new AdvObject(408, "letto_molly", "") {};
        AdvObject porta = new AdvObject(406, "porta_molly", "") {};
        AdvObject BoPeep = new AdvObject(407, "bo_peep", "") {};
        cameraMolly.getObjects().add(bauleMolly);
        cameraMolly.getObjects().add(letto);
        cameraMolly.getObjects().add(porta);
        cameraMolly.getObjects().add(pallina);
        cameraMolly.getObjects().add(forcina);
        cameraMolly.getObjects().add(BoPeep);
        
      
    }

    private void configureCucina() {
        // 1. USCITE (I collegamenti bidirezionali)
        cucina.addExit("porta_interna_cucina", corridoioPianoTerra);
        
        AdvObject scarafaggi = new AdvObject(501, "scarafaggi", "") {};
        AdvObject porta = new AdvObject(504, "porta_interna_cucina", "") {};
        
        cucina.getObjects().add(scarafaggi);
        cucina.getObjects().add(porta);
        
      
    }

    private void configureGiardino() {
        giardino.addExit("porta_cane",corridoioPianoTerra);
        giardino.addExit("tombino",ingressoFogna);

       
        PickupableObject torsolo = new PickupableObject(602, "torsolo", "Un torsolo di mela mezzo marcio recuperato dalla spazzatura. Noi di plastica non mangiamo, ma a qualcuno potrebbe far gola!", "mela.png");
        PickupableObject rametto = new PickupableObject(604, "rametto", "Un solido rametto di legno. È dritto e resistente.", "rametto.png");
        
        AdvObject sacchiSpazzatura = new AdvObject(601, "sacchi_neri", "");
        AdvObject albero = new AdvObject(603, "albero", "");
        

        // 4. OGGETTI DI SCENARIO EXTRA (Basati sull'immagine giardino.jpg)
        AdvObject portaObj = new AdvObject(605, "porta_ingresso", "La massiccia porta d'ingresso della casa di Andy. Passare per la porticina del cane è l'unico modo per tornare dentro di nascosto.");
        AdvObject tombinoObj = new AdvObject(607, "tombino", "Una pesante grata metallica buia e puzzolente. Gli scarafaggi si sono calati lì sotto con il nostro prezioso carico. Dobbiamo scendere anche noi, l'emergenza torta lo richiede!");
       
        // 5. AGGIUNTA DI TUTTI GLI OGGETTI ALLA STANZA
        // Nota: torsolo e rametto verranno trovati aprendo/cercando in sacchi e albero tramite observer
        giardino.getObjects().add(sacchiSpazzatura);
        giardino.getObjects().add(torsolo);
        giardino.getObjects().add(albero);
        giardino.getObjects().add(rametto);
        
        // Aggiungiamo lo scenario
        giardino.getObjects().add(portaObj);
        giardino.getObjects().add(tombinoObj);
        
    }

    private void configureIngressoFogna() {
        // 1. USCITE (I collegamenti bidirezionali)
        ingressoFogna.addExit("grata_sopra", giardino);
        ingressoFogna.addExit("tunnel", fognaPrimaStanza); // Accessibile liberamente
        
        // Il cancello porta alla Prima Stanza (dove c'è il topo elettricista), ma la logica
        // del gioco (Observer) dovrà bloccare il passaggio se il lucchetto non è stato forzato.
        ingressoFogna.addExit("cancello", fognaSecondaStanza); 

        // 2. OGGETTI PER GLI ENIGMI DELLA STORIA
        //AdvObject lucchetto = new AdvObject(701, "lucchetto", "Un pesante lucchetto di metallo arrugginito blocca la grata. La fessura della serratura è larga abbastanza da infilarci qualcosa di sottile e rigido.");
        
        // 3. OGGETTI DI SCENARIO EXTRA
        AdvObject cancello = new AdvObject(702, "cancello", "Un solido cancello di ferro chiuso a chiave. Gli scarafaggi sono passati attraverso le sbarre senza problemi, ma noi siamo troppo grandi. Dobbiamo trovare il modo di aprirlo.");
        AdvObject tunnelObj = new AdvObject(703, "tunnel", "Un'oscura galleria di mattoni che si addentra nelle fogne. Da laggiù arriva una forte puzza di rifiuti e... briciole di torta! Dobbiamo muoverci.");
        AdvObject grataSopra = new AdvObject(710, "grata_sopra", "La luce del sole filtra dal tombino sopra le nostre teste. Se falliamo la missione, Andy non ci perdonerà mai. Non guardiamo in alto, andiamo avanti!");
        

        // 4. AGGIUNTA DI TUTTI GLI OGGETTI ALLA STANZA
        
        ingressoFogna.getObjects().add(cancello);
        ingressoFogna.getObjects().add(tunnelObj);
        ingressoFogna.getObjects().add(grataSopra);
        
    }

    private void configureFogniaPrimaStanza() {
        // 1. USCITE (I collegamenti bidirezionali)
        fognaPrimaStanza.addExit("tunnel_ritorno", ingressoFogna); // Per tornare indietro
        fognaPrimaStanza.addExit("tubo_buio", stanzaBuia); // La stanza buia da esplorare con Buzz
        fognaPrimaStanza.addExit("porticina", casaTopo); // Inizialmente bloccata dalla logica del gioco
        
        // 2. NPC E OGGETTI DELLA STORIA
        AdvObject topo = new AdvObject(801, "topo", "");
        AdvObject tunnel = new AdvObject(804, "tunnel_ritorno", "");
        AdvObject tuboBuioObj = new AdvObject(802, "tubo_buio", "");
        AdvObject porticinaObj = new AdvObject(803, "porticina", "");

       
        // 4. AGGIUNTA DI TUTTI GLI OGGETTI ALLA STANZA
        fognaPrimaStanza.getObjects().add(topo);
        fognaPrimaStanza.getObjects().add(tuboBuioObj);
        fognaPrimaStanza.getObjects().add(porticinaObj);
        fognaPrimaStanza.getObjects().add(tunnel);
       
    }

    private void configureStanzaBuia() {
        // 1. USCITE (I collegamenti bidirezionali)
        stanzaBuia.addExit("tubo_ritorno", fognaPrimaStanza); // Per tornare indietro

        // 2. OGGETTI PER GLI ENIGMI DELLA STORIA
        AdvObject generatore = new AdvObject(901, "generatore", "");
        
        // 3. OGGETTI DI SCENARIO EXTRA (Basati sull'immagine stanzabuia.jpeg)
        AdvObject tuboRitorno = new AdvObject(902, "tubo_ritorno", "");
       
        // 4. AGGIUNTA DI TUTTI GLI OGGETTI ALLA STANZA
        stanzaBuia.getObjects().add(generatore);
        stanzaBuia.getObjects().add(tuboRitorno);
       
    }

    private void configureCasaTopo() {
        // 1. USCITE (I collegamenti bidirezionali)
        casaTopo.addExit("porticina_ritorno", fognaPrimaStanza); // Per tornare indietro
        
        // Il buco stretto porta alla stanza della leva. Il sistema dovrà verificare
        // che il personaggio attivo sia Jessie prima di permettere il passaggio.
        casaTopo.addExit("buco_stretto", stanzaLeva); 

        // 2. NPC E OGGETTI DELLA STORIA
        AdvObject topo = new AdvObject(1001, "topo_casa", "");
        AdvObject bucoStretto = new AdvObject(1002, "buco_stretto", "");
        AdvObject porticina = new AdvObject(1007, "porticina_ritorno", "");

       
        // 4. AGGIUNTA DI TUTTI GLI OGGETTI ALLA STANZA
        casaTopo.getObjects().add(topo);
        casaTopo.getObjects().add(bucoStretto);
        casaTopo.getObjects().add(porticina);
       
    }

    private void configureStanzaLeva() {
        // 1. USCITE (I collegamenti bidirezionali)
        // L'unica via d'uscita è ripassare per il cunicolo da cui Jessie è entrata.
        stanzaLeva.addExit("buco_stretto_ritorno", casaTopo); 

        // 2. OGGETTI PER GLI ENIGMI DELLA STORIA
        AdvObject leva = new AdvObject(1101, "leva", "");

        // 3. OGGETTI DI SCENARIO EXTRA (Basati sull'immagine leva.jpg)
        AdvObject bucoStretto = new AdvObject(1110, "buco_stretto_ritorno", "");
        

        // 4. AGGIUNTA DI TUTTI GLI OGGETTI ALLA STANZA
        stanzaLeva.getObjects().add(leva);
        stanzaLeva.getObjects().add(bucoStretto);
       
        
    }

    private void configureFognaSecondaStanza() {
        // L'uscita per tornare indietro verso l'ingresso, passando per il cancello aperto.
        fognaSecondaStanza.addExit("cancello_aperto", ingressoFogna); 
        
        // Il varco alle spalle dello scarafaggio. Inizialmente l'NPC bloccherà il passaggio,
        // ma la logica del gioco permetterà di passare dopo avergli dato il torsolo di mela.
        fognaSecondaStanza.addExit("varco", stanzaAcqua); 

        // 2. NPC E OGGETTI DELLA STORIA
        AdvObject scarafaggioCiccione = new AdvObject(1201, "scarafaggio_gigante", "");
        
        // 3. OGGETTI DI SCENARIO EXTRA (Basati sull'immagine image_7b53b1.jpg)
        AdvObject cancelloAperto = new AdvObject(1202, "cancello_aperto", "");
       
        // 4. AGGIUNTA DI TUTTI GLI OGGETTI ALLA STANZA
        fognaSecondaStanza.getObjects().add(scarafaggioCiccione);
        fognaSecondaStanza.getObjects().add(cancelloAperto);
       
    }

    private void configureStanzaAcqua() {
       // 1. USCITE (I collegamenti bidirezionali)
        stanzaAcqua.addExit("tunnel", fognaSecondaStanza); // Per tornare indietro dallo scarafaggio gigante
        
        // L'uscita verso il Boss. Il sistema bloccherà il passaggio con un messaggio 
        // finché il flag "ACQUA_SVUOTATA" non diventerà true (attivato da Jessie).
        

        // 2. OGGETTI PER GLI ENIGMI DELLA STORIA
        AdvObject botola = new AdvObject(1301, "botola", "");
        AdvObject tunnel = new AdvObject(1310, "tunnel", "");
        
        // 3. OGGETTI DI SCENARIO EXTRA (Basati sull'immagine image_85b95d.jpg)
       
        // 4. AGGIUNTA DI TUTTI GLI OGGETTI ALLA STANZA
        stanzaAcqua.getObjects().add(botola);
        stanzaAcqua.getObjects().add(tunnel);
      
    }

    private void configureStanzaSenzaAcqua() {
        // 1. USCITE (I collegamenti bidirezionali)
        stanzaSenzaAcqua.addExit("tunnel", fognaSecondaStanza); // Per tornare indietro
        
        // La botola ora è accessibile! Nessun blocco di sistema qui.
        stanzaSenzaAcqua.addExit("botola_sbloccata", bossFinale); 

        // 2. OGGETTI PRINCIPALI DELLA STORIA
        AdvObject botola = new AdvObject(1401, "botola_sbloccata", "");
        AdvObject tunnel = new AdvObject(1410, "tunnel", "");
        
       
        // 3. AGGIUNTA DI TUTTI GLI OGGETTI ALLA STANZA
        stanzaSenzaAcqua.getObjects().add(botola);
        stanzaSenzaAcqua.getObjects().add(tunnel);
     
    }

    private void configureBossFinale() {
      
        bossFinale.addExit("botolaRitorno",scenaFinale);
        
        AdvObject boss = new AdvObject(1501, "boss", "");
        AdvObject botolaRitorno = new AdvObject(1410, "botolaRitorno", "");
        
        bossFinale.getObjects().add(boss);
        bossFinale.getObjects().add(botolaRitorno);
        
    }
}