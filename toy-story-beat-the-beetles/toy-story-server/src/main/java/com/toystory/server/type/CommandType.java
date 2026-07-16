package com.toystory.server.type;

/**
 * Definisce l'insieme di tutte le azioni (comandi) che un giocatore può eseguire 
 * nel mondo di gioco.
 * <p>
 * Ogni valore di questo enum rappresenta un'intenzione specifica dell'utente 
 * che il server deve interpretare, validare ed eseguire tramite il sistema di 
 * {@link com.toystory.server.GameObserver} (Pattern Observer).
 * </p>
 */
public enum CommandType {
    /** Ispeziona una stanza o un oggetto specifico per leggerne la descrizione. */
    GUARDA, 
    
    /** Raccoglie un oggetto dall'ambiente e lo aggiunge all'inventario del personaggio attivo. */
    PRENDI,
    
    /** Utilizza un oggetto in combinazione con un altro o con l'ambiente (es. usare la chiave con il baule). */
    USA,   
    
    /** Cambia il personaggio giocabile attualmente sotto il controllo del client (es. passaggio da Woody a Buzz). */    
    CHIAMA,  
    
    /** Tenta di attraversare un varco o una porta per spostarsi in una stanza diversa. */
    VAI,  
    
    /** Interagisce verbalmente con un personaggio non giocante (NPC) presente nella stanza. */
    PARLA,
}
