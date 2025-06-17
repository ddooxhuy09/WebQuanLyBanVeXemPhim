package movie.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "BapNuoc")
public class BapNuocEntity {
    @Id
    @Column(name = "MaBapNuoc", length = 10)
    private String maBapNuoc;

    @Column(name = "TenBapNuoc", columnDefinition = "nvarchar(50)")
    private String tenBapNuoc;

    @Column(name = "GiaBapNuoc")
    private BigDecimal giaBapNuoc;

    @Column(name = "UrlHinhAnh")
    private String urlHinhAnh;

    @OneToMany(mappedBy = "bapNuoc", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChiTietComboEntity> chiTietCombos;

    // Constructors
    public BapNuocEntity() {}

    // Getters and Setters
    public String getMaBapNuoc() { return maBapNuoc; }
    public void setMaBapNuoc(String maBapNuoc) { this.maBapNuoc = maBapNuoc; }
    public String getTenBapNuoc() { return tenBapNuoc; }
    public void setTenBapNuoc(String tenBapNuoc) { this.tenBapNuoc = tenBapNuoc; }
    public BigDecimal getGiaBapNuoc() { return giaBapNuoc; }
    public void setGiaBapNuoc(BigDecimal giaBapNuoc) { this.giaBapNuoc = giaBapNuoc; }
    public String getUrlHinhAnh() { return urlHinhAnh; }
    public void setUrlHinhAnh(String urlHinhAnh) { this.urlHinhAnh = urlHinhAnh; }
    public List<ChiTietComboEntity> getChiTietCombos() { return chiTietCombos; }
    public void setChiTietCombos(List<ChiTietComboEntity> chiTietCombos) { this.chiTietCombos = chiTietCombos; }
}