
package com.hrms.model.dto.response;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

public class UserSummaryDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String username;
    private Boolean isActive;
    private LocalDateTime lastLogin;
    private String employeeDisplayName; // Dạng: EmployeeName(EmployeeCode)
    private String departmentAndPosition; // Dạng: DepartmentName(PositionName)
    private Set<String> roleNames;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmployeeDisplayName() {
        return employeeDisplayName;
    }

    public void setEmployeeDisplayName(String employeeDisplayName) {
        this.employeeDisplayName = employeeDisplayName;
    }

    public String getDepartmentAndPosition() {
        return departmentAndPosition;
    }

    public void setDepartmentAndPosition(String departmentAndPosition) {
        this.departmentAndPosition = departmentAndPosition;
    }

    public Set<String> getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(Set<String> roles) {
        this.roleNames = roles;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    
}
