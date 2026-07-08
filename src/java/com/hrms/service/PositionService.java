
package com.hrms.service;

import com.hrms.model.dto.common.PageResponseDTO;
import com.hrms.model.dto.request.PositionRequestDTO;
import com.hrms.model.dto.response.PositionDetailDTO;
import com.hrms.model.dto.response.PositionSummaryDTO;
import com.hrms.model.mapper.builder.PositionSearchBuilder;

public interface PositionService {
    PageResponseDTO<PositionSummaryDTO> search(PositionSearchBuilder builder, Integer page);
    PositionDetailDTO searchById(Long posiId);
    Long create(PositionRequestDTO request);
    void updateGeneral(PositionRequestDTO request);
    void changeStatus(Long posiId, String status);
}
