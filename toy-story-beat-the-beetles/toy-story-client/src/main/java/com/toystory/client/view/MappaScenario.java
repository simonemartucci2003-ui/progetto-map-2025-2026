/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.client.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mappa pulita degli oggetti cliccabili nella Camera di Andy.
 */
public class MappaScenario {
    
    private final Map<String, List<BoundingBox>> stanze;
    private String stanzaCorrenteId = "CAMERA_DI_ANDY";

    public MappaScenario() {
        this.stanze = new HashMap<>();
        inizializzaMappe();
    }

    private void inizializzaMappe() {
        // Dimensioni reali dell'immagine (cambiale se necessario)
        double imgW = 1424.0; 
        double imgH = 748.0; 

        List<BoundingBox> stanzaAndy = new ArrayList<>();
        
        // Segui questo formato: new BoundingBox(X_MIN, X_MAX, Y_MIN, Y_MAX, "NOME_IDENTIFICATIVO", imgW, imgH)
        
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
        corridoio.add(new BoundingBox(93, 222, 63, 560, "porta_andy", imgW, imgH));
        
        // 2. Porta per andare in camera di Molly
        corridoio.add(new BoundingBox(1025, 1221, 98, 489, "porta_molly", imgW, imgH));
        
        // 3. Scale per scendere al piano terra
        corridoio.add(new BoundingBox(596, 1422, 583, 720, "scale", imgW, imgH));

        // Registriamo la mappa del corridoio nel sistema usando il suo ID logico
        stanze.put("CORRIDOIO_PRIMO_PIANO", corridoio);
        
        // --- MAPPA DELLA CAMERA DI MOLLY ---
        List<BoundingBox> cameraMolly = new ArrayList<>();
        
        // 1. Porta (per tornare al corridoio)
        cameraMolly.add(new BoundingBox(1249, 1402, 150, 494, "porta", imgW, imgH));
        
        // 2. Baule (ora puoi usare PRENDI pallina direttamente cliccando qui)
        cameraMolly.add(new BoundingBox(1120, 1414, 518, 677, "baule_molly", imgW, imgH));
        
        // 3. Bo Peep (aggiungiamo solo l'oggetto di scenario per poterci parlare/guardare)
        cameraMolly.add(new BoundingBox(343, 403, 297, 399, "bo_peep", imgW, imgH));
       
        // 4. Letto 
        cameraMolly.add(new BoundingBox(17, 479, 433, 657, "letto_molly", imgW, imgH));

        stanze.put("CAMERA_DI_MOLLY", cameraMolly);
        
        // --- MAPPA DEL CORRIDOIO PIANO TERRA ---
        List<BoundingBox> corridoioTerra = new ArrayList<>();
        
        // 1. Scale per tornare su
        corridoioTerra.add(new BoundingBox(841, 1422, 1, 650, "scale", imgW, imgH));
        
        // 2. Porta cucina
        corridoioTerra.add(new BoundingBox(567, 800, 159, 571, "porta_cucina", imgW, imgH));
        
        // 3. Porticina cane per uscire in giardino
        corridoioTerra.add(new BoundingBox(84, 254, 162, 696, "porticina_cane", imgW, imgH));
        
        stanze.put("CORRIDOIO_PIANO_TERRA", corridoioTerra);
        
        // --- MAPPA DELLA CUCINA ---
        List<BoundingBox> cucina = new ArrayList<>();
        // 1. porta per uscire dalla cucina
        cucina.add(new BoundingBox(105, 370, 118, 692, "porta", imgW, imgH));
        
        // 2. scarafaggi che mangiano le briciole
        cucina.add(new BoundingBox(920, 1125, 505, 638, "scarafaggi", imgW, imgH));
        
        stanze.put("CUCINA", cucina);
        
        // --- MAPPA DEL GIARDINO ---
        List<BoundingBox> giardino = new ArrayList<>();
        // 1. porta per entrare in casa
        giardino.add(new BoundingBox(444, 598, 119, 431, "porta", imgW, imgH));
        
        //2. albero su cui puo salire jessie e trovare il ramo
        giardino.add(new BoundingBox(790, 1122, 26, 270, "albero", imgW, imgH));
        
        //3. ingresso alle fogne
        giardino.add(new BoundingBox(893, 1097, 595, 715, "tombino", imgW, imgH));
        
        //4. spazzatura dove si trovail torsolo di mela 
        giardino.add(new BoundingBox(1106, 1309, 448, 625, "sacchiSpazzatura", imgW, imgH));
        
        stanze.put("GIARDINO", giardino);
        
        // --- MAPPA DELL INGRESSO FOGNA ---
        List<BoundingBox> ingressoFogna = new ArrayList<>();
        // 1. tombino per risalire in superficie
        ingressoFogna.add(new BoundingBox(563, 871 ,137 ,221 , "tombino", imgW, imgH));
        
        //2. tunnel per accedere a fogna stanza 2
        ingressoFogna.add(new BoundingBox(1054, 1379 ,153 ,499 , "tunnel", imgW, imgH));
        
        //3. cancello per acedere a fogna stanza 1 
        ingressoFogna.add(new BoundingBox(159, 356 ,211 ,601 , "cancello", imgW, imgH));
        
        stanze.put("INGRESSO_FOGNATURE", ingressoFogna);
        
        // --- FOGNA PRIMA STANZA (topo)---
        List<BoundingBox> fognaStanza1 = new ArrayList<>();
        // 1. 
        fognaStanza1.add(new BoundingBox(440, 533 , 312 , 426 , "topo", imgW, imgH));
        
        // 2.
        fognaStanza1.add(new BoundingBox(541, 645 , 253 , 407 , "porticina", imgW, imgH));
        
        // 3.
        fognaStanza1.add(new BoundingBox(161, 357 , 206 , 505 , "tubo_buio", imgW, imgH));
        
        // 4.
        fognaStanza1.add(new BoundingBox(1066, 1377 , 223 , 511 , "tunnel", imgW, imgH));
        
        stanze.put("FOGNE_PRIMA_STANZA",  fognaStanza1);
        
        // --- FOGNA STANZA BUIA ---
        List<BoundingBox> fognaStanzaBuia = new ArrayList<>();
        // 1. 
        fognaStanzaBuia.add(new BoundingBox(1025, 1197 , 293 , 563 , "generatore", imgW, imgH));
        
        // 2. 
        fognaStanzaBuia.add(new BoundingBox(189, 652 , 182 , 611 , "tubo_ritorno", imgW, imgH));
        
        stanze.put("STANZA_BUIA", fognaStanzaBuia);
        
        // --- CASA TOPO ---
        List<BoundingBox> casaTopo = new ArrayList<>();
        // 1. 
        casaTopo.add(new BoundingBox(289, 479 , 240 , 659 , "porticina", imgW, imgH));
        
        // 2. 
        casaTopo.add(new BoundingBox(1216, 1341 , 88 , 256 , "buco_stretto", imgW, imgH));
        
        // 3. 
        casaTopo.add(new BoundingBox(798, 938 , 394 , 597 , "topo", imgW, imgH));
        
        stanze.put("CASA_DEL_TOPO", casaTopo);
        
        // --- STANZA LEVA---
        List<BoundingBox> stanzaLeva = new ArrayList<>();
        // 1. 
        stanzaLeva.add(new BoundingBox(311, 435 , 156 , 345 , "buco_stretto", imgW, imgH));
        
        // 2. 
        stanzaLeva.add(new BoundingBox(668, 806 , 300 ,469 , "leva", imgW, imgH));
       
        
        stanze.put("STANZA_DELLA_LEVA", stanzaLeva);
        
        
        // --- FOGNA SECONDA STANZA ---
        List<BoundingBox> fognaStanza2 = new ArrayList<>();
        // 1. tunnel bloccato da scarafaggione
        fognaStanza2.add(new BoundingBox(578, 860 , 215 , 521 , "varco", imgW, imgH));
        
        // 2. cancello per tornare indietro
        fognaStanza2.add(new BoundingBox(166, 355 , 213 , 568 , "cancello", imgW, imgH));
        
        stanze.put("FOGNE_SECONDA_STANZA",  fognaStanza2);
        
        
        // --- FOGNA STANZA CON ACQUA ---
        List<BoundingBox> StanzaAcqua = new ArrayList<>();
        // 1. 
        StanzaAcqua.add(new BoundingBox(167, 358 , 232 , 574 , "tunnel", imgW, imgH));
        
        // 2. 
        StanzaAcqua.add(new BoundingBox(764, 1003 , 444, 565 , "botola", imgW, imgH));
        
        stanze.put("STANZA_CON_ACQUA",  StanzaAcqua);
        
        // --- FOGNA STANZA CON ACQUA ---
        List<BoundingBox> StanzaSenzaAcqua = new ArrayList<>();
        // 1. 
        StanzaSenzaAcqua.add(new BoundingBox(167, 358 , 232 , 574 , "tunnel", imgW, imgH));
        
        // 2. 
        StanzaSenzaAcqua.add(new BoundingBox(764, 1003 , 444, 565 , "botola", imgW, imgH));
        
        stanze.put("STANZA_SENZA_ACQUA",  StanzaSenzaAcqua);
     
        

        
        
        
   
        
        
    }

    public void setStanzaCorrenteId(String stanzaId) {
        if (stanze.containsKey(stanzaId)) {
            this.stanzaCorrenteId = stanzaId;
        }
    }

   /* public void setStanzaCorrenteId(String stanzaId) {
        System.out.println("[DEBUG MAPPA] Richiesto caricamento BoundingBox per: " + stanzaId);
        
        if (stanze.containsKey(stanzaId)) {
            this.stanzaCorrenteId = stanzaId;
            System.out.println("[DEBUG MAPPA] Successo! Mappa cambiata.");
        } else {
            System.out.println("[DEBUG MAPPA] ERRORE CRITICO: La mappa '" + stanzaId + "' non esiste nel file MappaScenario!");
        }
    }*/
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