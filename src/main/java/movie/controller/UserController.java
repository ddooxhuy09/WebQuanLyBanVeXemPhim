package movie.controller;

import movie.model.*;
import movie.entity.*;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private SessionFactory sessionFactory;

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String showUserProfile(HttpSession session, Model model) {
        KhachHangModel loggedInUser = (KhachHangModel) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            model.addAttribute("error", "Vui lòng đăng nhập để xem thông tin cá nhân");
            return "redirect:/auth/login";
        }
        model.addAttribute("user", loggedInUser);

        Session dbSession = sessionFactory.openSession();
        try {
            Query query = dbSession.createQuery("FROM DonHangEntity WHERE maKhachHang = :maKhachHang");
            query.setParameter("maKhachHang", loggedInUser.getMaKhachHang());
            List donHangEntities = query.list();
            List<DonHangModel> donHangModels = new ArrayList<>();
            BigDecimal totalSpending = BigDecimal.ZERO;
            for (Object obj : donHangEntities) {
                DonHangEntity entity = (DonHangEntity) obj;
                DonHangModel donHangModel = new DonHangModel(entity);
                donHangModels.add(donHangModel);
                if (donHangModel.isDatHang()) {
                    totalSpending = totalSpending.add(donHangModel.getTongTien());
                }
            }
            // Calculate progress width for the spending bar
            int progressWidth;
            if (totalSpending.compareTo(new BigDecimal("4000000")) >= 0) {
                progressWidth = 90;
            } else if (totalSpending.compareTo(new BigDecimal("2000000")) >= 0) {
                progressWidth = 45;
            } else {
                progressWidth = totalSpending.divide(new BigDecimal("4000000"), 4, BigDecimal.ROUND_HALF_UP)
                        .multiply(new BigDecimal("90")).intValue();
            }
            model.addAttribute("donHangList", donHangModels);
            model.addAttribute("totalSpending", totalSpending);
            model.addAttribute("progressWidth", progressWidth);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi lấy danh sách đơn hàng: " + e.getMessage());
        } finally {
            dbSession.close();
        }

        return "user/profile";
    }

    @RequestMapping(value = "/change-password", method = RequestMethod.POST)
    public String changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        KhachHangModel loggedInUser = (KhachHangModel) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để đổi mật khẩu");
            return "redirect:/auth/login";
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu mới và xác nhận mật khẩu không khớp");
            return "redirect:/user/profile";
        }

        Session dbSession = sessionFactory.openSession();
        try {
            dbSession.beginTransaction();
            KhachHangEntity user = (KhachHangEntity) dbSession.get(KhachHangEntity.class, loggedInUser.getMaKhachHang());
            if (user == null || !user.getMatKhau().equals(currentPassword)) {
                redirectAttributes.addFlashAttribute("error", "Mật khẩu hiện tại không đúng");
                return "redirect:/user/profile";
            }

            user.setMatKhau(newPassword);
            dbSession.update(user);
            dbSession.getTransaction().commit();
            redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công!");
        } catch (Exception e) {
            if (dbSession.getTransaction() != null) {
                dbSession.getTransaction().rollback();
            }
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Lỗi khi đổi mật khẩu: " + e.getMessage());
        } finally {
            dbSession.close();
        }

        return "redirect:/user/profile";
    }

    @RequestMapping(value = "/order-details", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getOrderDetails(@RequestParam("maDonHang") String maDonHang, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        KhachHangModel loggedInUser = (KhachHangModel) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            response.put("error", "Vui lòng đăng nhập để xem chi tiết đơn hàng");
            return response;
        }

        Session dbSession = sessionFactory.openSession();
        try {
            // Get DonHang
            DonHangEntity donHang = (DonHangEntity) dbSession.get(DonHangEntity.class, maDonHang);
            if (donHang == null || !donHang.getMaKhachHang().equals(loggedInUser.getMaKhachHang())) {
                response.put("error", "Đơn hàng không tồn tại hoặc không thuộc về bạn");
                return response;
            }

            // Get Ve with Ghe and LoaiGhe details
            Query veQuery = dbSession.createQuery("FROM VeEntity v JOIN FETCH v.ghe g JOIN FETCH g.loaiGhe WHERE v.donHang.maDonHang = :maDonHang");
            veQuery.setParameter("maDonHang", maDonHang);
            List<VeEntity> veEntities = veQuery.list();
            List<Map<String, Object>> veDetails = new ArrayList<>();
            for (VeEntity ve : veEntities) {
                Map<String, Object> veMap = new HashMap<>();
                veMap.put("maVe", ve.getMaVe());
                veMap.put("soGhe", ve.getGhe().getSoGhe()); // Tên ghế
                veMap.put("tenLoaiGhe", ve.getGhe().getLoaiGhe().getTenLoaiGhe()); // Loại ghế
                veMap.put("giaVe", ve.getGiaVe()); // Giá vé
                veDetails.add(veMap);
            }

            // Get Combo
            Query comboQuery = dbSession.createQuery("FROM ChiTietDonHangComboEntity ctdh WHERE ctdh.donHang.maDonHang = :maDonHang");
            comboQuery.setParameter("maDonHang", maDonHang);
            List<ChiTietDonHangComboEntity> comboEntities = comboQuery.list();
            List<Map<String, Object>> comboDetails = new ArrayList<>();
            for (ChiTietDonHangComboEntity ctdh : comboEntities) {
                ComboEntity combo = (ComboEntity) dbSession.get(ComboEntity.class, ctdh.getMaCombo());
                if (combo != null) {
                    Map<String, Object> comboMap = new HashMap<>();
                    comboMap.put("maCombo", ctdh.getMaCombo());
                    comboMap.put("tenCombo", combo.getTenCombo()); // Tên combo
                    comboMap.put("soLuong", ctdh.getSoLuong()); // Số lượng
                    comboMap.put("giaCombo", combo.getGiaCombo()); // Giá combo
                    comboDetails.add(comboMap);
                }
            }

            // Get BapNuoc
            Query bapNuocQuery = dbSession.createQuery("FROM ChiTietDonHangBapNuocEntity ctdh WHERE ctdh.donHang.maDonHang = :maDonHang");
            bapNuocQuery.setParameter("maDonHang", maDonHang);
            List<ChiTietDonHangBapNuocEntity> bapNuocEntities = bapNuocQuery.list();
            List<Map<String, Object>> bapNuocDetails = new ArrayList<>();
            for (ChiTietDonHangBapNuocEntity ctdh : bapNuocEntities) {
                BapNuocEntity bapNuoc = (BapNuocEntity) dbSession.get(BapNuocEntity.class, ctdh.getMaBapNuoc());
                if (bapNuoc != null) {
                    Map<String, Object> bapNuocMap = new HashMap<>();
                    bapNuocMap.put("maBapNuoc", ctdh.getMaBapNuoc());
                    bapNuocMap.put("tenBapNuoc", bapNuoc.getTenBapNuoc()); // Tên bắp nước
                    bapNuocMap.put("soLuong", ctdh.getSoLuong()); // Số lượng
                    bapNuocMap.put("giaBapNuoc", bapNuoc.getGiaBapNuoc()); // Giá bắp nước
                    bapNuocDetails.add(bapNuocMap);
                }
            }

            response.put("veList", veDetails);
            response.put("comboList", comboDetails);
            response.put("bapNuocList", bapNuocDetails);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Lỗi khi lấy chi tiết đơn hàng: " + e.getMessage());
        } finally {
            dbSession.close();
        }

        System.out.println("Order details response: " + response);
        return response;
    }
}