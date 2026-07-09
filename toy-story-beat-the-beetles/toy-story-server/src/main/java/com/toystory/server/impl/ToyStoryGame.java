/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.toystory.server.impl;

import com.toystory.server.GameDescription;
import com.toystory.server.type.Room;
import com.toystory.server.type.AdvObject;
import com.toystory.server.type.ContainerObject;
import com.toystory.server.type.PickupableObject;
import com.toystory.server.type.PlayableCharacter;
import com.toystory.server.type.Ability;

/**
 * Istanzia concretamente l'universo di gioco: modella la planimetria della casa di Andy 
 * e delle fogne, colloca i passaggi segreti, definisce gli scarafaggi nemici 
 * e setta le condizioni di vittoria della storia.
 */
public class ToyStoryGame extends GameDescription {

    /**
     * Costruttore di ToyStoryGame. Richiama il costruttore della classe madre.
     */
    public ToyStoryGame() {
        super();
    }

    /**
     * Implementazione del metodo astratto init(). Viene invocato dall'Engine 
     * all'avvio del server per generare il mondo di gioco.
     */
    @Override
    public void init() throws Exception {
        // ---------------------------------------------------------------------
        // INIZIALIZZAZIONE DEI FLAG DI PROGRESSIONE (TRAMA)
        // ---------------------------------------------------------------------
        // Questi flag ereditati dalla classe madre verranno letti e modificati 
        // dagli Observer (es. UseObserver, OpenObserver) per far avanzare la storia.
        this.getFlags().put("TUTORIAL_START", true); // Il gioco è appena iniziato
        this.getFlags().put("CHEST_OPENED", false);   // Il baule parte chiuso
        this.getFlags().put("LASER_USED", false);     // Buzz non ha ancora illuminato il letto
        this.getFlags().put("LAZO_UNLOCKED", false);  // Woody non ha ancora ottenuto il lazo

        // ---------------------------------------------------------------------
        // AVVIO CONFIGURAZIONE DELLE STANZE
        // ---------------------------------------------------------------------
        // Configura il primo livello (Tutorial nella Camera di Andy)
        configureCameraAndy();
        
        // [Nota futura]: Qui verranno inseriti i metodi per le stanze successive
        // configureCorridoio();
        // configureFogne();

        //DATABASE
        // 2. Chiediamo alla classe base di occuparsi del database
        // ToyStoryGame non sa NIENTE del DB, sa solo che il mondo deve essere sincronizzato.
        this.syncWorldWithDatabase();
    }

    /**
     * Genera la mappa, gli oggetti interattivi, le hitbox logiche e i personaggi 
     * per la prima stanza di gioco (Camera di Andy).
     */
    private void configureCameraAndy() {
        // 1. CREAZIONE DELLA STANZA (Contenitore macroscopico di scenario)
        // ID: 1, Nome: Camera di Andy
        Room cameraAndy = new Room(1, "Camera di Andy", 
            "La stanza di Andy. È la mattina del suo compleanno, la stanza è vuota e silenziosa.");
        
        // 2. CREAZIONE DEGLI OGGETTI (Strutture dati del pacchetto com.toystory.server.type)
        // Definiamo i nomi rigorosamente in minuscolo per facilitare il parsing dei comandi di rete
        
        // Oggetto raccoglibile (PickupableObject): finirà nell'inventario tascabile
        PickupableObject chiave = new PickupableObject(101, "chiave", "Una piccola chiave dorata, ideale per un lucchetto.");
        
        // Oggetto di scenario fisso (AdvObject): l'utente può GUARDARE o PRENDERE oggetti da qui
        AdvObject libreria = new AdvObject(201, "libreria", "Una libreria in legno piena di fumetti e libri di avventure.") {};
        
        // Oggetto Contenitore (ContainerObject): può essere APERTO e bloccato/sbloccato con chiavi
        ContainerObject baule = new ContainerObject(202, "baule", "Il grande baule in legno dei giocattoli.") {};
        baule.setLocked(true); // Impone il blocco iniziale (richiede l'azione USA chiave CON baule)
        baule.setOpen(false);   // Il coperchio è abbassato

        // Elementi ambientali statici utili alla seconda parte del tutorial
        AdvObject letto = new AdvObject(203, "letto", "Il letto di Andy. Sotto è decisamente troppo buio per vedere a occhio nudo.") {};
        AdvObject porta = new AdvObject(204, "porta", "La porta della camera. Il pomello dorato è troppo in alto per un giocattolo di pezza.") {};

        // 3. COLLOCAMENTO DEGLI OGGETTI NELLO SCENARIO
        // Agganciamo gli oggetti appena creati alla lista degli elementi presenti in questa stanza
        cameraAndy.getObjects().add(chiave);
        cameraAndy.getObjects().add(libreria);
        cameraAndy.getObjects().add(baule);
        cameraAndy.getObjects().add(letto);
        cameraAndy.getObjects().add(porta);

        // 4. CONFIGURAZIONE DEI PERSONAGGI GIOCABILI (PlayableCharacter)
        PlayableCharacter woody = new PlayableCharacter("Woody");
        PlayableCharacter buzz = new PlayableCharacter("Buzz Lightyear");

        // Buzz Lightyear: Viene inizializzato equipaggiando la sua mossa peculiare (Il Laser)
        Ability laser = new Ability("Laser", "/images/skills/laser.png");
        buzz.setAbility(laser);

        // Woody: Nella prima parte della storia non ha il lazo (valore impostato a null)
        // Verrà sbloccato dinamicamente via codice durante gli eventi di gioco
        woody.setAbility(null); 

        // 5. REGISTRAZIONE DELLO STATO INIZIALE NELLA CLASSE MADRE (GameDescription)
        // Salva la stanza nell'elenco globale del gioco
        this.getRooms().add(cameraAndy);
        
        // Comunica al Server che la partita comincia fisicamente all'interno della Camera di Andy
        this.setCurrentRoom(cameraAndy);      
        
        // Imposta Woody come personaggio attivo selezionato di default al primo avvio
        this.setCurrentPlayer(woody);        
    }
}