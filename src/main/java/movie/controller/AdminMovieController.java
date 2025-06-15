package movie.controller;

import movie.entity.*;
import movie.model.PhimModel;
import org.hibernate.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;
import org.springframework.web.servlet.mvc.support.*;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;

@Controller
@RequestMapping("/admin")
public class AdminMovieController {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private ServletContext context;

    // Helper methods (unchanged, included for reference)
    private List<PhimModel> fetchPhimList(Session session) {
        Query query = session.createQuery("FROM PhimEntity");
        List<PhimEntity> phimEntities = query.list();
        List<PhimModel> phimModels = new ArrayList<>();
        for (PhimEntity entity : phimEntities) {
            phimModels.add(new PhimModel(entity));
        }
        return phimModels;
    }

    private List<TheLoaiEntity> fetchTheLoaiList(Session session) {
        Query query = session.createQuery("FROM TheLoaiEntity");
        return query.list();
    }

    private List<DienVienEntity> fetchDienVienList(Session session) {
        Query query = session.createQuery("FROM DienVienEntity");
        return query.list();
    }

    private List<DinhDangEntity> fetchDinhDangList(Session session) {
        Query query = session.createQuery("FROM DinhDangEntity");
        return query.list();
    }

    private void populateCommonModelAttributes(Model model, Session session, boolean isEdit) {
        model.addAttribute("phimList", fetchPhimList(session));
        model.addAttribute("theLoaiList", fetchTheLoaiList(session));
        model.addAttribute("dienVienList", fetchDienVienList(session));
        model.addAttribute("dinhDangList", fetchDinhDangList(session));
        model.addAttribute("isEdit", isEdit);
    }

    private void manageTheLoaiEntities(Session session, PhimEntity phim, String theLoaiStr) {
        Set<TheLoaiEntity> theLoais = new HashSet<>();
        if (theLoaiStr != null && !theLoaiStr.isEmpty()) {
            String[] theLoaiNames = theLoaiStr.split(",");
            for (String tenTheLoai : theLoaiNames) {
                tenTheLoai = tenTheLoai.trim();
                Query query = session.createQuery("FROM TheLoaiEntity WHERE tenTheLoai = :tenTheLoai");
                query.setParameter("tenTheLoai", tenTheLoai);
                TheLoaiEntity theLoai = (TheLoaiEntity) query.uniqueResult();
                if (theLoai == null) {
                    theLoai = new TheLoaiEntity();
                    theLoai.setMaTheLoai("TL" + System.currentTimeMillis() % 10000);
                    theLoai.setTenTheLoai(tenTheLoai);
                    session.save(theLoai);
                }
                theLoais.add(theLoai);
            }
        }
        phim.setTheLoais(theLoais);
    }

    private void manageDienVienEntities(Session session, PhimEntity phim, String dvChinhStr) {
        Set<DienVienEntity> dienViens = new HashSet<>();
        if (dvChinhStr != null && !dvChinhStr.isEmpty()) {
            String[] dienVienNames = dvChinhStr.split(",");
            for (String hoTen : dienVienNames) {
                hoTen = hoTen.trim();
                Query query = session.createQuery("FROM DienVienEntity WHERE hoTen = :hoTen");
                query.setParameter("hoTen", hoTen);
                DienVienEntity dienVien = (DienVienEntity) query.uniqueResult();
                if (dienVien == null) {
                    dienVien = new DienVienEntity();
                    dienVien.setMaDienVien("DV" + System.currentTimeMillis() % 10000);
                    dienVien.setHoTen(hoTen);
                    session.save(dienVien);
                }
                dienViens.add(dienVien);
            }
        }
        phim.setDienViens(dienViens);
    }

    private void manageDinhDangEntities(Session session, PhimEntity phim, String dinhDangStr) {
        Set<DinhDangEntity> dinhDangs = new HashSet<>();
        if (dinhDangStr != null && !dinhDangStr.isEmpty()) {
            String[] dinhDangNames = dinhDangStr.split(",");
            for (String tenDinhDang : dinhDangNames) {
                tenDinhDang = tenDinhDang.trim();
                Query query = session.createQuery("FROM DinhDangEntity WHERE tenDinhDang = :tenDinhDang");
                query.setParameter("tenDinhDang", tenDinhDang);
                DinhDangEntity dinhDang = (DinhDangEntity) query.uniqueResult();
                if (dinhDang == null) {
                    dinhDang = new DinhDangEntity();
                    dinhDang.setMaDinhDang("DD" + System.currentTimeMillis() % 1000);
                    dinhDang.setTenDinhDang(tenDinhDang);
                    session.save(dinhDang);
                }
             dinhDangs.add(dinhDang);
        }
        }
        phim.setDinhDangs(dinhDangs);
    }

    private String handlePosterUpload(MultipartFile poster, String oldPosterPath) throws Exception {
        if (poster == null || poster.isEmpty()) {
            if (oldPosterPath == null) {
                throw new IllegalArgumentException("Vui lòng chọn hình ảnh!");
            }
        }

        String dirPath = context.getRealPath("/resources/images/"); 
        File dir = new File(dirPath);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new RuntimeException("Lỗi tạo đường dẫn: " + dir.getAbsolutePath());
            }
        }

        String fileName = System.currentTimeMillis() + "_" + poster.getOriginalFilename();
        String filePath = dirPath + File.separator + fileName;
        File destination = new File(filePath);

        if (!destination.getParentFile().canWrite()) {
            throw new RuntimeException("No write permission for directory: " + dir.getAbsolutePath());
        }

        poster.transferTo(destination);
        return fileName;
    }

    private boolean hasShowtimes(Session session, String maPhim) {
        Query query = session.createQuery("FROM SuatChieuEntity WHERE maPhim = :maPhim");
        query.setParameter("maPhim", maPhim);
        return !query.list().isEmpty();
    }


    private String checkNgayKhoiChieu(Session session, String maPhim, Date newNgayKhoiChieu) {
        Query query = session.createQuery(
            "FROM SuatChieuEntity WHERE maPhim = :maPhim AND ngayGioChieu < :newNgayKhoiChieu"
        );
        query.setParameter("maPhim", maPhim);
        query.setParameter("newNgayKhoiChieu", new java.sql.Timestamp(newNgayKhoiChieu.getTime()));
        List<SuatChieuEntity> suatChieus = query.list();

        if (!suatChieus.isEmpty()) {
            return "Phim đã có suất chiếu. Không thể xóa hoặc sửa được!";
        }
        return null;
    }

    private String checkDuplicatePhim(Session session, String tenPhim, String maPhim) {
        if (tenPhim == null || tenPhim.trim().isEmpty()) {
            return null; // Không kiểm tra trùng nếu tên rỗng
        }
        // Chuẩn hóa tenPhim: loại bỏ dấu hai chấm, khoảng trắng thừa, chuyển về lowercase, chuẩn hóa Unicode NFC
        String normalizedTenPhim = Normalizer.normalize(tenPhim.trim(), Normalizer.Form.NFC)
                .toLowerCase()
                .replaceAll("[:;]", "") // Loại bỏ dấu hai chấm và dấu chấm phẩy
                .replaceAll("\\s+", " "); // Thay nhiều khoảng trắng bằng một
        Query query = session.createQuery(	
            "FROM PhimEntity WHERE REPLACE(LOWER(TRIM(tenPhim)), ':', '') = :tenPhim AND maPhim != :maPhim"
        );
        query.setParameter("tenPhim", normalizedTenPhim);
        query.setParameter("maPhim", maPhim != null && !maPhim.trim().isEmpty() ? maPhim.trim() : "");
        List<PhimEntity> results = query.list();
        if (!results.isEmpty()) {
            return "Tên phim đã tồn tại!";
        }
        return null;
    }

    @RequestMapping(value = "/movies", method = RequestMethod.GET)
    public String showMovieManager(
            @RequestParam(value = "viewMaPhim", required = false) String viewMaPhim,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "theLoai", required = false) String theLoai,
            @RequestParam(value = "dinhDang", required = false) String dinhDang,
            @RequestParam(value = "doTuoi", required = false) String doTuoi,
            @RequestParam(value = "quocGia", required = false) String quocGia,
            @RequestParam(value = "searchTenPhim", required = false) String searchTenPhim,
            HttpSession session,
            Model model) {
        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/auth/login";
        }

        Session dbSession = sessionFactory.openSession();
        try {
            Query query = dbSession.createQuery("FROM PhimEntity ORDER BY maPhim DESC");
            query.setMaxResults(1);
            PhimEntity latestPhim = (PhimEntity) query.uniqueResult();

            String newMaPhim;
            if (latestPhim == null) {
                newMaPhim = "P001";
            } else {
                String lastMaPhim = latestPhim.getMaPhim();
                int lastId = Integer.parseInt(lastMaPhim.substring(1));
                newMaPhim = String.format("P%03d", lastId + 1);
            }

            populateCommonModelAttributes(model, dbSession, false);

            PhimModel phimModel = new PhimModel();
            phimModel.setMaPhim(newMaPhim);
            model.addAttribute("phimModel", phimModel);

            StringBuilder hql = new StringBuilder("FROM PhimEntity p WHERE 1=1");
            List<String> params = new ArrayList<>();
            List<Object> values = new ArrayList<>();

            // Xử lý tìm kiếm theo tên phim
            if (searchTenPhim != null && !searchTenPhim.trim().isEmpty()) {
                hql.append(" AND LOWER(p.tenPhim) LIKE :searchTenPhim");
                params.add("searchTenPhim");
                values.add("%" + Normalizer.normalize(searchTenPhim.trim(), Normalizer.Form.NFC).toLowerCase() + "%");
            }

            if (theLoai != null && !theLoai.trim().isEmpty() && !theLoai.equals("all")) {
                hql.append(" AND EXISTS (SELECT 1 FROM p.theLoais tl WHERE tl.tenTheLoai = :theLoai)");
                params.add("theLoai");
                values.add(theLoai.trim());
            }

            if (dinhDang != null && !dinhDang.trim().isEmpty() && !dinhDang.equals("all")) {
                hql.append(" AND EXISTS (SELECT 1 FROM p.dinhDangs dd WHERE dd.tenDinhDang = :dinhDang)");
                params.add("dinhDang");
                values.add(dinhDang.trim());
            }

            Integer doTuoiInt = null;
            if (doTuoi != null && !doTuoi.trim().isEmpty() && !doTuoi.equals("all")) {
                try {
                    doTuoiInt = Integer.parseInt(doTuoi.trim());
                    hql.append(" AND p.doTuoi = :doTuoi");
                    params.add("doTuoi");
                    values.add(doTuoiInt);
                } catch (NumberFormatException e) {
                    model.addAttribute("error", "Invalid age rating: " + doTuoi);
                }
            }

            if (quocGia != null && !quocGia.trim().isEmpty() && !quocGia.equals("all")) {
                hql.append(" AND p.quocGia = :quocGia");
                params.add("quocGia");
                values.add(quocGia.trim());
            }

            if (sort != null && !sort.equals("all")) {
                if (sort.equals("ngayKhoiChieu_asc")) {
                    hql.append(" ORDER BY p.ngayKhoiChieu ASC");
                } else if (sort.equals("ngayKhoiChieu_desc")) {
                    hql.append(" ORDER BY p.ngayKhoiChieu DESC");
                }
            } else {
                hql.append(" ORDER BY p.ngayKhoiChieu DESC");
            }

            Query queryList = dbSession.createQuery(hql.toString());
            for (int i = 0; i < params.size(); i++) {
                queryList.setParameter(params.get(i), values.get(i));
            }

            List<PhimEntity> phimEntities = queryList.list();
            List<PhimModel> phimList = new ArrayList<>();
            for (PhimEntity entity : phimEntities) {
                phimList.add(new PhimModel(entity));
            }
            model.addAttribute("phimList", phimList);

            if (viewMaPhim != null) {
                PhimEntity phim = (PhimEntity) dbSession.get(PhimEntity.class, viewMaPhim);
                if (phim != null) {
                    PhimModel detailModel = new PhimModel(phim);
                    detailModel.setNgayKhoiChieuStr(new SimpleDateFormat("yyyy-MM-dd").format(phim.getNgayKhoiChieu()));
                    boolean hasShowtimes = hasShowtimes(dbSession, viewMaPhim);
                    boolean isEditable = true;
                    boolean isDeletable = !hasShowtimes;
                    if (hasShowtimes) {
                        isEditable = false;
                    } else {
                        Calendar todayCal = Calendar.getInstance(TimeZone.getTimeZone("GMT+07:00"));
                        todayCal.set(Calendar.HOUR_OF_DAY, 0);
                        todayCal.set(Calendar.MINUTE, 0);
                        todayCal.set(Calendar.SECOND, 0);
                        todayCal.set(Calendar.MILLISECOND, 0);
                        Date today = todayCal.getTime();
                        if (phim.getNgayKhoiChieu().compareTo(today) <= 0) {
                            isEditable = false;
                        }
                    }
                    model.addAttribute("isEditable", isEditable);
                    model.addAttribute("isDeletable", isDeletable);
                    model.addAttribute("phimModel", detailModel);
                    String theLoaiString = phim.getTheLoais().stream()
                            .map(TheLoaiEntity::getTenTheLoai)
                            .collect(Collectors.joining(", "));
                    String dvChinhString = phim.getDienViens().stream()
                            .map(DienVienEntity::getHoTen)
                            .collect(Collectors.joining(", "));
                    String dinhDangString = phim.getDinhDangs().stream()
                            .map(DinhDangEntity::getTenDinhDang)
                            .collect(Collectors.joining(", "));
                    model.addAttribute("theLoaiString", theLoaiString);
                    model.addAttribute("dvChinhString", dvChinhString);
                    model.addAttribute("dinhDangString", dinhDangString);
                    model.addAttribute("showDetailModal", true);
                } else {
                    model.addAttribute("error", "Movie with ID " + viewMaPhim + " not found");
                }
            }

            model.addAttribute("sort", sort != null ? sort : "all");
            model.addAttribute("theLoai", theLoai != null ? theLoai : "");
            model.addAttribute("dinhDang", dinhDang != null ? dinhDang : "");
            model.addAttribute("doTuoi", doTuoi != null ? doTuoi : "");
            model.addAttribute("quocGia", quocGia != null ? quocGia : "");
            model.addAttribute("searchTenPhim", searchTenPhim != null ? searchTenPhim : "");
            model.addAttribute("newMaPhim", newMaPhim);

            return "admin/movies_manager";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi lấy danh sách phim: " + e.getMessage());
            return "admin/movies_manager";
        } finally {
            dbSession.close();
        }
    }
    
    @Transactional
    @RequestMapping(value = "/movies/add", method = RequestMethod.POST)
    public String processAddMovie(
            @RequestParam("maPhim") String maPhim,
            @RequestParam("tenPhim") String tenPhim,
            @RequestParam("nhaSanXuat") String nhaSanXuat,
            @RequestParam("quocGia") String quocGia,
            @RequestParam(value = "doTuoi", required = false) String doTuoiStr,
            @RequestParam("daoDien") String daoDien,
            @RequestParam("ngayKhoiChieu") String ngayKhoiChieuStr,
            @RequestParam("thoiLuong") String thoiLuongStr,
            @RequestParam("urlTrailer") String urlTrailer,
            @RequestParam("giaVe") String giaVeStr,
            @RequestParam(value = "moTa", required = false) String moTa,
            @RequestParam("theLoai") String theLoai,
            @RequestParam("dinhDang") String dinhDang,
            @RequestParam("dvChinh") String dvChinh,
            @RequestParam("poster") MultipartFile poster,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        java.util.Date ngayKhoiChieu = null;

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/auth/login";
        }

        Session dbSession = sessionFactory.getCurrentSession();
        List<String> errors = new ArrayList<>();

        // Validate ngayKhoiChieu
        if (ngayKhoiChieuStr == null || ngayKhoiChieuStr.trim().isEmpty()) {
            errors.add("Ngày khởi chiếu không được để trống.");
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            try {
                ngayKhoiChieu = sdf.parse(ngayKhoiChieuStr);
                // Đặt ngày hiện tại về 00:00:00
                Calendar todayCal = Calendar.getInstance();
                todayCal.set(Calendar.HOUR_OF_DAY, 0);
                todayCal.set(Calendar.MINUTE, 0);
                todayCal.set(Calendar.SECOND, 0);
                todayCal.set(Calendar.MILLISECOND, 0);
                Date today = todayCal.getTime();

                if (ngayKhoiChieu.compareTo(today) < 0) {
                    errors.add("Ngày khởi chiếu không được là ngày trong quá khứ!");
                }
            } catch (Exception e) {
                errors.add("Ngày khởi chiếu không đúng định dạng (yyyy-MM-dd).");
            }
        }

        // Other validations (unchanged)
        if (tenPhim == null || tenPhim.trim().isEmpty()) {
            errors.add("Tên phim không được để trống.");
        }
        if (nhaSanXuat == null || nhaSanXuat.trim().isEmpty()) {
            errors.add("Nhà sản xuất không được để trống.");
        }
        if (quocGia == null || quocGia.trim().isEmpty()) {
            errors.add("Quốc gia không được để trống.");
        }
        int doTuoi = 0;
        if (doTuoiStr != null && !doTuoiStr.trim().isEmpty()) {
            try {
                doTuoi = Integer.parseInt(doTuoiStr);
                if (doTuoi < 0) {
                    errors.add("Độ tuổi phải là số không âm.");
                }
            } catch (NumberFormatException e) {
                errors.add("Độ tuổi phải là số hợp lệ.");
            }
        }
        if (daoDien == null || daoDien.trim().isEmpty()) {
            errors.add("Đạo diễn không được để trống.");
        }
        int thoiLuong = 0;
        if (thoiLuongStr != null && !thoiLuongStr.trim().isEmpty()) {
            try {
                thoiLuong = Integer.parseInt(thoiLuongStr);
                if (thoiLuong <= 0) {
                    errors.add("Thời lượng phải là số dương.");
                }
            } catch (NumberFormatException e) {
                errors.add("Thời lượng phải là số hợp lệ.");
            }
        } else {
            errors.add("Thời lượng không được để trống.");
        }
        if (urlTrailer == null || urlTrailer.trim().isEmpty()) {
            errors.add("URL trailer không được để trống.");
        }
        if (theLoai == null || theLoai.trim().isEmpty()) {
            errors.add("Thể loại không được để trống.");
        }
        if (dinhDang == null || dinhDang.trim().isEmpty()) {
            errors.add("Định dạng không được để trống.");
        }
        if (dvChinh == null || dvChinh.trim().isEmpty()) {
            errors.add("Diễn viên chính không được để trống.");
        }
        if (poster.isEmpty()) {
            errors.add("Vui lòng chọn file poster.");
        } else {
            String contentType = poster.getContentType();
            if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)) {
                errors.add("Poster phải là file jpg hoặc png.");
            }
            if (poster.getSize() > 5 * 1024 * 1024) {
                errors.add("Kích thước poster không được vượt quá 5MB.");
            }
        }
        BigDecimal giaVeBD = null;
        if (giaVeStr == null || giaVeStr.trim().isEmpty()) {
            errors.add("Giá vé không được để trống.");
        } else {
            try {
                String cleanedGiaVe = giaVeStr.replaceAll("[^0-9.]", "");
                giaVeBD = new BigDecimal(cleanedGiaVe).setScale(2, BigDecimal.ROUND_HALF_UP);
                if (giaVeBD.compareTo(BigDecimal.ZERO) <= 0) {
                    errors.add("Giá vé phải là số dương.");
                }
            } catch (NumberFormatException e) {
                errors.add("Giá vé không đúng định dạng số.");
            }
        }
        String duplicateError = checkDuplicatePhim(dbSession, tenPhim, maPhim);
        if (duplicateError != null) {
            errors.add(duplicateError);
        }

        if (!errors.isEmpty()) {
            PhimModel phimModel = new PhimModel();
            phimModel.setMaPhim(maPhim);
            phimModel.setTenPhim(tenPhim);
            phimModel.setNhaSanXuat(nhaSanXuat);
            phimModel.setQuocGia(quocGia);
            phimModel.setDoTuoi(doTuoi);
            phimModel.setDaoDien(daoDien);
            phimModel.setNgayKhoiChieuStr(ngayKhoiChieuStr); // Preserve input string
            phimModel.setThoiLuong(thoiLuong);
            phimModel.setUrlTrailer(urlTrailer);
            phimModel.setGiaVe(giaVeBD);
            phimModel.setMoTa(moTa);
            redirectAttributes.addFlashAttribute("error", String.join(" ", errors));
            redirectAttributes.addFlashAttribute("phimModel", phimModel);
            redirectAttributes.addFlashAttribute("theLoai", theLoai);
            redirectAttributes.addFlashAttribute("dinhDang", dinhDang);
            redirectAttributes.addFlashAttribute("dvChinh", dvChinh);
            return "redirect:/admin/movies";
        }

        try {
            String urlPoster = handlePosterUpload(poster, null);
            PhimEntity phim = new PhimEntity();
            phim.setMaPhim(maPhim);
            phim.setTenPhim(tenPhim);
            phim.setNhaSanXuat(nhaSanXuat);
            phim.setQuocGia(quocGia);
            phim.setDoTuoi(doTuoi);
            phim.setDaoDien(daoDien);
            phim.setNgayKhoiChieu(new java.sql.Date(ngayKhoiChieu.getTime()));
            phim.setThoiLuong(thoiLuong);
            phim.setUrlPoster(urlPoster);
            phim.setUrlTrailer(urlTrailer);
            phim.setGiaVe(giaVeBD);
            phim.setMoTa(moTa);

            manageTheLoaiEntities(dbSession, phim, theLoai);
            manageDienVienEntities(dbSession, phim, dvChinh);
            manageDinhDangEntities(dbSession, phim, dinhDang);

            dbSession.save(phim);
            redirectAttributes.addFlashAttribute("success", "Thêm phim " + tenPhim + " thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm phim: " + e.getMessage());
        }

        return "redirect:/admin/movies";
    }

    @Transactional
    @RequestMapping(value = "/movies/update", method = RequestMethod.POST)
    public String processUpdateMovie(
            @RequestParam("maPhim") String maPhim,
            @RequestParam("tenPhim") String tenPhim,
            @RequestParam("nhaSanXuat") String nhaSanXuat,
            @RequestParam("quocGia") String quocGia,
            @RequestParam("doTuoi") int doTuoi,
            @RequestParam("daoDien") String daoDien,
            @RequestParam("ngayKhoiChieu") String ngayKhoiChieuStr,
            @RequestParam("thoiLuong") int thoiLuong,
            @RequestParam("urlTrailer") String urlTrailer,
            @RequestParam("giaVe") String giaVeStr,
            @RequestParam(value = "moTa", required = false) String moTa,
            @RequestParam("theLoai") String theLoai,
            @RequestParam("dinhDang") String dinhDang,
            @RequestParam("dvChinh") String dvChinh,
            @RequestParam(value = "poster", required = false) MultipartFile poster,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "theLoaiFilter", required = false) String theLoaiFilter,
            @RequestParam(value = "dinhDangFilter", required = false) String dinhDangFilter,
            @RequestParam(value = "doTuoiFilter", required = false) String doTuoiFilter,
            @RequestParam(value = "quocGiaFilter", required = false) String quocGiaFilter,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/auth/login";
        }

        Session dbSession = sessionFactory.getCurrentSession();
        List<String> errors = new ArrayList<>();

        // Validate maPhim
        if (maPhim == null || maPhim.trim().isEmpty()) {
            errors.add("Mã phim không được để trống.");
        }

        // Fetch the existing movie
        PhimEntity phim = (PhimEntity) dbSession.get(PhimEntity.class, maPhim);
        if (phim == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy phim với mã " + maPhim);
            return "redirect:/admin/movies";
        }

        // Check for duplicate movie name
        String duplicateError = checkDuplicatePhim(dbSession, tenPhim, maPhim);
        if (duplicateError != null) {
            errors.add(duplicateError);
        }

        // Check if the movie is editable
        boolean hasShowtimes = hasShowtimes(dbSession, maPhim);
        Calendar todayCal = Calendar.getInstance(TimeZone.getTimeZone("GMT+07:00"));
        todayCal.set(Calendar.HOUR_OF_DAY, 0);
        todayCal.set(Calendar.MINUTE, 0);
        todayCal.set(Calendar.SECOND, 0);
        todayCal.set(Calendar.MILLISECOND, 0);
        Date today = todayCal.getTime();
        boolean isEditable = !(hasShowtimes || phim.getNgayKhoiChieu().compareTo(today) <= 0);
        if (!isEditable) {
            errors.add("Không thể chỉnh sửa phim vì đã có suất chiếu hoặc ngày khởi chiếu đã qua/hôm nay!");
        }

        // Validate inputs if editable
        java.util.Date ngayKhoiChieu = null;
        BigDecimal giaVeBD = null;
        if (isEditable) {
            // Validate ngayKhoiChieu
            if (ngayKhoiChieuStr == null || ngayKhoiChieuStr.trim().isEmpty()) {
                errors.add("Ngày khởi chiếu không được để trống.");
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false);
                try {
                    ngayKhoiChieu = sdf.parse(ngayKhoiChieuStr);
                    if (ngayKhoiChieu.compareTo(today) < 0) {
                        errors.add("Ngày khởi chiếu không được là ngày trong quá khứ!");
                    }
                    String ngayKhoiChieuError = checkNgayKhoiChieu(dbSession, maPhim, ngayKhoiChieu);
                    if (ngayKhoiChieuError != null) {
                        errors.add(ngayKhoiChieuError);
                    }
                } catch (Exception e) {
                    errors.add("Ngày khởi chiếu không đúng định dạng (yyyy-MM-dd).");
                }
            }

            // Other validations
            if (tenPhim == null || tenPhim.trim().isEmpty()) {
                errors.add("Tên phim không được để trống.");
            }
            if (nhaSanXuat == null || nhaSanXuat.trim().isEmpty()) {
                errors.add("Nhà sản xuất không được để trống.");
            }
            if (quocGia == null || quocGia.trim().isEmpty()) {
                errors.add("Quốc gia không được để trống.");
            }
            if (doTuoi < 0) {
                errors.add("Độ tuổi phải là số không âm.");
            }
            if (daoDien == null || daoDien.trim().isEmpty()) {
                errors.add("Đạo diễn không được để trống.");
            }
            if (thoiLuong <= 0) {
                errors.add("Thời lượng phải là số dương.");
            }
            if (urlTrailer == null || urlTrailer.trim().isEmpty()) {
                errors.add("URL trailer không được để trống.");
            }
            if (theLoai == null || theLoai.trim().isEmpty()) {
                errors.add("Thể loại không được để trống.");
            }
            if (dinhDang == null || dinhDang.trim().isEmpty()) {
                errors.add("Định dạng không được để trống.");
            }
            if (dvChinh == null || dvChinh.trim().isEmpty()) {
                errors.add("Diễn viên chính không được để trống.");
            }
            if (poster != null && !poster.isEmpty()) {
                String contentType = poster.getContentType();
                if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)) {
                    errors.add("Poster phải là file jpg hoặc png.");
                }
                if (poster.getSize() > 5 * 1024 * 1024) {
                    errors.add("Kích thước poster không được vượt quá 5MB.");
                }
            }
            if (giaVeStr == null || giaVeStr.trim().isEmpty()) {
                errors.add("Giá vé không được để trống.");
            } else {
                try {
                    String cleanedGiaVe = giaVeStr.replaceAll("[^0-9.]", "");
                    giaVeBD = new BigDecimal(cleanedGiaVe).setScale(2, BigDecimal.ROUND_HALF_UP);
                    if (giaVeBD.compareTo(BigDecimal.ZERO) <= 0) {
                        errors.add("Giá vé phải là số dương.");
                    }
                } catch (NumberFormatException e) {
                    errors.add("Giá vé không đúng định dạng số.");
                }
            }
         
        } else {
            ngayKhoiChieu = phim.getNgayKhoiChieu();
            giaVeBD = phim.getGiaVe();
        }

        if (!errors.isEmpty()) {
            PhimModel phimModel = new PhimModel();
            phimModel.setMaPhim(maPhim);
            phimModel.setTenPhim(tenPhim);
            phimModel.setNhaSanXuat(nhaSanXuat);
            phimModel.setQuocGia(quocGia);
            phimModel.setDoTuoi(doTuoi);
            phimModel.setDaoDien(daoDien);
            phimModel.setNgayKhoiChieuStr(ngayKhoiChieuStr);
            phimModel.setThoiLuong(thoiLuong);
            phimModel.setUrlTrailer(urlTrailer);
            phimModel.setGiaVe(giaVeBD);
            phimModel.setMoTa(moTa);
            model.addAttribute("error", String.join(" ", errors));
            model.addAttribute("phimModel", phimModel);
            model.addAttribute("theLoaiString", theLoai);
            model.addAttribute("dinhDangString", dinhDang);
            model.addAttribute("dvChinhString", dvChinh);
            model.addAttribute("isEditable", isEditable);
            model.addAttribute("isDeletable", !hasShowtimes);
            model.addAttribute("showDetailModal", true);
            // Truyền lại các tham số lọc và sắp xếp
            model.addAttribute("sort", sort != null ? sort : "ngayKhoiChieu_desc");
            model.addAttribute("theLoai", theLoaiFilter != null ? theLoaiFilter : "");
            model.addAttribute("dinhDang", dinhDangFilter != null ? dinhDangFilter : "");
            model.addAttribute("doTuoi", doTuoiFilter != null ? doTuoiFilter : "");
            model.addAttribute("quocGia", quocGiaFilter != null ? quocGiaFilter : "");
            populateCommonModelAttributes(model, dbSession, true);
            return "admin/movies_manager";
        }

        try {
            String oldPosterPath = phim.getUrlPoster();
            String newPosterPath = oldPosterPath;
            if (poster != null && !poster.isEmpty()) {
                newPosterPath = handlePosterUpload(poster, oldPosterPath);
                if (oldPosterPath != null && !oldPosterPath.equals(newPosterPath)) {
                    String oldFilePath = context.getRealPath("/resources/images/") + File.separator + oldPosterPath;
                    File oldFile = new File(oldFilePath);
                    if (oldFile.exists()) {
                        oldFile.delete();
                    }
                }
            }

            phim.setTenPhim(Normalizer.normalize(tenPhim.trim(), Normalizer.Form.NFC));
            phim.setNhaSanXuat(nhaSanXuat);
            phim.setQuocGia(quocGia);
            phim.setDoTuoi(doTuoi);
            phim.setDaoDien(daoDien);
            phim.setNgayKhoiChieu(new java.sql.Date(ngayKhoiChieu.getTime()));
            phim.setThoiLuong(thoiLuong);
            phim.setUrlPoster(newPosterPath);
            phim.setUrlTrailer(urlTrailer);
            phim.setGiaVe(giaVeBD != null ? giaVeBD : new BigDecimal(giaVeStr.replaceAll("[^0-9.]", "")).setScale(2, BigDecimal.ROUND_HALF_UP));
            phim.setMoTa(moTa);

            phim.getTheLoais().clear();
            phim.getDienViens().clear();
            phim.getDinhDangs().clear();

            manageTheLoaiEntities(dbSession, phim, theLoai);
            manageDienVienEntities(dbSession, phim, dvChinh);
            manageDinhDangEntities(dbSession, phim, dinhDang);

            dbSession.update(phim);
            redirectAttributes.addFlashAttribute("success", "Cập nhật phim " + tenPhim + " thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật phim: " + e.getMessage());
        }

        return "redirect:/admin/movies";
    }

    @Transactional
    @RequestMapping(value = "/movies/delete/{maPhim}", method = RequestMethod.GET)
    public String processDeleteMovie(
            @PathVariable("maPhim") String maPhim,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "theLoai", required = false) String theLoai,
            @RequestParam(value = "dinhDang", required = false) String dinhDang,
            @RequestParam(value = "doTuoi", required = false) String doTuoi,
            @RequestParam(value = "quocGia", required = false) String quocGia,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/auth/login";
        }

        Session dbSession = sessionFactory.getCurrentSession();
        try {
            PhimEntity phim = (PhimEntity) dbSession.get(PhimEntity.class, maPhim);
            if (phim == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy phim với mã " + maPhim);
            } else if (hasShowtimes(dbSession, maPhim)) {
                redirectAttributes.addFlashAttribute("error", "Không thể xóa phim vì đã có suất chiếu!");
            } else {
                dbSession.delete(phim);
                redirectAttributes.addFlashAttribute("success", "Xóa phim " + maPhim + " thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa phim: " + e.getMessage());
        }
        return "redirect:/admin/movies";
    }
}