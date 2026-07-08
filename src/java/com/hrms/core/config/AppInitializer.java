
package com.hrms.core.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import com.hrms.core.security.PermissionCache;

@WebListener
public class AppInitializer implements ServletContextListener {
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("========================================");
        System.out.println("Application Starting...");
        System.out.println("========================================");
        try {
            PermissionCache.reload();
            PermissionCache.printStats();
            SystemConfig.reload();
            System.out.println(">>> All system caches warmed up!");
        } catch (Exception e) {
            System.err.println("Application startup failed!");
        }
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Application shutting down...");
        // Đơn giản là ko cần, tắt app tự mất khỏi RAM
    }
}
