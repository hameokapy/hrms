<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglib.jsp" %>

<!DOCTYPE html>
<html>
    <head>
        <title>Login - HRMS System</title>
        
        <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/images/favicon.png">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        <style>
            body { background: #f4f7f6; display: flex; align-items: center; justify-content: center; height: 100vh; margin: 0; }
            .login-container { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); width: 100%; max-width: 400px; }
            .error-msg { color: #842029; background-color: #f8d7da; border: 1px solid #f5c2c7; padding: 10px; margin-bottom: 15px; border-radius: 4px; font-size: 14px; }
            .d-none { display: none !important; }
            ul { margin-bottom: 0; padding-left: 20px; }
        </style>
    </head>
    <body>
        <div class="login-container">
            <h2 class="text-center mb-4">Login to HRMS</h2>

            <c:if test="${not empty param.msg}">
                <div class="error-msg">
                    <c:choose>
                        <c:when test="${param.msg == 'no_permission'}">You have no permission to access the HRMS!</c:when>
                        <c:when test="${param.msg == 'invalid_role'}">You don't have valid role to access the HRMS!</c:when>
                        <c:otherwise>${param.msg}</c:otherwise>
                    </c:choose>
                </div>
            </c:if>

            <div id="apiError" class="error-msg d-none">
                <div id="errorTitle" class="fw-bold"></div>
                <ul id="errorList"></ul>
            </div>

            <form id="loginForm">
                <div class="mb-3">
                    <label class="form-label small fw-bold text-muted text-uppercase">Username</label>
                    <input type="text" name="username" class="form-control" required>
                </div>
                <div class="mb-3">
                    <label class="form-label small fw-bold text-muted text-uppercase">Password</label>
                    <div class="input-group">
                        <input type="password" name="password" id="loginPassword" class="form-control" required>
                        <button class="btn btn-outline-secondary" type="button" id="togglePassword">
                            <i class="bi bi-eye-slash" id="toggleIcon"></i>
                        </button>
                    </div>
                </div>
                <button type="submit" class="btn btn-primary w-100 fw-bold">Login</button>
            </form>
        </div>
            
        <script>
            const CTX = '${pageContext.request.contextPath}';

            const resetErrors = () => {
                const apiError = document.getElementById('apiError');
                const errorList = document.getElementById('errorList');
                const errorTitle = document.getElementById('errorTitle');
                
                const urlMsg = document.querySelector('.error-msg:not(#apiError)');

                apiError.classList.add('d-none');
                errorList.innerHTML = ''; 
                errorTitle.textContent = '';
                
                if (urlMsg) {
                    urlMsg.classList.add('d-none');
                }
            };
            
            document.getElementById('togglePassword').addEventListener('click', function () {
                const passwordInput = document.getElementById('loginPassword');
                const icon = document.getElementById('toggleIcon');

                const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
                passwordInput.setAttribute('type', type);

                icon.classList.toggle('bi-eye');
                icon.classList.toggle('bi-eye-slash');
            });

            document.getElementById('loginForm').addEventListener('submit', async (e) => {
                e.preventDefault();
                resetErrors();

                const body = Object.fromEntries(new FormData(e.target).entries());

                try {
                    const response = await fetch(CTX + '/api/auth/login', {
                        method: 'POST',
                        headers: {'Content-Type': 'application/json'},
                        body: JSON.stringify(body)
                    });

                    const res = await response.json();

                    if (res.status === 200) {
                        window.location.href = CTX + '/home';
                        return;
                    }

                    const apiError = document.getElementById('apiError');
                    const errorTitle = document.getElementById('errorTitle');
                    const errorList = document.getElementById('errorList');

                    if (res.status === 400 && res.data) {
                        errorTitle.textContent = res.message; 
                        Object.keys(res.data).forEach(field => {
                            const li = document.createElement('li');
                            li.textContent = res.data[field];
                            errorList.appendChild(li);
                        });
                        errorList.classList.remove('d-none');
                        apiError.classList.remove('d-none');
                    } else {
                        errorTitle.textContent = res.message || 'Undefined error.';
                        errorList.classList.add('d-none'); 
                        apiError.classList.remove('d-none');
                    }

                } catch (error) {
                    console.error("FE Error login");
                    document.getElementById('errorTitle').textContent = 'Oops, connecting server failed!';
                    document.getElementById('apiError').classList.remove('d-none');
                }
            });
        </script>
    </body>
</html>