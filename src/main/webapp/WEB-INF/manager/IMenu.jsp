<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Menu Quản lý - Restman</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        body {
            background: linear-gradient(-45deg, #0891b2, #06b6d4, #14b8a6, #0ea5e9, #0891b2);
            background-size: 400% 400%;
            animation: gradientShift 8s ease infinite;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
            position: relative;
            overflow: hidden;
        }
        body::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background-image:
                radial-gradient(circle at 20% 50%, rgba(255,255,255,0.3) 0%, transparent 50%),
                radial-gradient(circle at 80% 80%, rgba(255,255,255,0.2) 0%, transparent 50%),
                radial-gradient(circle at 40% 20%, rgba(6,182,212,0.4) 0%, transparent 50%);
            animation: floatingBubbles 10s ease-in-out infinite;
            z-index: 0;
        }
        body::after {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background-image:
                repeating-linear-gradient(45deg, transparent, transparent 35px, rgba(255,255,255,0.08) 35px, rgba(255,255,255,0.08) 70px);
            animation: stripeMove 20s linear infinite;
            z-index: 0;
        }
        body > * {
            position: relative;
            z-index: 1;
        }
        @keyframes gradientShift {
            0% { background-position: 0% 50%; }
            50% { background-position: 100% 50%; }
            100% { background-position: 0% 50%; }
        }
        @keyframes floatingBubbles {
            0%, 100% {
                transform: translate(0, 0) scale(1);
                opacity: 1;
            }
            50% {
                transform: translate(50px, -50px) scale(1.1);
                opacity: 0.8;
            }
        }
        @keyframes stripeMove {
            0% { transform: translateX(0) translateY(0); }
            100% { transform: translateX(70px) translateY(70px); }
        }
        .menu-container {
            background: white;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            padding: 60px 50px;
            max-width: 900px;
            width: 100%;
        }
        .welcome-text {
            text-align: center;
            margin-bottom: 40px;
        }
        .welcome-text h1 {
            color: #333;
            font-size: 32px;
            font-weight: bold;
            margin-bottom: 10px;
        }
        .welcome-text p {
            color: #666;
            font-size: 16px;
        }
        .module-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 30px;
            margin-top: 40px;
        }
        .module-card {
            background: white;
            border: 3px solid #e5e7eb;
            border-radius: 16px;
            padding: 40px 30px;
            text-align: center;
            transition: all 0.3s ease;
            cursor: pointer;
            text-decoration: none;
            color: inherit;
            display: block;
        }
        .module-card:hover {
            transform: translateY(-10px);
            box-shadow: 0 20px 40px rgba(6, 182, 212, 0.3);
            border-color: #06b6d4;
        }
        .module-card.active {
            background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);
            color: white;
            border-color: transparent;
        }
        .module-card.active:hover {
            box-shadow: 0 20px 40px rgba(6, 182, 212, 0.5);
        }
        .module-card.disabled {
            opacity: 0.5;
            cursor: not-allowed;
            pointer-events: none;
        }
        .module-icon {
            font-size: 64px;
            margin-bottom: 20px;
        }
        .module-card.active .module-icon {
            color: white;
        }
        .module-card:not(.active) .module-icon {
            background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
        }
        .module-title {
            font-size: 22px;
            font-weight: bold;
            margin-bottom: 10px;
        }
        .module-description {
            font-size: 14px;
            opacity: 0.9;
        }
        .logout-btn {
            position: absolute;
            top: 20px;
            right: 20px;
        }
        @media (max-width: 768px) {
            .menu-container {
                padding: 40px 30px;
            }
            .module-grid {
                grid-template-columns: 1fr;
                gap: 20px;
            }
        }
    </style>
</head>
<body>
    <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-light btn-custom logout-btn">
        <i class="fas fa-sign-out-alt"></i>
        <span>Đăng xuất</span>
    </a>

    <div class="menu-container fade-in">
        <div class="welcome-text">
            <i class="fas fa-user-shield" style="font-size: 60px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text;"></i>
            <h1>Chào mừng Quản lý, <%= session.getAttribute("username") != null ? session.getAttribute("username") : "Admin" %>!</h1>
            <p>Vui lòng chọn chức năng bạn muốn quản lý</p>
        </div>

        <div class="module-grid">
            <!-- Module 1: Quản lý món ăn (Active) -->
            <a href="${pageContext.request.contextPath}/manager/dishes" class="module-card active hover-lift">
                <div class="module-icon">
                    <i class="fas fa-utensils"></i>
                </div>
                <div class="module-title">Quản lý món ăn</div>
                <div class="module-description">Thêm, sửa, xóa món ăn trong thực đơn</div>
            </a>

            <!-- Module 2: Quản lý đặt bàn (Disabled) -->
            <div class="module-card disabled">
                <div class="module-icon">
                    <i class="fas fa-calendar-check"></i>
                </div>
                <div class="module-title">Quản lý đặt bàn</div>
                <div class="module-description">Xem và quản lý các đơn đặt bàn</div>
            </div>

            <!-- Module 3: Báo cáo thống kê (Disabled) -->
            <div class="module-card disabled">
                <div class="module-icon">
                    <i class="fas fa-chart-bar"></i>
                </div>
                <div class="module-title">Báo cáo thống kê</div>
                <div class="module-description">Xem báo cáo doanh thu và thống kê</div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
