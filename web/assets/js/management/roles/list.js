// File: /assets/js/management/roles/list.js

const editDescModal = new bootstrap.Modal(document.getElementById('editDescModal'));
const descForm = document.getElementById('descForm');

document.addEventListener('DOMContentLoaded', () => {
    loadRoles();
    loadPermissions();

    document.getElementById('roleSearchForm').onsubmit = (e) => {
        e.preventDefault();
        loadRoles(new URLSearchParams(new FormData(e.target)).toString());
    };
    document.getElementById('btnResetRole').onclick = () => {
        document.getElementById('roleSearchForm').reset();
        loadRoles();
    };

    document.getElementById('permSearchForm').onsubmit = (e) => {
        e.preventDefault();
        loadPermissions(new URLSearchParams(new FormData(e.target)).toString());
    };
    document.getElementById('btnResetPerm').onclick = () => {
        document.getElementById('permSearchForm').reset();
        loadPermissions();
    };

    descForm.onsubmit = handleUpdateDescription;
});

async function loadRoles(queryString = '') {
    const tbody = document.querySelector('#roleTable tbody');
    tbody.innerHTML = '<tr><td colspan="4" class="text-center py-3"><div class="spinner-border spinner-border-sm text-primary"></div> Loading...</td></tr>';
    
    try {
        const res = await fetch(`${CTX}/api/roles?${queryString}`);
        const result = await res.json();
        if (res.ok) {
            renderRoleTable(result.data);
        } else { 
            handleApiError(result); 
            tbody.innerHTML = `<tr><td colspan="8" class="text-center py-4" style="color: #856404; font-weight: 500;">
                <i class="bi bi-exclamation-triangle"></i> Data cannot be loaded!
                </td></tr>`;
        }
    } catch (err) { 
        tbody.innerHTML = '<tr><td colspan="4" class="text-center text-danger">Failed to load roles</td></tr>';
    }
}

async function loadPermissions(queryString = '') {
    const tbody = document.querySelector('#permTable tbody');
    tbody.innerHTML = '<tr><td colspan="4" class="text-center py-3"><div class="spinner-border spinner-border-sm text-primary"></div> Loading...</td></tr>';
    
    try {
        const res = await fetch(`${CTX}/api/permissions?${queryString}`);
        const result = await res.json();
        if (res.ok) {
            renderPermTable(result.data);
        } else { 
            handleApiError(result); 
            tbody.innerHTML = `<tr><td colspan="8" class="text-center py-4" style="color: #856404; font-weight: 500;">
                <i class="bi bi-exclamation-triangle"></i> Data cannot be loaded!
                </td></tr>`;
        }
    } catch (err) { 
        tbody.innerHTML = '<tr><td colspan="4" class="text-center text-danger">Failed to load permissions</td></tr>';
    }
}

function renderRoleTable(data) {
    const tbody = document.querySelector('#roleTable tbody');
    if (!data || data.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4" class="text-center text-muted py-3">No matched roles</td></tr>';
        return;
    }

    const template = document.getElementById('roleActionTemplate').innerHTML;

    tbody.innerHTML = data.map(item => {
        const actions = template
            .replaceAll('ID_PLH', item.id)
            .replaceAll('NAME_PLH', encodeURIComponent(item.roleName))
            .replaceAll('DESC_PLH', encodeURIComponent(item.description) || '');

        return `
            <tr>
                <td><span class="text-muted small">#${item.id}</span></td>
                <td><span class="badge bg-primary-subtle text-primary border border-primary-semibold px-2">${item.roleName}</span></td>
                <td class="text-secondary">${item.description || 'N/A'}</td>
                <td>${actions}</td>
            </tr>
        `;
    }).join('');
}

function renderPermTable(data) {
    const tbody = document.querySelector('#permTable tbody');
    if (!data || data.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4" class="text-center text-muted py-3">No matched permissions</td></tr>';
        return;
    }

    const template = document.getElementById('permActionTemplate').innerHTML;

    tbody.innerHTML = data.map(item => {
        const actions = template
            .replaceAll('ID_PLH', item.id)
            .replaceAll('NAME_PLH', encodeURIComponent(item.permissionKey))
            .replaceAll('DESC_PLH', encodeURIComponent(item.description) || '');

        return `
            <tr>
                <td><span class="text-muted small">#${item.id}</span></td>
                <td><code class="text-danger fw-bold">${item.permissionKey}</code></td>
                <td class="text-secondary">${item.description || 'N/A'}</td>
                <td>${actions}</td>
            </tr>
        `;
    }).join('');
}

function openEditDesc(id, name, desc, type) {
    document.getElementById('targetId').value = id;
    document.getElementById('targetType').value = type;
    document.getElementById('targetName').textContent = decodeURIComponent(name);
    document.getElementById('targetDesc').value = (desc == null || desc === 'undefined') ? '' : decodeURIComponent(desc) ;
    editDescModal.show();
}

async function handleUpdateDescription(e) {
    e.preventDefault();
    const type = document.getElementById('targetType').value;
    const body = {
        id: document.getElementById('targetId').value,
        description: document.getElementById('targetDesc').value
    };
    const apiUrl = type === 'role' ? `${CTX}/api/roles` : `${CTX}/api/permissions`;

    try {
        const res = await fetch(apiUrl, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });
        const result = await res.json();
        if (res.ok) {
            editDescModal.hide();
            showToast(result.message, "success");
            type === 'role' ? loadRoles() : loadPermissions();
        } else {
            handleApiError(result);
        }
    } catch (err) {
        showToast("Connection failed", "danger");
    }
}