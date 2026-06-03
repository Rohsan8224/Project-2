package rpggame.dao;

import rpggame.db.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for battle log records — tracks every combat encounter.
 */
public class BattleLogDAO {

    public void log(int playerId, String enemyName, String outcome,
                    int floor, int goldEarned, int xpEarned) {
        String sql = """
            INSERT INTO battle_log (player_id, enemy_name, outcome, floor, gold_earned, xp_earned)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            ps.setString(2, enemyName);
            ps.setString(3, outcome);
            ps.setInt(4, floor);
            ps.setInt(5, goldEarned);
            ps.setInt(6, xpEarned);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[DAO] BattleLog error: " + e.getMessage());
        }
    }

    public List<String> getLogForPlayer(int playerId) {
        List<String> entries = new ArrayList<>();
        String sql = """
            SELECT enemy_name, outcome, floor, gold_earned, xp_earned, logged_at
            FROM battle_log WHERE player_id=? ORDER BY logged_at DESC
        """;
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    entries.add(String.format("[Floor %d] vs %s — %s  (+%d gold, +%d XP)  @ %s",
                            rs.getInt("floor"),
                            rs.getString("enemy_name"),
                            rs.getString("outcome"),
                            rs.getInt("gold_earned"),
                            rs.getInt("xp_earned"),
                            rs.getTimestamp("logged_at").toString().substring(0, 16)));
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO] GetLog error: " + e.getMessage());
        }
        return entries;
    }
}
