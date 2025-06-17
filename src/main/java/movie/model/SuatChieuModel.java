package movie.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import movie.entity.PhuThuEntity;
import movie.entity.SuatChieuEntity;

public class SuatChieuModel {
    private String maSuatChieu;
    private String maPhim;
    private PhimModel phim; // Giữ lại nếu bạn cần thông tin phim đầy đủ
    private String maPhongChieu;
    private PhongChieuModel phongChieu; // Giữ lại nếu bạn cần thông tin phòng đầy đủ
    private Timestamp ngayGioChieu;
    private Timestamp ngayGioKetThuc;
    private String loaiManChieu;
    private List<PhuThuModel> phuThus; // Giữ lại nếu bạn cần đối tượng phụ thu đầy đủ
    private String danhSachTenPhuThu; // THÊM MỚI: Thuộc tính để hiển thị tên phụ thu
    private boolean hasTickets;

    // Constructors
    public SuatChieuModel() {
        this.phuThus = new ArrayList<>();
    }

    public SuatChieuModel(SuatChieuEntity entity) {
        this.maSuatChieu = entity.getMaSuatChieu();
        this.maPhim = entity.getMaPhim();
        if (entity.getPhim() != null) {
            this.phim = new PhimModel(entity.getPhim());
        }
        this.maPhongChieu = entity.getMaPhongChieu();
        if (entity.getPhongChieu() != null) {
            this.phongChieu = new PhongChieuModel(entity.getPhongChieu());
        }
        this.ngayGioChieu = entity.getNgayGioChieu();
        this.ngayGioKetThuc = entity.getNgayGioKetThuc();
        this.loaiManChieu = entity.getLoaiManChieu();

        this.phuThus = new ArrayList<>();
        if (entity.getPhuThus() != null && !entity.getPhuThus().isEmpty()) {
            for (PhuThuEntity phuThuEntity : entity.getPhuThus()) {
                this.phuThus.add(new PhuThuModel(phuThuEntity));
            }
            this.danhSachTenPhuThu = entity.getPhuThus().stream()
                                          .map(PhuThuEntity::getTenPhuThu)
                                          .collect(Collectors.joining(", "));
        } else {
            this.danhSachTenPhuThu = "Không có";
        }
    }
    
 // Getters and setters
    public boolean isHasTickets() {
        return hasTickets;
    }

    public void setHasTickets(boolean hasTickets) {
        this.hasTickets = hasTickets;
    }

    // Getters and Setters
    public String getMaSuatChieu() {
        return maSuatChieu;
    }

    public void setMaSuatChieu(String maSuatChieu) {
        this.maSuatChieu = maSuatChieu;
    }

    public String getMaPhim() {
        return maPhim;
    }

    public void setMaPhim(String maPhim) {
        this.maPhim = maPhim;
    }

    public PhimModel getPhim() {
        return phim;
    }

    public void setPhim(PhimModel phim) {
        this.phim = phim;
    }

    public String getMaPhongChieu() {
        return maPhongChieu;
    }

    public void setMaPhongChieu(String maPhongChieu) {
        this.maPhongChieu = maPhongChieu;
    }

    public PhongChieuModel getPhongChieu() {
        return phongChieu;
    }

    public void setPhongChieu(PhongChieuModel phongChieu) {
        this.phongChieu = phongChieu;
    }

    public Timestamp getNgayGioChieu() {
        return ngayGioChieu;
    }

    public void setNgayGioChieu(Timestamp ngayGioChieu) {
        this.ngayGioChieu = ngayGioChieu;
    }

    public Timestamp getNgayGioKetThuc() {
        return ngayGioKetThuc;
    }

    public void setNgayGioKetThuc(Timestamp ngayGioKetThuc) {
        this.ngayGioKetThuc = ngayGioKetThuc;
    }

    public String getLoaiManChieu() {
        return loaiManChieu;
    }

    public void setLoaiManChieu(String loaiManChieu) {
        this.loaiManChieu = loaiManChieu;
    }

    public List<PhuThuModel> getPhuThus() {
        return phuThus;
    }

    public void setPhuThus(List<PhuThuModel> phuThus) {
        this.phuThus = phuThus;
    }

    public String getDanhSachTenPhuThu() {
        return danhSachTenPhuThu;
    }

    public void setDanhSachTenPhuThu(String danhSachTenPhuThu) {
        this.danhSachTenPhuThu = danhSachTenPhuThu;
    }

    // Các phương thức tiện ích khác (ví dụ: getFormattedNgayGioChieu) giữ nguyên
    public String getFormattedNgayGioChieu() {
        if (ngayGioChieu == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(new Date(ngayGioChieu.getTime()));
    }

    public String getFormattedNgayGioKetThuc() {
        if (ngayGioKetThuc == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(new Date(ngayGioKetThuc.getTime()));
    }
}