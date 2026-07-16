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
 * e imposta le condizioni iniziali e di vittoria della storia.
 * <p>
 * Questa classe estende {@link GameDescription} e rappresenta la mappa  
 * iniziale del gioco, che verrà poi sincronizzata con il database.
 * </p>
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
     * Costruttore di ToyStoryGame. Richiama il costruttore della classe madre 
     * per inizializzare le liste vuote di stanze, giocatori e inventario.
     */
    public ToyStoryGame() {
        super();
    }

    /**
     * Metodo di supporto per registrare una stanza nella lista globale del gioco e restituirla.
     * 
     * @param room La stanza da aggiungere alla mappa.
     * @return La stessa stanza passata come parametro, utile per assegnazioni inline.
     */
    private Room registerRoom(Room room) {
        this.getRooms().add(room);
        return room;
    }
    
    /**
     * Inizializza tutti i personaggi giocabili (Woody, Buzz, Jessie) e le loro abilità speciali.
     * <p>
     * Poiché sono disponibili fin dall'inizio per lo switch, vengono caricati 
     * globalmente nella lista dei giocatori ereditata da {@code GameDescription}.
     * </p>
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

        // Salva i personaggi nella classe madre GameDescription
        this.getPlayers().add(woody);
        this.getPlayers().add(buzz);
        this.getPlayers().add(jessie);

        
    }
    
    /**
     * Implementazione del metodo astratto {@code init()}. Viene invocato dall'Engine 
     * all'avvio del server per generare fisicamente il mondo di gioco.
     * <p>
     * Questo metodo si occupa di:
     * <ol>
     * <li>Creare le istanze base di tutte le stanze ({@link Room}).</li>
     * <li>Inizializzare i personaggi giocabili.</li>
     * <li>Richiamare i metodi {@code configure...()} per popolare ogni stanza di uscite e oggetti.</li>
     * <li>Sincronizzare il mondo appena creato con il database per la persistenza.</li>
     * </ol>
     * 
     * @throws Exception Se si verifica un errore durante l'inizializzazione o la sincronizzazione col DB.
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
        scenaFinale = registerRoom(new Room(16, "Scena Finale", ""));

        // INIZIALIZZAZIONE PERSONAGGI GIOCABILI GLOBALI
        initPlayableCharacters();

        // AVVIO CONFIGURAZIONE DELLE STANZE
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
        // Chiediamo alla classe base di occuparsi del database
        // ToyStoryGame non sa NIENTE del DB, sa solo che il mondo deve essere sincronizzato.
        this.syncWorldWithDatabase();
        
    }

    /**
     * Configura la Camera di Andy aggiungendo le uscite, gli oggetti da raccogliere (es. la chiave)
     * e gli elementi di scenario interattivi (libreria, baule, letto, porta).
     */
    private void configureCameraAndy() {
        // 1. Uscite (Collegamenti logici)
        cameraAndy.addExit("porta", corridoioPrimoPiano);
        
        // 2. CREAZIONE OGGETTI
        // Chiave: l'oggetto che vogliamo raccogliere
        PickupableObject chiave = new PickupableObject(101, "chiave", "Una piccola chiave dorata.", "chiave.png");

        // la gestione narrativa rimane centralizzata nel LookAtObserver.
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
    
    /**
     * Configura il Corridoio al Primo Piano, impostando i collegamenti verso le stanze da letto 
     * e il piano terra, aggiungendo inoltre elementi decorativi per l'immersione.
     */
    private void configureCorridoioPrimoPiano() {
        // TODO: Inserisci qui addExit e aggiunta oggetti
        corridoioPrimoPiano.addExit("porta_camera_andy", cameraAndy);
        corridoioPrimoPiano.addExit("porta_camera_molly", cameraMolly);
        corridoioPrimoPiano.addExit("scale_giu",corridoioPianoTerra);
        
        // OGGETTI DI SCENARIO (Fissi, non si possono raccogliere)
        AdvObject portaAndyObj = new AdvObject(205, "porta_camera_andy", "");
        AdvObject portaMollyObj = new AdvObject(206, "porta_camera_molly", "");
        AdvObject scaleObj = new AdvObject(207, "scale_giu", "");

        // 4. AGGIUNTA DI TUTTI GLI OGGETTI ALLA STANZA
        corridoioPrimoPiano.getObjects().add(portaAndyObj);
        corridoioPrimoPiano.getObjects().add(portaMollyObj);
        corridoioPrimoPiano.getObjects().add(scaleObj);
    }

    /**
     * Configura il Corridoio al Piano Terra, con uscite verso il piano superiore,
     * la cucina e la porticina del cane per accedere al giardino.
     */
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

    /**
     * Configura la Camera di Molly inserendo gli oggetti legati agli enigmi (pallina, forcina)
     * e interazioni con Bo-Peep.
     */
    private void configureCameraMolly() {
        // USCITE 
        cameraMolly.addExit("porta_molly", corridoioPrimoPiano);
        
        // la pallina inizia "nascosta" 
        PickupableObject pallina = new PickupableObject(403, "pallina", "La pallina di Buster!", "pallina.png");
        PickupableObject forcina = new PickupableObject(405, "forcina", "Un ferretto per capelli di Molly. ", "forcina.png");
        
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

    /**
     * Configura la Cucina inserendo lo sciame di scarafaggi e l'uscita verso il corridoio.
     */
    private void configureCucina() {
        // 1. USCITE (I collegamenti bidirezionali)
        cucina.addExit("porta_interna_cucina", corridoioPianoTerra);
        
        AdvObject scarafaggi = new AdvObject(501, "scarafaggi", "") {};
        AdvObject porta = new AdvObject(504, "porta_interna_cucina", "") {};
        
        cucina.getObjects().add(scarafaggi);
        cucina.getObjects().add(porta);
    }

    /**
     * Configura il Giardino esterno. Include il tombino per le fogne e oggetti 
     * essenziali per gli enigmi successivi (torsolo di mela, rametto).
     */
    private void configureGiardino() {
        giardino.addExit("porta_cane",corridoioPianoTerra);
        giardino.addExit("tombino",ingressoFogna);

       
        PickupableObject torsolo = new PickupableObject(602, "torsolo", "Un torsolo di mela mezzo marcio recuperato dalla spazzatura. Noi di plastica non mangiamo, ma a qualcuno potrebbe far gola!", "mela.png");
        PickupableObject rametto = new PickupableObject(604, "rametto", "Un solido rametto di legno. È dritto e resistente.", "rametto.png");
        
        AdvObject sacchiSpazzatura = new AdvObject(601, "sacchi_neri", "");
        AdvObject albero = new AdvObject(603, "albero", "");
        
        AdvObject portaObj = new AdvObject(605, "porta_ingresso", "La massiccia porta d'ingresso della casa di Andy. Passare per la porticina del cane è l'unico modo per tornare dentro di nascosto.");
        AdvObject tombinoObj = new AdvObject(607, "tombino", "Una pesante grata metallica buia e puzzolente. Gli scarafaggi si sono calati lì sotto con il nostro prezioso carico. Dobbiamo scendere anche noi, l'emergenza torta lo richiede!");
       
        // AGGIUNTA DI TUTTI GLI OGGETTI ALLA STANZA
        giardino.getObjects().add(sacchiSpazzatura);
        giardino.getObjects().add(torsolo);
        giardino.getObjects().add(albero);
        giardino.getObjects().add(rametto);
        
        giardino.getObjects().add(portaObj);
        giardino.getObjects().add(tombinoObj); 
    }

    /**
     * Configura l'Ingresso alle Fognature. 
     * L'accesso alla seconda stanza è bloccato da un cancello chiuso a chiave.
     */
    private void configureIngressoFogna() {
        ingressoFogna.addExit("grata_sopra", giardino);
        ingressoFogna.addExit("tunnel", fognaPrimaStanza); // Accessibile liberamente
        
        // Il cancello porta alla Prima Stanza (dove c'è il topo elettricista), ma la logica
        // del gioco (Observer) dovrà bloccare il passaggio se il lucchetto non è stato forzato.
        ingressoFogna.addExit("cancello", fognaSecondaStanza); 

        AdvObject cancello = new AdvObject(702, "cancello", "Un solido cancello di ferro chiuso a chiave. Gli scarafaggi sono passati attraverso le sbarre senza problemi, ma noi siamo troppo grandi. Dobbiamo trovare il modo di aprirlo.");
        AdvObject tunnelObj = new AdvObject(703, "tunnel", "Un'oscura galleria di mattoni che si addentra nelle fogne. Da laggiù arriva una forte puzza di rifiuti e... briciole di torta! Dobbiamo muoverci.");
        AdvObject grataSopra = new AdvObject(710, "grata_sopra", "La luce del sole filtra dal tombino sopra le nostre teste. Se falliamo la missione, Andy non ci perdonerà mai. Non guardiamo in alto, andiamo avanti!");

        // 4. AGGIUNTA DI TUTTI GLI OGGETTI ALLA STANZA
        ingressoFogna.getObjects().add(cancello);
        ingressoFogna.getObjects().add(tunnelObj);
        ingressoFogna.getObjects().add(grataSopra); 
    }

    /**
     * Configura la Prima Stanza delle Fogne, introducendo il Topo 
     * e diramazioni verso enigmi.
     */
    private void configureFogniaPrimaStanza() {
        fognaPrimaStanza.addExit("tunnel_ritorno", ingressoFogna); // Per tornare indietro
        fognaPrimaStanza.addExit("tubo_buio", stanzaBuia); // La stanza buia da esplorare con Buzz
        fognaPrimaStanza.addExit("porticina", casaTopo); // Inizialmente bloccata dalla logica del gioco
        
        // OGGETTI DELLA STORIA
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

    /**
     * Configura la Stanza Buia dove si trova il generatore necessario 
     * per sbloccare la casa del Topo.
     */
    private void configureStanzaBuia() {
        stanzaBuia.addExit("tubo_ritorno", fognaPrimaStanza);

        AdvObject generatore = new AdvObject(901, "generatore", "");
        
        AdvObject tuboRitorno = new AdvObject(902, "tubo_ritorno", "");
       
        // 4. AGGIUNTA DI TUTTI GLI OGGETTI ALLA STANZA
        stanzaBuia.getObjects().add(generatore);
        stanzaBuia.getObjects().add(tuboRitorno);
    }

    /**
     * Configura la Casa del Topo. Nasconde l'ingresso ("buco_stretto") 
     * verso la stanza del meccanismo di drenaggio.
     */
    private void configureCasaTopo() {
        casaTopo.addExit("porticina_ritorno", fognaPrimaStanza); 
        
        // Il buco stretto porta alla stanza della leva. Il sistema dovrà verificare
        // che il personaggio attivo sia Jessie prima di permettere il passaggio.
        casaTopo.addExit("buco_stretto", stanzaLeva); 

        // OGGETTI DELLA STORIA
        AdvObject topo = new AdvObject(1001, "topo_casa", "");
        AdvObject bucoStretto = new AdvObject(1002, "buco_stretto", "");
        AdvObject porticina = new AdvObject(1007, "porticina_ritorno", "");

        // 4. AGGIUNTA DI TUTTI GLI OGGETTI ALLA STANZA
        casaTopo.getObjects().add(topo);
        casaTopo.getObjects().add(bucoStretto);
        casaTopo.getObjects().add(porticina); 
    }

    /**
     * Configura la Stanza della Leva in cui si risolve l'enigma per drenare l'acqua.
     */
    private void configureStanzaLeva() {
        stanzaLeva.addExit("buco_stretto_ritorno", casaTopo); 

        // OGGETTI PER GLI ENIGMI DELLA STORIA
        AdvObject leva = new AdvObject(1101, "leva", "");

        AdvObject bucoStretto = new AdvObject(1110, "buco_stretto_ritorno", "");

        // 4. AGGIUNTA DI TUTTI GLI OGGETTI ALLA STANZA
        stanzaLeva.getObjects().add(leva);
        stanzaLeva.getObjects().add(bucoStretto); 
    }

    /**
     * Configura la Seconda Stanza delle Fogne, dove uno Scarafaggio gigante blocca il varco.
     */
    private void configureFognaSecondaStanza() {
        fognaSecondaStanza.addExit("cancello_aperto", ingressoFogna); 
        
        // Il varco alle spalle dello scarafaggio. Inizialmente bloccherà il passaggio,
        // ma la logica del gioco permetterà di passare dopo avergli dato il torsolo di mela.
        fognaSecondaStanza.addExit("varco", stanzaAcqua); 

        // E OGGETTI DELLA STORIA
        AdvObject scarafaggioCiccione = new AdvObject(1201, "scarafaggio_gigante", "");
        AdvObject cancelloAperto = new AdvObject(1202, "cancello_aperto", "");
       
        // 4. AGGIUNTA DI TUTTI GLI OGGETTI ALLA STANZA
        fognaSecondaStanza.getObjects().add(scarafaggioCiccione);
        fognaSecondaStanza.getObjects().add(cancelloAperto);
    }

    /**
     * Configura la Stanza con Acqua, inaccessibile in profondità fino 
     * all'azionamento della leva da parte di Jessie.
     */
    private void configureStanzaAcqua() {
        stanzaAcqua.addExit("tunnel", fognaSecondaStanza); 

        //OGGETTI PER GLI ENIGMI DELLA STORIA
        AdvObject botola = new AdvObject(1301, "botola", "");
        AdvObject tunnel = new AdvObject(1310, "tunnel", "");
        
        // 4. AGGIUNTA DI TUTTI GLI OGGETTI ALLA STANZA
        stanzaAcqua.getObjects().add(botola);
        stanzaAcqua.getObjects().add(tunnel);
    }

    /**
     * Configura la variante della Stanza Senza Acqua. 
     * Sostituisce la precedente a livello logico dopo l'enigma della leva, 
     * consentendo l'accesso al Boss Finale.
     */
    private void configureStanzaSenzaAcqua() {
        stanzaSenzaAcqua.addExit("tunnel", fognaSecondaStanza); 
        
        // La botola ora è accessibile nessun blocco di sistema.
        stanzaSenzaAcqua.addExit("botola_sbloccata", bossFinale); 

        // OGGETTI  DELLA STORIA
        AdvObject botola = new AdvObject(1401, "botola_sbloccata", "");
        AdvObject tunnel = new AdvObject(1410, "tunnel", "");

        // AGGIUNTA DI TUTTI GLI OGGETTI ALLA STANZA
        stanzaSenzaAcqua.getObjects().add(botola);
        stanzaSenzaAcqua.getObjects().add(tunnel);
    }

    /**
     * Configura la stanza del Boss Finale. Interagire con l'ambiente scatenerà 
     * l'evento di vittoria del gioco.
     */
    private void configureBossFinale() {
      
        bossFinale.addExit("TUTTO",scenaFinale);
        
        AdvObject TUTTO = new AdvObject(1501, "TUTTO", "");
    
        bossFinale.getObjects().add(TUTTO);
        
    }
}