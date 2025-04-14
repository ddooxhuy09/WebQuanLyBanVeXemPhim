package movie.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "ChiTietCombo")
public class ChiTietComboEntity implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "MaCombo")
    private ComboEntity combo;

    @Id
    @Column(name = "MaBapNuoc", length = 10)
    private String maBapNuoc;

    @Column(name = "SoLuong")
    private Integer soLuong;

    @ManyToOne
    @JoinColumn(name = "MaBapNuoc", insertable = false, updatable = false)
    private BapNuocEntity bapNuoc;

    // Constructors
    public ChiTietComboEntity() {}

    // Getters và Setters
    public ComboEntity getCombo() {
        return combo;
    }

    public void setCombo(ComboEntity combo) {
        this.combo = combo;
    }

    public String getMaBapNuoc() {
        return maBapNuoc;
    }

    public void setMaBapNuoc(String maBapNuoc) {
        this.maBapNuoc = maBapNuoc;
    }

    public Integer getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(Integer soLuong) {
        this.soLuong = soLuong;
    }

    public BapNuocEntity getBapNuoc() {
        return bapNuoc;
    }

    public void setBapNuoc(BapNuocEntity bapNuoc) {
        this.bapNuoc = bapNuoc;
    }

    // Triển khai equals() và hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietComboEntity that = (ChiTietComboEntity) o;
        return Objects.equals(combo != null ? combo.getMaCombo() : null, that.combo != null ? that.combo.getMaCombo() : null) &&
               Objects.equals(maBapNuoc, that.maBapNuoc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(combo != null ? combo.getMaCombo() : null, maBapNuoc);
    }
}