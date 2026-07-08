<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglib.jsp" %>

<nav class="navbar navbar-expand-lg navbar-light bg-white border-bottom shadow-sm sticky-top">
    <div class="container-fluid px-4">
        <div class="d-flex align-items-center">
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb mb-0">
                    <li class="breadcrumb-item"><a href="#" class="text-decoration-none">Management</a></li>
                    <li class="breadcrumb-item active" aria-current="page">${pageTitle}</li>
                </ol>
            </nav>
        </div>

        <div class="ms-auto d-flex align-items-center">
            <div class="dropdown me-3">
                <span class="navbar-text">
                    <i class="bi bi-person-circle me-1"></i>
                    Hi, <strong>${sessionScope.sessionUser.employeeName}!</strong>
                </span>
            </div>
                
            <a href="${pageContext.request.contextPath}/home" class="btn btn-outline-secondary btn-sm me-2 d-flex align-items-center">
                <i class="bi bi-arrow-left-short me-1"></i> Back to Home
            </a>
                
            <button type="button" onclick="handleLogout()" 
                class="btn btn-outline-danger btn-sm d-flex align-items-center">
                <i class="bi bi-box-arrow-right me-1"></i> Logout
            </button>
        </div>
    </div>
</nav>