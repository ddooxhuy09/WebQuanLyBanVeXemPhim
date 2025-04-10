<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Lý Bắp Nước & Combo</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <style>
        .custom-btn { background: #007bff; color: white; padding: 10px 20px; border: none; border-radius: 5px; cursor: pointer; }
        .custom-btn:hover { background: #0069d9; }
        .btn-sm { padding: 5px 10px; font-size: 0.875rem; }
        .form-section { background-color: #f8f9fa; padding: 20px; border-radius: 5px; margin-bottom: 30px; }
        .modal { display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); justify-content: center; align-items: center; }
        .modal-content { background: white; padding: 20px; border-radius: 5px; width: 600px; }
        .modal-actions { display: flex; justify-content: space-between; }
        .bap-nuoc-item { margin-bottom: 10px; }
    </style>
</head>
<body>
    <div class="container">
        <!-- Danh sách bắp nước & combo -->
        <div class="food-combo-list">
            <h2>Danh Sách Bắp Nước & Combo</h2>
            <c:if test="${not empty error}">
                <div class="alert alert-danger">${error}</div>
            </c:if>
            <div class="table-responsive">
                <table class="table table-bordered table-striped">
                    <thead>
                        <tr>
                            <th>Mã</th>
                            <th>Tên</th>
                            <th>Giá (VNĐ)</th>
                            <th>Mô Tả</th>
                            <th>Loại</th>
                            <th>Hành Động</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${bapNuocList}">
                            <tr>
                                <td>${item.maBapNuoc}</td>
                                <td>${item.tenBapNuoc}</td>
                                <td><fmt:formatNumber value="${item.giaBapNuoc}" pattern="#,###"/></td>
                                <td>Không có mô tả</td>
                                <td>Bắp Nước</td>
                                <td>
                                    <button class="custom-btn btn-sm mr-1" onclick="showEditBapNuoc('${item.maBapNuoc}', '${item.tenBapNuoc}', ${item.giaBapNuoc})">Sửa</button>
                                    <button class="custom-btn btn-sm" onclick="confirmDelete('${item.maBapNuoc}', '${item.tenBapNuoc}')">Xóa</button>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:forEach var="item" items="${comboList}">
                            <tr>
                                <td>${item.maCombo}</td>
                                <td>${item.tenCombo}</td>
                                <td><fmt:formatNumber value="${item.giaCombo}" pattern="#,###"/></td>
                                <td>${not empty item.moTa ? item.moTa : 'Không có mô tả'}</td>
                                <td>Combo</td>
                                <td>
                                    <button class="custom-btn btn-sm mr-1" onclick="showEditCombo('${item.maCombo}')">Sửa</button>
                                    <button class="custom-btn btn-sm" onclick="confirmDelete('${item.maCombo}', '${item.tenCombo}')">Xóa</button>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- Form thêm bắp nước -->
        <div class="form-section">
            <h2>Thêm Bắp Nước</h2>
            <form action="${pageContext.request.contextPath}/admin/food-combo/add-bapnuoc" method="post">
                <div class="row">
                    <div class="col-md-6">
                        <div class="form-group">
                            <label for="maBapNuoc">Mã Bắp Nước</label>
                            <input type="text" class="form-control" id="maBapNuoc" name="maBapNuoc" value="${newMaBapNuoc}" readonly>
                        </div>
                        <div class="form-group">
                            <label for="tenBapNuoc">Tên Bắp Nước</label>
                            <input type="text" class="form-control" id="tenBapNuoc" name="tenBapNuoc" required>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="form-group">
                            <label for="giaBapNuoc">Giá (VNĐ)</label>
                            <input type="number" class="form-control" id="giaBapNuoc" name="giaBapNuoc" step="1000" required>
                        </div>
                    </div>
                </div>
                <button type="submit" class="custom-btn">Thêm Bắp Nước</button>
            </form>
        </div>

        <!-- Form thêm combo -->
        <div class="form-section">
            <h2>Thêm Combo</h2>
            <form action="${pageContext.request.contextPath}/admin/food-combo/add-combo" method="post">
                <div class="row">
                    <div class="col-md-6">
                        <div class="form-group">
                            <label for="maCombo">Mã Combo</label>
                            <input type="text" class="form-control" id="maCombo" name="maCombo" value="${newMaCombo}" readonly>
                        </div>
                        <div class="form-group">
                            <label for="tenCombo">Tên Combo</label>
                            <input type="text" class="form-control" id="tenCombo" name="tenCombo" required>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="form-group">
                            <label for="giaCombo">Giá (VNĐ)</label>
                            <input type="number" class="form-control" id="giaCombo" name="giaCombo" step="1000" required>
                        </div>
                        <div class="form-group">
                            <label for="moTa">Mô Tả</label>
                            <textarea class="form-control" id="moTa" name="moTa" rows="3"></textarea>
                        </div>
                    </div>
                </div>
                <div class="form-group">
                    <label>Chọn Bắp Nước</label>
                    <div id="bapNuocContainer">
                        <div class="bap-nuoc-item">
                            <select name="bapNuocIds" class="form-control">
                                <option value="">Chọn Bắp Nước</option>
                                <c:forEach var="bapNuoc" items="${bapNuocList}">
                                    <option value="${bapNuoc.maBapNuoc}">${bapNuoc.tenBapNuoc}</option>
                                </c:forEach>
                            </select>
                            <input type="number" name="soLuongs" class="form-control" placeholder="Số lượng" min="1">
                        </div>
                    </div>
                    <button type="button" class="custom-btn mt-2" onclick="addBapNuocItem()">Thêm Bắp Nước</button>
                </div>
                <button type="submit" class="custom-btn">Thêm Combo</button>
            </form>
        </div>

        <!-- Modal sửa -->
        <div class="modal" id="editModal">
            <div class="modal-content">
                <h3>Sửa Thông Tin</h3>
                <form action="${pageContext.request.contextPath}/admin/food-combo/update" method="post">
                    <div class="form-group">
                        <label for="editMa">Mã</label>
                        <input type="text" id="editMa" name="ma" class="form-control" readonly>
                    </div>
                    <div class="form-group">
                        <label for="editTen">Tên</label>
                        <input type="text" id="editTen" name="ten" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label for="editGia">Giá (VNĐ)</label>
                        <input type="number" id="editGia" name="gia" class="form-control" step="1000" required>
                    </div>
                    <div class="form-group" id="moTaField">
                        <label for="editMoTa">Mô Tả</label>
                        <textarea id="editMoTa" name="moTa" class="form-control" rows="3"></textarea>
                    </div>
                    <div class="form-group" id="bapNuocField">
                        <label>Chọn Bắp Nước</label>
                        <div id="editBapNuocContainer"></div>
                        <button type="button" class="custom-btn mt-2" onclick="addBapNuocItemEdit()">Thêm Bắp Nước</button>
                    </div>
                    <input type="hidden" id="editLoai" name="loai">
                    <div class="modal-actions">
                        <button type="submit" class="custom-btn">Lưu</button>
                        <button type="button" class="custom-btn" onclick="closeModal()">Hủy</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.5.2/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Lưu trữ dữ liệu chi tiết combo từ phía máy chủ
        const comboDetails = {
            <c:forEach var="combo" items="${comboList}" varStatus="status">
                "${combo.maCombo}": [
                    <c:forEach var="detail" items="${requestScope['chiTietCombo_'.concat(combo.maCombo)]}" varStatus="detailStatus">
                        {
                            maBapNuoc: "${detail.maBapNuoc}",
                            soLuong: ${detail.soLuong}
                        }<c:if test="${!detailStatus.last}">,</c:if>
                    </c:forEach>
                ]<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        };

        function showEditBapNuoc(maBapNuoc, tenBapNuoc, giaBapNuoc) {
            document.getElementById('editMa').value = maBapNuoc;
            document.getElementById('editTen').value = tenBapNuoc;
            document.getElementById('editGia').value = giaBapNuoc;
            document.getElementById('editLoai').value = 'Bắp Nước';
            document.getElementById('moTaField').style.display = 'none';
            document.getElementById('bapNuocField').style.display = 'none';
            document.getElementById('editModal').style.display = 'flex';
        }

        function showEditCombo(maCombo) {
            // Tìm thông tin combo
            const combo = findComboByMa(maCombo);
            const chiTietCombos = comboDetails[maCombo] || [];
            
            if (combo) {
                document.getElementById('editMa').value = combo.maCombo;
                document.getElementById('editTen').value = combo.tenCombo;
                document.getElementById('editGia').value = combo.giaCombo;
                document.getElementById('editMoTa').value = combo.moTa || '';
                document.getElementById('editLoai').value = 'Combo';
                document.getElementById('moTaField').style.display = 'block';
                document.getElementById('bapNuocField').style.display = 'block';

                // Xóa các item cũ
                const container = document.getElementById('editBapNuocContainer');
                container.innerHTML = '';
                
                // Thêm chi tiết combo
                if (chiTietCombos.length > 0) {
                    chiTietCombos.forEach(item => {
                        const div = document.createElement('div');
                        div.className = 'bap-nuoc-item';
                        
                        let selectHTML = '<select name="bapNuocIds" class="form-control">' +
                                        '<option value="">Chọn Bắp Nước</option>';
                        
                        <c:forEach var="bapNuoc" items="${bapNuocList}">
                            selectHTML += '<option value="${bapNuoc.maBapNuoc}" ' + 
                                        (item.maBapNuoc === '${bapNuoc.maBapNuoc}' ? 'selected' : '') + 
                                        '>${bapNuoc.tenBapNuoc}</option>';
                        </c:forEach>
                        
                        selectHTML += '</select>';
                        
                        div.innerHTML = selectHTML + 
                                       '<input type="number" name="soLuongs" class="form-control" value="' + 
                                       item.soLuong + '" placeholder="Số lượng" min="1">';
                        
                        container.appendChild(div);
                    });
                } else {
                    addBapNuocItemEdit();
                }
            }
            
            document.getElementById('editModal').style.display = 'flex';
        }

        function findComboByMa(maCombo) {
            <c:forEach var="combo" items="${comboList}">
                if ("${combo.maCombo}" === maCombo) {
                    return {
                        maCombo: "${combo.maCombo}",
                        tenCombo: "${combo.tenCombo}",
                        giaCombo: ${combo.giaCombo},
                        moTa: "${combo.moTa}"
                    };
                }
            </c:forEach>
            return null;
        }

        function closeModal() {
            document.getElementById('editModal').style.display = 'none';
        }

        function confirmDelete(ma, ten) {
            if (confirm(`Bạn có chắc muốn xóa "${ten}" không?`)) {
                window.location.href = "${pageContext.request.contextPath}/admin/food-combo/delete/" + ma;
            }
        }

        function addBapNuocItem() {
            const container = document.getElementById('bapNuocContainer');
            const div = document.createElement('div');
            div.className = 'bap-nuoc-item';
            
            let selectHTML = '<select name="bapNuocIds" class="form-control">' +
                           '<option value="">Chọn Bắp Nước</option>';
            
            <c:forEach var="bapNuoc" items="${bapNuocList}">
                selectHTML += '<option value="${bapNuoc.maBapNuoc}">${bapNuoc.tenBapNuoc}</option>';
            </c:forEach>
            
            selectHTML += '</select>';
            
            div.innerHTML = selectHTML + 
                           '<input type="number" name="soLuongs" class="form-control" placeholder="Số lượng" min="1">';
            
            container.appendChild(div);
        }

        function addBapNuocItemEdit() {
            const container = document.getElementById('editBapNuocContainer');
            const div = document.createElement('div');
            div.className = 'bap-nuoc-item';
            
            let selectHTML = '<select name="bapNuocIds" class="form-control">' +
                           '<option value="">Chọn Bắp Nước</option>';
            
            <c:forEach var="bapNuoc" items="${bapNuocList}">
                selectHTML += '<option value="${bapNuoc.maBapNuoc}">${bapNuoc.tenBapNuoc}</option>';
            </c:forEach>
            
            selectHTML += '</select>';
            
            div.innerHTML = selectHTML + 
                           '<input type="number" name="soLuongs" class="form-control" placeholder="Số lượng" min="1">';
            
            container.appendChild(div);
        }
    </script>
</body>
</html>
