package servlet;

import dao.ReservationDAO;
import dao.UserDAO;
import model.Reservation;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;

@WebServlet("/customer/reservation/create")
public class CreateReservationServlet extends HttpServlet {
    
    private ReservationDAO reservationDAO = new ReservationDAO();
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession(false);
        
        // Kiểm tra xem có phải khách vãng lai không
        String isGuest = request.getParameter("guest");
        boolean guestMode = "true".equals(isGuest);
        
        int userId = 0;
        
        if (!guestMode) {
            // Mode customer - cần đăng nhập
            if (session == null || session.getAttribute("userId") == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            userId = (Integer) session.getAttribute("userId");
        }
        
        try {
            // Lấy dữ liệu từ form
            int tableId = Integer.parseInt(request.getParameter("tableId"));
            String customerName = request.getParameter("customerName");
            String customerPhone = request.getParameter("customerPhone");
            String reservationDate = request.getParameter("reservationDate");
            String reservationTime = request.getParameter("reservationTime");
            int numberOfGuests = Integer.parseInt(request.getParameter("numberOfGuests"));
            String note = request.getParameter("note");
            
            // Nếu là guest, tạo/lấy user guest
            if (guestMode) {
                // Kiểm tra xem số điện thoại đã tồn tại chưa
                User guestUser = userDAO.findByUsername("guest_" + customerPhone.trim());
                
                if (guestUser == null) {
                    // Chưa có -> Tạo user mới với role='guest'
                    guestUser = new User(
                        "guest_" + customerPhone.trim(),
                        "guest123",
                        customerName.trim(),
                        null,
                        customerPhone.trim(),
                        "guest"
                    );
                    
                    boolean userCreated = userDAO.insert(guestUser);
                    if (!userCreated) {
                        request.setAttribute("error", "Không thể tạo thông tin khách!");
                        request.setAttribute("isGuest", true);
                        request.getRequestDispatcher("/WEB-INF/customer/ISelectAvailbleTable.jsp").forward(request, response);
                        return;
                    }
                    System.out.println("✅ Created new guest user: " + guestUser.getId());
                }
                userId = guestUser.getId();
                request.setAttribute("isGuest", true);
            }
            
            // Tạo Reservation object
            Reservation reservation = new Reservation();
            reservation.setUserId(userId);
            reservation.setTableId(tableId);
            reservation.setCustomerName(customerName);
            reservation.setCustomerPhone(customerPhone);
            reservation.setReservationDate(Date.valueOf(reservationDate));
            reservation.setReservationTime(Time.valueOf(reservationTime + ":00"));
            reservation.setNumberOfGuests(numberOfGuests);
            reservation.setNote(note);
            reservation.setStatus("pending");
            
            // Lưu vào database
            boolean success = reservationDAO.insert(reservation);
            
            if (success) {
                request.setAttribute("success", true);
                request.setAttribute("reservation", reservation);
                request.setAttribute("tableId", tableId);
                System.out.println("✅ Reservation created successfully!");
            } else {
                request.setAttribute("error", "Có lỗi xảy ra. Vui lòng thử lại!");
            }
            
            // Lấy thông tin user để hiển thị (nếu không phải guest)
            if (!guestMode && session != null) {
                String username = (String) session.getAttribute("username");
                User user = userDAO.findByUsername(username);
                if (user != null) {
                    request.setAttribute("user", user);
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            if (guestMode) {
                request.setAttribute("isGuest", true);
            }
        }
        
        // Forward về trang đặt bàn với kết quả
        request.getRequestDispatcher("/WEB-INF/customer/ISelectAvailbleTable.jsp").forward(request, response);
    }
}
