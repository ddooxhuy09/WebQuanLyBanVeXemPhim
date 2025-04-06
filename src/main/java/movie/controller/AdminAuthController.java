package movie.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/auth")
public class AdminAuthController {

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String showAdminLoginPage(Model model) {
        return "admin/login"; // Tên định nghĩa Tiles
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String processAdminLogin(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {
        final String ADMIN_USERNAME = "admin";
        final String ADMIN_PASSWORD = "admin";

        try {
            if (ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password)) {
                session.setAttribute("loggedInAdmin", "admin");
                return "redirect:/admin/auth/dashboard";
            } else {
                model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng");
                return "admin/login";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi hệ thống, vui lòng thử lại sau");
            return "admin/login";
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpSession session) {
        session.removeAttribute("loggedInAdmin");
        return "redirect:/home/";
    }

    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String showAdminDashboard(HttpSession session, Model model) {
        if (session.getAttribute("loggedInAdmin") == null) {
            model.addAttribute("error", "Vui lòng đăng nhập để truy cập dashboard");
            return "redirect:/admin/auth/login";
        }
        return "admin/dashboard"; // Tên định nghĩa Tiles
    }
}