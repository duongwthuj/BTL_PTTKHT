-- ============================================
-- TẠO TRIGGERS CHO RESTMAN
-- Chạy file này SAU KHI đã chạy schema.sql
-- ============================================

USE restman_db;

-- Xóa trigger cũ nếu có
DROP TRIGGER IF EXISTS after_reservation_insert;
DROP TRIGGER IF EXISTS after_reservation_update;

-- Trigger 1: Tự động cập nhật trạng thái bàn khi có reservation mới
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
DELIMITER ;

-- Trigger 2: Cập nhật trạng thái bàn khi hủy reservation
DELIMITER //
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

-- Kiểm tra triggers đã tạo
SHOW TRIGGERS FROM restman_db;
