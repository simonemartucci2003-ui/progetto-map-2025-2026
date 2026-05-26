/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.client.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

public class PixelButtonGenerator {

   /**
     * Genera dinamicamente un'icona in pixel art stile camera di Andy per i verbi.
     * * @param testo Il verbo da scrivere (es. "GUARDA")
     * @param isHover Se vero, altera i colori per simulare il passaggio del mouse
     * @return L'oggetto ImageIcon pronto per essere applicato al bottone Swing
     */
    public static ImageIcon createToyStoryButton(String testo, boolean isHover) {
        int larghezza = 90;
        int altezza = 40;

        // 1. Creiamo un'immagine virtuale nascosta in memoria (Buffer)
        BufferedImage img = new BufferedImage(larghezza, altezza, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        // Disattiviamo l'anti-alias per forzare l'effetto pixelato anni '90
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // 2. SFONDO: Azzurro Toy Story (colore più chiaro se il mouse ci passa sopra)
        Color azzurroAndy = isHover ? new Color(110, 190, 255) : new Color(54, 143, 240);
        g.setColor(azzurroAndy);
        g.fillRect(0, 0, larghezza, altezza);

        // 3. DISEGNO DELLE NUVOLETTE BIANCHE (Pixel art geometrica a blocchi)
        g.setColor(new Color(255, 255, 255, 170)); // Bianco leggermente trasparente
        
        // Nuvoletta in alto a sinistra
        g.fillRect(12, 6, 14, 5);
        g.fillRect(8, 9, 22, 4);
        
        // Nuvoletta in basso a destra
        g.fillRect(56, 22, 18, 5);
        g.fillRect(51, 25, 26, 4);

        // 4. DOPPIO BORDO RETRO (Stile interfaccia LucasArts)
        g.setColor(new Color(15, 60, 160)); // Bordo esterno blu scuro
        g.drawRect(0, 0, larghezza - 1, altezza - 1);
        g.setColor(Color.WHITE);             // Bordo interno di rifinitura bianco
        g.drawRect(1, 1, larghezza - 3, altezza - 3);

        // 5. SCRITTURA DEL TESTO IN STILE CARTOON
        // Usiamo Arial Black che è nativo in tutti i PC ed è cicciotto stile logo di un giocattolo
        Font fontCartoon = new Font("Arial Black", Font.BOLD, 11);
        g.setFont(fontCartoon);

        // Calcolo matematico per centrare perfettamente la scritta nel rettangolo
        FontMetrics fm = g.getFontMetrics();
        int x = (larghezza - fm.stringWidth(testo)) / 2;
        int y = ((altezza - fm.getHeight()) / 2) + fm.getAscent();

        // Disegniamo l'ombra nera spessa sotto al testo (tipica dei cartoni)
        g.setColor(new Color(0, 0, 0, 150));
        g.drawString(testo, x + 2, y + 2);

        // Colore della scritta: Giallo splendente, o Rosso fuoco se il mouse è sopra
        Color coloreTesto = isHover ? new Color(255, 50, 50) : new Color(255, 225, 0);
        g.setColor(coloreTesto);
        g.drawString(testo, x, y);

        g.dispose();
        return new ImageIcon(img);
    }
}