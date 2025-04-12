<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/user/css/styles.css">
    <title>Booking Seat - Galaxy Cinema</title>
    <style>
        .seat-row { display: flex; align-items: center; margin: 10px 0; }
        .row-label { width: 30px; font-weight: bold; }
        .seat { width: 40px; height: 40px; margin: 5px; text-align: center; line-height: 40px; cursor: pointer; border: 1px solid #ccc; }
        .seat.available { background-color: #fff; }
        .seat.selected { background-color: #ffd700; }
        .seat.occupied { background-color: #ccc; cursor: not-allowed; }
        .screen { background-color: #333; color: white; text-align: center; padding: 10px; margin: 20px 0; }
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

    <div class="booking-container">
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
            <c:forEach var="i" begin="0" end="${soHang - 1}">
                <div class="seat-row">
                    <span class="row-label">
                        <c:out value="${Character.toString((char)(65 + i))}" />
                    </span>
                    <c:forEach var="j" begin="1" end="${soCot}">
                        <c:set var="seatId" value="${Character.toString((char)(65 + i))}${j}" />
                        <c:choose>
                            <c:when test="${occupiedSeats.contains(seatId)}">
                                <div class="seat occupied">${seatId}</div>
                            </c:when>
                            <c:otherwise>
                                <div class="seat available" onclick="selectSeat('${seatId}')">${seatId}</div>
                            </c:otherwise>
                        </c:choose>
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
        </div>

        <div class="booking-summary">
            <h3>Thông tin đặt vé</h3>
            <form action="${pageContext.request.contextPath}/booking/confirm-booking" method="post" id="bookingForm">
                <input type="hidden" name="maPhim" value="${phim.maPhim}">
                <input type="hidden" name="maSuatChieu" value="${suatChieu.maSuatChieu}">
                <input type="hidden" name="selectedSeats" id="selected-seats-input">
                <div class="selected-seats">
                    <p>Ghế đã chọn: <span id="selected-seats-display"></span></p>
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
        let selectedSeats = [];
        const ticketPrice = ${phim.giaVe != null ? phim.giaVe : 90000};

        function selectSeat(seatId) {
            const seatElement = document.querySelector(`.seat[onclick="selectSeat('${seatId}')"]`);
            if (seatElement.classList.contains("occupied")) return;

            if (selectedSeats.includes(seatId)) {
                selectedSeats = selectedSeats.filter(id => id !== seatId);
                seatElement.classList.remove("selected");
            } else {
                selectedSeats.push(seatId);
                seatElement.classList.add("selected");
            }
            updateSummary();
        }

        function updateSummary() {
            document.getElementById("selected-seats-display").textContent = selectedSeats.join(", ");
            document.getElementById("total-price").textContent = (selectedSeats.length * ticketPrice) + "đ";
            document.getElementById("selected-seats-input").value = selectedSeats.join(",");
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