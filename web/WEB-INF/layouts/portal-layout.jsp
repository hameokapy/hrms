<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglib.jsp" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>${pageTitle} - Employee Portal</title>

        <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/images/favicon.png">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">

        <style>
            body { background-color: #f8f9fa; min-height: 100vh; display: flex; flex-direction: column; }
            .main-content { flex: 1; padding-top: 2rem; padding-bottom: 3rem; }
            .card { border: none; box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075); border-radius: 0.75rem; }
            .card-header { background-color: transparent; border-bottom: 1px solid rgba(0,0,0,0.05); padding: 1.25rem; }
        </style>
    </head>
    <body>
        <jsp:include page="portal-header.jsp" />

        <main class="main-content container py-4">
            <jsp:include page="${contentPage}" />
        </main>
        
        <%-- BOOTSTRAP TOAST: THÔNG BÁO Ở GÓC PHẢI CUỐI TRANG --%>
        <div class="toast-container position-fixed bottom-0 end-0 p-3">
          <div id="liveToast" class="toast hide" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="toast-header">
              <strong class="me-auto" id="toastTitle">Notification</strong>
              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="toast-body" id="toastMessage">
              </div>
          </div>
        </div>

        <footer class="footer py-3 bg-white border-top text-center mt-auto">
            <p class="mb-0 text-muted small">&copy; 2026 HRMS Management System</p>
        </footer>

        <script>
            const CTX = '${pageContext.request.contextPath}';
        </script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/js/common.js"></script>
        <c:if test="${not empty pageJS}">
            <script src="${pageContext.request.contextPath}${pageJS}"></script>
        </c:if>
    </body>
</html>