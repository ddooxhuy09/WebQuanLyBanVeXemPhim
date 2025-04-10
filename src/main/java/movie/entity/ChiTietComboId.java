package movie.entity;

import java.io.Serializable;
import javax.persistence.Column;

public class ChiTietComboId implements Serializable {
    @Column(name = "MaCombo", length = 10)
    private String maCombo;
    
    @Column(name = "MaBapNuoc", length = 10)
    private String maBapNuoc;

    // Constructors
    public ChiTietComboId() {}
    
    public ChiTietComboId(String maCombo, String maBapNuoc) {
        this.maCombo = maCombo;
        this.maBapNuoc = maBapNuoc;
    }

    // Getters and Setters
    public String getMaCombo() { return maCombo; }
    public void setMaCombo(String maCombo) { this.maCombo = maCombo; }
    public String getMaBapNuoc() { return maBapNuoc; }
    public void setMaBapNuoc(String maBapNuoc) { this.maBapNuoc = maBapNuoc; }

    // equals() and hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietComboId that = (ChiTietComboId) o;
        return maCombo.equals(that.maCombo) && maBapNuoc.equals(that.maBapNuoc);
    }

    @Override
    public int hashCode() {
        return 31 * maCombo.hashCode() + maBapNuoc.hashCode();
    }
}