<div class="bg-white border-bottom py-3 mb-4">
    <div class="container-fluid">
        <div class="row align-items-center">
            <div class="col-md-8">
                <h4 class="mb-0 fw-bold text-dark">
                    <span class="text-secondary fw-light">Welcome back,</span> 
                    ${not empty sessionScope.sessionUser.employeeName ? sessionScope.sessionUser.employeeName : 'Administrator'}.
                </h4>
                <p class="text-muted mb-0 small">Here's what's happening with your workforce today.</p>
            </div>
            <div class="col-md-4 text-md-end mt-3 mt-md-0">
                <div class="d-inline-block bg-light rounded-3 px-3 py-2 border">
                    <div class="d-flex align-items-center text-start">
                        <i class="bi bi-calendar3 fs-4 text-primary me-3"></i>
                        <div>
                            <div class="small text-muted text-uppercase" style="font-size: 0.65rem; letter-spacing: 1px;">Current Date</div>
                            <div class="fw-bold" id="currentDateDisplay">Loading...</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="container-fluid">
    <div class="row g-3 mb-4">
        <div class="col-md-4">
            <div class="card border-0 shadow-sm bg-primary text-white">
                <div class="card-body p-4">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <p class="text-uppercase small mb-1 opacity-75 fw-bold">Non-inactive Employees</p>
                            <h2 class="mb-0 fw-bold" id="totalEmployees">0</h2>
                        </div>
                        <div class="bg-white bg-opacity-25 rounded p-3">
                            <i class="bi bi-people fs-1"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card border-0 shadow-sm bg-success text-white">
                <div class="card-body p-4">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <p class="text-uppercase small mb-1 opacity-75 fw-bold">Active Departments</p>
                            <h2 class="mb-0 fw-bold" id="totalDepartments">0</h2>
                        </div>
                        <div class="bg-white bg-opacity-25 rounded p-3">
                            <i class="bi bi-building fs-1"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card border-0 shadow-sm bg-warning text-dark">
                <div class="card-body p-4">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <p class="text-uppercase small mb-1 opacity-75 fw-bold">Pending Leaves</p>
                            <h2 class="mb-0 fw-bold" id="pendingLeaveRequests">0</h2>
                        </div>
                        <div class="bg-dark bg-opacity-10 rounded p-3">
                            <i class="bi bi-calendar-check fs-1"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-lg-8 mb-4">
            <div class="card-body text-center d-flex flex-column justify-content-center align-items-center py-5">
                <div class="mb-4 text-secondary opacity-50">
                    <i class="bi bi-person-workspace" style="font-size: 6rem;"></i>
                </div>

                <h6 class="fw-bold text-dark">Module Under Construction</h6>
                <p class="text-muted small mb-0" style="max-width: 300px;">
                    Analytics, Charts, and Activity Logs are currently being developed. Stay tuned :)
            </div>
        </div>

        <div class="col-lg-4 mb-4">
            <div class="card border-0 shadow-sm">
                <div class="card-header bg-white py-3 border-bottom">
                    <h6 class="mb-0 fw-bold text-danger">
                        <i class="bi bi-shield-exclamation me-2"></i>Operational Alerts
                    </h6>
                </div>
                <div class="list-group list-group-flush" id="alertContainer">
                    <div class="text-center py-5">
                        <div class="spinner-border text-primary spinner-border-sm"></div>
                        <p class="small text-muted mt-2">Checking system integrity...</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>