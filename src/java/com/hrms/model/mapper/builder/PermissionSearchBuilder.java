
package com.hrms.model.mapper.builder;

public class PermissionSearchBuilder {
    private final Long id;
    private final String permissionKey;
    private final String description;
    private final String roleName;

    private PermissionSearchBuilder(Builder builder) {
        this.id = builder.id;
        this.permissionKey = builder.permissionKey;
        this.description = builder.description;
        this.roleName = builder.roleName;
    }

    public Long getId() {
        return id;
    }

    public String getPermissionKey() {
        return permissionKey;
    }

    public String getDescription() {
        return description;
    }

    public String getRoleName() {
        return roleName;
    }

    public static class Builder {
        private Long id;
        private String permissionKey;
        private String description;
        private String roleName;

        public Builder setId(Long id) { 
            this.id = id; 
            return this; 
        }
        public Builder setPermissionKey(String key) { 
            this.permissionKey = key; 
            return this; 
        }
        public Builder setDescription(String desc) { 
            this.description = desc; 
            return this; 
        }
        public Builder setRoleName(String roleName) { 
            this.roleName = roleName; 
            return this; 
        }

        public PermissionSearchBuilder build() {
            return new PermissionSearchBuilder(this);
        }
    }
}
