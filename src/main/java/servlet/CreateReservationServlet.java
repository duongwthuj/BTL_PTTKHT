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
        
        // Kiểm tra đăng nhập
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            int userId = (Integer) session.getAttribute("userId");
            
            // Lấy dữ liệu từ form
            int tableId = Integer.parseInt(request.getParameter("tableId"));
            String customerName = request.getParameter("customerName");
            String customerPhone = request.getParameter("customerPhone");
            String reservationDate = request.getParameter("reservationDate");
            String reservationTime = request.getParameter("reservationTime");
            int numberOfGuests = Integer.parseInt(request.getParameter("numberOfGuests"));
            String note = request.getParameter("note");
            
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
                // Truyền thông tin đặt bàn thành công
                request.setAttribute("success", true);
                request.setAttribute("reservation", reservation);
                request.setAttribute("tableId", tableId);
            } else {
                request.setAttribute("error", "Có lỗi xảy ra. Vui lòng thử lại!");
            }
            
            // Lấy thông tin user để hiển thị
            String username = (String) session.getAttribute("username");
            User user = userDAO.findByUsername(username);
            if (user != null) {
                request.setAttribute("user", user);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
        }
        
        // Forward về trang đặt bàn với kết quả
        request.getRequestDispatcher("/WEB-INF/customer/ISelectAvailbleTable.jsp").forward(request, response);
    }
}
