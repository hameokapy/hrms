// File: /assets/js/management/departments/list.js

const viewDeptModal = new bootstrap.Modal(document.getElementById('viewDeptModal'));
const deptEmployeesModal = new bootstrap.Modal(document.getElementById('deptEmployeesModal'));
const deptModal = new bootstrap.Modal(document.getElementById('deptModal'));
const deptForm = document.getElementById('deptForm');
let currentSearchParams = "";
let currentViewingDeptId = null;
let pageSize = 4;

/* ========== CÁC HÀM KHỞI CHẠY LÚC MỚI VÀO TRANG ========== */

document.addEventListener('DOMContentLoaded', () => {
    loadDepartments();

    document.getElementById('searchForm').onsubmit = handleSearch;
    document.getElementById('btnReset').onclick = handleReset;
    deptForm.onsubmit = handleSubmit; 
    
    initManagerSearch();
});

async function handleSubmit(e) {
    e.preventDefault();
    
    const id = document.getElementById('deptId').value;
    const title = document.getElementById('modalTitle').innerText;
    const formData = new FormData(e.target);
    const body = Object.fromEntries(formData.entries());

    let url = `${CTX}/api/departments`;
    const method = id ? 'PUT' : 'POST';

    if (id) {
        body.id = parseInt(id);
        if (title.includes("Manager")) url += "/manager";
        
        if (title.includes("Edit Info")) {
            delete body.code;
            delete body.managerId;
        } else if (title.includes("Manager")) {
            delete body.code;
            delete body.name;
            delete body.location;
        }
    }

    try {
        const res = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });
        const result = await res.json();

        if (res.ok) {
            deptModal.hide();
            loadDepartments();
            showToast(result.message, "success");
        } else {
            handleApiError(result);
        }
    } catch (err) {
        console.error("Submit error");
        showToast("Cannot connect to server.", "danger");
    }
}

function handleSearch(e) {
    e.preventDefault();
    const params = new URLSearchParams(new FormData(e.target)).toString();
    loadDepartments(params);
}

function handleReset() {
    document.getElementById('searchForm').reset();
    loadDepartments();
}

async function loadDepartments(queryString = '', page = 1) {
    const tbody = document.querySelector('#deptTable tbody');
    tbody.innerHTML = `<tr><td colspan="8" class="text-center py-4"><div class="spinner-border text-primary spinner-border-sm"></div> Loading...</td></tr>`;

    currentSearchParams = queryString;

    try {
        const res = await fetch(`${CTX}/api/departments?${queryString}&page=${page}`);
        const result = await res.json();
        if (res.ok) {
            const pageData = result.data;
            renderTable(pageData.content);
            renderPaginationMaster(pageData, 'pagination', 'loadDepartments', queryString);
            updatePaginationInfo(pageData);
        } else {
            handleApiError(result);
            tbody.innerHTML = `<tr><td colspan="8" class="text-center py-4" style="color: #856404; font-weight: 500;">
                <i class="bi bi-exclamation-triangle"></i> Data cannot be loaded!
                </td></tr>`;
        }
    } catch (err) {
        tbody.innerHTML = `<tr><td colspan="8" class="text-center text-danger">Connecting server failed!</td></tr>`;
    }
}

function renderTable(list) {
    const tbody = document.querySelector('#deptTable tbody');
    if (!list || list.length === 0) {
        tbody.innerHTML = `<tr><td colspan="8" class="text-center py-4 text-muted">No matched data</td></tr>`;
        return;
    }

    const templateHtml = document.getElementById('actionTemplate').innerHTML;
    
    tbody.innerHTML = list.map(item => {
        const isAction = item.status === 'ACTIVE';
        const statusClass = isAction ? 'btn-outline-danger' : 'btn-outline-success';
        const statusIcon = isAction ? 'bi-lock' : 'bi-unlock';
        const statusTitle = isAction ? 'Deactivate department' : 'Reactivate Department';

        let actions = templateHtml
            .replaceAll('ID_PLH', item.id)
            .replaceAll('NAME_PLH', encodeURIComponent(item.name))
            .replaceAll('LOC_PLH', encodeURIComponent(item.location) || '')
            .replaceAll('STATUS_PLH', item.status)
            .replaceAll('STATUS_CLASS_PLH', statusClass)
            .replaceAll('STATUS_ICON_PLH', statusIcon)
            .replaceAll('STATUS_TITLE_PLH', statusTitle);

        return `
            <tr>
                <td><small class="text-muted">#${item.id}</small></td>
                <td><span class="badge bg-light text-dark border">${item.code}</span></td>
                <td class="fw-bold">${item.name}</td>
                <td><small>${item.location || '-'}</small></td>
                <td><i class="bi bi-person-badge me-1"></i>${item.managerName || '-'}</td>
                <td class="text-center"><span class="badge rounded-pill bg-info">${item.totalEmployees || 0} NV</span></td>
                <td class="text-end px-4">
                    ${actions}
                </td>
            </tr>
        `;
    }).join('');
}

/* ========== CÁC HÀM KHỞI CHẠY CHỨC NĂNG RIÊNG KHI MUỐN ========== */

async function showDetail(id) {
    try {
        const res = await fetch(`${CTX}/api/departments/${id}`);
        const result = await res.json();
        if (res.ok) {
            const d = result.data; 
            document.getElementById('deptDetailContent').innerHTML = `
                <div class="col-md-6 border-bottom pb-2 mb-2"><strong>Code:</strong> <span class="text-primary">${d.code}</span></div>
                <div class="col-md-6 border-bottom pb-2 mb-2"><strong>Name:</strong> ${d.name}</div>
                <div class="col-md-6 border-bottom pb-2 mb-2"><strong>Location:</strong> ${d.location || 'N/A'}</div>
                <div class="col-md-6 border-bottom pb-2 mb-2"><strong>Manager name:</strong> ${d.managerName || 'N/A'} (ID: ${d.managerId || 'N/A'})</div>
                <div class="col-md-6 border-bottom pb-2 mb-2"><strong>Total employees:</strong> <span class="badge bg-info text-dark">${d.totalEmployees} NV</span></div>
                <div class="col-md-6 border-bottom pb-2 mb-2"><strong>Status:</strong> 
                    <span class="badge ${d.status === 'ACTIVE' ? 'bg-success' : 'bg-danger'}">${d.status}</span>
                </div>

                <div class="col-md-6 mt-3 small text-muted">
                    <i class="bi bi-plus-circle"></i> <strong>Created date:</strong> ${formatDateTime(d.createdDate)}
                </div>
                <div class="col-md-6 mt-3 small text-muted">
                    <i class="bi bi-person"></i> <strong>Created by:</strong> ${d.createdBy || 'System'}
                </div>
                <div class="col-md-6 mt-1 small text-muted border-top pt-1">
                    <i class="bi bi-pencil-square"></i> <strong>Last modified date:</strong> ${formatDateTime(d.modifiedDate)}
                </div>
                <div class="col-md-6 mt-1 small text-muted border-top pt-1">
                    <i class="bi bi-person-check"></i> <strong>Last modified by:</strong> ${d.modifiedBy || 'N/A'}
                </div>
            `;
            viewDeptModal.show();
        } else {
            handleApiError(result);
        }
    } catch (err) {
        console.error("Load department detail error");
        showToast("Cannot connect to server", "danger");
    }
}

async function showEmployees(deptId, page = 1) {
    currentViewingDeptId = deptId;
    
    try {
        const res = await fetch(`${CTX}/api/departments/${deptId}/employees?page=${page}`);
        const result = await res.json();

        if (res.ok) {
            const pageData = result.data;
            const list = pageData.content;
            const tbody = document.querySelector('#empInDeptTable tbody');
            
            if (!list || list.length === 0) {
                tbody.innerHTML = `<tr><td colspan="5" class="text-center py-4 text-muted">No employees found in this department.</td></tr>`;
            } else {
                tbody.innerHTML = list.map(emp => `
                    <tr>
                        <td><small class="text-muted">#${emp.id}</small></td>
                        <td><span class="badge bg-light text-dark border">${emp.employeeCode}</span></td>
                        <td class="fw-bold">${emp.fullName}</td>
                        <td><small class="text-muted">${emp.positionName || 'N/A'}</small></td>
                        <td><small>${emp.departmentNameAndCode}</small></td>
                    </tr>
                `).join('');
            }
            
            renderPaginationMaster(pageData, 'empPagination', 'showEmployees', deptId);
            document.getElementById('empTotalElements').innerText = pageData.totalElements;
            
            deptEmployeesModal.show();
            
        } else {
            handleApiError(result);
        }
    } catch (err) {
        console.error("Load employees error");
        showToast("Cannot connect to server", "danger");
    }
}

function resetModalLayout() {
    deptForm.reset();
    ['code', 'name', 'location', 'manager'].forEach(key => {
        const el = document.getElementById(`group-${key}`);
        if (el) el.style.display = 'block';
    });
}

function openAddModal() {
    resetModalLayout();
    clearSelectedManager();
    document.getElementById('deptId').value = '';
    document.getElementById('modalTitle').innerHTML = '<i class="bi bi-plus-lg"></i> Add New Department';
    deptModal.show();
}

function openEditModal(id, name, location) {
    resetModalLayout();
    document.getElementById('deptId').value = id;
    document.getElementById('modalTitle').innerHTML = '<i class="bi bi-pencil fs-6"></i> Edit Department General';
    
    document.getElementById('group-code').style.display = 'none';
    document.getElementById('group-manager').style.display = 'none';
    
    const decodedName = decodeURIComponent(name);
    const decodedLocation = location ? decodeURIComponent(location) : '';
    
    deptForm.querySelector('input[name="name"]').value = decodedName;
    deptForm.querySelector('input[name="location"]').value = (decodedLocation == null || decodedLocation === 'undefined') ? '' : decodedLocation;
    
    deptModal.show();
}

async function openAssignManagerModal(id) {
    resetModalLayout();
    clearSelectedManager();
    
    document.getElementById('deptId').value = id;
    document.getElementById('modalTitle').innerHTML = '<i class="bi bi-person-badge"></i> Assign Manager';

    document.getElementById('group-code').style.display = 'none';
    document.getElementById('group-name').style.display = 'none';
    document.getElementById('group-location').style.display = 'none';
    
    try {
        const res = await fetch(`${CTX}/api/departments/${id}`);
        const result = await res.json();

        if (res.ok) {
            const d = result.data; 
            
            if (d.managerId) {
                selectManager(d.managerId, d.managerName, "Current");
            }
            
            deptModal.show();
        } else {
            handleApiError(result);
        }
    } catch (err) {
        console.error("Load manager error");
        showToast("Cannot connect to server", "danger");
    }
}

async function toggleStatus(id, currentStatus) {
    const newStatus = currentStatus === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
    if (!confirm(`Are you sure to ${newStatus === 'ACTIVE' ? 'REACTIVATE' : 'DEACTIVATE'} this department?`)) return;

    try {
        const res = await fetch(`${CTX}/api/departments/status`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ id: id, status: newStatus })
        });
        const result = await res.json();
        if (res.ok) {
            loadDepartments();
            showToast(result.message, "success");
        } else {
            handleApiError(result);
        }
    } catch (err) {
        console.error("Toggle status error");
        showToast("Cannot connect to server", "danger");
    }
}

/* === LQ TÍNH NĂNG AUTO COMPLETION KHI NHẬP MANAGER ID CỦA TÍNH NĂNG CREATE DEPT / ASSIGN MANAGER === */
let searchTimeout = null;

function initManagerSearch() {
    const input = document.getElementById('managerSearchInput');
    const resultsContainer = document.getElementById('managerSearchResults');

    input.addEventListener('input', function() {
        const query = this.value.trim();
        clearTimeout(searchTimeout);

        if (query.length < 2) {
            resultsContainer.classList.add('d-none');
            return;
        }

        searchTimeout = setTimeout(async () => {
            try {
                const res = await fetch(`${CTX}/api/employees?employee=${query}&status=ACTIVE&pageSize=${pageSize}`);
                const result = await res.json();
                
                if (res.ok && result.data.content.length > 0) {
                    renderSearchItems(result.data.content);
                } else {
                    resultsContainer.innerHTML = '<div class="list-group-item small text-muted">No active employee found</div>';
                    resultsContainer.classList.remove('d-none');
                }
            } catch (err) {
                console.error("Search manager error", err);
            }
        }, 300); 
    });

    document.addEventListener('click', (e) => {
        if (!input.contains(e.target) && !resultsContainer.contains(e.target)) {
            resultsContainer.classList.add('d-none');
        }
    });
}

function renderSearchItems(employees) {
    const container = document.getElementById('managerSearchResults');
    container.innerHTML = employees.map(emp => `
        <a href="javascript:void(0)" class="list-group-item list-group-item-action d-flex justify-content-between align-items-center" 
           onclick="selectManager(${emp.id}, '${emp.fullName}', '${emp.employeeCode}')">
            <div>
                <div class="fw-bold small">${emp.fullName}</div>
                <div class="text-muted" style="font-size: 0.7rem;">${emp.employeeCode} - ${emp.positionName || 'Staff'}</div>
            </div>
            <i class="bi bi-plus-circle text-primary"></i>
        </a>
    `).join('');
    container.classList.remove('d-none');
}

function selectManager(id, name, code) {
    document.getElementById('managerIdHidden').value = id;
    document.getElementById('managerSearchInput').value = '';
    document.getElementById('managerSearchResults').classList.add('d-none');
    
    const infoBox = document.getElementById('selectedManagerInfo');
    infoBox.classList.remove('d-none');
    document.getElementById('displayManagerName').innerText = `${name} (${code})`;
}

function clearSelectedManager() {
    document.getElementById('managerIdHidden').value = '';
    document.getElementById('selectedManagerInfo').classList.add('d-none');
    document.getElementById('displayManagerName').innerText = '';
}
