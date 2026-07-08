package com.hrms.web.api;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.hrms.service.DepartmentService;
import com.hrms.service.impl.DepartmentServiceImpl;

import jakarta.servlet.annotation.WebServlet;
import java.util.Collections;
import java.util.Map;
import com.hrms.model.dto.request.DepartmentRequestDTO;
import com.hrms.model.mapper.builder.DepartmentSearchBuilder;
import com.hrms.utils.RequestParser;

@WebServlet(name="DepartmentAPI", urlPatterns={"/api/departments/*"})
public class DepartmentAPI extends BaseServlet {
    
    /*
        GET /api/departments: Xem tóm tắt departments
        GET /api/departments/{id}: Xem chi tiết 1 department
        GET /api/departments/{id}/employees: Xem NV của phòng ban
        POST /api/departments: Tạo mới 1 department
        PUT /api/departments: Chỉnh sửa department
        PUT /api/departments/manager: Gán/Gỡ manager khỏi dept
        PUT /api/departments/status: Xóa mềm/Mở lại phòng ban
    */
    
    private final DepartmentService departmentService = new DepartmentServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        try {
            RequestParser rp = new RequestParser(request);
            Integer page = rp.getInteger("page", false);
            Integer pageSize = rp.getInteger("pageSize", false);
            if(page == null)
                page = 1;
            if (pathInfo == null || pathInfo.equals("/")) {
                DepartmentSearchBuilder builder = new DepartmentSearchBuilder.Builder()
                        .setId(rp.getLong("id", false))
                        .setCode(rp.getString("code", false, 0, 10))
                        .setName(rp.getString("name", false, 0, 50))
                        .setManagerName(rp.getString("managerName", false, 0, 50))
                        .setLocation(rp.getString("location", false, 0, 100))
                        .setStatus(rp.getString("status", false, 0, 10))
                        .build();
                rp.validate();
                sendSuccess(response, departmentService.search(builder, page, pageSize));
            } else if (pathInfo.matches("^/\\d+$")) {
                Long deptId = rp.getLongFromPath("deptId", pathInfo.substring(1), true);
                rp.validate();
                sendSuccess(response, departmentService.searchById(deptId));
            } else if (pathInfo.matches("/\\d+/employees")) {
                String[] parts = pathInfo.split("/"); 
                Long deptId = rp.getLongFromPath("deptId", parts[1], true);
                rp.validate();
                sendSuccess(response, departmentService.getEmployeesInDept(deptId, page, pageSize));
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
            DepartmentRequestDTO requestDTO = new DepartmentRequestDTO();
            requestDTO.setCode(rp.getString("code", true, 2, 10));
            requestDTO.setName(rp.getString("name", true, 2, 50));
            requestDTO.setManagerId(rp.getLong("managerId", false));
            requestDTO.setLocation(rp.getString("location", false, 2, 100));
            rp.validate();
            Long id = departmentService.createDepartment(requestDTO);
            sendSuccess(response, Collections.singletonMap("deptId", id));
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
            Long deptId = rp.getLong("id", true);
            switch(pathInfo==null ? "/" : pathInfo) {
                case "/" -> {
                    DepartmentRequestDTO requestDTO = new DepartmentRequestDTO();
                    requestDTO.setId(deptId);
                    requestDTO.setName(rp.getString("name", false, 2, 50)); // để false vì ko bắt buộc phải đổi giá trị gốc của nó, # góc hiểu ở create là bắt buộc cái field phải có giá trị
                    requestDTO.setLocation(rp.getString("location", false, 2, 100));
                    rp.validate(); 
                    departmentService.updateGeneral(requestDTO);
                }
                case "/manager" -> {
                     Long managerId = rp.getLong("managerId", false);
                     rp.validate();
                     departmentService.assignManager(deptId, managerId);
                }
                case "/status" -> {
                    String status = rp.getString("status", true, 2, 8);
                    rp.validate();
                    departmentService.changeStatus(deptId, status.toUpperCase());
                }
            }
            sendSuccess(response, "Updated successfully");
        } catch (Exception e) {
            handleException(response, e);
        }
    }
}