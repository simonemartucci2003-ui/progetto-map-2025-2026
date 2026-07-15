package com.toystory.client.view;

import javax.swing.JOptionPane;
import java.awt.Component;

/**
 * Gestore centralizzato per l'interazione modale con l'utente.
 * <p>
 * Questa classe agisce come punto unico di accesso per tutti i messaggi di sistema, 
 * le richieste di input e le finestre di dialogo (popup) del client. 
 * </p>
 * <p>
 * Utilizzando questa classe invece di invocare direttamente {@link JOptionPane}, 
 * si garantisce:
 * <ul>
 *   <li><b>Coerenza visiva</b>: un aspetto uniforme per tutti gli avvisi e le richieste.</li>
 *   <li><b>Manutenibilità</b>: se in futuro si volesse sostituire lo stile dei messaggi </li>
 *   <li><b>Semplicità</b>: il codice del controller rimane pulito, delegando la gestione 
 *       dei componenti UI a un'interfaccia dedicata.</li>
 * </ul>
 * </p>
 * 
 * @author simon
 */
public class GameDialogs {
    /**
     * Mostra il menu principale all'avvio dell'applicazione.
     * 
     * @param parent Il componente genitore che ospita il dialogo.
     * @param haSalvataggi Indica se sono presenti salvataggi disponibili per la ripresa.
     * @return L'indice dell'opzione selezionata dall'utente.
     */
    public static int mostraMenuAvvio(Component parent) {
        Object[] opzioni = {"Nuova Partita", "Unisciti", "Riprendi Partita"};
        
        return JOptionPane.showOptionDialog(parent,
                "Benvenuto in Toy Story! Scegli come vuoi giocare:",
                "Menu di Avvio",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, 
                opzioni, 
                opzioni[0]);
    }
    
    /**
     * Richiede all'utente l'inserimento dell'ID di una partita esistente.
     * 
     * @param parent Il componente genitore.
     * @return La stringa inserita dall'utente, o null se il dialogo viene annullato.
     */
    public static String chiediIdPartita(Component parent) {
        return JOptionPane.showInputDialog(parent, 
                "Inserisci il Game ID della partita:", 
                "Unisciti", 
                JOptionPane.PLAIN_MESSAGE);
    }
    
    /**
     * Notifica l'utente dell'avvenuta creazione di una nuova partita.
     * 
     * @param parent Il componente genitore.
     * @param gameId L'identificativo univoco della partita appena creata.
     */
    public static void mostraSuccessoCreazione(Component parent, String gameId) {
        JOptionPane.showMessageDialog(parent, 
                "Partita creata!\n\nIl tuo ID è: " + gameId + "\n\nAnnota questo codice: ti servirà per riprendere la partita in futuro.",
                "Partita Creata",
                JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Mostra un messaggio di errore a schermo in caso di problemi di comunicazione o logica.
     * 
     * @param parent Il componente genitore.
     * @param messaggio Il testo dell'errore da visualizzare.
     */
    public static void mostraErrore(Component parent, String messaggio) {
        JOptionPane.showMessageDialog(parent, messaggio, "Errore", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Richiede una conferma per tornare al menu principale, avvisando del salvataggio automatico.
     * 
     * @param parent Il componente genitore.
     * @return true se l'utente conferma di voler tornare al menu, false altrimenti.
     */
    public static boolean confermaRitornoMenu(Component parent) {
        int conferma = JOptionPane.showConfirmDialog(parent,
            "Vuoi tornare al menu principale? La partita in corso rimarrà comunque salvata.",
            "Torna al menu",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        return conferma == JOptionPane.YES_OPTION;
    }
}