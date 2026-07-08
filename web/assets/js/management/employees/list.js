// File: /assets/js/management/employees/list.js

const Modals = {
    emp: new bootstrap.Modal(document.getElementById('empModal')),
    detail: new bootstrap.Modal(document.getElementById('detailModal'))
};
const empForm = document.getElementById('empForm');
let selectedEmpIds = [];
let currentSearchParams = "";

document.addEventListener('DOMContentLoaded', () => {
    loadEmployees();
    loadActiveOptions(); 

    empForm.onsubmit = handleSubmit;
    document.getElementById('searchForm').onsubmit = handleSearch;
    document.getElementById('btnReset').onclick = handleReset;
    
    document.getElementById('selectAll').onchange = function() {
        const checkboxes = document.querySelectorAll('.emp-checkbox');
        selectedEmpIds = [];
        checkboxes.forEach(cb => {
            cb.checked = this.checked;
            if (this.checked) selectedEmpIds.push(parseInt(cb.value));
        });
        toggleBulkBar();
    };
});

async function loadEmployees(queryString = '', page = 1) {
    const tbody = document.querySelector('#empTable tbody');
    tbody.innerHTML = `<tr><td colspan="8" class="text-center py-4"><div class="spinner-border text-primary spinner-border-sm"></div> Loading...</td></tr>`;

    currentSearchParams = queryString;

    try {
        const res = await fetch(`${CTX}/api/employees?${queryString}&page=${page}`);
        const result = await res.json();
        if (res.ok) {
            const pageData = result.data;
            renderTable(pageData.content);
            renderPaginationMaster(pageData, 'pagination', 'loadEmployees', queryString);
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
    const tbody = document.querySelector('#empTable tbody');
    if (!list || list.length === 0) {
        tbody.innerHTML = `<tr><td colspan="6" class="text-center py-4 text-muted">No employees found.</td></tr>`;
        return;
    }

    const templateHtml = document.getElementById('actionTemplate').innerHTML;
    
    tbody.innerHTML = list.map(item => {
        let actions = templateHtml
            .replaceAll('ID_PLH', item.id)
            .replaceAll('NAME_PLH', encodeURIComponent(item.fullName))
            .replaceAll('STATUS_PLH', item.status);

        return `
            <tr>
                <td><input type="checkbox" class="form-check-input emp-checkbox" value="${item.id}" onchange="handleSingleCheck(this)"></td>
                <td><small class="text-muted">#${item.id}</small></td>
                <td>
                    <span class="badge bg-primary-subtle text-primary fw-bold">${item.employeeCode}</span>
                </td>
                <td class="fw-bold text-dark">${item.fullName}</td>
                <td><div class="small fw-medium">${item.departmentNameAndCode || '-'}</div></td>
                <td><div class="small text-muted text-uppercase" style="font-size: 0.75rem;">${item.positionName || '-'}</div></td>
                <td class="text-end px-4">${actions}</td>
            </tr>
        `;
    }).join('');
}

async function loadActiveOptions() {
    try {
        const [depts, posis] = await Promise.all([
            fetch(`${CTX}/api/departments?status=ACTIVE`).then(r => r.json()),
            fetch(`${CTX}/api/positions?status=ACTIVE`).then(r => r.json())
        ]);
        
        const selDept = document.getElementById('selDepartment');
        const selPosi = document.getElementById('selPosition');

        if(selDept) {
            selDept.innerHTML = '<option value="">Select Department</option>';
            depts.data?.content?.forEach(d => selDept.add(new Option(`${d.name} (${d.code})`, d.id)));
        }
        if(selPosi) {
            selPosi.innerHTML = '<option value="">Select Position</option>';
            posis.data?.content?.forEach(p => selPosi.add(new Option(p.name, p.id)));
        }
    } catch (e) { 
        console.warn("Failed to load active options"); 
    }
}

function handleSearch(e) {
    e.preventDefault();
    const formData = new FormData(e.target); 
    const params = new URLSearchParams();

    for (const [key, value] of formData.entries()) {
        if (value) { params.append(key, value); }
    }

    loadEmployees(params.toString());
}

function handleReset() {
    document.getElementById('searchForm').reset();
    loadEmployees();
}

async function handleSubmit(e) {
    e.preventDefault();
    const empIdVal = document.getElementById('empId').value;
    const formData = new FormData(e.target);
    const rawData = Object.fromEntries(formData.entries());
    const action = e.submitter ? e.submitter.getAttribute('data-action') : null;

    if (action === 'department' && !rawData.departmentId) {
        showToast("Must select valid department.", "danger");
        return;
    }
    if (action === 'position' && !rawData.positionId) {
        showToast("Must select valid position.", "danger");
        return;
    }
    if (action === 'both' && (!rawData.departmentId || !rawData.positionId)) {
        showToast("Must select both valid department and position.", "danger");
        return;
    }

    const isBulk = empIdVal === 'BULK_ACTION';
    const targetIds = isBulk ? selectedEmpIds : [parseInt(empIdVal)];

    const createAssignBody = (field, value) => ({
        [field]: value ? parseInt(value) : null,
        employeeIds: targetIds
    });

    try {
        if (action === 'both') {
            const deptBody = createAssignBody('departmentId', rawData.departmentId);
            const posiBody = createAssignBody('positionId', rawData.positionId);

            const [resDept, resPosi] = await Promise.all([
                fetch(`${CTX}/api/employees/department`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(deptBody)
                }),
                fetch(`${CTX}/api/employees/position`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(posiBody)
                })
            ]);

            if (resDept.ok && resPosi.ok) {
                finalizeSubmit("Updated department and position successfully!");
            } else {
                const errResult = !resDept.ok ? await resDept.json() : await resPosi.json();
                handleApiError(errResult);
            }
            return; 
        }

        let url = `${CTX}/api/employees`;
        let method = (empIdVal && !isBulk) ? 'PUT' : 'POST'; 
        let body = { ...rawData };

        if (action === 'department') {
            url += "/department";
            method = 'PUT'; 
            body = createAssignBody('departmentId', rawData.departmentId);
        } 
        else if (action === 'position') {
            url += "/position";
            method = 'PUT';
            body = createAssignBody('positionId', rawData.positionId);
        } 
        else {
            const title = document.getElementById('modalTitle').innerText;
            if (empIdVal && !isBulk) {
                body.id = parseInt(empIdVal);
                method = 'PUT';
                if (title.includes("Status")) url += "/status";
            }
        }

        const res = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });

        const result = await res.json();
        if (res.ok) {
            finalizeSubmit(result.message);
        } else {
            handleApiError(result);
        }

    } catch (err) {
        showToast("Cannot connect to server.", "danger");
    }
}

function finalizeSubmit(message) {
    Modals.emp.hide();
    showToast(message, "success");
    selectedEmpIds = [];
    
    const selectAllCb = document.getElementById('selectAll');
    if (selectAllCb) selectAllCb.checked = false;
    
    toggleBulkBar();
    loadEmployees();
}

function resetModalLayout(title, visibleGroups = [], mode = 'standard') {
    empForm.reset();
    document.getElementById('empId').value = '';
    document.getElementById('modalTitle').innerHTML = title;
    
    ['group-basic', 'group-assignment', 'group-status'].forEach(id => {
        const groupEl = document.getElementById(id);
        if (groupEl) {
            const isVisible = visibleGroups.includes(id);
            groupEl.style.display = isVisible ? 'block' : 'none';
            groupEl.querySelectorAll('input, select').forEach(el => {
                el.disabled = !isVisible;
            });
        }
    });

    const stdFooter = document.getElementById('footer-standard');
    const assignFooter = document.getElementById('footer-assign');

    if (mode === 'assign') {
        stdFooter.classList.add('d-none');
        assignFooter.classList.remove('d-none');
        assignFooter.classList.add('d-flex'); 
    } else {
        stdFooter.classList.remove('d-none');
        assignFooter.classList.add('d-none');
        assignFooter.classList.remove('d-flex'); 
    }
}

function openAssignModal(id) {
    resetModalLayout('<i class="bi bi-briefcase"></i> Reassign department/position', ['group-assignment'], 'assign');
    
    document.getElementById('selDepartment').required = false;
    document.getElementById('selPosition').required = false;
    
    document.getElementById('selDepartment').closest('.col-md-6').style.display = 'block';
    document.getElementById('selPosition').closest('.col-md-6').style.display = 'block';
    document.getElementById('selDepartment').disabled = false;
    document.getElementById('selPosition').disabled = false;

    const footer = document.getElementById('footer-assign');
    footer.querySelector('[data-action="department"]').classList.remove('d-none');
    footer.querySelector('[data-action="position"]').classList.remove('d-none');
    footer.querySelector('[data-action="both"]').classList.remove('d-none');

    document.getElementById('empId').value = id;
    Modals.emp.show();
}

async function openEditModal(id) {
    resetModalLayout(`<i class="bi bi-pencil"></i> Edit basic info`, ['group-basic'], 'standard');
    document.getElementById('empId').value = id;
    
    try {
        const res = await fetch(`${CTX}/api/employees/${id}`);
        const result = await res.json();
        if (res.ok) {
            const d = result.data;
            empForm.querySelector('[name="fullName"]').value = d.fullName;
            empForm.querySelector('[name="email"]').value = d.email;
            empForm.querySelector('[name="phone"]').value = d.phone;
            Modals.emp.show();
        } else {
            handleApiError(result);
        }
    } catch (err) { 
        showToast("Cannot connect to server.", "danger");
    }
}

function openAddModal() {
    resetModalLayout('<i class="bi bi-person-plus"></i> Add new employee', ['group-basic', 'group-assignment'], 'standard');
    Modals.emp.show();
}

function openStatusModal(id, currentStatus) {
    resetModalLayout('<i class="bi bi-shield-exclamation"></i> Change Status', ['group-status'], 'standard');
    document.getElementById('empId').value = id;
    empForm.querySelector('select[name="status"]').value = currentStatus;
    Modals.emp.show();
}

function handleSingleCheck(el) {
    const id = parseInt(el.value);
    if (el.checked) {
        selectedEmpIds.push(id);
    } else {
        selectedEmpIds = selectedEmpIds.filter(i => i !== id);
        document.getElementById('selectAll').checked = false;
    }
    toggleBulkBar();
}

function toggleBulkBar() {
    const bar = document.getElementById('bulkActions');
    const label = bar.querySelector('.selected-count');
    if (selectedEmpIds.length > 0) {
        bar.classList.remove('d-none');
        bar.classList.add('d-flex');
        label.innerText = `(${selectedEmpIds.length} selected)`;
    } else {
        bar.classList.add('d-none');
        bar.classList.remove('d-flex');
    }
}

function openBulkModal(type) {
    const isDept = type === 'department';
    const title = isDept ? 'Bulk assign department' : 'Bulk assign position';
    
    resetModalLayout(`<i class="bi bi-layer-forward"></i> ${title}`, ['group-assignment'], 'assign');

    const selDept = document.getElementById('selDepartment');
    const selPosi = document.getElementById('selPosition');
    
    selDept.closest('.col-md-6').style.display = isDept ? 'block' : 'none';
    selPosi.closest('.col-md-6').style.display = !isDept ? 'block' : 'none';
    
    selDept.disabled = !isDept;
    selDept.required = isDept;
    
    selPosi.disabled = isDept;
    selPosi.required = !isDept;

    const footer = document.getElementById('footer-assign');
    footer.querySelector('[data-action="department"]').classList.toggle('d-none', !isDept);
    footer.querySelector('[data-action="position"]').classList.toggle('d-none', isDept);
    footer.querySelector('[data-action="both"]').classList.add('d-none');

    document.getElementById('empId').value = 'BULK_ACTION';
    Modals.emp.show();
}

async function showDetail(id) {
    const content = document.getElementById('detailContent');
    content.innerHTML = `<tr><td colspan="8" class="text-center py-4"><div class="spinner-border text-primary spinner-border-sm"></div> Loading...</td></tr>`;

    try {
        const res = await fetch(`${CTX}/api/employees/${id}`);
        const result = await res.json();

        if (res.ok) {
            const emp = result.data;
            const created = emp.createdDate ? formatDateTime(emp.createdDate) : 'N/A';
            const modified = emp.modifiedDate ? formatDateTime(emp.modifiedDate) : 'N/A';

            content.innerHTML = `
                <div class="row g-4">
                    <div class="col-12">
                        <h6 class="text-primary fw-bold border-bottom pb-2 mb-3"><i class="bi bi-person-vcard me-2"></i>IDENTITY</h6>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="small text-muted d-block">Full name</label>
                                <span class="fw-bold text-dark">${emp.fullName}</span>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label class="small text-muted d-block">Employee code</label>
                                <span class="badge bg-primary-subtle text-primary border border-primary-semibold">${emp.employeeCode}</span>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label class="small text-muted d-block"><i class="bi bi-envelope me-1"></i>Email</label>
                                <span class="text-dark">${emp.email || 'N/A'}</span>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label class="small text-muted d-block"><i class="bi bi-telephone me-1"></i>Phone</label>
                                <span class="text-dark">${emp.phone || 'N/A'}</span>
                            </div>
                        </div>
                    </div>

                    <div class="col-12 mt-2">
                        <h6 class="text-primary fw-bold border-bottom pb-2 mb-3"><i class="bi bi-briefcase me-2"></i>WORK ASSIGNMENT</h6>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="small text-muted d-block">Department</label>
                                <span class="fw-medium text-dark">${emp.departmentNameAndCode || 'N/A'}</span>
                            </div>
                            <div class="col-md-3 mb-3">
                                <label class="small text-muted d-block">Position</label>
                                <span class="text-uppercase small fw-bold text-secondary">${emp.positionName || 'N/A'}</span>
                            </div>
                            <div class="col-md-3 mb-3">
                                <label class="small text-muted d-block">Current status</label>
                                ${getStatusBadge(emp.status)}
                            </div>
                        </div>
                    </div>

                    <div class="col-12 mt-2">
                        <h6 class="text-primary fw-bold border-bottom pb-2 mb-3"><i class="bi bi-info-circle me-2"></i>SYSTEM INFORMATION</h6>
                        <div class="row px-2">
                            <div class="col-md-6 small text-muted">
                                <div class="d-flex justify-content-between border-bottom py-1">
                                    <span><i class="bi bi-clock-history"></i> Created date:</span>
                                    <span class="text-dark fw-medium">${created}</span>
                                </div>
                                <div class="d-flex justify-content-between py-1">
                                    <span><i class="bi bi-person-plus"></i> Created by:</span>
                                    <span class="text-dark fw-medium">${emp.createdBy || 'System'}</span>
                                </div>
                            </div>
                            <div class="col-md-6 small text-muted border-start ps-md-4">
                                <div class="d-flex justify-content-between border-bottom py-1">
                                    <span><i class="bi bi-pencil-square"></i> Last modified date:</span>
                                    <span class="text-dark fw-medium">${modified}</span>
                                </div>
                                <div class="d-flex justify-content-between py-1">
                                    <span><i class="bi bi-person-check"></i> Last modified by:</span>
                                    <span class="text-dark fw-medium">${emp.modifiedBy || 'N/A'}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            `;
            
            Modals.detail.show();
        } else {
            handleApiError(result);
        }
    } catch (e) {
        showToast("Cannot connect to server.", "danger");
    }
}

function getStatusBadge(status) {
    const config = {
        'ACTIVE': 'bg-success',
        'ON_LEAVE': 'bg-warning text-dark',
        'PENDING': 'bg-info text-white',
        'INACTIVE': 'bg-danger'
    };
    return `<span class="badge ${config[status] || 'bg-secondary'}">${status}</span>`;
}
