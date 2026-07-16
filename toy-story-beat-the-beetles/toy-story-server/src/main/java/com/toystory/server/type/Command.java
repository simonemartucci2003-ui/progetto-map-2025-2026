package com.toystory.server.type;

/**
 * Incapsula i dati di un'azione richiesta da un giocatore.
 * <p>
 * Questa classe agisce come un <b>Data Transfer Object (DTO)</b>, ovvero un 
 * "pacchetto" il cui unico scopo è trasportare le informazioni dal client 
 * fino al motore logico del server ({@link com.toystory.server.Engine}).
 * </p>
 * <p>
 * Invece di far transitare stringhe grezze o parametri isolati attraverso il sistema, 
 * il server impacchetta la tipologia dell'azione (es. PRENDI, USA) e il bersaglio 
 * (l'oggetto o la stanza) all'interno di questa struttura, rendendo il passaggio 
 * di dati tra i vari componenti del server (Engine e Observer) pulito, 
 * tipizzato e facilmente estensibile.
 * </p>
 */
public class Command {

    private final CommandType type;
    private final String targetName;

    /**
     * Costruisce un nuovo comando pronto per essere elaborato dagli Observer.
     * 
     * @param type La classificazione dell'azione (es. {@link CommandType#GUARDA}, {@link CommandType#PRENDI}).
     * @param targetName Il nome dell'entità bersaglio dell'azione (es. "chiave", "baule", "porta").
     */
    public Command(CommandType type, String targetName) {
        this.type = type;
        this.targetName = targetName;
    }

    /**
     * @return L'enumeratore che definisce il tipo di comando richiesto.
     */
    public CommandType getType() {
        return type;
    }
    
    /**
     * @return L'enumeratore che definisce il tipo di comando richiesto.
     */
    public String getTargetName() {
        return targetName;
    }
}