package movie.controller;

import movie.entity.BapNuocEntity;
import movie.entity.ComboEntity;
import movie.entity.ChiTietComboEntity;
import movie.model.BapNuocModel;
import movie.model.ComboModel;
import movie.model.ChiTietComboModel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminFoodComboController {

    @Autowired
    private SessionFactory sessionFactory;

    @RequestMapping(value = "/food-combo", method = RequestMethod.GET)
    public String showFoodComboManager(Model model) {
        Session dbSession = sessionFactory.openSession();
        try {
            Query bapNuocQuery = dbSession.createQuery("FROM BapNuocEntity ORDER BY maBapNuoc DESC");
            bapNuocQuery.setMaxResults(1);
            BapNuocEntity latestBapNuoc = (BapNuocEntity) bapNuocQuery.uniqueResult();
            String newMaBapNuoc = latestBapNuoc == null ? "BN001" : String.format("BN%03d", 
                Integer.parseInt(latestBapNuoc.getMaBapNuoc().substring(2)) + 1);

            Query comboQuery = dbSession.createQuery("FROM ComboEntity ORDER BY maCombo DESC");
            comboQuery.setMaxResults(1);
            ComboEntity latestCombo = (ComboEntity) comboQuery.uniqueResult();
            String newMaCombo = latestCombo == null ? "CB001" : String.format("CB%03d", 
                Integer.parseInt(latestCombo.getMaCombo().substring(2)) + 1);

            Query allBapNuocQuery = dbSession.createQuery("FROM BapNuocEntity");
            List<BapNuocEntity> bapNuocEntities = allBapNuocQuery.list();
            List<BapNuocModel> bapNuocList = new ArrayList<>();
            for (BapNuocEntity entity : bapNuocEntities) {
                bapNuocList.add(new BapNuocModel(entity));
            }

            Query allComboQuery = dbSession.createQuery("FROM ComboEntity c LEFT JOIN FETCH c.chiTietCombos");
            List<ComboEntity> comboEntities = allComboQuery.list();
            List<ComboModel> comboList = new ArrayList<>();
            for (ComboEntity entity : comboEntities) {
                ComboModel comboModel = new ComboModel(entity);
                List<ChiTietComboModel> chiTietComboModels = new ArrayList<>();
                for (ChiTietComboEntity chiTiet : entity.getChiTietCombos()) {
                    chiTietComboModels.add(new ChiTietComboModel(chiTiet));
                }
                model.addAttribute("chiTietCombo_" + entity.getMaCombo(), chiTietComboModels);
                comboList.add(comboModel);
            }

            model.addAttribute("bapNuocList", bapNuocList);
            model.addAttribute("comboList", comboList);
            model.addAttribute("newMaBapNuoc", newMaBapNuoc);
            model.addAttribute("newMaCombo", newMaCombo);
            model.addAttribute("isEdit", false);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi lấy danh sách bắp nước và combo: " + e.getMessage());
        } finally {
            dbSession.close();
        }
        return "admin/food_combo_manager";
    }

    @Transactional
    @RequestMapping(value = "/food-combo/add-bapnuoc", method = RequestMethod.POST)
    public String addBapNuoc(
            @RequestParam("maBapNuoc") String maBapNuoc,
            @RequestParam("tenBapNuoc") String tenBapNuoc,
            @RequestParam("giaBapNuoc") BigDecimal giaBapNuoc,
            Model model) {
        try {
            Session dbSession = sessionFactory.getCurrentSession();
            BapNuocEntity bapNuoc = new BapNuocEntity();
            bapNuoc.setMaBapNuoc(maBapNuoc);
            bapNuoc.setTenBapNuoc(tenBapNuoc);
            bapNuoc.setGiaBapNuoc(giaBapNuoc);
            dbSession.save(bapNuoc);
            return "redirect:/admin/food-combo";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi thêm bắp nước: " + e.getMessage());
            return "admin/food_combo_manager";
        }
    }

    @Transactional
    @RequestMapping(value = "/food-combo/add-combo", method = RequestMethod.POST)
    public String addCombo(
            @RequestParam("maCombo") String maCombo,
            @RequestParam("tenCombo") String tenCombo,
            @RequestParam("giaCombo") BigDecimal giaCombo,
            @RequestParam("moTa") String moTa,
            @RequestParam(value = "bapNuocIds", required = false) List<String> bapNuocIds,
            @RequestParam(value = "soLuongs", required = false) List<Integer> soLuongs,
            Model model) {
        try {
            Session dbSession = sessionFactory.getCurrentSession();
            ComboEntity combo = new ComboEntity();
            combo.setMaCombo(maCombo);
            combo.setTenCombo(tenCombo);
            combo.setGiaCombo(giaCombo);
            combo.setMoTa(moTa);

            List<ChiTietComboEntity> chiTietCombos = new ArrayList<>();
            if (bapNuocIds != null && soLuongs != null && bapNuocIds.size() == soLuongs.size()) {
                for (int i = 0; i < bapNuocIds.size(); i++) {
                    ChiTietComboEntity chiTiet = new ChiTietComboEntity();
                    chiTiet.setMaCombo(maCombo);
                    chiTiet.setMaBapNuoc(bapNuocIds.get(i));
                    chiTiet.setSoLuong(soLuongs.get(i));
                    chiTiet.setCombo(combo);
                    chiTietCombos.add(chiTiet);
                }
            }
            combo.setChiTietCombos(chiTietCombos);
            dbSession.save(combo);
            return "redirect:/admin/food-combo";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi thêm combo: " + e.getMessage());
            return "admin/food_combo_manager";
        }
    }

    @Transactional
    @RequestMapping(value = "/food-combo/update", method = RequestMethod.POST)
    public String updateFoodCombo(
            @RequestParam("ma") String ma,
            @RequestParam("ten") String ten,
            @RequestParam("gia") BigDecimal gia,
            @RequestParam(value = "moTa", required = false) String moTa,
            @RequestParam("loai") String loai,
            @RequestParam(value = "bapNuocIds", required = false) List<String> bapNuocIds,
            @RequestParam(value = "soLuongs", required = false) List<Integer> soLuongs,
            Model model) {
        try {
            Session dbSession = sessionFactory.getCurrentSession();
            if ("Bắp Nước".equals(loai)) {
                BapNuocEntity bapNuoc = (BapNuocEntity) dbSession.get(BapNuocEntity.class, ma);
                if (bapNuoc != null) {
                    bapNuoc.setTenBapNuoc(ten);
                    bapNuoc.setGiaBapNuoc(gia);
                    dbSession.update(bapNuoc);
                } else {
                    model.addAttribute("error", "Không tìm thấy bắp nước với mã " + ma);
                }
            } else if ("Combo".equals(loai)) {
                ComboEntity combo = (ComboEntity) dbSession.get(ComboEntity.class, ma);
                if (combo != null) {
                    combo.setTenCombo(ten);
                    combo.setGiaCombo(gia);
                    combo.setMoTa(moTa);

                    Query deleteQuery = dbSession.createQuery("DELETE FROM ChiTietComboEntity WHERE maCombo = :maCombo");
                    deleteQuery.setParameter("maCombo", ma);
                    deleteQuery.executeUpdate();

                    List<ChiTietComboEntity> chiTietCombos = new ArrayList<>();
                    if (bapNuocIds != null && soLuongs != null && bapNuocIds.size() == soLuongs.size()) {
                        for (int i = 0; i < bapNuocIds.size(); i++) {
                            ChiTietComboEntity chiTiet = new ChiTietComboEntity();
                            chiTiet.setMaCombo(ma);
                            chiTiet.setMaBapNuoc(bapNuocIds.get(i));
                            chiTiet.setSoLuong(soLuongs.get(i));
                            chiTiet.setCombo(combo);
                            chiTietCombos.add(chiTiet);
                        }
                    }
                    combo.setChiTietCombos(chiTietCombos);
                    dbSession.update(combo);
                } else {
                    model.addAttribute("error", "Không tìm thấy combo với mã " + ma);
                }
            }
            return "redirect:/admin/food-combo";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi cập nhật: " + e.getMessage());
            return "admin/food_combo_manager";
        }
    }

    @Transactional
    @RequestMapping(value = "/food-combo/delete/{ma}", method = RequestMethod.GET)
    public String deleteFoodCombo(@PathVariable("ma") String ma, Model model) {
        try {
            Session dbSession = sessionFactory.getCurrentSession();
            if (ma.startsWith("BN")) {
                BapNuocEntity bapNuoc = (BapNuocEntity) dbSession.get(BapNuocEntity.class, ma);
                if (bapNuoc != null) {
                    dbSession.delete(bapNuoc);
                } else {
                    model.addAttribute("error", "Không tìm thấy bắp nước với mã " + ma);
                }
            } else if (ma.startsWith("CB")) {
                ComboEntity combo = (ComboEntity) dbSession.get(ComboEntity.class, ma);
                if (combo != null) {
                    dbSession.delete(combo);
                } else {
                    model.addAttribute("error", "Không tìm thấy combo với mã " + ma);
                }
            }
            return "redirect:/admin/food-combo";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi xóa: " + e.getMessage());
            return "redirect:/admin/food-combo";
        }
    }
}