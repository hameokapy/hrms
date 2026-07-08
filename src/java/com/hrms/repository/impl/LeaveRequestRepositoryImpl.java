
package com.hrms.repository.impl;

import com.hrms.model.dto.common.PageResponseDTO;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.hrms.model.dto.response.LeaveRequestDTO;
import com.hrms.model.entity.EmployeeEntity;
import com.hrms.model.entity.LeaveRequestEntity;
import com.hrms.model.mapper.builder.LeaveRequestSearchBuilder;
import com.hrms.repository.LeaveRequestRepository;
import com.hrms.utils.DataMapper;

public class LeaveRequestRepositoryImpl extends AbstractDAO<LeaveRequestEntity> implements LeaveRequestRepository {

    private final String REQUEST_FROM_JOIN = """
        FROM leave_requests lr
        JOIN employees e ON lr.employee_id = e.id
        JOIN departments d ON e.department_id = d.id
        LEFT JOIN employees app ON lr.approved_by = app.id
        LEFT JOIN leave_balance lb ON lr.employee_id = lb.employee_id AND YEAR(lr.start_date) = lb.year
    """;
    
    @Override
    public Long createRequest(LeaveRequestEntity entity) {
        String sql = "INSERT INTO leave_requests (employee_id, type, start_date, end_date, reason, status, created_date, created_by) "
                + "VALUES (?,?,?,?,?,?, GETDATE(), ?)";
        return insert(sql, entity.getEmployee().getId(), entity.getType(), entity.getStartDate(), entity.getEndDate(),
                entity.getReason(), entity.getStatus(), entity.getCreatedBy());
    }
    
    @Override
    public PageResponseDTO<LeaveRequestDTO> search(LeaveRequestSearchBuilder builder, Long scopeEmpId, Long scopeDeptId, Integer currentPage, Integer pageSize) {
        // Chơi hệ tạo View dưới DB thì code readablity hơi khó vì bị con View che mất cái bản chất
        StringBuilder sql = new StringBuilder("""
            SELECT 
                lr.id, lr.employee_id, e.full_name AS employee_name, e.employee_code AS employee_code,
                d.name AS dept_name, d.code AS dept_code, lr.type, lr.start_date, lr.end_date, 
                DATEDIFF(day, lr.start_date, lr.end_date) + 1 AS num_days,
                lb.annual_remaining_days AS annual_balance_remain, lb.sick_remaining_days AS sick_balance_remain,
                lr.reason, lr.status, app.full_name AS approver_name, app.employee_code AS approver_code,
                lr.approved_date, lr.created_date
            """ + REQUEST_FROM_JOIN + " WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        String whereClause = buildWhereClause(builder, scopeEmpId, scopeDeptId, params);
        sql.append(whereClause);
        
        String countSql = "SELECT COUNT(*) " + REQUEST_FROM_JOIN + " WHERE 1=1 " + whereClause;
        int totalElements = count(countSql, params.toArray());
        sql.append(" ORDER BY CASE WHEN lr.status = 'PENDING' THEN 1 WHEN lr.status = 'APPROVED' THEN 2 WHEN lr.status = 'REJECTED' THEN 3 ELSE 4 END ASC, "
                + "lr.created_date DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY"); 
        params.add((currentPage-1)*pageSize);
        params.add(pageSize);
        
        List<LeaveRequestDTO> content = queryList(sql.toString(), rs -> {
            LeaveRequestDTO result = DataMapper.mapResultSetToObject(rs, LeaveRequestDTO.class);
            result.setEmployeeNameAndCode(rs.getString("employee_name") + " (" + rs.getString("employee_code") + ")");
            result.setApproverNameAndCode(rs.getString("approver_name") + " (" + rs.getString("approver_code") + ")");
            result.setDepartmentNameAndCode(rs.getString("dept_name") + " (" + rs.getString("dept_code") + ")");
            return result; 
        }, params.toArray());
        return new PageResponseDTO<>(content, totalElements, currentPage, pageSize);
    }
    
    public String buildWhereClause (LeaveRequestSearchBuilder builder, Long scopeEmpId, Long scopeDeptId, List<Object> params) {
        StringBuilder where = new StringBuilder();
        if (scopeEmpId != null) {
            where.append(" AND lr.employee_id = ? ");
            params.add(scopeEmpId);
        }
        if (scopeDeptId != null) {
            where.append(" AND e.department_id = ? ");
            params.add(scopeDeptId);
        }
        if(builder.getId() != null) {
            where.append(" AND lr.id = ? ");
            params.add(builder.getId());
        }
        if(builder.getKeyword()!=null && !builder.getKeyword().isBlank()) {
            where.append(" AND (e.full_name LIKE ? OR e.employee_code LIKE ? OR d.name LIKE ? OR d.code LIKE ?) ");
            params.add("%" + builder.getKeyword() + "%");
            params.add("%" + builder.getKeyword() + "%");
            params.add("%" + builder.getKeyword() + "%");
            params.add("%" + builder.getKeyword() + "%");
        }
        if (builder.getType()!=null && !builder.getType().isBlank()) {
            where.append(" AND lr.type = ? ");
            params.add(builder.getType());
        }
        if (builder.getStatus()!=null && !builder.getStatus().isBlank()) {
            where.append(" AND lr.status = ? ");
            params.add(builder.getStatus());
        }
        // Để ý logic tìm date range khác với cách hiểu thông thường
        if (builder.getFromDate() != null) {
            where.append(" AND lr.end_date >= ? ");
            params.add(builder.getFromDate());
        }
        if (builder.getToDate() != null) {
            where.append(" AND lr.start_date <= ? ");
            params.add(builder.getToDate());
        }
        return where.toString();
    }

    @Override
    public void updateRequest(LeaveRequestEntity entity) {
        String sql = "UPDATE leave_requests SET type = ?, start_date = ?, end_date = ?, reason = ?, modified_date = GETDATE(), modified_by = ? WHERE id = ?";
        update(sql, entity.getType(), entity.getStartDate(), entity.getEndDate(), entity.getReason(),
                entity.getModifiedBy(), entity.getId());
    }

    @Override
    public void updateStatusRequest(Long leaveRequestId, String status, String modifiedBy, String approvedBy) {
        String approverSql = approvedBy==null ? ", approved_by = NULL, approved_date = NULL" 
            : ", approved_by = (SELECT employee_id FROM users WHERE username = ?), approved_date = GETDATE()"; 
        String sql = "UPDATE leave_requests SET status = ?, modified_by = ?, modified_date = GETDATE() " +
            approverSql + " WHERE id = ?";
        if (approvedBy != null)
            update(sql, status, modifiedBy, approvedBy, leaveRequestId);
        else
            update(sql, status, modifiedBy, leaveRequestId);
    }

    @Override
    public boolean existOverlap(Long employeeId, LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT COUNT(*) FROM leave_requests WHERE employee_id = ? AND status IN ('PENDING', 'APPROVED') "
                + "AND start_date <= ? AND end_date >= ?";
        Integer count = querySingle(sql, rs -> rs.getInt(1), employeeId, endDate, startDate);
        return count!=null && count>0;
    }

    @Override
    public boolean existOverlapExcludeSelf(Long leaveRequestId, Long employeeId, LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT COUNT(*) FROM leave_requests WHERE employee_id = ? AND status IN ('PENDING', 'APPROVED') "
                + "AND start_date <= ? AND end_date >= ? AND id != ?";
        Integer count = querySingle(sql, rs -> rs.getInt(1), employeeId, endDate, startDate, leaveRequestId);
        return count!=null && count>0;
    }

    @Override
    public LeaveRequestEntity findByLeaveRequestId(Long leaveRequestId) {
        String sql = "SELECT * FROM leave_requests WHERE id = ?";
        return querySingle(sql, rs -> {
            LeaveRequestEntity result = DataMapper.mapResultSetToObject(rs, LeaveRequestEntity.class);
            result.setEmployee(new EmployeeEntity(rs.getLong("employee_id")));
            result.setApprover(new EmployeeEntity(rs.getLong("approved_by")));
            return result;
        }, leaveRequestId);
    }

    
    
    

    
}
