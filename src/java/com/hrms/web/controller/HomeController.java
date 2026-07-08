
package com.hrms.web.controller;

import com.hrms.core.constant.RoleEnums;
import com.hrms.core.security.SecurityContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@WebServlet(name="HomeController", urlPatterns={"/home"})
public class HomeController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Set<Long> roleIds = SecurityContext.getCurrentRoleIds();
            if (roleIds==null || roleIds.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/login?msg=no_permission");
                return;
            }
            boolean hasEmployeeRole = roleIds.contains(RoleEnums.EMPLOYEE.getId());
            boolean hasManagementRole = roleIds.stream().anyMatch(id -> {
                    RoleEnums r = RoleEnums.fromId(id);
                    return r!=null && r.isManagement();
                });
            if (hasEmployeeRole && hasManagementRole) {
                request.getRequestDispatcher("/WEB-INF/views/common/select-role.jsp").forward(request, response);
                return;
            }
            if (hasManagementRole) {
                response.sendRedirect(request.getContextPath() + "/management/dashboard");
                return;
            }
            if (hasEmployeeRole) {
                response.sendRedirect(request.getContextPath() + "/portal/dashboard");
                return;
            }
            response.sendRedirect(request.getContextPath() + "/login?msg=invalid_role");
        } catch (Exception e) {
            e.printStackTrace(); 
            request.setAttribute("errorMessage", "System under technical problem!");
            request.getRequestDispatcher("/WEB-INF/views/common/500.jsp").forward(request, response);
        }
    }
}
