package com.toystory.client;

import java.io.*;
import java.util.*;

public class SaveManager {

    private static final String SAVE_FILE = "recent_games.txt";

    public static void salvaPartita(String gameId) {
        String idPulito = gameId.toUpperCase();
        Set<String> partite = leggiTutteLePartite();
        
        // 1. Se l'ID è già stato salvato in passato, non facciamo nulla!
        if (partite.contains(idPulito)) {
            return; 
        }

        // 2. Se è un ID nuovo, apriamo il file in modalità APPEND (passando 'true' al FileWriter)
        // In questo modo scriviamo SOLO la nuova riga alla fine del file.
        try (PrintWriter out = new PrintWriter(new FileWriter(SAVE_FILE, true))) {
            out.println(idPulito);
        } catch (IOException e) { 
            System.err.println("[SaveManager] Errore durante il salvataggio: " + e.getMessage());
        }
    }

    public static Set<String> leggiTutteLePartite() {
        Set<String> partite = new HashSet<>();
        File file = new File(SAVE_FILE);
        
        if (!file.exists()) return partite;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                partite.add(line.trim());
            }
        } catch (IOException e) { 
            System.err.println("[SaveManager] Errore durante la lettura del file: " + e.getMessage()); 
        }
        return partite;
    }
}