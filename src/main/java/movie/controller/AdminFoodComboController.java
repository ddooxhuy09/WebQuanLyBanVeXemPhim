package movie.controller;

import movie.entity.BapNuocEntity;
import movie.entity.ComboEntity;
import movie.entity.ChiTietComboEntity;
import movie.model.BapNuocModel;
import movie.model.ChiTietComboModel;
import movie.model.ComboModel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Controller
@RequestMapping("/admin/food-combo")
public class AdminFoodComboController {

    private static final Logger logger = Logger.getLogger(AdminFoodComboController.class.getName());

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private ServletContext context;

    @RequestMapping(method = RequestMethod.GET)
    public String showFoodComboManager(
            @RequestParam(value = "editMa", required = false) String editMa,
            @RequestParam(value = "editLoai", required = false) String editLoai,
            Model model,
            HttpServletRequest request) {
        Session dbSession = sessionFactory.openSession();
        
        try {
            // LẤY DANH SÁCH BẮP NƯỚC
            Query bapNuocQuery = dbSession.createQuery("FROM BapNuocEntity");
            List<BapNuocEntity> listBapNuoc = (List<BapNuocEntity>) bapNuocQuery.list();
            
            List<BapNuocModel> bapNuocList = new ArrayList<>();
            for (BapNuocEntity entity : listBapNuoc) {
                BapNuocModel modelItem = new BapNuocModel();
                modelItem.setMaBapNuoc(entity.getMaBapNuoc());
                modelItem.setTenBapNuoc(entity.getTenBapNuoc());
                modelItem.setGiaBapNuoc(entity.getGiaBapNuoc());
                modelItem.setUrlHinhAnh(entity.getUrlHinhAnh());
                bapNuocList.add(modelItem);
            }

            // LẤY DANH SÁCH COMBO
            Query comboQuery = dbSession.createQuery(
                "SELECT DISTINCT c FROM ComboEntity c LEFT JOIN FETCH c.chiTietCombos"
            );
            List<ComboEntity> listCombo = (List<ComboEntity>) comboQuery.list();
            
            List<ComboModel> comboList = new ArrayList<>();
            for (ComboEntity entity : listCombo) {
                ComboModel modelItem = new ComboModel();
                modelItem.setMaCombo(entity.getMaCombo());
                modelItem.setTenCombo(entity.getTenCombo());
                modelItem.setGiaCombo(entity.getGiaCombo());
                modelItem.setMoTa(entity.getMoTa());
                modelItem.setUrlHinhAnh(entity.getUrlHinhAnh());
                
                List<ChiTietComboModel> chiTietList = new ArrayList<>();
                for (ChiTietComboEntity ctEntity : entity.getChiTietCombos()) {
                    ChiTietComboModel ctModel = new ChiTietComboModel();
                    ctModel.setMaCombo(entity.getMaCombo());
                    ctModel.setMaBapNuoc(ctEntity.getMaBapNuoc());
                    ctModel.setSoLuong(ctEntity.getSoLuong());
                    chiTietList.add(ctModel);
                }
                modelItem.setChiTietCombos(chiTietList);
                comboList.add(modelItem);
            }

            // LẤY THÔNG TIN CHI TIẾT NẾU CÓ YÊU CẦU CHỈNH SỬA
            Object editItem = null;
            if (editMa != null && editLoai != null) {
                if ("Bắp Nước".equals(editLoai)) {
                    BapNuocEntity bapNuoc = (BapNuocEntity) dbSession.get(BapNuocEntity.class, editMa);
                    if (bapNuoc != null) {
                        BapNuocModel bapNuocModel = new BapNuocModel();
                        bapNuocModel.setMaBapNuoc(bapNuoc.getMaBapNuoc());
                        bapNuocModel.setTenBapNuoc(bapNuoc.getTenBapNuoc());
                        bapNuocModel.setGiaBapNuoc(bapNuoc.getGiaBapNuoc());
                        bapNuocModel.setUrlHinhAnh(bapNuoc.getUrlHinhAnh());
                        editItem = bapNuocModel;
                    }
                } else if ("Combo".equals(editLoai)) {
                    Query editComboQuery = dbSession.createQuery(
                        "SELECT DISTINCT c FROM ComboEntity c LEFT JOIN FETCH c.chiTietCombos WHERE c.maCombo = :maCombo"
                    );
                    editComboQuery.setParameter("maCombo", editMa);
                    ComboEntity combo = (ComboEntity) editComboQuery.uniqueResult();
                    if (combo != null) {
                        ComboModel comboModel = new ComboModel();
                        comboModel.setMaCombo(combo.getMaCombo());
                        comboModel.setTenCombo(combo.getTenCombo());
                        comboModel.setGiaCombo(combo.getGiaCombo());
                        comboModel.setMoTa(combo.getMoTa());
                        comboModel.setUrlHinhAnh(combo.getUrlHinhAnh());

                        List<ChiTietComboModel> chiTietList = new ArrayList<>();
                        for (ChiTietComboEntity ctEntity : combo.getChiTietCombos()) {
                            ChiTietComboModel ctModel = new ChiTietComboModel(ctEntity);
                            chiTietList.add(ctModel);
                        }
                        comboModel.setChiTietCombos(chiTietList);
                        editItem = comboModel;
                    }
                }
            }

            // Tạo mã mới cho cả Bắp Nước và Combo để hiển thị trong modal thêm
            Map<String, String> newMaMap = new HashMap<>();
            newMaMap.put("Bắp Nước", generateId("Bắp Nước", dbSession));
            newMaMap.put("Combo", generateId("Combo", dbSession));

            // TRUYỀN DỮ LIỆU VÀO MODEL
            model.addAttribute("bapNuocList", bapNuocList);
            model.addAttribute("comboList", comboList);
            model.addAttribute("allBapNuocList", bapNuocList);
            model.addAttribute("editItem", editItem);
            model.addAttribute("editLoai", editLoai);
            model.addAttribute("showEditModal", editMa != null && editLoai != null);
            model.addAttribute("newMaMap", newMaMap);

        } catch (Exception e) {
            logger.severe("Error loading data: " + e.getMessage());
            model.addAttribute("error", "Lỗi khi tải dữ liệu: " + e.getMessage());
        } finally {
            dbSession.close();
        }
        
        return "admin/food_combo_manager";
    }

    // Hàm kiểm tra tên trùng
    private boolean checkDuplicateTen(String ten, String loai, String ma, Session dbSession) {
        String hql;
        if ("Bắp Nước".equals(loai)) {
            hql = "SELECT COUNT(*) FROM BapNuocEntity WHERE tenBapNuoc = :ten AND maBapNuoc != :ma";
        } else {
            hql = "SELECT COUNT(*) FROM ComboEntity WHERE tenCombo = :ten AND maCombo != :ma";
        }
        Query query = dbSession.createQuery(hql)
            .setParameter("ten", ten)
            .setParameter("ma", ma != null ? ma : "");
        Long count = (Long) query.uniqueResult();
        return count > 0;
    }

    // Hàm hỗ trợ upload hình ảnh
    private String handleImageUpload(MultipartFile imageFile, String oldImagePath) throws Exception {
        logger.info("Handling image upload. Old path: " + (oldImagePath != null ? oldImagePath : "null"));
        if (imageFile == null || imageFile.isEmpty()) {
            if (oldImagePath == null) {
                throw new IllegalArgumentException("Vui lòng chọn file hình ảnh!");
            }
            logger.info("No new image uploaded, keeping old image: " + oldImagePath);
            return oldImagePath;
        }

        String contentType = imageFile.getContentType();
        if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)) {
            throw new IllegalArgumentException("Hình ảnh phải là file jpg hoặc png.");
        }
        if (imageFile.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("Kích thước hình ảnh không được vượt quá 5MB.");
        }

        String uploadDir = context.getRealPath("/resources/images/");
        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists()) {
            if (!uploadPath.mkdirs()) {
                logger.severe("Cannot create directory: " + uploadPath.getAbsolutePath());
                throw new RuntimeException("Không thể tạo thư mục: " + uploadPath.getAbsolutePath());
            }
        }

        if (!uploadPath.canWrite()) {
            logger.severe("No write permission for directory: " + uploadPath.getAbsolutePath());
            throw new RuntimeException("Không có quyền ghi file vào thư mục: " + uploadPath.getAbsolutePath());
        }

        String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
        File destination = new File(uploadPath, fileName);
        logger.info("Saving image to: " + destination.getAbsolutePath());
        imageFile.transferTo(destination);

        if (oldImagePath != null && !oldImagePath.equals("resources/images/" + fileName)) {
            File oldFile = new File(uploadDir + oldImagePath.replace("resources/images/", ""));
            if (oldFile.exists()) {
                logger.info("Deleting old image: " + oldFile.getAbsolutePath());
                if (!oldFile.delete()) {
                    logger.warning("Failed to delete old image: " + oldFile.getAbsolutePath());
                }
            }
        }

        return "resources/images/" + fileName;
    }

    // Hàm hỗ trợ sinh mã tự động
    private String generateId(String loai, Session dbSession) {
        String prefix = "Bắp Nước".equals(loai) ? "BN" : "CB";
        String hql;
        if ("Bắp Nước".equals(loai)) {
            hql = "SELECT MAX(CAST(SUBSTRING(maBapNuoc, 3, 3) AS integer)) FROM BapNuocEntity WHERE maBapNuoc LIKE :prefix";
        } else {
            hql = "SELECT MAX(CAST(SUBSTRING(maCombo, 3, 3) AS integer)) FROM ComboEntity WHERE maCombo LIKE :prefix";
        }
        Query query = dbSession.createQuery(hql)
            .setParameter("prefix", prefix + "%");
        Integer maxNum = (Integer) query.uniqueResult();
        if (maxNum == null) maxNum = 0;
        return String.format("%s%03d", prefix, maxNum + 1);
    }

    @Transactional
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addItem(
            @RequestParam("loai") String loai,
            @RequestParam(value = "ten", required = false) String ten,
            @RequestParam(value = "gia", required = false) String giaStr,
            @RequestParam(value = "moTa", required = false) String moTa,
            @RequestParam("hinhAnh") MultipartFile hinhAnhFile,
            @RequestParam(value = "bapNuocHidden", required = false) String bapNuocHidden,
            Model model,
            HttpServletRequest request) {
        Session dbSession = sessionFactory.getCurrentSession();
        List<String> errors = new ArrayList<>();
        BigDecimal gia = null;
        List<String> bapNuocIds = new ArrayList<>();
        List<Integer> soLuongs = new ArrayList<>();

        try {
            // Validate tên
            if (ten == null || ten.trim().isEmpty()) {
                errors.add("Tên không được để trống.");
            } else if (checkDuplicateTen(ten.trim(), loai, null, dbSession)) {
                errors.add("Tên " + ten.trim() + " đã tồn tại trong hệ thống.");
            }

            // Làm sạch và chuyển đổi giá
            if (giaStr == null || giaStr.trim().isEmpty()) {
                errors.add("Giá không được để trống.");
            } else {
                try {
                    String cleanedGia = giaStr.replaceAll("[^0-9.]", ""); // Loại bỏ tất cả ký tự không phải số hoặc dấu chấm
                    if (cleanedGia.isEmpty()) {
                        errors.add("Giá phải là số hợp lệ.");
                    } else {
                        gia = new BigDecimal(cleanedGia).setScale(2, BigDecimal.ROUND_HALF_UP);
                        if (gia.compareTo(BigDecimal.ZERO) <= 0) {
                            errors.add("Giá phải là số dương.");
                        }
                    }
                } catch (NumberFormatException e) {
                    errors.add("Giá không đúng định dạng số.");
                }
            }

            // Validate bắp nước trong combo
            if ("Combo".equals(loai)) {
                if (bapNuocHidden == null || bapNuocHidden.trim().isEmpty()) {
                    errors.add("Danh sách bắp nước không được để trống.");
                } else {
                    String[] bapNuocPairs = bapNuocHidden.split(",");
                    for (String pair : bapNuocPairs) {
                        String[] parts = pair.split(":");
                        if (parts.length != 2) {
                            errors.add("Dữ liệu bắp nước không hợp lệ: " + pair);
                            break;
                        }
                        String maBapNuoc = parts[0].trim();
                        int soLuong;
                        try {
                            soLuong = Integer.parseInt(parts[1].trim());
                            if (soLuong <= 0) {
                                errors.add("Số lượng bắp nước phải là số dương cho mã " + maBapNuoc + ".");
                                break;
                            }
                        } catch (NumberFormatException e) {
                            errors.add("Số lượng bắp nước không hợp lệ cho mã " + maBapNuoc + ".");
                            break;
                        }
                        BapNuocEntity bapNuoc = (BapNuocEntity) dbSession.get(BapNuocEntity.class, maBapNuoc);
                        if (bapNuoc == null) {
                            errors.add("Mã bắp nước " + maBapNuoc + " không tồn tại trong hệ thống.");
                            break;
                        }
                        bapNuocIds.add(maBapNuoc);
                        soLuongs.add(soLuong);
                    }
                }
            }

            // Nếu có lỗi, trả về trang với dữ liệu form
            if (!errors.isEmpty()) {
                Map<String, Object> addFormData = new HashMap<>();
                addFormData.put("loai", loai);
                addFormData.put("ten", ten);
                addFormData.put("gia", giaStr);
                addFormData.put("moTa", moTa);
                addFormData.put("bapNuocIds", bapNuocIds);
                addFormData.put("soLuongs", soLuongs);
                model.addAttribute("addFormData", addFormData);
                model.addAttribute("error", String.join(" ", errors));
                return showFoodComboManager(null, null, model, request);
            }

            String urlHinhAnh = handleImageUpload(hinhAnhFile, null);
            String ma = generateId(loai, dbSession);

            if ("Bắp Nước".equals(loai)) {
                BapNuocEntity bapNuoc = new BapNuocEntity();
                bapNuoc.setMaBapNuoc(ma);
                bapNuoc.setTenBapNuoc(ten.trim());
                bapNuoc.setGiaBapNuoc(gia);
                bapNuoc.setUrlHinhAnh(urlHinhAnh);
                dbSession.save(bapNuoc);
                logger.info("Saved BapNuocEntity: " + ma);
            } else if ("Combo".equals(loai)) {
                ComboEntity combo = new ComboEntity();
                combo.setMaCombo(ma);
                combo.setTenCombo(ten.trim());
                combo.setGiaCombo(gia);
                combo.setMoTa(moTa != null ? moTa.trim() : null);
                combo.setUrlHinhAnh(urlHinhAnh);

                List<ChiTietComboEntity> chiTietCombos = new ArrayList<>();
                for (int i = 0; i < bapNuocIds.size(); i++) {
                    ChiTietComboEntity chiTiet = new ChiTietComboEntity();
                    chiTiet.setCombo(combo);
                    chiTiet.setMaBapNuoc(bapNuocIds.get(i));
                    chiTiet.setSoLuong(soLuongs.get(i));
                    chiTietCombos.add(chiTiet);
                }

                combo.setChiTietCombos(chiTietCombos);
                dbSession.save(combo);
                logger.info("Saved ComboEntity: " + ma + " with " + chiTietCombos.size() + " ChiTietComboEntity");
            }

            model.addAttribute("success", "Thêm " + loai + " thành công!");
            return "redirect:/admin/food-combo";

        } catch (Exception e) {
            logger.severe("Error adding item: " + e.getMessage());
            Map<String, Object> addFormData = new HashMap<>();
            addFormData.put("loai", loai);
            addFormData.put("ten", ten);
            addFormData.put("gia", giaStr);
            addFormData.put("moTa", moTa);
            addFormData.put("bapNuocIds", bapNuocIds);
            addFormData.put("soLuongs", soLuongs);
            model.addAttribute("addFormData", addFormData);
            model.addAttribute("error", "Lỗi khi thêm: " + e.getMessage());
            return showFoodComboManager(null, null, model, request);
        }
    }

    @Transactional
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String editItem(
            @RequestParam(value = "loai", required = false) String loai,
            @RequestParam(value = "ma", required = false) String ma,
            @RequestParam(value = "ten", required = false) String ten,
            @RequestParam(value = "gia", required = false) String giaStr,
            @RequestParam(value = "moTa", required = false) String moTa,
            @RequestParam("hinhAnh") MultipartFile hinhAnhFile,
            @RequestParam(value = "bapNuocHidden", required = false) String bapNuocHidden,
            Model model,
            HttpServletRequest request) {
        Session dbSession = sessionFactory.getCurrentSession();
        List<String> errors = new ArrayList<>();
        BigDecimal gia = null;
        List<String> bapNuocIds = new ArrayList<>();
        List<Integer> soLuongs = new ArrayList<>();

        // Infer loai and ma if not provided
        if (loai == null || loai.trim().isEmpty() || ma == null || ma.trim().isEmpty()) {
            Object editItem = model.asMap().get("editItem");
            if (editItem instanceof BapNuocModel) {
                loai = "Bắp Nước";
                ma = ((BapNuocModel) editItem).getMaBapNuoc();
            } else if (editItem instanceof ComboModel) {
                loai = "Combo";
                ma = ((ComboModel) editItem).getMaCombo();
            }
            if (ma == null || loai == null) {
                model.addAttribute("error", "Không thể xác định mã hoặc loại của mục cần chỉnh sửa.");
                return showFoodComboManager(null, null, model, request);
            }
            logger.info("Inferred loai: " + loai + ", ma: " + ma);
        }

        try {
            // Validate tên
            if (ten == null || ten.trim().isEmpty()) {
                errors.add("Tên không được để trống.");
            } else if (checkDuplicateTen(ten.trim(), loai, ma, dbSession)) {
                errors.add("Tên " + ten.trim() + " đã tồn tại trong hệ thống.");
            }

            // Làm sạch và chuyển đổi giá
            if (giaStr == null || giaStr.trim().isEmpty()) {
                errors.add("Giá không được để trống.");
            } else {
                try {
                    String cleanedGia = giaStr.replaceAll("[^0-9.]", ""); // Loại bỏ tất cả ký tự không phải số hoặc dấu chấm
                    if (cleanedGia.isEmpty()) {
                        errors.add("Giá phải là số hợp lệ.");
                    } else {
                        gia = new BigDecimal(cleanedGia).setScale(2, BigDecimal.ROUND_HALF_UP);
                        if (gia.compareTo(BigDecimal.ZERO) <= 0) {
                            errors.add("Giá phải là số dương.");
                        }
                    }
                } catch (NumberFormatException e) {
                    errors.add("Giá không đúng định dạng số.");
                }
            }

            // Validate bắp nước trong combo
            if ("Combo".equals(loai)) {
                if (bapNuocHidden == null || bapNuocHidden.trim().isEmpty()) {
                    errors.add("Danh sách bắp nước không được để trống.");
                } else {
                    String[] bapNuocPairs = bapNuocHidden.split(",");
                    for (String pair : bapNuocPairs) {
                        String[] parts = pair.split(":");
                        if (parts.length != 2) {
                            errors.add("Dữ liệu bắp nước không hợp lệ: " + pair);
                            break;
                        }
                        String maBapNuoc = parts[0].trim();
                        int soLuong;
                        try {
                            soLuong = Integer.parseInt(parts[1].trim());
                            if (soLuong <= 0) {
                                errors.add("Số lượng bắp nước phải là số dương cho mã " + maBapNuoc + ".");
                                break;
                            }
                        } catch (NumberFormatException e) {
                            errors.add("Số lượng bắp nước không hợp lệ cho mã " + maBapNuoc + ".");
                            break;
                        }
                        BapNuocEntity bapNuoc = (BapNuocEntity) dbSession.get(BapNuocEntity.class, maBapNuoc);
                        if (bapNuoc == null) {
                            errors.add("Mã bắp nước " + maBapNuoc + " không tồn tại trong hệ thống.");
                            break;
                        }
                        bapNuocIds.add(maBapNuoc);
                        soLuongs.add(soLuong);
                    }
                }
            }

            // Nếu có lỗi, trả về trang với dữ liệu chỉnh sửa
            if (!errors.isEmpty()) {
                Object editItem = null;
                if ("Bắp Nước".equals(loai)) {
                    BapNuocModel bapNuocModel = new BapNuocModel();
                    bapNuocModel.setMaBapNuoc(ma);
                    bapNuocModel.setTenBapNuoc(ten);
                    bapNuocModel.setGiaBapNuoc(gia);
                    editItem = bapNuocModel;
                } else {
                    ComboModel comboModel = new ComboModel();
                    comboModel.setMaCombo(ma);
                    comboModel.setTenCombo(ten);
                    comboModel.setGiaCombo(gia);
                    comboModel.setMoTa(moTa);
                    List<ChiTietComboModel> chiTietList = new ArrayList<>();
                    for (int i = 0; i < bapNuocIds.size(); i++) {
                        ChiTietComboModel ctModel = new ChiTietComboModel();
                        ctModel.setMaBapNuoc(bapNuocIds.get(i));
                        ctModel.setSoLuong(soLuongs.get(i));
                        chiTietList.add(ctModel);
                    }
                    comboModel.setChiTietCombos(chiTietList);
                    editItem = comboModel;
                }
                model.addAttribute("editItem", editItem);
                model.addAttribute("editLoai", loai);
                model.addAttribute("showEditModal", true);
                model.addAttribute("error", String.join(" ", errors));
                return showFoodComboManager(ma, loai, model, request);
            }

            if ("Bắp Nước".equals(loai)) {
                BapNuocEntity bapNuoc = (BapNuocEntity) dbSession.get(BapNuocEntity.class, ma);
                if (bapNuoc == null) {
                    model.addAttribute("error", "Không tìm thấy bắp nước với mã " + ma);
                    return showFoodComboManager(null, null, model, request);
                }

                String urlHinhAnh = handleImageUpload(hinhAnhFile, bapNuoc.getUrlHinhAnh());
                bapNuoc.setTenBapNuoc(ten.trim());
                bapNuoc.setGiaBapNuoc(gia);
                bapNuoc.setUrlHinhAnh(urlHinhAnh);

                dbSession.update(bapNuoc);
                logger.info("Updated BapNuocEntity: " + ma);
            } else if ("Combo".equals(loai)) {
                ComboEntity combo = (ComboEntity) dbSession.get(ComboEntity.class, ma);
                if (combo == null) {
                    model.addAttribute("error", "Không tìm thấy combo với mã " + ma);
                    return showFoodComboManager(null, null, model, request);
                }

                String urlHinhAnh = handleImageUpload(hinhAnhFile, combo.getUrlHinhAnh());
                combo.setTenCombo(ten.trim());
                combo.setGiaCombo(gia);
                combo.setMoTa(moTa != null ? moTa.trim() : null);
                combo.setUrlHinhAnh(urlHinhAnh);

                // Xóa chi tiết combo cũ
                combo.getChiTietCombos().clear();
                dbSession.flush();

                // Thêm chi tiết combo mới
                List<ChiTietComboEntity> chiTietCombos = new ArrayList<>();
                for (int i = 0; i < bapNuocIds.size(); i++) {
                    ChiTietComboEntity chiTiet = new ChiTietComboEntity();
                    chiTiet.setCombo(combo);
                    chiTiet.setMaBapNuoc(bapNuocIds.get(i));
                    chiTiet.setSoLuong(soLuongs.get(i));
                    chiTietCombos.add(chiTiet);
                }

                combo.setChiTietCombos(chiTietCombos);
                dbSession.update(combo);
                logger.info("Updated ComboEntity: " + ma + " with " + chiTietCombos.size() + " ChiTietComboEntity");
            }

            model.addAttribute("success", "Cập nhật " + loai + " thành công!");
            return "redirect:/admin/food-combo";

        } catch (Exception e) {
            logger.severe("Error updating item: " + e.getMessage());
            Object editItem = null;
            if ("Bắp Nước".equals(loai)) {
                BapNuocModel bapNuocModel = new BapNuocModel();
                bapNuocModel.setMaBapNuoc(ma);
                bapNuocModel.setTenBapNuoc(ten);
                bapNuocModel.setGiaBapNuoc(gia);
                editItem = bapNuocModel;
            } else {
                ComboModel comboModel = new ComboModel();
                comboModel.setMaCombo(ma);
                comboModel.setTenCombo(ten);
                comboModel.setGiaCombo(gia);
                comboModel.setMoTa(moTa);
                List<ChiTietComboModel> chiTietList = new ArrayList<>();
                for (int i = 0; i < bapNuocIds.size(); i++) {
                    ChiTietComboModel ctModel = new ChiTietComboModel();
                    ctModel.setMaBapNuoc(bapNuocIds.get(i));
                    ctModel.setSoLuong(soLuongs.get(i));
                    chiTietList.add(ctModel);
                }
                comboModel.setChiTietCombos(chiTietList);
                editItem = comboModel;
            }
            model.addAttribute("editItem", editItem);
            model.addAttribute("editLoai", loai);
            model.addAttribute("showEditModal", true);
            model.addAttribute("error", "Lỗi khi cập nhật: " + e.getMessage());
            return showFoodComboManager(ma, loai, model, request);
        }
    }

    @Transactional
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String deleteItem(
            @RequestParam("ma") String ma,
            @RequestParam("loai") String loai,
            Model model,
            HttpServletRequest request) {
        try {
            Session dbSession = sessionFactory.getCurrentSession();

            if ("Bắp Nước".equals(loai)) {
                Query checkQuery = dbSession.createQuery(
                    "SELECT COUNT(*) FROM ChiTietDonHangBapNuocEntity WHERE maBapNuoc = :maBapNuoc"
                );
                checkQuery.setParameter("maBapNuoc", ma);
                Long count = (Long) checkQuery.uniqueResult();

                if (count > 0) {
                    model.addAttribute("error", "Không thể xóa bắp nước với mã " + ma + " vì nó đã được sử dụng trong đơn hàng.");
                    return showFoodComboManager(null, null, model, request);
                }

                BapNuocEntity bapNuoc = (BapNuocEntity) dbSession.get(BapNuocEntity.class, ma);
                if (bapNuoc != null) {
                    dbSession.delete(bapNuoc);
                    logger.info("Deleted BapNuocEntity: " + ma);
                } else {
                    model.addAttribute("error", "Không tìm thấy bắp nước với mã " + ma);
                }
            } else if ("Combo".equals(loai)) {
                Query checkQuery = dbSession.createQuery(
                    "SELECT COUNT(*) FROM ChiTietDonHangComboEntity WHERE maCombo = :maCombo"
                );
                checkQuery.setParameter("maCombo", ma);
                Long count = (Long) checkQuery.uniqueResult();

                if (count > 0) {
                    model.addAttribute("error", "Không thể xóa combo với mã " + ma + " vì nó đã được sử dụng trong đơn hàng.");
                    return showFoodComboManager(null, null, model, request);
                }

                ComboEntity combo = (ComboEntity) dbSession.get(ComboEntity.class, ma);
                if (combo != null) {
                    combo.getChiTietCombos().clear();
                    dbSession.flush();
                    dbSession.delete(combo);
                    logger.info("Deleted ComboEntity: " + ma + " with associated ChiTietComboEntity records");
                } else {
                    model.addAttribute("error", "Không tìm thấy combo với mã " + ma);
                }
            }

            model.addAttribute("success", "Xóa " + loai + " thành công!");
            return "redirect:/admin/food-combo";

        } catch (Exception e) {
            logger.severe("Error deleting item: " + e.getMessage());
            model.addAttribute("error", "Lỗi khi xóa: " + e.getMessage());
            return showFoodComboManager(null, null, model, request);
        }
    }
}