package movie.controller;

import movie.entity.RapChieuEntity;
import movie.model.RapChieuModel;
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
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminTheaterController {

    @Autowired
    private SessionFactory sessionFactory;

    // Hiển thị danh sách rạp chiếu và form thêm mới
    @RequestMapping(value = "/theaters", method = RequestMethod.GET)
    public String showTheaterManager(HttpSession session, Model model) {
        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/auth/login";
        }

        Session dbSession = sessionFactory.openSession();
        try {
            // Lấy mã rạp mới nhất để tạo mã mới
            Query query = dbSession.createQuery("FROM RapChieuEntity ORDER BY maRapChieu DESC");
            query.setMaxResults(1);
            RapChieuEntity latestRap = (RapChieuEntity) query.uniqueResult();

            String newMaRapChieu;
            if (latestRap == null) {
                newMaRapChieu = "RC001";
            } else {
                String lastMaRap = latestRap.getMaRapChieu();
                int lastId = Integer.parseInt(lastMaRap.substring(2));
                newMaRapChieu = String.format("RC%03d", lastId + 1);
            }

            // Lấy danh sách tất cả rạp chiếu
            Query allRapQuery = dbSession.createQuery("FROM RapChieuEntity");
            List<RapChieuEntity> rapEntities = (List<RapChieuEntity>) allRapQuery.list();
            List<RapChieuModel> rapModels = new ArrayList<>();
            for (RapChieuEntity entity : rapEntities) {
                rapModels.add(new RapChieuModel(entity));
            }

            model.addAttribute("rapChieuList", rapModels);
            model.addAttribute("newMaRapChieu", newMaRapChieu);
            model.addAttribute("isEdit", false);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi lấy danh sách rạp chiếu: " + e.getMessage());
        } finally {
            dbSession.close();
        }

        return "admin/theater_manager";
    }

    // Hàm validate input cho thêm và sửa rạp
    private List<String> validateTheaterInput(Session dbSession, String maRapChieu, String tenRapChieu, String diaChi, String soDienThoaiLienHe, boolean isUpdate) {
        List<String> errors = new ArrayList<>();

        // Kiểm tra rỗng
        if (maRapChieu == null || maRapChieu.trim().isEmpty()) {
            errors.add("Mã rạp không được để trống.");
        }
        if (tenRapChieu == null || tenRapChieu.trim().isEmpty()) {
            errors.add("Tên rạp không được để trống.");
        }
        if (diaChi == null || diaChi.trim().isEmpty()) {
            errors.add("Địa chỉ không được để trống.");
        }
        if (soDienThoaiLienHe == null || !soDienThoaiLienHe.matches("\\d{10}")) {
            errors.add("Số điện thoại phải gồm đúng 10 chữ số.");
        }

        // Kiểm tra trùng lặp
        if (errors.isEmpty()) { // Chỉ kiểm tra trùng nếu không có lỗi rỗng hoặc định dạng
            String hql = isUpdate ?
                    "FROM RapChieuEntity WHERE maRapChieu != :maRap AND (:tenRap = tenRapChieu OR :diaChi = diaChi OR :sdt = soDienThoaiLienHe)" :
                    "FROM RapChieuEntity WHERE :tenRap = tenRapChieu OR :diaChi = diaChi OR :sdt = soDienThoaiLienHe";
            Query query = dbSession.createQuery(hql);
            if (isUpdate) {
                query.setParameter("maRap", maRapChieu);
            }
            query.setParameter("tenRap", tenRapChieu.trim());
            query.setParameter("diaChi", diaChi.trim());
            query.setParameter("sdt", soDienThoaiLienHe);
            if (!query.list().isEmpty()) {
                // Kiểm tra từng trường cụ thể
                if (isUpdate) {
                    hql = "FROM RapChieuEntity WHERE maRapChieu != :maRap AND tenRapChieu = :tenRap";
                } else {
                    hql = "FROM RapChieuEntity WHERE tenRapChieu = :tenRap";
                }
                query = dbSession.createQuery(hql);
                if (isUpdate) {
                    query.setParameter("maRap", maRapChieu);
                }
                query.setParameter("tenRap", tenRapChieu.trim());
                if (!query.list().isEmpty()) {
                    errors.add("Tên rạp đã tồn tại.");
                }

                if (isUpdate) {
                    hql = "FROM RapChieuEntity WHERE maRapChieu != :maRap AND diaChi = :diaChi";
                } else {
                    hql = "FROM RapChieuEntity WHERE diaChi = :diaChi";
                }
                query = dbSession.createQuery(hql);
                if (isUpdate) {
                    query.setParameter("maRap", maRapChieu);
                }
                query.setParameter("diaChi", diaChi.trim());
                if (!query.list().isEmpty()) {
                    errors.add("Địa chỉ đã tồn tại.");
                }

                if (isUpdate) {
                    hql = "FROM RapChieuEntity WHERE maRapChieu != :maRap AND soDienThoaiLienHe = :sdt";
                } else {
                    hql = "FROM RapChieuEntity WHERE soDienThoaiLienHe = :sdt";
                }
                query = dbSession.createQuery(hql);
                if (isUpdate) {
                    query.setParameter("maRap", maRapChieu);
                }
                query.setParameter("sdt", soDienThoaiLienHe);
                if (!query.list().isEmpty()) {
                    errors.add("Số điện thoại đã tồn tại.");
                }
            }
        }

        return errors;
    }

    // Thêm rạp chiếu mới
    @Transactional
    @RequestMapping(value = "/theaters/add", method = RequestMethod.POST)
    public String processAddTheater(
            @RequestParam("maRapChieu") String maRapChieu,
            @RequestParam("tenRapChieu") String tenRapChieu,
            @RequestParam("diaChi") String diaChi,
            @RequestParam("soDienThoaiLienHe") String soDienThoaiLienHe,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/auth/login";
        }

        Session dbSession = sessionFactory.getCurrentSession();

        // Validate input
        List<String> errors = validateTheaterInput(dbSession, maRapChieu, tenRapChieu, diaChi, soDienThoaiLienHe, false);

        // Nếu có lỗi, redirect và lưu dữ liệu đã nhập
        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", String.join(" ", errors));
            redirectAttributes.addFlashAttribute("tenRapChieu", tenRapChieu);
            redirectAttributes.addFlashAttribute("diaChi", diaChi);
            redirectAttributes.addFlashAttribute("soDienThoaiLienHe", soDienThoaiLienHe);
            redirectAttributes.addFlashAttribute("newMaRapChieu", maRapChieu);
            return "redirect:/admin/theaters";
        }

        // Lưu dữ liệu nếu không có lỗi
        try {
            RapChieuEntity rap = new RapChieuEntity();
            rap.setMaRapChieu(maRapChieu);
            rap.setTenRapChieu(tenRapChieu.trim());
            rap.setDiaChi(diaChi.trim());
            rap.setSoDienThoaiLienHe(soDienThoaiLienHe);
            dbSession.save(rap);
            redirectAttributes.addFlashAttribute("success", "Thêm rạp " + tenRapChieu.trim() + " thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm rạp: " + e.getMessage());
        }

        return "redirect:/admin/theaters";
    }

    // Cập nhật thông tin rạp chiếu
    @Transactional
    @RequestMapping(value = "/theaters/update", method = RequestMethod.POST)
    public String processUpdateTheater(
            @RequestParam("maRapChieu") String maRapChieu,
            @RequestParam("tenRapChieu") String tenRapChieu,
            @RequestParam("diaChi") String diaChi,
            @RequestParam("soDienThoaiLienHe") String soDienThoaiLienHe,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/auth/login";
        }

        Session dbSession = sessionFactory.getCurrentSession();

        // Kiểm tra rạp tồn tại
        RapChieuEntity rap = (RapChieuEntity) dbSession.get(RapChieuEntity.class, maRapChieu);
        if (rap == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy rạp chiếu với mã " + maRapChieu);
            return "redirect:/admin/theaters";
        }

        // Validate input
        List<String> errors = validateTheaterInput(dbSession, maRapChieu, tenRapChieu, diaChi, soDienThoaiLienHe, true);

        // Nếu có lỗi, redirect và lưu dữ liệu đã nhập
        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", String.join(" ", errors));
            redirectAttributes.addFlashAttribute("tenRapChieu_edit", tenRapChieu);
            redirectAttributes.addFlashAttribute("diaChi_edit", diaChi);
            redirectAttributes.addFlashAttribute("soDienThoaiLienHe_edit", soDienThoaiLienHe);
            redirectAttributes.addFlashAttribute("maRapChieu_edit", maRapChieu);
            return "redirect:/admin/theaters";
        }

        // Lưu nếu không có lỗi
        try {
            rap.setTenRapChieu(tenRapChieu.trim());
            rap.setDiaChi(diaChi.trim());
            rap.setSoDienThoaiLienHe(soDienThoaiLienHe);
            dbSession.update(rap);
            redirectAttributes.addFlashAttribute("success", "Cập nhật rạp " + tenRapChieu.trim() + " thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật rạp: " + e.getMessage());
        }

        return "redirect:/admin/theaters";
    }

    // Xóa rạp chiếu
    @Transactional
    @RequestMapping(value = "/theaters/delete/{maRapChieu}", method = RequestMethod.GET)
    public String deleteTheater(
            @PathVariable("maRapChieu") String maRapChieu,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/auth/login";
        }

        try {
            Session dbSession = sessionFactory.getCurrentSession();
            RapChieuEntity rap = (RapChieuEntity) dbSession.get(RapChieuEntity.class, maRapChieu);

            if (rap == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy rạp chiếu với mã " + maRapChieu);
                return "redirect:/admin/theaters";
            }

            // Kiểm tra xem rạp có phòng chiếu không
            Query query = dbSession.createQuery("FROM PhongChieuEntity WHERE rapChieu.maRapChieu = :maRap");
            query.setParameter("maRap", maRapChieu);
            List<?> phongChieuList = query.list();
            if (!phongChieuList.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Không thể xóa rạp " + rap.getTenRapChieu() + " vì vẫn còn phòng chiếu liên kết.");
                return "redirect:/admin/theaters";
            }

            // Xóa rạp nếu không có phòng chiếu liên kết
            dbSession.delete(rap);
            redirectAttributes.addFlashAttribute("success", "Xóa rạp " + rap.getTenRapChieu() + " thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa rạp: " + e.getMessage());
        }

        return "redirect:/admin/theaters";
    }
}