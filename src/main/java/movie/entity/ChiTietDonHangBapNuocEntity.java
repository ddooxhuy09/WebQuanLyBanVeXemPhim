package movie.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "ChiTietDonHangBapNuoc")
public class ChiTietDonHangBapNuocEntity implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "MaDonHang")
    private DonHangEntity donHang;

    @Id
    @Column(name = "MaBapNuoc", length = 10)
    private String maBapNuoc;

    @Column(name = "SoLuong")
    private int soLuong;

    // Getters và Setters
    public DonHangEntity getDonHang() {
        return donHang;
    }

    public void setDonHang(DonHangEntity donHang) {
        this.donHang = donHang;
    }

    public String getMaBapNuoc() {
        return maBapNuoc;
    }

    public void setMaBapNuoc(String maBapNuoc) {
        this.maBapNuoc = maBapNuoc;
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
        ChiTietDonHangBapNuocEntity that = (ChiTietDonHangBapNuocEntity) o;
        return Objects.equals(donHang != null ? donHang.getMaDonHang() : null, that.donHang != null ? that.donHang.getMaDonHang() : null) &&
               Objects.equals(maBapNuoc, that.maBapNuoc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(donHang != null ? donHang.getMaDonHang() : null, maBapNuoc);
    }
}