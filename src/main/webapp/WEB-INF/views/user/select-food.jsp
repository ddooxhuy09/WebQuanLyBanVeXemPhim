<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/user/css/styles.css?v=1.2">
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
            color: red;
        }
        .booking-container {
            max-width: 1200px;
            margin: 2rem auto;
            display: flex;
            gap: 20px;
        }
        .booking-left {
            flex: 1;
        }
        .booking-right {
            flex: 1;
            max-width: 400px;
        }
        .movie-info-right {
            background-color: #f5f5f5;
            padding: 1.5rem;
            border-radius: 8px;
            text-align: center;
        }
        .movie-info-right img {
            max-width: 100%;
            height: auto;
            border-radius: 8px;
        }
        .movie-info-right h2 {
            color: #333;
            margin: 1rem 0;
        }
        .movie-info-right p {
            margin: 0.5rem 0;
            color: #666;
        }
        .movie-info-right .price {
            font-size: 1.2rem;
            font-weight: bold;
            color: #ff5722;
        }
        .movie-info-right .booking-summary {
            text-align: left;
            margin-top: 20px;
            padding: 15px;
            background-color: #fff;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }
        .movie-info-right .booking-summary h3 {
            margin-top: 0;
            color: #333;
        }
        .movie-info-right .booking-summary .selected-seats p,
        .movie-info-right .booking-summary .selected-combos p,
        .movie-info-right .booking-summary .price-summary p {
            margin: 10px 0;
            color: #666;
        }
/* Combo and Bap Nuoc Selection Styling */
.combo-list {
    margin: 20px 0;
    padding: 0;
}

.combo-item {
    display: flex;
    align-items: center;
    background: linear-gradient(145deg, #ffffff, #f0f0f0);
    border: none;
    border-radius: 12px;
    padding: 15px;
    margin-bottom: 15px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.combo-item img {
    width: 80px;
    height: 80px;
    object-fit: cover;
    border-radius: 8px;
    margin-right: 20px;
    border: 1px solid #e0e0e0;
}

.combo-info {
    flex: 1;
    padding: 10px 0;
}

.combo-info h6 {
    font-size: 1.2rem;
    font-weight: 600;
    color: #222;
    margin: 0 0 8px;
    text-transform: uppercase;
    letter-spacing: 0.5px;
}

.combo-info small {
    font-size: 0.9rem;
    color: #555;
    display: block;
    margin-bottom: 8px;
    line-height: 1.4;
}

.combo-info strong {
    font-size: 1.1rem;
    color: #ff5722;
    font-weight: 700;
}

.quantity-controls {
    display: flex;
    align-items: center;
    gap: 12px;
    background: #fff;
    padding: 8px 12px;
    border-radius: 20px;
    box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.05);
}

.quantity-controls button {
    width: 32px;
    height: 32px;
    background-color: #ff5722;
    color: white;
    border: none;
    border-radius: 50%;
    font-size: 1.2rem;
    line-height: 1;
    cursor: pointer;
}

.quantity-controls button:disabled {
    background-color: #ccc;
    cursor: not-allowed;
}

.quantity-controls span {
    font-size: 1.1rem;
    font-weight: 600;
    color: #333;
    min-width: 30px;
    text-align: center;
}

h5.section-title {
    font-size: 1.5rem;
    font-weight: 700;
    color: #222;
    margin: 30px 0 15px;
    position: relative;
    padding-bottom: 10px;
}

h5.section-title::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    width: 60px;
    height: 3px;
    background-color: #ff5722;
    border-radius: 2px;
}    </style>
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
        <c:if test="${empty maPhim or empty maSuatChieu or empty selectedSeats}">
            <div class="error-message">Lỗi: Thiếu thông tin đặt vé. Vui lòng chọn lại ghế.</div>
        </c:if>
        <c:if test="${not empty sessionScope.selectedSeats}">
            <div class="timer-container">
                <span>Thời gian giữ ghế: </span>
                <span id="countdown-timer"></span>
            </div>
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

        <div class="booking-container">
            <div class="booking-left">
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
                    <input type="hidden" name="selectedSeats" id="selected-seats-input" value="${selectedSeats}">
                    <div class="combo-list">
                        <c:choose>
                            <c:when test="${empty combos}">
                                <p class="text-center">Đang tải danh sách combo...</p>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="combo" items="${combos}">
								    <div class="combo-item">
								        <img src="${pageContext.request.contextPath}/resources/images/${combo.urlHinhAnh}" alt="${combo.tenCombo}" />
								        <div class="combo-info">
								            <h6>${combo.tenCombo}</h6>
								            <small>${combo.moTa}</small><br/>
								            <strong>Giá: <fmt:formatNumber value="${combo.giaCombo}" type="number" maxFractionDigits="0" groupingUsed="true"/>đ</strong>
								        </div>
								        <div class="quantity-controls">
								            <button type="button" onclick="decreaseQuantity('combo_${combo.maCombo}')" 
								                    id="decrease_combo_${combo.maCombo}" 
								                    <c:if test="${empty sessionScope.selectedCombos[combo.maCombo] || sessionScope.selectedCombos[combo.maCombo] == 0}">disabled</c:if>>-</button>
								            <span id="quantity_combo_${combo.maCombo}">${sessionScope.selectedCombos[combo.maCombo] != null ? sessionScope.selectedCombos[combo.maCombo] : 0}</span>
								            <input type="hidden" name="combo_${combo.maCombo}" id="input_combo_${combo.maCombo}" 
								                   value="${sessionScope.selectedCombos[combo.maCombo] != null ? sessionScope.selectedCombos[combo.maCombo] : 0}"
								                   data-price="${combo.giaCombo}">
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
								        <img src="${pageContext.request.contextPath}/resources/images/${bapNuoc.urlHinhAnh}" alt="${bapNuoc.tenBapNuoc}" />
								        <div class="combo-info">
								            <h6>${bapNuoc.tenBapNuoc}</h6>
								            <strong>Giá: <fmt:formatNumber value="${bapNuoc.giaBapNuoc}" type="number" maxFractionDigits="0" groupingUsed="true"/>đ</strong>
								        </div>
								        <div class="quantity-controls">
								            <button type="button" onclick="decreaseQuantity('bapNuoc_${bapNuoc.maBapNuoc}')" 
								                    id="decrease_bapNuoc_${bapNuoc.maBapNuoc}" 
								                    <c:if test="${empty sessionScope.selectedBapNuocs[bapNuoc.maBapNuoc] || sessionScope.selectedBapNuocs[bapNuoc.maBapNuoc] == 0}">disabled</c:if>>-</button>
								            <span id="quantity_bapNuoc_${bapNuoc.maBapNuoc}">${sessionScope.selectedBapNuocs[bapNuoc.maBapNuoc] != null ? sessionScope.selectedBapNuocs[bapNuoc.maBapNuoc] : 0}</span>
								            <input type="hidden" name="bapNuoc_${bapNuoc.maBapNuoc}" id="input_bapNuoc_${bapNuoc.maBapNuoc}" 
								                   value="${sessionScope.selectedBapNuocs[bapNuoc.maBapNuoc] != null ? sessionScope.selectedBapNuocs[bapNuoc.maBapNuoc] : 0}"
								                   data-price="${bapNuoc.giaBapNuoc}">
								            <button type="button" onclick="increaseQuantity('bapNuoc_${bapNuoc.maBapNuoc}')">+</button>
								        </div>
								    </div>
								</c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </form>
            </div>

            <div class="booking-right">
                <div class="movie-info-right">
                    <img src="${pageContext.request.contextPath}/resources/images/${phim.urlPoster}"
                         alt="${phim.tenPhim}"
                         style="width: 200px; height: auto;">
                    <h2>${phim.tenPhim}</h2>
                    <p><strong>Rạp:</strong> ${rapChieu.tenRapChieu} - Rạp ${rapChieu.maRapChieu}</p>
                    <p><strong>Suất:</strong> <fmt:formatDate value="${suatChieu.ngayGioChieu}" pattern="HH:mm" /> - <fmt:formatDate value="${suatChieu.ngayGioChieu}" pattern="dd/MM/yyyy" /></p>
                    <p><strong>Giá ghế đơn:</strong> <span class="price">
                        <fmt:formatNumber value="${phim.giaVe != null ? phim.giaVe : 90000}" type="number" maxFractionDigits="0" groupingUsed="true"/>đ
                    </span></p>
                    <div class="booking-summary">
                        <h3>Thông tin đặt vé</h3>
                        <div class="selected-seats">
                            <p>Ghế đã chọn: <span id="selected-seats-display">${selectedSeats}</span></p>
                        </div>
                        <div class="selected-combos">
                            <p>Combo đã chọn: <span id="selected-combos-display"></span></p>
                            <p>Bắp nước đã chọn: <span id="selected-bapnuocs-display"></span></p>
                        </div>
                        <div class="price-summary">
                            <p>Tổng tiền: <span id="total-price">0đ</span></p>
                        </div>
                        <button type="submit" class="confirm-btn" form="selectionForm">Xác nhận đặt vé</button>
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
            updateSummary();
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
            updateSummary();
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
                updateSummary();
            }
        }

        function updateSummary() {
            // Display selected seats
            const selectedSeats = "${selectedSeats}".split(',').filter(seat => seat.trim() !== '');
            document.getElementById('selected-seats-display').textContent = selectedSeats.join(', ') || 'Chưa chọn ghế';

            // Calculate seat price
            const baseTicketPrice = ${phim.giaVe != null ? phim.giaVe : 90000};
            let seatPrice = 0;
            <c:forEach var="loaiGhe" items="${loaiGheList}">
                seatPrice += (${seatQuantities != null && seatQuantities[loaiGhe.maLoaiGhe] != null ? seatQuantities[loaiGhe.maLoaiGhe] : 0}) * baseTicketPrice * ${loaiGhe.heSoGia};
            </c:forEach>

            // Display and calculate combo price
            let comboSummary = [];
            let comboPrice = 0;
            <c:forEach var="combo" items="${combos}">
                const comboQty_${combo.maCombo} = parseInt(document.getElementById('quantity_combo_${combo.maCombo}').textContent);
                if (comboQty_${combo.maCombo} > 0) {
                    comboSummary.push('${combo.tenCombo} x' + comboQty_${combo.maCombo});
                    comboPrice += comboQty_${combo.maCombo} * ${combo.giaCombo};
                }	
            </c:forEach>
            document.getElementById('selected-combos-display').textContent = comboSummary.join(', ') || 'Chưa chọn combo';

            // Display and calculate bap nuoc price
            let bapNuocSummary = [];
            let bapNuocPrice = 0;
            <c:forEach var="bapNuoc" items="${bapNuocs}">
                const bapNuocQty_${bapNuoc.maBapNuoc} = parseInt(document.getElementById('quantity_bapNuoc_${bapNuoc.maBapNuoc}').textContent);
                if (bapNuocQty_${bapNuoc.maBapNuoc} > 0) {
                    bapNuocSummary.push('${bapNuoc.tenBapNuoc} x' + bapNuocQty_${bapNuoc.maBapNuoc});
                    bapNuocPrice += bapNuocQty_${bapNuoc.maBapNuoc} * ${bapNuoc.giaBapNuoc};
                }
            </c:forEach>
            document.getElementById('selected-bapnuocs-display').textContent = bapNuocSummary.join(', ') || 'Chưa chọn bắp nước';

            // Calculate and display total price
            const totalPrice = seatPrice + comboPrice + bapNuocPrice;
            document.getElementById('total-price').textContent = totalPrice.toLocaleString('vi-VN', { minimumFractionDigits: 0, maximumFractionDigits: 0 }) + 'đ';
        }
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