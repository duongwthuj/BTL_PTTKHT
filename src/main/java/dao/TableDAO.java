package dao;

import model.RestaurantTable;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TableDAO {

    /**
     * Lấy tất cả bàn
     */
    public List<RestaurantTable> findAll() {
        List<RestaurantTable> tables = new ArrayList<>();
        String sql = "SELECT * FROM restaurant_tables ORDER BY table_number";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                tables.add(extractTableFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tables;
    }

    /**
     * Lấy bàn theo ID
     */
    public RestaurantTable findById(int id) {
        String sql = "SELECT * FROM restaurant_tables WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractTableFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lấy bàn theo số bàn
     */
    public RestaurantTable findByTableNumber(int tableNumber) {
        String sql = "SELECT * FROM restaurant_tables WHERE table_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tableNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractTableFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lấy bàn theo trạng thái
     */
    public List<RestaurantTable> findByStatus(String status) {
        List<RestaurantTable> tables = new ArrayList<>();
        String sql = "SELECT * FROM restaurant_tables WHERE status = ? ORDER BY table_number";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tables.add(extractTableFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tables;
    }

    /**
     * Lấy bàn trống
     */
    public List<RestaurantTable> findAvailableTables() {
        return findByStatus("available");
    }

    /**
     * Lấy bàn theo capacity
     */
    public List<RestaurantTable> findByCapacity(int capacity) {
        List<RestaurantTable> tables = new ArrayList<>();
        String sql = "SELECT * FROM restaurant_tables WHERE capacity >= ? AND status = 'available' ORDER BY capacity, table_number";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, capacity);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tables.add(extractTableFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tables;
    }

    /**
     * Lấy tất cả bàn và đánh dấu trạng thái dựa trên ngày/giờ đặt
     * Trả về tất cả bàn, nhưng set status = 'reserved' cho bàn đã có người đặt
     */
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
                // Override status với trạng thái thực tế tại thời điểm đặt
                table.setStatus(rs.getString("current_status"));
                tables.add(table);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tables;
    }
    
    /**
     * Lấy bàn trống theo ngày và giờ (chỉ trả về bàn available)
     * Kiểm tra cả status của bàn VÀ không có reservation trùng ngày/giờ
     */
    public List<RestaurantTable> findAvailableTablesByDateTime(Date date, Time time) {
        List<RestaurantTable> tables = new ArrayList<>();
        String sql = "SELECT t.* FROM restaurant_tables t " +
                     "WHERE t.status = 'available' " +
                     "AND t.id NOT IN (" +
                     "    SELECT table_id FROM reservations " +
                     "    WHERE reservation_date = ? " +
                     "    AND reservation_time = ? " +
                     "    AND status NOT IN ('cancelled', 'completed')" +
                     ") " +
                     "ORDER BY t.table_number";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, date);
            stmt.setTime(2, time);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tables.add(extractTableFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tables;
    }

    /**
     * Lấy bàn trống trong khoảng thời gian ±3 giờ (theo phong cách dự án cũ)
     * Sử dụng cho reservation system với ReservationDetail
     */
    public List<RestaurantTable> getAvailableTables(Timestamp bookingTime) {
        List<RestaurantTable> tables = new ArrayList<>();
        String sql = "SELECT t.* FROM restaurant_tables t " +
                     "WHERE t.id NOT IN (" +
                     "    SELECT rd.table_id " +
                     "    FROM reservation_details rd " +
                     "    JOIN reservations r ON rd.reservation_id = r.id " +
                     "    WHERE TIMESTAMP(r.reservation_date, r.reservation_time) >= DATE_SUB(?, INTERVAL 3 HOUR) " +
                     "    AND TIMESTAMP(r.reservation_date, r.reservation_time) <= DATE_ADD(?, INTERVAL 3 HOUR) " +
                     "    AND r.status NOT IN ('cancelled', 'completed')" +
                     ") " +
                     "ORDER BY t.table_number";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, bookingTime);
            stmt.setTimestamp(2, bookingTime);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tables.add(extractTableFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tables;
    }

    /**
     * Thêm bàn mới
     */
    public boolean insert(RestaurantTable table) {
        String sql = "INSERT INTO restaurant_tables (table_number, name, location, capacity, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, table.getTableNumber());
            stmt.setString(2, table.getName());
            stmt.setString(3, table.getLocation());
            stmt.setInt(4, table.getCapacity());
            stmt.setString(5, table.getStatus());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    table.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật bàn
     */
    public boolean update(RestaurantTable table) {
        String sql = "UPDATE restaurant_tables SET table_number = ?, name = ?, location = ?, capacity = ?, status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, table.getTableNumber());
            stmt.setString(2, table.getName());
            stmt.setString(3, table.getLocation());
            stmt.setInt(4, table.getCapacity());
            stmt.setString(5, table.getStatus());
            stmt.setInt(6, table.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật trạng thái bàn
     */
    public boolean updateStatus(int tableId, String status) {
        String sql = "UPDATE restaurant_tables SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, tableId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Xóa bàn
     */
    public boolean delete(int tableId) {
        String sql = "DELETE FROM restaurant_tables WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tableId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Kiểm tra bàn có trống không
     */
    public boolean isTableAvailable(int tableId) {
        String sql = "SELECT status FROM restaurant_tables WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tableId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return "available".equals(rs.getString("status"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Đếm số bàn theo trạng thái
     */
    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM restaurant_tables WHERE status = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Extract RestaurantTable object từ ResultSet
     */
    private RestaurantTable extractTableFromResultSet(ResultSet rs) throws SQLException {
        RestaurantTable table = new RestaurantTable();
        table.setId(rs.getInt("id"));
        table.setTableNumber(rs.getInt("table_number"));

        // Hỗ trợ name và location (có thể null trong DB cũ)
        try {
            table.setName(rs.getString("name"));
            table.setLocation(rs.getString("location"));
        } catch (SQLException e) {
            // Nếu column không tồn tại, bỏ qua
            table.setName(null);
            table.setLocation(null);
        }

        table.setCapacity(rs.getInt("capacity"));
        table.setStatus(rs.getString("status"));
        table.setCreatedAt(rs.getTimestamp("created_at"));
        return table;
    }
}
