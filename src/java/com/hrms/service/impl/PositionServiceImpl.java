
package com.hrms.service.impl;

import com.hrms.core.config.SystemConfig;
import com.hrms.core.config.TransactionManager;
import com.hrms.core.constant.OtherEnums;
import com.hrms.core.exception.business.DuplicateResourceException;
import com.hrms.core.exception.business.IllegalOperationException;
import com.hrms.core.exception.business.ResourceNotFoundException;
import com.hrms.core.security.SecurityContext;
import com.hrms.model.dto.common.PageResponseDTO;
import java.util.Optional;
import com.hrms.model.dto.request.PositionRequestDTO;
import com.hrms.model.dto.response.PositionDetailDTO;
import com.hrms.model.dto.response.PositionSummaryDTO;
import com.hrms.model.entity.PositionEntity;
import com.hrms.model.mapper.builder.PositionSearchBuilder;
import com.hrms.repository.EmployeeRepository;
import com.hrms.repository.PositionRepository;
import com.hrms.repository.impl.EmployeeRepositoryImpl;
import com.hrms.repository.impl.PositionRepositoryImpl;
import com.hrms.service.PositionService;
import com.hrms.utils.DataMapper;

public class PositionServiceImpl implements PositionService {
    
    private final PositionRepository posiRepo = new PositionRepositoryImpl();
    private final EmployeeRepository empRepo = new EmployeeRepositoryImpl();

    @Override
    public PageResponseDTO<PositionSummaryDTO> search(PositionSearchBuilder builder, Integer currentPage) {
        if (builder.getSalaryFrom()!=null && builder.getSalaryTo()!=null) {
            if(builder.getSalaryFrom().compareTo(builder.getSalaryTo()) > 0)
                throw new IllegalOperationException("SalaryFrom cannot be greater than SalaryTo.");
        }
        return posiRepo.search(builder, currentPage, SystemConfig.getPageSize());
    }

    @Override
    public PositionDetailDTO searchById(Long posiId) {
        return Optional.ofNullable(posiRepo.searchById(posiId)).orElseThrow(
                () -> new ResourceNotFoundException("Position not found with ID: " + posiId)); 
    }

    @Override
    public Long create(PositionRequestDTO request) {
        if(posiRepo.existPosiName(request.getName()))
            throw new DuplicateResourceException("Position existed.");
        return TransactionManager.doInTransaction(() -> {
            PositionEntity entity = DataMapper.mapObjectToObject(request, PositionEntity.class);
            entity.setStatus(OtherEnums.Position.ACTIVE.getValue());
            entity.setCreatedBy(SecurityContext.getCurrentUsername());
            return posiRepo.create(entity);
        });
    }

    @Override
    public void updateGeneral(PositionRequestDTO request) {
        PositionEntity posi = posiRepo.findById(request.getId());
        if(posi == null)
            throw new ResourceNotFoundException("Position not found.");
        if (request.getName()!=null && !request.getName().equalsIgnoreCase(posi.getName())) {
            if(posiRepo.existPosiName(request.getName()))
                throw new DuplicateResourceException("Position with such name existed.");
        }
        TransactionManager.runInTransaction(() -> {
            DataMapper.copyPropertiesIgnoreNull(request, posi);
            posi.setDescription(request.getDescription());
            posi.setModifiedBy(SecurityContext.getCurrentUsername());
            posiRepo.updateGeneral(posi);
        });
    }

    @Override
    public void changeStatus(Long posiId, String status) {
        if (!posiRepo.existPosiId(posiId)) 
            throw new ResourceNotFoundException("Position not found.");
        boolean isInactive = status.equals(OtherEnums.Position.INACTIVE.getValue());
        boolean isActive = status.equals(OtherEnums.Position.ACTIVE.getValue());
        if (!isInactive && !isActive)
            throw new IllegalOperationException("Invalid status: " + status);
        if(isInactive) {
            Long count = empRepo.countEmployeesByPosiIdExcludeInactive(posiId);
            if(count>0)
                throw new IllegalOperationException("Not allowed, position still has " + count + " non-inactive employees.");
        }
        TransactionManager.runInTransaction(() -> {
            posiRepo.changeStatus(posiId, status, SecurityContext.getCurrentUsername());
        });
    }
    
}
