const rows = 20;
const cols = 20;

function initSeatGrid(containerId, editable = false, initialData = [], maxCapacity = null) {
    const seatGrid = document.getElementById(containerId);
    if (!seatGrid) {
        console.error(`Container with ID ${containerId} not found`);
        return null;
    }

    let gridData = Array(rows).fill().map(() => Array(cols).fill(null));
    let isDragging = false;
    let startCell = null;
    let seatTypes = [];

    // Lấy danh sách loại ghế từ API
    function fetchSeatTypes() {
        return fetch(`${contextPath}/admin/theater-rooms/seat-types/list`)
            .then(response => {
                if (!response.ok) throw new Error('Failed to fetch seat types: ' + response.status);
                return response.json();
            })
            .then(data => {
                seatTypes = data || [];
                console.log('Seat types loaded:', seatTypes);
                if (!data || data.length === 0) {
                    console.warn('No seat types loaded');
                }
                return data;
            })
            .catch(error => {
                console.error('Error fetching seat types:', error);
                alert('Không thể tải danh sách loại ghế: ' + error.message);
                return [];
            });
    }

    // Lấy màu ghế dựa trên maLoaiGhe
    function getSeatColor(type) {
        if (!type) {
            console.warn('Seat type is undefined or null');
            return '#f0f0f0';
        }
        const seatType = seatTypes.find(st => st.maLoaiGhe === type);
        if (!seatType) {
            console.warn(`No seat type found for maLoaiGhe: ${type}`);
            return '#f0f0f0';
        }
        return seatType.mauGhe || '#f0f0f0';
    }

    // Lấy số chỗ của loại ghế
    function getSeatCapacity(type) {
        if (!type) return 1;
        const seatType = seatTypes.find(st => st.maLoaiGhe === type);
        return seatType ? seatType.soCho || 1 : 1;
    }

    // Tạo lưới ghế
    function createGrid() {
        seatGrid.innerHTML = '';
        const colLabels = document.createElement('div');
        colLabels.className = 'seat-row seat-labels';
        colLabels.innerHTML = '<div class="seat-cell empty"></div>';
        for (let j = 1; j <= cols; j++) {
            const label = document.createElement('div');
            label.className = 'seat-cell label';
            label.textContent = j;
            colLabels.appendChild(label);
        }
        seatGrid.appendChild(colLabels);

        for (let i = 0; i < rows; i++) {
            const row = document.createElement('div');
            row.className = 'seat-row';
            const rowLabel = document.createElement('div');
            rowLabel.className = 'seat-cell label';
            rowLabel.textContent = String.fromCharCode(65 + i);
            row.appendChild(rowLabel);

            for (let j = 0; j < cols; j++) {
                const cell = document.createElement('div');
                cell.className = 'seat-cell empty';
                cell.dataset.row = i;
                cell.dataset.col = j;
                if (editable) {
                    cell.addEventListener('mousedown', startDrag);
                }
                row.appendChild(cell);
            }
            seatGrid.appendChild(row);
        }

        if (editable) {
            document.addEventListener('mousemove', dragOver);
            document.addEventListener('mouseup', endDrag);
        }
    }

    // Tải dữ liệu ghế
    function loadSeats(seatData) {
        const roomSeats = Array.isArray(seatData) ? seatData : [];
        console.log('Loading seats:', roomSeats);
        if (roomSeats.length === 0) {
            console.log('No seats to load, displaying empty grid');
            return;
        }
        roomSeats.forEach(seat => {
            if (!seat || typeof seat !== 'object') {
                console.warn('Invalid seat data:', seat);
                return;
            }
            const row = seat.row !== undefined ? parseInt(seat.row) : -1;
            const col = seat.col !== undefined ? parseInt(seat.col) : -1;
            if (isNaN(row) || isNaN(col) || row >= rows || col >= cols || row < 0 || col < 0) {
                console.warn(`Invalid seat position: row ${row}, col ${col}, seat:`, seat);
                return;
            }
            const cell = seatGrid.querySelector(`.seat-cell[data-row="${row}"][data-col="${col}"]`);
            if (cell) {
                gridData[row][col] = seat.type;
                const color = getSeatColor(seat.type);
                console.log(`Setting seat at row ${row}, col ${col}, type ${seat.type}, color ${color}`);
                cell.style.backgroundColor = color;
                cell.textContent = `${seat.tenHang || String.fromCharCode(65 + row)}${seat.soGhe || (col + 1)}`;
                cell.dataset.type = seat.type;
                cell.classList.remove('empty');
            } else {
                console.warn(`No cell found for seat at row ${row}, col ${col}`);
            }
        });
    }

    // Bắt đầu kéo
    function startDrag(event) {
        event.preventDefault();
        isDragging = true;
        startCell = event.target;
        if (startCell.classList.contains('seat-cell') && !startCell.classList.contains('label')) {
            toggleSeat(startCell);
            startCell.classList.add('dragging');
        }
    }

    // Kéo qua
    function dragOver(event) {
        if (!isDragging || !startCell) return;
        event.preventDefault();
        const currentCell = document.elementFromPoint(event.clientX, event.clientY);
        if (currentCell && currentCell.classList.contains('seat-cell') && !currentCell.classList.contains('label')) {
            selectRange(startCell, currentCell);
        }
    }

    // Kết thúc kéo
    function endDrag() {
        if (isDragging) {
            const draggingCells = document.querySelectorAll('.seat-cell.dragging');
            draggingCells.forEach(cell => cell.classList.remove('dragging'));
        }
        isDragging = false;
        startCell = null;
    }

    // Chọn phạm vi
    function selectRange(start, end) {
        const startRow = parseInt(start.dataset.row);
        const startCol = parseInt(start.dataset.col);
        const endRow = parseInt(end.dataset.row);
        const endCol = parseInt(end.dataset.col);

        const minRow = Math.min(startRow, endRow);
        const maxRow = Math.max(startRow, endRow);
        const minCol = Math.min(startCol, endCol);
        const maxCol = Math.max(startCol, endCol);

        const cells = document.querySelectorAll(`#${containerId} .seat-cell:not(.label)`);
        cells.forEach(cell => {
            const row = parseInt(cell.dataset.row);
            const col = parseInt(cell.dataset.col);
            if (row >= minRow && row <= maxRow && col >= minCol && col <= maxCol) {
                if (!cell.classList.contains('dragging')) {
                    toggleSeat(cell);
                    cell.classList.add('dragging');
                }
            } else {
                cell.classList.remove('dragging');
            }
        });
    }

    // Thêm/xóa ghế
    function toggleSeat(cell) {
        const row = parseInt(cell.dataset.row);
        const col = parseInt(cell.dataset.col);
        const loaiGheSelect = document.getElementById('loaiGheEdit');
        const selectedLoaiGhe = loaiGheSelect ? loaiGheSelect.value : null;
        const selectedColor = loaiGheSelect ? getSeatColor(selectedLoaiGhe) : '#f0f0f0';

        if (!selectedLoaiGhe && !gridData[row][col]) {
            alert('Vui lòng chọn loại ghế trước khi thêm ghế!');
            return;
        }

        if (gridData[row][col]) {
            gridData[row][col] = null;
            cell.style.backgroundColor = 'transparent';
            cell.textContent = '';
            cell.dataset.type = '';
            cell.classList.add('empty');
        } else {
            gridData[row][col] = selectedLoaiGhe;
            cell.style.backgroundColor = selectedColor;
            cell.textContent = `${String.fromCharCode(65 + row)}${col + 1}`;
            cell.dataset.type = selectedLoaiGhe;
            cell.classList.remove('empty');
        }

        if (editable && maxCapacity !== null) {
            const seatData = getSeatData();
            updateCapacityInfo(seatData.totalCapacity, maxCapacity);
        }
    }

    // Xóa toàn bộ ghế
    function resetGrid() {
        gridData = Array(rows).fill().map(() => Array(cols).fill(null));
        const cells = document.querySelectorAll(`#${containerId} .seat-cell:not(.label)`);
        cells.forEach(cell => {
            cell.style.backgroundColor = 'transparent';
            cell.textContent = '';
            cell.classList.remove('dragging');
            cell.classList.add('empty');
            cell.dataset.type = '';
        });

        if (editable && maxCapacity !== null) {
            updateCapacityInfo(0, maxCapacity);
        }
    }

    // Lấy dữ liệu ghế
    function getSeatData() {
        const updatedSeats = [];
        let totalCapacity = 0;

        for (let i = 0; i < rows; i++) {
            for (let j = 0; j < cols; j++) {
                if (gridData[i][j]) {
                    const seatType = seatTypes.find(st => st.maLoaiGhe === gridData[i][j]);
                    const seatCapacity = seatType ? seatType.soCho : 1;

                    updatedSeats.push({
                        row: i,
                        col: j,
                        tenHangAdmin: String.fromCharCode(65 + i),
                        soGheAdmin: String(j + 1),
                        type: gridData[i][j],
                        color: getSeatColor(gridData[i][j]),
                        capacity: seatCapacity
                    });

                    totalCapacity += seatCapacity;
                }
            }
        }

        console.log('Returning seat data:', {
            seats: updatedSeats,
            totalCapacity: totalCapacity
        });

        return {
            seats: updatedSeats,
            totalCapacity: totalCapacity
        };
    }

    // Cập nhật thông tin sức chứa
    function updateCapacityInfo(currentCapacity, maxCapacity) {
        const capacityInfo = document.getElementById('capacityInfo');
        if (capacityInfo) {
            capacityInfo.innerHTML = `Sức chứa: <span class="${currentCapacity > maxCapacity ? 'text-danger' : 'text-success'}">${currentCapacity}</span> / ${maxCapacity}`;
            if (currentCapacity > maxCapacity) {
                alert('Tổng số ghế vượt quá sức chứa cho phép!');
            }
        }
    }

    // Dọn dẹp sự kiện
    function cleanup() {
        if (editable) {
            document.removeEventListener('mousemove', dragOver);
            document.removeEventListener('mouseup', endDrag);
            const cells = seatGrid.querySelectorAll('.seat-cell:not(.label)');
            cells.forEach(cell => {
                cell.removeEventListener('mousedown', startDrag);
            });
        }
    }

    // Khởi tạo
    fetchSeatTypes().then(() => {
        console.log('Grid creation started');
        createGrid();
        console.log('Loading seats with data:', initialData);
        loadSeats(initialData);

        if (editable && maxCapacity !== null) {
            const seatData = getSeatData();
            updateCapacityInfo(seatData.totalCapacity, maxCapacity);
        }
    });

    // Cập nhật màu khi thay đổi loại ghế
    if (editable) {
        const loaiGheSelect = document.getElementById('loaiGheEdit');
        if (loaiGheSelect) {
            loaiGheSelect.addEventListener('change', () => {
                console.log('Seat type changed, updating colors');
                const cells = seatGrid.querySelectorAll('.seat-cell:not(.label):not(.empty)');
                cells.forEach(cell => {
                    const row = parseInt(cell.dataset.row);
                    const col = parseInt(cell.dataset.col);
                    if (gridData[row][col]) {
                        const color = getSeatColor(gridData[row][col]);
                        cell.style.backgroundColor = color;
                        console.log(`Updated seat at row ${row}, col ${col} to color ${color}`);
                    }
                });

                if (maxCapacity !== null) {
                    const seatData = getSeatData();
                    updateCapacityInfo(seatData.totalCapacity, maxCapacity);
                }
            });
        }
    }

    return {
        resetGrid,
        getSeatData,
        loadSeats,
        updateCapacityInfo,
        cleanup
    };
}