
package com.hrms.repository;

import com.hrms.model.entity.UserRoleEntity;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserRoleRepository extends GenericDAO<UserRoleEntity> {
    Map<Long, Set<String>> getRoleNamesByUserIds(List<Long> userIds);
    Set<String> getRoleNamesByUserId(Long userId);
    List<UserRoleEntity> getUserRolesByUserId(Long userid);
    void assignRolesToUser(Long userid, List<Long> roleIds, String assignedBy);
    void removeAllRoles (Long userid);
//    void removeRolesFromUser(Long userid, List<Long> roleIds);
//    Map<Long, Integer> countAllUsersGroupByRole();
    
}
