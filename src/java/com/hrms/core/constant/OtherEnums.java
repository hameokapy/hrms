
package com.hrms.core.constant;

import java.util.HashMap;
import java.util.Map;

public class OtherEnums {
    public enum Department {
        ACTIVE("ACTIVE"),
        INACTIVE("INACTIVE");

        private final String value;
        private static final Map<String, Department> CACHE = new HashMap<>();
        
        Department(String value) { 
            this.value = value; 
        }
        
        public String getValue() { 
            return value; 
        }
        
        static {
            for(Department s : values()){
                CACHE.put(s.value, s);
            }
        }
    }
    
    public enum Position {
        ACTIVE("ACTIVE"),
        INACTIVE("INACTIVE");

        private final String value;
        private static final Map<String, Position> CACHE = new HashMap<>();
        
        Position(String value) { 
            this.value = value; 
        }
        
        public String getValue() { 
            return value; 
        }
        
        static {
            for(Position s : values()){
                CACHE.put(s.value, s);
            }
        }
    }
    
    public enum Scope {
        ALL("ALL"),
        DEPT("DEPT"),
        OWN("OWN");
        
        private final String value;
        private static final Map<String, Scope> CACHE = new HashMap<>();
        
        Scope(String value) { 
            this.value = value; 
        }
        
        public String getValue() { 
            return value; 
        }
        
        static {
            for(Scope s : values()){
                CACHE.put(s.value, s);
            }
        }
    }
}
