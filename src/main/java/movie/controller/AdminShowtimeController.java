package movie.controller;

import movie.entity.PhimEntity;
import movie.entity.PhongChieuEntity;
import movie.entity.RapChieuEntity;
import movie.entity.SuatChieuEntity;
import movie.entity.PhuThuEntity;
import movie.model.PhimModel;
import movie.model.PhongChieuModel;
import movie.model.RapChieuModel;
import movie.model.SuatChieuModel;
import movie.model.PhuThuModel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.HashMap;

@Controller
@RequestMapping("/admin")
public class AdminShowtimeController {

    @Autowired
    private SessionFactory sessionFactory;

    private static final int ITEMS_PER_PAGE = 25; // 25 bản ghi/trang
    private static final int PAGES_TO_SHOW = 5; // Hiển thị 5 trang

    // Hiển thị trang quản lý suất chiếu
    @SuppressWarnings("deprecation")
    @RequestMapping(value = "/showtimes", method = RequestMethod.GET)
    public String showShowtimeManager(
            Model model,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "view", defaultValue = "calendar") String view) {
        Session dbSession = sessionFactory.openSession();
        try {
            // Truy vấn đếm tổng số suất chiếu
            Query countQuery = dbSession.createQuery("SELECT COUNT(s) FROM SuatChieuEntity s");
            Long totalItems = (Long) countQuery.uniqueResult();
            int totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);
            int start = (page - 1) * ITEMS_PER_PAGE;

            // Truy vấn suất chiếu với phân trang và JOIN FETCH
            Query suatChieuQuery = dbSession.createQuery(
                    "FROM SuatChieuEntity s " +
                    "JOIN FETCH s.phim p " +
                    "JOIN FETCH s.phongChieu pc " +
                    "JOIN FETCH pc.rapChieu r " +
                    "LEFT JOIN FETCH s.phuThus pt " + // Thêm LEFT JOIN FETCH để lấy danh sách phụ thu
                    "ORDER BY s.ngayGioChieu DESC"
                );
                suatChieuQuery.setFirstResult(start);
                suatChieuQuery.setMaxResults(ITEMS_PER_PAGE);
                List<SuatChieuEntity> suatChieuEntities = suatChieuQuery.list();
                List<SuatChieuModel> suatChieuModels = new ArrayList<>();
                for (SuatChieuEntity entity : suatChieuEntities) {
                    suatChieuModels.add(new SuatChieuModel(entity)); // Tạo model, bao gồm danhSachTenPhuThu
                }
                model.addAttribute("suatChieuList", suatChieuModels);

            // Lấy danh sách phim
            Query phimQuery = dbSession.createQuery("FROM PhimEntity");
            List<PhimEntity> phimEntities = phimQuery.list();
            List<PhimModel> phimModels = new ArrayList<>();
            for (PhimEntity entity : phimEntities) {
                phimModels.add(new PhimModel(entity));
            }
            System.out.println("phimList size: " + phimModels.size());
            model.addAttribute("phimList", phimModels);

            // Lấy danh sách rạp chiếu
            Query rapQuery = dbSession.createQuery("FROM RapChieuEntity");
            List<RapChieuEntity> rapEntities = rapQuery.list();
            List<RapChieuModel> rapModels = new ArrayList<>();
            for (RapChieuEntity entity : rapEntities) {
                rapModels.add(new RapChieuModel(entity));
            }
            System.out.println("rapList size: " + rapModels.size());
            model.addAttribute("rapList", rapModels);

            // Lấy danh sách phòng chiếu
            Query phongQuery = dbSession.createQuery("FROM PhongChieuEntity");
            List<PhongChieuEntity> phongEntities = phongQuery.list();
            List<PhongChieuModel> phongModels = new ArrayList<>();
            for (PhongChieuEntity entity : phongEntities) {
                phongModels.add(new PhongChieuModel(entity));
            }
            System.out.println("phongList size: " + phongModels.size());
            model.addAttribute("phongList", phongModels);

            // Lấy danh sách phụ thu
            Query phuThuQuery = dbSession.createQuery("FROM PhuThuEntity");
            List<PhuThuEntity> phuThuEntities = phuThuQuery.list();
            List<PhuThuModel> phuThuModels = new ArrayList<>();
            for (PhuThuEntity entity : phuThuEntities) {
                phuThuModels.add(new PhuThuModel(entity));
            }
            System.out.println("phuThuList size: " + phuThuModels.size());
            model.addAttribute("phuThuList", phuThuModels);

            // Tạo Map phimMap để hiển thị thông tin phim theo mã phim
            Map<String, PhimModel> phimMap = new HashMap<>();
            for (PhimModel phim : phimModels) {
                phimMap.put(phim.getMaPhim(), phim);
            }
            model.addAttribute("phimMap", phimMap);

            // Tạo Map phongMap để hiển thị thông tin phòng chiếu theo mã phòng
            Map<String, PhongChieuModel> phongMap = new HashMap<>();
            for (PhongChieuModel phong : phongModels) {
                phongMap.put(phong.getMaPhongChieu(), phong);
            }
            model.addAttribute("phongMap", phongMap);

            // Tạo Map rapMap để hiển thị thông tin rạp chiếu theo mã rạp
            Map<String, RapChieuModel> rapMap = new HashMap<>();
            for (RapChieuModel rap : rapModels) {
                rapMap.put(rap.getMaRapChieu(), rap);
            }
            model.addAttribute("rapMap", rapMap);

            // Thêm thời gian hiện tại để xác định trạng thái suất chiếu
            model.addAttribute("now", new Date());

            // Khởi tạo mã suất chiếu mới
            Query latestQuery = dbSession.createQuery("FROM SuatChieuEntity ORDER BY maSuatChieu DESC");
            latestQuery.setMaxResults(1);
            SuatChieuEntity latestSuat = (SuatChieuEntity) latestQuery.uniqueResult();
            String maSuatChieu = latestSuat == null ? "SC001" : String.format("SC%03d",
                    Integer.parseInt(latestSuat.getMaSuatChieu().substring(2)) + 1);

            SuatChieuModel suatChieuModel = new SuatChieuModel();
            suatChieuModel.setMaSuatChieu(maSuatChieu);
            model.addAttribute("suatChieuModel", suatChieuModel);
            model.addAttribute("isEdit", false);

            // Thêm thông tin phân trang
            List<Integer> pageRange = new ArrayList<>();
            int startPage = Math.max(1, page - (PAGES_TO_SHOW / 2));
            int endPage = Math.min(totalPages, startPage + PAGES_TO_SHOW - 1);
            startPage = Math.max(1, endPage - PAGES_TO_SHOW + 1);
            for (int i = startPage; i <= endPage; i++) {
                pageRange.add(i);
            }
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageRange", pageRange);

            // Thêm trạng thái giao diện
            model.addAttribute("view", view);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi lấy danh sách suất chiếu");
        } finally {
            dbSession.close();
        }
        return "admin/showtime_manager";
    }

    // Hiển thị form chỉnh sửa suất chiếu
    @SuppressWarnings("deprecation")
    @RequestMapping(value = "/showtimes/edit/{maSuatChieu}", method = RequestMethod.GET)
    public String showEditShowtimeForm(
            @PathVariable("maSuatChieu") String maSuatChieu,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "view", defaultValue = "calendar") String view,
            Model model) {
        Session dbSession = sessionFactory.openSession();
        try {
            SuatChieuEntity suatChieu = (SuatChieuEntity) dbSession.get(SuatChieuEntity.class, maSuatChieu);
            if (suatChieu == null) {
                model.addAttribute("error", "Không tìm thấy suất chiếu với mã " + maSuatChieu);
                return "redirect:/admin/showtimes?view=" + view;
            }

            // Lấy danh sách suất chiếu với phân trang
            Query countQuery = dbSession.createQuery("SELECT COUNT(s) FROM SuatChieuEntity s");
            Long totalItems = (Long) countQuery.uniqueResult();
            int totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);
            int start = (page - 1) * ITEMS_PER_PAGE;

            Query suatChieuQuery = dbSession.createQuery(
                "FROM SuatChieuEntity s " +
                "JOIN FETCH s.phim p " +
                "JOIN FETCH s.phongChieu pc " +
                "JOIN FETCH pc.rapChieu r " +
                "LEFT JOIN FETCH s.phuThus pt " + // Đảm bảo lấy danh sách phụ thu
                "ORDER BY s.ngayGioChieu DESC"
            );
            suatChieuQuery.setFirstResult(start);
            suatChieuQuery.setMaxResults(ITEMS_PER_PAGE);
            List<SuatChieuEntity> suatChieuEntities = suatChieuQuery.list();
            List<SuatChieuModel> suatChieuModels = new ArrayList<>();
            for (SuatChieuEntity entity : suatChieuEntities) {
                suatChieuModels.add(new SuatChieuModel(entity));
            }
            model.addAttribute("suatChieuList", suatChieuModels);

            // Lấy danh sách phụ thu
            Query phuThuQuery = dbSession.createQuery("FROM PhuThuEntity");
            List<PhuThuEntity> phuThuEntities = phuThuQuery.list();
            List<PhuThuModel> phuThuModels = new ArrayList<>();
            for (PhuThuEntity entity : phuThuEntities) {
                phuThuModels.add(new PhuThuModel(entity));
            }
            model.addAttribute("phuThuList", phuThuModels); // Đảm bảo truyền phuThuList

            SuatChieuModel suatChieuModel = new SuatChieuModel(suatChieu);
            model.addAttribute("suatChieuModel", suatChieuModel);
            model.addAttribute("isEdit", true);

            // ... (giữ nguyên phần phân trang và các map khác) ...

            return "admin/showtime_manager";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi lấy thông tin suất chiếu: " + e.getMessage());
            return "redirect:/admin/showtimes?view=" + view;
        } finally {
            dbSession.close();
        }
    }

    // Thêm suất chiếu mới
    @Transactional
    @RequestMapping(value = "/showtimes/add", method = RequestMethod.POST)
    public String processAddShowtime(
            @RequestParam("maPhim") String maPhim,
            @RequestParam("maRap") String maRap,
            @RequestParam("maPhongChieu") String maPhongChieu,
            @RequestParam("loaiManChieu") String loaiManChieu,
            @RequestParam Map<String, String> allParams,
            @RequestParam(value = "view", defaultValue = "calendar") String view,
            Model model) {
        try {
            Session dbSession = sessionFactory.getCurrentSession();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            
            // Lấy thông tin phim để tính thời lượng
            PhimEntity phim = (PhimEntity) dbSession.get(PhimEntity.class, maPhim);
            if (phim == null) {
                model.addAttribute("error", "Không tìm thấy phim với mã " + maPhim);
                return "admin/showtime_manager";
            }
            int thoiLuong = phim.getThoiLuong();

            // Lặp qua các suất chiếu từ form
            for (int i = 0; ; i++) {
                String ngayGioChieuKey = "showtimes[" + i + "].ngayGioChieu";
                if (!allParams.containsKey(ngayGioChieuKey)) {
                    break; // Không còn suất chiếu nào nữa
                }

                String ngayGioChieuStr = allParams.get(ngayGioChieuKey);
                Timestamp ngayGioChieu = new Timestamp(dateFormat.parse(ngayGioChieuStr).getTime());
                Timestamp ngayGioKetThuc = new Timestamp(ngayGioChieu.getTime() + thoiLuong * 60 * 1000);

                // Tạo mã suất chiếu mới
                Query latestQuery = dbSession.createQuery("FROM SuatChieuEntity ORDER BY maSuatChieu DESC");
                latestQuery.setMaxResults(1);
                SuatChieuEntity latestSuat = (SuatChieuEntity) latestQuery.uniqueResult();
                String maSuatChieu = latestSuat == null ? "SC001" : String.format("SC%03d",
                        Integer.parseInt(latestSuat.getMaSuatChieu().substring(2)) + 1);

                SuatChieuEntity suatChieu = new SuatChieuEntity();
                suatChieu.setMaSuatChieu(maSuatChieu);
                suatChieu.setMaPhim(maPhim);
                suatChieu.setMaPhongChieu(maPhongChieu);
                suatChieu.setNgayGioChieu(ngayGioChieu);
                suatChieu.setNgayGioKetThuc(ngayGioKetThuc);
                suatChieu.setLoaiManChieu(loaiManChieu);

                // Thêm các phụ thu
                for (int j = 0; ; j++) {
                    String maPhuThuKey = "showtimes[" + i + "].maPhuThus[" + j + "]";
                    if (!allParams.containsKey(maPhuThuKey)) {
                        break; // Không còn phụ thu nào nữa
                    }
                    String maPhuThu = allParams.get(maPhuThuKey);
                    if (maPhuThu != null && !maPhuThu.isEmpty()) {
                        PhuThuEntity phuThu = (PhuThuEntity) dbSession.get(PhuThuEntity.class, maPhuThu);
                        if (phuThu != null) {
                            suatChieu.getPhuThus().add(phuThu);
                        }
                    }
                }

                dbSession.save(suatChieu);
            }

            return "redirect:/admin/showtimes?view=" + view;
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi thêm suất chiếu: " + e.getMessage());
            return "admin/showtime_manager";
        }
    }

    // Cập nhật suất chiếu
    @Transactional
    @RequestMapping(value = "/showtimes/update", method = RequestMethod.POST)
    public String processUpdateShowtime(
            @RequestParam("maSuatChieu") String maSuatChieu,
            @RequestParam("maPhim") String maPhim,
            @RequestParam("maPhongChieu") String maPhongChieu,
            @RequestParam("ngayGioChieu") String ngayGioChieuStr,
            @RequestParam("loaiManChieu") String loaiManChieu,
            @RequestParam(value = "maPhuThus", required = false) List<String> maPhuThus,
            @RequestParam(value = "view", defaultValue = "calendar") String view,
            Model model) {
        try {
            Session dbSession = sessionFactory.getCurrentSession();

            SuatChieuEntity suatChieu = (SuatChieuEntity) dbSession.get(SuatChieuEntity.class, maSuatChieu);
            if (suatChieu == null) {
                model.addAttribute("error", "Không tìm thấy suất chiếu với mã " + maSuatChieu);
                return "redirect:/admin/showtimes?view=" + view;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            Timestamp ngayGioChieu = new Timestamp(dateFormat.parse(ngayGioChieuStr).getTime());

            // Lấy thông tin phim để tính thời lượng
            PhimEntity phim = (PhimEntity) dbSession.get(PhimEntity.class, maPhim);
            if (phim == null) {
                model.addAttribute("error", "Không tìm thấy phim với mã " + maPhim);
                return "admin/showtime_manager";
            }
            int thoiLuong = phim.getThoiLuong();
            Timestamp ngayGioKetThuc = new Timestamp(ngayGioChieu.getTime() + thoiLuong * 60 * 1000);

            suatChieu.setMaPhim(maPhim);
            suatChieu.setMaPhongChieu(maPhongChieu);
            suatChieu.setNgayGioChieu(ngayGioChieu);
            suatChieu.setNgayGioKetThuc(ngayGioKetThuc);
            suatChieu.setLoaiManChieu(loaiManChieu);

            // Cập nhật danh sách phụ thu
            suatChieu.getPhuThus().clear();
            if (maPhuThus != null && !maPhuThus.isEmpty()) {
                for (String maPhuThu : maPhuThus) {
                    PhuThuEntity phuThu = (PhuThuEntity) dbSession.get(PhuThuEntity.class, maPhuThu);
                    if (phuThu != null) {
                        suatChieu.getPhuThus().add(phuThu);
                    }
                }
            }

            dbSession.update(suatChieu);
            return "redirect:/admin/showtimes?view=" + view;
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi cập nhật suất chiếu: " + e.getMessage());
            return "admin/showtime_manager";
        }
    }

    // Xóa suất chiếu
    @Transactional
    @RequestMapping(value = "/showtimes/delete/{maSuatChieu}", method = RequestMethod.GET)
    public String deleteShowtime(
            @PathVariable("maSuatChieu") String maSuatChieu,
            @RequestParam(value = "view", defaultValue = "calendar") String view,
            Model model) {
        try {
            Session dbSession = sessionFactory.getCurrentSession();
            SuatChieuEntity suatChieu = (SuatChieuEntity) dbSession.get(SuatChieuEntity.class, maSuatChieu);

            if (suatChieu != null) {
                dbSession.delete(suatChieu);
            } else {
                model.addAttribute("error", "Không tìm thấy suất chiếu với mã " + maSuatChieu);
            }
            return "redirect:/admin/showtimes?view=" + view;
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi xóa suất chiếu: " + e.getMessage());
            return "redirect:/admin/showtimes?view=" + view;
        }
    }
}
