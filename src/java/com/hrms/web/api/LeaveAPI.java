
package com.hrms.web.api;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import com.hrms.model.dto.request.LeaveRequestRequestDTO;
import com.hrms.model.mapper.builder.LeaveBalanceSearchBuilder;
import com.hrms.model.mapper.builder.LeaveRequestSearchBuilder;
import com.hrms.service.impl.LeaveServiceImpl;
import com.hrms.utils.RequestParser;
import com.hrms.service.LeaveService;

@WebServlet(name="LeaveAPI", urlPatterns={"/api/leave/*"})
public class LeaveAPI extends BaseServlet {
    
    /*
        GET /api/leave/requests: Xem các leave requests
        GET /api/leave/balance: Xem các leave balance của từng employee
        POST /api/leave/requests: Tạo 1 leave request
        PUT /api/leave/requests/update: Sửa sơ sơ 1 leave request (status pending only)
        PUT /api/leave/requests/cancel: Cancel 1 leave request (status pending only)
        PUT /api/leave/requests/approve: Approve/Reject 1 leave request
    */
    
    private final LeaveService leaveService = new LeaveServiceImpl();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        try {
            RequestParser rp = new RequestParser(request);
            Integer page = rp.getInteger("page", false);
            if(page == null)
                page = 1;
            Integer pageSize = rp.getInteger("pageSize", false);
            switch (pathInfo) {
                case "/requests" -> {
                    LeaveRequestSearchBuilder builder = new LeaveRequestSearchBuilder.Builder()
                            .setId(rp.getLong("id", false))
                            .setKeyword(rp.getString("keyword", false, 0, 50))
                            .setType(rp.getString("type", false, 0, 6))
                            .setStatus(rp.getString("status", false, 0, 10))
                            .setFromDate(rp.getLocalDate("fromDate", false))
                            .setToDate(rp.getLocalDate("toDate", false))
                            .build();
                    rp.validate();
                    sendSuccess(response, leaveService.searchRequest(builder, page, pageSize));
                }
                case "/balance" -> {
                    LeaveBalanceSearchBuilder builder = new LeaveBalanceSearchBuilder.Builder()
                            .setYear(rp.getInteger("year", false))
                            .setEmployeeKeyword(rp.getString("employee", false, 0, 50))
                            .setDepartmentKeyword(rp.getString("department", false, 0, 50))
                            .build();
                    rp.validate();
                    sendSuccess(response, leaveService.searchBalance(builder, page)); 
                }
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
            LeaveRequestRequestDTO requestDTO = new LeaveRequestRequestDTO();
            requestDTO.setEmployeeId(rp.getLong("employeeId", true));
            String type = rp.getString("type", true, 2, 6);
            requestDTO.setType(type!=null ? type.toUpperCase() : null);
            requestDTO.setStartDate(rp.getLocalDate("startDate", true));
            requestDTO.setEndDate(rp.getLocalDate("endDate", true));
            requestDTO.setReason(rp.getString("reason", false, 2, 100));
            rp.validate();
            Long id = leaveService.createRequest(requestDTO);
            sendSuccess(response, Collections.singletonMap("leaveRequestId", id));
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
            Long leaveRequestId = rp.getLong("id", true);
            switch(pathInfo) {
                case "/requests/update" -> {
                    LeaveRequestRequestDTO requestDTO = new LeaveRequestRequestDTO();
                    requestDTO.setId(leaveRequestId);
                    String type = rp.getString("type", false, 2, 6);
                    requestDTO.setType(type!=null ? type.toUpperCase() : null);
                    requestDTO.setStartDate(rp.getLocalDate("startDate", false));
                    requestDTO.setEndDate(rp.getLocalDate("endDate", false));
                    requestDTO.setReason(rp.getString("reason", false, 2, 100));
                    rp.validate();
                    leaveService.updateRequest(requestDTO);
                }
                case "/requests/cancel" -> {
                    leaveService.cancelRequest(leaveRequestId);
                }
                case "/requests/approve" -> {
                    String newStatus = rp.getString("status", true, 2, 10);
                    rp.validate(); 
                    leaveService.approveRequest(leaveRequestId, newStatus.toUpperCase());
                }
            }
            sendSuccess(response, "Updated successfully.");
        } catch (Exception e) {
            handleException(response, e);
        }
    }
}
