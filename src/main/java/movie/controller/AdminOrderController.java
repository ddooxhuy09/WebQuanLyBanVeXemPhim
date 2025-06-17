package movie.controller;

import movie.entity.*;
import movie.model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminOrderController {

    @Autowired
    private SessionFactory sessionFactory;

    private static final int ITEMS_PER_PAGE = 25; // Tăng lên 25 để hiển thị nhiều hơn
    private static final int PAGES_TO_SHOW = 5; // Giữ số trang hiển thị là 5

    @RequestMapping(value = "/orders", method = RequestMethod.GET)
    public String showOrderManager(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "sort", defaultValue = "date-desc") String sortBy,
            Model model) {
        Session dbSession = sessionFactory.openSession();
        try {
            // Xây dựng truy vấn HQL với JOIN FETCH để lấy thông tin khách hàng
            String hql = "FROM DonHangEntity d LEFT JOIN FETCH d.khachHang k";
            String orderBy;

            switch (sortBy) {
                case "date-asc":
                    orderBy = " ORDER BY d.ngayDat ASC";
                    break;
                case "price-desc":
                    orderBy = " ORDER BY d.tongTien DESC";
                    break;
                case "price-asc":
                    orderBy = " ORDER BY d.tongTien ASC";
                    break;
                case "order-id-asc":
                    orderBy = " ORDER BY d.maDonHang ASC";
                    break;
                case "order-id-desc":
                    orderBy = " ORDER BY d.maDonHang DESC";
                    break;
                case "customer-asc":
                    orderBy = " ORDER BY k.tenKhachHang ASC";
                    break;
                case "customer-desc":
                    orderBy = " ORDER BY k.tenKhachHang DESC";
                    break;
                case "date-desc":
                default:
                    orderBy = " ORDER BY d.ngayDat DESC";
                    sortBy = "date-desc";
                    break;
            }

            hql += orderBy;

            // Đếm tổng số đơn hàng
            Query countQuery = dbSession.createQuery("SELECT COUNT(d) FROM DonHangEntity d");
            Long totalItems = (Long) countQuery.uniqueResult();

            // Tính tổng số trang
            int totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);
            int start = (page - 1) * ITEMS_PER_PAGE;

            // Lấy danh sách đơn hàng phân trang
            Query query = dbSession.createQuery(hql);
            query.setFirstResult(start);
            query.setMaxResults(ITEMS_PER_PAGE);
            List<DonHangEntity> donHangEntities = query.list();

            // Chuyển sang DonHangModel và gán KhachHangModel
            List<DonHangModel> donHangModels = donHangEntities.stream()
                    .map(d -> {
                        DonHangModel donHangModel = new DonHangModel(d);
                        if (d.getKhachHang() != null) {
                            KhachHangModel khachHangModel = new KhachHangModel(d.getKhachHang());
                            donHangModel.setKhachHang(khachHangModel);
                        }
                        return donHangModel;
                    })
                    .collect(Collectors.toList());

            // Tính phạm vi trang hiển thị
            List<Integer> pageRange = new ArrayList<>();
            int startPage;
            int endPage;

            // Kiểm tra xem có phải lần đầu truy cập không
            boolean isInitialRequest = (page == 1 && "date-desc".equals(sortBy));
            if (isInitialRequest) {
                // Nếu là lần đầu truy cập, bắt đầu từ trang 1
                startPage = 1;
                endPage = Math.min(totalPages, PAGES_TO_SHOW);
            } else {
                // Nếu không, tính phạm vi dựa trên trang hiện tại
                startPage = Math.max(1, page - (PAGES_TO_SHOW / 2));
                endPage = Math.min(totalPages, startPage + PAGES_TO_SHOW - 1);
                startPage = Math.max(1, endPage - PAGES_TO_SHOW + 1);
            }

            for (int i = startPage; i <= endPage; i++) {
                pageRange.add(i);
            }

            model.addAttribute("donHangList", donHangModels);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("pageRange", pageRange); // Thêm phạm vi trang

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi lấy danh sách đơn hàng: " + e.getMessage());
        } finally {
            dbSession.close();
        }
        return "admin/order_manager";
    }

    @RequestMapping(value = "/orders/detail/{maDonHang}", method = RequestMethod.GET)
    public String getOrderDetail(
            @PathVariable("maDonHang") String maDonHang,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "sort", defaultValue = "date-desc") String sortBy,
            Model model) {
        Session dbSession = sessionFactory.openSession();
        try {
            // Lấy danh sách đơn hàng để hiển thị lại danh sách
            String hql = "FROM DonHangEntity d LEFT JOIN FETCH d.khachHang k";
            String orderBy;

            switch (sortBy) {
                case "date-asc":
                    orderBy = " ORDER BY d.ngayDat ASC";
                    break;
                case "price-desc":
                    orderBy = " ORDER BY d.tongTien DESC";
                    break;
                case "price-asc":
                    orderBy = " ORDER BY d.tongTien ASC";
                    break;
                case "order-id-asc":
                    orderBy = " ORDER BY d.maDonHang ASC";
                    break;
                case "order-id-desc":
                    orderBy = " ORDER BY d.maDonHang DESC";
                    break;
                case "customer-asc":
                    orderBy = " ORDER BY k.tenKhachHang ASC";
                    break;
                case "customer-desc":
                    orderBy = " ORDER BY k.tenKhachHang DESC";
                    break;
                case "date-desc":
                default:
                    orderBy = " ORDER BY d.ngayDat DESC";
                    sortBy = "date-desc";
                    break;
            }

            hql += orderBy;

            Query countQuery = dbSession.createQuery("SELECT COUNT(d) FROM DonHangEntity d");
            Long totalItems = (Long) countQuery.uniqueResult();
            int totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);
            int start = (page - 1) * ITEMS_PER_PAGE;

            Query query = dbSession.createQuery(hql);
            query.setFirstResult(start);
            query.setMaxResults(ITEMS_PER_PAGE);
            List<DonHangEntity> donHangEntities = query.list();

            List<DonHangModel> donHangModels = donHangEntities.stream()
                    .map(d -> {
                        DonHangModel donHangModel = new DonHangModel(d);
                        if (d.getKhachHang() != null) {
                            KhachHangModel khachHangModel = new KhachHangModel(d.getKhachHang());
                            donHangModel.setKhachHang(khachHangModel);
                        }
                        return donHangModel;
                    })
                    .collect(Collectors.toList());

            // Tính phạm vi trang hiển thị (giữ logic động cho getOrderDetail)
            List<Integer> pageRange = new ArrayList<>();
            int startPage = Math.max(1, page - (PAGES_TO_SHOW / 2));
            int endPage = Math.min(totalPages, startPage + PAGES_TO_SHOW - 1);
            startPage = Math.max(1, endPage - PAGES_TO_SHOW + 1);

            for (int i = startPage; i <= endPage; i++) {
                pageRange.add(i);
            }

            model.addAttribute("donHangList", donHangModels);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("pageRange", pageRange); // Thêm phạm vi trang

            // Lấy chi tiết đơn hàng
            Query donHangQuery = dbSession.createQuery("FROM DonHangEntity d LEFT JOIN FETCH d.khachHang k WHERE d.maDonHang = :maDonHang");
            donHangQuery.setParameter("maDonHang", maDonHang);
            DonHangEntity donHang = (DonHangEntity) donHangQuery.uniqueResult();

            if (donHang == null) {
                model.addAttribute("error", "Không tìm thấy đơn hàng với mã " + maDonHang);
                return "admin/order_manager";
            }

            // Lấy thông tin vé
            List<Map<String, String>> tickets = new ArrayList<>();
            Query veQuery = dbSession.createQuery("FROM VeEntity v WHERE v.donHang.maDonHang = :maDonHang");
            veQuery.setParameter("maDonHang", maDonHang);
            List<VeEntity> veList = veQuery.list();

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            Date currentDate = new Date(); // Ngày hiện tại: 14/06/2025, 4:20 PM +07

            for (VeEntity ve : veList) {
                Map<String, String> ticket = new HashMap<>();
                Query gheQuery = dbSession.createQuery("FROM GheEntity WHERE maGhe = :maGhe");
                gheQuery.setParameter("maGhe", ve.getMaGhe());
                GheEntity ghe = (GheEntity) gheQuery.uniqueResult();

                if (ghe != null) {
                    ticket.put("thongTinGhe", ghe.getTenHang() + ghe.getSoGhe());
                    ticket.put("loaiGhe", ghe.getLoaiGhe() != null ? ghe.getLoaiGhe().getTenLoaiGhe() : "N/A");
                    ticket.put("giaTien", ve.getGiaVe() != null ? ve.getGiaVe().toString() : "0");
                    tickets.add(ticket);
                }
            }

            // Lấy mã code của khuyến mãi
            String maCode = "Không có";
            if (donHang.getMaKhuyenMai() != null) {
                Query khuyenMaiQuery = dbSession.createQuery("FROM KhuyenMaiEntity km WHERE km.maKhuyenMai = :maKhuyenMai");
                khuyenMaiQuery.setParameter("maKhuyenMai", donHang.getMaKhuyenMai());
                KhuyenMaiEntity khuyenMai = (KhuyenMaiEntity) khuyenMaiQuery.uniqueResult();
                if (khuyenMai != null) {
                    maCode = khuyenMai.getMaCode() != null ? khuyenMai.getMaCode() : "Không có";
                }
            }

            // Lấy thông tin combo và bắp nước
            List<Map<String, String>> combos = new ArrayList<>();
            BigDecimal comboTotal = BigDecimal.ZERO;

            Query comboQuery = dbSession.createQuery("FROM ChiTietDonHangComboEntity c WHERE c.donHang.maDonHang = :maDonHang");
            comboQuery.setParameter("maDonHang", maDonHang);
            List<ChiTietDonHangComboEntity> comboList = comboQuery.list();

            for (ChiTietDonHangComboEntity comboOrder : comboList) {
                Map<String, String> comboMap = new HashMap<>();
                Query comboEntityQuery = dbSession.createQuery("FROM ComboEntity WHERE maCombo = :maCombo");
                comboEntityQuery.setParameter("maCombo", comboOrder.getMaCombo());
                ComboEntity combo = (ComboEntity) comboEntityQuery.uniqueResult();

                if (combo != null) {
                    comboMap.put("tenDichVu", combo.getTenCombo());
                    comboMap.put("soLuong", String.valueOf(comboOrder.getSoLuong()));
                    
                    // Tính tổng giá combo dựa trên ChiTietComboEntity
                    BigDecimal comboPrice = BigDecimal.ZERO;
                    Query chiTietComboQuery = dbSession.createQuery("FROM ChiTietComboEntity ctc WHERE ctc.combo.maCombo = :maCombo");
                    chiTietComboQuery.setParameter("maCombo", comboOrder.getMaCombo());
                    List<ChiTietComboEntity> chiTietCombos = chiTietComboQuery.list();
                    for (ChiTietComboEntity chiTiet : chiTietCombos) {
                        if (chiTiet.getBapNuoc() != null && chiTiet.getBapNuoc().getGiaBapNuoc() != null && chiTiet.getSoLuong() != null) {
                            comboPrice = comboPrice.add(chiTiet.getBapNuoc().getGiaBapNuoc().multiply(new BigDecimal(chiTiet.getSoLuong())));
                        }
                    }
                    comboMap.put("donGia", comboPrice.toString());
                    BigDecimal tongTienCombo = comboPrice.multiply(new BigDecimal(comboOrder.getSoLuong()));
                    comboMap.put("tongTien", tongTienCombo.toString());
                    comboTotal = comboTotal.add(tongTienCombo != null ? tongTienCombo : BigDecimal.ZERO);
                    combos.add(comboMap);
                }
            }

            Query bapNuocQuery = dbSession.createQuery("FROM ChiTietDonHangBapNuocEntity b WHERE b.donHang.maDonHang = :maDonHang");
            bapNuocQuery.setParameter("maDonHang", maDonHang);
            List<ChiTietDonHangBapNuocEntity> bapNuocList = bapNuocQuery.list();

            for (ChiTietDonHangBapNuocEntity bapNuocOrder : bapNuocList) {
                Map<String, String> bapNuocMap = new HashMap<>();
                Query bapNuocEntityQuery = dbSession.createQuery("FROM BapNuocEntity WHERE maBapNuoc = :maBapNuoc");
                bapNuocEntityQuery.setParameter("maBapNuoc", bapNuocOrder.getMaBapNuoc());
                BapNuocEntity bapNuoc = (BapNuocEntity) bapNuocEntityQuery.uniqueResult();

                if (bapNuoc != null) {
                    bapNuocMap.put("tenDichVu", bapNuoc.getTenBapNuoc());
                    bapNuocMap.put("soLuong", String.valueOf(bapNuocOrder.getSoLuong()));
                    bapNuocMap.put("donGia", bapNuoc.getGiaBapNuoc() != null ? bapNuoc.getGiaBapNuoc().toString() : "0");
                    BigDecimal tongTienBapNuoc = (bapNuoc.getGiaBapNuoc() != null && bapNuocOrder.getSoLuong() > 0)
                        ? bapNuoc.getGiaBapNuoc().multiply(new BigDecimal(bapNuocOrder.getSoLuong()))
                        : BigDecimal.ZERO;
                    bapNuocMap.put("tongTien", tongTienBapNuoc.toString());
                    comboTotal = comboTotal.add(tongTienBapNuoc != null ? tongTienBapNuoc : BigDecimal.ZERO);
                    combos.add(bapNuocMap);
                }
            }

            // Lấy thông tin phim, suất chiếu, phòng chiếu, rạp chiếu
            String tenPhim = "N/A";
            String gioChieu = "N/A";
            String ngayChieu = "N/A";
            String phongChieu = "N/A";
            String rapChieu = "N/A";

            if (!veList.isEmpty()) {
                VeEntity ve = veList.get(0);
                Query suatChieuQuery = dbSession.createQuery("FROM SuatChieuEntity WHERE maSuatChieu = :maSuatChieu");
                suatChieuQuery.setParameter("maSuatChieu", ve.getMaSuatChieu());
                SuatChieuEntity suatChieu = (SuatChieuEntity) suatChieuQuery.uniqueResult();

                if (suatChieu != null) {
                    Query phimQuery = dbSession.createQuery("FROM PhimEntity WHERE maPhim = :maPhim");
                    phimQuery.setParameter("maPhim", suatChieu.getMaPhim());
                    PhimEntity phim = (PhimEntity) phimQuery.uniqueResult();

                    Query phongChieuQuery = dbSession.createQuery("FROM PhongChieuEntity WHERE maPhongChieu = :maPhongChieu");
                    phongChieuQuery.setParameter("maPhongChieu", suatChieu.getMaPhongChieu());
                    PhongChieuEntity pc = (PhongChieuEntity) phongChieuQuery.uniqueResult();

                    if (phim != null) tenPhim = phim.getTenPhim();
                    if (suatChieu.getNgayGioChieu() != null) {
                        gioChieu = timeFormat.format(suatChieu.getNgayGioChieu());
                        ngayChieu = dateFormat.format(suatChieu.getNgayGioChieu());
                    }
                    if (pc != null) {
                        phongChieu = pc.getTenPhongChieu();
                        RapChieuEntity rap = pc.getRapChieu();
                        if (rap != null) rapChieu = rap.getTenRapChieu();
                    }
                }
            }

            // Lấy phương thức thanh toán
            String phuongThucThanhToan = "N/A";
            if (!donHang.getThanhToans().isEmpty()) {
                ThanhToanEntity thanhToan = donHang.getThanhToans().get(0); // Giả định lấy thanh toán đầu tiên
                phuongThucThanhToan = thanhToan.getPhuongThuc() != null ? thanhToan.getPhuongThuc() : "N/A";
            }

            // Điền dữ liệu chi tiết đơn hàng vào Model
            model.addAttribute("showDetail", true);
            model.addAttribute("maDonHang", donHang.getMaDonHang());
            model.addAttribute("tenPhim", tenPhim);
            model.addAttribute("gioChieu", gioChieu);
            model.addAttribute("ngayChieu", ngayChieu);
            model.addAttribute("phongChieu", phongChieu);
            model.addAttribute("rapChieu", rapChieu);
            model.addAttribute("ngayDat", dateFormat.format(donHang.getNgayDat()));
            model.addAttribute("tenKhachHang", donHang.getKhachHang() != null ? donHang.getKhachHang().getHoVaTen() : "N/A");
            model.addAttribute("dienThoai", donHang.getKhachHang() != null ? donHang.getKhachHang().getSoDienThoai() : "N/A");
            model.addAttribute("email", donHang.getKhachHang() != null ? donHang.getKhachHang().getEmail() : "N/A");
            model.addAttribute("maCode", maCode);
            model.addAttribute("diemSuDung", donHang.getDiemSuDung() != null ? donHang.getDiemSuDung().toString() : "0");
            model.addAttribute("tongTien", donHang.getTongTien() != null ? donHang.getTongTien().toString() : "0");
            model.addAttribute("phuongThucThanhToan", phuongThucThanhToan);
            model.addAttribute("tickets", tickets);
            model.addAttribute("combos", combos);
            model.addAttribute("comboTotal", comboTotal != null ? comboTotal.toString() : "0");

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi lấy chi tiết đơn hàng: " + e.getMessage());
        } finally {
            dbSession.close();
        }
        return "admin/order_manager";
    }
}