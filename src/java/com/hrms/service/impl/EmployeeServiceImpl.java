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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.hrms.model.dto.request.EmployeeRequestDTO;
import com.hrms.model.dto.response.EmployeeDetailDTO;
import com.hrms.model.dto.response.EmployeeSummaryDTO;
import com.hrms.model.entity.DepartmentEntity;
import com.hrms.model.entity.EmployeeEntity;
import com.hrms.model.entity.LeaveBalanceEntity;
import com.hrms.model.entity.PositionEntity;
import com.hrms.model.mapper.builder.EmployeeSearchBuilder;
import com.hrms.repository.DepartmentRepository;
import com.hrms.repository.EmployeeRepository;
import com.hrms.repository.LeaveBalanceRepository;
import com.hrms.repository.PositionRepository;
import com.hrms.repository.UserRepository;
import com.hrms.repository.impl.DepartmentRepositoryImpl;
import com.hrms.repository.impl.EmployeeRepositoryImpl;
import com.hrms.repository.impl.LeaveBalanceRepositoryImpl;
import com.hrms.repository.impl.PositionRepositoryImpl;
import com.hrms.repository.impl.SystemSettingRepositoryImpl;
import com.hrms.repository.impl.UserRepositoryImpl;
import com.hrms.service.EmployeeService;
import com.hrms.utils.DataMapper;

public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository empRepo = new EmployeeRepositoryImpl();
    private final DepartmentRepository deptRepo = new DepartmentRepositoryImpl();
    private final PositionRepository posiRepo = new PositionRepositoryImpl();
    private final SystemSettingRepositoryImpl systemRepo = new SystemSettingRepositoryImpl();
    private final LeaveBalanceRepository leaveBalanceRepo = new LeaveBalanceRepositoryImpl();
    private final UserRepository userRepo = new UserRepositoryImpl();

    @Override
    public PageResponseDTO<EmployeeSummaryDTO> search(EmployeeSearchBuilder builder, Integer currentPage, Integer pageSize) {
        String scope = PermissionChecker.getHighestScope(PermissionConstants.Employee.VIEW);
        Long scopeDeptId = scope.equals(OtherEnums.Scope.DEPT.getValue()) ? SecurityContext.getCurrentDeptId() : null;
        List<String> builderStatuses = builder.getStatus();
        List<String> finalStatuses;
        
        if (scope.equals(OtherEnums.Scope.ALL.getValue())) {
            finalStatuses = builderStatuses!=null ? builderStatuses : new ArrayList<>();
        } else {
            List<String> whiteList = new ArrayList<>();
            whiteList.add(EmployeeEnums.Status.ACTIVE.getValue());
            whiteList.add(EmployeeEnums.Status.ON_LEAVE.getValue());
            if (SecurityContext.getCurrentRoleIds().contains(RoleEnums.MANAGER.getId())) 
                whiteList.add(EmployeeEnums.Status.PENDING.getValue());
            
            finalStatuses = Optional.ofNullable(builderStatuses).filter(list -> !list.isEmpty())
                .map(list -> list.stream().map(item -> item.toUpperCase()).filter(item -> whiteList.contains(item))
                .collect(Collectors.toList()))
                .map(list -> list.isEmpty() ? List.of("NONE_EXIST_STATUS") : list)
                .orElse(new ArrayList<>(whiteList));
        }
        return empRepo.search(builder.toBuilder().setStatus(finalStatuses).build(), scopeDeptId, currentPage, pageSize!=null ? pageSize : SystemConfig.getPageSize());
    }

    @Override
    public EmployeeDetailDTO searchById(Long empId) {
        EmployeeDetailDTO emp = Optional.ofNullable(empRepo.searchById(empId)).orElseThrow(
                () -> new ResourceNotFoundException("Employee not found with ID: " + empId));
        String scope = PermissionChecker.getHighestScope(PermissionConstants.Employee.VIEW_DETAIL);
        boolean isAllScope = scope.equals(OtherEnums.Scope.ALL.getValue());
        boolean isDeptScope = scope.equals(OtherEnums.Scope.DEPT.getValue())
                && emp.getDepartmentId().equals(SecurityContext.getCurrentDeptId());
        boolean isOwnScope = scope.equals(OtherEnums.Scope.OWN.getValue())
                && emp.getId().equals(SecurityContext.getCurrentEmpId());
        if (isAllScope || isDeptScope || isOwnScope) {
            return emp;
        }
        throw new AccessDeniedException("Not allowed to view this employee's info.");
    }

    @Override
    public Long createEmployee(EmployeeRequestDTO request) {
        if (empRepo.existEmail(request.getEmail())) 
            throw new DuplicateResourceException("Email existed.");
        if (empRepo.existPhone(request.getPhone())) 
            throw new DuplicateResourceException("Phone existed.");
        if (!deptRepo.isActive(request.getDepartmentId())) 
            throw new ResourceNotFoundException("Department not found or inactive.");
        if (!posiRepo.isActive(request.getPositionId())) 
            throw new ResourceNotFoundException("Position not found or inactive.");
        String createdBy = SecurityContext.getCurrentUsername();
        return TransactionManager.doInTransaction(() -> {
            EmployeeEntity entity = DataMapper.mapObjectToObject(request, EmployeeEntity.class);
            entity.setEmployeeCode(generateEmployeeCode());
            entity.setStatus(EmployeeEnums.Status.PENDING.getValue());
            entity.setDepartment(new DepartmentEntity(request.getDepartmentId()));
            entity.setPosition(new PositionEntity(request.getPositionId()));
            entity.setCreatedBy(createdBy);
            Long newEmpId = empRepo.createEmployee(entity);
            initEmployeeLeaveBalance(newEmpId, createdBy);
            return newEmpId;
        });
    }

    // Dùng synchronized để đảm bảo chỉ 1 thread đc reset sequence tại 1 thời điểm
    private synchronized String generateEmployeeCode() {
        int currentYear = LocalDate.now().getYear();
        int lastYearInDatabase = SystemConfig.getInt(SystemConfig.Key.LAST_GEN_YEAR.getValue());
        if (currentYear > lastYearInDatabase) {
            empRepo.resetSequence();
            systemRepo.updateYearConfig(currentYear);
            SystemConfig.reload();
        }
        Long nextSeq = empRepo.getNextSequenceValue();
        return String.format("EMP%d%05d", currentYear, nextSeq);
    }

    private void initEmployeeLeaveBalance(Long empId, String createdBy) {
        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();
        int defaultAnnual = SystemConfig.getInt(SystemConfig.Key.ANNUAL_LEAVE_DAYS.getValue());
        int defaultSick = SystemConfig.getInt(SystemConfig.Key.SICK_LEAVE_DAYS.getValue());
        // Cơ chế: Cty cấp sick days full bất kể tháng join, nhưng annual days thì phải trừ bớt đi
        int initialAnnual = (int) Math.round((defaultAnnual / 12.0) * (12 - currentMonth + 1));
        int initialSick = defaultSick;
        LeaveBalanceEntity entity = new LeaveBalanceEntity();
        entity.setAnnualTotalDays(initialAnnual);
        entity.setSickTotalDays(initialSick);
        entity.setYear(currentYear);
        entity.setCreatedBy(createdBy);
        entity.setEmployee(new EmployeeEntity(empId));
        leaveBalanceRepo.create(entity);
    }

    @Override
    public void updateGeneral(EmployeeRequestDTO request) {
        EmployeeEntity entity = empRepo.findById(request.getId());
        if (entity == null) 
            throw new ResourceNotFoundException("Employee not found.");
        String scope = PermissionChecker.getHighestScope(PermissionConstants.Employee.EDIT);
        if (OtherEnums.Scope.OWN.getValue().equals(scope) && !entity.getId().equals(SecurityContext.getCurrentEmpId())) 
            throw new AccessDeniedException("Not allowed to edit other employee's info.");
        if (request.getEmail()!=null && !request.getEmail().equals(entity.getEmail())) {
            if (empRepo.existEmail(request.getEmail())) 
                throw new DuplicateResourceException("Email existed.");
        } 
        if (request.getPhone()!=null && !request.getPhone().equals(entity.getPhone())) {
            if (empRepo.existPhone(request.getPhone())) 
                throw new DuplicateResourceException("Phone existed.");
        }
        TransactionManager.runInTransaction(() -> {
            // Dính chia scope nên ko map tự động DataMapper.copyPropertiesNotNull đc
            if (request.getPhone() != null)
                entity.setPhone(request.getPhone());
            if (OtherEnums.Scope.ALL.getValue().equals(scope)) {
                if (request.getFullName() != null) 
                    entity.setFullName(request.getFullName());
                if (request.getEmail() != null) 
                    entity.setEmail(request.getEmail());
            }
            entity.setModifiedBy(SecurityContext.getCurrentUsername());
            empRepo.updateGeneral(entity);
        });
    }

    @Override
    public void assignDepartmentInBulk(List<Long> empIds, Long deptId) {
        if (empIds==null || empIds.isEmpty()) 
            throw new IllegalOperationException("Empty list of employee ids found.");
        if (!deptRepo.isActive(deptId)) 
            throw new ResourceNotFoundException("Department not found or inactive.");
        List<EmployeeEntity> emps = empRepo.findAllByIds(empIds);
        List<Long> finalIdsToUpdate = emps.stream().filter(e -> !e.getStatus().equals(EmployeeEnums.Status.INACTIVE.getValue()))
            .map(e -> e.getId()).collect(Collectors.toList());
        if (finalIdsToUpdate.isEmpty())
            throw new IllegalOperationException("No valid employee to update.");
        List<Long> managerDeptIds = deptRepo.findDeptIdsByManagers(finalIdsToUpdate);
        if (!managerDeptIds.isEmpty())
            throw new IllegalOperationException("Not allowed to move managers out, must unassign their manager roles first!");
        TransactionManager.runInTransaction(() -> {
            empRepo.assignDepartmentInBulk(finalIdsToUpdate, deptId, SecurityContext.getCurrentUsername());
        });
    }

    @Override
    public void assignPositionInBulk(List<Long> empIds, Long posiId) {
        if (empIds==null || empIds.isEmpty()) 
            throw new IllegalOperationException("Empty list of employee ids found.");
        if (!posiRepo.isActive(posiId))
            throw new ResourceNotFoundException("Position not found or inactive.");
        List<EmployeeEntity> emps = empRepo.findAllByIds(empIds);
        List<Long> finalIdsToUpdate = emps.stream().filter(e -> !e.getStatus().equals(EmployeeEnums.Status.INACTIVE.getValue()))
            .map(e -> e.getId()).collect(Collectors.toList());
        if (finalIdsToUpdate.isEmpty()) 
            throw new IllegalOperationException("No valid employee to update.");
        TransactionManager.runInTransaction(() -> {
            empRepo.assignPositionInBulk(finalIdsToUpdate, posiId, SecurityContext.getCurrentUsername());
        });
    }

    @Override
    public void changeStatus(Long empId, String newStatus) {
        EmployeeEntity emp = empRepo.findById(empId);
        if (emp == null) 
            throw new ResourceNotFoundException("Employee not found.");
        String oldStatus = emp.getStatus();
        if (!isValidTransition(EmployeeEnums.Status.fromString(oldStatus), EmployeeEnums.Status.fromString(newStatus)))
            throw new IllegalOperationException("Not allowed to switch from " + oldStatus + " to " + newStatus);
        if (newStatus.equals(EmployeeEnums.Status.INACTIVE.getValue()) && deptRepo.isManager(emp.getId()))
            throw new IllegalOperationException("Employee with manager role must be removed from that role first!");
        TransactionManager.runInTransaction(() -> {
            switch (newStatus) {
                case "INACTIVE" -> {
                    userRepo.clearEmployeeReference(emp.getId());
                    //TODO: bảng contracts đưa contract active về terminated
                }
                case "ACTIVE" -> {
                    if (oldStatus.equals(EmployeeEnums.Status.PENDING.getValue()) || oldStatus.equals(EmployeeEnums.Status.INACTIVE.getValue())) {
                        //TODO: phải có contract active mới đc chuyển status sang active
                    }
                    if (oldStatus.equals(EmployeeEnums.Status.INACTIVE.getValue())) {
                        if (leaveBalanceRepo.findByEmployeeIdAndYear(emp.getId(), LocalDate.now().getYear()) == null)
                            initEmployeeLeaveBalance(emp.getId(), SecurityContext.getCurrentUsername());
                    }
                    // case ON_LEAVE -> ACTIVE: hiện ko có ảnh hưởng gì
                }
                case "ON_LEAVE" -> {
                    // case ACTIVE -> ON_LEAVE: hiện ko có ảnh hưởng gì
                }
            }
            empRepo.updateStatus(empId, newStatus, SecurityContext.getCurrentUsername());
        });
    }
    
    private boolean isValidTransition(EmployeeEnums.Status oldStatus, EmployeeEnums.Status newStatus) {
        if (oldStatus==newStatus || oldStatus==null || newStatus==null) 
            return false;
        return switch (oldStatus) {
            case PENDING -> newStatus == EmployeeEnums.Status.ACTIVE 
                         || newStatus == EmployeeEnums.Status.INACTIVE;
            case ACTIVE -> newStatus == EmployeeEnums.Status.ON_LEAVE 
                        || newStatus == EmployeeEnums.Status.INACTIVE;
            case ON_LEAVE -> newStatus == EmployeeEnums.Status.ACTIVE 
                          || newStatus == EmployeeEnums.Status.INACTIVE;
            case INACTIVE -> newStatus == EmployeeEnums.Status.ACTIVE;
            default -> false;
        };
    }

}
