package com.toystory.server.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestore centralizzato di tutti i testi e i dialoghi narrativi del gioco.
 * <p>
 * I testi non sono scritti all'interno del codice sorgente, ma vengono caricati
 * all'avvio da un file di testo esterno ({@code config/dialoghi.txt}). Questo approccio
 * facilita la traduzione e la modifica dei testi senza dover ricompilare il progetto.
 * </p>
 * <p>
 * La lettura avviene tramite {@link BufferedReader} e {@link FileReader} 
 * (character stream, line-oriented I/O).
 * </p>
 */
public class Dialoghi {

    /** Percorso relativo del file di configurazione contenente i testi del gioco. */
    private static final String PATH_DIALOGHI = "config/dialoghi.txt";

    /** Mappa in cui vengono memorizzate le coppie Chiave-Valore lette dal file. */
    private static final Map<String, String> TESTI = new HashMap<>();

    // Blocco statico: eseguito una sola volta, al primo utilizzo della classe
    static {
        caricaDialoghi();
    }

    /**
     * Carica e analizza il file esterno riga per riga, popolando la mappa dei testi.
     * <p>
     * Ogni riga valida deve rispettare il formato {@code CHIAVE=testo}. 
     * Eventuali errori di sintassi nella riga vengono segnalati 
     * ma non bloccano il caricamento dei restanti testi.
     * </p>
     */
    private static void caricaDialoghi() {
        File file = new File(PATH_DIALOGHI);

        // Uso della classe File per verificare l'esistenza PRIMA di aprire lo stream,
        // così diamo un errore chiaro invece di una FileNotFoundException generica
        if (!file.exists()) {
            System.err.println("[Dialoghi] ERRORE CRITICO: file non trovato: " + file.getAbsolutePath());
            return;
        }

        BufferedReader inputStream = null;

        try {
            // FileReader con Charset esplicito (UTF-8) per gestire correttamente gli accenti
            inputStream = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8));

            String riga;
            int numeroRiga = 0;

            while ((riga = inputStream.readLine()) != null) {
                numeroRiga++;
                riga = riga.trim();

                // Ignoriamo righe vuote e commenti
                if (riga.isEmpty() || riga.startsWith("#")) {
                    continue;
                }

                // Separiamo chiave e valore sul PRIMO '=' incontrato
                // (il testo può contenere altri '=' più avanti, quindi limit=2)
                String[] parti = riga.split("=", 2);
                if (parti.length != 2) {
                    System.err.println("[Dialoghi] Riga " + numeroRiga + " malformata, ignorata: " + riga);
                    continue;
                }

                TESTI.put(parti[0].trim(), parti[1]);
            }

            System.out.println("[Dialoghi] Caricati " + TESTI.size() + " testi da " + PATH_DIALOGHI);

        } catch (IOException e) {
            System.err.println("[Dialoghi] ERRORE durante la lettura del file: " + e.getMessage());
        } finally {
            // Chiudiamo sempre lo stream, anche in caso di eccezione,
            // per non lasciare risorse di sistema aperte
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    System.err.println("[Dialoghi] Errore nella chiusura del file: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Recupera un testo dalla mappa utilizzando la chiave.
     * <p>
     * Se la chiave richiesta non è presente nel file (es. errore di battitura o file mancante), 
     * restituisce un placeholder testuale invece di generare una NullPointerException, 
     * garantendo così che la partita non crashi.
     * </p>
     *
     * @param chiave L'identificativo univoco del testo da recuperare.
     * @return Il testo associato alla chiave, oppure un messaggio di "TESTO MANCANTE".
     */
    private static String get(String chiave) {
        String valore = TESTI.get(chiave);
        if (valore == null) {
            System.err.println("[Dialoghi] Chiave mancante nel file dialoghi.txt: " + chiave);
            return "[TESTO MANCANTE: " + chiave + "]";
        }
        return valore;
    }


    public static String getDialogoBauleAperto() { return get("DialogoBauleAperto"); }
    public static String getDescrizoneBauleChiuso() { return get("DescrizoneBauleChiuso"); }
    public static String getDescrizoneBauleAperto() { return get("DescrizoneBauleAperto"); }
    public static String getDescrizoneLibreria() { return get("DescrizoneLibreria"); }
    public static String getDescrizoneLettoBauleChiuso() { return get("DescrizoneLettoBauleChiuso"); }
    public static String getDescrizoneLettoBauleAperto() { return get("DescrizoneLettoBauleAperto"); }
    public static String getDescrizoneLettoBauleApertoLazoSbloccato() { return get("DescrizoneLettoBauleApertoLazoSbloccato"); }
    public static String getJessieSottoAlLetto() { return get("JessieSottoAlLetto"); }
    public static String getWoodySottoAlLetto() { return get("WoodySottoAlLetto"); }
    public static String getBuzzSottoAlLetto() { return get("BuzzSottoAlLetto"); }
    public static String getManigliaTroppoInAlto() { return get("ManigliaTroppoInAlto"); }
    public static String getPortaNonWoody() { return get("PortaNonWoody"); }
    public static String getPortaConWoody() { return get("PortaConWoody"); }
    public static String getDialogoBoPeep() { return get("DialogoBoPeep"); }
    public static String getBuzzSottoAlLettoMolly() { return get("BuzzSottoAlLettoMolly"); }
    public static String getDescrizoneLettoMollyBloccato() { return get("DescrizoneLettoMollyBloccato"); }
    public static String getDescrizoneLettoMollySbloccato() { return get("DescrizoneLettoMollySbloccato"); }
    public static String getDescrizonePortaAndy() { return get("DescrizonePortaAndy"); }
    public static String getDescrizonePortaMolly() { return get("DescrizonePortaMolly"); }
    public static String getDescrizoneScale() { return get("DescrizoneScale"); }
    public static String getDescrizoneScalePianoTerra() { return get("DescrizoneScalePianoTerra"); }
    public static String getDescrizonePortaCucuna() { return get("DescrizonePortaCucuna"); }
    public static String getDescrizionePortaCucunaSbloccata() { return get("DescrizionePortaCucunaSbloccata"); }
    public static String getDialogoPortaCucinaAperta() { return get("DialogoPortaCucinaAperta"); }
    public static String getDescrizonePorticina() { return get("DescrizonePorticina"); }
    public static String getDescrizonePortaInternaCucina() { return get("DescrizonePortaInternaCucina"); }
    public static String getDescrizioneScarafaggi() { return get("DescrizioneScarafaggi"); }
    public static String getDialogoScarafaggi() { return get("DialogoScarafaggi"); }
    public static String getDescrizioneTombino() { return get("DescrizioneTombino"); }
    public static String getDescrizioneSacchi() { return get("DescrizioneSacchi"); }
    public static String getDescrizioneAlbero() { return get("DescrizioneAlbero"); }
    public static String getJessieAlbero() { return get("JessieAlbero"); }
    public static String getWoodyAlbero() { return get("WoodyAlbero"); }
    public static String getBuzzAlbero() { return get("BuzzAlbero"); }
    public static String getDescrizoneGrata() { return get("DescrizoneGrata"); }
    public static String getDescrizioneTunnel() { return get("DescrizioneTunnel"); }
    public static String getDescrizioneCancello() { return get("DescrizioneCancello"); }
    public static String getDialogoCancelloAperto() { return get("DialogoCancelloAperto"); }
    public static String getCancelloAperto() { return get("CancelloAperto"); }
    public static String getDescrizonetunnelRitorno() { return get("DescrizonetunnelRitorno"); }
    public static String getDescrizioneTopo() { return get("DescrizioneTopo"); }
    public static String getDescrizioneporticina() { return get("Descrizioneporticina"); }
    public static String getDescrizioneTuboBuio() { return get("DescrizioneTuboBuio"); }
    public static String getDialogoTopo() { return get("DialogoTopo"); }
    public static String getDialogoTopoRingraziamento() { return get("DialogoTopoRingraziamento"); }
    public static String getDescrizoneTuboRitorno() { return get("DescrizoneTuboRitorno"); }
    public static String getDescrizioneGeneratore() { return get("DescrizioneGeneratore"); }
    public static String getDescrizioneGeneratoreAcceso() { return get("DescrizioneGeneratoreAcceso"); }
    public static String getDescrizoneTopoCasa() { return get("DescrizoneTopoCasa"); }
    public static String getDescrizionePorticinaRitorno() { return get("DescrizionePorticinaRitorno"); }
    public static String getDescrizioneBuco() { return get("DescrizioneBuco"); }
    public static String getDescrizioneBucoRitorno() { return get("DescrizioneBucoRitorno"); }
    public static String getDescrizioneLeva() { return get("DescrizioneLeva"); }
    public static String getDescrizioneLevaAggiustata() { return get("DescrizioneLevaAggiustata"); }
    public static String getDialogoLevaAggiustata() { return get("DialogoLevaAggiustata"); }
    public static String getDescrizioneCancelloAperto() { return get("DescrizioneCancelloAperto"); }
    public static String getDescrizioneScarafaggio() { return get("DescrizioneScarafaggio"); }
    public static String getDescrizioneScarafaggioDopo() { return get("DescrizioneScarafaggioDopo"); }
    public static String getDialogoScarafaggioCorotto() { return get("DialogoScarafaggioCorotto"); }
    public static String getDialogoScarafaggioRetto() { return get("DialogoScarafaggioRetto"); }
    public static String getDialogoUsoMela() { return get("DialogoUsoMela"); }
    public static String getDescrizioneBotola() { return get("DescrizioneBotola"); }
    public static String getDescrizioneBotolaSbloccata() { return get("DescrizioneBotolaSbloccata"); }
    public static String getDescrizioneBotolaRitorno() { return get("DescrizioneBotolaRitorno"); }
    public static String getDescrizioneBoss() { return get("DescrizioneBoss"); }
    public static String getDialogoBossFinale() { return get("DialogoBossFinale"); }
}