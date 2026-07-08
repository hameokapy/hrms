
package com.hrms.core.constant;

import java.util.HashMap;
import java.util.Map;

public class LeaveRequestEnums {
    
    public enum Type {
        ANNUAL("ANNUAL"),
        SICK("SICK"),
        UNPAID("UNPAID");

        private final String value;
        private static final Map<String, Type> CACHE = new HashMap<>();
        
        Type(String value) { 
            this.value = value; 
        }
        
        public String getValue() { 
            return value; 
        }
        
        static {
            for(Type s : values()){
                CACHE.put(s.value, s);
            }
        }
        
        public static boolean isValid(String type) {
            for (Type t : Type.values()) {
                if (t.name().equals(type)) 
                    return true;
            }
            return false;
        }
    }
    
    public enum Status {
        APPROVED("APPROVED"),
        PENDING("PENDING"),
        REJECTED("REJECTED"),
        CANCELLED("CANCELLED");

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
    }
}
