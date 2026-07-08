
package com.hrms.service;

import com.hrms.model.dto.common.PageResponseDTO;
import java.util.List;
import com.hrms.model.dto.request.EmployeeRequestDTO;
import com.hrms.model.dto.response.EmployeeDetailDTO;
import com.hrms.model.dto.response.EmployeeSummaryDTO;
import com.hrms.model.mapper.builder.EmployeeSearchBuilder;

public interface EmployeeService {
    PageResponseDTO<EmployeeSummaryDTO> search(EmployeeSearchBuilder builder, Integer currentPage, Integer pageSize);
    EmployeeDetailDTO searchById(Long empId);
    Long createEmployee(EmployeeRequestDTO request);
    void updateGeneral(EmployeeRequestDTO request);
    void assignDepartmentInBulk(List<Long> empIds, Long deptId);
    void assignPositionInBulk(List<Long> empIds, Long posiId);
    void changeStatus(Long empId, String status);
}
