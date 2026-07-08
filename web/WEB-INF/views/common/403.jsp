<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglib.jsp" %>

<!DOCTYPE html>
<html>
    <head>
        <title>403 - Forbidden</title>
        <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/images/favicon.png">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    </head>
    <body class="bg-light d-flex align-items-center" style="height: 100vh;">
        <div class="container text-center">
            <h1 class="display-1 fw-bold text-danger">403</h1>
            <h2 class="mb-3">Stop!</h2>
            
            <div class="mb-4">
                <p class="text-muted mb-1">Error detail:</p>
                <div class="d-inline-block px-3 py-2 bg-white border border-danger-subtle rounded text-danger fw-medium">
                    ${not empty errorMessage ? errorMessage : 'Not authorized zone.'}
                </div>
            </div>
            
            <div class="d-flex justify-content-center gap-2">
                <button onclick="history.back()" class="btn btn-outline-secondary px-4">Return</button>
                <a href="${pageContext.request.contextPath}/home" class="btn btn-primary px-5">Back to homepage</a>
            </div>
        </div>
    </body>
</html>