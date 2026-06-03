package rpggame.dao;

import rpggame.db.DatabaseManager;

import java.sql.*;

/**
 * DAO for recording shop purchase transactions.
 */
public class ShopDAO {

    public void recordPurchase(int playerId, String itemName, int cost) {
        String sql = "INSERT INTO shop_transactions (player_id, item_name, cost) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            ps.setString(2, itemName);
            ps.setInt(3, cost);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[DAO] ShopDAO error: " + e.getMessage());
        }
    }
}
