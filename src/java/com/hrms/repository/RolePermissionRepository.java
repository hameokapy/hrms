
package com.hrms.repository;

import com.hrms.model.entity.RolePermissionEntity;
import java.util.Map;
import java.util.Set;

public interface RolePermissionRepository extends GenericDAO<RolePermissionEntity> {
    Map<Long, Map<String, Set<String>>> getAllRolePermissionMappings();
//    List<RolePermissionEntity> getRolePermissions(Integer roleId);
//    void assignPermissionsToRole(Long roleId, List<Long> permissionIds, String assignedBy);
//    void removePermissionsFromRole(Long roleId, List<Long> permissionIds);
}
