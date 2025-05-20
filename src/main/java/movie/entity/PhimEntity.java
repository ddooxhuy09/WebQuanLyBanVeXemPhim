package movie.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Phim")
public class PhimEntity {
    @Id
    @Column(name = "MaPhim")
    private String maPhim;

    @Column(name = "TenPhim")
    private String tenPhim;

    @Column(name = "NhaSanXuat")
    private String nhaSanXuat;

    @Column(name = "QuocGia")
    private String quocGia;

    @Column(name = "DoTuoi")
    private int doTuoi;

    @Column(name = "DaoDien")
    private String daoDien;

    @Column(name = "NgayKhoiChieu")
    @Temporal(TemporalType.DATE)
    private Date ngayKhoiChieu;

    @Column(name = "ThoiLuong")
    private int thoiLuong;

    @Column(name = "UrlPoster")
    private String urlPoster;

    @Column(name = "UrlTrailer")
    private String urlTrailer;

    @Column(name = "GiaVe")
    private BigDecimal giaVe;

    @Column(name = "MoTa")
    private String moTa;

    @ManyToMany
    @JoinTable(
        name = "Phim_TheLoai",
        joinColumns = @JoinColumn(name = "MaPhim"),
        inverseJoinColumns = @JoinColumn(name = "MaTheLoai")
    )
    private Set<TheLoaiEntity> theLoais = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "Phim_DienVien",
        joinColumns = @JoinColumn(name = "MaPhim"),
        inverseJoinColumns = @JoinColumn(name = "MaDienVien")
    )
    private Set<DienVienEntity> dienViens = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "Phim_DinhDang",
        joinColumns = @JoinColumn(name = "MaPhim"),
        inverseJoinColumns = @JoinColumn(name = "MaDinhDang")
    )
    private Set<DinhDangEntity> dinhDangs = new HashSet<>();

    // Getters và Setters
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
    public Set<TheLoaiEntity> getTheLoais() { return theLoais; }
    public void setTheLoais(Set<TheLoaiEntity> theLoais) { this.theLoais = theLoais; }
    public Set<DienVienEntity> getDienViens() { return dienViens; }
    public void setDienViens(Set<DienVienEntity> dienViens) { this.dienViens = dienViens; }
    public Set<DinhDangEntity> getDinhDangs() { return dinhDangs; }
    public void setDinhDangs(Set<DinhDangEntity> dinhDangs) { this.dinhDangs = dinhDangs; }
}