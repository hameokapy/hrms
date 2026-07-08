
package com.hrms.repository;

import com.hrms.model.dto.common.PageResponseDTO;
import com.hrms.model.dto.response.PositionDetailDTO;
import com.hrms.model.dto.response.PositionSummaryDTO;
import com.hrms.model.entity.PositionEntity;
import com.hrms.model.mapper.builder.PositionSearchBuilder;

public interface PositionRepository extends GenericDAO<PositionEntity>{
    PageResponseDTO<PositionSummaryDTO> search(PositionSearchBuilder builder, Integer currentPage, Integer pageSize);
    PositionDetailDTO searchById(Long posiId);
    Long create(PositionEntity entity);
    void updateGeneral(PositionEntity entity);
    void changeStatus(Long posiId, String status, String modifiedBy); // aka soft delete or restore
    // Hàm bổ trợ các class service:
    PositionEntity findById(Long posiId);
    boolean existPosiName(String name);
    boolean existPosiId(Long id);
    boolean isActive (Long posiId);
}
