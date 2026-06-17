const API = 'http://localhost:8080';
const auth = localStorage.getItem('auth');
const currentPage = window.location.pathname;

if (auth && (currentPage.endsWith('index.html') || currentPage.endsWith('/'))) {
    window.location.replace('projects.html');
}
if (localStorage.getItem('auth') && window.location.pathname.endsWith('index.html')) {
    window.location.href = 'projects.html';
}

function showTab(tab) {
    document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));

    if (tab === 'login') {
        document.querySelector('.tab:first-child').classList.add('active');
        document.getElementById('loginForm').style.display = 'block';
        document.getElementById('registerForm').style.display = 'none';
    } else {
        document.querySelector('.tab:last-child').classList.add('active');
        document.getElementById('loginForm').style.display = 'none';
        document.getElementById('registerForm').style.display = 'block';
    }
}

function showAlert(message, type) {
    const alert = document.getElementById('alert');
    alert.className = `alert alert-${type}`;
    alert.textContent = message;
    alert.style.display = 'block';
    setTimeout(() => alert.style.display = 'none', 3000);
}

async function login() {
    const username = document.getElementById('loginUsername').value.trim();
    const password = document.getElementById('loginPassword').value.trim();

    if (!username || !password) {
        showAlert('Plotëso të gjitha fushat!', 'error');
        return;
    }

    const credentials = btoa(`${username}:${password}`);

    try {
        const response = await fetch(`${API}/api/users`, {
            headers: { 'Authorization': `Basic ${credentials}` }
        });

        if (response.ok) {
            localStorage.setItem('auth', credentials);
            localStorage.setItem('username', username);
            window.location.href = 'projects.html';
        } else {
            showAlert('Username ose password i gabuar!', 'error');
        }
    } catch (error) {
        showAlert('Gabim në lidhje me serverin!', 'error');
    }
}

async function register() {
    const username = document.getElementById('regUsername').value.trim();
    const email = document.getElementById('regEmail').value.trim();
    const password = document.getElementById('regPassword').value.trim();

    if (!username || !email || !password) {
        showAlert('Plotëso të gjitha fushat!', 'error');
        return;
    }
    if (username.length < 3) {
        showAlert('Username duhet të ketë min 3 karaktere!', 'error');
        return;
    }
    if (password.length < 8) {
        showAlert('Password duhet të ketë min 8 karaktere!', 'error');
        return;
    }

    try {
        const response = await fetch(`${API}/api/users`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, email, password })
        });

        if (response.ok) {
            document.getElementById('regUsername').value = '';
            document.getElementById('regEmail').value = '';
            document.getElementById('regPassword').value = '';

            showAlert('Regjistrim i suksesshëm! Tani mund të logohesh.', 'success');

            setTimeout(() => showTab('login'), 1500);
        } else {
            const error = await response.json();
            showAlert(error.error || 'Gabim gjatë regjistrimit!', 'error');
        }
    } catch (error) {
        showAlert('Gabim në lidhje me serverin!', 'error');
    }
}