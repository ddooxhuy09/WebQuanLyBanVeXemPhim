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
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.UUID;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private SessionFactory sessionFactory;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String showLoginPage(Model model) {
        return "auth/login";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String showRegisterPage(Model model) {
        return "auth/register";
    }

    @Transactional
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ModelAndView processLogin(@RequestParam("username") String username,
                                     @RequestParam("password") String password,
                                     HttpSession session,
                                     Model model) {
        try {
            if (username == null || password == null) {
                model.addAttribute("error", "Vui lòng nhập đầy đủ thông tin đăng nhập");
                return new ModelAndView("auth/login");
            }

            // Kiểm tra admin
            final String ADMIN_USERNAME = "admin";
            final String ADMIN_PASSWORD = "admin";
            if (ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password)) {
                session.setAttribute("loggedInAdmin", "admin");
                return new ModelAndView("redirect:/admin/dashboard");
            }

            // Kiểm tra user
            Session dbSession = sessionFactory.getCurrentSession();
            Query query = dbSession.createQuery(
                "FROM KhachHangEntity kh WHERE kh.email = :email AND kh.matKhau = :password"
            );
            query.setParameter("email", username);
            query.setParameter("password", password);
            KhachHangEntity khachHang = (KhachHangEntity) query.uniqueResult();

            if (khachHang != null) {
                KhachHangModel user = new KhachHangModel(khachHang);
                session.setAttribute("loggedInUser", user);
                
                String redirectUrl = (String) session.getAttribute("redirectAfterLogin");
                String redirectMaPhim = (String) session.getAttribute("redirectMaPhim");
                String redirectMaSuatChieu = (String) session.getAttribute("redirectMaSuatChieu");
                
                if (redirectUrl != null && redirectUrl.contains("/booking/select-seats") && 
                    redirectMaPhim != null && redirectMaSuatChieu != null) {
                    session.removeAttribute("redirectAfterLogin");
                    session.removeAttribute("redirectMaPhim");
                    session.removeAttribute("redirectMaSuatChieu");
                    // Chuyển hướng với query parameters
                    String redirectWithParams = String.format("redirect:/booking/select-seats?maPhim=%s&maSuatChieu=%s",
                                                             redirectMaPhim, redirectMaSuatChieu);
                    return new ModelAndView(redirectWithParams);
                } else if (redirectUrl != null && !redirectUrl.isEmpty()) {
                    session.removeAttribute("redirectAfterLogin");
                    session.removeAttribute("redirectMaPhim");
                    session.removeAttribute("redirectMaSuatChieu");
                    return new ModelAndView("redirect:" + redirectUrl);
                }
                
                return new ModelAndView("redirect:/home/");
            }

            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng");
            return new ModelAndView("auth/login");
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi hệ thống, vui lòng thử lại sau");
            return new ModelAndView("auth/login");
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
                return "auth/register";
            }

            KhachHangEntity khachHang = new KhachHangEntity();
            String uniqueId = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6);
            khachHang.setMaKhachHang("KH" + uniqueId);
            khachHang.setHoKhachHang(hoKh.trim());
            khachHang.setTenKhachHang(tenKh.trim());
            khachHang.setSoDienThoai(phone);
            khachHang.setEmail(email);
            khachHang.setMatKhau(password);
            khachHang.setNgayDangKy(new Date());
            khachHang.setTongDiem(0);
            khachHang.setNgaySinh(null);

            dbSession.save(khachHang);

            model.addAttribute("success", "Đăng ký thành công, vui lòng đăng nhập");
            return "auth/login";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi đăng ký: " + e.getMessage());
            return "auth/register";
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/home/";
    }
}