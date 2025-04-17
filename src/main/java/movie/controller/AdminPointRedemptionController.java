package movie.controller;

import movie.entity.QuyDoiDiemEntity;
import movie.model.QuyDoiDiemModel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            Model model) {
        Session dbSession = sessionFactory.openSession();
        try {
            // Xây dựng truy vấn HQL
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

            // Đếm tổng số quy đổi
            Query countQuery = dbSession.createQuery(countHql);
            if (!loaiUuDai.equals("all")) {
                countQuery.setParameter("loaiUuDai", loaiUuDai);
            }
            Long totalItems = (Long) countQuery.uniqueResult();

            // Tính phân trang
            int totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);
            int start = (page - 1) * ITEMS_PER_PAGE;

            // Lấy danh sách quy đổi phân trang
            Query query = dbSession.createQuery(hql);
            if (!loaiUuDai.equals("all")) {
                query.setParameter("loaiUuDai", loaiUuDai);
            }
            query.setFirstResult(start);
            query.setMaxResults(ITEMS_PER_PAGE);
            List<QuyDoiDiemEntity> quyDoiEntities = query.list();

            // Chuyển sang QuyDoiDiemModel
            List<QuyDoiDiemModel> quyDoiModels = quyDoiEntities.stream()
                    .map(QuyDoiDiemModel::new)
                    .collect(Collectors.toList());

            model.addAttribute("quyDoiList", quyDoiModels);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("loaiUuDai", loaiUuDai);

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
    @ResponseBody
    public Map<String, Object> addPointRedemption(@RequestBody QuyDoiDiemModel redemption) {
        Map<String, Object> response = new HashMap<>();
        Session dbSession = sessionFactory.openSession();
        try {
            dbSession.beginTransaction();

            // Kiểm tra mã quy đổi đã tồn tại
            Query checkQuery = dbSession.createQuery("FROM QuyDoiDiemEntity WHERE maQuyDoi = :maQuyDoi");
            checkQuery.setParameter("maQuyDoi", redemption.getMaQuyDoi());
            if (checkQuery.uniqueResult() != null) {
                response.put("error", "Mã quy đổi " + redemption.getMaQuyDoi() + " đã tồn tại!");
                return response;
            }

            // Tạo entity mới
            QuyDoiDiemEntity entity = new QuyDoiDiemEntity();
            entity.setMaQuyDoi(redemption.getMaQuyDoi());
            entity.setTenUuDai(redemption.getTenUuDai());
            entity.setSoDiemCan(redemption.getSoDiemCan());
            entity.setLoaiUuDai(redemption.getLoaiUuDai());
            entity.setGiaTriGiam(redemption.getGiaTriGiam()); // Sử dụng trực tiếp BigDecimal

            dbSession.save(entity);
            dbSession.getTransaction().commit();
            response.put("success", true);

        } catch (Exception e) {
            dbSession.getTransaction().rollback();
            e.printStackTrace();
            response.put("error", "Lỗi khi thêm quy đổi điểm: " + e.getMessage());
        } finally {
            dbSession.close();
        }
        return response;
    }

    @SuppressWarnings("deprecation")
    @RequestMapping(value = "/point-redemptions/edit/{maQuyDoi}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getPointRedemptionForEdit(@PathVariable("maQuyDoi") String maQuyDoi) {
        Map<String, Object> response = new HashMap<>();
        Session dbSession = sessionFactory.openSession();
        try {
            Query query = dbSession.createQuery("FROM QuyDoiDiemEntity WHERE maQuyDoi = :maQuyDoi");
            query.setParameter("maQuyDoi", maQuyDoi);
            QuyDoiDiemEntity entity = (QuyDoiDiemEntity) query.uniqueResult();

            if (entity == null) {
                response.put("error", "Không tìm thấy quy đổi điểm với mã " + maQuyDoi);
                return response;
            }

            QuyDoiDiemModel model = new QuyDoiDiemModel(entity);
            response.put("maQuyDoi", model.getMaQuyDoi());
            response.put("tenUuDai", model.getTenUuDai());
            response.put("soDiemCan", model.getSoDiemCan());
            response.put("loaiUuDai", model.getLoaiUuDai());
            response.put("giaTriGiam", model.getGiaTriGiam());

        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Lỗi khi lấy thông tin quy đổi điểm: " + e.getMessage());
        } finally {
            dbSession.close();
        }
        return response;
    }

    @SuppressWarnings("deprecation")
    @RequestMapping(value = "/point-redemptions/update", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> updatePointRedemption(@RequestBody QuyDoiDiemModel redemption) {
        Map<String, Object> response = new HashMap<>();
        Session dbSession = sessionFactory.openSession();
        try {
            dbSession.beginTransaction();

            Query query = dbSession.createQuery("FROM QuyDoiDiemEntity WHERE maQuyDoi = :maQuyDoi");
            query.setParameter("maQuyDoi", redemption.getMaQuyDoi());
            QuyDoiDiemEntity entity = (QuyDoiDiemEntity) query.uniqueResult();

            if (entity == null) {
                response.put("error", "Không tìm thấy quy đổi điểm với mã " + redemption.getMaQuyDoi());
                return response;
            }

            entity.setTenUuDai(redemption.getTenUuDai());
            entity.setSoDiemCan(redemption.getSoDiemCan());
            entity.setLoaiUuDai(redemption.getLoaiUuDai());
            entity.setGiaTriGiam(redemption.getGiaTriGiam()); // Sử dụng trực tiếp BigDecimal

            dbSession.update(entity);
            dbSession.getTransaction().commit();
            response.put("success", true);

        } catch (Exception e) {
            dbSession.getTransaction().rollback();
            e.printStackTrace();
            response.put("error", "Lỗi khi cập nhật quy đổi điểm: " + e.getMessage());
        } finally {
            dbSession.close();
        }
        return response;
    }

    @SuppressWarnings("deprecation")
    @RequestMapping(value = "/point-redemptions/delete/{maQuyDoi}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> deletePointRedemption(@PathVariable("maQuyDoi") String maQuyDoi) {
        Map<String, Object> response = new HashMap<>();
        Session dbSession = sessionFactory.openSession();
        try {
            dbSession.beginTransaction();

            Query query = dbSession.createQuery("FROM QuyDoiDiemEntity WHERE maQuyDoi = :maQuyDoi");
            query.setParameter("maQuyDoi", maQuyDoi);
            QuyDoiDiemEntity entity = (QuyDoiDiemEntity) query.uniqueResult();

            if (entity == null) {
                response.put("error", "Không tìm thấy quy đổi điểm với mã " + maQuyDoi);
                return response;
            }

            dbSession.delete(entity);
            dbSession.getTransaction().commit();
            response.put("success", true);

        } catch (Exception e) {
            dbSession.getTransaction().rollback();
            e.printStackTrace();
            response.put("error", "Lỗi khi xóa quy đổi điểm: " + e.getMessage());
        } finally {
            dbSession.close();
        }
        return response;
    }
}