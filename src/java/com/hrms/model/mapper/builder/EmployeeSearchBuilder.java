
package com.hrms.model.mapper.builder;

import java.util.List;

public class EmployeeSearchBuilder {
    private final Long id;
    private final String employeeNameCode;
    private final String departmentNameCode;
    private final String positionName;
    private final List<String> status;
    
    private EmployeeSearchBuilder(Builder builder) {
        this.id = builder.id;
        this.employeeNameCode = builder.employeeNameCode;
        this.departmentNameCode = builder.departmentNameCode;
        this.positionName = builder.positionName;
        this.status = builder.status;
    }

    public Builder toBuilder() {
        return new Builder()
            .setId(this.id)
            .setEmployeeNameCode(this.employeeNameCode)
            .setDepartmentNameCode(this.departmentNameCode)
            .setPositionName(this.positionName)
            .setStatus(this.status); 
    }
    
    public Long getId() {
        return id;
    }

    public String getEmployeeNameCode() {
        return employeeNameCode;
    }

    public String getDepartmentNameCode() {
        return departmentNameCode;
    }

    public String getPositionName() {
        return positionName;
    }

    public List<String> getStatus() {
        return status;
    }
    
    public static class Builder {
        private Long id;
        private String employeeNameCode; 
        private String departmentNameCode;
        private String positionName;
        private List<String> status;
        
        public EmployeeSearchBuilder build(){
            return new EmployeeSearchBuilder(this);
        }

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setEmployeeNameCode(String keyword) {
            this.employeeNameCode = keyword;
            return this;
        }

        public Builder setDepartmentNameCode(String departmentNameCode) {
            this.departmentNameCode = departmentNameCode;
            return this;
        }

        public Builder setPositionName(String positionNameCode) {
            this.positionName = positionNameCode;
            return this;
        }

        public Builder setStatus(List<String> status) {
            this.status = status;
            return this;
        }
        
        
    }
}
