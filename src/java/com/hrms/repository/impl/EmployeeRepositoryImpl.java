
package com.hrms.repository.impl;

import com.hrms.model.dto.common.PageResponseDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.hrms.model.dto.response.EmployeeDetailDTO;
import com.hrms.model.dto.response.EmployeeSummaryDTO;
import com.hrms.model.entity.DepartmentEntity;
import com.hrms.model.entity.EmployeeEntity;
import com.hrms.model.entity.PositionEntity;
import com.hrms.model.mapper.builder.EmployeeSearchBuilder;
import com.hrms.repository.EmployeeRepository;
import com.hrms.utils.DataMapper;

public class EmployeeRepositoryImpl extends AbstractDAO<EmployeeEntity> implements EmployeeRepository {
    
    String EMP_FROM_JOIN = " FROM employees e JOIN departments d ON d.id = e.department_id JOIN positions p ON p.id = e.position_id ";

    @Override
    public EmployeeEntity findById(Long employeeId) {
        String sql = "SELECT * FROM employees WHERE id = ?";
        return querySingle(sql, rs -> {
            EmployeeEntity emp = DataMapper.mapResultSetToObject(rs, EmployeeEntity.class);
            if (rs.getObject("department_id") != null)
                emp.setDepartment(new DepartmentEntity(rs.getLong("department_id")));
            if (rs.getObject("position_id") != null) 
                emp.setPosition(new PositionEntity(rs.getLong("position_id")));
            return emp;
        }, employeeId);
    }

    @Override
    public List<EmployeeEntity> findAllByIds(List<Long> ids) {
        String placeholders = ids.stream().map(id -> "?").collect(Collectors.joining(", "));
        String sql = "SELECT * FROM employees WHERE id IN (" + placeholders + ")";
        return queryList(sql, rs -> {
            EmployeeEntity emp = DataMapper.mapResultSetToObject(rs, EmployeeEntity.class);
            if (rs.getObject("department_id") != null)
                emp.setDepartment(new DepartmentEntity(rs.getLong("department_id")));
            if (rs.getObject("position_id") != null) 
                emp.setPosition(new PositionEntity(rs.getLong("position_id")));
            return emp; 
        }, ids.toArray());
    }
    
    @Override 
    public Long countEmployeesByDeptIdExcludeInactive(Long deptId) {
        String sql = "SELECT COUNT(*) FROM employees WHERE department_id = ? AND status NOT IN ('INACTIVE')";
        return querySingle(sql, rs -> rs.getLong(1), deptId);
    }

    @Override
    public Long countEmployeesByPosiIdExcludeInactive(Long posiId) {
        String sql = "SELECT COUNT(*) FROM employees WHERE position_id = ? AND status NOT IN ('INACTIVE')";
        return querySingle(sql, rs -> rs.getLong(1), posiId);
    }

    @Override
    public PageResponseDTO<EmployeeSummaryDTO> search(EmployeeSearchBuilder builder, Long scopeDeptId, Integer currentPage, Integer pageSize) {
        StringBuilder sql = new StringBuilder("SELECT e.id, e.employee_code, e.full_name, d.code AS deptCode, d.name AS deptName, p.name AS positionName " 
                + EMP_FROM_JOIN + " WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        String whereClause = buildWhereClause(builder, scopeDeptId, params);
        sql.append(whereClause);
        
        String countSql = "SELECT COUNT(*) " + EMP_FROM_JOIN + " WHERE 1=1 " + whereClause;
        int totalElements = count(countSql, params.toArray());
        sql.append(" ORDER BY e.id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY"); 
        params.add((currentPage-1)*pageSize);
        params.add(pageSize);
        
        List<EmployeeSummaryDTO> content = queryList(sql.toString(), rs -> {
            EmployeeSummaryDTO result = DataMapper.mapResultSetToObject(rs, EmployeeSummaryDTO.class);
            result.setDepartmentNameAndCode(rs.getString("deptName") + " (" + rs.getString("deptCode") + ")");
            return result;
        }, params.toArray());
        
        return new PageResponseDTO<>(content, totalElements, currentPage, pageSize);
    }
    
    private String buildWhereClause(EmployeeSearchBuilder builder, Long scopeDeptId, List<Object> params) {
        StringBuilder where = new StringBuilder();
        if (scopeDeptId != null) {
            where.append(" AND e.department_id = ? ");
            params.add(scopeDeptId);
        }
        if(builder.getId() != null) {
            where.append(" AND e.id = ? ");
            params.add(builder.getId());
        }
        if(builder.getEmployeeNameCode()!=null && !builder.getEmployeeNameCode().isBlank()) {
            where.append(" AND (e.employee_code LIKE ? OR e.full_name LIKE ?) ");
            params.add("%" + builder.getEmployeeNameCode() + "%");
            params.add("%" + builder.getEmployeeNameCode() + "%");
        }
        if(builder.getDepartmentNameCode()!=null && !builder.getDepartmentNameCode().isBlank()) {
            where.append(" AND (d.code LIKE ? OR d.name LIKE ?) ");
            params.add("%" + builder.getDepartmentNameCode() + "%");
            params.add("%" + builder.getDepartmentNameCode() + "%");
        }
        if(builder.getPositionName()!=null && !builder.getPositionName().isBlank()) {
            where.append(" AND p.name LIKE ? ");
            params.add("%" + builder.getPositionName() + "%");
        }
        if(builder.getStatus()!=null && !builder.getStatus().isEmpty()) {
            String statusList = builder.getStatus().stream().map(item -> "?").collect(Collectors.joining(", "));
            where.append(" AND e.status IN (").append(statusList).append(") ");
            params.addAll(builder.getStatus());
        }
        return where.toString();
    }

    @Override
    public EmployeeDetailDTO searchById(Long empId) {
        String sql = "SELECT e.*, d.code AS deptCode, d.name AS deptName, p.name AS positionName FROM employees e "
                + "JOIN departments d ON d.id = e.department_id JOIN positions p ON p.id = e.position_id WHERE e.id = ?";
        return querySingle(sql, rs -> {
            EmployeeDetailDTO result = DataMapper.mapResultSetToObject(rs, EmployeeDetailDTO.class);
            result.setDepartmentNameAndCode(rs.getString("deptName") + " (" + rs.getString("deptCode") + ")");
            return result;
        }, empId);
    }

    @Override
    public Long createEmployee(EmployeeEntity entity) {
        String sql = "INSERT INTO employees (employee_code, full_name, email, phone, department_id, position_id, status, "
                + "created_date, created_by) VALUES (?,?,?,?,?,?,?, GETDATE(), ?)";
        return insert(sql, entity.getEmployeeCode(), entity.getFullName(), entity.getEmail(), entity.getPhone(),
                entity.getDepartment().getId(), entity.getPosition().getId(), entity.getStatus(),entity.getCreatedBy());
    }

    @Override
    public void updateGeneral(EmployeeEntity entity) {
        String sql = "UPDATE employees SET full_name = ?, email = ?, phone = ?, modified_date = GETDATE(), modified_by = ? WHERE id = ?";
        update(sql, entity.getFullName(), entity.getEmail(), entity.getPhone(), 
            entity.getModifiedBy(),entity.getId());
    }

    @Override
    public void assignDepartmentInBulk(List<Long> empIds, Long deptId, String modifiedBy) {
        String placeholders = empIds.stream().map(id -> "?").collect(Collectors.joining(", "));
        String sql = String.format("UPDATE employees SET department_id = ?, modified_date = GETDATE(), modified_by = ? WHERE id IN (%s)", 
            placeholders);
        List<Object> params = new ArrayList<>();
        params.add(deptId);
        params.add(modifiedBy);
        params.addAll(empIds);
        update(sql, params.toArray());
    }

    @Override
    public void assignPositionInBulk(List<Long> empIds, Long posiId, String modifiedBy) {
        String placeholders = empIds.stream().map(id -> "?").collect(Collectors.joining(", "));
        String sql = String.format("UPDATE employees SET position_id = ?, modified_date = GETDATE(), modified_by = ? WHERE id IN (%s)", 
            placeholders);
        List<Object> params = new ArrayList<>();
        params.add(posiId);
        params.add(modifiedBy);
        params.addAll(empIds);
        update(sql, params.toArray());
    }

    @Override
    public void updateStatus(Long empId, String status, String modifiedBy) {
        String sql = "UPDATE employees SET status = ?, modified_date = GETDATE(), modified_by = ? WHERE id = ?";
        update(sql, status, modifiedBy, empId);
    }

    @Override
    public Long getNextSequenceValue() {
        String sql = "SELECT NEXT VALUE FOR Seq_EmployeeCode";
        return querySingle(sql, rs -> rs.getLong(1));
    }

    @Override
    public void resetSequence() {
        String sql = "ALTER SEQUENCE Seq_EmployeeCode RESTART WITH 1";
        update(sql);
    }

    @Override
    public boolean existEmail(String email) {
        String sql = "SELECT CASE WHEN EXISTS (SELECT 1 FROM employees WHERE email = ?) THEN 1 ELSE 0 END";
        Integer result = querySingle(sql, rs -> rs.getInt(1), email);
        return result!=null && result==1;
    }

    @Override
    public boolean existPhone(String phone) {
        String sql = "SELECT CASE WHEN EXISTS (SELECT 1 FROM employees WHERE phone = ?) THEN 1 ELSE 0 END";
        Integer result = querySingle(sql, rs -> rs.getInt(1), phone);
        return result!=null && result==1;
    }
    
}
