<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/user/css/styles.css?v=1.2" />
    <title>Movie Detail - Galaxy Cinema</title>
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
        <a href="${pageContext.request.contextPath}/home/" class="back-btn">Quay lại</a>

        <div class="progress-container">
            <div class="progress-step active" onclick="goToStep(1)">
                <div class="circle">1</div>
                <span>Chọn phim</span>
            </div>
            <div class="progress-step" onclick="goToStep(2)">
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

        <c:if test="${not empty error}">
            <div class="error-message" style="text-align: center; color: red; margin: 20px;">
                <p>${error}</p>
            </div>
        </c:if>

        <c:if test="${not empty phim}">
            <c:set var="maPhim" value="${phim.maPhim}" />
            <div class="movie-detail-container">
                <div class="movie-header">
                    <div class="movie-poster">
                        <img src="${pageContext.request.contextPath}/resources/images/${phim.urlPoster}" alt="${phim.tenPhim}" id="movie-poster" />
                    </div>
                    <div class="movie-info">
                        <h1 id="movie-title">${phim.tenPhim}</h1>
                        <div class="movie-meta">
                            <span class="age" id="movie-age">T${phim.doTuoi}</span>
                            <span class="duration" id="movie-duration">${phim.thoiLuong} phút</span>
                        </div>
                        <p class="synopsis" id="movie-synopsis">
                            <c:choose>
                                <c:when test="${not empty phim.moTa}">${phim.moTa}</c:when>
                                <c:otherwise>Không có mô tả</c:otherwise>
                            </c:choose>
                        </p>
                        <div class="movie-details">
                            <p><strong>Nhà sản xuất:</strong> <span id="movie-nhaSx">${phim.nhaSanXuat}</span></p>
                            <p><strong>Quốc gia:</strong> <span id="movie-quocGia">${phim.quocGia}</span></p>
                            <p><strong>Đạo diễn:</strong> <span id="movie-director">${phim.daoDien}</span></p>
                            <p><strong>Diễn viên:</strong> 
                                <span id="movie-cast">
                                    <c:forEach var="dienVien" items="${dienViens}" varStatus="loop">
                                        ${dienVien.hoTen}<c:if test="${!loop.last}">,</c:if>
                                    </c:forEach>
                                </span>
                            </p>
                            <p><strong>Thể loại:</strong> 
                                <span id="movie-genre">
                                    <c:forEach var="theLoai" items="${theLoais}" varStatus="loop">
                                        ${theLoai.tenTheLoai}<c:if test="${!loop.last}">,</c:if>
                                    </c:forEach>
                                </span>
                            </p>
                            <p><strong>Định dạng:</strong>
							    <span id="movie-dinhDang">
							        <c:forEach var="dinhDang" items="${phim.dinhDangs}" varStatus="loop">
							            ${dinhDang.tenDinhDang}<c:if test="${!loop.last}">,</c:if>
							        </c:forEach>
							    </span>
							</p>
                            <p><strong>Khởi chiếu:</strong> <span id="movie-release"><fmt:formatDate value="${phim.ngayKhoiChieu}" pattern="dd/MM/yyyy" /></span></p>
                        </div>
                    </div>
                </div>

                <div class="showtime-section">
                    <h2>Lịch Chiếu</h2>
                    <div class="date-selector">
                        <button class="date-nav prev">◀</button>
                        <div class="dates" id="dateContainer"></div>
                        <button class="date-nav next">▶</button>
                    </div>
                    <div class="theater-list">
                        <c:if test="${empty lichChieuMap}">
                            <p>Không có lịch chiếu</p>
                        </c:if>
                        <c:forEach var="entry" items="${lichChieuMap}">
                            <c:set var="rap" value="${entry.key}" />
                            <div class="theater" data-rap="${rap.maRapChieu}">
                                <div class="theater-name">${rap.tenRapChieu} - ${rap.diaChi}</div>
                                <div class="showtimes">
                                    <c:forEach var="suatChieu" items="${entry.value}">
                                        <div class="time-slot-wrapper" data-date="<fmt:formatDate value='${suatChieu.ngayGioChieu}' pattern='dd/MM/yyyy' />">
                                            <form action="${pageContext.request.contextPath}/booking/select-seats" method="get" style="display: inline;">
                                                <input type="hidden" name="maPhim" value="${maPhim}">
                                                <input type="hidden" name="maSuatChieu" value="${suatChieu.maSuatChieu}">
                                                <button type="submit" class="time-slot">
                                                    <fmt:formatDate value="${suatChieu.ngayGioChieu}" pattern="HH:mm" />
                                                    <br><small>(${suatChieu.loaiManChieu})</small>
                                                </button>
                                            </form>
                                        </div>
                                    </c:forEach>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </c:if>
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
        function goToStep(step) {
            if (step !== 1) {
                alert("Vui lòng chọn suất chiếu trước khi chuyển sang bước tiếp theo!");
            }
        }

        document.addEventListener("DOMContentLoaded", function() {
            const dateContainer = document.getElementById("dateContainer");
            const theaters = document.querySelectorAll(".theater");
            let uniqueDates = new Set();

            theaters.forEach(theater => {
                const timeSlots = theater.querySelectorAll(".time-slot-wrapper");
                timeSlots.forEach(slot => {
                    uniqueDates.add(slot.getAttribute("data-date"));
                });
            });

            let sortedDates = Array.from(uniqueDates).sort((a, b) => {
                const [dayA, monthA, yearA] = a.split('/').map(Number);
                const [dayB, monthB, yearB] = b.split('/').map(Number);
                const dateA = new Date(yearA, monthA - 1, dayA);
                const dateB = new Date(yearB, monthB - 1, dayB);
                return dateA - dateB;
            });

            // Chỉ lấy 4 ngày gần nhất
            sortedDates = sortedDates.slice(0, 4);

            sortedDates.forEach(date => {
                const button = document.createElement("button");
                button.textContent = date;
                button.className = "date-btn";
                button.addEventListener("click", () => filterShowtimes(date));
                dateContainer.appendChild(button);
            });

            function filterShowtimes(selectedDate) {
                theaters.forEach(theater => {
                    const timeSlots = theater.querySelectorAll(".time-slot-wrapper");
                    let hasVisibleSlots = false;
                    timeSlots.forEach(slot => {
                        const isVisible = slot.getAttribute("data-date") === selectedDate;
                        slot.style.display = isVisible ? "inline-block" : "none";
                        if (isVisible) hasVisibleSlots = true;
                    });
                    theater.style.display = hasVisibleSlots ? "block" : "none";
                });
                dateContainer.querySelectorAll(".date-btn").forEach(btn => {
                    btn.classList.toggle("active", btn.textContent === selectedDate);
                });
            }

            if (sortedDates.length > 0) {
                const firstDate = sortedDates[0];
                filterShowtimes(firstDate);
            }
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