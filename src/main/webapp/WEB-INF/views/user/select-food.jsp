<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chọn Combo - Galaxy Cinema</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
        }
        .navbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            background-color: #fff;
            padding: 10px 20px;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
        }
        .navbar .logo {
            font-size: 24px;
            font-weight: bold;
            color: #ff5722;
        }
        .nav-links {
            list-style: none;
            display: flex;
            gap: 20px;
        }
        .nav-links li {
            list-style: none;
        }
        .nav-links a {
            text-decoration: none;
            color: #333;
            font-size: 16px;
            font-weight: bold;
            transition: color 0.3s;
        }
        .nav-links a:hover {
            color: #ff5722;
        }
        .login-btn {
            background-color: #ff5722;
            color: #fff;
            padding: 8px 16px;
            border-radius: 20px;
            font-weight: bold;
        }
        .login-btn:hover {
            background-color: #e64a19;
        }
        .container {
            max-width: 1200px;
            margin: 2rem auto;
            padding: 0 1rem;
        }
        h5 {
            font-weight: bold;
            margin-bottom: 1rem;
        }
        .combo-list {
            display: flex;
            flex-direction: column;
        }
        .combo-item {
            display: flex;
            align-items: center;
            justify-content: space-between;
            border-bottom: 1px solid #ccc;
            padding: 1rem 0;
        }
        .combo-item img {
            width: 100px;
            height: 70px;
            object-fit: cover;
            border-radius: 8px;
        }
        .combo-info {
            flex-grow: 1;
            margin-left: 1rem;
        }
        .combo-info h6 {
            font-size: 18px;
            font-weight: 600;
            margin-bottom: 0.5rem;
        }
        .combo-info small {
            font-size: 14px;
            font-style: italic;
            color: #666;
        }
        .combo-info strong {
            color: #dc3545;
        }
        .quantity-controls {
            display: flex;
            align-items: center;
        }
        .quantity-controls button {
            width: 30px;
            height: 30px;
            border: 1px solid #ccc;
            background-color: #fff;
            cursor: pointer;
        }
        .quantity-controls button:disabled {
            background-color: #e9ecef;
            cursor: not-allowed;
        }
        .quantity-controls span {
            margin: 0 10px;
            font-size: 16px;
        }
        .confirm-btn {
            width: 100%;
            padding: 1rem;
            background-color: #1a73e8;
            color: white;
            border: none;
            border-radius: 4px;
            font-size: 1rem;
            cursor: pointer;
            margin-top: 2rem;
            transition: background-color 0.3s ease;
        }
        .confirm-btn:hover {
            background-color: #1976d2;
        }
        .error-message {
            color: red;
            text-align: center;
            margin: 20px;
        }
        .success-message {
            color: green;
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

        <h5>Chọn Combo</h5>
        <form action="${pageContext.request.contextPath}/booking/select-payment" method="post" id="selectionForm">
            <input type="hidden" name="maPhim" value="${maPhim}">
            <input type="hidden" name="maSuatChieu" value="${maSuatChieu}">
            <input type="hidden" name="selectedSeats" value="${selectedSeats}">
            <div class="combo-list">
                <c:choose>
                    <c:when test="${empty combos}">
                        <p class="text-center">Đang tải danh sách combo...</p>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="combo" items="${combos}">
                            <div class="combo-item">
                                <img src="https://via.placeholder.com/100" alt="${combo.tenCombo}" />
                                <div class="combo-info">
                                    <h6>${combo.tenCombo}</h6>
                                    <small>${combo.moTa}</small><br/>
                                    <strong>Giá: <fmt:formatNumber value="${combo.giaCombo}" type="currency" currencySymbol="đ" groupingUsed="true"/></strong>
                                </div>
                                <div class="quantity-controls">
                                    <button type="button" onclick="decreaseQuantity('combo_${combo.maCombo}')" 
                                            id="decrease_combo_${combo.maCombo}" 
                                            <c:if test="${empty sessionScope.selectedCombos[combo.maCombo] || sessionScope.selectedCombos[combo.maCombo] == 0}">disabled</c:if>>-</button>
                                    <span id="quantity_combo_${combo.maCombo}">${sessionScope.selectedCombos[combo.maCombo] != null ? sessionScope.selectedCombos[combo.maCombo] : 0}</span>
                                    <input type="hidden" name="combo_${combo.maCombo}" id="input_combo_${combo.maCombo}" 
                                           value="${sessionScope.selectedCombos[combo.maCombo] != null ? sessionScope.selectedCombos[combo.maCombo] : 0}">
                                    <button type="button" onclick="increaseQuantity('combo_${combo.maCombo}')">+</button>
                                </div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>

            <h5 class="section-title">Chọn Bắp Nước</h5>
            <div class="combo-list">
                <c:choose>
                    <c:when test="${empty bapNuocs}">
                        <p class="text-center">Đang tải danh sách bắp nước...</p>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="bapNuoc" items="${bapNuocs}">
                            <div class="combo-item">
                                <img src="https://via.placeholder.com/100" alt="${bapNuoc.tenBapNuoc}" />
                                <div class="combo-info">
                                    <h6>${bapNuoc.tenBapNuoc}</h6>
                                    <strong>Giá: <fmt:formatNumber value="${bapNuoc.giaBapNuoc}" type="currency" currencySymbol="đ" groupingUsed="true"/></strong>
                                </div>
                                <div class="quantity-controls">
                                    <button type="button" onclick="decreaseQuantity('bapNuoc_${bapNuoc.maBapNuoc}')" 
                                            id="decrease_bapNuoc_${bapNuoc.maBapNuoc}" 
                                            <c:if test="${empty sessionScope.selectedBapNuocs[bapNuoc.maBapNuoc] || sessionScope.selectedBapNuocs[bapNuoc.maBapNuoc] == 0}">disabled</c:if>>-</button>
                                    <span id="quantity_bapNuoc_${bapNuoc.maBapNuoc}">${sessionScope.selectedBapNuocs[bapNuoc.maBapNuoc] != null ? sessionScope.selectedBapNuocs[bapNuoc.maBapNuoc] : 0}</span>
                                    <input type="hidden" name="bapNuoc_${bapNuoc.maBapNuoc}" id="input_bapNuoc_${bapNuoc.maBapNuoc}" 
                                           value="${sessionScope.selectedBapNuocs[bapNuoc.maBapNuoc] != null ? sessionScope.selectedBapNuocs[bapNuoc.maBapNuoc] : 0}">
                                    <button type="button" onclick="increaseQuantity('bapNuoc_${bapNuoc.maBapNuoc}')">+</button>
                                </div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
            <button type="submit" class="confirm-btn">Tiếp tục đến thanh toán</button>
        </form>
    </div>

    <!-- Giữ nguyên footer -->
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

    <script>
        function increaseQuantity(itemId) {
            const quantityElement = document.getElementById('quantity_' + itemId);
            const inputElement = document.getElementById('input_' + itemId);
            const decreaseButton = document.getElementById('decrease_' + itemId);
            let currentQuantity = parseInt(quantityElement.textContent);
            currentQuantity++;
            quantityElement.textContent = currentQuantity;
            inputElement.value = currentQuantity;
            decreaseButton.disabled = false;
        }

        function decreaseQuantity(itemId) {
            const quantityElement = document.getElementById('quantity_' + itemId);
            const inputElement = document.getElementById('input_' + itemId);
            const decreaseButton = document.getElementById('decrease_' + itemId);
            let currentQuantity = parseInt(quantityElement.textContent);
            if (currentQuantity > 0) {
                currentQuantity--;
                quantityElement.textContent = currentQuantity;
                inputElement.value = currentQuantity;
                if (currentQuantity === 0) {
                    decreaseButton.disabled = true;
                }
            }
        }
    </script>
</body>
</html>