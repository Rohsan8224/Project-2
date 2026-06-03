package rpggame.dao;

import rpggame.db.DatabaseManager;
import rpggame.model.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Player entities.
 * Handles all CRUD operations against the players table.
 * Design Pattern: DAO
 */
public class PlayerDAO {

    /** Inserts a new player record and returns the generated ID. */
    public int save(Player player, int floor) {
        String sql = """
            INSERT INTO players (name, player_class, level, health, max_health,
                                 attack, defense, gold, experience, dungeon_floor)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, player.getName());
            ps.setString(2, player.getPlayerClass().name());
            ps.setInt(3, player.getLevel());
            ps.setInt(4, player.getHealth());
            ps.setInt(5, player.getMaxHealth());
            ps.setInt(6, player.getAttack());
            ps.setInt(7, player.getDefense());
            ps.setInt(8, player.getGold());
            ps.setInt(9, player.getExperience());
            ps.setInt(10, floor);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Save player error: " + e.getMessage());
        }
        return -1;
    }

    /** Updates an existing player record. */
    public void update(int playerId, Player player, int floor) {
        String sql = """
            UPDATE players SET level=?, health=?, max_health=?, attack=?,
                               defense=?, gold=?, experience=?, dungeon_floor=?
            WHERE id=?
        """;
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, player.getLevel());
            ps.setInt(2, player.getHealth());
            ps.setInt(3, player.getMaxHealth());
            ps.setInt(4, player.getAttack());
            ps.setInt(5, player.getDefense());
            ps.setInt(6, player.getGold());
            ps.setInt(7, player.getExperience());
            ps.setInt(8, floor);
            ps.setInt(9, playerId);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[DAO] Update player error: " + e.getMessage());
        }
    }

    /** Returns all saved players, newest first. */
    public List<SavedPlayerRecord> findAll() {
        List<SavedPlayerRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM players ORDER BY created_at DESC";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new SavedPlayerRecord(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("player_class"),
                        rs.getInt("level"),
                        rs.getInt("health"),
                        rs.getInt("max_health"),
                        rs.getInt("attack"),
                        rs.getInt("defense"),
                        rs.getInt("gold"),
                        rs.getInt("experience"),
                        rs.getInt("dungeon_floor")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[DAO] FindAll error: " + e.getMessage());
        }
        return list;
    }

    /** Deletes a player and all related records. */
    public void delete(int playerId) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM shop_transactions WHERE player_id=?")) {
                    ps.setInt(1, playerId); ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM battle_log WHERE player_id=?")) {
                    ps.setInt(1, playerId); ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM players WHERE id=?")) {
                    ps.setInt(1, playerId); ps.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Delete player error: " + e.getMessage());
        }
    }

    /** Immutable record returned from DB queries. */
    public record SavedPlayerRecord(
            int id, String name, String playerClass,
            int level, int health, int maxHealth,
            int attack, int defense, int gold,
            int experience, int dungeonFloor) {

        @Override public String toString() {
            return String.format("%s  |  Lv%d %s  |  Floor %d  |  HP %d/%d  |  Gold %d",
                    name, level, playerClass, dungeonFloor, health, maxHealth, gold);
        }
    }
}
