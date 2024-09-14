document.getElementById('code').addEventListener('keydown', function (e) {
    if (e.key === 'Tab') {
        e.preventDefault();
        const tabSize = 4;
        const textarea = e.target;
        const start = textarea.selectionStart;
        const end = textarea.selectionEnd;
        textarea.value = textarea.value.substring(0, start) + ' '.repeat(tabSize) + textarea.value.substring(end);
        textarea.selectionStart = textarea.selectionEnd = start + tabSize;
    }

})
