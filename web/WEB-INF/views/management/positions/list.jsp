<%-- File: /WEB-INF/views/management/positions/list.jsp --%>

<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglib.jsp" %>

<div class="container-fluid">
    <%-- Form tìm kiếm --%>
    <div class="card shadow-sm border-0 mb-4">
        <div class="card-body">
            <form id="searchForm" class="row g-3">
                <div class="col-md-3">
                    <input type="text" name="name" class="form-control form-control-sm" placeholder="Position name">
                </div>
                <div class="col-md-2">
                    <input type="number" name="salaryFrom" class="form-control form-control-sm" placeholder="Min salary">
                </div>
                <div class="col-md-2">
                    <input type="number" name="salaryTo" class="form-control form-control-sm" placeholder="Max salary">
                </div>
                <div class="col-md-2">
                    <select name="status" class="form-select form-select-sm text-center">
                        <option value="">All Status</option>
                        <option value="ACTIVE">Active</option>
                        <option value="INACTIVE">Inactive</option>
                    </select>
                </div>
                <div class="col-md-3 d-flex gap-2">
                    <button type="submit" class="btn btn-primary btn-sm w-100"><i class="bi bi-search"></i> Filter</button>
                    <button type="button" id="btnReset" class="btn btn-outline-secondary btn-sm"><i class="bi bi-arrow-clockwise"></i></button>
                </div>
            </form>
        </div>
    </div>

    <%-- Bảng danh sách --%>
    <div class="card shadow-sm border-0">
        <div class="card-header bg-white py-3 d-flex justify-content-between">
            <h5 class="mb-0 fw-bold text-primary"><i class="bi bi-briefcase"></i> Position List</h5>
            <button class="btn btn-success btn-sm" onclick="openAddModal()">
                <i class="bi bi-plus-lg"></i> Add new position
            </button>
        </div>
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0 text-center" id="posiTable">
                    <thead class="table-light">
                        <tr>
                            <th>ID</th>
                            <th>Position name</th>
                            <th>Base salary level</th>
                            <th>Employee counts</th>
                            <th>Status</th>
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

<%-- Modal create/edit --%>
<div class="modal fade" id="posiModal" tabindex="-1">
    <div class="modal-dialog">
        <form id="posiForm" class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title fw-bold" id="modalTitle">Position info</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <input type="hidden" name="id" id="posiId">
                <div class="mb-3">
                    <label class="form-label fw-medium">Position name</label>
                    <input type="text" name="name" class="form-control" required maxlength="50">
                </div>
                <div class="mb-3">
                    <label class="form-label fw-medium">Base salary level</label>
                    <div class="input-group">
                        <span class="input-group-text">₫</span>
                        <input type="number" name="baseSalaryLevel" class="form-control" required step="0.01">
                    </div>
                </div>
                <div class="mb-3">
                    <label class="form-label fw-medium">Description</label>
                    <textarea name="description" class="form-control" rows="3" maxlength="100"></textarea>
                </div>
            </div>
            <div class="modal-footer bg-light">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <button type="submit" class="btn btn-primary">Confirm</button>
            </div>
        </form>
    </div>
</div>

<%-- Modal detail --%>
<div class="modal fade" id="viewPosiModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content border-0 shadow">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title"><i class="bi bi-info-circle me-2"></i>Position detail</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body p-4">
                <div class="row g-4" id="posiDetailContent">
                    <%-- Nội dung sẽ được đổ bằng JS vào đây --%>
                </div>
            </div>
            <div class="modal-footer bg-light">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

<%-- Support bảng danh sách --%>
<div id="actionTemplate" style="display: none;">
    <div class="btn-group">
        <button class="btn btn-sm btn-outline-primary" onclick="showDetail(ID_PLH)" title="View detail"><i class="bi bi-eye"></i></button>
        <button class="btn btn-sm btn-outline-primary" onclick="openEditModal(ID_PLH)" title="Edit"><i class="bi bi-pencil"></i></button>
        <c:if test="${userPermis.contains(P.DELETE)}">
            <button class="btn btn-sm STATUS_CLASS_PLH" onclick="toggleStatus(ID_PLH, `STATUS_PLH`)" title="STATUS_TITLE_PLH">
                <i class="bi STATUS_ICON_PLH"></i>
            </button>
        </c:if>
    </div>
</div>
