package movie.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    @RequestMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("message", "Chào mừng quản trị viên");
        return "admin/dashboard";
    }
}