package movie.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "ChiTietDonHangCombo")
public class ChiTietDonHangComboEntity implements Serializable {

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
    public DonHangEntity getDonHang() {
        return donHang;
    }

    public void setDonHang(DonHangEntity donHang) {
        this.donHang = donHang;
    }

    public String getMaCombo() {
        return maCombo;
    }

    public void setMaCombo(String maCombo) {
        this.maCombo = maCombo;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    // Triển khai equals() và hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietDonHangComboEntity that = (ChiTietDonHangComboEntity) o;
        return Objects.equals(donHang != null ? donHang.getMaDonHang() : null, that.donHang != null ? that.donHang.getMaDonHang() : null) &&
               Objects.equals(maCombo, that.maCombo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(donHang != null ? donHang.getMaDonHang() : null, maCombo);
    }
}