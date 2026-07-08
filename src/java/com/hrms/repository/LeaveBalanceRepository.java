
package com.hrms.repository;

import com.hrms.model.dto.common.PageResponseDTO;
import com.hrms.model.dto.response.LeaveBalanceSummaryDTO;
import com.hrms.model.entity.LeaveBalanceEntity;
import com.hrms.model.mapper.builder.LeaveBalanceSearchBuilder;

public interface LeaveBalanceRepository extends GenericDAO<LeaveBalanceEntity> {
    LeaveBalanceEntity findByEmployeeIdAndYear(Long employeeId, Integer year);
    Long create(LeaveBalanceEntity entity);
    void updateAnnualUsage(Long balanceId, Integer days, String modifiedBy);
    void updateSickUsage(Long balanceId, Integer days, String modifiedBy);
    PageResponseDTO<LeaveBalanceSummaryDTO> searchBalance(LeaveBalanceSearchBuilder builder, Long scopeDeptId, Long scopeEmpId, Integer currentPage, Integer pageSize);
}
