package movie.controller;

import movie.model.KhachHangModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String showUserProfile(HttpSession session, Model model) {
        KhachHangModel loggedInUser = (KhachHangModel) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            model.addAttribute("error", "Vui lòng đăng nhập để xem thông tin cá nhân");
            return "redirect:/auth/login";
        }
        model.addAttribute("user", loggedInUser);
        return "user/profile";
    }
}