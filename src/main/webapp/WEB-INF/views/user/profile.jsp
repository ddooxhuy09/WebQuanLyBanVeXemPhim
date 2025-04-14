<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/user/css/styles.css?v=1.0">
    <title>Thông Tin Khách Hàng - Galaxy Cinema</title>
    <style>
        .container {
            max-width: 800px;
            margin: 2rem auto;
            padding: 0 1rem;
        }
        .card {
            padding: 20px;
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
            border: none;
            background: #fff;
            border-radius: 8px;
        }
        .card h2 {
            color: #1a73e8;
            margin-bottom: 1rem;
        }
        .card p {
            font-size: 16px;
            margin: 0.5rem 0;
        }
        .card p strong {
            color: #333;
        }
        .error-message {
            color: red;
            text-align: center;
            margin: 20px;
        }
    </style>
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

    <div class="container">
        <c:if test="${not empty error}">
            <div class="error-message">${error}</div>
        </c:if>
        <div class="card">
            <h2>Thông Tin Khách Hàng</h2>
            <p><strong>Mã khách hàng:</strong> ${user.maKhachHang}</p>
            <p><strong>Họ:</strong> ${user.hoKhachHang}</p>
            <p><strong>Tên:</strong> ${user.tenKhachHang}</p>
            <p><strong>Số điện thoại:</strong> ${user.soDienThoai}</p>
            <p><strong>Email:</strong> ${user.email}</p>
            <c:if test="${not empty user.ngaySinh}">
                <p><strong>Ngày sinh:</strong> <fmt:formatDate value="${user.ngaySinh}" pattern="dd/MM/yyyy" /></p>
            </c:if>
            <p><strong>Ngày đăng ký:</strong> <fmt:formatDate value="${user.ngayDangKy}" pattern="dd/MM/yyyy" /></p>
            <p><strong>Tổng điểm:</strong> ${user.tongDiem}</p>
        </div>
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
                    <input type="email" placeholder="Enter your email">
                    <button type="submit">Subscribe</button>
                </form>
            </div>
        </div>
        <div class="footer-bottom">
            <p>© 2024 Galaxy Cinema. All rights reserved.</p>
        </div>
    </footer>
</body>
</html>