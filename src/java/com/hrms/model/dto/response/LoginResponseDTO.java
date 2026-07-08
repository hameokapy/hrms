
package com.hrms.model.dto.response;

import java.io.Serializable;
import java.util.Set;

public class LoginResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long userId;
    private String username;
    private String employeeName; // hiện chỗ "Hi, [empName]!" ở dashboard
    
    // Này dùng trasient để GSON ko chuyển cho FE cái này, vì dùng cho session lưu bên BE thôi
    private transient Set<Long> roleIds;
    private transient Long employeeId;
    private transient Long deptId;

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Set<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(Set<Long> roleIds) {
        this.roleIds = roleIds;
    }
    
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    
}
