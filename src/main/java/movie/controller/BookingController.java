package movie.controller;

import movie.entity.*;
import movie.model.*;
import movie.config.VNPayConfig;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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

    @Transactional
    @RequestMapping(value = "/select-seats", method = RequestMethod.GET)
    public String showSeatSelectionGet(@RequestParam(value = "maPhim", required = false) String maPhim,
                                       @RequestParam(value = "maSuatChieu", required = false) String maSuatChieu,
                                       HttpSession session,
                                       Model model) {
        // Fall back to session attributes if query parameters are missing
        if (maPhim == null || maSuatChieu == null) {
            maPhim = (String) session.getAttribute("redirectMaPhim");
            maSuatChieu = (String) session.getAttribute("redirectMaSuatChieu");
        }
        
        // If still null, return an error or redirect to a safe page
        if (maPhim == null || maSuatChieu == null) {
            model.addAttribute("error", "Thông tin phim hoặc suất chiếu không được cung cấp. Vui lòng chọn lại.");
            return "redirect:/home/"; // Redirect to home or an error page
        }

        return showSeatSelection(maPhim, maSuatChieu, session, model);
    }

    @Transactional
    @RequestMapping(value = "/select-seats", method = RequestMethod.POST)
    public String showSeatSelection(@RequestParam("maPhim") String maPhim,
                                    @RequestParam("maSuatChieu") String maSuatChieu,
                                    HttpSession session,
                                    Model model) {
        try {
            if (maPhim == null || maSuatChieu == null) {
                model.addAttribute("error", "Thông tin phim hoặc suất chiếu không hợp lệ");
                return "user/book-ticket";
            }

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
            model.addAttribute("occupiedSeats", occupiedSeats);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi tải trang chọn ghế: " + e.getMessage());
            return "user/book-ticket";
        }
        return "user/book-ticket";
    }

    @Transactional
    @RequestMapping(value = "/select-food", method = RequestMethod.POST)
    public String showFoodSelection(@RequestParam("maPhim") String maPhim,
                                    @RequestParam("maSuatChieu") String maSuatChieu,
                                    @RequestParam("selectedSeats") String selectedSeats,
                                    HttpSession session,
                                    Model model) {
        try {
            Session dbSession = sessionFactory.getCurrentSession();

            Query comboQuery = dbSession.createQuery("FROM ComboEntity c");
            List<ComboEntity> comboEntities = comboQuery.list();
            List<ComboModel> combos = new ArrayList<>();
            for (ComboEntity entity : comboEntities) {
                combos.add(new ComboModel(entity));
            }

            Query bapNuocQuery = dbSession.createQuery("FROM BapNuocEntity b");
            List<BapNuocEntity> bapNuocEntities = bapNuocQuery.list();
            List<BapNuocModel> bapNuocs = new ArrayList<>();
            for (BapNuocEntity entity : bapNuocEntities) {
                bapNuocs.add(new BapNuocModel(entity));
            }

            session.setAttribute("selectedSeats", selectedSeats);
            session.setAttribute("maPhim", maPhim);
            session.setAttribute("maSuatChieu", maSuatChieu);

            model.addAttribute("combos", combos);
            model.addAttribute("bapNuocs", bapNuocs);
            model.addAttribute("selectedSeats", selectedSeats);
            model.addAttribute("maPhim", maPhim);
            model.addAttribute("maSuatChieu", maSuatChieu);

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
            Session dbSession = sessionFactory.getCurrentSession();

            Query phimQuery = dbSession.createQuery("FROM PhimEntity p WHERE p.maPhim = :maPhim");
            phimQuery.setParameter("maPhim", maPhim);
            PhimEntity phimEntity = (PhimEntity) phimQuery.uniqueResult();

            Query suatChieuQuery = dbSession.createQuery("FROM SuatChieuEntity sc WHERE sc.maSuatChieu = :maSuatChieu");
            suatChieuQuery.setParameter("maSuatChieu", maSuatChieu);
            SuatChieuEntity suatChieuEntity = (SuatChieuEntity) suatChieuQuery.uniqueResult();
            Query gheQuery = dbSession.createQuery("FROM GheEntity g WHERE g.phongChieu.maPhongChieu = :maPhongChieu");
            gheQuery.setParameter("maPhongChieu", suatChieuEntity.getMaPhongChieu());
            List<GheEntity> gheList = gheQuery.list();

            List<String> selectedSeatList = Arrays.asList(selectedSeats.split(","));
            BigDecimal totalVe = BigDecimal.ZERO;
            Map<String, BigDecimal> vePrices = new HashMap<>();
            for (String seatId : selectedSeatList) {
                for (GheEntity ghe : gheList) {
                    String fullSeatId = ghe.getTenHang() + ghe.getSoGhe();
                    if (fullSeatId.equals(seatId.trim())) {
                        double heSoGia = ghe.getLoaiGhe().getHeSoGia();
                        BigDecimal giaVe = phimEntity.getGiaVe().multiply(BigDecimal.valueOf(heSoGia));
                        vePrices.put(ghe.getMaGhe(), giaVe);
                        totalVe = totalVe.add(giaVe);
                        break;
                    }
                }
            }

            Map<String, Integer> selectedCombos = new HashMap<>();
            Map<String, BigDecimal> comboPrices = new HashMap<>();
            BigDecimal totalCombo = BigDecimal.ZERO;
            for (Map.Entry<String, String> entry : allParams.entrySet()) {
                if (entry.getKey().startsWith("combo_")) {
                    String maCombo = entry.getKey().substring(6);
                    int quantity = Integer.parseInt(entry.getValue());
                    if (quantity > 0) {
                        selectedCombos.put(maCombo, quantity);
                        Query comboQuery = dbSession.createQuery("FROM ComboEntity c WHERE c.maCombo = :maCombo");
                        comboQuery.setParameter("maCombo", maCombo);
                        ComboEntity combo = (ComboEntity) comboQuery.uniqueResult();
                        BigDecimal giaCombo = combo.getGiaCombo().multiply(BigDecimal.valueOf(quantity));
                        comboPrices.put(maCombo, giaCombo);
                        totalCombo = totalCombo.add(giaCombo);
                    }
                }
            }

            Map<String, Integer> selectedBapNuocs = new HashMap<>();
            Map<String, BigDecimal> bapNuocPrices = new HashMap<>();
            BigDecimal totalBapNuoc = BigDecimal.ZERO;
            for (Map.Entry<String, String> entry : allParams.entrySet()) {
                if (entry.getKey().startsWith("bapNuoc_")) {
                    String maBapNuoc = entry.getKey().substring(8);
                    int quantity = Integer.parseInt(entry.getValue());
                    if (quantity > 0) {
                        selectedBapNuocs.put(maBapNuoc, quantity);
                        Query bapNuocQuery = dbSession.createQuery("FROM BapNuocEntity b WHERE b.maBapNuoc = :maBapNuoc");
                        bapNuocQuery.setParameter("maBapNuoc", maBapNuoc);
                        BapNuocEntity bapNuoc = (BapNuocEntity) bapNuocQuery.uniqueResult();
                        BigDecimal giaBapNuoc = bapNuoc.getGiaBapNuoc().multiply(BigDecimal.valueOf(quantity));
                        bapNuocPrices.put(maBapNuoc, giaBapNuoc);
                        totalBapNuoc = totalBapNuoc.add(giaBapNuoc);
                    }
                }
            }

            BigDecimal tongTien = totalVe.add(totalCombo).add(totalBapNuoc);

            session.setAttribute("selectedSeats", selectedSeats);
            session.setAttribute("maPhim", maPhim);
            session.setAttribute("maSuatChieu", maSuatChieu);
            session.setAttribute("selectedCombos", selectedCombos);
            session.setAttribute("selectedBapNuocs", selectedBapNuocs);
            session.setAttribute("vePrices", vePrices);
            session.setAttribute("comboPrices", comboPrices);
            session.setAttribute("bapNuocPrices", bapNuocPrices);
            session.setAttribute("tongTien", tongTien);

            model.addAttribute("tongTien", tongTien);
            model.addAttribute("selectedSeats", selectedSeatList);
            model.addAttribute("selectedCombos", selectedCombos);
            model.addAttribute("selectedBapNuocs", selectedBapNuocs);

            return "user/payment";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi tải trang thanh toán: " + e.getMessage());
            return "user/select-food";
        }
    }

    @Transactional
    @RequestMapping(value = "/confirm-payment", method = RequestMethod.POST)
    public String confirmPayment(@RequestParam("paymentMethod") String paymentMethod,
                                 @RequestParam(value = "promoCode", required = false) String promoCode,
                                 HttpSession session,
                                 HttpServletRequest request,
                                 Model model) throws java.io.UnsupportedEncodingException {
        KhachHangModel loggedInUser = (KhachHangModel) session.getAttribute("loggedInUser");
        String selectedSeats = (String) session.getAttribute("selectedSeats");
        String maPhim = (String) session.getAttribute("maPhim");
        String maSuatChieu = (String) session.getAttribute("maSuatChieu");
        @SuppressWarnings("unchecked")
        Map<String, Integer> selectedCombos = (Map<String, Integer>) session.getAttribute("selectedCombos");
        @SuppressWarnings("unchecked")
        Map<String, Integer> selectedBapNuocs = (Map<String, Integer>) session.getAttribute("selectedBapNuocs");
        @SuppressWarnings("unchecked")
        Map<String, BigDecimal> vePrices = (Map<String, BigDecimal>) session.getAttribute("vePrices");
        @SuppressWarnings("unchecked")
        Map<String, BigDecimal> comboPrices = (Map<String, BigDecimal>) session.getAttribute("comboPrices");
        @SuppressWarnings("unchecked")
        Map<String, BigDecimal> bapNuocPrices = (Map<String, BigDecimal>) session.getAttribute("bapNuocPrices");
        BigDecimal tongTien = (BigDecimal) session.getAttribute("tongTien");

        if (loggedInUser == null) {
            model.addAttribute("error", "Vui lòng đăng nhập để thanh toán");
            return "user/payment";
        }

        if (tongTien == null || tongTien.compareTo(BigDecimal.ZERO) <= 0) {
            model.addAttribute("error", "Tổng tiền không hợp lệ");
            return "user/payment";
        }

        try {
            Session dbSession = sessionFactory.getCurrentSession();

            DonHangEntity donHang = new DonHangEntity();
            String maDonHang = "DH" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
            donHang.setMaDonHang(maDonHang);
            donHang.setMaKhachHang(loggedInUser.getMaKhachHang());
            donHang.setMaKhuyenMai(promoCode != null && !promoCode.isEmpty() ? promoCode : null);
            donHang.setTongTien(tongTien);
            donHang.setDatHang(true);
            donHang.setNgayDat(new Date());
            donHang.setDiemSuDung(0);

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
                System.out.println("VNPAY Callback URL: " + returnUrl);
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
                System.out.println("Payment URL: " + paymentUrl.toString());
                return "redirect:" + paymentUrl.toString();
            } else {
                dbSession.save(donHang);

                List<String> selectedSeatList = Arrays.asList(selectedSeats.split(","));
                Query gheQuery = dbSession.createQuery("FROM GheEntity g WHERE g.phongChieu.maPhongChieu = :maPhongChieu");
                Query suatChieuQuery = dbSession.createQuery("FROM SuatChieuEntity sc WHERE sc.maSuatChieu = :maSuatChieu");
                suatChieuQuery.setParameter("maSuatChieu", maSuatChieu);
                SuatChieuEntity suatChieuEntity = (SuatChieuEntity) suatChieuQuery.uniqueResult();
                gheQuery.setParameter("maPhongChieu", suatChieuEntity.getMaPhongChieu());
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
                    VeEntity ve = new VeEntity();
                    String maVe = "VE" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
                    ve.setMaVe(maVe);
                    ve.setMaKhachHang(loggedInUser.getMaKhachHang());
                    ve.setMaSuatChieu(maSuatChieu);
                    ve.setMaGhe(selectedGhe.getMaGhe());
                    ve.setGiaVe(vePrices.get(selectedGhe.getMaGhe()));
                    ve.setNgayMua(new Date());
                    ve.setTrangThai("Đã thanh toán");
                    dbSession.save(ve);

                    ChiTietDonHangVeEntity chiTietVe = new ChiTietDonHangVeEntity();
                    chiTietVe.setDonHang(donHang);
                    chiTietVe.setMaVe(maVe);
                    dbSession.save(chiTietVe);
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

                clearSession(session);

                model.addAttribute("success", "Thanh toán thành công! Mã đơn hàng: " + maDonHang);
                return "redirect:/home/";
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

            System.out.println("VNPAY Callback Received Params: " + params);

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
            
            System.out.println("VNPAY Callback - Received Hash: " + vnp_SecureHash);
            System.out.println("VNPAY Callback - Calculated Hash: " + secureHash);
            System.out.println("VNPAY Callback - Response Code: " + vnp_ResponseCode);
            
            if (secureHash.equals(vnp_SecureHash)) {
                if ("00".equals(vnp_ResponseCode)) {
                    Session dbSession = sessionFactory.getCurrentSession();
                    DonHangEntity donHang = (DonHangEntity) session.getAttribute("pendingOrder");

                    if (donHang == null) {
                        model.addAttribute("error", "Không tìm thấy đơn hàng trong session");
                        return "user/payment";
                    }

                    dbSession.save(donHang);
                    saveOrderDetails(dbSession, donHang, session);

                    ThanhToanEntity thanhToan = new ThanhToanEntity();
                    String maThanhToan = "TT" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
                    thanhToan.setMaThanhToan(maThanhToan);
                    thanhToan.setDonHang(donHang);
                    thanhToan.setPhuongThuc("vnpay");
                    thanhToan.setSoTien(donHang.getTongTien());
                    thanhToan.setNgayThanhToan(new Date());
                    thanhToan.setTrangThai("Thành công");
                    dbSession.save(thanhToan);

                    clearSession(session);
                    session.removeAttribute("pendingOrder");

                    model.addAttribute("success", "Thanh toán thành công! Mã đơn hàng: " + donHang.getMaDonHang());
                    return "redirect:/home/";
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
                                 @RequestParam("selectedSeats") String selectedSeats,
                                 HttpSession session,
                                 Model model) {
        KhachHangModel loggedInUser = (KhachHangModel) session.getAttribute("loggedInUser");

        if (selectedSeats == null || selectedSeats.isEmpty()) {
            model.addAttribute("error", "Vui lòng chọn ít nhất một ghế");
            ModelAndView mav = new ModelAndView("forward:/booking/select-seats");
            mav.addObject("maPhim", maPhim);
            mav.addObject("maSuatChieu", maSuatChieu);
            return "forward:/booking/select-seats";
        }

        ModelAndView mav = new ModelAndView("forward:/booking/select-food");
        mav.addObject("maPhim", maPhim);
        mav.addObject("maSuatChieu", maSuatChieu);
        mav.addObject("selectedSeats", selectedSeats);
        return "forward:/booking/select-food";
    }

    @Transactional
    @RequestMapping(value = "/confirm-all", method = RequestMethod.POST)
    public String confirmAll(@RequestParam("maPhim") String maPhim,
                             @RequestParam("maSuatChieu") String maSuatChieu,
                             @RequestParam("selectedSeats") String selectedSeats,
                             @RequestParam Map<String, String> allParams,
                             HttpSession session,
                             Model model) {
        ModelAndView mav = new ModelAndView("forward:/booking/select-payment");
        mav.addObject("maPhim", maPhim);
        mav.addObject("maSuatChieu", maSuatChieu);
        mav.addObject("selectedSeats", selectedSeats);
        allParams.forEach(mav::addObject);
        return "forward:/booking/select-payment";
    }

    private String hmacSHA512(String key, String data) {
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA512");
            javax.crypto.spec.SecretKeySpec secretKey = new javax.crypto.spec.SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
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

    private void saveOrderDetails(Session dbSession, DonHangEntity donHang, HttpSession session) {
        String selectedSeats = (String) session.getAttribute("selectedSeats");
        String maSuatChieu = (String) session.getAttribute("maSuatChieu");
        @SuppressWarnings("unchecked")
        Map<String, Integer> selectedCombos = (Map<String, Integer>) session.getAttribute("selectedCombos");
        @SuppressWarnings("unchecked")
        Map<String, Integer> selectedBapNuocs = (Map<String, Integer>) session.getAttribute("selectedBapNuocs");
        @SuppressWarnings("unchecked")
        Map<String, BigDecimal> vePrices = (Map<String, BigDecimal>) session.getAttribute("vePrices");

        List<String> selectedSeatList = Arrays.asList(selectedSeats.split(","));
        Query gheQuery = dbSession.createQuery("FROM GheEntity g WHERE g.phongChieu.maPhongChieu = :maPhongChieu");
        Query suatChieuQuery = dbSession.createQuery("FROM SuatChieuEntity sc WHERE sc.maSuatChieu = :maSuatChieu");
        suatChieuQuery.setParameter("maSuatChieu", maSuatChieu);
        SuatChieuEntity suatChieuEntity = (SuatChieuEntity) suatChieuQuery.uniqueResult();
        gheQuery.setParameter("maPhongChieu", suatChieuEntity.getMaPhongChieu());
        List<GheEntity> gheList = gheQuery.list();

        KhachHangModel loggedInUser = (KhachHangModel) session.getAttribute("loggedInUser");
        for (String seatId : selectedSeatList) {
            GheEntity selectedGhe = null;
            for (GheEntity ghe : gheList) {
                String fullSeatId = ghe.getTenHang() + ghe.getSoGhe();
                if (fullSeatId.equals(seatId.trim())) {
                    selectedGhe = ghe;
                    break;
                }
            }
            VeEntity ve = new VeEntity();
            String maVe = "VE" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
            ve.setMaVe(maVe);
            ve.setMaKhachHang(loggedInUser.getMaKhachHang());
            ve.setMaSuatChieu(maSuatChieu);
            ve.setMaGhe(selectedGhe.getMaGhe());
            ve.setGiaVe(vePrices.get(selectedGhe.getMaGhe()));
            ve.setNgayMua(new Date());
            ve.setTrangThai("Đã thanh toán");
            dbSession.save(ve);

            ChiTietDonHangVeEntity chiTietVe = new ChiTietDonHangVeEntity();
            chiTietVe.setDonHang(donHang);
            chiTietVe.setMaVe(maVe);
            dbSession.save(chiTietVe);
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
    }

    private void clearSession(HttpSession session) {
        session.removeAttribute("selectedSeats");
        session.removeAttribute("maPhim");
        session.removeAttribute("maSuatChieu");
        session.removeAttribute("selectedCombos");
        session.removeAttribute("selectedBapNuocs");
        session.removeAttribute("vePrices");
        session.removeAttribute("comboPrices");
        session.removeAttribute("bapNuocPrices");
        session.removeAttribute("tongTien");
    }
}