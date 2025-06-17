package movie.controller;

import movie.entity.PhimEntity;
import movie.entity.PhongChieuEntity;
import movie.entity.RapChieuEntity;
import movie.entity.SuatChieuEntity;
import movie.entity.VeEntity;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminShowtimeController {

    @Autowired
    private SessionFactory sessionFactory;

    @RequestMapping(value = "/showtimes", method = RequestMethod.GET)
    public String showShowtimeManager(
            HttpSession session,
            Model model,
            @RequestParam(value = "view", defaultValue = "calendar") String view) {
        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/auth/login";
        }

        Session dbSession = sessionFactory.openSession();
        try {
            // Lấy tất cả suất chiếu (không phân trang)
            Query suatChieuQuery = dbSession.createQuery("FROM SuatChieuEntity ORDER BY ngayGioChieu DESC");
            List<SuatChieuEntity> suatChieuEntities = suatChieuQuery.list();
            List<SuatChieuModel> suatChieuModels = new ArrayList<>();
            for (SuatChieuEntity entity : suatChieuEntities) {
                suatChieuModels.add(new SuatChieuModel(entity));
            }
            System.out.println("Number of showtimes: " + suatChieuModels.size());
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

            // Tạo Map phimMap, phongMap, rapMap
            Map<String, PhimModel> phimMap = new HashMap<>();
            for (PhimModel phim : phimModels) {
                phimMap.put(phim.getMaPhim(), phim);
            }
            model.addAttribute("phimMap", phimMap);

            Map<String, PhongChieuModel> phongMap = new HashMap<>();
            for (PhongChieuModel phong : phongModels) {
                phongMap.put(phong.getMaPhongChieu(), phong);
            }
            model.addAttribute("phongMap", phongMap);

            Map<String, RapChieuModel> rapMap = new HashMap<>();
            for (RapChieuModel rap : rapModels) {
                rapMap.put(rap.getMaRapChieu(), rap);
            }
            model.addAttribute("rapMap", rapMap);

            // Thêm thời gian hiện tại
            model.addAttribute("now", new Date());
            model.addAttribute("viewMode", view.equals("table") ? "table" : "calendar");
            System.out.println("viewMode=" + model.asMap().get("viewMode"));

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

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi lấy danh sách suất chiếu: " + e.getMessage());
        } finally {
            dbSession.close();
        }
        return "admin/showtime_manager";
    }

    @Transactional
    @RequestMapping(value = "/showtimes/add", method = RequestMethod.POST)
    public String processAddShowtime(
            @RequestParam("maPhim") String maPhim,
            @RequestParam("maRap") String maRap,
            @RequestParam("maPhongChieu") String maPhongChieu,
            @RequestParam("loaiManChieu") String loaiManChieu,
            @RequestParam(value = "view", defaultValue = "calendar") String view,
            @RequestParam Map<String, String> allParams,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/auth/login";
        }

        Session dbSession = sessionFactory.getCurrentSession();
        List<String> errors = new ArrayList<>();

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            dateFormat.setLenient(false);
            dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

            PhimEntity phim = (PhimEntity) dbSession.get(PhimEntity.class, maPhim);
            if (phim == null) {
                errors.add("Không tìm thấy phim với mã " + maPhim);
            } else {
                int thoiLuong = phim.getThoiLuong();

                Calendar todayCal = Calendar.getInstance();
                todayCal.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
                todayCal.set(Calendar.HOUR_OF_DAY, 0);
                todayCal.set(Calendar.MINUTE, 0);
                todayCal.set(Calendar.SECOND, 0);
                todayCal.set(Calendar.MILLISECOND, 0);
                Date today = todayCal.getTime();

                for (int i = 0; ; i++) {
                    String ngayGioChieuKey = "showtimes[" + i + "].ngayGioChieu";
                    if (!allParams.containsKey(ngayGioChieuKey)) {
                        break;
                    }

                    String ngayGioChieuStr = allParams.get(ngayGioChieuKey);
                    Timestamp ngayGioChieu;
                    try {
                        ngayGioChieu = new Timestamp(dateFormat.parse(ngayGioChieuStr).getTime());
                    } catch (Exception e) {
                        errors.add("Ngày giờ chiếu không đúng định dạng (yyyy-MM-dd'T'HH:mm): " + ngayGioChieuStr);
                        break;
                    }

                    if (ngayGioChieu.before(today)) {
                        errors.add("Ngày giờ chiếu không được là quá khứ: " + ngayGioChieuStr);
                        break;
                    }

                    Timestamp ngayGioKetThuc = new Timestamp(ngayGioChieu.getTime() + thoiLuong * 60 * 1000);
                    if (ngayGioKetThuc.before(ngayGioChieu) || ngayGioKetThuc.getTime() == ngayGioChieu.getTime()) {
                        errors.add("Ngày giờ kết thúc phải sau ngày giờ chiếu!");
                        break;
                    }

                    // Check for overlapping showtimes in the same maPhongChieu
                    Query overlapQuery = dbSession.createQuery(
                        "FROM SuatChieuEntity WHERE maPhongChieu = :maPhongChieu " +
                        "AND ngayGioChieu < :ngayGioKetThuc AND ngayGioKetThuc > :ngayGioChieu"
                    );
                    overlapQuery.setParameter("maPhongChieu", maPhongChieu);
                    overlapQuery.setParameter("ngayGioKetThuc", ngayGioKetThuc);
                    overlapQuery.setParameter("ngayGioChieu", ngayGioChieu);
                    List<SuatChieuEntity> overlappingShowtimes = overlapQuery.list();
                    if (!overlappingShowtimes.isEmpty()) {
                        errors.add("Suất chiếu từ " + ngayGioChieuStr + " bị trùng với suất chiếu khác trong cùng phòng");
                        break;
                    }

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

                    for (int j = 0; ; j++) {
                        String maPhuThuKey = "showtimes[" + i + "].maPhuThus[" + j + "]";
                        if (!allParams.containsKey(maPhuThuKey)) {
                            break;
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
            }

            if (!errors.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", String.join(" ", errors));
                return "redirect:/admin/showtimes?view=" + view;
            }

            redirectAttributes.addFlashAttribute("success", "Thêm suất chiếu thành công!");
            return "redirect:/admin/showtimes?view=" + view;
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm suất chiếu: " + e.getMessage());
            return "redirect:/admin/showtimes?view=" + view;
        }
    }

    @Transactional
    @RequestMapping(value = "/showtimes/update/{maSuatChieu}", method = RequestMethod.POST)
    public String processUpdateShowtime(
            @PathVariable("maSuatChieu") String maSuatChieu,
            @RequestParam("maPhim") String maPhim,
            @RequestParam("maPhongChieu") String maPhongChieu,
            @RequestParam("ngayGioChieu") String ngayGioChieuStr,
            @RequestParam("loaiManChieu") String loaiManChieu,
            @RequestParam(value = "view", defaultValue = "calendar") String view,
            @RequestParam(value = "maPhuThus", required = false) List<String> maPhuThus,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (session.getAttribute("loggedInAdmin") == null) {
            System.err.println("No logged-in admin for update maSuatChieu: " + maSuatChieu);
            return "redirect:/auth/login";
        }

        System.err.println("Updating showtime: maSuatChieu=" + maSuatChieu + ", maPhim=" + maPhim + 
                           ", maPhongChieu=" + maPhongChieu + ", ngayGioChieu=" + ngayGioChieuStr + 
                           ", loaiManChieu=" + loaiManChieu + ", view=" + view + ", maPhuThus=" + maPhuThus);

        Session dbSession = sessionFactory.getCurrentSession();
        List<String> errors = new ArrayList<>();

        try {
            // Check if showtime exists
            SuatChieuEntity suatChieu = (SuatChieuEntity) dbSession.get(SuatChieuEntity.class, maSuatChieu);
            if (suatChieu == null) {
                errors.add("Không tìm thấy suất chiếu với mã " + maSuatChieu);
                redirectAttributes.addFlashAttribute("error", String.join(" ", errors));
                System.err.println("Showtime not found: " + maSuatChieu);
                return "redirect:/admin/showtimes?view=" + view;
            }

            // Check for associated tickets
            Query ticketQuery = dbSession.createQuery("FROM VeEntity WHERE maSuatChieu = :maSuatChieu");
            ticketQuery.setParameter("maSuatChieu", maSuatChieu);
            List<VeEntity> tickets = ticketQuery.list();
            if (!tickets.isEmpty()) {
                errors.add("Không thể sửa suất chiếu vì đã có vé được đặt.");
                redirectAttributes.addFlashAttribute("error", String.join(" ", errors));
                System.err.println("Tickets found for maSuatChieu: " + maSuatChieu);
                return "redirect:/admin/showtimes?view=" + view;
            }

            // Validate movie
            PhimEntity phim = (PhimEntity) dbSession.get(PhimEntity.class, maPhim);
            if (phim == null) {
                errors.add("Không tìm thấy phim với mã " + maPhim);
                redirectAttributes.addFlashAttribute("error", String.join(" ", errors));
                System.err.println("Movie not found: " + maPhim);
                return "redirect:/admin/showtimes?view=" + view;
            }

            // Parse and validate date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            dateFormat.setLenient(false);
            dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
            Timestamp ngayGioChieu;
            try {
                ngayGioChieu = new Timestamp(dateFormat.parse(ngayGioChieuStr).getTime());
            } catch (Exception e) {
                errors.add("Ngày giờ chiếu không đúng định dạng (yyyy-MM-dd'T'HH:mm): " + ngayGioChieuStr);
                redirectAttributes.addFlashAttribute("error", String.join(" ", errors));
                System.err.println("Invalid date format: " + ngayGioChieuStr);
                return "redirect:/admin/showtimes?view=" + view;
            }

            // Check past date
            Calendar todayCal = Calendar.getInstance();
            todayCal.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
            todayCal.set(Calendar.HOUR_OF_DAY, 0);
            todayCal.set(Calendar.MINUTE, 0);
            todayCal.set(Calendar.SECOND, 0);
            todayCal.set(Calendar.MILLISECOND, 0);
            Date today = todayCal.getTime();
            if (ngayGioChieu.before(today)) {
                errors.add("Ngày giờ chiếu không được là quá khứ: " + ngayGioChieuStr);
                redirectAttributes.addFlashAttribute("error", String.join(" ", errors));
                System.err.println("Past showtime: " + ngayGioChieuStr);
                return "redirect:/admin/showtimes?view=" + view;
            }

            // Calculate end time
            int thoiLuong = phim.getThoiLuong();
            Timestamp ngayGioKetThuc = new Timestamp(ngayGioChieu.getTime() + thoiLuong * 60 * 1000);
            if (ngayGioKetThuc.before(ngayGioChieu) || ngayGioKetThuc.getTime() == ngayGioChieu.getTime()) {
                errors.add("Ngày giờ kết thúc phải sau ngày giờ chiếu!");
                redirectAttributes.addFlashAttribute("error", String.join(" ", errors));
                System.err.println("Invalid end time for: " + ngayGioChieuStr);
                return "redirect:/admin/showtimes?view=" + view;
            }

            // Check for overlapping showtimes
            Query overlapQuery = dbSession.createQuery(
                "FROM SuatChieuEntity WHERE maPhongChieu = :maPhongChieu " +
                "AND maSuatChieu != :maSuatChieu " +
                "AND ngayGioChieu < :ngayGioKetThuc AND ngayGioKetThuc > :ngayGioChieu"
            );
            overlapQuery.setParameter("maPhongChieu", maPhongChieu);
            overlapQuery.setParameter("maSuatChieu", maSuatChieu);
            overlapQuery.setParameter("ngayGioKetThuc", ngayGioKetThuc);
            overlapQuery.setParameter("ngayGioChieu", ngayGioChieu);
            List<SuatChieuEntity> overlappingShowtimes = overlapQuery.list();
            if (!overlappingShowtimes.isEmpty()) {
                errors.add("Suất chiếu từ " + ngayGioChieuStr + " bị trùng với suất chiếu khác trong phòng ");
                redirectAttributes.addFlashAttribute("error", String.join(" ", errors));
                System.err.println("Overlap detected for: " + ngayGioChieuStr + " in room " + maPhongChieu);
                return "redirect:/admin/showtimes?view=" + view;
            }

            // Update showtime
            suatChieu.setMaPhim(maPhim);
            suatChieu.setMaPhongChieu(maPhongChieu);
            suatChieu.setNgayGioChieu(ngayGioChieu);
            suatChieu.setNgayGioKetThuc(ngayGioKetThuc);
            suatChieu.setLoaiManChieu(loaiManChieu);

            // Update phuThu
            suatChieu.getPhuThus().clear();
            if (maPhuThus != null && !maPhuThus.isEmpty()) {
                for (String maPhuThu : maPhuThus) {
                    if (maPhuThu != null && !maPhuThu.isEmpty()) {
                        PhuThuEntity phuThu = (PhuThuEntity) dbSession.get(PhuThuEntity.class, maPhuThu);
                        if (phuThu != null) {
                            suatChieu.getPhuThus().add(phuThu);
                            System.err.println("Added PhuThu: " + maPhuThu);
                        }
                    }
                }
            }

            dbSession.update(suatChieu);
            redirectAttributes.addFlashAttribute("success", "Cập nhật suất chiếu thành công!");
            System.err.println("Showtime updated: " + maSuatChieu);
            return "redirect:/admin/showtimes?view=" + view;

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật suất chiếu: " + e.getMessage());
            System.err.println("Error updating showtime " + maSuatChieu + ": " + e.getMessage());
            return "redirect:/admin/showtimes?view=" + view;
        }
    }

    @Transactional
    @RequestMapping(value = "/showtimes/check-edit/{maSuatChieu}", method = RequestMethod.GET)
    public String checkEditShowtime(
            @PathVariable("maSuatChieu") String maSuatChieu,
            @RequestParam(value = "view", defaultValue = "calendar") String view,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/auth/login";
        }

        Session dbSession = sessionFactory.getCurrentSession();
        try {
            SuatChieuEntity suatChieu = (SuatChieuEntity) dbSession.get(SuatChieuEntity.class, maSuatChieu);
            if (suatChieu == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy suất chiếu với mã " + maSuatChieu);
                return "redirect:/admin/showtimes?view=" + view;
            }

            Query ticketQuery = dbSession.createQuery("FROM VeEntity WHERE maSuatChieu = :maSuatChieu");
            ticketQuery.setParameter("maSuatChieu", maSuatChieu);
            List<VeEntity> tickets = ticketQuery.list();
            if (!tickets.isEmpty()) {
                redirectAttributes.addFlashAttribute("warning", "Không thể sửa suất chiếu vì đã có vé được đặt.");
                return "redirect:/admin/showtimes?view=" + view;
            }

            // Nếu không có vé, trả về view với thông tin để mở modal
            redirectAttributes.addFlashAttribute("editableShowtime", maSuatChieu);
            return "redirect:/admin/showtimes?view=" + view;
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Lỗi khi kiểm tra suất chiếu: " + e.getMessage());
            return "redirect:/admin/showtimes?view=" + view;
        }
    }

    @Transactional
    @RequestMapping(value = "/showtimes/delete", method = RequestMethod.POST)
    public String deleteShowtime(
            @RequestParam("maSuatChieu") String maSuatChieu,
            @RequestParam(value = "view", defaultValue = "calendar") String view,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/auth/login";
        }

        Session dbSession = sessionFactory.getCurrentSession();
        try {
            SuatChieuEntity suatChieu = (SuatChieuEntity) dbSession.get(SuatChieuEntity.class, maSuatChieu);
            if (suatChieu == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy suất chiếu với mã " + maSuatChieu);
                return "redirect:/admin/showtimes?view=" + view;
            }

            // Check for associated tickets
            Query ticketQuery = dbSession.createQuery("FROM VeEntity WHERE maSuatChieu = :maSuatChieu");
            ticketQuery.setParameter("maSuatChieu", maSuatChieu);
            List<VeEntity> tickets = ticketQuery.list();
            if (!tickets.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Không thể xóa suất chiếu vì đã có vé được đặt.");
                return "redirect:/admin/showtimes?view=" + view;
            }

            dbSession.delete(suatChieu);
            redirectAttributes.addFlashAttribute("success", "Xóa suất chiếu thành công!");
            return "redirect:/admin/showtimes?view=" + view;
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa suất chiếu: " + e.getMessage());
            return "redirect:/admin/showtimes?view=" + view;
        }
    }
}