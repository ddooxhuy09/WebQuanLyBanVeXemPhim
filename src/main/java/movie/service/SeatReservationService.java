package movie.service;

import movie.entity.GheEntity;
import movie.entity.VeEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class SeatReservationService {

    @Autowired
    private SessionFactory sessionFactory;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Transactional
    public void reserveSeat(String maVe, String maKhachHang, String maSuatChieu, String maGhe, BigDecimal giaVe) {
        Session dbSession = sessionFactory.getCurrentSession();
        VeEntity ve = new VeEntity();
        ve.setMaVe(maVe);
        ve.setMaKhachHang(maKhachHang);
        ve.setMaSuatChieu(maSuatChieu);
        ve.setMaGhe(maGhe);
        ve.setGiaVe(giaVe);
        ve.setNgayMua(new Date());
        dbSession.save(ve);
    }

    @Transactional
    public void releaseExpiredSeats() {
        Session dbSession = sessionFactory.getCurrentSession();
        Date expirationTime = new Date(System.currentTimeMillis() - 5 * 60 * 1000); // 5 phút trước
        Query query = dbSession.createQuery(
                "FROM VeEntity v WHERE v.donHang IS NULL AND v.ngayMua <= :expirationTime");
        query.setParameter("expirationTime", expirationTime);
        List<VeEntity> expiredSeats = query.list();
        for (VeEntity ve : expiredSeats) {
            dbSession.delete(ve);
        }
    }

    @Transactional
    public void updateSeatReservation(String maSuatChieu, List<String> newSeats, String maKhachHang, BigDecimal giaVe) {
        Session dbSession = sessionFactory.getCurrentSession();
        Query deleteQuery = dbSession.createQuery(
                "DELETE FROM VeEntity v WHERE v.maKhachHang = :maKhachHang AND v.maSuatChieu = :maSuatChieu AND v.donHang IS NULL");
        deleteQuery.setParameter("maKhachHang", maKhachHang);
        deleteQuery.setParameter("maSuatChieu", maSuatChieu);
        deleteQuery.executeUpdate();

        for (String seatId : newSeats) {
            Query gheQuery = dbSession.createQuery(
                    "FROM GheEntity g WHERE g.tenHang || g.soGhe = :seatId AND g.phongChieu.maPhongChieu IN " +
                    "(SELECT sc.phongChieu.maPhongChieu FROM SuatChieuEntity sc WHERE sc.maSuatChieu = :maSuatChieu)");
            gheQuery.setParameter("seatId", seatId);
            gheQuery.setParameter("maSuatChieu", maSuatChieu);
            GheEntity ghe = (GheEntity) gheQuery.uniqueResult();
            if (ghe != null) {
                String maVe = "VE" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
                reserveSeat(maVe, maKhachHang, maSuatChieu, ghe.getMaGhe(), giaVe);
            }
        }
    }

    public void startExpirationCheck() {
        scheduler.scheduleAtFixedRate(this::releaseExpiredSeats, 0, 30, TimeUnit.SECONDS);
    }
}