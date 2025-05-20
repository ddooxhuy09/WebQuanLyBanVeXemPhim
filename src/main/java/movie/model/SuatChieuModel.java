package movie.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class SuatChieuModel {
    private String maSuatChieu;
    private String maPhim;
    private String maPhongChieu;
    private Timestamp ngayGioChieu;
    private Timestamp ngayGioKetThuc;
    private String loaiManChieu;
    private List<String> maPhuThus;

    public SuatChieuModel() {
        this.maPhuThus = new ArrayList<>();
    }

    public SuatChieuModel(movie.entity.SuatChieuEntity entity) {
        if (entity != null) {
            this.maSuatChieu = entity.getMaSuatChieu();
            this.maPhim = entity.getMaPhim();
            this.maPhongChieu = entity.getMaPhongChieu();
            this.ngayGioChieu = entity.getNgayGioChieu();
            this.ngayGioKetThuc = entity.getNgayGioKetThuc();
            this.loaiManChieu = entity.getLoaiManChieu();
            this.maPhuThus = new ArrayList<>();
            if (entity.getPhuThus() != null) {
                for (movie.entity.PhuThuEntity phuThu : entity.getPhuThus()) {
                    this.maPhuThus.add(phuThu.getMaPhuThu());
                }
            }
        }
    }

    // Getters và Setters
    public String getMaSuatChieu() { return maSuatChieu; }
    public void setMaSuatChieu(String maSuatChieu) { this.maSuatChieu = maSuatChieu; }
    public String getMaPhim() { return maPhim; }
    public void setMaPhim(String maPhim) { this.maPhim = maPhim; }
    public String getMaPhongChieu() { return maPhongChieu; }
    public void setMaPhongChieu(String maPhongChieu) { this.maPhongChieu = maPhongChieu; }
    public Timestamp getNgayGioChieu() { return ngayGioChieu; }
    public void setNgayGioChieu(Timestamp ngayGioChieu) { this.ngayGioChieu = ngayGioChieu; }
    public Timestamp getNgayGioKetThuc() { return ngayGioKetThuc; }
    public void setNgayGioKetThuc(Timestamp ngayGioKetThuc) { this.ngayGioKetThuc = ngayGioKetThuc; }
    public String getLoaiManChieu() { return loaiManChieu; }
    public void setLoaiManChieu(String loaiManChieu) { this.loaiManChieu = loaiManChieu; }
    public List<String> getMaPhuThus() { return maPhuThus; }
    public void setMaPhuThus(List<String> maPhuThus) { this.maPhuThus = maPhuThus; }
}