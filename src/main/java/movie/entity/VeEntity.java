package movie.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "Ve")
public class VeEntity {
    @Id
    @Column(name = "MaVe", length = 10)
    private String maVe;

    @Column(name = "MaKhachHang", length = 10)
    private String maKhachHang;

    @Column(name = "MaSuatChieu", length = 10)
    private String maSuatChieu;

    @Column(name = "MaGhe", length = 10)
    private String maGhe;

    @Column(name = "GiaVe")
    private BigDecimal giaVe;

    @Column(name = "NgayMua")
    @Temporal(TemporalType.DATE)
    private Date ngayMua;

    @Column(name = "TrangThai", columnDefinition = "nvarchar(20)")
    private String trangThai;

    @ManyToOne
    @JoinColumn(name = "MaKhachHang", referencedColumnName = "MaKhachHang", insertable = false, updatable = false)
    private KhachHangEntity khachHang;

    @ManyToOne
    @JoinColumn(name = "MaSuatChieu", referencedColumnName = "MaSuatChieu", insertable = false, updatable = false)
    private SuatChieuEntity suatChieu;

    @ManyToOne
    @JoinColumn(name = "MaGhe", referencedColumnName = "MaGhe", insertable = false, updatable = false)
    private GheEntity ghe;

    // Getters và Setters
    public String getMaVe() { return maVe; }
    public void setMaVe(String maVe) { this.maVe = maVe; }
    public String getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(String maKhachHang) { this.maKhachHang = maKhachHang; }
    public String getMaSuatChieu() { return maSuatChieu; }
    public void setMaSuatChieu(String maSuatChieu) { this.maSuatChieu = maSuatChieu; }
    public String getMaGhe() { return maGhe; }
    public void setMaGhe(String maGhe) { this.maGhe = maGhe; }
    public BigDecimal getGiaVe() { return giaVe; }
    public void setGiaVe(BigDecimal giaVe) { this.giaVe = giaVe; }
    public Date getNgayMua() { return ngayMua; }
    public void setNgayMua(Date ngayMua) { this.ngayMua = ngayMua; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public KhachHangEntity getKhachHang() { return khachHang; }
    public void setKhachHang(KhachHangEntity khachHang) { this.khachHang = khachHang; }
    public SuatChieuEntity getSuatChieu() { return suatChieu; }
    public void setSuatChieu(SuatChieuEntity suatChieu) { this.suatChieu = suatChieu; }
    public GheEntity getGhe() { return ghe; }
    public void setGhe(GheEntity ghe) { this.ghe = ghe; }
}