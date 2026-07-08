document.addEventListener('DOMContentLoaded', () => {
    initDashboard();
    updateCurrentDate();
});

async function initDashboard() {
    const alertContainer = document.getElementById('alertContainer');
    
    if (alertContainer) {
        alertContainer.innerHTML = `<div class="text-center py-4"><div class="spinner-border spinner-border-sm text-primary"></div> Loading...</div>`;
    }

    try {
        const res = await fetch(`${CTX}/api/dashboardmgt`);
        const result = await res.json();

        if (res.ok) {
            const data = result.data;
            renderStats(data);
            renderAlerts(data, CTX);
        } else {
            if (typeof handleApiError === 'function') {
                handleApiError(result);
            }
            console.error("Dashboard API Error:", result.message);
        }
    } catch (err) {
        console.error("Dashboard connection error");
        if (alertContainer) {
            alertContainer.innerHTML = `<div class="list-group-item text-danger text-center small py-3">Cannot connect to server!</div>`;
        }
    }
}

function renderStats(data) {
    const fields = ['totalEmployees', 'totalDepartments', 'pendingLeaveRequests'];
    fields.forEach(field => {
        const el = document.getElementById(field);
        if (el) el.innerText = data[field] || 0;
    });
}

function renderAlerts(data, ctx) {
    const container = document.getElementById('alertContainer');
    if (!container) return;

    const alerts = [
        { 
            val: data.deptsWithoutManager, 
            label: "Active Departments without Manager", 
            link: "/management/departments?managerName=none",
            icon: "bi-person-badge",
            color: "warning" 
        },
        { 
            val: data.empsWithoutActiveContract, 
            label: "Valid Employees without Active Contract", 
            link: "/management/employees?contract=none", 
            icon: "bi-file-earmark-medical",
            color: "danger" 
        },
        { 
            val: data.empsWithoutUserAccount, 
            label: "Valid Employees without User Account", 
            link: "/management/users?status=inactive", 
            icon: "bi-person-lock",
            color: "info" 
        }
    ];

    container.innerHTML = alerts.map(a => `
        <a href="${ctx}${a.link}" class="list-group-item list-group-item-action d-flex justify-content-between align-items-center py-3">
            <div class="small">
                <i class="bi ${a.icon} me-2 text-${a.color}"></i>
                <span class="${a.val > 0 ? 'fw-bold text-dark' : 'text-muted'}">${a.label}</span>
            </div>
            <span class="badge ${a.val > 0 ? 'bg-danger' : 'bg-secondary'} rounded-pill">
                ${a.val}
            </span>
        </a>
    `).join('');
}

function updateCurrentDate() {
    const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
    const today  = new Date();
    document.getElementById('currentDateDisplay').innerText = today.toLocaleDateString('en-US', options);
}