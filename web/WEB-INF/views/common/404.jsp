<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglib.jsp" %>

<!DOCTYPE html>
<html>
    <head>
        <title>404 - Page Not Found</title>
        <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/images/favicon.png">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
        <style>
            body { background-color: #f8f9fa; }
            .error-code { font-size: 10rem; line-height: 1; color: #6c757d; opacity: 0.5; }
            .error-icon { font-size: 5rem; color: #0d6efd; }
        </style>
    </head>
    <body class="d-flex align-items-center" style="height: 100vh;">
        <div class="container text-center">
            <div class="row justify-content-center">
                <div class="col-md-6">
                    <div class="error-icon mb-4">
                        <i class="bi bi-geo-alt-fill"></i>
                    </div>
                    
                    <h1 class="error-code fw-bold mb-0">404</h1>
                    <h2 class="h3 mb-3 fw-bold">Oops! Page not found</h2>
                    
                    <p class="text-muted mb-4 px-lg-5">
                        Seems like the link you're trying to access is broken or does not exist.
                    </p>
                    
                    <div class="d-flex justify-content-center gap-3 mt-2">
                        <button onclick="history.back()" class="btn btn-outline-secondary px-4 py-2">
                            <i class="bi bi-arrow-left me-2"></i>Return
                        </button>
                        <a href="${pageContext.request.contextPath}/home" class="btn btn-primary px-4 py-2">
                            <i class="bi bi-house-door me-2"></i>Back to homepage
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
