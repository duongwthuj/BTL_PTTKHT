-- ============================================
-- RESTMAN DATABASE SCHEMA
-- Hệ thống quản lý nhà hàng
-- 2 chức năng chính: Thêm món ăn + Đặt bàn
-- ============================================

-- XÓA database cũ nếu có (để tạo lại trigger)
DROP DATABASE IF EXISTS restman_db;

-- Tạo database
CREATE DATABASE restman_db
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE restman_db;

-- ============================================
-- 1. BẢNG USERS (Người dùng)
-- ============================================
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    role ENUM('customer', 'manager') NOT NULL DEFAULT 'customer',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 2. BẢNG DISHES (Món ăn)
-- ============================================
CREATE TABLE dishes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    description TEXT,
    available BOOLEAN DEFAULT TRUE,
    image_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_category (category),
    INDEX idx_available (available),
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 3. BẢNG RESTAURANT_TABLES (Bàn ăn)
-- ============================================
CREATE TABLE restaurant_tables (
    id INT PRIMARY KEY AUTO_INCREMENT,
    table_number INT NOT NULL UNIQUE,
    name VARCHAR(50),
    location VARCHAR(100),
    capacity INT NOT NULL,
    status ENUM('available', 'occupied', 'reserved') DEFAULT 'available',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_table_number (table_number),
    INDEX idx_status (status),
    INDEX idx_capacity (capacity)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 4. BẢNG RESERVATIONS (Đặt bàn)
-- Lưu ý: Mỗi reservation chỉ đặt 1 bàn (tableId là INT)
-- ============================================
CREATE TABLE reservations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    table_id INT NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    customer_phone VARCHAR(20) NOT NULL,
    reservation_date DATE NOT NULL,
    reservation_time TIME NOT NULL,
    number_of_guests INT NOT NULL,
    note TEXT,
    status ENUM('pending', 'confirmed', 'cancelled', 'completed') DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (table_id) REFERENCES restaurant_tables(id) ON DELETE CASCADE,
    
    INDEX idx_user_id (user_id),
    INDEX idx_table_id (table_id),
    INDEX idx_reservation_date (reservation_date),
    INDEX idx_status (status),
    INDEX idx_datetime (reservation_date, reservation_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- DỮ LIỆU MẪU (Sample Data)
-- ============================================

-- Thêm user mẫu
INSERT INTO users (username, password, full_name, email, phone, role) VALUES
('admin', 'admin123', 'Quản lý Hệ thống', 'admin@restman.com', '0123456789', 'manager'),
('customer1', 'customer123', 'Nguyễn Văn A', 'nguyenvana@gmail.com', '0987654321', 'customer'),
('customer2', 'customer123', 'Trần Thị B', 'tranthib@gmail.com', '0912345678', 'customer');

-- Thêm món ăn mẫu
INSERT INTO dishes (name, category, price, description, available) VALUES
('Phở Bò', 'Món chính', 85000.00, 'Phở bò truyền thống với nước dùng thơm ngon', TRUE),
('Cơm Tấm Sườn', 'Món chính', 65000.00, 'Cơm tấm sườn nướng đặc biệt', TRUE),
('Bún Bò Huế', 'Món chính', 75000.00, 'Bún bò Huế chuẩn vị', TRUE),
('Gỏi Cuốn', 'Món khai vị', 45000.00, 'Gỏi cuốn tôm thịt tươi ngon', TRUE),
('Nem Rán', 'Món khai vị', 50000.00, 'Nem rán giòn tan', TRUE),
('Chè Ba Màu', 'Món tráng miệng', 30000.00, 'Chè ba màu mát lạnh', TRUE),
('Trà Sữa Trân Châu', 'Đồ uống', 35000.00, 'Trà sữa thơm ngon với trân châu đường đen', TRUE),
('Cà Phê Sữa Đá', 'Đồ uống', 25000.00, 'Cà phê phin truyền thống', TRUE),
('Bánh Mì Thịt Nướng', 'Món chính', 30000.00, 'Bánh mì thịt nướng đặc biệt', TRUE),
('Hủ Tiếu Nam Vang', 'Món chính', 70000.00, 'Hủ tiếu Nam Vang thơm ngon', TRUE);

-- Thêm bàn ăn mẫu
INSERT INTO restaurant_tables (table_number, name, location, capacity, status) VALUES
(1, 'Bàn 01', 'Tầng 1', 4, 'available'),
(2, 'Bàn 02', 'Tầng 1', 4, 'available'),
(3, 'Bàn 03', 'Tầng 1', 6, 'available'),
(4, 'Bàn 04', 'Tầng 1', 6, 'available'),
(5, 'Bàn 05', 'Tầng 1', 4, 'available'),
(6, 'Bàn 06', 'Tầng 2', 8, 'available'),
(7, 'Bàn 07', 'Tầng 2', 4, 'available'),
(8, 'Bàn 08', 'Tầng 2', 8, 'available'),
(9, 'Bàn 09', 'Tầng 2', 4, 'available'),
(10, 'Bàn 10', 'Tầng 2', 6, 'available');

-- Thêm một vài reservation mẫu (để test tìm bàn trống)
INSERT INTO reservations (user_id, table_id, customer_name, customer_phone, reservation_date, reservation_time, number_of_guests, note, status) VALUES
(2, 1, 'Nguyễn Văn A', '0987654321', '2025-11-09', '12:00:00', 4, 'Gần cửa sổ', 'confirmed'),
(3, 3, 'Trần Thị B', '0912345678', '2025-11-09', '19:00:00', 6, 'Yên tĩnh', 'pending');

-- ============================================
-- VIEWS & PROCEDURES (Optional - Tùy chọn)
-- ============================================

-- View: Xem tất cả reservation với thông tin đầy đủ
CREATE OR REPLACE VIEW v_reservations AS
SELECT 
    r.id,
    r.user_id,
    u.username,
    u.full_name AS user_full_name,
    r.table_id,
    t.table_number,
    t.name AS table_name,
    t.location,
    r.customer_name,
    r.customer_phone,
    r.reservation_date,
    r.reservation_time,
    r.number_of_guests,
    r.note,
    r.status,
    r.created_at
FROM reservations r
JOIN users u ON r.user_id = u.id
JOIN restaurant_tables t ON r.table_id = t.id
ORDER BY r.reservation_date DESC, r.reservation_time DESC;

-- View: Xem tất cả món ăn available
CREATE OR REPLACE VIEW v_available_dishes AS
SELECT 
    id,
    name,
    category,
    price,
    description,
    image_url,
    created_at
FROM dishes
WHERE available = TRUE
ORDER BY category, name;

-- ============================================
-- TRIGGERS (Optional - Tùy chọn)
-- ============================================

-- Trigger: Tự động cập nhật trạng thái bàn khi có reservation mới
DELIMITER //
CREATE TRIGGER after_reservation_insert
AFTER INSERT ON reservations
FOR EACH ROW
BEGIN
    -- Cập nhật bàn sang 'reserved' khi có reservation mới (pending hoặc confirmed)
    IF NEW.status IN ('pending', 'confirmed') THEN
        UPDATE restaurant_tables 
        SET status = 'reserved' 
        WHERE id = NEW.table_id;
    END IF;
END//

-- Trigger: Cập nhật trạng thái bàn khi hủy reservation
CREATE TRIGGER after_reservation_update
AFTER UPDATE ON reservations
FOR EACH ROW
BEGIN
    IF NEW.status = 'cancelled' AND OLD.status != 'cancelled' THEN
        -- Kiểm tra xem bàn còn reservation nào khác không
        IF NOT EXISTS (
            SELECT 1 FROM reservations 
            WHERE table_id = NEW.table_id 
            AND status IN ('pending', 'confirmed')
            AND id != NEW.id
        ) THEN
            UPDATE restaurant_tables 
            SET status = 'available' 
            WHERE id = NEW.table_id;
        END IF;
    END IF;
END//
DELIMITER ;

-- ============================================
-- NOTES (Ghi chú quan trọng)
-- ============================================
-- 1. Password trong users chưa được hash - cần implement BCrypt trong Java
-- 2. Mỗi reservation CHỈ ĐẶT 1 BÀN DUY NHẤT (table_id là INT)
-- 3. Status của reservation: pending → confirmed → completed (hoặc cancelled)
-- 4. Khi tìm bàn trống, query theo reservation_date, reservation_time và status
