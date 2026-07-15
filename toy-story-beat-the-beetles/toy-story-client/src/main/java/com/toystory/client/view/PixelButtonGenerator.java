/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.client.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class PixelButtonGenerator {

   /**
     * Genera dinamicamente un'icona in pixel art stile camera di Andy per i verbi.
     * @param testo Il verbo da scrivere (es. "GUARDA")
     * @param isHover Se vero, altera i colori per simulare il passaggio del mouse
     * @return L'oggetto ImageIcon pronto per essere applicato al bottone Swing
     */
    public static ImageIcon createToyStoryButton(String testo, boolean isHover) {
        int larghezza = 150;
        int altezza = 100;

        BufferedImage img = new BufferedImage(larghezza, altezza, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // 2. SFONDO
        Color azzurroAndy = isHover ? new Color(110, 190, 255) : new Color(54, 143, 240);
        g.setColor(azzurroAndy);
        g.fillRect(0, 0, larghezza, altezza);

        // 3. NUVOLETTE (Scalate proporzionalmente)
        g.setColor(new Color(255, 255, 255, 170));
    
        // Nuvoletta in alto a sinistra (ingrandite per il nuovo spazio)
        g.fillRect(30, 35, 24, 8);
        g.fillRect(24, 40, 36, 7);
    
        // Nuvoletta in basso a destra
        g.fillRect(94, 55, 30, 8);
        g.fillRect(85, 60, 44, 7);


        // 5. TESTO (Font ingrandito per 150x100)
        Font fontCartoon = new Font("Arial Black", Font.PLAIN, 18);
        g.setFont(fontCartoon);

        FontMetrics fm = g.getFontMetrics();
        int x = (larghezza - fm.stringWidth(testo)) / 2;
        int y = ((altezza - fm.getHeight()) / 2) + fm.getAscent();

        // Ombra
        g.setColor(new Color(0, 0, 0, 150));
        g.drawString(testo, x + 2, y + 2);

        // Testo principale
        Color coloreTesto = isHover ? new Color(255, 50, 50) : new Color(255, 225, 0);
        g.setColor(coloreTesto);
        g.drawString(testo, x, y);

        g.dispose();
        return new ImageIcon(img);
        
        
    }
    
    
    /**
     * Applica dinamicamente uno sfondo a forma di Lavagna Magica (Etch-A-Sketch).
     * Perfetto per il pannello dei comandi/verbi.
     * @param panel Il pannello di cui modificare l'estetica.
     */
    public static void applicaSfondoPersonaggi(JPanel panel) {
        panel.setOpaque(false);
        
        panel.setBorder(new javax.swing.border.Border() {
            @Override
            public void paintBorder(java.awt.Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2 = (Graphics2D) g.create();
                // Antialiasing per le curve e le manopole rotonde
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 1. Schermo interno grigio sabbia (Tipico della Lavagna Magica)
                g2.setColor(new Color(215, 215, 205));
                g2.fillRoundRect(x, y, width, height, 15, 15);

                // 2. Cornice Rossa di plastica esterna (Molto spessa)
                g2.setColor(new Color(210, 30, 30));
                g2.setStroke(new java.awt.BasicStroke(12));
                g2.drawRoundRect(x + 6, y + 6, width - 12, height - 12, 15, 15);

                // 3. Ombra scura interna (Dà l'effetto di profondità allo schermo)
                g2.setColor(new Color(150, 150, 140, 200));
                g2.setStroke(new java.awt.BasicStroke(2));
                g2.drawRoundRect(x + 12, y + 12, width - 24, height - 24, 10, 10);
                
                // 4. Riflesso di luce (plastica lucida) sul bordo in alto
                g2.setColor(new Color(255, 100, 100, 150));
                g2.setStroke(new java.awt.BasicStroke(2));
                g2.drawLine(x + 15, y + 6, x + width - 15, y + 6);

                // 5. Le due Manopole Bianche iconiche negli angoli in basso
                g2.setColor(Color.WHITE);
                g2.fillOval(x + 2, y + height - 20, 18, 18); // Manopola Sinistra
                g2.fillOval(x + width - 20, y + height - 20, 18, 18); // Manopola Destra
                
                // Contorno scuro per far risaltare le manopole
                g2.setColor(new Color(150, 20, 20));
                g2.setStroke(new java.awt.BasicStroke(1));
                g2.drawOval(x + 2, y + height - 20, 18, 18);
                g2.drawOval(x + width - 20, y + height - 20, 18, 18);

                g2.dispose();
            }

            @Override
            public java.awt.Insets getBorderInsets(java.awt.Component c) {
                // Margini calcolati per non "schiacciare" i bottoni dei verbi,
                // lasciando giusto un po' di spazio in più in basso per le manopole.
                return new java.awt.Insets(14, 14, 18, 14);
            }

            @Override
            public boolean isBorderOpaque() {
                return false;
            }
        });
    }
    
   /**
     * Applica uno sfondo "Cielo di Andy" (Azzurro pastello con bordo rialzato).
     * @param panel Il pannello da trasformare.
     */
    public static void applicaSfondoBottoni(JPanel panel) {
        panel.setOpaque(false);
        
        panel.setBorder(new javax.swing.border.Border() {
            @Override
            public void paintBorder(java.awt.Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 1. Sfondo Azzurro Pastello (il "Cielo" di Andy)
                g2.setColor(new Color(135, 206, 235)); 
                g2.fillRoundRect(x + 2, y + 2, width - 4, height - 4, 30, 30);

                // 2. Bordo esterno (leggermente più scuro per definire il perimetro)
                g2.setColor(new Color(100, 180, 210));
                g2.setStroke(new java.awt.BasicStroke(3));
                g2.drawRoundRect(x + 2, y + 2, width - 5, height - 5, 30, 30);

                // 3. Riflesso interno (simula la lucentezza della plastica/giocattolo)
                g2.setColor(new Color(255, 255, 255, 120));
                g2.setStroke(new java.awt.BasicStroke(2));
                g2.drawRoundRect(x + 7, y + 7, width - 15, height - 15, 20, 20);

                g2.dispose();
            }

            @Override
            public java.awt.Insets getBorderInsets(java.awt.Component c) {
                return new java.awt.Insets(6, 6, 6, 6);
            }

            @Override
            public boolean isBorderOpaque() {
                return false;
            }
        });
    }
 
    
    /**
     * Applica dinamicamente uno sfondo a forma di cassa di legno molto dettagliata.
     * @param panel Il pannello di cui modificare l'estetica.
     */
    public static void applicaSfondoTasche(JPanel panel) {
        panel.setOpaque(false);
        
        panel.setBorder(new Border() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2 = (Graphics2D) g.create();
                // Attiviamo l'antialiasing per rendere i chiodi metallici perfettamente rotondi
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 1. Sfondo base (Colore legno medio)
                g2.setColor(new Color(160, 104, 48));
                g2.fillRect(x, y, width, height);

                // 2. Disegno delle assi orizzontali con Effetto 3D e Venature
                int numAssi = 4; // Dividiamo la cassa in 4 listoni
                int altezzaAsse = height / numAssi;
                
                for (int i = 0; i < numAssi; i++) {
                    int asseY = y + (i * altezzaAsse);
                    
                    // Venature del legno (linee sottili e sfalsate)
                    g2.setColor(new Color(130, 75, 30, 120)); // Marrone scuro trasparente
                    for(int j = 0; j < 4; j++) {
                        int venaturaY = asseY + 8 + (j * 10) + (i % 2 * 4); // Sfalsiamo in base all'asse
                        if(venaturaY < asseY + altezzaAsse) {
                            g2.drawLine(x + 5, venaturaY, x + width - 5, venaturaY + (j % 3 - 1));
                        }
                    }

                    // Ombra profonda inferiore (stacco tra un'asse e l'altra)
                    g2.setColor(new Color(60, 30, 10, 200));
                    g2.drawLine(x, asseY + altezzaAsse - 1, x + width, asseY + altezzaAsse - 1);
                    g2.drawLine(x, asseY + altezzaAsse - 2, x + width, asseY + altezzaAsse - 2); // Più spessa
                    
                    // Luce superiore (riflesso di profondità)
                    g2.setColor(new Color(255, 255, 255, 50));
                    g2.drawLine(x, asseY, x + width, asseY);
                }

                // 3. Cornice Spessa Esterna (Il telaio della cassa)
                int spessoreCornice = 10;
                g2.setColor(new Color(120, 70, 25)); // Legno più scuro per la cornice
                g2.fillRect(x, y, width, spessoreCornice); // Alto
                g2.fillRect(x, y + height - spessoreCornice, width, spessoreCornice); // Basso
                g2.fillRect(x, y, spessoreCornice, height); // Sinistra
                g2.fillRect(x + width - spessoreCornice, y, spessoreCornice, height); // Destra

                // Ombreggiatura 3D della cornice esterna
                g2.setColor(new Color(40, 20, 5)); // Bordo scurissimo esterno
                g2.drawRect(x, y, width - 1, height - 1);
                g2.drawRect(x + 1, y + 1, width - 3, height - 3);
                
                g2.setColor(new Color(200, 140, 80, 150)); // Bordo luminoso interno (Luce che batte)
                g2.drawRect(x + spessoreCornice - 1, y + spessoreCornice - 1, width - (spessoreCornice*2) + 1, height - (spessoreCornice*2) + 1);

                // 4. Chiodi di ferro realistici (con ombra e luce speculare)
                int[] chiodiX = {x + 5, x + width - 13, x + 5, x + width - 13};
                int[] chiodiY = {y + 5, y + 5, y + height - 13, y + height - 13};
                
                for (int i = 0; i < 4; i++) {
                    // Ombra caduta del chiodo
                    g2.setColor(new Color(0, 0, 0, 180));
                    g2.fillOval(chiodiX[i] + 1, chiodiY[i] + 1, 8, 8);
                    
                    // Base metallica del chiodo
                    g2.setColor(Color.DARK_GRAY);
                    g2.fillOval(chiodiX[i], chiodiY[i], 8, 8);
                    
                    // Riflesso della luce (fa sembrare il chiodo a cupola)
                    g2.setColor(Color.LIGHT_GRAY);
                    g2.fillOval(chiodiX[i] + 2, chiodiY[i] + 2, 3, 3);
                    g2.setColor(Color.WHITE);
                    g2.fillOval(chiodiX[i] + 2, chiodiY[i] + 2, 1, 1);
                }
                
                g2.dispose();
            }

            @Override
            public Insets getBorderInsets(Component c) {
                // Aumentiamo i margini interni a 12 pixel per non far accavallare gli oggetti alla cornice
                return new Insets(12, 12, 12, 12);
            }

            @Override
            public boolean isBorderOpaque() {
                return true;
            }
        });
    }
    
    /**
     * Trasforma un bottone standard in un bottone Toy Story.
     */
    public static void applicaStileToyStory(javax.swing.JButton bottone, String verbo) {
        bottone.setIcon(createToyStoryButton(verbo, false));
        bottone.setRolloverIcon(createToyStoryButton(verbo, true));
        bottone.setPressedIcon(createToyStoryButton(verbo, true));

        bottone.setBorderPainted(false);
        bottone.setContentAreaFilled(false);
        bottone.setFocusPainted(false);
        bottone.setText(""); 
    }

    /**
     * Imposta un'icona tonda scalata per i bottoni degli Avatar.
     */
    public static void impostaIconaAvatar(javax.swing.AbstractButton bottone, String nomeFile) {
        String path = nomeFile.startsWith("/") ? nomeFile : "/" + nomeFile;
        java.net.URL imgURL = PixelButtonGenerator.class.getResource(path);
        
        if (imgURL != null) {
            javax.swing.ImageIcon iconaOriginale = new javax.swing.ImageIcon(imgURL);
            int larghezza = 55; 
            int altezza = 55;
            
            java.awt.Image imgScalata = iconaOriginale.getImage().getScaledInstance(larghezza, altezza, java.awt.Image.SCALE_SMOOTH);
            
            bottone.setIcon(new javax.swing.ImageIcon(imgScalata));
            bottone.setPreferredSize(new java.awt.Dimension(larghezza, altezza));
            bottone.setText(""); 
            bottone.setContentAreaFilled(false);
            bottone.setBorderPainted(true);
        } else {
            System.err.println("[GUI] ERRORE GRAVE: Immagine " + nomeFile + " introvabile.");
        }
    }
    
    /**
     * Ridimensiona un'immagine in modo uniforme mantenendo la qualità.
     */
    public static javax.swing.ImageIcon ridimensionaIcona(java.net.URL imgURL, int larghezza, int altezza) {
        if (imgURL == null) return null;
        
        javax.swing.ImageIcon iconaOriginale = new javax.swing.ImageIcon(imgURL);
        java.awt.Image imgScalata = iconaOriginale.getImage().getScaledInstance(larghezza, altezza, java.awt.Image.SCALE_SMOOTH);
        return new javax.swing.ImageIcon(imgScalata);
    }
}