package movie.entity;

import javax.persistence.*;

@Entity
@Table(name = "LoaiGhe")
public class LoaiGheEntity {
    @Id
    @Column(name = "MaLoaiGhe", length = 10)
    private String maLoaiGhe;

    @Column(name = "TenLoaiGhe", columnDefinition = "nvarchar(50)")
    private String tenLoaiGhe;

    @Column(name = "HeSoGia")
    private double heSoGia;

    // Getters và Setters
    public String getMaLoaiGhe() { return maLoaiGhe; }
    public void setMaLoaiGhe(String maLoaiGhe) { this.maLoaiGhe = maLoaiGhe; }
    public String getTenLoaiGhe() { return tenLoaiGhe; }
    public void setTenLoaiGhe(String tenLoaiGhe) { this.tenLoaiGhe = tenLoaiGhe; }
    public double getHeSoGia() { return heSoGia; }
    public void setHeSoGia(double heSoGia) { this.heSoGia = heSoGia; }
}