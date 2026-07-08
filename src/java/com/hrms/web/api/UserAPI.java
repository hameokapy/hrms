
package com.hrms.web.api;

import com.hrms.model.mapper.builder.UserSearchBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import com.hrms.service.UserService;
import com.hrms.service.impl.UserServiceImpl;
import com.hrms.utils.RequestParser;

@WebServlet(name="UserAPI", urlPatterns={"/api/users/*"})
public class UserAPI extends BaseServlet {

    /*    
        GET /api/users: Tìm kiếm users
        GET /api/users/{id}: Lấy chi tiết by userID
        POST /api/users: Tạo mới user account
        PUT /api/users/status: Khóa/Mở mềm account
        PUT /api/users/roles: Cập nhật roles bên user_role
        PUT /api/users/password: Reset pass dạng hashed
        PUT /api/users/employee: Bind employee với user
    */
    
    private final UserService userService = new UserServiceImpl();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        try {
            RequestParser rp = new RequestParser(request);
            Integer page = rp.getInteger("page", false);
            if(page == null)
                page = 1;
            // Ko chơi switch-case đc vì /api/users/{id} ko phải fixed value
            if (pathInfo == null || pathInfo.equals("/")) {
                UserSearchBuilder builder = new UserSearchBuilder.Builder()
                        .setIsActive(rp.getBoolean("isActive", false))
                        .setDeptId(rp.getLong("deptId", false))
                        .setPosiId(rp.getLong("posiId", false))
                        .setRoleId(rp.getLong("roleId", false))
                        .setKeyword(rp.getString("keyword", false, 0, 100))
                        .build();
                rp.validate();
                sendSuccess(response, userService.searchUsers(builder, page));
            } else if (pathInfo.matches("^/\\d+$")) {   
                Long userId = rp.getLongFromPath("userId", pathInfo.substring(1), true);
                rp.validate();
                sendSuccess(response, userService.getUserDetailByUserId(userId));
            } 
        } catch (Exception e) {
            handleException(response, e);
        }
    }    
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Map<String, Object> body = getRequestBody(request, Map.class);
            RequestParser rp = new RequestParser(body);
            String username = rp.getUsername("username", true);
            String password = rp.getPassword("password", true);
            Long employeeId = rp.getLong("employeeId", false);
            rp.validate();
            Long id = userService.createUser(username, password, employeeId);
            // Nên đồng nhất gửi dạng JSON có Key-Value đàng hoàng -> dùng Collections.singletonMap()
            sendSuccess(response, Collections.singletonMap("userId", id));
        } catch (Exception e) {
            handleException(response, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        try {
            Map<String, Object> body = getRequestBody(request, Map.class);
            RequestParser rp = new RequestParser(body);
            Long userId = rp.getLong("userId", true);
            switch (pathInfo) {
                case "/status" -> {
                    Boolean isActive = rp.getBoolean("isActive", true);
                    rp.validate();
                    userService.toggleUserStatus(userId, isActive);
                }
                case "/roles" -> {
                    List<Long> roleIds = rp.getListLong("roleIds", true);
                    rp.validate();
                    userService.updateUserRoles(userId, roleIds);
                }
                case "/employee" -> {
                    Long employeeId = rp.getLong("employeeId", false);
                    rp.validate();
                    userService.bindEmployeeToUser(userId, employeeId);
                }
                case "/password" -> {
                    String newPassword = rp.getPassword("newPassword", true);
                    rp.validate();
                    userService.resetPassword(userId, newPassword);
                }
            }
            sendSuccess(response, "Updated successfully.");
        } catch (Exception e) {
            handleException(response, e);
        }
    }
}
  