<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User" %>
<%@ page import="model.RestaurantTable" %>
<%@ page import="model.Reservation" %>
<%@ page import="java.util.List" %>
<%
    User currentUser = (User) request.getAttribute("user");
    String userFullName = (currentUser != null && currentUser.getFullName() != null) ? currentUser.getFullName() : "";
    String userPhone = (currentUser != null && currentUser.getPhone() != null) ? currentUser.getPhone() : "";
    
    List<RestaurantTable> tables = (List<RestaurantTable>) request.getAttribute("tables");
    String selectedDate = (String) request.getAttribute("selectedDate");
    String selectedTime = (String) request.getAttribute("selectedTime");
    Boolean showTables = (Boolean) request.getAttribute("showTables");
    
    Boolean success = (Boolean) request.getAttribute("success");
    Reservation reservation = (Reservation) request.getAttribute("reservation");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đặt bàn - Restman</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        body {
            background: #f5f5f5;
        }
        .datetime-section {
            background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);
            color: white;
            padding: 40px 0;
            margin-bottom: 30px;
        }
        .datetime-form {
            background: rgba(255, 255, 255, 0.1);
            backdrop-filter: blur(10px);
            border-radius: 16px;
            padding: 30px;
            border: 2px solid rgba(255, 255, 255, 0.2);
        }
        .datetime-form label {
            color: white;
            font-weight: 600;
            margin-bottom: 8px;
        }
        .datetime-form input,
        .datetime-form select {
            background: white;
            border: none;
            border-radius: 10px;
            padding: 12px 15px;
            font-size: 15px;
        }
        .modal-custom .modal-content {
            border-radius: 20px;
            border: none;
        }
        .modal-custom .modal-header {
            background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);
            color: white;
            border-radius: 20px 20px 0 0;
            border: none;
        }
        .success-icon {
            font-size: 80px;
            color: #10b981;
            margin-bottom: 20px;
        }
        /* Bàn đã đặt - màu đỏ, không click được */
        .table-item.reserved {
            background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
            opacity: 0.7;
            cursor: not-allowed !important;
        }
        .table-item.reserved:hover {
            transform: none !important;
            box-shadow: 0 4px 15px rgba(239, 68, 68, 0.3);
        }
    </style>
</head>
<body>
    <!-- Navbar -->
    <nav class="navbar navbar-expand-lg navbar-custom">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/customer/menu">
                <i class="fas fa-utensils"></i>
                <span>Restman</span>
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/customer/menu">
                            <i class="fas fa-home"></i> Trang chủ
                        </a>
                    </li>
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button" data-bs-toggle="dropdown">
                            <i class="fas fa-user-circle"></i>
                            <%
                                String username = (String) session.getAttribute("username");
                                out.print(username != null ? username : "Khách hàng");
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

    <!-- DateTime Selection Section -->
    <div class="datetime-section">
        <div class="container">
            <h2 class="text-center mb-4">
                <i class="fas fa-calendar-check me-2"></i>Chọn ngày và giờ đặt bàn
            </h2>
            <div class="row justify-content-center">
                <div class="col-lg-8">
                    <form action="${pageContext.request.contextPath}/customer/reservation/select-table" method="post">
                        <div class="datetime-form">
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label><i class="fas fa-calendar me-2"></i>Ngày đặt bàn</label>
                                    <input type="date" class="form-control" name="bookingDate" id="bookingDate" 
                                           value="<%= selectedDate != null ? selectedDate : "" %>" required>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label><i class="fas fa-clock me-2"></i>Khung giờ</label>
                                    <select class="form-control" name="bookingTime" id="bookingTime" required>
                                        <option value="">Chọn khung giờ</option>
                                        <option value="10:00" <%= "10:00".equals(selectedTime) ? "selected" : "" %>>10:00 - 12:00 (Sáng)</option>
                                        <option value="12:00" <%= "12:00".equals(selectedTime) ? "selected" : "" %>>12:00 - 14:00 (Trưa)</option>
                                        <option value="17:00" <%= "17:00".equals(selectedTime) ? "selected" : "" %>>17:00 - 19:00 (Chiều)</option>
                                        <option value="19:00" <%= "19:00".equals(selectedTime) ? "selected" : "" %>>19:00 - 21:00 (Tối)</option>
                                    </select>
                                </div>
                            </div>
                            <div class="text-center mt-2">
                                <button type="submit" class="btn btn-light btn-custom">
                                    <i class="fas fa-search me-2"></i>Xem bàn trống
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- Tables Section -->
    <div class="container container-custom">
        <% if (showTables == null || !showTables) { %>
            <div class="text-center py-5">
                <i class="fas fa-calendar-alt fa-3x text-muted mb-3"></i>
                <h4 class="text-muted">Vui lòng chọn ngày và giờ để xem bàn trống</h4>
            </div>
        <% } else { %>
            <!-- Legend -->
            <div class="card-custom mb-4">
                <div class="card-body">
                    <div class="row text-center">
                        <div class="col-md-6">
                            <i class="fas fa-check-circle fa-2x" style="color: #10b981;"></i>
                            <p class="mb-0 mt-2"><strong>Bàn trống</strong><br><small class="text-muted">Có thể đặt ngay</small></p>
                        </div>
                        <div class="col-md-6">
                            <i class="fas fa-times-circle fa-2x" style="color: #ef4444;"></i>
                            <p class="mb-0 mt-2"><strong>Đã đặt</strong><br><small class="text-muted">Không khả dụng</small></p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Tables Grid -->
            <div class="table-grid">
                <% if (tables != null && !tables.isEmpty()) {
                    for (RestaurantTable table : tables) {
                        boolean isAvailable = "available".equals(table.getStatus());
                %>
                    <div class="table-item <%= table.getStatus() %> hover-lift table-card" 
                         <%= isAvailable ? "style='cursor:pointer;' onclick='selectTable(" + table.getId() + ")'" : "" %>>
                        <i class="fas fa-utensils"></i>
                        <div class="table-number">Bàn <%= String.format("%02d", table.getTableNumber()) %></div>
                        <div class="table-capacity"><i class="fas fa-users me-1"></i><%= table.getCapacity() %> người</div>
                        <span class="badge badge-custom <%= isAvailable ? "badge-status-available" : "badge-status-occupied" %> mt-2">
                            <%= isAvailable ? "Trống" : "Đã đặt" %>
                        </span>
                    </div>
                <% }
                } else { %>
                    <div class="text-center py-5">
                        <i class="fas fa-inbox fa-3x text-muted mb-3"></i>
                        <h4 class="text-muted">Không có bàn trống vào thời gian này</h4>
                    </div>
                <% } %>
            </div>
        <% } %>
    </div>

    <!-- Modal Điền thông tin -->
    <div class="modal fade modal-custom" id="bookingModal" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">
                        <i class="fas fa-edit me-2"></i>Thông tin đặt bàn
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <form action="${pageContext.request.contextPath}/customer/reservation/create" method="post" id="bookingForm">
                        <input type="hidden" name="tableId" id="selectedTable">
                        <input type="hidden" name="reservationDate" id="hiddenDate">
                        <input type="hidden" name="reservationTime" id="hiddenTime">

                        <div class="mb-3">
                            <label class="form-label-custom">
                                <i class="fas fa-user me-2"></i>Họ và tên *
                            </label>
                            <input type="text" class="form-control form-control-custom" name="customerName"
                                   placeholder="Nhập họ và tên" value="<%= userFullName %>" required>
                        </div>

                        <div class="mb-3">
                            <label class="form-label-custom">
                                <i class="fas fa-phone me-2"></i>Số điện thoại *
                            </label>
                            <input type="tel" class="form-control form-control-custom" name="customerPhone"
                                   placeholder="Nhập số điện thoại" value="<%= userPhone %>" required>
                        </div>

                        <div class="mb-3">
                            <label class="form-label-custom">
                                <i class="fas fa-users me-2"></i>Số người
                            </label>
                            <input type="number" class="form-control form-control-custom" name="numberOfGuests"
                                   min="1" max="20" value="2" required>
                        </div>

                        <div class="d-grid">
                            <button type="submit" class="btn btn-primary-custom btn-custom">
                                <i class="fas fa-check-circle me-2"></i>Xác nhận đặt bàn
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- Modal Thành công -->
    <div class="modal fade modal-custom" id="successModal" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-body text-center py-5">
                    <i class="fas fa-check-circle success-icon"></i>
                    <h3 class="mb-3">Đặt bàn thành công!</h3>
                    <p class="text-muted mb-4">Cảm ơn bạn đã đặt bàn tại Restman.<br>Chúng tôi sẽ liên hệ với bạn sớm nhất.</p>
                    <div class="alert alert-info" id="bookingInfo">
                        <!-- Booking info will be displayed here -->
                    </div>
                    <button type="button" class="btn btn-primary-custom btn-custom" data-bs-dismiss="modal">
                        <i class="fas fa-times me-2"></i>Đóng
                    </button>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Set today as default date
        var today = new Date().toISOString().split('T')[0];
        var bookingDateInput = document.getElementById('bookingDate');
        if (bookingDateInput && !bookingDateInput.value) {
            bookingDateInput.value = today;
        }
        if (bookingDateInput) {
            bookingDateInput.min = today;
        }

        var bookingModal = new bootstrap.Modal(document.getElementById('bookingModal'));
        var successModal = new bootstrap.Modal(document.getElementById('successModal'));

        // Hàm chọn bàn
        function selectTable(tableId) {
            document.getElementById('selectedTable').value = tableId;
            
            // Copy date and time from search form to hidden fields in booking form
            var searchDate = document.getElementById('bookingDate').value;
            var searchTime = document.getElementById('bookingTime').value;
            document.getElementById('hiddenDate').value = searchDate;
            document.getElementById('hiddenTime').value = searchTime;
            
            bookingModal.show();
        }

        <% if (success != null && success && reservation != null) { %>
            // Show success modal when reservation is created
            window.addEventListener('load', function() {
                var info = '<div class="text-start">' +
                    '<p class="mb-2"><i class="fas fa-chair me-2"></i><strong>Bàn:</strong> Bàn <%= String.format("%02d", reservation.getTableId()) %></p>' +
                    '<p class="mb-2"><i class="fas fa-calendar me-2"></i><strong>Ngày:</strong> <%= reservation.getReservationDate() %></p>' +
                    '<p class="mb-2"><i class="fas fa-clock me-2"></i><strong>Giờ:</strong> <%= reservation.getReservationTime() %></p>' +
                    '<p class="mb-2"><i class="fas fa-user me-2"></i><strong>Tên:</strong> <%= reservation.getCustomerName() %></p>' +
                    '<p class="mb-2"><i class="fas fa-phone me-2"></i><strong>SĐT:</strong> <%= reservation.getCustomerPhone() %></p>' +
                    '<p class="mb-0"><i class="fas fa-users me-2"></i><strong>Số người:</strong> <%= reservation.getNumberOfGuests() %> người</p>' +
                    '</div>';
                document.getElementById('bookingInfo').innerHTML = info;
                successModal.show();
            });
        <% } else if (success != null && !success) { %>
            alert('Không thể đặt bàn. Vui lòng thử lại!');
        <% } %>
    </script>
</body>
</html>
