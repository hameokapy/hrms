<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglib.jsp" %>

<div class="container-fluid d-flex align-items-center justify-content-center" style="min-height: 80vh;">
    <div class="text-center px-4">
        <div class="display-1 text-primary opacity-25 mb-4">
            <i class="bi bi-cone-striped"></i>
        </div>
        
        <h1 class="fw-bold text-dark">Feature Under Construction</h1>
        <p class="text-muted mb-4 px-lg-5">
            We’re hard at work building this feature.<br>
            Stay tuned - coming soon!
        </p>

        <a href="${pageContext.request.contextPath}/management/dashboard" class="btn btn-primary px-4 py-2 shadow-sm">
            <i class="bi bi-house-door me-2"></i> Back to Dashboard
        </a>
        
        <div class="mt-5 pt-4 border-top border-light">
            <div class="spinner-border text-primary spinner-border-sm me-2" role="status"></div>
            <small class="text-muted text-uppercase tracking-wider">Estimated completion: Coming Soon</small>
        </div>
    </div>
</div>
