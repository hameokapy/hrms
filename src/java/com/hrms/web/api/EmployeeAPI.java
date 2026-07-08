
package com.hrms.web.api;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import com.hrms.model.dto.request.EmployeeRequestDTO;
import com.hrms.model.mapper.builder.EmployeeSearchBuilder;
import com.hrms.service.EmployeeService;
import com.hrms.service.impl.EmployeeServiceImpl;
import com.hrms.utils.RequestParser;

@WebServlet(name="EmployeeAPI", urlPatterns={"/api/employees/*"})
public class EmployeeAPI extends BaseServlet {
    
    /*
        GET /api/employees: Xem tóm tắt employees
        GET /api/employees/{id}: Xem chi tiết 1 employee
        POST /api/employees: Tạo mới 1 employee
        PUT /api/employees: Chỉnh sửa employee
        PUT /api/employees/department: Gán/Gỡ employees khỏi dept
        PUT /api/employees/position: Gán/Gỡ employees khỏi posi
        PUT /api/employees/status: Thay đổi status employee
    */
    
    private final EmployeeService empService = new EmployeeServiceImpl();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        try {
            RequestParser rp = new RequestParser(request);
            Integer page = rp.getInteger("page", false);
            if(page == null)
                page = 1;
            Integer pageSize = rp.getInteger("pageSize", false);
            if (pathInfo == null || pathInfo.equals("/")) {
                EmployeeSearchBuilder builder = new EmployeeSearchBuilder.Builder()
                        .setId(rp.getLong("id", false))
                        .setEmployeeNameCode(rp.getString("employee", false, 0, 50))
                        .setDepartmentNameCode(rp.getString("department", false, 0, 50))
                        .setPositionName(rp.getString("position", false, 0, 50))
                        .setStatus(rp.getListString("status", false, 0, 10))
                        .build();
                rp.validate(); 
                sendSuccess(response, empService.search(builder, page, pageSize));
            } else if (pathInfo.matches("^/\\d+$")) {
                Long empId = rp.getLongFromPath("empId", pathInfo.substring(1), true);
                rp.validate();
                sendSuccess(response, empService.searchById(empId));
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
            EmployeeRequestDTO requestDTO = new EmployeeRequestDTO();
            requestDTO.setFullName(rp.getString("fullName", true, 2, 50));
            requestDTO.setEmail(rp.getEmail("email", true));
            requestDTO.setPhone(rp.getPhone("phone", true));
            requestDTO.setDepartmentId(rp.getLong("departmentId", true));
            requestDTO.setPositionId(rp.getLong("positionId", true));
            rp.validate();
            Long id = empService.createEmployee(requestDTO);
            sendSuccess(response, Collections.singletonMap("empId", id));
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
            switch(pathInfo==null ? "/" : pathInfo) {
                case "/" -> {
                    EmployeeRequestDTO requestDTO = new EmployeeRequestDTO();
                    requestDTO.setId(rp.getLong("id", true));
                    requestDTO.setFullName(rp.getString("fullName", false, 2, 50));
                    requestDTO.setEmail(rp.getEmail("email", false));
                    requestDTO.setPhone(rp.getPhone("phone", false));
                    rp.validate();
                    empService.updateGeneral(requestDTO);
                }
                case "/department" -> {
                    Long deptId = rp.getLong("departmentId", true);
                    List<Long> empIds = rp.getListLong("employeeIds", true);
                    rp.validate();
                    empService.assignDepartmentInBulk(empIds, deptId);
                }
                case "/position" -> {
                    Long posiId = rp.getLong("positionId", true);
                    List<Long> empIds = rp.getListLong("employeeIds", true);
                    rp.validate();
                    empService.assignPositionInBulk(empIds, posiId);
                }
                case "/status" -> {
                    Long empId = rp.getLong("id", true);
                    String status = rp.getString("status", true, 2, 20);
                    rp.validate();
                    empService.changeStatus(empId, status.toUpperCase());
                }
            }
            sendSuccess(response, "Updated successfully.");
        } catch (Exception e) {
            handleException(response, e);
        }
    }
}
