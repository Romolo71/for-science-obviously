# For Science. Obviously.
### Un tower defence ambientato nell'universo di Portal e Half-Life 2

> *"I test continueranno."* — GLaDOS

---

## 📖 Storia

Durante gli eventi di Half-Life 2, i Combine captano un segnale anomalo proveniente dalle profondità del Michigan. La fonte è Aperture Science — ancora operativa, ancora testando, completamente ignara del mondo crollato in superficie.

I Combine mandano ondate di zombie per silenziare il segnale. GLaDOS, assistita da Wheatley, Atlas e Peabody, deve difendere il laboratorio torretta per torretta.

*I test non possono essere interrotti.*

> Per la storia completa vedi [LORE.md](./LORE.md)

---

## 🎮 Gameplay

**For Science. Obviously.** è un tower defence a ondate in cui il giocatore posiziona e potenzia torrette per difendere i laboratori di Aperture Science dall'invasione zombie orchestrata dai Combine.

Ogni difensore offre un tipo di potenziamento diverso:

| Personaggio | Ruolo | Tipo di Upgrade |
|-------------|-------|-----------------|
| **GLaDOS** | Controllo centrale | Posiziona le torrette |
| **Wheatley** | Logistica | Numero e varietà di torrette disponibili |
| **Atlas** | Offensiva | Danno, cadenza di fuoco, raggio |
| **Peabody** | Difensiva | Resistenza torrette, integrità settori |

---

## 🛠️ Tecnologie

- **Linguaggio:** Java
- **UI & Grafica:** JavaFX
- **IDE consigliato:** IntelliJ IDEA
- **Build system:** Maven (`pom.xml`)
- **Java version:** 17+

---

## 📁 Struttura del Progetto

```
for-science-obviously/
│
├── src/
│   └── main/
│       ├── java/
│       │   └── com/forscience/
│       │       │
│       │       ├── Main.java                  # Entry point dell'applicazione
│       │       │
│       │       ├── app/
│       │       │   └── App.java               # Inizializzazione JavaFX
│       │       │
│       │       ├── controller/                # Controller JavaFX (collegati agli FXML)
│       │       │   ├── MenuController.java
│       │       │   ├── GameController.java
│       │       │   └── UpgradeController.java
│       │       │
│       │       ├── model/                     # Logica di gioco
│       │       │   ├── game/
│       │       │   │   ├── GameLoop.java
│       │       │   │   ├── Wave.java          # Gestione ondate zombie
│       │       │   │   └── Map.java           # Mappa e settori
│       │       │   │
│       │       │   ├── entity/
│       │       │   │   ├── Turret.java        # Torretta base
│       │       │   │   ├── Zombie.java        # Zombie base
│       │       │   │   └── Headcrab.java
│       │       │   │
│       │       │   ├── defender/              # I quattro difensori
│       │       │   │   ├── GLaDOS.java
│       │       │   │   ├── Wheatley.java
│       │       │   │   ├── Atlas.java
│       │       │   │   └── Peabody.java
│       │       │   │
│       │       │   └── upgrade/               # Sistema di upgrade
│       │       │       ├── Upgrade.java
│       │       │       ├── AttackUpgrade.java
│       │       │       └── DefenseUpgrade.java
│       │       │
│       │       └── util/                      # Utility e costanti
│       │           ├── Constants.java
│       │           └── AudioManager.java
│       │
│       └── resources/
│           ├── fxml/                          # Layout JavaFX
│           │   ├── menu.fxml
│           │   ├── game.fxml
│           │   └── upgrade.fxml
│           │
│           ├── images/                        # Sprite e grafica
│           │   ├── turrets/
│           │   ├── zombies/
│           │   ├── defenders/
│           │   └── ui/
│           │
│           ├── audio/                         # Musica ed effetti
│           │   ├── music/
│           │   └── sfx/
│           │
│           └── maps/                          # File delle mappe
│               └── aperture_labs.json
│
├── pom.xml                                    # Configurazione Maven + dipendenze JavaFX
└── README.md
```

---

## 🚀 Come Avviare il Progetto

### Prerequisiti
- Java 17 o superiore installato
- IntelliJ IDEA (consigliato)
- Plugin JavaFX configurato in IntelliJ

### Avvio con IntelliJ
1. Clona il repository
   ```bash
   git clone https://github.com/tuousername/for-science-obviously.git
   ```
2. Apri la cartella con IntelliJ IDEA
3. IntelliJ rileverà automaticamente il `pom.xml` e scaricherà le dipendenze
4. Esegui `Main.java` come applicazione Java

### Avvio da terminale
```bash
mvn clean javafx:run
```

---

## 👥 Team

| Nome | Ruolo |
|------|-------|
| — | — |
| — | — |
| — | — |

---

## 📌 Stato del Progetto

🔴 In sviluppo

---

## 📄 Licenza

Progetto sviluppato a scopo didattico. I personaggi e l'universo di Portal e Half-Life appartengono a **Valve Corporation**.
