package movie.controller;

import movie.entity.PhimEntity;
import movie.model.PhimModel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/home/")
public class HomeController {

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String showHomePage(Model model) {
        try {
            Session session = sessionFactory.getCurrentSession();
            Query query = session.createQuery("FROM PhimEntity");
            List<PhimEntity> entities = query.list();
            List<PhimModel> phimList = new ArrayList<>();
            for (PhimEntity entity : entities) {
                phimList.add(new PhimModel(entity));
            }
            model.addAttribute("phimList", phimList);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi lấy danh sách phim");
        }
        return "user/index";
    }
}