package movie.entity;

import javax.persistence.*;

@Entity
@Table(name = "PhongChieu")
public class PhongChieuEntity {
    @Id
    @Column(name = "MaPhongChieu", length = 10)
    private String maPhongChieu;

    @Column(name = "TenPhongChieu", columnDefinition = "nvarchar(50)")
    private String tenPhongChieu;

    @Column(name = "SucChua")
    private int sucChua;

    @Column(name = "UrlHinhAnh", length = 255)
    private String urlHinhAnh;

    @ManyToOne
    @JoinColumn(name = "MaRapChieu", referencedColumnName = "MaRapChieu")
    private RapChieuEntity rapChieu;

    // Getters và Setters
    public String getMaPhongChieu() { return maPhongChieu; }
    public void setMaPhongChieu(String maPhongChieu) { this.maPhongChieu = maPhongChieu; }
    public String getTenPhongChieu() { return tenPhongChieu; }
    public void setTenPhongChieu(String tenPhongChieu) { this.tenPhongChieu = tenPhongChieu; }
    public int getSucChua() { return sucChua; }
    public void setSucChua(int sucChua) { this.sucChua = sucChua; }
    public String getUrlHinhAnh() { return urlHinhAnh; }
    public void setUrlHinhAnh(String urlHinhAnh) { this.urlHinhAnh = urlHinhAnh; }
    public RapChieuEntity getRapChieu() { return rapChieu; }
    public void setRapChieu(RapChieuEntity rapChieu) { this.rapChieu = rapChieu; }
}