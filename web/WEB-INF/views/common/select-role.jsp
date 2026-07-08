<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglib.jsp" %>

<!DOCTYPE html>
<html>
<head>
    <title>Select Role to Access HRMS</title>
    
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/images/favicon.png">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .role-card { transition: transform 0.2s; cursor: pointer; text-decoration: none; color: inherit; }
        .role-card:hover { transform: translateY(-5px); box-shadow: 0 10px 20px rgba(0,0,0,0.1); }
    </style>
</head>
<body class="bg-light">
<div class="container vh-100 d-flex flex-column justify-content-center align-items-center">
    <h3 class="mb-4 fw-bold text-secondary">Hello, which role you do want to access HRMS?</h3>
    <div class="row g-4 w-100" style="max-width: 800px;">
        <div class="col-md-6">
            <a href="${pageContext.request.contextPath}/management/dashboard" class="card h-100 border-0 shadow-sm role-card text-center p-4">
                <div class="card-body">
                    <div class="display-4 mb-3 text-primary">💼</div>
                    <h4 class="card-title fw-bold">Management Gate</h4>
                    <p class="text-muted small">For management purpose only</p>
                </div>
            </a>
        </div>
        <div class="col-md-6">
            <a href="${pageContext.request.contextPath}/portal/dashboard" class="card h-100 border-0 shadow-sm role-card text-center p-4">
                <div class="card-body">
                    <div class="display-4 mb-3 text-success">🙋‍️</div>
                    <h4 class="card-title fw-bold">Employee Gate</h4>
                    <p class="text-muted small">View payroll, leaves, personal info,...</p>
                </div>
            </a>
        </div>
    </div>
</div>
</body>
</html>
