package servlet;

import dao.UserDAO;
import dao.TableDAO;
import model.User;
import model.RestaurantTable;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

@WebServlet("/customer/reservation/select-table")
public class CustomerSelectTableServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();
    private TableDAO tableDAO = new TableDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Kiểm tra đăng nhập
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Lấy thông tin user từ database
        String username = (String) session.getAttribute("username");
        User user = userDAO.findByUsername(username);
        
        // Truyền thông tin user vào request để JSP sử dụng
        if (user != null) {
            request.setAttribute("user", user);
        }

        // Hiển thị trang chọn bàn
        request.getRequestDispatcher("/WEB-INF/customer/ISelectAvailbleTable.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Kiểm tra đăng nhập
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Lấy thông tin user
        String username = (String) session.getAttribute("username");
        User user = userDAO.findByUsername(username);
        if (user != null) {
            request.setAttribute("user", user);
        }

        // Lấy ngày và giờ từ form
        String dateStr = request.getParameter("bookingDate");
        String timeStr = request.getParameter("bookingTime");

        if (dateStr != null && timeStr != null && !dateStr.isEmpty() && !timeStr.isEmpty()) {
            try {
                Date date = Date.valueOf(dateStr);
                Time time = Time.valueOf(timeStr + ":00");

                // Lấy TẤT CẢ bàn với trạng thái tại thời điểm đặt (cả trống và đã đặt)
                List<RestaurantTable> tables = tableDAO.findAllTablesByDateTime(date, time);

                // Truyền danh sách bàn và thông tin đã chọn vào request
                request.setAttribute("tables", tables);
                request.setAttribute("selectedDate", dateStr);
                request.setAttribute("selectedTime", timeStr);
                request.setAttribute("showTables", true);

            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "Lỗi khi tìm bàn: " + e.getMessage());
            }
        }

        // Hiển thị lại trang với danh sách bàn
        request.getRequestDispatcher("/WEB-INF/customer/ISelectAvailbleTable.jsp").forward(request, response);
    }
}