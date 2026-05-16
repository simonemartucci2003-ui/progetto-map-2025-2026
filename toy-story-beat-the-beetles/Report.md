# Relazione di Progetto: Toy Story - Beat the Beetles
**Corso di Metodi Avanzati di Programmazione (MAP)** **Università degli Studi di Bari Aldo Moro** **Sviluppatori:** Simone e Tiziana  
**Anno Accademico:** 2025/2026  

---

## 1. Introduzione e Caso di Studio

Il presente documento descrive la progettazione e l'implementazione di **"Toy Story: Beat the Beetles"**, un'avventura testuale e grafica sviluppata in linguaggio Java come caso di studio per l'esame di Metodi Avanzati di Programmazione. 

**Bozza Trama:** L'opera si ispira all'universo narrativo di *Toy Story*. La trama segue le vicende dei celebri giocattoli di Andy, rimasti soli in casa, nel disperato tentativo di recuperare la torta di compleanno del loro padroncino, sottratta da una banda di scarafaggi. 

L'obiettivo ingegneristico del progetto è applicare i pilastri della programmazione a oggetti e del clean code, estendendo l'architettura fornita a lezione e integrando i requisiti tecnici richiesti in sede d'esame: l'interfaccia grafica proprietaria (Swing), la gestione della persistenza dei dati (JDBC/Database), la programmazione concorrente (Thread) e la comunicazione in rete (Socket).

---

## 2. Architettura del Sistema e Albero del Progetto

Il software adotta un'architettura **Client-Server distribuita (con approccio "Fat Server")**, evoluzione del paradigma **MVC (Model-View-Controller)**

* **Il Client (View e Input Parsing):** È un'applicazione desktop leggera incentrata sulle librerie **Java Swing**. Ha il solo scopo di renderizzare l'interfaccia visiva per l'utente, intercettare l'input (testuale o tramite pulsanti) e inoltrarlo via rete.

* **Il Server (Engine, Model e Controller):** È il nucleo centrale del sistema. Gestisce lo stato del mondo di gioco (stanze, oggetti, inventario), elabora le azioni tramite il pattern **Observer**, orchestra la concorrenza via **Thread** e isola il Data Layer (**Database**).

La comunicazione tra i due moduli avviene in tempo reale tramite connessioni bidirezionali su **Socket TCP/IP**. Di seguito viene riportata l'organizzazione strutturale del repository suddiviso nei due progetti Maven indipendenti e basati su pacchetti personalizzati:

```text
toy-story-beat-the-beetles/        # Repository radice del progetto
│
│
├── Report.md                       # Presente relazione tecnica d'esame
├── pom.xml                         # Configurazione Maven del Progetto Padre (Multi-Modulo)
│
├── 💻 toy-story-client/            # MODULO CLIENT (Interfaccia e Rete Client)
│     ├── pom.xml                   # Configurazione Maven e dipendenze grafiche
│     └── src/main/java/com/toystory/client/
│           ├── ClientMain.java     # Entry point del Client e inizializzazione Socket
│           │
│           ├── parser/             # Sottosistema di Analisi Sintattica locale
│           │     ├── Parser.java   # Tokenizzatore dell'input utente
│           │     └── ParserOutput.java # Data Transfer Object (Azione + Target)
│           │
│           └── view/               # Sottosistema Grafico (VIEW)
│                 ├── GameWindow.java # Struttura JFrame e componenti Swing
│                 └── GUIHandler.java # Aggiornamento dinamico di testi, icone e inventario
│
└── 🖧 toy-story-server/            # MODULO SERVER (Logica, Persistenza e Concorrenza)
      ├── pom.xml                   # Configurazione Maven, driver JDBC e utility
      ├── src/main/resources/
      │     └── stopwords           # Dizionario dei filtri sintattici per il parsing
      └── src/main/java/com/toystory/server/
            ├── ServerMain.java     # Entry point del Server e attivazione ServerSocket
            ├── ServerThread.java   # Gestore multithreading per le sessioni dei singoli client
            ├── Engine.java         # Game Loop di background associato alla partita
            ├── GameDescription.java # Stato astratto del mondo di gioco
            ├── GameObservable.java  # Infrastruttura di notifica comandi
            ├── GameObserver.java    # Interfaccia per i listener delle azioni
            ├── GameUtils.java       # Classi di supporto algoritmico
            ├── Utils.java           # Gestione I/O generali e utility di sistema
            │
            ├── impl/               # LOGICA APPLICATIVA (CONTROLLER)
            │     ├── ToyStoryGame.java # Definizione del grafo di stanze (Casa/Fogne) e trama
            │     ├── MoveObserver.java   # Logica di navigazione direzionale
            │     ├── LookAtObserver.java # Ispezione degli elementi della stanza
            │     ├── PickUpObserver.java # Raccolta oggetti e trasferimento nell'inventario
            │     ├── OpenObserver.java   # Interazione con contenitori o accessi sbloccabili
            │     ├── PushObserver.java   # Attivazione di interruttori/meccanismi ambientali
            │     └── UseObserver.java    # Interazione mirata tra oggetti dell'inventario e mondo
            │
            ├── type/               # STRUTTURE DATI CORE (MODEL)
            │     ├── Room.java           # Struttura dati della stanza e adiacenze
            │     ├── AdvObject.java      # Astrazione degli item di gioco
            │     ├── AdvObjectContainer.java # Specializzazione per oggetti contenitori
            │     ├── Inventory.java      # Struttura dell'inventario del giocatore
            │     ├── Command.java        # Definizione formale del comando e sinonimi (alias)
            │     └── CommandType.java    # Enumerazione delle macro-azioni di sistema
            │
            └── database/           # PERSISTENZA (DATA LAYER)
                  └── DatabaseManager.java # Connessione JDBC (Salvataggi di stato e Hall of Fame)
```
## 3. Descrizione Dettagliata dei Pacchetti e delle Componenti
### 📂 Modulo Client (`toy-story-client`)

Il modulo Client rappresenta l'interfaccia utente (Frontend) dell'applicazione. È un guscio leggero che non possiede informazioni sulla mappa, sugli oggetti o sulla trama del gioco; il suo unico compito è gestire l'Input (intercettare i comandi) e l'Output (mostrare i risultati inviati dal Server).

#### 🗁 com.toystory.client (Package Root)
Rappresenta il punto di partenza del programma sul computer del giocatore.
* **`ClientMain.java`**: Contiene il metodo `main` del client. Ha la responsabilità di:
  1. Inizializzare l'interfaccia grafica Swing lanciando la finestra di gioco.
  2. Aprire un canale di comunicazione di rete (`Socket`) verso l'indirizzo IP e la porta del Server.
  3. Gestire il ciclo di ascolto: riceve i pacchetti inviati dal server e li passa alla grafica per l'aggiornamento a schermo.

#### 🗁 com.toystory.client.parser
È il motore di analisi linguistica locale. Esegue l'elaborazione del testo prima che questo venga inviato sulla rete, riducendo lo spreco di banda.

* **`Parser.java`**: Prende la stringa testuale digitata dall'utente (es. *"prendi il laser di Buzz"*) o generata dal click di un pulsante. Riconosce i verbi e le parole chiave ignorando le congiunzioni, isolando l'azione principale e l'eventuale oggetto su cui applicarla.

* **`ParserOutput.java`**: È un oggetto di puro trasferimento dati (DTO - Data Transfer Object). Contiene semplicemente due informazioni pulite: l'azione individuata (es. comando `PRENDI`) e il target (es. oggetto `laser`). Questo pacchetto leggero viene serializzato e spedito via Socket al Server.

#### 🗁 com.toystory.client.view
Gestisce l'intera interfaccia visiva del gioco tramite la libreria nativa Java Swing.

* **`GameWindow.java`**: Estende `JFrame` e definisce la struttura visiva del gioco. Configura la finestra desktop, l'area di testo dove scorre la storia, la barra inferiore per digitare i comandi e i pannelli laterali dedicati a mostrare l'inventario dei giocattoli.

* **`GUIHandler.java`**: È il "regista" della grafica. Non contiene logica decisionale, ma risponde agli ordini del server. Quando dal Socket arriva il messaggio *"Mostra la stanza delle fogne e aggiungi la torcia all'inventario"*, questa classe aggiorna istantaneamente i componenti della `GameWindow` in modo asincrono.

---

### 📂 Modulo Server (`toy-story-server`)

Il modulo Server è il motore computazionale centralizzato (Backend). Conserva in memoria lo stato dell'universo di gioco, applica le regole dell'avventura, valida le mosse del giocatore e isola il database.

#### 🗁 com.toystory.server (Package Root)
Ospita l'infrastruttura di rete, il motore di gioco e le classi astratte ereditate dall'architettura del docente.

* **`ServerMain.java`**: È l'entry point del server. Attiva un `ServerSocket` bloccante su una porta specifica e si mette in ascolto. Quando un computer client si connette per giocare, accetta la connessione e delega immediatamente la gestione ad un thread dedicato.

* **`ServerThread.java`**: Estende `Thread`. Implementa il requisito d'esame della **concorrenza (Multithreading)**. Ogni giocatore connesso ottiene il proprio `ServerThread` isolato; in questo modo più utenti possono giocare contemporaneamente le proprie partite sullo stesso server senza interferire tra loro.

* **`Engine.java`**: Coordina il flusso dei comandi all'interno del singolo thread. Riceve l'oggetto `ParserOutput` inviato dal client tramite la rete, estrae il comando e attiva il sistema di notifica per elaborarlo.

* **`GameDescription.java`**: Classe astratta che definisce la struttura base che una partita deve avere (mappa delle stanze, inventario corrente, stanza attuale).

* **`GameObservable.java` & `GameObserver.java`**: Implementano il pattern architetturale **Observer**. Permettono di slegare l'Engine dalla logica dei singoli comandi. L'Engine lancia un evento su `GameObservable`, e l'Observer registrato per quel comando si attiva automaticamente.

* **`Utils.java` & `GameUtils.java`**: Classi di servizio che contengono funzioni di utilità generale per la manipolazione delle stringhe, la gestione dei file di configurazione e il caricamento delle risorse di sistema.

#### 🗁 com.toystory.server.impl
Rappresenta lo strato dei Controller del gioco. Qui risiede la sceneggiatura dell'avventura e le regole per risolvere gli enigmi.
* **`ToyStoryGame.java`**: Estende `GameDescription`. È la classe dove viene materialmente creata la mappa di gioco. Definisce le stanze (la camera di Andy, la cucina, le fogne degli scarafaggi), i loro collegamenti (Nord, Sud, Est, Ovest), gli oggetti nascosti e le condizioni di vittoria.

* **`MoveObserver.java`**: Gestisce lo spostamento tra le stanze. Verifica se la direzione richiesta è valida e se il passaggio è libero o bloccato (es. una grata chiusa a chiave).

* **`LookAtObserver.java`**: Elabora l'ispezione ambientale. Restituisce la descrizione dettagliata della stanza o di un oggetto specifico quando il giocatore usa il comando "guarda".

* **`PickUpObserver.java`**: Gestisce la raccolta degli oggetti. Controlla se l'oggetto è presente nella stanza e se è raccoglibile, rimuovendolo dalla stanza e inserendolo nell'inventario del giocatore.

* **`OpenObserver.java`**: Controlla l'apertura di contenitori (es. il baule dei giocattoli) o di varchi d'accesso, modificandone lo stato interno.

* **`PushObserver.java`**: Gestisce l'attivazione di bottoni, leve o interruttori presenti nello scenario (es. la leva per deviare il flusso d'acqua nelle fogne).

* **`UseObserver.java`**: Isola la logica più complessa degli enigmi: l'interazione combinata tra oggetti (es. *USA il laser di Buzz SULLA grata arrugginita* per scioglierla e aprirsi la via verso il boss).

#### 🗁 com.toystory.server.type
Costituisce il **Model** del sistema, ovvero le strutture dati pure che descrivono i componenti dell'universo di gioco.
* **`Room.java`**: Modella una stanza del gioco. Memorizza il nome, la descrizione testuale, i collegamenti con le altre stanze adiacenti e la lista degli oggetti presenti al suo interno.

* **`AdvObject.java`**: Rappresenta un qualunque oggetto di gioco (la torcia, la torta, uno scarafaggio). Contiene attributi come il nome, una descrizione, se può essere raccolto o se è invisibile.

* **`AdvObjectContainer.java`**: Estende `AdvObject`. Rappresenta un oggetto speciale che può contenere altri oggetti al suo interno (es. un cassetto o un armadio).

* **`Inventory.java`**: Rappresenta lo zaino/inventario condiviso dei giocattoli, esponendo i metodi per aggiungere, rimuovere e controllare il possesso di un determinato strumento.

* **`Command.java`**: Definisce la struttura di un comando di gioco, associando alla macro-azione principale una lista di sinonimi o alias (es. il comando `PRENDI` risponderà anche a *"raccogli"*, *"afferra"*, *"prendi"*).

* **`CommandType.java`**: Una classe di tipo `Enum` che elenca in modo rigido e standardizzato tutte le azioni permesse dal motore di gioco (`NORD`, `SUD`, `GUARDA`, `PRENDI`, `USA`, ecc.).

#### 🗁 com.toystory.server.database
Rappresenta l'accesso alla persistenza dei dati sul disco fisso (Data Layer).

* **`DatabaseManager.java`**: Gestisce l'apertura e la chiusura delle connessioni tramite **JDBC**. Al suo interno si trovano le query SQL per:
  1. Salvare lo stato della partita corrente (permettendo al giocatore di chiudere il gioco e riprendere da dove aveva interrotto).
  2. Scrivere e leggere i record dei giocatori all'interno della tabella della classifica online ("Hall of Fame"), calcolando chi ha sconfitto gli scarafaggi nel minor tempo possibile.