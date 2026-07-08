
package com.hrms.repository;

import com.hrms.model.mapper.builder.PermissionSearchBuilder;
import com.hrms.model.dto.response.PermissionDTO;
import com.hrms.model.entity.PermissionEntity;
import java.util.List;

public interface PermissionRepository extends GenericDAO<PermissionEntity> {
    // Thực ra 3 hàm search này có thể lấy từ RAM (nhờ Permission Cache)
    List<PermissionDTO> search(PermissionSearchBuilder builder);
    PermissionEntity findById(Long id);
//    PermissionEntity findByKey(String permissionKey);
    void updateDescription(Long id, String description);
}
