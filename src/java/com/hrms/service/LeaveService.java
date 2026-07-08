
package com.hrms.service;

import com.hrms.model.dto.common.PageResponseDTO;
import com.hrms.model.dto.request.LeaveRequestRequestDTO;
import com.hrms.model.dto.response.LeaveBalanceSummaryDTO;
import com.hrms.model.dto.response.LeaveRequestDTO;
import com.hrms.model.mapper.builder.LeaveBalanceSearchBuilder;
import com.hrms.model.mapper.builder.LeaveRequestSearchBuilder;

public interface LeaveService {
    // leave_requests
    PageResponseDTO<LeaveRequestDTO> searchRequest(LeaveRequestSearchBuilder builder, Integer page, Integer pageSize);
    Long createRequest(LeaveRequestRequestDTO request);
    void updateRequest(LeaveRequestRequestDTO request);
    void cancelRequest(Long leaveRequestId);
    void approveRequest(Long leaveRequestId, String status);
    // leave_balance
    PageResponseDTO<LeaveBalanceSummaryDTO> searchBalance(LeaveBalanceSearchBuilder builder, Integer page);
}
