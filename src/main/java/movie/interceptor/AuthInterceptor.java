package movie.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();

        // Các trang công khai không cần đăng nhập
        if (requestURI.startsWith(contextPath + "/auth/login") ||
                requestURI.startsWith(contextPath + "/auth/register") ||
                requestURI.startsWith(contextPath + "/auth/logout") || // Thêm /auth/logout
                requestURI.startsWith(contextPath + "/home/") ||
                requestURI.startsWith(contextPath + "/movie-detail") ||
                requestURI.startsWith(contextPath + "/oauth/")) {
                return true;
        }

        // Kiểm tra session
        if (session == null || (session.getAttribute("loggedInUser") == null && session.getAttribute("loggedInAdmin") == null)) {
            if (requestURI.startsWith(contextPath + "/booking/select-seats")) {
                String maPhim = request.getParameter("maPhim");
                String maSuatChieu = request.getParameter("maSuatChieu");
                session = request.getSession(true);
                session.setAttribute("redirectAfterLogin", contextPath + "/booking/select-seats");
                if (maPhim != null && maSuatChieu != null) {
                    session.setAttribute("redirectMaPhim", maPhim);
                    session.setAttribute("redirectMaSuatChieu", maSuatChieu);
                }
            } else {
                if (session == null) {
                    session = request.getSession(true);
                }
                String redirectPath = requestURI.startsWith(contextPath) ? requestURI.substring(contextPath.length()) : requestURI;
                session.setAttribute("redirectAfterLogin", contextPath + redirectPath);
            }
            
            response.sendRedirect(contextPath + "/auth/login");
            return false;
        }

        // Kiểm tra quyền admin
        if (requestURI.startsWith(contextPath + "/admin")) {
            if (session.getAttribute("loggedInAdmin") == null) {
                response.sendRedirect(contextPath + "/auth/login");
                return false;
            }
            return true;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}