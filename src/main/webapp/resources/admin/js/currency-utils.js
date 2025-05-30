/**
 * Hàm format số thành hàng ngàn, bỏ phần thập phân nếu là số nguyên
 * @param {number|string} number
 * @returns {string}
 */
function formatCurrencyWithDecimal(number) {
    const num = parseFloat(number);
    if (isNaN(num)) return '';
    if (Number.isInteger(num)) {
        const formattedInteger = num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
        return `${formattedInteger} đ`;
    } else {
        const formatted = num.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',');
        return `${formatted} đ`;
    }
}

/**
 * Hàm parse giá trị từ input sang số float
 * @param {string} value
 * @returns {number|null}
 */
function parseCurrencyInput(value) {
    const cleaned = value.replace(/\s+/g, '').replace(/[^0-9.]/g, '');
    const num = parseFloat(cleaned);
    return isNaN(num) ? null : num;
}