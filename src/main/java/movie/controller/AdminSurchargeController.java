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

    @RequestMapping(value = "/surcharges", method = RequestMethod.GET)
    public String showSurchargeManager(HttpSession session, Model model) {
        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/auth/login";
        }

        Session dbSession = sessionFactory.openSession();
        try {
            Query query = dbSession.createQuery("FROM PhuThuEntity ORDER BY maPhuThu DESC");
            query.setMaxResults(1);
            PhuThuEntity latestPhuThu = (PhuThuEntity) query.uniqueResult();
            String newMaPhuThu = latestPhuThu == null ? "PT001" : String.format("PT%03d",
                    Integer.parseInt(latestPhuThu.getMaPhuThu().substring(2)) + 1);

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
            if (maPhuThu == null || maPhuThu.trim().isEmpty() || tenPhuThu == null || tenPhuThu.trim().isEmpty() || gia == null || gia.compareTo(BigDecimal.ZERO) <= 0) {
                model.addAttribute("error", "Vui lòng nhập đầy đủ thông tin và giá phụ thu phải lớn hơn 0.");
                return "admin/surcharge_manager";
            }

            Session dbSession = sessionFactory.getCurrentSession();
            PhuThuEntity phuThu = new PhuThuEntity();
            phuThu.setMaPhuThu(maPhuThu);
            phuThu.setTenPhuThu(tenPhuThu);
            phuThu.setGiaPhuThu(gia);

            dbSession.save(phuThu);
            return "redirect:/admin/surcharges";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi thêm phụ thu: " + e.getMessage());
            return "admin/surcharge_manager";
        }
    }

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
            if (maPhuThu == null || maPhuThu.trim().isEmpty() || tenPhuThu == null || tenPhuThu.trim().isEmpty() || gia == null || gia.compareTo(BigDecimal.ZERO) <= 0) {
                model.addAttribute("error", "Vui lòng nhập đầy đủ thông tin và giá phụ thu phải lớn hơn 0.");
                return "admin/surcharge_manager";
            }

            Session dbSession = sessionFactory.getCurrentSession();
            PhuThuEntity phuThu = (PhuThuEntity) dbSession.get(PhuThuEntity.class, maPhuThu);
            if (phuThu == null) {
                model.addAttribute("error", "Không tìm thấy phụ thu với mã " + maPhuThu);
                return "redirect:/admin/surcharges";
            }

            phuThu.setTenPhuThu(tenPhuThu);
            phuThu.setGiaPhuThu(gia);
            dbSession.update(phuThu);
            return "redirect:/admin/surcharges";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi cập nhật phụ thu: " + e.getMessage());
            return "admin/surcharge_manager";
        }
    }

    @Transactional
    @RequestMapping(value = "/surcharges/delete/{maPhuThu}", method = RequestMethod.GET)
    public String deleteSurcharge(@PathVariable("maPhuThu") String maPhuThu, HttpSession session, Model model) {
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

            if (!phuThu.getSuatChieus().isEmpty()) {
                model.addAttribute("error", "Không thể xóa phụ thu vì nó đang được liên kết với các suất chiếu.");
                return "redirect:/admin/surcharges";
            }

            dbSession.delete(phuThu);
            return "redirect:/admin/surcharges";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi xóa phụ thu: " + e.getMessage());
            return "redirect:/admin/surcharges";
        }
    }
}