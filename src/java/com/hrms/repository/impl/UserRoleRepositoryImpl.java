
package com.hrms.repository.impl;

import com.hrms.model.entity.RoleEntity;
import com.hrms.model.entity.UserRoleEntity;
import java.util.List;
import com.hrms.repository.UserRoleRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import com.hrms.utils.DataMapper;

public class UserRoleRepositoryImpl extends AbstractDAO<UserRoleEntity> implements UserRoleRepository {
    @Override
    public Map<Long, Set<String>> getRoleNamesByUserIds(List<Long> userIds){
        if (userIds==null || userIds.isEmpty())
            return new HashMap<>();
        String concatUserId = userIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = "SELECT ur.user_id, r.role_name FROM user_role ur JOIN roles r ON r.id = ur.role_id WHERE ur.user_id IN ("
                + concatUserId + ")";
        Map<Long, Set<String>> map = new HashMap<>();
        queryList(sql, rs -> {
            Long uid = rs.getLong("user_id");
            String roleName = rs.getString("role_name");
            map.computeIfAbsent(uid, roles -> new HashSet<>()).add(roleName);
            return null;
        }, userIds.toArray());
        return map;
    }
    
    @Override
    public Set<String> getRoleNamesByUserId(Long userId) {
        String sql = "SELECT r.role_name FROM user_role ur JOIN roles r ON r.id = ur.role_id WHERE ur.user_id = ?";
        Set<String> roleNames = new HashSet<>();
        queryList(sql, rs -> {
            roleNames.add(rs.getString("role_name"));
            return null;
        }, userId);
        return roleNames;
    }
    
    @Override
    public List<UserRoleEntity> getUserRolesByUserId(Long userid) {
        String sql = "SELECT ur.*, r.role_name, r.description FROM user_role ur JOIN roles r ON ur.role_id = r.id WHERE ur.user_id = ?";
        return queryList(sql, rs -> {
            UserRoleEntity ur = DataMapper.mapResultSetToObject(rs, UserRoleEntity.class);
            if (rs.getObject("role_id") != null) {
                RoleEntity role = new RoleEntity();
                role.setId(rs.getLong("role_id"));
                role.setRoleName(rs.getString("role_name"));
                role.setDescription(rs.getString("description"));
                ur.setRole(role);
            }
            return ur;
        }, userid);
    }
    
    @Override
    public void assignRolesToUser(Long userId, List<Long> roleIds, String assignedBy) {
        String sql = "INSERT INTO user_role (user_id, role_id, assigned_by, assigned_date) "
                   + "VALUES (?, ?, ?, GETDATE())";
        List<Object[]> paramList = new ArrayList<>();
        for (Long roleId : roleIds) {
            paramList.add(new Object[] {userId, roleId, assignedBy});
        }
        executeBatch(sql, paramList);
    }
    
    @Override
    public void removeAllRoles(Long userid){
        String sql = "DELETE FROM user_role WHERE user_id = ?";
        update(sql, userid);
    }

//    @Override
//    public void removeRolesFromUser(Long userId, List<Long> roleIds) {
//        String sql = "DELETE FROM user_role WHERE user_id = ? AND role_id = ?";
//        List<Object[]> paramList = new ArrayList<>();
//        for (Long roleId : roleIds) {
//            paramList.add(new Object[] {userId, roleId});
//        }
//        executeBatch(sql, paramList);
//    }

//    @Override
//    public Map<Long, Integer> countAllUsersGroupByRole() {
//        String sql = "SELECT role_id, COUNT(*) as soLuong FROM user_role GROUP BY role_id";
//        return queryMap(sql, rs -> rs.getLong("role_id"), rs -> rs.getInt("soLuong"));
//    }
    
    
    
}
