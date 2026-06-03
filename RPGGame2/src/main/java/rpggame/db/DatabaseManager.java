package rpggame.db;

import java.sql.*;

/**
 * Singleton that manages the Apache Derby embedded database connection.
 * Automatically creates the database and tables on first run.
 * Design Pattern: Singleton
 */
public class DatabaseManager {

    private static DatabaseManager instance;
    private static final String DB_URL = "jdbc:derby:rpgGameDB;create=true";

    private DatabaseManager() {
        initialise();
    }

    /** Returns the single instance (thread-safe via synchronized). */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /** Opens and returns a fresh connection to the embedded Derby DB. */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    /** Creates all tables if they do not already exist. */
    private void initialise() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Players table
            stmt.execute("""
                CREATE TABLE players (
                    id          INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                    name        VARCHAR(50)  NOT NULL,
                    player_class VARCHAR(20) NOT NULL,
                    level       INT DEFAULT 1,
                    health      INT NOT NULL,
                    max_health  INT NOT NULL,
                    attack      INT NOT NULL,
                    defense     INT NOT NULL,
                    gold        INT DEFAULT 50,
                    experience  INT DEFAULT 0,
                    dungeon_floor INT DEFAULT 1,
                    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // Battle log table
            stmt.execute("""
                CREATE TABLE battle_log (
                    id          INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                    player_id   INT REFERENCES players(id),
                    enemy_name  VARCHAR(50),
                    outcome     VARCHAR(10),  -- WIN / LOSS / FLEE
                    floor       INT,
                    gold_earned INT DEFAULT 0,
                    xp_earned   INT DEFAULT 0,
                    logged_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // Shop transactions table
            stmt.execute("""
                CREATE TABLE shop_transactions (
                    id          INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                    player_id   INT REFERENCES players(id),
                    item_name   VARCHAR(50),
                    cost        INT,
                    purchased_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            System.out.println("[DB] Tables created successfully.");

        } catch (SQLException e) {
            // Tables already exist — expected on subsequent runs
            if (!e.getSQLState().startsWith("X0Y32")) {
                System.err.println("[DB] Init warning: " + e.getMessage());
            }
        }
    }

    /** Gracefully shuts down the Derby engine. */
    public void shutdown() {
        try {
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException e) {
            // Derby always throws SQLState 'XJ015' on clean shutdown — expected
            if (!"XJ015".equals(e.getSQLState())) {
                System.err.println("[DB] Shutdown error: " + e.getMessage());
            }
        }
    }
}
