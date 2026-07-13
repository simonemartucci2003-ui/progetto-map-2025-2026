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
        txtAreaStoria.setPreferredSize(null);
        impostaIconaSuBottone(btnSelezionaBuzz, "/icone_buzz.png");
        impostaIconaSuBottone(btnSelezionaWoody, "/icone_woody.png");
        impostaIconaSuBottone(btnSelezionaJessie, "/icone_jessie.png");
         
        btnSelezionaWoody.setSelected(true); // Seleziona Woody fisicamente
        aggiornaBordiPersonaggi();
        
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
        applicaStileToyStory(btnVai, "VAI");
        
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
        buttonGroup1 = new javax.swing.ButtonGroup();
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
        btnVai = new javax.swing.JButton();
        pnlTasche = new javax.swing.JPanel();
        btnSlotInventario1 = new javax.swing.JButton();
        btnSlotInventario2 = new javax.swing.JButton();
        lblIconaAbilita = new javax.swing.JLabel();
        pnlPersonaggio = new javax.swing.JPanel();
        labelAvatar = new javax.swing.JLabel();
        cambiaIconaAvatar = new javax.swing.JPanel();
        Woody = new javax.swing.JPanel();
        btnSelezionaWoody = new javax.swing.JToggleButton();
        Jessie = new javax.swing.JPanel();
        btnSelezionaJessie = new javax.swing.JToggleButton();
        Buzz = new javax.swing.JPanel();
        btnSelezionaBuzz = new javax.swing.JToggleButton();
        lblGiocatoreCorrente = new javax.swing.JLabel();

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
            .addGap(0, 1116, Short.MAX_VALUE)
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
        btnPrendi.addActionListener(this::btnPrendiActionPerformed);
        pnlVerbi.add(btnPrendi);

        btnUsa.setText("Usa");
        btnUsa.addActionListener(this::btnUsaActionPerformed);
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
        btnParla.addActionListener(this::btnParlaActionPerformed);
        pnlVerbi.add(btnParla);

        btnVai.setText("Vai");
        btnVai.addActionListener(this::btnVaiActionPerformed);
        pnlVerbi.add(btnVai);

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

        labelAvatar.setEnabled(false);
        pnlPersonaggio.add(labelAvatar, java.awt.BorderLayout.CENTER);

        cambiaIconaAvatar.setPreferredSize(new java.awt.Dimension(208, 80));
        cambiaIconaAvatar.setLayout(new java.awt.GridLayout(1, 3));

        Woody.setOpaque(false);

        buttonGroup1.add(btnSelezionaWoody);
        btnSelezionaWoody.setText("jToggleButton2");
        btnSelezionaWoody.setPreferredSize(new java.awt.Dimension(75, 75));
        btnSelezionaWoody.addActionListener(this::btnSelezionaWoodyActionPerformed);
        Woody.add(btnSelezionaWoody);

        cambiaIconaAvatar.add(Woody);

        Jessie.setOpaque(false);

        buttonGroup1.add(btnSelezionaJessie);
        btnSelezionaJessie.setText("jToggleButton2");
        btnSelezionaJessie.setPreferredSize(new java.awt.Dimension(75, 75));
        btnSelezionaJessie.addActionListener(this::btnSelezionaJessieActionPerformed);
        Jessie.add(btnSelezionaJessie);

        cambiaIconaAvatar.add(Jessie);

        Buzz.setOpaque(false);

        buttonGroup1.add(btnSelezionaBuzz);
        btnSelezionaBuzz.setText("jToggleButton2");
        btnSelezionaBuzz.setPreferredSize(new java.awt.Dimension(75, 75));
        btnSelezionaBuzz.addActionListener(this::btnSelezionaBuzzActionPerformed);
        Buzz.add(btnSelezionaBuzz);

        cambiaIconaAvatar.add(Buzz);

        pnlPersonaggio.add(cambiaIconaAvatar, java.awt.BorderLayout.PAGE_START);

        lblGiocatoreCorrente.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblGiocatoreCorrente.setText("NOME PERSONAGGIO ATTIVO");
        pnlPersonaggio.add(lblGiocatoreCorrente, java.awt.BorderLayout.SOUTH);

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

    private void btnSelezionaWoodyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelezionaWoodyActionPerformed
        this.clientRete.sendCommand("CHIAMA", "Woody");
        aggiornaBordiPersonaggi();
    }//GEN-LAST:event_btnSelezionaWoodyActionPerformed

    private void btnSelezionaBuzzActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelezionaBuzzActionPerformed
       this.clientRete.sendCommand("CHIAMA", "Buzz");
       aggiornaBordiPersonaggi();
    }//GEN-LAST:event_btnSelezionaBuzzActionPerformed

    private void btnSelezionaJessieActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelezionaJessieActionPerformed
        this.clientRete.sendCommand("CHIAMA", "Jessie");
        aggiornaBordiPersonaggi();
    }//GEN-LAST:event_btnSelezionaJessieActionPerformed

    private void btnPrendiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrendiActionPerformed
       this.azioneSelezionata = "PRENDI";
        txtAreaStoria.append("[Sistema]: Hai selezionato l'azione PRENDI. Ora clicca su un oggetto nello scenario.\n");
    }//GEN-LAST:event_btnPrendiActionPerformed

    private void btnUsaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUsaActionPerformed
       this.azioneSelezionata = "USA";
       txtAreaStoria.append("[Sistema]: Hai selezionato USA. Clicca sull'oggetto con cui interagire.\n");
    }//GEN-LAST:event_btnUsaActionPerformed

    private void btnVaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVaiActionPerformed
       this.azioneSelezionata = "VAI";
       txtAreaStoria.append("[Sistema]: Hai selezionato VAI. Clicca dove vuoi andare.\n");
    }//GEN-LAST:event_btnVaiActionPerformed

    private void btnParlaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnParlaActionPerformed
        this.azioneSelezionata = "PARLA";
        txtAreaStoria.append("[Sistema]: Hai selezionato PARLA. Clicca con chi vuoi parlare.\n");
    }//GEN-LAST:event_btnParlaActionPerformed

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
     * @param idStanza
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
        
        String percorsoImmagine = "";
        
        // Mappatura dinamica degli ID alle immagini fisiche
        if (idStanza.equals("CORRIDOIO_PRIMO_PIANO")) {
            percorsoImmagine = "/CorridoioRoom2.jpg"; 
        } else if (idStanza.equals("CAMERA_DI_ANDY")) {
            percorsoImmagine = "/AndyRoom1.jpg";
        } else if (idStanza.equals("CAMERA_DI_MOLLY")) {
            percorsoImmagine = "/MollyRoom3.png"; // (Esempio)
        } else if (idStanza.equals("CORRIDOIO_PIANO_TERRA")) {
            percorsoImmagine = "/CorridoioRoom4.png"; // (Esempio per le scale)
        } else if (idStanza.equals("CUCINA")) {
            percorsoImmagine = "/CucinaRoom6.png"; 
        } else if (idStanza.equals("GIARDINO")) {
            percorsoImmagine = "/StradaRoom5.png"; 
        }else if (idStanza.equals("INGRESSO_FOGNATURE")) {
            percorsoImmagine = "/IngressoFognaRoom7.png"; 
        }else if (idStanza.equals("FOGNE_PRIMA_STANZA")) {
            percorsoImmagine = "/FognaRoom8.png"; 
        }else if (idStanza.equals("STANZA_BUIA")) {
            percorsoImmagine = "/FognaRoom11.png"; 
        }else if (idStanza.equals("CASA_DEL_TOPO")) {
            percorsoImmagine = "/StanzaTopoRoom9.png"; 
        }else if (idStanza.equals("STANZA_DELLA_LEVA")) {
            percorsoImmagine = "/FognaRoom10.png"; 
        }else if (idStanza.equals("FOGNE_SECONDA_STANZA")) {
            percorsoImmagine = "/FognaRoom12.png"; 
        }else if (idStanza.equals("STANZA_CON_ACQUA")) {
            percorsoImmagine = "/FognaRoom13.1.png"; 
        }else if (idStanza.equals("STANZA_SENZA_ACQUA")) {
            percorsoImmagine = "/FognaRoom13.2.png"; 
        //}else if (idStanza.equals("BOSS_FINALE")) {
           // percorsoImmagine = "/"; 
        }
        
        
        
        if (!percorsoImmagine.isEmpty()) {
            ((com.toystory.client.view.PannelloImmagineAdattiva) pnlRappresentazioneStanza).cambiaImmagine(percorsoImmagine);
        }
    }

    /**
     * Gestisce l'aggiornamento dello slot abilità.
     * @param nomeAbilita Il nome testuale (es. "Lazo", "Laser" o "Nessuna")
     * @param icona Il percorso dell'immagine inviato dal server
     */
    public void aggiornaSlotAbilita(String nomeAbilita, String icona) {
        scriviNelLog("[Notifica Gioco]: Abilità attiva - " + nomeAbilita);
        
        // Se il server ci dice che c'è un'icona valida (non è "vuoto")
        if (icona != null && !icona.isEmpty() && !icona.equals("vuoto")) {
            try {
                java.net.URL imgURL = getClass().getResource(icona);
                if (imgURL != null) {
                    lblIconaAbilita.setIcon(new javax.swing.ImageIcon(imgURL));
                    lblIconaAbilita.setText(""); // Nasconde il testo per mostrare solo l'immagine
                } else {
                    System.err.println("[GUI] Immagine abilità non trovata: " + icona);
                    lblIconaAbilita.setIcon(null);
                    lblIconaAbilita.setText(nomeAbilita); // Fallback: mostra il testo
                }
            } catch (Exception e) {
                System.err.println("[GUI] Errore caricamento icona abilità: " + e.getMessage());
            }
        } else {
            // Se il personaggio non ha abilità (es. Woody all'inizio del gioco)
            lblIconaAbilita.setIcon(null); // Rimuove eventuali vecchie immagini
            lblIconaAbilita.setText("[Nessuna Abilità]");
        }
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
        String path = nomeFile.startsWith("/") ? nomeFile : "/" + nomeFile;
        java.net.URL imgURL = getClass().getResource(path);
        
        if (imgURL != null) {
            // 1. Carichiamo l'icona originale
            javax.swing.ImageIcon iconaOriginale = new javax.swing.ImageIcon(imgURL);
            
            // 2. Definiamo le dimensioni desiderate (es. 50x50 pixel, o quelle del tuo bottone)
            int larghezza = 75; 
            int altezza = 75;
            
            // 3. Creiamo una versione scalata dell'immagine
            java.awt.Image imgScalata = iconaOriginale.getImage().getScaledInstance(larghezza, altezza, java.awt.Image.SCALE_SMOOTH);
            
            // 4. Applichiamo l'immagine ridimensionata al bottone
            bottone.setIcon(new javax.swing.ImageIcon(imgScalata));
 
            //Forza il bottone ad avere esattamente l'ingombro dell'immagine
            bottone.setPreferredSize(new java.awt.Dimension(larghezza, altezza));
            
            // Pulizia grafica
            bottone.setText(""); 
            bottone.setContentAreaFilled(false);
            bottone.setBorderPainted(true);
            
        } else {
            System.err.println("[GUI] ERRORE GRAVE: Immagine " + nomeFile + " introvabile.");
        }
    }
    private void aggiornaBordiPersonaggi() {
        // 1. Aggiorna il testo della label in base al bottone attualmente premuto
        if (btnSelezionaWoody.isSelected()) {
            lblGiocatoreCorrente.setText("WOODY");
        } else if (btnSelezionaBuzz.isSelected()) {
            lblGiocatoreCorrente.setText("BUZZ LIGHTYEAR");
        } else if (btnSelezionaJessie.isSelected()) {
            lblGiocatoreCorrente.setText("JESSIE");
        }
        
        // Bordo Verde se selezionato, Rosso se non selezionato (spessore 3 pixel)
        btnSelezionaWoody.setBorder(javax.swing.BorderFactory.createLineBorder(
            btnSelezionaWoody.isSelected() ? java.awt.Color.GREEN : java.awt.Color.RED, 3));
            
        btnSelezionaBuzz.setBorder(javax.swing.BorderFactory.createLineBorder(
            btnSelezionaBuzz.isSelected() ? java.awt.Color.GREEN : java.awt.Color.RED, 3));
            
        btnSelezionaJessie.setBorder(javax.swing.BorderFactory.createLineBorder(
            btnSelezionaJessie.isSelected() ? java.awt.Color.GREEN : java.awt.Color.RED, 3));
    }

    public void aggiungiAllInventario(String nomeOggetto) {
        // Formattiamo il nome per renderlo carino (es. "chiave" -> "CHIAVE")
        String nomePulito = nomeOggetto.toUpperCase();

        // Controlliamo se il primo slot è libero
        if (btnSlotInventario1.getText().equals("[Vuoto]")) {
            btnSlotInventario1.setText(nomePulito);
        } 
        // Se il primo è pieno, proviamo col secondo
        else if (btnSlotInventario2.getText().equals("[Vuoto]")) {
            btnSlotInventario2.setText(nomePulito);
        } 
        // Se sono entrambi pieni (non dovrebbe succedere se il server fa i controlli, ma per sicurezza)
        else {
            scriviNelLog("[Sistema - GUI]: Le tue tasche sono piene!");
        }
    }
    public void aggiungiAllInventario(String nomeOggetto, String nomeFile) {
        // Carichiamo l'immagine dalla cartella resources
        java.net.URL imgURL = getClass().getResource("/" + nomeFile);
        javax.swing.ImageIcon icona = null;
    
        if (imgURL != null) {
            icona = new javax.swing.ImageIcon(imgURL);
            // Scaliamo l'immagine per farla stare nel bottone
            java.awt.Image imgScalata = icona.getImage().getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH);
            icona = new javax.swing.ImageIcon(imgScalata);
        }

        if (btnSlotInventario1.getText().equals("[Vuoto]") || btnSlotInventario1.getIcon() == null) {
            btnSlotInventario1.setIcon(icona);
            btnSlotInventario1.setText(""); // Togliamo il testo
        } else if (btnSlotInventario2.getText().equals("[Vuoto]") || btnSlotInventario2.getIcon() == null) {
            btnSlotInventario2.setIcon(icona);
            btnSlotInventario2.setText("");
        }
    }
    
    public void svuotaInventario() {
        // Togliamo le icone e rimettiamo la scritta [Vuoto]
        btnSlotInventario1.setIcon(null);
        btnSlotInventario1.setText("[Vuoto]");
        
        btnSlotInventario2.setIcon(null);
        btnSlotInventario2.setText("[Vuoto]");
    }
    
    // Aggiungi questo metodo in GameWindow.java
    public void stampaTestoConPausa(String testoCompleto) {
        // 1. Traduciamo il nostro codice di rete <BR> in un vero e proprio "a capo" per la grafica
        testoCompleto = testoCompleto.replace("<BR>", "\n");

        if (!testoCompleto.contains("<PAUSA>")) {
            txtAreaStoria.append(testoCompleto + "\n");
            txtAreaStoria.setCaretPosition(txtAreaStoria.getDocument().getLength());
            return;
        }

        // Dividiamo il testo usando il nostro segnalibro temporale
        String[] battute = testoCompleto.split("<PAUSA>");
        
        // Stampiamo subito la prima frase senza aspettare
        txtAreaStoria.append(battute[0] + "\n");
        txtAreaStoria.setCaretPosition(txtAreaStoria.getDocument().getLength());
        
        // Timer per stampare le restanti frasi ogni 1.5 secondi
        javax.swing.Timer timer = new javax.swing.Timer(1000, new java.awt.event.ActionListener() {
            // PARTIAMO DA 1 (perché la frase 0 l'abbiamo appena stampata!)
            int indice = 1; 

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (indice < battute.length) {
                    txtAreaStoria.append(battute[indice] + "\n");
                    txtAreaStoria.setCaretPosition(txtAreaStoria.getDocument().getLength());
                    indice++;
                } else {
                    ((javax.swing.Timer) e.getSource()).stop();
                }
            }
        });

        timer.setInitialDelay(1500);
        timer.start();
    }
    
    public MappaScenario getMappaScenario() { return mappaScenario; }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Buzz;
    private javax.swing.JPanel Jessie;
    private javax.swing.JPanel Woody;
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
    private javax.swing.JButton btnUsa;
    private javax.swing.JButton btnVai;
    private javax.swing.ButtonGroup buttonGroup1;
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
