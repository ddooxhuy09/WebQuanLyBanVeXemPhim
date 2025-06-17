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
    <title>Thanh Toán - Galaxy Cinema</title>
    <style>
        .back-btn {
            display: inline-block;
            padding: 12px 24px;
            background-color: #4a4a4a;
            color: white;
            text-decoration: none;
            border-radius: 8px;
            margin: 12px 0;
            font-weight: 500;
            transition: background-color 0.3s ease, transform 0.2s ease;
        }
        .back-btn:hover {
            background-color: #6b6b6b;
            transform: translateY(-2px);
        }
        .timer-container {
            font-size: 16px;
            margin: 12px 0;
            color: #333;
        }
        #timer {
            color: #ff5722;
            font-weight: 600;
        }
        .booking-container {
            max-width: 1200px;
            margin: 2.5rem auto;
            display: flex;
            gap: 24px;
        }
        .booking-left {
            flex: 2;
            background-color: #ffffff;
            padding: 24px;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
        }
        .booking-right {
            flex: 1;
        }
        .payment-section {
            padding: 20px;
            border: 1px solid #e0e0e0;
            border-radius: 12px;
            background-color: #fafafa;
            margin-bottom: 24px;
        }
        .payment-section h5 {
            margin: 0 0 20px;
            font-size: 22px;
            color: #222;
            font-weight: 600;
        }
        .promo-form, .points-form {
            display: flex;
            gap: 12px;
            margin-bottom: 20px;
        }
        .promo-form input, .points-form input {
            flex: 1;
            padding: 12px 16px;
            border: 1px solid #d0d0d0;
            border-radius: 8px;
            font-size: 15px;
            background-color: #ffffff;
            transition: border-color 0.3s ease, box-shadow 0.3s ease;
        }
        .promo-form input:focus, .points-form input:focus {
            outline: none;
            border-color: #ff5722;
            box-shadow: 0 0 0 3px rgba(255, 87, 34, 0.1);
        }
        .promo-form button, .points-form button {
            padding: 12px 24px;
            background-color: #ff5722;
            color: white;
            border: none;
            border-radius: 8px;
            font-size: 15px;
            font-weight: 500;
            cursor: pointer;
            transition: background-color 0.3s ease, transform 0.2s ease;
        }
        .promo-form button:hover, .points-form button:hover {
            background-color: #e64a19;
            transform: translateY(-2px);
        }
        .discount-info, .surcharge-info {
            margin: 20px 0;
            padding: 16px;
            background-color: #ffffff;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
        }
        .discount-info p, .surcharge-info p {
            margin: 8px 0;
            font-size: 14px;
            color: #333;
        }
        .discount-badge {
            background-color: #4caf50;
            color: white;
            padding: 4px 10px;
            border-radius: 6px;
            font-size: 13px;
            font-weight: 500;
        }
        .surcharge-info ul {
            list-style: none;
            padding: 0;
            margin: 0;
        }
        .surcharge-info li {
            margin: 8px 0;
            font-size: 14px;
            color: #333;
        }
        .payment-option {
            display: flex;
            align-items: center;
            padding: 14px;
            margin: 12px 0;
            border: 1px solid #e0e0e0;
            border-radius: 10px;
            background-color: #ffffff;
            cursor: pointer;
            transition: border-color 0.3s ease, background-color 0.3s ease, box-shadow 0.3s ease;
        }
        .payment-option:hover, .payment-option.selected {
            border-color: #ff5722;
            background-color: #fff8f0;
            box-shadow: 0 2px 8px rgba(255, 87, 34, 0.15);
        }
        .payment-option img {
            width: 48px;
            height: auto;
            margin-right: 16px;
        }
        .payment-option label {
            flex: 1;
            font-size: 15px;
            color: #222;
            font-weight: 500;
        }
        .movie-info-right {
            background-color: #f7f7f7;
            padding: 2rem;
            border-radius: 12px;
            text-align: center;
        }
        .movie-info-right img {
            max-width: 100%;
            height: auto;
            border-radius: 10px;
        }
        .movie-info-right h2 {
            color: #222;
            margin: 1.2rem 0;
            font-size: 26px;
            font-weight: 600;
        }
        .movie-info-right p {
            margin: 0.6rem 0;
            color: #555;
            font-size: 14px;
        }
        .movie-info-right .price {
            font-size: 1.3rem;
            font-weight: 600;
            color: #ff5722;
        }
        .movie-info-right .booking-summary {
            text-align: left;
            margin-top: 24px;
            padding: 20px;
            background-color: #ffffff;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
        }
        .movie-info-right .booking-summary h3 {
            margin-top: 0;
            color: #222;
            font-size: 19px;
            font-weight: 600;
        }
        .movie-info-right .booking-summary p {
            margin: 10px 0;
            color: #555;
            font-size: 14px;
        }
        .movie-info-right .booking-summary .confirm-btn {
            width: 100%;
            padding: 14px;
            background-color: #ff5722;
            color: white;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            font-size: 16px;
            font-weight: 500;
            transition: background-color 0.3s ease, transform 0.2s ease;
        }
        .movie-info-right .booking-summary .confirm-btn:hover {
            background-color: #e64a19;
            transform: translateY(-2px);
        }
        .error-message, .success-message {
            padding: 12px;
            margin: 12px 0;
            border-radius: 8px;
        }
        .error-message {
            background-color: #ffe6e6;
            color: #d32f2f;
        }
        .success-message {
            background-color: #e6ffe6;
            color: #2e7d32;
        }
        .error-message a {
            display: inline-block;
            margin-top: 10px;
            padding: 10px 20px;
            background-color: #ff5722;
            color: white;
            text-decoration: none;
            border-radius: 6px;
            font-size: 14px;
            font-weight: 500;
            transition: background-color 0.3s ease, transform 0.2s ease;
        }
        .error-message a:hover {
            background-color: #e64a19;
            transform: translateY(-2px);
        }
        .points-form {
            margin-top: 20px;
        }
        .points-form h6 {
            margin: 0 0 10px;
            font-size: 18px;
            color: #222;
            font-weight: 600;
        }
        .points-form p {
            margin: 0 0 10px;
            font-size: 14px;
            color: #333;
        }
        .points-form input[type="number"] {
            -webkit-appearance: none;
            -moz-appearance: textfield;
        }
        .points-form input[type="number"]::-webkit-inner-spin-button,
        .points-form input[type="number"]::-webkit-outer-spin-button {
            -webkit-appearance: none;
            margin: 0;
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
            <div class="error-message">
                ${error}
                <c:if test="${error.contains('hết hạn')}">
                    <a href="${pageContext.request.contextPath}/booking/select-payment">Thực hiện lại giao dịch</a>
                </c:if>
            </div>
        </c:if>
        <c:if test="${not empty success}">
            <div class="success-message">${success}</div>
        </c:if>
        <c:if test="${not empty sessionScope.selectedSeats}">
            <div class="timer-container">
                <span>Thời gian giữ ghế: </span>
                <span id="timer"></span>
            </div>
        </c:if>
        <form action="${pageContext.request.contextPath}/booking/select-food" method="post" style="display: inline">
            <input type="hidden" name="maPhim" value="${maPhim}" />
            <input type="hidden" name="maSuatChieu" value="${maSuatChieu}" />
            <input type="hidden" name="selectedSeats" value="${selectedSeats}" />
            <button type="submit" class="back-btn">Quay lại</button>
        </form>

        <div class="progress-container">
            <div class="progress-step completed" onclick="goToStep(1)">
                <div class="circle">1</div>
                <span>Chọn phim</span>
            </div>
            <div class="progress-step completed" onclick="goToStep(2)">
                <div class="circle">2</div>
                <span>Chọn ghế</span>
            </div>
            <div class="progress-step completed" onclick="goToStep(3)">
                <div class="circle">3</div>
                <span>Chọn đồ ăn</span>
            </div>
            <div class="progress-step active" onclick="goToStep(4)">
                <div class="circle">4</div>
                <span>Thanh toán</span>
            </div>
        </div>

        <div class="booking-container">
            <div class="booking-left">
                <div class="payment-section">
                    <h5>Khuyến mãi</h5>
                    <form action="${pageContext.request.contextPath}/booking/apply-promo-code" method="post" class="promo-form">
                        <input type="hidden" name="maPhim" value="${maPhim}" />
                        <input type="hidden" name="maSuatChieu" value="${maSuatChieu}" />
                        <input type="hidden" name="selectedSeats" value="${selectedSeats}" />
                        <input type="text" name="promoCode" class="form-control" placeholder="Nhập mã khuyến mãi" value="${promoCode}" />
                        <button type="submit" class="btn-warning">Áp dụng mã</button>
                    </form>

                    <c:if test="${not empty khuyenMai}">
                        <div class="discount-info">
                            <p>
                                <span class="discount-badge">Mã giảm giá</span>
                                ${khuyenMai.moTa}
                            </p>
                            <p>
                                Loại giảm giá: ${khuyenMai.loaiGiamGia == 'Phần trăm' ? 'Giảm ' : 'Giảm cố định '}
                                <strong><fmt:formatNumber value="${khuyenMai.giaTriGiam}" type="number" />
                                ${khuyenMai.loaiGiamGia == 'Phần trăm' ? '%' : 'đ'}</strong>
                            </p>
                            <p>
                                Số tiền giảm:
                                <strong><fmt:formatNumber value="${discountAmount}" type="currency" currencySymbol="đ" groupingUsed="true" /></strong>
                            </p>
                        </div>
                    </c:if>

                    <div class="points-form">
                        <h6>Sử dụng điểm tích lũy</h6>
                        <p>Số điểm hiện có: <strong>${sessionScope.loggedInUser.tongDiem}</strong></p>
                        <form action="${pageContext.request.contextPath}/booking/apply-points" method="post" class="points-form">
                            <input type="hidden" name="maPhim" value="${maPhim}" />
                            <input type="hidden" name="maSuatChieu" value="${maSuatChieu}" />
                            <input type="hidden" name="selectedSeats" value="${selectedSeats}" />
                            <input type="hidden" name="promoCode" value="${promoCode}" />
                            <input type="number" name="points" class="form-control" placeholder="Nhập số điểm muốn sử dụng" min="0" value="${appliedPoints != null ? appliedPoints : ''}" />
                            <button type="submit" class="btn-warning">Áp dụng điểm</button>
                        </form>
                        <c:if test="${not empty appliedPoints && appliedPoints > 0}">
                            <div class="discount-info">
                                <p>
                                    <span class="discount-badge">Điểm tích lũy</span>
                                    Đã sử dụng ${appliedPoints} điểm
                                </p>
                                <p>
                                    Số tiền giảm:
                                    <strong><fmt:formatNumber value="${pointsDiscount}" type="currency" currencySymbol="đ" groupingUsed="true" maxFractionDigits="0" /></strong>
                                </p>
                            </div>
                        </c:if>
                    </div>
                </div>

                <div class="payment-section">
                    <h5>Phương thức thanh toán</h5>
                    <form action="${pageContext.request.contextPath}/booking/confirm-payment" method="post" id="paymentForm">
                        <input type="hidden" name="promoCode" value="${promoCode}" />
                        <input type="hidden" name="selectedSeats" value="${selectedSeats}" />
                        <div class="payment-option" id="zalopay_option" onclick="selectPaymentMethod('zalopay')">
                            <img src="${pageContext.request.contextPath}/resources/images/zalopay.png" alt="ZaloPay" />
                            <label>ZaloPay - Nhập mã GIAMSAU - Giảm 50% tối đa 40K</label>
                            <input type="radio" name="paymentMethod" value="zalopay" ${paymentMethod == 'zalopay' ? 'checked' : ''} style="display:none;">
                        </div>
                        <div class="payment-option" id="vnpay_option" onclick="selectPaymentMethod('vnpay')">
                            <img src="${pageContext.request.contextPath}/resources/images/vnpay.png" alt="VNPAY" />
                            <label>VNPAY</label>
                            <input type="radio" name="paymentMethod" value="vnpay" ${paymentMethod == 'vnpay' ? 'checked' : ''} style="display:none;">
                        </div>
                    </form>
                </div>

                <c:if test="${not empty phuThuList}">
                    <div class="surcharge-info">
                        <p><strong>Phụ thu:</strong></p>
                        <ul>
                            <c:forEach var="pt" items="${phuThuList}">
                                <li>
                                    ${pt.tenPhuThu}:
                                    <fmt:formatNumber value="${pt.gia}" type="currency" currencySymbol="đ" groupingUsed="true" />
                                </li>
                            </c:forEach>
                        </ul>
                        <p>
                            <strong>Tổng phụ thu (cho ${fn:length(selectedSeats.split(','))} vé):</strong>
                            <fmt:formatNumber value="${tongPhuThu * fn:length(selectedSeats.split(','))}" type="currency" currencySymbol="đ" groupingUsed="true" />
                        </p>
                    </div>
                </c:if>
            </div>

            <div class="booking-right">
                <div class="movie-info-right">
                    <img src="${pageContext.request.contextPath}/resources/images/${phim.urlPoster}" alt="${phim.tenPhim}" style="width: 200px; height: auto" />
                    <h2>${phim.tenPhim}</h2>
                    <p><strong>Rạp:</strong> ${rapChieu.tenRapChieu} - Rạp ${rapChieu.maRapChieu}</p>
                    <p>
                        <strong>Suất:</strong>
                        <fmt:formatDate value="${suatChieu.ngayGioChieu}" pattern="HH:mm" /> -
                        <fmt:formatDate value="${suatChieu.ngayGioChieu}" pattern="dd/MM/yyyy" />
                    </p>
                    <p>
                        <strong>Giá ghế đơn:</strong>
                        <span class="price"><fmt:formatNumber value="${phim.giaVe != null ? phim.giaVe : 90000}" pattern="#,###" />đ</span>
                    </p>
                    <div class="booking-summary">
                        <h3>Thông tin đặt vé</h3>
                        <p><strong>Ghế đã chọn:</strong> ${selectedSeats}</p>
                        <div class="price-details">
                            <c:if test="${not empty selectedSeats}">
                                <c:forEach var="seatId" items="${selectedSeats.split(',')}">
                                    <c:if test="${not empty vePrices[seatId]}">
                                        <p>
                                            Ghế ${seatId}:
                                            <fmt:formatNumber value="${vePrices[seatId]}" type="currency" currencySymbol="đ" groupingUsed="true" maxFractionDigits="0" />
                                        </p>
                                    </c:if>
                                </c:forEach>
                            </c:if>
                            <c:if test="${not empty selectedCombos}">
                                <p><strong>Combo:</strong></p>
                                <c:forEach var="combo" items="${selectedCombos}">
                                    <p>
                                        ${comboNames[combo.key] != null ? comboNames[combo.key] : combo.key} (x${combo.value}):
                                        <fmt:formatNumber value="${comboPrices[combo.key]}" type="currency" currencySymbol="đ" groupingUsed="true" maxFractionDigits="0" />
                                    </p>
                                </c:forEach>
                            </c:if>
                            <c:if test="${not empty selectedBapNuocs}">
                                <p><strong>Bắp nước:</strong></p>
                                <c:forEach var="bapNuoc" items="${selectedBapNuocs}">
                                    <p>
                                        ${bapNuocNames[bapNuoc.key] != null ? bapNuocNames[bapNuoc.key] : bapNuoc.key} (x${bapNuoc.value}):
                                        <fmt:formatNumber value="${bapNuocPrices[bapNuoc.key]}" type="currency" currencySymbol="đ" groupingUsed="true" maxFractionDigits="0" />
                                    </p>
                                </c:forEach>
                            </c:if>
                            <c:if test="${not empty phuThuList}">
                                <p>
                                    <strong>Phụ thu (${fn:length(selectedSeats.split(','))} vé):</strong>
                                    <fmt:formatNumber value="${tongPhuThu * fn:length(selectedSeats.split(','))}" type="currency" currencySymbol="đ" groupingUsed="true" maxFractionDigits="0" />
                                </p>
                            </c:if>
                            <c:if test="${not empty discountAmount && discountAmount > 0}">
                                <p>
                                    <strong>Giảm giá:</strong>
                                    <fmt:formatNumber value="${discountAmount}" type="currency" currencySymbol="đ" groupingUsed="true" maxFractionDigits="0" />
                                </p>
                            </c:if>
                            <c:if test="${not empty appliedPoints && appliedPoints > 0}">
                                <p>
                                    <strong>Giảm từ điểm:</strong>
                                    <fmt:formatNumber value="${pointsDiscount}" type="currency" currencySymbol="đ" groupingUsed="true" maxFractionDigits="0" />
                                </p>
                            </c:if>
                            <p>
                                <strong>Tổng tiền:</strong>
                                <fmt:formatNumber value="${tongTien}" type="currency" currencySymbol="đ" groupingUsed="true" maxFractionDigits="0" />
                            </p>
                        </div>
                        <button type="submit" form="paymentForm" class="confirm-btn">Xác nhận thanh toán</button>
                    </div>
                </div>
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
        let timeLeft = calculateTimeLeft();
        let timerId;

        function calculateTimeLeft() {
            const reservationStartTime = ${sessionScope.reservationStartTime != null ? sessionScope.reservationStartTime : 0};
            if (reservationStartTime) {
                const now = new Date().getTime();
                const elapsed = now - reservationStartTime;
                const remaining = (5 * 60 * 1000) - elapsed;
                return remaining > 0 ? Math.floor(remaining / 1000) : 0;
            }
            return 5 * 60;
        }

        function startBookingTimer() {
            const timerDisplay = document.getElementById('timer');
            if (!timerDisplay) return;

            function updateTimer() {
                if (timeLeft <= 0) {
                    clearInterval(timerId);
                    alert("Hết thời gian giữ ghế! Vui lòng chọn lại ghế.");
                    sessionStorage.removeItem("timeLeft");
                    window.location.href = "${pageContext.request.contextPath}/booking/select-seats?maPhim=${maPhim}&maSuatChieu=${maSuatChieu}";
                    return;
                }
                const minutes = Math.floor(timeLeft / 60);
                const seconds = timeLeft % 60;
                timerDisplay.textContent = minutes + ':' + (seconds < 10 ? '0' : '') + seconds;
                timeLeft--;
                sessionStorage.setItem("timeLeft", timeLeft);
            }

            updateTimer();
            timerId = setInterval(updateTimer, 1000);

            document.getElementById("paymentForm").addEventListener("submit", function() {
                clearInterval(timerId);
            });
        }

        function goToStep(step) {
            const form = document.createElement("form");
            form.method = "post";
            form.action = step === 1 ? "${pageContext.request.contextPath}/movie-detail?id=${maPhim}" :
                          step === 2 ? "${pageContext.request.contextPath}/booking/update-seats" :
                          "${pageContext.request.contextPath}/booking/select-food";
            const inputs = [
                { name: "maPhim", value: "${maPhim}" },
                { name: "maSuatChieu", value: "${maSuatChieu}" },
                { name: "selectedSeats", value: "${selectedSeats}" }
            ];
            if (step !== 1) {
                inputs.forEach(input => {
                    const el = document.createElement("input");
                    el.type = "hidden";
                    el.name = input.name;
                    el.value = input.value;
                    form.appendChild(el);
                });
            }
            document.body.appendChild(form);
            form.submit();
        }

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

        document.addEventListener("DOMContentLoaded", function() {
            const checkedInput = document.querySelector('input[name="paymentMethod"]:checked');
            if (checkedInput) {
                const parentOption = checkedInput.closest('.payment-option');
                if (parentOption) parentOption.classList.add('selected');
            } else {
                const firstOption = document.querySelector('.payment-option');
                if (firstOption) {
                    firstOption.classList.add('selected');
                    firstOption.querySelector('input').checked = true;
                }
            }
            if (${not empty sessionScope.selectedSeats} && timeLeft > 0) {
                startBookingTimer();
            }
        });

        window.addEventListener("beforeunload", () => {
            sessionStorage.setItem("timeLeft", timeLeft);
        });
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