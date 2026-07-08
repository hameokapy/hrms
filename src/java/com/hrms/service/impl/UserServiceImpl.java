package com.hrms.service.impl;

import com.hrms.core.config.SystemConfig;
import com.hrms.model.mapper.builder.UserSearchBuilder;
import com.hrms.model.mapper.converter.UserConverter;
import com.hrms.model.dto.response.UserDetailDTO;
import com.hrms.model.dto.response.UserSummaryDTO;
import com.hrms.model.entity.EmployeeEntity;
import com.hrms.model.entity.UserEntity;
import com.hrms.core.constant.RoleEnums;
import java.util.*;
import java.util.stream.Collectors;
import com.hrms.repository.EmployeeRepository;
import com.hrms.repository.UserRepository;
import com.hrms.repository.UserRoleRepository;
import com.hrms.repository.impl.EmployeeRepositoryImpl;
import com.hrms.repository.impl.UserRepositoryImpl;
import com.hrms.repository.impl.UserRoleRepositoryImpl;
import com.hrms.core.security.PasswordHash;
import com.hrms.service.UserService;
import com.hrms.core.config.TransactionManager;
import com.hrms.core.constant.EmployeeEnums;
import com.hrms.core.exception.business.AccessDeniedException;
import com.hrms.core.exception.business.DuplicateResourceException;
import com.hrms.core.exception.business.IllegalOperationException;
import com.hrms.core.exception.business.ResourceNotFoundException;
import com.hrms.core.security.SecurityContext;
import com.hrms.model.dto.common.PageResponseDTO;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepo = new UserRepositoryImpl();
    private final UserRoleRepository userRoleRepo = new UserRoleRepositoryImpl();
    private final EmployeeRepository employeeRepo = new EmployeeRepositoryImpl();
    
    @Override
    public PageResponseDTO<UserSummaryDTO> searchUsers(UserSearchBuilder builder, Integer currentPage) {
        PageResponseDTO<UserEntity> entities = userRepo.search(builder, currentPage, SystemConfig.getPageSize());
        // entities.isEmpty() thì stream() vẫn chạy đc nên ko cần xét đk
        List<Long> userIds = entities.getContent().stream().map(u -> u.getId()).collect(Collectors.toList());
        Map<Long, Set<String>> roleMap = userRoleRepo.getRoleNamesByUserIds(userIds);
        List<UserSummaryDTO> dtos = entities.getContent().stream()
            .map(e -> UserConverter.toUserDTO(e, UserSummaryDTO.class, roleMap)).collect(Collectors.toList());
        return new PageResponseDTO<>(dtos, entities.getTotalElements(), entities.getCurrentPage(), entities.getPageSize());
    }

    @Override
    public UserDetailDTO getUserDetailByUserId(Long userId) {
        UserEntity entity = userRepo.findByUserId(userId);
        if(entity == null)
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        UserDetailDTO dto = UserConverter.toUserDTO(entity, UserDetailDTO.class, null);
        Set<String> roleMap = userRoleRepo.getRoleNamesByUserId(userId);
        dto.setRoleNames(roleMap);
        return dto;
    }

    @Override
    public Long createUser(String username, String password, Long employeeId) {
        if (userRepo.findByUsername(username) != null)
            throw new DuplicateResourceException("Username existed!");
        // Lưu ý: hàm validateEmployee() tính cả case tạo user nhưng để trống employeeId hộ r
        EmployeeEntity targetEmp = validateEmployee(employeeId);
        return TransactionManager.doInTransaction(() -> {
            UserEntity entity = new UserEntity();
            entity.setUsername(username);
            entity.setPassword(PasswordHash.hashPassword(password));
            entity.setCreatedBy(SecurityContext.getCurrentUsername());
            entity.setIsActive(true);
            entity.setEmployee(targetEmp);
            return userRepo.create(entity); 
        });
    }
    
    private EmployeeEntity validateEmployee(Long employeeId) {
        if (employeeId == null) 
            return null;
        EmployeeEntity emp = employeeRepo.findById(employeeId);
        if (emp==null || emp.getStatus().equals(EmployeeEnums.Status.INACTIVE.getValue())) 
            throw new ResourceNotFoundException("Employee not found or already inactive!");
        if (userRepo.existEmployeeId(employeeId)) 
            throw new IllegalOperationException("One employee per one account only!");
        return emp;
    }

    @Override
    public void bindEmployeeToUser(Long userId, Long employeeId) {
        UserEntity user = userRepo.findByUserId(userId);
        if (user==null || !user.getIsActive()) 
            throw new ResourceNotFoundException("User not found or already inactive!");
        validateEmployee(employeeId);        
        TransactionManager.runInTransaction(() -> {
            userRepo.updateEmployee(userId, employeeId, SecurityContext.getCurrentUsername());
        });
    }

    @Override
    public void toggleUserStatus(Long userId, Boolean isActive) {
        Long currentLoggedInUserId = SecurityContext.getCurrentUserId();
        UserEntity user = userRepo.findByUserId(userId);
        if (user == null) 
            throw new ResourceNotFoundException("User not found: " + userId);
        if(userId.equals(currentLoggedInUserId) && !isActive)
            throw new AccessDeniedException("Deactivating your own account is forbidden!");
        TransactionManager.runInTransaction(() -> {
            String modifiedBy = SecurityContext.getCurrentUsername();
            userRepo.updateStatus(userId, isActive, modifiedBy);
            if(!isActive) {
                userRepo.updateEmployee(userId, null, modifiedBy);
                userRoleRepo.removeAllRoles(userId);
            }
        });
    }

    @Override
    public void resetPassword(Long userId, String newPassword) {
        if (userRepo.findByUserId(userId) == null) 
            throw new ResourceNotFoundException("User not found: " + userId);
        //TODO: Cân nhắc cơ chế forceChangePasswordNextLogin và clearSessionDataImmediately
        TransactionManager.runInTransaction(() -> {
            String hashedPassword = PasswordHash.hashPassword(newPassword);
            userRepo.updatePassword(userId, hashedPassword, SecurityContext.getCurrentUsername());
        });
    }

    @Override
    public void updateUserRoles(Long userId, List<Long> roleIds) {
        Long currentLoggedInUserId = SecurityContext.getCurrentUserId();
        UserEntity user = userRepo.findByUserId(userId);
        if (user==null || !user.getIsActive()) 
            throw new ResourceNotFoundException("User not found or already inactive!");
        if (roleIds!=null && roleIds.stream().anyMatch(id -> RoleEnums.fromId(id)==null)) 
            throw new IllegalOperationException("Invalid roleID(s) found!");
        if (userId.equals(currentLoggedInUserId)) {
            boolean hasAdminRole = roleIds!=null && roleIds.contains(RoleEnums.ADMIN.getId());
            if (!hasAdminRole)
                throw new AccessDeniedException("Revoking your own Admin role is forbidden!");
        }
        TransactionManager.runInTransaction(() -> {
            userRoleRepo.removeAllRoles(userId);
            if (roleIds!=null && !roleIds.isEmpty()) 
                userRoleRepo.assignRolesToUser(userId, roleIds, SecurityContext.getCurrentUsername());
        });
    }
    
}
