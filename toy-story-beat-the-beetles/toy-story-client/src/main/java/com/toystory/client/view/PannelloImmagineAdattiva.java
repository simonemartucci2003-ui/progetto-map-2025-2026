/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.client.view; // Controlla che il package sia identico al tuo

import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class PannelloImmagineAdattiva extends JPanel {
    private Image immagineStanza;

    public PannelloImmagineAdattiva() {
        setOpaque(false);
        // Carichiamo l'immagine di default (assicurati che il nome file sia corretto)
        URL url = getClass().getResource("/AndyRoom1.jpg");
        if (url != null) {
            this.immagineStanza = new ImageIcon(url).getImage();
        } else {
            // Fallback locale di sicurezza se Maven fa i capricci con le risorse
            this.immagineStanza = new ImageIcon("src/main/java/resources/AndyRoom1.jpg").getImage();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (immagineStanza != null) {
            // Questo è il comando magico: costringe l'immagine a seguire 
            // la larghezza e l'altezza del pannello in tempo reale
            g.drawImage(immagineStanza, 0, 0, getWidth(), getHeight(), this);
        }
    }
}