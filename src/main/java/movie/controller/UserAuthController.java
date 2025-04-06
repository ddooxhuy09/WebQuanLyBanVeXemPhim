package movie.controller;

import movie.entity.KhachHangEntity;
import movie.model.KhachHangModel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.UUID;

@Controller
@RequestMapping("/user/auth")
public class UserAuthController {

    @Autowired
    private SessionFactory sessionFactory;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String showLoginPage(Model model) {
        return "user/login";
    }

    @Transactional
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String processLogin(@RequestParam("email") String email,
                               @RequestParam("password") String password,
                               HttpSession session,
                               Model model) {
        try {
            Session dbSession = sessionFactory.getCurrentSession();
            Query query = dbSession.createQuery(
                "FROM KhachHangEntity kh WHERE kh.email = :email AND kh.matKhau = :password" // Sửa mkHau thành matKhau
            );
            query.setParameter("email", email);
            query.setParameter("password", password);
            KhachHangEntity khachHang = (KhachHangEntity) query.uniqueResult();

            if (khachHang != null) {
                KhachHangModel user = new KhachHangModel(khachHang);
                session.setAttribute("loggedInUser", user);

                String redirectMaPhim = (String) session.getAttribute("redirectMaPhim");
                String redirectMaSuatChieu = (String) session.getAttribute("redirectMaSuatChieu");
                if (redirectMaPhim != null && redirectMaSuatChieu != null) {
                    session.removeAttribute("redirectMaPhim");
                    session.removeAttribute("redirectMaSuatChieu");
                    return "redirect:/booking/select-seats?maPhim=" + redirectMaPhim + "&maSuatChieu=" + redirectMaSuatChieu;
                }
                return "redirect:/home/";
            } else {
                model.addAttribute("error", "Email hoặc mật khẩu không đúng");
                return "user/login";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi hệ thống, vui lòng thử lại sau");
            return "user/login";
        }
    }

    @Transactional
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String processRegister(@RequestParam("hoKh") String hoKh,
                                  @RequestParam("tenKh") String tenKh,
                                  @RequestParam("phone") String phone,
                                  @RequestParam("email") String email,
                                  @RequestParam("password") String password,
                                  Model model) {
        try {
            Session dbSession = sessionFactory.getCurrentSession();
            Query checkQuery = dbSession.createQuery(
                "FROM KhachHangEntity kh WHERE kh.email = :email"
            );
            checkQuery.setParameter("email", email);
            if (checkQuery.uniqueResult() != null) {
                model.addAttribute("error", "Email đã được sử dụng");
                return "user/login";
            }

            KhachHangEntity khachHang = new KhachHangEntity();
            String uniqueId = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6);
            khachHang.setMaKhachHang("KH" + uniqueId); // Sửa setMaKh thành setMaKhachHang
            khachHang.setHoKhachHang(hoKh.trim()); // Sửa setHoKh thành setHoKhachHang
            khachHang.setTenKhachHang(tenKh.trim()); // Sửa setTenKh thành setTenKhachHang
            khachHang.setSoDienThoai(phone); // Sửa setSdt thành setSoDienThoai
            khachHang.setEmail(email);
            khachHang.setMatKhau(password); // Sửa setMkHau thành setMatKhau
            khachHang.setNgayDangKy(new Date()); // Sửa setNgayDk thành setNgayDangKy
            khachHang.setTongDiem(0);
            khachHang.setNgaySinh(null);

            dbSession.save(khachHang);

            model.addAttribute("success", "Đăng ký thành công, vui lòng đăng nhập");
            return "user/login";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi đăng ký: " + e.getMessage());
            return "user/login";
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpSession session) {
        session.removeAttribute("loggedInUser");
        return "redirect:/home/";
    }
}