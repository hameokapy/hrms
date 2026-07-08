// File: /assets/js/management/leaves/list.js

const leaveModal = new bootstrap.Modal(document.getElementById('leaveModal'));
const leaveForm = document.getElementById('leaveForm');
let currentRequests = [];
let currentSearchParams = "";

document.addEventListener('DOMContentLoaded', () => {
    initYearFilter();
    
    const tabReq = document.getElementById('tab-req');
    const tabBal = document.getElementById('tab-bal');

    if (tabReq) {
        loadRequests();
        tabReq.addEventListener('shown.bs.tab', () => {
            setupForm('REQUEST');
            loadRequests();
        });
    } else if (tabBal) {
        setupForm('BALANCE');
        loadBalances();
    }

    if (tabBal) {
        tabBal.addEventListener('shown.bs.tab', () => {
            setupForm('BALANCE');
            loadBalances();
        });
    }

    const btnReset = document.getElementById('btnReset');
    if (btnReset) {
        btnReset.onclick = handleReset;
    }

    document.getElementById('leaveSearchForm').onsubmit = (e) => {
        e.preventDefault();
        const activeTabEl = document.querySelector('.nav-link.active');
        if(!activeTabEl) return;
        
        const activeTab = activeTabEl.id;
        const queryString = new URLSearchParams(new FormData(e.target)).toString();
        
        if (activeTab === 'tab-req') loadRequests(queryString);
        else loadBalances(queryString);
    };
    
    leaveForm.onsubmit = async (e) => {
        e.preventDefault();
        const formData = new FormData(leaveForm);
        const data = Object.fromEntries(formData.entries());
        const id = data.id;

        const url = id ? `${CTX}/api/leave/requests/update` : `${CTX}/api/leave/requests`;
        const method = id ? 'PUT' : 'POST';

        try {
            const res = await fetch(url, {
                method: method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
            const result = await res.json();

            if (res.ok) {
                leaveModal.hide();
                loadRequests();
                showToast(result.message, "success");
            } else { 
                handleApiError(result); 
            }
        } catch (err) { 
            showToast("Cannot connect to server.", "danger"); 
        }
    };
});

function initYearFilter() {
    const yearSelect = document.getElementById('filterYear');
    const curYear = new Date().getFullYear();
    yearSelect.innerHTML = '';
    for (let y = curYear + 1; y >= curYear - 1; y--) {
        const opt = new Option(y, y);
        if (y === curYear) opt.selected = true;
        yearSelect.add(opt);
    }
}

function setupForm(mode) {
    const isReq = (mode === 'REQUEST');
    const filterActions = document.getElementById('filterActions');
    const createBtnContainer = document.getElementById('createRequestAction');
    
    const form = document.getElementById('leaveSearchForm');
    if(form) form.reset();
    
    initYearFilter();

    document.querySelectorAll('.filter-request').forEach(el => {
        isReq ? el.classList.remove('d-none') : el.classList.add('d-none');
    });
    
    document.querySelectorAll('.filter-balance').forEach(el => {
        isReq ? el.classList.add('d-none') : el.classList.remove('d-none');
    });

    if (createBtnContainer) {
        isReq ? createBtnContainer.classList.remove('d-none') : createBtnContainer.classList.add('d-none');
    }

    if (filterActions) {
        if (isReq) filterActions.classList.replace('col-md-2', 'col-md-12');
        else filterActions.classList.replace('col-md-12', 'col-md-2');
    }
}

function handleReset() {
    const form = document.getElementById('leaveSearchForm');
    if (form) form.reset();
    
    initYearFilter();

    const activeTab = document.querySelector('.nav-link.active')?.id || 'tab-bal';

    if (activeTab === 'tab-req') {
        loadRequests();
    } else {
        loadBalances();
    }
}

async function loadRequests(qs = '', page = 1) {
    const tbody = document.querySelector('#requestTable tbody');
    tbody.innerHTML = `<tr><td colspan="8" class="text-center py-4"><div class="spinner-border text-primary spinner-border-sm"></div> Loading...</td></tr>`;
    
    try {
        const res = await fetch(`${CTX}/api/leave/requests?${qs}&page=${page}`);
        const result = await res.json();
        
        if (res.ok && result.data) {
            const pageData = result.data;
            renderTable(pageData.content);
            renderPaginationMaster(pageData, 'pagination', 'loadRequests', qs);
            updatePaginationInfo(pageData);
            
            currentRequests = pageData.content;
        } else {
            handleApiError(result);
            tbody.innerHTML = `<tr><td colspan="8" class="text-center py-4" style="color: #856404; font-weight: 500;">
                <i class="bi bi-exclamation-triangle"></i> Data cannot be loaded!</td></tr>`;
        }
    } catch (err) {
        console.log(err);
        tbody.innerHTML = `<tr><td colspan="8" class="text-center text-danger">Connecting server failed!</td></tr>`;
    }
}

function renderTable(list) {
    const tbody = document.querySelector('#requestTable tbody');
    if (!list || list.length === 0) {
        tbody.innerHTML = `<tr><td colspan="6" class="text-center py-4 text-muted">No matched data.</td></tr>`;
        return;
    }
    
    const templateHtml = document.getElementById('actionTemplate').innerHTML;

    const typeMap = {
        'ANNUAL': 'bg-primary',
        'SICK':   'bg-danger',
        'UNPAID': 'bg-warning text-dark'
    };

    tbody.innerHTML = list.map(item => {
        const typeClass = typeMap[item.type] || 'bg-secondary';

        let actionContent = '';

        if (item.status === 'CANCELLED') {
            actionContent = `<span class="text-muted px-2" style="font-size: 11px;"><i class="bi bi-lock-fill"></i> Locked</span>`;
        } else {
            let temp = templateHtml;
            
            temp = temp.replaceAll('ID_PLH', item.id);
            temp = temp.replaceAll('openEditModal(JSON_PLH)', `prepareEdit(${item.id})`);

            const tempDiv = document.createElement('div');
            tempDiv.innerHTML = temp;

            if (item.status !== 'PENDING') {
                tempDiv.querySelector('.btn-edit')?.remove();
                tempDiv.querySelector('.btn-cancel')?.remove();
            }

            actionContent = tempDiv.innerHTML;
        }

        return `
        <tr>
            <td class="ps-4">
                <div class="text-primary mt-1" style="font-size: 11px; font-weight: 500;">
                    <i class="bi bi-send-fill me-1"></i>Sent: ${formatDateTime(item.createdDate)}
                </div>
                <div class="fw-bold text-dark">${item.employeeNameAndCode}</div>
                <div class="text-muted" style="font-size: 11px;">
                    <i class="bi bi-building me-1"></i>${item.departmentNameAndCode}
                </div>
                ${item.reason ? `
                    <div class="mt-2 p-2 bg-light rounded-2 border-start border-3 border-primary-subtle" 
                         style="font-size: 11.5px; line-height: 1.4; max-width: 250px;">
                        <i class="bi bi-chat-left-text-fill text-primary me-1"></i>
                        <span class="text-dark">"${item.reason}"</span>
                    </div>
                ` : ''}
            </td>

            <td class="text-center">
                <span class="badge ${typeClass} mb-1 px-2">${item.type}</span>
                <div class="small text-dark fw-medium">
                    ${formatDate(item.startDate)} <span class="text-muted">→</span> ${formatDate(item.endDate)}
                </div>
            </td>

            <td class="text-center">
                <div class="h5 mb-0 fw-bold text-primary">
                    ${item.numDays} <small class="fw-normal text-muted" style="font-size: 12px;">days</small>
                </div>
                <div class="d-flex align-items-center justify-content-center gap-2 mt-1">
                    <span class="text-muted fw-bold text-uppercase" style="font-size: 9px; letter-spacing: 0.5px; white-space: nowrap;">
                        Remaining:
                    </span>
                    <div class="d-flex gap-1">
                        <span class="badge bg-success-subtle text-success border border-success-semibold" 
                              style="font-size: 10px; padding: 3px 6px;">
                            ${item.annualBalanceRemain} Annual
                        </span>
                        <span class="badge bg-danger-subtle text-danger border border-danger-semibold" 
                              style="font-size: 10px; padding: 3px 6px;">
                            ${item.sickBalanceRemain} Sick
                        </span>
                    </div>
                </div>
            </td>

            <td class="text-center">
                ${getStatusBadge(item.status)}
                ${(item.status !== 'CANCELLED') ? `
                    <div class="mt-2" style="font-size: 10px; line-height: 1.2;">
                        <div class="text-dark fw-medium mb-1">
                            By: ${(!item.approverNameAndCode || item.approverNameAndCode.includes('null')) ? 'N/A' : item.approverNameAndCode}
                        </div>
                        <div class="text-muted">
                            Audit date: ${formatDateTime(item.approvedDate)}
                        </div>
                    </div>
                ` : ''}
            </td>

            <td class="text-end px-4">${actionContent}</td>
        </tr>`;
    }).join('');
}

async function loadBalances(qs = '', page = 1) {
    if (!qs) {
        const yearEl = document.getElementById('filterYear');
        qs = `year=${yearEl ? yearEl.value : new Date().getFullYear()}`;
    }
    
    const tbody = document.querySelector('#balanceTable tbody');
    tbody.innerHTML = '<tr><td colspan="5" class="text-center py-4"><div class="spinner-border spinner-border-sm text-primary"></div></td></tr>';

    try {
        const params = new URLSearchParams(qs);
        const res = await fetch(`${CTX}/api/leave/balance?${params.toString()}&page=${page}`);
        const result = await res.json();
        const pageData = result.data;
        
        if (res.ok && result.data && result.data.content && result.data.content.length > 0) {
            tbody.innerHTML = pageData.content.map(item => `
                <tr>
                    <td class="ps-4 py-3 fw-bold text-dark">${item.employeeNameCode}</td>
                    <td class="text-muted small">${item.departmentNameCode}</td>
                    <td class="text-center">
                        <b class="text-success">${item.annualUsedDays}</b> <small class="text-muted">/</small> <b class="text-dark">${item.annualRemainingDays + item.annualUsedDays}</b>
                    </td>
                    <td class="text-center">
                        <b class="text-danger">${item.sickUsedDays}</b> <small class="text-muted">/</small> <b class="text-dark">${item.sickRemainingDays + item.sickUsedDays}</b>
                    </td>
                    <td class="text-center px-4">
                        <div class="d-flex flex-column gap-1 align-items-center">
                            <span class="badge bg-success-subtle text-success border border-success-semibold w-100" style="max-width: 120px;">
                                <i class="bi bi-sun-fill me-1"></i> ${item.annualRemainingDays} Annual Left
                            </span>
                            <span class="badge bg-danger-subtle text-danger border border-danger-semibold w-100" style="max-width: 120px;">
                                <i class="bi bi-thermometer-half me-1"></i> ${item.sickRemainingDays} Sick Left
                            </span>
                        </div>
                    </td>
                </tr>
            `).join('');
            
            renderPaginationMaster(pageData, 'pagination', 'loadBalances', qs);
            updatePaginationInfo(pageData);
        } else {
            tbody.innerHTML = '<tr><td colspan="5" class="text-center py-4 text-muted">No balance data found for this year.</td></tr>';
            const paginEl = document.getElementById('pagination');
            if (paginEl) paginEl.innerHTML = '';
            updatePaginationInfo(pageData);
        }
    } catch (err) {
        console.error("Load balance error");
        tbody.innerHTML = '<tr><td colspan="5" class="text-center py-4 text-danger">Connecting server failed!</td></tr>';
    }
}

function getStatusBadge(s) {
    const statusMap = {
        'PENDING':   'bg-warning-subtle text-warning border-warning',
        'APPROVED':  'bg-success-subtle text-success border-success',
        'REJECTED':  'bg-danger-subtle text-danger border-danger',
        'CANCELLED': 'bg-secondary-subtle text-secondary border-secondary'
    };

    const cls = statusMap[s] || 'bg-secondary-subtle text-secondary border-secondary';
    
    return `<span class="badge ${cls} border px-2" style="min-width:80px">${s}</span>`;
}

function openAddModal(defaultEmpId, isAdminRole) {
    leaveForm.reset();
    document.getElementById('leaveId').value = '';
    document.getElementById('leaveModalTitle').innerHTML = '<i class="bi bi-plus-lg"></i> Create new request';
    
    const empInput = document.getElementById('leaveEmpId');
    empInput.value = defaultEmpId;
    if (!isAdminRole) {
        empInput.readOnly = true;
        empInput.classList.add('bg-light');
        empInput.title = "You can only create requests for yourself.";
    } else {
        empInput.readOnly = false;
        empInput.classList.remove('bg-light');
        empInput.title = "Enter Employee ID to create request.";
    }
    
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    document.getElementById('leaveStart').value = tomorrow.toISOString().split('T')[0];
    document.getElementById('leaveEnd').value = tomorrow.toISOString().split('T')[0];
    
    toggleModalMode('ADD');
    leaveModal.show();
}

function prepareEdit(id) {
    const item = currentRequests.find(r => r.id == id);
    if (item) {
        openEditModal(item);
    }
}

function openEditModal(item) {
    if (typeof item === 'string') item = JSON.parse(item);
    
    leaveForm.reset();
    fillFormData(item);
    
    document.getElementById('leaveModalTitle').innerHTML = '<i class="bi bi-pencil fs-6"></i> Update request';
    toggleModalMode('EDIT');
    leaveModal.show();
}

function fillFormData(item) {
    leaveForm.reset();
    document.getElementById('leaveId').value = item.id;
    document.getElementById('leaveEmpId').value = item.employeeId;
    document.getElementById('leaveType').value = item.type;
    document.getElementById('leaveStart').value = item.startDate;
    document.getElementById('leaveEnd').value = item.endDate;
    document.getElementById('leaveReason').value = item.reason || '';
}

async function handleCancel(id) {
    if (!confirm('Are you sure to CANCEL this request?')) return;

    try {
        const res = await fetch(`${CTX}/api/leave/requests/cancel`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ id: id })
        });
        const result = await res.json();
        
        if (res.ok) {
            showToast(result.message, 'success');
            loadRequests();
        } else { 
            handleApiError(result); 
        }
    } catch (err) { 
        showToast("Cannot connect to server.", "danger");
    }
}

function handleApprove(id) {
    const item = currentRequests.find(r => r.id == id);
    if (!item) return;
    
    fillFormData(item);
    document.getElementById('leaveModalTitle').innerHTML = '<i class="bi bi-shield-check text-success"></i> Review request';
    toggleModalMode('APPROVE');
    leaveModal.show();
}

function toggleModalMode(mode) {
    const isApprove = (mode === 'APPROVE');
    const form = document.getElementById('leaveForm');
    const btnSubmit = document.getElementById('btnSubmitForm');
    const approveActions = document.getElementById('approveActions');
    
    if (isApprove) {
        btnSubmit.classList.add('d-none');
        approveActions.classList.remove('d-none');
    } else {
        btnSubmit.classList.remove('d-none');
        approveActions.classList.add('d-none');
    }

    const inputs = form.querySelectorAll('input:not([type="hidden"]), select, textarea');
    inputs.forEach(input => {
        if (input.id === 'leaveEmpId') {
            if (mode === 'EDIT' || mode === 'APPROVE') {
                input.readOnly = true;
                input.classList.add('bg-light');
            }
        } else {
            input.readOnly = isApprove;
            input.disabled = isApprove;
            isApprove ? input.classList.add('bg-light') : input.classList.remove('bg-light');
        }
//        if (input.id === 'leaveEmpId' && mode !== 'ADD') {
//            input.readOnly = true;
//            input.classList.add('bg-light');
//        } else {
//            input.readOnly = isApprove;
//            input.disabled = isApprove;
//            isApprove ? input.classList.add('bg-light') : input.classList.remove('bg-light');
//        }
    });
}

async function submitApproval(status) {
    const id = document.getElementById('leaveId').value;
    const btnContainer = document.getElementById('approveActions');
    
    const originalContent = btnContainer.innerHTML;
    
    btnContainer.innerHTML = `<div class="text-center w-100"><div class="spinner-border spinner-border-sm text-primary"></div> Processing...</div>`;

    try {
        const res = await fetch(`${CTX}/api/leave/requests/approve`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ 
                id: id, 
                status: status 
            })
        });
        
        const result = await res.json();

        if (res.ok) {
            leaveModal.hide();
            showToast(result.message || `Request ${status.toLowerCase()} successfully!`, "success");
            loadRequests();
            
            setTimeout(() => { btnContainer.innerHTML = originalContent; }, 500);
        } else {
            handleApiError(result);
            btnContainer.innerHTML = originalContent; 
        }
    } catch (err) {
        showToast("Cannot connect to server.", "danger");
        btnContainer.innerHTML = originalContent; 
    }
}