
package com.hrms.model.entity;

import java.math.BigDecimal;

public class PositionEntity extends BaseEntity {
    private String name;
    private BigDecimal baseSalaryLevel;
    private String description;
    private String status;

    public PositionEntity() {
    }

    public PositionEntity(Long id) {
        super(id);
    }
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    
}
