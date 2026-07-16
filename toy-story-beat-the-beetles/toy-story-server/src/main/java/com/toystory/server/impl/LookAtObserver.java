package com.toystory.server.impl;

import com.toystory.server.GameDescription;
import com.toystory.server.GameObserver;
import com.toystory.server.type.Command;
import com.toystory.server.type.CommandType;
import com.toystory.server.ClientState;
import com.toystory.server.GameSession;

public class LookAtObserver implements GameObserver {

    @Override
    public String update(Command command, GameDescription state, ClientState client, GameSession session) {
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
                
             
            //INGRESSO FOGNA    
            case "grata_sopra":
                descrizione = Dialoghi.getDescrizoneGrata();
                return "TESTO|" + descrizione;
                
            case "cancello":
                boolean CancelloSbloccato = state.getFlags().getOrDefault("CANCELLO_SBLOCCATO", false);
                
                 if (!CancelloSbloccato) {
                        // cancello NON ancora aperta
                         descrizione = Dialoghi.getDescrizioneCancello();
                        return "TESTO|" + descrizione;
                    } else {
                        // porta aperta
                        String testoDialogo = Dialoghi.getCancelloAperto(); 
                        return "TESTO|" + testoDialogo;
                    }
                
            case "tunnel":
                descrizione = Dialoghi.getDescrizioneTunnel();
                return "TESTO|" + descrizione;
                
         
            //STANZA 1
            case "tunnel_ritorno":
                descrizione = Dialoghi.getDescrizonetunnelRitorno();
                return "TESTO|" + descrizione;
                
            case "topo":
                descrizione = Dialoghi.getDescrizioneTopo();
                return "TESTO|" + descrizione;
                
            case "porticina":
                descrizione = Dialoghi.getDescrizioneporticina();
                return "TESTO|" + descrizione;
                
            case "tubo_buio":
                descrizione = Dialoghi.getDescrizioneTuboBuio();
                return "TESTO|" + descrizione;  
                
            
            //STANZA BUIA 
            case "tubo_ritorno":
                descrizione = Dialoghi.getDescrizoneTuboRitorno();
                return "TESTO|" + descrizione;
                
            case "generatore":
                boolean generatoreAcceso = state.getFlags().getOrDefault("GENERATORE_ACCESO", false);
                
                if (!generatoreAcceso) {
                        // cancello NON ancora aperta
                         descrizione = Dialoghi.getDescrizioneGeneratore();
                        return "TESTO|" + descrizione;
                    } else {
                        // porta aperta
                        String testoDialogo = Dialoghi.getDescrizioneGeneratoreAcceso(); 
                        return "TESTO|" + testoDialogo;
                    }
                
            //CASA TOPO
            case "topo_casa":
                descrizione = Dialoghi.getDescrizoneTopoCasa();
                return "TESTO|" + descrizione;
                
            case "porticina_ritorno":
                descrizione = Dialoghi.getDescrizionePorticinaRitorno();
                return "TESTO|" + descrizione;
                
            case "buco_stretto":
                descrizione = Dialoghi.getDescrizioneBuco();
                return "TESTO|" + descrizione;

                
            //STANZA LEVA
            case "buco_stretto_ritorno":
                 descrizione = Dialoghi.getDescrizioneBucoRitorno();
                return "TESTO|" + descrizione;
                
            case "leva":
                boolean LevaAggiustata = state.getFlags().getOrDefault("LEVA_AGGIUSTATA", false);
                
                if (!LevaAggiustata) {
                        // leva NON ancora aggiustata
                         descrizione = Dialoghi.getDescrizioneLeva();
                        return "TESTO|" + descrizione;
                    } else {
                        // leva aggiustata
                        String testoDialogo = Dialoghi.getDescrizioneLevaAggiustata(); 
                        return "TESTO|" + testoDialogo;
                    }
            
                
            //FOGNA STANZA 2                
            case "cancello_aperto":
                descrizione = Dialoghi.getDescrizioneCancelloAperto();
                return "TESTO|" + descrizione;
                
            case "varco":
                boolean melaData = state.getFlags().getOrDefault("MELA_DATA", false);
                
                if (!melaData) {
                        // mela NON ancora data
                        descrizione = Dialoghi.getDescrizioneScarafaggio();
                        return "TESTO|" + descrizione;
                    } else {
                        // mel data
                        String testoDialogo = Dialoghi.getDescrizioneScarafaggioDopo(); 
                        return "TESTO|" + testoDialogo;
                    }
            
                
            //STANZA CON ACQUA/SENZA ACQUA
            case "botola":
                descrizione = Dialoghi.getDescrizioneBotola();
                return "TESTO|" + descrizione;
            
            case "botola_sbloccata":
                descrizione = Dialoghi.getDescrizioneBotolaSbloccata();
                return "TESTO|" + descrizione;
                
                
            //BOSS FINALE
            case "botolaRitorno":
                descrizione = Dialoghi.getDescrizioneBotolaRitorno();
                return "TESTO|" + descrizione;
            
            case "boss":
                descrizione = Dialoghi.getDescrizioneBoss();
                return "TESTO|" + descrizione;
                
                
                
            default:
                descrizione = "Lì non c'è nulla di interessante. ";
                break;  
        }
        
       
        

        return "TESTO|" + descrizione;
    }
}