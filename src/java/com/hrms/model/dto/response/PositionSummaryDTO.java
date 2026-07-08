
package com.hrms.model.dto.response;

import java.io.Serializable;
import java.math.BigDecimal;

public class PositionSummaryDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String name;
    private BigDecimal baseSalaryLevel;
    private String status;
    private Long employeeCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBaseSalaryLevel() {
        return baseSalaryLevel;
    }

    public void setBaseSalaryLevel(BigDecimal baseSalaryLevel) {
        this.baseSalaryLevel = baseSalaryLevel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getEmployeeCount() {
        return employeeCount;
    }

    public void setEmployeeCount(Long employeeCount) {
        this.employeeCount = employeeCount;
    }
    
    
    
}
