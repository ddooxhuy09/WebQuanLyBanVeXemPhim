var showtimes = [];
var currentWeekStart = new Date();
currentWeekStart.setDate(currentWeekStart.getDate() - (currentWeekStart.getDay() === 0 ? 6 : currentWeekStart.getDay() - 1));
currentWeekStart.setHours(0, 0, 0, 0);
var HOUR_HEIGHT = 80;
var MILLISECONDS_PER_MINUTE = 60 * 1000;
var MINUTES_PER_HOUR = 60;

// Add constants for pagination
const ITEMS_PER_PAGE = 25;
const PAGES_TO_SHOW = 5;
let currentPage = 1;

var calendarView, tableView, toggleBtn, showtimeTableBody, noDataRow,
	calendarTable, calendarMessage, calendarHeader, calendarBody, weekRangeElement,
	addModal, editModal, addTimeSlotsContainer, hiddenShowtimeInputsContainer,
	deleteForm, deleteMaSuatHidden;

function padZero(num) {
	return num.toString().padStart ? num.toString().padStart(2, '0') : (num < 10 ? '0' + num : '' + num);
}

function formatDate(date) {
	if (!date || isNaN(new Date(date).getTime())) return '';
	var d = new Date(date);
	return padZero(d.getDate()) + '/' + padZero(d.getMonth() + 1);
}

function formatTime(date) {
	if (!date || isNaN(new Date(date).getTime())) return '';
	var d = new Date(date);
	return padZero(d.getHours()) + ':' + padZero(d.getMinutes());
}

function getDayName(date) {
	if (!date || isNaN(new Date(date).getTime())) return '';
	var days = ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7'];
	return days[date.getDay()];
}

function formatDateTimeFull(date) {
	if (!date || isNaN(new Date(date).getTime())) return '';
	var d = new Date(date);
	return padZero(d.getDate()) + '/' + padZero(d.getMonth() + 1) + '/' + d.getFullYear() + ' ' +
		padZero(d.getHours()) + ':' + padZero(d.getMinutes());
}

function formatTableDateTime(date) {
	if (!date || isNaN(new Date(date).getTime())) return '';
	var d = new Date(date);
	return padZero(d.getDate()) + '/' + padZero(d.getMonth() + 1) + '/' + d.getFullYear() + ' ' +
		padZero(d.getHours()) + ':' + padZero(d.getMinutes());
}

function formatDateTimeLocal(date) {
	if (!date || isNaN(date.getTime())) {
		return '';
	}
	var d = new Date(date.getTime() - (date.getTimezoneOffset() * 60000));
	return d.toISOString().slice(0, 16);
}

function filterShowtimes() {
	var filterSearch = document.getElementById('filterSearch').value.trim().toLowerCase();
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

	return showtimes.filter(function(showtime) {
		if (!showtime || !showtime.ngayGioChieu || !(showtime.ngayGioChieu instanceof Date)) {
			console.warn("Invalid showtime object found:", showtime);
			return false;
		}

		// Search by movie title
		if (filterSearch && (!showtime.tenPhim || !showtime.tenPhim.toLowerCase().includes(filterSearch))) {
			return false;
		}

		var showtimeDate = new Date(showtime.ngayGioChieu);
		showtimeDate.setHours(0, 0, 0, 0);

		if (filterStatus !== 'all') {
			if (filterStatus === 'has_surcharge' && (!Array.isArray(showtime.maPhuThu) || showtime.maPhuThu.length === 0)) return false;
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
	let newViewMode;
	if (calendarView.style.display !== 'none') {
		calendarView.style.display = 'none';
		tableView.style.display = 'block';
		toggleBtn.innerHTML = '<i class="fas fa-calendar-alt"></i> Chuyển sang dạng lịch';
		newViewMode = 'table';
		renderTableView(currentPage);
	} else {
		tableView.style.display = 'none';
		calendarView.style.display = 'block';
		toggleBtn.innerHTML = '<i class="fas fa-list"></i> Chuyển sang dạng bảng';
		newViewMode = 'calendar';
		applyFilterAndRender();
	}

	// Update URL without reloading
	const url = new URL(window.location);
	url.searchParams.set('view', newViewMode);
	window.history.replaceState({}, '', url);
}

function updatePaginationLinks() {
	const paginationLinks = document.querySelectorAll('.pagination .page-link');
	paginationLinks.forEach(link => {
		let href = link.getAttribute('href');
		if (href && !href.includes('view=table')) {
			link.setAttribute('href', href + (href.includes('?') ? '&' : '?') + 'view=table');
		}
	});
}

function updatePagination(totalPages, currentPage) {
	const paginationContainer = document.getElementById('paginationContainer');
	paginationContainer.innerHTML = '';

	// Previous button
	const prevLi = document.createElement('li');
	prevLi.className = `page-item ${currentPage === 1 ? 'disabled' : ''}`;
	const prevLink = document.createElement('a');
	prevLink.className = 'page-link';
	prevLink.href = '#';
	prevLink.textContent = 'Trước';
	if (currentPage > 1) {
		prevLink.onclick = () => renderTableView(currentPage - 1);
	}
	prevLi.appendChild(prevLink);
	paginationContainer.appendChild(prevLi);

	// Page numbers
	const startPage = Math.max(1, currentPage - Math.floor(PAGES_TO_SHOW / 2));
	const endPage = Math.min(totalPages, startPage + PAGES_TO_SHOW - 1);
	for (let i = startPage; i <= endPage; i++) {
		const pageLi = document.createElement('li');
		pageLi.className = `page-item ${i === currentPage ? 'active' : ''}`;
		const pageLink = document.createElement('a');
		pageLink.className = 'page-link';
		pageLink.href = '#';
		pageLink.textContent = i;
		pageLink.onclick = () => renderTableView(i);
		pageLi.appendChild(pageLink);
		paginationContainer.appendChild(pageLi);
	}

	// Next button
	const nextLi = document.createElement('li');
	nextLi.className = `page-item ${currentPage === totalPages ? 'disabled' : ''}`;
	const nextLink = document.createElement('a');
	nextLink.className = 'page-link';
	nextLink.href = '#';
	nextLink.textContent = 'Sau';
	if (currentPage < totalPages) {
		nextLink.onclick = () => renderTableView(currentPage + 1);
	}
	nextLi.appendChild(nextLink);
	paginationContainer.appendChild(nextLi);
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
		'<div style="font-weight: bold; font-size: 11px; line-height: 1.2;">' + (showtime.tenPhim || 'N/A') + '</div>' +
		'<div style="font-size: 10px;">' + formatTime(trueStartDate) + ' - ' + formatTime(trueEndDate) + '</div>' +
		'<div class="tooltip" style="pointer-events: none;">' + getTooltipHtml(showtime, trueStartDate, trueEndDate) + '</div>';

	console.log('Attaching onclick to eventDiv with maSuat:', showtime.maSuat);
	eventDiv.onclick = function(e) {
		e.stopPropagation();
		console.log('Event block clicked, calling handleEditClick with maSuat:', showtime.maSuat);
		handleEditClick(showtime.maSuat, true); // Pass true for isFromCalendar
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

function renderTableView(page = 1) {
	currentPage = page;
	var filteredData = filterShowtimes();
	console.log("Filtered showtimes:", filteredData);

	// Paginate filtered data
	const totalItems = filteredData.length;
	const totalPages = Math.max(1, Math.ceil(totalItems / ITEMS_PER_PAGE));
	const startIndex = (page - 1) * ITEMS_PER_PAGE;
	const paginatedData = filteredData.slice(startIndex, startIndex + ITEMS_PER_PAGE);

	showtimeTableBody.innerHTML = '';
	var dataRows = showtimeTableBody.querySelectorAll('tr:not(.no-data-row)');
	for (var i = 0; i < dataRows.length; i++) {
		dataRows[i].remove();
	}

	if (paginatedData.length === 0) {
		noDataRow.style.display = 'table-row';
		updatePagination(totalPages, page);
		return;
	}

	noDataRow.style.display = 'none';

	for (var j = 0; j < paginatedData.length; j++) {
		var showtime = paginatedData[j];
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
			'<td>' + (showtime.tenPhuThu || 'Không') + '</td>' +
			'<td>' +
			'<button class="custom-btn btn-sm mr-1 btn-edit" onclick="handleEditClick(\'' + showtime.maSuat + '\')">' +
			'<i class="fas fa-edit"></i> Sửa' +
			'</button>' +
			'<button class="custom-btn btn-sm" onclick="handleDeleteClick(\'' + showtime.maSuat + '\')">' +
			'<i class="fas fa-trash"></i> Xóa' +
			'</button>' +
			'</td>';
		showtimeTableBody.appendChild(row);
	}

	updatePagination(totalPages, page);
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

		select.value = '';
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
	inputStart.name = 'showtimes[' + addTimeSlotsContainer.children.length + '].ngayGioChieu';
	inputStart.required = true;
	inputStart.onchange = function() {
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
		removeBtn.onclick = function() {
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

function getCurrentView() {
	const urlParams = new URLSearchParams(window.location.search);
	return urlParams.get('view') || 'calendar';
}

function prepareAndSubmitAddForm() {
	var form = document.getElementById('addShowtimeForm');
	hiddenShowtimeInputsContainer.innerHTML = '';

	// Cập nhật input ẩn view
	var viewInput = document.getElementById('addViewHidden');
	if (viewInput) {
		viewInput.value = getCurrentView();
	}

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

	startTimeInputs.forEach((input, index) => {
		console.log(`Time slot[${index}]: ${input.value}`);
	});

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

			var hiddenInputStart = document.createElement('input');
			hiddenInputStart.type = 'hidden';
			hiddenInputStart.name = 'showtimes[' + showtimeIndex + '].ngayGioChieu';
			hiddenInputStart.value = ngayGioChieuStr;
			hiddenShowtimeInputsContainer.appendChild(hiddenInputStart);

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
		console.log('Submitting add form with view: ' + viewInput.value);
		form.submit();
	} else if (!validationError && showtimeIndex === 0) {
		alert("Không có khung giờ hợp lệ nào được nhập để thêm.");
	}
}

function handleEditClick(maSuatChieu) {
	console.log('handleEditClick called with maSuatChieu:', maSuatChieu);

	var selectedShowtime = showtimes.find(showtime => showtime.maSuat === maSuatChieu);
	if (!selectedShowtime) {
		alert('Không tìm thấy suất chiếu với mã ' + maSuatChieu);
		console.error('Showtime not found:', maSuatChieu);
		console.log('Current showtimes:', showtimes);
		return;
	}

	console.log('Selected showtime:', selectedShowtime);
	showEditModal(selectedShowtime);
}

function addEditPhuThuTag() {
	const select = document.getElementById('editPhuThuSelect');
	if (!select) {
		console.error('Element #editPhuThuSelect not found in DOM');
		return;
	}
	const value = select.value;
	if (select.selectedIndex === -1) {
		console.warn('No option selected in #editPhuThuSelect');
		return;
	}
	let text = select.options[select.selectedIndex].text || '';
	if (value && text) {
		const tagContainer = document.getElementById('editPhuThuContainer');
		if (!tagContainer) {
			console.error('Element #editPhuThuContainer not found in DOM');
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
			removeSpan.textContent = '×';
			removeSpan.addEventListener('click', function() {
				removeEditPhuThuTag(this);
			});
			newTag.appendChild(removeSpan);
			tagContainer.appendChild(newTag);
			console.log('Added new phuThu tag:', value);
		}
		select.value = '';
		updateEditPhuThuHidden();
	}
}

function removeEditPhuThuTag(element) {
	if (element && element.parentElement) {
		element.parentElement.remove();
		updateEditPhuThuHidden();
		console.log('Removed phuThu tag');
	} else {
		console.error('Invalid element passed to removeEditPhuThuTag');
	}
}

function updateEditPhuThuHidden() {
	const container = document.getElementById('editPhuThuContainer');
	const hiddenInput = document.getElementById('editPhuThuHidden');
	if (!container || !hiddenInput) {
		console.error('Element #editPhuThuContainer or #editPhuThuHidden not found in DOM');
		return;
	}
	const tags = Array.from(container.querySelectorAll('.tag'))
		.map(tag => tag.getAttribute('data-ma-phu-thu'));
	hiddenInput.value = tags.join(',');
	console.log('Updated editPhuThuHidden with value:', hiddenInput.value);
}

function showEditModal(selectedShowtime) {
	const form = document.getElementById('editShowtimeForm');
	if (!form) {
		console.error('Element #editShowtimeForm not found in DOM');
		alert('Lỗi: Không tìm thấy form sửa. Vui lòng làm mới trang.');
		return;
	}

	if (!editModal) {
		console.error('Element #editModal not found in DOM');
		alert('Lỗi: Không tìm thấy modal sửa. Vui lòng làm mới trang.');
		return;
	}

	console.log('showEditModal called with showtime:', selectedShowtime);

	if (!selectedShowtime || !selectedShowtime.maSuat) {
		console.error('Invalid showtime object:', selectedShowtime);
		alert('Không tìm thấy thông tin suất chiếu để sửa.');
		return;
	}

	form.action = contextPath + '/admin/showtimes/update/' + selectedShowtime.maSuat;
	console.log('Set editShowtimeForm action to:', form.action);

	form.reset();
	console.log('showEditModal: selectedShowtime data:', {
		maSuat: selectedShowtime.maSuat,
		maPhim: selectedShowtime.maPhim,
		maRap: selectedShowtime.maRap,
		maPhongChieu: selectedShowtime.maPhong,
		tenPhong: selectedShowtime.tenPhong,
		ngayGioChieu: selectedShowtime.ngayGioChieu,
		loaiManChieu: selectedShowtime.loaiManChieu,
		maPhuThu: selectedShowtime.maPhuThu
	});

	const fields = [
		{ id: 'editMaSuatDisplay', value: selectedShowtime.maSuat },
		{ id: 'editPhim', value: selectedShowtime.maPhim },
		{ id: 'editRapChieu', value: selectedShowtime.maRap },
		{ id: 'editNgayGioChieu', value: formatDateTimeLocal(selectedShowtime.ngayGioChieu) },
		{ id: 'editLoaiManChieu', value: selectedShowtime.loaiManChieu }
	];
	fields.forEach(field => {
		const element = document.getElementById(field.id);
		if (element) {
			element.value = field.value || '';
			console.log(`Set ${field.id} to:`, element.value);
		} else {
			console.warn('Element #' + field.id + ' not found in DOM');
		}
	});

	const phongSelect = document.getElementById('editPhongChieu');
	if (!phongSelect) {
		console.error('Element #editPhongChieu not found in DOM');
		return;
	}
	filterEditPhongChieu(selectedShowtime.maRap, selectedShowtime.maPhong);
	console.log('editPhongChieu options:', Array.from(phongSelect.options).map(opt => ({
		value: opt.value,
		text: opt.text,
		dataRap: opt.dataset.rap
	})));
	phongSelect.value = selectedShowtime.maPhong || '';
	console.log('Set editPhongChieu to:', phongSelect.value);

	const phuThuContainer = document.getElementById('editPhuThuContainer');
	if (!phuThuContainer) {
		console.error('Element #editPhuThuContainer not found in DOM');
		return;
	}
	phuThuContainer.innerHTML = '';
	if (Array.isArray(selectedShowtime.maPhuThu) && selectedShowtime.maPhuThu.length > 0) {
		selectedShowtime.maPhuThu.forEach(maPhuThu => {
			const safeMaPhuThu = String(maPhuThu).replace(/"/g, '\\"');
			const option = document.querySelector(`#editPhuThuSelect option[value="${safeMaPhuThu}"]`);
			if (option) {
				const newTag = document.createElement('span');
				newTag.className = 'tag';
				newTag.setAttribute('data-ma-phu-thu', maPhuThu);
				newTag.textContent = option.text + ' ';
				const removeSpan = document.createElement('span');
				removeSpan.className = 'remove-tag';
				removeSpan.textContent = '×';
				removeSpan.addEventListener('click', function() {
					removeEditPhuThuTag(this);
				});
				newTag.appendChild(removeSpan);
				phuThuContainer.appendChild(newTag);
				console.log('Loaded phuThu tag:', maPhuThu);
			}
		});
	}

	const editPhuThuSelect = document.getElementById('editPhuThuSelect');
	if (editPhuThuSelect) {
		editPhuThuSelect.value = '';
	} else {
		console.warn('Element #editPhuThuSelect not found in DOM');
	}

	const viewInput = document.getElementById('editViewHidden');
	if (viewInput) {
		viewInput.value = getCurrentView();
		console.log('editViewHidden set to:', viewInput.value);
	} else {
		console.error('Element #editViewHidden not found in DOM');
	}

	updateEditPhuThuHidden();
	updateEditThoiLuongAndEndTime();

	editModal.style.display = 'flex';
	console.log('Edit modal displayed');
}

function filterEditPhongChieu(maRap, maPhong) {
	var rapChieu = document.getElementById('editRapChieu').value;
	var phongSelect = document.getElementById('editPhongChieu');
	var firstVisibleOptionValue = null;

	phongSelect.value = ''; // Xóa giá trị cũ

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

	// Gán giá trị maPhong nếu tồn tại
	if (maPhong && phongSelect.querySelector(`option[value="${maPhong}"]`)) {
		phongSelect.value = maPhong;
	} else {
		phongSelect.selectedIndex = 0; // Mặc định
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
	var viewInput = document.getElementById('editViewHidden');
	console.log('editViewHidden element:', viewInput); // Debug để kiểm tra phần tử
	if (!viewInput) {
		console.error('Element with ID "editViewHidden" not found in DOM');
		alert('Lỗi: Không tìm thấy phần tử hidden cho view. Vui lòng làm mới trang.');
		return;
	}
	viewInput.value = getCurrentView(); // Gán giá trị view từ URL hiện tại

	if (!document.getElementById('editPhim').value ||
		!document.getElementById('editRapChieu').value ||
		!document.getElementById('editPhongChieu').value ||
		!document.getElementById('editNgayGioChieu').value) {
		alert('Vui lòng điền đầy đủ các trường bắt buộc (Phim, Rạp, Phòng, Giờ chiếu).');
		return;
	}
	console.log('Submitting edit form with view: ' + viewInput.value);
	document.getElementById('editShowtimeForm').submit();
}

// Cập nhật handleDeleteClick
function handleDeleteClick(maSuat) {
	var selectedShowtime = showtimes.find(showtime => showtime.maSuat === maSuat);

	var confirmMsg = selectedShowtime
		? 'Bạn có chắc muốn xóa suất chiếu "' + (selectedShowtime.tenPhim || 'N/A') + '" (' + maSuat + ') lúc ' +
		formatTableDateTime(selectedShowtime.ngayGioChieu) + '?'
		: 'Bạn có chắc muốn xóa suất chiếu "' + maSuat + '"?';

	if (confirm(confirmMsg + "\nHành động này không thể hoàn tác.")) {
		var deleteMaSuatHidden = document.getElementById('deleteMaSuatHidden');
		var deleteViewHidden = document.getElementById('deleteViewHidden');
		if (!deleteMaSuatHidden || !deleteViewHidden) {
			console.error('One or more hidden inputs not found in delete form');
			alert('Lỗi: Không tìm thấy phần tử hidden. Vui lòng làm mới trang.');
			return;
		}

		deleteMaSuatHidden.value = maSuat;
		deleteViewHidden.value = getCurrentView(); // Gán giá trị view từ URL
		console.log('Submitting delete form with maSuat:', maSuat, 'and view:', deleteViewHidden.value);

		// Không cần set action vì form đã có action mặc định
		document.getElementById('deleteShowtimeForm').submit();
	}
}

function handleDeleteClick(maSuat) {
	var selectedShowtime = showtimes.find(showtime => showtime.maSuat === maSuat);

	var confirmMsg = selectedShowtime
		? 'Bạn có chắc muốn xóa suất chiếu "' + (selectedShowtime.tenPhim || 'N/A') + '" (' + maSuat + ') lúc ' +
		formatTableDateTime(selectedShowtime.ngayGioChieu) + '?'
		: 'Bạn có chắc muốn xóa suất chiếu "' + maSuat + '"?';

	if (confirm(confirmMsg + "\nHành động này không thể hoàn tác.")) {
		var deleteMaSuatHidden = document.getElementById('deleteMaSuatHidden');
		var deleteViewHidden = document.getElementById('deleteViewHidden');
		if (!deleteMaSuatHidden || !deleteViewHidden) {
			console.error('One or more hidden inputs not found in delete form');
			alert('Lỗi: Không tìm thấy phần tử hidden. Vui lòng làm mới trang.');
			return;
		}

		deleteMaSuatHidden.value = maSuat;
		deleteViewHidden.value = getCurrentView(); // Gán giá trị view từ URL
		console.log('Submitting delete form with maSuat:', maSuat, 'and view:', deleteViewHidden.value);

		// Submit form với action mặc định
		document.getElementById('deleteShowtimeForm').submit();
	}
}

document.addEventListener('DOMContentLoaded', function() {
    // Initialize elements
    calendarView = document.getElementById('calendarView');
    tableView = document.getElementById('tableView');
    toggleBtn = document.getElementById('toggleViewBtn');
    showtimeTableBody = document.getElementById('showtimeTableBody');
    noDataRow = tableView ? tableView.querySelector('.no-data-row') : null;
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

    // Kiểm tra các phần tử quan trọng
    if (!editModal || !noDataRow) {
        console.error('Critical elements missing: editModal or noDataRow not found in DOM');
        alert('Lỗi: Một số thành phần quan trọng không được tìm thấy. Vui lòng làm mới trang.');
        return; // Ngăn chặn tiếp tục nếu thiếu phần tử quan trọng
    }

    // Initialize showtimes
	try {
	    console.log("serverData:", serverData);
	    if (serverData && Array.isArray(serverData)) {
	        showtimes = [];
	        serverData.forEach((st, index) => {
	            try {
	                if (!st.maSuat || !st.maPhim) {
	                    console.warn(`Skipping invalid showtime at index ${index}: missing maSuat or maPhim`, st);
	                    return;
	                }
	                const ngayGioChieu = st.ngayGioChieu ? new Date(st.ngayGioChieu) : null;
	                const ngayGioKetThuc = st.ngayGioKetThuc ? new Date(st.ngayGioKetThuc) : null;
	                if (ngayGioChieu && isNaN(ngayGioChieu.getTime())) {
	                    console.warn(`Skipping showtime ${st.maSuat}: invalid ngayGioChieu`, st.ngayGioChieu);
	                    return;
	                }
	                if (ngayGioKetThuc && isNaN(ngayGioKetThuc.getTime())) {
	                    console.warn(`Skipping showtime ${st.maSuat}: invalid ngayGioKetThuc`, st.ngayGioKetThuc);
	                    return;
	                }
	                showtimes.push({
	                    maSuat: st.maSuat,
	                    maPhim: st.maPhim,
	                    tenPhim: st.tenPhim || 'N/A',
	                    thoiLuong: st.thoiLuong || 0,
	                    maRap: st.maRap,
	                    tenRap: st.tenRap || 'N/A',
	                    maPhong: st.maPhong,
	                    tenPhong: st.tenPhong || 'N/A',
	                    ngayGioChieu: ngayGioChieu,
	                    ngayGioKetThuc: ngayGioKetThuc,
	                    loaiManChieu: st.loaiManChieu || 'N/A',
	                    maPhuThu: Array.isArray(st.maPhuThu) ? st.maPhuThu : [],
	                    tenPhuThu: st.tenPhuThu || 'Không có',
	                    status: st.status || 'not_started'
	                });
	            } catch (e) {
	                console.warn(`Error processing showtime at index ${index}:`, e, st);
	            }
	        });

	        var validShowtimes = showtimes.filter(st => st.ngayGioChieu && st.ngayGioKetThuc && !isNaN(st.ngayGioChieu.getTime()) && !isNaN(st.ngayGioKetThuc.getTime()));
	        showtimes = validShowtimes;
	        console.log("Initialized showtimes:", showtimes.map(st => st.maSuat));
	    } else {
	        console.warn("Server data not valid. Initializing empty array.");
	        showtimes = [];
	    }
	} catch (e) {
	    console.error("Error initializing showtimes:", e);
	    showtimes = [];
	}

    const editableShowtimeMatch = document.querySelector('script[data-editable-showtime]') ? document.querySelector('script[data-editable-showtime]').getAttribute('data-editable-showtime') : null;
    if (editableShowtimeMatch) {
        const maSuatChieu = editableShowtimeMatch;
        console.log('Found editableShowtime:', maSuatChieu);
        var selectedShowtime = showtimes.find(showtime => showtime.maSuat === maSuatChieu);
        if (selectedShowtime) {
            console.log('Opening edit modal for:', maSuatChieu);
            showEditModal(selectedShowtime);
        } else {
            console.error('Editable showtime not found in showtimes:', maSuatChieu);
            alert('Không tìm thấy suất chiếu với mã ' + maSuatChieu + ' để sửa.');
        }
    } else {
        console.log('No editableShowtime found in script tags.');
    }

    // Set initial view
    const urlParams = new URLSearchParams(window.location.search);
    const viewMode = urlParams.get('view') || 'calendar';
    if (viewMode === 'table') {
        calendarView.style.display = 'none';
        tableView.style.display = 'block';
        toggleBtn.innerHTML = '<i class="fas fa-calendar-alt"></i> Chuyển sang dạng lịch';
        renderTableView();
    } else {
        tableView.style.display = 'none';
        calendarView.style.display = 'block';
        toggleBtn.innerHTML = '<i class="fas fa-list"></i> Chuyển sang dạng bảng';
        applyFilterAndRender();
    }

    // Prevent form submission and apply filters
    const filterForm = document.getElementById('filterForm');
    if (filterForm) {
        filterForm.addEventListener('submit', function(event) {
            event.preventDefault();
            applyFilterAndRender();
        });
    }

    // Add filter event listeners
    var filterSearch = document.getElementById('filterSearch');
    if (filterSearch) filterSearch.addEventListener('input', applyFilterAndRender);

    var filterStatus = document.getElementById('filterStatus');
    if (filterStatus) filterStatus.addEventListener('change', applyFilterAndRender);

    var filterTime = document.getElementById('filterTime');
    if (filterTime) filterTime.addEventListener('change', applyFilterAndRender);

    var filterRap = document.getElementById('filterRap');
    if (filterRap) filterRap.addEventListener('change', applyFilterAndRender);

    var filterPhong = document.getElementById('filterPhong');
    if (filterPhong) filterPhong.addEventListener('change', applyFilterAndRender);

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
            e.preventDefault(); // Ngăn chặn submit mặc định để kiểm tra
            updatePhuThuHidden();
            console.log('Form submitting with phuThuHidden: ' + document.getElementById('phuThuHidden').value);
            // Thêm logic submit form nếu cần
            addForm.submit(); // Gửi form nếu mọi thứ hợp lệ
        });
    }

    updateAddThoiLuong();
    filterAddPhongChieu();
});