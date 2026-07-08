
package com.hrms.core.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.hrms.repository.impl.SystemSettingRepositoryImpl;

public class SystemConfig {
    
    public enum Key {
        // có field gì mới thì fill tiếp vào đây
        LAST_GEN_YEAR("LAST_GEN_YEAR"),
        ANNUAL_LEAVE_DAYS("ANNUAL_LEAVE_DAYS"),
        SICK_LEAVE_DAYS("SICK_LEAVE_DAYS"),
        DEFAULT_PAGE_SIZE("DEFAULT_PAGE_SIZE");

        private final String value;
        
        Key(String value) { 
            this.value = value; 
        }
        
        public String getValue() { 
            return value; 
        }
    }
    
    private SystemConfig() {}

    // Dùng ConcurrentHashMap thì không cần volatile nữa vì nó thread safe sẵn rồi
    private static final Map<String, String> cache = new ConcurrentHashMap<>();

    public static void reload() {
        System.out.println("SystemConfig: Reloading...");
        try {
            SystemSettingRepositoryImpl repo = new SystemSettingRepositoryImpl(); 
            Map<String, String> settings = repo.getAllSettingMappings();
            if (settings != null) {
                cache.clear();
                cache.putAll(settings);
            }
        } catch (Exception e) {
            System.err.println("SystemConfig: Reload failed!");
        }
    }

    public static String get(String key) {
        return cache.getOrDefault(key, "");
    }

    public static int getInt(String key) {
        try {
            return Integer.parseInt(cache.getOrDefault(key, "0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public static int getPageSize() {
        int size = getInt(Key.DEFAULT_PAGE_SIZE.getValue());
        return size>0 ? size : 10;
    }
    
}
