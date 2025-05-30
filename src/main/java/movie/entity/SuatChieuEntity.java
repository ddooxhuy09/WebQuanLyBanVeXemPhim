package movie.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "SuatChieu")
public class SuatChieuEntity {
    @Id
    @Column(name = "MaSuatChieu")
    private String maSuatChieu;

    @Column(name = "MaPhim")
    private String maPhim;

    @Column(name = "MaPhongChieu")
    private String maPhongChieu;

    @Column(name = "NgayGioChieu")
    private Timestamp ngayGioChieu;

    @Column(name = "NgayGioKetThuc")
    private Timestamp ngayGioKetThuc;

    @Column(name = "LoaiManChieu")
    private String loaiManChieu;

    @ManyToOne
    @JoinColumn(name = "MaPhim", insertable = false, updatable = false)
    private PhimEntity phim;

    @ManyToOne
    @JoinColumn(name = "MaPhongChieu", insertable = false, updatable = false)
    private PhongChieuEntity phongChieu;

    @ManyToMany
    @JoinTable(
        name = "PhuThuSuatChieu", // Sửa tên bảng từ "SuatChieu_PhuThu" thành "PhuThuSuatChieu"
        joinColumns = @JoinColumn(name = "MaSuatChieu"),
        inverseJoinColumns = @JoinColumn(name = "MaPhuThu")
    )
    private Set<PhuThuEntity> phuThus = new HashSet<>();

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
    public PhimEntity getPhim() { return phim; }
    public void setPhim(PhimEntity phim) { this.phim = phim; }
    public PhongChieuEntity getPhongChieu() { return phongChieu; }
    public void setPhongChieu(PhongChieuEntity phongChieu) { this.phongChieu = phongChieu; }
    public Set<PhuThuEntity> getPhuThus() { return phuThus; }
    public void setPhuThus(Set<PhuThuEntity> phuThus) { this.phuThus = phuThus; }
}