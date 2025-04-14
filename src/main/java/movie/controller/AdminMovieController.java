package movie.controller;

import movie.entity.DienVienEntity;
import movie.entity.PhimEntity;
import movie.entity.TheLoaiEntity;
import movie.model.PhimModel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminMovieController {

    @Autowired
    private SessionFactory sessionFactory;

    @RequestMapping(value = "/movies", method = RequestMethod.GET)
    public String showMovieManager(Model model) {
        Session dbSession = sessionFactory.openSession();
        try {
            Query query = dbSession.createQuery("FROM PhimEntity ORDER BY maPhim DESC");
            query.setMaxResults(1);
            PhimEntity latestPhim = (PhimEntity) query.uniqueResult();

            String maPhim = latestPhim == null ? "P001" : String.format("P%03d", 
                Integer.parseInt(latestPhim.getMaPhim().substring(1)) + 1);

            Query allPhimQuery = dbSession.createQuery("FROM PhimEntity");
            List<PhimEntity> phimEntities = allPhimQuery.list();
            List<PhimModel> phimModels = new ArrayList<>();
            for (PhimEntity entity : phimEntities) {
                phimModels.add(new PhimModel(entity));
            }
            model.addAttribute("phimList", phimModels);

            Query theLoaiQuery = dbSession.createQuery("FROM TheLoaiEntity");
            List<TheLoaiEntity> theLoaiEntities = theLoaiQuery.list();
            model.addAttribute("theLoaiList", theLoaiEntities);

            Query dienVienQuery = dbSession.createQuery("FROM DienVienEntity");
            List<DienVienEntity> dienVienEntities = dienVienQuery.list();
            model.addAttribute("dienVienList", dienVienEntities);

            PhimModel phimModel = new PhimModel();
            phimModel.setMaPhim(maPhim);
            model.addAttribute("phimModel", phimModel);
            model.addAttribute("isEdit", false);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi lấy danh sách phim");
        } finally {
            dbSession.close();
        }
        return "admin/movies_manager";
    }

    @Transactional
    @RequestMapping(value = "/movies/add", method = RequestMethod.POST)
    public String processAddMovie(
            @RequestParam("maPhim") String maPhim,
            @RequestParam("tenPhim") String tenPhim,
            @RequestParam("nhaSX") String nhaSanXuat,
            @RequestParam("quocGia") String quocGia,
            @RequestParam("dinhDang") String dinhDang,
            @RequestParam("doTuoi") int doTuoi,
            @RequestParam("daoDien") String daoDien,
            @RequestParam("ngayKhoiChieu") String ngayKhoiChieuStr,
            @RequestParam("thoiLuong") int thoiLuong,
            @RequestParam("urlPoster") String urlPoster,
            @RequestParam("urlTrailer") String urlTrailer,
            @RequestParam("giaVe") BigDecimal giaVe,
            @RequestParam("theLoai") String theLoaiStr,
            @RequestParam("dvChinh") String dvChinhStr,
            @RequestParam(value = "moTa", required = false) String moTa, // Thêm moTa
            Model model) {
        try {
            Session dbSession = sessionFactory.getCurrentSession();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date ngayKhoiChieu = dateFormat.parse(ngayKhoiChieuStr);

            PhimEntity phim = new PhimEntity();
            phim.setMaPhim(maPhim);
            phim.setTenPhim(tenPhim);
            phim.setNhaSanXuat(nhaSanXuat);
            phim.setQuocGia(quocGia);
            phim.setDinhDang(dinhDang);
            phim.setDoTuoi(doTuoi);
            phim.setDaoDien(daoDien);
            phim.setNgayKhoiChieu(ngayKhoiChieu);
            phim.setThoiLuong(thoiLuong);
            phim.setUrlPoster(urlPoster);
            phim.setUrlTrailer(urlTrailer);
            phim.setGiaVe(giaVe);
            phim.setMoTa(moTa); // Lưu moTa

            Set<TheLoaiEntity> theLoais = new HashSet<>();
            if (!theLoaiStr.isEmpty()) {
                String[] theLoaiNames = theLoaiStr.split(",");
                for (String tenTheLoai : theLoaiNames) {
                    tenTheLoai = tenTheLoai.trim();
                    Query query = dbSession.createQuery("FROM TheLoaiEntity WHERE tenTheLoai = :tenTheLoai");
                    query.setParameter("tenTheLoai", tenTheLoai);
                    TheLoaiEntity theLoai = (TheLoaiEntity) query.uniqueResult();
                    if (theLoai == null) {
                        theLoai = new TheLoaiEntity();
                        theLoai.setMaTheLoai("TL" + System.currentTimeMillis() % 10000);
                        theLoai.setTenTheLoai(tenTheLoai);
                        dbSession.save(theLoai);
                    }
                    theLoais.add(theLoai);
                }
            }
            phim.setTheLoais(theLoais);

            Set<DienVienEntity> dienViens = new HashSet<>();
            if (!dvChinhStr.isEmpty()) {
                String[] dienVienNames = dvChinhStr.split(",");
                for (String hoTen : dienVienNames) {
                    hoTen = hoTen.trim();
                    Query query = dbSession.createQuery("FROM DienVienEntity WHERE hoTen = :hoTen");
                    query.setParameter("hoTen", hoTen);
                    DienVienEntity dienVien = (DienVienEntity) query.uniqueResult();
                    if (dienVien == null) {
                        dienVien = new DienVienEntity();
                        dienVien.setMaDienVien("DV" + System.currentTimeMillis() % 10000);
                        dienVien.setHoTen(hoTen);
                        dbSession.save(dienVien);
                    }
                    dienViens.add(dienVien);
                }
            }
            phim.setDienViens(dienViens);

            dbSession.save(phim);
            return "redirect:/admin/movies";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi thêm phim: " + e.getMessage());
            return "admin/movies_manager";
        }
    }

    @RequestMapping(value = "/movies/edit/{maPhim}", method = RequestMethod.GET)
    public String showEditMovieForm(@PathVariable("maPhim") String maPhim, Model model) {
        Session dbSession = sessionFactory.openSession();
        try {
            PhimEntity phim = (PhimEntity) dbSession.get(PhimEntity.class, maPhim);
            if (phim == null) {
                model.addAttribute("error", "Không tìm thấy phim với mã " + maPhim);
                return "redirect:/admin/movies";
            }

            Query query = dbSession.createQuery("FROM PhimEntity");
            List<PhimEntity> phimEntities = query.list();
            List<PhimModel> phimModels = new ArrayList<>();
            for (PhimEntity entity : phimEntities) {
                phimModels.add(new PhimModel(entity));
            }
            model.addAttribute("phimList", phimModels);

            Query theLoaiQuery = dbSession.createQuery("FROM TheLoaiEntity");
            List<TheLoaiEntity> theLoaiEntities = theLoaiQuery.list();
            model.addAttribute("theLoaiList", theLoaiEntities);

            Query dienVienQuery = dbSession.createQuery("FROM DienVienEntity");
            List<DienVienEntity> dienVienEntities = dienVienQuery.list();
            model.addAttribute("dienVienList", dienVienEntities);

            PhimModel phimModel = new PhimModel(phim);
            String theLoaiString = (phimModel.getMaTheLoais() != null && !phimModel.getMaTheLoais().isEmpty()) 
                ? String.join(",", phimModel.getMaTheLoais()) : "";
            String dvChinhString = (phimModel.getMaDienViens() != null && !phimModel.getMaDienViens().isEmpty()) 
                ? String.join(",", phimModel.getMaDienViens()) : "";
            model.addAttribute("theLoaiString", theLoaiString);
            model.addAttribute("dvChinhString", dvChinhString);

            model.addAttribute("phimModel", phimModel);
            model.addAttribute("isEdit", true);

            return "admin/movies_manager";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi lấy thông tin phim: " + e.getMessage());
            return "redirect:/admin/movies";
        } finally {
            dbSession.close();
        }
    }

    @Transactional
    @RequestMapping(value = "/movies/update", method = RequestMethod.POST)
    public String processUpdateMovie(
            @RequestParam("maPhim") String maPhim,
            @RequestParam("tenPhim") String tenPhim,
            @RequestParam("nhaSX") String nhaSanXuat,
            @RequestParam("quocGia") String quocGia,
            @RequestParam("dinhDang") String dinhDang,
            @RequestParam("doTuoi") int doTuoi,
            @RequestParam("daoDien") String daoDien,
            @RequestParam("ngayKhoiChieu") String ngayKhoiChieuStr,
            @RequestParam("thoiLuong") int thoiLuong,
            @RequestParam("urlPoster") String urlPoster,
            @RequestParam("urlTrailer") String urlTrailer,
            @RequestParam("giaVe") BigDecimal giaVe,
            @RequestParam("theLoai") String theLoaiStr,
            @RequestParam("dvChinh") String dvChinhStr,
            @RequestParam(value = "moTa", required = false) String moTa, // Thêm moTa
            Model model) {
        try {
            Session dbSession = sessionFactory.getCurrentSession();

            PhimEntity phim = (PhimEntity) dbSession.get(PhimEntity.class, maPhim);
            if (phim == null) {
                model.addAttribute("error", "Không tìm thấy phim với mã " + maPhim);
                return "redirect:/admin/movies";
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date ngayKhoiChieu = dateFormat.parse(ngayKhoiChieuStr);

            phim.setTenPhim(tenPhim);
            phim.setNhaSanXuat(nhaSanXuat);
            phim.setQuocGia(quocGia);
            phim.setDinhDang(dinhDang);
            phim.setDoTuoi(doTuoi);
            phim.setDaoDien(daoDien);
            phim.setNgayKhoiChieu(ngayKhoiChieu);
            phim.setThoiLuong(thoiLuong);
            phim.setUrlPoster(urlPoster);
            phim.setUrlTrailer(urlTrailer);
            phim.setGiaVe(giaVe);
            phim.setMoTa(moTa); // Cập nhật moTa

            phim.getTheLoais().clear();
            phim.getDienViens().clear();

            if (!theLoaiStr.isEmpty()) {
                String[] theLoaiNames = theLoaiStr.split(",");
                for (String tenTheLoai : theLoaiNames) {
                    tenTheLoai = tenTheLoai.trim();
                    Query query = dbSession.createQuery("FROM TheLoaiEntity WHERE tenTheLoai = :tenTheLoai");
                    query.setParameter("tenTheLoai", tenTheLoai);
                    TheLoaiEntity theLoai = (TheLoaiEntity) query.uniqueResult();
                    if (theLoai == null) {
                        theLoai = new TheLoaiEntity();
                        theLoai.setMaTheLoai("TL" + System.currentTimeMillis() % 10000);
                        theLoai.setTenTheLoai(tenTheLoai);
                        dbSession.save(theLoai);
                    }
                    phim.getTheLoais().add(theLoai);
                }
            }

            if (!dvChinhStr.isEmpty()) {
                String[] dienVienNames = dvChinhStr.split(",");
                for (String hoTen : dienVienNames) {
                    hoTen = hoTen.trim();
                    Query query = dbSession.createQuery("FROM DienVienEntity WHERE hoTen = :hoTen");
                    query.setParameter("hoTen", hoTen);
                    DienVienEntity dienVien = (DienVienEntity) query.uniqueResult();
                    if (dienVien == null) {
                        dienVien = new DienVienEntity();
                        dienVien.setMaDienVien("DV" + System.currentTimeMillis() % 10000);
                        dienVien.setHoTen(hoTen);
                        dbSession.save(dienVien);
                    }
                    phim.getDienViens().add(dienVien);
                }
            }

            dbSession.update(phim);
            return "redirect:/admin/movies";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi cập nhật phim: " + e.getMessage());
            return "admin/movies_manager";
        }
    }

    @Transactional
    @RequestMapping(value = "/movies/delete/{maPhim}", method = RequestMethod.GET)
    public String deleteMovie(@PathVariable("maPhim") String maPhim, Model model) {
        try {
            Session dbSession = sessionFactory.getCurrentSession();
            PhimEntity phim = (PhimEntity) dbSession.get(PhimEntity.class, maPhim);

            if (phim != null) {
                phim.getTheLoais().clear();
                phim.getDienViens().clear();
                dbSession.delete(phim);
            } else {
                model.addAttribute("error", "Không tìm thấy phim với mã " + maPhim);
            }
            return "redirect:/admin/movies";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi xóa phim: " + e.getMessage());
            return "redirect:/admin/movies";
        }
    }
}