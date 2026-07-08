
package com.hrms.service.impl;

import com.hrms.core.config.SystemConfig;
import com.hrms.core.config.TransactionManager;
import com.hrms.core.constant.EmployeeEnums;
import com.hrms.core.constant.OtherEnums;
import com.hrms.core.constant.PermissionConstants;
import com.hrms.core.constant.RoleEnums;
import com.hrms.core.exception.business.AccessDeniedException;
import com.hrms.core.exception.business.DuplicateResourceException;
import com.hrms.core.exception.business.IllegalOperationException;
import com.hrms.core.exception.business.ResourceNotFoundException;
import com.hrms.core.security.PermissionChecker;
import com.hrms.core.security.SecurityContext;
import com.hrms.model.dto.common.PageResponseDTO;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import com.hrms.model.dto.request.DepartmentRequestDTO;
import com.hrms.model.dto.response.DepartmentDetailDTO;
import com.hrms.model.dto.response.DepartmentSummaryDTO;
import com.hrms.model.dto.response.EmployeeSummaryDTO;
import com.hrms.model.entity.DepartmentEntity;
import com.hrms.model.entity.EmployeeEntity;
import com.hrms.model.mapper.builder.DepartmentSearchBuilder;
import com.hrms.model.mapper.builder.EmployeeSearchBuilder;
import com.hrms.repository.DepartmentRepository;
import com.hrms.repository.EmployeeRepository;
import com.hrms.repository.impl.DepartmentRepositoryImpl;
import com.hrms.repository.impl.EmployeeRepositoryImpl;
import com.hrms.service.DepartmentService;
import com.hrms.utils.DataMapper;

public class DepartmentServiceImpl implements DepartmentService {
    
    private final DepartmentRepository deptRepo = new DepartmentRepositoryImpl();
    private final EmployeeRepository empRepo = new EmployeeRepositoryImpl();

    @Override
    public PageResponseDTO<DepartmentSummaryDTO> search(DepartmentSearchBuilder builder, Integer currentPage, Integer pageSize) {
        Set<Long> roleIds = SecurityContext.getCurrentRoleIds();
        boolean isManagement = roleIds.stream().anyMatch(roleId -> RoleEnums.fromId(roleId).isManagement());
        if(!isManagement)
            builder = builder.toBuilder().setStatus(OtherEnums.Department.ACTIVE.getValue()).build();
        return deptRepo.search(builder, currentPage, pageSize!=null ? pageSize : SystemConfig.getPageSize());
    }

    @Override
    public DepartmentDetailDTO searchById(Long deptId) {
        DepartmentDetailDTO dept = Optional.ofNullable(deptRepo.searchById(deptId)).orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + deptId)); 
        String scope = PermissionChecker.getHighestScope(PermissionConstants.Department.VIEW_DETAIL);
        if(scope.equals(OtherEnums.Scope.ALL.getValue())) {
            return dept;
        } else if (scope.equals(OtherEnums.Scope.DEPT.getValue())) {
            if(deptId.equals(SecurityContext.getCurrentDeptId()))
                return dept;
            throw new AccessDeniedException("Not allowed to view other departments info!");
        }
        throw new AccessDeniedException("Not allowed to view this information!");
    }

    @Override
    public Long createDepartment(DepartmentRequestDTO request) {
        if(deptRepo.existDeptCode(request.getCode()) || deptRepo.existDeptName(request.getName()))
            throw new DuplicateResourceException("Department with such name/code existed!");
        EmployeeEntity manager = checkManager(request.getManagerId());
        return TransactionManager.doInTransaction(() -> {
            // Để ý là có bị map thừa ID thì vì ở dưới repo ko gán ID nên ko sao
            DepartmentEntity entity = DataMapper.mapObjectToObject(request, DepartmentEntity.class);
            entity.setManager(manager);
            entity.setStatus(OtherEnums.Department.ACTIVE.getValue());
            entity.setCreatedBy(SecurityContext.getCurrentUsername());
            return deptRepo.create(entity);
        });
    }
    
    // Đang dùng cơ chế manager kiêm nhiệm đc nhiều dept, kể cả ko phải dept của mình
    private EmployeeEntity checkManager(Long employeeId){ 
        if (employeeId == null) 
            return null; // Trả NULL nếu ko muốn gán
        EmployeeEntity emp = empRepo.findById(employeeId);
        if (emp==null || !emp.getStatus().equals(EmployeeEnums.Status.ACTIVE.getValue()))
            throw new ResourceNotFoundException("Employee not found or not active!");
        return emp;
    }

    @Override
    public void updateGeneral(DepartmentRequestDTO request) {
        DepartmentEntity dept = deptRepo.findById(request.getId());
        if (dept == null)
            throw new ResourceNotFoundException("Department not found!");
        if (request.getName()!=null && !request.getName().equalsIgnoreCase(dept.getName())) {
            if(deptRepo.existDeptName(request.getName()))
                throw new DuplicateResourceException("Department with such name existed!");
        }
        TransactionManager.runInTransaction(() -> {
            // requestDTO trên lọc field đc chọn r, còn lại dùng copy not null này là đủ
            DataMapper.copyPropertiesIgnoreNull(request, dept);
            dept.setLocation(request.getLocation()); // bị phản tác dụng của copy not null
            dept.setModifiedBy(SecurityContext.getCurrentUsername());
            deptRepo.updateGeneral(dept);
        });
    }

    @Override
    public void assignManager(Long deptId, Long managerId) {
        if (!deptRepo.existDeptId(deptId)) 
            throw new ResourceNotFoundException("Department not found!");
        checkManager(managerId); 
        TransactionManager.runInTransaction(() -> {
            deptRepo.assignManager(deptId, managerId, SecurityContext.getCurrentUsername());
        });
    }

    @Override
    public void changeStatus(Long deptId, String status) {
        if (!deptRepo.existDeptId(deptId)) 
            throw new ResourceNotFoundException("Department not found!");
        if(status.equals(OtherEnums.Department.INACTIVE.getValue())) {
            Long count = empRepo.countEmployeesByDeptIdExcludeInactive(deptId);
            if(count>0)
                throw new IllegalOperationException("Not allowed, department still has " + count + " non-inactive employees!");
            TransactionManager.runInTransaction(() -> {
                deptRepo.assignManager(deptId, null, SecurityContext.getCurrentUsername());
                deptRepo.changeStatus(deptId, status, SecurityContext.getCurrentUsername());
            });
        } else if(status.equals(OtherEnums.Department.ACTIVE.getValue())) {
            TransactionManager.runInTransaction(() -> {
                deptRepo.changeStatus(deptId, status, SecurityContext.getCurrentUsername());
            });
        } else {
            throw new IllegalOperationException("Invalid status: " + status);
        }
    }

    @Override
    public PageResponseDTO<EmployeeSummaryDTO> getEmployeesInDept(Long deptId, Integer currentPage, Integer pageSize) {
        String scope = PermissionChecker.getHighestScope(PermissionConstants.Employee.VIEW);
        if (!scope.equals(OtherEnums.Scope.ALL.getValue())) {
            if (!SecurityContext.getCurrentDeptId().equals(deptId))
                throw new AccessDeniedException("Not allowed to view other department's employees!");
        }
        EmployeeSearchBuilder builder = new EmployeeSearchBuilder.Builder()
            .setStatus(List.of(EmployeeEnums.Status.ACTIVE.getValue(), EmployeeEnums.Status.PENDING.getValue(), EmployeeEnums.Status.ON_LEAVE.getValue())) 
            .build();
        return empRepo.search(builder, deptId, currentPage, pageSize!=null ? pageSize : SystemConfig.getPageSize());
    }
    
    
}

