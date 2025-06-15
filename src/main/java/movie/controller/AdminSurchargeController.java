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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String showSurchargeManager(HttpSession session, Model model, @RequestParam(value = "search", required = false) String search) {
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

            String hql = "FROM PhuThuEntity";
            if (search != null && !search.trim().isEmpty()) {
                hql += " WHERE tenPhuThu LIKE :search";
            }
            Query allPhuThuQuery = dbSession.createQuery(hql);
            if (search != null && !search.trim().isEmpty()) {
                allPhuThuQuery.setParameter("search", "%" + search.trim() + "%");
            }
            List<PhuThuEntity> phuThuEntities = allPhuThuQuery.list();
            List<PhuThuModel> phuThuList = new ArrayList<>();
            for (PhuThuEntity entity : phuThuEntities) {
                phuThuList.add(new PhuThuModel(entity));
            }

            model.addAttribute("phuThuList", phuThuList);
            model.addAttribute("newMaPhuThu", newMaPhuThu);
            model.addAttribute("search", search);

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
            Model model,
            RedirectAttributes redirectAttributes) {
        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/auth/login";
        }

        try {
            // Validate input
            if (maPhuThu == null || maPhuThu.trim().isEmpty() || tenPhuThu == null || tenPhuThu.trim().isEmpty() || gia == null || gia.compareTo(BigDecimal.ZERO) <= 0) {
                model.addAttribute("error", "Vui lòng nhập đầy đủ thông tin và giá phụ thu phải lớn hơn 0.");
                model.addAttribute("addMaPhuThu", maPhuThu);
                model.addAttribute("addTenPhuThu", tenPhuThu);
                model.addAttribute("addGia", gia);
                redirectAttributes.addFlashAttribute("error", "Vui lòng nhập đầy đủ thông tin và giá phụ thu phải lớn hơn 0.");
                return "redirect:/admin/surcharges";
            }

            Session dbSession = sessionFactory.getCurrentSession();
            // Check for duplicate surcharge name
            Query checkNameQuery = dbSession.createQuery("FROM PhuThuEntity WHERE tenPhuThu = :tenPhuThu");
            checkNameQuery.setParameter("tenPhuThu", tenPhuThu.trim());
            if (!checkNameQuery.list().isEmpty()) {
                model.addAttribute("error", "Tên phụ thu '" + tenPhuThu + "' đã tồn tại.");
                model.addAttribute("addMaPhuThu", maPhuThu);
                model.addAttribute("addTenPhuThu", tenPhuThu);
                model.addAttribute("addGia", gia);
                redirectAttributes.addFlashAttribute("error", "Tên phụ thu '" + tenPhuThu + "' đã tồn tại.");
                return "redirect:/admin/surcharges";
            }

            // Save new surcharge
            PhuThuEntity phuThu = new PhuThuEntity();
            phuThu.setMaPhuThu(maPhuThu);
            phuThu.setTenPhuThu(tenPhuThu.trim());
            phuThu.setGiaPhuThu(gia);

            dbSession.save(phuThu);
            redirectAttributes.addFlashAttribute("success", "Thêm phụ thu thành công!");
            return "redirect:/admin/surcharges";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi thêm phụ thu: " + e.getMessage());
            model.addAttribute("addMaPhuThu", maPhuThu);
            model.addAttribute("addTenPhuThu", tenPhuThu);
            model.addAttribute("addGia", gia);
            return showSurchargeManager(session, model, null);
        }
    }

    @Transactional
    @RequestMapping(value = "/surcharges/update", method = RequestMethod.POST)
    public String updateSurcharge(
            @RequestParam("maPhuThu") String maPhuThu,
            @RequestParam("tenPhuThu") String tenPhuThu,
            @RequestParam("gia") BigDecimal gia,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/auth/login";
        }

        try {
            if (maPhuThu == null || maPhuThu.trim().isEmpty() || tenPhuThu == null || tenPhuThu.trim().isEmpty() || gia == null || gia.compareTo(BigDecimal.ZERO) <= 0) {
                model.addAttribute("error", "Vui lòng nhập đầy đủ thông tin và giá phụ thu phải lớn hơn 0.");
                return showSurchargeManager(session, model, null);
            }

            Session dbSession = sessionFactory.getCurrentSession();
            PhuThuEntity phuThu = (PhuThuEntity) dbSession.get(PhuThuEntity.class, maPhuThu);
            if (phuThu == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy phụ thu với mã " + maPhuThu);
                return "redirect:/admin/surcharges";
            }

            // Check for duplicate name (excluding current surcharge)
            Query checkNameQuery = dbSession.createQuery("FROM PhuThuEntity WHERE tenPhuThu = :tenPhuThu AND maPhuThu != :maPhuThu");
            checkNameQuery.setParameter("tenPhuThu", tenPhuThu.trim());
            checkNameQuery.setParameter("maPhuThu", maPhuThu);
            if (!checkNameQuery.list().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Tên phụ thu '" + tenPhuThu + "' đã tồn tại.");
                return "redirect:/admin/surcharges";
            }

            phuThu.setTenPhuThu(tenPhuThu.trim());
            phuThu.setGiaPhuThu(gia);
            dbSession.update(phuThu);
            redirectAttributes.addFlashAttribute("success", "Cập nhật phụ thu thành công!");
            return "redirect:/admin/surcharges";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi cập nhật phụ thu: " + e.getMessage());
            return showSurchargeManager(session, model, null);
        }
    }

    @Transactional
    @RequestMapping(value = "/surcharges/delete/{maPhuThu}", method = RequestMethod.GET)
    public String deleteSurcharge(
            @PathVariable("maPhuThu") String maPhuThu,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/auth/login";
        }

        try {
            Session dbSession = sessionFactory.getCurrentSession();
            PhuThuEntity phuThu = (PhuThuEntity) dbSession.get(PhuThuEntity.class, maPhuThu);
            if (phuThu == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy phụ thu với mã " + maPhuThu);
                return "redirect:/admin/surcharges";
            }

            // Check if surcharge is linked to any showtimes
            if (!phuThu.getSuatChieus().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Không thể xóa phụ thu vì nó đang được liên kết với các suất chiếu.");
                return "redirect:/admin/surcharges";
            }

            dbSession.delete(phuThu);
            redirectAttributes.addFlashAttribute("success", "Xóa phụ thu thành công!");
            return "redirect:/admin/surcharges";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa phụ thu: " + e.getMessage());
            return "redirect:/admin/surcharges";
        }
    }
}