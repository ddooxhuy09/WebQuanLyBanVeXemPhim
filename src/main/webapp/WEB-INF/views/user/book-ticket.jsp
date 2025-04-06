<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Booking Seat - Galaxy Cinema</title>
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
        .booking-container {
            max-width: 1200px;
            margin: 2rem auto;
            padding: 0 1rem;
        }
        .movie-info-summary {
            background-color: #f5f5f5;
            padding: 1.5rem;
            border-radius: 8px;
            margin-bottom: 2rem;
        }
        .movie-info-summary h2 {
            color: #333;
            margin-bottom: 1rem;
        }
        .booking-details {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1rem;
        }
        .screen-container {
            text-align: center;
            margin: 2rem 0;
        }
        .screen {
            width: 80%;
            height: 40px;
            margin: 0 auto;
            background: linear-gradient(to bottom, #ffffff, #e0e0e0);
            border-radius: 50%/100% 100% 0 0;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: bold;
            color: #666;
            box-shadow: 0 3px 10px rgba(0, 0, 0, 0.1);
        }
        .seating-map {
            display: flex;
            flex-direction: column;
            align-items: center;
            padding: 2rem;
            background-color: #f9f9f9;
            border-radius: 8px;
            margin: 2rem 0;
        }
        .seat-row {
            display: flex;
            flex-direction: row;
            align-items: center;
            margin: 10px 0;
        }
        .row-label {
            width: 40px;
            font-weight: bold;
            text-align: center;
        }
        .seat {
            width: 40px;
            height: 40px;
            margin: 0 5px;
            text-align: center;
            line-height: 40px;
            cursor: pointer;
            border: 1px solid #ccc;
            border-radius: 4px;
            background-color: #fff;
        }
        .seat.double {
            width: 85px;
            background-color: #e6f3ff;
        }
        .seat.available:hover {
            background-color: #e3f2fd;
        }
        .seat.selected {
            background-color: #1a73e8 !important;
            color: white !important;
            border-color: #1976d2 !important;
        }
        .seat.occupied {
            background-color: #ffd700;
            cursor: not-allowed;
            border-color: #bdbdbd;
        }
        .seat-legend {
            display: flex;
            justify-content: center;
            gap: 20px;
            margin: 20px 0;
        }
        .legend-item {
            display: flex;
            align-items: center;
            gap: 5px;
        }
        .seat-example {
            width: 20px;
            height: 20px;
            border: 1px solid #ccc;
        }
        .seat-example.available {
            background-color: #fff;
        }
        .seat-example.selected {
            background-color: #1a73e8;
        }
        .seat-example.occupied {
            background-color: #ffd700;
        }
        .seat-example.double {
            width: 40px;
            background-color: #e6f3ff;
        }
        .booking-summary {
            background-color: #f5f5f5;
            padding: 1.5rem;
            border-radius: 8px;
            margin-top: 2rem;
        }
        .booking-summary h3 {
            color: #333;
            margin-bottom: 1rem;
        }
        .selected-seats, .price-summary {
            margin: 1rem 0;
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
                    <li><a href="${pageContext.request.contextPath}/user/auth/logout" class="login-btn">Đăng Xuất</a></li>
                </c:when>
                <c:otherwise>
                    <li><a href="${pageContext.request.contextPath}/user/auth/login" class="login-btn">Đăng Nhập</a></li>
                </c:otherwise>
            </c:choose>
        </ul>
    </nav>

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
                <p><strong>Rạp:</strong> ${rapChieu.tenRapChieu}</p> <!-- Sửa tenRap thành tenRapChieu -->
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
                            <c:set var="isOccupied" value="${occupiedSeats.contains(seatId)}" />
                            <div id="seat-${seatId}" 
                                 class="seat ${isDouble ? 'double' : ''} ${isOccupied ? 'occupied' : 'available'}"
                                 data-seat-id="${seatId}" 
                                 data-is-occupied="${isOccupied}">
                                ${seatId}
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
                <button type="submit" class="confirm-btn">Xác nhận đặt vé</button>
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
        document.addEventListener('DOMContentLoaded', function() {
            document.querySelectorAll('.seat').forEach(seat => {
                if (seat.classList.contains('selected')) {
                    seat.classList.remove('selected');
                }
            });
            
            selectedSeats = [];
            console.log("Reset all seats, selectedSeats:", selectedSeats);
            
            const availableSeats = document.querySelectorAll('.seat.available');
            console.log("Found available seats:", availableSeats.length);
            
            availableSeats.forEach(seat => {
                seat.addEventListener('click', function(e) {
                    e.stopPropagation();
                    const seatId = this.getAttribute('data-seat-id');
                    console.log("Seat directly clicked:", seatId);
                    toggleSeatSelection(this, seatId);
                });
            });
            
            updateSummary();
        });

        let selectedSeats = [];
        const ticketPrice = ${phim.giaVe != null ? phim.giaVe : 90000};

        function toggleSeatSelection(seatElement, seatId) {
            if (seatElement.classList.contains('occupied')) {
                console.log("Cannot select occupied seat:", seatId);
                return;
            }
            
            console.log("Toggling seat selection for:", seatId);
            console.log("Current classList:", Array.from(seatElement.classList));
            
            if (selectedSeats.includes(seatId)) {
                selectedSeats = selectedSeats.filter(id => id !== seatId);
                seatElement.classList.remove("selected");
                console.log("Seat deselected:", seatId);
            } else {
                selectedSeats.push(seatId);
                seatElement.classList.add("selected");
                console.log("Seat selected:", seatId);
            }
            
            console.log("After toggle, classList:", Array.from(seatElement.classList));
            console.log("Updated selectedSeats array:", selectedSeats);
            updateSummary();
        }

        function updateSummary() {
            document.getElementById("selected-seats-display").textContent = selectedSeats.join(", ");
            document.getElementById("total-price").textContent = (selectedSeats.length * ticketPrice) + "đ";
            document.getElementById("selected-seats-input").value = selectedSeats.join(",");
            console.log("Summary updated. Selected seats sent to server:", selectedSeats);
        }

        document.getElementById("bookingForm").addEventListener("submit", function(event) {
            if (selectedSeats.length === 0) {
                event.preventDefault();
                alert("Vui lòng chọn ít nhất một ghế!");
            }
        });
    </script>
</body>
</html>