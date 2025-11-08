<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý món ăn - Restman</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        body {
            background: #f5f5f5;
        }
        .header-section {
            background: white;
            border-radius: 16px;
            padding: 30px;
            margin-bottom: 30px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .add-dish-btn {
            font-size: 18px;
            padding: 15px 40px;
            font-weight: bold;
            box-shadow: 0 8px 20px rgba(6, 182, 212, 0.4);
            animation: pulse 2s infinite;
        }
        @keyframes pulse {
            0%, 100% {
                transform: scale(1);
            }
            50% {
                transform: scale(1.05);
            }
        }
        .table-section {
            background: white;
            border-radius: 16px;
            padding: 30px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        .dish-table {
            width: 100%;
        }
        .dish-table thead {
            background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);
            color: white;
        }
        .dish-table thead th {
            padding: 15px;
            font-weight: 600;
            border: none;
        }
        .dish-table tbody td {
            padding: 15px;
            vertical-align: middle;
            border-bottom: 1px solid #e5e7eb;
        }
        .dish-table tbody tr:hover {
            background-color: #ecfeff;
        }
        .price-text {
            color: #06b6d4;
            font-weight: bold;
            font-size: 16px;
        }
        .action-btn {
            margin: 0 3px;
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
        <!-- Success Alert -->
        <% if (request.getAttribute("success") != null) { %>
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="fas fa-check-circle me-2"></i>
            <%= request.getAttribute("success") %>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <% } %>

        <!-- Header with Add Button -->
        <div class="header-section">
            <h2 class="mb-0"><i class="fas fa-utensils me-2"></i>Quản lý món ăn</h2>
            <a href="${pageContext.request.contextPath}/manager/dishes/create" class="btn btn-primary-custom btn-custom add-dish-btn">
                <i class="fas fa-plus-circle me-2"></i>Thêm món mới
            </a>
        </div>

        <!-- Table Section -->
        <div class="table-section">
            <table class="dish-table">
                <thead>
                    <tr>
                        <th style="width: 5%;">STT</th>
                        <th style="width: 30%;">Tên món ăn</th>
                        <th style="width: 15%;">Danh mục</th>
                        <th style="width: 15%;">Giá</th>
                        <th style="width: 25%;">Mô tả</th>
                        <th style="width: 10%; text-align: center;">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <% 
                    java.util.List<model.Dish> dishes = (java.util.List<model.Dish>) request.getAttribute("dishes");
                    if (dishes != null && !dishes.isEmpty()) {
                        int index = 1;
                        for (model.Dish dish : dishes) {
                            String badgeClass = "bg-primary";
                            if ("Món khai vị".equals(dish.getCategory())) {
                                badgeClass = "bg-success";
                            } else if ("Món tráng miệng".equals(dish.getCategory())) {
                                badgeClass = "bg-warning";
                            } else if ("Đồ uống".equals(dish.getCategory())) {
                                badgeClass = "bg-info";
                            }
                    %>
                    <tr>
                        <td><%= index++ %></td>
                        <td><strong><%= dish.getName() %></strong></td>
                        <td><span class="badge <%= badgeClass %>"><%= dish.getCategory() %></span></td>
                        <td><span class="price-text"><%= String.format("%,.0f", dish.getPrice()) %>đ</span></td>
                        <td><%= dish.getDescription() != null ? dish.getDescription() : "" %></td>
                        <td style="text-align: center;">
                            <button class="btn btn-sm btn-outline-primary action-btn" onclick="editDish(<%= dish.getId() %>)">
                                <i class="fas fa-edit"></i>
                            </button>
                            <button class="btn btn-sm btn-outline-danger action-btn" onclick="deleteDish(<%= dish.getId() %>)">
                                <i class="fas fa-trash"></i>
                            </button>
                        </td>
                    </tr>
                    <% 
                        }
                    } else { 
                    %>
                    <tr>
                        <td colspan="6" class="text-center text-muted py-4">
                            <i class="fas fa-inbox fa-3x mb-3"></i>
                            <p>Chưa có món ăn nào. Vui lòng thêm món mới.</p>
                        </td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
