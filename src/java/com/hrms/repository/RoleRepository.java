
package com.hrms.repository;

import com.hrms.model.mapper.builder.RoleSearchBuilder;
import com.hrms.model.dto.response.RoleDTO;
import com.hrms.model.entity.RoleEntity;
import java.util.List;

public interface RoleRepository extends GenericDAO<RoleEntity> {
    List<RoleDTO> search(RoleSearchBuilder builder); 
    RoleEntity findById(Long roleId);
//    RoleEntity findByName(String roleName);
    void updateDescription(Long roleId, String description);
    
    // Bỏ 2 quyền sau: vì lỡ chơi RoleEnums (chỉ chơi tốt với hệ values fixed)
    // Long create(RoleEntity entity); 
    // void delete(Long roleId);
}
