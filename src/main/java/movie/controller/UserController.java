package movie.controller;

import movie.model.DonHangModel;
import movie.model.KhachHangModel;
import movie.entity.DonHangEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

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

        // Lấy danh sách đơn hàng của khách hàng
        Session dbSession = sessionFactory.openSession();
        try {
            Query query = dbSession.createQuery("FROM DonHangEntity WHERE maKhachHang = :maKhachHang");
            query.setParameter("maKhachHang", loggedInUser.getMaKhachHang());
            List donHangEntities = query.list();
            List<DonHangModel> donHangModels = new ArrayList<>();
            for (Object obj : donHangEntities) {
                DonHangEntity entity = (DonHangEntity) obj;
                donHangModels.add(new DonHangModel(entity));
            }
            model.addAttribute("donHangList", donHangModels);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi lấy danh sách đơn hàng: " + e.getMessage());
        } finally {
            dbSession.close();
        }

        return "user/profile";
    }
}