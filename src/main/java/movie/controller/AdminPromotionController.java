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

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminPromotionController {

    @Autowired
    private SessionFactory sessionFactory;

    // Hiển thị trang quản lý khuyến mãi
    @RequestMapping(value = "/promotions", method = RequestMethod.GET)
    public String showPromotionManager(HttpSession session, Model model) {
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

            // Lấy danh sách tất cả khuyến mãi
            Query allKhuyenMaiQuery = dbSession.createQuery("FROM KhuyenMaiEntity");
            List<KhuyenMaiEntity> khuyenMaiEntities = allKhuyenMaiQuery.list();
            List<KhuyenMaiModel> khuyenMaiList = new ArrayList<>();
            for (KhuyenMaiEntity entity : khuyenMaiEntities) {
                khuyenMaiList.add(new KhuyenMaiModel(entity));
            }

            model.addAttribute("khuyenMaiList", khuyenMaiList);
            model.addAttribute("newMaKhuyenMai", newMaKhuyenMai);
            model.addAttribute("isEdit", false);

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
            @RequestParam("apDungCho") String apDungCho,
            HttpSession session,
            Model model) {
        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/auth/login";
        }

        try {
            Session dbSession = sessionFactory.getCurrentSession();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date ngayBatDau = dateFormat.parse(ngayBatDauStr);
            Date ngayKetThuc = dateFormat.parse(ngayKetThucStr);

            KhuyenMaiEntity khuyenMai = new KhuyenMaiEntity();
            khuyenMai.setMaKhuyenMai(maKhuyenMai);
            khuyenMai.setMaCode(maCode);
            khuyenMai.setMoTa(moTa);
            khuyenMai.setLoaiGiamGia(loaiGiamGia);
            khuyenMai.setGiaTriGiam(giaTriGiam);
            khuyenMai.setNgayBatDau(ngayBatDau);
            khuyenMai.setNgayKetThuc(ngayKetThuc);
            khuyenMai.setApDungCho(apDungCho);

            dbSession.save(khuyenMai);
            return "redirect:/admin/promotions";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi thêm khuyến mãi: " + e.getMessage());
            return "admin/promotion_manager";
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
            @RequestParam("giaTriGiam") BigDecimal giaTriGiam,
            @RequestParam("ngayBatDau") String ngayBatDauStr,
            @RequestParam("ngayKetThuc") String ngayKetThucStr,
            @RequestParam("apDungCho") String apDungCho,
            HttpSession session,
            Model model) {
        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/auth/login";
        }

        try {
            Session dbSession = sessionFactory.getCurrentSession();

            KhuyenMaiEntity khuyenMai = (KhuyenMaiEntity) dbSession.get(KhuyenMaiEntity.class, maKhuyenMai);
            if (khuyenMai == null) {
                model.addAttribute("error", "Không tìm thấy khuyến mãi với mã " + maKhuyenMai);
                return "redirect:/admin/promotions";
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date ngayBatDau = dateFormat.parse(ngayBatDauStr);
            Date ngayKetThuc = dateFormat.parse(ngayKetThucStr);

            khuyenMai.setMaCode(maCode);
            khuyenMai.setMoTa(moTa);
            khuyenMai.setLoaiGiamGia(loaiGiamGia);
            khuyenMai.setGiaTriGiam(giaTriGiam);
            khuyenMai.setNgayBatDau(ngayBatDau);
            khuyenMai.setNgayKetThuc(ngayKetThuc);
            khuyenMai.setApDungCho(apDungCho);

            dbSession.update(khuyenMai);
            return "redirect:/admin/promotions";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi cập nhật khuyến mãi: " + e.getMessage());
            return "admin/promotion_manager";
        }
    }

    // Xóa khuyến mãi
    @Transactional
    @RequestMapping(value = "/promotions/delete/{maKhuyenMai}", method = RequestMethod.GET)
    public String deletePromotion(@PathVariable("maKhuyenMai") String maKhuyenMai, HttpSession session, Model model) {
        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/auth/login";
        }

        try {
            Session dbSession = sessionFactory.getCurrentSession();
            KhuyenMaiEntity khuyenMai = (KhuyenMaiEntity) dbSession.get(KhuyenMaiEntity.class, maKhuyenMai);

            if (khuyenMai != null) {
                dbSession.delete(khuyenMai);
            } else {
                model.addAttribute("error", "Không tìm thấy khuyến mãi với mã " + maKhuyenMai);
            }
            return "redirect:/admin/promotions";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi xóa khuyến mãi: " + e.getMessage());
            return "redirect:/admin/promotions";
        }
    }
}