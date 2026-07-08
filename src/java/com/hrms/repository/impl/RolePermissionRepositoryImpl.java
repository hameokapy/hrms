
package com.hrms.repository.impl;

import com.hrms.model.entity.RolePermissionEntity;
import java.util.*;
import java.util.stream.Collectors;
import com.hrms.model.entity.PermissionEntity;
import com.hrms.repository.RolePermissionRepository;
import com.hrms.utils.DataMapper;

public class RolePermissionRepositoryImpl extends AbstractDAO<RolePermissionEntity> implements RolePermissionRepository {
    @Override
    public Map<Long, Map<String, Set<String>>> getAllRolePermissionMappings() {
        String sql = "SELECT rp.role_id, p.permission_key, rp.scope FROM role_permission rp JOIN permissions p ON rp.permission_id = p.id";
        List<RolePermissionEntity> rawList = queryList(sql,  rs -> {
            RolePermissionEntity result = DataMapper.mapResultSetToObject(rs, RolePermissionEntity.class);
            if (rs.getObject("permission_key") != null) {
                PermissionEntity permi = new PermissionEntity();
                permi.setPermissionKey(rs.getString("permission_key"));
                result.setPermission(permi);
            };
            return result;
        });
        return rawList.stream().collect(Collectors.groupingBy(RolePermissionEntity::getRoleId,
            Collectors.groupingBy(rp -> rp.getPermission().getPermissionKey(),
            Collectors.mapping(RolePermissionEntity::getScope, Collectors.toSet()))
        ));
    }
    
//    @Override
//    public List<RolePermissionEntity> getRolePermissions(Integer roleId) {
//        String sql = "SELECT rp.*, p.permission_key, p.description FROM role_permission rp JOIN permissions p "
//                + "ON rp.permission_id = p.id WHERE rp.role_id = ?";                        
//        return queryList(sql, rs -> {
//            RolePermissionEntity rp = DataMapper.mapResultSetToObject(rs, RolePermissionEntity.class);
//            // Cấp data vào biến transient của RolePermissionEntity
//            if (rs.getObject("permission_id") != null) {
//            PermissionEntity permi = new PermissionEntity();
//                permi.setId(rs.getLong("permission_id"));
//                permi.setPermissionKey(rs.getString("permission_key"));
//                permi.setDescription(rs.getString("description"));
//                rp.setPermission(permi);
//            };
//            return rp;
//        }, roleId);
//    }

//    @Override
//    public void assignPermissionsToRole(Long roleId, List<Long> permissionIds, String assignedBy) {
//        if (permissionIds == null || permissionIds.isEmpty()) 
//            return;
//        String sql = "INSERT INTO role_permission(role_id, permission_id, assigned_date, assigned_by) VALUES (?, ?, NOW(), ?)";
//        List<Object[]> paramList = new ArrayList<>();
//        for (Long pid : permissionIds) {
//            paramList.add(new Object[] {roleId, pid, assignedBy});
//        }
//        executeBatch(sql, paramList);
//    }
//
//    @Override
//    public void removePermissionsFromRole(Long roleId, List<Long> permissionIds) {
//        if (permissionIds == null || permissionIds.isEmpty()) 
//            return;
//        String sql = "DELETE FROM role_permission WHERE role_id = ? AND permission_id = ?";
//        List<Object[]> paramList = new ArrayList<>();
//        for (Long pid : permissionIds) {
//            paramList.add(new Object[] {roleId, pid});
//        }
//        executeBatch(sql, paramList);
//    }
    
    
}
