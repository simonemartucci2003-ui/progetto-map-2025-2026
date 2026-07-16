package com.toystory.server.type;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta un personaggio giocabile all'interno del mondo di gioco (es. Woody, Buzz, Jessie).
 * <p>
 * Questa classe estende {@link GameCharacter} e gestisce le caratteristiche dinamiche 
 * del personaggio, come le abilità speciali sbloccabili durante la trama e una 
 * gestione dell'inventario (la "tasca").
 * </p>
 */
public class PlayableCharacter extends GameCharacter {
    
    /** Lista degli oggetti raccolti dal personaggio. */
    private final List<PickupableObject> pocket = new ArrayList<>();
    
    /** Numero massimo di oggetti trasportabili contemporaneamente (limite di inventario). */
    private static final int MAX_POCKET_SIZE = 2;
    
    /** L'abilità speciale associata al personaggio. */
    private Ability ability;
    
    /**
     * Crea un nuovo personaggio giocabile.
     * 
     * @param id L'ID univoco del personaggio.
     * @param name Il nome del personaggio.
     * @param description La descrizione biografica del personaggio.
     */
    public PlayableCharacter(int id, String name, String description) {
        super(id, name, description); 
    }
    
    /**
     * Recupera l'abilità speciale attualmente assegnata al personaggio.
     * 
     * @return Un oggetto {@link Ability}, o null se non ne possiede.
     */
    public Ability getAbility() {
        return ability;
    }

    /**
     * Assegna o aggiorna l'abilità speciale del personaggio (es. durante lo sblocco di un power-up).
     * 
     * @param specialAbility L'oggetto {@link Ability} da assegnare.
     */
    public void setAbility(Ability specialAbility) {
        this.ability = specialAbility;
    }

    /**
     * Verifica se il personaggio possiede attualmente un'abilità speciale attiva.
     * 
     * @return true se l'abilità è configurata, false altrimenti.
     */
    public boolean hasSpecialAbility() {
        return ability != null;
    }
    
    /**
     * Restituisce la lista degli oggetti contenuti nell'inventario del personaggio.
     * 
     * @return Una lista di {@link PickupableObject}.
     */
    public List<PickupableObject> getPocket() { 
        return pocket; 
    }
    
    /**
     * Verifica se il personaggio ha spazio sufficiente nell'inventario per raccogliere un nuovo oggetto.
     * 
     * @return true se il numero di oggetti è inferiore a {@value #MAX_POCKET_SIZE}.
     */
    public boolean canPickUp() {
        return pocket.size() < MAX_POCKET_SIZE;
    }
    
    /**
     * Tenta di aggiungere un oggetto all'inventario del personaggio, se lo spazio lo consente.
     * 
     * @param obj L'oggetto {@link PickupableObject} da raccogliere.
     * @return true se l'oggetto è stato aggiunto, false se l'inventario è pieno.
     */
    public boolean addToInventory(PickupableObject obj) {
        if (canPickUp()) {
            pocket.add(obj);
            return true;
        }
        return false;
    }
    
    /**
     * Rimuove un oggetto dall'inventario del personaggio (es. dopo averlo usato o posato).
     * 
     * @param obj L'oggetto {@link PickupableObject} da rimuovere.
     */
    public void removeObject(PickupableObject obj) {
        pocket.remove(obj);
    }
}