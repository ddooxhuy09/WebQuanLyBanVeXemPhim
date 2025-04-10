<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thanh Toán - Galaxy Cinema</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: Arial, sans-serif; background-color: #f4f4f4; }
        .navbar { display: flex; justify-content: space-between; align-items: center; background-color: #fff; padding: 10px 20px; box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1); }
        .navbar .logo { font-size: 24px; font-weight: bold; color: #ff5722; }
        .nav-links { list-style: none; display: flex; gap: 20px; }
        .nav-links li { list-style: none; }
        .nav-links a { text-decoration: none; color: #333; font-size: 16px; font-weight: bold; transition: color 0.3s; }
        .nav-links a:hover { color: #ff5722; }
        .login-btn { background-color: #ff5722; color: #fff; padding: 8px 16px; border-radius: 20px; font-weight: bold; }
        .login-btn:hover { background-color: #e64a19; }
        .container { max-width: 1200px; margin: 2rem auto; padding: 0 1rem; }
        .card { padding: 20px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); border: none; background: #fff; border-radius: 8px; }
        h5 { font-weight: bold; margin-bottom: 1rem; color: #1a73e8; }
        .form-group { display: flex; align-items: center; gap: 10px; }
        .form-control { max-width: 300px; padding: 8px; border: 1px solid #ccc; border-radius: 4px 0 0 4px; }
        .btn-warning { background-color: #ffca28; color: #333; padding: 8px 16px; border: none; border-radius: 0 4px 4px 0; font-weight: bold; }
        .btn-warning:hover { background-color: #ffb300; }
        .payment-option { padding: 15px; border: 1px solid #ddd; border-radius: 4px; margin-bottom: 10px; cursor: pointer; }
        .payment-option.selected { border-color: #1a73e8; background-color: #f8f9fa; }
        .payment-option img { width: 50px; height: 30px; object-fit: contain; margin-right: 15px; }
        .confirm-btn { width: 100%; padding: 1rem; background-color: #1a73e8; color: white; border: none; border-radius: 4px; font-size: 1rem; cursor: pointer; margin-top: 2rem; }
        .confirm-btn:hover { background-color: #1976d2; }
        .error-message { color: red; text-align: center; margin: 20px; }
        .success-message { color: green; text-align: center; margin: 20px; }
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
                    <li><span>Xin chào, ${sessionScope.loggedInUser.tenKhachHang}</span></li>
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
        <c:if test="${not empty success}">
            <div class="success-message">${success}</div>
        </c:if>

        <div class="card">
            <h5>Khuyến mãi</h5>
            <form action="${pageContext.request.contextPath}/booking/confirm-payment" method="post" id="paymentForm">
                <div class="form-group">
                    <input type="text" name="promoCode" class="form-control" placeholder="Nhập mã khuyến mãi" value="${promoCode}">
                    <button type="button" class="btn-warning">Áp Dụng</button>
                </div>

                <hr>

                <h5>Phương thức thanh toán</h5>
                <div>
                    <div class="payment-option" id="zalopay_option" onclick="selectPaymentMethod('zalopay')">
                        <img src="https://cdn.galaxycine.vn/media/2024/7/10/zalopay_1720600308412.png" alt="ZaloPay">
                        <label>ZaloPay - Nhập mã GIAMSAU - Giảm 50% tối đa 40K</label>
                        <input type="radio" name="paymentMethod" value="zalopay" ${paymentMethod == 'zalopay' ? 'checked' : ''} style="display:none;">
                    </div>
                    <div class="payment-option" id="vnpay_option" onclick="selectPaymentMethod('vnpay')">
                        <img src="https://cdn.galaxycine.vn/media/2021/12/2/download_1638460623615.png" alt="VNPAY">
                        <label>VNPAY</label>
                        <input type="radio" name="paymentMethod" value="vnpay" ${paymentMethod == 'vnpay' ? 'checked' : ''} style="display:none;">
                    </div>
                </div>

                <p>Tổng tiền: <fmt:formatNumber value="${tongTien}" type="currency" currencySymbol="đ" groupingUsed="true"/></p>
                <button type="submit" class="confirm-btn">Xác nhận thanh toán</button>
            </form>
        </div>
    </div>

    <script>
        function selectPaymentMethod(method) {
            document.querySelectorAll('.payment-option').forEach(option => {
                option.classList.remove('selected');
                option.querySelector('input').checked = false;
            });
            const selectedOption = document.getElementById(method + '_option');
            if (selectedOption) {
                selectedOption.classList.add('selected');
                selectedOption.querySelector('input').checked = true;
            }
        }

        window.onload = function() {
            const checkedInput = document.querySelector('input[name="paymentMethod"]:checked');
            if (checkedInput) {
                const parentOption = checkedInput.closest('.payment-option');
                if (parentOption) {
                    parentOption.classList.add('selected');
                }
            }
        };
    </script>
</body>
</html>