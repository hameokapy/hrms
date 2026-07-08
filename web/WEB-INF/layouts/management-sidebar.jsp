<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglib.jsp" %>

<button class="btn btn-dark d-lg-none m-2" type="button" data-bs-toggle="offcanvas" data-bs-target="#sidebar-wrapper">
    <i class="bi bi-list"></i>
</button>

<aside id="sidebar-wrapper" class="offcanvas-lg offcanvas-start bg-dark text-white shadow" tabindex="-1" style="width: 300px; border: none;">
    
    <div class="sidebar-heading p-4 fs-5 fw-bold border-bottom border-secondary text-center text-uppercase tracking-wider">
        <i class="bi bi-shield-lock-fill me-2"></i>HRMS Admin
    </div>
    
    <div class="list-group list-group-flush pt-2 overflow-y-auto" style="height: calc(100vh - 85px);">
        
        <div class="px-4 py-2 mt-2"><small class="text-secondary fw-semibold text-uppercase" style="font-size: 0.7rem;">Main Control</small></div>
        
        <a href="${pageContext.request.contextPath}/management/dashboard" 
           class="list-group-item list-group-item-action bg-dark text-white border-0 py-2 px-4 d-flex align-items-center 
           ${activeMenu == 'dashboard' ? 'active bg-primary border-start border-4' : ''}">
           <i class="bi bi-grid-1x2-fill me-3"></i> Dashboard
        </a>

        <div class="px-4 py-2 mt-3"><small class="text-secondary fw-semibold text-uppercase" style="font-size: 0.7rem;">Organization</small></div>
        
        <a href="${pageContext.request.contextPath}/management/departments" 
           class="list-group-item list-group-item-action bg-dark text-white border-0 py-2 px-4 d-flex align-items-center 
           ${activeMenu == 'department' ? 'active bg-primary border-start border-4' : ''}">
           <i class="bi bi-building-fill me-3"></i> Departments
        </a>
           
        <c:if test="${userPermis.contains(P.VIEW_POSI)}">
            <a href="${pageContext.request.contextPath}/management/positions" 
               class="list-group-item list-group-item-action bg-dark text-white border-0 py-2 px-4 d-flex align-items-center 
               ${activeMenu == 'position' ? 'active bg-primary border-start border-4' : ''}">
               <i class="bi bi-briefcase-fill me-3"></i> Positions
            </a>
        </c:if>
           
        <div class="px-4 py-2 mt-3"><small class="text-secondary fw-semibold text-uppercase" style="font-size: 0.7rem;">Resources</small></div>

        <a href="${pageContext.request.contextPath}/management/employees" 
           class="list-group-item list-group-item-action bg-dark text-white border-0 py-2 px-4 d-flex align-items-center 
           ${activeMenu == 'employee' ? 'active bg-primary border-start border-4' : ''}">
           <i class="bi bi-people-fill me-3"></i> Employees
        </a>
        
        <a href="${pageContext.request.contextPath}/management/leaves" 
           class="list-group-item list-group-item-action bg-dark text-white border-0 py-2 px-4 d-flex align-items-center 
           ${activeMenu == 'leave' ? 'active bg-primary border-start border-4' : ''}">
           <i class="bi bi-calendar-check-fill me-3"></i> Leave Requests
        </a>

        <c:if test="${userPermis.contains(P.VIEW_USER) || userPermis.contains(P.VIEW_ROLE)}">
            <div class="px-4 py-2 mt-3"><small class="text-secondary fw-semibold text-uppercase" style="font-size: 0.7rem;">Security</small></div>

            <a href="${pageContext.request.contextPath}/management/users" 
               class="list-group-item list-group-item-action bg-dark text-white border-0 py-2 px-4 d-flex align-items-center 
               ${activeMenu == 'user' ? 'active bg-primary border-start border-4' : ''}">
               <i class="bi bi-person-fill-lock me-3"></i> User Accounts
            </a>
        
            <a href="${pageContext.request.contextPath}/management/roles" 
               class="list-group-item list-group-item-action bg-dark text-white border-0 py-2 px-4 d-flex align-items-center 
               ${activeMenu == 'role' ? 'active bg-primary border-start border-4' : ''}">
               <i class="bi bi-key-fill me-3"></i> Roles & Permissions
            </a>
        </c:if>

        <div class="px-4 py-2 mt-3"><small class="text-secondary fw-semibold text-uppercase" style="font-size: 0.7rem;">Others</small></div>
        
        <a href="${pageContext.request.contextPath}/management/under-construction" 
           class="list-group-item list-group-item-action bg-dark text-white border-0 py-2 px-4 d-flex align-items-center opacity-50 small">
           <i class="bi bi-file-earmark-text me-3"></i> Contracts
        </a>
        
        <a href="${pageContext.request.contextPath}/management/under-construction" 
           class="list-group-item list-group-item-action bg-dark text-white border-0 py-2 px-4 d-flex align-items-center opacity-50 small">
           <i class="bi bi-clock-fill me-3"></i> Attendance
        </a>
           
        <a href="${pageContext.request.contextPath}/management/under-construction" 
           class="list-group-item list-group-item-action bg-dark text-white border-0 py-2 px-4 d-flex align-items-center opacity-50 small">
           <i class="bi bi-cash-stack me-3"></i> Payroll & Salary
        </a>
    </div>
</aside>