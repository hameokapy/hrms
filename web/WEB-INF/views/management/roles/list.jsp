<%-- File: /WEB-INF/views/management/roles/list.jsp --%>

<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglib.jsp" %>

<div class="container-fluid">
    <div class="card shadow-sm border-0">
        <div class="card-header bg-white pt-3">
            <h5 class="fw-bold text-primary mb-3"><i class="bi bi-shield-check"></i> Security Configuration</h5>
            <ul class="nav nav-tabs card-header-tabs" id="securityTab" role="tablist">
                <li class="nav-item">
                    <button class="nav-link active fw-bold" id="roles-tab" data-bs-toggle="tab" data-bs-target="#roles-panel">
                        <i class="bi bi-person-badge"></i> Roles
                    </button>
                </li>
                <li class="nav-item">
                    <button class="nav-link fw-bold" id="perms-tab" data-bs-toggle="tab" data-bs-target="#perms-panel">
                        <i class="bi bi-key"></i> Permissions
                    </button>
                </li>
            </ul>
        </div>
        
        <div class="card-body p-0">
            <div class="tab-content">
                <div class="tab-pane fade show active p-4" id="roles-panel">
                    <%-- Search form cho role --%>
                    <form id="roleSearchForm" class="row g-2 mb-4">
                        <div class="col-md-4">
                            <input type="text" name="roleName" class="form-control form-control-sm" placeholder="Role name">
                        </div>
                        <div class="col-md-5">
                            <input type="text" name="description" class="form-control form-control-sm" placeholder="Description">
                        </div>
                        <div class="col-md-3 d-flex gap-2">
                            <button type="submit" class="btn btn-primary btn-sm w-100"><i class="bi bi-search"></i> Filter</button>
                            <button type="button" id="btnResetRole" class="btn btn-outline-secondary btn-sm"><i class="bi bi-arrow-clockwise"></i></button>
                        </div>
                    </form>

                    <div class="table-responsive">
                        <table class="table table-hover align-middle" id="roleTable">
                            <thead class="table-light">
                                <tr>
                                    <th width="10%">ID</th>
                                    <th width="25%">Role name</th>
                                    <th width="50%">Description</th>
                                    <th width="15%" class="text-end">Action</th>
                                </tr>
                            </thead>
                            <tbody></tbody>
                        </table>
                    </div>
                </div>

                <div class="tab-pane fade p-4" id="perms-panel">
                    <%-- Search form cho permission --%>
                    <form id="permSearchForm" class="row g-2 mb-4">
                        <div class="col-md-3">
                            <input type="text" name="permissionKey" class="form-control form-control-sm" placeholder="Permission key">
                        </div>
                        <div class="col-md-3">
                            <input type="text" name="roleName" class="form-control form-control-sm" placeholder="Related role name">
                        </div>
                        <div class="col-md-4">
                            <input type="text" name="description" class="form-control form-control-sm" placeholder="Description">
                        </div>
                        <div class="col-md-2 d-flex gap-2">
                            <button type="submit" class="btn btn-primary btn-sm w-100"><i class="bi bi-search"></i> Filter</button>
                            <button type="button" id="btnResetPerm" class="btn btn-outline-secondary btn-sm"><i class="bi bi-arrow-clockwise"></i></button>
                        </div>
                    </form>

                    <div class="table-responsive">
                        <table class="table table-hover align-middle" id="permTable">
                            <thead class="table-light">
                                <tr>
                                    <th width="10%">ID</th>
                                    <th width="30%">Permission key</th>
                                    <th width="45%">Description</th>
                                    <th width="15%" class="text-end">Action</th>
                                </tr>
                            </thead>
                            <tbody></tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="editDescModal" tabindex="-1">
    <div class="modal-dialog">
        <form id="descForm" class="modal-content border-0 shadow">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title fw-bold"><i class="bi bi-pencil-square"></i> Update description</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>  
            <div class="modal-body">
                <input type="hidden" name="id" id="targetId">
                <input type="hidden" id="targetType"> <div class="mb-3">
                    <label class="form-label fw-bold text-muted small text-uppercase">Identification</label>
                    <p id="targetName" class="fw-bold fs-6 text-primary"></p>
                </div>
                <div class="mb-3">
                    <label class="form-label fw-bold text-muted small text-uppercase">Description</label>
                    <textarea name="description" id="targetDesc" class="form-control" rows="4" maxlength="100"></textarea>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <button type="submit" class="btn btn-primary fw-bold">Update now</button>
            </div>
        </form>
    </div>
</div>

<%-- BỔ TRỢ renderTable BÊN list.js --%>
<div id="roleActionTemplate" style="display: none;">
    <div class="text-end">
        <button class="btn btn-sm btn-outline-primary" 
                onclick="openEditDesc(ID_PLH, `NAME_PLH`, `DESC_PLH`, 'role')">
            <i class="bi bi-pencil"></i>
        </button>
    </div>
</div>
<div id="permActionTemplate" style="display: none;">
    <div class="text-end">
        <button class="btn btn-sm btn-outline-primary" 
                onclick="openEditDesc(ID_PLH, `NAME_PLH`, `DESC_PLH`, 'permission')">
            <i class="bi bi-pencil"></i>
        </button>
    </div>
</div>