package movie.controller;

import movie.entity.*;
import movie.model.*;
import movie.config.VNPayConfig;
import movie.service.SeatReservationService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private SeatReservationService seatReservationService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Transactional
    @RequestMapping(value = "/select-seats", method = { RequestMethod.GET, RequestMethod.POST })
    public String showSeatSelection(@RequestParam(value = "maPhim", required = false) String maPhim,
            @RequestParam(value = "maSuatChieu", required = false) String maSuatChieu,
            HttpSession session,
            Model model) {
        // Ưu tiên lấy từ session
        if (maPhim == null) {
            maPhim = (String) session.getAttribute("maPhim");
        }
        if (maSuatChieu == null) {
            maSuatChieu = (String) session.getAttribute("maSuatChieu");
        }

        // Nếu vẫn không có, lấy từ redirect attributes
        if (maPhim == null || maSuatChieu == null) {
            maPhim = (String) session.getAttribute("redirectMaPhim");
            maSuatChieu = (String) session.getAttribute("redirectMaSuatChieu");
        }

        if (maPhim == null || maSuatChieu == null) {
            model.addAttribute("error", "Thông tin phim hoặc suất chiếu không được cung cấp. Vui lòng chọn lại.");
            return "redirect:/home/";
        }

        try {
            KhachHangModel loggedInUser = (KhachHangModel) session.getAttribute("loggedInUser");
            if (loggedInUser == null) {
                session.setAttribute("redirectMaPhim", maPhim);
                session.setAttribute("redirectMaSuatChieu", maSuatChieu);
                model.addAttribute("error", "Vui lòng đăng nhập để đặt vé");
                return "redirect:/auth/login";
            }

            Session dbSession = sessionFactory.getCurrentSession();

            Query phimQuery = dbSession.createQuery(
                    "FROM PhimEntity p LEFT JOIN FETCH p.theLoais LEFT JOIN FETCH p.dienViens WHERE p.maPhim = :maPhim");
            phimQuery.setParameter("maPhim", maPhim);
            PhimEntity phimEntity = (PhimEntity) phimQuery.uniqueResult();
            if (phimEntity == null) {
                model.addAttribute("error", "Phim không tồn tại");
                return "user/book-ticket";
            }

            Query suatChieuQuery = dbSession.createQuery(
                    "SELECT sc, sc.phongChieu, sc.phongChieu.rapChieu FROM SuatChieuEntity sc WHERE sc.maSuatChieu = :maSuatChieu");
            suatChieuQuery.setParameter("maSuatChieu", maSuatChieu);
            Object[] suatChieuResult = (Object[]) suatChieuQuery.uniqueResult();
            if (suatChieuResult == null) {
                model.addAttribute("error", "Suất chiếu không tồn tại");
                return "user/book-ticket";
            }

            SuatChieuEntity suatChieuEntity = (SuatChieuEntity) suatChieuResult[0];
            PhongChieuEntity phongChieu = (PhongChieuEntity) suatChieuResult[1];
            RapChieuEntity rapChieu = (RapChieuEntity) suatChieuResult[2];

            Query gheQuery = dbSession.createQuery(
                    "FROM GheEntity g WHERE g.phongChieu.maPhongChieu = :maPhongChieu ORDER BY g.tenHang, g.soGhe");
            gheQuery.setParameter("maPhongChieu", phongChieu.getMaPhongChieu());
            List<GheEntity> gheList = gheQuery.list();

            // Lấy danh sách loại ghế có sẵn trong phòng chiếu
            Query loaiGheQuery = dbSession.createQuery(
                    "SELECT DISTINCT g.loaiGhe FROM GheEntity g WHERE g.phongChieu.maPhongChieu = :maPhongChieu");
            loaiGheQuery.setParameter("maPhongChieu", phongChieu.getMaPhongChieu());
            List<LoaiGheEntity> loaiGheList = loaiGheQuery.list();
            List<LoaiGheModel> loaiGheModels = new ArrayList<>();
            for (LoaiGheEntity entity : loaiGheList) {
                loaiGheModels.add(new LoaiGheModel(entity));
            }

            // Lấy tất cả vé cho suất chiếu
            Query veQuery = dbSession.createQuery(
                    "FROM VeEntity v WHERE v.maSuatChieu = :maSuatChieu");
            veQuery.setParameter("maSuatChieu", maSuatChieu);
            List<VeEntity> veList = veQuery.list();

            Date fiveMinutesAgo = new Date(System.currentTimeMillis() - 5 * 60 * 1000);
            Set<String> reservedSeats = new HashSet<>();
            Set<String> paidSeats = new HashSet<>();
            Map<String, Long> seatReservationTimes = new HashMap<>();
            for (VeEntity ve : veList) {
                for (GheEntity ghe : gheList) {
                    if (ghe.getMaGhe().equals(ve.getMaGhe())) {
                        String seatId = ghe.getTenHang() + ghe.getSoGhe();
                        if (ve.getDonHang() == null && ve.getNgayMua().after(fiveMinutesAgo)) {
                            reservedSeats.add(seatId);
                            seatReservationTimes.put(seatId, ve.getNgayMua().getTime());
                        } else if (ve.getDonHang() != null) {
                            paidSeats.add(seatId);
                        }
                        break;
                    }
                }
            }

            // Lấy danh sách ghế đã chọn từ session
            String selectedSeats = (String) session.getAttribute("selectedSeats");
            if (selectedSeats != null && !selectedSeats.isEmpty()) {
                model.addAttribute("selectedSeats", Arrays.asList(selectedSeats.split(",")));
            } else {
                model.addAttribute("selectedSeats", new ArrayList<String>());
            }

            // Lấy singleSeats và doubleSeats từ session nếu có
            Integer singleSeats = (Integer) session.getAttribute("singleSeats");
            Integer doubleSeats = (Integer) session.getAttribute("doubleSeats");
            model.addAttribute("singleSeats", singleSeats != null ? singleSeats : 0);
            model.addAttribute("doubleSeats", doubleSeats != null ? doubleSeats : 0);

            Set<String> uniqueRows = new TreeSet<>();
            int maxCot = 0;
            for (GheEntity ghe : gheList) {
                uniqueRows.add(ghe.getTenHang());
                try {
                    int soGheAsInt = Integer.parseInt(ghe.getSoGhe());
                    maxCot = Math.max(maxCot, soGheAsInt);
                } catch (NumberFormatException e) {
                    System.out.println("Không thể chuyển soGhe thành số: " + ghe.getSoGhe());
                }
            }
            List<String> rowLabels = new ArrayList<>(uniqueRows);

            model.addAttribute("phim", new PhimModel(phimEntity));
            model.addAttribute("suatChieu", new SuatChieuModel(suatChieuEntity));
            model.addAttribute("rapChieu", new RapChieuModel(rapChieu));
            model.addAttribute("gheList", gheList);
            model.addAttribute("rowLabels", rowLabels);
            model.addAttribute("soCot", maxCot);
            model.addAttribute("reservedSeats", reservedSeats);
            model.addAttribute("paidSeats", paidSeats);
            model.addAttribute("seatReservationTimes", seatReservationTimes);
            model.addAttribute("loaiGheList", loaiGheModels);

            session.setAttribute("maPhim", maPhim);
            session.setAttribute("maSuatChieu", maSuatChieu);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi tải trang chọn ghế: " + e.getMessage());
            return "user/book-ticket";
        }
        return "user/book-ticket";
    }

    @Transactional
    @RequestMapping(value = "/reserve-seats", method = RequestMethod.POST)
    public String reserveSeats(@RequestParam("maPhim") String maPhim,
            @RequestParam("maSuatChieu") String maSuatChieu,
            @RequestParam("selectedSeats") String selectedSeats,
            HttpSession session,
            Model model) {
        try {
            KhachHangModel loggedInUser = (KhachHangModel) session.getAttribute("loggedInUser");
            if (loggedInUser == null) {
                session.setAttribute("redirectMaPhim", maPhim);
                session.setAttribute("redirectMaSuatChieu", maSuatChieu);
                model.addAttribute("error", "Vui lòng đăng nhập để đặt vé");
                return "redirect:/auth/login";
            }

            if (selectedSeats == null || selectedSeats.isEmpty()) {
                model.addAttribute("error", "Vui lòng chọn ít nhất một ghế");
                return showSeatSelection(maPhim, maSuatChieu, session, model);
            }

            Session dbSession = sessionFactory.getCurrentSession();
            Query phimQuery = dbSession.createQuery("FROM PhimEntity p WHERE p.maPhim = :maPhim");
            phimQuery.setParameter("maPhim", maPhim);
            PhimEntity phimEntity = (PhimEntity) phimQuery.uniqueResult();
            if (phimEntity == null) {
                model.addAttribute("error", "Phim không tồn tại");
                return "user/book-ticket";
            }

            Query suatChieuQuery = dbSession.createQuery("FROM SuatChieuEntity sc WHERE sc.maSuatChieu = :maSuatChieu");
            suatChieuQuery.setParameter("maSuatChieu", maSuatChieu);
            SuatChieuEntity suatChieuEntity = (SuatChieuEntity) suatChieuQuery.uniqueResult();
            if (suatChieuEntity == null) {
                model.addAttribute("error", "Suất chiếu không tồn tại");
                return "user/book-ticket";
            }

            List<String> selectedSeatList = Arrays.asList(selectedSeats.split(","));
            Query gheQuery = dbSession.createQuery(
                    "FROM GheEntity g WHERE g.phongChieu.maPhongChieu = :maPhongChieu");
            gheQuery.setParameter("maPhongChieu", suatChieuEntity.getPhongChieu().getMaPhongChieu());
            List<GheEntity> gheList = gheQuery.list();

            // Đếm số lượng ghế theo loại ghế
            Map<String, Integer> seatCountByType = new HashMap<>();
            for (String seatId : selectedSeatList) {
                for (GheEntity ghe : gheList) {
                    if ((ghe.getTenHang() + ghe.getSoGhe()).equals(seatId.trim())) {
                        String tenLoaiGhe = ghe.getLoaiGhe().getTenLoaiGhe();
                        seatCountByType.put(tenLoaiGhe, seatCountByType.getOrDefault(tenLoaiGhe, 0) + 1);
                        break;
                    }
                }
            }

            // Xóa các vé cũ của người dùng cho suất chiếu này (MaDonHang = null)
            Query deleteOldVeQuery = dbSession.createQuery(
                    "DELETE FROM VeEntity v WHERE v.maSuatChieu = :maSuatChieu AND v.maKhachHang = :maKhachHang AND v.donHang IS NULL");
            deleteOldVeQuery.setParameter("maSuatChieu", maSuatChieu);
            deleteOldVeQuery.setParameter("maKhachHang", loggedInUser.getMaKhachHang());
            deleteOldVeQuery.executeUpdate();

            Date fiveMinutesAgo = new Date(System.currentTimeMillis() - 5 * 60 * 1000);
            for (String seatId : selectedSeatList) {
                GheEntity selectedGhe = null;
                for (GheEntity ghe : gheList) {
                    if ((ghe.getTenHang() + ghe.getSoGhe()).equals(seatId.trim())) {
                        selectedGhe = ghe;
                        break;
                    }
                }
                if (selectedGhe != null) {
                    Query veQuery = dbSession.createQuery(
                            "FROM VeEntity v WHERE v.maSuatChieu = :maSuatChieu AND v.maGhe = :maGhe");
                    veQuery.setParameter("maSuatChieu", maSuatChieu);
                    veQuery.setParameter("maGhe", selectedGhe.getMaGhe());
                    VeEntity existingVe = (VeEntity) veQuery.uniqueResult();
                    if (existingVe != null) {
                        if (existingVe.getDonHang() != null) {
                            model.addAttribute("error", "Ghế " + seatId + " đã được thanh toán");
                            return showSeatSelection(maPhim, maSuatChieu, session, model);
                        } else if (!existingVe.getMaKhachHang().equals(loggedInUser.getMaKhachHang()) &&
                                existingVe.getNgayMua().after(fiveMinutesAgo)) {
                            model.addAttribute("error", "Ghế " + seatId + " đang được giữ bởi người khác");
                            return showSeatSelection(maPhim, maSuatChieu, session, model);
                        }
                    }
                    // Tạo vé mới
                    VeEntity ve = new VeEntity();
                    ve.setMaVe("VE" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8));
                    ve.setMaKhachHang(loggedInUser.getMaKhachHang());
                    ve.setMaSuatChieu(maSuatChieu);
                    ve.setMaGhe(selectedGhe.getMaGhe());
                    ve.setGiaVe(
                            phimEntity.getGiaVe().multiply(BigDecimal.valueOf(selectedGhe.getLoaiGhe().getHeSoGia())));
                    ve.setNgayMua(new Date());
                    ve.setDonHang(null);
                    dbSession.save(ve);
                }
            }

            session.setAttribute("selectedSeats", selectedSeats);
            session.setAttribute("maPhim", maPhim);
            session.setAttribute("maSuatChieu", maSuatChieu);
            // Lưu số lượng ghế theo loại vào session nếu cần
            session.setAttribute("seatCountByType", seatCountByType);
            session.setAttribute("reservationStartTime", System.currentTimeMillis());
            session.setMaxInactiveInterval(5 * 60);

            messagingTemplate.convertAndSend("/topic/seats/" + maSuatChieu, selectedSeatList);
            return "redirect:/booking/select-food";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi giữ ghế: " + e.getMessage());
            return showSeatSelection(maPhim, maSuatChieu, session, model);
        }
    }

    @Transactional
    @RequestMapping(value = "/update-seats", method = RequestMethod.POST)
    public String updateSeats(@RequestParam("maPhim") String maPhim,
            @RequestParam("maSuatChieu") String maSuatChieu,
            @RequestParam(value = "selectedSeats", required = false) String selectedSeats,
            HttpSession session,
            Model model,
            @RequestParam(value = "fromSelectFood", required = false) String fromSelectFood) {
        try {
            KhachHangModel loggedInUser = (KhachHangModel) session.getAttribute("loggedInUser");
            if (loggedInUser == null) {
                session.setAttribute("redirectMaPhim", maPhim);
                session.setAttribute("redirectMaSuatChieu", maSuatChieu);
                model.addAttribute("error", "Vui lòng đăng nhập để đặt vé");
                return "redirect:/auth/login";
            }

            // Nếu yêu cầu từ select-food.jsp, trả về trang chọn ghế mà không cập nhật
            if ("true".equals(fromSelectFood)) {
                // Đảm bảo selectedSeats từ session được truyền vào model
                String sessionSeats = (String) session.getAttribute("selectedSeats");
                if (sessionSeats != null && !sessionSeats.isEmpty()) {
                    model.addAttribute("selectedSeats", Arrays.asList(sessionSeats.split(",")));
                }
                // Bỏ singleSeats và doubleSeats, thay bằng seatCountByType nếu cần
                Map<String, Integer> seatCountByType = (Map<String, Integer>) session.getAttribute("seatCountByType");
                model.addAttribute("seatCountByType", seatCountByType != null ? seatCountByType : new HashMap<String, Integer>());
                return showSeatSelection(maPhim, maSuatChieu, session, model);
            }

            if (selectedSeats == null || selectedSeats.isEmpty()) {
                model.addAttribute("error", "Vui lòng chọn ít nhất một ghế");
                return showSeatSelection(maPhim, maSuatChieu, session, model);
            }

            // Gọi lại reserveSeats với đúng tham số
            return reserveSeats(maPhim, maSuatChieu, selectedSeats, session, model);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi cập nhật ghế: " + e.getMessage());
            return showSeatSelection(maPhim, maSuatChieu, session, model);
        }
    }

    @Transactional
    @RequestMapping(value = "/select-food", method = { RequestMethod.GET, RequestMethod.POST })
    public String showFoodSelection(@RequestParam(value = "maPhim", required = false) String maPhim,
            @RequestParam(value = "maSuatChieu", required = false) String maSuatChieu,
            @RequestParam(value = "selectedSeats", required = false) String selectedSeats,
            HttpSession session,
            Model model) {
        if (maPhim == null || maSuatChieu == null || selectedSeats == null) {
            maPhim = (String) session.getAttribute("maPhim");
            maSuatChieu = (String) session.getAttribute("maSuatChieu");
            selectedSeats = (String) session.getAttribute("selectedSeats");
        }

        if (maPhim == null || maSuatChieu == null || selectedSeats == null || selectedSeats.isEmpty()) {
            model.addAttribute("error", "Thông tin đặt vé không đầy đủ. Vui lòng chọn lại ghế.");
            return "redirect:/home/";
        }

        try {
            KhachHangModel loggedInUser = (KhachHangModel) session.getAttribute("loggedInUser");
            if (loggedInUser == null) {
                session.setAttribute("redirectMaPhim", maPhim);
                session.setAttribute("redirectMaSuatChieu", maSuatChieu);
                model.addAttribute("error", "Vui lòng đăng nhập để đặt vé");
                return "redirect:/auth/login";
            }

            Session dbSession = sessionFactory.getCurrentSession();

            // Lấy thông tin phim
            Query phimQuery = dbSession.createQuery(
                    "FROM PhimEntity p LEFT JOIN FETCH p.theLoais LEFT JOIN FETCH p.dienViens WHERE p.maPhim = :maPhim");
            phimQuery.setParameter("maPhim", maPhim);
            PhimEntity phimEntity = (PhimEntity) phimQuery.uniqueResult();
            if (phimEntity == null) {
                model.addAttribute("error", "Phim không tồn tại");
                return "user/book-ticket";
            }

            // Lấy thông tin suất chiếu và rạp
            Query suatChieuQuery = dbSession.createQuery(
                    "SELECT sc, sc.phongChieu, sc.phongChieu.rapChieu FROM SuatChieuEntity sc WHERE sc.maSuatChieu = :maSuatChieu");
            suatChieuQuery.setParameter("maSuatChieu", maSuatChieu);
            Object[] suatChieuResult = (Object[]) suatChieuQuery.uniqueResult();
            if (suatChieuResult == null) {
                model.addAttribute("error", "Suất chiếu không tồn tại");
                return "user/book-ticket";
            }

            SuatChieuEntity suatChieuEntity = (SuatChieuEntity) suatChieuResult[0];
            RapChieuEntity rapChieu = (RapChieuEntity) suatChieuResult[2];

            // Lấy danh sách ghế và tính giá vé
            List<String> selectedSeatList = Arrays.asList(selectedSeats.split(","));
            Query gheQuery = dbSession.createQuery(
                    "FROM GheEntity g WHERE g.phongChieu.maPhongChieu = :maPhongChieu");
            gheQuery.setParameter("maPhongChieu", suatChieuEntity.getPhongChieu().getMaPhongChieu());
            List<GheEntity> gheList = gheQuery.list();

            Map<String, BigDecimal> vePrices = new HashMap<>();
            BigDecimal totalVe = BigDecimal.ZERO;
            for (String seatId : selectedSeatList) {
                for (GheEntity ghe : gheList) {
                    String fullSeatId = ghe.getTenHang() + ghe.getSoGhe();
                    if (fullSeatId.equals(seatId.trim())) {
                        BigDecimal giaVe = phimEntity.getGiaVe().multiply(BigDecimal.valueOf(ghe.getLoaiGhe().getHeSoGia()));
                        vePrices.put(seatId, giaVe);
                        totalVe = totalVe.add(giaVe);
                        break;
                    }
                }
            }

            // Tính phụ thu suất chiếu
            Set<PhuThuEntity> phuThus = suatChieuEntity.getPhuThus();
            BigDecimal tongPhuThu = BigDecimal.ZERO;
            for (PhuThuEntity phuThu : phuThus) {
                tongPhuThu = tongPhuThu.add(phuThu.getGiaPhuThu());
            }
            BigDecimal totalPhuThu = tongPhuThu.multiply(new BigDecimal(selectedSeatList.size()));
            BigDecimal tongTien = totalVe.add(totalPhuThu);

            // Lấy danh sách combo
            Query comboQuery = dbSession.createQuery("FROM ComboEntity c");
            List<ComboEntity> comboEntities = comboQuery.list();
            List<ComboModel> combos = new ArrayList<>();
            for (ComboEntity entity : comboEntities) {
                combos.add(new ComboModel(entity));
            }

            // Lấy danh sách bắp nước
            Query bapNuocQuery = dbSession.createQuery("FROM BapNuocEntity b");
            List<BapNuocEntity> bapNuocEntities = bapNuocQuery.list();
            List<BapNuocModel> bapNuocs = new ArrayList<>();
            for (BapNuocEntity entity : bapNuocEntities) {
                bapNuocs.add(new BapNuocModel(entity));
            }

            // Truyền dữ liệu vào model
            model.addAttribute("phim", new PhimModel(phimEntity));
            model.addAttribute("rapChieu", new RapChieuModel(rapChieu));
            model.addAttribute("suatChieu", new SuatChieuModel(suatChieuEntity));
            model.addAttribute("vePrices", vePrices);
            model.addAttribute("tongTien", tongTien);
            model.addAttribute("selectedSeats", selectedSeats);
            model.addAttribute("combos", combos);
            model.addAttribute("bapNuocs", bapNuocs);
            model.addAttribute("maPhim", maPhim);
            model.addAttribute("maSuatChieu", maSuatChieu);

            session.setAttribute("selectedSeats", selectedSeats);
            session.setAttribute("maPhim", maPhim);
            session.setAttribute("maSuatChieu", maSuatChieu);
            session.setAttribute("vePrices", vePrices);
            session.setAttribute("tongTien", tongTien);

            return "user/select-food";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi tải trang chọn combo/bắp nước: " + e.getMessage());
            return "user/book-ticket";
        }
    }

    @Transactional
    @RequestMapping(value = "/select-payment", method = RequestMethod.POST)
    public String showPayment(@RequestParam("maPhim") String maPhim,
            @RequestParam("maSuatChieu") String maSuatChieu,
            @RequestParam("selectedSeats") String selectedSeats,
            @RequestParam Map<String, String> allParams,
            HttpSession session,
            Model model) {
        try {
            KhachHangModel loggedInUser = (KhachHangModel) session.getAttribute("loggedInUser");
            if (loggedInUser == null) {
                session.setAttribute("redirectMaPhim", maPhim);
                session.setAttribute("redirectMaSuatChieu", maSuatChieu);
                model.addAttribute("error", "Vui lòng đăng nhập để đặt vé");
                return "redirect:/auth/login";
            }

            Session dbSession = sessionFactory.getCurrentSession();

            // Lấy thông tin phim
            Query phimQuery = dbSession.createQuery(
                    "FROM PhimEntity p LEFT JOIN FETCH p.theLoais LEFT JOIN FETCH p.dienViens WHERE p.maPhim = :maPhim");
            phimQuery.setParameter("maPhim", maPhim);
            PhimEntity phimEntity = (PhimEntity) phimQuery.uniqueResult();
            if (phimEntity == null) {
                model.addAttribute("error", "Phim không tồn tại");
                return "user/select-food";
            }

            // Lấy thông tin suất chiếu và rạp
            Query suatChieuQuery = dbSession.createQuery(
                    "SELECT sc, sc.phongChieu, sc.phongChieu.rapChieu FROM SuatChieuEntity sc WHERE sc.maSuatChieu = :maSuatChieu");
            suatChieuQuery.setParameter("maSuatChieu", maSuatChieu);
            Object[] suatChieuResult = (Object[]) suatChieuQuery.uniqueResult();
            if (suatChieuResult == null) {
                model.addAttribute("error", "Suất chiếu không tồn tại");
                return "user/select-food";
            }

            SuatChieuEntity suatChieuEntity = (SuatChieuEntity) suatChieuResult[0];
            RapChieuEntity rapChieu = (RapChieuEntity) suatChieuResult[2];

            // Lấy danh sách ghế và tính giá vé
            List<String> selectedSeatList = Arrays.asList(selectedSeats.split(","));
            Query gheQuery = dbSession.createQuery(
                    "FROM GheEntity g WHERE g.phongChieu.maPhongChieu = :maPhongChieu");
            gheQuery.setParameter("maPhongChieu", suatChieuEntity.getPhongChieu().getMaPhongChieu());
            List<GheEntity> gheList = gheQuery.list();

            Map<String, BigDecimal> vePrices = new HashMap<>();
            BigDecimal totalVe = BigDecimal.ZERO;
            for (String seatId : selectedSeatList) {
                for (GheEntity ghe : gheList) {
                    String fullSeatId = ghe.getTenHang() + ghe.getSoGhe();
                    if (fullSeatId.equals(seatId.trim())) {
                        double heSoGia = ghe.getLoaiGhe().getHeSoGia();
                        BigDecimal giaVe = phimEntity.getGiaVe().multiply(BigDecimal.valueOf(heSoGia));
                        vePrices.put(seatId.trim(), giaVe);
                        totalVe = totalVe.add(giaVe);
                        break;
                    }
                }
            }

            // Tính phụ thu suất chiếu
            Set<PhuThuEntity> phuThus = suatChieuEntity.getPhuThus();
            BigDecimal tongPhuThu = BigDecimal.ZERO;
            List<PhuThuModel> phuThuList = new ArrayList<>();
            for (PhuThuEntity phuThu : phuThus) {
                tongPhuThu = tongPhuThu.add(phuThu.getGiaPhuThu());
                phuThuList.add(new PhuThuModel(phuThu));
            }

            // Tính giá combo
            Map<String, Integer> selectedCombos = new HashMap<>();
            Map<String, BigDecimal> comboPrices = new HashMap<>();
            BigDecimal totalCombo = BigDecimal.ZERO;
            for (Map.Entry<String, String> entry : allParams.entrySet()) {
                if (entry.getKey().startsWith("combo_")) {
                    String maCombo = entry.getKey().substring(6);
                    int quantity;
                    try {
                        quantity = Integer.parseInt(entry.getValue());
                    } catch (NumberFormatException e) {
                        quantity = 0;
                    }
                    if (quantity > 0) {
                        selectedCombos.put(maCombo, quantity);
                        Query comboQuery = dbSession.createQuery("FROM ComboEntity c WHERE c.maCombo = :maCombo");
                        comboQuery.setParameter("maCombo", maCombo);
                        ComboEntity combo = (ComboEntity) comboQuery.uniqueResult();
                        if (combo != null) {
                            BigDecimal giaCombo = combo.getGiaCombo().multiply(BigDecimal.valueOf(quantity));
                            comboPrices.put(maCombo, giaCombo);
                            totalCombo = totalCombo.add(giaCombo);
                        }
                    }
                }
            }

            // Tính giá bắp nước
            Map<String, Integer> selectedBapNuocs = new HashMap<>();
            Map<String, BigDecimal> bapNuocPrices = new HashMap<>();
            BigDecimal totalBapNuoc = BigDecimal.ZERO;
            for (Map.Entry<String, String> entry : allParams.entrySet()) {
                if (entry.getKey().startsWith("bapNuoc_")) {
                    String maBapNuoc = entry.getKey().substring(8);
                    int quantity;
                    try {
                        quantity = Integer.parseInt(entry.getValue());
                    } catch (NumberFormatException e) {
                        quantity = 0;
                    }
                    if (quantity > 0) {
                        selectedBapNuocs.put(maBapNuoc, quantity);
                        Query bapNuocQuery = dbSession.createQuery("FROM BapNuocEntity b WHERE b.maBapNuoc = :maBapNuoc");
                        bapNuocQuery.setParameter("maBapNuoc", maBapNuoc);
                        BapNuocEntity bapNuoc = (BapNuocEntity) bapNuocQuery.uniqueResult();
                        if (bapNuoc != null) {
                            BigDecimal giaBapNuoc = bapNuoc.getGiaBapNuoc().multiply(BigDecimal.valueOf(quantity));
                            bapNuocPrices.put(maBapNuoc, giaBapNuoc);
                            totalBapNuoc = totalBapNuoc.add(giaBapNuoc);
                        }
                    }
                }
            }

            // Tính tổng tiền bao gồm phụ thu
            BigDecimal tongTien = totalVe.add(totalCombo).add(totalBapNuoc).add(tongPhuThu.multiply(new BigDecimal(selectedSeatList.size())));

            // Lưu vào session
            session.setAttribute("selectedSeats", selectedSeats);
            session.setAttribute("maPhim", maPhim);
            session.setAttribute("maSuatChieu", maSuatChieu);
            session.setAttribute("selectedCombos", selectedCombos);
            session.setAttribute("selectedBapNuocs", selectedBapNuocs);
            session.setAttribute("vePrices", vePrices);
            session.setAttribute("comboPrices", comboPrices);
            session.setAttribute("bapNuocPrices", bapNuocPrices);
            session.setAttribute("tongTien", tongTien);
            session.setAttribute("originTongTien", tongTien);
            session.setAttribute("phuThuList", phuThuList);
            session.setAttribute("tongPhuThu", tongPhuThu);

            // Truyền dữ liệu vào model
            model.addAttribute("phim", new PhimModel(phimEntity));
            model.addAttribute("rapChieu", new RapChieuModel(rapChieu));
            model.addAttribute("suatChieu", new SuatChieuModel(suatChieuEntity));
            model.addAttribute("tongTien", tongTien);
            model.addAttribute("selectedSeats", selectedSeats);
            model.addAttribute("selectedCombos", selectedCombos);
            model.addAttribute("selectedBapNuocs", selectedBapNuocs);
            model.addAttribute("vePrices", vePrices);
            model.addAttribute("comboPrices", comboPrices);
            model.addAttribute("bapNuocPrices", bapNuocPrices);
            model.addAttribute("phuThuList", phuThuList);
            model.addAttribute("tongPhuThu", tongPhuThu);

            return "user/payment";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi tải trang thanh toán: " + e.getMessage());
            return "user/select-food";
        }
    }

    @Transactional
    @RequestMapping(value = "/apply-promo-code", method = RequestMethod.POST)
    public String applyPromoCode(@RequestParam("promoCode") String promoCode,
            HttpSession session,
            Model model) {
        try {
            if (promoCode == null || promoCode.isEmpty()) {
                model.addAttribute("error", "Vui lòng nhập mã khuyến mãi!");
                return "user/payment";
            }

            Session dbSession = sessionFactory.getCurrentSession();

            Query khuyenMaiQuery = dbSession.createQuery(
                    "FROM KhuyenMaiEntity k WHERE k.maCode = :maCode AND :currentDate BETWEEN k.ngayBatDau AND k.ngayKetThuc");
            khuyenMaiQuery.setParameter("maCode", promoCode);
            khuyenMaiQuery.setParameter("currentDate", new Date());
            KhuyenMaiEntity khuyenMai = (KhuyenMaiEntity) khuyenMaiQuery.uniqueResult();

            if (khuyenMai == null) {
                model.addAttribute("error", "Mã khuyến mãi không hợp lệ hoặc đã hết hạn!");
                return "user/payment";
            }

            BigDecimal tongTien = (BigDecimal) session.getAttribute("originTongTien");
            if (tongTien == null) {
                tongTien = (BigDecimal) session.getAttribute("tongTien");
                if (tongTien == null) {
                    model.addAttribute("error", "Không tìm thấy thông tin đơn hàng!");
                    return "user/payment";
                }
                session.setAttribute("originTongTien", tongTien);
            }

            BigDecimal discountAmount;
            BigDecimal newTotal;
            if ("Phần trăm".equals(khuyenMai.getLoaiGiamGia())) {
                BigDecimal discountPercentage = khuyenMai.getGiaTriGiam().divide(new BigDecimal("100"));
                discountAmount = tongTien.multiply(discountPercentage);
                newTotal = tongTien.subtract(discountAmount);
            } else {
                discountAmount = khuyenMai.getGiaTriGiam();
                newTotal = tongTien.subtract(discountAmount);
                if (newTotal.compareTo(BigDecimal.ZERO) < 0) {
                    newTotal = BigDecimal.ZERO;
                }
            }

            session.setAttribute("appliedPromoCode", promoCode);
            session.setAttribute("discountAmount", discountAmount);
            session.setAttribute("maKhuyenMai", khuyenMai.getMaKhuyenMai());
            session.setAttribute("tongTien", newTotal);

            model.addAttribute("promoCode", promoCode);
            model.addAttribute("khuyenMai", khuyenMai);
            model.addAttribute("discountAmount", discountAmount);
            model.addAttribute("tongTien", newTotal);
            model.addAttribute("success", "Mã khuyến mãi đã được áp dụng thành công!");

            String selectedSeats = (String) session.getAttribute("selectedSeats");
            Map<String, Integer> selectedCombos = (Map<String, Integer>) session.getAttribute("selectedCombos");
            Map<String, Integer> selectedBapNuocs = (Map<String, Integer>) session.getAttribute("selectedBapNuocs");
            Map<String, BigDecimal> vePrices = (Map<String, BigDecimal>) session.getAttribute("vePrices");
            Map<String, BigDecimal> comboPrices = (Map<String, BigDecimal>) session.getAttribute("comboPrices");
            Map<String, BigDecimal> bapNuocPrices = (Map<String, BigDecimal>) session.getAttribute("bapNuocPrices");
            List<PhuThuModel> phuThuList = (List<PhuThuModel>) session.getAttribute("phuThuList");
            BigDecimal tongPhuThu = (BigDecimal) session.getAttribute("tongPhuThu");

            if (selectedSeats != null) {
                model.addAttribute("selectedSeats", Arrays.asList(selectedSeats.split(",")));
            }
            model.addAttribute("selectedCombos", selectedCombos);
            model.addAttribute("selectedBapNuocs", selectedBapNuocs);
            model.addAttribute("vePrices", vePrices);
            model.addAttribute("comboPrices", comboPrices);
            model.addAttribute("bapNuocPrices", bapNuocPrices);
            model.addAttribute("phuThuList", phuThuList);
            model.addAttribute("tongPhuThu", tongPhuThu);

            return "user/payment";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi áp dụng mã giảm giá: " + e.getMessage());
            return "user/payment";
        }
    }

    @Transactional
    @RequestMapping(value = "/confirm-payment", method = RequestMethod.POST)
    public String confirmPayment(@RequestParam("paymentMethod") String paymentMethod,
            @RequestParam(value = "promoCode", required = false) String promoCode,
            HttpSession session,
            HttpServletRequest request,
            Model model) throws java.io.UnsupportedEncodingException {
        // Khai báo biến
        KhachHangModel loggedInUser = (KhachHangModel) session.getAttribute("loggedInUser");
        String selectedSeats = (String) session.getAttribute("selectedSeats");
        String maPhim = (String) session.getAttribute("maPhim");
        String maSuatChieu = (String) session.getAttribute("maSuatChieu");
        Map<String, Integer> selectedCombos = (Map<String, Integer>) session.getAttribute("selectedCombos");
        Map<String, Integer> selectedBapNuocs = (Map<String, Integer>) session.getAttribute("selectedBapNuocs");
        Map<String, BigDecimal> vePrices = (Map<String, BigDecimal>) session.getAttribute("vePrices");
        Map<String, BigDecimal> comboPrices = (Map<String, BigDecimal>) session.getAttribute("comboPrices");
        Map<String, BigDecimal> bapNuocPrices = (Map<String, BigDecimal>) session.getAttribute("bapNuocPrices");
        BigDecimal tongTien = (BigDecimal) session.getAttribute("tongTien");
        String maKhuyenMai = (String) session.getAttribute("maKhuyenMai");
        BigDecimal discountAmount = (BigDecimal) session.getAttribute("discountAmount");
        List<PhuThuModel> phuThuList = (List<PhuThuModel>) session.getAttribute("phuThuList");
        BigDecimal tongPhuThu = (BigDecimal) session.getAttribute("tongPhuThu");

        // Kiểm tra đăng nhập
        if (loggedInUser == null) {
            model.addAttribute("error", "Vui lòng đăng nhập để thanh toán");
            return "user/payment";
        }

        // Kiểm tra tổng tiền
        if (tongTien == null || tongTien.compareTo(BigDecimal.ZERO) <= 0) {
            model.addAttribute("error", "Tổng tiền không hợp lệ");
            return "user/payment";
        }

        // Khối try-catch chính
        try {
            Session dbSession = sessionFactory.getCurrentSession();

            // Tạo đơn hàng
            DonHangEntity donHang = new DonHangEntity();
            String maDonHang = "DH" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
            donHang.setMaDonHang(maDonHang);
            donHang.setMaKhachHang(loggedInUser.getMaKhachHang());
            if (maKhuyenMai != null && !maKhuyenMai.isEmpty()) {
                donHang.setMaKhuyenMai(maKhuyenMai);
            }
            donHang.setTongTien(tongTien);
            donHang.setDatHang(true);
            donHang.setNgayDat(new Date());
            donHang.setDiemSuDung(0);

            int diemDonHang = donHang.tinhDiem();

            // Xử lý thanh toán VNPAY
            if ("vnpay".equals(paymentMethod)) {
                String vnp_TxnRef = maDonHang;
                String vnp_IpAddr = request.getRemoteAddr();
                String vnp_CreateDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                String vnp_OrderInfo = "Thanh toan don hang " + maDonHang;

                Map<String, String> vnp_Params = new TreeMap<>();
                vnp_Params.put("vnp_Version", VNPayConfig.vnp_Version);
                vnp_Params.put("vnp_Command", VNPayConfig.vnp_Command);
                vnp_Params.put("vnp_TmnCode", VNPayConfig.vnp_TmnCode);
                long amount = tongTien.multiply(new BigDecimal("100")).longValue();
                vnp_Params.put("vnp_Amount", String.valueOf(amount));
                vnp_Params.put("vnp_CurrCode", VNPayConfig.vnp_CurrCode);
                vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
                vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
                vnp_Params.put("vnp_OrderType", "250000");
                vnp_Params.put("vnp_Locale", VNPayConfig.vnp_Locale);
                String returnUrl = VNPayConfig.getReturnUrl(request);
                vnp_Params.put("vnp_ReturnUrl", returnUrl);
                vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
                vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

                StringBuilder hashData = new StringBuilder();
                Iterator<Map.Entry<String, String>> iterator = vnp_Params.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, String> entry = iterator.next();
                    hashData.append(URLEncoder.encode(entry.getKey(), StandardCharsets.US_ASCII.toString()));
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII.toString()));
                    if (iterator.hasNext()) {
                        hashData.append('&');
                    }
                }

                String vnp_SecureHash = hmacSHA512(VNPayConfig.vnp_HashSecret, hashData.toString());
                vnp_Params.put("vnp_SecureHash", vnp_SecureHash);

                StringBuilder paymentUrl = new StringBuilder(VNPayConfig.vnp_Url);
                paymentUrl.append('?');
                Iterator<Map.Entry<String, String>> iteratorUrl = vnp_Params.entrySet().iterator();
                while (iteratorUrl.hasNext()) {
                    Map.Entry<String, String> entry = iteratorUrl.next();
                    paymentUrl.append(URLEncoder.encode(entry.getKey(), StandardCharsets.US_ASCII.toString()));
                    paymentUrl.append('=');
                    paymentUrl.append(URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII.toString()));
                    if (iteratorUrl.hasNext()) {
                        paymentUrl.append('&');
                    }
                }

                session.setAttribute("pendingOrder", donHang);
                return "redirect:" + paymentUrl.toString();
            } else {
                // Xử lý thanh toán ZaloPay
                dbSession.save(donHang);

                List<String> selectedSeatList = Arrays.asList(selectedSeats.split(","));
                Query gheQuery = dbSession.createQuery("FROM GheEntity g WHERE g.phongChieu.maPhongChieu = :maPhongChieu");
                Query suatChieuQuery = dbSession.createQuery("FROM SuatChieuEntity sc WHERE sc.maSuatChieu = :maSuatChieu");
                suatChieuQuery.setParameter("maSuatChieu", maSuatChieu);
                SuatChieuEntity suatChieuEntity = (SuatChieuEntity) suatChieuQuery.uniqueResult();
                gheQuery.setParameter("maPhongChieu", suatChieuEntity.getPhongChieu().getMaPhongChieu());
                List<GheEntity> gheList = gheQuery.list();

                for (String seatId : selectedSeatList) {
                    GheEntity selectedGhe = null;
                    for (GheEntity ghe : gheList) {
                        String fullSeatId = ghe.getTenHang() + ghe.getSoGhe();
                        if (fullSeatId.equals(seatId.trim())) {
                            selectedGhe = ghe;
                            break;
                        }
                    }
                    if (selectedGhe != null) {
                        Query veQuery = dbSession.createQuery(
                                "FROM VeEntity v WHERE v.maSuatChieu = :maSuatChieu AND v.maGhe = :maGhe AND v.donHang IS NULL");
                        veQuery.setParameter("maSuatChieu", maSuatChieu);
                        veQuery.setParameter("maGhe", selectedGhe.getMaGhe());
                        VeEntity ve = (VeEntity) veQuery.uniqueResult();
                        if (ve != null) {
                            ve.setDonHang(donHang);
                            dbSession.update(ve);
                        }
                    }
                }

                if (selectedCombos != null && !selectedCombos.isEmpty()) {
                    for (Map.Entry<String, Integer> combo : selectedCombos.entrySet()) {
                        String maCombo = combo.getKey();
                        int quantity = combo.getValue();
                        if (quantity > 0) {
                            ChiTietDonHangComboEntity chiTietCombo = new ChiTietDonHangComboEntity();
                            chiTietCombo.setDonHang(donHang);
                            chiTietCombo.setMaCombo(maCombo);
                            chiTietCombo.setSoLuong(quantity);
                            dbSession.save(chiTietCombo);
                        }
                    }
                }

                if (selectedBapNuocs != null && !selectedBapNuocs.isEmpty()) {
                    for (Map.Entry<String, Integer> bapNuoc : selectedBapNuocs.entrySet()) {
                        String maBapNuoc = bapNuoc.getKey();
                        int quantity = bapNuoc.getValue();
                        if (quantity > 0) {
                            ChiTietDonHangBapNuocEntity chiTietBapNuoc = new ChiTietDonHangBapNuocEntity();
                            chiTietBapNuoc.setDonHang(donHang);
                            chiTietBapNuoc.setMaBapNuoc(maBapNuoc);
                            chiTietBapNuoc.setSoLuong(quantity);
                            dbSession.save(chiTietBapNuoc);
                        }
                    }
                }

                ThanhToanEntity thanhToan = new ThanhToanEntity();
                String maThanhToan = "TT" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
                thanhToan.setMaThanhToan(maThanhToan);
                thanhToan.setDonHang(donHang);
                thanhToan.setPhuongThuc(paymentMethod);
                thanhToan.setSoTien(tongTien);
                thanhToan.setNgayThanhToan(new Date());
                thanhToan.setTrangThai("Thành công");
                dbSession.save(thanhToan);

                Query khachHangQuery = dbSession.createQuery("FROM KhachHangEntity kh WHERE kh.maKhachHang = :maKhachHang");
                khachHangQuery.setParameter("maKhachHang", loggedInUser.getMaKhachHang());
                KhachHangEntity khachHang = (KhachHangEntity) khachHangQuery.uniqueResult();
                if (khachHang != null) {
                    khachHang.congDiem(diemDonHang);
                    dbSession.update(khachHang);
                    loggedInUser.setTongDiem(khachHang.getTongDiem());
                    session.setAttribute("loggedInUser", loggedInUser);
                }

                messagingTemplate.convertAndSend("/topic/seats/" + maSuatChieu, selectedSeatList);

                // Thêm dữ liệu cần thiết cho pay-success.jsp
                model.addAttribute("selectedSeats", selectedSeats);
                model.addAttribute("vePrices", vePrices);
                model.addAttribute("selectedCombos", selectedCombos);
                model.addAttribute("comboPrices", comboPrices);
                model.addAttribute("selectedBapNuocs", selectedBapNuocs);
                model.addAttribute("bapNuocPrices", bapNuocPrices);
                model.addAttribute("phuThuList", phuThuList);
                model.addAttribute("tongPhuThu", tongPhuThu);
                model.addAttribute("discountAmount", discountAmount);
                model.addAttribute("paymentMethod", paymentMethod);
                model.addAttribute("tongTien", tongTien);

                clearSession(session);
                return "user/pay-success"; // Chuyển hướng đến pay-success.jsp
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi xác nhận thanh toán: " + e.getMessage());
            return "user/payment";
        }
    }

        @Transactional
        @RequestMapping(value = "/vnpay-payment-return", method = RequestMethod.GET)
        public String vnpayPaymentReturn(HttpServletRequest request, HttpSession session, Model model) {
            try {
                Map<String, String> params = new HashMap<>();
                Map<String, String[]> requestParams = request.getParameterMap();
                for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
                    String paramName = entry.getKey();
                    String[] paramValues = entry.getValue();
                    if (paramValues != null && paramValues.length > 0) {
                        params.put(paramName, paramValues[0]);
                    }
                }

                String vnp_SecureHash = params.get("vnp_SecureHash");
                params.remove("vnp_SecureHash");
                params.remove("vnp_SecureHashType");

                StringBuilder hashData = new StringBuilder();
                TreeMap<String, String> sortedParams = new TreeMap<>(params);
                Iterator<Map.Entry<String, String>> iterator = sortedParams.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, String> entry = iterator.next();
                    hashData.append(URLEncoder.encode(entry.getKey(), StandardCharsets.US_ASCII.toString()));
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII.toString()));
                    if (iterator.hasNext()) {
                        hashData.append('&');
                    }
                }

                String secureHash = hmacSHA512(VNPayConfig.vnp_HashSecret, hashData.toString());
                String vnp_ResponseCode = params.get("vnp_ResponseCode");

                if (secureHash.equals(vnp_SecureHash)) {
                    if ("00".equals(vnp_ResponseCode)) {
                        Session dbSession = sessionFactory.getCurrentSession();
                        DonHangEntity donHang = (DonHangEntity) session.getAttribute("pendingOrder");

                        if (donHang == null) {
                            model.addAttribute("error", "Không tìm thấy đơn hàng trong session");
                            return "user/payment";
                        }

                        int diemDonHang = donHang.tinhDiem();
                        dbSession.save(donHang);

                        String selectedSeats = (String) session.getAttribute("selectedSeats");
                        String maSuatChieu = (String) session.getAttribute("maSuatChieu");
                        List<String> selectedSeatList = Arrays.asList(selectedSeats.split(","));
                        Query gheQuery = dbSession.createQuery("FROM GheEntity g WHERE g.phongChieu.maPhongChieu = :maPhongChieu");
                        Query suatChieuQuery = dbSession.createQuery("FROM SuatChieuEntity sc WHERE sc.maSuatChieu = :maSuatChieu");
                        suatChieuQuery.setParameter("maSuatChieu", maSuatChieu);
                        SuatChieuEntity suatChieuEntity = (SuatChieuEntity) suatChieuQuery.uniqueResult();
                        gheQuery.setParameter("maPhongChieu", suatChieuEntity.getPhongChieu().getMaPhongChieu());
                        List<GheEntity> gheList = gheQuery.list();

                        for (String seatId : selectedSeatList) {
                            GheEntity selectedGhe = null;
                            for (GheEntity ghe : gheList) {
                                String fullSeatId = ghe.getTenHang() + ghe.getSoGhe();
                                if (fullSeatId.equals(seatId.trim())) {
                                    selectedGhe = ghe;
                                    break;
                                }
                            }
                            if (selectedGhe != null) {
                                Query veQuery = dbSession.createQuery(
                                        "FROM VeEntity v WHERE v.maSuatChieu = :maSuatChieu AND v.maGhe = :maGhe AND v.donHang IS NULL");
                                veQuery.setParameter("maSuatChieu", maSuatChieu);
                                veQuery.setParameter("maGhe", selectedGhe.getMaGhe());
                                VeEntity ve = (VeEntity) veQuery.uniqueResult();
                                if (ve != null) {
                                    ve.setDonHang(donHang);
                                    dbSession.update(ve);
                                }
                            }
                        }

                        Map<String, Integer> selectedCombos = (Map<String, Integer>) session.getAttribute("selectedCombos");
                        Map<String, Integer> selectedBapNuocs = (Map<String, Integer>) session.getAttribute("selectedBapNuocs");
                        Map<String, BigDecimal> vePrices = (Map<String, BigDecimal>) session.getAttribute("vePrices");
                        Map<String, BigDecimal> comboPrices = (Map<String, BigDecimal>) session.getAttribute("comboPrices");
                        Map<String, BigDecimal> bapNuocPrices = (Map<String, BigDecimal>) session.getAttribute("bapNuocPrices");
                        BigDecimal tongTien = (BigDecimal) session.getAttribute("tongTien");
                        BigDecimal discountAmount = (BigDecimal) session.getAttribute("discountAmount");
                        List<PhuThuModel> phuThuList = (List<PhuThuModel>) session.getAttribute("phuThuList");
                        BigDecimal tongPhuThu = (BigDecimal) session.getAttribute("tongPhuThu");

                        if (selectedCombos != null && !selectedCombos.isEmpty()) {
                            for (Map.Entry<String, Integer> combo : selectedCombos.entrySet()) {
                                String maCombo = combo.getKey();
                                int quantity = combo.getValue();
                                if (quantity > 0) {
                                    ChiTietDonHangComboEntity chiTietCombo = new ChiTietDonHangComboEntity();
                                    chiTietCombo.setDonHang(donHang);
                                    chiTietCombo.setMaCombo(maCombo);
                                    chiTietCombo.setSoLuong(quantity);
                                    dbSession.save(chiTietCombo);
                                }
                            }
                        }

                        if (selectedBapNuocs != null && !selectedBapNuocs.isEmpty()) {
                            for (Map.Entry<String, Integer> bapNuoc : selectedBapNuocs.entrySet()) {
                                String maBapNuoc = bapNuoc.getKey();
                                int quantity = bapNuoc.getValue();
                                if (quantity > 0) {
                                    ChiTietDonHangBapNuocEntity chiTietBapNuoc = new ChiTietDonHangBapNuocEntity();
                                    chiTietBapNuoc.setDonHang(donHang);
                                    chiTietBapNuoc.setMaBapNuoc(maBapNuoc);
                                    chiTietBapNuoc.setSoLuong(quantity);
                                    dbSession.save(chiTietBapNuoc);
                                }
                            }
                        }

                        ThanhToanEntity thanhToan = new ThanhToanEntity();
                        String maThanhToan = "TT" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
                        thanhToan.setMaThanhToan(maThanhToan);
                        thanhToan.setDonHang(donHang);
                        thanhToan.setPhuongThuc("vnpay");
                        thanhToan.setSoTien(donHang.getTongTien());
                        thanhToan.setNgayThanhToan(new Date());
                        thanhToan.setTrangThai("Thành công");
                        dbSession.save(thanhToan);

                        KhachHangModel loggedInUser = (KhachHangModel) session.getAttribute("loggedInUser");
                        Query khachHangQuery = dbSession.createQuery("FROM KhachHangEntity kh WHERE kh.maKhachHang = :maKhachHang");
                        khachHangQuery.setParameter("maKhachHang", loggedInUser.getMaKhachHang());
                        KhachHangEntity khachHang = (KhachHangEntity) khachHangQuery.uniqueResult();
                        if (khachHang != null) {
                            khachHang.congDiem(diemDonHang);
                            dbSession.update(khachHang);
                            loggedInUser.setTongDiem(khachHang.getTongDiem());
                            session.setAttribute("loggedInUser", loggedInUser);
                        }

                        messagingTemplate.convertAndSend("/topic/seats/" + maSuatChieu, selectedSeatList);

                        // Thêm dữ liệu cần thiết cho pay-success.jsp
                        model.addAttribute("selectedSeats", selectedSeats);
                        model.addAttribute("vePrices", vePrices);
                        model.addAttribute("selectedCombos", selectedCombos);
                        model.addAttribute("comboPrices", comboPrices);
                        model.addAttribute("selectedBapNuocs", selectedBapNuocs);
                        model.addAttribute("bapNuocPrices", bapNuocPrices);
                        model.addAttribute("phuThuList", phuThuList);
                        model.addAttribute("tongPhuThu", tongPhuThu);
                        model.addAttribute("discountAmount", discountAmount);
                        model.addAttribute("paymentMethod", "vnpay");
                        model.addAttribute("tongTien", tongTien);

                        clearSession(session);
                        session.removeAttribute("pendingOrder");

                        return "user/pay-success"; // Chuyển hướng đến pay-success.jsp
                    } else {
                        model.addAttribute("error", "Thanh toán thất bại. Mã lỗi: " + vnp_ResponseCode);
                        return "user/payment";
                    }
                } else {
                    model.addAttribute("error", "Chữ ký không hợp lệ!");
                    return "user/payment";
                }
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("error", "Lỗi xử lý kết quả thanh toán: " + e.getMessage());
                return "user/payment";
            }
        }

    @Transactional
    @RequestMapping(value = "/confirm-booking", method = RequestMethod.POST)
    public String confirmBooking(@RequestParam("maPhim") String maPhim,
            @RequestParam("maSuatChieu") String maSuatChieu,
            @RequestParam(value = "selectedSeats", required = false) String selectedSeats,
            HttpSession session,
            Model model) {
        return "forward:/booking/update-seats";
    }

    @Transactional
    @RequestMapping(value = "/confirm-all", method = RequestMethod.POST)
    public String confirmAll(@RequestParam("maPhim") String maPhim,
            @RequestParam("maSuatChieu") String maSuatChieu,
            @RequestParam("selectedSeats") String selectedSeats,
            @RequestParam Map<String, String> allParams,
            HttpSession session,
            Model model) {
        return "forward:/booking/select-payment";
    }

    private String hmacSHA512(String key, String data) {
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA512");
            javax.crypto.spec.SecretKeySpec secretKey = new javax.crypto.spec.SecretKeySpec(
                    key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            mac.init(secretKey);
            byte[] hmacData = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hmacData) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create HMAC SHA512", e);
        }
    }

    private void clearSession(HttpSession session) {
        session.removeAttribute("selectedSeats");
        session.removeAttribute("maPhim");
        session.removeAttribute("maSuatChieu");
        session.removeAttribute("reservationStartTime");
        session.removeAttribute("selectedCombos");
        session.removeAttribute("selectedBapNuocs");
        session.removeAttribute("vePrices");
        session.removeAttribute("comboPrices");
        session.removeAttribute("bapNuocPrices");
        session.removeAttribute("tongTien");
        session.removeAttribute("originTongTien");
        session.removeAttribute("appliedPromoCode");
        session.removeAttribute("discountAmount");
        session.removeAttribute("maKhuyenMai");
        session.removeAttribute("pendingOrder");
        session.removeAttribute("redirectMaPhim");
        session.removeAttribute("redirectMaSuatChieu");
        session.removeAttribute("singleSeats");
        session.removeAttribute("doubleSeats");
    }
}