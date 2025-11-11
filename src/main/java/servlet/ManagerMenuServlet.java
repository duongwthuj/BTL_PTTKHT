package servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/manager/menu")
public class ManagerMenuServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Kiểm tra đăng nhập
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Kiểm tra role manager
        String role = (String) session.getAttribute("role");
        if (!"manager".equalsIgnoreCase(role)) {
            response.sendRedirect(request.getContextPath() + "/customer/menu");
            return;
        }

        // Hiển thị trang menu manager
        request.getRequestDispatcher("/WEB-INF/manager/IMenu.jsp").forward(request, response);
    }
}
