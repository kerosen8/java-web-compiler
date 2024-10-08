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

function showSaveModal() {
    document.getElementById('saveModal').style.display = 'block';
}

function closeModal() {
    document.getElementById('saveModal').style.display = 'none';
}

function submitForm() {
    const fileName = document.getElementById('savedCodeTitle').value;
    if (fileName) {
        const form = document.getElementById('codeForm');
        const actionInput = document.createElement('input');
        actionInput.type = 'hidden';
        actionInput.name = 'action';
        actionInput.value = 'save';

        const fileNameInput = document.createElement('input');
        fileNameInput.type = 'hidden';
        fileNameInput.name = 'savedCodeTitle';
        fileNameInput.value = fileName;

        form.appendChild(actionInput);
        form.appendChild(fileNameInput);
        form.submit();
    } else {
        alert('Please enter a file name.');
    }
}