package movie.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "ChiTietDonHangCombo")
public class ChiTietDonHangComboEntity implements Serializable { // Thêm Serializable
    @Id
    @ManyToOne
    @JoinColumn(name = "MaDonHang")
    private DonHangEntity donHang;

    @Id
    @Column(name = "MaCombo", length = 10)
    private String maCombo;

    @Column(name = "SoLuong")
    private int soLuong;

    // Getters và Setters
    public DonHangEntity getDonHang() { return donHang; }
    public void setDonHang(DonHangEntity donHang) { this.donHang = donHang; }
    public String getMaCombo() { return maCombo; }
    public void setMaCombo(String maCombo) { this.maCombo = maCombo; }
    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
}