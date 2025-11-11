package dao;

import model.RestaurantTable;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TableDAO {

    public List<RestaurantTable> findAllTablesByDateTime(Date date, Time time) {
        List<RestaurantTable> tables = new ArrayList<>();
        String sql = "SELECT t.*, " +
                     "CASE " +
                     "    WHEN EXISTS (" +
                     "        SELECT 1 FROM reservations r " +
                     "        WHERE r.table_id = t.id " +
                     "        AND r.reservation_date = ? " +
                     "        AND r.reservation_time = ? " +
                     "        AND r.status NOT IN ('cancelled', 'completed')" +
                     "    ) THEN 'reserved' " +
                     "    ELSE 'available' " +
                     "END AS current_status " +
                     "FROM restaurant_tables t " +
                     "ORDER BY t.table_number";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, date);
            stmt.setTime(2, time);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                RestaurantTable table = extractTableFromResultSet(rs);
                table.setStatus(rs.getString("current_status"));
                tables.add(table);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tables;
    }

    private RestaurantTable extractTableFromResultSet(ResultSet rs) throws SQLException {
        RestaurantTable table = new RestaurantTable();
        table.setId(rs.getInt("id"));
        table.setTableNumber(rs.getInt("table_number"));
        table.setCapacity(rs.getInt("capacity"));
        table.setStatus(rs.getString("status"));
        table.setCreatedAt(rs.getTimestamp("created_at"));
        return table;
    }
}