package com.toystory.server.type;

/**
 * Rappresenta un'abilità speciale peculiare di un personaggio giocabile.
 * <p>
 * Ogni abilità (es. il Lazo per Woody, il Laser per Buzz) è caratterizzata da un nome 
 * e da un'icona grafica. Queste abilità possono essere necessarie per risolvere 
 * specifici enigmi o per interagire in modi unici con l'ambiente.
 * </p>
 */
public class Ability {
    private final String name;
    private final String iconPath; // Percorso dell'immagine nei file della GUI
    
    /**
     * Crea una nuova abilità speciale.
     * 
     * @param name Il nome dell'abilità (es. "Lazo", "Laser").
     * @param iconPath Il percorso relativo dell'icona da mostrare nell'interfaccia grafica.
     */
    public Ability(String name, String iconPath) {
        this.name = name;
        this.iconPath = iconPath;
    }
    
    /**
     * @return Il nome descrittivo dell'abilità.
     */
    public String getName() {
        return name;
    }
    
    /**
     * @return Il percorso dell'immagine associata all'abilità.
     */
    public String getIconPath() {
        return iconPath;
    }
}
