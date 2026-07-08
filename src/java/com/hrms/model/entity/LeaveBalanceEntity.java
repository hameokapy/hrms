
package com.hrms.model.entity;

public class LeaveBalanceEntity extends BaseEntity {
    private Integer year;
    private Integer annualTotalDays;
    private Integer annualUsedDays;
    private Integer annualRemainingDays;
    private Integer sickTotalDays;
    private Integer sickUsedDays;
    private Integer sickRemainingDays;
    // Employee-LeaveBalance mqh 1:N
    private EmployeeEntity employee;

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getAnnualTotalDays() {
        return annualTotalDays;
    }

    public void setAnnualTotalDays(Integer annualTotalDays) {
        this.annualTotalDays = annualTotalDays;
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

    public Integer getSickTotalDays() {
        return sickTotalDays;
    }

    public void setSickTotalDays(Integer sickTotalDays) {
        this.sickTotalDays = sickTotalDays;
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

    public EmployeeEntity getEmployee() {
        return employee;
    }

    public void setEmployee(EmployeeEntity employee) {
        this.employee = employee;
    }
    
    
}
