
package com.hrms.filter;

import com.hrms.core.config.SecurityConfig;
import com.hrms.core.constant.RoleEnums;
import com.hrms.core.security.PermissionCache;
import com.hrms.core.security.SecurityContext;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import com.hrms.utils.ResponseHelper;

public class AuthorizationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String path = ResponseHelper.getFullPath(req);
        String method = req.getMethod();
        // LỚP 1+2: public path và authenticated only path
        // Thật ra được Authen Filter bảo trợ cho r mới dám cho con authen_only pass luôn
        if (SecurityConfig.isPublic(path) || SecurityConfig.isAuthenticatedOnly(path)) {
            chain.doFilter(request, response);
            return;
        }
        // LỚP 3: check permission user có đủ pass path đang gọi ko?
        Set<Long> originRoleIds = SecurityContext.getCurrentRoleIds();
        Set<Long> effectiveRoleIds = filterRoleIdsByPath(path, originRoleIds);
        if (effectiveRoleIds.isEmpty()) {
            ResponseHelper.handleForbidden(req, res, "No authorized role to access.", false);
            return;
        }
        if (path.startsWith("/api/")) {
            handleApiAccess(req, res, chain, method, path, effectiveRoleIds);
            return; 
        }
        if (path.startsWith("/portal/") || path.startsWith("/management/")) {
            handlePageAccess(req, res, chain, method, path, effectiveRoleIds, originRoleIds);
            return; 
        }
        res.sendError(404); // Những link ko đc /api or /portal or /management or public or authen path bảo chứng thì trả 404 hết
    }
    
    private void handleApiAccess(HttpServletRequest req, HttpServletResponse res, FilterChain chain, 
        String method, String path, Set<Long> effectiveRoleIds) throws IOException, ServletException {
        // Cơ chế xử lý link /api ko tồn tại: trả về giao diện 403 thay vì báo ko có để bảo mật
        String requiredPerm = SecurityConfig.getRequiredApiPermission(method, path);
        if (requiredPerm == null) {
            ResponseHelper.handleForbidden(req, res, "You are not authorized.", true);
            return;
        }
        if (PermissionCache.hasPermission(effectiveRoleIds, requiredPerm)) {
            chain.doFilter(req, res);
        } else {
            ResponseHelper.handleForbidden(req, res, "Missing permission to access.", false);
        }
    }

    private void handlePageAccess(HttpServletRequest req, HttpServletResponse res, FilterChain chain, 
        String method, String path, Set<Long> effectiveRoleIds, Set<Long> originRoleIds) throws IOException, ServletException {
        // Cơ chế xử lý link /portal or /management ko tồn tại: web.xml lo hộ với 404 cho rồi
        String requiredPerm = SecurityConfig.getRequiredPagePermission(method, path);
        if (requiredPerm == null) {
            res.sendError(404);
            return;
        }
        if (PermissionCache.hasPermission(effectiveRoleIds, requiredPerm)) {
            SecurityContext sc = SecurityContext.get();
            sc.cloneWithNewRoles(effectiveRoleIds);
            try {
                chain.doFilter(req, res);
            } finally {
                sc.cloneWithNewRoles(originRoleIds);
            }
        } else {
            ResponseHelper.handleForbidden(req, res, "Missing permission to access page.", false);
        }
    }
    
    private Set<Long> filterRoleIdsByPath(String path, Set<Long> originRoleIds) {
        if (originRoleIds==null || originRoleIds.isEmpty()) 
            return Collections.emptySet();
        Set<Long> effectiveRoles = new HashSet<>();
        // CASE 1: truy cập được /portal nếu có role employee
        if (path.startsWith("/portal")) {
            if (originRoleIds.contains(RoleEnums.EMPLOYEE.getId()))
                effectiveRoles.add(RoleEnums.EMPLOYEE.getId());
        } else if (path.startsWith("/management")) {
        // CASE 2: truy cập được /management nếu có role admin or HR or manager
            for (Long id : originRoleIds) {
                RoleEnums role = RoleEnums.fromId(id);
                if (role!=null && role.isManagement()) 
                    effectiveRoles.add(id);
            }
        } else {
        // CASE 3: các đường dẫn chung của hai cái trên
            effectiveRoles.addAll(originRoleIds);
        }
        return effectiveRoles;
    }   
    
    
    
}



