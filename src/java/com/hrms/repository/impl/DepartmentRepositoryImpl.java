
package com.hrms.repository.impl;

import com.hrms.model.dto.common.PageResponseDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.hrms.model.dto.response.DepartmentDetailDTO;
import com.hrms.model.dto.response.DepartmentSummaryDTO;
import com.hrms.model.entity.DepartmentEntity;
import com.hrms.model.entity.EmployeeEntity;
import com.hrms.model.mapper.builder.DepartmentSearchBuilder;
import com.hrms.repository.DepartmentRepository;
import com.hrms.utils.DataMapper;

public class DepartmentRepositoryImpl extends AbstractDAO<DepartmentEntity> implements DepartmentRepository {

    private final String DEPT_SUMMARY_COLS = "d.id, d.code, d.name, d.location, d.status, e.full_name AS managerName, (SELECT COUNT(*) FROM employees emp " +
        " WHERE emp.department_id = d.id AND emp.status IN ('ACTIVE', 'ON_LEAVE', 'PENDING')) AS totalEmployees ";

    private final String DEPT_DETAIL_COLS =  "d.manager_id, d.created_date, d.created_by, d.modified_date, d.modified_by ";

    private final String DEPT_FROM_JOIN =   "FROM departments d LEFT JOIN employees e ON d.manager_id = e.id";
    
    @Override
    public PageResponseDTO<DepartmentSummaryDTO> search(DepartmentSearchBuilder builder, Integer currentPage, Integer pageSize) {
        StringBuilder sql = new StringBuilder("SELECT " + DEPT_SUMMARY_COLS + DEPT_FROM_JOIN + " WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        String whereClause = buildWhereClause(builder, params);
        sql.append(whereClause);
        
        String countSql = "SELECT COUNT(*) " + DEPT_FROM_JOIN + " WHERE 1=1 " + whereClause;
        int totalElements = count(countSql, params.toArray());
        sql.append(" ORDER BY d.id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY"); 
        params.add((currentPage-1)*pageSize);
        params.add(pageSize);
        
        List<DepartmentSummaryDTO> content = queryList(sql.toString(), DepartmentSummaryDTO.class, params.toArray());
        return new PageResponseDTO<>(content, totalElements, currentPage, pageSize);
    }
    
    private String buildWhereClause(DepartmentSearchBuilder builder, List<Object> params) {
        StringBuilder where = new StringBuilder();
        if(builder.getId()!=null) {
            where.append(" AND d.id = ? ");
            params.add(builder.getId());
        }
        if(builder.getCode()!=null && !builder.getCode().isBlank()) {
            where.append(" AND d.code LIKE ? ");
            params.add("%" + builder.getCode() + "%");
        }
        if(builder.getName()!=null && !builder.getName().isBlank()) {
            where.append(" AND d.name LIKE ? ");
            params.add("%" + builder.getName() + "%");
        }
        if(builder.getLocation()!=null && !builder.getLocation().isBlank()) {
            where.append(" AND d.location LIKE ?");
            params.add("%" + builder.getLocation() + "%");
        }
        if(builder.getStatus()!=null && !builder.getStatus().isBlank()) {
            where.append(" AND d.status = ?"); // Ko để "=" là search active nó show cả inactive :)
            params.add(builder.getStatus());
        }
        if(builder.getManagerName()!=null && !builder.getManagerName().isBlank()) {
            where.append(" AND e.full_name LIKE ?");
            params.add("%" + builder.getManagerName() + "%");
        }
        return where.toString();
    }

    @Override
    public DepartmentDetailDTO searchById(Long deptId) {
        String sql = "SELECT " + DEPT_SUMMARY_COLS + ", " + DEPT_DETAIL_COLS + DEPT_FROM_JOIN + " WHERE d.id = ?";
        return querySingle(sql, DepartmentDetailDTO.class, deptId);
    }

    @Override
    public Long create(DepartmentEntity entity) {
        String sql = "INSERT INTO departments (code, name, manager_id, location, status, created_date, created_by) VALUES (?,?,?,?,?, GETDATE(), ?)";
        return insert(sql, entity.getCode(), entity.getName(), entity.getManager()!=null ? entity.getManager().getId() : null,
                entity.getLocation(), entity.getStatus(), entity.getCreatedBy());
    }

    @Override
    public void updateGeneral(DepartmentEntity entity) {
        String sql = "UPDATE departments SET name = ?, location = ?, modified_date = GETDATE(), modified_by = ? WHERE id = ?";
        update(sql, entity.getName(), entity.getLocation(), entity.getModifiedBy(), entity.getId());
    }

    @Override
    public void assignManager(Long deptId, Long managerId, String modifiedBy) {
        String sql = "UPDATE departments SET manager_id = ?, modified_date = GETDATE(), modified_by = ? WHERE id = ?";
        update (sql, managerId, modifiedBy, deptId);
    }

    @Override
    public void changeStatus(Long deptId, String status, String modifiedBy) {
        String sql = "UPDATE departments SET status = ?, modified_date = GETDATE(), modified_by = ? WHERE id = ?";
        update(sql,status, modifiedBy, deptId);
    }

    @Override
    public boolean existDeptCode(String deptCode) {
        String sql = "SELECT CASE WHEN EXISTS (SELECT 1 FROM departments WHERE code = ?) THEN 1 ELSE 0 END";
        Integer result = querySingle(sql, rs -> rs.getInt(1), deptCode);
        return result!=null && result==1;
    }

    @Override
    public boolean existDeptName(String deptName) {
        String sql = "SELECT CASE WHEN EXISTS (SELECT 1 FROM departments WHERE name = ?) THEN 1 ELSE 0 END";
        Integer result = querySingle(sql, rs -> rs.getInt(1), deptName);
        return result!=null && result==1;
    }

    @Override
    public boolean existDeptId(Long deptId) {
        String sql = "SELECT CASE WHEN EXISTS (SELECT 1 FROM departments WHERE id = ?) THEN 1 ELSE 0 END";
        Integer result = querySingle(sql, rs -> rs.getInt(1), deptId);
        return result!=null && result==1;
    }

    @Override
    public boolean isActive(Long deptId) {
        String sql = "SELECT CASE WHEN EXISTS (SELECT 1 FROM departments WHERE id = ? AND status = 'ACTIVE') THEN 1 ELSE 0 END";
        Integer result = querySingle(sql, rs -> rs.getInt(1), deptId);
        return result!=null && result==1;
    }

    @Override
    public DepartmentEntity findById(Long deptId) {
        String sql = "SELECT * FROM departments WHERE id = ?";
        return querySingle(sql, rs -> {
            DepartmentEntity dept = DataMapper.mapResultSetToObject(rs, DepartmentEntity.class);
            if(rs.getObject("manager_id") != null)
                dept.setManager(new EmployeeEntity(rs.getLong("manager_id")));
            return dept; 
        }, deptId);
    }

    @Override
    public List<Long> findDeptIdsByManagers(List<Long> empIds) {
        String placeholders = empIds.stream().map(id -> "?").collect(Collectors.joining(", "));
        String sql = "SELECT id FROM departments WHERE manager_id IN (" + placeholders + ")";
        return queryList(sql, rs -> rs.getLong("id"), empIds.toArray());
    }
    
    @Override
    public boolean isManager(Long empId) {
        String sql = "SELECT CASE WHEN EXISTS (SELECT 1 FROM departments WHERE manager_id = ? AND status = 'ACTIVE') THEN 1 ELSE 0 END";
        Integer result = querySingle(sql, rs -> rs.getInt(1), empId);
        return result!=null && result==1;
    }

}