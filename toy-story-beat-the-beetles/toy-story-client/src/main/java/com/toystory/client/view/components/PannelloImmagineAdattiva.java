package com.toystory.client.view.components; 

import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * Componente grafico personalizzato per il rendering di sfondi adattivi.
 * <p>
 * Questo pannello estende {@link JPanel} e garantisce che l'immagine di sfondo 
 * venga ridimensionata automaticamente per coprire l'intera area disponibile del 
 * componente, indipendentemente dalle dimensioni del pannello stesso.
 * </p>
 * 
 */
public class PannelloImmagineAdattiva extends JPanel {
    private Image immagineStanza;
    
    /**
     * Inizializza il pannello e carica l'immagine di sfondo predefinita.
     */
    public PannelloImmagineAdattiva() {
        setOpaque(false);
        // Carichiamo l'immagine di default 
        URL url = getClass().getResource("/AndyRoom1.jpg");
        if (url != null) {
            this.immagineStanza = new ImageIcon(url).getImage();
        } else {
            // Fallback per ambienti di sviluppo dove il path delle risorse potrebbe variare
            this.immagineStanza = new ImageIcon("src/main/java/resources/AndyRoom1.jpg").getImage();
        }
    }
    
    /**
     * Esegue il rendering del componente, forzando l'immagine di sfondo a 
     * adattarsi alle dimensioni correnti del pannello.
     * 
     * @param g L'oggetto {@link Graphics} utilizzato per il disegno.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (immagineStanza != null) {
            // Il ridimensionamento avviene in tempo reale basandosi su getWidth() e getHeight()
            g.drawImage(immagineStanza, 0, 0, getWidth(), getHeight(), this);
        }
    }
    /**
     * Aggiorna l'immagine di sfondo visualizzata nel pannello.
     * <p>
     * Una volta caricata la nuova immagine, il pannello invoca automaticamente 
     * {@code repaint()} per aggiornare immediatamente la vista.
     * </p>
     * 
     * @param percorsoRisorsa Il path relativo alla risorsa immagine (es. "/MollyRoom3.png").
     */
    public void cambiaImmagine(String percorsoRisorsa) {
        java.net.URL url = getClass().getResource(percorsoRisorsa);
        if (url != null) {
            this.immagineStanza = new javax.swing.ImageIcon(url).getImage();
            this.repaint(); // Forza il ridisegno immediato con il nuovo sfondo!
        } else {
            System.err.println("[Errore Grafica] Immagine sfondo non trovata: " + percorsoRisorsa);
        }
    }
}