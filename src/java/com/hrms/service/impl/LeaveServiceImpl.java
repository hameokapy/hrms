
package com.hrms.service.impl;

import com.hrms.core.config.SystemConfig;
import com.hrms.core.config.TransactionManager;
import com.hrms.core.constant.EmployeeEnums;
import com.hrms.core.constant.LeaveRequestEnums;
import com.hrms.core.constant.OtherEnums;
import com.hrms.core.constant.PermissionConstants;
import com.hrms.core.exception.business.AccessDeniedException;
import com.hrms.core.exception.business.DuplicateResourceException;
import com.hrms.core.exception.business.IllegalOperationException;
import com.hrms.core.exception.business.ResourceNotFoundException;
import com.hrms.core.security.PermissionChecker;
import com.hrms.core.security.SecurityContext;
import com.hrms.model.dto.common.PageResponseDTO;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import com.hrms.model.dto.request.LeaveRequestRequestDTO;
import com.hrms.model.dto.response.LeaveBalanceSummaryDTO;
import com.hrms.model.dto.response.LeaveRequestDTO;
import com.hrms.model.entity.EmployeeEntity;
import com.hrms.model.entity.LeaveBalanceEntity;
import com.hrms.model.entity.LeaveRequestEntity;
import com.hrms.model.mapper.builder.LeaveBalanceSearchBuilder;
import com.hrms.model.mapper.builder.LeaveRequestSearchBuilder;
import com.hrms.repository.EmployeeRepository;
import com.hrms.repository.LeaveBalanceRepository;
import com.hrms.repository.LeaveRequestRepository;
import com.hrms.repository.impl.EmployeeRepositoryImpl;
import com.hrms.repository.impl.LeaveBalanceRepositoryImpl;
import com.hrms.repository.impl.LeaveRequestRepositoryImpl;
import com.hrms.utils.DataMapper;
import com.hrms.service.LeaveService;

public class LeaveServiceImpl implements LeaveService {
    
    private final LeaveRequestRepository leaveRequestRepo = new LeaveRequestRepositoryImpl();
    private final EmployeeRepository empRepo = new EmployeeRepositoryImpl();
    private final LeaveBalanceRepository leaveBalanceRepo = new LeaveBalanceRepositoryImpl();

    @Override
    public PageResponseDTO<LeaveRequestDTO> searchRequest(LeaveRequestSearchBuilder builder, Integer currentPage, Integer pageSize) {
        if (builder.getFromDate()!=null && builder.getToDate()!=null) {
            if (builder.getFromDate().isAfter(builder.getToDate()))
                throw new IllegalOperationException("FromDate cannot be greater than ToDate.");
        }
        Long scopeEmpId = null;
        Long scopeDeptId = null;
        String scope = PermissionChecker.getHighestScope(PermissionConstants.Leave.VIEW_REQUEST);
        if(scope.equals(OtherEnums.Scope.OWN.getValue())) {
            scopeEmpId = SecurityContext.getCurrentEmpId();
        } else if (scope.equals(OtherEnums.Scope.DEPT.getValue())) {
            scopeDeptId = SecurityContext.getCurrentDeptId();
        }
        return leaveRequestRepo.search(builder, scopeEmpId, scopeDeptId, currentPage, pageSize!=null ? pageSize : SystemConfig.getPageSize());
    }

    @Override
    public Long createRequest(LeaveRequestRequestDTO request) {
        //TODO: department chưa có manager thì throw excp với mess: phòng ban chưa có qly, liên hệ HR!
        Long empId = request.getEmployeeId();
        LocalDate start = request.getStartDate();
        LocalDate end = request.getEndDate();
        String type = request.getType();
        
        String permission = PermissionConstants.Leave.CREATE_REQUEST;
        checkScope(empId, permission);
        checkDates(start, end, permission);
        EmployeeEntity emp = empRepo.findById(empId);
        if (emp==null || !emp.getStatus().equalsIgnoreCase(EmployeeEnums.Status.ACTIVE.getValue()))
            throw new ResourceNotFoundException("Employee not found or not active!");
        if(leaveRequestRepo.existOverlap(empId, start, end))
            throw new DuplicateResourceException("Duplicated leave request not allowed.");
        if(!LeaveRequestEnums.Type.isValid(type))
            throw new IllegalOperationException("Invalid request type: ANNUAL/SICK/UNPAID.");
        int totalDays = (int) start.until(end, ChronoUnit.DAYS) + 1;
        checkBalance(empId, type, totalDays, start.getYear());
        return TransactionManager.doInTransaction(() -> {
            LeaveRequestEntity entity = DataMapper.mapObjectToObject(request, LeaveRequestEntity.class);
            entity.setEmployee(emp);
            entity.setCreatedBy(SecurityContext.getCurrentUsername());
            entity.setStatus(LeaveRequestEnums.Status.PENDING.getValue());
            return leaveRequestRepo.createRequest(entity);
        });
    }
    
    private void checkDates(LocalDate start, LocalDate end, String permission) {
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        
        if (start.isAfter(end)) 
            throw new IllegalOperationException("FromDate cannot be greater than ToDate.");
        if (start.getYear() != end.getYear()) 
            throw new IllegalOperationException("Cross-year request not allowed, must split into 2 requests.");
        
        String scope = PermissionChecker.getHighestScope(permission);
        if (OtherEnums.Scope.ALL.getValue().equals(scope)) {
            if (start.getYear() < currentYear - 1 || start.getYear() > currentYear + 1)
                throw new IllegalOperationException("Request year must be from year: " + (currentYear-1) + " to year: " + (currentYear+1));
        } else {
            if (start.isBefore(today) || start.getYear() > currentYear + 1)
                throw new IllegalOperationException("Request date must be from today to within year: " + (currentYear+1));
        }
    }
    
    private Long checkBalance(Long employeeId, String type, Integer days, Integer year) {
        LeaveBalanceEntity balance = Optional.ofNullable(leaveBalanceRepo.findByEmployeeIdAndYear(employeeId, year))
            .orElseThrow(() -> new ResourceNotFoundException("Employee's leave balance not yet initialized for " + year));
        if(type.equalsIgnoreCase(LeaveRequestEnums.Type.ANNUAL.getValue())) {
            if (balance.getAnnualRemainingDays() < days)
                throw new IllegalOperationException("Switch to UNPAID type! Not enough annual days left. Remaining: " + balance.getAnnualRemainingDays());
        } else if (type.equalsIgnoreCase(LeaveRequestEnums.Type.SICK.getValue())) {
            if (balance.getSickRemainingDays() < days)
                throw new IllegalOperationException("Switch to UNPAID type! Not enough sick days left. Remaining: " + balance.getSickRemainingDays());
        }
        return balance.getId();
    }

    @Override
    public void updateRequest(LeaveRequestRequestDTO request) {
        LeaveRequestEntity existing = Optional.ofNullable(leaveRequestRepo.findByLeaveRequestId(request.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Request not found."));
        if (!LeaveRequestEnums.Status.PENDING.getValue().equals(existing.getStatus()))
            throw new IllegalOperationException("Only pending request can be updated.");
        
        String permission = PermissionConstants.Leave.UPDATE_REQUEST;
        Long requestEmpId = checkScope(existing.getEmployee().getId(), permission);
        DataMapper.copyPropertiesIgnoreNull(request, existing);
        LocalDate start = existing.getStartDate();
        LocalDate end = existing.getEndDate();
        checkDates(start, end, permission);
        if (leaveRequestRepo.existOverlapExcludeSelf(existing.getId(), requestEmpId, start, end))
            throw new DuplicateResourceException("Duplicated leave request not allowed.");
        if (!LeaveRequestEnums.Type.isValid(existing.getType()))
            throw new IllegalOperationException("Invalid request type: ANNUAL/SICK/UNPAID.");
        int totalDays = (int) start.until(end, ChronoUnit.DAYS) + 1;
        checkBalance(requestEmpId, existing.getType(), totalDays, start.getYear());
        TransactionManager.runInTransaction(() -> {
            existing.setModifiedBy(SecurityContext.getCurrentUsername());
            leaveRequestRepo.updateRequest(existing);
        });
    } 
    
    private Long checkScope(Long requestEmpId, String permission) {
        Long currentEmpId = SecurityContext.getCurrentEmpId();
        String scope = PermissionChecker.getHighestScope(permission);
        if (OtherEnums.Scope.OWN.getValue().equals(scope) && !requestEmpId.equals(currentEmpId))
            throw new AccessDeniedException("Not allowed to create/update other employee's request.");
        return requestEmpId;
    }

    @Override
    public void cancelRequest(Long leaveRequestId) {
        LeaveRequestEntity existing = Optional.ofNullable(leaveRequestRepo.findByLeaveRequestId(leaveRequestId))
            .orElseThrow(() -> new ResourceNotFoundException("Request not found."));
        if (!LeaveRequestEnums.Status.PENDING.getValue().equals(existing.getStatus()))
            throw new IllegalOperationException("Only pending request can be updated.");
        checkScope(existing.getEmployee().getId(), PermissionConstants.Leave.CANCEL_REQUEST);
        TransactionManager.runInTransaction(() -> {
            leaveRequestRepo.updateStatusRequest(leaveRequestId, LeaveRequestEnums.Status.CANCELLED.getValue(), SecurityContext.getCurrentUsername(), null);
        });
    }

    @Override
    public void approveRequest(Long leaveRequestId, String newStatus) {
        LeaveRequestEntity existing = Optional.ofNullable(leaveRequestRepo.findByLeaveRequestId(leaveRequestId))
            .orElseThrow(() -> new ResourceNotFoundException("Request not found."));
        String oldStatus = existing.getStatus();
        if (oldStatus.equalsIgnoreCase(newStatus))
            return;
        boolean isFromPending = oldStatus.equals(LeaveRequestEnums.Status.PENDING.getValue()) && (newStatus.equals(LeaveRequestEnums.Status.APPROVED.getValue()) || newStatus.equals("REJECTED"));
        boolean isFromRejected = oldStatus.equals(LeaveRequestEnums.Status.REJECTED.getValue()) && newStatus.equals(LeaveRequestEnums.Status.APPROVED.getValue());
        boolean isFromApproved = oldStatus.equals(LeaveRequestEnums.Status.APPROVED.getValue()) && newStatus.equals(LeaveRequestEnums.Status.REJECTED.getValue());
        if (!(isFromPending || isFromRejected || isFromApproved))
            throw new IllegalOperationException("Not allowed to change from " + oldStatus + " to " + newStatus);
        String currentUser = SecurityContext.getCurrentUsername();
        int days = (int) existing.getStartDate().until(existing.getEndDate(), ChronoUnit.DAYS) + 1;
        TransactionManager.runInTransaction(() -> {
            if (LeaveRequestEnums.Status.APPROVED.getValue().equalsIgnoreCase(newStatus)) {
                Long balanceId = checkBalance(existing.getEmployee().getId(), existing.getType(), days, existing.getStartDate().getYear());
                updateBalance(balanceId, existing.getType(), days, currentUser); 
            } else if (LeaveRequestEnums.Status.APPROVED.getValue().equalsIgnoreCase(oldStatus)) {
                Long balanceId = leaveBalanceRepo.findByEmployeeIdAndYear(existing.getEmployee().getId(), existing.getStartDate().getYear()).getId();
                updateBalance(balanceId, existing.getType(), -days, currentUser); 
            }
            leaveRequestRepo.updateStatusRequest(leaveRequestId, newStatus, currentUser, currentUser);
        });
    }

    private void updateBalance(Long balanceId, String requestType, int deltaDays, String modifiedBy) {
        if (LeaveRequestEnums.Type.ANNUAL.getValue().equals(requestType)) {
            leaveBalanceRepo.updateAnnualUsage(balanceId, deltaDays, modifiedBy);
        } else if (LeaveRequestEnums.Type.SICK.getValue().equals(requestType)) {
            leaveBalanceRepo.updateSickUsage(balanceId, deltaDays, modifiedBy);
        }
    }

    @Override
    public PageResponseDTO<LeaveBalanceSummaryDTO> searchBalance(LeaveBalanceSearchBuilder builder, Integer currentPage) {
        String scope = PermissionChecker.getHighestScope(PermissionConstants.Leave.VIEW_BALANCE);
        Long scopeDeptId = scope.equals(OtherEnums.Scope.DEPT.getValue()) ? SecurityContext.getCurrentDeptId() : null;
        Long scopeEmpId = scope.equals(OtherEnums.Scope.OWN.getValue()) ? SecurityContext.getCurrentEmpId(): null;
        return leaveBalanceRepo.searchBalance(builder, scopeDeptId, scopeEmpId, currentPage, SystemConfig.getPageSize());
    }
}
    
    

