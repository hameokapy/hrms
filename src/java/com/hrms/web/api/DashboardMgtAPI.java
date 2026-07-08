
package com.hrms.web.api;

import com.hrms.service.impl.DashboardMgtServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name="DashboardMgtAPI", urlPatterns={"/api/dashboardmgt/*"})
public class DashboardMgtAPI extends BaseServlet {
    
    private final DashboardMgtServiceImpl service = new DashboardMgtServiceImpl();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        try {
            if (pathInfo == null || pathInfo.equals("/"))
                sendSuccess(response, service.getStats());
        } catch (Exception e) {
            handleException(response, e);
        }
    }
        
}
