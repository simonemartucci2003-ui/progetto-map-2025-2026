/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.toystory.client.view;

import com.toystory.client.GameClient;
import com.toystory.client.SaveManager;

/**
 *
 * @author simon
 */
public class GameWindow extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GameWindow.class.getName());
    private final MappaScenario mappaScenario = new MappaScenario();
    private GameClient clientRete;
    private GUIHandler handlerGrafico;
    private String azioneSelezionata = "GUARDA";
    

    /**
     * Creates new form GameWindow
     */
    public GameWindow() {
        initComponents();
        
        impostaIconaSuBottone(btnSelezionaBuzz, "icone_buzz.png");
        impostaIconaSuBottone(btnSelezionaWoody, "icone_woody.png");
        impostaIconaSuBottone(btnSelezionaJessie, "icone_jessie.png");
        
        // 1. Inizializziamo l'handler passandogli la finestra corrente (this)
        this.handlerGrafico = new GUIHandler(this); 
        
        this.clientRete = new GameClient(messaggioServer -> {
            javax.swing.SwingUtilities.invokeLater(() -> {
                handlerGrafico.processaComando(messaggioServer);
            });
        });
        
        // 1. Recuperiamo le partite salvate
        java.util.Set<String> partiteSalvate = com.toystory.client.SaveManager.leggiTutteLePartite();

        // 2. Creiamo una lista dinamica (usiamo un ArrayList perché la dimensione cambia)
        java.util.List<Object> listaOpzioni = new java.util.ArrayList<>();
        listaOpzioni.add("Nuova Partita");
        listaOpzioni.add("Unisciti");

        // Se ci sono partite salvate, aggiungiamo l'opzione per riprendere
        if (!partiteSalvate.isEmpty()) {
            listaOpzioni.add("Riprendi Partita");
        }

        Object[] opzioni = listaOpzioni.toArray();

        int scelta = javax.swing.JOptionPane.showOptionDialog(this,
                "Benvenuto in Toy Story! Scegli come vuoi giocare:",
                "Menu di Avvio",
                javax.swing.JOptionPane.YES_NO_CANCEL_OPTION,
                javax.swing.JOptionPane.QUESTION_MESSAGE,
                null, 
                opzioni, 
                opzioni[0]);

        // 3. Gestione della scelta
        // Attenzione all'indice: "Nuova Partita" è 0, "Unisciti" è 1, "Riprendi" è 2 (se esiste)
        if (scelta == 0) { 
            avviaNuovaPartita();
        } else if (scelta == 1) { 
            uniscitiPartita(null);
        } else if (scelta == 2 && !partiteSalvate.isEmpty()) {
            // Se l'utente clicca su Riprendi, facciamo scegliere quale tra quelle salvate
            String[] arrayPartite = partiteSalvate.toArray(new String[0]);
            String partitaScelta = (String) javax.swing.JOptionPane.showInputDialog(this, 
                    "Quale partita vuoi riprendere?", "Riprendi Partita", 
                    javax.swing.JOptionPane.QUESTION_MESSAGE, null, arrayPartite, arrayPartite[0]);

            if (partitaScelta != null) {
                eseguiJoin(partitaScelta);
            } else {
                System.exit(0);
            }
        } else {
            System.exit(0);
    }
          
        // Trasformiamo i bottoni standard in bottoni Pixel Art azzurri con le nuvole
        applicaStileToyStory(btnGuarda, "GUARDA");
        applicaStileToyStory(btnPrendi, "PRENDI");
        applicaStileToyStory(btnUsa, "USA");
        applicaStileToyStory(btnApri, "APRI");
        applicaStileToyStory(btnChiudi, "CHIUDI");
        applicaStileToyStory(btnParla, "PARLA");
        applicaStileToyStory(btnDai, "DAI");
        applicaStileToyStory(btnSpingi, "SPINGI");
        applicaStileToyStory(btnTira, "TIRA");
        
        // ====================================================================
        // INIZIALIZZAZIONE E TRACCIAMENTO DELLE COORDINATE DELLA STANZA
        // ====================================================================

        // 1. Puliamo il pannello da eventuali vecchi residui grafici
        pnlRappresentazioneStanza.removeAll();

        // 2. Creiamo il visualizzatore adattivo
        com.toystory.client.view.PannelloImmagineAdattiva visualizzatoreStanza = 
                new com.toystory.client.view.PannelloImmagineAdattiva();

        // 3. Lo impostiamo al centro del pannello principale
        pnlRappresentazioneStanza.setLayout(new java.awt.BorderLayout());
        pnlRappresentazioneStanza.add(visualizzatoreStanza, java.awt.BorderLayout.CENTER);

        // 4. Diciamo al visualizzatore di inoltrare i clic a "pnlRappresentazioneStanzaMouseClicked"
        visualizzatoreStanza.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pnlRappresentazioneStanzaMouseClicked(evt);
            }
        });

        // 5. Aggiorniamo la grafica del pannello per renderlo visibile subito
        pnlRappresentazioneStanza.revalidate();
        pnlRappresentazioneStanza.repaint();
    }
    
    // Metodo per avviare una nuova partita
    private void avviaNuovaPartita() {
        String gameId = this.clientRete.connectAndHandshake(true, "");
        if (gameId.equals("ERROR")) {
            javax.swing.JOptionPane.showMessageDialog(this, "Errore server.", "Errore", javax.swing.JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        } else {
            SaveManager.salvaPartita(gameId); // <--- Salvataggio ID
            javax.swing.JOptionPane.showMessageDialog(this, "Partita creata! ID: " + gameId);
            this.setTitle("Toy Story - Partita ID: " + gameId);
        }
    }

    // Metodo per unire a una partita (nuova o ripresa)
    private void eseguiJoin(String gameId) {
        String risultato = this.clientRete.connectAndHandshake(false, gameId.trim().toUpperCase());
        if (risultato.equals("SUCCESS")) {
            SaveManager.salvaPartita(gameId.trim().toUpperCase()); // <--- Salvataggio ID
            this.setTitle("Toy Story - Partita ID: " + gameId.trim().toUpperCase());
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "Partita non trovata.", "Errore", javax.swing.JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }
    
    // Questo metodo gestisce la richiesta di unirti a una partita digitando l'ID
    private void uniscitiPartita(String idPredefinito) {
        String gameId = idPredefinito;
        
        // Se non abbiamo un ID passato (es. cliccando il bottone generico), lo chiediamo
        if (gameId == null) {
            gameId = javax.swing.JOptionPane.showInputDialog(this, 
                    "Inserisci il Game ID della partita:", 
                    "Unisciti", 
                    javax.swing.JOptionPane.PLAIN_MESSAGE);
        }
        
        if (gameId != null && !gameId.trim().isEmpty()) {
            eseguiJoin(gameId.trim().toUpperCase());
        } else {
            System.exit(0);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jToggleButton1 = new javax.swing.JToggleButton();
        pnlSuperiore = new javax.swing.JPanel();
        btnMenu = new javax.swing.JButton();
        lblNomeStanza = new javax.swing.JLabel();
        pnlRappresentazioneStanza = new com.toystory.client.view.PannelloImmagineAdattiva();
        pnlInferiore = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtAreaStoria = new javax.swing.JTextArea();
        pnlPulsantiera = new javax.swing.JPanel();
        pnlVerbi = new javax.swing.JPanel();
        btnDai = new javax.swing.JButton();
        btnPrendi = new javax.swing.JButton();
        btnUsa = new javax.swing.JButton();
        btnApri = new javax.swing.JButton();
        btnGuarda = new javax.swing.JButton();
        btnSpingi = new javax.swing.JButton();
        btnChiudi = new javax.swing.JButton();
        btnParla = new javax.swing.JButton();
        btnTira = new javax.swing.JButton();
        pnlTasche = new javax.swing.JPanel();
        btnSlotInventario1 = new javax.swing.JButton();
        btnSlotInventario2 = new javax.swing.JButton();
        lblIconaAbilita = new javax.swing.JLabel();
        pnlPersonaggio = new javax.swing.JPanel();
        lblGiocatoreCorrente = new javax.swing.JLabel();
        labelAvatar = new javax.swing.JLabel();
        cambiaIconaAvatar = new javax.swing.JPanel();
        btnSelezionaWoody = new javax.swing.JToggleButton();
        btnSelezionaBuzz = new javax.swing.JToggleButton();
        btnSelezionaJessie = new javax.swing.JToggleButton();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jTable1);

        jToggleButton1.setText("jToggleButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pnlSuperiore.setMinimumSize(new java.awt.Dimension(162, 13));
        pnlSuperiore.setPreferredSize(new java.awt.Dimension(800, 27));
        pnlSuperiore.setLayout(new java.awt.BorderLayout());

        btnMenu.setText("MENU");
        btnMenu.addActionListener(this::btnMenuActionPerformed);
        pnlSuperiore.add(btnMenu, java.awt.BorderLayout.LINE_START);

        lblNomeStanza.setBackground(new java.awt.Color(51, 51, 0));
        lblNomeStanza.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNomeStanza.setText("NOME STANZA ");
        lblNomeStanza.setPreferredSize(new java.awt.Dimension(86, 10));
        pnlSuperiore.add(lblNomeStanza, java.awt.BorderLayout.CENTER);

        getContentPane().add(pnlSuperiore, java.awt.BorderLayout.PAGE_START);

        pnlRappresentazioneStanza.setBackground(new java.awt.Color(153, 153, 153));
        pnlRappresentazioneStanza.setPreferredSize(new java.awt.Dimension(762, 233));
        pnlRappresentazioneStanza.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pnlRappresentazioneStanzaMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnlRappresentazioneStanzaLayout = new javax.swing.GroupLayout(pnlRappresentazioneStanza);
        pnlRappresentazioneStanza.setLayout(pnlRappresentazioneStanzaLayout);
        pnlRappresentazioneStanzaLayout.setHorizontalGroup(
            pnlRappresentazioneStanzaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 762, Short.MAX_VALUE)
        );
        pnlRappresentazioneStanzaLayout.setVerticalGroup(
            pnlRappresentazioneStanzaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 284, Short.MAX_VALUE)
        );

        getContentPane().add(pnlRappresentazioneStanza, java.awt.BorderLayout.CENTER);

        pnlInferiore.setPreferredSize(new java.awt.Dimension(800, 166));
        pnlInferiore.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(234, 55));

        txtAreaStoria.setEditable(false);
        txtAreaStoria.setBackground(new java.awt.Color(51, 153, 255));
        txtAreaStoria.setColumns(20);
        txtAreaStoria.setLineWrap(true);
        txtAreaStoria.setRows(5);
        txtAreaStoria.setText("TESTO\n");
        txtAreaStoria.setWrapStyleWord(true);
        txtAreaStoria.setPreferredSize(new java.awt.Dimension(232, 70));
        jScrollPane1.setViewportView(txtAreaStoria);

        pnlInferiore.add(jScrollPane1, java.awt.BorderLayout.NORTH);

        pnlPulsantiera.setLayout(new java.awt.GridLayout(1, 3));

        pnlVerbi.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        pnlVerbi.setLayout(new java.awt.GridLayout(3, 3, 5, 5));

        btnDai.setText("Dai");
        btnDai.addActionListener(this::btnDaiActionPerformed);
        pnlVerbi.add(btnDai);

        btnPrendi.setText("Prendi");
        pnlVerbi.add(btnPrendi);

        btnUsa.setText("Usa");
        pnlVerbi.add(btnUsa);

        btnApri.setText("Apri");
        btnApri.addActionListener(this::btnApriActionPerformed);
        pnlVerbi.add(btnApri);

        btnGuarda.setText("Guarda");
        btnGuarda.addActionListener(this::btnGuardaActionPerformed);
        pnlVerbi.add(btnGuarda);

        btnSpingi.setText("Chiudi");
        btnSpingi.addActionListener(this::btnSpingiActionPerformed);
        pnlVerbi.add(btnSpingi);

        btnChiudi.setText("Chiudi");
        pnlVerbi.add(btnChiudi);

        btnParla.setText("Parla");
        pnlVerbi.add(btnParla);

        btnTira.setText("Tira");
        pnlVerbi.add(btnTira);

        pnlPulsantiera.add(pnlVerbi);

        pnlTasche.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 50, 1, 50));
        pnlTasche.setMaximumSize(new java.awt.Dimension(32767, 30000));
        pnlTasche.setLayout(new java.awt.GridLayout(2, 1, 0, 10));

        btnSlotInventario1.setText("[Vuoto]");
        btnSlotInventario1.addActionListener(this::btnSlotInventario1ActionPerformed);
        pnlTasche.add(btnSlotInventario1);

        btnSlotInventario2.setText("[Vuoto]");
        pnlTasche.add(btnSlotInventario2);

        lblIconaAbilita.setText("abilita");
        pnlTasche.add(lblIconaAbilita);

        pnlPulsantiera.add(pnlTasche);

        pnlPersonaggio.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        pnlPersonaggio.setLayout(new java.awt.BorderLayout());

        lblGiocatoreCorrente.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblGiocatoreCorrente.setText("NOME PERSONAGGIO ATTIVO");
        pnlPersonaggio.add(lblGiocatoreCorrente, java.awt.BorderLayout.SOUTH);
        pnlPersonaggio.add(labelAvatar, java.awt.BorderLayout.CENTER);

        btnSelezionaWoody.setText("jToggleButton2");

        btnSelezionaBuzz.setText("jToggleButton2");

        btnSelezionaJessie.setText("jToggleButton2");

        javax.swing.GroupLayout cambiaIconaAvatarLayout = new javax.swing.GroupLayout(cambiaIconaAvatar);
        cambiaIconaAvatar.setLayout(cambiaIconaAvatarLayout);
        cambiaIconaAvatarLayout.setHorizontalGroup(
            cambiaIconaAvatarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cambiaIconaAvatarLayout.createSequentialGroup()
                .addContainerGap(17, Short.MAX_VALUE)
                .addComponent(btnSelezionaJessie, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSelezionaBuzz, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSelezionaWoody, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        cambiaIconaAvatarLayout.setVerticalGroup(
            cambiaIconaAvatarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cambiaIconaAvatarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cambiaIconaAvatarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSelezionaJessie, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSelezionaBuzz, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSelezionaWoody, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(63, Short.MAX_VALUE))
        );

        pnlPersonaggio.add(cambiaIconaAvatar, java.awt.BorderLayout.PAGE_START);

        pnlPulsantiera.add(pnlPersonaggio);

        pnlInferiore.add(pnlPulsantiera, java.awt.BorderLayout.CENTER);

        getContentPane().add(pnlInferiore, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnMenuActionPerformed

    private void btnDaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDaiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDaiActionPerformed

    private void pnlRappresentazioneStanzaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlRappresentazioneStanzaMouseClicked
                                                   
         // 1. Recupera le coordinate X e Y di dove ha cliccato il mouse rispetto al pannello
        int mouseX = evt.getX();
        int mouseY = evt.getY();

        System.out.println("X: " + mouseX + " Y: " + mouseY);

        // Recuperiamo le dimensioni correnti del pannello grafico

        int larghezzaPannello = pnlRappresentazioneStanza.getWidth();
        int altezzaPannello = pnlRappresentazioneStanza.getHeight();

        // Chiediamo al gestore delle mappe se c'è qualcosa sotto questi pixel
        String targetEffettivo = mappaScenario.cercaTarget(mouseX, mouseY, larghezzaPannello, altezzaPannello);

         // Se ha trovato qualcosa, lo manda alla rete, altrimenti non fa nulla!
        if (targetEffettivo != null) {
            this.clientRete.sendCommand(this.azioneSelezionata, targetEffettivo);
        } else {
            txtAreaStoria.append("[Sistema]: Lì non c'è nulla di interessante.\n");
        }
    }//GEN-LAST:event_pnlRappresentazioneStanzaMouseClicked

    private void btnGuardaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardaActionPerformed
        // TODO add your handling code here:                                   
        this.azioneSelezionata = "GUARDA";
        txtAreaStoria.append("[Sistema]: Hai selezionato l'azione GUARDA. Ora clicca su un oggetto nello scenario.\n");
    }//GEN-LAST:event_btnGuardaActionPerformed

    private void btnApriActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApriActionPerformed
        // TODO add your handling code here:
        this.azioneSelezionata = "APRI";
        txtAreaStoria.append("[Sistema]: Hai selezionato l'azione APRI. Ora clicca su un oggetto nello scenario.\n");
    }//GEN-LAST:event_btnApriActionPerformed

    private void btnSpingiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSpingiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSpingiActionPerformed

    private void btnSlotInventario1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSlotInventario1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSlotInventario1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new GameWindow().setVisible(true));
    }
    
    // =============================================================================
    // 🔌 METODI PONTE COSTRUITI SUI TUOI COMPONENTI REALI (Richiesti dal GUIHandler)
    // =============================================================================

    /**
     * Scrive un testo nell'area principale dei dialoghi.
     * @param testo
     */
    public void scriviNelLog(String testo) {
        txtAreaStoria.append(testo + "\n");
        txtAreaStoria.setCaretPosition(txtAreaStoria.getDocument().getLength());
    }

    /**
     * Aggiorna il titolo della stanza in alto.
     * @param nomeStanza
     */
    public void aggiornaNomeStanza(String nomeStanza) {
        lblNomeStanza.setText(nomeStanza.toUpperCase());
    }

    /**
     * Cambia la foto dell'avatar a destra.
     * @param percorsoImmagine
     */
    /*public void cambiaIconaAvatar(String percorsoImmagine) {
        try {
            lblAvatarPersonaggio.setIcon(new javax.swing.ImageIcon(getClass().getResource(percorsoImmagine)));
            lblAvatarPersonaggio.setText(""); 
        } catch (Exception e) {
            logger.warning("Impossibile caricare l'avatar: " + percorsoImmagine);
        }
    }

    /**
     * Notifica lo spostamento in una nuova stanza.
     * @param idStanza
     */
    public void aggiornaSfondoScenario(String idStanza) {
        scriviNelLog("[Cambio Scenario]: Ti sposti nella stanza ID " + idStanza);
    }

    /**
     * Gestisce l'aggiornamento di slot o abilità.
     * @param nomeAbilita
     * @param icona
     */
    public void aggiornaSlotAbilita(String nomeAbilita, String icona) {
        scriviNelLog("[Notifica Gioco]: " + nomeAbilita);
    }
    
    private void applicaStileToyStory(javax.swing.JButton bottone, String verbo) {
        // Genera l'immagine standard e quella per quando il mouse ci passa sopra (Rollover/Hover)
        bottone.setIcon(PixelButtonGenerator.createToyStoryButton(verbo, false));
        bottone.setRolloverIcon(PixelButtonGenerator.createToyStoryButton(verbo, true));
        bottone.setPressedIcon(PixelButtonGenerator.createToyStoryButton(verbo, true));

           // Rimuove la vecchia grafica grigia standard delle finestre
           bottone.setBorderPainted(false);
           bottone.setContentAreaFilled(false);
           bottone.setFocusPainted(false);
           // Rimuove il vecchio testo testuale, visto che ora la parola è disegnata dentro l'immagine
           bottone.setText(""); 
    }
    
    // Questo serve al Server per cambiare l'immagine (NON ELIMINARLO)
    public void cambiaIconaAvatar(String imagePath) {
        try {
            java.net.URL imgURL = getClass().getResource(imagePath);
            if (imgURL != null) {
                labelAvatar.setIcon(new javax.swing.ImageIcon(imgURL)); 
            } else {
                System.err.println("[GUI] Immagine avatar non trovata: " + imagePath);
            }
        } catch (Exception e) {
            System.err.println("[GUI] Errore caricamento avatar: " + e.getMessage());
        }
    }
    
    /**
     * Aggiorna l'icona dell'avatar nella GUI.
     * @param imagePath Il percorso dell'immagine inviato dal Server (es. "/images/avatars/buzz.png")
     */
    private void impostaIconaSuBottone(javax.swing.AbstractButton bottone, String nomeFile) {
        // Cerchiamo in tutti i modi possibili
        java.net.URL imgURL = null;

        // Tentativo 1: Cercare dal classloader (spesso risolve i problemi di pacchetto)
        imgURL = Thread.currentThread().getContextClassLoader().getResource(nomeFile);

        // Tentativo 2: Cercare nella cartella resources come root
        if (imgURL == null) {
            imgURL = getClass().getResource("/resources/" + nomeFile);
        }

        // Tentativo 3: Cercare nel percorso esatto che vedevi prima
        if (imgURL == null) {
            imgURL = getClass().getResource("/com/toystory/client/resources/" + nomeFile);
        }

        System.out.println("DEBUG: Cercando " + nomeFile + ". Trovato: " + imgURL);

        if (imgURL != null) {
            bottone.setIcon(new javax.swing.ImageIcon(imgURL));
            bottone.setText("");
            bottone.setContentAreaFilled(false);
            bottone.setBorderPainted(false);
        } else {
            System.err.println("[GUI] ERRORE GRAVE: Immagine " + nomeFile + " introvabile.");
        }
    }


    public MappaScenario getMappaScenario() { return mappaScenario; }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnApri;
    private javax.swing.JButton btnChiudi;
    private javax.swing.JButton btnDai;
    private javax.swing.JButton btnGuarda;
    private javax.swing.JButton btnMenu;
    private javax.swing.JButton btnParla;
    private javax.swing.JButton btnPrendi;
    private javax.swing.JToggleButton btnSelezionaBuzz;
    private javax.swing.JToggleButton btnSelezionaJessie;
    private javax.swing.JToggleButton btnSelezionaWoody;
    private javax.swing.JButton btnSlotInventario1;
    private javax.swing.JButton btnSlotInventario2;
    private javax.swing.JButton btnSpingi;
    private javax.swing.JButton btnTira;
    private javax.swing.JButton btnUsa;
    private javax.swing.JPanel cambiaIconaAvatar;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JLabel labelAvatar;
    private javax.swing.JLabel lblGiocatoreCorrente;
    private javax.swing.JLabel lblIconaAbilita;
    private javax.swing.JLabel lblNomeStanza;
    private javax.swing.JPanel pnlInferiore;
    private javax.swing.JPanel pnlPersonaggio;
    private javax.swing.JPanel pnlPulsantiera;
    private javax.swing.JPanel pnlRappresentazioneStanza;
    private javax.swing.JPanel pnlSuperiore;
    private javax.swing.JPanel pnlTasche;
    private javax.swing.JPanel pnlVerbi;
    private javax.swing.JTextArea txtAreaStoria;
    // End of variables declaration//GEN-END:variables
}
