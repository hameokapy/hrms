<%-- File: /WEB-INF/views/management/users/list.jsp --%>

<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglib.jsp" %>

<div class="container-fluid">
    <div class="card shadow-sm border-0 mb-4">
        <div class="card-body">
            <form id="searchForm" class="row g-2">
                <div class="col-md-4">
                    <input type="text" name="keyword" class="form-control form-control-sm" placeholder="Username, Employee name/code">
                </div>
                <div class="col-md-2">
                    <select name="deptId" id="filterDept" class="form-select form-select-sm text-center">
                        <option value="">All Departments</option>
                    </select>
                </div>
                <div class="col-md-2">
                    <select name="posiId" id="filterPosi" class="form-select form-select-sm text-center">
                        <option value="">All Positions</option>
                    </select>
                </div>
                <div class="col-md-2">
                    <select name="roleId" id="filterRole" class="form-select form-select-sm text-center">
                        <option value="">All Roles</option>
                    </select>
                </div>
                <div class="col-md-2">
                    <select name="isActive" class="form-select form-select-sm text-center">
                        <option value="">All Status</option>
                        <option value="true">Active</option>
                        <option value="false">Inactive</option>
                    </select>
                </div>
                <div class="col-md-12 d-flex gap-1">
                    <button type="submit" class="btn btn-primary btn-sm flex-grow-1">
                        <i class="bi bi-search"></i> Filter
                    </button>
                    <button type="button" id="btnReset" class="btn btn-outline-secondary btn-sm">
                        <i class="bi bi-arrow-clockwise"></i>
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="card shadow-sm border-0">
        <div class="card-header bg-white py-3 d-flex justify-content-between align-items-center">
            <h5 class="mb-0 fw-bold text-primary"><i class="bi bi-person-badge"></i> User Accounts</h5>
            <button class="btn btn-success btn-sm" onclick="openCreateUserModal()">
                <i class="bi bi-person-plus"></i> Create new account
            </button>
        </div>
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0" id="userTable">
                    <thead class="table-light">
                        <tr>
                            <th>ID</th>
                            <th>Username</th>
                            <th>Linked employee</th>
                            <th>Roles</th>
                            <th class="text-center">Status</th>
                            <th class="text-end px-4">Action</th>
                        </tr>
                    </thead>
                    <tbody></tbody>
                </table>
            </div>
        </div>
        <div class="card-footer bg-white border-0 d-flex justify-content-between align-items-center py-3">
            <div class="small text-muted">
                Showing <span id="currentRange">0-0</span> of <span id="totalElements">0</span> positions
            </div>
            <nav aria-label="Page navigation">
                <ul class="pagination pagination-sm mb-0" id="pagination">
                    </ul>
            </nav>
        </div>
    </div>
</div>

<%-- MODAL: VIEW DETAIL --%>
<div class="modal fade" id="userDetailModal" tabindex="-1">
    <div class="modal-dialog modal-lg"> <div class="modal-content border-0 shadow">
            <div class="modal-header bg-primary text-white py-3">
                <h5 class="modal-title fw-bold"><i class="bi bi-info-circle-fill me-2"></i> User account details</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body p-4">
                <div id="userDetailContent" class="row g-3">
                    </div>
            </div>
            <div class="modal-footer border-0 bg-light py-2">
                <button type="button" class="btn btn-secondary px-4" data-bs-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

<%-- MODAL: CREATE USER --%>
<div class="modal fade" id="createUserModal" tabindex="-1">
    <div class="modal-dialog">
        <form id="createUserForm" class="modal-content border-0 shadow">
            <div class="modal-header bg-success text-white">
                <h5 class="modal-title fw-bold"><i class="bi bi-person-plus-fill"></i> Create new account</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <div class="mb-3">
                    <label class="form-label small fw-bold text-muted text-uppercase">Username</label>
                    <input type="text" name="username" class="form-control" required minlength="4" placeholder="Enter username...">
                </div>

                <div class="mb-3 position-relative">
                    <label class="form-label small fw-bold text-muted text-uppercase">Link employee (optional)</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="bi bi-search"></i></span>
                        <input type="text" id="ceSearchInput" class="form-control" placeholder="Type employee name/code to search...">
                    </div>
                    <input type="hidden" name="employeeId" id="ceEmployeeId">
                    <div id="ceResults" class="list-group position-absolute w-100 shadow-sm d-none" style="z-index: 1050; max-height: 200px; overflow-y: auto;"></div>
                    <div class="form-text">Input Employee if you want to link now.</div>
                </div>

                <div class="mb-3">
                    <label class="form-label small fw-bold text-muted text-uppercase">Password</label>
                    <div class="input-group">
                        <input type="password" name="password" id="createPassInput" class="form-control" required minlength="6">
                    </div>
                </div>
            </div>
            <div class="modal-footer border-0 bg-light">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <button type="submit" class="btn btn-success fw-bold">Create user</button>
            </div>
        </form>
    </div>
</div>

<%-- MODAL: BIND EMPLOYEE --%>
<div class="modal fade" id="bindEmployeeModal" tabindex="-1">
    <div class="modal-dialog">
        <form id="bindEmployeeForm" class="modal-content border-0 shadow">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title fw-bold"><i class="bi bi-link-45deg"></i> Link to Employee</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <input type="hidden" name="userId" id="beUserId">
                
                <div class="mb-4 text-center">
                    <label class="form-label small fw-bold text-muted text-uppercase d-block">Target User Account</label>
                    <span id="beUsername" class="badge bg-primary-subtle text-primary fs-6 px-3"></span>
                </div>

                <div class="mb-3 position-relative">
                    <label class="form-label small fw-bold text-muted text-uppercase">Find Employee</label>
                    <div class="input-group">
                        <span class="input-group-text bg-light"><i class="bi bi-search"></i></span>
                        <input type="text" id="beSearchInput" class="form-control" placeholder="Type employee name/code to search..." required>
                    </div>
                    <input type="hidden" name="employeeId" id="beEmployeeId">
                    <div id="beResults" class="list-group position-absolute w-100 shadow-sm d-none" style="z-index: 1050; max-height: 200px; overflow-y: auto;"></div>
                </div>
            </div>
            <div class="modal-footer border-0 bg-light">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <button type="submit" class="btn btn-primary fw-bold">Link now</button>
            </div>
        </form>
    </div>
</div>

<%-- MODAL: RESET PASSWORD --%>
<div class="modal fade" id="resetPassModal" tabindex="-1">
    <div class="modal-dialog">
        <form id="resetPassForm" class="modal-content border-0 shadow">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title fw-bold"><i class="bi bi-key-fill"></i> Reset password</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <input type="hidden" name="userId" id="rpUserId">
                <div class="mb-3">
                    <label class="form-label small fw-bold text-muted text-uppercase">Account</label>
                    <p id="rpUsername" class="fw-bold text-primary fs-5"></p>
                </div>
                <div class="mb-3">
                    <label class="form-label small fw-bold text-muted text-uppercase">New password</label>
                    <div class="input-group">
                        <input type="password" name="newPassword" id="newPassInput" class="form-control" required minlength="6">
                        <button class="btn btn-outline-secondary" type="button" id="togglePassword">
                            <i class="bi bi-eye-slash" id="toggleIcon"></i>
                        </button>
                    </div>
                </div>
            </div>
            <div class="modal-footer border-0 bg-light">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <button type="submit" class="btn btn-primary fw-bold">Update password</button>
            </div>
        </form>
    </div>
</div>

<%-- MODAL: ASSIGN ROLES --%>
<div class="modal fade" id="assignRoleModal" tabindex="-1">
    <div class="modal-dialog">
        <form id="assignRoleForm" class="modal-content border-0 shadow">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title fw-bold"><i class="bi bi-shield-lock-fill"></i> Assign roles</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <input type="hidden" name="userId" id="arUserId">
                <div class="mb-3">
                    <label class="form-label small fw-bold text-muted text-uppercase">User</label>
                    <p id="arUsername" class="fw-bold text-primary fs-5"></p>
                </div>
                <label class="form-label small fw-bold text-muted text-uppercase">Permissions</label>
                <div id="roleCheckboxList" class="border rounded p-3 bg-light" style="max-height: 250px; overflow-y: auto;"></div>
            </div>
            <div class="modal-footer border-0 bg-light">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <button type="submit" class="btn btn-primary fw-bold">Save changes</button>
            </div>
        </form>
    </div>
</div>

<%-- ACTION TEMPLATE CHO JS --%>
<div id="actionTemplate" style="display: none;">
    <div class="btn-group">
        <button class="btn btn-sm btn-outline-primary" onclick="openUserDetail(ID_PLH)" title="View detail">
            <i class="bi bi-eye"></i>
        </button>
        <button class="btn btn-sm btn-outline-primary" onclick="openAssignRole(ID_PLH, `NAME_PLH`)" title="Assign roles">
            <i class="bi bi-shield-lock"></i>
        </button>
        <button class="btn btn-sm btn-outline-primary" onclick="openBindEmployee(ID_PLH, `NAME_PLH`)" title="Link employee">
            <i class="bi bi-link-45deg"></i>
        </button>
        <button class="btn btn-sm btn-outline-primary" onclick="openResetPass(ID_PLH, `NAME_PLH`)" title="Reset password">
            <i class="bi bi-key"></i>
        </button>
        <button class="btn btn-sm STATUS_CLASS_PLH" onclick="toggleStatus(ID_PLH, IS_ACTIVE_PLH)" title="STATUS_TITLE_PLH">
            <i class="bi STATUS_ICON_PLH"></i>
        </button>
    </div>
</div>
