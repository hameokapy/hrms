
package com.hrms.core.security;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class PermissionChecker {
    private PermissionChecker() {}
    
    public static boolean hasPermission(String permissionKey) {
        Set<Long> roleIds = SecurityContext.getCurrentRoleIds();
        return PermissionCache.hasPermission(roleIds, permissionKey);
    }
    
    public static boolean hasAnyPermission(String... permissionKeys) {
        Set<Long> roleIds = SecurityContext.getCurrentRoleIds();
        return PermissionCache.hasAnyPermission(roleIds, permissionKeys);
    }
    
    public static boolean hasAllPermissions(String... permissionKeys) {
        Set<Long> roleIds = SecurityContext.getCurrentRoleIds();
        return PermissionCache.hasAllPermissions(roleIds, permissionKeys);
    }
    
    public static void requirePermission(String permissionKey) {
        if (!hasPermission(permissionKey)) {
            throw new SecurityException("Access denied: missing permission " + permissionKey);
        }
    }
    
    private static final List<String> SCOPE_PRIORITY = List.of("ALL", "DEPT", "OWN"); // Dùng List để đảm bảo thứ tự put vào
    public static String getHighestScope(String permissionKey) {
        Map<String, Set<String>> userPermi = PermissionCache.getPermissionsAndScopes(SecurityContext.getCurrentRoleIds());        
        Set<String> scopes = userPermi.get(permissionKey);
        if (scopes==null || scopes.isEmpty()) 
            return null;
        for (String priority : SCOPE_PRIORITY) {
            if (scopes.contains(priority))
                return priority;
        }
        return null;
    }

}
