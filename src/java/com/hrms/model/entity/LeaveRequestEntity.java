
package com.hrms.model.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LeaveRequestEntity extends BaseEntity {
    private String type;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private String status;
    private LocalDateTime approvedDate;
    // Employee-LeaveRequest mqh 1:N
    private EmployeeEntity employee;
    // Manager-LeaveRequest mqh 1:N
    private EmployeeEntity approver;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(LocalDateTime approvedDate) {
        this.approvedDate = approvedDate;
    }

    public EmployeeEntity getEmployee() {
        return employee;
    }

    public void setEmployee(EmployeeEntity employee) {
        this.employee = employee;
    }

    public EmployeeEntity getApprover() {
        return approver;
    }

    public void setApprover(EmployeeEntity approver) {
        this.approver = approver;
    }
    
    
}
