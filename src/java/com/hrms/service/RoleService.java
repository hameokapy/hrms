
package com.hrms.service;

import java.util.List;
import com.hrms.model.dto.response.RoleDTO;
import com.hrms.model.mapper.builder.RoleSearchBuilder;

public interface RoleService {
    List<RoleDTO> search(RoleSearchBuilder builder);
    void updateDescription(Long id, String description);
}
