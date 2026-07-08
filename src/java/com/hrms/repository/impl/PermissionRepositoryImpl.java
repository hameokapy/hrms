
package com.hrms.repository.impl;

import com.hrms.model.mapper.builder.PermissionSearchBuilder;
import com.hrms.model.dto.response.PermissionDTO;
import com.hrms.model.entity.PermissionEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import com.hrms.repository.PermissionRepository;
import com.hrms.utils.DataMapper;

public class PermissionRepositoryImpl extends AbstractDAO<PermissionEntity> implements PermissionRepository {

    @Override
    public List<PermissionDTO> search(PermissionSearchBuilder builder) {
        StringBuilder sql = new StringBuilder("SELECT p.id, p.permission_key, p.description, r.role_name FROM permissions p "
                + "LEFT JOIN role_permission rp ON rp.permission_id = p.id LEFT JOIN roles r ON r.id = rp.role_id WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        if(builder.getId() != null) {
            sql.append(" AND p.id = ? ");
            params.add(builder.getId());
        }
        if(builder.getPermissionKey()!=null && !builder.getPermissionKey().trim().isEmpty()){
            sql.append(" AND p.permission_key LIKE ? ");
            params.add("%" + builder.getPermissionKey().trim() + "%");
        }
        if(builder.getDescription()!=null && !builder.getDescription().trim().isEmpty()){
            sql.append(" AND p.description LIKE ? ");
            params.add("%" + builder.getDescription().trim() + "%");
        }
        if(builder.getRoleName()!=null && !builder.getRoleName().trim().isEmpty()){
            sql.append(" AND r.role_name LIKE ? ");
            params.add("%" + builder.getRoleName().trim() + "%");
        }
        Map<Long, PermissionDTO> tempMap = new HashMap<>();
        queryList(sql.toString(), rs -> {
            Long id = rs.getLong("id");
            if(!tempMap.containsKey(id)){
                PermissionDTO dto = DataMapper.mapResultSetToObject(rs, PermissionDTO.class);
                dto.setRoleNames(new HashSet<>());
                tempMap.put(id, dto);
            }
            String roleName = rs.getString("role_name");
            if(roleName!=null)
                tempMap.get(id).getRoleNames().add(roleName);
            return null;
        }, params.toArray());
        return new ArrayList<>(tempMap.values());
    }

    @Override
    public PermissionEntity findById(Long id) {
        String sql = "SELECT * FROM permissions WHERE id = ?";
        return querySingle(sql, PermissionEntity.class, id);
    }
//
//    @Override
//    public PermissionEntity findByKey(String permissionKey) {
//        String sql = "SELECT * FROM permissions WHERE permission_key = ?";
//        return querySingle(sql, PermissionEntity.class, permissionKey);
//    }

    @Override
    public void updateDescription(Long id, String description) {
        String sql = "UPDATE permissions SET description = ? WHERE id = ?";
        update(sql, description, id);
    }
    
}
