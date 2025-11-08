package dao;

import model.Dish;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DishDAO {

    /**
     * Lấy tất cả món ăn
     */
    public List<Dish> findAll() {
        List<Dish> dishes = new ArrayList<>();
        String sql = "SELECT * FROM dishes ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                dishes.add(extractDishFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dishes;
    }

    /**
     * Lấy món ăn theo ID
     */
    public Dish findById(int id) {
        String sql = "SELECT * FROM dishes WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractDishFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lấy món ăn theo danh mục
     */
    public List<Dish> findByCategory(String category) {
        List<Dish> dishes = new ArrayList<>();
        String sql = "SELECT * FROM dishes WHERE category = ? ORDER BY name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                dishes.add(extractDishFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dishes;
    }

    /**
     * Lấy món ăn available
     */
    public List<Dish> findAvailable() {
        List<Dish> dishes = new ArrayList<>();
        String sql = "SELECT * FROM dishes WHERE available = true ORDER BY category, name";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                dishes.add(extractDishFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dishes;
    }

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
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, dish.getName());
            stmt.setString(2, dish.getCategory());
            stmt.setBigDecimal(3, dish.getPrice());
            stmt.setString(4, dish.getDescription());
            stmt.setBoolean(5, dish.isAvailable());

            int affectedRows = stmt.executeUpdate();
            System.out.println("✅ Inserted " + affectedRows + " row(s) into database");

            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    dish.setId(generatedKeys.getInt(1));
                    System.out.println("✅ Generated ID: " + dish.getId());
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error inserting dish: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật món ăn
     */
    public boolean update(Dish dish) {
        String sql = "UPDATE dishes SET name = ?, category = ?, price = ?, description = ?, available = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dish.getName());
            stmt.setString(2, dish.getCategory());
            stmt.setBigDecimal(3, dish.getPrice());
            stmt.setString(4, dish.getDescription());
            stmt.setBoolean(5, dish.isAvailable());
            stmt.setInt(6, dish.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Xóa món ăn
     */
    public boolean delete(int dishId) {
        String sql = "DELETE FROM dishes WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, dishId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật trạng thái available
     */
    public boolean updateAvailability(int dishId, boolean available) {
        String sql = "UPDATE dishes SET available = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, available);
            stmt.setInt(2, dishId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Tìm kiếm món ăn theo tên
     */
    public List<Dish> searchByName(String keyword) {
        List<Dish> dishes = new ArrayList<>();
        String sql = "SELECT * FROM dishes WHERE name LIKE ? ORDER BY name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                dishes.add(extractDishFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dishes;
    }

    /**
     * Extract Dish object từ ResultSet
     */
    private Dish extractDishFromResultSet(ResultSet rs) throws SQLException {
        Dish dish = new Dish();
        dish.setId(rs.getInt("id"));
        dish.setName(rs.getString("name"));
        dish.setCategory(rs.getString("category"));
        dish.setPrice(rs.getBigDecimal("price"));
        dish.setDescription(rs.getString("description"));
        dish.setAvailable(rs.getBoolean("available"));
        dish.setImageUrl(rs.getString("image_url"));
        dish.setCreatedAt(rs.getTimestamp("created_at"));
        dish.setUpdatedAt(rs.getTimestamp("updated_at"));
        return dish;
    }
}
