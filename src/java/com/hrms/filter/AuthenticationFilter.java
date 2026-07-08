
package com.hrms.filter;

import com.hrms.core.config.SecurityConfig;
import com.hrms.core.security.SecurityContext;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Set;
import com.hrms.model.dto.common.UserSessionDTO;
import com.hrms.utils.ResponseHelper;

public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String path = ResponseHelper.getFullPath(req);
        if(SecurityConfig.isPublic(path)) {
            chain.doFilter(request, response);
            return; 
        }
        HttpSession session = req.getSession(false);
        if(session!=null && session.getAttribute("sessionUser")!=null){
            Long userId = ((UserSessionDTO) session.getAttribute("sessionUser")).getUserId();
            String username = ((UserSessionDTO) session.getAttribute("sessionUser")).getUsername();
            Long deptId = ((UserSessionDTO) session.getAttribute("sessionUser")).getDeptId();
            Long empId = ((UserSessionDTO) session.getAttribute("sessionUser")).getEmployeeId();
            Set<Long> roleIds = ((UserSessionDTO) session.getAttribute("sessionUser")).getRoleIds();
            SecurityContext.setContext(userId, username, roleIds, empId, deptId);
            chain.doFilter(request, response);
        } else {
            ResponseHelper.handleUnauthenticated(req, (HttpServletResponse) response, "Authentication failed");
        }
    }
    
}