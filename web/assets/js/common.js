async function handleLogout() {
    if (!confirm("Are you sure to logout?")) {
        return;
    }

    try {
        const response = await fetch(CTX + '/api/auth/logout', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'}
        });
        
        const res = await response.json();
        
        if (response.status === 200) {
            const msg = encodeURIComponent(res.message || 'logged_out_successful');
            window.location.href = CTX + '/login?msg=' + msg;
        } else {
            alert(res.message || "Logout failed!");
        }
    } catch (error) {
        console.error("FE Error");
        alert('Oops, connecting server failed!');
    }
}

function handleApiError(result) {
    console.log("Error details:", result);
    
    let fullMsg = result.message || "Error occurred";

    if (result.data && typeof result.data === 'object') {
        fullMsg += "\n";
        for (const [field, msg] of Object.entries(result.data)) {
            fullMsg += ` • ${msg}\n`;
        }
    } else if (Array.isArray(result.data)) {
        fullMsg += ":\n" + result.data.join("\n");
    }

    showToast(fullMsg, 'danger');
}

function showToast(message, type = 'success') {
    const toastEl = document.getElementById('liveToast');
    const toastMessage = document.getElementById('toastMessage');
    const toastTitle = document.getElementById('toastTitle');
    
    toastEl.classList.remove('text-white', 'bg-success', 'bg-danger');
    if (type === 'danger') {
        toastEl.classList.add('bg-danger', 'text-white');
        toastTitle.innerText = "Error!";
    } else {
        toastEl.classList.add('bg-success', 'text-white');
        toastTitle.innerText = "Success!";
    }

    toastMessage.innerText = message;
    
    const toast = new bootstrap.Toast(toastEl, { delay: 3000 });
    toast.show();
}

function formatDateTime(dateStr) {
    if (!dateStr) return 'N/A';
    const date = new Date(dateStr);
    if (isNaN(date.getTime())) return dateStr;

    const d = String(date.getDate()).padStart(2, '0');
    const m = String(date.getMonth() + 1).padStart(2, '0');
    const y = date.getFullYear();
    const h = String(date.getHours()).padStart(2, '0');
    const i = String(date.getMinutes()).padStart(2, '0');

    return `${h}:${i} ${d}/${m}/${y}`;
}

function formatDate(dateStr) {
    if (!dateStr) return 'N/A';
    const date = new Date(dateStr);
    if (isNaN(date.getTime())) return dateStr;

    const d = String(date.getDate()).padStart(2, '0');
    const m = String(date.getMonth() + 1).padStart(2, '0');
    const y = date.getFullYear();

    return `${d}/${m}/${y}`;
}

function formatCurrency(amount, currency = 'VND') {
    if (amount === null || amount === undefined || isNaN(amount)) {
        return '0 ₫';
    }
    
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: currency
    }).format(amount);
}

function renderPaginationMaster(pageData, elementId, functionName, contextParam) {
    const pagination = document.getElementById(elementId);
    if (!pagination || pageData.totalPages <= 1) {
        if(pagination) pagination.innerHTML = '';
        return;
    }

    const formattedParam = typeof contextParam === 'string' ? `'${contextParam}'` : contextParam;

    let html = '';
    
    html += `<li class="page-item ${pageData.currentPage === 1 ? 'disabled' : ''}">
                <a class="page-link" href="javascript:void(0)" 
                   onclick="${functionName}(${formattedParam}, ${pageData.currentPage - 1})">
                   <i class="bi bi-chevron-left"></i>
                </a>
             </li>`;

    for (let i = 1; i <= pageData.totalPages; i++) {
        if (i === 1 || i === pageData.totalPages || (i >= pageData.currentPage - 2 && i <= pageData.currentPage + 2)) {
            html += `<li class="page-item ${i === pageData.currentPage ? 'active' : ''}">
                        <a class="page-link" href="javascript:void(0)" 
                           onclick="${functionName}(${formattedParam}, ${i})">${i}</a>
                     </li>`;
        } else if (i === pageData.currentPage - 3 || i === pageData.currentPage + 3) {
            html += `<li class="page-item disabled"><span class="page-link">...</span></li>`;
        }
    }

    html += `<li class="page-item ${pageData.currentPage === pageData.totalPages ? 'disabled' : ''}">
                <a class="page-link" href="javascript:void(0)" 
                   onclick="${functionName}(${formattedParam}, ${pageData.currentPage + 1})">
                   <i class="bi bi-chevron-right"></i>
                </a>
             </li>`;

    pagination.innerHTML = html;
}

function updatePaginationInfo(pageData) {
    const start = (pageData.currentPage - 1) * pageData.pageSize + 1;
    const end = Math.min(pageData.currentPage * pageData.pageSize, pageData.totalElements);
    
    document.getElementById('currentRange').innerText = pageData.totalElements === 0 ? "0-0" : `${start}-${end}`;
    document.getElementById('totalElements').innerText = pageData.totalElements;
}