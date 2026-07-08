
package com.hrms.model.mapper.builder;

public class RoleSearchBuilder {
    private final Long id;
    private final String roleName;
    private final String description;
    
    private RoleSearchBuilder(Builder builder){
        this.id = builder.id;
        this.roleName = builder.roleName;
        this.description = builder.description;
    }

    public Long getId() {
        return id;
    }    
    
    public String getRoleName() {
        return roleName;
    }

    public String getDescription() {
        return description;
    }
    
    public static class Builder {
        private Long id;
        private String roleName;
        private String description;
        
        public RoleSearchBuilder build(){
            return new RoleSearchBuilder(this);
        }

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }
        
        public Builder setRoleName(String roleName) {
            this.roleName = roleName;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }        
    }
    
}
