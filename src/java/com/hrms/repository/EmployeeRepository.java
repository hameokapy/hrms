
package com.hrms.repository;

import com.hrms.model.dto.common.PageResponseDTO;
import java.util.List;
import com.hrms.model.dto.response.EmployeeDetailDTO;
import com.hrms.model.dto.response.EmployeeSummaryDTO;
import com.hrms.model.entity.EmployeeEntity;
import com.hrms.model.mapper.builder.EmployeeSearchBuilder;

public interface EmployeeRepository extends GenericDAO<EmployeeEntity> {
    PageResponseDTO<EmployeeSummaryDTO> search(EmployeeSearchBuilder builder, Long deptId, Integer currentPage, Integer pageSize);
    EmployeeDetailDTO searchById(Long empId);
    Long createEmployee(EmployeeEntity entity);
    void updateGeneral(EmployeeEntity entity);
    void assignDepartmentInBulk(List<Long> empIds, Long deptId, String modifiedBy);
    void assignPositionInBulk(List<Long> empIds, Long posiId, String modifiedBy);
    void updateStatus(Long empId, String status, String modifiedBy); // aka soft delete or restore
    // Hàm bổ trợ các class service:
    EmployeeEntity findById(Long employeeId);
    List<EmployeeEntity> findAllByIds(List<Long> ids);
    Long countEmployeesByDeptIdExcludeInactive(Long deptId);
    Long countEmployeesByPosiIdExcludeInactive(Long posiId);
    Long getNextSequenceValue();
    void resetSequence();
    boolean existEmail(String email);
    boolean existPhone(String phone);
}
