package movie.model;

public class PhongChieuModel {
    private String maPhongChieu;
    private String tenPhongChieu;
    private int sucChua;
    private String maRapChieu; // Keep the code for internal logic/linking
    private String tenRapChieu; // Add the name for display
    private String urlHinhAnh;

    // Constructors
    public PhongChieuModel() {}

    // Updated constructor to map tenRapChieu from entity
    public PhongChieuModel(movie.entity.PhongChieuEntity entity) {
        if (entity != null) {
            this.maPhongChieu = entity.getMaPhongChieu();
            this.tenPhongChieu = entity.getTenPhongChieu();
            this.sucChua = entity.getSucChua();
            this.urlHinhAnh = entity.getUrlHinhAnh();
            // Get both code and name from the related RapChieuEntity
            if (entity.getRapChieu() != null) {
                this.maRapChieu = entity.getRapChieu().getMaRapChieu();
                this.tenRapChieu = entity.getRapChieu().getTenRapChieu(); // Map the name here
            } else {
                this.maRapChieu = null;
                this.tenRapChieu = null; // Or set a default like "Không xác định"
            }
        }
    }

    // Getters và Setters
    public String getMaPhongChieu() { return maPhongChieu; }
    public void setMaPhongChieu(String maPhongChieu) { this.maPhongChieu = maPhongChieu; }
    public String getTenPhongChieu() { return tenPhongChieu; }
    public void setTenPhongChieu(String tenPhongChieu) { this.tenPhongChieu = tenPhongChieu; }
    public int getSucChua() { return sucChua; }
    public void setSucChua(int sucChua) { this.sucChua = sucChua; }
    public String getMaRapChieu() { return maRapChieu; }
    public void setMaRapChieu(String maRapChieu) { this.maRapChieu = maRapChieu; }
    public String getTenRapChieu() { return tenRapChieu; } // Getter for the name
    public void setTenRapChieu(String tenRapChieu) { this.tenRapChieu = tenRapChieu; } // Setter for the name
    public String getUrlHinhAnh() { return urlHinhAnh; }
    public void setUrlHinhAnh(String urlHinhAnh) { this.urlHinhAnh = urlHinhAnh; }
}
