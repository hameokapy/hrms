
package com.hrms.model.mapper.builder;

import java.math.BigDecimal;

public class PositionSearchBuilder {
    private final Long id;
    private final String name;
    private final String status;
    private final BigDecimal salaryFrom;
    private final BigDecimal salaryTo;

    private PositionSearchBuilder(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.status = builder.status;
        this.salaryFrom = builder.salaryFrom;
        this.salaryTo = builder.salaryTo;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getSalaryFrom() {
        return salaryFrom;
    }

    public BigDecimal getSalaryTo() {
        return salaryTo;
    }
    
    public static class Builder {
        private Long id;
        private String name;
        private String status;
        private BigDecimal salaryFrom;
        private BigDecimal salaryTo;
        
        public PositionSearchBuilder build(){
            return new PositionSearchBuilder(this);
        }

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder setSalaryFrom(BigDecimal salaryFrom) {
            this.salaryFrom = salaryFrom;
            return this;
        }

        public Builder setSalaryTo(BigDecimal salaryTo) {
            this.salaryTo = salaryTo;
            return this;
        }
        
        
    }
}
