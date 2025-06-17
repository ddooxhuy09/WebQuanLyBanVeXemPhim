/**
 * Hàm chỉ cho phép nhập số nguyên
 * @param {HTMLInputElement} inputElement
 */
function restrictToIntegerInput(inputElement) {
    inputElement.addEventListener('input', function (e) {
        this.value = this.value.replace(/[^0-9]/g, '');
    });

    inputElement.addEventListener('paste', function (e) {
        e.preventDefault();
        const pastedData = e.clipboardData.getData('text').replace(/[^0-9]/g, '');
        document.execCommand('insertText', false, pastedData);
    });
}