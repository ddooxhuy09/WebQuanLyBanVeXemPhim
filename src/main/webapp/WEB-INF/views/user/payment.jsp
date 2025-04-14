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

    <!-- Bộ đếm giờ -->
    <div class="timer-container">
        <span>Thời gian còn lại: </span>
        <span id="timer">10:00</span>
    </div>

    <div class="container">
        <c:if test="${not empty error}">
            <div class="error-message">${error}</div>
        </c:if>
        <c:if test="${not empty success}">
            <div class="success-message">${success}</div>
        </c:if>

        <div class="card">
            <h5>Khuyến mãi</h5>
            
            <!-- Form để áp dụng mã khuyến mãi -->
            <form action="${pageContext.request.contextPath}/booking/apply-promo-code" method="post" class="promo-form">
                <div class="form-group">
                    <input type="text" name="promoCode" class="form-control" placeholder="Nhập mã khuyến mãi" value="${promoCode}">
                    <button type="submit" class="btn-warning">Áp Dụng</button>
                </div>
            </form>

            <!-- Hiển thị thông tin giảm giá nếu có -->
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

            <!-- Form thanh toán -->
            <form action="${pageContext.request.contextPath}/booking/confirm-payment" method="post" id="paymentForm">
                <input type="hidden" name="promoCode" value="${promoCode}">
                
                <h5>Phương thức thanh toán</h5>
                <div>
                    <div class="payment-option" id="zalopay_option" onclick="selectPaymentMethod('zalopay')">
                        <img src="https://cdn.galaxycine.vn/media/2024/7/10/zalopay_1720600308412.png" alt="ZaloPay">
                        <label>ZaloPay - Nhập mã GIAMSAU - Giảm 50% tối đa 40K</label>
                        <input type="radio" name="paymentMethod" value="zalopay" ${paymentMethod == 'zalopay' ? 'checked' : ''} style="display:none;">
                    </div>
                    <div class="payment-option" id="vnpay_option" onclick="selectPaymentMethod('vnpay')">
                        <img src="https://cdn.galaxycine.vn/media/2021/12/2/download_1638460623615.png" alt="VNPAY">
                        <label>VNPAY</label>
                        <input type="radio" name="paymentMethod" value="vnpay" ${paymentMethod == 'vnpay' ? 'checked' : ''} style="display:none;">
                    </div>
                </div>

                <p style="margin-top: 20px; font-size: 18px;">Tổng tiền: <strong><fmt:formatNumber value="${tongTien}" type="currency" currencySymbol="đ" groupingUsed="true"/></strong></p>
                <button type="submit" class="confirm-btn">Xác nhận thanh toán</button>
            </form>
        </div>
    </div>

    <script>
        let timeLeft = sessionStorage.getItem("timeLeft") || (10 * 60);
        let timerId;

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
                window.location.href = "${pageContext.request.contextPath}/movie-detail?id=${maPhim}";
            } else if (step === 2) {
                const form = document.createElement("form");
                form.method = "post";
                form.action = "${pageContext.request.contextPath}/booking/select-seats";
                const maPhimInput = document.createElement("input");
                maPhimInput.type = "hidden";
                maPhimInput.name = "maPhim";
                maPhimInput.value = "${maPhim}";
                const maSuatChieuInput = document.createElement("input");
                maSuatChieuInput.type = "hidden";
                maSuatChieuInput.name = "maSuatChieu";
                maSuatChieuInput.value = "${maSuatChieu}";
                form.appendChild(maPhimInput);
                form.appendChild(maSuatChieuInput);
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

        window.onload = function() {
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
            startBookingTimer();
        };

        document.getElementById("paymentForm").addEventListener("submit", function() {
            sessionStorage.removeItem("timeLeft");
        });

        window.addEventListener("beforeunload", () => {
            sessionStorage.setItem("timeLeft", timeLeft);
        });
    </script>
</body>
</html>