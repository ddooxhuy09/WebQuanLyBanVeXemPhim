package movie.controller;

import movie.entity.KhachHangEntity;
import movie.model.KhachHangModel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import io.github.cdimascio.dotenv.Dotenv;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;



@Controller
@RequestMapping("/oauth")
public class GoogleOAuthController {
    private static final Dotenv dotenv = Dotenv.load();
    String CLIENT_ID = dotenv.get("GOOGLE_CLIENT_ID");
    String CLIENT_SECRET = dotenv.get("GOOGLE_CLIENT_SECRET");
    private static final String REDIRECT_URI = "http://localhost:8080/WebBanVeXemPhim/oauth/google/callback";

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping(value = "/google/login", method = RequestMethod.GET)
    public void redirectToGoogleAuth(HttpServletResponse response) throws IOException {
        String authUrl = "https://accounts.google.com/o/oauth2/v2/auth" +
                "?client_id=" + CLIENT_ID +
                "&redirect_uri=" + REDIRECT_URI +
                "&response_type=code" +
                "&scope=email%20profile" +
                "&access_type=offline";
        response.sendRedirect(authUrl);
    }

    @Transactional
    @RequestMapping(value = "/google/callback", method = RequestMethod.GET)
    public String handleGoogleCallback(
            @RequestParam("code") String code,
            HttpSession session,
            Model model) {
        try {
            // Exchange authorization code for access token
            String tokenUrl = "https://oauth2.googleapis.com/token";

            MultiValueMap<String, String> tokenRequest = new LinkedMultiValueMap<>();
            tokenRequest.add("code", code);
            tokenRequest.add("client_id", CLIENT_ID);
            tokenRequest.add("client_secret", CLIENT_SECRET);
            tokenRequest.add("redirect_uri", REDIRECT_URI);
            tokenRequest.add("grant_type", "authorization_code");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(tokenRequest, headers);

            Map<String, Object> tokenResponse = restTemplate.postForObject(
                    tokenUrl,
                    requestEntity,
                    Map.class);

            String accessToken = (String) tokenResponse.get("access_token");

            String userInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo";
            Map<String, Object> userInfo = restTemplate.getForObject(
                    userInfoUrl + "?access_token=" + accessToken,
                    Map.class);

            String email = (String) userInfo.get("email");
            String givenName = (String) userInfo.get("given_name");
            String familyName = (String) userInfo.get("family_name");

            if (email == null) {
                model.addAttribute("error", "Không thể lấy email từ tài khoản Google");
                return "auth/login";
            }

            Session dbSession = sessionFactory.getCurrentSession();
            Query query = dbSession.createQuery(
                    "FROM KhachHangEntity kh WHERE kh.email = :email");
            query.setParameter("email", email);
            KhachHangEntity khachHang = (KhachHangEntity) query.uniqueResult();

            if (khachHang == null) {
                khachHang = new KhachHangEntity();
                String uniqueId = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6);
                khachHang.setMaKhachHang("KH" + uniqueId);
                khachHang.setHoKhachHang(familyName != null ? familyName.trim() : "");
                khachHang.setTenKhachHang(givenName != null ? givenName.trim() : "");
                khachHang.setEmail(email);
                khachHang.setMatKhau("");
                khachHang.setNgayDangKy(new Date());
                khachHang.setTongDiem(0);
                khachHang.setSoDienThoai("");
                khachHang.setNgaySinh(null);
                dbSession.save(khachHang);
            }

            KhachHangModel user = new KhachHangModel(khachHang);
            session.setAttribute("loggedInUser", user);

            String redirectUrl = (String) session.getAttribute("redirectAfterLogin");
            String redirectMaPhim = (String) session.getAttribute("redirectMaPhim");
            String redirectMaSuatChieu = (String) session.getAttribute("redirectMaSuatChieu");

            if (redirectUrl != null && redirectUrl.contains("/booking/select-seats") &&
                    redirectMaPhim != null && redirectMaSuatChieu != null) {
                session.removeAttribute("redirectAfterLogin");
                session.removeAttribute("redirectMaPhim");
                session.removeAttribute("redirectMaSuatChieu");
                // Chuyển hướng với query parameters
                String redirectWithParams = String.format("/booking/select-seats?maPhim=%s&maSuatChieu=%s",
                        redirectMaPhim, redirectMaSuatChieu);
                return "redirect:" + redirectWithParams;
            } else if (redirectUrl != null && !redirectUrl.isEmpty()) {
                session.removeAttribute("redirectAfterLogin");
                session.removeAttribute("redirectMaPhim");
                session.removeAttribute("redirectMaSuatChieu");
                return "redirect:" + redirectUrl;
            }

            return "redirect:/home/";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi đăng nhập với Google: " + e.getMessage());
            return "auth/login";
        }
    }
}