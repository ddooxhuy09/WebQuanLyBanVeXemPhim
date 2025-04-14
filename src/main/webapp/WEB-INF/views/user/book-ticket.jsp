<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/user/css/styles.css?v=1.0">
    <title>Booking Seat - Galaxy Cinema</title>
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

    <!-- Thanh tiến trình -->
    <div class="progress-container">
        <div class="progress-step completed" onclick="goToStep(1)">
            <div class="circle">1</div>
            <span>Chọn phim</span>
        </div>
        <div class="progress-step active" onclick="goToStep(2)">
            <div class="circle">2</div>
            <span>Chọn ghế</span>
        </div>
        <div class="progress-step" onclick="goToStep(3)">
            <div class="circle">3</div>
            <span>Chọn đồ ăn</span>
        </div>
        <div class="progress-step" onclick="goToStep(4)">
            <div class="circle">4</div>
            <span>Thanh toán</span>
        </div>
    </div>

    <!-- Bộ đếm giờ -->
    <div class="booking-container">
        <c:if test="${not empty error}">
            <div class="error-message">${error}</div>
        </c:if>
        <c:if test="${not empty success}">
            <div class="success-message">${success}</div>
        </c:if>

        <div class="movie-info-summary">
            <h2>${phim.tenPhim}</h2>
            <div class="booking-details">
                <p><strong>Rạp:</strong> ${rapChieu.tenRapChieu}</p>
                <p><strong>Suất chiếu:</strong> <fmt:formatDate value="${suatChieu.ngayGioChieu}" pattern="HH:mm" /></p>
                <p><strong>Ngày:</strong> <fmt:formatDate value="${suatChieu.ngayGioChieu}" pattern="dd/MM/yyyy" /></p>
            </div>
        </div>

        <div class="screen-container">
            <div class="screen">SCREEN</div>
        </div>

        <div class="seating-map">
            <c:forEach var="row" items="${rowLabels}">
                <div class="seat-row">
                    <span class="row-label">${row}</span>
                    <c:forEach var="ghe" items="${gheList}">
                        <c:if test="${ghe.tenHang eq row}">
                            <c:set var="seatId" value="${ghe.tenHang}${ghe.soGhe}" />
                            <c:set var="isDouble" value="${ghe.loaiGhe.tenLoaiGhe eq 'Đôi'}" />
                            <c:set var="isPaid" value="${paidSeats.contains(seatId)}" />
                            <c:set var="isReserved" value="${reservedSeats.contains(seatId)}" />
                            <div id="seat-${seatId}" 
                                 class="seat ${isDouble ? 'double' : ''} ${isPaid ? 'occupied' : isReserved ? 'reserved' : 'available'}"
                                 data-seat-id="${seatId}" 
                                 data-is-paid="${isPaid}"
                                 data-is-reserved="${isReserved}"
                                 data-he-so-gia="${ghe.loaiGhe.heSoGia}"
                                 data-reserve-time="${seatReservationTimes[seatId] != null ? seatReservationTimes[seatId] : ''}">
                                ${seatId}
                                <c:if test="${isReserved}">
                                    <span class="timer" id="timer-${seatId}"></span>
                                </c:if>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>
            </c:forEach>
        </div>

        <div class="seat-legend">
            <div class="legend-item">
                <div class="seat-example available"></div>
                <span>Ghế trống</span>
            </div>
            <div class="legend-item">
                <div class="seat-example selected"></div>
                <span>Ghế đang chọn</span>
            </div>
            <div class="legend-item">
                <div class="seat-example occupied"></div>
                <span>Ghế đã đặt</span>
            </div>
            <div class="legend-item">
                <div class="seat-example reserved"></div>
                <span>Ghế đang giữ</span>
            </div>
            <div class="legend-item">
                <div class="seat-example double"></div>
                <span>Ghế đôi</span>
            </div>
        </div>

        <div class="booking-summary">
            <h3>Thông tin đặt vé</h3>
            <form action="${pageContext.request.contextPath}/booking/confirm-booking" method="post" id="bookingForm">
                <input type="hidden" name="maPhim" value="${phim.maPhim}">
                <input type="hidden" name="maSuatChieu" value="${suatChieu.maSuatChieu}">
                <div class="selected-seats">
                    <p>Ghế đã chọn: <span id="selected-seats-display"></span></p>
                    <input type="hidden" name="selectedSeats" id="selected-seats-input">
                </div>
                <div class="price-summary">
                    <p>Tổng tiền: <span id="total-price">0đ</span></p>
                </div>
                <button type="submit" class="confirm-btn" id="confirm-btn">Xác nhận đặt vé</button>
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

    <script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.6.1/sockjs.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
    <script>
        let selectedSeats = [];
        const baseTicketPrice = ${phim.giaVe != null ? phim.giaVe : 90000};
        const RESERVATION_TIMEOUT = 5 * 60 * 1000;
        let timers = {};
        let timeLeft = sessionStorage.getItem("timeLeft") || (10 * 60);
        let timerId;

        document.addEventListener('DOMContentLoaded', function() {
            initializeSeats();
            connectWebSocket();
            startTimers();
            startBookingTimer();
        });

        function startBookingTimer() {
            timerId = setInterval(() => {
                if (timeLeft <= 0) {
                    clearInterval(timerId);
                    alert("Hết thời gian đặt vé! Vui lòng bắt đầu lại.");
                    window.location.href = "${pageContext.request.contextPath}/home/";
                    return;
                }
                const minutes = Math.floor(timeLeft / 60);
                const seconds = timeLeft % 60;
                document.getElementById("timer").textContent = 
                    `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
                timeLeft--;
            }, 1000);
        }

        function goToStep(step) {
            if (step === 1) {
                window.location.href = "${pageContext.request.contextPath}/movie-detail?id=${phim.maPhim}";
            } else if (step > 2) {
                if (selectedSeats.length === 0) {
                    alert("Vui lòng chọn ghế trước khi chuyển sang bước tiếp theo!");
                } else {
                    document.getElementById("bookingForm").submit();
                }
            }
        }

        function initializeSeats() {
            document.querySelectorAll('.seat').forEach(seat => {
                const seatId = seat.getAttribute('data-seat-id');
                const isPaid = seat.getAttribute('data-is-paid') === 'true';
                const isReserved = seat.getAttribute('data-is-reserved') === 'true';
                seat.classList.remove('selected', 'available', 'reserved', 'occupied');
                if (isPaid) {
                    seat.classList.add('occupied');
                    console.log('Seat ' + seatId + ' marked as occupied');
                } else if (isReserved) {
                    seat.classList.add('reserved');
                } else {
                    seat.classList.add('available');
                }
                seat.addEventListener('click', function(e) {
                    e.stopPropagation();
                    toggleSeatSelection(this, seatId);
                });
            });
            updateSummary();
        }

        function toggleSeatSelection(seatElement, seatId) {
            if (seatElement.classList.contains('occupied') || seatElement.classList.contains('reserved')) {
                console.log('Cannot select seat ' + seatId + ': occupied or reserved');
                return;
            }
            if (selectedSeats.includes(seatId)) {
                selectedSeats = selectedSeats.filter(id => id !== seatId);
                seatElement.classList.remove('selected');
            } else {
                selectedSeats.push(seatId);
                seatElement.classList.add('selected');
            }
            updateSummary();
        }

        function updateSummary() {
            document.getElementById('selected-seats-display').textContent = selectedSeats.join(', ');
            document.getElementById('selected-seats-input').value = selectedSeats.join(',');
            let totalPrice = 0;
            selectedSeats.forEach(seatId => {
                const seatElement = document.getElementById('seat-' + seatId);
                const heSoGia = parseFloat(seatElement.getAttribute('data-he-so-gia') || '1');
                totalPrice += baseTicketPrice * heSoGia;
            });
            document.getElementById('total-price').textContent = totalPrice.toLocaleString('vi-VN') + 'đ';
            document.getElementById('confirm-btn').disabled = selectedSeats.length === 0;
        }

        function startTimers() {
            document.querySelectorAll('.seat.reserved').forEach(seat => {
                const seatId = seat.getAttribute('data-seat-id');
                const reserveTime = parseInt(seat.getAttribute('data-reserve-time') || '0');
                if (reserveTime) {
                    updateTimer(seatId, reserveTime);
                }
            });
        }

        function updateTimer(seatId, reserveTime) {
            if (timers[seatId]) {
                clearInterval(timers[seatId]);
            }
            const timerElement = document.getElementById('timer-' + seatId);
            if (!timerElement) return;

            timers[seatId] = setInterval(() => {
                const now = new Date().getTime();
                const elapsed = now - reserveTime;
                const remaining = RESERVATION_TIMEOUT - elapsed;
                if (remaining <= 0) {
                    clearInterval(timers[seatId]);
                    delete timers[seatId];
                    const seatElement = document.getElementById('seat-' + seatId);
                    seatElement.classList.remove('reserved');
                    seatElement.classList.add('available');
                    seatElement.removeAttribute('data-is-reserved');
                    seatElement.querySelector('.timer')?.remove();
                } else {
                    const minutes = Math.floor(remaining / 60000);
                    const seconds = Math.floor((remaining % 60000) / 1000);
                    timerElement.textContent = `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
                }
            }, 1000);
        }

        function connectWebSocket() {
            const socket = new SockJS('${pageContext.request.contextPath}/ws');
            const stompClient = Stomp.over(socket);
            stompClient.connect({}, function(frame) {
                stompClient.subscribe('/topic/seats/${suatChieu.maSuatChieu}', function(message) {
                    const updatedSeats = JSON.parse(message.body);
                    document.querySelectorAll('.seat').forEach(seat => {
                        const seatId = seat.getAttribute('data-seat-id');
                        if (updatedSeats.includes(seatId) && !seat.classList.contains('selected')) {
                            seat.classList.remove('available');
                            seat.classList.add('reserved');
                            seat.setAttribute('data-is-reserved', 'true');
                            let timer = seat.querySelector('.timer');
                            if (!timer) {
                                timer = document.createElement('span');
                                timer.className = 'timer';
                                timer.id = 'timer-' + seatId;
                                seat.appendChild(timer);
                            }
                            const now = new Date().getTime();
                            seat.setAttribute('data-reserve-time', now);
                            updateTimer(seatId, now);
                        }
                    });
                });
                stompClient.subscribe('/topic/paid-seats/${suatChieu.maSuatChieu}', function(message) {
                    const paidSeats = JSON.parse(message.body);
                    document.querySelectorAll('.seat').forEach(seat => {
                        const seatId = seat.getAttribute('data-seat-id');
                        if (paidSeats.includes(seatId)) {
                            seat.classList.remove('available', 'reserved', 'selected');
                            seat.classList.add('occupied');
                            seat.setAttribute('data-is-paid', 'true');
                            seat.setAttribute('data-is-reserved', 'false');
                            seat.querySelector('.timer')?.remove();
                            console.log('WebSocket: Seat ' + seatId + ' marked as paid');
                        }
                    });
                });
            }, function(error) {
                console.error('WebSocket connection error:', error);
            });
        }

        document.getElementById('bookingForm').addEventListener('submit', function(event) {
            if (selectedSeats.length === 0) {
                event.preventDefault();
                alert('Vui lòng chọn ít nhất một ghế!');
            }
        });

        window.addEventListener("beforeunload", () => {
            sessionStorage.setItem("timeLeft", timeLeft);
        });
    </script>
</body>
</html>