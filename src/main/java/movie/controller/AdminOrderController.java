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

    private static final int ITEMS_PER_PAGE = 10;

    @SuppressWarnings("deprecation")
    @RequestMapping(value = "/orders", method = RequestMethod.GET)
    public String showOrderManager(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "sort", defaultValue = "date-desc") String sortBy,
            Model model) {
        Session dbSession = sessionFactory.openSession();
        try {
            // Xây dựng truy vấn HQL với sắp xếp
            String hql = "FROM DonHangEntity d";
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
                    hql = "FROM DonHangEntity d LEFT JOIN FETCH d.maKhachHang k";
                    orderBy = " ORDER BY k.tenKhachHang ASC";
                    break;
                case "customer-desc":
                    hql = "FROM DonHangEntity d LEFT JOIN FETCH d.maKhachHang k";
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

            // Tính phân trang
            int totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);
            int start = (page - 1) * ITEMS_PER_PAGE;

            // Lấy danh sách đơn hàng phân trang
            Query query = dbSession.createQuery(hql);
            query.setFirstResult(start);
            query.setMaxResults(ITEMS_PER_PAGE);
            List<DonHangEntity> donHangEntities = query.list();

            // Chuyển sang DonHangModel
            List<DonHangModel> donHangModels = donHangEntities.stream()
                    .map(DonHangModel::new)
                    .collect(Collectors.toList());

            // Gắn thông tin khách hàng vào DonHangModel
            for (DonHangModel donHang : donHangModels) {
                Query khachHangQuery = dbSession.createQuery("FROM KhachHangEntity WHERE maKhachHang = :maKhachHang");
                khachHangQuery.setParameter("maKhachHang", donHang.getMaKhachHang());
                KhachHangEntity khachHang = (KhachHangEntity) khachHangQuery.uniqueResult();
                if (khachHang != null) {
                    donHang.setKhachHang(new KhachHangModel(khachHang));
                }
            }

            model.addAttribute("donHangList", donHangModels);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("sortBy", sortBy);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi lấy danh sách đơn hàng: " + e.getMessage());
        } finally {
            dbSession.close();
        }
        return "admin/order_manager";
    }

    @SuppressWarnings("deprecation")
    @RequestMapping(value = "/orders/detail/{maDonHang}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getOrderDetail(@PathVariable("maDonHang") String maDonHang) {
        Session dbSession = sessionFactory.openSession();
        Map<String, Object> response = new HashMap<>();
        try {
            // Lấy đơn hàng
            Query donHangQuery = dbSession.createQuery("FROM DonHangEntity d WHERE d.maDonHang = :maDonHang");
            donHangQuery.setParameter("maDonHang", maDonHang);
            DonHangEntity donHang = (DonHangEntity) donHangQuery.uniqueResult();

            if (donHang == null) {
                response.put("error", "Không tìm thấy đơn hàng với mã " + maDonHang);
                return response;
            }

            // Lấy thông tin khách hàng
            Query khachHangQuery = dbSession.createQuery("FROM KhachHangEntity WHERE maKhachHang = :maKhachHang");
            khachHangQuery.setParameter("maKhachHang", donHang.getMaKhachHang());
            KhachHangEntity khachHang = (KhachHangEntity) khachHangQuery.uniqueResult();

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

            // Lấy thông tin vé
            List<Map<String, String>> tickets = new ArrayList<>();
            Query veQuery = dbSession.createQuery("FROM VeEntity v WHERE v.donHang.maDonHang = :maDonHang");
            veQuery.setParameter("maDonHang", maDonHang);
            List<VeEntity> veList = veQuery.list();

            for (VeEntity ve : veList) {
                Map<String, String> ticket = new HashMap<>();
                Query gheQuery = dbSession.createQuery("FROM GheEntity WHERE maGhe = :maGhe");
                gheQuery.setParameter("maGhe", ve.getMaGhe());
                GheEntity ghe = (GheEntity) gheQuery.uniqueResult();

                if (ghe != null) {
                    ticket.put("thongTinGhe", ghe.getTenHang() + ghe.getSoGhe());
                    ticket.put("loaiGhe", ghe.getLoaiGhe() != null ? ghe.getLoaiGhe().getTenLoaiGhe() : "N/A");
                    ticket.put("giaTien", ve.getGiaVe().toString() + " VNĐ");
                    tickets.add(ticket);
                }
            }

            // Lấy thông tin combo và bắp nước từ ChiTietDonHangCombo và ChiTietDonHangBapNuoc
            List<Map<String, String>> combos = new ArrayList<>();
            BigDecimal comboTotal = BigDecimal.ZERO;

            // Combo từ ChiTietDonHangCombo
            Query comboQuery = dbSession.createQuery("FROM ChiTietDonHangComboEntity c WHERE c.donHang.maDonHang = :maDonHang");
            comboQuery.setParameter("maDonHang", maDonHang);
            List<ChiTietDonHangComboEntity> comboList = comboQuery.list();

            for (ChiTietDonHangComboEntity combo : comboList) {
                Map<String, String> comboMap = new HashMap<>();
                Query comboEntityQuery = dbSession.createQuery("FROM ComboEntity WHERE maCombo = :maCombo");
                comboEntityQuery.setParameter("maCombo", combo.getMaCombo());
                ComboEntity comboEntity = (ComboEntity) comboEntityQuery.uniqueResult();

                if (comboEntity != null) {
                    comboMap.put("tenDichVu", comboEntity.getTenCombo());
                    comboMap.put("soLuong", String.valueOf(combo.getSoLuong()));
                    comboMap.put("donGia", comboEntity.getGiaCombo().toString() + " VNĐ");
                    BigDecimal tongTienCombo = comboEntity.getGiaCombo().multiply(new BigDecimal(combo.getSoLuong()));
                    comboMap.put("tongTien", tongTienCombo.toString() + " VNĐ");
                    comboTotal = comboTotal.add(tongTienCombo);
                    combos.add(comboMap);
                }
            }

            // Bắp nước từ ChiTietDonHangBapNuoc
            Query bapNuocQuery = dbSession.createQuery("FROM ChiTietDonHangBapNuocEntity b WHERE b.donHang.maDonHang = :maDonHang");
            bapNuocQuery.setParameter("maDonHang", maDonHang);
            List<ChiTietDonHangBapNuocEntity> bapNuocList = bapNuocQuery.list();

            for (ChiTietDonHangBapNuocEntity bapNuoc : bapNuocList) {
                Map<String, String> bapNuocMap = new HashMap<>();
                Query bapNuocEntityQuery = dbSession.createQuery("FROM BapNuocEntity WHERE maBapNuoc = :maBapNuoc");
                bapNuocEntityQuery.setParameter("maBapNuoc", bapNuoc.getMaBapNuoc());
                BapNuocEntity bapNuocEntity = (BapNuocEntity) bapNuocEntityQuery.uniqueResult();

                if (bapNuocEntity != null) {
                    bapNuocMap.put("tenDichVu", bapNuocEntity.getTenBapNuoc());
                    bapNuocMap.put("soLuong", String.valueOf(bapNuoc.getSoLuong()));
                    bapNuocMap.put("donGia", bapNuocEntity.getGiaBapNuoc().toString() + " VNĐ");
                    BigDecimal tongTienBapNuoc = bapNuocEntity.getGiaBapNuoc().multiply(new BigDecimal(bapNuoc.getSoLuong()));
                    bapNuocMap.put("tongTien", tongTienBapNuoc.toString() + " VNĐ");
                    comboTotal = comboTotal.add(tongTienBapNuoc);
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

            // Xây dựng phản hồi JSON
            response.put("maDonHang", donHang.getMaDonHang());
            response.put("tenPhim", tenPhim);
            response.put("gioChieu", gioChieu);
            response.put("ngayChieu", ngayChieu);
            response.put("phongChieu", phongChieu);
            response.put("rapChieu", rapChieu);
            response.put("ngayDat", dateFormat.format(donHang.getNgayDat()));
            response.put("tenKhachHang", khachHang != null ? khachHang.getTenKhachHang() : "N/A");
            response.put("dienThoai", khachHang != null ? khachHang.getSoDienThoai() : "N/A");
            response.put("email", khachHang != null ? khachHang.getEmail() : "N/A");
            response.put("trangThai", donHang.isDatHang() ? "Đã xác nhận" : "Chưa xác nhận");
            response.put("maKhuyenMai", donHang.getMaKhuyenMai() != null ? donHang.getMaKhuyenMai() : "Không có");
            response.put("giamGia", "0 VNĐ"); // Giả sử chưa có logic giảm giá
            response.put("phuThu", "0 VNĐ"); // Giả sử chưa có logic phụ thu
            response.put("thanhTien", donHang.getTongTien().toString() + " VNĐ");
            response.put("tongTien", donHang.getTongTien().toString() + " VNĐ");
            response.put("tickets", tickets);
            response.put("combos", combos);
            response.put("comboTotal", comboTotal.toString() + " VNĐ");

        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Lỗi khi lấy chi tiết đơn hàng: " + e.getMessage());
        } finally {
            dbSession.close();
        }
        return response;
    }
}