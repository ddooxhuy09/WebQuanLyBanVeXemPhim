<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ taglib uri="http://java.sun.com/jsp/jstl/core"
prefix="c" %> <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/resources/user/css/styles.css?v=1.0"
    />
    <title>Thông Tin Khách Hàng - Galaxy Cinema</title>
    <style>
      .container {
        max-width: 900px;
        margin: 2rem auto;
        padding: 0 1.5rem;
      }
      .card {
        padding: 25px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        background: #fff;
        border-radius: 10px;
        margin-bottom: 25px;
        transition: transform 0.2s;
      }

      .card h2 {
        color: #ff5722;
        font-size: 24px;
        margin-bottom: 1.5rem;
        border-bottom: 2px solid #ff5722;
        padding-bottom: 8px;
      }
      .card p {
        font-size: 16px;
        margin: 0.75rem 0;
        color: #333;
        line-height: 1.6;
      }
      .card p strong {
        color: #ff5722;
        font-weight: 600;
      }
      .card p.highlight {
        background-color: #f5f5f5;
        padding: 10px;
        border-left: 4px solid #ff5722;
        border-radius: 4px;
        font-size: 18px;
      }
      .error-message {
        color: #d32f2f;
        text-align: center;
        margin: 20px 0;
        padding: 10px;
        background-color: #ffe6e6;
        border-radius: 5px;
        font-size: 16px;
      }
      .order-history table {
        width: 100%;
        border-collapse: collapse;
        margin-top: 1rem;
      }
      .order-history th,
      .order-history td {
        padding: 12px 15px;
        border: 1px solid #ddd;
        text-align: left;
        font-size: 15px;
      }
      .order-history th {
        background-color: #ff5722;
        color: white;
        font-weight: 600;
      }
      .order-history td {
        background-color: #fff;
        transition: background-color 0.2s;
      }
      .order-history tr:hover td {
        background-color: #fff3e0;
      }
      .order-history .no-orders {
        text-align: center;
        padding: 20px;
        font-size: 16px;
        color: #666;
      }
    </style>
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

    <div class="container">
      <c:if test="${not empty error}">
        <div class="error-message">${error}</div>
      </c:if>
      <div class="card">
        <h2>Thông Tin Khách Hàng</h2>
        <p class="highlight">
          <strong>Mã khách hàng:</strong> ${user.maKhachHang}
        </p>
        <p><strong>Họ:</strong> ${user.hoKhachHang}</p>
        <p><strong>Tên:</strong> ${user.tenKhachHang}</p>
        <p><strong>Số điện thoại:</strong> ${user.soDienThoai}</p>
        <p><strong>Email:</strong> ${user.email}</p>
        <c:if test="${not empty user.ngaySinh}">
          <p>
            <strong>Ngày sinh:</strong>
            <fmt:formatDate value="${user.ngaySinh}" pattern="dd/MM/yyyy" />
          </p>
        </c:if>
        <p>
          <strong>Ngày đăng ký:</strong>
          <fmt:formatDate value="${user.ngayDangKy}" pattern="dd/MM/yyyy" />
        </p>
        <p class="highlight"><strong>Tổng điểm:</strong> ${user.tongDiem}</p>
      </div>

      <div class="card order-history">
        <h2>Lịch Sử Đơn Hàng</h2>
        <c:choose>
          <c:when test="${not empty donHangList}">
            <table>
              <thead>
                <tr>
                  <th>Mã Đơn Hàng</th>
                  <th>Ngày Đặt</th>
                  <th>Tổng Tiền</th>
                  <th>Trạng Thái</th>
                </tr>
              </thead>
              <tbody>
                <c:forEach var="donHang" items="${donHangList}">
                  <tr>
                    <td>${donHang.maDonHang}</td>
                    <td>
                      <fmt:formatDate
                        value="${donHang.ngayDat}"
                        pattern="dd/MM/yyyy"
                      />
                    </td>
                    <td>
                      <fmt:formatNumber
                        value="${donHang.tongTien}"
                        pattern="#,###đ"
                      />
                    </td>
                    <td>${donHang.datHang ? 'Đã đặt' : 'Chưa đặt'}</td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
          </c:when>
          <c:otherwise>
            <p class="no-orders">Chưa có đơn hàng nào.</p>
          </c:otherwise>
        </c:choose>
      </div>
    </div>

    <footer class="footer">
      <div class="footer-content">
        <div class="footer-section">
          <h3>About Galaxy Cinema</h3>
          <p>
            Your premier destination for the latest movies and entertainment
            experiences.
          </p>
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
