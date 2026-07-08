
package com.hrms.service;

import com.hrms.model.dto.common.PageResponseDTO;
import com.hrms.model.dto.response.DepartmentSummaryDTO;
import com.hrms.model.dto.response.DepartmentDetailDTO;
import com.hrms.model.dto.request.DepartmentRequestDTO;
import com.hrms.model.dto.response.EmployeeSummaryDTO;
import com.hrms.model.mapper.builder.DepartmentSearchBuilder;

public interface DepartmentService {
    PageResponseDTO<DepartmentSummaryDTO> search(DepartmentSearchBuilder builder, Integer page, Integer pageSize);
    DepartmentDetailDTO searchById(Long deptId);
    PageResponseDTO<EmployeeSummaryDTO> getEmployeesInDept(Long deptId, Integer page, Integer pageSize);
    Long createDepartment(DepartmentRequestDTO request);
    void updateGeneral(DepartmentRequestDTO request);
    void assignManager(Long deptId, Long managerId);
    void changeStatus(Long deptId, String status);
}
