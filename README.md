# 🍽️ RESTMAN - Hệ Thống Quản Lý Nhà Hàng

## 📋 Tổng Quan

**Restman** là hệ thống quản lý nhà hàng đơn giản được xây dựng bằng **Java Servlet, JSP và MySQL**.

### ✨ Chức Năng Chính (2 Module)

1. **Thêm Món Ăn** (Manager)
   - Quản lý thông tin món ăn
   - Thêm, sửa, xóa món ăn trong thực đơn

2. **Đặt Bàn Trực Tuyến** (Customer)
   - Tìm kiếm bàn trống theo ngày/giờ
   - Đặt bàn trực tuyến (1 bàn/lần đặt)

---

## 🔧 Công Nghệ Sử Dụng

- **Backend:** Java Servlet 4.0, JSP
- **Database:** MySQL 8.0
- **Build Tool:** Maven
- **Server:** Apache Tomcat 9.x / GlassFish 5.x
- **Frontend:** Bootstrap 5.3, Font Awesome 6.4

---

## 📦 Cài Đặt & Chạy Dự Án

### Bước 1: Chuẩn bị môi trường

**Yêu cầu:**
- Java JDK 8+
- MySQL 8.0+
- Apache Maven 3.6+
- Apache Tomcat 9+ hoặc GlassFish 5+

### Bước 2: Tạo Database

```powershell
# Mở MySQL Command Line hoặc MySQL Workbench
mysql -u root -p

# Chạy file schema.sql
source D:/Năm\ 4/phân\ tích\ thiết\ kế\ hệ\ thống/Restman/schema.sql

# Hoặc copy-paste nội dung schema.sql vào MySQL Workbench và Execute
```

### Bước 3: Cấu hình Database Connection

Mở file `src/main/java/util/DBConnection.java` và cập nhật:

```java
private static final String URL = "jdbc:mysql://localhost:3306/restman_db";
private static final String USERNAME = "root";
private static final String PASSWORD = "your_password_here"; // Thay bằng password MySQL của bạn
```

### Bước 4: Build Project

```powershell
# Di chuyển đến thư mục project
cd "D:\Năm 4\phân tích thiết kế hệ thống\Restman"

# Clean và compile
mvn clean compile

# Tạo file WAR
mvn clean package
```

### Bước 5: Deploy lên Server

**Cách 1: Sử dụng IDE (NetBeans/IntelliJ/Eclipse)**
- Import project dạng Maven
- Cấu hình Tomcat/GlassFish server
- Run project (F6 trong NetBeans)

**Cách 2: Deploy thủ công**
```powershell
# Copy file WAR vào Tomcat
copy target\Restman-1.0-SNAPSHOT.war C:\path\to\tomcat\webapps\

# Start Tomcat
C:\path\to\tomcat\bin\startup.bat
```

### Bước 6: Truy cập ứng dụng

Mở trình duyệt và truy cập:
```
http://localhost:8080/Restman-1.0-SNAPSHOT/
```

---

## 👥 Tài Khoản Mẫu

### Quản lý (Manager)
- **Username:** admin
- **Password:** admin123
- **Chức năng:** Quản lý món ăn, xem đơn đặt bàn

### Khách hàng (Customer)
- **Username:** customer1
- **Password:** customer123
- **Chức năng:** Đặt bàn trực tuyến

---

## 📊 Cấu Trúc Database

### 1. Bảng `users`
| Field | Type | Description |
|-------|------|-------------|
| id | INT | Primary Key |
| username | VARCHAR(50) | Tên đăng nhập (unique) |
| password | VARCHAR(255) | Mật khẩu (cần hash) |
| full_name | VARCHAR(100) | Họ tên |
| email | VARCHAR(100) | Email |
| phone | VARCHAR(20) | Số điện thoại |
| role | ENUM | 'customer' hoặc 'manager' |
| created_at | TIMESTAMP | Ngày tạo |

### 2. Bảng `dishes`
| Field | Type | Description |
|-------|------|-------------|
| id | INT | Primary Key |
| name | VARCHAR(100) | Tên món ăn |
| category | VARCHAR(50) | Danh mục |
| price | DECIMAL(10,2) | Giá |
| description | TEXT | Mô tả |
| available | BOOLEAN | Còn phục vụ? |
| image_url | VARCHAR(255) | URL hình ảnh |
| created_at | TIMESTAMP | Ngày tạo |
| updated_at | TIMESTAMP | Ngày cập nhật |

### 3. Bảng `restaurant_tables`
| Field | Type | Description |
|-------|------|-------------|
| id | INT | Primary Key |
| table_number | INT | Số bàn (unique) |
| name | VARCHAR(50) | Tên bàn |
| location | VARCHAR(100) | Vị trí |
| capacity | INT | Sức chứa |
| status | ENUM | 'available', 'occupied', 'reserved' |
| created_at | TIMESTAMP | Ngày tạo |

### 4. Bảng `reservations`
| Field | Type | Description |
|-------|------|-------------|
| id | INT | Primary Key |
| user_id | INT | FK → users.id |
| **table_id** | **INT** | **FK → restaurant_tables.id (CHỈ 1 BÀN)** |
| customer_name | VARCHAR(100) | Tên khách |
| customer_phone | VARCHAR(20) | SĐT khách |
| reservation_date | DATE | Ngày đặt |
| reservation_time | TIME | Giờ đặt |
| number_of_guests | INT | Số người |
| note | TEXT | Ghi chú |
| status | ENUM | 'pending', 'confirmed', 'cancelled', 'completed' |
| created_at | TIMESTAMP | Ngày tạo |

---

## 🚨 LỖI THIẾT KẾ ĐÃ SỬA

### ❌ Vấn đề ban đầu:
- **Thiết kế phức tạp:** Ban đầu có model ReservationDetail cho phép đặt nhiều bàn (many-to-many)
- **Không cần thiết:** Yêu cầu chỉ cần đặt 1 bàn/lần

### ✅ Giải pháp:
- **Đã xóa bỏ:** ReservationDetail.java và ReservationDetailDAO.java
- **Thiết kế đơn giản:** Reservation.tableId là INT → CHỈ ĐẶT 1 BÀN DUY NHẤT
- **Database schema rõ ràng:** FOREIGN KEY trực tiếp từ reservations.table_id → restaurant_tables.id

### 📝 Business Logic:
1. Khách chọn ngày/giờ → Hệ thống tìm bàn trống
2. Khách chọn **1 BÀN DUY NHẤT** từ danh sách
3. Điền thông tin → Hệ thống lưu 1 reservation với 1 tableId
4. **KHÔNG HỖ TRỢ** đặt nhiều bàn cùng lúc (theo yêu cầu)

---

## 📂 Cấu Trúc Thư Mục

```
Restman/
├── src/
│   └── main/
│       ├── java/
│       │   ├── dao/              # Data Access Object
│       │   │   ├── DishDAO.java
│       │   │   ├── ReservationDAO.java
│       │   │   ├── TableDAO.java
│       │   │   └── UserDAO.java
│       │   ├── model/            # Entity Models
│       │   │   ├── Dish.java
│       │   │   ├── Reservation.java  ← tableId: INT (1 bàn)
│       │   │   ├── RestaurantTable.java
│       │   │   └── User.java
│       │   ├── servlet/          # Controllers
│       │   │   ├── LoginServlet.java
│       │   │   ├── RegisterServlet.java
│       │   │   ├── ManagerCreateDishServlet.java
│       │   │   └── CustomerSelectTableServlet.java
│       │   └── util/             # Utilities
│       │       ├── DBConnection.java
│       │       └── DatabaseTest.java
│       └── webapp/
│           ├── index.jsp
│           ├── css/
│           │   └── style.css
│           └── WEB-INF/
│               ├── web.xml
│               ├── login.jsp
│               ├── register.jsp
│               ├── customer/
│               │   └── ISelectAvailbleTable.jsp
│               └── manager/
│                   ├── ICreateNewDish.jsp
│                   └── IManageDish.jsp
├── schema.sql                    # Database schema
├── pom.xml                       # Maven config
└── README.md                     # File này
```

---

## 🔄 Quy Trình Sử Dụng

### 1️⃣ Chức năng THÊM MÓN (Manager)

```
1. Đăng nhập với tài khoản manager (admin/admin123)
2. Chọn "Quản lý món ăn"
3. Click "Thêm món mới"
4. Điền thông tin:
   - Tên món ăn (*)
   - Danh mục (*): Món chính/Món khai vị/Món tráng miệng/Đồ uống
   - Giá (*)
   - Mô tả
   - Trạng thái: Còn phục vụ (checkbox)
5. Click "Lưu món ăn"
6. Hệ thống báo thành công và hiển thị trong danh sách
```

### 2️⃣ Chức năng ĐẶT BÀN (Customer)

```
1. Đăng nhập với tài khoản customer (customer1/customer123)
2. Chọn "Đặt bàn"
3. Chọn ngày và khung giờ
4. Click "Xem bàn trống"
5. Hệ thống hiển thị danh sách bàn:
   - Màu xanh: Bàn trống (có thể đặt)
   - Màu đỏ: Đã đặt (không khả dụng)
6. Click vào 1 BÀN trống
7. Điền thông tin:
   - Họ tên (*)
   - Số điện thoại (*)
   - Số người
   - Ghi chú
8. Click "Xác nhận đặt bàn"
9. Hệ thống lưu reservation vào DB và hiển thị thông tin đặt bàn
```

---

## 🧪 Testing

### Test Database Connection

```powershell
# Compile project
mvn clean compile

# Run DatabaseTest
mvn exec:java -Dexec.mainClass="util.DatabaseTest"
```

### Test Manual

1. **Test thêm món:**
   - Login: admin/admin123
   - Tạo món mới với đầy đủ thông tin
   - Kiểm tra database: `SELECT * FROM dishes;`

2. **Test đặt bàn:**
   - Login: customer1/customer123
   - Chọn ngày mai, giờ 19:00
   - Đặt bàn 01
   - Kiểm tra database: `SELECT * FROM reservations;`

---

## ⚠️ Lưu Ý Quan Trọng

1. **Security:**
   - Password chưa được hash (cần implement BCrypt)
   - Chưa có CSRF protection
   - Session timeout: 30 phút

2. **Business Rules:**
   - **Mỗi reservation CHỈ ĐẶT 1 BÀN DUY NHẤT**
   - Không cho phép đặt bàn quá khứ
   - Không kiểm tra trùng lặp khung giờ (cần implement)

3. **Database:**
   - Cần tạo index cho performance
   - Triggers tự động cập nhật trạng thái bàn
   - View để query dễ dàng

4. **Deployment:**
   - Context path: `/Restman-1.0-SNAPSHOT`
   - Port: 8080 (default Tomcat)
   - Encoding: UTF-8

---

## 📝 TODO - Các Bước Tiếp Theo

- [x] Tạo Database Schema SQL
- [ ] Hoàn thiện CustomerSelectTableServlet
- [ ] Tạo API endpoint lấy bàn trống
- [ ] Tạo API endpoint lưu reservation
- [ ] Cập nhật JSP - Gọi API thật thay vì dữ liệu giả
- [ ] Testing chức năng Đặt bàn
- [ ] Testing chức năng Thêm món
- [ ] Thêm validation
- [ ] Implement password hashing
- [ ] Deploy lên server

---

## 📞 Liên Hệ & Hỗ Trợ

- **Project:** Đồ án Phân tích Thiết kế Hệ thống
- **Version:** 1.0-SNAPSHOT
- **Date:** November 2025

---

## 📄 License

This project is for educational purposes only.

---

**Happy Coding! 🚀**
