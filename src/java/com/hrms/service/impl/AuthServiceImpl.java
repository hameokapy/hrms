
package com.hrms.service.impl;

import com.hrms.core.config.TransactionManager;
import com.hrms.core.exception.security.InvalidCredentialsException;
import com.hrms.core.security.PasswordHash;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.hrms.model.dto.response.LoginResponseDTO;
import com.hrms.model.entity.UserEntity;
import com.hrms.model.entity.UserRoleEntity;
import com.hrms.repository.UserRepository;
import com.hrms.repository.UserRoleRepository;
import com.hrms.repository.impl.UserRepositoryImpl;
import com.hrms.repository.impl.UserRoleRepositoryImpl;
import com.hrms.service.AuthService;

public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepo = new UserRepositoryImpl();
    private final UserRoleRepository userRoleRepo = new UserRoleRepositoryImpl();
    
    @Override
    public LoginResponseDTO login(String username, String password) {
        UserEntity user = userRepo.findByUsername(username);
        if(user==null)
            throw new InvalidCredentialsException("Invalid username or password.");
        if(!user.getIsActive())
            throw new InvalidCredentialsException("This account is disabled!");
        // === FOR TESTING PLAIN-TEXT PASSWORD ONLY ===
        // if(!password.equals(user.getPassword()))
        //    throw new RuntimeException("Invalid password");
        // ============================================
        if(!PasswordHash.verifyPassword(password, user.getPassword()))
            throw new InvalidCredentialsException("Invalid username or password.");
        List<UserRoleEntity> userRoles = userRoleRepo.getUserRolesByUserId(user.getId());
        if (userRoles == null || userRoles.isEmpty()) {
            throw new InvalidCredentialsException("Account not authorized, contact admin!");
        }
        Set<Long> roleIds = new HashSet<>();
        for (UserRoleEntity userRole : userRoles) {
            if(userRole.getRole() != null) {
                roleIds.add(userRole.getRoleId());
            }
        }
        LoginResponseDTO response = new LoginResponseDTO();
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        if (user.getEmployee() != null) {
            response.setEmployeeName(user.getEmployee().getFullName());
            response.setEmployeeId(user.getEmployee().getId());
            if(user.getEmployee().getDepartment()!=null)
                response.setDeptId(user.getEmployee().getDepartment().getId());
        }
        response.setRoleIds(roleIds);
        updateLastLogin(user.getId());
        return response;
    }

    private void updateLastLogin(Long userId) {
        try {
            // Chỉ cái này cần TransactionManager thôi, mấy cái trên dạng search nên ko cần
            TransactionManager.runInTransaction(() -> {userRepo.updateLastLogin(userId);});
        } catch(Exception e) {
            System.err.println("Updating last_login time failed.");
            // Setup vậy để lỗi cx ko đc phá khâu login ở trên
        }
    }
    
}
