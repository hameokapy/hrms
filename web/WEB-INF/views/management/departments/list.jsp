<%-- File: /WEB-INF/views/management/departments/list.jsp --%>

<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglib.jsp" %>

<%-- MAIN LAYOUT CỦA TRANG NÀY --%>
<div class="container-fluid">
    <div class="card shadow-sm border-0 mb-4">
        <div class="card-body">
            <form id="searchForm" class="row g-3">
                <div class="col-md-2">
                    <input type="text" name="code" class="form-control form-control-sm" placeholder="Code">
                </div>
                <div class="col-md-2">
                    <input type="text" name="name" class="form-control form-control-sm" placeholder="Name">
                </div>
                <div class="col-md-2">
                    <input type="text" name="managerName" class="form-control form-control-sm" placeholder="Manager name">
                </div>
                <div class="col-md-2">
                    <input type="text" name="location" class="form-control form-control-sm" placeholder="Location">
                </div>
                <div class="col-md-2">
                    <select name="status" class="form-select form-select-sm text-center">
                        <option value="">All Status</option>
                        <option value="ACTIVE">Active</option>
                        <option value="INACTIVE">Inactive</option>
                    </select>
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
        <div class="card-header bg-white py-3 d-flex justify-content-between">
            <h5 class="mb-0 fw-bold text-primary"><i class="bi bi-building"></i> Department List</h5>
            <c:if test="${userPermis.contains(P.CREATE)}">
                <button class="btn btn-success btn-sm" onclick="openAddModal()">
                    <i class="bi bi-plus-lg"></i> Add new department
                </button>
            </c:if>
        </div>
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0" id="deptTable">
                    <thead class="table-light">
                        <tr>
                            <th>ID</th>
                            <th>Code</th>
                            <th>Department name</th>
                            <th>Location</th>
                            <th>Manager name</th>
                            <th class="text-center">Total employees</th>
                            <th class="text-end px-4">Action</th>
                        </tr>
                    </thead>
                    <tbody></tbody>
                </table>
            </div>
        </div>
        <div class="card-footer bg-white border-0 d-flex justify-content-between align-items-center py-3">
            <div class="small text-muted">
                Showing <span id="currentRange">0-0</span> of <span id="totalElements">0</span> departments
            </div>
            <nav aria-label="Page navigation">
                <ul class="pagination pagination-sm mb-0" id="pagination">
                    </ul>
            </nav>
        </div>
    </div>
</div>

<%-- CỬA SỔ CON: XEM CHI TIẾT 1 DEPARTMENT --%>
<div class="modal fade" id="viewDeptModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content border-0 shadow">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title"><i class="bi bi-info-circle me-2"></i>Department Detail</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <div class="row g-3" id="deptDetailContent">
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

<%-- CỬA SỔ CON: XEM DANH SÁCH NHÂN VIÊN CỦA 1 DEPARTMENT --%>
<div class="modal fade" id="deptEmployeesModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content border-0 shadow">
            <div class="modal-header bg-info text-white">
                <h5 class="modal-title"><i class="bi bi-people me-2"></i>Employees in Department</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body p-0">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0" id="empInDeptTable">
                        <thead class="table-light">
                            <tr>
                                <th>ID</th>
                                <th>Code</th>
                                <th>Full name</th>
                                <th>Position</th>
                                <th>Dept Info</th>
                            </tr>
                        </thead>
                        <tbody></tbody>
                    </table>
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

<%-- CỬA SỔ CON: CHO CREATE, EDIT --%>
<div class="modal fade" id="deptModal" tabindex="-1">
    <div class="modal-dialog">
        <form id="deptForm" class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title fw-bold" id="modalTitle">####</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <input type="hidden" name="id" id="deptId">
                
                <div class="mb-3" id="group-code">
                    <label class="form-label fw-medium">Department code (unchangeable)</label>
                    <input type="text" name="code" class="form-control" maxlength="10">
                </div>
                
                <div class="mb-3" id="group-name">
                    <label class="form-label fw-medium">Department name</label>
                    <input type="text" name="name" class="form-control" maxlength="50">
                </div>
                
                <div class="mb-3" id="group-location">
                    <label class="form-label fw-medium">Location (optional)</label>
                    <input type="text" name="location" class="form-control" maxlength="100">
                </div>
                
                <div class="mb-3" id="group-manager">
                    <label class="form-label fw-medium">Assign manager (search by employee name/code)</label>
                    <div class="position-relative">
                        <input type="text" id="managerSearchInput" class="form-control" placeholder="Type to search employee..." autocomplete="off">
                        <input type="hidden" name="managerId" id="managerIdHidden">

                        <div id="managerSearchResults" class="list-group shadow-sm position-absolute w-100 z-3 d-none" style="top: 100%;">
                            </div>
                    </div>
                    <div id="selectedManagerInfo" class="mt-2 p-2 border rounded bg-light d-none">
                        <small class="text-muted d-block">Selected Manager:</small>
                        <span class="fw-bold text-primary" id="displayManagerName"></span>
                        <button type="button" class="btn btn-sm btn-link text-danger p-0 ms-2" onclick="clearSelectedManager()">Clear</button>
                    </div>
                </div>
            </div>
            <div class="modal-footer bg-light">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <button type="submit" class="btn btn-primary">Confirm</button>
            </div>
        </form>
    </div>
</div>

<%-- BỔ TRỢ renderTable() BÊN list.js --%>
<div id="actionTemplate" style="display: none;">
    <div class="btn-group">
        <button class="btn btn-sm btn-outline-primary" onclick="showDetail(ID_PLH)" title="View details">
            <i class="bi bi-eye"></i>
        </button>
        <button class="btn btn-sm btn-outline-primary" onclick="showEmployees(ID_PLH)" title="View employees">
            <i class="bi bi-people"></i>
        </button>

        <c:if test="${userPermis.contains(P.EDIT)}">
            <button class="btn btn-sm btn-outline-primary" title="Edit general info"
                    onclick="openEditModal(ID_PLH, `NAME_PLH`, `LOC_PLH`)">
                <i class="bi bi-pencil"></i>
            </button>
        </c:if>

        <c:if test="${userPermis.contains(P.ASSIGN)}">
            <button class="btn btn-sm btn-outline-primary" onclick="openAssignManagerModal(ID_PLH)" title="Assign manager">
                <i class="bi bi-person-gear"></i>
            </button>
        </c:if>

        <c:if test="${userPermis.contains(P.DELETE)}">
            <button class="btn btn-sm STATUS_CLASS_PLH" 
                    onclick="toggleStatus(ID_PLH, `STATUS_PLH`)" title="STATUS_TITLE_PLH">
                <i class="bi STATUS_ICON_PLH"></i>
            </button>
        </c:if>
    </div>
</div>