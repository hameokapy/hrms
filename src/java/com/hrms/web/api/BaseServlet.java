package com.hrms.web.api;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.hrms.utils.GsonHelper;
import com.hrms.utils.ResponseHelper;

//    Tầng Controller check: Dữ liệu có đúng định dạng ko
//    Tầng Service check: Dữ liệu có đúng logic hệ thống ko

public abstract class BaseServlet extends HttpServlet {
    
    protected <T> T getRequestBody(HttpServletRequest request, Class<T> tClass) {
        try {
            T body = GsonHelper.fromJson(request.getReader(), tClass);
            if (body == null) 
                throw new RuntimeException("Found empty request body!");
            return body;
        } catch (Exception e) { 
            throw new RuntimeException("Invalid JSON format: " + e.getMessage());
        }
    }
    
    protected void sendSuccess(HttpServletResponse response, Object data) throws IOException {
        ResponseHelper.sendSuccess(response, data);
    }
    
    protected void sendSuccess(HttpServletResponse response, String message) throws IOException {
        ResponseHelper.sendSuccess(response, message);
    } 
    
    protected void handleException(HttpServletResponse response, Exception e) throws IOException {
        ResponseHelper.handleException(response, e);
    }
    

}
