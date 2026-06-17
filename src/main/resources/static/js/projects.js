const API = 'http://localhost:8080';

const auth = localStorage.getItem('auth');
const username = localStorage.getItem('username');

if (!auth || !username) {
    window.location.replace('index.html');
    throw new Error('Not authenticated');
}
document.getElementById('welcomeMsg').textContent = `👋 ${username}`;

function logout() {
    localStorage.clear();
    window.location.href = 'index.html';
}

function showAlert(message, type) {
    const alert = document.getElementById('alert');
    alert.className = `alert alert-${type}`;
    alert.textContent = message;
    alert.style.display = 'block';
    setTimeout(() => alert.style.display = 'none', 3000);
}

function getHeaders() {
    return {
        'Authorization': `Basic ${auth}`,
        'Content-Type': 'application/json'
    };
}

function openModal() {
    document.getElementById('projectModal').classList.add('active');
}

function closeModal() {
    document.getElementById('projectModal').classList.remove('active');
    document.getElementById('projectName').value = '';
    document.getElementById('projectDesc').value = '';
}

async function loadProjects() {
    try {
        const response = await fetch(`${API}/api/projects`, {
            headers: getHeaders()
        });

        if (response.status === 401) {
            window.location.href = 'index.html';
            return;
        }

        const data = await response.json();
        const projects = data.content || [];

        const list = document.getElementById('projectsList');

        if (projects.length === 0) {
            list.innerHTML = '<p>Nuk ka projekte ende. Krijo njërin!</p>';
            return;
        }

        list.innerHTML = projects.map(p => `
            <div class="card">
                <h3>${p.name}</h3>
                <p style="color:#666; margin:8px 0">${p.description || 'Pa pershkrim'}</p>
                <p style="font-size:12px; color:#999">Owner: ${p.ownerUsername}</p>
                <p style="font-size:12px; color:#999">Krijuar: ${new Date(p.createdAt).toLocaleDateString()}</p>
                <div style="margin-top:15px; display:flex; gap:10px">
                    <button class="btn btn-primary" onclick="viewTasks(${p.id}, '${p.name}', '${p.ownerUsername}')">
                        Shiko Tasks
                    </button>
                    ${p.ownerUsername === username ? `
                        <button class="btn btn-danger" onclick="deleteProject(${p.id})">Fshi</button>
                    ` : ''}
                </div>
            </div>
        `).join('');

    } catch (error) {
        showAlert('Gabim në ngarkimin e projekteve!', 'error');
    }
}

async function createProject() {
    const name = document.getElementById('projectName').value.trim();
    const description = document.getElementById('projectDesc').value.trim();

    if (!name || name.length < 3) {
        showAlert('Emri duhet të ketë min 3 karaktere!', 'error');
        return;
    }

    // Gjej ID e userit të loguar
    try {
        const userRes = await fetch(`${API}/api/users?size=100`, {
            headers: getHeaders()
        });
        const userData = await userRes.json();
        const currentUser = userData.content.find(u => u.username === username);

        if (!currentUser) {
            showAlert('Gabim: Useri nuk u gjet!', 'error');
            return;
        }

        const response = await fetch(`${API}/api/projects`, {
            method: 'POST',
            headers: getHeaders(),
            body: JSON.stringify({
                name,
                description,
                ownerId: currentUser.id
            })
        });

        if (response.ok) {
            showAlert('Projekti u krijua me sukses!', 'success');
            closeModal();
            loadProjects();
        } else {
            const error = await response.json();
            showAlert(error.name || 'Gabim gjatë krijimit!', 'error');
        }
    } catch (error) {
        showAlert('Gabim në lidhje me serverin!', 'error');
    }
}

async function deleteProject(id) {
    if (!confirm('A jeni i sigurt që doni të fshini këtë projekt?')) return;

    try {
        const response = await fetch(`${API}/api/projects/${id}`, {
            method: 'DELETE',
            headers: getHeaders()
        });

        if (response.ok) {
            showAlert('Projekti u fshi!', 'success');
            loadProjects();
        } else {
            showAlert('Gabim gjatë fshirjes!', 'error');
        }
    } catch (error) {
        showAlert('Gabim në lidhje me serverin!', 'error');
    }
}

function viewTasks(projectId, projectName, ownerUsername) {
    localStorage.setItem('projectId', projectId);
    localStorage.setItem('projectName', projectName);
    localStorage.setItem('projectOwner', ownerUsername);
    window.location.href = 'tasks.html';
}

// Ngarko projektet kur hapet faqja
loadProjects();