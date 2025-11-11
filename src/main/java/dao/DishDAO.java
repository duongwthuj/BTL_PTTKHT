package dao;

import model.Dish;
import util.DBConnection;

import java.sql.*;

public class DishDAO {

    /**
     * Kiểm tra xem tên món ăn đã tồn tại chưa
     */
    public boolean existsByName(String name) {
        String sql = "SELECT COUNT(*) FROM dishes WHERE LOWER(name) = LOWER(?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name.trim());
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
     * Thêm món ăn mới
     */
    public boolean insert(Dish dish) {
        String sql = "INSERT INTO dishes (name, category, price, description, available) VALUES (?, ?, ?, ?, ?)";
        System.out.println("=== DEBUG: Insert Dish ===");
        System.out.println("Name: " + dish.getName());
        System.out.println("Category: " + dish.getCategory());
        System.out.println("Price: " + dish.getPrice());
        System.out.println("Description: " + dish.getDescription());
        System.out.println("Available: " + dish.isAvailable());
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            System.out.println("Connection: " + (conn != null ? "OK" : "NULL"));
            
            stmt.setString(1, dish.getName());
            stmt.setString(2, dish.getCategory());
            stmt.setBigDecimal(3, dish.getPrice());
            stmt.setString(4, dish.getDescription());
            stmt.setBoolean(5, dish.isAvailable());

            System.out.println("Executing SQL: " + sql);
            int affectedRows = stmt.executeUpdate();
            System.out.println("Affected rows: " + affectedRows);

            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    dish.setId(generatedKeys.getInt(1));
                    System.out.println("Generated ID: " + dish.getId());
                }
                System.out.println("✅ Insert SUCCESS!");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("❌ Insert FAILED!");
        return false;
    }
}
