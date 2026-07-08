
package com.hrms.model.dto.response;

import java.io.Serializable;
import java.util.Set;

public class PermissionDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String permissionKey;
    private String description;
    private Set<String> roleNames;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPermissionKey() {
        return permissionKey;
    }

    public void setPermissionKey(String permissionKey) {
        this.permissionKey = permissionKey;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<String> getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(Set<String> roleName) {
        this.roleNames = roleName;
    }

    
    
}
