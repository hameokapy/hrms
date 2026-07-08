// File: /assets/js/management/users/list.js

let currentSearchParams = "";
let pageSize = 3;

const Modals = {
    resetPass: new bootstrap.Modal(document.getElementById('resetPassModal')),
    assignRole: new bootstrap.Modal(document.getElementById('assignRoleModal')),
    createUser: new bootstrap.Modal(document.getElementById('createUserModal')),
    bindEmployee: new bootstrap.Modal(document.getElementById('bindEmployeeModal')),
    userDetail: new bootstrap.Modal(document.getElementById('userDetailModal'))
};

document.addEventListener('DOMContentLoaded', () => {
    loadUsers();
    loadFilterOptions();

    const formHandlers = {
        'searchForm': handleSearch,
        'createUserForm': (e) => handleBaseSubmit(e, `${CTX}/api/users`, 'createUser', 'POST'),
        'resetPassForm': (e) => handleBaseSubmit(e, `${CTX}/api/users/password`, 'resetPass', 'PUT'),
        'assignRoleForm': handleAssignRoleSubmit,
        'bindEmployeeForm': handleBindEmployeeSubmit
    };

    Object.entries(formHandlers).forEach(([id, handler]) => {
        const form = document.getElementById(id);
        if (form) form.onsubmit = handler;
    });

    document.getElementById('btnReset').onclick = handleReset;
    
    document.getElementById('togglePassword').addEventListener('click', function () {
        const passwordInput = document.getElementById('newPassInput');
        const icon = document.getElementById('toggleIcon');

        const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
        passwordInput.setAttribute('type', type);

        icon.classList.toggle('bi-eye');
        icon.classList.toggle('bi-eye-slash');
    });
    
    initEmployeeSearch('ceSearchInput', 'ceEmployeeId', 'ceResults');
    initEmployeeSearch('beSearchInput', 'beEmployeeId', 'beResults');
});

async function loadUsers(queryString = '', page = 1) {
    const tbody = document.querySelector('#userTable tbody');
    tbody.innerHTML = `<tr><td colspan="6" class="text-center py-4"><div class="spinner-border text-primary spinner-border-sm"></div> Loading...</td></tr>`;

    try {
        const res = await fetch(`${CTX}/api/users?${queryString}&page=${page}`);
        const result = await res.json();
        if (res.ok) {
            const pageData = result.data;
            renderTable(pageData.content);
            renderPaginationMaster(pageData, 'pagination', 'loadUsers', queryString);
            updatePaginationInfo(pageData);
        } else {
            handleApiError(result);
            tbody.innerHTML = `<tr><td colspan="6" class="text-center py-4 text-warning fw-medium"><i class="bi bi-exclamation-triangle"></i> Data cannot be loaded!</td></tr>`;
        }
    } catch (err) {
        tbody.innerHTML = `<tr><td colspan="6" class="text-center text-danger py-4">Connecting server failed!</td></tr>`;
    }
}

async function loadFilterOptions() {
    try {
        const [roles, depts, posis] = await Promise.all([
            fetch(`${CTX}/api/roles`).then(r => r.json()),
            fetch(`${CTX}/api/departments`).then(r => r.json()),
            fetch(`${CTX}/api/positions`).then(r => r.json())
        ]);
        roles.data?.forEach(r => document.getElementById('filterRole').add(new Option(r.roleName, r.id)));
        depts.data?.content?.forEach(d => document.getElementById('filterDept').add(new Option(d.name, d.id)));
        posis.data?.content?.forEach(p => document.getElementById('filterPosi').add(new Option(p.name, p.id)));
    } catch (e) { 
        console.warn("Filter load failed"); 
    }
}

function renderTable(list) {
    const tbody = document.querySelector('#userTable tbody');
    if (!list || list.length === 0) {
        tbody.innerHTML = `<tr><td colspan="6" class="text-center py-4 text-muted">No matched data</td></tr>`;
        return;
    }

    const templateHtml = document.getElementById('actionTemplate').innerHTML;
    
    tbody.innerHTML = list.map(item => {
        const empInfo = item.employeeDisplayName 
            ? `<div><strong class="text-dark">${item.employeeDisplayName}</strong><div class="small text-muted italic">${item.departmentAndPosition}</div></div>`
            : `<span class="text-warning-emphasis small italic"><i class="bi bi-exclamation-triangle"></i> Not linked</span>`;

        const roleBadges = item.roleNames?.length > 0
            ? item.roleNames.map(r => `<span class="badge bg-primary-subtle text-primary border border-primary-semibold me-1">${r}</span>`).join('')
            : `<span class="badge border border-secondary border-dashed text-secondary opacity-75" style="border-style: dashed !important;"><i class="bi bi-shield-slash me-1"></i> No roles</span>`;

        const actions = templateHtml
            .replaceAll('ID_PLH', item.id)
            .replaceAll('NAME_PLH', encodeURIComponent(item.username))
            .replaceAll('IS_ACTIVE_PLH', item.isActive)
            .replaceAll('STATUS_CLASS_PLH', item.isActive ? 'btn-outline-danger' : 'btn-outline-success')
            .replaceAll('STATUS_TITLE_PLH', item.isActive ? 'Deactivate' : 'Reactivate')
            .replaceAll('STATUS_ICON_PLH', item.isActive ? 'bi-lock' : 'bi-unlock');

        return `
            <tr>
                <td><small class="text-muted">#${item.id}</small></td>
                <td>
                    <span class="fw-bold text-primary">${item.username}</span>
                    <div class="small text-muted" style="font-size: 0.7rem;">Last login: ${item.lastLogin ? formatDateTime(item.lastLogin) : 'Never'}</div>
                </td>
                <td>${empInfo}</td>
                <td>${roleBadges}</td>
                <td class="text-center">${item.isActive ? '<span class="badge bg-success">ACTIVE</span>' : '<span class="badge bg-danger">INACTIVE</span>'}</td>
                <td class="text-end px-4">${actions}</td>
            </tr>`;
    }).join('');
}

function handleSearch(e) { 
    e.preventDefault(); 
    loadUsers(new URLSearchParams(new FormData(e.target)).toString()); 
}

function handleReset() { 
    document.getElementById('searchForm').reset(); 
    loadUsers(); 
}

async function handleBaseSubmit(e, url, modalKey, method) {
    e.preventDefault();
    const body = Object.fromEntries(new FormData(e.target).entries());
    try {
        const res = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });
        const result = await res.json();
        if (res.ok) {
            Modals[modalKey].hide();
            showToast(result.message, "success");
            loadUsers();
        } else { 
            handleApiError(result); 
        }
    } catch (err) { 
        showToast("Cannot connect to server.", "danger");
    }
}

async function handleAssignRoleSubmit(e) {
    e.preventDefault();
    const userId = document.getElementById('arUserId').value;
    const roleIds = Array.from(e.target.querySelectorAll('input[name="roleIds"]:checked')).map(cb => parseInt(cb.value));
    
    try {
        const res = await fetch(`${CTX}/api/users/roles`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ userId, roleIds })
        });
        const result = await res.json();
        if (res.ok) { 
            Modals.assignRole.hide(); 
            showToast(result.message, "success"); 
            loadUsers(); 
        } else { 
            handleApiError(result); 
        }
    } catch (e) { 
        showToast("Cannot connect to server.", "danger");
    }
}

async function handleBindEmployeeSubmit(e) {
    e.preventDefault();
    const body = Object.fromEntries(new FormData(e.target).entries());
    try {
        const res = await fetch(`${CTX}/api/users/employee`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });
        const result = await res.json();
        if (res.ok) { 
            Modals.bindEmployee.hide(); 
            showToast(result.message, "success"); 
            loadUsers(); 
        } else { 
            handleApiError(result); 
        }
    } catch (e) { 
        showToast("Cannot connect to server.", "danger");
    }
}

function openCreateUserModal() {
    document.getElementById('createUserForm').reset();
    Modals.createUser.show();
}

function openResetPass(id, name) {
    fillModalHeader('rp', id, name);
    document.getElementById('newPassInput').value = '';
    Modals.resetPass.show();
}

function openBindEmployee(id, name) {
    fillModalHeader('be', id, name);
    
    const form = document.getElementById('bindEmployeeForm');
    form.querySelector('input[name="employeeId"]').value = "";
    
    const searchInput = document.getElementById('beSearchInput');
    if (searchInput) searchInput.value = "";
    const resultsDiv = document.getElementById('beResults');
    if (resultsDiv) {
        resultsDiv.innerHTML = "";
        resultsDiv.classList.add('d-none');
    }
    
    Modals.bindEmployee.show();
}

async function openAssignRole(id, name) {
    fillModalHeader('ar', id, name);
    const box = document.getElementById('roleCheckboxList');
    box.innerHTML = '<div class="text-center"><div class="spinner-border spinner-border-sm text-primary"></div></div>';
    try {
        const [roles, user] = await Promise.all([
            fetch(`${CTX}/api/roles`).then(r => r.json()),
            fetch(`${CTX}/api/users/${id}`).then(r => r.json())
        ]);
        box.innerHTML = roles.data.map(r => `
            <div class="form-check mb-2">
                <input class="form-check-input" type="checkbox" name="roleIds" value="${r.id}" id="rc_${r.id}" ${(user.data.roleNames || []).includes(r.roleName) ? 'checked' : ''}>
                <label class="form-check-label d-block" for="rc_${r.id}"><span class="fw-bold">${r.roleName}</span><div class="small text-muted">${r.description || ''}</div></label>
            </div>`).join('');
        Modals.assignRole.show();
    } catch (e) { 
        box.innerHTML = 'Error loading roles'; 
    }
}

function fillModalHeader(prefix, id, name) {
    document.getElementById(`${prefix}UserId`).value = id;
    document.getElementById(`${prefix}Username`).textContent = decodeURIComponent(name);
}

async function toggleStatus(id, current) {
    if (!confirm(`Are you sure to ${current ? 'DEACTIVATE' : 'REACTIVATE'} this user account?`)) return;
    try {
        const res = await fetch(`${CTX}/api/users/status`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ userId: id, isActive: !current })
        });
        const result = await res.json();
        if (res.ok) { 
            loadUsers(); 
            showToast(result.message, "success");
        } else { 
            handleApiError(result); 
        }
    } catch (e) { 
        showToast("Cannot connect to server", "danger");
    }
}

async function openUserDetail(id) {
    const content = document.getElementById('userDetailContent');
    content.innerHTML = `<tr><td colspan="2" class="text-center py-4"><div class="spinner-border text-info spinner-border-sm"></div> Loading...</td></tr>`;

    try {
        const res = await fetch(`${CTX}/api/users/${id}`);
        const result = await res.json();
        if (res.ok) {
            const d = result.data;
            const created = d.createdDate ? formatDateTime(d.createdDate) : 'N/A';
            const modified = d.modifiedDate ? formatDateTime(d.modifiedDate) : 'N/A';

            content.innerHTML = `
                <div class="col-md-6 mb-2">
                    <div class="p-2 border rounded bg-light">
                        <i class="bi bi-hash text-muted"></i> <small class="text-muted">User ID:</small> 
                        <span class="fw-medium">#${d.id}</span>
                    </div>
                </div>
                <div class="col-md-6 mb-2">
                    <div class="p-2 border rounded bg-light">
                        <i class="bi bi-shield-check text-muted"></i> <small class="text-muted">Status:</small> 
                        <span class="badge ${d.isActive ? 'bg-success' : 'bg-danger'} ms-1">
                            ${d.isActive ? 'ACTIVE' : 'INACTIVE'}
                        </span>
                    </div>
                </div>
                <div class="col-md-6 mb-2">
                    <i class="bi bi-person-badge text-primary"></i> <strong class="ms-1 small text-uppercase text-muted">Username:</strong> 
                    <div class="ps-4 text-primary fw-bold">${d.username}</div>
                </div>
                <div class="col-md-6 mb-2">
                    <i class="bi bi-person-vcard text-muted"></i> <strong class="ms-1 small text-uppercase text-muted">Linked employee:</strong> 
                    <div class="ps-4 mt-1 fw-medium">${d.employeeDisplayName || 'N/A'}</div>
                </div>

                <div class="col-md-6 mb-2">
                    <i class="bi bi-building text-muted"></i> <strong class="ms-1 small text-uppercase text-muted">Department & Position:</strong>
                    <div class="ps-4 mt-1 fw-medium">${d.departmentAndPosition || 'N/A'}</div>
                </div>
                <div class="col-md-6 mb-2">
                    <i class="bi bi-tags text-muted"></i> <strong class="ms-1 small text-uppercase text-muted">Assigned roles:</strong>
                    <div class="ps-4 mt-1">
                        ${(d.roleNames || []).map(r => 
                            `<span class="badge rounded-pill bg-primary-subtle text-primary me-1">${r}</span>`
                        ).join('') || '<span class="text-muted small">None</span>'}
                    </div>
                </div>

                <div class="col-md-6 small text-muted">
                    <div class="d-flex justify-content-between border-bottom py-1">
                        <span><i class="bi bi-clock-history"></i> Created date:</span>
                        <span class="text-dark">${created}</span>
                    </div>
                    <div class="d-flex justify-content-between py-1">
                        <span><i class="bi bi-person-plus"></i> Created by:</span>
                        <span class="text-dark">${d.createdBy || 'System'}</span>
                    </div>
                </div>
                <div class="col-md-6 small text-muted border-start ps-md-4">
                    <div class="d-flex justify-content-between border-bottom py-1">
                        <span><i class="bi bi-pencil-square"></i> Last modified date:</span>
                        <span class="text-dark">${modified}</span>
                    </div>
                    <div class="d-flex justify-content-between py-1">
                        <span><i class="bi bi-person-check"></i> Last modified by:</span>
                        <span class="text-dark">${d.modifiedBy || 'N/A'}</span>
                    </div>
                </div>
            `;
            
            Modals.userDetail.show();
        } else {
            handleApiError(result);
        }
    } catch (e) {
        console.log(e);
        showToast("Cannot connect to server.", "danger");
    }
}

/* === LQ TÍNH NĂNG AUTOCOMPLETE SEARCH EMPLOYEE CỦA CHỨC NĂNG CREATE USER / BIND EMPLOYEE === */
function initEmployeeSearch(inputId, hiddenId, resultsId) {
    const input = document.getElementById(inputId);
    const hidden = document.getElementById(hiddenId);
    const results = document.getElementById(resultsId);
    let timeout = null;

    input.addEventListener('input', function() {
        const keyword = this.value.trim();
        clearTimeout(timeout);
        
        if (keyword.length < 2) {
            results.classList.add('d-none');
            hidden.value = "";
            return;
        }

        timeout = setTimeout(async () => {
            try {
                const res = await fetch(`${CTX}/api/employees?employee=${encodeURIComponent(keyword)}&status=ACTIVE&pageSize=${pageSize}`);
                const result = await res.json();
                
                if (res.ok && result.data.content.length > 0) {
                    renderSearchDropdown(result.data.content, results, input, hidden);
                } else {
                    results.innerHTML = '<div class="list-group-item small text-muted">No employee found</div>';
                    results.classList.remove('d-none');
                }
            } catch (err) {
                console.error("Search failed", err);
            }
        }, 300);
    });

    document.addEventListener('click', (e) => {
        if (!input.contains(e.target) && !results.contains(e.target)) {
            results.classList.add('d-none');
        }
    });
}

function renderSearchDropdown(list, container, inputEl, hiddenEl) {
    container.innerHTML = list.map(emp => `
        <button type="button" class="list-group-item list-group-item-action d-flex justify-content-between align-items-center" 
                onclick="selectEmployee('${emp.id}', '${emp.fullName} - ${emp.employeeCode}', '${inputEl.id}', '${hiddenEl.id}', '${container.id}')">
            <div>
                <div class="fw-bold small">${emp.fullName}</div>
                <div class="text-muted" style="font-size: 0.7rem;">${emp.employeeCode} - ${emp.positionName || 'Staff'}</div>
            </div>
            <i class="bi bi-plus-circle text-primary"></i>
        </button>
    `).join('');
    container.classList.remove('d-none');
}

function selectEmployee(id, displayName, inputId, hiddenId, resultsId) {
    document.getElementById(inputId).value = displayName;
    document.getElementById(hiddenId).value = id;
    document.getElementById(resultsId).classList.add('d-none');
}