package movie.controller;

import movie.entity.PhuThuEntity;
import movie.model.PhuThuModel;
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
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminSurchargeController {

    @Autowired
    private SessionFactory sessionFactory;

    // Hiển thị trang quản lý phụ thu
    @RequestMapping(value = "/surcharges", method = RequestMethod.GET)
    public String showSurchargeManager(HttpSession session, Model model) {
        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/auth/login";
        }

        Session dbSession = sessionFactory.openSession();
        try {
            // Tạo mã phụ thu mới
            Query query = dbSession.createQuery("FROM PhuThuEntity ORDER BY maPhuThu DESC");
            query.setMaxResults(1);
            PhuThuEntity latestPhuThu = (PhuThuEntity) query.uniqueResult();
            String newMaPhuThu = latestPhuThu == null ? "PT001" : String.format("PT%03d",
                    Integer.parseInt(latestPhuThu.getMaPhuThu().substring(2)) + 1);

            // Lấy danh sách tất cả phụ thu
            Query allPhuThuQuery = dbSession.createQuery("FROM PhuThuEntity");
            List<PhuThuEntity> phuThuEntities = allPhuThuQuery.list();
            List<PhuThuModel> phuThuList = new ArrayList<>();
            for (PhuThuEntity entity : phuThuEntities) {
                phuThuList.add(new PhuThuModel(entity));
            }

            model.addAttribute("phuThuList", phuThuList);
            model.addAttribute("newMaPhuThu", newMaPhuThu);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi lấy danh sách phụ thu: " + e.getMessage());
        } finally {
            dbSession.close();
        }
        return "admin/surcharge_manager";
    }

    // Thêm phụ thu mới
    @Transactional
    @RequestMapping(value = "/surcharges/add", method = RequestMethod.POST)
    public String addSurcharge(
            @RequestParam("maPhuThu") String maPhuThu,
            @RequestParam("tenPhuThu") String tenPhuThu,
            @RequestParam("gia") BigDecimal gia,
            HttpSession session,
            Model model) {
        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/auth/login";
        }

        try {
            Session dbSession = sessionFactory.getCurrentSession();
            PhuThuEntity phuThu = new PhuThuEntity();
            phuThu.setMaPhuThu(maPhuThu);
            phuThu.setTenPhuThu(tenPhuThu);
            phuThu.setGia(gia);

            dbSession.save(phuThu);
            return "redirect:/admin/surcharges";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi thêm phụ thu: " + e.getMessage());
            return "admin/surcharge_manager";
        }
    }

    // Cập nhật phụ thu
    @Transactional
    @RequestMapping(value = "/surcharges/update", method = RequestMethod.POST)
    public String updateSurcharge(
            @RequestParam("maPhuThu") String maPhuThu,
            @RequestParam("tenPhuThu") String tenPhuThu,
            @RequestParam("gia") BigDecimal gia,
            HttpSession session,
            Model model) {
        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/auth/login";
        }

        try {
            Session dbSession = sessionFactory.getCurrentSession();
            PhuThuEntity phuThu = (PhuThuEntity) dbSession.get(PhuThuEntity.class, maPhuThu);
            if (phuThu == null) {
                model.addAttribute("error", "Không tìm thấy phụ thu với mã " + maPhuThu);
                return "redirect:/admin/surcharges";
            }

            phuThu.setTenPhuThu(tenPhuThu);
            phuThu.setGia(gia);
            dbSession.update(phuThu);
            return "redirect:/admin/surcharges";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi cập nhật phụ thu: " + e.getMessage());
            return "admin/surcharge_manager";
        }
    }

    // Xóa phụ thu
    @Transactional
    @RequestMapping(value = "/surcharges/delete/{maPhuThu}", method = RequestMethod.GET)
    public String deleteSurcharge(@PathVariable("maPhuThu") String maPhuThu, HttpSession session, Model model) {
        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/auth/login";
        }

        try {
            Session dbSession = sessionFactory.getCurrentSession();
            PhuThuEntity phuThu = (PhuThuEntity) dbSession.get(PhuThuEntity.class, maPhuThu);
            if (phuThu != null) {
                dbSession.delete(phuThu);
            } else {
                model.addAttribute("error", "Không tìm thấy phụ thu với mã " + maPhuThu);
            }
            return "redirect:/admin/surcharges";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi xóa phụ thu: " + e.getMessage());
            return "redirect:/admin/surcharges";
        }
    }
}