package movie.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "Combo")
public class ComboEntity {
    @Id
    @Column(name = "MaCombo", length = 10)
    private String maCombo;

    @Column(name = "TenCombo", columnDefinition = "nvarchar(50)")
    private String tenCombo;

    @Column(name = "GiaCombo")
    private BigDecimal giaCombo;

    @Column(name = "MoTa", columnDefinition = "nvarchar(255)")
    private String moTa;

    @OneToMany(mappedBy = "combo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChiTietComboEntity> chiTietCombos;

    // Constructors
    public ComboEntity() {}

    // Getters and Setters
    public String getMaCombo() { return maCombo; }
    public void setMaCombo(String maCombo) { this.maCombo = maCombo; }
    public String getTenCombo() { return tenCombo; }
    public void setTenCombo(String tenCombo) { this.tenCombo = tenCombo; }
    public BigDecimal getGiaCombo() { return giaCombo; }
    public void setGiaCombo(BigDecimal giaCombo) { this.giaCombo = giaCombo; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    public List<ChiTietComboEntity> getChiTietCombos() { return chiTietCombos; }
    public void setChiTietCombos(List<ChiTietComboEntity> chiTietCombos) { this.chiTietCombos = chiTietCombos; }
}