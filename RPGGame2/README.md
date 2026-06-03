# Dungeon Quest RPG — COMP603/ENSE600 Project 2

A turn-based RPG dungeon-crawler. Project 2 extends the Project 1 design into a
**Java Swing GUI** application backed by an **Apache Derby embedded database**
(JDBC + DAO), with a JUnit 4 test layer and a layered MVC architecture.

## Running the project
This is a **Maven** project. It opens directly in NetBeans with no manual setup.

- **NetBeans:** *File → Open Project* → select the `RPGGame2` folder → press **Run** (F6).
- **Command line:** `mvn clean compile exec:java` *(or)* `mvn clean package` then run the jar.

**JDK:** 25 &nbsp;|&nbsp; **Main class:** `rpggame.gui.MainApp`

No login credentials are required. The Apache Derby database (`rpgGameDB/`) is
created automatically on first launch via `create=true` — nothing to configure,
start, or import.

## Testing
`mvn test` runs the JUnit 4 suite (business-logic and database tests):
`PlayerTest`, `EnemyTest`, `ItemTest`, `InventoryTest`, `CombatEngineTest`,
`DatabaseTest`.

## Package structure
| Package          | Layer            | Responsibility                                   |
|------------------|------------------|--------------------------------------------------|
| `rpggame.model`  | Domain           | Entities, items, inventory (encapsulated OOP)    |
| `rpggame.gui`    | View             | Swing panels + `MainApp` (CardLayout mediator)   |
| `rpggame.service`| Controller       | `GameService` facade + `GameEventListener` (Observer) |
| `rpggame.dao`    | Data access      | `PlayerDAO`, `BattleLogDAO`, `ShopDAO` (DAO)      |
| `rpggame.db`     | Persistence      | `DatabaseManager` (Singleton, JDBC)              |
| `rpggame.util`   | Utilities        | `CombatEngine`, `EnemyFactory` (Factory)         |
| `rpggame.data`   | Static data      | `ShopData` (shop catalogue)                      |

## Design patterns
Singleton (`DatabaseManager`), DAO (`*DAO`), Factory (`EnemyFactory`),
MVC (model / service / view), Mediator (`MainApp`), Observer (`GameEventListener`).
