<%-- File: /WEB-INF/views/management/leaves/list.jsp --%>

<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglib.jsp" %>

<div class="container-fluid">
    <div class="card shadow-sm border-0 mb-4">
        <div class="card-header bg-white pt-3 border-bottom-0">
            <h5 class="fw-bold text-primary mb-0">
                <i class="bi bi-calendar2-check-fill"></i> Leave Management
            </h5>
        </div>
        <div class="card-body">
            <form id="leaveSearchForm" class="row g-3 align-items-end">
                
                <c:if test="${userPermis.contains(P.VIEW)}">
                <div class="col-md-4 filter-request">
                    <label class="small fw-bold text-muted text-uppercase" id="lblSearch">Keyword</label>
                    <div class="input-group input-group-sm">
                        <span class="input-group-text bg-light"><i class="bi bi-person-rolodex"></i></span>
                        <input type="text" id="inputSearch" name="keyword" class="form-control" placeholder="Employee/Department code, name">
                    </div>
                </div>
                <div class="col-md-2 filter-request">
                    <label class="small fw-bold text-muted text-uppercase">Type</label>
                    <select name="type" class="form-select form-select-sm">
                        <option value="">All Types</option>
                        <option value="ANNUAL">Annual</option>
                        <option value="SICK">Sick</option>
                        <option value="UNPAID">Unpaid</option>
                    </select>
                </div>
                <div class="col-md-2 filter-request">
                    <label class="small fw-bold text-muted text-uppercase">Status</label>
                    <select name="status" class="form-select form-select-sm">
                        <option value="">All Status</option>
                        <option value="PENDING">Pending</option>
                        <option value="APPROVED">Approved</option>
                        <option value="REJECTED">Rejected</option>
                        <option value="CANCELLED">Cancelled</option>
                    </select>
                </div>
                <div class="col-md-2 filter-request">
                    <label class="small fw-bold text-muted text-uppercase">From date</label>
                    <input type="date" name="fromDate" class="form-control form-control-sm">
                </div>
                <div class="col-md-2 filter-request">
                    <label class="small fw-bold text-muted text-uppercase">To date</label>
                    <input type="date" name="toDate" class="form-control form-control-sm">
                </div>
                </c:if>
                
                <div class="col-md-4 filter-balance d-none">
                    <label class="small fw-bold text-muted text-uppercase">Employee</label>
                    <div class="input-group input-group-sm">
                        <span class="input-group-text bg-light"><i class="bi bi-person-badge"></i></span>
                        <input type="text" name="employee" class="form-control" placeholder="Employee name/code">
                    </div>
                </div>
                <div class="col-md-4 filter-balance d-none">
                    <label class="small fw-bold text-muted text-uppercase">Department</label>
                    <div class="input-group input-group-sm">
                        <span class="input-group-text"><i class="bi bi-building"></i></span>
                        <input type="text" name="department" class="form-control" placeholder="Department name/code">
                    </div>
                </div>
                <div class="col-md-2 filter-balance d-none">
                    <label class="small fw-bold text-primary text-uppercase">Fiscal Year</label>
                    <div class="input-group input-group-sm">
                        <span class="input-group-text border-primary"><i class="bi bi-calendar-check text-primary"></i></span>
                        <select name="year" id="filterYear" class="form-select form-select-sm border-primary text-primary fw-bold">
                        </select>
                    </div>
                </div>

                <div id="filterActions" class="col-md-12 d-flex gap-2">
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
        <div class="card-header bg-white pt-3">
            <div class="d-flex align-items-center justify-content-between mb-3">
                <ul class="nav nav-tabs card-header-tabs border-bottom-0 m-0" id="leaveTab" role="tablist">
                    <c:if test="${userPermis.contains(P.VIEW)}">
                    <li class="nav-item">
                        <button class="nav-link active fw-bold border-bottom-0" id="tab-req" data-bs-toggle="tab" data-bs-target="#requests-panel">
                            <i class="bi bi-envelope-paper me-2"></i>Requests
                        </button>
                    </li>
                    </c:if>
                    <li class="nav-item">
                        <button class="nav-link fw-bold border-bottom-0 ${!userPermis.contains(P.VIEW) ? 'active' : ''}" id="tab-bal" data-bs-toggle="tab" data-bs-target="#balances-panel">
                            <i class="bi bi-wallet2 me-2"></i>Balances
                        </button>
                    </li>
                </ul>
                <c:if test="${userPermis.contains(P.CREATE)}">
                <div class="ms-auto pb-2" id="createRequestAction">
                    <%-- cái hàm onclick của button ở đây là chữa cháy thôi, not cách tối ưu --%>
                    <button class="btn btn-success btn-sm shadow-sm px-3" 
                            onclick="openAddModal('${sessionScope.sessionUser.employeeId}', ${isAdminRole})">
                        <i class="bi bi-plus-lg me-1"></i> Create new request
                    </button>
                </div>    
                </c:if>
            </div>
        </div>

        <div class="card-body p-0">
            <div class="tab-content">
                <c:if test="${userPermis.contains(P.VIEW)}">
                <div class="tab-pane fade show active" id="requests-panel">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0" id="requestTable">
                            <thead class="table-light">
                                <tr>
                                    <th class="ps-4" style="width: 28%;">Employee, Reason & Sent Date</th>
                                    <th class="text-center" style="width: 18%;">Type & Period</th>
                                    <th class="text-center" style="width: 18%;">Duration & Balance</th>
                                    <th class="text-center" style="width: 18%;">Status & Audit</th>
                                    <th class="text-end px-4" style="width: 15%;">Action</th>
                                </tr>
                            </thead>
                            <tbody></tbody>
                        </table>
                    </div>
                </div>
                </c:if>

                <div class="tab-pane fade ${!userPermis.contains(P.VIEW) ? 'show active' : ''}" id="balances-panel">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0" id="balanceTable">
                            <thead class="table-light">
                                <tr>
                                    <th class="ps-4">Employee</th>
                                    <th>Department</th>
                                    <th class="text-center">Annual (Used/Total)</th>
                                    <th class="text-center">Sick (Used/Total)</th>
                                    <th class="text-center px-4">Summary</th>
                                </tr>
                            </thead>
                            <tbody></tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
        <div class="card-footer bg-white border-0 d-flex justify-content-between align-items-center py-3">
            <div class="small text-muted">
                Showing <span id="currentRange">0-0</span> of <span id="totalElements">0</span> records
            </div>
            <nav aria-label="Page navigation">
                <ul class="pagination pagination-sm mb-0" id="pagination">
                    </ul>
            </nav>
        </div>
    </div>
</div>

<%-- MODAL cho create, edit, approve --%>
<div class="modal fade" id="leaveModal" tabindex="-1">
    <div class="modal-dialog">
        <form id="leaveForm" class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title fw-bold" id="leaveModalTitle">Leave Request info</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <input type="hidden" name="id" id="leaveId">
                
                <div class="mb-3">
                    <label class="form-label fw-medium">Employee ID</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="bi bi-hash"></i></span>
                        <input type="number" name="employeeId" id="leaveEmpId" class="form-control" required>
                    </div>
                </div>

                <div class="mb-3">
                    <label class="form-label fw-medium">Leave type</label>
                    <select name="type" id="leaveType" class="form-select" required>
                        <option value="ANNUAL">Annual leave</option>
                        <option value="SICK">Sick leave</option>
                        <option value="UNPAID">Unpaid leave</option>
                    </select>
                </div>

                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-medium">Start date</label>
                        <input type="date" name="startDate" id="leaveStart" class="form-control" required>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-medium">End date</label>
                        <input type="date" name="endDate" id="leaveEnd" class="form-control" required>
                    </div>
                </div>

                <div class="mb-3">
                    <label class="form-label fw-medium">Reason</label>
                    <textarea name="reason" id="leaveReason" class="form-control" rows="3" maxlength="100" placeholder="Enter reason..."></textarea>
                </div>
            </div>
            
            <div class="modal-footer bg-light border-top-0">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <button type="submit" id="btnSubmitForm" class="btn btn-primary">Confirm</button>
                <div id="approveActions" class="d-none d-flex gap-2 flex-grow-1 flex-md-grow-0">
                    <button type="button" class="btn btn-outline-danger flex-fill" onclick="submitApproval('REJECTED')">
                        <i class="bi bi-x-circle me-1"></i> Reject
                    </button>
                    <button type="button" class="btn btn-success flex-fill px-4" onclick="submitApproval('APPROVED')">
                        <i class="bi bi-check-circle me-1"></i> Approve
                    </button>
                </div>
            </div>
        </form>
    </div>
</div>

<%-- SUPPORT ẩn/hiện quyền cho con loadRequest() bên js --%>
<div id="actionTemplate" style="display: none;">
    <div class="btn-group btn-group-sm shadow-sm">
        <c:if test="${userPermis.contains(P.APPROVE)}">
            <button class="btn btn-outline-success btn-approve" onclick="handleApprove(ID_PLH)" title="Approve/Re-audit">
                <i class="bi bi-check-circle"></i>
            </button>
        </c:if>
        <c:if test="${userPermis.contains(P.UPDATE)}">
            <button class="btn btn-outline-primary btn-edit" onclick='prepareEdit(ID_PLH)' title="Edit request">
                <i class="bi bi-pencil-square"></i>
            </button>
        </c:if>
        <c:if test="${userPermis.contains(P.CANCEL)}">
            <button class="btn btn-outline-danger btn-cancel" onclick="handleCancel(ID_PLH)" title="Cancel request">
                <i class="bi bi-x-circle"></i>
            </button>
        </c:if>
    </div>
</div>


