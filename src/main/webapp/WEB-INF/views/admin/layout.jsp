<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin - <tiles:getAsString name="title" /></title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css"
        integrity="sha384-JcKb8q3iqJ61gNV9KGb8thSsNjpSL0n8PARn9HuZOnIxN0hoP+VmmDGMN5t9UJ0Z" crossorigin="anonymous">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css"
        integrity="sha512-Fo3rlrZj/k7ujTnHg4CGR2D7kSs0v4LLanw2qksYuRlEzO+tcaEPQogQ0KaoGN26/zrn20ImR1DfuLWnOo7aBA=="
        crossorigin="anonymous" referrerpolicy="no-referrer" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/admin/css/reset.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/admin/css/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/admin/css/sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/admin/css/home.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/admin/css/movies_manager.css">
</head>
<body>
    <div class="container-wrapper d-flex">
        <!-- Sidebar -->
        <div class="sidebar d-flex flex-column p-3">
            <h2 class="text-center mb-4">Admin Panel</h2>
            <!-- User Info -->
            <div class="user-info mb-4">
                <i class="fas fa-user-circle"></i>
                <span>Xin chào, Admin</span>
            </div>

            <!-- Quản Lý Chung -->
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-link"><i class="fas fa-home"></i> <span>Trang Chủ</span></a>

            <!-- Quản Lý Rạp & Chiếu Phim -->
            <div class="nav-section-title">Quản Lý Rạp & Chiếu Phim</div>
            <a href="${pageContext.request.contextPath}/admin/movies" class="nav-link"><i class="fas fa-film"></i> <span>Quản Lý Phim</span></a>
            <a href="#" class="nav-link"><i class="fas fa-calendar-alt"></i> <span>Quản Lý Suất Chiếu</span></a>
            <a href="#" class="nav-link"><i class="fas fa-chair"></i> <span>Quản Lý Ghế</span></a>
            <a href="#" class="nav-link"><i class="fas fa-theater-masks"></i> <span>Quản Lý Phòng Chiếu</span></a>
            <a href="${pageContext.request.contextPath}/admin/theaters" class="nav-link"><i class="fas fa-building"></i> <span>Quản Lý Rạp Chiếu</span></a>

            <!-- Quản Lý Vé & Giao Dịch -->
            <div class="nav-section-title">Quản Lý Vé & Giao Dịch</div>
            <a href="#" class="nav-link"><i class="fas fa-ticket-alt"></i> <span>Quản Lý Vé</span></a>
            <a href="#" class="nav-link"><i class="fas fa-shopping-cart"></i> <span>Quản Lý Đơn Hàng</span></a>
            <a href="#" class="nav-link"><i class="fas fa-money-bill"></i> <span>Quản Lý Thanh Toán</span></a>

            <!-- Quản Lý Thực Phẩm -->
            <div class="nav-section-title">Quản Lý Thực Phẩm</div>
            <a href="${pageContext.request.contextPath}/admin/food-combo" class="nav-link"><i class="fas fa-box"></i> <span>Quản Lý Bắp Nước và Combo</span></a>

            <!-- Quản Lý Khách Hàng & Ưu Đãi -->
            <div class="nav-section-title">Quản Lý Khách Hàng & Ưu Đãi</div>
            <a href="#" class="nav-link"><i class="fas fa-users"></i> <span>Quản Lý Khách Hàng</span></a>
            <a href="#" class="nav-link"><i class="fas fa-star"></i> <span>Quản Lý Điểm Khách Hàng</span></a>
            <a href="#" class="nav-link"><i class="fas fa-gift"></i> <span>Quản Lý Khuyến Mãi</span></a>
            <a href="#" class="nav-link"><i class="fas fa-exchange-alt"></i> <span>Quản Lý Quy Đổi Điểm</span></a>
            <a href="#" class="nav-link"><i class="fas fa-plus-circle"></i> <span>Quản Lý Phụ Thu</span></a>

            <!-- Hệ Thống -->
            <div class="nav-section-title">Hệ Thống</div>
            <a href="${pageContext.request.contextPath}/admin/auth/logout" class="nav-link"><i class="fas fa-sign-out-alt"></i> <span>Đăng Xuất</span></a>
        </div>

        <!-- Nội dung chính -->
        <div class="main-content flex-grow-1">
            <!-- Thanh tìm kiếm -->
            <div class="search-bar mb-4">
                <form class="form-inline justify-content-center">
                    <div class="input-group w-50">
                        <input type="text" class="form-control" placeholder="Tìm kiếm..." aria-label="Search">
                        <div class="input-group-append">
                            <button class="btn btn-outline" type="button"><i class="fas fa-search"></i></button>
                        </div>
                    </div>
                </form>
            </div>
            <!-- Nội dung động -->
            <div class="content">
                <tiles:insertAttribute name="body" />
            </div>
        </div>
    </div>
</body>
</html>