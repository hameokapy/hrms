<%-- File: /WEB-INF/views/management/employees/list.jsp --%>

<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglib.jsp" %>

<div class="container-fluid">
    <div class="card shadow-sm border-0 mb-4">
        <div class="card-body">
            <form id="searchForm" class="row g-3">
                <div class="col-md-3">
                    <input type="text" name="employee" class="form-control form-control-sm" placeholder="Employee name/code">
                </div>
                <div class="col-md-3">
                    <input type="text" name="department" class="form-control form-control-sm" placeholder="Department name/code">
                </div>
                <div class="col-md-2">
                    <input type="text" name="position" class="form-control form-control-sm" placeholder="Position name">
                </div>
                <div class="col-md-2">
                    <div class="dropdown" id="statusDropdown">
                        <button class="btn btn-sm btn-white border w-100 dropdown-toggle text-start d-flex justify-content-between align-items-center" 
                                type="button" data-bs-toggle="dropdown" data-bs-auto-close="outside" aria-expanded="false">
                            <span class="text-truncate">Status</span>
                        </button>
                        <div class="dropdown-menu p-3 shadow-sm" style="min-width: 180px;">
                            <div class="form-check mb-2">
                                <input class="form-check-input status-checkbox" type="checkbox" name="status" value="ACTIVE" id="stActive">
                                <label class="form-check-label small" for="stActive">Active</label>
                            </div>
                            <div class="form-check mb-2">
                                <input class="form-check-input status-checkbox" type="checkbox" name="status" value="ON_LEAVE" id="stLeave">
                                <label class="form-check-label small" for="stLeave">On Leave</label>
                            </div>
                            <div class="form-check mb-2">
                                <input class="form-check-input status-checkbox" type="checkbox" name="status" value="PENDING" id="stPending">
                                <label class="form-check-label small" for="stPending">Pending</label>
                            </div>
                            <div class="form-check">
                                <input class="form-check-input status-checkbox" type="checkbox" name="status" value="INACTIVE" id="stInactive">
                                <label class="form-check-label small" for="stInactive">Inactive</label>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-2 d-flex gap-2">
                    <button type="submit" class="btn btn-primary btn-sm w-100">
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
            <h5 class="mb-0 fw-bold text-primary"><i class="bi bi-people"></i> Employee List</h5>
            <c:if test="${userPermis.contains(P.ASSIGN)}">
                <div id="bulkActions" class="d-none animate__animated animate__fadeIn align-items-center gap-2">
                    <div class="border-start ms-2 ps-3" style="height: 20px;"></div>
                    <button class="btn btn-outline-primary btn-sm" onclick="openBulkModal('department')">
                        <i class="bi bi-building"></i> Assign dept in bulk
                    </button>
                    <button class="btn btn-outline-primary btn-sm" onclick="openBulkModal('position')">
                        <i class="bi bi-person-badge"></i> Assign posi in bulk
                    </button>
                    <span class="small text-muted selected-count align-self-center ms-1"></span>
                </div>
            </c:if>
            <c:if test="${userPermis.contains(P.CREATE)}">
                <button class="btn btn-success btn-sm" onclick="openAddModal()">
                    <i class="bi bi-person-plus"></i> Add new employee
                </button>
            </c:if>
        </div>
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0 text-center" id="empTable">
                    <thead class="table-light">
                        <tr>
                            <th width="40" class="text-center">
                                <input type="checkbox" class="form-check-input shadow-none" id="selectAll">
                            </th>
                            <th width="80">ID</th>
                            <th width="150">Employee code</th>
                            <th>Fullname</th>
                            <th>Department</th>
                            <th>Position</th>
                            <th class="text-end px-4">Actions</th>
                        </tr>
                    </thead>
                    <tbody></tbody>
                </table>
            </div>
        </div>
        <div class="card-footer bg-white border-0 d-flex justify-content-between align-items-center py-3">
            <div class="small text-muted">
                Showing <span id="currentRange">0-0</span> of <span id="totalElements">0</span> employees
            </div>
            <nav aria-label="Page navigation">
                <ul class="pagination pagination-sm mb-0" id="pagination">
                    </ul>
            </nav>
        </div>
    </div>
</div>

<div class="modal fade" id="empModal" tabindex="-1">
    <div class="modal-dialog">
        <form id="empForm" class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title fw-bold" id="modalTitle">####</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <input type="hidden" name="id" id="empId">
                
                <div id="group-basic">
                    <div class="row">
                        <div class="col-md-12 mb-3">
                            <label class="form-label fw-medium small">Full name</label>
                            <input type="text" name="fullName" class="form-control form-control-sm" required>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-medium small">Email address</label>
                            <input type="email" name="email" class="form-control form-control-sm" required>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-medium small">Phone number</label>
                            <input type="text" name="phone" class="form-control form-control-sm" required>
                        </div>
                    </div>
                </div>

                <div id="group-assignment">
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-medium small">Department</label>
                            <select name="departmentId" id="selDepartment" class="form-select form-select-sm" required>
                                <option value="">Select Department</option>
                                </select>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-medium small">Position</label>
                            <select name="positionId" id="selPosition" class="form-select form-select-sm" required>
                                <option value="">Select Position</option>
                                </select>
                        </div>
                    </div>
                </div>

                <div id="group-status" style="display: none;">
                    <div class="mb-3">
                        <label class="form-label fw-medium">Select status</label>
                        <select name="status" class="form-select text-center">
                            <option value="ACTIVE">ACTIVE</option>
                            <option value="ON_LEAVE">ON_LEAVE</option>
                            <option value="PENDING">PENDING</option>
                            <option value="INACTIVE">INACTIVE</option>
                        </select>
                    </div>
                </div>
            </div>
            <div class="modal-footer bg-light">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <div id="footer-standard">
                    <button type="submit" class="btn btn-primary">Confirm</button>
                </div>
                <div id="footer-assign" class="d-none gap-2">
                    <button type="submit" class="btn btn-outline-primary" data-action="department">
                        <i class="bi bi-building"></i> Update Dept
                    </button>
                    <button type="submit" class="btn btn-outline-primary" data-action="position">
                        <i class="bi bi-person-badge"></i> Update Posi
                    </button>
                    <button type="submit" class="btn btn-primary" data-action="both">
                        <i class="bi bi-check-all"></i> Update Both
                    </button>
                </div>
            </div>
        </form>
    </div>
</div>

<div class="modal fade" id="detailModal" tabindex="-1">
    <div class="modal-dialog modal-lg"> <div class="modal-content border-0 shadow-lg">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title fw-bold"><i class="bi bi-person-badge me-2"></i>Employee Details</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body p-4" id="detailContent">
                </div>
            <div class="modal-footer bg-light">
                <button type="button" class="btn btn-secondary px-4" data-bs-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

<div id="actionTemplate" style="display: none;">
    <div class="btn-group">
        <button class="btn btn-sm btn-outline-primary" onclick="showDetail(ID_PLH)" title="View details">
            <i class="bi bi-eye"></i>
        </button>
        
        <c:if test="${userPermis.contains(P.EDIT)}">
            <button class="btn btn-sm btn-outline-primary" onclick="openEditModal(ID_PLH)" title="Edit info">
                <i class="bi bi-pencil"></i>
            </button>
        </c:if>

        <c:if test="${userPermis.contains(P.ASSIGN)}">
            <button class="btn btn-sm btn-outline-primary" onclick="openAssignModal(ID_PLH)" title="Move dept/pos">
                <i class="bi bi-briefcase"></i>
            </button>
        </c:if>

        <c:if test="${userPermis.contains(P.DELETE)}">
            <button class="btn btn-sm btn-outline-danger" onclick="openStatusModal(ID_PLH, `STATUS_PLH`)" title="Change status">
                <i class="bi bi-shield-exclamation"></i>
            </button>
        </c:if>
    </div>
</div>
