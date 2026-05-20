/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.server.impl;

import com.toystory.server.Engine;
import com.toystory.server.type.PickupableObject; 
import com.toystory.server.type.ContainerObject; 
import com.toystory.server.type.PlayableCharacter;
import com.toystory.server.type.HammInventory;
import com.toystory.server.type.Room;
import com.toystory.server.type.CommandType;
import java.util.HashMap;
import java.util.Map;

/**
 * Questa classe rappresenta l'implementazione effettiva del gioco di Toy Story.
 * Si occupa di inizializzare la mappa (le stanze), gli oggetti, i personaggi giocabili 
 * e l'inventario globale gestito da Hamm, mantenendo lo stato corrente della partita.
 */
public class ToyStoryGame {

    // --- ATTRIBUTI DI STATO DEL GIOCO ---
    
    /** La stanza in cui si trovano attualmente i personaggi. */
    private Room currentRoom;
    
    /** Il personaggio che il giocatore sta controllando in questo preciso momento (es. "WOODY"). */
    private PlayableCharacter activeCharacter;
    
    /** L'inventario globale condiviso, ovvero la pancia del salvadanaio Hamm. */
    private HammInventory hamm;
    
    /** Mappa per rintracciare rapidamente i personaggi giocabili tramite il loro nome. */
    private final Map<String, PlayableCharacter> playableCharacters = new HashMap<>();
    
    /** Mappa di tutte le stanze del gioco, indicizzate per ID (utile per i passaggi e il database). */
    private final Map<Integer, Room> rooms = new HashMap<>();

    /**
     * Metodo di inizializzazione dell'avventura.
     * Deve essere chiamato all'avvio del Server per caricare l'Atto 1 (La camera di Andy).
     */
    public void init() {
        // 1. CREAZIONE DEI PERSONAGGI GIOCABILI
        PlayableCharacter woody = new PlayableCharacter("WOODY");
        PlayableCharacter buzz = new PlayableCharacter("BUZZ");
        PlayableCharacter jessie = new PlayableCharacter("JESSIE");
        hamm = new HammInventory("HAMM");
        
        
        playableCharacters.put("WOODY", woody);
        playableCharacters.put("BUZZ", buzz);
        playableCharacters.put("JESSIE", jessie);
        
        // Impostiamo lo Sceriffo Woody come personaggio iniziale predefinito
        activeCharacter = woody;

        // 2. CREAZIONE DELLA PRIMA STANZA (CAMERA DI ANDY)
        String descrizioneCamera = "Ti trovi nella Camera di Andy. Le pareti sono decorate con le iconiche nuvolette azzurre. "
                + "Vedi il letto disfatto, la scrivania scolastica e un grande baule dei giocattoli in legno. "
                + "La porta verso il corridoio è accostata, ma la maniglia è troppo in alto per un giocattolo da solo!";
        
        Room cameraAndy = new Room(1, "La Camera di Andy", descrizioneCamera);
        rooms.put(cameraAndy.getId(), cameraAndy);
        
        // Impostiamo la Camera di Andy come punto di partenza del gioco
        currentRoom = cameraAndy;

        // 3. CREAZIONE E POSIZIONAMENTO DEGLI OGGETTI (Con Ereditarietà)
        // Creiamo il Lazo (Oggetto raccoglibile)
        PickupableObject lazo = new PickupableObject(101, "Lazo", "Un lazo di corda robusto, perfetto per agganciare oggetti.");
        cameraAndy.addObject(lazo); // Lo buttiamo sul pavimento della stanza
        
        // Creiamo la Scrivania (Oggetto contenitore fisso)
        ContainerObject scrivania = new ContainerObject(201, "Scrivania", "La scrivania in legno di Andy. Sopra ci sono dei libri scolastici.");
        cameraAndy.addObject(scrivania); // Anche la scrivania è un oggetto presente nella stanza
        
        // 4. PREPARAZIONE DELLE STANZE SUCCESSIVE (BOZZA PER IL PUNTA E CLICCA)
        // Creiamo una stanza finta ("Corridoio") solo per configurare il varco della porta
        Room corridoio = new Room(2, "Il Corridoio", "Il corridoio del piano di sopra. Senti Buster russare in lontananza.");
        rooms.put(corridoio.getId(), corridoio);
        
        // Colleghiamo la camera al corridoio tramite il varco grafico denominato "Porta"
        cameraAndy.addExit("Porta", corridoio);
    }

    // --- METODI GETTER E SETTER PER LA LOGICA DI GIOCO ---

    public Room getCurrentRoom() {
        return currentRoom;
    }
    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }
    /**
     * @return Il personaggio giocabile attualmente sotto il controllo dell'utente.
     */
    public PlayableCharacter getActiveCharacter() { 
        return activeCharacter; 
    }
    /**
     * Permette il cambio del personaggio attivo (Meccanica del "CHIAMA").
     * @param characterName Il nome del personaggio da attivare (es. "BUZZ").
     */
    public void switchCharacter(String characterName) {
        PlayableCharacter target = playableCharacters.get(characterName.toUpperCase());
        if (target != null) { 
            this.activeCharacter = target; 
        }
    }
    /**
     * @return Il personaggio NPC speciale Hamm, che funge da inventario di squadra.
     */
    public HammInventory getHamm() { 
        return hamm; 
    }
    /**
     * @return La mappa contenente tutti i personaggi giocabili registrati nel gioco.
     */
    public Map<String, PlayableCharacter> getPlayableCharacters() { 
        return playableCharacters; 
    }
    /**
     * @return La mappa completa di tutte le stanze del gioco, indicizzate per ID.
     */
    public Map<Integer, Room> getRooms() {
        return rooms;
    }
    // --- TEST LOCALE (TAPPA 1 DEL NOSTRO PIANO) ---
    
    /**
     * Piccolo metodo main temporaneo. Serve per fare click destro su questo file 
     * in NetBeans e selezionare "Run File" per verificare che tutto si carichi correttamente.
     */
    public static void main(String[] args) {
        // 1. Inizializziamo il gioco
        ToyStoryGame game = new ToyStoryGame();
        game.init();
        
        // 2. Inizializziamo l'Engine passandogli il gioco
        Engine engine = new Engine(game);
        
        System.out.println("=== INIZIO SIMULAZIONE DIRETTA ENGINE (PUNTA E CLICCA) ===");
        
        // Simulazione Click su: GUARDA STANZA
        System.out.println("\n[Utente clicca GUARDA]");
        System.out.println("Risposta Server: " + engine.executeAction(CommandType.GUARDA, ""));
        
        // Simulazione Click su: PRENDI -> Lazo
        System.out.println("\n[Utente clicca PRENDI su 'Lazo']");
        System.out.println("Risposta Server: " + engine.executeAction(CommandType.PRENDI, "Lazo"));
        
        // Simulazione Click su: CHIAMA -> Buzz
        System.out.println("\n[Utente clicca CHIAMA su 'Buzz']");
        System.out.println("Risposta Server: " + engine.executeAction(CommandType.CHIAMA, "Buzz"));
        
        // Simulazione Click su: VAI_A -> Porta (Senza aver risolto l'enigma)
        System.out.println("\n[Utente clicca sul varco 'Porta']");
        System.out.println("Risposta Server: " + engine.executeAction(CommandType.VAI_A, "Porta"));
        
        System.out.println("\n========================================================");
    }
}