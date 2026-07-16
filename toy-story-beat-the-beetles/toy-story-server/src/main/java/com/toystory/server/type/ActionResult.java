package com.toystory.server.type;

/**
 * Wrapper generico per l'esito di un'azione di gioco.
 * T rappresenta il tipo di "payload" opzionale associato al successo 
 * dell'azione (es. l'oggetto raccolto, la stanza raggiunta, ecc.)
 */
public class ActionResult<T> {
    private final boolean success;
    private final String message;
    private final T payload;

    private ActionResult(boolean success, String message, T payload) {
        this.success = success;
        this.message = message;
        this.payload = payload;
    }

    public static <T> ActionResult<T> ok(String message, T payload) {
        return new ActionResult<>(true, message, payload);
    }

    public static <T> ActionResult<T> fail(String message) {
        return new ActionResult<>(false, message, null);
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public T getPayload() { return payload; }
}