package movie.model;

import movie.entity.PhimEntity;
import movie.entity.TheLoaiEntity;
import movie.entity.DienVienEntity;
import movie.entity.DinhDangEntity;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PhimModel {
    private String maPhim;
    private String tenPhim;
    private String nhaSanXuat;
    private String quocGia;
    private int doTuoi;
    private String daoDien;
    private Date ngayKhoiChieu;
    private int thoiLuong;
    private String urlPoster;
    private String urlTrailer;
    private BigDecimal giaVe;
    private String moTa;
    private Set<TheLoaiModel> theLoais = new HashSet<>();
    private Set<DienVienModel> dienViens = new HashSet<>();
    private Set<DinhDangModel> dinhDangs = new HashSet<>();

    // Constructors
    public PhimModel() {}

    public PhimModel(PhimEntity entity) {
        if (entity != null) {
            this.maPhim = entity.getMaPhim();
            this.tenPhim = entity.getTenPhim();
            this.nhaSanXuat = entity.getNhaSanXuat();
            this.quocGia = entity.getQuocGia();
            this.doTuoi = entity.getDoTuoi();
            this.daoDien = entity.getDaoDien();
            this.ngayKhoiChieu = entity.getNgayKhoiChieu();
            this.thoiLuong = entity.getThoiLuong();
            this.urlPoster = entity.getUrlPoster();
            this.urlTrailer = entity.getUrlTrailer();
            this.giaVe = entity.getGiaVe();
            this.moTa = entity.getMoTa();
            this.theLoais = entity.getTheLoais().stream()
                .map(TheLoaiModel::new)
                .collect(Collectors.toSet());
            this.dienViens = entity.getDienViens().stream()
                .map(DienVienModel::new)
                .collect(Collectors.toSet());
            this.dinhDangs = entity.getDinhDangs().stream()
                .map(DinhDangModel::new)
                .collect(Collectors.toSet());
        }
    }

    // Getters and Setters
    public String getMaPhim() { return maPhim; }
    public void setMaPhim(String maPhim) { this.maPhim = maPhim; }
    public String getTenPhim() { return tenPhim; }
    public void setTenPhim(String tenPhim) { this.tenPhim = tenPhim; }
    public String getNhaSanXuat() { return nhaSanXuat; }
    public void setNhaSanXuat(String nhaSanXuat) { this.nhaSanXuat = nhaSanXuat; }
    public String getQuocGia() { return quocGia; }
    public void setQuocGia(String quocGia) { this.quocGia = quocGia; }
    public int getDoTuoi() { return doTuoi; }
    public void setDoTuoi(int doTuoi) { this.doTuoi = doTuoi; }
    public String getDaoDien() { return daoDien; }
    public void setDaoDien(String daoDien) { this.daoDien = daoDien; }
    public Date getNgayKhoiChieu() { return ngayKhoiChieu; }
    public void setNgayKhoiChieu(Date ngayKhoiChieu) { this.ngayKhoiChieu = ngayKhoiChieu; }
    public int getThoiLuong() { return thoiLuong; }
    public void setThoiLuong(int thoiLuong) { this.thoiLuong = thoiLuong; }
    public String getUrlPoster() { return urlPoster; }
    public void setUrlPoster(String urlPoster) { this.urlPoster = urlPoster; }
    public String getUrlTrailer() { return urlTrailer; }
    public void setUrlTrailer(String urlTrailer) { this.urlTrailer = urlTrailer; }
    public BigDecimal getGiaVe() { return giaVe; }
    public void setGiaVe(BigDecimal giaVe) { this.giaVe = giaVe; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    public Set<TheLoaiModel> getTheLoais() { return theLoais; }
    public void setTheLoais(Set<TheLoaiModel> theLoais) { this.theLoais = theLoais; }
    public Set<DienVienModel> getDienViens() { return dienViens; }
    public void setDienViens(Set<DienVienModel> dienViens) { this.dienViens = dienViens; }
    public Set<DinhDangModel> getDinhDangs() { return dinhDangs; }
    public void setDinhDangs(Set<DinhDangModel> dinhDangs) { this.dinhDangs = dinhDangs; }

    // Derived getter for maTheLoais to maintain compatibility
    public List<String> getMaTheLoais() {
        return theLoais.stream()
            .map(TheLoaiModel::getMaTheLoai)
            .collect(Collectors.toList());
    }
    public List<String> getMaDienViens() {
        return dienViens.stream()
            .map(DienVienModel::getMaDienVien)
            .collect(Collectors.toList());
    }
    public List<String> getMaDinhDangs() {
        return dinhDangs.stream()
            .map(DinhDangModel::getMaDinhDang)
            .collect(Collectors.toList());
    }
    
 // Thêm vào class PhimModel
    private String ngayKhoiChieuStr;

    public String getNgayKhoiChieuStr() {
        if (ngayKhoiChieu != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.format(ngayKhoiChieu);
        }
        return "";
    }

    public void setNgayKhoiChieuStr(String ngayKhoiChieuStr) {
        if (ngayKhoiChieuStr != null && !ngayKhoiChieuStr.isEmpty()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                this.ngayKhoiChieu = dateFormat.parse(ngayKhoiChieuStr);
            } catch (Exception e) {
                this.ngayKhoiChieu = null; // Xử lý lỗi parse nếu cần
            }
        }
    }
}