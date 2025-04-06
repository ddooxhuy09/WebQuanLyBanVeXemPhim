package movie.controller;

import movie.entity.*;
import movie.model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    @RequestMapping(value = "/select-seats", method = {RequestMethod.GET, RequestMethod.POST})
    public String showSeatSelection(@RequestParam("maPhim") String maPhim,
                                    @RequestParam("maSuatChieu") String maSuatChieu,
                                    HttpSession session,
                                    Model model) {
        KhachHangModel loggedInUser = (KhachHangModel) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            session.setAttribute("redirectMaPhim", maPhim);
            session.setAttribute("redirectMaSuatChieu", maSuatChieu);
            model.addAttribute("error", "Vui lòng đăng nhập để đặt vé");
            return "redirect:/user/auth/login";
        }

        try {
            Session dbSession = sessionFactory.getCurrentSession();

            Query phimQuery = dbSession.createQuery(
                "FROM PhimEntity p LEFT JOIN FETCH p.theLoais LEFT JOIN FETCH p.dienViens WHERE p.maPhim = :maPhim"
            );
            phimQuery.setParameter("maPhim", maPhim);
            PhimEntity phimEntity = (PhimEntity) phimQuery.uniqueResult();
            if (phimEntity == null) {
                model.addAttribute("error", "Phim không tồn tại");
                return "user/book-ticket";
            }

            Query suatChieuQuery = dbSession.createQuery(
                "SELECT sc, sc.phongChieu, sc.phongChieu.rapChieu " +
                "FROM SuatChieuEntity sc " +
                "WHERE sc.maSuatChieu = :maSuatChieu"
            );
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
                "FROM GheEntity g WHERE g.phongChieu.maPhongChieu = :maPhongChieu ORDER BY g.tenHang, g.soGhe"
            );
            gheQuery.setParameter("maPhongChieu", phongChieu.getMaPhongChieu());
            List<GheEntity> gheList = gheQuery.list();

            Query veQuery = dbSession.createQuery(
                "FROM VeEntity v WHERE v.maSuatChieu = :maSuatChieu"
            );
            veQuery.setParameter("maSuatChieu", maSuatChieu);
            List<VeEntity> veList = veQuery.list();

            Set<String> occupiedSeats = new HashSet<>();
            for (VeEntity ve : veList) {
                for (GheEntity ghe : gheList) {
                    if (ghe.getMaGhe().equals(ve.getMaGhe())) {
                        occupiedSeats.add(ghe.getTenHang() + ghe.getSoGhe());
                        break;
                    }
                }
            }

            Set<String> uniqueRows = new TreeSet<>();
            int maxCot = 0;
            for (GheEntity ghe : gheList) {
                uniqueRows.add(ghe.getTenHang());
                // Chuyển soGhe từ String sang int để tính maxCot
                try {
                    int soGheAsInt = Integer.parseInt(ghe.getSoGhe());
                    maxCot = Math.max(maxCot, soGheAsInt);
                } catch (NumberFormatException e) {
                    // Xử lý khi soGhe không phải là số hợp lệ
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
            model.addAttribute("occupiedSeats", occupiedSeats);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi tải trang chọn ghế: " + e.getMessage());
            return "user/book-ticket";
        }
        return "user/book-ticket";
    }

    @Transactional
    @RequestMapping(value = "/confirm-booking", method = RequestMethod.POST)
    public String confirmBooking(@RequestParam("maPhim") String maPhim,
                                 @RequestParam("maSuatChieu") String maSuatChieu,
                                 @RequestParam("selectedSeats") List<String> selectedSeats,
                                 HttpSession session,
                                 Model model) {
        KhachHangModel loggedInUser = (KhachHangModel) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            model.addAttribute("error", "Vui lòng đăng nhập để đặt vé");
            return "redirect:/user/auth/login";
        }

        if (selectedSeats == null || selectedSeats.isEmpty()) {
            model.addAttribute("error", "Vui lòng chọn ít nhất một ghế");
            return "redirect:/booking/select-seats?maPhim=" + maPhim + "&maSuatChieu=" + maSuatChieu;
        }

        try {
            Session dbSession = sessionFactory.getCurrentSession();

            Query phimQuery = dbSession.createQuery(
                "FROM PhimEntity p LEFT JOIN FETCH p.theLoais LEFT JOIN FETCH p.dienViens WHERE p.maPhim = :maPhim"
            );
            phimQuery.setParameter("maPhim", maPhim);
            PhimEntity phimEntity = (PhimEntity) phimQuery.uniqueResult();
            if (phimEntity == null) {
                model.addAttribute("error", "Phim không tồn tại");
                return "user/book-ticket";
            }

            Query suatChieuQuery = dbSession.createQuery(
                "FROM SuatChieuEntity sc WHERE sc.maSuatChieu = :maSuatChieu"
            );
            suatChieuQuery.setParameter("maSuatChieu", maSuatChieu);
            SuatChieuEntity suatChieuEntity = (SuatChieuEntity) suatChieuQuery.uniqueResult();
            if (suatChieuEntity == null) {
                model.addAttribute("error", "Suất chiếu không tồn tại");
                return "user/book-ticket";
            }

            Query gheQuery = dbSession.createQuery(
                "FROM GheEntity g WHERE g.phongChieu.maPhongChieu = :maPhongChieu"
            );
            gheQuery.setParameter("maPhongChieu", suatChieuEntity.getMaPhongChieu());
            List<GheEntity> gheList = gheQuery.list();

            Query checkVeQuery = dbSession.createQuery(
                "FROM VeEntity v WHERE v.maSuatChieu = :maSuatChieu AND v.maGhe IN (:maGheList)"
            );
            List<String> maGheList = new ArrayList<>();
            for (String seatId : selectedSeats) {
                for (GheEntity ghe : gheList) {
                    String fullSeatId = ghe.getTenHang() + ghe.getSoGhe();
                    if (fullSeatId.equals(seatId)) {
                        maGheList.add(ghe.getMaGhe());
                        break;
                    }
                }
            }
            checkVeQuery.setParameter("maSuatChieu", maSuatChieu);
            checkVeQuery.setParameterList("maGheList", maGheList);
            List<VeEntity> existingVeList = checkVeQuery.list();

            if (!existingVeList.isEmpty()) {
                StringBuilder occupiedSeatIds = new StringBuilder();
                for (VeEntity ve : existingVeList) {
                    for (GheEntity ghe : gheList) {
                        if (ghe.getMaGhe().equals(ve.getMaGhe())) {
                            if (occupiedSeatIds.length() > 0) occupiedSeatIds.append(", ");
                            occupiedSeatIds.append(ghe.getTenHang() + ghe.getSoGhe());
                            break;
                        }
                    }
                }
                model.addAttribute("error", "Ghế " + occupiedSeatIds + " đã được đặt bởi người khác");
                return "redirect:/booking/select-seats?maPhim=" + maPhim + "&maSuatChieu=" + maSuatChieu;
            }

            for (String seatId : selectedSeats) {
                GheEntity selectedGhe = null;
                for (GheEntity ghe : gheList) {
                    String fullSeatId = ghe.getTenHang() + ghe.getSoGhe();
                    if (fullSeatId.equals(seatId)) {
                        selectedGhe = ghe;
                        break;
                    }
                }
                if (selectedGhe == null) {
                    model.addAttribute("error", "Ghế " + seatId + " không hợp lệ");
                    return "user/book-ticket";
                }

                VeEntity ve = new VeEntity();
                String maVe = "VE" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
                ve.setMaVe(maVe);
                ve.setMaKhachHang(loggedInUser.getMaKhachHang());
                ve.setMaSuatChieu(maSuatChieu);
                ve.setMaGhe(selectedGhe.getMaGhe());
                double heSoGia = selectedGhe.getLoaiGhe().getHeSoGia();
                ve.setGiaVe(phimEntity.getGiaVe().multiply(new java.math.BigDecimal(heSoGia)));
                ve.setNgayMua(new Date());
                ve.setTrangThai("Đã thanh toán");
                dbSession.save(ve);
            }

            model.addAttribute("success", "Đặt vé thành công cho các ghế: " + String.join(", ", selectedSeats));
            return "redirect:/home/";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi xác nhận đặt vé: " + e.getMessage());
            return "user/book-ticket";
        }
    }
}
