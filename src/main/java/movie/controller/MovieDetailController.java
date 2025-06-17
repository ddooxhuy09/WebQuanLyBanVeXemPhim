package movie.controller;

import movie.entity.PhimEntity;
import movie.entity.RapChieuEntity;
import movie.entity.SuatChieuEntity;
import movie.entity.DienVienEntity;
import movie.entity.TheLoaiEntity;
import movie.model.PhimModel;
import movie.model.RapChieuModel;
import movie.model.SuatChieuModel;
import movie.model.DienVienModel;
import movie.model.TheLoaiModel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Query;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
@RequestMapping("/movie-detail")
public class MovieDetailController {

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    @RequestMapping(method = RequestMethod.GET)
    public String showMovieDetail(@RequestParam("id") String maPhim, Model model) {
        try {
            Session session = sessionFactory.getCurrentSession();
            
            PhimEntity phimEntity = (PhimEntity) session.get(PhimEntity.class, maPhim);
            if (phimEntity == null) {
                model.addAttribute("error", "Phim không tồn tại");
                return "user/movie-detail";
            }
            
            Hibernate.initialize(phimEntity.getTheLoais());
            Hibernate.initialize(phimEntity.getDienViens());

            List<TheLoaiModel> theLoaiModels = new ArrayList<>();
            for (TheLoaiEntity theLoai : phimEntity.getTheLoais()) {
                theLoaiModels.add(new TheLoaiModel(theLoai));
            }

            List<DienVienModel> dienVienModels = new ArrayList<>();
            for (DienVienEntity dienVien : phimEntity.getDienViens()) {
                dienVienModels.add(new DienVienModel(dienVien));
            }

            Query lichChieuQuery = session.createQuery(
                "SELECT DISTINCT sc, sc.phongChieu, sc.phongChieu.rapChieu, sc.ngayGioChieu " +
                "FROM SuatChieuEntity sc " +
                "WHERE sc.maPhim = :maPhim AND sc.ngayGioChieu >= :currentTime " +
                "ORDER BY sc.ngayGioChieu"
            );
            lichChieuQuery.setParameter("maPhim", maPhim);
            lichChieuQuery.setParameter("currentTime", new Date());
            List<Object[]> results = lichChieuQuery.list();

            Map<RapChieuModel, Set<SuatChieuModel>> lichChieuMap = new LinkedHashMap<>();
            for (Object[] row : results) {
                SuatChieuEntity scEntity = (SuatChieuEntity) row[0];
                RapChieuEntity rcEntity = (RapChieuEntity) row[2];
                RapChieuModel rcModel = new RapChieuModel(rcEntity);
                SuatChieuModel scModel = new SuatChieuModel(scEntity);
                lichChieuMap.computeIfAbsent(rcModel, k -> new TreeSet<>(
                    Comparator.comparing(SuatChieuModel::getNgayGioChieu)
                )).add(scModel);
            }

            Map<RapChieuModel, List<SuatChieuModel>> finalLichChieuMap = new LinkedHashMap<>();
            lichChieuMap.forEach((rap, suatChieus) ->
                finalLichChieuMap.put(rap, new ArrayList<>(suatChieus))
            );

            model.addAttribute("phim", new PhimModel(phimEntity));
            model.addAttribute("lichChieuMap", finalLichChieuMap);
            model.addAttribute("dienViens", dienVienModels);
            model.addAttribute("theLoais", theLoaiModels);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi lấy chi tiết phim: " + e.getMessage());
        }
        return "user/movie-detail";
    }
}