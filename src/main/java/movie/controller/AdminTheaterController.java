package movie.controller;

import movie.entity.RapChieuEntity;
import movie.model.RapChieuModel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminTheaterController {

    @Autowired
    private SessionFactory sessionFactory;

    // Hiển thị danh sách rạp chiếu và form thêm mới
    @RequestMapping(value = "/theaters", method = RequestMethod.GET)
    public String showTheaterManager(HttpSession session, Model model) {
        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/auth/login";
        }

        Session dbSession = sessionFactory.openSession();
        try {
            // Lấy mã rạp mới nhất để tạo mã mới
            Query query = dbSession.createQuery("FROM RapChieuEntity ORDER BY maRapChieu DESC");
            query.setMaxResults(1);
            RapChieuEntity latestRap = (RapChieuEntity) query.uniqueResult();

            String newMaRapChieu;
            if (latestRap == null) {
                newMaRapChieu = "RC001";
            } else {
                String lastMaRap = latestRap.getMaRapChieu();
                int lastId = Integer.parseInt(lastMaRap.substring(2));
                newMaRapChieu = String.format("RC%03d", lastId + 1);
            }

            // Lấy danh sách tất cả rạp chiếu
            Query allRapQuery = dbSession.createQuery("FROM RapChieuEntity");
            @SuppressWarnings("unchecked")
            List<RapChieuEntity> rapEntities = (List<RapChieuEntity>) allRapQuery.list();
            List<RapChieuModel> rapModels = new ArrayList<>();
            for (RapChieuEntity entity : rapEntities) {
                rapModels.add(new RapChieuModel(entity));
            }

            model.addAttribute("rapChieuList", rapModels);
            model.addAttribute("newMaRapChieu", newMaRapChieu);
            model.addAttribute("isEdit", false);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi lấy danh sách rạp chiếu: " + e.getMessage());
        } finally {
            dbSession.close();
        }

        return "admin/theater_manager";
    }

    // Thêm rạp chiếu mới
    @Transactional
    @RequestMapping(value = "/theaters/add", method = RequestMethod.POST)
    public String processAddTheater(
            @RequestParam("maRapChieu") String maRapChieu,
            @RequestParam("tenRapChieu") String tenRapChieu,
            @RequestParam("diaChi") String diaChi,
            @RequestParam("soDienThoaiLienHe") String soDienThoaiLienHe,
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/auth/login";
        }

        try {
            Session dbSession = sessionFactory.getCurrentSession();

            RapChieuEntity rap = new RapChieuEntity();
            rap.setMaRapChieu(maRapChieu);
            rap.setTenRapChieu(tenRapChieu);
            rap.setDiaChi(diaChi);
            rap.setSoDienThoaiLienHe(soDienThoaiLienHe);

            dbSession.save(rap);

            return "redirect:/admin/theaters";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi thêm rạp chiếu: " + e.getMessage());
            return "admin/theater_manager";
        }
    }

    // Hiển thị modal sửa rạp chiếu (trong thực tế, JSP đã xử lý qua JavaScript)
    // Ở đây chỉ cần redirect lại trang chính sau khi chỉnh sửa
    @RequestMapping(value = "/theaters/edit/{maRapChieu}", method = RequestMethod.GET)
    public String showEditTheaterForm(@PathVariable("maRapChieu") String maRapChieu, HttpSession session, Model model) {
        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/auth/login";
        }

        // Modal đã được xử lý trong JSP, nên chỉ cần load lại trang
        return "redirect:/admin/theaters";
    }

    // Cập nhật thông tin rạp chiếu
    @Transactional
    @RequestMapping(value = "/theaters/update", method = RequestMethod.POST)
    public String processUpdateTheater(
            @RequestParam("maRapChieu") String maRapChieu,
            @RequestParam("tenRapChieu") String tenRapChieu,
            @RequestParam("diaChi") String diaChi,
            @RequestParam("soDienThoaiLienHe") String soDienThoaiLienHe,
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/auth/login";
        }

        try {
            Session dbSession = sessionFactory.getCurrentSession();

            RapChieuEntity rap = (RapChieuEntity) dbSession.get(RapChieuEntity.class, maRapChieu);
            if (rap == null) {
                model.addAttribute("error", "Không tìm thấy rạp chiếu với mã " + maRapChieu);
                return "redirect:/admin/theaters";
            }

            rap.setTenRapChieu(tenRapChieu);
            rap.setDiaChi(diaChi);
            rap.setSoDienThoaiLienHe(soDienThoaiLienHe);

            dbSession.update(rap);

            return "redirect:/admin/theaters";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi cập nhật rạp chiếu: " + e.getMessage());
            return "admin/theater_manager";
        }
    }

    // Xóa rạp chiếu
    @Transactional
    @RequestMapping(value = "/theaters/delete/{maRapChieu}", method = RequestMethod.GET)
    public String deleteTheater(@PathVariable("maRapChieu") String maRapChieu, HttpSession session, Model model) {
        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/auth/login";
        }

        try {
            Session dbSession = sessionFactory.getCurrentSession();
            RapChieuEntity rap = (RapChieuEntity) dbSession.get(RapChieuEntity.class, maRapChieu);

            if (rap != null) {
                dbSession.delete(rap);
            } else {
                model.addAttribute("error", "Không tìm thấy rạp chiếu với mã " + maRapChieu);
            }

            return "redirect:/admin/theaters";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi xóa rạp chiếu: " + e.getMessage());
            return "redirect:/admin/theaters";
        }
    }
}