document.getElementById('registerForm').addEventListener('submit', function(e) {
    let valid = true;
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    const confirmError = document.getElementById('confirmError');

    if (password !== confirmPassword) {
        confirmError.style.display = 'block';
        valid = false;
    } else {
        confirmError.style.display = 'none';
    }

    if (!valid) {
        e.preventDefault();
    }
});