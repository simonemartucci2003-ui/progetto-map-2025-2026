# Relazione di Progetto: Toy Story - Beat the Beetles
**Corso di Metodi Avanzati di Programmazione (MAP)** **Università degli Studi di Bari Aldo Moro** **Sviluppatori:** Simone e Tiziana  
**Anno Accademico:** 2025/2026  

---

## 1. Introduzione e Caso di Studio

Il presente documento descrive la progettazione e l'implementazione di **"Toy Story: Beat the Beetles"**, un'avventura testuale e grafica sviluppata in linguaggio Java come caso di studio per l'esame di Metodi Avanzati di Programmazione. 

**Bozza Trama:** L'opera si ispira all'universo narrativo di *Toy Story*. La trama segue le vicende dei celebri giocattoli di Andy, rimasti soli in casa, nel disperato tentativo di recuperare la torta di compleanno del loro padroncino, sottratta da una banda di scarafaggi. 

L'obiettivo ingegneristico del progetto è applicare i pilastri della programmazione a oggetti e del clean code, estendendo l'architettura fornita a lezione e integrando i requisiti tecnici richiesti in sede d'esame: l'interfaccia grafica proprietaria (Swing), la gestione della persistenza dei dati (JDBC/Database), la programmazione concorrente (Thread) e la comunicazione in rete (Socket).

---

## 2. Albero del Progetto

Di seguito viene riportato l'albero strutturale del codice sorgente e delle risorse all'interno del repository:

```text
adventure/
│
├── resources/
│     └── stopwords                 # File di configurazione per il text parsing (articoli/preposizioni)
│
├── src/main/java/di/uniba/map/b/adventure/
│     │
│     ├── Engine.java               # Engine principale del gioco (gestione del ciclo di esecuzione)
│     ├── GameDescription.java      # Classe astratta madre per la definizione dello stato del gioco
│     ├── GameObservable.java       # Gestore delle notifiche per il pattern Observer
│     ├── GameObserver.java         # Interfaccia nativa per i Listener delle azioni
│     ├── GameUtils.java            # Classi di utilità per le meccaniche di gioco
│     ├── Utils.java                # Funzioni helper (I/O dei file, gestione flussi)
│     │
│     ├── impl/                     # LOGICA DI GIOCO (IMPLEMENTAZIONE)
│     │     ├── ToyStoryGame.java   # Classe core del gioco (inizializzazione stanze, oggetti e plot)
│     │     ├── MoveObserver.java   # Gestore della navigazione tra le stanze (Casa e Fogne)
│     │     ├── LookAtObserver.java # Gestore del comando di ispezione ambientale ("guarda")
│     │     ├── PickUpObserver.java # Gestore della raccolta degli oggetti e interazione con l'inventario
│     │     ├── OpenObserver.java   # Gestore dell'apertura di contenitori o varchi
│     │     ├── PushObserver.java   # Gestore della pressione di pulsanti o leve ambientali
│     │     └── UseObserver.java    # Gestore dell'utilizzo mirato degli oggetti di gioco
│     │
│     ├── parser/                   # PARSING DELL'INPUT TESTUALE
│     │     ├── Parser.java         # Analizzatore sintattico dell'input inserito dall'utente
│     │     └── ParserOutput.java   # Token finale generato [Azione + Oggetto Target]
│     │
│     ├── type/                     # COMPONENTI CORE (MODEL)
│     │     ├── Room.java           # Definizione delle stanze e dei loro collegamenti direzionali
│     │     ├── AdvObject.java      # Entità oggetto (raccoglibile, interattivo o statico)
│     │     ├── AdvObjectContainer.java # Estensione per oggetti contenitori (es. bauli, cassetti)
│     │     ├── Inventory.java      # Gestione della collezione di oggetti posseduti dal giocatore
│     │     ├── Command.java        # Astrazione del comando testuale e dei relativi sinonimi (alias)
│     │     └── CommandType.java    # Enumerazione dei comandi nativi supportati dal sistema
│     │
│     ├── view/                     # INTERFACCIA GRAFICA (VIEW)
│     │     ├── GameWindow.java     # JFrame principale e layout dei componenti Swing
│     │     └── GUIHandler.java     # Controller grafico per l'aggiornamento dinamico di testi e icone
│     │
│     └── database/                 # PERSISTENZA DATI (DATA LAYER)
│           └── DatabaseManager.java # Gestione del database di salvataggio e record (JDBC)
│
├── nb-configuration.xml            # Configurazione del progetto specifica per l'IDE NetBeans
├── nbactions.xml                   # Mappatura delle azioni di build e run per l'ambiente locale
└── pom.xml                         # Configurazione Maven e gestione centralizzata delle dipendenze