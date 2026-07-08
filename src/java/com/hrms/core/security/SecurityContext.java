
package com.hrms.core.security;

import java.util.Set;

public class SecurityContext {
    private static final ThreadLocal<SecurityContext> context = new ThreadLocal<>();
    private Long userId;
    private String username;
    private Set<Long> roleIds;
    private Long empId;
    private Long deptId;
    
    // bỏ dsach permissions vì mỗi Thread cx đều gọi ntn thì tốn RAM, đã có PermissionCache sẵn rồi
    // private Map<String, Set<String>> permissionsAndScopes;
    
    private SecurityContext() {}
    
    public static void setContext(Long userId, String username, Set<Long> roleIds, Long empId, Long deptId) {
        SecurityContext ctx = new SecurityContext();
        ctx.userId = userId;
        ctx.username = username;
        // Định chơi Defensive Copy nhưng class này ko chơi setters nên ko cần bảo trợ
        // vì nhìn chung nó dùng getters only để tránh bị mutable cho rồi
        // ctx.roleIds = (roleIds != null) ? new HashSet<>(roleIds) : new HashSet<>();
        ctx.roleIds = roleIds;
        ctx.deptId = deptId;
        ctx.empId = empId;
        context.set(ctx);
    }
    
    public static SecurityContext get() {
        return context.get();
    }
    
    public static void clear() {
        context.remove();
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public Set<Long> getRoleIds() {
        return roleIds;
    }

    public Long getEmpId() {
        return empId;
    }

    public Long getDeptId() {
        return deptId;
    }
    
    // Mấy static methods dưới này tránh đc NullPointerException so với khi gọi SecurityContext.get().getUserId()
    
    public static Long getCurrentUserId() {
        SecurityContext ctx = context.get();
        return ctx!=null ? ctx.userId : null;
    }
    
    public static String getCurrentUsername() {
        SecurityContext ctx = context.get();
        return ctx!=null ? ctx.username : null;
    }
    
    public static Set<Long> getCurrentRoleIds() {
        SecurityContext ctx = context.get();
        return ctx!=null ? ctx.roleIds : null;
    }
    
    public static Long getCurrentDeptId() {
        SecurityContext ctx = context.get();
        return ctx!=null ? ctx.deptId : null;
    }
    
    public static Long getCurrentEmpId() {
        SecurityContext ctx = context.get();
        return ctx!=null ? ctx.getEmpId() : null;
    }
    
    public SecurityContext cloneWithNewRoles(Set<Long> newRoles) {
        SecurityContext newCtx = new SecurityContext();
        newCtx.userId = this.userId;
        newCtx.username = this.username;
        newCtx.empId = this.empId;
        newCtx.deptId = this.deptId;
        newCtx.roleIds = newRoles; 
        return newCtx;
    }
}
