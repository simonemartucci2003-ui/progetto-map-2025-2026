package com.toystory.server.impl;

import com.toystory.server.GameDescription;
import com.toystory.server.GameObserver;
import com.toystory.server.type.Command;
import com.toystory.server.type.CommandType;

public class LookAtObserver implements GameObserver {

    @Override
    public String update(Command command, GameDescription state) {
        // 1. Si attiva solo per il comando GUARDA
        if (command.getType() != CommandType.GUARDA) {
            return null;
        }

        String target = command.getTargetName();
        if (target == null) {
            return "TESTO|Cosa vorresti guardare?";
        }

        
       String descrizione;

        switch (target.toLowerCase()) {
            case "libreria":
                descrizione = Dialoghi.getDescrizoneLibreria();
                return "TESTO|" + descrizione;
                
            case "baule":
                boolean bauleAperto = state.getFlags().getOrDefault("BAULE_APERTO", false);
                
                if (!bauleAperto) {
                    // IL BAULE È CHIUSO: I giocattoli sono intrappolati e parlano.
                    String testoDialogo = Dialoghi.getDescrizoneBauleChiuso(); 
                
                    return "TESTO|" + testoDialogo;
                
                } else {
                    String testoDialogo = Dialoghi.getDescrizoneBauleAperto(); 
                            return "TESTO|" + testoDialogo;
                    }
        
            case "letto":
                // 1. Controlliamo la memoria del gioco: il baule è aperto o chiuso?
                // Se il flag non esiste ancora, getOrDefault restituisce 'false' (baule chiuso).
                bauleAperto = state.getFlags().getOrDefault("BAULE_APERTO", false);
                boolean lazoSbloccatoLetto = state.getFlags().getOrDefault("LAZO_UNLOCKED", false);

                if (!bauleAperto) {
                    // IL BAULE È CHIUSO: I giocattoli sono intrappolati e parlano.
                    String testoDialogo = Dialoghi.getDescrizoneLettoBauleChiuso(); 
                
                    return "TESTO|" + testoDialogo;
                
                } else {
                    // IL BAULE È APERTO: differenziamo in base al lazo
                    if (!lazoSbloccatoLetto) {
                        // Lazo NON ancora trovato
                        String testoDialogo = Dialoghi.getDescrizoneLettoBauleAperto(); 
                        return "TESTO|" + testoDialogo;
                    } else {
                        // Lazo GIÀ trovato
                        String testoDialogo = Dialoghi.getDescrizoneLettoBauleApertoLazoSbloccato(); 
                        return "TESTO|" + testoDialogo;
                    }
                }
                
            case "porta":
                descrizione = "La porta della camera. La maniglia è troppo in alto per un giocattolo...";
                break;
                
                
            //CORRIDOIO PRIMO PIANO
            case "porta_andy":
                descrizione = Dialoghi.getDescrizonePortaAndy();
                return "TESTO|" + descrizione;
                    
            case "porta_camera_molly":
                    descrizione = Dialoghi.getDescrizonePortaMolly();
                return "TESTO|" + descrizione;
                
            case "scale_giu": 
                descrizione = Dialoghi.getDescrizoneScale();
                return "TESTO|" + descrizione;
                
                

             //STANZA MOLLY
            case "baule_molly":
                descrizione = "Il baule dei giocattoli. Sembra socchiuso. Molly mette veraemente di tutto li dentro";
                return "TESTO|" + descrizione; 
                
            case "letto_molly":
                // 1. Controlliamo la memoria del gioco: la forcina e sbloccata o no?
                boolean ForcinaSbloccata = state.getFlags().getOrDefault("FORCINA_UNLOCKED", false);

                    if (!ForcinaSbloccata) {
                        // Forcina NON ancora trovata
                        String testoDialogo = Dialoghi.getDescrizoneLettoMollyBloccato(); 
                        return "TESTO|" + testoDialogo;
                    } else {
                        // Forcina GIÀ trovata
                        String testoDialogo = Dialoghi.getDescrizoneLettoMollySbloccato(); 
                        return "TESTO|" + testoDialogo;
                    }
                    
            case "bo_peep":   
                descrizione = "La nostra cara amica Bo-Peep, sicuramente potra darci una mano a risolvere questo mistero";
                return "TESTO|" + descrizione; 
                
            case "porta_molly":
                descrizione = "La porta della camera. Menoamele che Molly l'ha lasciata socchiusa!";
                break;
                
                
            //  CORRIDOIO PIANO TERRA
            case "scale_su":
                descrizione = Dialoghi.getDescrizoneScalePianoTerra();
                return "TESTO|" + descrizione;
                
            case "porta_cucina":
                 boolean PortaSbloccata = state.getFlags().getOrDefault("PORTA_SBLOCCATA", false);
                
                 if (!PortaSbloccata) {
                        // porta NON ancora aperta
                        descrizione = Dialoghi.getDescrizonePortaCucuna();
                        return "TESTO|" + descrizione;
                    } else {
                        // porta aperta
                        String testoDialogo = Dialoghi.getDescrizionePortaCucunaSbloccata(); 
                        return "TESTO|" + testoDialogo;
                    }

                
            case "porticina_cane":
                descrizione = Dialoghi.getDescrizonePorticina();
                return "TESTO|" + descrizione;
                
                
            //  CUCUNA
            case "porta_interna_cucina":
                descrizione = Dialoghi.getDescrizonePortaInternaCucina();
                return "TESTO|" + descrizione;
                
            case "scarafaggi":
                descrizione = Dialoghi.getDescrizioneScarafaggi();
                return "TESTO|" + descrizione;
                
                  
            //GIARDINO 
            case "porta_cane":
                descrizione = Dialoghi.getDescrizonePorticina();
                return "TESTO|" + descrizione;
                
            case "tombino":
                descrizione = Dialoghi.getDescrizioneTombino();
                return "TESTO|" + descrizione;
                
            case "sacchi_neri":
                descrizione = Dialoghi.getDescrizioneSacchi();
                return "TESTO|" + descrizione;
                
            case "albero":
                descrizione = Dialoghi.getDescrizioneAlbero();
                return "TESTO|" + descrizione;
                
                
                default:
                descrizione = "Lì non c'è nulla di interessante. ";
                break;  
        }
        
       
        

        return "TESTO|" + descrizione;
    }
}