
package com.hrms.service;

import com.hrms.model.dto.common.PageResponseDTO;
import com.hrms.model.mapper.builder.UserSearchBuilder;
import com.hrms.model.dto.response.UserDetailDTO;
import com.hrms.model.dto.response.UserSummaryDTO;
import java.util.List;

public interface UserService {
    PageResponseDTO<UserSummaryDTO> searchUsers(UserSearchBuilder builder, Integer currentPage);
    UserDetailDTO getUserDetailByUserId(Long userId);
    Long createUser(String username, String password, Long employeeId);
    // Method nhánh update user_role:
    void updateUserRoles(Long userId, List<Long> roleIds);
    // Methods nhánh update users:
    void bindEmployeeToUser(Long userId, Long employeeId);
    void toggleUserStatus(Long userId, Boolean isActive);
    void resetPassword(Long userId, String newPassword);
    //TODO: void changeOwnPassword(String username, String oldPassword, String newPassword);
    //Nếu là User tự update: Nên lấy userId trực tiếp từ SecurityContext.getCurrentUserId() để đảm bảo họ ko đổi hộ người khác
}
