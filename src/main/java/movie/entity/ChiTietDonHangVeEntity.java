package movie.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "ChiTietDonHangVe")
public class ChiTietDonHangVeEntity implements Serializable { // Thêm Serializable
    @Id
    @ManyToOne
    @JoinColumn(name = "MaDonHang")
    private DonHangEntity donHang;

    @Id
    @Column(name = "MaVe", length = 10)
    private String maVe;

    // Getters và Setters
    public DonHangEntity getDonHang() { return donHang; }
    public void setDonHang(DonHangEntity donHang) { this.donHang = donHang; }
    public String getMaVe() { return maVe; }
    public void setMaVe(String maVe) { this.maVe = maVe; }
}