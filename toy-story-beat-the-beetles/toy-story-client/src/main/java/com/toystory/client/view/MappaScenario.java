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
        // --- CONFIGURAZIONE STANZA: STANZA DI ANDY ---
        List<BoundingBox> stanzaAndy = new ArrayList<>();
        stanzaAndy.add(new BoundingBox(823, 1033, 287, 393, "baule"));
        stanzaAndy.add(new BoundingBox(500, 600, 100, 250, "porta"));
        stanze.put("STANZA_ANDY", stanzaAndy);

        // --- CONFIGURAZIONE STANZA: LE FOGNE ---
        List<BoundingBox> fogne = new ArrayList<>();
        fogne.add(new BoundingBox(50, 180, 300, 420, "tombino"));
        fogne.add(new BoundingBox(400, 550, 200, 350, "scarafaggio_boss"));
        stanze.put("FOGNE", fogne);
        
        // Puoi aggiungere qui infinite stanze (es. CUCINA, GIARDINO...) senza toccare la grafica!
    }

    /**
     * Cambia la stanza attiva (viene chiamato quando il server dice che ci siamo spostati).
     */
    public void setStanzaCorrenteId(String stanzaId) {
        if (stanze.containsKey(stanzaId)) {
            this.stanzaCorrenteId = stanzaId;
        }
    }

    /**
     * Riceve i pixel X e Y e restituisce il nome dell'oggetto colpito, se esiste.
     */
    public String cercaTarget(int x, int y) {
        List<BoundingBox> boundingBoxes = stanze.get(stanzaCorrenteId);
        if (boundingBoxes != null) {
            for (BoundingBox box : boundingBoxes) {
                if (box.contiene(x, y)) {
                    return box.getTargetName(); // Trovato!
                }
            }
        }
        return null; // Cliccato sul vuoto
    }
}