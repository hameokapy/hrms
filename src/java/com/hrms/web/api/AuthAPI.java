
package com.hrms.web.api;

import com.hrms.core.security.SecurityContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import com.hrms.model.dto.common.UserSessionDTO;
import com.hrms.model.dto.response.LoginResponseDTO;
import com.hrms.service.AuthService;
import com.hrms.service.impl.AuthServiceImpl;
import com.hrms.utils.DataMapper;
import com.hrms.utils.RequestParser;

@WebServlet(name="AuthAPI", urlPatterns={"/api/auth/*"})
public class AuthAPI extends BaseServlet {
    
    /*
        POST /api/auth/login: Đăng nhập
        POST /api/auth/logout: Thoát
    */

    private final AuthService authService = new AuthServiceImpl();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        try {
            switch (pathInfo) {
                case "/login" -> {
                    Map<String, Object> body = getRequestBody(request, Map.class);
                    RequestParser rp = new RequestParser(body);
                    String username = rp.getUsername("username", true);
                    String password = rp.getPassword("password", true);
                    rp.validate();
                    LoginResponseDTO result = authService.login(username, password);
                    UserSessionDTO sessionObj = DataMapper.mapObjectToObject(result, UserSessionDTO.class);
                    request.getSession(true).setAttribute("sessionUser", sessionObj);
                    sendSuccess(response, result);
                }
                case "/logout" -> {
                    // Ko cần check session null vì thằng Authen Filter ko cho r
                    request.getSession(false).invalidate();
                    // Xóa cái ThreadLocal này vì "explicit is better than implicit", chứ ở TransactionManager cuối request tự đc xóa r
                    SecurityContext.clear();
                    sendSuccess(response, "Logged out successfully.");
                }
            }
        } catch (Exception e) {
            handleException(response, e);
        }
    }
}
