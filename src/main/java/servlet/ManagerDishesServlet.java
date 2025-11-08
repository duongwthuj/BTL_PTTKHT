package servlet;

import dao.DishDAO;
import model.Dish;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/manager/dishes")
public class ManagerDishesServlet extends HttpServlet {

    private DishDAO dishDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        dishDAO = new DishDAO();
    }

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

        // Lấy danh sách món ăn từ database
        List<Dish> dishes = dishDAO.findAll();
        request.setAttribute("dishes", dishes);

        // Lấy thông báo success từ session (nếu có)
        String successMessage = (String) session.getAttribute("dishSuccess");
        if (successMessage != null) {
            request.setAttribute("success", successMessage);
            session.removeAttribute("dishSuccess"); // Xóa sau khi hiển thị
        }

        // Hiển thị trang quản lý món ăn
        request.getRequestDispatcher("/WEB-INF/manager/IManageDish.jsp").forward(request, response);
    }
}
