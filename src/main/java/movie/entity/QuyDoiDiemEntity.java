package movie.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "QuyDoiDiem")
public class QuyDoiDiemEntity implements Serializable {

    @Id
    @Column(name = "MaQuyDoi", length = 10)
    private String maQuyDoi;

    @Column(name = "TenUuDai", length = 50)
    private String tenUuDai;

    @Column(name = "SoDiemCan")
    private Integer soDiemCan;

    @Column(name = "LoaiUuDai", length = 20)
    private String loaiUuDai;

    @Column(name = "GiaTriGiam")
    private BigDecimal giaTriGiam;

    // Getters và Setters
    public String getMaQuyDoi() {
        return maQuyDoi;
    }

    public void setMaQuyDoi(String maQuyDoi) {
        this.maQuyDoi = maQuyDoi;
    }

    public String getTenUuDai() {
        return tenUuDai;
    }

    public void setTenUuDai(String tenUuDai) {
        this.tenUuDai = tenUuDai;
    }

    public Integer getSoDiemCan() {
        return soDiemCan;
    }

    public void setSoDiemCan(Integer soDiemCan) {
        this.soDiemCan = soDiemCan;
    }

    public String getLoaiUuDai() {
        return loaiUuDai;
    }

    public void setLoaiUuDai(String loaiUuDai) {
        this.loaiUuDai = loaiUuDai;
    }

    public BigDecimal getGiaTriGiam() {
        return giaTriGiam;
    }

    public void setGiaTriGiam(BigDecimal giaTriGiam) {
        this.giaTriGiam = giaTriGiam;
    }

    // Triển khai equals() và hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuyDoiDiemEntity that = (QuyDoiDiemEntity) o;
        return Objects.equals(maQuyDoi, that.maQuyDoi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maQuyDoi);
    }
}