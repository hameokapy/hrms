
package com.hrms.web.api;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import com.hrms.model.mapper.builder.PermissionSearchBuilder;
import com.hrms.service.PermissionService;
import com.hrms.service.impl.PermissionServiceImpl;
import com.hrms.utils.RequestParser;

@WebServlet(name="PermissionAPI", urlPatterns={"/api/permissions/*"})
public class PermissionAPI extends BaseServlet {
    
    /*
        GET:/api/permissions: Tìm kiếm permissions
        PUT:/api/permissions: Cập nhật mô tả cho permission
    */
    
    private final PermissionService permiService = new PermissionServiceImpl();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            RequestParser rp = new RequestParser(request);
            PermissionSearchBuilder builder = new PermissionSearchBuilder.Builder()
                    .setId(rp.getLong("id", false))
                    .setPermissionKey(rp.getString("permissionKey", false, 0, 50))
                    .setRoleName(rp.getString("roleName", false, 0, 20))
                    .setDescription(rp.getString("description", false, 0, 100))
                    .build();
            rp.validate();
            sendSuccess(response, permiService.search(builder));
        } catch (Exception e) {
            handleException(response, e);
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Map<String, Object> body = getRequestBody(request, Map.class);
            RequestParser rp = new RequestParser(body);
            Long id = rp.getLong("id", true);
            String description = rp.getString("description", false, 2, 100);
            rp.validate();
            permiService.updateDescription(id, description);
            sendSuccess(response, "Updated successfully.");
        } catch (Exception e) {
            handleException(response, e);
        }
    }
    
}
