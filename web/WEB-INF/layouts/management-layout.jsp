<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglib.jsp" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>${pageTitle} - Management</title>
        
        <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/images/favicon.png">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
        
        <style>
            #wrapper { display: flex; min-height: 100vh; }
            #page-content-wrapper { 
                flex: 1;
                min-width: 0; 
                display: flex;
                flex-direction: column;
                overflow-x: hidden; 
            }
            .main-content-area { flex: 1; padding: 1.5rem; }
            #sidebar-wrapper { width: 300px; transition: transform 0.3s ease; }
            @media (min-width: 992px) {
                #sidebar-wrapper {
                    position: sticky !important;
                    top: 0;
                    height: 100vh;
                    z-index: 1020;
                }
            }
            @media (max-width: 991.98px) {
                #wrapper { display: block; }
                #sidebar-wrapper { position: fixed; height: 100%; }
            }
        </style>
    </head>
    
    <body>
        <div class="d-flex" id="wrapper">
            <jsp:include page="management-sidebar.jsp" />

            <div id="page-content-wrapper">
                <jsp:include page="management-header.jsp" />

                <main class="main-content-area container-fluid p-4">
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

                <footer class="footer py-3 bg-light border-top text-center">
                    <p class="mb-0 text-muted">&copy; 2026 HRMS Management System</p>
                </footer>
            </div>
        </div>

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