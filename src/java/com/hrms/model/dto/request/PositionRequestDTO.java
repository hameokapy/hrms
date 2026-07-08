
package com.hrms.model.dto.request;

import java.math.BigDecimal;

public class PositionRequestDTO {
    private Long id;
    private String name;
    private BigDecimal baseSalaryLevel;
    private String description;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    
}
