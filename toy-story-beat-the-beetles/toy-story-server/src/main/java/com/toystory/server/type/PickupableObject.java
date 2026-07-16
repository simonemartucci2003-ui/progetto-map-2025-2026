package com.toystory.server.type;

/**
 * Rappresenta un oggetto presente nel mondo di gioco che può essere raccolto, 
 * trasportato nell'inventario e utilizzato dai personaggi.
 * <p>
 * Estende la classe base {@link AdvObject} ereditandone le proprietà identificative 
 * (ID, nome e descrizione) e aggiungendo le informazioni necessarie per la 
 * rappresentazione visiva nell'interfaccia grafica del client.
 * </p>
 */
public class PickupableObject extends AdvObject {
    
   /** Percorso o nome del file dell'icona che rappresenta l'oggetto nella GUI del client. */
   private final String icona;
   
   /**
    * Crea un nuovo oggetto raccoglibile.
    * 
    * @param id L'identificativo univoco dell'oggetto.
    * @param name Il nome dell'oggetto.
    * @param description La descrizione dell'oggetto.
    * @param icona Il riferimento al file grafico dell'icona associata.
    */
   public PickupableObject(int id, String name, String description, String icona) {
        super(id, name, description);
        this.icona = icona;
    }
    
    /**
     * @return Il nome del file o il percorso dell'icona grafica dell'oggetto.
     */
    public String getIcona() { return icona; }
    
 }
