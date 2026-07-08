
package com.hrms.core.security;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.hrms.repository.RolePermissionRepository;
import com.hrms.repository.impl.RolePermissionRepositoryImpl;

public class PermissionCache {
    private PermissionCache() {}
    
    // Dùng volatile vì: Nhiều ng dùng là nhiều thread, dùng nó các Thread khác đc update dữ liệu mới right away
    private static volatile Map<Long, Map<String, Set<String>>> cache = new HashMap<>();    
       
    public static void reload() {
        System.out.println("PermissionCache: Reloading...");
        try {
            RolePermissionRepository repo = new RolePermissionRepositoryImpl();
            Map<Long, Map<String, Set<String>>> mappings = repo.getAllRolePermissionMappings();
            Map<Long, Map<String, Set<String>>> newCache = new HashMap<>();
            for (Map.Entry<Long, Map<String, Set<String>>> roleEntry : mappings.entrySet()) {
                Map<String, Set<String>> permMap = roleEntry.getValue();
                Map<String, Set<String>> immutablePermMap = new HashMap<>();
                for (Map.Entry<String, Set<String>> permEntry : permMap.entrySet())
                    immutablePermMap.put(permEntry.getKey(), Collections.unmodifiableSet(new HashSet<>(permEntry.getValue())));
                newCache.put(roleEntry.getKey(), Collections.unmodifiableMap(immutablePermMap));
                // Phòng thủ level 1: áp dụng Defensive Copy: 
                // nếu truyền cái gốc ra đây, tức cache.get(roleId), thì thằng user nào đó sẽ có thể
                // manipulate rồi .clear() để xóa hết dữ liệu của con role tương ứng trong cache gốc
                // nên mới phải chơi trò truyền cứng dữ liệu vào biến mới toanh rồi đưa biến đó cho user
                // Phòng thủ level 2: dùng Collections.unmodifiableSet:
                // triệt tiêu quyền sửa bất cứ gì bên trong cái new HashSet mới khởi tạo đó 
            }
            cache = newCache;
        } catch (Exception e) {
            System.err.println("PermissionCache: Failed to reload!");
        }
    }
    
    public static Map<String, Set<String>> getPermissionsAndScopes(Set<Long> userRoleIds) {
        if (userRoleIds==null || userRoleIds.isEmpty()) 
            return Collections.emptyMap();
        Map<String, Set<String>> combined = new HashMap<>();
        for (Long roleId : userRoleIds) {
            Map<String, Set<String>> rolePerms = cache.get(roleId);
            if (rolePerms != null) 
                rolePerms.forEach((perm, scopes) -> {combined.computeIfAbsent(perm, k -> new HashSet<>()).addAll(scopes);});
        }
        return combined;
    }
    
    public static void printStats() {
        System.out.println("========== PermissionCache Stats ==========");
        cache.forEach((roleId, perms) -> {
            System.out.println("Role " + roleId + ": " + perms.size() + " permissions");
            perms.forEach((key, scopes) -> {
                System.out.println("   - " + key + " " + scopes);
            });
        });
        System.out.println("===========================================");
    }
    
    public static Set<String> getUserPermissions(Set<Long> userRoleIds) {
        if (userRoleIds==null || userRoleIds.isEmpty())
            return Collections.emptySet();
        Set<String> allPermissions = new HashSet<>();
        for (Long roleId : userRoleIds) {
            Map<String, Set<String>> permissions = cache.get(roleId);
            if (permissions != null)
                allPermissions.addAll(permissions.keySet());
        }
        return allPermissions;
    }
    
    public static boolean hasPermission(Set<Long> roleIds, String key) {
        if(roleIds==null || key==null)
            return false;
        for (Long roleId : roleIds) {
            Map<String, Set<String>> permissions = cache.get(roleId);
            if(permissions!=null && permissions.containsKey(key))
                return true;
        }
        return false;
    }
    
    public static boolean hasAnyPermission(Set<Long> roleIds, String... keys) {
        if(roleIds==null || keys==null)
            return false;
        for (String key : keys) {
            if(hasPermission(roleIds, key))
                return true;
        }
        return false;
    }
    
    public static boolean hasAllPermissions(Set<Long> roleIds, String... keys) {
        if(roleIds==null || keys==null)
            return false;
        for (String key : keys) {
            if(!hasPermission(roleIds, key))
                return false;
        }
        return true;
    }
}
