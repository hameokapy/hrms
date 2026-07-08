
package com.hrms.web.controller;

import com.hrms.core.config.SystemConfig;
import com.hrms.core.constant.PermissionConstants;
import com.hrms.core.constant.RoleEnums;
import com.hrms.core.security.PermissionCache;
import com.hrms.core.security.SecurityContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name="ManagementController", urlPatterns={"/management/*"})
public class ManagementController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();
        if(path==null || path.equals("/")) {
            response.sendRedirect(request.getContextPath() + "/management/dashboard");
            return;
        }
        String contentPage = "";
        String pageTitle = "HRMS Management";
        String activeMenu = "";
        Map<String, String> pMap = new HashMap<>();
        
        // Nạp cho thằng sidebar ẩn đc
        pMap.put("VIEW_POSI", PermissionConstants.Position.VIEW);
        pMap.put("VIEW_ROLE", PermissionConstants.Role.VIEW);
        pMap.put("VIEW_USER", PermissionConstants.User.VIEW_ALL);
        
        switch (path) {
            case "/dashboard" -> {
                contentPage = "/WEB-INF/views/management/dashboard/dashboard.jsp";
                pageTitle = "Dashboard";
                activeMenu = "dashboard";
                request.setAttribute("pageJS", "/assets/js/management/dashboard/dashboard.js");
            }
            case "/departments" -> {
                contentPage = "/WEB-INF/views/management/departments/list.jsp";
                pageTitle = "Departments";
                activeMenu = "department";
                request.setAttribute("pageJS", "/assets/js/management/departments/list.js");
                
                pMap.put("CREATE", PermissionConstants.Department.CREATE);
                pMap.put("EDIT", PermissionConstants.Department.EDIT);
                pMap.put("ASSIGN", PermissionConstants.Department.ASSIGN_MANAGER);
                pMap.put("DELETE", PermissionConstants.Department.DELETE);
            }
            case "/positions" -> {
                contentPage = "/WEB-INF/views/management/positions/list.jsp";
                pageTitle = "Positions";
                activeMenu = "position";
                request.setAttribute("pageJS", "/assets/js/management/positions/list.js");
                
                pMap.put("DELETE", PermissionConstants.Position.DELETE);
            }
            case "/employees" -> {
                contentPage = "/WEB-INF/views/management/employees/list.jsp";
                pageTitle = "Employees";
                activeMenu = "employee";
                request.setAttribute("pageJS", "/assets/js/management/employees/list.js");
                
                pMap.put("CREATE", PermissionConstants.Employee.CREATE);
                pMap.put("EDIT", PermissionConstants.Employee.EDIT);
                pMap.put("ASSIGN", PermissionConstants.Employee.ASSIGN_DEPT);
                pMap.put("DELETE", PermissionConstants.Employee.DELETE);
            }
            case "/leaves" -> {
                contentPage = "/WEB-INF/views/management/leaves/list.jsp";
                pageTitle = "Leave Requests & Balances";
                activeMenu = "leave";
                request.setAttribute("pageJS", "/assets/js/management/leaves/list.js");
                
                pMap.put("CREATE", PermissionConstants.Leave.CREATE_REQUEST);
                pMap.put("VIEW", PermissionConstants.Leave.VIEW_REQUEST);
                pMap.put("UPDATE", PermissionConstants.Leave.UPDATE_REQUEST);
                pMap.put("CANCEL", PermissionConstants.Leave.CANCEL_REQUEST);
                pMap.put("APPROVE", PermissionConstants.Leave.APPROVE_REQUEST);
                
                //chữa cháy, ko phải cách tối ưu
                boolean isAdmin = SecurityContext.getCurrentRoleIds().contains(RoleEnums.ADMIN.getId()); 
                request.setAttribute("isAdminRole", isAdmin);
            }
            case "/users" -> {
                contentPage = "/WEB-INF/views/management/users/list.jsp";
                pageTitle = "Users";
                activeMenu = "user";
                request.setAttribute("pageJS", "/assets/js/management/users/list.js");
            }
            case "/roles" -> {
                contentPage = "/WEB-INF/views/management/roles/list.jsp";
                pageTitle = "Roles & Permissions";
                activeMenu = "role";
                request.setAttribute("pageJS", "/assets/js/management/roles/list.js");
            }
            case "/under-construction" -> {
                contentPage = "/WEB-INF/views/common/under-construction.jsp";
                pageTitle = "Coming Soon";
            }
            // case default: con autho filter xử lý cho r
        }
        request.setAttribute("contentPage", contentPage);
        request.setAttribute("pageTitle", pageTitle);
        request.setAttribute("activeMenu", activeMenu);
        
        request.setAttribute("userPermis", PermissionCache.getUserPermissions(SecurityContext.getCurrentRoleIds()));
        request.setAttribute("P", pMap);
        
        request.getRequestDispatcher("/WEB-INF/layouts/management-layout.jsp").forward(request, response);
    }
}
