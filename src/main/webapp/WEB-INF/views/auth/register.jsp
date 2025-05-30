<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ taglib uri="http://java.sun.com/jsp/jstl/core"
prefix="c" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/resources/user/css/styles.css?v=1.0"
    />
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/resources/user/css/asset/moviecontainer.css?v=1.0"
    />
    <title>Register - Galaxy Cinema</title>
    <style>
      * {
        margin: 0;
        padding: 0;
        box-sizing: border-box;
        font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
      }

      body {
        background: #fff8f4;
        display: flex;
        flex-direction: column;
        min-height: 100vh;
      }

      .container {
        width: 400px;
        padding: 20px;
        margin: auto;
        flex-grow: 1;
        display: flex;
        justify-content: center;
        align-items: center;
      }

      .form-box {
        background: #fff;
        padding: 40px;
        border-radius: 12px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        width: 100%;
      }

      .form-box h2 {
        text-align: center;
        margin-bottom: 30px;
        color: #ff5722;
        font-size: 26px;
        border-bottom: 2px solid #ff5722;
        padding-bottom: 10px;
      }

      .input-group {
        margin-bottom: 20px;
      }

      .input-group label {
        display: block;
        margin-bottom: 6px;
        color: #333;
        font-weight: 600;
      }

      .input-group input {
        width: 100%;
        padding: 10px 14px;
        border: 1px solid #ccc;
        border-radius: 6px;
        font-size: 15px;
        transition: border-color 0.3s;
      }

      .input-group input:focus {
        outline: none;
        border-color: #ff5722;
        box-shadow: 0 0 0 2px rgba(255, 87, 34, 0.2);
      }

      .btn {
        width: 100%;
        padding: 12px;
        background: #ff5722;
        border: none;
        border-radius: 6px;
        color: white;
        font-size: 16px;
        cursor: pointer;
        font-weight: bold;
        transition: background-color 0.3s;
      }

      .btn:hover {
        background: #e64a19;
      }

      .google-btn {
        background: #db4437;
        margin-top: 10px;
      }

      .google-btn:hover {
        background: #c1351d;
      }

      .error-message,
      .success-message {
        text-align: center;
        margin-bottom: 20px;
        padding: 10px;
        border-radius: 5px;
        font-weight: bold;
      }

      .error-message {
        color: #d32f2f;
        background-color: #ffebee;
        border: 1px solid #e57373;
      }

      .success-message {
        color: #388e3c;
        background-color: #e8f5e9;
        border: 1px solid #81c784;
      }

      .login-link,
      .google-register {
        text-align: center;
        margin-top: 20px;
      }

      .login-link a,
      .google-register a {
        color: #fffff;
        text-decoration: none;
        font-weight: bold;
        transition: color 0.3s;
      }

      .login-link a:hover,
      .google-register a:hover {
        color: #e64a19;
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
      <div class="form-box">
        <h2>Đăng Ký</h2>
        <c:if test="${not empty error}">
          <div class="error-message">${error}</div>
        </c:if>
        <c:if test="${not empty success}">
          <div class="success-message">${success}</div>
        </c:if>
        <form
          action="${pageContext.request.contextPath}/auth/register"
          method="post"
        >
          <div class="input-group">
            <label for="hoKh">Họ</label>
            <input
              type="text"
              id="hoKh"
              name="hoKh"
              placeholder="Nhập họ"
              required
            />
          </div>
          <div class="input-group">
            <label for="tenKh">Tên</label>
            <input
              type="text"
              id="tenKh"
              name="tenKh"
              placeholder="Nhập tên"
              required
            />
          </div>
          <div class="input-group">
            <label for="phone">Số điện thoại</label>
            <input
              type="tel"
              id="phone"
              name="phone"
              placeholder="Nhập số điện thoại"
              required
            />
          </div>
          <div class="input-group">
            <label for="email">Email</label>
            <input
              type="email"
              id="email"
              name="email"
              placeholder="Nhập email"
              required
            />
          </div>
          <div class="input-group">
            <label for="password">Mật Khẩu</label>
            <input
              type="password"
              id="password"
              name="password"
              placeholder="Nhập mật khẩu"
              required
            />
          </div>
          <button type="submit" class="btn">Đăng Ký</button>
        </form>
        <div class="google-register">
          <a
            href="${pageContext.request.contextPath}/oauth/google/login"
            class="btn google-btn"
            >Đăng nhập/Đăng ký bằng Google</a
          >
        </div>
        <div class="login-link">
          Đã có tài khoản?
          <a href="${pageContext.request.contextPath}/auth/login">Đăng nhập</a>
        </div>
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
