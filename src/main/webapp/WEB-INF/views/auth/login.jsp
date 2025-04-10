<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Login - Galaxy Cinema</title>
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
            width: 400px;
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
        .error-message, .success-message {
            text-align: center;
            margin-bottom: 20px;
        }
        .error-message { color: red; }
        .success-message { color: green; }
        .register-link {
            text-align: center;
            margin-top: 20px;
        }
        .register-link a {
            color: #1a73e8;
            text-decoration: none;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="form-box">
            <h2>Đăng Nhập</h2>
            <c:if test="${not empty error}">
                <div class="error-message">${error}</div>
            </c:if>
            <c:if test="${not empty success}">
                <div class="success-message">${success}</div>
            </c:if>
            <form action="${pageContext.request.contextPath}/auth/login" method="post">
                <div class="input-group">
                    <label for="username">Email hoặc Tên đăng nhập</label>
                    <input type="text" id="username" name="username" placeholder="Nhập email hoặc tên đăng nhập" required />
                </div>
                <div class="input-group">
                    <label for="password">Mật Khẩu</label>
                    <input type="password" id="password" name="password" placeholder="Nhập mật khẩu" required />
                </div>
                <button type="submit" class="btn">Đăng Nhập</button>
            </form>
            <div class="register-link">
                Chưa có tài khoản? <a href="${pageContext.request.contextPath}/auth/register">Đăng ký</a>
            </div>
        </div>
    </div>
</body>
</html>