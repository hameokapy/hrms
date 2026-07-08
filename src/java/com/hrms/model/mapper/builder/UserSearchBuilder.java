
package com.hrms.model.mapper.builder;

public class UserSearchBuilder {
    private final String keyword;
    private final Boolean isActive;
    private final Long deptId;  
    private final Long posiId;  
    private final Long roleId;  
    
    private UserSearchBuilder(Builder builder){
        this.keyword = builder.keyword;
        this.isActive = builder.isActive;
        this.deptId = builder.deptId;
        this.posiId = builder.posiId;
        this.roleId = builder.roleId;
    }

    public String getKeyword() {
        return keyword;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public Long getDeptId() {
        return deptId;
    }

    public Long getPosiId() {
        return posiId;
    }

    public Long getRoleId() {
        return roleId;
    }
    
    public static class Builder {
        private String keyword;
        private Boolean isActive;
        private Long deptId;  
        private Long posiId;  
        private Long roleId;  

        public UserSearchBuilder build(){
            return new UserSearchBuilder(this);
        }
        
        public Builder setKeyword(String keyword) {
            this.keyword = keyword;
            return this;
        }

        public Builder setIsActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public Builder setDeptId(Long deptId) {
            this.deptId = deptId;
            return this;
        }

        public Builder setPosiId(Long posiId) {
            this.posiId = posiId;
            return this;
        }

        public Builder setRoleId(Long roleId) {
            this.roleId = roleId;
            return this;
        }
    }
}
