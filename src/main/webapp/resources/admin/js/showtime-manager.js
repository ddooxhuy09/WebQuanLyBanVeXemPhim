var showtimes = [];
var currentWeekStart = new Date();
currentWeekStart.setDate(currentWeekStart.getDate() - (currentWeekStart.getDay() === 0 ? 6 : currentWeekStart.getDay() - 1));
currentWeekStart.setHours(0, 0, 0, 0);
var HOUR_HEIGHT = 80;
var MILLISECONDS_PER_MINUTE = 60 * 1000;
var MINUTES_PER_HOUR = 60;

var calendarView, tableView, toggleBtn, showtimeTableBody, noDataRow,
    calendarTable, calendarMessage, calendarHeader, calendarBody, weekRangeElement,
    addModal, editModal, addTimeSlotsContainer, hiddenShowtimeInputsContainer,
    deleteForm, deleteMaSuatHidden;

function padZero(num) {
    return num.toString().padStart ? num.toString().padStart(2, '0') : (num < 10 ? '0' + num : '' + num);
}

function formatDate(date) {
    if (!date || isNaN(new Date(date).getTime())) return 'N/A';
    var d = new Date(date);
    return padZero(d.getDate()) + '/' + padZero(d.getMonth() + 1);
}

function formatTime(date) {
    if (!date || isNaN(new Date(date).getTime())) return 'N/A';
    var d = new Date(date);
    return padZero(d.getHours()) + ':' + padZero(d.getMinutes());
}

function getDayName(date) {
    if (!date || isNaN(new Date(date).getTime())) return 'N/A';
    var days = ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7'];
    return days[date.getDay()];
}

function formatDateTimeFull(date) {
    if (!date || isNaN(new Date(date).getTime())) return 'N/A';
    var d = new Date(date);
    return padZero(d.getDate()) + '/' + padZero(d.getMonth() + 1) + '/' + d.getFullYear() + ' ' + 
           padZero(d.getHours()) + ':' + padZero(d.getMinutes());
}

function formatTableDateTime(date) {
    if (!date || isNaN(new Date(date).getTime())) return 'N/A';
    var d = new Date(date);
    return padZero(d.getDate()) + '/' + padZero(d.getMonth() + 1) + '/' + d.getFullYear() + ' ' + 
           padZero(d.getHours()) + ':' + padZero(d.getMinutes());
}

function formatDateTimeLocal(date) {
    if (!date || isNaN(new Date(date).getTime())) return '';
    var d = new Date(date);
    try {
        return d.getFullYear() + '-' + padZero(d.getMonth() + 1) + '-' + padZero(d.getDate()) + 
               'T' + padZero(d.getHours()) + ':' + padZero(d.getMinutes());
    } catch (e) {
        console.error("Error formatting date to datetime-local:", date, e);
        return "";
    }
}

function filterShowtimes() {
    var filterStatus = document.getElementById('filterStatus').value;
    var filterTimeValue = document.getElementById('filterTime').value;
    var filterRap = document.getElementById('filterRap').value;
    var filterPhong = document.getElementById('filterPhong').value;

    var filterDate = null;
    if (filterTimeValue) {
        var parts = filterTimeValue.split('-');
        if (parts.length === 3) {
            filterDate = new Date(parseInt(parts[0]), parseInt(parts[1]) - 1, parseInt(parts[2]));
            filterDate.setHours(0, 0, 0, 0);
        }
    }

    var filtered = showtimes.filter(function(showtime) {
        console.log('Filtering showtime:', showtime); // Debug mỗi suất chiếu
        if (!showtime || !showtime.ngayGioChieu || !(showtime.ngayGioChieu instanceof Date)) {
            console.warn("Invalid showtime object found:", showtime);
            return false;
        }
        var showtimeDate = new Date(showtime.ngayGioChieu);
        showtimeDate.setHours(0, 0, 0, 0);

        if (filterStatus !== 'all') {
            if (filterStatus === 'has_surcharge' && !showtime.maPhuThu) return false;
            if (filterStatus !== 'has_surcharge' && showtime.status !== filterStatus) return false;
        }

        if (filterDate && showtimeDate.getTime() !== filterDate.getTime()) {
            return false;
        }

        if (filterRap !== 'all' && showtime.maRap !== filterRap) {
            return false;
        }

        if (filterPhong !== 'all' && showtime.maPhong !== filterPhong) {
            return false;
        }

        return true;
    });
    console.log('Filtered showtimes:', filtered); // Debug kết quả lọc
    return filtered;
}

function applyFilterAndRender() {
    if (tableView.style.display !== 'none') {
        renderTableView();
    } else {
        var filterRapVal = document.getElementById('filterRap').value;
        var filterPhongVal = document.getElementById('filterPhong').value;
        if (filterRapVal !== 'all' && filterPhongVal !== 'all') {
            renderCalendar();
        } else {
            calendarTable.style.display = 'none';
            calendarMessage.textContent = 'Vui lòng chọn Rạp Chiếu và Phòng Chiếu để xem lịch.';
            calendarMessage.style.display = 'block';
        }
    }
}

function toggleShowtimeView() {
    const urlParams = new URLSearchParams(window.location.search);
    const currentPage = urlParams.get('page') || '1';
    
    if (calendarView.style.display !== 'none') {
        // Chuyển sang dạng bảng
        calendarView.style.display = 'none';
        tableView.style.display = 'block';
        toggleBtn.innerHTML = '<i class="fas fa-calendar-alt"></i> Chuyển Sang Dạng Lịch';
        renderTableView();
        // Cập nhật URL
        urlParams.set('view', 'table');
    } else {
        // Chuyển sang dạng lịch
        tableView.style.display = 'none';
        calendarView.style.display = 'block';
        toggleBtn.innerHTML = '<i class="fas fa-list"></i> Chuyển Sang Dạng Bảng';
        applyFilterAndRender();
        // Cập nhật URL
        urlParams.set('view', 'calendar');
    }

    // Cập nhật URL mà không tải lại trang
    window.history.replaceState({}, '', `${window.location.pathname}?${urlParams.toString()}`);
}

function renderCalendar() {
    if (!calendarTable || !calendarMessage || !calendarHeader || !calendarBody || !weekRangeElement) {
        console.error("Calendar elements not found!");
        return;
    }

    var filterRap = document.getElementById('filterRap').value;
    var filterPhong = document.getElementById('filterPhong').value;

    if (filterRap === 'all' || filterPhong === 'all') {
        calendarTable.style.display = 'none';
        calendarMessage.textContent = 'Vui lòng chọn Rạp Chiếu và Phòng Chiếu để xem lịch.';
        calendarMessage.style.display = 'block';
        return;
    }

    var filteredShowtimes = filterShowtimes();

    if (filteredShowtimes.length === 0) {
        var rapText = document.getElementById('filterRap').options[document.getElementById('filterRap').selectedIndex] ? 
                      document.getElementById('filterRap').options[document.getElementById('filterRap').selectedIndex].text : 
                      filterRap;
        var phongText = document.getElementById('filterPhong').options[document.getElementById('filterPhong').selectedIndex] ? 
                        document.getElementById('filterPhong').options[document.getElementById('filterPhong').selectedIndex].text : 
                        filterPhong;
        calendarTable.style.display = 'none';
        calendarMessage.textContent = 'Không có suất chiếu nào được tìm thấy cho "' + phongText + '" tại "' + rapText + '" trong tuần này hoặc theo bộ lọc đã chọn.';
        calendarMessage.style.display = 'block';
        return;
    }

    calendarTable.style.display = 'table';
    calendarMessage.style.display = 'none';

    var weekEnd = new Date(currentWeekStart);
    weekEnd.setDate(weekEnd.getDate() + 6);
    weekRangeElement.textContent = formatDate(currentWeekStart) + ' - ' + formatDate(weekEnd);

    var headerHtml = '<th>Giờ</th>';
    var tempDateHeader = new Date(currentWeekStart);
    for (var i = 0; i < 7; i++) {
        headerHtml += '<th>' + formatDate(tempDateHeader) + '<br>' + getDayName(tempDateHeader) + '</th>';
        tempDateHeader.setDate(tempDateHeader.getDate() + 1);
    }
    calendarHeader.innerHTML = headerHtml;

    var bodyHtml = '';
    for (var hour = 0; hour < 24; hour++) {
        var timeStr = padZero(hour) + ':00';
        bodyHtml += '<tr><td class="time-slot">' + timeStr + '</td>';
        for (var day = 0; day < 7; day++) {
            bodyHtml += '<td data-hour="' + hour + '-' + day + '"></td>';
        }
        bodyHtml += '</tr>';
    }
    calendarBody.innerHTML = bodyHtml;

    var weekStartNorm = new Date(currentWeekStart.getFullYear(), currentWeekStart.getMonth(), currentWeekStart.getDate());

    for (var j = 0; j < filteredShowtimes.length; j++) {
        var showtime = filteredShowtimes[j];
        try {
            var startDate = showtime.ngayGioChieu instanceof Date ? showtime.ngayGioChieu : new Date(showtime.ngayGioChieu);
            var endDate = showtime.ngayGioKetThuc instanceof Date ? showtime.ngayGioKetThuc : new Date(showtime.ngayGioKetThuc);

            if (isNaN(startDate.getTime()) || isNaN(endDate.getTime())) {
                console.warn('Invalid date for Showtime ' + showtime.maSuat + ', skipping.');
                continue;
            }

            var spansMidnight = startDate.getDate() !== endDate.getDate() || 
                               startDate.getMonth() !== endDate.getMonth() || 
                               startDate.getFullYear() !== endDate.getFullYear();

            if (!spansMidnight) {
                renderSingleEventBlock(showtime, startDate, endDate, weekStartNorm, calendarBody);
            } else {
                renderSplitEventBlocks(showtime, startDate, endDate, weekStartNorm, calendarBody);
            }
        } catch (error) {
            console.error('Error rendering showtime ' + (showtime.maSuat || 'unknown') + ':', error);
        }
    }
}

function renderSingleEventBlock(showtime, startDate, endDate, weekStartNorm, calendarBody) {
    var startDateNorm = new Date(startDate.getFullYear(), startDate.getMonth(), startDate.getDate());
    var dayIndex = Math.floor((startDateNorm.getTime() - weekStartNorm.getTime()) / (MILLISECONDS_PER_MINUTE * MINUTES_PER_HOUR * 24));

    if (dayIndex < 0 || dayIndex > 6) return;

    var startHour = startDate.getHours();
    var startMinutes = startDate.getMinutes();
    var durationMinutes = (endDate.getTime() - startDate.getTime()) / MILLISECONDS_PER_MINUTE;

    if (durationMinutes <= 0) return;

    var topOffsetWithinCell = (startMinutes / MINUTES_PER_HOUR) * HOUR_HEIGHT;
    var height = (durationMinutes / MINUTES_PER_HOUR) * HOUR_HEIGHT;
    var startCellSelector = 'td[data-hour="' + startHour + '-' + dayIndex + '"]';
    var startCell = calendarBody.querySelector(startCellSelector);

    if (startCell) {
        createAndAppendEventDiv(showtime, startDate, endDate, topOffsetWithinCell, height, startCell);
    } else {
        console.error('Single block: Could not find cell with selector: ' + startCellSelector);
    }
}

function renderSplitEventBlocks(showtime, startDate, endDate, weekStartNorm, calendarBody) {
    var startDateNorm = new Date(startDate.getFullYear(), startDate.getMonth(), startDate.getDate());
    var dayIndex1 = Math.floor((startDateNorm.getTime() - weekStartNorm.getTime()) / (MILLISECONDS_PER_MINUTE * MINUTES_PER_HOUR * 24));

    if (dayIndex1 >= 0 && dayIndex1 <= 6) {
        var startHour1 = startDate.getHours();
        var startMinutes1 = startDate.getMinutes();
        var midnight = new Date(startDate);
        midnight.setHours(24, 0, 0, 0);
        var duration1Minutes = (midnight.getTime() - startDate.getTime()) / MILLISECONDS_PER_MINUTE;

        if (duration1Minutes > 0) {
            var top1 = (startMinutes1 / MINUTES_PER_HOUR) * HOUR_HEIGHT;
            var height1 = (duration1Minutes / MINUTES_PER_HOUR) * HOUR_HEIGHT;
            var startCellSelector1 = 'td[data-hour="' + startHour1 + '-' + dayIndex1 + '"]';
            var startCell1 = calendarBody.querySelector(startCellSelector1);
            if (startCell1) createAndAppendEventDiv(showtime, startDate, endDate, top1, height1, startCell1);
            else console.error('Split block 1: Could not find cell with selector: ' + startCellSelector1);
        }
    }

    var dayIndex2 = dayIndex1 + 1;
    if (dayIndex2 >= 0 && dayIndex2 <= 6) {
        var startHour2 = 0;
        var midnightStart = new Date(startDate);
        midnightStart.setHours(24, 0, 0, 0);
        var duration2Minutes = (endDate.getTime() - midnightStart.getTime()) / MILLISECONDS_PER_MINUTE;

        if (duration2Minutes > 0) {
            var top2 = 0;
            var height2 = (duration2Minutes / MINUTES_PER_HOUR) * HOUR_HEIGHT;
            var startCellSelector2 = 'td[data-hour="' + startHour2 + '-' + dayIndex2 + '"]';
            var startCell2 = calendarBody.querySelector(startCellSelector2);
            if (startCell2) createAndAppendEventDiv(showtime, startDate, endDate, top2, height2, startCell2);
            else console.error('Split block 2: Could not find cell with selector: ' + startCellSelector2);
        }
    }
}

function getTooltipHtml(st, startD, endD) {
    return '<strong>Phim:</strong> ' + (st.tenPhim || 'N/A') + '<br>' +
           '<strong>Thời gian:</strong> ' + formatTime(startD) + ' - ' + formatTime(endD) + '<br>' +
           '<strong>Phòng:</strong> ' + (st.tenPhong || 'N/A') + '<br>' +
           '<strong>Rạp:</strong> ' + (st.tenRap || 'N/A') + '<br>' +
           '<strong>Loại màn:</strong> ' + (st.loaiManChieu || 'N/A');
}

function createAndAppendEventDiv(showtime, trueStartDate, trueEndDate, topPos, height, targetCell) {
    var eventDiv = document.createElement('div');
    var statusClass = ['not_started', 'playing', 'finished', 'has_surcharge'].indexOf(showtime.status) !== -1 ? 
                      showtime.status : 'not_started';
    eventDiv.className = 'event ' + statusClass;
    eventDiv.style.position = 'absolute';
    eventDiv.style.top = topPos + 'px';
    eventDiv.style.height = height + 'px';
    eventDiv.style.left = '3px';
    eventDiv.style.right = '3px';

    eventDiv.innerHTML = 
        '<div style="font-weight: bold; font-size: 11px; line-height: 1.2;">' + showtime.tenPhim + '</div>' +
        '<div style="font-size: 10px;">' + formatTime(trueStartDate) + ' - ' + formatTime(trueEndDate) + '</div>' +
        '<div class="tooltip">' + getTooltipHtml(showtime, trueStartDate, trueEndDate) + '</div>';

    eventDiv.onclick = function(e) {
        e.stopPropagation();
        handleEditClick(showtime.maSuat);
    };

    targetCell.appendChild(eventDiv);
}

function previousWeek() {
    currentWeekStart.setDate(currentWeekStart.getDate() - 7);
    applyFilterAndRender();
}

function nextWeek() {
    currentWeekStart.setDate(currentWeekStart.getDate() + 7);
    applyFilterAndRender();
}

function renderTableView() {
    var filteredData = filterShowtimes();
    console.log('filteredData trong renderTableView:', filteredData);
    showtimeTableBody.innerHTML = '';
    var dataRows = showtimeTableBody.querySelectorAll('tr:not(.no-data-row)');
    console.log('dataRows trước khi xóa:', dataRows.length);
    for (var i = 0; i < dataRows.length; i++) {
        dataRows[i].remove();
    }

    if (filteredData.length === 0) {
        noDataRow.style.display = 'table-row';
        console.log('Không có dữ liệu, hiển thị noDataRow');
        return;
    }

    noDataRow.style.display = 'none';

    for (var j = 0; j < filteredData.length; j++) {
        var showtime = filteredData[j];
        console.log('Render hàng cho showtime:', showtime.maSuat);
        var row = document.createElement('tr');
        row.setAttribute('data-showtime-id', showtime.maSuat);

        row.innerHTML = 
            '<td>' + showtime.maSuat + '</td>' +
            '<td>' + (showtime.tenPhim || 'N/A') + '</td>' +
            '<td>' + (showtime.tenRap || 'N/A') + '</td>' +
            '<td>' + (showtime.tenPhong || 'N/A') + '</td>' +
            '<td>' + formatTableDateTime(showtime.ngayGioChieu) + '</td>' +
            '<td>' + formatTableDateTime(showtime.ngayGioKetThuc) + '</td>' +
            '<td>' + (showtime.loaiManChieu || 'N/A') + '</td>' +
            '<td>' + (showtime.tenPhuThu || 'Không có') + '</td>' +
            '<td>' +
                '<button class="custom-btn btn-sm mr-1 btn-edit" data-ma-suat="' + showtime.maSuat + '">' +
                    '<i class="fas fa-edit"></i> Sửa' +
                '</button>' +
                '<button class="custom-btn btn-sm btn-danger btn-delete" data-ma-suat="' + showtime.maSuat + '">' +
                    '<i class="fas fa-trash"></i> Xóa' +
                '</button>' +
            '</td>';
        showtimeTableBody.appendChild(row);
    }
    console.log('Đã render bảng, số hàng:', showtimeTableBody.querySelectorAll('tr:not(.no-data-row)').length);

    // Gắn lại sự kiện cho các nút "Sửa"
    const editButtons = document.querySelectorAll('.btn-edit');
    console.log('Số lượng nút chỉnh sửa sau render:', editButtons.length);
    editButtons.forEach(button => {
        console.log('Gắn sự kiện cho nút chỉnh sửa với data-ma-suat:', button.getAttribute('data-ma-suat'));
        button.addEventListener('click', function () {
            const maSuat = this.getAttribute('data-ma-suat');
            console.log('Nút chỉnh sửa được click sau render, maSuat:', maSuat);
            handleEditClick(maSuat);
        });
    });

    // Gắn lại sự kiện cho các nút "Xóa"
    const deleteButtons = document.querySelectorAll('.btn-delete');
    console.log('Số lượng nút xóa sau render:', deleteButtons.length);
    deleteButtons.forEach(button => {
        console.log('Gắn sự kiện cho nút xóa với data-ma-suat:', button.getAttribute('data-ma-suat'));
        button.addEventListener('click', function () {
            const maSuat = this.getAttribute('data-ma-suat');
            console.log('Nút xóa được click sau render, maSuat:', maSuat);
            handleDeleteClick(maSuat);
        });
    });
}

function removeTag(element) {
    element.parentElement.remove();
    updatePhuThuHidden();
    console.log('Removed phuThu tag');
}

function updatePhuThuHidden() {
    const container = document.getElementById('phuThuContainer');
    const hiddenInput = document.getElementById('phuThuHidden');

    if (!container || !hiddenInput) {
        console.log('Error: phuThuContainer or phuThuHidden not found');
        return;
    }

    const tags = Array.from(container.querySelectorAll('.tag'))
        .map(tag => tag.getAttribute('data-ma-phu-thu'));
    
    hiddenInput.value = tags.join(',');
    console.log('Updated phuThuHidden with value: ' + hiddenInput.value);
}

function addPhuThuTag() {
    const select = document.getElementById('addPhuThuSelect');
    const value = select.value;
    let text = '';

    if (select.selectedIndex !== -1) {
        text = select.options[select.selectedIndex].text;
    }

    if (value && text) {
        const tagContainer = document.getElementById('phuThuContainer');
        if (!tagContainer) {
            console.log('Error: phuThuContainer not found');
            return;
        }

        const existingTags = Array.from(tagContainer.querySelectorAll('.tag'))
            .map(tag => tag.getAttribute('data-ma-phu-thu'));

        if (!existingTags.includes(value)) {
            const newTag = document.createElement('span');
            newTag.className = 'tag';
            newTag.setAttribute('data-ma-phu-thu', value);
            newTag.textContent = text + ' ';

            const removeSpan = document.createElement('span');
            removeSpan.className = 'remove-tag';
            removeSpan.setAttribute('onclick', 'removeTag(this)');
            removeSpan.textContent = '×';

            newTag.appendChild(removeSpan);
            tagContainer.appendChild(newTag);

            console.log('Added new phuThu tag: ' + value);
        }

        select.value = ''; // Reset select
        updatePhuThuHidden();
    }
}

function createTimeSlotInput(includeRemoveButton) {
    var timeSlotDiv = document.createElement('div');
    timeSlotDiv.className = 'mb-2 time-slot-row';

    var rowFlex = document.createElement('div');
    rowFlex.className = 'd-flex align-items-center';

    var inputStart = document.createElement('input');
    inputStart.type = 'datetime-local';
    inputStart.className = 'form-control mr-2 add-start-time flex-grow-1';
    inputStart.name = 'ngayGioChieu';
    inputStart.required = true;
    inputStart.onchange = function () {
        updateSpecificEndTime(this);
    };
    rowFlex.appendChild(inputStart);

    var inputEndDisplay = document.createElement('input');
    inputEndDisplay.type = 'text';
    inputEndDisplay.className = 'form-control mr-2 add-end-time-display flex-grow-1';
    inputEndDisplay.readOnly = true;
    inputEndDisplay.placeholder = "Giờ kết thúc (tự động)";
    inputEndDisplay.style.backgroundColor = '#e9ecef';
    rowFlex.appendChild(inputEndDisplay);

    if (includeRemoveButton) {
        var removeBtn = document.createElement('button');
        removeBtn.type = 'button';
        removeBtn.className = 'custom-btn btn-danger btn-sm flex-shrink-0';
        removeBtn.innerHTML = '<i class="fas fa-times"></i>';
        removeBtn.onclick = function () {
            if (addTimeSlotsContainer.querySelectorAll('.time-slot-row').length > 1) {
                timeSlotDiv.remove();
            } else {
                alert("Phải có ít nhất một khung giờ chiếu.");
            }
        };
        rowFlex.appendChild(removeBtn);
    }

    timeSlotDiv.appendChild(rowFlex);
    return timeSlotDiv;
}

function updateSpecificEndTime(startTimeInput) {
    var timeSlotRow = startTimeInput.closest('.time-slot-row');
    if (!timeSlotRow) return;

    var endTimeDisplay = timeSlotRow.querySelector('.add-end-time-display');
    if (!endTimeDisplay) return;

    var thoiLuongInput = document.getElementById('addThoiLuong');
    if (!thoiLuongInput || !thoiLuongInput.value) {
        endTimeDisplay.value = '';
        return;
    }

    var thoiLuong = parseInt(thoiLuongInput.value);
    if (isNaN(thoiLuong) || thoiLuong <= 0) {
        endTimeDisplay.value = '';
        return;
    }

    var startTimeStr = startTimeInput.value;
    if (!startTimeStr) {
        endTimeDisplay.value = '';
        return;
    }

    try {
        var startTime = new Date(startTimeStr);
        if (isNaN(startTime.getTime())) {
            endTimeDisplay.value = 'Ngày giờ bắt đầu không hợp lệ';
            return;
        }

        var endTime = new Date(startTime.getTime() + thoiLuong * MILLISECONDS_PER_MINUTE);
        endTimeDisplay.value = formatTableDateTime(endTime);
    } catch (e) {
        console.error("Error calculating end time:", e);
        endTimeDisplay.value = 'Lỗi tính toán';
    }
}

function addMoreTimeSlot() {
    addTimeSlotsContainer.appendChild(createTimeSlotInput(true));
}

function showAddModal() {
    document.getElementById('addShowtimeForm').reset();
    filterAddPhongChieu();
    updateAddThoiLuong();
    addTimeSlotsContainer.innerHTML = '';
    addTimeSlotsContainer.appendChild(createTimeSlotInput(false));
    document.getElementById('phuThuContainer').innerHTML = '';
    document.getElementById('phuThuHidden').value = '';
    document.getElementById('addPhuThuSelect').value = '';
    addModal.style.display = 'flex';
}

function closeModal() {
    if (addModal) addModal.style.display = 'none';
    if (editModal) editModal.style.display = 'none';
}

function closeAddModal() { closeModal(); }

function updateAddThoiLuong() {
    var phimSelect = document.getElementById('addPhim');
    var thoiLuongInput = document.getElementById('addThoiLuong');
    var selectedOption = phimSelect.options[phimSelect.selectedIndex];
    var thoiLuong = selectedOption && selectedOption.dataset ? 
                    parseInt(selectedOption.dataset.thoiluong) : 0;

    thoiLuongInput.value = (!isNaN(thoiLuong) && thoiLuong > 0) ? thoiLuong : '';
    var allStartTimeInputs = addTimeSlotsContainer.querySelectorAll('.add-start-time');
    for (var i = 0; i < allStartTimeInputs.length; i++) {
        updateSpecificEndTime(allStartTimeInputs[i]);
    }
}

function filterAddPhongChieu() {
    var rapChieu = document.getElementById('addRapChieu').value;
    var phongSelect = document.getElementById('addPhongChieu');
    var firstVisibleOptionValue = null;
    phongSelect.value = '';

    for (var i = 0; i < phongSelect.options.length; i++) {
        var option = phongSelect.options[i];
        if (option.value === "") {
            option.style.display = 'block';
            continue;
        }
        if (option.dataset && option.dataset.rap === rapChieu) {
            option.style.display = 'block';
            if (firstVisibleOptionValue === null) firstVisibleOptionValue = option.value;
        } else {
            option.style.display = 'none';
        }
    }
}

function prepareAndSubmitAddForm() {
    var form = document.getElementById('addShowtimeForm');
    hiddenShowtimeInputsContainer.innerHTML = '';

    var thoiLuong = parseInt(document.getElementById('addThoiLuong').value);
    if (isNaN(thoiLuong) || thoiLuong <= 0) {
        alert('Thời lượng phim không hợp lệ! Hãy chọn một phim.');
        return;
    }

    if (!document.getElementById('addPhim').value ||
        !document.getElementById('addRapChieu').value ||
        !document.getElementById('addPhongChieu').value) {
        alert('Vui lòng chọn Phim, Rạp và Phòng chiếu.');
        return;
    }

    var startTimeInputs = addTimeSlotsContainer.querySelectorAll('.add-start-time');
    var validationError = false;
    var showtimeIndex = 0;

    if (startTimeInputs.length === 0) {
        alert('Vui lòng thêm ít nhất một khung giờ chiếu.');
        return;
    }

    updatePhuThuHidden();
    var phuThuHidden = document.getElementById('phuThuHidden').value;
    var selectedPhuThus = phuThuHidden ? phuThuHidden.split(',').filter(val => val !== '') : [];

    for (var i = 0; i < startTimeInputs.length; i++) {
        if (validationError) continue;
        var input = startTimeInputs[i];
        var ngayGioChieuStr = input.value;
        if (!ngayGioChieuStr) {
            alert('Vui lòng nhập đầy đủ các khung giờ chiếu đã thêm.');
            validationError = true; 
            continue;
        }
        try {
            var ngayGioChieu = new Date(ngayGioChieuStr);
            if (isNaN(ngayGioChieu.getTime())) throw new Error("Invalid date format");
            var ngayGioKetThuc = new Date(ngayGioChieu.getTime() + thoiLuong * MILLISECONDS_PER_MINUTE);

            var hiddenInputStart = document.createElement('input');
            hiddenInputStart.type = 'hidden';
            hiddenInputStart.name = 'showtimes[' + showtimeIndex + '].ngayGioChieu';
            hiddenInputStart.value = ngayGioChieu.toISOString();
            hiddenShowtimeInputsContainer.appendChild(hiddenInputStart);

            var hiddenInputEnd = document.createElement('input');
            hiddenInputEnd.type = 'hidden';
            hiddenInputEnd.name = 'showtimes[' + showtimeIndex + '].ngayGioKetThuc';
            hiddenInputEnd.value = ngayGioKetThuc.toISOString();
            hiddenShowtimeInputsContainer.appendChild(hiddenInputEnd);

            selectedPhuThus.forEach((maPhuThu, phuThuIndex) => {
                var hiddenInputPhuThu = document.createElement('input');
                hiddenInputPhuThu.type = 'hidden';
                hiddenInputPhuThu.name = 'showtimes[' + showtimeIndex + '].maPhuThus[' + phuThuIndex + ']';
                hiddenInputPhuThu.value = maPhuThu;
                hiddenShowtimeInputsContainer.appendChild(hiddenInputPhuThu);
            });

            showtimeIndex++;
        } catch (e) {
            alert('Định dạng ngày giờ không hợp lệ: ' + ngayGioChieuStr);
            validationError = true;
        }
    }

    if (!validationError && showtimeIndex > 0) {
        form.submit();
    } else if (!validationError && showtimeIndex === 0) {
        alert("Không có khung giờ hợp lệ nào được nhập để thêm.");
    }
}

function handleEditClick(maSuat) {
    console.log('handleEditClick được gọi với maSuat:', maSuat); // Debug mã suất
    const showtime = serverData.find(s => s.maSuat === maSuat);
    console.log('showtime tìm thấy:', showtime); // Debug dữ liệu suất chiếu
    if (!showtime) {
        console.error("Không tìm thấy suất chiếu với mã: " + maSuat);
        alert("Không tìm thấy suất chiếu với mã: " + maSuat);
        return;
    }

    const form = document.getElementById('editShowtimeForm');
    console.log('editShowtimeForm:', form); // Debug form
    if (!form) {
        console.error("editShowtimeForm not found!");
        return;
    }

    const editMaSuatHidden = document.getElementById('editMaSuatHidden');
    const editMaSuatDisplay = document.getElementById('editMaSuatDisplay');
    const editPhim = document.getElementById('editPhim');
    const editRapChieu = document.getElementById('editRapChieu');
    const editPhongChieu = document.getElementById('editPhongChieu');
    const editNgayGioChieu = document.getElementById('editNgayGioChieu');
    const editLoaiManChieu = document.getElementById('editLoaiManChieu');
    const editPhuThuContainer = document.getElementById('editPhuThuContainer');
    const editThoiLuong = document.getElementById('editThoiLuong');

    console.log('editModal:', editModal); // Debug modal
    console.log('editMaSuatHidden:', editMaSuatHidden); // Debug các phần tử
    console.log('editPhim:', editPhim);
    console.log('editRapChieu:', editRapChieu);
    console.log('editPhongChieu:', editPhongChieu);
    console.log('editNgayGioChieu:', editNgayGioChieu);
    console.log('editLoaiManChieu:', editLoaiManChieu);
    console.log('editPhuThuContainer:', editPhuThuContainer);
    console.log('editThoiLuong:', editThoiLuong);

    if (!editMaSuatHidden || !editMaSuatDisplay || !editPhim || !editRapChieu || 
        !editPhongChieu || !editNgayGioChieu || !editLoaiManChieu || !editPhuThuContainer || !editThoiLuong) {
        console.error("Một hoặc nhiều phần tử trong editModal không được tìm thấy!");
        return;
    }

    editMaSuatHidden.value = showtime.maSuat;
    editMaSuatDisplay.value = showtime.maSuat;
    editPhim.value = showtime.maPhim;
    editThoiLuong.value = phimMap[showtime.maPhim].thoiLuong;
    editRapChieu.value = showtime.maRap;
    filterEditPhongChieu();
    editPhongChieu.value = showtime.maPhong;
    editNgayGioChieu.value = formatDateTimeLocal(showtime.ngayGioChieu);
    editLoaiManChieu.value = showtime.loaiManChieu;

    console.log('Điền giá trị modal:', {
        maSuat: showtime.maSuat,
        maPhim: showtime.maPhim,
        thoiLuong: phimMap[showtime.maPhim].thoiLuong,
        maRap: showtime.maRap,
        maPhong: showtime.maPhong,
        ngayGioChieu: formatDateTimeLocal(showtime.ngayGioChieu),
        loaiManChieu: showtime.loaiManChieu
    }); // Debug giá trị điền vào modal

    editPhuThuContainer.innerHTML = '';
    const phuThuArray = showtime.tenPhuThu.split(', ').filter(t => t !== 'Không có');
    console.log('phuThuArray:', phuThuArray); // Debug danh sách phụ thu
    phuThuArray.forEach(phuThuName => {
        const phuThu = phuThuList.find(p => p.tenPhuThu === phuThuName);
        console.log('Tìm phuThu:', phuThuName, phuThu); // Debug phụ thu
        if (phuThu) {
            addEditPhuThuTagFromData(phuThu.maPhuThu, phuThu.tenPhuThu);
        }
    });

    updateEditThoiLuongAndEndTime();
    console.log('Hiển thị modal, editModal.style.display:', editModal.style.display); // Debug trước khi hiển thị
    editModal.style.display = 'flex';
    console.log('Modal đã được hiển thị, editModal.style.display:', editModal.style.display); // Debug sau khi hiển thị
    console.log('Computed style display:', window.getComputedStyle(editModal).display); // Debug style thực tế
}

function filterEditPhongChieu() {
    var rapChieu = document.getElementById('editRapChieu').value;
    var phongSelect = document.getElementById('editPhongChieu');
    var firstVisibleOptionValue = null;
    phongSelect.value = '';

    for (var i = 0; i < phongSelect.options.length; i++) {
        var option = phongSelect.options[i];
        if (option.value === "") { 
            option.style.display = 'block'; 
            continue; 
        }
        if (option.dataset && option.dataset.rap === rapChieu) {
            option.style.display = 'block';
            if (firstVisibleOptionValue === null) firstVisibleOptionValue = option.value;
        } else {
            option.style.display = 'none';
        }
    }
}

function updateEditThoiLuongAndEndTime() {
    var phimSelect = document.getElementById('editPhim');
    var thoiLuongInput = document.getElementById('editThoiLuong');
    var selectedOption = phimSelect.options[phimSelect.selectedIndex];
    var thoiLuong = selectedOption && selectedOption.dataset ? 
                    parseInt(selectedOption.dataset.thoiluong) : 0;
                    
    thoiLuongInput.value = (!isNaN(thoiLuong) && thoiLuong > 0) ? thoiLuong : '';
    updateEditEndTime();
}

function updateEditEndTime() {
    var startTimeInput = document.getElementById('editNgayGioChieu');
    var thoiLuongInput = document.getElementById('editThoiLuong');
    var endTimeInput = document.getElementById('editNgayGioKetThuc');
    if (!startTimeInput.value || !thoiLuongInput.value || !endTimeInput) {
        if (endTimeInput) endTimeInput.value = ''; 
        return;
    }
    try {
        var startTime = new Date(startTimeInput.value);
        var thoiLuong = parseInt(thoiLuongInput.value);
        if (isNaN(startTime.getTime()) || isNaN(thoiLuong) || thoiLuong <= 0) 
            throw new Error("Invalid input");
        var endTime = new Date(startTime.getTime() + thoiLuong * MILLISECONDS_PER_MINUTE);
        endTimeInput.value = formatDateTimeLocal(endTime);
    } catch (e) {
        endTimeInput.value = '';
    }
}

function prepareAndSubmitEditForm() {
    if (!document.getElementById('editPhim').value ||
        !document.getElementById('editRapChieu').value ||
        !document.getElementById('editPhongChieu').value ||
        !document.getElementById('editNgayGioChieu').value) {
        alert('Vui lòng điền đầy đủ các trường bắt buộc (Phim, Rạp, Phòng, Giờ chiếu).');
        return;
    }
    document.getElementById('editShowtimeForm').submit();
}

function handleDeleteClick(maSuat) {
    console.log('handleDeleteClick started with maSuat:', maSuat);
    if (!maSuat) {
        console.error('maSuat is undefined or null');
        alert('Lỗi: Mã suất chiếu không hợp lệ.');
        return;
    }

    var showtimeToDelete = showtimes.find(s => s.maSuat === maSuat);
    console.log('showtimeToDelete:', showtimeToDelete);
    var confirmMsg = showtimeToDelete
        ? 'Bạn có chắc muốn xóa suất chiếu "' + (showtimeToDelete.tenPhim || 'N/A') + '" (' + maSuat + ') lúc ' + 
          formatTableDateTime(showtimeToDelete.ngayGioChieu) + ' không?'
        : 'Bạn có chắc muốn xóa suất chiếu "' + maSuat + '" không?';
    console.log('Confirm message:', confirmMsg);

    if (confirm(confirmMsg + "\nHành động này không thể hoàn tác.")) {
        if (!contextPath) {
            console.error('contextPath is undefined');
            alert('Lỗi: contextPath không được định nghĩa.');
            return;
        }
        console.log('contextPath:', contextPath);
        const viewMode = tableView.style.display === 'none' ? 'calendar' : 'table';
        const deleteUrl = contextPath + '/admin/showtimes/delete/' + maSuat + '?view=' + viewMode;
        console.log('Redirecting to:', deleteUrl);
        try {
            window.location.href = deleteUrl;
            console.log('GET request initiated');
        } catch (e) {
            console.error('Error sending GET request:', e);
            alert('Lỗi khi gửi yêu cầu xóa.');
        }
    } else {
        console.log('Xóa bị hủy bởi người dùng');
    }
}

function addEditPhuThuTag() {
    const select = document.getElementById('editPhuThuSelect');
    const container = document.getElementById('editPhuThuContainer');
    const hiddenInput = document.getElementById('editPhuThuHidden');
    const selectedValue = select.value;

    if (selectedValue && !Array.from(container.children).some(tag => tag.dataset.value === selectedValue)) {
        const tag = document.createElement('span');
        tag.className = 'tag';
        tag.dataset.value = selectedValue;
        tag.textContent = select.options[select.selectedIndex].text;
        const closeBtn = document.createElement('span');
        closeBtn.className = 'close-btn';
        closeBtn.textContent = '×';
        closeBtn.onclick = () => {
            container.removeChild(tag);
            updateEditPhuThuHidden();
        };
        tag.appendChild(closeBtn);
        container.appendChild(tag);
        select.value = ''; // Reset select
        updateEditPhuThuHidden();
    }
}

function updateEditPhuThuHidden() {
    const container = document.getElementById('editPhuThuContainer');
    const hiddenInput = document.getElementById('editPhuThuHidden');
    const tags = Array.from(container.children).map(tag => tag.dataset.value);
    hiddenInput.value = tags.join(',');
}

function addEditPhuThuTagFromData(maPhuThu, tenPhuThu) {
    const container = document.getElementById('editPhuThuContainer');
    if (!Array.from(container.children).some(tag => tag.dataset.value === maPhuThu)) {
        const tag = document.createElement('span');
        tag.className = 'tag';
        tag.dataset.value = maPhuThu;
        tag.textContent = tenPhuThu;
        const closeBtn = document.createElement('span');
        closeBtn.className = 'close-btn';
        closeBtn.textContent = '×';
        closeBtn.onclick = () => {
            container.removeChild(tag);
            updateEditPhuThuHidden();
        };
        tag.appendChild(closeBtn);
        container.appendChild(tag);
        updateEditPhuThuHidden();
    }
}

function navigateToPage(page) {
    const viewMode = tableView.style.display !== 'none' ? 'table' : 'calendar';
    const urlParams = new URLSearchParams(window.location.search);
    urlParams.set('page', page);
    urlParams.set('view', viewMode);
    window.location.href = `${window.location.pathname}?${urlParams.toString()}`;
}

document.addEventListener('DOMContentLoaded', function () {
    // Khởi tạo các phần tử
    calendarView = document.getElementById('calendarView');
    tableView = document.getElementById('tableView');
    toggleBtn = document.getElementById('toggleViewBtn');
    showtimeTableBody = document.getElementById('showtimeTableBody');
    noDataRow = tableView.querySelector('.no-data-row');
    calendarTable = document.getElementById('calendarTable');
    calendarMessage = document.getElementById('calendarMessage');
    calendarHeader = document.getElementById('calendarHeader');
    calendarBody = document.getElementById('calendarBody');
    weekRangeElement = document.getElementById('weekRange');
    addModal = document.getElementById('addModal');
    editModal = document.getElementById('editModal');
    addTimeSlotsContainer = document.getElementById('addTimeSlotsContainer');
    hiddenShowtimeInputsContainer = document.getElementById('hiddenShowtimeInputsContainer');
    deleteForm = document.getElementById('deleteShowtimeForm');
    deleteMaSuatHidden = document.getElementById('deleteMaSuatHidden');

    if (!editModal) {
        console.error("editModal not found in DOM!");
        return;
    }

    // Lấy tham số view từ URL
    const urlParams = new URLSearchParams(window.location.search);
	const viewMode = urlParams.get('view') || 'calendar';
	if (viewMode === 'table') {
	    calendarView.style.display = 'none';
	    tableView.style.display = 'block';
	    toggleBtn.innerHTML = '<i class="fas fa-calendar-alt"></i> Chuyển Sang Dạng Lịch';
	    renderTableView();
	} else {
	    calendarView.style.display = 'block';
	    tableView.style.display = 'none';
	    toggleBtn.innerHTML = '<i class="fas fa-list"></i> Chuyển Sang Dạng Bảng';
	    applyFilterAndRender();
	}

    // Khởi tạo showtimes từ serverData
    try {
        console.log("serverData:", serverData);
        if (typeof serverData !== 'undefined' && serverData instanceof Array) {
            showtimes = [];
            for (var i = 0; i < serverData.length; i++) {
                var st = serverData[i];
                showtimes.push({
                    maSuat: st.maSuat,
                    maPhim: st.maPhim,
                    tenPhim: st.tenPhim,
                    thoiLuong: st.thoiLuong,
                    maRap: st.maRap,
                    tenRap: st.tenRap,
                    maPhong: st.maPhong,
                    tenPhong: st.tenPhong,
                    ngayGioChieu: st.ngayGioChieu ? new Date(st.ngayGioChieu) : null,
                    ngayGioKetThuc: st.ngayGioKetThuc ? new Date(st.ngayGioKetThuc) : null,
                    loaiManChieu: st.loaiManChieu,
                    maPhuThu: st.maPhuThu,
                    tenPhuThu: st.tenPhuThu,
                    status: st.status
                });
            }
            
            var validShowtimes = [];
            for (var j = 0; j < showtimes.length; j++) {
                if (showtimes[j].ngayGioChieu && showtimes[j].ngayGioKetThuc) {
                    validShowtimes.push(showtimes[j]);
                }
            }
            showtimes = validShowtimes;
            console.log("Initialized showtimes from server data:", showtimes.length);
        } else {
            console.warn("Server data not found or invalid. Initializing empty showtimes array.");
            showtimes = [];
        }
    } catch (e) {
        console.error("Error initializing showtimes data:", e);
        showtimes = [];
    }

    // Gắn sự kiện cho các nút "Sửa"
    const editButtons = document.querySelectorAll('.btn-edit');
    console.log('Số lượng nút chỉnh sửa tìm thấy:', editButtons.length);
    editButtons.forEach(button => {
        console.log('Gắn sự kiện cho nút chỉnh sửa với data-ma-suat:', button.getAttribute('data-ma-suat'));
        button.addEventListener('click', function () {
            const maSuat = this.getAttribute('data-ma-suat');
            console.log('Nút chỉnh sửa được click, maSuat:', maSuat);
            handleEditClick(maSuat);
        });
    });

    // Gắn sự kiện cho các nút "Xóa"
    const deleteButtons = document.querySelectorAll('.btn-delete');
    console.log('Số lượng nút xóa tìm thấy:', deleteButtons.length);
    deleteButtons.forEach(button => {
        console.log('Gắn sự kiện cho nút xóa với data-ma-suat:', button.getAttribute('data-ma-suat'));
        button.addEventListener('click', function () {
            const maSuat = this.getAttribute('data-ma-suat');
            console.log('Nút xóa được click, maSuat:', maSuat);
            handleDeleteClick(maSuat);
        });
    });

    // Gắn sự kiện cho các bộ lọc
    var filterStatus = document.getElementById('filterStatus');
    if (filterStatus) filterStatus.onchange = applyFilterAndRender;
    
    var filterTime = document.getElementById('filterTime');
    if (filterTime) filterTime.onchange = applyFilterAndRender;
    
    var filterRap = document.getElementById('filterRap');
    if (filterRap) filterRap.onchange = applyFilterAndRender;
    
    var filterPhong = document.getElementById('filterPhong');
    if (filterPhong) filterPhong.onchange = applyFilterAndRender;

    var editPhim = document.getElementById('editPhim');
    if (editPhim) editPhim.addEventListener('change', updateEditThoiLuongAndEndTime);
    
    var editNgayGioChieu = document.getElementById('editNgayGioChieu');
    if (editNgayGioChieu) editNgayGioChieu.addEventListener('change', updateEditEndTime);
    
    var editThoiLuong = document.getElementById('editThoiLuong');
    if (editThoiLuong) editThoiLuong.addEventListener('change', updateEditEndTime);
    
    var editRapChieu = document.getElementById('editRapChieu');
    if (editRapChieu) editRapChieu.addEventListener('change', filterEditPhongChieu);

    var addForm = document.getElementById('addShowtimeForm');
    if (addForm) {
        addForm.addEventListener('submit', function(e) {
            updatePhuThuHidden();
            console.log('Form submitting with phuThuHidden: ' + document.getElementById('phuThuHidden').value);
        });
    }

    updateAddThoiLuong();
    filterAddPhongChieu();
});