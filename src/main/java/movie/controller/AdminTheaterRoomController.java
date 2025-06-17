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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;

@Controller
@RequestMapping("/admin")
public class AdminTheaterRoomController {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private ServletContext context;

	private String handleImageUpload(MultipartFile file, String oldFileName) throws Exception {
		if (file == null || file.isEmpty()) {
			return oldFileName;
		}
		String contentType = file.getContentType();
		if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)) {
			throw new IllegalArgumentException("Hình ảnh phải là file JPG hoặc PNG!");
		}
		if (file.getSize() > 5 * 1024 * 1024) {
			throw new IllegalArgumentException("Kích thước file không được vượt quá 5MB!");
		}

		String dirPath = context.getRealPath("/resources/images/");
		File dir = new File(dirPath);
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				throw new RuntimeException("Không thể tạo thư mục: " + dir.getAbsolutePath());
			}
		}
		String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
		String filePath = dirPath + File.separator + fileName;
		File destination = new File(filePath);
		if (!destination.getParentFile().canWrite()) {
			throw new RuntimeException("Không có quyền ghi vào thư mục: " + dir.getAbsolutePath());
		}
		file.transferTo(destination);
		return fileName;
	}
	
	private boolean isRoomHasBookedSeats(Session dbSession, String maPhongChieu) {
	    String hql = "FROM VeEntity WHERE ghe.phongChieu.maPhongChieu = :maPhongChieu";
	    Query query = dbSession.createQuery(hql);
	    query.setParameter("maPhongChieu", maPhongChieu);
	    return !query.list().isEmpty();
	}

	@RequestMapping(value = "/theater-rooms", method = RequestMethod.GET)
	public String showTheaterRoomManager(Model model,
	                                     @RequestParam(value = "edit", required = false) String editMaPhongChieu,
	                                     @RequestParam(value = "maRapChieu", required = false) String maRapChieuFilter,
	                                     @RequestParam Map<String, String> params) {
	    Session dbSession = sessionFactory.openSession();
	    try {
	        Query query = dbSession.createQuery("FROM PhongChieuEntity ORDER BY maPhongChieu DESC");
	        query.setMaxResults(1);
	        PhongChieuEntity latestPhong = (PhongChieuEntity) query.uniqueResult();

	        String newMaPhongChieu;
	        if (latestPhong == null) {
	            newMaPhongChieu = "PC001";
	        } else {
	            String lastMaPhong = latestPhong.getMaPhongChieu();
	            int lastId = Integer.parseInt(lastMaPhong.substring(2));
	            newMaPhongChieu = String.format("PC%03d", lastId + 1);
	        }
	        model.addAttribute("newMaPhongChieu", newMaPhongChieu);

	        Query rapQuery = dbSession.createQuery("FROM RapChieuEntity");
	        List<RapChieuEntity> rapChieuEntities = rapQuery.list();
	        model.addAttribute("rapChieuList", rapChieuEntities);

	        Query seatTypeQuery = dbSession.createQuery("FROM LoaiGheEntity");
	        List<LoaiGheEntity> loaiGheEntities = seatTypeQuery.list();
	        model.addAttribute("seatTypeList", loaiGheEntities);

	        String hql = "FROM PhongChieuEntity";
	        if (maRapChieuFilter != null && !maRapChieuFilter.isEmpty()) {
	            hql += " WHERE rapChieu.maRapChieu = :maRapChieu";
	        }
	        Query phongChieuQuery = dbSession.createQuery(hql);
	        if (maRapChieuFilter != null && !maRapChieuFilter.isEmpty()) {
	            phongChieuQuery.setParameter("maRapChieu", maRapChieuFilter);
	        }

	        List<PhongChieuEntity> phongChieuEntities = phongChieuQuery.list();
	        List<PhongChieuModel> roomList = new ArrayList<>();
	        for (Object obj : phongChieuEntities) {
	            PhongChieuEntity entity = (PhongChieuEntity) obj;
	            roomList.add(new PhongChieuModel(entity));
	        }

	        model.addAttribute("roomList", roomList);

	        if (editMaPhongChieu != null && !editMaPhongChieu.isEmpty()) {
	            PhongChieuEntity phongChieu = (PhongChieuEntity) dbSession.get(PhongChieuEntity.class, editMaPhongChieu);
	            if (phongChieu != null) {
	                model.addAttribute("room", new PhongChieuModel(phongChieu));
	                model.addAttribute("isEdit", true);
	            } else {
	                model.addAttribute("isEdit", false);
	            }
	        } else {
	            model.addAttribute("isEdit", false);
	        }

	        return "admin/theater_room_manager";

	    } catch (Exception e) {
	        e.printStackTrace();
	        model.addAttribute("notificationType", "error");
	        model.addAttribute("notificationMessage", "Lỗi khi lấy danh sách phòng chiếu: " + e.getMessage());
	        return "admin/theater_room_manager";
	    } finally {
	        dbSession.close();
	    }
	}

	@Transactional
	@RequestMapping(value = "/theater-rooms/add", method = RequestMethod.POST)
	public String addTheaterRoom(@RequestParam("maPhongChieu") String maPhongChieu,
	                            @RequestParam("tenPhongChieu") String tenPhongChieu,
	                            @RequestParam("sucChua") int sucChua,
	                            @RequestParam("maRapChieu") String maRapChieu,
	                            @RequestParam("hinhAnh") MultipartFile hinhAnh,
	                            RedirectAttributes redirectAttributes) {
	    try {
	        Session dbSession = sessionFactory.getCurrentSession();
	        
	        Query checkDuplicateQuery = dbSession.createQuery(
	                "FROM PhongChieuEntity WHERE tenPhongChieu = :tenPhongChieu AND rapChieu.maRapChieu = :maRapChieu");
	        checkDuplicateQuery.setParameter("tenPhongChieu", tenPhongChieu);
	        checkDuplicateQuery.setParameter("maRapChieu", maRapChieu);
	        List<PhongChieuEntity> existingRooms = checkDuplicateQuery.list();

	        if (!existingRooms.isEmpty()) {
	            redirectAttributes.addFlashAttribute("notificationType", "error");
	            redirectAttributes.addFlashAttribute("notificationMessage", "Tên phòng '" + tenPhongChieu + "' đã tồn tại trong rạp này. Vui lòng chọn tên khác.");
	            return "redirect:/admin/theater-rooms";
	        }

	        PhongChieuEntity phongChieu = new PhongChieuEntity();
	        phongChieu.setMaPhongChieu(maPhongChieu);
	        phongChieu.setTenPhongChieu(tenPhongChieu);
	        phongChieu.setSucChua(sucChua);

	        String urlHinhAnh = handleImageUpload(hinhAnh, null);
	        phongChieu.setUrlHinhAnh(urlHinhAnh);

	        RapChieuEntity rapChieu = (RapChieuEntity) dbSession.get(RapChieuEntity.class, maRapChieu);
	        if (rapChieu == null) {
	            redirectAttributes.addFlashAttribute("notificationType", "error");
	            redirectAttributes.addFlashAttribute("notificationMessage", "Rạp chiếu không tồn tại");
	            return "redirect:/admin/theater-rooms";
	        }
	        phongChieu.setRapChieu(rapChieu);

	        dbSession.save(phongChieu);

	        redirectAttributes.addFlashAttribute("notificationType", "success");
	        redirectAttributes.addFlashAttribute("notificationMessage", "Thêm phòng chiếu '" + tenPhongChieu + "' thành công!");
	        return "redirect:/admin/theater-rooms";

	    } catch (Exception e) {
	        e.printStackTrace();
	        redirectAttributes.addFlashAttribute("notificationType", "error");
	        redirectAttributes.addFlashAttribute("notificationMessage", "Lỗi khi thêm phòng chiếu: " + e.getMessage());
	        return "redirect:/admin/theater-rooms";
	    }
	}

	@Transactional
	@RequestMapping(value = "/theater-rooms/update", method = RequestMethod.POST)
	public String editTheaterRoom(@RequestParam("maPhongChieu") String maPhongChieu,
	                             @RequestParam("tenPhongChieu") String tenPhongChieu,
	                             @RequestParam("sucChua") int sucChua,
	                             @RequestParam("maRapChieu") String maRapChieu,
	                             @RequestParam("hinhAnh") MultipartFile hinhAnh,
	                             Model model,
	                             RedirectAttributes redirectAttributes) {
	    Session dbSession = sessionFactory.openSession();
	    try {
	        PhongChieuEntity existingPhong = (PhongChieuEntity) dbSession.get(PhongChieuEntity.class, maPhongChieu);

	        if (existingPhong == null) {
	            redirectAttributes.addFlashAttribute("notificationType", "error");
	            redirectAttributes.addFlashAttribute("notificationMessage", "Phòng chiếu không tồn tại");
	            return "redirect:/admin/theater-rooms";
	        }

	        // Kiểm tra suất chiếu
	        String hqlShowtime = "FROM SuatChieuEntity WHERE phongChieu.maPhongChieu = :maPhongChieu";
	        Query showtimeQuery = dbSession.createQuery(hqlShowtime);
	        showtimeQuery.setParameter("maPhongChieu", maPhongChieu);
	        boolean hasShowtimes = !showtimeQuery.list().isEmpty();
	        if (hasShowtimes) {
	            // Populate full model to ensure page renders correctly
	            // Get newMaPhongChieu
	            Query newIdQuery = dbSession.createQuery("FROM PhongChieuEntity ORDER BY maPhongChieu DESC");
	            newIdQuery.setMaxResults(1);
	            PhongChieuEntity latestPhong = (PhongChieuEntity) newIdQuery.uniqueResult();
	            String newMaPhongChieu = latestPhong == null ? "PC001" : String.format("PC%03d", 
	                Integer.parseInt(latestPhong.getMaPhongChieu().substring(2)) + 1);
	            model.addAttribute("newMaPhongChieu", newMaPhongChieu);

	            // Get rapChieuList
	            Query rapQuery = dbSession.createQuery("FROM RapChieuEntity");
	            List<RapChieuEntity> rapChieuEntities = rapQuery.list();
	            model.addAttribute("rapChieuList", rapChieuEntities);

	            // Get seatTypeList
	            Query seatTypeQuery = dbSession.createQuery("FROM LoaiGheEntity");
	            List<LoaiGheEntity> loaiGheEntities = seatTypeQuery.list();
	            model.addAttribute("seatTypeList", loaiGheEntities);

	            // Get roomList
	            Query phongChieuQuery = dbSession.createQuery("FROM PhongChieuEntity");
	            List<PhongChieuEntity> phongChieuEntities = phongChieuQuery.list();
	            List<PhongChieuModel> roomList = phongChieuEntities.stream()
	                .map(PhongChieuModel::new)
	                .collect(Collectors.toList());
	            model.addAttribute("roomList", roomList);

	            // Set room and edit state
	            model.addAttribute("room", new PhongChieuModel(existingPhong));
	            model.addAttribute("isEdit", true);
	            model.addAttribute("editMaPhongChieu", maPhongChieu);
	            model.addAttribute("notificationType", "error");
	            model.addAttribute("notificationMessage", "Không thể sửa phòng chiếu vì đã có suất chiếu.");
	            return "admin/theater_room_manager";
	        }

	        Query checkDuplicateQuery = dbSession.createQuery(
	                "FROM PhongChieuEntity WHERE tenPhongChieu = :tenPhongChieu AND rapChieu.maRapChieu = :maRapChieu AND maPhongChieu != :maPhongChieu");
	        checkDuplicateQuery.setParameter("tenPhongChieu", tenPhongChieu);
	        checkDuplicateQuery.setParameter("maRapChieu", maRapChieu);
	        checkDuplicateQuery.setParameter("maPhongChieu", maPhongChieu);
	        List<PhongChieuEntity> existingRooms = checkDuplicateQuery.list();

	        if (!existingRooms.isEmpty()) {
	            // Populate full model for duplicate name error
	            Query newIdQuery = dbSession.createQuery("FROM PhongChieuEntity ORDER BY maPhongChieu DESC");
	            newIdQuery.setMaxResults(1);
	            PhongChieuEntity latestPhong = (PhongChieuEntity) newIdQuery.uniqueResult();
	            String newMaPhongChieu = latestPhong == null ? "PC001" : String.format("PC%03d", 
	                Integer.parseInt(latestPhong.getMaPhongChieu().substring(2)) + 1);
	            model.addAttribute("newMaPhongChieu", newMaPhongChieu);

	            Query rapQuery = dbSession.createQuery("FROM RapChieuEntity");
	            List<RapChieuEntity> rapChieuEntities = rapQuery.list();
	            model.addAttribute("rapChieuList", rapChieuEntities);

	            Query seatTypeQuery = dbSession.createQuery("FROM LoaiGheEntity");
	            List<LoaiGheEntity> loaiGheEntities = seatTypeQuery.list();
	            model.addAttribute("seatTypeList", loaiGheEntities);

	            Query phongChieuQuery = dbSession.createQuery("FROM PhongChieuEntity");
	            List<PhongChieuEntity> phongChieuEntities = phongChieuQuery.list();
	            List<PhongChieuModel> roomList = phongChieuEntities.stream()
	                .map(PhongChieuModel::new)
	                .collect(Collectors.toList());
	            model.addAttribute("roomList", roomList);

	            model.addAttribute("room", new PhongChieuModel(existingPhong));
	            model.addAttribute("isEdit", true);
	            model.addAttribute("editMaPhongChieu", maPhongChieu);
	            model.addAttribute("notificationType", "error");
	            model.addAttribute("notificationMessage", "Tên phòng '" + tenPhongChieu + "' đã tồn tại trong rạp này. Vui lòng chọn tên khác.");
	            return "admin/theater_room_manager";
	        }

	        existingPhong.setTenPhongChieu(tenPhongChieu);
	        existingPhong.setSucChua(sucChua);

	        if (!hinhAnh.isEmpty()) {
	            String urlHinhAnh = handleImageUpload(hinhAnh, existingPhong.getUrlHinhAnh());
	            existingPhong.setUrlHinhAnh(urlHinhAnh);
	        }

	        RapChieuEntity rapChieu = (RapChieuEntity) dbSession.get(RapChieuEntity.class, maRapChieu);
	        if (rapChieu == null) {
	            redirectAttributes.addFlashAttribute("notificationType", "error");
	            redirectAttributes.addFlashAttribute("notificationMessage", "Rạp chiếu không tồn tại");
	            return "redirect:/admin/theater-rooms";
	        }
	        existingPhong.setRapChieu(rapChieu);
	        dbSession.update(existingPhong);

	        redirectAttributes.addFlashAttribute("notificationType", "success");
	        redirectAttributes.addFlashAttribute("notificationMessage", "Cập nhật phòng chiếu '" + tenPhongChieu + "' thành công!");
	        return "redirect:/admin/theater-rooms";

	    } catch (Exception e) {
	        e.printStackTrace();
	        redirectAttributes.addFlashAttribute("notificationType", "error");
	        redirectAttributes.addFlashAttribute("notificationMessage", "Lỗi khi sửa phòng chiếu: " + e.getMessage());
	        return "redirect:/admin/theater-rooms";
	    } finally {
	        dbSession.close();
	    }
	}

	@Transactional
	@RequestMapping(value = "/theater-rooms/delete/{maPhongChieu}", method = RequestMethod.GET)
	public String deleteTheaterRoom(@PathVariable("maPhongChieu") String maPhongChieu, RedirectAttributes redirectAttributes) {
	    try {
	        Session dbSession = sessionFactory.getCurrentSession();
	        PhongChieuEntity phongChieu = (PhongChieuEntity) dbSession.get(PhongChieuEntity.class, maPhongChieu);

	        if (phongChieu == null) {
	            redirectAttributes.addFlashAttribute("notificationType", "error");
	            redirectAttributes.addFlashAttribute("notificationMessage", "Không tìm thấy phòng chiếu với mã " + maPhongChieu);
	            return "redirect:/admin/theater-rooms";
	        }

	        if (isRoomHasBookedSeats(dbSession, maPhongChieu)) {
	            redirectAttributes.addFlashAttribute("notificationType", "error");
	            redirectAttributes.addFlashAttribute("notificationMessage", "Không thể xóa phòng chiếu vì đã có ghế trong phòng này được đặt vé.");
	            return "redirect:/admin/theater-rooms";
	        }

	        // Kiểm tra suất chiếu
	        String hqlShowtime = "FROM SuatChieuEntity WHERE phongChieu.maPhongChieu = :maPhongChieu";
	        Query showtimeQuery = dbSession.createQuery(hqlShowtime);
	        showtimeQuery.setParameter("maPhongChieu", maPhongChieu);
	        boolean hasShowtimes = !showtimeQuery.list().isEmpty();
	        if (hasShowtimes) {
	            redirectAttributes.addFlashAttribute("notificationType", "error");
	            redirectAttributes.addFlashAttribute("notificationMessage", "Không thể xóa phòng chiếu vì đã có suất chiếu.");
	            return "redirect:/admin/theater-rooms";
	        }

	        Query query = dbSession.createQuery("FROM GheEntity WHERE phongChieu.maPhongChieu = :maPhongChieu");
	        query.setParameter("maPhongChieu", maPhongChieu);
	        List gheEntities = query.list();

	        if (!gheEntities.isEmpty()) {
	            redirectAttributes.addFlashAttribute("notificationType", "error");
	            redirectAttributes.addFlashAttribute("notificationMessage", "Không thể xóa phòng chiếu vì có ghế liên quan.");
	            return "redirect:/admin/theater-rooms";
	        }

	        dbSession.delete(phongChieu);

	        redirectAttributes.addFlashAttribute("notificationType", "success");
	        redirectAttributes.addFlashAttribute("notificationMessage", "Xóa phòng chiếu thành công!");
	        return "redirect:/admin/theater-rooms";

	    } catch (Exception e) {
	        e.printStackTrace();
	        redirectAttributes.addFlashAttribute("notificationType", "error");
	        redirectAttributes.addFlashAttribute("notificationMessage", "Lỗi khi xóa phòng chiếu: " + e.getMessage());
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
			@RequestBody List<Map<String, Object>> seatData, RedirectAttributes redirectAttributes) {
		try {
			Session dbSession = sessionFactory.getCurrentSession();

			PhongChieuEntity phongChieu = (PhongChieuEntity) dbSession.get(PhongChieuEntity.class, maPhongChieu);
			if (phongChieu == null) {
				redirectAttributes.addFlashAttribute("notificationType", "error");
				redirectAttributes.addFlashAttribute("notificationMessage", "Không tìm thấy phòng chiếu với mã " + maPhongChieu);
				return "redirect:/admin/theater-rooms";
			}

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
					totalCapacity += 1;
				}
			}

			if (totalCapacity > phongChieu.getSucChua()) {
				redirectAttributes.addFlashAttribute("notificationType", "error");
				redirectAttributes.addFlashAttribute("notificationMessage", "Tổng số chỗ ngồi (" + totalCapacity + ") vượt quá sức chứa của phòng (" + phongChieu.getSucChua() + ")");
				return "redirect:/admin/theater-rooms";
			}

			Query deleteQuery = dbSession.createQuery("DELETE FROM GheEntity WHERE phongChieu.maPhongChieu = :maPhongChieu");
			deleteQuery.setParameter("maPhongChieu", maPhongChieu);
			deleteQuery.executeUpdate();

			Map<String, String> adminToUserRowMap = new HashMap<>();
			List<String> allTenHangAdmin = new ArrayList<>();
			for (Map<String, Object> seat : seatData) {
				String tenHangAdmin = (String) seat.get("tenHangAdmin");
				if (!allTenHangAdmin.contains(tenHangAdmin)) {
					allTenHangAdmin.add(tenHangAdmin);
				}
			}
			allTenHangAdmin.sort(String::compareTo);

			for (int i = 0; i < allTenHangAdmin.size(); i++) {
				adminToUserRowMap.put(allTenHangAdmin.get(i), String.valueOf((char) ('A' + i)));
			}

			Map<String, List<Integer>> rowSeats = new HashMap<>();
			for (Map<String, Object> seat : seatData) {
				int row = ((Number) seat.get("row")).intValue();
				int col = ((Number) seat.get("col")).intValue();
				rowSeats.computeIfAbsent(String.valueOf(row), k -> new ArrayList<>()).add(col);
			}

			Map<Integer, Integer> adminToUserSeatMap = new HashMap<>();
			List<Integer> allCols = rowSeats.values().stream().flatMap(List::stream).distinct().sorted().collect(Collectors.toList());
			int minCol = allCols.get(0);
			int colOffset = minCol - 1;
			for (int col : allCols) {
				adminToUserSeatMap.put(col, col - colOffset);
			}

			for (Map<String, Object> seat : seatData) {
				int row = ((Number) seat.get("row")).intValue();
				int col = ((Number) seat.get("col")).intValue();
				String tenHangAdmin = (String) seat.get("tenHangAdmin");
				String maLoaiGhe = (String) seat.get("type");

				GheEntity ghe = new GheEntity();
				ghe.setMaGhe("G" + System.currentTimeMillis() % 10000 + row + col);
				ghe.setPhongChieu(phongChieu);
				ghe.setSoGheAdmin(String.valueOf(col + 1));
				ghe.setTenHangAdmin(tenHangAdmin);
				ghe.setSoGhe(String.valueOf(adminToUserSeatMap.get(col)));
				ghe.setTenHang(adminToUserRowMap.get(tenHangAdmin));

				LoaiGheEntity loaiGhe = loaiGheMap.get(maLoaiGhe);
				if (loaiGhe != null) {
					ghe.setLoaiGhe(loaiGhe);
				} else {
					throw new IllegalArgumentException("Loại ghế không tồn tại: " + maLoaiGhe);
				}

				dbSession.save(ghe);
			}

			redirectAttributes.addFlashAttribute("notificationType", "success");
			redirectAttributes.addFlashAttribute("notificationMessage", "Lưu sơ đồ ghế cho phòng " + maPhongChieu + " thành công!");
			return "redirect:/admin/theater-rooms";
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("notificationType", "error");
			redirectAttributes.addFlashAttribute("notificationMessage", "Lỗi khi lưu sơ đồ ghế: " + e.getMessage());
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
	public String addSeatType(@RequestParam("maLoaiGhe") String maLoaiGhe,
	                         @RequestParam("tenLoaiGhe") String tenLoaiGhe,
	                         @RequestParam("heSoGia") double heSoGia,
	                         @RequestParam("mauGhe") String mauGhe,
	                         @RequestParam("soCho") int soCho,
	                         RedirectAttributes redirectAttributes) {
	    try {
	        Session dbSession = sessionFactory.getCurrentSession();

	        if (maLoaiGhe == null || maLoaiGhe.trim().isEmpty()) {
	            redirectAttributes.addFlashAttribute("notificationType", "error");
	            redirectAttributes.addFlashAttribute("notificationMessage", "Mã loại ghế không được để trống.");
	            return "redirect:/admin/theater-rooms";
	        }

	        if (tenLoaiGhe == null || tenLoaiGhe.trim().isEmpty()) {
	            redirectAttributes.addFlashAttribute("notificationType", "error");
	            redirectAttributes.addFlashAttribute("notificationMessage", "Tên loại ghế không được để trống.");
	            return "redirect:/admin/theater-rooms";
	        }

	        LoaiGheEntity existingByCode = (LoaiGheEntity) dbSession.get(LoaiGheEntity.class, maLoaiGhe);
	        if (existingByCode != null) {
	            redirectAttributes.addFlashAttribute("notificationType", "error");
	            redirectAttributes.addFlashAttribute("notificationMessage", "Mã loại ghế '" + maLoaiGhe + "' đã tồn tại.");
	            return "redirect:/admin/theater-rooms";
	        }

	        Query nameCheckQuery = dbSession.createQuery("FROM LoaiGheEntity WHERE tenLoaiGhe = :tenLoaiGhe");
	        nameCheckQuery.setParameter("tenLoaiGhe", tenLoaiGhe);
	        List<LoaiGheEntity> existingByName = nameCheckQuery.list();
	        if (!existingByName.isEmpty()) {
	            redirectAttributes.addFlashAttribute("notificationType", "error");
	            redirectAttributes.addFlashAttribute("notificationMessage", "Tên loại ghế '" + tenLoaiGhe + "' đã tồn tại.");
	            return "redirect:/admin/theater-rooms";
	        }

	        Query colorCheckQuery = dbSession.createQuery("FROM LoaiGheEntity WHERE mauGhe = :mauGhe");
	        colorCheckQuery.setParameter("mauGhe", mauGhe);
	        List<LoaiGheEntity> existingByColor = colorCheckQuery.list();
	        if (!existingByColor.isEmpty()) {
	            redirectAttributes.addFlashAttribute("notificationType", "error");
	            redirectAttributes.addFlashAttribute("notificationMessage", "Màu ghế '" + mauGhe + "' đã được sử dụng cho loại ghế khác.");
	            return "redirect:/admin/theater-rooms";
	        }

	        LoaiGheEntity loaiGhe = new LoaiGheEntity();
	        loaiGhe.setMaLoaiGhe(maLoaiGhe);
	        loaiGhe.setTenLoaiGhe(tenLoaiGhe);
	        loaiGhe.setHeSoGia(heSoGia);
	        loaiGhe.setMauGhe(mauGhe);
	        loaiGhe.setSoCho(soCho);

	        dbSession.save(loaiGhe);

	        redirectAttributes.addFlashAttribute("notificationType", "success");
	        redirectAttributes.addFlashAttribute("notificationMessage", "Thêm loại ghế '" + tenLoaiGhe + "' thành công!");
	        return "redirect:/admin/theater-rooms";

	    } catch (Exception e) {
	        e.printStackTrace();
	        redirectAttributes.addFlashAttribute("notificationType", "error");
	        redirectAttributes.addFlashAttribute("notificationMessage", "Lỗi khi thêm loại ghế: " + e.getMessage());
	        return "redirect:/admin/theater-rooms";
	    }
	}	

	@Transactional
	@RequestMapping(value = "/theater-rooms/seat-types/update", method = RequestMethod.POST)
	public String updateSeatType(@RequestParam("maLoaiGhe") String maLoaiGhe,
	                            @RequestParam("tenLoaiGhe") String tenLoaiGhe,
	                            @RequestParam("heSoGia") double heSoGia,
	                            @RequestParam("mauGhe") String mauGhe,
	                            @RequestParam("soCho") int soCho,
	                            RedirectAttributes redirectAttributes) {
	    try {
	        Session dbSession = sessionFactory.getCurrentSession();

	        LoaiGheEntity loaiGhe = (LoaiGheEntity) dbSession.get(LoaiGheEntity.class, maLoaiGhe);
	        if (loaiGhe == null) {
	            redirectAttributes.addFlashAttribute("notificationType", "error");
	            redirectAttributes.addFlashAttribute("notificationMessage", "Không tìm thấy loại ghế với mã " + maLoaiGhe);
	            return "redirect:/admin/theater-rooms";
	        }

	        Query nameCheckQuery = dbSession.createQuery(
	                "FROM LoaiGheEntity WHERE tenLoaiGhe = :tenLoaiGhe AND maLoaiGhe != :currentMa");
	        nameCheckQuery.setParameter("tenLoaiGhe", tenLoaiGhe);
	        nameCheckQuery.setParameter("currentMa", maLoaiGhe);
	        List<LoaiGheEntity> existingByName = nameCheckQuery.list();
	        if (!existingByName.isEmpty()) {
	            redirectAttributes.addFlashAttribute("notificationType", "error");
	            redirectAttributes.addFlashAttribute("notificationMessage", "Tên loại ghế '" + tenLoaiGhe + "' đã tồn tại.");
	            return "redirect:/admin/theater-rooms";
	        }

	        Query colorCheckQuery = dbSession.createQuery(
	                "FROM LoaiGheEntity WHERE mauGhe = :mauGhe AND maLoaiGhe != :currentMa");
	        colorCheckQuery.setParameter("mauGhe", mauGhe);
	        colorCheckQuery.setParameter("currentMa", maLoaiGhe);
	        List<LoaiGheEntity> existingByColor = colorCheckQuery.list();
	        if (!existingByColor.isEmpty()) {
	            redirectAttributes.addFlashAttribute("notificationType", "error");
	            redirectAttributes.addFlashAttribute("notificationMessage", "Màu ghế '" + mauGhe + "' đã được sử dụng bởi loại ghế khác.");
	            return "redirect:/admin/theater-rooms";
	        }

	        loaiGhe.setTenLoaiGhe(tenLoaiGhe);
	        loaiGhe.setHeSoGia(heSoGia);
	        loaiGhe.setMauGhe(mauGhe);
	        loaiGhe.setSoCho(soCho);

	        dbSession.update(loaiGhe);

	        redirectAttributes.addFlashAttribute("notificationType", "success");
	        redirectAttributes.addFlashAttribute("notificationMessage", "Cập nhật loại ghế '" + tenLoaiGhe + "' thành công!");
	        return "redirect:/admin/theater-rooms";

	    } catch (Exception e) {
	        e.printStackTrace();
	        redirectAttributes.addFlashAttribute("notificationType", "error");
	        redirectAttributes.addFlashAttribute("notificationMessage", "Lỗi khi cập nhật loại ghế: " + e.getMessage());
	        return "redirect:/admin/theater-rooms";
	    }
	}

	@Transactional
	@RequestMapping(value = "/theater-rooms/seat-types/delete/{maLoaiGhe}", method = RequestMethod.POST)
	public String deleteSeatType(@PathVariable("maLoaiGhe") String maLoaiGhe, RedirectAttributes redirectAttributes) {
	    try {
	        Session dbSession = sessionFactory.getCurrentSession();

	        LoaiGheEntity loaiGhe = (LoaiGheEntity) dbSession.get(LoaiGheEntity.class, maLoaiGhe);
	        if (loaiGhe == null) {
	            redirectAttributes.addFlashAttribute("notificationType", "error");
	            redirectAttributes.addFlashAttribute("notificationMessage", "Không tìm thấy loại ghế với mã " + maLoaiGhe);
	            return "redirect:/admin/theater-rooms";
	        }

	        Query query = dbSession.createQuery("FROM GheEntity WHERE maLoaiGhe = :maLoaiGhe");
	        query.setParameter("maLoaiGhe", maLoaiGhe);
	        List gheEntities = query.list();

	        if (!gheEntities.isEmpty()) {
	            redirectAttributes.addFlashAttribute("notificationType", "error");
	            redirectAttributes.addFlashAttribute("notificationMessage", "Không thể xóa loại ghế vì có ghế đang sử dụng loại này.");
	            return "redirect:/admin/theater-rooms";
	        }

	        dbSession.delete(loaiGhe);

	        redirectAttributes.addFlashAttribute("notificationType", "success");
	        redirectAttributes.addFlashAttribute("notificationMessage", "Xóa loại ghế thành công!");
	        return "redirect:/admin/theater-rooms";

	    } catch (Exception e) {
	        e.printStackTrace();
	        redirectAttributes.addFlashAttribute("notificationType", "error");
	        redirectAttributes.addFlashAttribute("notificationMessage", "Lỗi khi xóa loại ghế: " + e.getMessage());
	        return "redirect:/admin/theater-rooms";
	    }
	}
}