<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Login & Register - Galaxy Cinema</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: Arial, sans-serif;
        }
        body {
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            background: #f0f2f5;
        }
        .container {
            width: 800px;
            display: flex;
            gap: 40px;
            padding: 20px;
        }
        .form-box {
            background: white;
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
            width: 100%;
        }
        .form-box h2 {
            text-align: center;
            margin-bottom: 30px;
            color: #1a73e8;
        }
        .input-group {
            margin-bottom: 20px;
        }
        .input-group label {
            display: block;
            margin-bottom: 5px;
            color: #333;
        }
        .input-group input {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 16px;
        }
        .btn {
            width: 100%;
            padding: 12px;
            background: #1a73e8;
            border: none;
            border-radius: 5px;
            color: white;
            font-size: 16px;
            cursor: pointer;
            margin-top: 20px;
        }
        .btn:hover {
            background: #1557b0;
        }
        .switch-form {
            text-align: center;
            margin-top: 20px;
            color: #666;
        }
        .switch-form a {
            color: #1a73e8;
            text-decoration: none;
        }
        .error-message, .success-message {
            text-align: center;
            margin-bottom: 20px;
        }
        .error-message { color: red; }
        .success-message { color: green; }
    </style>
</head>
<body>
    <div class="container">
        <!-- Login Form -->
        <div class="form-box login" id="login-form">
            <h2>Đăng Nhập</h2>
            <c:if test="${not empty error}">
                <div class="error-message">${error}</div>
            </c:if>
            <c:if test="${not empty success}">
                <div class="success-message">${success}</div>
            </c:if>
            <form action="${pageContext.request.contextPath}/user/auth/login" method="post">
                <div class="input-group">
                    <label for="login-email">Email</label>
                    <input type="email" id="login-email" name="email" placeholder="Nhập email" required />
                </div>
                <div class="input-group">
                    <label for="login-password">Mật Khẩu</label>
                    <input type="password" id="login-password" name="password" placeholder="Nhập mật khẩu" required />
                </div>
                <button type="submit" class="btn">Đăng Nhập</button>
                <div class="switch-form">
                    Chưa có tài khoản? <a href="#" onclick="toggleForm()">Đăng ký</a>
                </div>
            </form>
        </div>

        <!-- Register Form -->
        <div class="form-box register" id="register-form" style="display: none">
            <h2>Đăng Ký</h2>
            <c:if test="${not empty error}">
                <div class="error-message">${error}</div>
            </c:if>
            <form action="${pageContext.request.contextPath}/user/auth/register" method="post">
                <div class="input-group">
                    <label for="reg-hoKh">Họ</label>
                    <input type="text" id="reg-hoKh" name="hoKh" placeholder="Nhập họ" required />
                </div>
                <div class="input-group">
                    <label for="reg-tenKh">Tên</label>
                    <input type="text" id="reg-tenKh" name="tenKh" placeholder="Nhập tên" required />
                </div>
                <div class="input-group">
                    <label for="reg-phone">Số Điện Thoại</label>
                    <input type="tel" id="reg-phone" name="phone" placeholder="Nhập số điện thoại" required />
                </div>
                <div class="input-group">
                    <label for="reg-email">Email</label>
                    <input type="email" id="reg-email" name="email" placeholder="Nhập email" required />
                </div>
                <div class="input-group">
                    <label for="reg-password">Mật Khẩu</label>
                    <input type="password" id="reg-password" name="password" placeholder="Nhập mật khẩu" required />
                </div>
                <button type="submit" class="btn">Đăng Ký</button>
                <div class="switch-form">
                    Đã có tài khoản? <a href="#" onclick="toggleForm()">Đăng nhập</a>
                </div>
            </form>
        </div>
    </div>

    <script>
        function toggleForm() {
            const loginForm = document.getElementById("login-form");
            const registerForm = document.getElementById("register-form");
            if (loginForm.style.display === "none") {
                loginForm.style.display = "block";
                registerForm.style.display = "none";
            } else {
                loginForm.style.display = "none";
                registerForm.style.display = "block";
            }
        }
    </script>
</body>
</html>