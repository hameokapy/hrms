// File: /assets/js/management/positions/list.js

const posiModal = new bootstrap.Modal(document.getElementById('posiModal'));
const viewPosiModal = new bootstrap.Modal(document.getElementById('viewPosiModal'));
const posiForm = document.getElementById('posiForm');
let currentSearchParams = "";

document.addEventListener('DOMContentLoaded', () => {
    loadPositions();
    document.getElementById('searchForm').onsubmit = handleSearch;
    document.getElementById('btnReset').onclick = handleReset;
    posiForm.onsubmit = handleSubmit;
});

function handleSearch(e) { 
    e.preventDefault(); 
    loadPositions(new URLSearchParams(new FormData(e.target)).toString()); 
}

function handleReset() { 
    document.getElementById('searchForm').reset(); 
    loadPositions(); 
}

async function handleSubmit(e) {
    e.preventDefault();
    const id = document.getElementById('posiId').value;
    const body = Object.fromEntries(new FormData(e.target));
    
    try {
        const res = await fetch(`${CTX}/api/positions`, {
            method: id ? 'PUT' : 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });
        const result = await res.json();
        if (res.ok) {
            posiModal.hide();
            loadPositions();
            showToast(result.message, "success");
        } else { 
            handleApiError(result); 
        }
    } catch (err) { 
        showToast("Cannot connect to server.", "danger"); 
    }
}

async function loadPositions(queryString = '', page = 1) {
    const tbody = document.querySelector('#posiTable tbody');
    tbody.innerHTML = `<tr><td colspan="6" class="text-center py-4"><div class="spinner-border text-primary spinner-border-sm"></div> Loading...</td></tr>`;

    try {
        const res = await fetch(`${CTX}/api/positions?${queryString}&page=${page}`);
        const result = await res.json();
        if (res.ok) {
            const pageData = result.data;
            renderTable(pageData.content);
            renderPaginationMaster(pageData, 'pagination', 'loadPositions', queryString);
            updatePaginationInfo(pageData);
        } else {
            handleApiError(result);
            tbody.innerHTML = `<tr><td colspan="8" class="text-center py-4" style="color: #856404; font-weight: 500;">
                <i class="bi bi-exclamation-triangle"></i> Data cannot be loaded!
                </td></tr>`;
        }
    } catch (err) {
        console.log(err);
        tbody.innerHTML = `<tr><td colspan="6" class="text-center text-danger">Connecting server failed!</td></tr>`;
    }
}

function renderTable(list) {
    const tbody = document.querySelector('#posiTable tbody');
    if (!list || list.length === 0) {
        tbody.innerHTML = `<tr><td colspan="6" class="text-center py-4 text-muted">No matched data</td></tr>`;
        return;
    }

    const templateHtml = document.getElementById('actionTemplate').innerHTML;
    
    tbody.innerHTML = list.map(item => {
        const isActive = item.status === 'ACTIVE';
        const actions = templateHtml
            .replaceAll('ID_PLH', item.id)
            .replaceAll('STATUS_PLH', item.status)
            .replaceAll('STATUS_CLASS_PLH', isActive ? 'btn-outline-danger' : 'btn-outline-success')
            .replaceAll('STATUS_ICON_PLH', isActive ? 'bi-lock' : 'bi-unlock')
            .replaceAll('STATUS_TITLE_PLH', isActive ? 'Deactivate' : 'Reactivate');

        return `
            <tr>
                <td><small class="text-muted">#${item.id}</small></td>
                <td class="fw-bold">${item.name}</td>
                <td><span class="text-success fw-medium">${formatCurrency(item.baseSalaryLevel)}</span></td>
                <td class="text-center"><span class="badge rounded-pill bg-info">${item.employeeCount || 0} NV</span></td>
                <td><span class="badge ${isActive ? 'bg-success' : 'bg-danger'}">${item.status}</span></td>
                <td class="text-end px-4">${actions}</td>
            </tr>
        `;
    }).join('');
}

async function showDetail(id) {
    try {
        const res = await fetch(`${CTX}/api/positions/${id}`);
        const result = await res.json();
        
        if (res.ok) {
            const d = result.data;
            const statusBadge = d.status === 'ACTIVE' 
                ? '<span class="badge bg-success">Active</span>' 
                : '<span class="badge bg-danger">Inactive</span>';

            document.getElementById('posiDetailContent').innerHTML = `
                <div class="col-md-7 border-end">
                    <div class="mb-4">
                        <label class="text-muted small fw-bold text-uppercase">Position name</label>
                        <p class="fs-5 fw-bold text-primary mb-0">${d.name}</p>
                    </div>
                    <div class="row mb-4">
                        <div class="col-6">
                            <label class="text-muted small fw-bold text-uppercase d-block">Base salary</label>
                            <span class="text-success fw-bold fs-5">${formatCurrency(d.baseSalaryLevel)}</span>
                        </div>
                        <div class="col-6">
                            <label class="text-muted small fw-bold text-uppercase d-block">Status</label>
                            ${statusBadge}
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="text-muted small fw-bold text-uppercase mb-2 d-block">
                            <i class="bi bi-justify-left"></i> Description
                        </label>
                        <div class="bg-light rounded-end border-start border-4 border-primary py-3 px-4" 
                             style="border-top-right-radius: 8px; border-bottom-right-radius: 8px;">
                            <p class="mb-0 text-dark lh-base ${d.description ? '' : 'fst-italic text-muted'}" 
                               style="font-size: 0.95rem;">
                                ${d.description || 'No description available for this position.'}
                            </p>
                        </div>
                    </div>
                </div>

                <div class="col-md-5 ps-md-4">
                    <div class="card bg-light border-0 mb-4">
                        <div class="card-body text-center py-4">
                            <div class="display-6 fw-bold text-info">${d.employeeCount || 0}</div>
                            <div class="text-muted small fw-bold text-uppercase">Active employees</div>
                        </div>
                    </div>
                    
                    <div class="small mt-2">
                        <h6 class="fw-bold border-bottom pb-2 mb-3 mt-4"><i class="bi bi-clock-history"></i> Audit logs</h6>
                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-muted">Created by:</span>
                            <span class="fw-medium">${d.createdBy}</span>
                        </div>
                        <div class="d-flex justify-content-between mb-3 border-bottom pb-2">
                            <span class="text-muted">Created date:</span>
                            <span>${formatDateTime(d.createdDate)}</span>
                        </div>
                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-muted">Last modified by:</span>
                            <span class="fw-medium">${d.modifiedBy || 'N/A'}</span>
                        </div>
                        <div class="d-flex justify-content-between">
                            <span class="text-muted">Last modified date:</span>
                            <span>${d.modifiedDate ? formatDateTime(d.modifiedDate) : 'N/A'}</span>
                        </div>
                    </div>
                </div>
            `;
            viewPosiModal.show();
        } else {
            handleApiError(result);
        }
    } catch (err) { 
        console.error(err);
        showToast("Error loading position details.", "danger"); 
    }
}

async function openEditModal(id) {
    posiForm.reset();
    try {
        const res = await fetch(`${CTX}/api/positions/${id}`);
        const result = await res.json();
        if (res.ok) {
            const d = result.data;
            document.getElementById('posiId').value = d.id;
            posiForm.querySelector('[name="name"]').value = d.name;
            posiForm.querySelector('[name="baseSalaryLevel"]').value = d.baseSalaryLevel;
            posiForm.querySelector('[name="description"]').value = d.description || '';
            document.getElementById('modalTitle').innerHTML = "<i class='bi bi-pencil fs-6'></i> Edit position";
            posiModal.show();
        }
    } catch (err) { 
        showToast("Error fetching data.", "danger"); 
    }
}

function openAddModal() {
    posiForm.reset();
    document.getElementById('posiId').value = '';
    document.getElementById('modalTitle').innerHTML = '<i class="bi bi-plus-lg"></i> Add new position';
    posiModal.show();
}

async function toggleStatus(id, currentStatus) {
    const newStatus = currentStatus === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
    if (!confirm(`Switch status to ${newStatus}?`)) return;
    try {
        const res = await fetch(`${CTX}/api/positions/status`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ id, status: newStatus })
        });
        const result = await res.json();
        if (res.ok) { 
            loadPositions(); 
            showToast(result.message, "success"); 
        } else {
            handleApiError(result);
        }
    } catch (err) { 
        showToast("Cannot connect to server.", "danger");
    }
}

