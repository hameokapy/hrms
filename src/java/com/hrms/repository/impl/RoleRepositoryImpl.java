
package com.hrms.repository.impl;

import com.hrms.model.mapper.builder.RoleSearchBuilder;
import com.hrms.model.dto.response.RoleDTO;
import com.hrms.model.entity.RoleEntity;
import java.util.ArrayList;
import java.util.List;
import com.hrms.repository.RoleRepository;

public class RoleRepositoryImpl extends AbstractDAO<RoleEntity> implements RoleRepository {

    @Override
    public List<RoleDTO> search(RoleSearchBuilder builder) {
        StringBuilder sql = new StringBuilder(
                "SELECT r.*, COUNT(ur.user_id) AS userCount FROM roles r LEFT JOIN user_role ur ON r.id = ur.role_id WHERE 1=1 "); 
        // Câu query này ko phải lo vụ user inactive vì căn bản là xóa thẳng ở user_role tương ứng r
        List<Object> params = new ArrayList<>();
        if(builder.getId() != null) {
            sql.append(" AND r.id = ? ");
            params.add(builder.getId());
        }
        if(builder.getRoleName()!=null && !builder.getRoleName().trim().isEmpty()){
            sql.append(" AND r.role_name LIKE ? ");
            params.add("%" + builder.getRoleName().trim() + "%");
        }
        if(builder.getDescription()!=null && !builder.getDescription().trim().isEmpty()){
            sql.append(" AND r.description LIKE ? ");
            params.add("%" + builder.getDescription().trim() + "%");
        }
        sql.append(" GROUP BY r.id, r.role_name, r.description");
        return queryList(sql.toString(), RoleDTO.class, params.toArray());
    }

    @Override
    public RoleEntity findById(Long roleId) {
        String sql = "SELECT * FROM roles WHERE id = ?";
        return querySingle(sql, RoleEntity.class, roleId);
    }

//    @Override
//    public RoleEntity findByName(String roleName) {
//        String sql = "SELECT * FROM roles WHERE role_name = ?";
//        return querySingle(sql, RoleEntity.class, roleName);
//    }

    @Override
    public void updateDescription(Long roleId, String description) {
        String sql = "UPDATE roles SET description = ? WHERE id = ?";
        update (sql, description, roleId);
    }

//    @Override
//    public Long create(RoleEntity entity) {
//        String sql = "INSERT INTO roles(role_name, description) VALUES (?,?)";
//        return insert(sql, entity.getRoleName(), entity.getDescription());
//    }
//
//    @Override
//    public void delete(Long roleId) {
//        if(roleId==1L)
//            throw new RuntimeException("Deleting ADMIN role is forbidden!");
//        String sql = "DELETE FROM roles WHERE id = ?";
//        update (sql, roleId);
//    }
    
}
