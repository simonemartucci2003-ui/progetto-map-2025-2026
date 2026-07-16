package com.toystory.server.type;

/**
 * Classe base astratta per tutte le entità fisiche o interattive all'interno del gioco.
 * <p>
 * Definisce le proprietà fondamentali comuni sia agli oggetti di scenario (elementi fissi 
 * come porte o mobili) sia agli oggetti raccoglibili (vedi {@link PickupableObject}). 
 * Ogni oggetto è identificato in modo univoco da un ID numerico, essenziale per 
 * la serializzazione nel database.
 * </p>
 */
public class AdvObject {
    
    private final int id;
    private final String name;
    private final String description;
    
    /**
     * Costruisce un nuovo elemento interattivo del gioco.
     * 
     * @param id Identificativo numerico univoco dell'oggetto.
     * @param name Nome logico o identificativo testuale dell'oggetto.
     * @param description Breve testo descrittivo associato all'oggetto.
     */
    public AdvObject(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
    
    /**
     * @return L'ID numerico univoco dell'oggetto.
     */
    public int getId() { return id; }
    
    /**
     * @return Il nome testuale dell'oggetto (usato anche come target nei comandi).
     */
    public String getName() { return name; }
    
    /**
     * @return La descrizione dettagliata dell'oggetto.
     */
    public String getDescription() { return description; }
}