
package com.hrms.service.impl;

import com.hrms.core.config.TransactionManager;
import com.hrms.core.exception.business.ResourceNotFoundException;
import java.util.List;
import com.hrms.model.dto.response.PermissionDTO;
import com.hrms.model.mapper.builder.PermissionSearchBuilder;
import com.hrms.repository.PermissionRepository;
import com.hrms.repository.impl.PermissionRepositoryImpl;
import com.hrms.service.PermissionService;

public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepo = new PermissionRepositoryImpl();
    
    @Override
    public List<PermissionDTO> search(PermissionSearchBuilder builder) {
      return permissionRepo.search(builder);
    }

    @Override
    public void updateDescription(Long permissionId, String description) {
        if(permissionRepo.findById(permissionId) == null)
            throw new ResourceNotFoundException("Permission not found: " + permissionId);
        TransactionManager.runInTransaction(() -> permissionRepo.updateDescription(permissionId, description));
    }
    
}
