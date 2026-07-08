
package com.hrms.web.api;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import com.hrms.model.dto.request.PositionRequestDTO;
import com.hrms.model.mapper.builder.PositionSearchBuilder;
import com.hrms.service.PositionService;
import com.hrms.service.impl.PositionServiceImpl;
import com.hrms.utils.RequestParser;

@WebServlet(name="PositionAPI", urlPatterns={"/api/positions/*"})
public class PositionAPI extends BaseServlet {
    
    /*
        GET /api/positions: Xem tóm tắt positions
        GET /api/positions/{id}: Xem chi tiết 1 position
        POST /api/positions: Tạo mới 1 position
        PUT /api/positions: Update 1 position (ko tính status)
        PUT /api/positions/status: Xóa mềm/Mở lại position
    */
    
    private final PositionService posiService = new PositionServiceImpl();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        try {
            RequestParser rp = new RequestParser(request);
            Integer page = rp.getInteger("page", false);
            if(page == null)
                page = 1;
            if (pathInfo == null || pathInfo.equals("/")) {
                PositionSearchBuilder builder = new PositionSearchBuilder.Builder()
                        .setId(rp.getLong("id", false))
                        .setName(rp.getString("name", false, 0, 50))
                        .setStatus(rp.getString("status", false, 0, 8))
                        .setSalaryFrom(rp.getBigDecimal("salaryFrom", false))
                        .setSalaryTo(rp.getBigDecimal("salaryTo", false))
                        .build();
                rp.validate();
                sendSuccess(response, posiService.search(builder, page));
            } else if (pathInfo.matches("^/\\d+$")) {
                Long posiId = rp.getLongFromPath("posiId", pathInfo.substring(1), true);
                rp.validate();
                sendSuccess(response, posiService.searchById(posiId));
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
            PositionRequestDTO requestDTO = new PositionRequestDTO();
            requestDTO.setName(rp.getString("name", true, 2, 50));
            requestDTO.setBaseSalaryLevel(rp.getBigDecimal("baseSalaryLevel", true));
            requestDTO.setDescription(rp.getString("description", false, 2, 100));
            rp.validate();
            Long posiId = posiService.create(requestDTO);
            sendSuccess(response, Collections.singletonMap("posiId", posiId));
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
            Long posiId = rp.getLong("id", true);
            switch(pathInfo==null ? "/" : pathInfo) {
                case "/" -> {
                    PositionRequestDTO requestDTO = new PositionRequestDTO();
                    requestDTO.setId(posiId);
                    requestDTO.setName(rp.getString("name", false, 2, 50));
                    requestDTO.setDescription(rp.getString("description", false, 2, 100));
                    requestDTO.setBaseSalaryLevel(rp.getBigDecimal("baseSalaryLevel", false));
                    rp.validate();
                    posiService.updateGeneral(requestDTO);
                }  
                case "/status" -> {
                    String status = rp.getString("status", true, 2, 8);
                    rp.validate();
                    posiService.changeStatus(posiId, status.toUpperCase());
                }
            }
            sendSuccess(response, "Updated successfully.");
        } catch (Exception e) {
            handleException(response, e);
        }
    }
    
}
