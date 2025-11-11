package dao;

import model.Reservation;
import util.DBConnection;

import java.sql.*;

public class ReservationDAO {

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
}