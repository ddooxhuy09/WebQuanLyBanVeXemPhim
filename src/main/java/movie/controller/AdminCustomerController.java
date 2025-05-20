package movie.controller;

import movie.entity.DonHangEntity;
import movie.entity.KhachHangEntity;
import movie.model.DonHangModel;
import movie.model.KhachHangModel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminCustomerController {

    @Autowired
    private SessionFactory sessionFactory;

    @RequestMapping(value = "/customers", method = RequestMethod.GET)
    public String showCustomerManager(Model model) {
        Session dbSession = sessionFactory.openSession();
        try {
            // Lấy danh sách khách hàng, tương tự AdminMovieController
            Query query = dbSession.createQuery("FROM KhachHangEntity");
            List khachHangEntities = query.list(); // Không chỉ định kiểu ngay
            List<KhachHangModel> customerList = new ArrayList<>();
            for (Object obj : khachHangEntities) {
                KhachHangEntity entity = (KhachHangEntity) obj;
                customerList.add(new KhachHangModel(entity));
            }
            model.addAttribute("customerList", customerList);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi lấy danh sách khách hàng: " + e.getMessage());
        } finally {
            dbSession.close();
        }
        return "admin/customer_manager";
    }

    @Transactional
    @RequestMapping(value = "/customers/delete/{maKhachHang}", method = RequestMethod.GET)
    public String deleteCustomer(@PathVariable("maKhachHang") String maKhachHang, Model model) {
        try {
            Session dbSession = sessionFactory.getCurrentSession();
            KhachHangEntity khachHang = (KhachHangEntity) dbSession.get(KhachHangEntity.class, maKhachHang);
            if (khachHang != null) {
                // Kiểm tra đơn hàng, tương tự cách AdminMovieController kiểm tra liên kết
                Query query = dbSession.createQuery("FROM DonHangEntity WHERE maKhachHang = :maKhachHang");
                query.setParameter("maKhachHang", maKhachHang);
                List donHangs = query.list();
                if (!donHangs.isEmpty()) {
                    model.addAttribute("error", "Không thể xóa khách hàng vì có đơn hàng liên quan.");
                    return "redirect:/admin/customers";
                }
                dbSession.delete(khachHang);
            } else {
                model.addAttribute("error", "Không tìm thấy khách hàng với mã " + maKhachHang);
            }
            return "redirect:/admin/customers";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi xóa khách hàng: " + e.getMessage());
            return "redirect:/admin/customers";
        }
    }

    @RequestMapping(value = "/customers/orders/{maKhachHang}", method = RequestMethod.GET)
    @ResponseBody
    public List<DonHangModel> getCustomerOrders(@PathVariable("maKhachHang") String maKhachHang) {
        Session dbSession = sessionFactory.openSession();
        try {
            // Lấy danh sách đơn hàng theo khách hàng
            Query query = dbSession.createQuery("FROM DonHangEntity WHERE maKhachHang = :maKhachHang");
            query.setParameter("maKhachHang", maKhachHang);
            List donHangEntities = query.list();
            List<DonHangModel> orderList = new ArrayList<>();
            for (Object obj : donHangEntities) {
                DonHangEntity entity = (DonHangEntity) obj;
                orderList.add(new DonHangModel(entity));
            }
            return orderList;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            dbSession.close();
        }
    }
}