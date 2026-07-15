package com.toystory.client.view;

import javax.swing.JOptionPane;
import java.awt.Component;

public class GameDialogs {

    public static int mostraMenuAvvio(Component parent, boolean haSalvataggi) {
        java.util.List<Object> listaOpzioni = new java.util.ArrayList<>();
        listaOpzioni.add("Nuova Partita");
        listaOpzioni.add("Unisciti");
        
        if (haSalvataggi) {
            listaOpzioni.add("Riprendi Partita");
        }
        
        Object[] opzioni = listaOpzioni.toArray();
        
        return JOptionPane.showOptionDialog(parent,
                "Benvenuto in Toy Story! Scegli come vuoi giocare:",
                "Menu di Avvio",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, 
                opzioni, 
                opzioni[0]);
    }

    public static String chiediIdPartita(Component parent) {
        return JOptionPane.showInputDialog(parent, 
                "Inserisci il Game ID della partita:", 
                "Unisciti", 
                JOptionPane.PLAIN_MESSAGE);
    }

    public static void mostraSuccessoCreazione(Component parent, String gameId) {
        JOptionPane.showMessageDialog(parent, 
                "Partita creata!\n\nIl tuo ID è: " + gameId + "\n\nAnnota questo codice: ti servirà per riprendere la partita in futuro.",
                "Partita Creata",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void mostraErrore(Component parent, String messaggio) {
        JOptionPane.showMessageDialog(parent, messaggio, "Errore", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confermaRitornoMenu(Component parent) {
        int conferma = JOptionPane.showConfirmDialog(parent,
            "Vuoi tornare al menu principale? La partita in corso rimarrà comunque salvata.",
            "Torna al menu",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        return conferma == JOptionPane.YES_OPTION;
    }
}