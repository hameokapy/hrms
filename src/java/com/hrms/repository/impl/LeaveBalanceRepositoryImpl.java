
package com.hrms.repository.impl;

import com.hrms.model.dto.common.PageResponseDTO;
import java.util.ArrayList;
import java.util.List;
import com.hrms.model.dto.response.LeaveBalanceSummaryDTO;
import com.hrms.model.entity.EmployeeEntity;
import com.hrms.model.entity.LeaveBalanceEntity;
import com.hrms.model.mapper.builder.LeaveBalanceSearchBuilder;
import com.hrms.repository.LeaveBalanceRepository;
import com.hrms.utils.DataMapper;

public class LeaveBalanceRepositoryImpl extends AbstractDAO<LeaveBalanceEntity> implements LeaveBalanceRepository {

    private final String BALANCE_FROM_JOIN =   "FROM leave_balance lb JOIN employees e ON lb.employee_id = e.id JOIN departments d ON e.department_id = d.id";
    
    @Override
    public LeaveBalanceEntity findByEmployeeIdAndYear(Long employeeId, Integer year) {
        String sql = "SELECT * FROM leave_balance WHERE employee_id = ? AND year = ?";
        return querySingle(sql, rs -> {
            LeaveBalanceEntity result = DataMapper.mapResultSetToObject(rs, LeaveBalanceEntity.class);
            EmployeeEntity emp = new EmployeeEntity();
            emp.setId(rs.getLong("employee_id"));
            result.setEmployee(emp);
            return result; 
        }, employeeId, year);
    }

    @Override
    public PageResponseDTO<LeaveBalanceSummaryDTO> searchBalance(LeaveBalanceSearchBuilder builder, Long scopeDeptId, Long scopeEmpId, Integer currentPage, Integer pageSize) {
        StringBuilder sql = new StringBuilder("SELECT lb.*, e.full_name, e.employee_code, d.name, d.code " + BALANCE_FROM_JOIN + " WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        String whereClause = buildWhereClause(builder, scopeDeptId, scopeEmpId, params);
        sql.append(whereClause);
        
        String countSql = "SELECT COUNT(*) " + BALANCE_FROM_JOIN + " WHERE 1=1 " + whereClause;
        int totalElements = count(countSql, params.toArray());
        sql.append(" ORDER BY e.id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY"); 
        params.add((currentPage-1)*pageSize);
        params.add(pageSize);
        
        List<LeaveBalanceSummaryDTO> content = queryList(sql.toString(), rs -> {
            LeaveBalanceSummaryDTO result = DataMapper.mapResultSetToObject(rs, LeaveBalanceSummaryDTO.class);
            result.setDepartmentNameCode(rs.getString("name") + " (" + rs.getString("code") + ")");
            result.setEmployeeNameCode(rs.getString("full_name") + " (" + rs.getString("employee_code") + ")");
            return result; 
        }, params.toArray());
        return new PageResponseDTO<>(content, totalElements, currentPage, pageSize);
    }
    
    private String buildWhereClause (LeaveBalanceSearchBuilder builder, Long scopeDeptId, Long scopeEmpId, List<Object> params) {
        StringBuilder where = new StringBuilder();
        if (scopeDeptId != null) {
            where.append(" AND e.department_id = ? ");
            params.add(scopeDeptId);
        }
        if(scopeEmpId != null) {
            where.append(" AND lb.employee_id = ? ");
            params.add(scopeEmpId);
        }
        if (builder.getYear() != null) {
            where.append(" AND lb.year = ?");
            params.add(builder.getYear());
        }
        if (builder.getEmployeeKeyword()!=null && !builder.getEmployeeKeyword().isEmpty()) {
            where.append(" AND (e.full_name LIKE ? OR e.employee_code LIKE ?)");
            String keyword = "%" + builder.getEmployeeKeyword() + "%";
            params.add(keyword); 
            params.add(keyword);
        }
        if (builder.getDepartmentKeyword()!=null && !builder.getDepartmentKeyword().isEmpty()) {
            where.append(" AND (d.name LIKE ? OR d.code LIKE ?)");
            String keyword = "%" + builder.getDepartmentKeyword() + "%";
            params.add(keyword); 
            params.add(keyword);
        }
        return where.toString();
    }
    
    @Override
    public Long create(LeaveBalanceEntity entity) {
        String sql = "INSERT INTO leave_balance (employee_id, year, annual_total_days, annual_used_days, sick_total_days, "
                + "sick_used_days, created_date, created_by) VALUES (?,?,?,0,?,0, GETDATE(), ?)";
        return insert(sql, entity.getEmployee().getId(), entity.getYear(), entity.getAnnualTotalDays(),
                entity.getSickTotalDays(), entity.getCreatedBy());
    }

    @Override
    public void updateAnnualUsage(Long balanceId, Integer days, String modifiedBy) {
        String sql = "UPDATE leave_balance SET annual_used_days = annual_used_days + ?, modified_by = ?, modified_date = GETDATE() WHERE id = ?";
        update(sql, days, modifiedBy, balanceId);
    }

    @Override
    public void updateSickUsage(Long balanceId, Integer days, String modifiedBy) {
        String sql = "UPDATE leave_balance SET sick_used_days = sick_used_days + ?, modified_by = ?, modified_date = GETDATE() WHERE id = ?";
        update(sql, days, modifiedBy, balanceId);
    }
    
}
