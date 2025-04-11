package movie.entity;

import javax.persistence.*;
import java.io.Serializable;

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
    @JoinColumn(name = "MaBapNuoc", insertable = false, updatable = false) // Liên kết với MaBapNuoc
    private BapNuocEntity bapNuoc; // Thêm trường này

    // Constructors
    public ChiTietComboEntity() {}

    // Getters và Setters
    public ComboEntity getCombo() { return combo; }
    public void setCombo(ComboEntity combo) { this.combo = combo; }
    public String getMaBapNuoc() { return maBapNuoc; }
    public void setMaBapNuoc(String maBapNuoc) { this.maBapNuoc = maBapNuoc; }
    public Integer getSoLuong() { return soLuong; }
    public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }
    public BapNuocEntity getBapNuoc() { return bapNuoc; }
    public void setBapNuoc(BapNuocEntity bapNuoc) { this.bapNuoc = bapNuoc; }
}