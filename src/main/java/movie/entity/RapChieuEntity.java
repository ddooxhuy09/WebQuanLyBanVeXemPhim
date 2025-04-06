package movie.entity;

import javax.persistence.*;

@Entity
@Table(name = "RapChieu")
public class RapChieuEntity {
    @Id
    @Column(name = "MaRapChieu")
    private String maRapChieu;

    @Column(name = "TenRapChieu")
    private String tenRapChieu;

    @Column(name = "DiaChi")
    private String diaChi;

    @Column(name = "SoDienThoaiLienHe")
    private String soDienThoaiLienHe;

    // Getters và Setters
    public String getMaRapChieu() { return maRapChieu; }
    public void setMaRapChieu(String maRapChieu) { this.maRapChieu = maRapChieu; }
    public String getTenRapChieu() { return tenRapChieu; }
    public void setTenRapChieu(String tenRapChieu) { this.tenRapChieu = tenRapChieu; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public String getSoDienThoaiLienHe() { return soDienThoaiLienHe; }
    public void setSoDienThoaiLienHe(String soDienThoaiLienHe) { this.soDienThoaiLienHe = soDienThoaiLienHe; }
}