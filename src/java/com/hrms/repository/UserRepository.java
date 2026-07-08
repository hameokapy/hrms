
package com.hrms.repository;

import com.hrms.model.dto.common.PageResponseDTO;
import com.hrms.model.mapper.builder.UserSearchBuilder;
import com.hrms.model.entity.UserEntity;

public interface UserRepository extends GenericDAO<UserEntity> {
    PageResponseDTO<UserEntity> search(UserSearchBuilder builder, Integer currentPage, Integer pageSize);
    UserEntity findByUserId(Long userId);
    UserEntity findByUsername(String username);
    Long create(UserEntity user); 
    void updateEmployee(Long userId, Long employeeId, String modifiedBy);
    void updatePassword(Long userId, String passwordHash, String modifiedBy);
    void updateStatus(Long userId, Boolean isActive, String modifiedBy); // aka soft delete or restore
    void updateLastLogin(Long userId);
    // Hàm bổ trợ các class service:
    boolean existEmployeeId(Long employeeId);
    void clearEmployeeReference(Long empId);
}
