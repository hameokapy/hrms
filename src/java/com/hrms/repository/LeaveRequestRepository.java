
package com.hrms.repository;

import com.hrms.model.dto.common.PageResponseDTO;
import java.time.LocalDate;
import com.hrms.model.dto.response.LeaveRequestDTO;
import com.hrms.model.entity.LeaveRequestEntity;
import com.hrms.model.mapper.builder.LeaveRequestSearchBuilder;

public interface LeaveRequestRepository extends GenericDAO<LeaveRequestEntity>{
    PageResponseDTO<LeaveRequestDTO> search(LeaveRequestSearchBuilder builder, Long empId, Long deptId, Integer currentPage, Integer pageSize);
    Long createRequest(LeaveRequestEntity entity);
    void updateRequest(LeaveRequestEntity entity);
    void updateStatusRequest(Long leaveRequestId, String status, String modifiedBy, String approvedBy); // cho cancel hoặc approve/reject
    boolean existOverlap(Long employeeId, LocalDate startDate, LocalDate endDate);
    boolean existOverlapExcludeSelf(Long leaveRequestId, Long employeeId, LocalDate startDate, LocalDate endDate);
    LeaveRequestEntity findByLeaveRequestId(Long leaveRequestId);
}
