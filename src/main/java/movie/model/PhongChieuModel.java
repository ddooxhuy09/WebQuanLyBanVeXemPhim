package movie.model;

public class PhongChieuModel {
    private String maPhongChieu;
    private String tenPhongChieu;
    private int sucChua;
    private String maRapChieu;
    private String urlHinhAnh;

    // Constructors
    public PhongChieuModel() {}

    public PhongChieuModel(movie.entity.PhongChieuEntity entity) {
        if (entity != null) {
            this.maPhongChieu = entity.getMaPhongChieu();
            this.tenPhongChieu = entity.getTenPhongChieu();
            this.sucChua = entity.getSucChua();
            this.maRapChieu = entity.getRapChieu() != null ? entity.getRapChieu().getMaRapChieu() : null;
            this.urlHinhAnh = entity.getUrlHinhAnh();
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
    public String getUrlHinhAnh() { return urlHinhAnh; }
    public void setUrlHinhAnh(String urlHinhAnh) { this.urlHinhAnh = urlHinhAnh; }
}