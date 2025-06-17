package movie.model;

public class RapChieuModel {
    private String maRapChieu;
    private String tenRapChieu;
    private String diaChi;
    private String soDienThoaiLienHe;

    public RapChieuModel() {}

    public RapChieuModel(movie.entity.RapChieuEntity entity) {
        if (entity != null) {
            this.maRapChieu = entity.getMaRapChieu();
            this.tenRapChieu = entity.getTenRapChieu();
            this.diaChi = entity.getDiaChi();
            this.soDienThoaiLienHe = entity.getSoDienThoaiLienHe();
        }
    }

    // Getters và Setters
    public String getMaRapChieu() { return maRapChieu; }
    public void setMaRapChieu(String maRapChieu) { this.maRapChieu = maRapChieu; }
    public String getTenRapChieu() { return tenRapChieu; }
    public void setTenRapChieu(String tenRapChieu) { this.tenRapChieu = tenRapChieu; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public String getSoDienThoaiLienHe() { return soDienThoaiLienHe; }
    public void setSoDienThoaiLienHe(String soDienThoaiLienHe) { this.soDienThoaiLienHe = soDienThoaiLienHe; }

    // Triển khai equals() và hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RapChieuModel that = (RapChieuModel) o;
        return maRapChieu != null && maRapChieu.equals(that.maRapChieu);
    }

    @Override
    public int hashCode() {
        return maRapChieu != null ? maRapChieu.hashCode() : 0;
    }
}