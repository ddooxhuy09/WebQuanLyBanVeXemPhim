<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/user/css/styles.css?v=1.0">
    <title>Thanh Toán - Galaxy Cinema</title>
    <style>
        .back-btn {
            display: inline-block;
            padding: 10px 20px;
            background-color: #555;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            margin: 10px 0;
        }
        .back-btn:hover {
            background-color: #777;
        }
        .timer-container {
            display: block;
            font-size: 16px;
        }
        #timer {
            display: inline;
            color: red; /* Tạm thời để kiểm tra hiển thị */
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
        <form action="${pageContext.request.contextPath}/booking/select-food" method="post" style="display: inline;">
            <input type="hidden" name="maPhim" value="${maPhim}">
            <input type="hidden" name="maSuatChieu" value="${maSuatChieu}">
            <input type="hidden" name="selectedSeats" value="${selectedSeats}">
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

        <c:if test="${not empty sessionScope.selectedSeats}">
            <div class="timer-container">
                <span>Thời gian giữ ghế: </span>
                <span id="timer"></span>
            </div>
        </c:if>

        <div class="container">
            <c:if test="${not empty error}">
                <div class="error-message">${error}</div>
            </c:if>
            <c:if test="${not empty success}">
                <div class="success-message">${success}</div>
            </c:if>

            <div class="card">
                <h5>Khuyến mãi</h5>
                
                <form action="${pageContext.request.contextPath}/booking/apply-promo-code" method="post" class="promo-form">
                    <div class="form-group">
                        <input type="text" name="promoCode" class="form-control" placeholder="Nhập mã khuyến mãi" value="${promoCode}">
                        <button type="submit" class="btn-warning">Áp Dụng</button>
                    </div>
                </form>

                <c:if test="${not empty khuyenMai}">
                    <div class="discount-info">
                        <p><span class="discount-badge">Mã giảm giá</span> ${khuyenMai.moTa}</p>
                        <p>Loại giảm giá: ${khuyenMai.loaiGiamGia == 'Phần trăm' ? 'Giảm ' : 'Giảm cố định '}
                           <strong><fmt:formatNumber value="${khuyenMai.giaTriGiam}" type="number" />
                           ${khuyenMai.loaiGiamGia == 'Phần trăm' ? '%' : 'đ'}</strong>
                        </p>
                        <p>Số tiền giảm: <strong><fmt:formatNumber value="${discountAmount}" type="currency" currencySymbol="đ" groupingUsed="true"/></strong></p>
                    </div>
                </c:if>

                <hr>

                <form action="${pageContext.request.contextPath}/booking/confirm-payment" method="post" id="paymentForm">
                    <input type="hidden" name="promoCode" value="${promoCode}">
                    
                    <h5>Phương thức thanh toán</h5>
                    <div>
                        <div class="payment-option" id="zalopay_option" onclick="selectPaymentMethod('zalopay')">
                            <img src="${pageContext.request.contextPath}/resources/user/images/zalopay.png" alt="ZaloPay">
                            <label>ZaloPay - Nhập mã GIAMSAU - Giảm 50% tối đa 40K</label>
                            <input type="radio" name="paymentMethod" value="zalopay" ${paymentMethod == 'zalopay' ? 'checked' : ''} style="display:none;">
                        </div>
                        <div class="payment-option" id="vnpay_option" onclick="selectPaymentMethod('vnpay')">
                            <img src="${pageContext.request.contextPath}/resources/user/images/vnpay.png" alt="VNPAY">
                            <label>VNPAY</label>
                            <input type="radio" name="paymentMethod" value="vnpay" ${paymentMethod == 'vnpay' ? 'checked' : ''} style="display:none;">
                        </div>
                    </div>

                    <p style="margin-top: 20px; font-size: 18px;">Tổng tiền: <strong><fmt:formatNumber value="${tongTien}" type="currency" currencySymbol="đ" groupingUsed="true"/></strong></p>
                    <button type="submit" class="confirm-btn">Xác nhận thanh toán</button>
                </form>
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
            if (!timerDisplay) {
                console.log("Timer display element not found.");
                return;
            }

            console.log("Starting countdown timer...");
            console.log("Initial timeLeft:", timeLeft);

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
                console.log("Time left:", timeLeft, "Display:", timerDisplay.textContent);
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
            if (step === 1) {
                window.location.href = "${pageContext.request.contextPath}/movie-detail?id=${maPhim}";
            } else if (step === 2) {
                const form = document.createElement("form");
                form.method = "post";
                form.action = "${pageContext.request.contextPath}/booking/update-seats";
                const maPhimInput = document.createElement("input");
                maPhimInput.type = "hidden";
                maPhimInput.name = "maPhim";
                maPhimInput.value = "${maPhim}";
                const maSuatChieuInput = document.createElement("input");
                maSuatChieuInput.type = "hidden";
                maSuatChieuInput.name = "maSuatChieu";
                maSuatChieuInput.value = "${maSuatChieu}";
                const selectedSeatsInput = document.createElement("input");
                selectedSeatsInput.type = "hidden";
                selectedSeatsInput.name = "selectedSeats";
                selectedSeatsInput.value = "${selectedSeats}";
                form.appendChild(maPhimInput);
                form.appendChild(maSuatChieuInput);
                form.appendChild(selectedSeatsInput);
                document.body.appendChild(form);
                form.submit();
            } else if (step === 3) {
                const form = document.createElement("form");
                form.method = "post";
                form.action = "${pageContext.request.contextPath}/booking/select-food";
                const maPhimInput = document.createElement("input");
                maPhimInput.type = "hidden";
                maPhimInput.name = "maPhim";
                maPhimInput.value = "${maPhim}";
                const maSuatChieuInput = document.createElement("input");
                maSuatChieuInput.type = "hidden";
                maSuatChieuInput.name = "maSuatChieu";
                maSuatChieuInput.value = "${maSuatChieu}";
                const selectedSeatsInput = document.createElement("input");
                selectedSeatsInput.type = "hidden";
                selectedSeatsInput.name = "selectedSeats";
                selectedSeatsInput.value = "${selectedSeats}";
                form.appendChild(maPhimInput);
                form.appendChild(maSuatChieuInput);
                form.appendChild(selectedSeatsInput);
                document.body.appendChild(form);
                form.submit();
            }
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
                if (parentOption) {
                    parentOption.classList.add('selected');
                }
            } else {
                const firstOption = document.querySelector('.payment-option');
                if (firstOption) {
                    firstOption.classList.add('selected');
                    const radioInput = firstOption.querySelector('input[type="radio"]');
                    if (radioInput) {
                        radioInput.checked = true;
                    }
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
</body>
</html>