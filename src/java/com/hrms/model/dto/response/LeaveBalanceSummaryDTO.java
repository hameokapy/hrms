
package com.hrms.model.dto.response;

import java.io.Serializable;

public class LeaveBalanceSummaryDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String employeeNameCode;
    private String departmentNameCode;
    private Integer annualUsedDays;
    private Integer annualRemainingDays;
    private Integer sickUsedDays;
    private Integer sickRemainingDays;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmployeeNameCode() {
        return employeeNameCode;
    }

    public void setEmployeeNameCode(String employeeNameCode) {
        this.employeeNameCode = employeeNameCode;
    }

    public String getDepartmentNameCode() {
        return departmentNameCode;
    }

    public void setDepartmentNameCode(String departmentNameCode) {
        this.departmentNameCode = departmentNameCode;
    }

    public Integer getAnnualUsedDays() {
        return annualUsedDays;
    }

    public void setAnnualUsedDays(Integer annualUsedDays) {
        this.annualUsedDays = annualUsedDays;
    }

    public Integer getAnnualRemainingDays() {
        return annualRemainingDays;
    }

    public void setAnnualRemainingDays(Integer annualRemainingDays) {
        this.annualRemainingDays = annualRemainingDays;
    }

    public Integer getSickUsedDays() {
        return sickUsedDays;
    }

    public void setSickUsedDays(Integer sickUsedDays) {
        this.sickUsedDays = sickUsedDays;
    }

    public Integer getSickRemainingDays() {
        return sickRemainingDays;
    }

    public void setSickRemainingDays(Integer sickRemainingDays) {
        this.sickRemainingDays = sickRemainingDays;
    }
    
    
}
