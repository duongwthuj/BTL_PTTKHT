package servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/customer/menu")
public class CustomerMenuServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Kiểm tra đăng nhập
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Kiểm tra role customer
        String role = (String) session.getAttribute("role");
        if (!"customer".equalsIgnoreCase(role)) {
            response.sendRedirect(request.getContextPath() + "/manager/menu");
            return;
        }

        // Hiển thị trang menu customer
        request.getRequestDispatcher("/WEB-INF/customer/IMenuCustommer.jsp").forward(request, response);
    }
}
