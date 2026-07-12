/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.client;

import java.io.*;
import java.util.*;

public class SaveManager {
    private static final String SAVE_FILE = "recent_games.txt";

    public static void salvaPartita(String gameId) {
        Set<String> partite = leggiTutteLePartite();
        partite.add(gameId.toUpperCase()); // Aggiunge l'ID alla lista
        
        try (PrintWriter out = new PrintWriter(new FileWriter(SAVE_FILE))) {
            for (String id : partite) out.println(id);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static Set<String> leggiTutteLePartite() {
        Set<String> partite = new HashSet<>();
        File file = new File(SAVE_FILE);
        if (!file.exists()) return partite;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) partite.add(line);
        } catch (IOException e) { e.printStackTrace(); }
        return partite;
    }
}
