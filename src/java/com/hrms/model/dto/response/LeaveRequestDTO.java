
package com.hrms.model.dto.response;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class LeaveRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Long employeeId;
    private String employeeNameAndCode;
    private String departmentNameAndCode;
    private String type;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer numDays;
    private Integer annualBalanceRemain;
    private Integer sickBalanceRemain;
    private String reason;
    private String status;
    private String approverNameAndCode;
    private LocalDateTime approvedDate;
    private LocalDateTime createdDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDepartmentNameAndCode() {
        return departmentNameAndCode;
    }

    public void setDepartmentNameAndCode(String departmentName) {
        this.departmentNameAndCode = departmentName;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeNameAndCode() {
        return employeeNameAndCode;
    }

    public void setEmployeeNameAndCode(String employeeNameAndCode) {
        this.employeeNameAndCode = employeeNameAndCode;
    }

    public String getApproverNameAndCode() {
        return approverNameAndCode;
    }

    public void setApproverNameAndCode(String approverNameAndCode) {
        this.approverNameAndCode = approverNameAndCode;
    }

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

    public Integer getNumDays() {
        return numDays;
    }

    public void setNumDays(Integer numDays) {
        this.numDays = numDays;
    }

    public Integer getAnnualBalanceRemain() {
        return annualBalanceRemain;
    }

    public void setAnnualBalanceRemain(Integer annualBalanceRemain) {
        this.annualBalanceRemain = annualBalanceRemain;
    }

    public Integer getSickBalanceRemain() {
        return sickBalanceRemain;
    }

    public void setSickBalanceRemain(Integer sickBalanceRemain) {
        this.sickBalanceRemain = sickBalanceRemain;
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

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    
}
