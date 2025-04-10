package movie.entity;

import javax.persistence.*;

@Entity
@Table(name = "ChiTietCombo")
@IdClass(ChiTietComboId.class) // Composite key class
public class ChiTietComboEntity {
    
    @Id
    @Column(name = "MaCombo", length = 10)
    private String maCombo;
    
    @Id
    @Column(name = "MaBapNuoc", length = 10)
    private String maBapNuoc;
    
    @Column(name = "SoLuong")
    private Integer soLuong;
    
    @ManyToOne
    @JoinColumn(name = "MaCombo", insertable = false, updatable = false)
    private ComboEntity combo;
    
    @ManyToOne
    @JoinColumn(name = "MaBapNuoc", insertable = false, updatable = false)
    private BapNuocEntity bapNuoc;

    // Constructors
    public ChiTietComboEntity() {}

    // Getters and Setters
    public String getMaCombo() { return maCombo; }
    public void setMaCombo(String maCombo) { this.maCombo = maCombo; }
    public String getMaBapNuoc() { return maBapNuoc; }
    public void setMaBapNuoc(String maBapNuoc) { this.maBapNuoc = maBapNuoc; }
    public Integer getSoLuong() { return soLuong; }
    public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }
    public ComboEntity getCombo() { return combo; }
    public void setCombo(ComboEntity combo) { this.combo = combo; }
    public BapNuocEntity getBapNuoc() { return bapNuoc; }
    public void setBapNuoc(BapNuocEntity bapNuoc) { this.bapNuoc = bapNuoc; }
}