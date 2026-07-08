
package com.hrms.repository.impl;

import com.hrms.model.dto.response.DashboardMgtDTO;

public class DashboardMgtRepositoryImpl extends AbstractDAO<DashboardMgtDTO>{
    
    public DashboardMgtDTO getDashboardStats() {
        String sql = """
            SELECT 
                (SELECT COUNT(*) FROM employees WHERE status != 'INACTIVE') AS totalEmployees,
                (SELECT COUNT(*) FROM departments WHERE status = 'ACTIVE') AS totalDepartments,
                (SELECT COUNT(*) FROM leave_requests WHERE status = 'PENDING') AS pendingLeaveRequests,
                (SELECT COUNT(*) FROM departments WHERE status = 'ACTIVE' AND manager_id IS NULL) AS deptsWithoutManager,
                (SELECT COUNT(*) FROM employees e WHERE e.status IN ('ACTIVE', 'PENDING', 'ON_LEAVE') 
                       AND NOT EXISTS (SELECT 1 FROM contracts c WHERE c.employee_id = e.id AND c.status = 'ACTIVE'
                )) AS empsWithoutActiveContract,
                (SELECT COUNT(*) FROM employees e WHERE e.status IN ('ACTIVE', 'ON_LEAVE') 
                       AND NOT EXISTS (SELECT 1 FROM users u WHERE u.employee_id = e.id AND u.is_active = 1
                 )) AS empsWithoutUserAccount
        """;
        return querySingle(sql, DashboardMgtDTO.class);
    }
}
