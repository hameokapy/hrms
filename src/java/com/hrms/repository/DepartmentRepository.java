
package com.hrms.repository;

import com.hrms.model.dto.common.PageResponseDTO;
import com.hrms.model.entity.DepartmentEntity;
import java.util.List;
import com.hrms.model.dto.response.DepartmentDetailDTO;
import com.hrms.model.dto.response.DepartmentSummaryDTO;
import com.hrms.model.mapper.builder.DepartmentSearchBuilder;

public interface DepartmentRepository extends GenericDAO<DepartmentEntity> {
    PageResponseDTO<DepartmentSummaryDTO> search(DepartmentSearchBuilder builder, Integer currentPage, Integer pageSize);
    DepartmentDetailDTO searchById(Long deptId);
    Long create(DepartmentEntity entity);
    void updateGeneral(DepartmentEntity entity);
    void assignManager(Long deptId, Long managerId, String modifiedBy);
    void changeStatus(Long deptId, String status, String modifiedBy); // aka soft delete or restore
    // Hàm bổ trợ các class service:
    DepartmentEntity findById(Long deptId);
    List<Long> findDeptIdsByManagers(List<Long> empIds);
    boolean existDeptCode(String deptCode);
    boolean existDeptName(String deptName);
    boolean existDeptId(Long deptId);
    boolean isActive(Long deptId);
    boolean isManager(Long empId);
}

