<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/user/css/styles.css?v=1.2" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/user/css/asset/moviecontainer.css?v=1.2" />
    <title>Movie Container - Galaxy Cinema</title>
</head>
<body>
    <nav class="navbar">
        <div class="container-nav">
            <div class="navbar-brand">
                <a href="${pageContext.request.contextPath}/home/" class="logo">Galaxy Cinema</a>
            </div>
            <button class="navbar-toggle" aria-label="Toggle navigation">
                <span></span>
                <span></span>
                <span></span>
            </button>
            <ul class="nav-links">
                <li><a href="${pageContext.request.contextPath}/home/">Phim</a></li>
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
        </div>
    </nav>

    <div class="carousel">
        <div class="carousel-inner">
            <img src="https://cdn.galaxycine.vn/media/2025/3/3/hitman-2-2048_1740974435644.jpg" alt="Banner 1" />
            <img src="https://cdn.galaxycine.vn/media/2025/3/6/8-thang-3-1_1741254164111.jpg" alt="Banner 2" />
            <img src="https://cdn.galaxycine.vn/media/2025/2/28/glx-shopeepay-2_1740731168962.jpg" alt="Banner 3" />
            <img src="https://cdn.galaxycine.vn/media/2025/5/8/phi--hpromo-5_1746699744732.jpg" alt="Banner 4" />
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
		        <div class="movie-poster">
		            <img src="${pageContext.request.contextPath}/resources/images/${phim.urlPoster}" alt="${phim.tenPhim}" />
		            <span class="age-restriction">T${phim.doTuoi}</span>
		            <div class="overlay">
		                <a href="${pageContext.request.contextPath}/movie-detail?id=${phim.maPhim}" class="buy-ticket">Đặt vé</a>
                        <a href="${phim.urlTrailer}" class="trailer-btn" target="_blank">Xem Trailer</a>
		            </div>
		        </div>
		        <h3 class="movie-title">${phim.tenPhim}</h3>
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
    <script>
        document.addEventListener('DOMContentLoaded', () => {
            const toggleButton = document.querySelector('.navbar-toggle');
            const navLinks = document.querySelector('.nav-links');
            const navbar = document.querySelector('.navbar');

            if (toggleButton && navLinks && navbar) {
                // Toggle menu on hamburger click
                toggleButton.addEventListener('click', (e) => {
                    e.stopPropagation(); // Prevent click from bubbling to document
                    navLinks.classList.toggle('active');
                    toggleButton.classList.toggle('open');
                });

                // Close menu when clicking outside
                document.addEventListener('click', (e) => {
                    if (!navbar.contains(e.target) && navLinks.classList.contains('active')) {
                        navLinks.classList.remove('active');
                        toggleButton.classList.remove('open');
                    }
                });

                // Prevent clicks inside nav-links from closing the menu
                navLinks.addEventListener('click', (e) => {
                    e.stopPropagation();
                });
            } else {
                console.error('Navbar toggle, nav-links, or navbar not found');
            }
        });
    </script>
</body>
</html>