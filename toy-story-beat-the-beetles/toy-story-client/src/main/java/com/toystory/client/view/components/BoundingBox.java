package com.toystory.client.view.components;

/**
 * Definisce un'area rettangolare interattiva all'interno di un'immagine di gioco.
 * <p>
 * La classe memorizza le coordinate come percentuali (0.0 - 1.0) anziché come pixel assoluti.
 * Questo approccio garantisce che le aree cliccabili rimangano coerenti anche se il pannello
 * grafico viene ridimensionato o visualizzato su risoluzioni differenti.
 * </p>
 * 
 */
public class BoundingBox {
    // Memorizziamo le percentuali (da 0.0 a 1.0) anziché i pixel fissi
    private final double minXPct, maxXPct, menuYPct, maxYPct;
    private final String targetName;

    /**
     * Crea una nuova area interattiva basata sulle coordinate in pixel dell'immagine sorgente.
     * 
     * @param minX Coordinata X iniziale (in pixel).
     * @param maxX Coordinata X finale (in pixel).
     * @param minY Coordinata Y iniziale (in pixel).
     * @param maxY Coordinata Y finale (in pixel).
     * @param targetName Identificativo logico dell'oggetto associato a quest'area.
     * @param imgWidth Larghezza dell'immagine di riferimento originale (in pixel).
     * @param imgHeight Altezza dell'immagine di riferimento originale (in pixel).
     */
    public BoundingBox(int minX, int maxX, int minY, int maxY, String targetName, double imgWidth, double imgHeight) {
        this.minXPct = minX / imgWidth;
        this.maxXPct = maxX / imgWidth;
        this.menuYPct = minY / imgHeight;
        this.maxYPct = maxY / imgHeight;
        this.targetName = targetName;
    }

    /**
     * Verifica se un punto cliccato si trova all'interno di questa area rettangolare.
     * 
     * @param clickX Coordinata X del mouse nel pannello.
     * @param clickY Coordinata Y del mouse nel pannello.
     * @param pannelloWidth Larghezza attuale del pannello in pixel.
     * @param pannelloHeight Altezza attuale del pannello in pixel.
     * @return true se il punto è contenuto nell'area, false altrimenti.
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