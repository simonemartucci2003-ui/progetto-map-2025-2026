/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.client.view;

public class BoundingBox {
    // Memorizziamo le percentuali (da 0.0 a 1.0) anziché i pixel fissi
    private final double minXPct, maxXPct, menuYPct, maxYPct;
    private final String targetName;

    // Passiamo le coordinate dell'immagine originale e la risoluzione originale dell'immagine (es. 1920x1080)
    public BoundingBox(int minX, int maxX, int minY, int maxY, String targetName, double imgWidth, double imgHeight) {
        this.minXPct = minX / imgWidth;
        this.maxXPct = maxX / imgWidth;
        this.menuYPct = minY / imgHeight;
        this.maxYPct = maxY / imgHeight;
        this.targetName = targetName;
    }

    /**
     * Verifica se il click è proporzionalmente dentro l'area, 
     * conoscendo la larghezza e l'altezza ATTUALI del pannello di gioco.
     */
    public boolean contiene(int clickX, int clickY, int pannelloWidth, int pannelloHeight) {
        double pctX = (double) clickX / pannelloWidth;
        double pctY = (double) clickY / pannelloHeight;

        return pctX >= minXPct && pctX <= maxXPct && pctY >= menuYPct && pctY <= maxYPct;
    }

    public String getTargetName() {
        return targetName;
    }
}