
package com.toystory.client.view;

import com.toystory.client.view.components.BoundingBox;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestisce la struttura logica e spaziale dello scenario di gioco.
 * <p>
 * Questa classe mantiene le definizioni delle aree cliccabili ({@link BoundingBox}) 
 * per ogni stanza e associa ciascun identificativo di stanza al relativo file immagine.
 * Agisce come un database locale per il client, permettendo la traduzione delle 
 * coordinate del mouse in identificativi logici di gioco.
 * </p>
 * 
 */
public class MappaScenario {
    
    private final Map<String, List<BoundingBox>> stanze;
    private final Map<String, String> sfondiStanze;
    private String stanzaCorrenteId = "CAMERA_DI_ANDY";
    
    /**
     * Inizializza la mappa, creando i contenitori per le stanze e i percorsi delle immagini.
     */
    public MappaScenario() {
        this.stanze = new HashMap<>();
        this.sfondiStanze = new HashMap<>();
        inizializzaMappe();
    }
    
    /**
     * Popola le strutture dati con le definizioni delle aree interattive 
     * e le associazioni tra ID stanza e file immagine.
     */
    private void inizializzaMappe() {
        // Dimensioni reali dell'immagine (cambiale se necessario)
        double imgW = 1424.0; 
        double imgH = 748.0; 

        
        // --- MAPPA STANZA DI ANDY ---
        List<BoundingBox> stanzaAndy = new ArrayList<>();
        
        // formato: new BoundingBox(X_MIN, X_MAX, Y_MIN, Y_MAX, "NOME_IDENTIFICATIVO", imgW, imgH)
        
        // 1. PORTA
        stanzaAndy.add(new BoundingBox(85, 225, 64, 557 , "porta", imgW, imgH));
        
        // 2. BAULE
        stanzaAndy.add(new BoundingBox(1124, 1401, 484, 679, "baule", imgW, imgH));
        
        // 3. LIBRERIA
        stanzaAndy.add(new BoundingBox(303, 574, 223, 502, "libreria", imgW, imgH));
        
        // 4. LETTO
        stanzaAndy.add(new BoundingBox(922, 1074, 375, 635, "letto", imgW, imgH));

        stanze.put("CAMERA_DI_ANDY", stanzaAndy);
        
        // --- MAPPA DEL CORRIDOIO PRIMO PIANO ---
        List<BoundingBox> corridoio = new ArrayList<>();
        
        // 1. Porta per tornare in camera di Andy
        corridoio.add(new BoundingBox(93, 222, 63, 560, "porta_camera_andy", imgW, imgH));
        
        // 2. Porta per andare in camera di Molly
        corridoio.add(new BoundingBox(1025, 1221, 98, 489, "porta_camera_molly", imgW, imgH));
        
        // 3. Scale per scendere al piano terra
        corridoio.add(new BoundingBox(596, 1422, 583, 720, "scale_giu", imgW, imgH));

        // Registriamo la mappa del corridoio nel sistema usando il suo ID logico
        stanze.put("CORRIDOIO_PRIMO_PIANO", corridoio);
        
        // --- MAPPA DELLA CAMERA DI MOLLY ---
        List<BoundingBox> cameraMolly = new ArrayList<>();
        
        // 1. Porta (per tornare al corridoio)
        cameraMolly.add(new BoundingBox(1249, 1402, 150, 494, "porta_molly", imgW, imgH));
        
        // 2. Baule 
        cameraMolly.add(new BoundingBox(1120, 1414, 518, 677, "baule_molly", imgW, imgH));
        
        // 3. Bo Peep 
        cameraMolly.add(new BoundingBox(343, 403, 297, 399, "bo_peep", imgW, imgH));
       
        // 4. Letto 
        cameraMolly.add(new BoundingBox(17, 479, 433, 657, "letto_molly", imgW, imgH));

        stanze.put("CAMERA_DI_MOLLY", cameraMolly);
        
        // --- MAPPA DEL CORRIDOIO PIANO TERRA ---
        List<BoundingBox> corridoioTerra = new ArrayList<>();
        
        // 1. Scale per tornare su
        corridoioTerra.add(new BoundingBox(841, 1422, 1, 650, "scale_su", imgW, imgH));
        
        // 2. Porta cucina
        corridoioTerra.add(new BoundingBox(567, 800, 159, 571, "porta_cucina", imgW, imgH));
        
        // 3. Porticina cane per uscire in giardino
        corridoioTerra.add(new BoundingBox(84, 254, 162, 696, "porticina_cane", imgW, imgH));
        
        stanze.put("CORRIDOIO_PIANO_TERRA", corridoioTerra);
        
        // --- MAPPA DELLA CUCINA ---
        List<BoundingBox> cucina = new ArrayList<>();
        
        // 1. porta per uscire dalla cucina
        cucina.add(new BoundingBox(105, 370, 118, 692, "porta_interna_cucina", imgW, imgH));
        
        // 2. scarafaggi che mangiano le briciole
        cucina.add(new BoundingBox(920, 1125, 505, 638, "scarafaggi", imgW, imgH));
        
        stanze.put("CUCINA", cucina);
        
        // --- MAPPA DEL GIARDINO ---
        List<BoundingBox> giardino = new ArrayList<>();
        
        // 1. porta per entrare in casa
        giardino.add(new BoundingBox(444, 598, 119, 431, "porta_cane", imgW, imgH));
        
        //2. albero su cui puo salire jessie e trovare il ramo
        giardino.add(new BoundingBox(790, 1122, 26, 270, "albero", imgW, imgH));
        
        //3. ingresso alle fogne
        giardino.add(new BoundingBox(893, 1097, 595, 715, "tombino", imgW, imgH));
        
        //4. spazzatura dove si trovail torsolo di mela 
        giardino.add(new BoundingBox(1106, 1309, 448, 625, "sacchi_neri", imgW, imgH));
        
        stanze.put("GIARDINO", giardino);
        
        // --- MAPPA DELL INGRESSO FOGNA ---
        List<BoundingBox> ingressoFogna = new ArrayList<>();
        
        // 1. tombino per risalire in superficie
        ingressoFogna.add(new BoundingBox(563, 871 ,137 ,221 , "grata_sopra", imgW, imgH));
        
        //2. tunnel per accedere a fogna stanza 2
        ingressoFogna.add(new BoundingBox(1054, 1379 ,153 ,499 , "tunnel", imgW, imgH));
        
        //3. cancello per acedere a fogna stanza 1 
        ingressoFogna.add(new BoundingBox(159, 356 ,211 ,601 , "cancello", imgW, imgH));
        
        stanze.put("INGRESSO_FOGNATURE", ingressoFogna);
        
        // --- FOGNA PRIMA STANZA (topo)---
        List<BoundingBox> fognaStanza1 = new ArrayList<>();
        // 1. topo nerd
        fognaStanza1.add(new BoundingBox(440, 533 , 312 , 426 , "topo", imgW, imgH));
        
        // 2. porta casa del topo
        fognaStanza1.add(new BoundingBox(541, 645 , 253 , 407 , "porticina", imgW, imgH));
        
        // 3. tubo buio utilizzabile solo da buzz
        fognaStanza1.add(new BoundingBox(161, 357 , 206 , 505 , "tubo_buio", imgW, imgH));
        
        // 4. tunnel di ritorno all ingresso fogne
        fognaStanza1.add(new BoundingBox(1066, 1377 , 223 , 511 , "tunnel_ritorno", imgW, imgH));
        
        stanze.put("FOGNE_PRIMA_STANZA",  fognaStanza1);
        
        // --- FOGNA STANZA BUIA ---
        List<BoundingBox> fognaStanzaBuia = new ArrayList<>();
        
        // 1. generatore per riattivare corrente casa topo
        fognaStanzaBuia.add(new BoundingBox(1025, 1197 , 293 , 563 , "generatore", imgW, imgH));
        
        // 2. tubo da cui siamo entrati e uscita
        fognaStanzaBuia.add(new BoundingBox(189, 652 , 182 , 611 , "tubo_ritorno", imgW, imgH));
        
        stanze.put("STANZA_BUIA", fognaStanzaBuia);
        
        // --- CASA TOPO ---
        List<BoundingBox> casaTopo = new ArrayList<>();
        
        // 1. porta da cui siamo entrati
        casaTopo.add(new BoundingBox(289, 479 , 240 , 659 , "porticina_ritorno", imgW, imgH));
        
        // 2. buco in cui puo entrare solo jessie
        casaTopo.add(new BoundingBox(1216, 1341 , 88 , 256 , "buco_stretto", imgW, imgH));
        
        // 3. topo nella casa seduto al computer
        casaTopo.add(new BoundingBox(798, 938 , 394 , 597 , "topo_casa", imgW, imgH));
        
        stanze.put("CASA_DEL_TOPO", casaTopo);
        
        // --- STANZA LEVA---
        List<BoundingBox> stanzaLeva = new ArrayList<>();
        
        // 1. buco da cui siamo entrati
        stanzaLeva.add(new BoundingBox(311, 435 , 156 , 345 , "buco_stretto_ritorno", imgW, imgH));
        
        // 2. leva per abbassare il livello dell'acqua
        stanzaLeva.add(new BoundingBox(668, 806 , 300 ,469 , "leva", imgW, imgH));
       
        stanze.put("STANZA_DELLA_LEVA", stanzaLeva);
        
        // --- FOGNA SECONDA STANZA ---
        List<BoundingBox> fognaStanza2 = new ArrayList<>();
        
        // 1. tunnel bloccato da scarafaggione
        fognaStanza2.add(new BoundingBox(578, 860 , 215 , 521 , "varco", imgW, imgH));
        
        // 2. cancello per tornare indietro
        fognaStanza2.add(new BoundingBox(166, 355 , 213 , 568 , "cancello_aperto", imgW, imgH));
        
        stanze.put("FOGNE_SECONDA_STANZA",  fognaStanza2);
        
        // --- FOGNA STANZA CON ACQUA ---
        List<BoundingBox> StanzaAcqua = new ArrayList<>();
        
        // 1. tunnel da cui siamo entrati
        StanzaAcqua.add(new BoundingBox(167, 358 , 232 , 574 , "tunnel", imgW, imgH));
        
        // 2. botola che porta al boss
        StanzaAcqua.add(new BoundingBox(764, 1003 , 444, 565 , "botola", imgW, imgH));
        
        stanze.put("STANZA_CON_ACQUA",  StanzaAcqua);
        
        // --- FOGNA STANZA ACQUA ---
        List<BoundingBox> StanzaSenzaAcqua = new ArrayList<>();
        
        // 1. tunnel da cui siamo entrati
        StanzaSenzaAcqua.add(new BoundingBox(167, 358 , 232 , 574 , "tunnel", imgW, imgH));
        
        // 2. botola che porta al boss
        StanzaSenzaAcqua.add(new BoundingBox(764, 1003 , 444, 565 , "botola_sbloccata", imgW, imgH));
        
        stanze.put("STANZA_SENZA_ACQUA",  StanzaSenzaAcqua);
        
         // --- FOGNA BOSS ---
        List<BoundingBox> StanzaBoss = new ArrayList<>();
        
        // 1. tunnel da cui siamo entrati
        StanzaBoss.add(new BoundingBox(0, 1424 , 0 , 747, "TUTTO", imgW, imgH));
        
        stanze.put("BOSS",  StanzaBoss);
        
        
        
        
        // --- ASSOCIAZIONE SFONDI ---
        sfondiStanze.put("CORRIDOIO_PRIMO_PIANO", "/CorridoioRoom2.jpg");
        sfondiStanze.put("CAMERA_DI_ANDY", "/AndyRoom1.jpg");
        sfondiStanze.put("CAMERA_DI_MOLLY", "/MollyRoom3.png");
        sfondiStanze.put("CORRIDOIO_PIANO_TERRA", "/CorridoioRoom4.png");
        sfondiStanze.put("CUCINA", "/CucinaRoom6.png");
        sfondiStanze.put("GIARDINO", "/StradaRoom5.png");
        sfondiStanze.put("INGRESSO_FOGNATURE", "/IngressoFognaRoom7.png");
        sfondiStanze.put("FOGNE_PRIMA_STANZA", "/FognaRoom8.png");
        sfondiStanze.put("STANZA_BUIA", "/FognaRoom11.png");
        sfondiStanze.put("CASA_DEL_TOPO", "/StanzaTopoRoom9.png");
        sfondiStanze.put("STANZA_DELLA_LEVA", "/FognaRoom10.png");
        sfondiStanze.put("FOGNE_SECONDA_STANZA", "/FognaRoom12.png");
        sfondiStanze.put("STANZA_CON_ACQUA", "/FognaRoom13.1.png");
        sfondiStanze.put("STANZA_SENZA_ACQUA", "/FognaRoom13.2.png");
        sfondiStanze.put("BOSS", "/BossRoom14.png");
        sfondiStanze.put("SCENA_FINALE", "/FineRoom15.png");
     
    }
    
    /**
     * Recupera il percorso del file immagine associato a una specifica stanza.
     * 
     * @param stanzaId L'identificativo logico della stanza.
     * @return Il percorso dell'immagine (es. "/AndyRoom1.jpg"), o null se non trovato.
     */
    public String getPercorsoImmagine(String stanzaId) {
        return sfondiStanze.get(stanzaId);
    }
    
    /**
     * Imposta la stanza attualmente visualizzata dal client.
     * 
     * @param stanzaId L'ID della nuova stanza da impostare come corrente.
     */
    public void setStanzaCorrenteId(String stanzaId) {
       if (stanze.containsKey(stanzaId)) {
        this.stanzaCorrenteId = stanzaId;
        } else {
        // log di errore se il server invia un ID stanza inesistente
        System.err.println("[MappaScenario] Tentativo di impostare stanza inesistente: " + stanzaId);
        }
    }
    
    /**
     * Verifica se le coordinate cliccate dall'utente corrispondono a un oggetto 
     * interattivo nella stanza corrente.
     * 
     * @param x Coordinata X del mouse.
     * @param y Coordinata Y del mouse.
     * @param larghezzaPannello Larghezza attuale del pannello grafico.
     * @param altezzaPannello Altezza attuale del pannello grafico.
     * @return Il nome del target (oggetto) cliccato, o null se non vi è alcuna interazione.
     */
    public String cercaTarget(int x, int y, int larghezzaPannello, int altezzaPannello) {
        List<BoundingBox> boundingBoxes = stanze.get(stanzaCorrenteId);
        if (boundingBoxes != null) {
            for (BoundingBox box : boundingBoxes) {
                if (box.contiene(x, y, larghezzaPannello, altezzaPannello)) {
                    return box.getTargetName();
                }
            }
        }
        return null;
    }
}