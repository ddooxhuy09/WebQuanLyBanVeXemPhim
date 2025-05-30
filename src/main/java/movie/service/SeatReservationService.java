package movie.service;

import movie.entity.GheEntity;
import movie.entity.VeEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SeatReservationService {

    private final SessionFactory sessionFactory;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public SeatReservationService(SessionFactory sessionFactory, SimpMessagingTemplate messagingTemplate) {
        this.sessionFactory = sessionFactory;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public void reserveSeat(String maVe, String maKhachHang, String maSuatChieu, String maGhe, BigDecimal giaVe) {
        try {
            Session dbSession = sessionFactory.getCurrentSession();
            VeEntity ve = new VeEntity();
            ve.setMaVe(maVe);
            ve.setMaKhachHang(maKhachHang);
            ve.setMaSuatChieu(maSuatChieu);
            ve.setMaGhe(maGhe);
            ve.setGiaVe(giaVe);
            ve.setNgayMua(new Date());
            dbSession.save(ve);
            System.out.println("Reserved seat: MaVe=" + maVe + ", MaGhe=" + maGhe + ", NgayMua=" + ve.getNgayMua());
        } catch (Exception e) {
            System.err.println("Error reserving seat: " + e.getMessage());
            throw e;
        }
    }

    @Transactional
    @Scheduled(fixedRate = 5000)
    public void releaseExpiredSeats() {
        try {
            Session dbSession = sessionFactory.getCurrentSession();
            Date expirationTime = new Date(System.currentTimeMillis() - 5 * 60 * 1000);
            System.out.println("JVM timezone: " + TimeZone.getDefault().getID());
            System.out.println("Checking expired seats at: " + new Date() + ", expirationTime=" + expirationTime);

            Query query = dbSession.createQuery(
                    "FROM VeEntity v WHERE v.donHang IS NULL AND v.ngayMua <= :expirationTime");
            query.setParameter("expirationTime", expirationTime);
            List<VeEntity> expiredSeats = query.list();
            System.out.println("Found " + expiredSeats.size() + " expired seats");

            if (expiredSeats.isEmpty()) {
                return;
            }

            List<String> maGheList = expiredSeats.stream()
                    .map(VeEntity::getMaGhe)
                    .collect(Collectors.toList());

            Query gheQuery = dbSession.createQuery(
                    "FROM GheEntity g WHERE g.maGhe IN :maGheList");
            gheQuery.setParameterList("maGheList", maGheList);
            List<GheEntity> gheList = gheQuery.list();

            Map<String, GheEntity> gheMap = gheList.stream()
                    .collect(Collectors.toMap(GheEntity::getMaGhe, ghe -> ghe));

            Map<String, List<String>> seatsByShowtime = new HashMap<>();
            for (VeEntity ve : expiredSeats) {
                GheEntity ghe = gheMap.get(ve.getMaGhe());
                if (ghe != null) {
                    String seatId = ghe.getTenHang() + ghe.getSoGhe();
                    seatsByShowtime.computeIfAbsent(ve.getMaSuatChieu(), k -> new ArrayList<>()).add(seatId);
                }
                System.out.println("Deleting expired seat: MaVe=" + ve.getMaVe() + ", MaGhe=" + ve.getMaGhe() + ", NgayMua=" + ve.getNgayMua());
                dbSession.delete(ve);
            }

            dbSession.flush();
            System.out.println("Expired seats deleted successfully");

            seatsByShowtime.forEach((maSuatChieu, seatIds) -> {
                if (!seatIds.isEmpty()) {
                    messagingTemplate.convertAndSend("/topic/expired-seats/" + maSuatChieu, seatIds);
                    System.out.println("Sent WebSocket notification for maSuatChieu=" + maSuatChieu + ", seats: " + seatIds);
                }
            });
        } catch (Exception e) {
            System.err.println("Error releasing expired seats: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional
    public void updateSeatReservation(String maSuatChieu, List<String> newSeats, String maKhachHang, BigDecimal giaVe) {
        try {
            Session dbSession = sessionFactory.getCurrentSession();
            Query deleteQuery = dbSession.createQuery(
                    "DELETE FROM VeEntity v WHERE v.maKhachHang = :maKhachHang AND v.maSuatChieu = :maSuatChieu AND v.donHang IS NULL");
            deleteQuery.setParameter("maKhachHang", maKhachHang);
            deleteQuery.setParameter("maSuatChieu", maSuatChieu);
            int deletedCount = deleteQuery.executeUpdate();
            System.out.println("Deleted " + deletedCount + " old reservations for MaKhachHang=" + maKhachHang + ", MaSuatChieu=" + maSuatChieu);

            // Tạo danh sách ghế với thông tin maKhachHang
            List<Map<String, String>> seatInfoList = new ArrayList<>();
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
                    Map<String, String> seatInfo = new HashMap<>();
                    seatInfo.put("seatId", seatId);
                    seatInfo.put("maKhachHang", maKhachHang);
                    seatInfoList.add(seatInfo);
                } else {
                    System.err.println("Seat not found: " + seatId);
                }
            }

            // Gửi thông báo WebSocket với thông tin ghế và maKhachHang
            messagingTemplate.convertAndSend("/topic/seats/" + maSuatChieu, seatInfoList);
        } catch (Exception e) {
            System.err.println("Error updating seat reservation: " + e.getMessage());
            throw e;
        }
    }
}