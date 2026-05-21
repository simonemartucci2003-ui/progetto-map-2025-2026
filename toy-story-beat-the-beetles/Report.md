# Relazione di Progetto: Toy Story - Beat the Beetles

**Corso di Metodi Avanzati di Programmazione (MAP)** Università degli Studi di Bari Aldo Moro

**Sviluppatori:** Simone Martucci e Tiziana Mascolo

**Anno Accademico:** 2025/2026

---
## 🗺️ Indice dei Contenuti
* [1. Introduzione e Caso di Studio](#1-introduzione-e-caso-di-studio)
* [2. Architettura del Sistema e Albero del Progetto](#2-architettura-del-sistema-e-albero-del-progetto)
* [3. Descrizione Dettagliata dei Pacchetti e delle Componenti](#3-descrizione-dettagliata-dei-pacchetti-e-delle-componenti)
    * [📂 Modulo Client (`toy-story-client`)](#-modulo-client-toy-story-client)
        * [🗁 `com.toystory.client` (Package Root)](#-comtoystoryclient-package-root)
        * [🗁 `com.toystory.client.view`](#-comtoystoryclientview)
    * [📂 Modulo Server (`toy-story-server`)](#-modulo-server-toy-story-server)
        * [🗁 `com.toystory.server` (Package Root)](#-comtoystoryserver-package-root)
        * [🗁 `com.toystory.server.impl`](#-comtoystoryserverimpl)
        * [🗁 `com.toystory.server.type`](#-comtoystoryservertype)
        * [🗁 `com.toystory.server.database`](#-comtoystoryserverdatabase)

## 1. Introduzione e Caso di Studio

Il presente documento descrive la progettazione e l'implementazione di **"Toy Story: Beat the Beetles"**, un'avventura grafica punta-e-clicca cooperativa sviluppata in linguaggio Java come caso di studio per l'esame di Metodi Avanzati di Programmazione.

**Bozza Trama:** L'opera si ispira all'universo narrativo di *Toy Story*. La trama segue le vicende dei celebri giocattoli di Andy, rimasti soli in casa, nel disperato tentativo di recuperare la torta di compleanno del loro padroncino, sottratta da una banda di scarafaggi.

L'obiettivo ingegneristico del progetto è applicare i pilastri della programmazione a oggetti (OOP) e del *clean code*, estendendo l'architettura fornita a lezione e integrando i requisiti tecnici richiesti in sede d'esame:

* **Interfaccia Grafica Proprietaria:** Sviluppata interamente con Java Swing.
* **Persistenza dei Dati:** Gestita mediante JDBC e Database relazionale.
* **Programmazione Concorrente:** Realizzata tramite Thread per l'architettura di rete.
* **Comunicazione in Rete:** Basata su Socket TCP/IP strutturate per il supporto **Multiplayer cooperativo in tempo reale**, che permette a più client di interagire contemporaneamente sullo stesso stato di gioco condiviso.

---

## 2. Architettura del Sistema e Albero del Progetto

Il software adotta un'architettura Client-Server distribuita di tipo **Fat Server**, evoluzione del paradigma **MVC (Model-View-Controller)**:

* **Il Client (View ed Event Handling):** Rappresenta un'applicazione desktop leggera. Intercetta i click dell'utente sui componenti grafici e li traduce immediatamente in comandi di rete strutturati (`AZIONE|TARGET`), delegando ogni calcolo al server.
* **Il Server (Engine, Model e Controller):** Centralizza lo stato del mondo di gioco. Elabora le richieste dei client, gestisce la concorrenza tra più giocatori connessi, aggiorna il database e invia i responsi in tempo reale.

La sincronizzazione multiplayer si basa su un meccanismo di **Broadcast**: ogni volta che un'azione altera il mondo di gioco, il server notifica contemporaneamente tutti i client connessi, aggiornando le rispettive interfacce grafiche all'istante.

L'organizzazione strutturale del repository è suddivisa in due progetti Maven indipendenti:

```text
toy-story-beat-the-beetles/        # Repository radice del progetto
│
├── Report.md                       # Presente relazione tecnica d'esame
├── Trama.md                        # Documento di sceneggiatura e progressione degli enigmi
├── pom.xml                         # Configurazione Maven del Progetto Padre (Multi-Modulo)
│
├── 💻 toy-story-client/            # MODULO CLIENT (Interfaccia e Rete)
│     ├── pom.xml                   # Configurazione Maven e dipendenze
│     └── src/main/java/com/toystory/client/
│           ├── ClientMain.java     # Entry point del Client e avvio dell'applicazione
│           ├── GameClient.java     # Gestore Socket e Thread di ascolto asincrono (uso di Lambda)
│           │
│           └── view/               # Sottosistema Grafico (VIEW)
│                 ├── GameWindow.java # Finestra JFrame principale (Editor Matisse)
│                 └── GUIHandler.java # Gestore del rendering dinamico di testi, inventari e icone
│
└── 🖧 toy-story-server/            # MODULO SERVER (Logica, Persistenza e Concorrenza)
      ├── pom.xml                   # Configurazione Maven e driver JDBC
      └── src/main/java/com/toystory/server/
            ├── ServerMain.java     # Entry point, ServerSocket e Registro Thread per Broadcast
            ├── ServerThread.java   # Thread dedicato alla sessione del singolo client e I/O di rete
            ├── Engine.java         # Centralizzatore dei comandi e smistatore ad eventi
            ├── GameDescription.java # Stato astratto della partita (Mappa, Stanze, Player attivi)
            ├── GameObservable.java  # Infrastruttura del Pattern Observer per la notifica dei comandi
            ├── GameObserver.java    # Interfaccia base per i listener delle azioni
            ├── GameUtils.java       # Classi di supporto algoritmico
            ├── Utils.java           # Utility di sistema (I/O, manipolazione stringhe)
            │
            ├── impl/               # LOGICA APPLICATIVA (CONTROLLER)
            │     ├── ToyStoryGame.java # Grafo delle stanze (Casa, Fogne), posizionamento item e trama
            │     ├── MoveObserver.java   # Gestore della navigazione direzionale tra stanze
            │     ├── LookAtObserver.java # Gestore dell'ispezione visiva di stanze e oggetti
            │     ├── PickUpObserver.java # Gestore della raccolta degli item
            │     ├── OpenObserver.java   # Gestore dell'apertura di varchi e contenitori sbloccabili
            │     ├── PushObserver.java   # Gestore della pressione di bottoni e interruttori ambientali
            │     └── UseObserver.java    # Risolutore degli enigmi combinati (es. Oggetto su Elemento)
            │
            ├── type/               # STRUTTURE DATI CORE (MODEL)
            │     ├── Room.java           # Modello della stanza e dei suoi collegamenti adiacenti
            │     ├── GameCharacter.java  # Classe astratta base per le entità biologiche
            │     ├── PlayableCharacter.java # Specializzazione per i personaggi giocabili (es. Woody, Buzz)
            │     ├── NonPlayableCharacter.java # Specializzazione per NPC e nemici (es. Scarafaggi)
            │     ├── AdvObject.java      # Classe astratta base per gli item di gioco
            │     ├── PickupableObject.java # Oggetti inseribili negli inventari
            │     ├── ContainerObject.java # Oggetti di scenario fissi che fungono da contenitori
            │     ├── HammInventory.java  # Inventario di squadra centralizzato (Pattern Singleton)
            │     ├── Command.java        # Struttura dei comandi ammessi e relativi alias
            │     └── CommandType.java    # Enumerazione rigida delle macro-azioni di gioco
            │
            └── database/           # PERSISTENZA (DATA LAYER)
                  └── DatabaseManager.java # Connessione JDBC (Salvataggio partite e Classifica Hall of Fame)

```

---

## 3. Descrizione Dettagliata dei Pacchetti e delle Componenti

### 📂 Modulo Client (`toy-story-client`)

Il modulo Client funge da puro Frontend (si limita a fare da tramite tra gli occhi dell'utente e il server.). È un'architettura leggera slegata dalle regole di business del gioco: non conosce la mappa, la trama o gli enigmi, ma reagisce unicamente agli input fisici dell'utente e ai pacchetti di testo provenienti dal server.

#### 🗁 `com.toystory.client` (Package Root)

* **`ClientMain.java`:** Rappresenta l'avvio del modulo. Ha il compito di inizializzare l'interfaccia grafica e istanziare il motore di rete.
* **`GameClient.java`:** Centralizza la gestione del Socket TCP/IP. All'avvio apre i flussi di input/output e fa partire un thread interno (`NetworkListenerThread`) che rimane in ascolto asincrono della rete per intercettare i messaggi del server. Non c'è alcuna traccia, importazione o riferimento diretto ai componenti grafici delle finestre, si occupa esclusivamente di bit, byte e stringhe che viaggiano sul Socket.
> La classe GameClient.java non è accoppiata a Swing. Utilizza l'interfaccia funzionale nativa **`Consumer<String>`** tramite espressioni **Lambda** per delegare l'output verso l'esterno. In questo modo la rete notifica l'arrivo del testo in modo asincrono senza bloccare l'interfaccia (evitando il congelamento della GUI). 
>
>* **Separazione delle Responsabilità (SRP):** ClientMain avvia, GameWindow mostra i pixel, GameClient gestisce i bit sulla rete. Nessuno fa il lavoro dell'altro.
>
>* **Asincronia e Concorrenza:** Il socket vive su un thread di background (NetworkListenerThread) isolato, garantendo che l'interfaccia grafica rimanga fluida, reattiva e non si congeli mai (anche in presenza di lag della rete).
>
>* **Estendibilità e Riuso (Disaccoppiamento):** Grazie alla Lambda Expression (Consumer<String>), la logica di rete è pura e universale. Funziona sia stampando in console che aggiornando componenti Swing complessi, rendendo il codice pronto per qualsiasi evoluzione futura del software.





#### 🗁 `com.toystory.client.view`

* **`GameWindow.java`:** Estende `JFrame`. Definisce il layout visivo desktop del gioco: l'area di scorrimento della storia, i pannelli laterali per gli inventari personali dei giocattoli e i pulsanti contestuali per interagire con l'ambiente circostante.
* **`GUIHandler.java`:** Agisce da mediatore per la View. Riceve le stringhe inoltrate dal `GameClient` (tramite la Lambda di callback) ed esegue in sicurezza gli aggiornamenti grafici sul thread grafico di Java (Event Dispatch Thread via `SwingUtilities.invokeLater`).

---

### 📂 Modulo Server (`toy-story-server`)

Il modulo Server rappresenta il Backend dell'applicazione. È responsabile del mantenimento dello stato di gioco, della computazione delle regole, della concorrenza e dell'accesso al database.

#### 🗁 `com.toystory.server` (Package Root)

* **`ServerMain.java`:** Rappresenta l'entry point assoluto del Backend. Ha la responsabilità di inizializzare l'istanza globale del gioco, accendere l'Engine e configurare una `ServerSocket` ancorata alla porta `6666`. La classe implementa un ciclo di ascolto infinito guidato dal metodo bloccante `serverSocket.accept()`. Ogni volta che un client si connette, la portineria del server si risveglia, genera un canale di comunicazione dedicato (`Socket`) e lo delega immediatamente a un thread isolato (`ServerThread`), liberando istantaneamente la socket principale per i giocatori successivi.

  Per orchestrare l'architettura multiplayer distribuita, `ServerMain` introduce tre pilastri fondamentali:

  1. **Disaccoppiamento della Concorrenza (Multithreading):** Il server non comunica mai direttamente con i singoli client all'interno del flusso principale. Delegando la gestione dell'I/O di rete a istanze separate di `ServerThread`, il sistema garantisce la totale asincronia: più utenti possono inviare comandi e giocare contemporaneamente senza bloccare il server o subire latenze l'uno dall'altro.
  
  2. **Gestione della Concorrenza e Thread-Safety (`synchronizedList`):** Il server mantiene in memoria un registro statico globale di tutti i client attualmente attivi. Poiché i thread dei giocatori possono accedere a questa lista in modo asincrono e concorrente (ad esempio, un giocatore invia un comando mentre un altro si disconnette), l'uso di una classica struttura dati come `ArrayList` causerebbe corruzioni di memoria ed eccezioni di tipo `ConcurrentModificationException`. Per blindare il sistema, la lista viene dichiarata tramite:
     ```java
     public static final List<ServerThread> clientThreads = Collections.synchronizedList(new ArrayList<>());
     ```
     Questo guscio di protezione della libreria standard di Java applica un meccanismo di mutua esclusione (Lock) invisibile: un thread alla volta esegue le operazioni di lettura/scrittura, mentre gli altri attendono in coda per frazioni di millisecondo, garantendo la totale stabilità del server.

  3. **Il Meccanismo di Sincronizzazione (Broadcast):** La presenza del registro sincronizzato dei thread è l'elemento cardine che materializza l'esperienza cooperativa. Quando un client esegue un'azione che altera lo stato del mondo di gioco (es. "Woody raccoglie il Lazo"), l'evento viene convalidato dall'Engine sul thread di quel giocatore. Tuttavia, per evitare che l'aggiornamento rimanga isolato, il thread mittente interroga la lista statica di `ServerMain` e cicla in tempo reale su tutti i `ServerThread` memorizzati, inviando a tappeto lo stesso messaggio a tutti i partecipanti. Questo approccio garantisce che ogni client veda la stessa identica storia evolversi in simultanea sul proprio schermo.

* **`ServerThread.java`:** Estende `Thread` e isola la comunicazione con ciascun client. Riceve i comandi generati dai bottoni (es. `GUARDA|Baule`), li converte in token puliti e ne richiede l'esecuzione all'`Engine`. Una volta ottenuta la risposta testuale, cicla sulla lista globale dei thread per trasmetterla a tutti i giocatori attivi contemporaneamente.
* **`Engine.java`:** Il controllore dei comandi. Riceve l'azione dall'infrastruttura di rete, mappa la stringa sulla costante corrispondente dell'enum `CommandType` e sveglia i relativi Observer registrati.
* **`GameDescription.java`:** Classe astratta fondamentale che modella i requisiti minimi di una partita (struttura delle stanze, giocatore correntemente attivo, stato dei flag di progressione).
* **`GameObservable.java` & `GameObserver.java`:** Implementano il pattern architetturale **Observer** (noto anche come *Publish-Subscribe*). Questa scelta ingegneristica è fondamentale per garantire il completo disaccoppiamento tra il motore principale del gioco (`Engine.java`) e le specifiche routine algoritmiche che governano i singoli comandi d'azione (`MoveObserver`, `PickUpObserver`, `UseObserver`, ecc.).

  L'adozione di questo design pattern permette di risolvere le problematiche tipiche delle architetture monolitiche e rigide, articolandosi su tre aspetti progettuali chiave:

  1. **Separazione delle Responsabilità (Single Responsibility Principle):** In un'implementazione ingenua, l'Engine dovrebbe contenere un costrutto condizionale (`if-else` o `switch`) mastodontico per interpretare ed eseguire ogni singola azione. Questo approccio centralizzerebbe troppe competenze in un unico file (gestione della rete, regole di movimento, risoluzione degli enigmi). Tramite il Pattern Observer, l'`Engine` (che estende `GameObservable`) assume l'unico compito di fare da "banditore": riceve il comando dalla rete e lo notifica a una lista di ascoltatori (`List<GameObserver>`), ignorando completamente i dettagli implementativi su come quel comando verrà processato.
  
  2. **Disaccoppiamento tramite Interfacce Astratte:** La comunicazione tra l'Engine e la logica applicativa avviene esclusivamente attraverso l'interfaccia astratta `GameObserver`, che impone il metodo standardizzato `update(Command comando, GameDescription state)`. All'avvio del server, tutte le classi specializzate (i controller in `com.toystory.server.impl`) si iscrivono al registro dell'Engine. Quando un client genera un evento (es. `PRENDI|Lazo`), l'Engine lancia il metodo `notifyObservers()`. Ciascun osservatore si risveglia in modo asincrono: quelli non interessati tornano immediatamente a riposo, mentre l'osservatore specifico (in questo caso `PickUpObserver`) si attiva per validare la mossa, aggiornare il modello e restituire la stringa di responso.

  3. **Estendibilità del Sistema (Principio Open/Closed):** Questa architettura rende il software nativamente predisposto a future espansioni senza il rischio di introdurre regressioni o bug nel codice preesistente. Se in una fase successiva dello sviluppo si decidesse di implementare una nuova azione di gioco (ad esempio il comando `SPARA` per utilizzare il laser di Buzz Lightyear), non sarà necessario modificare la classe `Engine.java`. Sarà sufficiente creare una nuova classe isolata (es. `ShootObserver.java`), farle implementare l'interfaccia `GameObserver` e registrarla nel motore all'avvio. L'Engine la integrerà ed eseguirà in modo completamente trasparente e automatico.

#### 🗁 `com.toystory.server.impl`

Costituisce il livello dei Controller in cui è racchiusa la logica applicativa dell'avventura.

* **`ToyStoryGame.java`:** Estende `GameDescription`. Istanzia concretamente l'universo di gioco: modella la planimetria della casa di Andy e delle fogne, colloca i passaggi segreti, definisce gli scarafaggi nemici e setta le condizioni di vittoria.
* **Classi Observer (`MoveObserver`, `PickUpObserver`, `UseObserver`, ecc.):** Implementano l'interfaccia `GameObserver`. Ognuna si occupa di convalidare ed eseguire una specifica azione. Ad esempio, `UseObserver` contiene i controlli condizionali per risolvere gli enigmi del gioco (es. verificare se il giocatore possiede un determinato oggetto prima di permettergli di sbloccare un meccanismo della stanza).

#### 🗁 `com.toystory.server.type`

Costituisce il Model del sistema. L'intero pacchetto è stato ingegnerizzato sfruttando l'ereditarietà e il polimorfismo per mappare in modo pulito lo scenario descritto nell'UML:

* **Gerarchia degli Oggetti (`AdvObject`):** La classe base astratta definisce le proprietà comuni a tutti gli elementi fisici. Da essa derivano **`PickupableObject`** (oggetti raccoglibili e inseribili nelle tasche) e **`ContainerObject`** (oggetti fissi dello scenario che fungono da contenitori di altri item, come bauli o armadi).
* **Gerarchia dei Personaggi (`GameCharacter`):** Classe astratta estesa da **`PlayableCharacter`** (i personaggi controllabili come Woody o Buzz, dotati di un inventario locale/tasca per la risoluzione di enigmi specifici del personaggio) e **`NonPlayableCharacter`** (entità secondarie o ostili).
* **`Room.java`:** Modella una stanza dell'avventura, contenente le descrizioni testuali, la lista degli oggetti presenti e i riferimenti direzionali (Nord, Sud, Est, Ovest) verso le stanze adiacenti.
* **`HammInventory.java`:** Rappresenta la pancia del salvadanaio Hamm, adoperato come inventario globale e condiviso della squadra dei giocattoli. È implementato tramite il **Pattern Singleton** per garantire l'esistenza di un unico punto di deposito centralizzato accessibile da tutti i thread.

#### 🗁 `com.toystory.server.database`

Rappresenta il Data Layer per l'accesso e la gestione della persistenza.

* **`DatabaseManager.java`:** Isola l'apertura e la chiusura delle connessioni verso il database relazionale mediante driver JDBC. Al suo interno risiedono le query SQL dedicate a:
1. Salvare e ripristinare lo stato globale della partita (posizione dei giocatori, oggetti raccolti, enigmi superati).
2. Registrare i record di completamento dei giocatori all'interno della tabella della classifica online (**Hall of Fame**), tenendo traccia del tempo impiegato per sconfiggere la banda degli scarafaggi.