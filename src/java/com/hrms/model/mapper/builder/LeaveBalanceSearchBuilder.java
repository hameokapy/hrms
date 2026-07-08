
package com.hrms.model.mapper.builder;

public class LeaveBalanceSearchBuilder {
    private final Integer year;
    private final String employeeKeyword; // search emp code và name
    private final String departmentKeyword; // search dept code và name

    public Integer getYear() {
        return year;
    }

    public String getEmployeeKeyword() { 
        return employeeKeyword;
    }

    public String getDepartmentKeyword() {
        return departmentKeyword;
    }

    private LeaveBalanceSearchBuilder(Builder builder) {
        this.year = builder.year;
        this.employeeKeyword = builder.employeeKeyword;
        this.departmentKeyword = builder.departmentKeyword;
    }
    
    public static class Builder {
        private Integer year;
        private String employeeKeyword; 
        private String departmentKeyword;
        
        public LeaveBalanceSearchBuilder build(){
            return new LeaveBalanceSearchBuilder(this);
        }

        public Builder setYear(Integer year) {
            this.year = year;
            return this;
        }

        public Builder setEmployeeKeyword(String employeeKeyword) {
            this.employeeKeyword = employeeKeyword;
            return this;
        }

        public Builder setDepartmentKeyword(String departmentKeyword) {
            this.departmentKeyword = departmentKeyword;
            return this;
        }
    }
    
}
