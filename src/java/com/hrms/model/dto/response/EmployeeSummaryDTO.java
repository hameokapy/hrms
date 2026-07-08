
package com.hrms.model.dto.response;

import java.io.Serializable;

public class EmployeeSummaryDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String employeeCode;
    private String fullName;
    private String departmentNameAndCode;
    private String positionName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDepartmentNameAndCode() {
        return departmentNameAndCode;
    }

    public void setDepartmentNameAndCode(String departmentNameAndCode) {
        this.departmentNameAndCode = departmentNameAndCode;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionNameAndCode) {
        this.positionName = positionNameAndCode;
    }
    
    
}
