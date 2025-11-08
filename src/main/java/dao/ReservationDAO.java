package dao;

import model.Reservation;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    /**
     * Lấy tất cả reservations
     */
    public List<Reservation> findAll() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations ORDER BY reservation_date DESC, reservation_time DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                reservations.add(extractReservationFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    /**
     * Lấy reservation theo ID
     */
    public Reservation findById(int id) {
        String sql = "SELECT * FROM reservations WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractReservationFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lấy reservations theo user ID
     */
    public List<Reservation> findByUserId(int userId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE user_id = ? ORDER BY reservation_date DESC, reservation_time DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                reservations.add(extractReservationFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    /**
     * Lấy reservations theo bàn
     */
    public List<Reservation> findByTableId(int tableId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE table_id = ? ORDER BY reservation_date DESC, reservation_time DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tableId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                reservations.add(extractReservationFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    /**
     * Lấy reservations theo trạng thái
     */
    public List<Reservation> findByStatus(String status) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE status = ? ORDER BY reservation_date DESC, reservation_time DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                reservations.add(extractReservationFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    /**
     * Lấy reservations theo ngày
     */
    public List<Reservation> findByDate(Date date) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE reservation_date = ? ORDER BY reservation_time";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, date);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                reservations.add(extractReservationFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    /**
     * Lấy reservations theo ngày và giờ
     */
    public List<Reservation> findByDateTime(Date date, Time time) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE reservation_date = ? AND reservation_time = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, date);
            stmt.setTime(2, time);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                reservations.add(extractReservationFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    /**
     * Kiểm tra bàn đã được đặt chưa
     */
    public boolean isTableReserved(int tableId, Date date, Time time) {
        String sql = "SELECT COUNT(*) FROM reservations WHERE table_id = ? AND reservation_date = ? AND reservation_time = ? AND status NOT IN ('cancelled', 'completed')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tableId);
            stmt.setDate(2, date);
            stmt.setTime(3, time);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Thêm reservation mới
     */
    public boolean insert(Reservation reservation) {
        String sql = "INSERT INTO reservations (user_id, table_id, customer_name, customer_phone, reservation_date, reservation_time, number_of_guests, note, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, reservation.getUserId());
            stmt.setInt(2, reservation.getTableId());
            stmt.setString(3, reservation.getCustomerName());
            stmt.setString(4, reservation.getCustomerPhone());
            stmt.setDate(5, reservation.getReservationDate());
            stmt.setTime(6, reservation.getReservationTime());
            stmt.setInt(7, reservation.getNumberOfGuests());
            stmt.setString(8, reservation.getNote());
            stmt.setString(9, reservation.getStatus());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    reservation.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật reservation
     */
    public boolean update(Reservation reservation) {
        String sql = "UPDATE reservations SET user_id = ?, table_id = ?, customer_name = ?, customer_phone = ?, reservation_date = ?, reservation_time = ?, number_of_guests = ?, note = ?, status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reservation.getUserId());
            stmt.setInt(2, reservation.getTableId());
            stmt.setString(3, reservation.getCustomerName());
            stmt.setString(4, reservation.getCustomerPhone());
            stmt.setDate(5, reservation.getReservationDate());
            stmt.setTime(6, reservation.getReservationTime());
            stmt.setInt(7, reservation.getNumberOfGuests());
            stmt.setString(8, reservation.getNote());
            stmt.setString(9, reservation.getStatus());
            stmt.setInt(10, reservation.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật trạng thái reservation
     */
    public boolean updateStatus(int reservationId, String status) {
        String sql = "UPDATE reservations SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, reservationId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Hủy reservation
     */
    public boolean cancel(int reservationId) {
        return updateStatus(reservationId, "cancelled");
    }

    /**
     * Xác nhận reservation
     */
    public boolean confirm(int reservationId) {
        return updateStatus(reservationId, "confirmed");
    }

    /**
     * Hoàn thành reservation
     */
    public boolean complete(int reservationId) {
        return updateStatus(reservationId, "completed");
    }

    /**
     * Xóa reservation
     */
    public boolean delete(int reservationId) {
        String sql = "DELETE FROM reservations WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reservationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Đếm số reservations theo trạng thái
     */
    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM reservations WHERE status = ?";
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
     * Lấy reservations trong khoảng thời gian
     */
    public List<Reservation> findByDateRange(Date startDate, Date endDate) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE reservation_date BETWEEN ? AND ? ORDER BY reservation_date, reservation_time";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                reservations.add(extractReservationFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    /**
     * Extract Reservation object từ ResultSet
     */
    private Reservation extractReservationFromResultSet(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setId(rs.getInt("id"));
        reservation.setUserId(rs.getInt("user_id"));
        reservation.setTableId(rs.getInt("table_id"));
        reservation.setCustomerName(rs.getString("customer_name"));
        reservation.setCustomerPhone(rs.getString("customer_phone"));
        reservation.setReservationDate(rs.getDate("reservation_date"));
        reservation.setReservationTime(rs.getTime("reservation_time"));
        reservation.setNumberOfGuests(rs.getInt("number_of_guests"));
        reservation.setNote(rs.getString("note"));
        reservation.setStatus(rs.getString("status"));
        reservation.setCreatedAt(rs.getTimestamp("created_at"));
        return reservation;
    }
}
