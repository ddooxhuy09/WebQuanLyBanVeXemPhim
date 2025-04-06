package movie.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "TheLoai")
public class TheLoaiEntity {
    @Id
    @Column(name = "MaTheLoai", length = 10)
    private String maTheLoai;

    @Column(name = "TenTheLoai", columnDefinition = "nvarchar(50)")
    private String tenTheLoai;

    @ManyToMany(mappedBy = "theLoais")
    private List<PhimEntity> phims;

    // Getters và Setters
    public String getMaTheLoai() { return maTheLoai; }
    public void setMaTheLoai(String maTheLoai) { this.maTheLoai = maTheLoai; }
    public String getTenTheLoai() { return tenTheLoai; }
    public void setTenTheLoai(String tenTheLoai) { this.tenTheLoai = tenTheLoai; }
    public List<PhimEntity> getPhims() { return phims; }
    public void setPhims(List<PhimEntity> phims) { this.phims = phims; }
}