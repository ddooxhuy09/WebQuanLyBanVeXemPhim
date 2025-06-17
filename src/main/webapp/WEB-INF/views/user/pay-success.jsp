<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/user/css/styles.css?v=1.2" />
    <title>Thanh Toán Thành Công - Galaxy Cinema</title>
    <style>
        .success-container {
            max-width: 1200px;
            margin: 2rem auto;
        }
        .success-left {
            background-color: #fff;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }
        .success-message {
            background-color: #e6ffe6;
            color: #2e7d32;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
            text-align: center;
            font-size: 20px;
            font-weight: bold;
        }
        .success-message i {
            margin-right: 10px;
            font-size: 28px;
        }
        .booking-details {
            padding: 20px;
            border: 1px solid #ddd;
            border-radius: 8px;
            background-color: #f9f9f9;
        }
        .booking-details h3 {
            margin: 0 0 20px;
            font-size: 24px;
            color: #333;
            font-weight: 600;
        }
        .booking-details p {
            margin: 12px 0;
            font-size: 16px;
            color: #333;
            line-height: 1.6;
        }
        .booking-details p strong {
            color: #ff5722;
            font-weight: 700;
        }
        .booking-details .highlight {
            font-size: 18px;
            padding: 10px;
            background-color: #fff;
            border-left: 4px solid #ff5722;
            margin: 10px 0;
            border-radius: 4px;
        }
        .history-btn {
            display: inline-block;
            padding: 12px 30px;
            background-color: #ff5722;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            margin-top: 20px;
            font-size: 16px;
            font-weight: 600;
            text-align: center;
            transition: background-color 0.3s;
        }
        .history-btn:hover {
            background-color: #e64a19;
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
        <div class="success-container">
            <div class="success-left">
                <div class="success-message">
                    <i class="fas fa-check-circle"></i> Thanh toán thành công! Cảm ơn bạn đã đặt vé tại Galaxy Cinema.
                </div>
                <div class="booking-details">
                    <h3>Chi tiết đặt vé</h3>
                    <p class="highlight"><strong>Ghế đã chọn:</strong> ${selectedSeats}</p>
                    <c:if test="${not empty selectedSeats}">
                        <c:forEach var="seatId" items="${selectedSeats.split(',')}">
                            <c:choose>
                                <c:when test="${not empty vePrices[seatId]}">
                                    <p class="highlight">
                                        <strong>Ghế ${seatId}:</strong>
                                        <fmt:formatNumber value="${vePrices[seatId]}" pattern="#,###đ" />
                                    </p>
                                </c:when>
                                <c:otherwise>
                                    <p class="highlight">
                                        <strong>Ghế ${seatId}:</strong> Giá không khả dụng
                                    </p>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </c:if>
                    <c:if test="${not empty selectedCombos}">
                        <p><strong>Combo:</strong></p>
                        <c:forEach var="combo" items="${selectedCombos}">
                            <p class="highlight">
                                ${comboNames[combo.key]} (x${combo.value}):
                                <fmt:formatNumber value="${comboPrices[combo.key]}" pattern="#,###đ" />
                            </p>
                        </c:forEach>
                    </c:if>
                    <c:if test="${not empty selectedBapNuocs}">
                        <p><strong>Bắp nước:</strong></p>
                        <c:forEach var="bapNuoc" items="${selectedBapNuocs}">
                            <p class="highlight">
                                ${bapNuocNames[bapNuoc.key]} (x${bapNuoc.value}):
                                <fmt:formatNumber value="${bapNuocPrices[bapNuoc.key]}" pattern="#,###đ" />
                            </p>
                        </c:forEach>
                    </c:if>
                    <c:if test="${not empty phuThuList}">
                        <p class="highlight">
                            <strong>Phụ thu (${fn:length(selectedSeats.split(','))} vé):</strong>
                            <fmt:formatNumber value="${tongPhuThu * fn:length(selectedSeats.split(','))}" pattern="#,###đ" />
                        </p>
                    </c:if>
                    <c:if test="${not empty discountAmount && discountAmount > 0}">
                        <p class="highlight">
                            <strong>Giảm giá:</strong>
                            <fmt:formatNumber value="${discountAmount}" pattern="#,###đ" />
                        </p>
                    </c:if>
                    <c:if test="${not empty appliedPoints && appliedPoints > 0}">
                        <p class="highlight">
                            <strong>Điểm sử dụng:</strong> ${appliedPoints} điểm
                        </p>
                        <p class="highlight">
                            <strong>Giảm từ điểm:</strong>
                            <fmt:formatNumber value="${pointsDiscount}" pattern="#,###đ" />
                        </p>
                    </c:if>
                    <p class="highlight">
                        <strong>Phương thức thanh toán:</strong> ${paymentMethod == 'zalopay' ? 'ZaloPay' : 'VNPAY'}
                    </p>
                    <p class="highlight">
                        <strong>Tổng tiền:</strong>
                        <fmt:formatNumber value="${tongTien}" pattern="#,###đ" />
                    </p>
                </div>
                <a href="${pageContext.request.contextPath}/user/profile" class="history-btn">Xem lịch sử đặt vé</a>
            </div>
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
        document.addEventListener("DOMContentLoaded", function () {
            sessionStorage.removeItem("timeLeft");
            sessionStorage.removeItem("countdownTime");
        });
    </script>
</body>
</html>