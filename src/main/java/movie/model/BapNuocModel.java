package movie.model;

import java.math.BigDecimal;
import movie.entity.BapNuocEntity;

public class BapNuocModel {
    private String maBapNuoc;
    private String tenBapNuoc;
    private BigDecimal giaBapNuoc;

    // Constructors
    public BapNuocModel() {}

    public BapNuocModel(BapNuocEntity entity) {
        if (entity != null) {
            this.maBapNuoc = entity.getMaBapNuoc();
            this.tenBapNuoc = entity.getTenBapNuoc();
            this.giaBapNuoc = entity.getGiaBapNuoc();
        }
    }

    // Getters and Setters
    public String getMaBapNuoc() { return maBapNuoc; }
    public void setMaBapNuoc(String maBapNuoc) { this.maBapNuoc = maBapNuoc; }
    public String getTenBapNuoc() { return tenBapNuoc; }
    public void setTenBapNuoc(String tenBapNuoc) { this.tenBapNuoc = tenBapNuoc; }
    public BigDecimal getGiaBapNuoc() { return giaBapNuoc; }
    public void setGiaBapNuoc(BigDecimal giaBapNuoc) { this.giaBapNuoc = giaBapNuoc; }
}