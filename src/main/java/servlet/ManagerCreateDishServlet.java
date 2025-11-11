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
import java.math.BigDecimal;

@WebServlet("/manager/dishes/create")
public class ManagerCreateDishServlet extends HttpServlet {

    private DishDAO dishDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        dishDAO = new DishDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Kiểm tra đăng nhập và role
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

        // Hiển thị form thêm món ăn
        request.getRequestDispatcher("/WEB-INF/manager/ICreateNewDish.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Kiểm tra đăng nhập và role
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String role = (String) session.getAttribute("role");
        if (!"manager".equalsIgnoreCase(role)) {
            response.sendRedirect(request.getContextPath() + "/customer/menu");
            return;
        }

        // Set encoding để xử lý tiếng Việt
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        // Lấy thông tin món ăn
        String name = request.getParameter("name");
        String category = request.getParameter("category");
        String priceStr = request.getParameter("price");
        String description = request.getParameter("description");
        String availableStr = request.getParameter("available");

        // Validate dữ liệu bắt buộc
        if (name == null || name.trim().isEmpty() ||
            category == null || category.trim().isEmpty() ||
            priceStr == null || priceStr.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng điền đầy đủ thông tin bắt buộc!");
            request.getRequestDispatcher("/WEB-INF/manager/ICreateNewDish.jsp").forward(request, response);
            return;
        }

        // Parse price
        BigDecimal price;
        try {
            price = new BigDecimal(priceStr.trim());
            if (price.compareTo(BigDecimal.ZERO) < 0) {
                throw new NumberFormatException("Price must be positive");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Giá không hợp lệ!");
            request.getRequestDispatcher("/WEB-INF/manager/ICreateNewDish.jsp").forward(request, response);
            return;
        }

        // Parse available (checkbox: null = unchecked, "on" = checked)
        boolean available = "on".equals(availableStr);

        // Kiểm tra tên món ăn đã tồn tại chưa
        if (dishDAO.existsByName(name.trim())) {
            request.setAttribute("error", "Tên món ăn \"" + name.trim() + "\" đã tồn tại trong hệ thống!");
            request.setAttribute("name", name);
            request.setAttribute("category", category);
            request.setAttribute("price", priceStr);
            request.setAttribute("description", description);
            request.setAttribute("available", availableStr);
            request.getRequestDispatcher("/WEB-INF/manager/ICreateNewDish.jsp").forward(request, response);
            return;
        }

        // Tạo Dish object
        Dish newDish = new Dish(
            name.trim(),
            category.trim(),
            price,
            description != null ? description.trim() : "",
            available
        );

        // Lưu vào database thông qua DishDAO
        boolean success = dishDAO.insert(newDish);

        if (success) {
            // Thêm món ăn thành công - reset form
            request.setAttribute("success", "Thêm món ăn \"" + name.trim() + "\" thành công!");
            request.getRequestDispatcher("/WEB-INF/manager/ICreateNewDish.jsp").forward(request, response);
        } else {
            // Thêm món ăn thất bại
            request.setAttribute("error", "Thêm món ăn thất bại! Vui lòng thử lại.");
            request.getRequestDispatcher("/WEB-INF/manager/ICreateNewDish.jsp").forward(request, response);
        }
    }
}
