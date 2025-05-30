package movie.controller;

import movie.entity.GheEntity;
import movie.entity.LoaiGheEntity;
import movie.entity.PhongChieuEntity;
import movie.entity.RapChieuEntity;
import movie.model.GheModel;
import movie.model.LoaiGheModel;
import movie.model.PhongChieuModel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminTheaterRoomController {

    @Autowired
    private SessionFactory sessionFactory;

    @RequestMapping(value = "/theater-rooms", method = RequestMethod.GET)
    public String showTheaterRoomManager(Model model) {
        Session dbSession = sessionFactory.openSession();
        try {
            // Lấy danh sách phòng chiếu
            Query query = dbSession.createQuery("FROM PhongChieuEntity");
            List phongChieuEntities = query.list();
            List<PhongChieuModel> roomList = new ArrayList<>();
            for (Object obj : phongChieuEntities) {
                PhongChieuEntity entity = (PhongChieuEntity) obj;
                roomList.add(new PhongChieuModel(entity));
            }
            model.addAttribute("roomList", roomList);

            // Lấy danh sách rạp chiếu cho bộ lọc
            Query rapQuery = dbSession.createQuery("FROM RapChieuEntity");
            List rapChieuEntities = rapQuery.list();
            List<String> rapChieuList = new ArrayList<>();
            for (Object obj : rapChieuEntities) {
                RapChieuEntity entity = (RapChieuEntity) obj;
                rapChieuList.add(entity.getMaRapChieu());
            }
            model.addAttribute("rapChieuList", rapChieuList);

            // Lấy danh sách loại ghế
            Query seatTypeQuery = dbSession.createQuery("FROM LoaiGheEntity");
            List loaiGheEntities = seatTypeQuery.list();
            List<LoaiGheModel> seatTypeList = new ArrayList<>();
            for (Object obj : loaiGheEntities) {
                LoaiGheEntity entity = (LoaiGheEntity) obj;
                seatTypeList.add(new LoaiGheModel(entity));
            }
            model.addAttribute("seatTypeList", seatTypeList);

            // Lấy mã phòng chiếu mới
            Query maxQuery = dbSession.createQuery("FROM PhongChieuEntity ORDER BY maPhongChieu DESC");
            maxQuery.setMaxResults(1);
            PhongChieuEntity latestPhong = (PhongChieuEntity) maxQuery.uniqueResult();
            String newMaPhongChieu = latestPhong == null ? "PC001" : String.format("PC%03d",
                    Integer.parseInt(latestPhong.getMaPhongChieu().substring(2)) + 1);
            model.addAttribute("newMaPhongChieu", newMaPhongChieu);

            // Lấy mã loại ghế mới
            Query maxSeatTypeQuery = dbSession.createQuery("FROM LoaiGheEntity ORDER BY maLoaiGhe DESC");
            maxSeatTypeQuery.setMaxResults(1);
            LoaiGheEntity latestLoaiGhe = (LoaiGheEntity) maxSeatTypeQuery.uniqueResult();
            String newMaLoaiGhe = latestLoaiGhe == null ? "LG001" : String.format("LG%03d",
                    Integer.parseInt(latestLoaiGhe.getMaLoaiGhe().substring(2)) + 1);
            model.addAttribute("newMaLoaiGhe", newMaLoaiGhe);

            model.addAttribute("isEdit", false);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi lấy danh sách phòng chiếu hoặc loại ghế: " + e.getMessage());
        } finally {
            dbSession.close();
        }
        return "admin/theater_room_manager";
    }

    @Transactional
    @RequestMapping(value = "/theater-rooms/add", method = RequestMethod.POST)
    public String addTheaterRoom(
            @RequestParam("maPhongChieu") String maPhongChieu,
            @RequestParam("tenPhongChieu") String tenPhongChieu,
            @RequestParam("sucChua") int sucChua,
            @RequestParam("maRapChieu") String maRapChieu,
            @RequestParam("urlHinhAnh") String urlHinhAnh,
            Model model) {
        try {
            Session dbSession = sessionFactory.getCurrentSession();
            PhongChieuEntity phongChieu = new PhongChieuEntity();
            phongChieu.setMaPhongChieu(maPhongChieu);
            phongChieu.setTenPhongChieu(tenPhongChieu);
            phongChieu.setSucChua(sucChua);
            phongChieu.setUrlHinhAnh(urlHinhAnh);

            RapChieuEntity rapChieu = (RapChieuEntity) dbSession.get(RapChieuEntity.class, maRapChieu);
            if (rapChieu == null) {
                model.addAttribute("error", "Rạp chiếu không tồn tại");
                return "admin/theater_room_manager";
            }
            phongChieu.setRapChieu(rapChieu);

            dbSession.save(phongChieu);
            return "redirect:/admin/theater-rooms";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi thêm phòng chiếu: " + e.getMessage());
            return "admin/theater_room_manager";
        }
    }

    @Transactional
    @RequestMapping(value = "/theater-rooms/update", method = RequestMethod.POST)
    public String updateTheaterRoom(
            @RequestParam("maPhongChieu") String maPhongChieu,
            @RequestParam("tenPhongChieu") String tenPhongChieu,
            @RequestParam("sucChua") int sucChua,
            @RequestParam("maRapChieu") String maRapChieu,
            @RequestParam("urlHinhAnh") String urlHinhAnh,
            Model model) {
        try {
            Session dbSession = sessionFactory.getCurrentSession();
            PhongChieuEntity phongChieu = (PhongChieuEntity) dbSession.get(PhongChieuEntity.class, maPhongChieu);
            if (phongChieu == null) {
                model.addAttribute("error", "Không tìm thấy phòng chiếu với mã " + maPhongChieu);
                return "redirect:/admin/theater-rooms";
            }

            phongChieu.setTenPhongChieu(tenPhongChieu);
            phongChieu.setSucChua(sucChua);
            phongChieu.setUrlHinhAnh(urlHinhAnh);

            RapChieuEntity rapChieu = (RapChieuEntity) dbSession.get(RapChieuEntity.class, maRapChieu);
            if (rapChieu == null) {
                model.addAttribute("error", "Rạp chiếu không tồn tại");
                return "admin/theater_room_manager";
            }
            phongChieu.setRapChieu(rapChieu);

            dbSession.update(phongChieu);
            return "redirect:/admin/theater-rooms";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi cập nhật phòng chiếu: " + e.getMessage());
            return "admin/theater_room_manager";
        }
    }

    @Transactional
    @RequestMapping(value = "/theater-rooms/delete/{maPhongChieu}", method = RequestMethod.GET)
    public String deleteTheaterRoom(@PathVariable("maPhongChieu") String maPhongChieu, Model model) {
        try {
            Session dbSession = sessionFactory.getCurrentSession();
            PhongChieuEntity phongChieu = (PhongChieuEntity) dbSession.get(PhongChieuEntity.class, maPhongChieu);
            if (phongChieu != null) {
                Query query = dbSession.createQuery("FROM GheEntity WHERE phongChieu.maPhongChieu = :maPhongChieu");
                query.setParameter("maPhongChieu", maPhongChieu);
                List gheEntities = query.list();
                if (!gheEntities.isEmpty()) {
                    model.addAttribute("error", "Không thể xóa phòng chiếu vì có ghế liên quan.");
                    return "redirect:/admin/theater-rooms";
                }
                dbSession.delete(phongChieu);
            } else {
                model.addAttribute("error", "Không tìm thấy phòng chiếu với mã " + maPhongChieu);
            }
            return "redirect:/admin/theater-rooms";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi xóa phòng chiếu: " + e.getMessage());
            return "redirect:/admin/theater-rooms";
        }
    }

    @RequestMapping(value = "/theater-rooms/seats/{maPhongChieu}", method = RequestMethod.GET)
    @ResponseBody
    public List<GheModel> getSeatsByRoom(@PathVariable("maPhongChieu") String maPhongChieu) {
        Session dbSession = sessionFactory.openSession();
        try {
            Query query = dbSession.createQuery("FROM GheEntity WHERE phongChieu.maPhongChieu = :maPhongChieu");
            query.setParameter("maPhongChieu", maPhongChieu);
            List gheEntities = query.list();
            List<GheModel> seatList = new ArrayList<>();
            for (Object obj : gheEntities) {
                GheEntity entity = (GheEntity) obj;
                seatList.add(new GheModel(entity));
            }
            System.out.println("Seats found for maPhongChieu " + maPhongChieu + ": " + seatList.size());
            return seatList;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            dbSession.close();
        }
    }

    @Transactional
    @RequestMapping(value = "/theater-rooms/seats/save", method = RequestMethod.POST)
    public String saveSeatMap(@RequestParam("maPhongChieu") String maPhongChieu,
                             @RequestBody List<Map<String, Object>> seatData,
                             Model model) {
        try {
            Session dbSession = sessionFactory.getCurrentSession();
            
            // Lấy thông tin phòng chiếu
            PhongChieuEntity phongChieu = (PhongChieuEntity) dbSession.get(PhongChieuEntity.class, maPhongChieu);
            if (phongChieu == null) {
                model.addAttribute("error", "Không tìm thấy phòng chiếu với mã " + maPhongChieu);
                return "redirect:/admin/theater-rooms";
            }
            
            // Tính tổng sức chứa từ các ghế
            int totalCapacity = 0;
            Map<String, LoaiGheEntity> loaiGheMap = new HashMap<>();
            
            for (Map<String, Object> seat : seatData) {
                String maLoaiGhe = (String) seat.get("type");
                if (!loaiGheMap.containsKey(maLoaiGhe)) {
                    LoaiGheEntity loaiGhe = (LoaiGheEntity) dbSession.get(LoaiGheEntity.class, maLoaiGhe);
                    if (loaiGhe != null) {
                        loaiGheMap.put(maLoaiGhe, loaiGhe);
                    }
                }
            }
            
            for (Map<String, Object> seat : seatData) {
                String maLoaiGhe = (String) seat.get("type");
                LoaiGheEntity loaiGhe = loaiGheMap.get(maLoaiGhe);
                if (loaiGhe != null) {
                    totalCapacity += loaiGhe.getSoCho();
                } else {
                    totalCapacity += 1; // Mặc định 1 chỗ nếu không tìm thấy loại ghế
                }
            }
            
            // Kiểm tra sức chứa
            if (totalCapacity > phongChieu.getSucChua()) {
                model.addAttribute("error", "Tổng số chỗ ngồi (" + totalCapacity + ") vượt quá sức chứa của phòng (" + phongChieu.getSucChua() + ")");
                return "redirect:/admin/theater-rooms";
            }

            // Xóa các ghế hiện có của phòng chiếu
            Query deleteQuery = dbSession.createQuery("DELETE FROM GheEntity WHERE phongChieu.maPhongChieu = :maPhongChieu");
            deleteQuery.setParameter("maPhongChieu", maPhongChieu);
            deleteQuery.executeUpdate();

            // Thu thập tất cả các giá trị tenHangAdmin và sắp xếp theo thứ tự bảng chữ cái
            Map<String, String> adminToUserRowMap = new HashMap<>();
            List<String> allTenHangAdmin = new ArrayList<>();
            for (Map<String, Object> seat : seatData) {
                String tenHangAdmin = (String) seat.get("tenHangAdmin");
                if (!allTenHangAdmin.contains(tenHangAdmin)) {
                    allTenHangAdmin.add(tenHangAdmin);
                }
            }
            allTenHangAdmin.sort(String::compareTo); // Sắp xếp theo thứ tự bảng chữ cái: A, B, C, ...

            // Ánh xạ tenHangAdmin thành tenHang, bắt đầu từ "A"
            for (int i = 0; i < allTenHangAdmin.size(); i++) {
                adminToUserRowMap.put(allTenHangAdmin.get(i), String.valueOf((char) ('A' + i))); // M → A, N → B, P → C
            }

            // Tính toán SoGhe cho user
            Map<String, List<Integer>> rowSeats = new HashMap<>();
            for (Map<String, Object> seat : seatData) {
                int row = ((Number) seat.get("row")).intValue();
                int col = ((Number) seat.get("col")).intValue();
                rowSeats.computeIfAbsent(String.valueOf(row), k -> new ArrayList<>()).add(col);
            }

            Map<Integer, Integer> adminToUserSeatMap = new HashMap<>();
            List<Integer> allCols = rowSeats.values().stream().flatMap(List::stream).distinct().sorted().collect(Collectors.toList());
            int minCol = allCols.get(0); // Số cột admin nhỏ nhất
            int colOffset = minCol - 1; // Offset để ánh xạ số ghế user
            for (int col : allCols) {
                adminToUserSeatMap.put(col, col - colOffset); // Ví dụ: 5-4=1, 6-4=2
            }

            // Lưu ghế mới
            for (Map<String, Object> seat : seatData) {
                int row = ((Number) seat.get("row")).intValue();
                int col = ((Number) seat.get("col")).intValue();
                String tenHangAdmin = (String) seat.get("tenHangAdmin");
                String maLoaiGhe = (String) seat.get("type");

                GheEntity ghe = new GheEntity();
                ghe.setMaGhe("G" + System.currentTimeMillis() % 10000 + row + col);
                ghe.setPhongChieu(phongChieu);
                ghe.setSoGheAdmin(String.valueOf(col + 1)); // Số cột admin: 1, 2, ...
                ghe.setTenHangAdmin(tenHangAdmin); // Số hàng admin: M, N, P, ...
                ghe.setSoGhe(String.valueOf(adminToUserSeatMap.get(col))); // Số ghế user: 1, 2, ...
                ghe.setTenHang(adminToUserRowMap.get(tenHangAdmin)); // Tên hàng user: A, B, C, ...

                LoaiGheEntity loaiGhe = loaiGheMap.get(maLoaiGhe);
                if (loaiGhe != null) {
                    ghe.setLoaiGhe(loaiGhe);
                } else {
                    throw new IllegalArgumentException("Loại ghế không tồn tại: " + maLoaiGhe);
                }

                dbSession.save(ghe);
            }

            return "redirect:/admin/theater-rooms";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi lưu sơ đồ ghế: " + e.getMessage());
            return "redirect:/admin/theater-rooms";
        }
    }

    @RequestMapping(value = "/theater-rooms/seat-types/list", method = RequestMethod.GET)
    @ResponseBody
    public List<LoaiGheModel> getSeatTypes() {
        Session dbSession = sessionFactory.openSession();
        try {
            Query query = dbSession.createQuery("FROM LoaiGheEntity");
            List loaiGheEntities = query.list();
            List<LoaiGheModel> seatTypeList = new ArrayList<>();
            for (Object obj : loaiGheEntities) {
                LoaiGheEntity entity = (LoaiGheEntity) obj;
                seatTypeList.add(new LoaiGheModel(entity));
            }
            return seatTypeList;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            dbSession.close();
        }
    }

    @RequestMapping(value = "/theater-rooms/seat-types/{maLoaiGhe}", method = RequestMethod.GET)
    @ResponseBody
    public LoaiGheModel getSeatType(@PathVariable("maLoaiGhe") String maLoaiGhe) {
        Session dbSession = sessionFactory.openSession();
        try {
            LoaiGheEntity entity = (LoaiGheEntity) dbSession.get(LoaiGheEntity.class, maLoaiGhe);
            return entity != null ? new LoaiGheModel(entity) : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            dbSession.close();
        }
    }

    @Transactional
    @RequestMapping(value = "/theater-rooms/seat-types/add", method = RequestMethod.POST)
    public String addSeatType(
            @RequestParam("maLoaiGhe") String maLoaiGhe,
            @RequestParam("tenLoaiGhe") String tenLoaiGhe,
            @RequestParam("heSoGia") double heSoGia,
            @RequestParam("mauGhe") String mauGhe,
            @RequestParam("soCho") int soCho,
            Model model) {
        try {
            Session dbSession = sessionFactory.getCurrentSession();
            LoaiGheEntity loaiGhe = new LoaiGheEntity();
            loaiGhe.setMaLoaiGhe(maLoaiGhe);
            loaiGhe.setTenLoaiGhe(tenLoaiGhe);
            loaiGhe.setHeSoGia(heSoGia);
            loaiGhe.setMauGhe(mauGhe);
            loaiGhe.setSoCho(soCho);
            dbSession.save(loaiGhe);
            return "redirect:/admin/theater-rooms";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi thêm loại ghế: " + e.getMessage());
            return "redirect:/admin/theater-rooms";
        }
    }

    @Transactional
    @RequestMapping(value = "/theater-rooms/seat-types/update", method = RequestMethod.POST)
    public String updateSeatType(
            @RequestParam("maLoaiGhe") String maLoaiGhe,
            @RequestParam("tenLoaiGhe") String tenLoaiGhe,
            @RequestParam("heSoGia") double heSoGia,
            @RequestParam("mauGhe") String mauGhe,
            @RequestParam("soCho") int soCho,
            Model model) {
        try {
            Session dbSession = sessionFactory.getCurrentSession();
            LoaiGheEntity loaiGhe = (LoaiGheEntity) dbSession.get(LoaiGheEntity.class, maLoaiGhe);
            if (loaiGhe == null) {
                model.addAttribute("error", "Không tìm thấy loại ghế với mã " + maLoaiGhe);
                return "redirect:/admin/theater-rooms";
            }
            loaiGhe.setTenLoaiGhe(tenLoaiGhe);
            loaiGhe.setHeSoGia(heSoGia);
            loaiGhe.setMauGhe(mauGhe);
            loaiGhe.setSoCho(soCho);
            dbSession.update(loaiGhe);
            return "redirect:/admin/theater-rooms";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi cập nhật loại ghế: " + e.getMessage());
            return "redirect:/admin/theater-rooms";
        }
    }

    @Transactional
    @RequestMapping(value = "/theater-rooms/seat-types/delete/{maLoaiGhe}", method = RequestMethod.POST)
    public String deleteSeatType(@PathVariable("maLoaiGhe") String maLoaiGhe, Model model) {
        try {
            Session dbSession = sessionFactory.getCurrentSession();
            LoaiGheEntity loaiGhe = (LoaiGheEntity) dbSession.get(LoaiGheEntity.class, maLoaiGhe);
            if (loaiGhe != null) {
                Query query = dbSession.createQuery("FROM GheEntity WHERE maLoaiGhe = :maLoaiGhe");
                query.setParameter("maLoaiGhe", maLoaiGhe);
                List gheEntities = query.list();
                if (!gheEntities.isEmpty()) {
                    model.addAttribute("error", "Không thể xóa loại ghế vì có ghế đang sử dụng loại này.");
                    return "redirect:/admin/theater-rooms";
                }
                dbSession.delete(loaiGhe);
            } else {
                model.addAttribute("error", "Không tìm thấy loại ghế với mã " + maLoaiGhe);
            }
            return "redirect:/admin/theater-rooms";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi xóa loại ghế: " + e.getMessage());
            return "redirect:/admin/theater-rooms";
        }
    }
}