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
```
## 3. Descrizione Dettagliata dei Pacchetti e delle Componenti

Al fine di garantire modularità, manutenibilità e rispetto del principio di singola responsabilità (Single Responsibility Principle), il codice è stato suddiviso in pacchetti logici ben definiti. Di seguito viene analizzato il ruolo di ciascuna sezione dell'architettura.

---

### 📂 Root del Progetto (Risorse e Configurazione)
* **resources/**: È la cartella destinata agli asset statici del gioco. Contiene il file `stopwords`, utilizzato dal parser per eliminare gli elementi grammaticali irrilevanti (articoli, preposizioni) dall'input dell'utente. In futuro ospiterà anche file di testo/JSON per i dialoghi e le risorse multimediali (immagini delle stanze e file audio).

* **pom.xml (Project Object Model)**: Il file di configurazione di Maven. Gestisce in modo centralizzato il ciclo di vita del software e le dipendenze esterne. Sarà fondamentale per includere i driver JDBC per il database e le librerie per la gestione dei flussi di rete e multimediali senza installare file JAR manuali.

* **nbactions.xml & nb-configuration.xml**: File di configurazione nativi dell'ambiente di sviluppo NetBeans. Servono a mappare i comandi dell'IDE (Run, Debug, Build) con i target di Maven, garantendo l'allineamento dell'ambiente di lavoro tra i membri del team.

---

### 📂 Pacchetto Core (`di.uniba.map.b.adventure`)
Contiene le classi strutturali che orchestrano l'intero ciclo di vita dell'applicazione e definiscono lo scheletro comportamentale del gioco.
* **Engine.java**: Il motore del gioco. Gestisce il ciclo principale (Game Loop), riceve l'input dalla View, lo passa al Parser e ne invia l'output al sistema di notifica. Implementa la programmazione concorrente (Thread) per far girare la logica in background ed evitare il congelamento dell'interfaccia grafica.

* **GameDescription.java**: Classe astratta fondamentale che modella lo stato globale del gioco. Tiene traccia della stanza corrente, dello stato dell'inventario, della lista dei comandi ammessi e delle stanze totali. Viene estesa dalla classe specifica del gioco per caricarne la trama.

* **GameObservable.java & GameObserver.java**: Il cuore del design pattern **Observer/Observable**. `GameObservable` mantiene il registro dei listener ed esegue il "dispatching" (notifica) dei comandi. `GameObserver` è l'interfaccia standard che ogni classe di azione deve implementare per ricevere ed elaborare i comandi dell'utente.

* **Utils.java & GameUtils.java**: Classi helper contenenti metodi di utilità generale, come algoritmi per il caricamento di file di testo, la pulizia delle stringhe e la gestione di funzioni di supporto ripetitive.

---

### 📂 Pacchetto `impl/` (Logica di Gioco / Controller Azioni)
Rappresenta l'implementazione pratica del nostro caso di studio. Sfrutta il polimorfismo per eliminare i costrutti condizionali (`switch-case` o `if-else` mastodontici) nella gestione dei comandi.

* **ToyStoryGame.java**: La classe principale del gioco (estensione di `GameDescription`). Nel suo metodo di inizializzazione costruisce la mappa (la casa di Andy, le fogne), crea gli oggetti interattivi, definisce i sinonimi dei comandi e istanzia i personaggi.

* **I vari *Observer (Move, LookAt, PickUp, Open, Push, Use)**: Classi dedicate ciascuna a un singolo comportamento. Ad esempio, `MoveObserver` controlla se esiste un passaggio nella direzione richiesta e sposta il giocatore; `UseObserver` verifica se un oggetto dell'inventario può interagire con un elemento della stanza. Questo approccio rende il gioco facilmente estendibile con nuove azioni.

---

### 📂 Pacchetto `parser/` (Analisi Sintattica)
Si occupa della traduzione del linguaggio naturale digitato dall'utente in comandi comprensibili dal software.

* **Parser.java**: Prende la stringa grezza inserita dal giocatore, la scompone in token (parole singole), confronta i token con la lista delle `stopwords` per rimuovere il "rumore" grammaticale e infine cerca la corrispondenza tra le parole rimaste e i comandi/oggetti definiti nel gioco.

* **ParserOutput.java**: Un oggetto contenitore (Data Transfer Object) che incapsula il risultato del parsing. Restituisce all'Engine una struttura pulita del tipo: *Azione individuata* (es. `CommandType.PICK_UP`) e *Oggetto target* (es. l'oggetto Chiave), pronti per essere passati agli Observer.

---

### 📂 Pacchetto `type/` (Componenti del Modello / Model)
Contiene le classi di tipo "entità" che definiscono la struttura dati degli elementi del mondo di gioco.
* **Room.java**: Rappresenta la singola stanza. Contiene la descrizione testuale dell'ambiente, i riferimenti adiacenti (Nord, Sud, Est, Ovest) e la lista degli oggetti presenti al suo interno in quel momento.

* **AdvObject.java**: Modella gli oggetti di gioco (Adventure Object). Possiede attributi come nome, descrizione, alias (sinonimi), e flag booleani per stabilire se l'oggetto è raccoglibile, invisibile o richiede interazioni particolari.

* **AdvObjectContainer.java**: Sottoclasse di `AdvObject`. Rappresenta oggetti speciali che possono contenere altri oggetti (es. un cassetto nella cucina, un baule dei giochi), permettendo strutture dati gerarchiche.

* **Inventory.java**: Gestisce la collezione di oggetti capitanata dal giocatore, offrendo metodi per aggiungere, rimuovere o scansionare gli strumenti a disposizione.

* **Command.java & CommandType.java**: `CommandType` è l'enumerazione (Enum) di tutte le azioni di sistema possibili. `Command` associa a ciascun tipo una stringa identificativa e una serie di array di stringhe (alias) per far sì che il gioco capisca espressioni diverse per la stessa azione (es. "prendi", "raccogli", "afferra").

---

### 📂 Pacchetto `view/` (Interfaccia Grafica / View)
Adempie al compito di mostrare visivamente lo stato del gioco e raccogliere gli input, sostituendo l'interfaccia standard a riga di comando.
* **GameWindow.java**: Estende la classe `JFrame` di Java Swing. Definisce il layout della finestra principale, i pannelli di testo per la storia, la sezione per l'inventario grafico e la casella di input o i pulsanti di interazione.

* **GUIHandler.java**: Gestisce l'aggiornamento dinamico degli elementi grafici. Viene invocato dall'Engine o dagli Observer per stampare i nuovi testi della storia o modificare le icone a schermo senza ricreare la finestra.

---

### 📂 Pacchetto `database/` (Persistenza dei Dati / Data Layer)
Isola la logica di comunicazione con i sistemi di memorizzazione di massa.
* **DatabaseManager.java**: Si occupa di salvare in modo persistente lo stato della partita corrente (permettendo funzioni di "Salva e Continua") e di gestire la tabella dei record locali (Hall of Fame) con i tempi migliori dei giocatori.