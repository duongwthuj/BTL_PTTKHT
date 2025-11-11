<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thêm món mới - Restman</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        body {
            background: #f5f5f5;
        }
        .form-section {
            background: white;
            border-radius: 16px;
            padding: 40px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            max-width: 800px;
            margin: 0 auto;
        }
    </style>
</head>
<body>
    <!-- Navbar -->
    <nav class="navbar navbar-expand-lg navbar-custom">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/manager/menu">
                <i class="fas fa-utensils"></i>
                <span>Restman - Quản lý</span>
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/manager/menu">
                            <i class="fas fa-home"></i> Trang chủ
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/manager/dishes/create">
                            <i class="fas fa-plus-circle"></i> Thêm món ăn
                        </a>
                    </li>
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button" data-bs-toggle="dropdown">
                            <i class="fas fa-user-circle"></i>
                            <%
                                String username = (String) session.getAttribute("username");
                                out.print(username != null ? username : "Admin");
                            %>
                        </a>
                        <ul class="dropdown-menu">
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout">
                                <i class="fas fa-sign-out-alt"></i> Đăng xuất
                            </a></li>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <!-- Main Content -->
    <div class="container container-custom">
        <div class="mb-4">
            <a href="${pageContext.request.contextPath}/manager/menu" class="text-decoration-none">
                <i class="fas fa-arrow-left me-2"></i>Quay lại trang chủ
            </a>
        </div>

        <div class="form-section">
            <h2 class="mb-4">
                <i class="fas fa-plus-circle me-2"></i>Thêm món ăn mới
            </h2>

            <% if(request.getAttribute("success") != null) { %>
                <div class="alert alert-success alert-custom fade-in" role="alert">
                    <i class="fas fa-check-circle"></i>
                    <span><%= request.getAttribute("success") %></span>
                </div>
            <% } %>

            <% if(request.getAttribute("error") != null) { %>
                <div class="alert alert-danger alert-custom fade-in" role="alert">
                    <i class="fas fa-exclamation-circle"></i>
                    <span><%= request.getAttribute("error") %></span>
                </div>
            <% } %>

            <%
                String name = (String) request.getAttribute("name");
                String category = (String) request.getAttribute("category");
                String price = (String) request.getAttribute("price");
                String description = (String) request.getAttribute("description");
                String available = (String) request.getAttribute("available");
            %>

            <form action="${pageContext.request.contextPath}/manager/dishes/create" method="post">
                <div class="mb-3">
                    <label class="form-label-custom">
                        <i class="fas fa-utensils me-2"></i>Tên món ăn *
                    </label>
                    <input type="text" class="form-control form-control-custom" name="name"
                           placeholder="Nhập tên món ăn" value="<%= name != null ? name : "" %>" required>
                </div>

                <div class="mb-3">
                    <label class="form-label-custom">
                        <i class="fas fa-list me-2"></i>Danh mục *
                    </label>
                    <select class="form-control form-control-custom" name="category" required>
                        <option value="">Chọn danh mục</option>
                        <option value="Món chính" <%= "Món chính".equals(category) ? "selected" : "" %>>Món chính</option>
                        <option value="Món khai vị" <%= "Món khai vị".equals(category) ? "selected" : "" %>>Món khai vị</option>
                        <option value="Món tráng miệng" <%= "Món tráng miệng".equals(category) ? "selected" : "" %>>Món tráng miệng</option>
                        <option value="Đồ uống" <%= "Đồ uống".equals(category) ? "selected" : "" %>>Đồ uống</option>
                    </select>
                </div>

                <div class="mb-3">
                    <label class="form-label-custom">
                        <i class="fas fa-dollar-sign me-2"></i>Giá (VNĐ) *
                    </label>
                    <input type="text" class="form-control form-control-custom" name="price" id="priceInput"
                           placeholder="Nhập giá món ăn (>= 1.000 VNĐ)" autocomplete="off"
                           value="<%= price != null ? price : "" %>" required>
                    <div class="invalid-feedback" id="priceError">
                        Giá phải là số nguyên dương, tối thiểu 1.000 VNĐ.
                    </div>
                </div>

                <div class="mb-3">
                    <label class="form-label-custom">
                        <i class="fas fa-align-left me-2"></i>Mô tả
                    </label>
                    <textarea class="form-control form-control-custom" name="description"
                              rows="4" placeholder="Nhập mô tả món ăn"><%= description != null ? description : "" %></textarea>
                </div>

                <div class="mb-3">
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" id="available" name="available" 
                               <%= available == null || "on".equals(available) ? "checked" : "" %>>
                        <label class="form-check-label" for="available">
                            <i class="fas fa-check-circle me-1"></i>Món ăn đang có sẵn
                        </label>
                    </div>
                </div>

                <div class="d-flex gap-2">
                    <button type="submit" class="btn btn-primary-custom btn-custom">
                        <i class="fas fa-save me-2"></i>Lưu món ăn
                    </button>
                    <a href="${pageContext.request.contextPath}/manager/menu" class="btn btn-outline-secondary btn-custom">
                        <i class="fas fa-times me-2"></i>Hủy
                    </a>
                </div>
            </form>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
    // Format giá và validate
    const priceInput = document.getElementById('priceInput');
    const priceError = document.getElementById('priceError');
    
    if (priceInput) {
        // Chỉ cho nhập số khi đang gõ
        priceInput.addEventListener('keypress', function(e) {
            // Chỉ cho phép số (0-9)
            if (!/^\d$/.test(e.key)) {
                e.preventDefault();
            }
        });
        
        // Format số khi rời khỏi ô input (blur)
        priceInput.addEventListener('blur', function(e) {
            let raw = this.value.replace(/[^\d]/g, '');
            if (raw) {
                // Format với dấu phẩy ngăn cách
                this.value = Number(raw).toLocaleString('vi-VN');
            }
        });
        
        // Xóa format khi focus vào để dễ chỉnh sửa
        priceInput.addEventListener('focus', function(e) {
            let raw = this.value.replace(/[^\d]/g, '');
            if (raw) {
                this.value = raw;
            }
            // Ẩn lỗi
            priceInput.classList.remove('is-invalid');
            priceError.style.display = 'none';
        });
        
        // Validate khi submit form
        priceInput.form.addEventListener('submit', function(e) {
            let raw = priceInput.value.replace(/[^\d]/g, '');
            let price = parseInt(raw, 10);
            
            // Điều kiện: >= 1000 (bỏ điều kiện chia hết cho 1000)
            if (isNaN(price) || price < 1000) {
                e.preventDefault();
                priceInput.classList.add('is-invalid');
                priceError.style.display = 'block';
                priceInput.focus();
                return false;
            } else {
                // Gán lại value "thô" (số thuần) để gửi lên server
                priceInput.value = price;
                return true;
            }
        });
    }
    </script>
</body>
</html>
