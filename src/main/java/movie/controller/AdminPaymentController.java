package movie.controller;

import movie.entity.ThanhToanEntity;
import movie.model.ThanhToanModel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminPaymentController {

    @Autowired
    private SessionFactory sessionFactory;

    private static final int ITEMS_PER_PAGE = 25; // 25 bản ghi/trang, như AdminOrderController
    private static final int PAGES_TO_SHOW = 5; // Hiển thị 5 trang

    // Hàm tiện ích để ghi log vào file
    private void logToFile(String message) {
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(
                Paths.get("logs/app.log"), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            out.println(String.format("%tF %tT - %s", new Date(), new Date(), message));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    @RequestMapping(value = "/payment", method = RequestMethod.GET)
    public String showThanhToanManager(
            HttpSession session,
            Model model,
            @RequestParam(value = "filterMaDonHang", required = false) String filterMaDonHang,
            @RequestParam(value = "filterPhuongThuc", required = false) String filterPhuongThuc,
            @RequestParam(value = "sortBy", required = false, defaultValue = "ngayThanhToan") String sortBy,
            @RequestParam(value = "page", defaultValue = "1") int page) {

        // Log 1: Truy cập endpoint
        logToFile("Accessing /admin/payment at " + new Date());

        // Log 2: Kiểm tra đăng nhập admin
        if (session.getAttribute("loggedInAdmin") == null) {
            logToFile("No admin logged in, redirecting to /admin/login");
            return "redirect:/admin/login";
        }
        logToFile("Admin logged in: " + session.getAttribute("loggedInAdmin"));

        // Log 3: Tham số đầu vào
        logToFile(String.format("Filter parameters - maDonHang: %s, phuongThuc: %s, sortBy: %s, page: %d",
                filterMaDonHang, filterPhuongThuc, sortBy, page));

        Session dbSession = null;
        try {
            // Log 4: Kiểm tra sessionFactory
            if (sessionFactory == null) {
                logToFile("SessionFactory is null!");
                model.addAttribute("error", "Lỗi: SessionFactory không được khởi tạo!");
                return "admin/payment_manager";
            }
            logToFile("Opening Hibernate session");
            dbSession = sessionFactory.openSession();

            // Xây dựng HQL để đếm tổng số bản ghi
            StringBuilder countHql = new StringBuilder("SELECT COUNT(t) FROM ThanhToanEntity t WHERE 1=1");
            List<String> params = new ArrayList<>();
            List<Object> values = new ArrayList<>();

            if (filterMaDonHang != null && !filterMaDonHang.trim().isEmpty()) {
                countHql.append(" AND t.maDonHang LIKE :maDonHang");
                params.add("maDonHang");
                values.add("%" + filterMaDonHang.trim() + "%");
            }

            if (filterPhuongThuc != null && !filterPhuongThuc.trim().isEmpty() && !filterPhuongThuc.equals("all")) {
                countHql.append(" AND t.phuongThuc = :phuongThuc");
                params.add("phuongThuc");
                values.add(filterPhuongThuc.trim());
            }

            Query countQuery = dbSession.createQuery(countHql.toString());
            for (int i = 0; i < params.size(); i++) {
                countQuery.setParameter(params.get(i), values.get(i));
            }
            Long totalItems = (Long) countQuery.uniqueResult();

            // Tính tổng số trang
            int totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);
            int start = (page - 1) * ITEMS_PER_PAGE;

            // Xây dựng HQL với JOIN FETCH
            StringBuilder hql = new StringBuilder("FROM ThanhToanEntity t JOIN FETCH t.donHang WHERE 1=1");
            if (filterMaDonHang != null && !filterMaDonHang.trim().isEmpty()) {
                hql.append(" AND t.maDonHang LIKE :maDonHang");
            }
            if (filterPhuongThuc != null && !filterPhuongThuc.trim().isEmpty() && !filterPhuongThuc.equals("all")) {
                hql.append(" AND t.phuongThuc = :phuongThuc");
            }
            if ("soTien".equals(sortBy)) {
                hql.append(" ORDER BY t.soTien DESC");
            } else {
                hql.append(" ORDER BY t.ngayThanhToan DESC");
                sortBy = "ngayThanhToan";
            }

            // Log 5: Truy vấn HQL
            logToFile("HQL Query: " + hql.toString() + ", Params: " + params + ", Values: " + values);

            Query query = dbSession.createQuery(hql.toString());
            for (int i = 0; i < params.size(); i++) {
                query.setParameter(params.get(i), values.get(i));
            }
            query.setFirstResult(start);
            query.setMaxResults(ITEMS_PER_PAGE);

            @SuppressWarnings("unchecked")
            List<ThanhToanEntity> thanhToanEntities = (List<ThanhToanEntity>) query.list();
            logToFile("Retrieved " + thanhToanEntities.size() + " ThanhToanEntity records");

            List<ThanhToanModel> thanhToanModels = new ArrayList<>();
            for (ThanhToanEntity entity : thanhToanEntities) {
                try {
                    thanhToanModels.add(new ThanhToanModel(entity));
                } catch (Exception e) {
                    logToFile("Error converting ThanhToanEntity to ThanhToanModel: " + e.getMessage());
                    model.addAttribute("error", "Lỗi khi chuyển đổi entity sang model: " + e.getMessage());
                }
            }
            logToFile("Converted to " + thanhToanModels.size() + " ThanhToanModel records");

            // Tính phạm vi trang hiển thị
            List<Integer> pageRange = new ArrayList<>();
            int startPage = Math.max(1, page - (PAGES_TO_SHOW / 2));
            int endPage = Math.min(totalPages, startPage + PAGES_TO_SHOW - 1);
            startPage = Math.max(1, endPage - PAGES_TO_SHOW + 1);
            for (int i = startPage; i <= endPage; i++) {
                pageRange.add(i);
            }

            model.addAttribute("thanhToanList", thanhToanModels);
            model.addAttribute("filterMaDonHang", filterMaDonHang);
            model.addAttribute("filterPhuongThuc", filterPhuongThuc);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageRange", pageRange);

        } catch (Exception e) {
            logToFile("Error retrieving payment list: " + e.getMessage());
            model.addAttribute("error", "Lỗi khi lấy danh sách thanh toán: " + e.getMessage());
        } finally {
            if (dbSession != null && dbSession.isOpen()) {
                logToFile("Closing Hibernate session");
                dbSession.close();
            }
        }

        // Log 6: Trả về view
        logToFile("Returning view: admin/payment_manager");
        return "admin/payment_manager";
    }
}