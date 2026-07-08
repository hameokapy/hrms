
package com.hrms.web.api;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import com.hrms.model.mapper.builder.RoleSearchBuilder;
import com.hrms.service.RoleService;
import com.hrms.service.impl.RoleServiceImpl;
import com.hrms.utils.RequestParser;

@WebServlet(name="RoleAPI", urlPatterns={"/api/roles/*"})
public class RoleAPI extends BaseServlet {
    
    /*
        GET /api/roles: Tìm kiếm roles
        PUT /api/roles: Cập nhật mô tả cho role
    */
    
    private final RoleService roleService = new RoleServiceImpl();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            RequestParser rp = new RequestParser(request);
            RoleSearchBuilder builder = new RoleSearchBuilder.Builder()
                    .setId(rp.getLong("id", false))
                    .setRoleName(rp.getString("roleName", false, 0, 20))
                    .setDescription(rp.getString("description", false, 0, 100))
                    .build();
            rp.validate();
            sendSuccess(response, roleService.search(builder));
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
            roleService.updateDescription(id, description);
            sendSuccess(response, "Updated successfully.");
        } catch (Exception e) {
            handleException(response, e);
        }
    }
}
