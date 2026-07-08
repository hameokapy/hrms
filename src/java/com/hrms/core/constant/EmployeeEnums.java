
package com.hrms.core.constant;

import java.util.HashMap;
import java.util.Map;

public class EmployeeEnums {

    public enum Status {
        ACTIVE("ACTIVE"),
        ON_LEAVE("ON_LEAVE"),
        INACTIVE("INACTIVE"),
        PENDING("PENDING");

        private final String value;
        private static final Map<String, Status> CACHE = new HashMap<>();
        
        Status(String value) { 
            this.value = value; 
        }
        
        public String getValue() { 
            return value; 
        }
        
        static {
            for(Status s : values()){
                CACHE.put(s.value, s);
            }
        }
        
        public static Status fromString(String status) {
            return CACHE.get(status.toUpperCase());
            // Trả về NULL nếu ko tìm thấy
        }
    }
}