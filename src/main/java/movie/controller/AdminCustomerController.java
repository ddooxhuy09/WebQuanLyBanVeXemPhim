package movie.controller;

import movie.entity.DonHangEntity;
import movie.entity.KhachHangEntity;
import movie.model.DonHangModel;
import movie.model.KhachHangModel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminCustomerController {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private JavaMailSender mailSender; // Tiêm JavaMailSender để gửi email

    private static final int ITEMS_PER_PAGE = 25; // 25 bản ghi/trang
    private static final int PAGES_TO_SHOW = 5; // Hiển thị 5 trang

    @RequestMapping(value = "/customers", method = RequestMethod.GET)
    public String showCustomerManager(
            Model model,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "sortOrder", required = false, defaultValue = "asc") String sortOrder) {
        Session dbSession = sessionFactory.openSession();
        try {
            // Xây dựng truy vấn đếm tổng số bản ghi
            String countHql = "SELECT COUNT(k) FROM KhachHangEntity k";
            String hql = "FROM KhachHangEntity k";
            if (search != null && !search.trim().isEmpty()) {
                countHql += " WHERE k.hoKhachHang LIKE :search OR k.tenKhachHang LIKE :search OR k.soDienThoai LIKE :search OR k.email LIKE :search";
                hql += " WHERE k.hoKhachHang LIKE :search OR k.tenKhachHang LIKE :search OR k.soDienThoai LIKE :search OR k.email LIKE :search";
            }

            // Xử lý sắp xếp
            if (sortBy != null && !sortBy.isEmpty()) {
                String orderField = switch (sortBy) {
                    case "name" -> "k.tenKhachHang";
                    case "points" -> "k.tongDiem";
                    case "regDate" -> "k.ngayDangKy";
                    default -> "k.maKhachHang";
                };
                hql += " ORDER BY " + orderField + " " + (sortOrder.equalsIgnoreCase("desc") ? "DESC" : "ASC");
            } else {
                hql += " ORDER BY k.maKhachHang";
            }

            // Đếm tổng số bản ghi
            Query countQuery = dbSession.createQuery(countHql);
            if (search != null && !search.trim().isEmpty()) {
                countQuery.setParameter("search", "%" + search + "%");
            }
            Long totalItems = (Long) countQuery.uniqueResult();

            // Tính tổng số trang
            int totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);
            int start = (page - 1) * ITEMS_PER_PAGE;

            // Lấy danh sách khách hàng cho trang hiện tại
            Query query = dbSession.createQuery(hql);
            if (search != null && !search.trim().isEmpty()) {
                query.setParameter("search", "%" + search + "%");
            }
            query.setFirstResult(start);
            query.setMaxResults(ITEMS_PER_PAGE);
            List khachHangEntities = query.list();
            List<KhachHangModel> customerList = new ArrayList<>();
            List<String> maKhachHangList = new ArrayList<>();
            for (Object obj : khachHangEntities) {
                KhachHangEntity entity = (KhachHangEntity) obj;
                customerList.add(new KhachHangModel(entity));
                maKhachHangList.add(entity.getMaKhachHang());
            }

            // Lấy danh sách đơn hàng cho các khách hàng trong trang hiện tại
            Map<String, List<DonHangModel>> customerOrdersMap = new HashMap<>();
            if (!maKhachHangList.isEmpty()) {
                Query ordersQuery = dbSession.createQuery("FROM DonHangEntity WHERE maKhachHang IN (:maKhachHangList)");
                ordersQuery.setParameterList("maKhachHangList", maKhachHangList);
                List<DonHangEntity> donHangEntities = ordersQuery.list();
                for (DonHangEntity donHang : donHangEntities) {
                    String maKhachHang = donHang.getMaKhachHang();
                    customerOrdersMap.computeIfAbsent(maKhachHang, k -> new ArrayList<>()).add(new DonHangModel(donHang));
                }
            }

            // Tính phạm vi trang hiển thị
            List<Integer> pageRange = new ArrayList<>();
            int startPage = Math.max(1, page - (PAGES_TO_SHOW / 2));
            int endPage = Math.min(totalPages, startPage + PAGES_TO_SHOW - 1);
            startPage = Math.max(1, endPage - PAGES_TO_SHOW + 1);
            for (int i = startPage; i <= endPage; i++) {
                pageRange.add(i);
            }

            model.addAttribute("customerList", customerList);
            model.addAttribute("customerOrdersMap", customerOrdersMap);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageRange", pageRange);
            model.addAttribute("search", search);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortOrder", sortOrder);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi lấy danh sách khách hàng: " + e.getMessage());
        } finally {
            dbSession.close();
        }
        return "admin/customer_manager";
    }

    @Transactional
    @RequestMapping(value = "/customers/delete/{maKhachHang}", method = RequestMethod.GET)
    public String deleteCustomer(
            @PathVariable("maKhachHang") String maKhachHang,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "sortOrder", required = false) String sortOrder,
            RedirectAttributes redirectAttributes) {
        try {
            Session dbSession = sessionFactory.getCurrentSession();
            KhachHangEntity khachHang = (KhachHangEntity) dbSession.get(KhachHangEntity.class, maKhachHang);
            if (khachHang == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy khách hàng với mã " + maKhachHang);
                return "redirect:/admin/customers?page=" + page + "&search=" + (search != null ? search : "") + "&sortBy=" + (sortBy != null ? sortBy : "") + "&sortOrder=" + (sortOrder != null ? sortOrder : "");
            }

            // Kiểm tra đơn hàng
            Query query = dbSession.createQuery("FROM DonHangEntity WHERE maKhachHang = :maKhachHang");
            query.setParameter("maKhachHang", maKhachHang);
            List donHangs = query.list();
            if (!donHangs.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Không thể xóa khách hàng với mã " + maKhachHang + " vì đã có đơn hàng liên quan.");
                return "redirect:/admin/customers?page=" + page + "&search=" + (search != null ? search : "") + "&sortBy=" + (sortBy != null ? sortBy : "") + "&sortOrder=" + (sortOrder != null ? sortOrder : "");
            }

            dbSession.delete(khachHang);
            redirectAttributes.addFlashAttribute("success", "Xóa khách hàng với mã " + maKhachHang + " thành công.");
            return "redirect:/admin/customers?page=" + page + "&search=" + (search != null ? search : "") + "&sortBy=" + (sortBy != null ? sortBy : "") + "&sortOrder=" + (sortOrder != null ? sortOrder : "");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa khách hàng: " + e.getMessage());
            return "redirect:/admin/customers?page=" + page + "&search=" + (search != null ? search : "") + "&sortBy=" + (sortBy != null ? sortBy : "") + "&sortOrder=" + (sortOrder != null ? sortOrder : "");
        }
    }

    @Transactional
    @RequestMapping(value = "/customers/send-email", method = RequestMethod.POST)
    public String sendEmail(
            @RequestParam("maKhachHang") String maKhachHang,
            @RequestParam("from") String from,
            @RequestParam("to") String to,
            @RequestParam("subject") String subject,
            @RequestParam("body") String body,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "sortOrder", required = false) String sortOrder,
            RedirectAttributes redirectAttributes) {
        try {
            Session dbSession = sessionFactory.getCurrentSession();
            KhachHangEntity khachHang = (KhachHangEntity) dbSession.get(KhachHangEntity.class, maKhachHang);
            if (khachHang == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy khách hàng với mã " + maKhachHang);
                return "redirect:/admin/customers?page=" + page + "&search=" + (search != null ? search : "") + "&sortBy=" + (sortBy != null ? sortBy : "") + "&sortOrder=" + (sortOrder != null ? sortOrder : "");
            }

            // Send email
            MimeMessage mail = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mail, true, "utf-8");
            helper.setFrom(from, from);
            helper.setTo(to);
            helper.setReplyTo(from, from);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(mail);

            redirectAttributes.addFlashAttribute("success", "Gửi email tới " + to + " thành công.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Lỗi khi gửi email: " + e.getMessage());
        }
        return "redirect:/admin/customers?page=" + page + "&search=" + (search != null ? search : "") + "&sortBy=" + (sortBy != null ? sortBy : "") + "&sortOrder=" + (sortOrder != null ? sortOrder : "");
    }
}