package movie.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import movie.entity.DienVienEntity;
import movie.entity.PhimEntity;
import movie.entity.TheLoaiEntity;

public class PhimModel {
    private String maPhim;
    private String tenPhim;
    private String nhaSanXuat;
    private String quocGia;
    private String dinhDang;
    private int doTuoi;
    private String daoDien;
    private Date ngayKhoiChieu;
    private int thoiLuong;
    private String urlPoster;
    private String urlTrailer;
    private BigDecimal giaVe;
    private String moTa; // Thêm trường moTa
    private List<String> maTheLoais;
    private List<String> maDienViens;

    // Constructors
    public PhimModel() {
        this.maTheLoais = new ArrayList<>();
        this.maDienViens = new ArrayList<>();
    }

    public PhimModel(PhimEntity entity) {
        if (entity != null) {
            this.maPhim = entity.getMaPhim();
            this.tenPhim = entity.getTenPhim();
            this.nhaSanXuat = entity.getNhaSanXuat();
            this.quocGia = entity.getQuocGia();
            this.dinhDang = entity.getDinhDang();
            this.doTuoi = entity.getDoTuoi();
            this.daoDien = entity.getDaoDien();
            this.ngayKhoiChieu = entity.getNgayKhoiChieu();
            this.thoiLuong = entity.getThoiLuong();
            this.urlPoster = entity.getUrlPoster();
            this.urlTrailer = entity.getUrlTrailer();
            this.giaVe = entity.getGiaVe();
            this.moTa = entity.getMoTa(); // Ánh xạ MoTa
            this.maTheLoais = new ArrayList<>();
            this.maDienViens = new ArrayList<>();
            if (entity.getTheLoais() != null) {
                for (TheLoaiEntity theLoai : entity.getTheLoais()) {
                    this.maTheLoais.add(theLoai.getTenTheLoai());
                }
            }
            if (entity.getDienViens() != null) {
                for (DienVienEntity dienVien : entity.getDienViens()) {
                    this.maDienViens.add(dienVien.getHoTen());
                }
            }
        }
    }

    // Getters và Setters
    public String getMaPhim() { return maPhim; }
    public void setMaPhim(String maPhim) { this.maPhim = maPhim; }
    public String getTenPhim() { return tenPhim; }
    public void setTenPhim(String tenPhim) { this.tenPhim = tenPhim; }
    public String getNhaSanXuat() { return nhaSanXuat; }
    public void setNhaSanXuat(String nhaSanXuat) { this.nhaSanXuat = nhaSanXuat; }
    public String getQuocGia() { return quocGia; }
    public void setQuocGia(String quocGia) { this.quocGia = quocGia; }
    public String getDinhDang() { return dinhDang; }
    public void setDinhDang(String dinhDang) { this.dinhDang = dinhDang; }
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
    public void setUrlTrailer(String urlTrailer) { this.urlTrailer = urlTrailer; }
    public String getUrlTrailer() { return urlTrailer; }
    public BigDecimal getGiaVe() { return giaVe; }
    public void setGiaVe(BigDecimal giaVe) { this.giaVe = giaVe; }
    public String getMoTa() { return moTa; } // Getter cho moTa
    public void setMoTa(String moTa) { this.moTa = moTa; } // Setter cho moTa
    public List<String> getMaTheLoais() { return maTheLoais; }
    public void setMaTheLoais(List<String> maTheLoais) { this.maTheLoais = maTheLoais; }
    public List<String> getMaDienViens() { return maDienViens; }
    public void setMaDienViens(List<String> maDienViens) { this.maDienViens = maDienViens; }
}