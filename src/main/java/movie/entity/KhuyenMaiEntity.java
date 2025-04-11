package movie.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "KhuyenMai")
public class KhuyenMaiEntity {

    @Id
    @Column(name = "MaKhuyenMai", length = 10)
    private String maKhuyenMai;

    @Column(name = "MaCode", length = 20)
    private String maCode;

    @Column(name = "MoTa", columnDefinition = "nvarchar(255)")
    private String moTa;

    @Column(name = "LoaiGiamGia", length = 20)
    private String loaiGiamGia;

    @Column(name = "GiaTriGiam")
    private BigDecimal giaTriGiam;

    @Column(name = "NgayBatDau")
    @Temporal(TemporalType.DATE)
    private Date ngayBatDau;

    @Column(name = "NgayKetThuc")
    @Temporal(TemporalType.DATE)
    private Date ngayKetThuc;

    @Column(name = "ApDungCho", length = 50)
    private String apDungCho;

    // Constructors
    public KhuyenMaiEntity() {}

    // Getters and Setters
    public String getMaKhuyenMai() { return maKhuyenMai; }
    public void setMaKhuyenMai(String maKhuyenMai) { this.maKhuyenMai = maKhuyenMai; }

    public String getMaCode() { return maCode; }
    public void setMaCode(String maCode) { this.maCode = maCode; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public String getLoaiGiamGia() { return loaiGiamGia; }
    public void setLoaiGiamGia(String loaiGiamGia) { this.loaiGiamGia = loaiGiamGia; }

    public BigDecimal getGiaTriGiam() { return giaTriGiam; }
    public void setGiaTriGiam(BigDecimal giaTriGiam) { this.giaTriGiam = giaTriGiam; }

    public Date getNgayBatDau() { return ngayBatDau; }
    public void setNgayBatDau(Date ngayBatDau) { this.ngayBatDau = ngayBatDau; }

    public Date getNgayKetThuc() { return ngayKetThuc; }
    public void setNgayKetThuc(Date ngayKetThuc) { this.ngayKetThuc = ngayKetThuc; }

    public String getApDungCho() { return apDungCho; }
    public void setApDungCho(String apDungCho) { this.apDungCho = apDungCho; }
}