
package com.hrms.utils;

import com.hrms.core.exception.BaseException;
import com.hrms.core.exception.ValidationException;
import com.hrms.core.exception.security.AuthenticationException;
import com.hrms.core.exception.security.AuthorizationException;
import com.hrms.core.exception.technical.TechnicalException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.hrms.model.dto.common.ResponseDTO;

public class ResponseHelper {
    private ResponseHelper() {}
    
    public static void sendResponse(HttpServletResponse response, ResponseDTO<?> responseObj) throws IOException {
        response.setStatus(responseObj.getStatus());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().print(GsonHelper.toJson(responseObj));
        response.getWriter().flush();
    }
    
    public static void sendSuccess(HttpServletResponse response, Object data) throws IOException {
        sendResponse(response, new ResponseDTO<>(200, "Success.", data));
    }
    
    public static void sendSuccess(HttpServletResponse response, String message) throws IOException {
        sendResponse(response, new ResponseDTO<>(200, message, null));
    }
    
    public static void handleUnauthenticated(HttpServletRequest request, HttpServletResponse response, String message) throws IOException {
        if (isApiRequest(request)) {
            handleException(response, new AuthenticationException(message));
        } else {
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }
    
    public static void handleForbidden(HttpServletRequest request, HttpServletResponse response, String message, boolean forceForward) 
        throws IOException, ServletException {
        if (forceForward || !isApiRequest(request)) {
            request.setAttribute("errorMessage", message);
            request.getRequestDispatcher("/WEB-INF/views/common/403.jsp").forward(request, response);
        } else {
            handleException(response, new AuthorizationException(message));
        }
    }
    
    public static void handleException(HttpServletResponse response, Exception e) throws IOException {
        int status = 500;
        String message = "System error";
        Object data = null;
        if (e instanceof BaseException) {
            BaseException be = (BaseException) e;
            status = be.getHttpStatus();
            message = be.getMessage();
            if (e instanceof ValidationException) {
                data = ((ValidationException) e).getErrors();
            } else if (e instanceof TechnicalException) {
                message = "System error, contact admin if needed!"; // Ẩn cấu trúc lỗi thật khỏi outsiders
                System.err.println("[TECHNICAL ERROR]: " + be.getMessage());
                be.printStackTrace();
            }
            // Mấy loại exception khác thì mặc định BaseException thâu hết data cho r
        } else if (e instanceof RuntimeException) {
            e.printStackTrace();
        }
        sendResponse(response, new ResponseDTO<>(status, message, data));
    }
    
    public static String getFullPath(HttpServletRequest request) {
        return request.getServletPath() + (request.getPathInfo()!=null ? request.getPathInfo() : "");
    }
    
    public static boolean isApiRequest(HttpServletRequest request) {
        return getFullPath(request).startsWith("/api/");
    }
}
