<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglib.jsp" %>

<input type="hidden" id="currentEmpId" value="${sessionScope.sessionUser.employeeId}">

<div class="row g-4">
    <div class="col-lg-4">
        <div class="card shadow-sm mb-4 border-0">
            <div class="card-body text-center">
                <div class="position-relative d-inline-block mb-3">
                    <i class="bi bi-person-circle display-4 text-secondary"></i>
                </div>
                <h5 class="fw-bold mb-0" id="empName">Loading...</h5>
                <p class="text-muted small mb-2" id="empPos">Position</p>
                
                <div class="w-100 py-2 mb-3 border-top border-bottom d-flex justify-content-between align-items-center">
                    <span class="small text-muted fw-semibold">Employment Status:</span>
                    <span class="fw-bold text-primary text-uppercase" id="empStatus" style="font-size: 0.85rem; letter-spacing: 1px;">
                        <i class="bi bi-shield-check me-1"></i>Official
                    </span>
                </div>

                <button class="btn btn-sm btn-outline-primary rounded-pill w-100" onclick="showProfileModal()">
                    <i class="bi bi-eye-fill me-1"></i> View & Edit Profile
                </button>
            </div>
            <div class="card-footer bg-light border-0 py-3">
                <div class="small text-muted mb-2 d-flex align-items-center">
                    <i class="bi bi-envelope me-2"></i> <span id="empEmail" class="text-dark text-truncate">...</span>
                </div>
                <div class="small text-muted d-flex align-items-center">
                    <i class="bi bi-telephone me-2"></i> <span id="empPhone" class="text-dark">...</span>
                </div>
            </div>
        </div>

        <div class="card shadow-sm mb-4 border-0">
    <div class="card-header bg-white fw-bold text-uppercase small text-secondary border-bottom py-3">
        <i class="bi bi-building me-2 text-primary"></i>My Department
    </div>
    <div class="card-body">
        <div class="mb-4 text-center">
            <span id="deptCode" class="badge bg-primary bg-opacity-10 text-primary mb-2" style="font-size: 0.8rem;">...</span>
            <h5 class="fw-bold mb-0" id="deptName">...</h5>
        </div>

        <div class="d-flex flex-column gap-3 mb-4">
            <div class="d-flex align-items-center">
                <div class="bg-light rounded-circle p-2 me-3">
                    <i class="bi bi-person-badge text-secondary"></i>
                </div>
                <div>
                    <small class="text-muted d-block" style="font-size: 0.75rem;">Manager</small>
                    <span class="fw-bold text-dark" id="deptManager">...</span>
                </div>
            </div>

            <div class="d-flex align-items-center">
                <div class="bg-light rounded-circle p-2 me-3">
                    <i class="bi bi-geo-alt text-danger"></i>
                </div>
                <div>
                    <small class="text-muted d-block" style="font-size: 0.75rem;">Location</small>
                    <span class="small text-dark fw-medium" id="deptLocation">...</span>
                </div>
            </div>
            
            <div class="d-flex align-items-center p-3 rounded-3 bg-primary bg-opacity-10 border border-primary border-opacity-25">
                <div class="bg-primary rounded-circle p-2 me-3">
                    <i class="bi bi-people-fill text-white"></i>
                </div>
                <div>
                    <small class="text-primary fw-bold d-block text-uppercase" style="font-size: 0.7rem; letter-spacing: 0.5px;">Total Employees</small>
                    <h4 class="fw-bold mb-0 text-primary" id="deptCount">0</h4>
                </div>
            </div>

        </div>

        <hr class="my-3 opacity-25">

        <div class="d-grid gap-2">
            <button class="btn btn-primary btn-sm rounded-pill px-3 d-flex align-items-center justify-content-center shadow-sm" onclick="viewDeptEmployees()">
                <i class="bi bi-people me-1"></i> View Dept Employees
            </button>
            
            <button class="btn btn-outline-primary btn-sm rounded-pill px-3 d-flex align-items-center justify-content-center" onclick="viewAllDepts()">
                <i class="bi bi-arrow-right-circle me-1"></i> View All Departments
            </button>
        </div>
    </div>
        </div>
    </div>

    <div class="col-lg-8">
        <div class="row g-3 mb-4 text-center">
            <div class="col-sm-6">
                <div class="card border-0 shadow-sm bg-primary text-white">
                    <div class="card-body">
                        <div class="small opacity-75 fw-bold text-uppercase">Annual Leaves Left</div>
                        <h2 class="fw-bold my-1" id="annualDays">0</h2>
                        <div class="small">Days Available</div>
                    </div>
                </div>
            </div>
            <div class="col-sm-6">
                <div class="card border-0 shadow-sm bg-success text-white">
                    <div class="card-body">
                        <div class="small opacity-75 fw-bold text-uppercase">Sick Leaves Left</div>
                        <h2 class="fw-bold my-1" id="sickDays">0</h2>
                        <div class="small">Days Available</div>
                    </div>
                </div>
            </div>
        </div>

        <div class="card shadow-sm mb-4 border-0">
            <div class="card-header bg-white d-flex justify-content-between align-items-center py-3">
                <h6 class="mb-0 fw-bold text-uppercase small text-secondary">My Leave Requests</h6>
                <button class="btn btn-primary btn-sm rounded-pill px-3" onclick="createNewLeave()">
                    <i class="bi bi-plus-lg me-1"></i> New Request
                </button>
            </div>
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0" id="leaveTable">
                    <thead class="bg-light small">
                        <tr>
                            <th class="ps-4">Duration</th>
                            <th>Type & Reason</th>
                            <th>Status</th>
                            <th class="text-end pe-4">Actions</th>
                        </tr>
                    </thead>
                    <tbody></tbody>
                </table>
            </div>
            <div class="card-footer bg-white border-0 d-flex justify-content-between align-items-center">
                <div class="small text-muted">
                    Showing <span id="currentRange">0-0</span> of <span id="totalElements">0</span> leave requests
                </div>
                <nav aria-label="Page navigation">
                    <ul class="pagination pagination-sm mb-0" id="leavePagination">
                        </ul>
                </nav>
            </div>
        </div>
        <div class="card border-0 shadow-none bg-light">
            <div class="card-body">
                <h6 class="fw-bold text-muted small text-uppercase mb-3">Other Modules (Work in Progress)</h6>
                <div class="row g-2 text-center">
                    <div class="col-4">
                        <div class="p-3 border rounded opacity-50 bg-white">
                            <i class="bi bi-cash-stack d-block mb-1"></i> Payroll
                        </div>
                    </div>
                    <div class="col-4">
                        <div class="p-3 border rounded opacity-50 bg-white">
                            <i class="bi bi-file-earmark-text d-block mb-1"></i> Contract
                        </div>
                    </div>
                    <div class="col-4">
                        <div class="p-3 border rounded opacity-50 bg-white">
                            <i class="bi bi-clock-history d-block mb-1"></i> Attendance
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<%-- MODAL: EDIT/SHOW EMP PROFILE --%>
<div class="modal fade" id="profileModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content border-0 shadow">
            <div class="modal-header bg-light">
                <h5 class="modal-title fw-bold"><i class="bi bi-person-lines-fill me-2"></i>Employee Profile Detail</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body p-4">
                <form id="profileForm">
                    <div class="row g-3 mb-4">
                        <div class="col-md-12">
                            <h6 class="text-primary fw-bold text-uppercase small border-bottom pb-2">System Information</h6>
                        </div>
                        <div class="col-md-4">
                            <label class="small text-muted">Employee Code</label>
                            <input type="text" id="modalEmpCode" class="form-control bg-light" readonly>
                        </div>
                        <div class="col-md-4">
                            <label class="small text-muted">Department</label>
                            <input type="text" id="modalDeptName" class="form-control bg-light" readonly>
                        </div>
                        <div class="col-md-4">
                            <label class="small text-muted">Position</label>
                            <input type="text" id="modalPosName" class="form-control bg-light" readonly>
                        </div>
                    </div>

                    <div class="row g-3 mb-4">
                        <div class="col-md-12">
                            <h6 class="text-primary fw-bold text-uppercase small border-bottom pb-2">Personal Information (Editable)</h6>
                        </div>
                        <div class="col-md-6">
                            <label class="small text-muted">Full Name</label>
                            <input type="text" id="modalFullName" readonly name="fullName" class="form-control bg-light" placeholder="Enter full name">
                        </div>
                        <div class="col-md-6">
                            <label class="small text-muted">Email Address</label>
                            <input type="email" id="modalEmail" readonly name="email" class="form-control bg-light" placeholder="Enter email">
                        </div>
                        <div class="col-md-6">
                            <label class="small fw-bold">Phone Number</label>
                            <input type="text" id="modalPhone" name="phone" class="form-control border-primary-subtle" placeholder="Enter phone number">
                        </div>
                        <div class="col-md-6">
                            <label class="small text-muted">Account Status</label>
                            <input type="text" id="modalStatus" class="form-control bg-light" readonly>
                        </div>
                    </div>

                    <div class="row g-2 bg-light rounded p-3">
                        <div class="col-md-6 small text-muted">
                            <div>Created by: <span id="modalCreatedBy" class="fw-medium"></span></div>
                            <div>Created date: <span id="modalCreatedDate" class="fw-medium"></span></div>
                        </div>
                        <div class="col-md-6 small text-muted text-md-end border-start">
                            <div>Last modified by: <span id="modalModifiedBy" class="fw-medium"></span></div>
                            <div>Last modified date: <span id="modalModifiedDate" class="fw-medium"></span></div>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer border-0">
                <button type="button" class="btn btn-light rounded-pill px-4" data-bs-dismiss="modal">Close</button>
                <button type="button" class="btn btn-primary rounded-pill px-4" onclick="saveProfile()">
                    <i class="bi bi-save me-2"></i>Save Changes
                </button>
            </div>
        </div>
    </div>
</div>

<%-- MODAL: VIEW DEPT EMPLOYEES --%>
<div class="modal fade" id="deptEmployeesModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title"><i class="bi bi-people me-2"></i>Team Members</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body p-0">
                <div class="list-group list-group-flush" id="deptEmpList">
                    </div>
            </div>
            <div class="modal-footer d-flex justify-content-between align-items-center bg-light py-2">
                <div class="small text-muted">
                    Total: <span id="empTotalElements">0</span> employees
                </div>
                <nav>
                    <ul class="pagination pagination-sm mb-0" id="empPagination">
                        </ul>
                </nav>
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

<%-- MODAL: VIEW ALL DEPTS --%>
<div class="modal fade" id="allDeptsModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content border-0 shadow">
            <div class="modal-header bg-light">
                <h5 class="modal-title fw-bold">Company Departments</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <div class="table-responsive">
                    <table class="table table-hover align-middle">
                        <thead class="small text-muted">
                            <tr>
                                <th>Code</th>
                                <th>Department Name</th>
                                <th>Manager</th>
                                <th class="text-center">Staff</th>
                            </tr>
                        </thead>
                        <tbody id="allDeptsTableBody"></tbody>
                    </table>
                </div>
            </div>
            <div class="modal-footer d-flex justify-content-between align-items-center bg-light py-2">
                <div class="small text-muted">
                    Total: <span id="deptTotalElements">0</span> departments
                </div>
                <nav>
                    <ul class="pagination pagination-sm mb-0" id="deptPagination">
                        </ul>
                </nav>
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

<%-- MODAL: CREATE or UPDATE LEAVE REQUEST --%>
<div class="modal fade" id="leaveModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <form id="leaveForm" class="modal-content border-0 shadow-lg">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title fw-bold" id="leaveModalTitle">
                    <i class="bi bi-calendar-plus me-2"></i>New Leave Request
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body p-4">
                <input type="hidden" name="id" id="leaveId">
                <input type="hidden" name="employeeId" id="leaveEmpId" value="${sessionScope.sessionUser.employeeId}">

                <div class="mb-4">
                    <label class="form-label fw-bold small text-uppercase text-muted">Leave Type</label>
                    <div class="row g-2">
                        <div class="col-4">
                            <input type="radio" class="btn-check" name="type" id="typeAnnual" value="ANNUAL" checked>
                            <label class="btn btn-outline-primary w-100 small py-2" for="typeAnnual">Annual</label>
                        </div>
                        <div class="col-4">
                            <input type="radio" class="btn-check" name="type" id="typeSick" value="SICK">
                            <label class="btn btn-outline-success w-100 small py-2" for="typeSick">Sick</label>
                        </div>
                        <div class="col-4">
                            <input type="radio" class="btn-check" name="type" id="typeUnpaid" value="UNPAID">
                            <label class="btn btn-outline-secondary w-100 small py-2" for="typeUnpaid">Unpaid</label>
                        </div>
                    </div>
                </div>

                <div class="row g-3 mb-4">
                    <div class="col-md-6">
                        <label class="form-label fw-bold small text-uppercase text-muted">Start Date</label>
                        <input type="date" name="startDate" id="leaveStart" class="form-control border-primary-subtle" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-bold small text-uppercase text-muted">End Date</label>
                        <input type="date" name="endDate" id="leaveEnd" class="form-control border-primary-subtle" required>
                    </div>
                </div>

                <div class="mb-0">
                    <label class="form-label fw-bold small text-uppercase text-muted">Reason</label>
                    <textarea name="reason" id="leaveReason" class="form-control border-primary-subtle" 
                              rows="3" maxlength="100" placeholder="Why do you need to leave?"></textarea>
                </div>
            </div>
            
            <div class="modal-footer bg-light border-top-0 py-3">
                <button type="button" class="btn btn-light rounded-pill px-4" data-bs-dismiss="modal">Close</button>
                <button type="submit" id="btnSubmitForm" class="btn btn-primary rounded-pill px-4 shadow-sm">
                    <i class="bi bi-send-fill me-2"></i>Submit Request
                </button>
            </div>
        </form>
    </div>
</div>