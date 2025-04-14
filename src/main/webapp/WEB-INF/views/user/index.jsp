<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/user/css/styles.css?v=1.0" />
    <title>Movie Container - Galaxy Cinema</title>
</head>
<body>
    <nav class="navbar">
        <div class="logo">Galaxy Cinema</div>
        <ul class="nav-links">
            <li><a href="${pageContext.request.contextPath}/home/">Phim</a></li>
            <li><a href="#">Góc Điện Ảnh</a></li>
            <li><a href="#">Sự Kiện</a></li>
            <li><a href="#">Rạp/Giá Vé</a></li>
            <c:choose>
                <c:when test="${not empty sessionScope.loggedInUser}">
				    <li><a href="${pageContext.request.contextPath}/user/profile">Xin chào, ${sessionScope.loggedInUser.tenKhachHang}</a></li>
				    <li><a href="${pageContext.request.contextPath}/auth/logout" class="login-btn">Đăng Xuất</a></li>
				</c:when>
                <c:otherwise>
                    <li><a href="${pageContext.request.contextPath}/auth/login" class="login-btn">Đăng Nhập</a></li>
                </c:otherwise>
            </c:choose>
        </ul>
    </nav>

    <div class="carousel">
        <div class="carousel-inner">
            <img src="https://cdn.galaxycine.vn/media/2025/3/3/hitman-2-2048_1740974435644.jpg" alt="Banner 1" />
            <img src="https://cdn.galaxycine.vn/media/2025/3/6/8-thang-3-1_1741254164111.jpg" alt="Banner 2" />
            <img src="https://cdn.galaxycine.vn/media/2025/2/28/glx-shopeepay-2_1740731168962.jpg" alt="Banner 3" />
        </div>
        <button class="carousel-btn left-btn">◀</button>
        <button class="carousel-btn right-btn">▶</button>
    </div>

    <div class="movie-container">
        <c:if test="${not empty error}">
            <div class="error-message" style="text-align: center; color: red; margin: 20px;">
                <p>${error}</p>
            </div>
        </c:if>

        <c:forEach var="phim" items="${phimList}">
            <div class="movie-item">
                <img src="${pageContext.request.contextPath}/resources/user/images/${phim.urlPoster}" alt="${phim.tenPhim}" id="movie-poster" />
                <div class="movie-info">
                    <h3>${phim.tenPhim}</h3>
                    <p>Thể loại: 
                        <c:forEach var="maTheLoai" items="${phim.maTheLoais}" varStatus="loop">
                            ${maTheLoai}<c:if test="${!loop.last}">,</c:if>
                        </c:forEach>
                    </p>
                    <p>Thời lượng: ${phim.thoiLuong} phút</p>
                    <p>Ngày khởi chiếu: <fmt:formatDate value="${phim.ngayKhoiChieu}" pattern="dd/MM/yyyy" /></p>
                    <span class="rating">N/A</span>
                    <span class="age-restriction">T${phim.doTuoi}</span>
                </div>
                <div class="overlay">
                    <form action="${pageContext.request.contextPath}/movie-detail" method="post">
                        <input type="hidden" name="id" value="${phim.maPhim}">
                        <button type="submit" class="btn">Đặt Vé</button>
                    </form>
                    <button class="btn trailer-btn" onclick="alert('Opening trailer for ${phim.tenPhim}')">Trailer</button>
                </div>
            </div>
        </c:forEach>
    </div>

    <footer class="footer">
        <div class="footer-content">
            <div class="footer-section">
                <h3>About Galaxy Cinema</h3>
                <p>Your premier destination for the latest movies and entertainment experiences.</p>
            </div>
            <div class="footer-section">
                <h3>Quick Links</h3>
                <ul class="footer-links">
                    <li><a href="#">Now Showing</a></li>
                    <li><a href="#">Coming Soon</a></li>
                    <li><a href="#">Promotions</a></li>
                    <li><a href="#">Gift Cards</a></li>
                </ul>
            </div>
            <div class="footer-section">
                <h3>Connect With Us</h3>
                <div class="social-links">
                    <a href="#">Facebook</a>
                    <a href="#">Twitter</a>
                    <a href="#">Instagram</a>
                    <a href="#">YouTube</a>
                </div>
            </div>
            <div class="footer-section">
                <h3>Newsletter</h3>
                <p>Subscribe for updates and exclusive offers</p>
                <form class="newsletter-form">
                    <input type="email" placeholder="Enter your email" />
                    <button type="submit">Subscribe</button>
                </form>
            </div>
        </div>
        <div class="footer-bottom">
            <p>© 2024 Galaxy Cinema. All rights reserved.</p>
        </div>
    </footer>

    <script type="module">
        import { MovieController } from '${pageContext.request.contextPath}/resources/user/js/controllers/MovieController.js';
        new MovieController();
    </script>
</body>
</html>