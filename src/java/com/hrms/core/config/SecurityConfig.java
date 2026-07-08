
package com.hrms.core.config;

import com.hrms.core.constant.PermissionConstants;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class SecurityConfig {
    private SecurityConfig() {}
    
    private static final Set<String> PUBLIC_PATHS = Set.of(
        "/api/auth/login",
        "/login",
        "/assets/.*"
    );
    
    private static final Set<String> AUTHEN_ONLY_PATHS = Set.of(
        "/api/auth/logout",
        "/home"
    ); 
    
    /**
     * API Permission Mapping
     * 
     * Dùng LinkedHashMap để giữ insertion order (thật ra mấy cái ở dưới toàn pattern unique 
     * nên order doesnt matter, nhưng khi có case pattern trùng thì phải chơi trò này)
     * 
     * Bộ quy tắc sắp xếp:
     *      1. Pattern cụ thể TRƯỚC pattern chung chung
     *      2. Path dài TRƯỚC path ngắn
     *      3. Group theo từng module
     *      4. Trong module: trạng thái POST/PUT (write) TRƯỚC trạng thái GET (read)
     */
    private static final Map<Pattern, String> API_PERMISSION_MAP = new LinkedHashMap<>();
    private static final Map<Pattern, String> PAGE_PERMISSION_MAP = new LinkedHashMap<>();

    static {
        // THUỘC PAGE PERMISSION: CHÁU MANAGEMENT
        registerPage("GET:/management/dashboard$", PermissionConstants.Department.VIEW); // để tượng trưng permission này thôi (vì cả 3 role management đều có), thằng filterRoleIdsByPath() mới là thứ thực sự lo đc truy cập hay ko
        registerPage("GET:/management/under-construction$", PermissionConstants.Department.VIEW); // để tượng trưng permission này thôi 
        registerPage("GET:/management/departments$", PermissionConstants.Department.VIEW);
        registerPage("GET:/management/positions$", PermissionConstants.Position.VIEW);
        registerPage("GET:/management/employees$", PermissionConstants.Employee.VIEW);
        registerPage("GET:/management/leaves$", PermissionConstants.Leave.VIEW_BALANCE);
        registerPage("GET:/management/users$", PermissionConstants.User.VIEW_ALL);
        registerPage("GET:/management/roles$", PermissionConstants.Role.VIEW);
        // THUỘC PAGE PERMISSION: CHÁU EMPLOYEE
        registerPage("GET:/portal/dashboard$", PermissionConstants.Department.VIEW); // để tượng trưng permission này thôi 
        
        // THUỘC API PERMISSION: DASHBOARD BÊN MANAGEMENT
        registerApi("GET:/api/dashboardmgt$", PermissionConstants.Department.VIEW); // để tượng trưng permission này thôi 
        // THUỘC API PERMISSION: MODULE USER
        registerApi("PUT:/api/users/employee$", PermissionConstants.User.BIND_EMPLOYEE);
        registerApi("PUT:/api/users/password$", PermissionConstants.User.RESET_PASSWORD);
        registerApi("PUT:/api/users/roles$", PermissionConstants.User.ASSIGN_ROLE);
        registerApi("PUT:/api/users/status$", PermissionConstants.User.TOGGLE_STATUS);
        registerApi("POST:/api/users$", PermissionConstants.User.CREATE);
        registerApi("GET:/api/users/\\d+$", PermissionConstants.User.VIEW_DETAIL);
        registerApi("GET:/api/users$", PermissionConstants.User.VIEW_ALL);
        // THUỘC API PERMISSION: MODULE ROLE
        registerApi("PUT:/api/roles$", PermissionConstants.Role.UPDATE);
        registerApi("GET:/api/roles$", PermissionConstants.Role.VIEW);
        // THUỘC API PERMISSION: MODULE PERMISSION
        registerApi("PUT:/api/permissions$", PermissionConstants.Permission.UPDATE);
        registerApi("GET:/api/permissions$", PermissionConstants.Permission.VIEW);
        // THUỘC API PERMISSION: MODULE DEPARTMENT
        registerApi("GET:/api/departments/\\d+/employees$", PermissionConstants.Employee.VIEW);
        registerApi("PUT:/api/departments/manager$", PermissionConstants.Department.ASSIGN_MANAGER);
        registerApi("PUT:/api/departments/status$", PermissionConstants.Department.DELETE);
        registerApi("PUT:/api/departments$", PermissionConstants.Department.EDIT);
        registerApi("POST:/api/departments$", PermissionConstants.Department.CREATE);
        registerApi("GET:/api/departments/\\d+$", PermissionConstants.Department.VIEW_DETAIL);
        registerApi("GET:/api/departments$", PermissionConstants.Department.VIEW);
        // THUỘC API PERMISSION: MODULE POSITION
        registerApi("PUT:/api/positions/status$", PermissionConstants.Position.DELETE);
        registerApi("PUT:/api/positions$", PermissionConstants.Position.EDIT);
        registerApi("POST:/api/positions$", PermissionConstants.Position.CREATE);
        registerApi("GET:/api/positions/\\d+$", PermissionConstants.Position.VIEW_DETAIL);
        registerApi("GET:/api/positions$", PermissionConstants.Position.VIEW);
        // THUỘC API PERMISSION: MODULE EMPLOYEE
        registerApi("PUT:/api/employees/department$", PermissionConstants.Employee.ASSIGN_DEPT);
        registerApi("PUT:/api/employees/position$", PermissionConstants.Employee.ASSIGN_POSI);
        registerApi("PUT:/api/employees/status$", PermissionConstants.Employee.DELETE);
        registerApi("PUT:/api/employees$", PermissionConstants.Employee.EDIT);
        registerApi("POST:/api/employees$", PermissionConstants.Employee.CREATE);
        registerApi("GET:/api/employees/\\d+$", PermissionConstants.Employee.VIEW_DETAIL);
        registerApi("GET:/api/employees$", PermissionConstants.Employee.VIEW);
        // THUỘC API PERMISSION: MODULE LEAVE
        registerApi("PUT:/api/leave/requests/approve$", PermissionConstants.Leave.APPROVE_REQUEST);
        registerApi("PUT:/api/leave/requests/cancel$", PermissionConstants.Leave.CANCEL_REQUEST);
        registerApi("PUT:/api/leave/requests/update$", PermissionConstants.Leave.UPDATE_REQUEST);
        registerApi("POST:/api/leave/requests$", PermissionConstants.Leave.CREATE_REQUEST);
        registerApi("GET:/api/leave/requests$", PermissionConstants.Leave.VIEW_REQUEST);
        registerApi("GET:/api/leave/balance$", PermissionConstants.Leave.VIEW_BALANCE);
    }

    private static String findPermission(String method, String path, Map<Pattern, String> map) {
        String target = method.toUpperCase() + ":" + path;
        for (Map.Entry<Pattern, String> entry : map.entrySet()) {
            if (entry.getKey().matcher(target).matches()) {
                return entry.getValue();
            }
        }
        return null;
    }
    
    public static String getRequiredApiPermission(String method, String path) {
        return findPermission(method, path, API_PERMISSION_MAP);
    }
    
    public static String getRequiredPagePermission(String method, String path) {
        return findPermission(method, path, PAGE_PERMISSION_MAP);
    }

    public static boolean isPublic(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::matches);
    }
    
    public static boolean isAuthenticatedOnly(String path) {
        return AUTHEN_ONLY_PATHS.stream().anyMatch(path::matches);
    }
    
    private static void registerPage(String regex, String perm) {
        PAGE_PERMISSION_MAP.put(Pattern.compile(regex), perm);
    }
    
    private static void registerApi(String regex, String perm) {
        API_PERMISSION_MAP.put(Pattern.compile(regex), perm);
    }
}
