
package com.hrms.model.mapper.builder;

import java.time.LocalDate;

public class LeaveRequestSearchBuilder {
    private final Long id;
    private final String keyword; // tìm employee_name or employee_code or dept_code or dept_name
    private final String status;
    private final String type;
    private final LocalDate fromDate;
    private final LocalDate toDate;
    
    private LeaveRequestSearchBuilder(Builder builder) {
        this. id = builder.id;
        this.keyword = builder.keyword;
        this.status = builder.status;
        this.type = builder.type;
        this.fromDate = builder.fromDate;
        this.toDate = builder.toDate;
    }

    public Long getId() {
        return id;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }
    
    public static class Builder {
        private Long id;
        private String keyword;
        private String status;
        private String type;
        private LocalDate fromDate;
        private LocalDate toDate;
        
        public LeaveRequestSearchBuilder build(){
            return new LeaveRequestSearchBuilder(this);
        }

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }
        
        public Builder setKeyword(String keyword) {
            this.keyword = keyword;
            return this;
        }

        public Builder setStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Builder setFromDate(LocalDate fromDate) {
            this.fromDate = fromDate;
            return this;
        }

        public Builder setToDate(LocalDate toDate) {
            this.toDate = toDate;
            return this;
        }
        
        
    }
}
