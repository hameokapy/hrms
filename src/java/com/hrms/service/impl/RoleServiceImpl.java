
package com.hrms.service.impl;

import com.hrms.core.config.TransactionManager;
import com.hrms.core.exception.business.ResourceNotFoundException;
import java.util.List;
import com.hrms.model.dto.response.RoleDTO;
import com.hrms.model.mapper.builder.RoleSearchBuilder;
import com.hrms.repository.RoleRepository;
import com.hrms.repository.impl.RoleRepositoryImpl;
import com.hrms.service.RoleService;

public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepo = new RoleRepositoryImpl();
    
    @Override
    public List<RoleDTO> search(RoleSearchBuilder builder) {
        return roleRepo.search(builder);
    }

    @Override
    public void updateDescription(Long roleId, String description) {
        if(roleRepo.findById(roleId) == null)
            throw new ResourceNotFoundException("Role not found: " + roleId);
        TransactionManager.runInTransaction(() -> roleRepo.updateDescription(roleId, description));
    }
    
}
