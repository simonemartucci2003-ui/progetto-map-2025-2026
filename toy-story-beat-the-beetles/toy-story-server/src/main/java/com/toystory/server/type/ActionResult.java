package com.toystory.server.type;

/**
 * Struttura wrapper generica che incapsula l'esito di un'azione elaborata dal gioco.
 * <p>
 * Viene utilizzata per standardizzare le risposte interne del server, restituendo 
 * non solo un messaggio testuale, ma anche un indicatore di successo (boolean) 
 * e, opzionalmente, un dato complesso associato all'operazione (il "payload").
 * </p>
 * <p>
 * <b>Design Pattern - Static Factory Method:</b> Questa classe evita l'uso tradizionale 
 * della parola chiave {@code new} all'esterno. Fornisce invece metodi statici con nomi 
 * espliciti ({@link #ok} e {@link #fail}) per creare le istanze. Questo approccio 
 * rende il codice chiamante estremamente più leggibile e previene la creazione di 
 * stati incoerenti.
 * </p>
 *
 * @param <T> Il tipo del dato opzionale restituito (es. un oggetto raccolto, una stanza).
 */
public class ActionResult<T> {
    private final boolean success;
    private final String message;
    private final T payload;
    
    /**
     * Costruttore reso intenzionalmente privato per impedire l'istanziazione diretta.
     * <p>
     * Obbliga chi utilizza la classe a chiamare i metodi factory statici, 
     * garantendo così che il codice sia più parlante (es. leggere {@code ActionResult.fail("Errore")} 
     * è molto più chiaro di {@code new ActionResult(false, "Errore", null)}).
     * </p>
     * 
     * @param success true se l'azione ha avuto successo, false altrimenti.
     * @param message Il messaggio descrittivo.
     * @param payload Il dato risultante dall'azione, se presente.
     */
    private ActionResult(boolean success, String message, T payload) {
        this.success = success;
        this.message = message;
        this.payload = payload;
    }
    
    /**
     * Metodo factory statico per creare un risultato di SUCCESSO.
     * 
     * @param <T> Il tipo del payload.
     * @param message Il messaggio testuale di feedback positivo.
     * @param payload Il dato risultante dall'azione.
     * @return Una nuova istanza di ActionResult configurata per il successo (success = true).
     */
    public static <T> ActionResult<T> ok(String message, T payload) {
        return new ActionResult<>(true, message, payload);
    }
    
    /**
     * Metodo factory statico per creare un risultato di FALLIMENTO.
     * 
     * @param <T> Il tipo del payload.
     * @param message Il messaggio che spiega il motivo del fallimento.
     * @return Una nuova istanza di ActionResult configurata per l'errore (success = false, payload nullo).
     */
    public static <T> ActionResult<T> fail(String message) {
        return new ActionResult<>(false, message, null);
    }
    
    /**
     * @return true se l'azione è andata a buon fine, false altrimenti.
     */
    public boolean isSuccess() { return success; }
    
    /**
     * @return Il messaggio descrittivo associato all'esito.
     */
    public String getMessage() { return message; }
    
    /**
     * @return Il dato opzionale restituito dall'azione, o null in caso di fallimento.
     */
    public T getPayload() { return payload; }
}