
package com.hrms.service;

import java.util.List;
import com.hrms.model.dto.response.PermissionDTO;
import com.hrms.model.mapper.builder.PermissionSearchBuilder;

public interface PermissionService {
    List<PermissionDTO> search(PermissionSearchBuilder builder);
    void updateDescription(Long permissionId, String description);
}
