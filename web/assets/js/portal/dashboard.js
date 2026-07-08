let currentDeptId = null;
let currentEmpCode = null;
let leaveRequestsCache = [];
let pageSize = 5;

document.addEventListener('DOMContentLoaded', () => {
    initDashboard();
    initLeaveFormHandler();
});

async function initDashboard() {
    
    const leaveTableBody = document.querySelector('#leaveTable tbody');
    if (leaveTableBody) leaveTableBody.innerHTML = '<tr><td colspan="4" class="text-center py-3"><div class="spinner-border spinner-border-sm text-primary"></div></td></tr>';

    try {
        const emp = await fetchEmployeeInfo(); 
        
        if (emp) {
            currentEmpCode = emp.employeeCode;
            currentDeptId = emp.departmentId;

            const tasks = [
                fetchLeaveBalance(currentEmpCode),
                loadLeaveRequests(currentEmpCode)
            ];
            
            if (currentDeptId) {
                tasks.push(fetchDepartmentInfo(currentDeptId));
            }
            
            await Promise.all(tasks);
        }
        console.log("All data fetched successfully");
    } catch (err) {
        console.warn(err);
        if (leaveTableBody) 
            leaveTableBody.innerHTML = `<tr><td colspan="4"><div class="list-group-item text-danger text-center small py-3">Cannot connect to server!</div></td></tr>`;
    }
}

async function fetchEmployeeInfo() {
    const empId = document.getElementById('currentEmpId').value;
    
    const res = await fetch(`${CTX}/api/employees/${empId}`);
    
    if (res.ok) {
        const result = await res.json();
        const emp = result.data; 
        
        document.getElementById('empName').innerText = emp.fullName || 'N/A';
        document.getElementById('empPos').innerText = emp.positionName || 'N/A';
        document.getElementById('empEmail').innerText = emp.email || 'N/A';
        document.getElementById('empPhone').innerText = emp.phone || 'N/A';
        document.getElementById('empStatus').innerText = emp.status || 'N/A';
        
        return emp;
    }
}

async function fetchDepartmentInfo(deptId) {
    if (!deptId) return; 

    try {
        const res = await fetch(`${CTX}/api/departments?id=${deptId}`);
        if (res.ok) {
            const result = await res.json();
            const list = result.data.content || [];
            if (list.length > 0) {
                const dept = list[0];
                
                document.getElementById('deptCode').innerText = dept.code || '...';
                document.getElementById('deptName').innerText = dept.name || '...';
                document.getElementById('deptManager').innerText = dept.managerName || 'Not Assigned';
                document.getElementById('deptCount').innerText = dept.totalEmployees || 0;
                document.getElementById('deptLocation').innerText = dept.location || '...';
            } else {
                console.warn("No department found with such ID");
            }
        }
    } catch (err) {
        showToast("Fetch dept error", "danger");
    }
}

async function fetchLeaveBalance(empCode) {
    if (!empCode) {
        document.getElementById('annualDays').innerText = 'N/A';
        document.getElementById('sickDays').innerText = 'N/A';
        return;
    }

    const currentYear = new Date().getFullYear();
    
    try {
        const res = await fetch(`${CTX}/api/leave/balance?year=${currentYear}&employee=${empCode}`);
        
        if (res.ok) {
            const result = await res.json();
            const list = result.data.content || []; 
            
            if (list.length > 0) {
                const balance = list[0];
                document.getElementById('annualDays').innerText = balance.annualRemainingDays ?? 'N/A';
                document.getElementById('sickDays').innerText = balance.sickRemainingDays ?? 'N/A';
            } else {
                document.getElementById('annualDays').innerText = 'N/A';
                document.getElementById('sickDays').innerText = 'N/A';
            }
        }
    } catch (err) {
        showToast("Fetch leave balance error", "danger");
        document.getElementById('annualDays').innerText = 'N/A';
        document.getElementById('sickDays').innerText = 'N/A';
    }
}

async function loadLeaveRequests(empCode, page = 1) {
    if (!empCode) return;

    const tbody = document.querySelector('#leaveTable tbody');
    if (!tbody) return;

    const res = await fetch(`${CTX}/api/leave/requests?keyword=${empCode}&page=${page}&pageSize=${pageSize}`);

    if (res.ok) {
        const result = await res.json();
        const pageContent = result.data;
        const list = pageContent.content || [];
        leaveRequestsCache = list;
        
        tbody.innerHTML = ''; 

        if (list.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="4" class="text-center text-muted small py-4">
                        <i class="bi bi-inbox me-1"></i>No leave requests found.
                    </td>
                </tr>`;
            return;
        }

        const statusCfg = {
            'PENDING':   { class: 'bg-warning text-dark', label: 'Pending', actions: true },
            'APPROVED':  { class: 'bg-success text-white', label: 'Approved', actions: false },
            'REJECTED':  { class: 'bg-danger text-white', label: 'Rejected', actions: false },
            'CANCELLED': { class: 'bg-secondary text-white', label: 'Cancelled', actions: false }
        };

        const html = list.map(item => {
            const cfg = statusCfg[item.status] || { class: 'bg-secondary text-white', label: item.status, actions: false };

            let actionButtons = cfg.actions ? `
                <div class="btn-group btn-group-sm">
                    <button class="btn btn-outline-primary" title="Edit" onclick="updateRequest(${item.id})">
                        <i class="bi bi-pencil-square"></i>
                    </button>
                    <button class="btn btn-outline-danger" title="Cancel" onclick="cancelRequest(${item.id})">
                        <i class="bi bi-x-circle"></i>
                    </button>
                </div>` : '<i class="bi bi-lock-fill text-muted" title="Locked"></i>';

            return `
                <tr class="align-middle">
                    <td class="ps-4">
                        <div class="fw-bold small text-dark">${formatDate(item.startDate)} to ${formatDate(item.endDate)}</div>
                        <div class="text-muted" style="font-size: 0.7rem;">
                            <i class="bi bi-clock-history me-1"></i>${item.numDays} days
                        </div>
                    </td>
                    <td>
                        <div class="small text-dark fw-medium">${item.type}</div>
                        <div class="text-muted text-truncate" style="font-size: 0.7rem; max-width: 150px;" title="${item.reason || 'No reason provided'}">
                            <i class="bi bi-chat-left-text me-1"></i>${item.reason || 'N/A'}
                        </div>
                    </td>
                    <td>
                        <span class="badge ${cfg.class} px-3 rounded-pill" style="font-size: 0.7rem;">${cfg.label}</span>
                    </td>
                    <td class="text-end pe-4">
                        ${actionButtons}
                    </td>
                </tr>`;
        }).join('');
        
        tbody.innerHTML = html;
        
        renderPaginationMaster(pageContent, 'leavePagination', 'loadLeaveRequests', empCode);
        updatePaginationInfo(pageContent);
    }
}

async function showProfileModal() {
    const empId = document.getElementById('currentEmpId').value;
    const modal = new bootstrap.Modal(document.getElementById('profileModal'));
    
    try {
        const res = await fetch(`${CTX}/api/employees/${empId}`);
        if (res.ok) {
            const result = await res.json();
            const emp = result.data;

            document.getElementById('modalEmpCode').value = emp.employeeCode || 'N/A';
            document.getElementById('modalDeptName').value = emp.departmentNameAndCode || 'N/A';
            document.getElementById('modalPosName').value = emp.positionName || 'N/A';
            document.getElementById('modalStatus').value = emp.status || 'N/A';
            
            document.getElementById('modalCreatedBy').innerText = emp.createdBy || 'System';
            document.getElementById('modalCreatedDate').innerText = formatDateTime(emp.createdDate);
            document.getElementById('modalModifiedBy').innerText = emp.modifiedBy || 'N/A';
            document.getElementById('modalModifiedDate').innerText = formatDateTime(emp.modifiedDate);

            document.getElementById('modalFullName').value = emp.fullName || 'N/A';
            document.getElementById('modalEmail').value = emp.email || 'N/A';
            document.getElementById('modalPhone').value = emp.phone || 'N/A';

            modal.show();
        } else {
            handleApiError(result);
        }
    } catch (err) {
        showToast("Cannot connect to server.", "danger");
    }
}

async function saveProfile() {
    const empId = document.getElementById('currentEmpId').value;
    const modalEl = document.getElementById('profileModal');
    
    const data = {
        id: empId,
        phone: document.getElementById('modalPhone').value.trim()
    };

    try {
        const res = await fetch(`${CTX}/api/employees`, { 
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        const result = await res.json();

        if (!res.ok) {
            handleApiError(result);
            return; 
        }

        bootstrap.Modal.getInstance(modalEl)?.hide();
        showToast(result.message, "success");
        
        fetchEmployeeInfo();
        
    } catch (err) {
        showToast("Cannot connect to server", "danger");
    }
}

async function viewDeptEmployees(qs='', page = 1) {
    const modalEl = document.getElementById('deptEmployeesModal');
    
    let modal = bootstrap.Modal.getInstance(modalEl);
    if (!modal) {
        modal = new bootstrap.Modal(modalEl);
    }
    
    const listContainer = document.getElementById('deptEmpList');
    const deptId = currentDeptId; 

    if (!deptId) {
        showToast("Not found department ID!", "danger");
        return;
    }

    listContainer.innerHTML = '<div class="p-4 text-center"><div class="spinner-border text-primary"></div></div>';
    modal.show();

    try {
        const res = await fetch(`${CTX}/api/departments/${deptId}/employees?page=${page}&pageSize=${pageSize}`);
        const result = await res.json();
        
        const pageContent = result.data;
        const employees = pageContent.content || [];

        if (employees.length === 0) {
            listContainer.innerHTML = '<div class="p-4 text-center text-muted small">Department with no employee.</div>';
        } else {
            listContainer.innerHTML = employees.map(emp => `
                <div class="list-group-item d-flex align-items-center p-3 border-0 border-bottom">
                    <div class="bg-primary bg-opacity-10 rounded-circle p-2 me-3">
                        <i class="bi bi-person-fill text-primary"></i>
                    </div>
                    <div class="flex-grow-1">
                        <div class="fw-bold mb-0" style="font-size: 0.85rem;">${emp.fullName}</div>
                        <div class="text-muted" style="font-size: 0.7rem;">${emp.positionName}</div>
                    </div>
                    <div class="text-end">
                         <span class="badge bg-light text-secondary fw-normal border" style="font-size: 0.65rem;">${emp.employeeCode}</span>
                    </div>
                </div>
            `).join('');
            renderPaginationMaster(pageContent, 'empPagination', 'viewDeptEmployees', qs);
            document.getElementById('empTotalElements').innerText = pageContent.totalElements;
        }
    } catch (err) {
        listContainer.innerHTML = `<div class="p-3 text-danger text-center">Connecting server failed!</div>`;;
    }
}

async function viewAllDepts(qs = '', page = 1) {
    const modalEl = document.getElementById('allDeptsModal');
    
    let modal = bootstrap.Modal.getInstance(modalEl);
    if (!modal) {
        modal = new bootstrap.Modal(modalEl);
    }
    
    const tbody = document.getElementById('allDeptsTableBody');
    
    tbody.innerHTML = '<tr><td colspan="4" class="text-center py-4"><div class="spinner-border text-primary"></div></td></tr>';
    modal.show();

    try {
        const res = await fetch(`${CTX}/api/departments?status=ACTIVE&page=${page}&pageSize=${pageSize}`);
        const result = await res.json();
        const pageContent = result.data;
        const depts = pageContent.content || [];

        if(depts.length === 0) {
            tbody.innerHTML = '<div class="p-4 text-center text-muted small">Company with no departments.</div>';
        } else {
            tbody.innerHTML = depts.map(d => `
                <tr>
                    <td><span class="badge bg-light text-primary border border-primary-subtle">${d.code}</span></td>
                    <td class="fw-bold small">${d.name}</td>
                    <td class="small">${d.managerName || '<span class="text-muted">N/A</span>'}</td>
                    <td class="text-center"><span class="badge rounded-pill bg-primary bg-opacity-10 text-primary">${d.totalEmployees}</span></td>
                </tr>
            `).join('');

            renderPaginationMaster(pageContent, 'deptPagination', 'viewAllDepts', qs);
            document.getElementById('deptTotalElements').innerText = pageContent.totalElements;
        }
    } catch (err) {
        tbody.innerHTML = `<tr><td colspan="8" class="text-center text-danger">Connecting server failed!</td></tr>`;;
    }
}

function initLeaveFormHandler() {
    const form = document.getElementById('leaveForm');
    if (!form) return;

    form.onsubmit = async (e) => {
        e.preventDefault();
        const btn = document.getElementById('btnSubmitForm');
        const formData = new FormData(e.target);
        const data = Object.fromEntries(formData.entries());
        
        const isUpdate = !!data.id;
        const endpoint = isUpdate ? `${CTX}/api/leave/requests/update` : `${CTX}/api/leave/requests`;
        
        btn.disabled = true;
        btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Processing...';

        try {
            const res = await fetch(endpoint, {
                method: isUpdate ? 'PUT' : 'POST', 
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });

            const result = await res.json();
            if (res.ok) {
                bootstrap.Modal.getInstance(document.getElementById('leaveModal'))?.hide();
                showToast(isUpdate ? "Updated!" : "Submitted!", "success");
                
                loadLeaveRequests(currentEmpCode);
                fetchLeaveBalance(currentEmpCode);
            } else {
                showToast(result.message, "danger");
            }
        } catch (err) {
            showToast("Connection error", "danger");
        } finally {
            btn.disabled = false;
            btn.innerHTML = isUpdate ? '<i class="bi bi-pencil-square me-2"></i>Edit Request' : `<i class="bi bi-send-fill me-2"></i>Submit Request`;
        }
    };
}

function createNewLeave() {
    const form = document.getElementById('leaveForm');
    form.reset();
    document.getElementById('leaveId').value = '';
    document.getElementById('leaveModalTitle').innerHTML = '<i class="bi bi-calendar-plus me-2"></i> New Leave Request';
    
    document.getElementById('btnSubmitForm').innerHTML = '<i class="bi bi-send-fill me-2"></i>Submit Request';
    
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    document.getElementById('leaveStart').value = tomorrow.toISOString().split('T')[0];
    document.getElementById('leaveEnd').value = tomorrow.toISOString().split('T')[0];

    new bootstrap.Modal(document.getElementById('leaveModal')).show();
}

function updateRequest(id) {
    const item = leaveRequestsCache.find(r => r.id === id);
    if (!item) {
        showToast("Request data not found.", "danger");
        return;
    }

    document.getElementById('leaveId').value = item.id;
    document.getElementById('leaveModalTitle').innerHTML = '<i class="bi bi-pencil-square me-2"></i> Edit Request';
    
    document.getElementById('btnSubmitForm').innerHTML = '<i class="bi bi-pencil-square me-2"></i>Edit Request';

    const radioType = document.querySelector(`input[name="type"][value="${item.type.toUpperCase()}"]`);
    if (radioType) radioType.checked = true;

    document.getElementById('leaveStart').value = item.startDate;
    document.getElementById('leaveEnd').value = item.endDate;
    document.getElementById('leaveReason').value = item.reason || '';

    let modalEl = document.getElementById('leaveModal');
    let modal = bootstrap.Modal.getInstance(modalEl) || new bootstrap.Modal(modalEl);
    modal.show();
}

async function cancelRequest(id) {
    if (!confirm("Are you sure to CANCEL this request?")) return;

    try {
        const res = await fetch(`${CTX}/api/leave/requests/cancel`, { 
            method: 'PUT', 
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ id: parseInt(id) }) 
        });
        const result = await res.json();
        
        if (res.ok) {
            showToast(result.message, "success");
            loadLeaveRequests(currentEmpCode);
            fetchLeaveBalance(currentEmpCode);
        } else {
            const result = await res.json();
            showToast(result.message, "danger");
        }
    } catch (err) {
        showToast("Connection error", "danger");
    }
}
