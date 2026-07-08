
package com.hrms.model.mapper.builder;

public class DepartmentSearchBuilder {
    private final Long id;
    private final String code;
    private final String name;
    private final String managerName;
    private final String location;
    private final String status;
    
    private DepartmentSearchBuilder(Builder builder) {
        this.id = builder.id;
        this.code = builder.code;
        this.name = builder.name;
        this.managerName = builder.managerName;
        this.location = builder.location;
        this.status = builder.status;
    }
    
    public Builder toBuilder() {
        return new Builder()
            .setId(this.id)
            .setCode(this.code)
            .setName(this.name)
            .setManagerName(this.managerName)
            .setLocation(this.location)
            .setStatus(this.status);
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getManagerName() {
        return managerName;
    }

    public String getLocation() {
        return location;
    }

    public String getStatus() {
        return status;
    }
    
    public static class Builder {
        private Long id;
        private String code;
        private String name;
        private String managerName;
        private String location;
        private String status;
        
        public DepartmentSearchBuilder build(){
            return new DepartmentSearchBuilder(this);
        }

        public Builder setId(Long id) {
            this.id = id;   
            return this;
        }

        public Builder setCode(String code) {
            this.code = code;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setManagerName(String managerName) {
            this.managerName = managerName;
            return this;
        }

        public Builder setLocation(String location) {
            this.location = location;
            return this;
        }

        public Builder setStatus(String status) {
            this.status = status;
            return this;
        }
        
        
    }
    
    
}
