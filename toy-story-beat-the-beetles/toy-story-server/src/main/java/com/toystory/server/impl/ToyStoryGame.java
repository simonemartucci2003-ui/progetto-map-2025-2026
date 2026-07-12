/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.server.impl;

import com.toystory.server.GameDescription;
import com.toystory.server.type.Room;
import com.toystory.server.type.AdvObject;
import com.toystory.server.type.ContainerObject;
import com.toystory.server.type.PickupableObject;
import com.toystory.server.type.PlayableCharacter;
import com.toystory.server.type.NonPlayableCharacter;
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
        Ability laser = new Ability("Laser", "/images/skills/laser.png");
        buzz.setAbility(laser);

        // 3. JESSIE
        PlayableCharacter jessie = new PlayableCharacter(3, "Jessie", "La scattante cowgirl Jessie. Yodel-le-hi-hoo!");
        Ability agilita = new Ability("Destrezza", "/images/skills/destrezza.png");
        jessie.setAbility(agilita);

        // 4. SALVATAGGIO NELLA LISTA GLOBALE
        // Salva i personaggi nella classe madre GameDescription
        this.getPlayers().add(woody);
        this.getPlayers().add(buzz);
        this.getPlayers().add(jessie);

        // 5. PERSONAGGIO DI PARTENZA
        // Imposta Woody come personaggio attivo selezionato di default all'avvio
        this.setCurrentPlayer(woody); 
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
        fognaPrimaStanza = registerRoom(new Room(8, "Fogne - Prima Stanza", 
                "Il tunnel era sporco e umido, ma finalmente ne siamo usciti. C'è una strana figura qui... un topino, forse?"));
        stanzaBuia = registerRoom(new Room(9, "Stanza Buia", 
                "Non si vede assolutamente nulla; è necessaria una fonte di luce per procedere."));
        casaTopo = registerRoom(new Room(10, "Casa del Topo", 
                "La piccola tana del nostro nuovo amico. Si vede che è un nerd: guarda quanti vecchi apparecchi tecnologici rari!"));
        stanzaLeva = registerRoom(new Room(11, "Stanza della Leva", 
                "Questa stanza segreta ospita una grande leva al centro, ma sembra essere rotta."));
        fognaSecondaStanza = registerRoom(new Room(12, "Fogne - Seconda Stanza", 
                "Questa stanza è ancora più umida e sporca della precedente."));
        stanzaAcqua = registerRoom(new Room(13, "Stanza dell'Acqua", 
                "Una grande vasca di raccolta; l'acqua è troppo alta per poter passare."));
        stanzaSenzaAcqua = registerRoom(new Room(14, "Stanza Senza Acqua", 
                "La vasca ora è vuota; ecco cosa nascondeva il livello dell'acqua!"));
        bossFinale = registerRoom(new Room(15, "Boss Finale", 
                "L'antro del boss. Il pericolo è imminente, bisogna fare attenzione!"));
    
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
        // Configura il primo livello (Tutorial nella Camera di Andy)
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
     * per la prima stanza di gioco (Camera di Andy).
     */
    private void configureCameraAndy() {
        // 1. Collegamento: il giocatore clicca sulla "porta" e arriva al corridoio
        cameraAndy.addExit("porta", corridoioPrimoPiano);
        
        // 2. CREAZIONE DEGLI OGGETTI (Strutture dati del pacchetto com.toystory.server.type)
        // Definiamo i nomi rigorosamente in minuscolo per facilitare il parsing dei comandi di rete
        
        // Oggetto raccoglibile (PickupableObject): finirà nell'inventario tascabile
        PickupableObject chiave = new PickupableObject(101, "chiave", "Una piccola chiave dorata, ideale per un lucchetto.");
        
        // Oggetto di scenario fisso (AdvObject): l'utente può GUARDARE o PRENDERE oggetti da qui
        AdvObject libreria = new AdvObject(201, "libreria", "Una libreria in legno piena di fumetti e libri di avventure.") {};
        
        // Oggetto Contenitore (ContainerObject): può essere APERTO e bloccato/sbloccato con chiavi
        ContainerObject baule = new ContainerObject(202, "baule", "Il grande baule in legno dei giocattoli.") {};
        baule.setLocked(true); // Impone il blocco iniziale (richiede l'azione USA chiave CON baule)
        baule.setOpen(false);   // Il coperchio è abbassato

        // Elementi ambientali statici utili alla seconda parte del tutorial
        AdvObject letto = new AdvObject(203, "letto", "Il letto di Andy. Sotto è decisamente troppo buio per vedere a occhio nudo.") {};
        AdvObject porta = new AdvObject(204, "porta", "La porta della camera. Il pomello dorato è troppo in alto per un giocattolo di pezza.") {};

        // 3. COLLOCAMENTO DEGLI OGGETTI NELLO SCENARIO
        // Agganciamo gli oggetti appena creati alla lista degli elementi presenti in questa stanza
        cameraAndy.getObjects().add(chiave);
        cameraAndy.getObjects().add(libreria);
        cameraAndy.getObjects().add(baule);
        cameraAndy.getObjects().add(letto);
        cameraAndy.getObjects().add(porta);
        
        // Comunica al Server che la partita comincia fisicamente all'interno della Camera di Andy
        this.setCurrentRoom(cameraAndy);             
    }
    
    private void configureCorridoioPrimoPiano() {
        // TODO: Inserisci qui addExit e aggiunta oggetti
        corridoioPrimoPiano.addExit("cameraAndy", cameraAndy);
        corridoioPrimoPiano.addExit("cameraMolly", cameraMolly);
        corridoioPrimoPiano.addExit("scale",corridoioPianoTerra);
        
        // OGGETTI DI SCENARIO (Fissi, non si possono raccogliere)
        // Usiamo ID univoci a partire da 205 per non sovrapporci agli oggetti della Camera di Andy (che arrivavano a 204).
        AdvObject portaAndyObj = new AdvObject(205, "porta_andy", "La porta della camera di Andy. Ci sono degli adesivi spaziali incollati sul legno.");
        AdvObject portaMollyObj = new AdvObject(206, "porta_molly", "La porta della camera di Molly. È di un rosa pastello molto acceso.");
        AdvObject scaleObj = new AdvObject(207, "scale", "La rampa di scale in legno che scende al piano terra. Da laggiù il rumore si sente in modo più distinto.");

        // 3. OGGETTI DI SCENARIO EXTRA (Non raccoglibili, solo per immersione)
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
        corridoioPianoTerra.addExit("scale",corridoioPrimoPiano);
        corridoioPianoTerra.addExit("porta_cucina",cucina);
        corridoioPianoTerra.addExit("porticina_cane",giardino);
        
        AdvObject portaCucinaObj = new AdvObject(302, "porta_cucina", 
            "La porta bianca che conduce in cucina. È socchiusa, ma Buster ci dorme davanti e non ti fa passare.Sembra sognare, forse vorrebbe giocare.");
        AdvObject porticina = new AdvObject(310, "porticina_cane", 
                "Lo sportellino di Buster sulla porta d'ingresso.");
         AdvObject scaleObj = new AdvObject(304, "scale", 
                 "Le imponenti scale di legno che riportano al piano di sopra. I gradini sembrano montagne da scalare.");
         
        // 3. OGGETTI DI SCENARIO EXTRA (Basati sull'immagine)
        AdvObject portaIngresso = new AdvObject(303, "porta_ingresso", "La massiccia porta d'ingresso della casa. È chiusa a chiave, ma la luce filtra dai vetri in alto.");
        AdvObject giacche = new AdvObject(305, "giacche", "Due giacche appese all'attaccapanni. Da questa prospettiva sembrano enormi mantelli da gigante.");
        AdvObject scarpe = new AdvObject(306, "scarpe", "Delle scarpe e degli scarponi allineati sul pavimento. Speriamo che nessuno decida di indossarli mentre passiamo di qui!");
        AdvObject tappeto = new AdvObject(307, "tappeto", "Un grande tappeto decorato. Attraversarlo è un po' faticoso per via delle frange e del tessuto spesso.");
        AdvObject orologio = new AdvObject(308, "orologio", "Un orologio a pendolo appeso al muro. Il suo ticchettio costante ci ricorda che dobbiamo sbrigarci.");
        AdvObject interruttore = new AdvObject(309, "interruttore", "L'interruttore della luce. Irraggiungibile per noi giocattoli, come al solito.");
        

        // 4. AGGIUNTA DI TUTTI GLI OGGETTI ALLA STANZA
        corridoioPianoTerra.getObjects().add(portaCucinaObj);
        corridoioPianoTerra.getObjects().add(portaIngresso);
        corridoioPianoTerra.getObjects().add(scaleObj);
        corridoioPianoTerra.getObjects().add(giacche);
        corridoioPianoTerra.getObjects().add(scarpe);
        corridoioPianoTerra.getObjects().add(tappeto);
        corridoioPianoTerra.getObjects().add(orologio);
        corridoioPianoTerra.getObjects().add(interruttore);
        corridoioPianoTerra.getObjects().add(porticina);
        corridoioPianoTerra.getObjects().add(portaCucinaObj);

    }

    private void configureCameraMolly() {
        // 1. USCITE (I collegamenti bidirezionali)
        cameraMolly.addExit("porta", corridoioPrimoPiano);

        // 2. NPC E OGGETTI DELLA STORIA
        NonPlayableCharacter boPeep = new NonPlayableCharacter(401, "bo_peep", "Bo Peep sembra terrorizzata. Stringe il suo bastone da pastorella e non stacca gli occhi dalla porta. Dobbiamo parlarle per scoprire cosa l'ha spaventata così tanto!");
        
        AdvObject letto = new AdvObject(402, "letto", "Il letto di Molly. Per noi giocattoli è un ottimo nascondiglio, ma non c'è tempo per nascondersi ora. Aspetta... c'è qualcosa incastrato lì sotto...ma è troppo buio per vedere");
        PickupableObject pallina = new PickupableObject(403, "pallina", "La pallina di Buster! Se gliela facciamo vedere, quel bestione peloso la inseguirebbe ovunque.");
        
        ContainerObject baule = new ContainerObject(404, "baule_molly", "Il baule dei giocattoli. Di solito è un posto sicuro in cui riposare.");
        baule.setOpen(false);
        baule.setLocked(false); 
        PickupableObject forcina = new PickupableObject(405, "forcina", "Un ferretto per capelli di Molly. Per un umano è solo un accessorio, ma per un giocattolo è un grimaldello perfetto per scassinare serrature.");

        // 3. OGGETTI DI SCENARIO EXTRA
        AdvObject portaObj = new AdvObject(406, "porta", "L'unica via d'uscita per tornare al corridoio. La maniglia è irraggiungibile, ma per fortuna l'hanno lasciata socchiusa. Sbrighiamoci!");
        AdvObject casaBambole = new AdvObject(407, "casa_bambole", "La casa delle bambole è silenziosa, i residenti devono essersi rintanati per la paura. Nessuno di loro ha il coraggio di aiutarci");
        AdvObject setTe = new AdvObject(408, "set_te", "Un servizio da tè di porcellana gigante. Dobbiamo fare attenzione a non fare rumore.");
        AdvObject peluche = new AdvObject(409, "peluche", "Un coniglietto e un orsacchiotto sono bloccati nel mezzo di un tea party. Vorrei dirgli di mettersi al sicuro, ma l'emergenza ha la precedenza.");
        AdvObject libreria = new AdvObject(410, "libreria", "I libri di Molly. Ottimi per arrampicarsi e raggiungere i ripiani più alti, ma l'orologio lassù in cima ci ricorda che il tempo stringe: Andy tornerà presto per la festa!");
        AdvObject pecorelle = new AdvObject(411, "pecorelle", "Le pecorelle di Bo Peep sono sparpagliate ovunque e tremano come foglie.");
        AdvObject finestra = new AdvObject(412, "finestra", "Fuori il sole splende e gli umani si preparano per la festa, completamente ignari del furto. Tocca a noi giocattoli risolvere questo disastro.");
        AdvObject tappeto = new AdvObject(413, "tappeto", "Questo tappeto a scacchi è morbidissimo, ma rallenta parecchio i nostri passi di plastica. Muoviamoci!");
        AdvObject lampada = new AdvObject(414, "lampada", "Una lampada gigante che illumina la stanza. Meglio stare lontani dalla luce per non dare troppo nell'occhio.");
        AdvObject decorazioni = new AdvObject(415, "decorazioni", "I quadretti di Molly. Non c'è niente di utile qui, dobbiamo concentrarci sugli oggetti che possono farci avanzare verso il piano terra.");

        // 4. AGGIUNTA DI TUTTI GLI OGGETTI ALLA STANZA
        cameraMolly.getObjects().add(boPeep);
        cameraMolly.getObjects().add(letto);
        cameraMolly.getObjects().add(pallina);
        cameraMolly.getObjects().add(baule);
        cameraMolly.getObjects().add(forcina);
        cameraMolly.getObjects().add(portaObj);
        cameraMolly.getObjects().add(casaBambole);
        cameraMolly.getObjects().add(setTe);
        cameraMolly.getObjects().add(peluche);
        cameraMolly.getObjects().add(libreria);
        cameraMolly.getObjects().add(pecorelle);
        cameraMolly.getObjects().add(finestra);
        cameraMolly.getObjects().add(tappeto);
        cameraMolly.getObjects().add(lampada);
        cameraMolly.getObjects().add(decorazioni);
    }

    private void configureCucina() {
        // 1. USCITE (I collegamenti bidirezionali)
        cucina.addExit("porta", corridoioPianoTerra);
        
       // 2. NPC E OGGETTI DELLA STORIA
        // Ricorda: la descrizione è solo visiva. Scopriremo cosa sanno della torta usando "PARLA SCARAFAGGI"
        NonPlayableCharacter scarafaggi = new NonPlayableCharacter(501, "scarafaggi", "Due scarafaggi in piedi sul tavolo della cucina. Uno indossa minuscoli occhiali da sole, l'altro occhiali da vista. Sembrano confabulare tra di loro.");
        AdvObject piatto = new AdvObject(502, "piatto", "Il grande piatto dove un tempo riposava la torta di compleanno di Andy. Ora ci sono solo misere briciole... che tragedia!");
        AdvObject buster = new AdvObject(503, "buster", "Buster è placidamente accucciato nella sua cuccia. Ora che ha la sua amata pallina rossa in bocca, sembra il cane più innocuo del mondo.");

        // 3. OGGETTI DI SCENARIO EXTRA (Basati sull'immagine)
        AdvObject portaObj = new AdvObject(504, "porta", "La porta bianca che riporta al corridoio.");
        AdvObject cuccia = new AdvObject(505, "cuccia", "La morbida cuccia a pois di Buster. Sembra davvero comoda, ma meglio non disturbarlo.");
        AdvObject orologio = new AdvObject(507, "orologio", "Un piccolo orologio da muro. Meglio sbrigarsi a trovare la torta!");
        AdvObject lavandino = new AdvObject(508, "lavandino", "Il lavello d'acciaio. Il rubinetto sembra un gigantesco serpente di metallo.");
        AdvObject forno = new AdvObject(509, "forno", "Il grande forno bianco con i fornelli. Troppo caldo e pericoloso per noi giocattoli di plastica.");
        AdvObject elettrodomestici = new AdvObject(510, "elettrodomestici", "Un tostapane e una macchina per il caffè. Misteriosi marchingegni da cui gli adulti traggono energia la mattina.");
        AdvObject frigorifero = new AdvObject(511, "frigorifero", "Un enorme frigorifero bianco. Una vera e propria montagna ghiacciata invalicabile.");

        // 4. AGGIUNTA DI TUTTI GLI OGGETTI ALLA STANZA
        cucina.getObjects().add(scarafaggi);
        cucina.getObjects().add(piatto);
        cucina.getObjects().add(buster);
        cucina.getObjects().add(portaObj);
        cucina.getObjects().add(cuccia);
        cucina.getObjects().add(orologio);
        cucina.getObjects().add(lavandino);
        cucina.getObjects().add(forno);
        cucina.getObjects().add(elettrodomestici);
        cucina.getObjects().add(frigorifero);
    }

    private void configureGiardino() {
        giardino.addExit("porta",corridoioPianoTerra);
        giardino.addExit("tombino",ingressoFogna);

        // 3. OGGETTI PER GLI ENIGMI DELLA STORIA
        ContainerObject sacchiSpazzatura = new ContainerObject(601, "sacchi_neri", "Due giganteschi sacchi della spazzatura neri. Puzzano terribilmente e sono viscidi, ma a mali estremi...");
        sacchiSpazzatura.setOpen(false);
        sacchiSpazzatura.setLocked(false);
        
        PickupableObject torsolo = new PickupableObject(602, "torsolo", "Un torsolo di mela mezzo marcio recuperato dalla spazzatura. Noi di plastica non mangiamo, ma a qualcuno potrebbe far gola!");
        
        AdvObject albero = new AdvObject(603, "albero", "Un albero imponente. C'è un ramo spezzato impigliato tra le foglie più basse. È un'arrampicata pericolosa, ci vorrebbe molta agilità per recuperarlo.");
        PickupableObject rametto = new PickupableObject(604, "rametto", "Un solido rametto di legno. È dritto e resistente.");

        // 4. OGGETTI DI SCENARIO EXTRA (Basati sull'immagine giardino.jpg)
        AdvObject portaObj = new AdvObject(605, "porta_ingresso", "La massiccia porta d'ingresso della casa di Andy. Passare per la porticina del cane è l'unico modo per tornare dentro di nascosto.");
        AdvObject scarafaggiStrada = new AdvObject(606, "scarafaggi_strada", "Ehi! Guarda laggiù sul marciapiede! Due scarafaggi si stanno infilando nel tombino... Devono essere i complici che hanno rubato la torta. Presto, dobbiamo seguirli!");
        AdvObject tombinoObj = new AdvObject(607, "tombino", "Una pesante grata metallica buia e puzzolente. Gli scarafaggi si sono calati lì sotto con il nostro prezioso carico. Dobbiamo scendere anche noi, l'emergenza torta lo richiede!");
        AdvObject cassettaPosta = new AdvObject(608, "cassetta_posta", "La cassetta della posta. Un monolite di metallo, inutile per la nostra missione. Ignoriamola.");
        AdvObject idrante = new AdvObject(609, "idrante", "Un idrante rosso fuoco. Sembra una tozza guardia di metallo a presidio della strada. Per fortuna non ci presta attenzione.");
        AdvObject bidoneVerde = new AdvObject(610, "bidone_verde", "Il bidone della raccolta differenziata. È vuoto, a differenza di quei maleodoranti sacchi neri sul marciapiede.");

        // 5. AGGIUNTA DI TUTTI GLI OGGETTI ALLA STANZA
        // Nota: torsolo e rametto verranno trovati aprendo/cercando in sacchi e albero tramite observer
        giardino.getObjects().add(sacchiSpazzatura);
        giardino.getObjects().add(torsolo);
        giardino.getObjects().add(albero);
        giardino.getObjects().add(rametto);
        
        // Aggiungiamo lo scenario
        giardino.getObjects().add(portaObj);
        giardino.getObjects().add(scarafaggiStrada);
        giardino.getObjects().add(tombinoObj);
        giardino.getObjects().add(cassettaPosta);
        giardino.getObjects().add(idrante);
        giardino.getObjects().add(bidoneVerde);
    }

    private void configureIngressoFogna() {
        // 1. USCITE (I collegamenti bidirezionali)
        ingressoFogna.addExit("tombino", giardino);
        ingressoFogna.addExit("tunnel", fognaSecondaStanza); // Accessibile liberamente
        
        // Il cancello porta alla Prima Stanza (dove c'è il topo elettricista), ma la logica
        // del gioco (Observer) dovrà bloccare il passaggio se il lucchetto non è stato forzato.
        ingressoFogna.addExit("cancello", fognaPrimaStanza); 

        // 2. OGGETTI PER GLI ENIGMI DELLA STORIA
        AdvObject lucchetto = new AdvObject(701, "lucchetto", "Un pesante lucchetto di metallo arrugginito blocca la grata. La fessura della serratura è larga abbastanza da infilarci qualcosa di sottile e rigido.");
        
        // 3. OGGETTI DI SCENARIO EXTRA
        AdvObject cancello = new AdvObject(702, "cancello", "Un solido cancello di ferro chiuso a chiave. Gli scarafaggi sono passati attraverso le sbarre senza problemi, ma noi siamo troppo grandi. Dobbiamo trovare il modo di aprirlo.");
        AdvObject tunnelObj = new AdvObject(703, "tunnel", "Un'oscura galleria di mattoni che si addentra nelle fogne. Da laggiù arriva una forte puzza di rifiuti e... briciole di torta! Dobbiamo muoverci.");
        AdvObject acquaSporca = new AdvObject(704, "acqua", "Un canale di melma tossica pieno di immondizia umana: bottiglie di plastica e mozziconi. Caderci dentro significherebbe la fine per i nostri circuiti e le nostre cuciture. Meglio restare sulla passerella.");
        AdvObject passerella = new AdvObject(705, "passerella", "Delle assi di legno marcio che costeggiano il canale. Per noi sono un ponte fondamentale, ma scricchiolano a ogni passo.");
        AdvObject scarafaggiMuro = new AdvObject(707, "scarafaggi", "Ci sono degli scarafaggi che strisciano sui muri umidi. Sembrano di guardia. Meglio ignorarli e puntare al bersaglio grosso.");
        AdvObject tubature = new AdvObject(708, "tubature", "Grosse tubature di metallo corrono lungo le pareti, gocciolando acqua lurida. Rendono l'ambiente ancora più inquietante e rumoroso.");
        AdvObject lampada = new AdvObject(709, "lampada", "Una vecchia lampada industriale illumina debolmente l'ingresso. Crea delle ombre mostruose sulle pareti.");
        AdvObject grataSopra = new AdvObject(710, "grata_sopra", "La luce del sole filtra dal tombino sopra le nostre teste. Se falliamo la missione, Andy non ci perdonerà mai. Non guardiamo in alto, andiamo avanti!");

        // 4. AGGIUNTA DI TUTTI GLI OGGETTI ALLA STANZA
        ingressoFogna.getObjects().add(lucchetto);
        ingressoFogna.getObjects().add(cancello);
        ingressoFogna.getObjects().add(tunnelObj);
        ingressoFogna.getObjects().add(acquaSporca);
        ingressoFogna.getObjects().add(passerella);
        ingressoFogna.getObjects().add(scarafaggiMuro);
        ingressoFogna.getObjects().add(tubature);
        ingressoFogna.getObjects().add(lampada);
        ingressoFogna.getObjects().add(grataSopra);
    }

    private void configureFogniaPrimaStanza() {
        // 1. USCITE (I collegamenti bidirezionali)
        fognaPrimaStanza.addExit("cancello", ingressoFogna); // Per tornare indietro
        fognaPrimaStanza.addExit("tubo_buio", stanzaBuia); // La stanza buia da esplorare con Buzz
        fognaPrimaStanza.addExit("porticina", casaTopo); // Inizialmente bloccata dalla logica del gioco
        
        // 2. NPC E OGGETTI DELLA STORIA
        NonPlayableCharacter topo = new NonPlayableCharacter(801, "topo", "Un topo con degli spessi occhiali da vista e una giacchetta rossa logora. Se ne sta seduto comodamente su una piattaforma, bloccando l'accesso alla sua porticina. Dobbiamo parlargli per convincerlo a farci passare.");
        
        AdvObject tuboBuioObj = new AdvObject(802, "tubo_buio", "Un gigantesco tubo di scarico circolare sulla parete sinistra. Lì dentro è completamente buio, non si vede a un palmo di naso.");
        
        AdvObject porticinaObj = new AdvObject(803, "porticina", "Una minuscola porta di legno rinforzata con dei cardini di metallo.");

        // 3. OGGETTI DI SCENARIO EXTRA
        AdvObject acquaRifiuti = new AdvObject(804, "acqua_sporca", "La pozza d'acqua melmosa è piena di cianfrusaglie cadute dal mondo di sopra. Ci sono delle vecchie scarpe da ginnastica che per noi sembrano enormi navi incagliate e lattine arrugginite.");
        AdvObject interruttore = new AdvObject(805, "presa_elettrica", "Vicino alla lampada c'è una presa di corrente a muro. I fili sono scoperti e non c'è traccia di energia. Ecco perché è saltata la luce nel tubo!");
        AdvObject funghi = new AdvObject(806, "funghi", "Dei misteriosi funghi verdastri crescono sulle pareti di mattoni umidi, emanando una fioca luce fluorescente. Sicuramente non sono commestibili.");
        AdvObject passerella = new AdvObject(807, "passerella", "Le passerelle di legno scricchiolano sotto i nostri piedi di plastica. Sono l'unico percorso sicuro per non finire nell'acqua tossica.");
        AdvObject tunnelDestra = new AdvObject(808, "tunnel_ritorno", "Il tunnel ad arco sulla destra da cui siamo arrivati. Speriamo di non dover scappare in quella direzione inseguiti da qualche mostro.");
        AdvObject scarafaggi = new AdvObject(809, "scarafaggi", "Ancora scarafaggi che zampettano sulle rocce e sul legno. Ignoriamoli, la nostra missione è trovare la torta, non fare disinfestazione.");

        // 4. AGGIUNTA DI TUTTI GLI OGGETTI ALLA STANZA
        fognaPrimaStanza.getObjects().add(topo);
        fognaPrimaStanza.getObjects().add(tuboBuioObj);
        fognaPrimaStanza.getObjects().add(porticinaObj);
        fognaPrimaStanza.getObjects().add(acquaRifiuti);
        fognaPrimaStanza.getObjects().add(interruttore);
        fognaPrimaStanza.getObjects().add(funghi);
        fognaPrimaStanza.getObjects().add(passerella);
        fognaPrimaStanza.getObjects().add(tunnelDestra);
        fognaPrimaStanza.getObjects().add(scarafaggi);
    }

    private void configureStanzaBuia() {
        // 1. USCITE (I collegamenti bidirezionali)
        stanzaBuia.addExit("tubo_buio", fognaPrimaStanza); // Per tornare indietro

        // 2. OGGETTI PER GLI ENIGMI DELLA STORIA
        AdvObject generatore = new AdvObject(901, "generatore", "Un massiccio pannello metallico con la scritta 'GENERATORE AUSILIARIO'. Al centro c'è una grossa leva. Se uniamo le nostre forze di plastica per abbassarla, potremmo riattivare la corrente per quel topo e farci aprire la porta!");
        
        // 3. OGGETTI DI SCENARIO EXTRA (Basati sull'immagine stanzabuia.jpeg)
        AdvObject tuboRitorno = new AdvObject(902, "tubo_ritorno", "L'oscuro tunnel da cui siamo venuti. Senza la fonte di luce di Buzz, saremmo rimasti bloccati lì dentro al buio totale. Meglio non allontanarsi troppo.");
        AdvObject cavi = new AdvObject(903, "cavi", "Spessi cavi elettrici neri e tubature si intrecciano sul muro di mattoni come viscere di un mostro meccanico. Sembrano scollegati dalla rete principale.");
        AdvObject ragnatele = new AdvObject(904, "ragnatele", "Grosse ragnatele polverose pendono dai tubi in alto. Spero vivamente che il ragno che le ha tessute non sia nei paraggi, o saremo nei guai.");
        AdvObject grataPavimento = new AdvObject(905, "grata_pavimento", "Una solida grata di ferro sul pavimento. Sotto si sente scorrere altra acqua fetida.");
        AdvObject chiaveInglese = new AdvObject(906, "chiave_inglese", "Una pesante chiave inglese d'acciaio abbandonata dagli umani sulla grata. È troppo grande e pesante per essere sollevata da noi giocattoli, inutile provarci.");
        AdvObject lattina = new AdvObject(907, "lattina", "Una lattina rossa schiacciata. Un altro rifiuto del mondo di sopra finito in questo abisso.");
        AdvObject scarafaggi = new AdvObject(908, "scarafaggi", "Persino qui dentro ci sono quegli insetti disgustosi che zampettano sui mattoni rossastri. Non perdiamo tempo con loro, concentriamoci sulla leva!");

        // 4. AGGIUNTA DI TUTTI GLI OGGETTI ALLA STANZA
        stanzaBuia.getObjects().add(generatore);
        stanzaBuia.getObjects().add(tuboRitorno);
        stanzaBuia.getObjects().add(cavi);
        stanzaBuia.getObjects().add(ragnatele);
        stanzaBuia.getObjects().add(grataPavimento);
        stanzaBuia.getObjects().add(chiaveInglese);
        stanzaBuia.getObjects().add(lattina);
        stanzaBuia.getObjects().add(scarafaggi);
    }

    private void configureCasaTopo() {
        // 1. USCITE (I collegamenti bidirezionali)
        casaTopo.addExit("porticina", fognaPrimaStanza); // Per tornare indietro
        
        // Il buco stretto porta alla stanza della leva. Il sistema dovrà verificare
        // che il personaggio attivo sia Jessie prima di permettere il passaggio.
        casaTopo.addExit("buco_stretto", stanzaLeva); 

        // 2. NPC E OGGETTI DELLA STORIA
        NonPlayableCharacter topoHacker = new NonPlayableCharacter(1001, "topo", "Il topo smanettone! Ora che gli abbiamo ridato la corrente, sta digitando furiosamente sulla tastiera del suo computer.");
        
        AdvObject bucoStretto = new AdvObject(1002, "buco_stretto", "In alto a destra, tra i mattoni, c'è uno stretto condotto d'aerazione buio. Serve un giocattolo molto agile, snodato e scattante per infilarcisi.");

        // 3. OGGETTI DI SCENARIO EXTRA (Basati sull'immagine casatoo.jpg)
        AdvObject cartello = new AdvObject(1003, "cartello", "Un'insegna luminosa al neon recita: 'MOUSECRAFT HQ - INTRUDERS WILL BE ZAPPED!'. Questo roditore fa sul serio in fatto di sicurezza.");
        AdvObject computer = new AdvObject(1004, "computer", "Un vecchio monitor a tubo catodico che mostra mappe di rete e linee di codice. Spero stia tracciando i ladri di torte e non scaricando virus.");
        AdvObject scaffaliConsole = new AdvObject(1005, "scaffali", "Mensole stracolme di vecchie console grigie e cartucce di videogiochi. Se non fossimo in un'emergenza torta di livello rosso, chiederei al topo di fare una partita!");
        AdvObject actionFigures = new AdvObject(1006, "action_figures", "Ehi, ci sono altri giocattoli in alto sulla mensola! Un idraulico baffuto e un elfo con la spada. Ma sembrano immobili, roba da collezione. Non possono aiutarci.");
        AdvObject portaTecnologica = new AdvObject(1007, "porta_interna", "Il retro della porta da cui siamo entrati è un groviglio di cavi, schermi di stato e persino dei controller arcade incastrati nel legno.");
        AdvObject topoPiccolo = new AdvObject(1008, "topo_piccolo", "C'è un topolino normale che fa capolino da un tubicino in basso a destra nell'acqua. Probabilmente è solo un fattorino, ignoriamolo.");
        AdvObject spazzaturaAcqua = new AdvObject(1009, "spazzatura_acqua", "Anche qui l'acqua puzza e galleggiano lattine vuote. Rimanere all'asciutto sul pavimento di legno del topo è la priorità assoluta.");

        // 4. AGGIUNTA DI TUTTI GLI OGGETTI ALLA STANZA
        casaTopo.getObjects().add(topoHacker);
        casaTopo.getObjects().add(bucoStretto);
        casaTopo.getObjects().add(cartello);
        casaTopo.getObjects().add(computer);
        casaTopo.getObjects().add(scaffaliConsole);
        casaTopo.getObjects().add(actionFigures);
        casaTopo.getObjects().add(portaTecnologica);
        casaTopo.getObjects().add(topoPiccolo);
        casaTopo.getObjects().add(spazzaturaAcqua);
    }

    private void configureStanzaLeva() {
        // 1. USCITE (I collegamenti bidirezionali)
        // L'unica via d'uscita è ripassare per il cunicolo da cui Jessie è entrata.
        stanzaLeva.addExit("buco_stretto", casaTopo); 

        // 2. OGGETTI PER GLI ENIGMI DELLA STORIA
        AdvObject leva = new AdvObject(1101, "leva", "Il meccanismo di controllo principale per il deflusso dell'acqua. Il manico di metallo è completamente spezzato alla base! La fessura è larga, ci vorrebbe qualcosa da incastrare per fare leva e sbloccare le valvole.");

        // 3. OGGETTI DI SCENARIO EXTRA (Basati sull'immagine leva.jpg)
        AdvObject cartello = new AdvObject(1102, "cartello", "Una targa metallica avvitata al muro recita: 'LEVA DI COMANDO - ROTTA'. Molto rassicurante... Se non la aggiusto, i miei amici resteranno bloccati di là!");
        AdvObject tubature = new AdvObject(1103, "tubature", "Un intrico di vecchi tubi incrostati e valvole arrugginite. Devono essere collegati alla vasca principale della fogna.");
        AdvObject manometri = new AdvObject(1104, "manometri", "Dei vecchi indicatori di pressione industriali. Le lancette sono immobili sullo zero, il sistema idraulico è chiaramente in stallo.");
        AdvObject grata = new AdvObject(1106, "grata_pavimento", "Una pesante grata di metallo sotto i miei stivali. L'acqua scorre silenziosa laggiù in basso.");
        AdvObject utensili = new AdvObject(1107, "utensili", "Delle chiavi inglesi abbandonate sul pavimento. Sono enormi e d'acciaio massiccio, le mie braccia di stoffa non riuscirebbero mai a sollevarle.");
        AdvObject ragnatele = new AdvObject(1108, "ragnatele", "Spessi fili di ragnatela coprono gli angoli più alti della stanza. Speriamo che il ragno non sia in casa.");
        AdvObject scarafaggi = new AdvObject(1109, "scarafaggi", "Un paio di scarafaggi pattugliano il muro di mattoni rossi. Fanno venire i brividi, ma non sembrano interessati a me. Meglio concentrarsi sulla leva.");
        AdvObject arcoMuro = new AdvObject(1110, "arco_muro", "L'apertura ad arco da cui sono sbucata. È davvero stretta, Woody o Buzz si sarebbero incastrati al primo metro.");

        // 4. AGGIUNTA DI TUTTI GLI OGGETTI ALLA STANZA
        stanzaLeva.getObjects().add(leva);
        stanzaLeva.getObjects().add(cartello);
        stanzaLeva.getObjects().add(tubature);
        stanzaLeva.getObjects().add(manometri);
        stanzaLeva.getObjects().add(grata);
        stanzaLeva.getObjects().add(utensili);
        stanzaLeva.getObjects().add(ragnatele);
        stanzaLeva.getObjects().add(scarafaggi);
        stanzaLeva.getObjects().add(arcoMuro);
    }

    private void configureFognaSecondaStanza() {
        // L'uscita per tornare indietro verso l'ingresso, passando per il cancello aperto.
        fognaSecondaStanza.addExit("cancello", ingressoFogna); 
        
        // Il varco alle spalle dello scarafaggio. Inizialmente l'NPC bloccherà il passaggio,
        // ma la logica del gioco permetterà di passare dopo avergli dato il torsolo di mela.
        fognaSecondaStanza.addExit("varco", stanzaAcqua); 

        // 2. NPC E OGGETTI DELLA STORIA
        NonPlayableCharacter scarafaggioCiccione = new NonPlayableCharacter(1201, "scarafaggio_gigante", "Un enorme scarafaggio incredibilmente grasso. Ha persino dei funghi che gli crescono in testa! Se ne sta seduto a bloccare completamente l'arco del tunnel e si sfrega le zampe sulla pancia. Ha l'aria di chi non si sposterà di un millimetro...");
        
        // 3. OGGETTI DI SCENARIO EXTRA (Basati sull'immagine image_7b53b1.jpg)
        AdvObject cancelloAperto = new AdvObject(1202, "cancello_aperto", "Il pesante cancello di ferro a sbarre. Per fortuna siamo riusciti a forzarlo, la via per tornare all'ingresso è libera.");
        AdvObject lucchettoRotto = new AdvObject(1203, "lucchetto_rotto", "Un lucchetto dorato giace rotto sui gradini di pietra. La forcina di Molly ha fatto il suo dovere.");
        AdvObject paperella = new AdvObject(1204, "paperella", "Una piccola paperella di gomma colorata galleggia tristemente nella melma verde. Sembra aver perso ogni speranza. Dobbiamo recuperare la torta in fretta, o faremo la sua stessa fine!");
        AdvObject cartone = new AdvObject(1205, "cartone", "Dei pezzi di cartone inzuppati galleggiano nell'acqua fetida. Potremmo usarli come piattaforme improvvisate, ma non sembrano in grado di reggere il nostro peso di plastica.");
        AdvObject elmetto = new AdvObject(1206, "guscio_sospetto", "C'è qualcosa di tondo e grigiastro nell'acqua... Sembra un vecchio elmetto di plastica bucato o il guscio di qualche strana creatura. Meglio starci alla larga.");
        AdvObject scarafaggiPiccoli = new AdvObject(1207, "scarafaggi_piccoli", "Altri scarafaggi pattugliano i bordi asciutti della stanza. Fortunatamente sembrano intimoriti dal bestione gigante al centro e non ci badano.");
        AdvObject acquaVerde = new AdvObject(1208, "acqua_verde", "Un vero e proprio lago di scarti radioattivi. Caderci dentro significherebbe cortocircuiti certi per Buzz e macchie indelebili per Woody e Jessie.");

        // 4. AGGIUNTA DI TUTTI GLI OGGETTI ALLA STANZA
        fognaSecondaStanza.getObjects().add(scarafaggioCiccione);
        fognaSecondaStanza.getObjects().add(cancelloAperto);
        fognaSecondaStanza.getObjects().add(lucchettoRotto);
        fognaSecondaStanza.getObjects().add(paperella);
        fognaSecondaStanza.getObjects().add(cartone);
        fognaSecondaStanza.getObjects().add(elmetto);
        fognaSecondaStanza.getObjects().add(scarafaggiPiccoli);
        fognaSecondaStanza.getObjects().add(acquaVerde);
    }

    private void configureStanzaAcqua() {
       // 1. USCITE (I collegamenti bidirezionali)
        stanzaAcqua.addExit("tunnel", fognaSecondaStanza); // Per tornare indietro dallo scarafaggio gigante
        
        // L'uscita verso il Boss. Il sistema bloccherà il passaggio con un messaggio 
        // finché il flag "ACQUA_SVUOTATA" non diventerà true (attivato da Jessie).
        stanzaAcqua.addExit("botola", bossFinale); 

        // 2. OGGETTI PER GLI ENIGMI DELLA STORIA
        AdvObject botola = new AdvObject(1301, "botola", "Una massiccia botola circolare sul pavimento, posizionata proprio al centro della stanza. Sicuramente porta al covo del ladro di torte! Purtroppo è completamente sommersa. Se proviamo ad aprirla e a scendere ora, i nostri meccanismi interni faranno cortocircuito. Dobbiamo assolutamente trovare il modo di prosciugare questa vasca.");
        
        // 3. OGGETTI DI SCENARIO EXTRA (Basati sull'immagine image_85b95d.jpg)
        AdvObject acqua = new AdvObject(1302, "acqua", "Un disgustoso lago sotterraneo verde e melmoso. Ci impedisce di raggiungere la botola senza bagnarci. Serve un miracolo idraulico per farla sparire.");
        AdvObject cartello = new AdvObject(1303, "cartello", "Un vecchio cartello arrugginito con la scritta 'MAINTENANCE'. Evidentemente gli umani non fanno manutenzione qui sotto da decenni. Tocca a noi giocattoli risolvere il problema.");
        AdvObject graffito = new AdvObject(1304, "graffito", "Qualcuno ha graffiato lo scheletro di un topo sul muro vicino all'ingresso. Un avvertimento macabro per chiunque osi addentrarsi troppo a fondo.");
        AdvObject pneumatici = new AdvObject(1305, "pneumatici", "Enormi pneumatici di scarto affondano nella melma come mostri marini di gomma. Troppo viscidi e instabili per usarli come ponte.");
        AdvObject catene = new AdvObject(1307, "catene", "Pesanti catene di ferro pendono dal soffitto. Non arrivano fino alla botola, quindi non possiamo usarle per calarci senza toccare l'acqua.");
        AdvObject grataFondo = new AdvObject(1308, "grata_fondo", "Una massiccia grata in fondo alla stanza. Oltre quelle spesse sbarre si intravede solo oscurità e altra spazzatura.");
        AdvObject rifiuti = new AdvObject(1309, "rifiuti", "Lattine rosse schiacciate, tappi e pezzi di cartone galleggiano tristi nell'acqua. Non permetteremo che la torta di Andy faccia questa fine!");

        // 4. AGGIUNTA DI TUTTI GLI OGGETTI ALLA STANZA
        stanzaAcqua.getObjects().add(botola);
        stanzaAcqua.getObjects().add(acqua);
        stanzaAcqua.getObjects().add(cartello);
        stanzaAcqua.getObjects().add(graffito);
        stanzaAcqua.getObjects().add(pneumatici);
        stanzaAcqua.getObjects().add(catene);
        stanzaAcqua.getObjects().add(grataFondo);
        stanzaAcqua.getObjects().add(rifiuti);
    }

    private void configureStanzaSenzaAcqua() {
        // 1. USCITE (I collegamenti bidirezionali)
        stanzaSenzaAcqua.addExit("tunnel", fognaSecondaStanza); // Per tornare indietro
        
        // La botola ora è accessibile! Nessun blocco di sistema qui.
        stanzaSenzaAcqua.addExit("botola", bossFinale); 

        // 2. OGGETTI PRINCIPALI DELLA STORIA
        AdvObject botola = new AdvObject(1401, "botola", "La massiccia botola circolare è finalmente all'asciutto! La spessa ruggine la rende un po' dura da sollevare, ma unendo le forze possiamo aprirla e scendere nel covo del ladro di torte. Prepariamoci al peggio.");

        // 3. OGGETTI DI SCENARIO EXTRA
        AdvObject pavimento = new AdvObject(1402, "pavimento", "Ora che l'acqua è defluita, si vedono le vecchie lastre di pietra coperte di fango scivoloso. Dobbiamo fare molta attenzione a non scivolare con i nostri piedi di plastica.");
        AdvObject pneumatici = new AdvObject(1403, "pneumatici", "Gli enormi pneumatici ora riposano sul fondo melmoso. Puzzano terribilmente di gomma marcia e umidità.");
        AdvObject rifiuti = new AdvObject(1404, "rifiuti", "Lattine, tappi colorati, pezzi di cartone e chiavi inglesi arrugginite giacciono abbandonati sul pavimento bagnato. Non c'è niente di utile per noi qui, l'unica cosa che conta è tuffarci oltre quella botola.");
        AdvObject cartello = new AdvObject(1405, "cartello", "Il solito vecchio cartello 'MAINTENANCE'. Fortunatamente la vera manutenzione l'ha fatta Jessie tirando quella leva!");
        AdvObject graffito = new AdvObject(1406, "graffito", "Lo scheletro di topo graffiato sul muro sembra fissarci dal buio. Speriamo vivamente di non fare la stessa fine una volta scesi di sotto.");
        AdvObject grataFondo = new AdvObject(1407, "grata_fondo", "L'acqua putrida è defluita tutta oltre questa massiccia grata di scarico in fondo alla stanza, lasciandoci via libera.");

        // 4. AGGIUNTA DI TUTTI GLI OGGETTI ALLA STANZA
        stanzaSenzaAcqua.getObjects().add(botola);
        stanzaSenzaAcqua.getObjects().add(pavimento);
        stanzaSenzaAcqua.getObjects().add(pneumatici);
        stanzaSenzaAcqua.getObjects().add(rifiuti);
        stanzaSenzaAcqua.getObjects().add(cartello);
        stanzaSenzaAcqua.getObjects().add(graffito);
        stanzaSenzaAcqua.getObjects().add(grataFondo);
    }

    private void configureBossFinale() {
        // TODO: Inserisci qui addExit e aggiunta oggetti
        bossFinale.addExit("botola",stanzaSenzaAcqua);
    }
}