package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Thông tin kết nối database
    private static final String URL = "jdbc:mysql://localhost:3306/restman_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    // Singleton connection
    private static Connection connection = null;

    // Private constructor để ngăn khởi tạo từ bên ngoài
    private DBConnection() {
    }

    /**
     * Lấy connection đến database
     * @return Connection object
     * @throws SQLException nếu không thể kết nối
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Tạo connection mới nếu chưa có hoặc đã đóng
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Database connected successfully!");
            }

            return connection;
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
            throw new SQLException("Driver not found", e);
        } catch (SQLException e) {
            System.err.println("Connection failed!");
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Đóng connection
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection!");
            e.printStackTrace();
        }
    }

    /**
     * Test connection
     */
    public static void main(String[] args) {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                System.out.println("Test connection successful!");
                closeConnection();
            }
        } catch (SQLException e) {
            System.err.println("Test connection failed!");
            e.printStackTrace();
        }
    }
}
