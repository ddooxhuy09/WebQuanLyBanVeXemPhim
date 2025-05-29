package movie.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import movie.entity.GheEntity;
import movie.entity.LoaiGheEntity;
import movie.entity.PhongChieuEntity;
import movie.entity.RapChieuEntity;
import movie.model.GheModel; // Keep for potential internal use, but not for @ResponseBody
import movie.model.LoaiGheModel;
import movie.model.PhongChieuModel; // Use the corrected version
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional; // Keep for service layer if used, but manage session manually here for clarity
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminTheaterRoomController {

    private static final Logger logger = Logger.getLogger(AdminTheaterRoomController.class.getName());
    private final ObjectMapper objectMapper = new ObjectMapper(); // For JSON parsing

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private ServletContext context;

    // --- Helper Methods ---

    private String handleImageUpload(MultipartFile imageFile, String oldImagePath) throws Exception {
        // (Keep existing image upload logic as is)
        logger.info("Handling image upload for theater room. Old path: " + (oldImagePath != null ? oldImagePath : "null"));
        if (imageFile == null || imageFile.isEmpty()) {
            if (oldImagePath != null && !oldImagePath.isEmpty()) {
                 logger.info("No new image uploaded, keeping old image: " + oldImagePath);
                 return oldImagePath;
            }
             logger.warning("No image file provided and no existing image path.");
             return null; 
        }

        String contentType = imageFile.getContentType();
        if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)) {
            throw new IllegalArgumentException("Hình ảnh phải là file jpg hoặc png.");
        }
        if (imageFile.getSize() > 5 * 1024 * 1024) { // 5MB limit
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

        if (oldImagePath != null && !oldImagePath.isEmpty() && !oldImagePath.equals("resources/images/" + fileName)) {
            File oldFile = new File(context.getRealPath("/") + oldImagePath);
             if (oldFile.exists()) {
                logger.info("Deleting old image: " + oldFile.getAbsolutePath());
                if (!oldFile.delete()) {
                    logger.warning("Failed to delete old image: " + oldFile.getAbsolutePath());
                }
            }
        }
        return "resources/images/" + fileName;
    }

    // Helper to load common data needed for the view
    private void loadCommonViewData(Model model, Session dbSession) {
        // Lấy danh sách phòng chiếu (using corrected PhongChieuModel)
        Query query = dbSession.createQuery("FROM PhongChieuEntity");
        List<PhongChieuEntity> phongChieuEntities = query.list();
        List<PhongChieuModel> roomList = new ArrayList<>();
        for (PhongChieuEntity entity : phongChieuEntities) {
            roomList.add(new PhongChieuModel(entity)); // Assumes PhongChieuModel is corrected
        }
        model.addAttribute("roomList", roomList);

        // Lấy danh sách rạp chiếu cho bộ lọc
        Query rapQuery = dbSession.createQuery("FROM RapChieuEntity");
        List<RapChieuEntity> rapChieuEntities = rapQuery.list();
        model.addAttribute("rapChieuList", rapChieuEntities);

        // Lấy danh sách loại ghế (ALWAYS load this for SSR)
        Query seatTypeQuery = dbSession.createQuery("FROM LoaiGheEntity");
        List<LoaiGheEntity> loaiGheEntities = seatTypeQuery.list();
        List<LoaiGheModel> seatTypeList = new ArrayList<>();
        for (LoaiGheEntity entity : loaiGheEntities) {
            seatTypeList.add(new LoaiGheModel(entity));
        }
        model.addAttribute("seatTypeList", seatTypeList);
        // Also add as JSON for JavaScript embedding
        try {
            model.addAttribute("seatTypesJson", objectMapper.writeValueAsString(seatTypeList));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error converting seat types to JSON", e);
            model.addAttribute("seatTypesJson", "[]"); // Provide empty array on error
        }

        // Lấy mã phòng chiếu mới
        Query maxQuery = dbSession.createQuery("SELECT MAX(CAST(SUBSTRING(maPhongChieu, 3, 3) AS integer)) FROM PhongChieuEntity WHERE maPhongChieu LIKE \'PC%\'");
        Integer maxNum = (Integer) maxQuery.uniqueResult();
        String newMaPhongChieu = String.format("PC%03d", (maxNum == null ? 0 : maxNum) + 1);
        model.addAttribute("newMaPhongChieu", newMaPhongChieu);

        // Lấy mã loại ghế mới
        Query maxSeatTypeQuery = dbSession.createQuery("SELECT MAX(CAST(SUBSTRING(maLoaiGhe, 3, 3) AS integer)) FROM LoaiGheEntity WHERE maLoaiGhe LIKE \'LG%\'");
        Integer maxSeatTypeNum = (Integer) maxSeatTypeQuery.uniqueResult();
        String newMaLoaiGhe = String.format("LG%03d", (maxSeatTypeNum == null ? 0 : maxSeatTypeNum) + 1);
        model.addAttribute("newMaLoaiGhe", newMaLoaiGhe);
    }

    // --- Main Controller Methods ---

    @RequestMapping(value = "/theater-rooms", method = RequestMethod.GET)
    public String showTheaterRoomManager(Model model) {
        Session dbSession = null;
        try {
            dbSession = sessionFactory.openSession();
            loadCommonViewData(model, dbSession);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading theater room manager data", e);
            model.addAttribute("error", "Lỗi khi tải dữ liệu quản lý phòng chiếu: " + e.getMessage());
        } finally {
            if (dbSession != null && dbSession.isOpen()) {
                dbSession.close();
            }
        }
        return "admin/theater_room_manager";
    }

    // --- Theater Room CRUD ---

    @RequestMapping(value = "/theater-rooms/add", method = RequestMethod.POST)
    public String addTheaterRoom(
            @RequestParam("maPhongChieu") String maPhongChieu,
            @RequestParam("tenPhongChieu") String tenPhongChieu,
            @RequestParam("sucChua") int sucChua,
            @RequestParam("maRapChieu") String maRapChieu,
            @RequestParam("hinhAnh") MultipartFile hinhAnhFile,
            @RequestParam(value = "seatData", required = false, defaultValue = "[]") String seatDataJson, // Added seatData param
            RedirectAttributes redirectAttributes) {
        Session dbSession = null;
        Transaction tx = null;
        try {
            dbSession = sessionFactory.openSession();
            tx = dbSession.beginTransaction();

            // Handle image upload first
            String urlHinhAnh = handleImageUpload(hinhAnhFile, null);

            PhongChieuEntity phongChieu = new PhongChieuEntity();
            phongChieu.setMaPhongChieu(maPhongChieu);
            phongChieu.setTenPhongChieu(tenPhongChieu);
            phongChieu.setSucChua(sucChua);
            phongChieu.setUrlHinhAnh(urlHinhAnh);

            RapChieuEntity rapChieu = (RapChieuEntity) dbSession.get(RapChieuEntity.class, maRapChieu);
            if (rapChieu == null) {
                redirectAttributes.addFlashAttribute("error", "Rạp chiếu không tồn tại với mã: " + maRapChieu);
                return "redirect:/admin/theater-rooms";
            }
            phongChieu.setRapChieu(rapChieu);

            dbSession.save(phongChieu);
            logger.info("Saved new theater room entity: " + maPhongChieu);

            // Save seats from JSON data
            saveSeatsFromJson(seatDataJson, phongChieu, dbSession);

            tx.commit();
            logger.info("Added new theater room and seats: " + maPhongChieu);
            redirectAttributes.addFlashAttribute("success", "Thêm phòng chiếu và sơ đồ ghế thành công!");
            return "redirect:/admin/theater-rooms";
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.log(Level.SEVERE, "Error adding theater room", e);
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm phòng chiếu: " + e.getMessage());
            return "redirect:/admin/theater-rooms";
        } finally {
            if (dbSession != null && dbSession.isOpen()) {
                dbSession.close();
            }
        }
    }

    @RequestMapping(value = "/theater-rooms/update", method = RequestMethod.POST)
    public String updateTheaterRoom(
            @RequestParam("maPhongChieu") String maPhongChieu,
            @RequestParam("tenPhongChieu") String tenPhongChieu,
            @RequestParam("sucChua") int sucChua,
            @RequestParam("maRapChieu") String maRapChieu,
            @RequestParam("hinhAnh") MultipartFile hinhAnhFile,
            @RequestParam(value = "seatData", required = false, defaultValue = "[]") String seatDataJson, // Added seatData param
            RedirectAttributes redirectAttributes) {
        Session dbSession = null;
        Transaction tx = null;
        try {
            dbSession = sessionFactory.openSession();
            tx = dbSession.beginTransaction();

            PhongChieuEntity phongChieu = (PhongChieuEntity) dbSession.get(PhongChieuEntity.class, maPhongChieu);
            if (phongChieu == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy phòng chiếu với mã " + maPhongChieu);
                return "redirect:/admin/theater-rooms";
            }

            // Handle image upload, passing the existing path
            String urlHinhAnh = handleImageUpload(hinhAnhFile, phongChieu.getUrlHinhAnh());

            phongChieu.setTenPhongChieu(tenPhongChieu);
            phongChieu.setSucChua(sucChua);
            phongChieu.setUrlHinhAnh(urlHinhAnh);

            RapChieuEntity rapChieu = (RapChieuEntity) dbSession.get(RapChieuEntity.class, maRapChieu);
            if (rapChieu == null) {
                redirectAttributes.addFlashAttribute("error", "Rạp chiếu không tồn tại với mã: " + maRapChieu);
                return "redirect:/admin/theater-rooms";
            }
            phongChieu.setRapChieu(rapChieu);

            dbSession.update(phongChieu);
            logger.info("Updated theater room entity: " + maPhongChieu);

            // Update seats: Delete existing and save new ones from JSON
            // (More robust would be to update existing, but delete/insert is simpler for now)
            Query deleteQuery = dbSession.createQuery("DELETE FROM GheEntity WHERE phongChieu.maPhongChieu = :maPhongChieu");
            deleteQuery.setParameter("maPhongChieu", maPhongChieu);
            int deletedCount = deleteQuery.executeUpdate();
            logger.info("Deleted " + deletedCount + " existing seats for room " + maPhongChieu);

            saveSeatsFromJson(seatDataJson, phongChieu, dbSession);

            tx.commit();
            logger.info("Updated theater room and seats: " + maPhongChieu);
            redirectAttributes.addFlashAttribute("success", "Cập nhật phòng chiếu và sơ đồ ghế thành công!");
            return "redirect:/admin/theater-rooms";
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.log(Level.SEVERE, "Error updating theater room", e);
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật phòng chiếu: " + e.getMessage());
            return "redirect:/admin/theater-rooms";
        } finally {
            if (dbSession != null && dbSession.isOpen()) {
                dbSession.close();
            }
        }
    }

    @RequestMapping(value = "/theater-rooms/delete/{maPhongChieu}", method = RequestMethod.POST)
    public String deleteTheaterRoom(@PathVariable("maPhongChieu") String maPhongChieu, RedirectAttributes redirectAttributes) {
        Session dbSession = null;
        Transaction tx = null;
        try {
            dbSession = sessionFactory.openSession();
            tx = dbSession.beginTransaction();

            PhongChieuEntity phongChieu = (PhongChieuEntity) dbSession.get(PhongChieuEntity.class, maPhongChieu);
            if (phongChieu != null) {
                // Check constraints (LichChieuEntity)
                Query checkLichChieuQuery = dbSession.createQuery("SELECT COUNT(*) FROM LichChieuEntity WHERE phongChieu.maPhongChieu = :maPhongChieu");
                checkLichChieuQuery.setParameter("maPhongChieu", maPhongChieu);
                Long lichChieuCount = (Long) checkLichChieuQuery.uniqueResult();

                if (lichChieuCount > 0) {
                    String message = "Không thể xóa phòng chiếu vì có lịch chiếu (" + lichChieuCount + ") liên quan.";
                    logger.warning("Deletion failed for room " + maPhongChieu + ": " + message);
                    redirectAttributes.addFlashAttribute("error", message);
                    return "redirect:/admin/theater-rooms";
                }

                // Delete related seats first
                Query deleteGheQuery = dbSession.createQuery("DELETE FROM GheEntity WHERE phongChieu.maPhongChieu = :maPhongChieu");
                deleteGheQuery.setParameter("maPhongChieu", maPhongChieu);
                int deletedGheCount = deleteGheQuery.executeUpdate();
                logger.info("Deleted " + deletedGheCount + " seats associated with room " + maPhongChieu);

                // Delete image file
                String imagePath = phongChieu.getUrlHinhAnh();
                if (imagePath != null && !imagePath.isEmpty()) {
                    File imageFile = new File(context.getRealPath("/") + imagePath);
                    if (imageFile.exists()) {
                        logger.info("Deleting image file for room " + maPhongChieu + ": " + imageFile.getAbsolutePath());
                        if (!imageFile.delete()) {
                            logger.warning("Failed to delete image file: " + imageFile.getAbsolutePath());
                        }
                    }
                }

                // Delete the room itself
                dbSession.delete(phongChieu);
                tx.commit();
                logger.info("Deleted theater room: " + maPhongChieu);
                redirectAttributes.addFlashAttribute("success", "Xóa phòng chiếu thành công!");
            } else {
                logger.warning("Attempted to delete non-existent room: " + maPhongChieu);
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy phòng chiếu với mã " + maPhongChieu);
            }
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.log(Level.SEVERE, "Error deleting theater room", e);
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa phòng chiếu: " + e.getMessage());
        } finally {
            if (dbSession != null && dbSession.isOpen()) {
                dbSession.close();
            }
        }
        return "redirect:/admin/theater-rooms";
    }

    // --- Seat Map SSR Endpoints ---

    // Endpoint to show the page with the 'View Seat Map' modal open
    @RequestMapping(value = "/theater-rooms/view-map/{maPhongChieu}", method = RequestMethod.GET)
    public String viewSeatMap(@PathVariable("maPhongChieu") String maPhongChieu, Model model) {
        Session dbSession = null;
        try {
            dbSession = sessionFactory.openSession();
            loadCommonViewData(model, dbSession); // Load base data

            // Load specific seat map data
            Query seatQuery = dbSession.createQuery("FROM GheEntity WHERE phongChieu.maPhongChieu = :maPhongChieu ORDER BY tenHangAdmin, soGheAdmin");
            seatQuery.setParameter("maPhongChieu", maPhongChieu);
            List<GheEntity> seatEntities = seatQuery.list();
            List<Map<String, Object>> seatMapData = new ArrayList<>();
            for (GheEntity seat : seatEntities) {
                Map<String, Object> seatInfo = new HashMap<>();
                seatInfo.put("maGhe", seat.getMaGhe());
                seatInfo.put("tenHangAdmin", seat.getTenHangAdmin());
                seatInfo.put("soGheAdmin", seat.getSoGheAdmin());
                seatInfo.put("tenHang", seat.getTenHang());
                seatInfo.put("soGhe", seat.getSoGhe());
                seatInfo.put("maLoaiGhe", seat.getLoaiGhe() != null ? seat.getLoaiGhe().getMaLoaiGhe() : null);
                seatInfo.put("mauGhe", seat.getLoaiGhe() != null ? seat.getLoaiGhe().getMauGhe() : "#f0f0f0"); // Include color
                seatMapData.add(seatInfo);
            }
            model.addAttribute("seatMapJson", objectMapper.writeValueAsString(seatMapData));
            model.addAttribute("openViewMapModal", maPhongChieu); // Flag to open modal

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading seat map for view", e);
            model.addAttribute("error", "Lỗi khi tải sơ đồ ghế: " + e.getMessage());
            model.addAttribute("seatMapJson", "[]");
        } finally {
            if (dbSession != null && dbSession.isOpen()) {
                dbSession.close();
            }
        }
        return "admin/theater_room_manager"; // Return the main view
    }

    // Endpoint to show the page with the 'Edit Seat Map' modal open
    @RequestMapping(value = "/theater-rooms/edit-map/{maPhongChieu}", method = RequestMethod.GET)
    public String editSeatMap(@PathVariable("maPhongChieu") String maPhongChieu, Model model) {
        Session dbSession = null;
        try {
            dbSession = sessionFactory.openSession();
            loadCommonViewData(model, dbSession); // Load base data

            // Load specific room data for capacity
            PhongChieuEntity room = (PhongChieuEntity) dbSession.get(PhongChieuEntity.class, maPhongChieu);
            if (room == null) {
                 model.addAttribute("error", "Không tìm thấy phòng chiếu: " + maPhongChieu);
                 return "admin/theater_room_manager"; // Or redirect
            }
            model.addAttribute("currentRoomCapacity", room.getSucChua());

            // Load specific seat map data
            Query seatQuery = dbSession.createQuery("FROM GheEntity WHERE phongChieu.maPhongChieu = :maPhongChieu ORDER BY tenHangAdmin, soGheAdmin");
            seatQuery.setParameter("maPhongChieu", maPhongChieu);
            List<GheEntity> seatEntities = seatQuery.list();
            List<Map<String, Object>> seatMapData = new ArrayList<>();
             for (GheEntity seat : seatEntities) {
                Map<String, Object> seatInfo = new HashMap<>();
                // Include necessary fields for seatGrid.js initialization
                seatInfo.put("row", seat.getTenHangAdmin() != null ? seat.getTenHangAdmin().charAt(0) - 'A' : -1); // Convert 'A'->0, 'B'->1 etc.
                seatInfo.put("col", seat.getSoGheAdmin() != null ? Integer.parseInt(seat.getSoGheAdmin()) - 1 : -1); // Convert '1'->0, '2'->1 etc.
                seatInfo.put("type", seat.getLoaiGhe() != null ? seat.getLoaiGhe().getMaLoaiGhe() : null);
                seatInfo.put("color", seat.getLoaiGhe() != null ? seat.getLoaiGhe().getMauGhe() : "#f0f0f0");
                seatMapData.add(seatInfo);
            }
            model.addAttribute("seatMapJson", objectMapper.writeValueAsString(seatMapData));
            model.addAttribute("openEditMapModal", maPhongChieu); // Flag to open modal

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading seat map for edit", e);
            model.addAttribute("error", "Lỗi khi tải sơ đồ ghế để chỉnh sửa: " + e.getMessage());
            model.addAttribute("seatMapJson", "[]");
        } finally {
            if (dbSession != null && dbSession.isOpen()) {
                dbSession.close();
            }
        }
        return "admin/theater_room_manager"; // Return the main view
    }

    // Helper method to save seats from JSON
    private void saveSeatsFromJson(String seatDataJson, PhongChieuEntity phongChieu, Session dbSession) throws Exception {
        if (seatDataJson == null || seatDataJson.isEmpty() || seatDataJson.equals("[]")) {
            logger.info("No seat data provided for room: " + phongChieu.getMaPhongChieu());
            return; // No seats to save
        }

        List<Map<String, Object>> seatDataList = objectMapper.readValue(seatDataJson, new TypeReference<List<Map<String, Object>>>() {});
        logger.info("Saving " + seatDataList.size() + " seats for room " + phongChieu.getMaPhongChieu());

        int savedCount = 0;
        for (Map<String, Object> seatData : seatDataList) {
            try {
                String tenHangAdmin = (String) seatData.get("tenHangAdmin");
                String soGheAdminStr = String.valueOf(seatData.get("soGheAdmin")); // Handle potential number
                String maLoaiGhe = (String) seatData.get("type");

                if (tenHangAdmin == null || soGheAdminStr == null || maLoaiGhe == null) {
                    logger.warning("Skipping seat due to missing data: " + seatData);
                    continue;
                }

                LoaiGheEntity loaiGhe = (LoaiGheEntity) dbSession.get(LoaiGheEntity.class, maLoaiGhe);
                if (loaiGhe == null) {
                    logger.warning("Skipping seat because LoaiGheEntity not found: " + maLoaiGhe);
                    continue;
                }

                GheEntity ghe = new GheEntity();
                // Generate MaGhe (Example: PC001-A01)
                String maGhe = String.format("%s-%s%02d", phongChieu.getMaPhongChieu(), tenHangAdmin, Integer.parseInt(soGheAdminStr));
                ghe.setMaGhe(maGhe);
                ghe.setPhongChieu(phongChieu);
                ghe.setLoaiGhe(loaiGhe);
                ghe.setTenHangAdmin(tenHangAdmin);
                ghe.setSoGheAdmin(soGheAdminStr);
                // Set TenHang and SoGhe based on LoaiGhe soCho if needed, or keep simple for now
                ghe.setTenHang(tenHangAdmin); // Assuming 1 seat = 1 display seat
                ghe.setSoGhe(soGheAdminStr);
				/* ghe.setTrangThai(true); */ // Default to available

                dbSession.save(ghe);
                savedCount++;
            } catch (Exception ex) {
                 logger.log(Level.SEVERE, "Error saving individual seat: " + seatData, ex);
                 // Decide whether to continue or rethrow
            }
        }
         logger.info("Successfully saved " + savedCount + " seats out of " + seatDataList.size() + " for room " + phongChieu.getMaPhongChieu());
    }

    // --- Seat Type CRUD (SSR Refactored) ---

    @RequestMapping(value = "/theater-rooms/seat-types/add", method = RequestMethod.POST)
    public String addSeatType(
            @RequestParam("maLoaiGhe") String maLoaiGhe,
            @RequestParam("tenLoaiGhe") String tenLoaiGhe,
            @RequestParam("heSoGia") double heSoGia,
            @RequestParam("mauGhe") String mauGhe,
            @RequestParam("soCho") int soCho,
            RedirectAttributes redirectAttributes) {
        Session dbSession = null;
        Transaction tx = null;
        try {
            dbSession = sessionFactory.openSession();
            tx = dbSession.beginTransaction();

            if (tenLoaiGhe == null || tenLoaiGhe.trim().isEmpty()) {
                 redirectAttributes.addFlashAttribute("error", "Tên loại ghế không được để trống.");
                 return "redirect:/admin/theater-rooms";
            }

            LoaiGheEntity seatType = new LoaiGheEntity();
            seatType.setMaLoaiGhe(maLoaiGhe);
            seatType.setTenLoaiGhe(tenLoaiGhe);
            seatType.setHeSoGia(heSoGia);
            seatType.setMauGhe(mauGhe);
            seatType.setSoCho(soCho);

            dbSession.save(seatType);
            tx.commit();
            logger.info("Added new seat type: " + maLoaiGhe);
            redirectAttributes.addFlashAttribute("success", "Thêm loại ghế thành công!");
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.log(Level.SEVERE, "Error adding seat type", e);
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm loại ghế: " + e.getMessage());
        } finally {
            if (dbSession != null && dbSession.isOpen()) {
                dbSession.close();
            }
        }
        return "redirect:/admin/theater-rooms";
    }

    @RequestMapping(value = "/theater-rooms/seat-types/update", method = RequestMethod.POST)
    public String updateSeatType(
            @RequestParam("maLoaiGhe") String maLoaiGhe,
            @RequestParam("tenLoaiGhe") String tenLoaiGhe,
            @RequestParam("heSoGia") double heSoGia,
            @RequestParam("mauGhe") String mauGhe,
            @RequestParam("soCho") int soCho,
            RedirectAttributes redirectAttributes) {
        Session dbSession = null;
        Transaction tx = null;
        try {
            dbSession = sessionFactory.openSession();
            tx = dbSession.beginTransaction();

            LoaiGheEntity seatType = (LoaiGheEntity) dbSession.get(LoaiGheEntity.class, maLoaiGhe);
            if (seatType == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy loại ghế: " + maLoaiGhe);
                return "redirect:/admin/theater-rooms";
            }
             if (tenLoaiGhe == null || tenLoaiGhe.trim().isEmpty()) {
                 redirectAttributes.addFlashAttribute("error", "Tên loại ghế không được để trống.");
                 return "redirect:/admin/theater-rooms";
            }

            seatType.setTenLoaiGhe(tenLoaiGhe);
            seatType.setHeSoGia(heSoGia);
            seatType.setMauGhe(mauGhe);
            seatType.setSoCho(soCho);

            dbSession.update(seatType);
            tx.commit();
            logger.info("Updated seat type: " + maLoaiGhe);
            redirectAttributes.addFlashAttribute("success", "Cập nhật loại ghế thành công!");
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.log(Level.SEVERE, "Error updating seat type", e);
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật loại ghế: " + e.getMessage());
        } finally {
            if (dbSession != null && dbSession.isOpen()) {
                dbSession.close();
            }
        }
        return "redirect:/admin/theater-rooms";
    }

    @RequestMapping(value = "/theater-rooms/seat-types/delete/{maLoaiGhe}", method = RequestMethod.POST)
    public String deleteSeatType(@PathVariable("maLoaiGhe") String maLoaiGhe, RedirectAttributes redirectAttributes) {
        Session dbSession = null;
        Transaction tx = null;
        try {
            dbSession = sessionFactory.openSession();
            tx = dbSession.beginTransaction();

            LoaiGheEntity seatType = (LoaiGheEntity) dbSession.get(LoaiGheEntity.class, maLoaiGhe);
            if (seatType != null) {
                // Check constraints (GheEntity)
                Query checkGheQuery = dbSession.createQuery("SELECT COUNT(*) FROM GheEntity WHERE loaiGhe.maLoaiGhe = :maLoaiGhe");
                checkGheQuery.setParameter("maLoaiGhe", maLoaiGhe);
                Long gheCount = (Long) checkGheQuery.uniqueResult();

                if (gheCount > 0) {
                    String message = "Không thể xóa loại ghế vì đang được sử dụng bởi " + gheCount + " ghế.";
                    logger.warning("Deletion failed for seat type " + maLoaiGhe + ": " + message);
                    redirectAttributes.addFlashAttribute("error", message);
                    return "redirect:/admin/theater-rooms";
                }

                dbSession.delete(seatType);
                tx.commit();
                logger.info("Deleted seat type: " + maLoaiGhe);
                redirectAttributes.addFlashAttribute("success", "Xóa loại ghế thành công!");
            } else {
                logger.warning("Attempted to delete non-existent seat type: " + maLoaiGhe);
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy loại ghế: " + maLoaiGhe);
            }
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.log(Level.SEVERE, "Error deleting seat type", e);
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa loại ghế: " + e.getMessage());
        } finally {
            if (dbSession != null && dbSession.isOpen()) {
                dbSession.close();
            }
        }
        return "redirect:/admin/theater-rooms";
    }

    // REMOVED @ResponseBody endpoints for seats and seat types
    // getSeatsByRoom, getSeatTypesList etc. are no longer needed as separate API endpoints
    // saveSeatMap is handled within add/update room methods or potentially a dedicated POST endpoint if needed

}

