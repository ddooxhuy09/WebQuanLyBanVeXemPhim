<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/user/css/styles.css?v=1.0">
    <title>Chọn Combo - Galaxy Cinema</title>
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
        #countdown-timer {
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
        <c:if test="${empty maPhim or empty maSuatChieu or empty selectedSeats}">
            <div class="error-message">Lỗi: Thiếu thông tin đặt vé. Vui lòng chọn lại ghế.</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/booking/update-seats" method="post" style="display: inline;">
            <input type="hidden" name="maPhim" value="${maPhim}">
            <input type="hidden" name="maSuatChieu" value="${maSuatChieu}">
            <input type="hidden" name="selectedSeats" value="${selectedSeats}">
            <input type="hidden" name="fromSelectFood" value="true">
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
            <div class="progress-step active" onclick="goToStep(3)">
                <div class="circle">3</div>
                <span>Chọn đồ ăn</span>
            </div>
            <div class="progress-step" onclick="goToStep(4)">
                <div class="circle">4</div>
                <span>Thanh toán</span>
            </div>
        </div>

        <c:if test="${not empty sessionScope.selectedSeats}">
            <div class="timer-container">
                <span>Thời gian giữ ghế: </span>
                <span id="countdown-timer"></span>
            </div>
        </c:if>

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
                                    <img src="${pageContext.request.contextPath}/resources/user/images/placeholder.jpg" alt="${combo.tenCombo}" />
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
                                    <img src="${pageContext.request.contextPath}/resources/user/images/placeholder.jpg" alt="${bapNuoc.tenBapNuoc}" />
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
        document.addEventListener("DOMContentLoaded", function() {
            startCountdownTimer();
        });

        function startCountdownTimer() {
            const timerDisplay = document.getElementById('countdown-timer');
            if (!timerDisplay) {
                console.log("Timer display element not found.");
                return;
            }

            console.log("Starting countdown timer...");
            let timeLeft = parseInt(sessionStorage.getItem('countdownTime'));
            if (!timeLeft) {
                timeLeft = 300; // 5 phút
                sessionStorage.setItem('countdownTime', timeLeft);
            }
            console.log("Initial timeLeft:", timeLeft);

            function updateTimer() {
                if (timeLeft <= 0) {
                    clearInterval(timerInterval);
                    alert("Hết thời gian giữ ghế! Vui lòng chọn lại ghế.");
                    sessionStorage.removeItem('countdownTime');
                    window.location.href = "${pageContext.request.contextPath}/booking/select-seats?maPhim=${maPhim}&maSuatChieu=${maSuatChieu}";
                    return;
                }

                const minutes = Math.floor(timeLeft / 60);
                const seconds = timeLeft % 60;
                timerDisplay.textContent = minutes + ':' + (seconds < 10 ? '0' : '') + seconds;
                console.log("Time left:", timeLeft, "Display:", timerDisplay.textContent);
                timeLeft--;
                sessionStorage.setItem('countdownTime', timeLeft);
            }

            updateTimer();
            const timerInterval = setInterval(updateTimer, 1000);

            document.getElementById('selectionForm').addEventListener('submit', function() {
                clearInterval(timerInterval);
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
                const fromSelectFoodInput = document.createElement("input");
                fromSelectFoodInput.type = "hidden";
                fromSelectFoodInput.name = "fromSelectFood";
                fromSelectFoodInput.value = "true";
                form.appendChild(maPhimInput);
                form.appendChild(maSuatChieuInput);
                form.appendChild(selectedSeatsInput);
                form.appendChild(fromSelectFoodInput);
                document.body.appendChild(form);
                form.submit();
            }
        }

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