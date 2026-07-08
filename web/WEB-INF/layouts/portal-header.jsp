<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglib.jsp" %>

<nav class="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm sticky-top py-2">
    <div class="container">
        <a class="navbar-brand d-flex align-items-center mb-0" href="${pageContext.request.contextPath}/portal/dashboard">
            <i class="bi bi-person-workspace text-white fs-4 me-2"></i>
            <span class="fw-bold text-white">HRMS</span>
            <div class="vr mx-3 opacity-25 text-white d-none d-sm-block align-self-center" style="height: 18px;"></div>
            <span class="small fw-light text-white-50 d-none d-sm-block align-self-center" style="font-size: 0.8rem; letter-spacing: 1px;"></span>
        </a>
        <a href="${pageContext.request.contextPath}/home" class="text-white-50 text-decoration-none small fw-light hover-white" style="font-size: 0.8rem; letter-spacing: 1px;">
            <i class="bi bi-house-door me-1"></i>HOME
        </a>
            
        
        <div class="ms-auto d-flex align-items-center">
            <div class="text-white me-3 d-none d-md-block opacity-75 small">
                Welcome, <strong>${sessionScope.sessionUser.employeeName}!</strong>
            </div>

            <div class="dropdown">
                <a href="#" class="text-white text-decoration-none dropdown-toggle d-flex align-items-center" data-bs-toggle="dropdown">
                    <i class="bi bi-person-circle fs-5"></i>
                </a>
                <ul class="dropdown-menu dropdown-menu-end shadow border-0 mt-2">
                    <li><h6 class="dropdown-header text-uppercase small" style="font-size: 0.7rem;">Settings</h6></li>
                    <li>
                        <a class="dropdown-item py-2 d-flex align-items-center" href="#" onclick="showProfileModal()">
                            <i class="bi bi-person-vcard me-2 text-primary"></i>My Profile
                        </a>
                    </li>
                    <li><hr class="dropdown-divider"></li>
                    <li>
                        <a class="dropdown-item py-2 text-danger d-flex align-items-center fw-bold" href="javascript:void(0)" onclick="handleLogout()">
                            <i class="bi bi-box-arrow-right me-2"></i>Logout
                        </a>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</nav>