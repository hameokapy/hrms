
package com.hrms.core.constant;

import java.util.HashMap;
import java.util.Map;

public enum RoleEnums {
    ADMIN(1L), MANAGER(3L), HR(2L), EMPLOYEE(4L);
    
    private final Long id;
    private static final Map<Long, RoleEnums> CACHE = new HashMap<>();
    
    RoleEnums(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    // Này để nạp dữ liệu vào Map ngay khi app start
    static {
        for (RoleEnums r : values()) {
            CACHE.put(r.id, r);
        }
    }

    public static RoleEnums fromId(Long id) {
        return CACHE.get(id);
        // Trả về NULL nếu ko tìm thấy
    }
    
    public boolean isManagement() {
        return this == ADMIN || this == HR || this == MANAGER;
    }
}