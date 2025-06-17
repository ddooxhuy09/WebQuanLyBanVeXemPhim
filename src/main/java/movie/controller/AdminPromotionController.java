package movie.controller;

import movie.entity.KhuyenMaiEntity;
import movie.model.KhuyenMaiModel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminPromotionController {

    @Autowired
    private SessionFactory sessionFactory;

    private static final int ITEMS_PER_PAGE = 25; // 25 bản ghi/trang
    private static final int PAGES_TO_SHOW = 5; // Hiển thị 5 trang

    // Hiển thị trang quản lý khuyến mãi
    @RequestMapping(value = "/promotions", method = RequestMethod.GET)
    public String showPromotionManager(
            HttpSession session,
            Model model,
            @RequestParam(value = "page", defaultValue = "1") int page) {
        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/auth/login";
        }

        Session dbSession = sessionFactory.openSession();
        try {
            // Tạo mã khuyến mãi mới
            Query query = dbSession.createQuery("FROM KhuyenMaiEntity ORDER BY maKhuyenMai DESC");
            query.setMaxResults(1);
            KhuyenMaiEntity latestKhuyenMai = (KhuyenMaiEntity) query.uniqueResult();
            String newMaKhuyenMai = latestKhuyenMai == null ? "KM001" : String.format("KM%03d",
                    Integer.parseInt(latestKhuyenMai.getMaKhuyenMai().substring(2)) + 1);

            // Đếm tổng số bản ghi
            Query countQuery = dbSession.createQuery("SELECT COUNT(k) FROM KhuyenMaiEntity k");
            Long totalItems = (Long) countQuery.uniqueResult();

            // Tính tổng số trang
            int totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);
            int start = (page - 1) * ITEMS_PER_PAGE;

            // Lấy danh sách khuyến mãi cho trang hiện tại
            Query allKhuyenMaiQuery = dbSession.createQuery("FROM KhuyenMaiEntity ORDER BY maKhuyenMai DESC");
            allKhuyenMaiQuery.setFirstResult(start);
            allKhuyenMaiQuery.setMaxResults(ITEMS_PER_PAGE);
            List<KhuyenMaiEntity> khuyenMaiEntities = allKhuyenMaiQuery.list();
            List<KhuyenMaiModel> khuyenMaiList = new ArrayList<>();
            for (KhuyenMaiEntity entity : khuyenMaiEntities) {
                khuyenMaiList.add(new KhuyenMaiModel(entity));
            }

            // Tính phạm vi trang hiển thị
            List<Integer> pageRange = new ArrayList<>();
            int startPage = Math.max(1, page - (PAGES_TO_SHOW / 2));
            int endPage = Math.min(totalPages, startPage + PAGES_TO_SHOW - 1);
            startPage = Math.max(1, endPage - PAGES_TO_SHOW + 1);
            for (int i = startPage; i <= endPage; i++) {
                pageRange.add(i);
            }

            model.addAttribute("khuyenMaiList", khuyenMaiList);
            model.addAttribute("newMaKhuyenMai", newMaKhuyenMai);
            model.addAttribute("isEdit", false);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageRange", pageRange);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi lấy danh sách khuyến mãi: " + e.getMessage());
        } finally {
            dbSession.close();
        }
        return "admin/promotion_manager";
    }

    // Thêm khuyến mãi mới
    @Transactional
    @RequestMapping(value = "/promotions/add", method = RequestMethod.POST)
    public String addPromotion(
            @RequestParam("maKhuyenMai") String maKhuyenMai,
            @RequestParam("maCode") String maCode,
            @RequestParam("moTa") String moTa,
            @RequestParam("loaiGiamGia") String loaiGiamGia,
            @RequestParam("giaTriGiam") BigDecimal giaTriGiam,
            @RequestParam("ngayBatDau") String ngayBatDauStr,
            @RequestParam("ngayKetThuc") String ngayKetThucStr,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/auth/login";
        }

        Session dbSession = sessionFactory.getCurrentSession();
        List<String> errors = new ArrayList<>();

        // Lưu dữ liệu để hiển thị lại form nếu lỗi
        KhuyenMaiModel formData = new KhuyenMaiModel();
        formData.setMaKhuyenMai(maKhuyenMai);
        formData.setMaCode(maCode);
        formData.setMoTa(moTa);
        formData.setLoaiGiamGia(loaiGiamGia);
        formData.setGiaTriGiam(giaTriGiam);

        // Validation
        if (maCode == null || maCode.trim().isEmpty()) {
            errors.add("Mã code không được để trống.");
        }
        if (moTa == null || moTa.trim().isEmpty()) {
            errors.add("Mô tả không được để trống.");
        }
        if (loaiGiamGia == null || loaiGiamGia.trim().isEmpty()) {
            errors.add("Loại giảm giá không được để trống.");
        }
        if (giaTriGiam == null || giaTriGiam.compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("Giá trị giảm phải là số dương.");
        }
        if (ngayBatDauStr == null || ngayBatDauStr.trim().isEmpty()) {
            errors.add("Ngày bắt đầu không được để trống.");
        }
        if (ngayKetThucStr == null || ngayKetThucStr.trim().isEmpty()) {
            errors.add("Ngày kết thúc không được để trống.");
        }

        // Parse và kiểm tra định dạng ngày
        Date ngayBatDau = null;
        Date ngayKetThuc = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            ngayBatDau = sdf.parse(ngayBatDauStr);
            ngayKetThuc = sdf.parse(ngayKetThucStr);
            formData.setNgayBatDau(ngayBatDau);
            formData.setNgayKetThuc(ngayKetThuc);
        } catch (Exception e) {
            errors.add("Ngày bắt đầu hoặc kết thúc không đúng định dạng (yyyy-MM-dd).");
        }

        // Kiểm tra trùng maCode
        if (maCode != null && !maCode.trim().isEmpty()) {
            Query checkCodeQuery = dbSession.createQuery("FROM KhuyenMaiEntity WHERE maCode = :maCode");
            checkCodeQuery.setParameter("maCode", maCode);
            if (checkCodeQuery.uniqueResult() != null) {
                errors.add("Mã code " + maCode + " đã tồn tại!");
            }
        }

        // Kiểm tra ngày hợp lệ
        if (ngayBatDau != null) {
            Calendar todayCal = Calendar.getInstance();
            todayCal.set(Calendar.HOUR_OF_DAY, 0);
            todayCal.set(Calendar.MINUTE, 0);
            todayCal.set(Calendar.SECOND, 0);
            todayCal.set(Calendar.MILLISECOND, 0);
            Date today = todayCal.getTime();

            if (ngayBatDau.before(today)) {
                errors.add("Ngày bắt đầu không được là quá khứ!");
            }
        }

        if (ngayBatDau != null && ngayKetThuc != null) {
            if (ngayBatDau.getTime() == ngayKetThuc.getTime()) {
                errors.add("Ngày bắt đầu không được trùng ngày kết thúc!");
            }
            if (ngayKetThuc.before(ngayBatDau)) {
                errors.add("Ngày kết thúc phải sau ngày bắt đầu!");
            }
        }

        // Nếu có lỗi, trả về form
        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", String.join(" ", errors));
            redirectAttributes.addFlashAttribute("addFormData", formData);
            return "redirect:/admin/promotions";
        }

        try {
            KhuyenMaiEntity khuyenMai = new KhuyenMaiEntity();
            khuyenMai.setMaKhuyenMai(maKhuyenMai);
            khuyenMai.setMaCode(maCode);
            khuyenMai.setMoTa(moTa);
            khuyenMai.setLoaiGiamGia(loaiGiamGia);
            khuyenMai.setGiaTriGiam(giaTriGiam);
            khuyenMai.setNgayBatDau(ngayBatDau);
            khuyenMai.setNgayKetThuc(ngayKetThuc);

            dbSession.save(khuyenMai);
            redirectAttributes.addFlashAttribute("success", "Thêm khuyến mãi thành công!");
            return "redirect:/admin/promotions";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm khuyến mãi: " + e.getMessage());
            redirectAttributes.addFlashAttribute("addFormData", formData);
            return "redirect:/admin/promotions";
        }
    }

    // Cập nhật khuyến mãi
    @Transactional
    @RequestMapping(value = "/promotions/update", method = RequestMethod.POST)
    public String updatePromotion(
            @RequestParam("maKhuyenMai") String maKhuyenMai,
            @RequestParam("maCode") String maCode,
            @RequestParam("moTa") String moTa,
            @RequestParam("loaiGiamGia") String loaiGiamGia,
            @RequestParam("giaTriGiam") String giaTriGiamStr,
            @RequestParam("ngayBatDau") String ngayBatDauStr,
            @RequestParam("ngayKetThuc") String ngayKetThucStr,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/auth/login";
        }

        Session dbSession = sessionFactory.getCurrentSession();
        List<String> errors = new ArrayList<>();
        KhuyenMaiModel formData = new KhuyenMaiModel();
        formData.setMaKhuyenMai(maKhuyenMai);
        formData.setMaCode(maCode);
        formData.setMoTa(moTa);
        formData.setLoaiGiamGia(loaiGiamGia);

        // Validate giaTriGiam
        BigDecimal giaTriGiam = null;
        try {
            giaTriGiam = new BigDecimal(giaTriGiamStr.trim());
            if (giaTriGiam.compareTo(BigDecimal.ZERO) <= 0) {
                errors.add("Giá trị giảm phải là số dương.");
            }
            if (loaiGiamGia.equals("Phần trăm") && giaTriGiam.compareTo(new BigDecimal("100")) > 0) {
                errors.add("Giá trị phần trăm giảm không được lớn hơn 100.");
            }
            formData.setGiaTriGiam(giaTriGiam);
        } catch (NumberFormatException e) {
            errors.add("Giá trị giảm không hợp lệ, phải là một số.");
        }

        // Existing validations for other fields
        if (maCode == null || maCode.trim().isEmpty()) {
            errors.add("Mã code không được để trống.");
        }
        if (moTa == null || moTa.trim().isEmpty()) {
            errors.add("Mô tả không được để trống.");
        }
        if (loaiGiamGia == null || loaiGiamGia.trim().isEmpty()) {
            errors.add("Loại giảm giá không được để trống.");
        }
        if (ngayBatDauStr == null || ngayBatDauStr.trim().isEmpty()) {
            errors.add("Ngày bắt đầu không được để trống.");
        }
        if (ngayKetThucStr == null || ngayKetThucStr.trim().isEmpty()) {
            errors.add("Ngày kết thúc không được để trống.");
        }

        // Parse and validate dates
        Date ngayBatDau = null;
        Date ngayKetThuc = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            ngayBatDau = sdf.parse(ngayBatDauStr);
            ngayKetThuc = sdf.parse(ngayKetThucStr);
            formData.setNgayBatDau(ngayBatDau);
            formData.setNgayKetThuc(ngayKetThuc);
        } catch (Exception e) {
            errors.add("Ngày bắt đầu hoặc kết thúc không đúng định dạng (yyyy-MM-dd).");
        }

        // Validate maCode uniqueness
        if (maCode != null && !maCode.trim().isEmpty()) {
            Query checkCodeQuery = dbSession.createQuery("FROM KhuyenMaiEntity WHERE maCode = :maCode AND maKhuyenMai != :maKhuyenMai");
            checkCodeQuery.setParameter("maCode", maCode);
            checkCodeQuery.setParameter("maKhuyenMai", maKhuyenMai);
            if (checkCodeQuery.uniqueResult() != null) {
                errors.add("Mã code " + maCode + " đã tồn tại.");
            }
        }

        // Validate dates
        if (ngayBatDau != null) {
            Calendar todayCal = Calendar.getInstance();
            todayCal.set(Calendar.HOUR_OF_DAY, 0);
            todayCal.set(Calendar.MINUTE, 0);
            todayCal.set(Calendar.SECOND, 0);
            todayCal.set(Calendar.MILLISECOND, 0);
            Date today = todayCal.getTime();
            if (ngayBatDau.before(today)) {
                errors.add("Ngày bắt đầu không được là quá khứ.");
            }
        }
        if (ngayBatDau != null && ngayKetThuc != null) {
            if (ngayBatDau.getTime() == ngayKetThuc.getTime()) {
                errors.add("Ngày bắt đầu không được trùng ngày kết thúc.");
            }
            if (ngayKetThuc.before(ngayBatDau)) {
                errors.add("Ngày kết thúc phải sau ngày bắt đầu.");
            }
        }

        // If errors exist, return to form
        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", String.join(" ", errors));
            redirectAttributes.addFlashAttribute("editFormData", formData);
            return "redirect:/admin/promotions";
        }

        try {
            KhuyenMaiEntity khuyenMai = (KhuyenMaiEntity) dbSession.get(KhuyenMaiEntity.class, maKhuyenMai);
            if (khuyenMai == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy khuyến mãi với mã " + maKhuyenMai);
                return "redirect:/admin/promotions";
            }

            khuyenMai.setMaCode(maCode);
            khuyenMai.setMoTa(moTa);
            khuyenMai.setLoaiGiamGia(loaiGiamGia);
            khuyenMai.setGiaTriGiam(giaTriGiam);
            khuyenMai.setNgayBatDau(ngayBatDau);
            khuyenMai.setNgayKetThuc(ngayKetThuc);

            dbSession.update(khuyenMai);
            redirectAttributes.addFlashAttribute("success", "Cập nhật khuyến mãi thành công!");
            return "redirect:/admin/promotions";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật khuyến mãi: " + e.getMessage());
            redirectAttributes.addFlashAttribute("editFormData", formData);
            return "redirect:/admin/promotions";
        }
    }

    // Xóa khuyến mãi
    @Transactional
    @RequestMapping(value = "/promotions/delete/{maKhuyenMai}", method = RequestMethod.GET)
    public String deletePromotion(
            @PathVariable("maKhuyenMai") String maKhuyenMai,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/auth/login";
        }

        try {
            Session dbSession = sessionFactory.getCurrentSession();
            KhuyenMaiEntity khuyenMai = (KhuyenMaiEntity) dbSession.get(KhuyenMaiEntity.class, maKhuyenMai);

            if (khuyenMai == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy khuyến mãi với mã " + maKhuyenMai);
                return "redirect:/admin/promotions";
            }

            // Kiểm tra xem khuyến mãi đã bắt đầu chưa
            Calendar todayCal = Calendar.getInstance();
            todayCal.set(Calendar.HOUR_OF_DAY, 0);
            todayCal.set(Calendar.MINUTE, 0);
            todayCal.set(Calendar.SECOND, 0);
            todayCal.set(Calendar.MILLISECOND, 0);
            Date today = todayCal.getTime();

            if (khuyenMai.getNgayBatDau() != null && !khuyenMai.getNgayBatDau().after(today)) {
                redirectAttributes.addFlashAttribute("error", "Không thể xóa khuyến mãi " + khuyenMai.getMaCode() + " vì đã bắt đầu!");
                return "redirect:/admin/promotions";
            }

            dbSession.delete(khuyenMai);
            redirectAttributes.addFlashAttribute("success", "Xóa khuyến mãi thành công!");
            return "redirect:/admin/promotions";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa khuyến mãi: " + e.getMessage());
            return "redirect:/admin/promotions";
        }
    }
}