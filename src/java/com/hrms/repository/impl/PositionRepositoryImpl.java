
package com.hrms.repository.impl;

import com.hrms.model.dto.common.PageResponseDTO;
import java.util.ArrayList;
import java.util.List;
import com.hrms.model.dto.response.PositionDetailDTO;
import com.hrms.model.dto.response.PositionSummaryDTO;
import com.hrms.model.entity.PositionEntity;
import com.hrms.model.mapper.builder.PositionSearchBuilder;
import com.hrms.repository.PositionRepository;

public class PositionRepositoryImpl extends AbstractDAO<PositionEntity> implements PositionRepository {

    private final String POSI_SUMMARY_COLS = "p.id, p.name, p.base_salary_level, p.status, (SELECT COUNT(*) FROM employees emp " +
        " WHERE emp.position_id = p.id AND emp.status IN ('ACTIVE', 'ON_LEAVE')) AS employeeCount ";

    private final String POSI_DETAIL_COLS =  "p.description, p.created_date, p.created_by, p.modified_date, p.modified_by ";

    private final String POSI_FROM_JOIN =   "FROM positions p";
    
    @Override
    public PageResponseDTO<PositionSummaryDTO> search(PositionSearchBuilder builder, Integer currentPage, Integer pageSize) {
        StringBuilder sql = new StringBuilder("SELECT " + POSI_SUMMARY_COLS + POSI_FROM_JOIN + " WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        String whereClause = buildWhereClause(builder, params);
        sql.append(whereClause);
        
        String countSql = "SELECT COUNT(*) " + POSI_FROM_JOIN + " WHERE 1=1 " + whereClause;
        int totalElements = count(countSql, params.toArray());
        sql.append(" ORDER BY p.id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY"); 
        params.add((currentPage-1)*pageSize);
        params.add(pageSize);
        
        List<PositionSummaryDTO> content = queryList(sql.toString(), PositionSummaryDTO.class, params.toArray());
        return new PageResponseDTO<>(content, totalElements, currentPage, pageSize);
    }
    
    private String buildWhereClause(PositionSearchBuilder builder, List<Object> params) {
        StringBuilder where = new StringBuilder();
        if(builder.getId() != null) {
            where.append(" AND p.id = ? ");
            params.add(builder.getId());
        }
        if(builder.getName()!=null && !builder.getName().isBlank()) {
            where.append(" AND p.name LIKE ? ");
            params.add("%" + builder.getName() + "%");
        }
        if(builder.getStatus()!=null && !builder.getStatus().isBlank()) {
            where.append(" AND p.status = ? ");
            params.add(builder.getStatus());
        }
        if (builder.getSalaryFrom() != null) {
            where.append(" AND p.base_salary_level >= ? ");
            params.add(builder.getSalaryFrom());
        }
        if (builder.getSalaryTo() != null) {
            where.append(" AND p.base_salary_level <= ? ");
            params.add(builder.getSalaryTo());
        }
        return where.toString();
    }

    @Override
    public PositionDetailDTO searchById(Long posiId) {
        String sql = "SELECT " + POSI_SUMMARY_COLS + ", " + POSI_DETAIL_COLS + POSI_FROM_JOIN + " WHERE p.id = ?";
        return querySingle(sql, PositionDetailDTO.class, posiId);
    }

    @Override
    public Long create(PositionEntity entity) {
        String sql = "INSERT INTO positions (name, base_salary_level, status, description, created_date, created_by) VALUES (?,?,?,?, GETDATE(), ?)";
        return insert(sql, entity.getName(), entity.getBaseSalaryLevel(), entity.getStatus(), 
                entity.getDescription(), entity.getCreatedBy());
    }

    @Override
    public void updateGeneral(PositionEntity entity) {
        String sql = "UPDATE positions SET name = ?, base_salary_level = ?, description = ?, modified_date = GETDATE(), modified_by = ? WHERE id = ?";
        update(sql, entity.getName(), entity.getBaseSalaryLevel(), entity.getDescription(), entity.getModifiedBy(), entity.getId());
    }

    @Override
    public void changeStatus(Long posiId, String status, String modifiedBy) {
        String sql = "UPDATE positions SET status = ?, modified_date = GETDATE(), modified_by = ? WHERE id = ?";
        update(sql,status, modifiedBy, posiId);
    }

    @Override
    public boolean existPosiName(String name) {
        String sql = "SELECT CASE WHEN EXISTS (SELECT 1 FROM positions WHERE name = ?) THEN 1 ELSE 0 END";
        Integer result = querySingle(sql, rs -> rs.getInt(1), name);
        return result!=null && result==1;
    }

    @Override
    public boolean existPosiId(Long id) {
        String sql = "SELECT CASE WHEN EXISTS (SELECT 1 FROM positions WHERE id = ?) THEN 1 ELSE 0 END";
        Integer result = querySingle(sql, rs -> rs.getInt(1), id);
        return result!=null && result==1;
    }

    @Override
    public boolean isActive(Long posiId) {
        String sql = "SELECT CASE WHEN EXISTS (SELECT 1 FROM positions WHERE id = ? AND status = 'ACTIVE') THEN 1 ELSE 0 END";
        Integer result = querySingle(sql, rs -> rs.getInt(1), posiId);
        return result!=null && result==1;
    }

    @Override
    public PositionEntity findById(Long posiId) {
        String sql = "SELECT * FROM positions WHERE id = ?";
        return querySingle(sql, PositionEntity.class, posiId);
    }
    
}
