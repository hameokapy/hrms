
package com.hrms.core.config;

import com.hrms.core.exception.technical.ConnectionException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBContext {
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=HRMS2;encrypt=true;trustServerCertificate=true";
    private static final String USER = "admin";
    private static final String PASS = "admin";
    
    private static final ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

    private DBContext() {}
    
    public static Connection getConnection() {
        Connection conn = threadLocal.get();
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                conn = DriverManager.getConnection(URL, USER, PASS);
                threadLocal.set(conn); 
                // Đại khái ThreadLocal giống hộc tủ cất dữ liệu của một con Thread tương ứng
                // threadLocal.set() là set vào đúng con Thread đó, con Thread khác get() ra null
                // Thread đc cấp lúc nào? khi có 1 request gửi tới, thread được cấp để xử lý cv đó
                // đại khái thread là đơn vị con Tomcat (as web server) dùng để xử lý các request
                // khi nào đến bước trả response, thì con thread này bị thu hồi trở lại Thread Pool
                // nên mới cần .clear() thông tin lưu tạm ở thread đó ở cuối mỗi request
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            throw new ConnectionException("Connecting database failed", e);
        }
        return conn;
    }

    public static void releaseConnection() {
        try {
            Connection conn = threadLocal.get();
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) { 
            System.err.println("Cannot close database connection: " + e.getMessage());
        }
        threadLocal.remove();
    }
}
