package movie.controller;

import movie.entity.DienVienEntity;
import movie.entity.PhimEntity;
import movie.entity.SuatChieuEntity;
import movie.entity.TheLoaiEntity;
import movie.entity.DinhDangEntity;
import movie.model.PhimModel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Query;
import org.hibernate.exception.JDBCConnectionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminMovieController {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private ServletContext context;

    // Helper method to fetch all PhimModels
    private List<PhimModel> fetchPhimList(Session session) {
        Query query = session.createQuery("FROM PhimEntity");
        List<PhimEntity> phimEntities = query.list();
        List<PhimModel> phimModels = new ArrayList<>();
        for (PhimEntity entity : phimEntities) {
            phimModels.add(new PhimModel(entity));
        }
        return phimModels;
    }

    // Helper method to fetch TheLoaiEntities
    private List<TheLoaiEntity> fetchTheLoaiList(Session session) {
        Query query = session.createQuery("FROM TheLoaiEntity");
        return query.list();
    }

    // Helper method to fetch DienVienEntities
    private List<DienVienEntity> fetchDienVienList(Session session) {
        Query query = session.createQuery("FROM DienVienEntity");
        return query.list();
    }

    // Helper method to fetch DinhDangEntities
    private List<DinhDangEntity> fetchDinhDangList(Session session) {
        Query query = session.createQuery("FROM DinhDangEntity");
        return query.list();
    }

    // Helper method to populate common model attributes
    private void populateCommonModelAttributes(Model model, Session session, boolean isEdit) {
        model.addAttribute("phimList", fetchPhimList(session));
        model.addAttribute("theLoaiList", fetchTheLoaiList(session));
        model.addAttribute("dienVienList", fetchDienVienList(session));
        model.addAttribute("dinhDangList", fetchDinhDangList(session));
        model.addAttribute("isEdit", isEdit);
    }

    // Helper method to manage TheLoaiEntities for a PhimEntity
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

    // Helper method to manage DienVienEntities for a PhimEntity
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

    // Helper method to manage DinhDangEntities for a PhimEntity
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
                    dinhDang.setMaDinhDang("DD" + System.currentTimeMillis() % 10000);
                    dinhDang.setTenDinhDang(tenDinhDang);
                    session.save(dinhDang);
                }
                dinhDangs.add(dinhDang);
            }
        }
        phim.setDinhDangs(dinhDangs);
    }

    // Helper method to handle file upload and return the file name
    private String handlePosterUpload(MultipartFile poster, String oldPosterPath) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        PrintWriter logWriter = null;
        try {
            File logFile = new File("logs/app.log");
            System.err.println("Debug: Writing log to: " + logFile.getAbsolutePath());
            logWriter = new PrintWriter(new FileWriter(logFile, true));
            logWriter.println("[" + sdf.format(new Date()) + "] handlePosterUpload started");
            logWriter.flush();
        } catch (Exception e) {
            System.err.println("Debug: Error writing log: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            if (poster == null || poster.isEmpty()) {
                if (oldPosterPath == null) {
                    if (logWriter != null) {
                        logWriter.println("[" + sdf.format(new Date()) + "] No poster uploaded, old path is null");
                        logWriter.flush();
                    }
                    throw new IllegalArgumentException("Vui lòng chọn file poster!");
                }
                if (logWriter != null) {
                    logWriter.println("[" + sdf.format(new Date()) + "] No poster uploaded, using old path: " + oldPosterPath);
                    logWriter.flush();
                }
                return oldPosterPath;
            }

            // Use ServletContext to get the real path of the images directory
            String dirPath = context.getRealPath("/resources/images/");
            File dir = new File(dirPath);

            if (logWriter != null) {
                logWriter.println("[" + sdf.format(new Date()) + "] Directory path: " + dir.getAbsolutePath());
                logWriter.println("[" + sdf.format(new Date()) + "] Directory exists: " + dir.exists());
                logWriter.println("[" + sdf.format(new Date()) + "] Directory canWrite: " + dir.canWrite());
                logWriter.flush();
            }
            System.err.println("Debug: Poster directory: " + dir.getAbsolutePath());

            if (!dir.exists()) {
                if (logWriter != null) {
                    logWriter.println("[" + sdf.format(new Date()) + "] Creating directory: " + dir.getAbsolutePath());
                    logWriter.flush();
                }
                if (!dir.mkdirs()) {
                    if (logWriter != null) {
                        logWriter.println("[" + sdf.format(new Date()) + "] Failed to create directory");
                        logWriter.flush();
                    }
                    throw new RuntimeException("Không thể tạo thư mục: " + dir.getAbsolutePath());
                }
            }

            String fileName = System.currentTimeMillis() + "_" + poster.getOriginalFilename();
            String filePath = dirPath + File.separator + fileName;
            File destination = new File(filePath);

            if (logWriter != null) {
                logWriter.println("[" + sdf.format(new Date()) + "] File path: " + filePath);
                logWriter.println("[" + sdf.format(new Date()) + "] File canWrite: " + destination.getParentFile().canWrite());
                logWriter.flush();
            }

            if (!destination.getParentFile().canWrite()) {
                if (logWriter != null) {
                    logWriter.println("[" + sdf.format(new Date()) + "] No write permission for directory");
                    logWriter.flush();
                }
                throw new RuntimeException("Không có quyền ghi file vào thư mục: " + dir.getAbsolutePath());
            }

            poster.transferTo(destination);
            if (logWriter != null) {
                logWriter.println("[" + sdf.format(new Date()) + "] Poster uploaded successfully: " + fileName);
                logWriter.flush();
            }
            System.err.println("Debug: Poster uploaded to: " + filePath);

            return fileName;
        } catch (Exception e) {
            if (logWriter != null) {
                logWriter.println("[" + sdf.format(new Date()) + "] Error in handlePosterUpload: " + e.getMessage());
                e.printStackTrace(logWriter);
                logWriter.flush();
            }
            throw e;
        } finally {
            if (logWriter != null) {
                logWriter.close();
            }
        }
    }

    // Helper method to check if a movie has scheduled showtimes
    private boolean hasShowtimes(Session session, String maPhim) {
        Query query = session.createQuery("FROM SuatChieuEntity WHERE maPhim = :maPhim");
        query.setParameter("maPhim", maPhim);
        return !query.list().isEmpty();
    }

    // Helper method to check for overlapping showtimes when increasing duration
    private String checkShowtimeOverlap(Session session, String maPhim, int newThoiLuong, int oldThoiLuong, Date ngayKhoiChieu) {
        if (newThoiLuong <= oldThoiLuong) {
            return null;
        }

        Query query = session.createQuery("FROM SuatChieuEntity WHERE maPhim = :maPhim");
        query.setParameter("maPhim", maPhim);
        List<SuatChieuEntity> suatChieus = query.list();

        for (SuatChieuEntity suatChieu : suatChieus) {
            long startTime = suatChieu.getNgayGioChieu().getTime();
            long newEndTime = startTime + (newThoiLuong * 60 * 1000L);

            Query overlapQuery = session.createQuery(
                "FROM SuatChieuEntity WHERE maPhongChieu = :maPhongChieu AND maSuatChieu != :maSuatChieu " +
                "AND ngayGioChieu < :newEndTime AND ngayGioKetThuc > :startTime"
            );
            overlapQuery.setParameter("maPhongChieu", suatChieu.getMaPhongChieu());
            overlapQuery.setParameter("maSuatChieu", suatChieu.getMaSuatChieu());
            overlapQuery.setParameter("newEndTime", new java.sql.Timestamp(newEndTime));
            overlapQuery.setParameter("startTime", suatChieu.getNgayGioChieu());
            List<SuatChieuEntity> overlapping = overlapQuery.list();

            if (!overlapping.isEmpty()) {
                return "Tăng thời lượng phim sẽ gây trùng với suất chiếu khác trong cùng phòng!";
            }
        }
        return null;
    }

    // Helper method to check if ngayKhoiChieu is valid with existing showtimes
    private String checkNgayKhoiChieu(Session session, String maPhim, Date newNgayKhoiChieu) {
        Query query = session.createQuery(
            "FROM SuatChieuEntity WHERE maPhim = :maPhim AND ngayGioChieu < :newNgayKhoiChieu"
        );
        query.setParameter("maPhim", maPhim);
        query.setParameter("newNgayKhoiChieu", new java.sql.Timestamp(newNgayKhoiChieu.getTime()));
        List<SuatChieuEntity> suatChieus = query.list();

        if (!suatChieus.isEmpty()) {
            return "Ngày khởi chiếu mới không hợp lệ vì phim đã có suất chiếu trước ngày này!";
        }
        return null;
    }

    // Helper method to check for duplicate tenPhim
    private String checkDuplicatePhim(Session session, String tenPhim, String maPhim) {
        Query query = session.createQuery("FROM PhimEntity WHERE tenPhim = :tenPhim AND maPhim != :maPhim");
        query.setParameter("tenPhim", tenPhim);
        query.setParameter("maPhim", maPhim);
        if (!query.list().isEmpty()) {
            return "Tên phim đã tồn tại!";
        }
        return null;
    }

    // Helper method to check if ngayKhoiChieu is after current date
    private String checkNgayKhoiChieuAfterCurrentDate(Date ngayKhoiChieu) {
        Date currentDate = new Date();
        if (ngayKhoiChieu.before(currentDate)) {
            return "Ngày khởi chiếu phải sau ngày hiện tại!";
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
            HttpSession session,
            Model model) {
        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin/auth/login";
        }

        Session dbSession = sessionFactory.openSession();
        try {
            // Lấy mã phim mới nhất để tạo mã mới
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

            // Xây dựng câu truy vấn HQL động
            StringBuilder hql = new StringBuilder("FROM PhimEntity p WHERE 1=1");
            List<String> params = new ArrayList<>();
            List<Object> values = new ArrayList<>();

            // Lọc theo thể loại
            if (theLoai != null && !theLoai.trim().isEmpty() && !theLoai.equals("all")) {
                hql.append(" AND EXISTS (SELECT 1 FROM p.theLoais tl WHERE tl.tenTheLoai = :theLoai)");
                params.add("theLoai");
                values.add(theLoai.trim());
            }

            // Lọc theo định dạng
            if (dinhDang != null && !dinhDang.trim().isEmpty() && !dinhDang.equals("all")) {
                hql.append(" AND EXISTS (SELECT 1 FROM p.dinhDangs dd WHERE dd.tenDinhDang = :dinhDang)");
                params.add("dinhDang");
                values.add(dinhDang.trim());
            }

            // Lọc theo độ tuổi
            Integer doTuoiInt = null;
            if (doTuoi != null && !doTuoi.trim().isEmpty() && !doTuoi.equals("all")) {
                try {
                    doTuoiInt = Integer.parseInt(doTuoi.trim());
                    hql.append(" AND p.doTuoi = :doTuoi");
                    params.add("doTuoi");
                    values.add(doTuoiInt);
                } catch (NumberFormatException e) {
                    // Bỏ qua nếu không phải số, mặc định không lọc
                    model.addAttribute("error", "Độ tuổi không hợp lệ: " + doTuoi);
                }
            }

            // Lọc theo quốc gia
            if (quocGia != null && !quocGia.trim().isEmpty() && !quocGia.equals("all")) {
                hql.append(" AND p.quocGia = :quocGia");
                params.add("quocGia");
                values.add(quocGia.trim());
            }

            // Sắp xếp
            if (sort != null && !sort.equals("all")) {
                if (sort.equals("ngayKhoiChieu_asc")) {
                    hql.append(" ORDER BY p.ngayKhoiChieu ASC");
                } else if (sort.equals("ngayKhoiChieu_desc")) {
                    hql.append(" ORDER BY p.ngayKhoiChieu DESC");
                }
            } else {
                hql.append(" ORDER BY p.ngayKhoiChieu DESC"); // Mặc định
            }

            // Tạo truy vấn
            Query queryList = dbSession.createQuery(hql.toString());
            for (int i = 0; i < params.size(); i++) {
                queryList.setParameter(params.get(i), values.get(i));
            }

            // Lấy danh sách phim
            List<PhimEntity> phimEntities = queryList.list();
            List<PhimModel> phimList = new ArrayList<>();
            for (PhimEntity entity : phimEntities) {
                phimList.add(new PhimModel(entity));
            }
            model.addAttribute("phimList", phimList);

            // Handle view detail
            if (viewMaPhim != null) {
                PhimEntity phim = (PhimEntity) dbSession.get(PhimEntity.class, viewMaPhim);
                if (phim != null) {
                    PhimModel detailModel = new PhimModel(phim);
                    detailModel.setNgayKhoiChieuStr(new SimpleDateFormat("yyyy-MM-dd").format(phim.getNgayKhoiChieu()));
                    String theLoaiString = detailModel.getMaTheLoais().stream().collect(Collectors.joining(","));
                    String dvChinhString = detailModel.getMaDienViens().stream().collect(Collectors.joining(","));
                    String dinhDangString = detailModel.getMaDinhDangs().stream().collect(Collectors.joining(","));
                    model.addAttribute("phimModel", detailModel);
                    model.addAttribute("theLoaiString", theLoaiString);
                    model.addAttribute("dvChinhString", dvChinhString);
                    model.addAttribute("dinhDangString", dinhDangString);
                    model.addAttribute("showDetailModal", true);
                } else {
                    model.addAttribute("error", "Không tìm thấy phim với mã " + viewMaPhim);
                }
            }

            // Add filter parameters to model for persistence
            model.addAttribute("sort", sort != null ? sort : "all");
            model.addAttribute("theLoai", theLoai != null ? theLoai : "all");
            model.addAttribute("dinhDang", dinhDang != null ? dinhDang : "all");
            model.addAttribute("doTuoi", doTuoi != null ? doTuoi : "all");
            model.addAttribute("quocGia", quocGia != null ? quocGia : "all");
            model.addAttribute("newMaPhim", newMaPhim);

            return "admin/movies_manager";
        } catch (Exception e) {
            e.printStackTrace();
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
            @RequestParam("poster") MultipartFile poster,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        // Khởi tạo log file
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        PrintWriter logWriter = null;
        try {
            File logFile = new File("logs/app.log");
            System.err.println("Debug: Writing log to: " + logFile.getAbsolutePath());
            logWriter = new PrintWriter(new FileWriter(logFile, true));
            logWriter.println("[" + sdf.format(new Date()) + "] Received data: maPhim=" + maPhim +
                    ", tenPhim=" + tenPhim +
                    ", nhaSanXuat=" + nhaSanXuat +
                    ", quocGia=" + quocGia +
                    ", doTuoi=" + doTuoi +
                    ", daoDien=" + daoDien +
                    ", ngayKhoiChieuStr=" + ngayKhoiChieuStr +
                    ", thoiLuong=" + thoiLuong +
                    ", urlTrailer=" + urlTrailer +
                    ", giaVeStr=" + giaVeStr +
                    ", moTa=" + moTa +
                    ", theLoai=" + theLoai +
                    ", dinhDang=" + dinhDang +
                    ", dvChinh=" + dvChinh +
                    ", posterIsEmpty=" + poster.isEmpty());
            logWriter.flush();
        } catch (Exception e) {
            System.err.println("Debug: Error writing log: " + e.getMessage());
            e.printStackTrace();
        }

        // Kiểm tra session
        if (session.getAttribute("loggedInAdmin") == null) {
            if (logWriter != null) {
                logWriter.println("[" + sdf.format(new Date()) + "] Unauthorized access attempt");
                logWriter.flush();
                logWriter.close();
            }
            return "redirect:/admin/auth/login";
        }

        Session dbSession = sessionFactory.getCurrentSession();
        List<String> errors = new ArrayList<>();

        // Lưu dữ liệu để hiển thị lại nếu có lỗi
        model.addAttribute("maPhim", maPhim);
        model.addAttribute("tenPhim", tenPhim);
        model.addAttribute("nhaSanXuat", nhaSanXuat);
        model.addAttribute("quocGia", quocGia);
        model.addAttribute("doTuoi", doTuoi);
        model.addAttribute("daoDien", daoDien);
        model.addAttribute("ngayKhoiChieu", ngayKhoiChieuStr);
        model.addAttribute("thoiLuong", thoiLuong);
        model.addAttribute("urlTrailer", urlTrailer);
        model.addAttribute("giaVe", giaVeStr);
        model.addAttribute("moTa", moTa);
        model.addAttribute("theLoai", theLoai);
        model.addAttribute("dinhDang", dinhDang);
        model.addAttribute("dvChinh", dvChinh);

        // Validation
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
        if (ngayKhoiChieuStr == null || ngayKhoiChieuStr.trim().isEmpty()) {
            errors.add("Ngày khởi chiếu không được để trống.");
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

        // Validate và parse giá vé
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

        // Kiểm tra trùng tên phim
        String duplicateError = checkDuplicatePhim(dbSession, tenPhim, maPhim);
        if (duplicateError != null) {
            errors.add(duplicateError);
        }

        // Parse và kiểm tra ngày khởi chiếu
        Date ngayKhoiChieu = null;
        if (ngayKhoiChieuStr != null && !ngayKhoiChieuStr.trim().isEmpty()) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                dateFormat.setLenient(false);
                ngayKhoiChieu = dateFormat.parse(ngayKhoiChieuStr);
                String ngayKhoiChieuError = checkNgayKhoiChieuAfterCurrentDate(ngayKhoiChieu);
                if (ngayKhoiChieuError != null) {
                    errors.add(ngayKhoiChieuError);
                }
            } catch (Exception e) {
                errors.add("Ngày khởi chiếu không đúng định dạng (yyyy-MM-dd).");
            }
        }

        // Nếu có lỗi, trả về trang JSP
        if (!errors.isEmpty()) {
            if (logWriter != null) {
                logWriter.println("[" + sdf.format(new Date()) + "] Validation errors: " + String.join("; ", errors));
                logWriter.flush();
            }
            model.addAttribute("error", String.join(" ", errors));
            populateCommonModelAttributes(model, dbSession, false);
            if (logWriter != null) {
                logWriter.close();
            }
            return "admin/movies_manager";
        }

        // Xử lý poster và lưu phim
        try {
            if (logWriter != null) {
                logWriter.println("[" + sdf.format(new Date()) + "] Uploading poster...");
                logWriter.flush();
            }
            String urlPoster = handlePosterUpload(poster, null);
            if (logWriter != null) {
                logWriter.println("[" + sdf.format(new Date()) + "] Poster uploaded: " + urlPoster);
                logWriter.flush();
            }

            // Tạo và lưu phim
            PhimEntity phim = new PhimEntity();
            phim.setMaPhim(maPhim);
            phim.setTenPhim(tenPhim);
            phim.setNhaSanXuat(nhaSanXuat);
            phim.setQuocGia(quocGia);
            phim.setDoTuoi(doTuoi);
            phim.setDaoDien(daoDien);
            phim.setNgayKhoiChieu(ngayKhoiChieu);
            phim.setThoiLuong(thoiLuong);
            phim.setUrlPoster(urlPoster);
            phim.setUrlTrailer(urlTrailer);
            phim.setGiaVe(giaVeBD);
            phim.setMoTa(moTa);

            if (logWriter != null) {
                logWriter.println("[" + sdf.format(new Date()) + "] Managing relationships...");
                logWriter.flush();
            }
            manageTheLoaiEntities(dbSession, phim, theLoai);
            manageDienVienEntities(dbSession, phim, dvChinh);
            manageDinhDangEntities(dbSession, phim, dinhDang);
            if (logWriter != null) {
                logWriter.println("[" + sdf.format(new Date()) + "] Relationships managed");
                logWriter.flush();
            }

            if (logWriter != null) {
                logWriter.println("[" + sdf.format(new Date()) + "] Saving phim: " + maPhim);
                logWriter.flush();
            }
            dbSession.save(phim);
            if (logWriter != null) {
                logWriter.println("[" + sdf.format(new Date()) + "] Phim saved successfully");
                logWriter.flush();
            }

            redirectAttributes.addFlashAttribute("success", "Thêm phim " + tenPhim + " thành công!");
        } catch (Exception e) {
            if (logWriter != null) {
                logWriter.println("[" + sdf.format(new Date()) + "] Error adding phim: " + e.getMessage());
                e.printStackTrace(logWriter);
                logWriter.flush();
            }
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm phim: " + e.getMessage());
        } finally {
            if (logWriter != null) {
                logWriter.close();
            }
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
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        // Khởi tạo log file
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        PrintWriter logWriter = null;
        try {
            File logFile = new File("logs/app.log");
            System.err.println("Debug: Writing log to: " + logFile.getAbsolutePath());
            logWriter = new PrintWriter(new FileWriter(logFile, true));
            logWriter.println("[" + sdf.format(new Date()) + "] Received data: maPhim=" + maPhim +
                    ", tenPhim=" + tenPhim +
                    ", nhaSanXuat=" + nhaSanXuat +
                    ", quocGia=" + quocGia +
                    ", doTuoi=" + doTuoi +
                    ", daoDien=" + daoDien +
                    ", ngayKhoiChieuStr=" + ngayKhoiChieuStr +
                    ", thoiLuong=" + thoiLuong +
                    ", urlTrailer=" + urlTrailer +
                    ", giaVeStr=" + giaVeStr +
                    ", moTa=" + moTa +
                    ", theLoai=" + theLoai +
                    ", dinhDang=" + dinhDang +
                    ", dvChinh=" + dvChinh +
                    ", posterIsEmpty=" + (poster == null || poster.isEmpty()));
            logWriter.flush();
        } catch (Exception e) {
            System.err.println("Debug: Error writing log: " + e.getMessage());
            e.printStackTrace();
        }

        // Kiểm tra session
        if (session.getAttribute("loggedInAdmin") == null) {
            if (logWriter != null) {
                logWriter.println("[" + sdf.format(new Date()) + "] Unauthorized access attempt");
                logWriter.flush();
                logWriter.close();
            }
            return "redirect:/admin/auth/login";
        }

        Session dbSession = sessionFactory.getCurrentSession();
        List<String> errors = new ArrayList<>();

        // Lưu dữ liệu để hiển thị lại nếu có lỗi
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
        phimModel.setGiaVe(giaVeStr != null ? new BigDecimal(giaVeStr.replaceAll("[^0-9.]", "")) : BigDecimal.ZERO);
        phimModel.setMoTa(moTa);

        // Validation
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
        if (ngayKhoiChieuStr == null || ngayKhoiChieuStr.trim().isEmpty()) {
            errors.add("Ngày khởi chiếu không được để trống.");
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

        // Validate và parse giá vé
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

        // Kiểm tra trùng tên phim
        String duplicateError = checkDuplicatePhim(dbSession, tenPhim, maPhim);
        if (duplicateError != null) {
            errors.add(duplicateError);
        }

        // Parse và kiểm tra ngày khởi chiếu
        Date ngayKhoiChieu = null;
        if (ngayKhoiChieuStr != null && !ngayKhoiChieuStr.trim().isEmpty()) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                dateFormat.setLenient(false);
                ngayKhoiChieu = dateFormat.parse(ngayKhoiChieuStr);
                String ngayKhoiChieuError = checkNgayKhoiChieuAfterCurrentDate(ngayKhoiChieu);
                if (ngayKhoiChieuError != null) {
                    errors.add(ngayKhoiChieuError);
                }
            } catch (Exception e) {
                errors.add("Ngày khởi chiếu không đúng định dạng (yyyy-MM-dd).");
            }
        }

        // Nếu có lỗi, trả về trang JSP và hiển thị lại modal
        if (!errors.isEmpty()) {
            if (logWriter != null) {
                logWriter.println("[" + sdf.format(new Date()) + "] Validation errors: " + String.join("; ", errors));
                logWriter.flush();
            }
            model.addAttribute("error", String.join(" ", errors));
            model.addAttribute("phimModel", phimModel);
            model.addAttribute("theLoaiString", theLoai);
            model.addAttribute("dinhDangString", dinhDang);
            model.addAttribute("dvChinhString", dvChinh);
            model.addAttribute("showDetailModal", true); // Mở lại modal
            populateCommonModelAttributes(model, dbSession, true); // true để edit mode
            if (logWriter != null) {
                logWriter.close();
            }
            return "admin/movies_manager";
        }

        // Xử lý update phim
        try {
            if (logWriter != null) {
                logWriter.println("[" + sdf.format(new Date()) + "] Updating phim: " + maPhim);
                logWriter.flush();
            }

            // Lấy phim hiện tại
            PhimEntity phim = (PhimEntity) dbSession.get(PhimEntity.class, maPhim);
            if (phim == null) {
                if (logWriter != null) {
                    logWriter.println("[" + sdf.format(new Date()) + "] Phim not found: " + maPhim);
                    logWriter.flush();
                }
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy phim với mã " + maPhim);
                return "redirect:/admin/movies";
            }

            // Validate sensitive fields
            String thoiLuongError = checkShowtimeOverlap(dbSession, maPhim, thoiLuong, phim.getThoiLuong(), ngayKhoiChieu);
            if (thoiLuongError != null) {
                if (logWriter != null) {
                    logWriter.println("[" + sdf.format(new Date()) + "] Error: " + thoiLuongError);
                    logWriter.flush();
                }
                redirectAttributes.addFlashAttribute("error", thoiLuongError);
                return "redirect:/admin/movies";
            }

            String ngayKhoiChieuError = checkNgayKhoiChieu(dbSession, maPhim, ngayKhoiChieu);
            if (ngayKhoiChieuError != null) {
                if (logWriter != null) {
                    logWriter.println("[" + sdf.format(new Date()) + "] Error: " + ngayKhoiChieuError);
                    logWriter.flush();
                }
                redirectAttributes.addFlashAttribute("error", ngayKhoiChieuError);
                return "redirect:/admin/movies";
            }

            // Xử lý poster
            String oldPosterPath = phim.getUrlPoster();
            String newPosterPath = oldPosterPath; // Mặc định giữ hình cũ nếu không upload mới
            if (poster != null && !poster.isEmpty()) {
                if (logWriter != null) {
                    logWriter.println("[" + sdf.format(new Date()) + "] Uploading new poster...");
                    logWriter.flush();
                }
                newPosterPath = handlePosterUpload(poster, oldPosterPath);

                // Xóa hình cũ nếu khác với hình mới
                if (oldPosterPath != null && !oldPosterPath.equals(newPosterPath)) {
                    String oldFilePath = context.getRealPath("/resources/images/") + File.separator + oldPosterPath;
                    File oldFile = new File(oldFilePath);
                    if (oldFile.exists()) {
                        if (logWriter != null) {
                            logWriter.println("[" + sdf.format(new Date()) + "] Deleting old poster: " + oldFilePath);
                            logWriter.flush();
                        }
                        if (oldFile.delete()) {
                            if (logWriter != null) {
                                logWriter.println("[" + sdf.format(new Date()) + "] Old poster deleted successfully");
                                logWriter.flush();
                            }
                        } else {
                            if (logWriter != null) {
                                logWriter.println("[" + sdf.format(new Date()) + "] Failed to delete old poster");
                                logWriter.flush();
                            }
                        }
                    }
                }
            }

            // Cập nhật phim
            phim.setTenPhim(tenPhim);
            phim.setNhaSanXuat(nhaSanXuat);
            phim.setQuocGia(quocGia);
            phim.setDoTuoi(doTuoi);
            phim.setDaoDien(daoDien);
            phim.setNgayKhoiChieu(ngayKhoiChieu);
            phim.setThoiLuong(thoiLuong);
            phim.setUrlPoster(newPosterPath);
            phim.setUrlTrailer(urlTrailer);
            phim.setGiaVe(giaVeBD);
            phim.setMoTa(moTa);

            // Xóa các quan hệ cũ và thêm mới
            phim.getTheLoais().clear();
            phim.getDienViens().clear();
            phim.getDinhDangs().clear();

            if (logWriter != null) {
                logWriter.println("[" + sdf.format(new Date()) + "] Managing relationships...");
                logWriter.flush();
            }
            manageTheLoaiEntities(dbSession, phim, theLoai);
            manageDienVienEntities(dbSession, phim, dvChinh);
            manageDinhDangEntities(dbSession, phim, dinhDang);
            if (logWriter != null) {
                logWriter.println("[" + sdf.format(new Date()) + "] Relationships managed");
                logWriter.flush();
            }

            dbSession.update(phim);
            if (logWriter != null) {
                logWriter.println("[" + sdf.format(new Date()) + "] Phim updated successfully: " + maPhim);
                logWriter.flush();
            }

            redirectAttributes.addFlashAttribute("success", "Cập nhật phim " + tenPhim + " thành công!");
        } catch (Exception e) {
            if (logWriter != null) {
                logWriter.println("[" + sdf.format(new Date()) + "] Error updating phim: " + e.getMessage());
                e.printStackTrace(logWriter);
                logWriter.flush();
            }
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật phim: " + e.getMessage());
        } finally {
            if (logWriter != null) {
                logWriter.close();
            }
        }

        return "redirect:/admin/movies";
    }

    @Transactional
    @RequestMapping(value = "/movies/delete/{maPhim}", method = RequestMethod.GET)
    public String deleteMovie(
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
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa phim: " + e.getMessage());
        }

        return "redirect:/admin/movies";
    }
}