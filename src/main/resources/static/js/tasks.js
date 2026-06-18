const API = 'http://localhost:8080';

const auth = localStorage.getItem('auth');
const username = localStorage.getItem('username');
const projectId = localStorage.getItem('projectId');
const projectName = localStorage.getItem('projectName');
const projectOwner = localStorage.getItem('projectOwner');

if (!auth || !username || !projectId) {
    window.location.replace('index.html');
    throw new Error('Not authenticated');
}

document.getElementById('welcomeMsg').textContent = `User: ${username}`;
document.getElementById('projectTitle').textContent = projectName;

const isOwner = username === projectOwner;

if (isOwner) {
    document.getElementById('addTaskBtn').style.display = 'block';
}

let editingTaskId = null;
let allTasks = [];

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

function getStatusBadge(status) {
    const map = {
        'TODO': '<span class="badge badge-todo">TODO</span>',
        'IN_PROGRESS': '<span class="badge badge-progress">IN PROGRESS</span>',
        'COMPLETED': '<span class="badge badge-completed">COMPLETED</span>'
    };
    return map[status] || status;
}

function getPriorityBadge(priority) {
    const map = {
        'HIGH': '<span class="badge badge-high">HIGH</span>',
        'MEDIUM': '<span class="badge badge-medium">MEDIUM</span>',
        'LOW': '<span class="badge badge-low">LOW</span>'
    };
    return map[priority] || priority;
}

async function loadUsers() {
    const res = await fetch(`${API}/api/users?size=100`, { headers: getHeaders() });
    const data = await res.json();
    const select = document.getElementById('taskAssignee');
    data.content.forEach(u => {
        const option = document.createElement('option');
        option.value = u.id;
        option.textContent = u.username;
        select.appendChild(option);
    });
}

async function loadTasks() {
    try {
        const response = await fetch(`${API}/api/projects/${projectId}/tasks?size=100`, {
            headers: getHeaders()
        });

        if (response.status === 401) {
            window.location.href = 'index.html';
            return;
        }

        const data = await response.json();
        allTasks = data.content || [];
        renderTasks(allTasks);

    } catch (error) {
        showAlert('Gabim në ngarkimin e tasks!', 'error');
    }
}

function renderTasks(tasks) {
    const list = document.getElementById('tasksList');

    if (tasks.length === 0) {
        list.innerHTML = '<p>Nuk ka tasks ende.</p>';
        return;
    }

    list.innerHTML = tasks.map(t => {
        const isAssignee = t.assigneeUsername === username;

        let buttons = '';
        if (isOwner) {
            buttons = `
        <div style="margin-top:15px; display:flex; gap:10px">
            <button class="btn btn-primary" onclick="editTask(${t.id}, true)">Edito</button>
            <button class="btn btn-danger" onclick="deleteTask(${t.id})">Fshi</button>
            <button class="btn" style="background:#9b59b6;color:white" onclick="showActivity(${t.id})">📋 Historia</button>
        </div>`;
        } else if (isAssignee) {
            buttons = `
                <div style="margin-top:15px; display:flex; gap:10px">
                    <button class="btn btn-primary" onclick="editTask(${t.id}, false)">Ndrysho Status</button>
                    <button class="btn" style="background:#9b59b6;color:white" onclick="showActivity(${t.id})">📋 Historia</button>
                </div>`;
        }

        return `
            <div class="card">
                <div style="display:flex; justify-content:space-between; align-items:start">
                    <h3>${t.title}</h3>
                    <div>${getStatusBadge(t.status)}</div>
                </div>
                <p style="color:#666; margin:8px 0">${t.description || 'Pa pershkrim'}</p>
                <p>${getPriorityBadge(t.priority)}</p>
                <p style="font-size:12px; color:#999; margin-top:8px">
                    Assignee: ${t.assigneeUsername || 'Pa assignee'}
                </p>
                <p style="font-size:12px; color:#999">
                    Due: ${t.dueDate || 'Pa datë'}
                </p>
                ${buttons}
            </div>
        `;
    }).join('');
}

function filterTasks() {
    const title = document.getElementById('searchTitle').value.toLowerCase();
    const status = document.getElementById('filterStatus').value;
    const priority = document.getElementById('filterPriority').value;

    const filtered = allTasks.filter(t => {
        const matchTitle = !title || t.title.toLowerCase().includes(title);
        const matchStatus = !status || t.status === status;
        const matchPriority = !priority || t.priority === priority;
        return matchTitle && matchStatus && matchPriority;
    });

    renderTasks(filtered);
}

function openModal() {
    editingTaskId = null;
    document.getElementById('modalTitle').textContent = 'Krijo Task të Ri';
    document.getElementById('taskTitle').value = '';
    document.getElementById('taskDesc').value = '';
    document.getElementById('taskStatus').value = 'TODO';
    document.getElementById('taskPriority').value = 'HIGH';
    document.getElementById('taskDueDate').value = '';
    document.getElementById('taskAssignee').value = '';

    document.getElementById('fullEditFields').style.display = 'block';
    document.getElementById('ownerOnlyFields').style.display = 'block';

    document.getElementById('taskModal').classList.add('active');
}

function closeModal() {
    document.getElementById('taskModal').classList.remove('active');
    editingTaskId = null;
}

function editTask(id, fullEdit) {
    const task = allTasks.find(t => t.id === id);
    if (!task) return;

    editingTaskId = id;

    if (fullEdit) {
        document.getElementById('modalTitle').textContent = 'Edito Task';
        document.getElementById('fullEditFields').style.display = 'block';
        document.getElementById('ownerOnlyFields').style.display = 'block';
        document.getElementById('taskTitle').value = task.title;
        document.getElementById('taskDesc').value = task.description || '';
        document.getElementById('taskPriority').value = task.priority;
        document.getElementById('taskDueDate').value = task.dueDate || '';
        document.getElementById('taskAssignee').value = task.assigneeId || '';
    } else {
        document.getElementById('modalTitle').textContent = 'Ndrysho Statusin';
        document.getElementById('fullEditFields').style.display = 'none';
        document.getElementById('ownerOnlyFields').style.display = 'none';
    }

    document.getElementById('taskStatus').value = task.status;
    document.getElementById('taskModal').classList.add('active');
}

async function saveTask() {
    const status = document.getElementById('taskStatus').value;
    const task = allTasks.find(t => t.id === editingTaskId);

    let body;

    if (!isOwner && editingTaskId) {
        body = {
            title: task.title,
            description: task.description,
            status: status,
            priority: task.priority,
            dueDate: task.dueDate,
            assigneeId: task.assigneeId
        };
    } else {
        const title = document.getElementById('taskTitle').value.trim();
        const description = document.getElementById('taskDesc').value.trim();
        const priority = document.getElementById('taskPriority').value;
        const dueDate = document.getElementById('taskDueDate').value;
        const assigneeId = document.getElementById('taskAssignee').value;

        if (!title || title.length < 3) {
            showAlert('Titulli duhet të ketë min 3 karaktere!', 'error');
            return;
        }

        body = {
            title,
            description,
            status,
            priority,
            dueDate: dueDate || null,
            assigneeId: assigneeId ? parseInt(assigneeId) : null
        };
    }

    try {
        let response;

        if (editingTaskId) {
            response = await fetch(`${API}/api/tasks/${editingTaskId}`, {
                method: 'PUT',
                headers: getHeaders(),
                body: JSON.stringify(body)
            });
        } else {
            response = await fetch(`${API}/api/projects/${projectId}/tasks`, {
                method: 'POST',
                headers: getHeaders(),
                body: JSON.stringify(body)
            });
        }

        if (response.ok) {
            showAlert(editingTaskId ? 'Task u përditësua!' : 'Task u krijua!', 'success');
            closeModal();
            loadTasks();
        } else {
            const error = await response.json();
            showAlert(error.title || error.error || 'Gabim!', 'error');
        }
    } catch (error) {
        showAlert('Gabim në lidhje me serverin!', 'error');
    }
}

async function deleteTask(id) {
    if (!confirm('A jeni i sigurt që doni të fshini këtë task?')) return;

    try {
        const response = await fetch(`${API}/api/tasks/${id}`, {
            method: 'DELETE',
            headers: getHeaders()
        });

        if (response.ok) {
            showAlert('Task u fshi!', 'success');
            loadTasks();
        } else {
            showAlert('Gabim gjatë fshirjes!', 'error');
        }
    } catch (error) {
        showAlert('Gabim në lidhje me serverin!', 'error');
    }
}
async function showActivity(taskId) {
    try {
        const response = await fetch(`${API}/api/tasks/${taskId}/activities`, {
            headers: getHeaders()
        });

        const activities = await response.json();
        const list = document.getElementById('activityList');

        if (activities.length === 0) {
            list.innerHTML = '<p style="color:#999; text-align:center">Nuk ka ndryshime ende.</p>';
        } else {
            list.innerHTML = activities.map(a => `
                <div style="border-left: 3px solid #9b59b6; padding: 10px 15px; margin-bottom: 10px; background:#f9f9f9; border-radius:0 8px 8px 0">
                    <div style="display:flex; justify-content:space-between">
                        <strong style="color:#9b59b6">${formatAction(a.action)}</strong>
                        <small style="color:#999">${new Date(a.performedAt).toLocaleString()}</small>
                    </div>
                    <p style="margin-top:5px; font-size:13px">
                        <span style="color:#e74c3c">${a.oldValue}</span> 
                        → 
                        <span style="color:#2ecc71">${a.newValue}</span>
                    </p>
                    <small style="color:#666">nga: <strong>${a.performedBy}</strong></small>
                </div>
            `).join('');
        }

        document.getElementById('activityModal').classList.add('active');

    } catch (error) {
        showAlert('Gabim në ngarkimin e historisë!', 'error');
    }
}

function formatAction(action) {
    const map = {
        'STATUS_CHANGED': '🔄 Statusi u ndryshua',
        'PRIORITY_CHANGED': '⚡ Prioriteti u ndryshua',
        'ASSIGNEE_CHANGED': '👤 Assignee u ndryshua'
    };
    return map[action] || action;
}

function closeActivityModal() {
    document.getElementById('activityModal').classList.remove('active');
}

loadUsers();
loadTasks();