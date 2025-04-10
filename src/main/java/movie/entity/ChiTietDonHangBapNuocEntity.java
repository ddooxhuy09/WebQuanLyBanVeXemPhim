package movie.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "ChiTietDonHangBapNuoc")
public class ChiTietDonHangBapNuocEntity implements Serializable { // Thêm Serializable
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
    public DonHangEntity getDonHang() { return donHang; }
    public void setDonHang(DonHangEntity donHang) { this.donHang = donHang; }
    public String getMaBapNuoc() { return maBapNuoc; }
    public void setMaBapNuoc(String maBapNuoc) { this.maBapNuoc = maBapNuoc; }
    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
}