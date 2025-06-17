package movie.controller;

import movie.entity.QuyDoiDiemEntity;
import movie.model.QuyDoiDiemModel;
import movie.entity.DonHangEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminPointRedemptionController {

    @Autowired
    private SessionFactory sessionFactory;

    private static final int ITEMS_PER_PAGE = 10;

    @SuppressWarnings("deprecation")
    @RequestMapping(value = "/point-redemptions", method = RequestMethod.GET)
    public String showPointRedemptionManager(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "sort", defaultValue = "all") String sortBy,
            @RequestParam(value = "loai", defaultValue = "all") String loaiUuDai,
            HttpSession session,
            Model model) {
        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/auth/login";
        }

        Session dbSession = sessionFactory.openSession();
        try {
            // Tạo mã quy đổi mới
            Query query = dbSession.createQuery("FROM QuyDoiDiemEntity ORDER BY maQuyDoi DESC");
            query.setMaxResults(1);
            QuyDoiDiemEntity latestQuyDoi = (QuyDoiDiemEntity) query.uniqueResult();
            String newMaQuyDoi = latestQuyDoi == null ? "QD001" : String.format("QD%03d",
                    Integer.parseInt(latestQuyDoi.getMaQuyDoi().substring(2)) + 1);

            // Lấy danh sách quy đổi
            String hql = "FROM QuyDoiDiemEntity q";
            String countHql = "SELECT COUNT(q) FROM QuyDoiDiemEntity q";
            String whereClause = "";
            if (!loaiUuDai.equals("all")) {
                whereClause = " WHERE q.loaiUuDai = :loaiUuDai";
                hql += whereClause;
                countHql += whereClause;
            }

            String orderBy = "";
            switch (sortBy) {
                case "sodiem_asc":
                    orderBy = " ORDER BY q.soDiemCan ASC";
                    break;
                case "sodiem_desc":
                    orderBy = " ORDER BY q.soDiemCan DESC";
                    break;
                case "giatri_asc":
                    orderBy = " ORDER BY q.giaTriGiam ASC";
                    break;
                case "giatri_desc":
                    orderBy = " ORDER BY q.giaTriGiam DESC";
                    break;
                case "all":
                default:
                    sortBy = "all";
                    break;
            }
            hql += orderBy;

            Query countQuery = dbSession.createQuery(countHql);
            if (!loaiUuDai.equals("all")) {
                countQuery.setParameter("loaiUuDai", loaiUuDai);
            }
            Long totalItems = (Long) countQuery.uniqueResult();

            int totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);
            int start = (page - 1) * ITEMS_PER_PAGE;

            Query dataQuery = dbSession.createQuery(hql);
            if (!loaiUuDai.equals("all")) {
                dataQuery.setParameter("loaiUuDai", loaiUuDai);
            }
            dataQuery.setFirstResult(start);
            dataQuery.setMaxResults(ITEMS_PER_PAGE);
            List<QuyDoiDiemEntity> quyDoiEntities = dataQuery.list();

            List<QuyDoiDiemModel> quyDoiModels = quyDoiEntities.stream()
                    .map(QuyDoiDiemModel::new)
                    .collect(Collectors.toList());

            model.addAttribute("quyDoiList", quyDoiModels);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("loaiUuDai", loaiUuDai);
            model.addAttribute("newMaQuyDoi", newMaQuyDoi);
            model.addAttribute("debugInfo", "quyDoiList size: " + quyDoiModels.size());

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi lấy danh sách quy đổi điểm: " + e.getMessage());
        } finally {
            dbSession.close();
        }
        return "admin/point_redemption_manager";
    }

    @SuppressWarnings("deprecation")
    @RequestMapping(value = "/point-redemptions/add", method = RequestMethod.POST)
    public String addPointRedemption(
            @RequestParam("maQuyDoi") String maQuyDoi,
            @RequestParam("tenUuDai") String tenUuDai,
            @RequestParam("soDiemCan") int soDiemCan,
            @RequestParam("loaiUuDai") String loaiUuDai,
            @RequestParam("giaTriGiam") BigDecimal giaTriGiam,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/auth/login";
        }

        Session dbSession = sessionFactory.openSession();
        try {
            dbSession.beginTransaction();

            // Kiểm tra mã trùng
            Query checkMaQuery = dbSession.createQuery("FROM QuyDoiDiemEntity WHERE maQuyDoi = :maQuyDoi");
            checkMaQuery.setParameter("maQuyDoi", maQuyDoi);
            if (checkMaQuery.uniqueResult() != null) {
                redirectAttributes.addFlashAttribute("error", "Mã quy đổi " + maQuyDoi + " đã tồn tại!");
                redirectAttributes.addFlashAttribute("addMaQuyDoi", maQuyDoi);
                redirectAttributes.addFlashAttribute("addTenUuDai", tenUuDai);
                redirectAttributes.addFlashAttribute("addSoDiemCan", soDiemCan);
                redirectAttributes.addFlashAttribute("addLoaiUuDai", loaiUuDai);
                redirectAttributes.addFlashAttribute("addGiaTriGiam", giaTriGiam);
                dbSession.getTransaction().rollback();
                return "redirect:/admin/point-redemptions";
            }

            // Kiểm tra tên ưu đãi trùng
            Query checkTenQuery = dbSession.createQuery("FROM QuyDoiDiemEntity WHERE tenUuDai = :tenUuDai");
            checkTenQuery.setParameter("tenUuDai", tenUuDai);
            if (checkTenQuery.uniqueResult() != null) {
                redirectAttributes.addFlashAttribute("error", "Tên ưu đãi " + tenUuDai + " đã tồn tại!");
                redirectAttributes.addFlashAttribute("addMaQuyDoi", maQuyDoi);
                redirectAttributes.addFlashAttribute("addTenUuDai", tenUuDai);
                redirectAttributes.addFlashAttribute("addSoDiemCan", soDiemCan);
                redirectAttributes.addFlashAttribute("addLoaiUuDai", loaiUuDai);
                redirectAttributes.addFlashAttribute("addGiaTriGiam", giaTriGiam);
                dbSession.getTransaction().rollback();
                return "redirect:/admin/point-redemptions";
            }

            // Kiểm tra dữ liệu
            if (maQuyDoi == null || maQuyDoi.trim().isEmpty() || tenUuDai == null || tenUuDai.trim().isEmpty() ||
                soDiemCan <= 0 || giaTriGiam == null || giaTriGiam.compareTo(BigDecimal.ZERO) <= 0) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng nhập đầy đủ thông tin và giá trị hợp lệ.");
                redirectAttributes.addFlashAttribute("addMaQuyDoi", maQuyDoi);
                redirectAttributes.addFlashAttribute("addTenUuDai", tenUuDai);
                redirectAttributes.addFlashAttribute("addSoDiemCan", soDiemCan);
                redirectAttributes.addFlashAttribute("addLoaiUuDai", loaiUuDai);
                redirectAttributes.addFlashAttribute("addGiaTriGiam", giaTriGiam);
                dbSession.getTransaction().rollback();
                return "redirect:/admin/point-redemptions";
            }

            QuyDoiDiemEntity entity = new QuyDoiDiemEntity();
            entity.setMaQuyDoi(maQuyDoi);
            entity.setTenUuDai(tenUuDai);
            entity.setSoDiemCan(soDiemCan);
            entity.setLoaiUuDai(loaiUuDai);
            entity.setGiaTriGiam(giaTriGiam);

            dbSession.save(entity);
            dbSession.getTransaction().commit();
            redirectAttributes.addFlashAttribute("success", "Thêm quy đổi điểm thành công!");

        } catch (Exception e) {
            dbSession.getTransaction().rollback();
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm quy đổi mới: " + e.getMessage());
            redirectAttributes.addFlashAttribute("addMaQuyDoi", maQuyDoi);
            redirectAttributes.addFlashAttribute("addTenUuDai", tenUuDai);
            redirectAttributes.addFlashAttribute("addSoDiemCan", soDiemCan);
            redirectAttributes.addFlashAttribute("addLoaiUuDai", loaiUuDai);
            redirectAttributes.addFlashAttribute("addGiaTriGiam", giaTriGiam);
            return "redirect:/admin/point-redemptions";
        } finally {
            dbSession.close();
        }
        return "redirect:/admin/point-redemptions";
    }

    @SuppressWarnings("deprecation")
    @RequestMapping(value = "/point-redemptions/update", method = RequestMethod.POST)
    public String updatePointRedemption(
            @RequestParam("maQuyDoi") String maQuyDoi,
            @RequestParam("tenUuDai") String tenUuDai,
            @RequestParam("soDiemCan") int soDiemCan,
            @RequestParam("loaiUuDai") String loaiUuDai,
            @RequestParam("giaTriGiam") BigDecimal giaTriGiam,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/auth/login";
        }

        Session dbSession = sessionFactory.openSession();
        try {
            dbSession.beginTransaction();

            // Kiểm tra dữ liệu
            if (maQuyDoi == null || maQuyDoi.trim().isEmpty() || tenUuDai == null || tenUuDai.trim().isEmpty() ||
                soDiemCan <= 0 || giaTriGiam == null || giaTriGiam.compareTo(BigDecimal.ZERO) <= 0) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng nhập đầy đủ thông tin và giá trị hợp lệ.");
                dbSession.getTransaction().rollback();
                return "redirect:/admin/point-redemptions";
            }

            // Kiểm tra tên ưu đãi trùng (loại trừ chính bản ghi hiện tại)
            Query checkTenQuery = dbSession.createQuery("FROM QuyDoiDiemEntity WHERE tenUuDai = :tenUuDai AND maQuyDoi != :maQuyDoi");
            checkTenQuery.setParameter("tenUuDai", tenUuDai);
            checkTenQuery.setParameter("maQuyDoi", maQuyDoi);
            if (checkTenQuery.uniqueResult() != null) {
                redirectAttributes.addFlashAttribute("error", "Tên ưu đãi " + tenUuDai + " đã tồn tại!");
                dbSession.getTransaction().rollback();
                return "redirect:/admin/point-redemptions";
            }

            Query query = dbSession.createQuery("FROM QuyDoiDiemEntity WHERE maQuyDoi = :maQuyDoi");
            query.setParameter("maQuyDoi", maQuyDoi);
            QuyDoiDiemEntity entity = (QuyDoiDiemEntity) query.uniqueResult();

            if (entity == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy quy đổi điểm với mã " + maQuyDoi);
                dbSession.getTransaction().rollback();
                return "redirect:/admin/point-redemptions";
            }

            entity.setTenUuDai(tenUuDai);
            entity.setSoDiemCan(soDiemCan);
            entity.setLoaiUuDai(loaiUuDai);
            entity.setGiaTriGiam(giaTriGiam);

            dbSession.update(entity);
            dbSession.getTransaction().commit();
            redirectAttributes.addFlashAttribute("success", "Cập nhật quy đổi điểm thành công!");

        } catch (Exception e) {
            dbSession.getTransaction().rollback();
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật quy đổi điểm: " + e.getMessage());
        } finally {
            dbSession.close();
        }
        return "redirect:/admin/point-redemptions";
    }

    @SuppressWarnings("deprecation")
    @RequestMapping(value = "/point-redemptions/delete/{maQuyDoi}", method = RequestMethod.GET)
    public String deletePointRedemption(
            @PathVariable("maQuyDoi") String maQuyDoi,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/auth/login";
        }

        Session dbSession = sessionFactory.openSession();
        try {
            dbSession.beginTransaction();

            // Kiểm tra xem quy đổi điểm có được sử dụng trong đơn hàng không
            Query checkOrderQuery = dbSession.createQuery("FROM DonHangEntity WHERE maQuyDoi = :maQuyDoi");
            checkOrderQuery.setParameter("maQuyDoi", maQuyDoi);
            if (checkOrderQuery.uniqueResult() != null) {
                redirectAttributes.addFlashAttribute("error", "Không thể xóa quy đổi điểm vì đã được sử dụng trong đơn hàng!");
                dbSession.getTransaction().rollback();
                return "redirect:/admin/point-redemptions";
            }

            Query query = dbSession.createQuery("FROM QuyDoiDiemEntity WHERE maQuyDoi = :maQuyDoi");
            query.setParameter("maQuyDoi", maQuyDoi);
            QuyDoiDiemEntity entity = (QuyDoiDiemEntity) query.uniqueResult();

            if (entity == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy quy đổi điểm với mã " + maQuyDoi);
                dbSession.getTransaction().rollback();
                return "redirect:/admin/point-redemptions";
            }

            dbSession.delete(entity);
            dbSession.getTransaction().commit();
            redirectAttributes.addFlashAttribute("success", "Xóa quy đổi điểm thành công!");

        } catch (Exception e) {
            dbSession.getTransaction().rollback();
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa quy đổi điểm: " + e.getMessage());
        } finally {
            dbSession.close();
        }
        return "redirect:/admin/point-redemptions";
    }
}