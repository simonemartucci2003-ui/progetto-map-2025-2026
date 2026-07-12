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
 * Gestisce i rettangoli cliccabili di tutte le stanze del gioco.
 */
public class MappaScenario {
    
    // Associa l'ID della stanza alla lista di oggetti cliccabili in quella stanza
    private final Map<String, List<BoundingBox>> stanze;
    private String stanzaCorrenteId = "STANZA_ANDY"; // Stanza di partenza

    public MappaScenario() {
        this.stanze = new HashMap<>();
        inizializzaMappe();
    }

    /**
     * Configura le coordinate geometriche per ogni stanza del gioco.
     */
    private void inizializzaMappe() {
        double imgW = 1424.0; // Metti la larghezza reale della tua immagine originale
        double imgH = 748.0; // Metti l'altezza reale della tua immagine originale

        List<BoundingBox> stanzaAndy = new ArrayList<>();
        // =====================================================================
        // 📦 IL BAULE DEI GIOCATTOLI (In basso a destra, sotto il letto)
        // =====================================================================
        stanzaAndy.add(new BoundingBox(1110, 1410, 450, 720, "baule", imgW, imgH));
        
        // 🚪 LA PORTA DI CASA (A sinistra dello schermo)
        stanzaAndy.add(new BoundingBox(60, 160, 40, 780, "porta", imgW, imgH));
        
        // 📚 LA LIBRERIA IN LEGNO (Accanto alla porta)
        stanzaAndy.add(new BoundingBox(210, 420, 310, 700, "libreria", imgW, imgH));

        stanze.put("STANZA_ANDY", stanzaAndy);
        
        // Fai lo stesso per le fogne usando le dimensioni della foto delle fogne
        
        // Puoi aggiungere qui infinite stanze (es. CUCINA, GIARDINO...) senza toccare la grafica!
    }

    /**
     * Cambia la stanza attiva (viene chiamato quando il server dice che ci siamo spostati).
     * @param stanzaId
     */
    public void setStanzaCorrenteId(String stanzaId) {
        if (stanze.containsKey(stanzaId)) {
            this.stanzaCorrenteId = stanzaId;
        }
    }

    /**
     * Riceve i pixel X e Y e restituisce il nome dell'oggetto colpito, se esiste.
     * @param x
     * @param y
     * @param larghezzaPannello
     * @param altezzaPannello
     * @return 
     */
    public String cercaTarget(int x, int y, int larghezzaPannello, int altezzaPannello) {
        List<BoundingBox> boundingBoxes = stanze.get(stanzaCorrenteId);
        if (boundingBoxes != null) {
            for (BoundingBox box : boundingBoxes) {
                // Passiamo le dimensioni correnti per il calcolo della percentuale
                if (box.contiene(x, y, larghezzaPannello, altezzaPannello)) {
                    return box.getTargetName();
                }
            }
        }
        return null;
    }
}
