
package com.hrms.repository.impl;

import com.hrms.model.dto.common.PageResponseDTO;
import com.hrms.model.mapper.builder.UserSearchBuilder;
import com.hrms.model.entity.DepartmentEntity;
import com.hrms.model.entity.EmployeeEntity;
import com.hrms.model.entity.PositionEntity;
import com.hrms.model.entity.UserEntity;
import java.util.List;
import com.hrms.repository.UserRepository;
import java.sql.*;
import java.util.ArrayList;
import com.hrms.utils.DataMapper;

public class UserRepositoryImpl extends AbstractDAO<UserEntity> implements UserRepository {
    
    private final String USER_CORE_COLS =   "u.id, u.username, u.is_active, u.last_login, " +
                                            "e.id as emp_id, e.employee_code, e.full_name, " +
                                            "d.id as dept_id, d.name as dept_name, " +
                                            "p.id as posi_id, p.name as posi_name ";

    private final String USER_AUDIT_COLS =  "u.created_date, u.created_by, u.modified_date, u.modified_by ";

    private final String USER_FROM_JOIN =   "FROM users u " +
                                            "LEFT JOIN employees e ON u.employee_id = e.id " +
                                            "LEFT JOIN departments d ON e.department_id = d.id " +
                                            "LEFT JOIN positions p ON e.position_id = p.id ";
    
    private UserEntity mapUser(ResultSet rs) throws SQLException {
        UserEntity user = DataMapper.mapResultSetToObject(rs, UserEntity.class);
        if (rs.getObject("emp_id") != null) {
            EmployeeEntity emp = new EmployeeEntity();
            emp.setId(rs.getLong("emp_id"));
            emp.setEmployeeCode(rs.getString("employee_code"));
            emp.setFullName(rs.getString("full_name"));
            if (rs.getObject("dept_id") != null) {
                DepartmentEntity dept = new DepartmentEntity();
                dept.setId(rs.getLong("dept_id"));
                dept.setName(rs.getString("dept_name"));
                emp.setDepartment(dept);
            }
            if (rs.getObject("posi_id") != null) {
                PositionEntity pos = new PositionEntity();
                pos.setId(rs.getLong("posi_id"));
                pos.setName(rs.getString("posi_name"));
                emp.setPosition(pos);
            }
            user.setEmployee(emp);
        }
        return user; 
    }
    
    @Override
    public UserEntity findByUserId(Long userId){
        String sql = "SELECT " + USER_CORE_COLS + "," + USER_AUDIT_COLS + USER_FROM_JOIN + " WHERE u.id = ?";
        return querySingle(sql, rs -> mapUser(rs), userId);
    }
   
    @Override
    public UserEntity findByUsername(String username) {
        String sql = "SELECT u.password, " + USER_CORE_COLS + USER_FROM_JOIN + " WHERE username = ?";
        return querySingle(sql, rs -> mapUser(rs), username);
    }
    
    @Override
    public PageResponseDTO<UserEntity> search(UserSearchBuilder builder, Integer currentPage, Integer pageSize) {      
        // SQL dùng IN tối ưu hơn SELECT DISTINCT
        StringBuilder sql = new StringBuilder("SELECT " + USER_CORE_COLS + USER_FROM_JOIN + " WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        String whereClause = buildWhereClause(builder, params);
        sql.append(whereClause);
        
        String countSql = "SELECT COUNT(*) " + USER_FROM_JOIN + " WHERE 1=1 " + whereClause;
        int totalElements = count(countSql, params.toArray());
        sql.append(" ORDER BY u.id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY"); 
        params.add((currentPage-1)*pageSize);
        params.add(pageSize);
        
        List<UserEntity> content = queryList(sql.toString(), rs -> mapUser(rs), params.toArray());
        return new PageResponseDTO<>(content, totalElements, currentPage, pageSize);
    }
    
    private String buildWhereClause(UserSearchBuilder builder, List<Object> params) {
        StringBuilder where = new StringBuilder();
        if (builder.getKeyword() != null && !builder.getKeyword().trim().isEmpty()) {
            where.append(" AND (u.username LIKE ? OR e.full_name LIKE ? OR e.employee_code LIKE ?) ");
            String value = "%" + builder.getKeyword().trim() + "%";
            params.add(value); 
            params.add(value); 
            params.add(value);
        }
        if (builder.getIsActive() != null) {
            where.append(" AND u.is_active = ? ");
            params.add(builder.getIsActive());
        }
        if (builder.getDeptId() != null) {
            where.append(" AND e.department_id = ? ");
            params.add(builder.getDeptId());
        }
        if (builder.getPosiId() != null) { 
            where.append(" AND e.position_id = ? ");
            params.add(builder.getPosiId());
        }
        if (builder.getRoleId() != null) {
            where.append(" AND u.id IN (SELECT user_id FROM user_role WHERE role_id = ?) ");
            params.add(builder.getRoleId());
        }
        return where.toString();
    }

    @Override
    public Long create(UserEntity user) {
        String sql = "INSERT INTO users (username, password, employee_id, is_active, created_by, created_date) "
               + "VALUES (?, ?, ?, ?, ?, GETDATE())";
        return insert(sql, user.getUsername(), user.getPassword(), 
            user.getEmployee()!=null ? user.getEmployee().getId() : null,
            user.getIsActive(), user.getCreatedBy());
    }

    @Override
    public void updateEmployee(Long userId, Long employeeId, String modifiedBy) {
        String sql = "UPDATE users SET employee_id = ?, modified_by = ?, modified_date = GETDATE() WHERE id = ?";
        update(sql, employeeId, modifiedBy, userId);
        // employeeId ở đây NULLABLE được
    }
    
    @Override
    public void updateStatus(Long userId, Boolean isActive, String modifiedBy) {
        String sql = "UPDATE users SET is_active = ?, modified_by = ?, modified_date = GETDATE() WHERE id = ?";
        update(sql, isActive, modifiedBy, userId); 
    }

    @Override
    public void updatePassword(Long userId, String passwordHash, String modifiedBy) {
        String sql = "UPDATE users SET password = ?, modified_by = ?, modified_date = GETDATE() WHERE id = ?";
        update(sql, passwordHash, modifiedBy, userId); 
    }
    
    @Override
    public void updateLastLogin(Long userId){
        String sql = "UPDATE users SET last_login = GETDATE() WHERE id = ?";
        update(sql, userId);
    }

    @Override
    public boolean existEmployeeId(Long employeeId){
        String sql = "SELECT CASE WHEN EXISTS (SELECT 1 FROM users WHERE employee_id = ?) THEN 1 ELSE 0 END";
        Integer result = querySingle(sql, rs -> rs.getInt(1), employeeId);
        return result!=null && result==1;
    }
    
    @Override
    public void clearEmployeeReference(Long empId) {
        String sql = "UPDATE users SET employee_id = NULL, is_active = 0 WHERE employee_id = ?";
        update(sql, empId);
    }

}
