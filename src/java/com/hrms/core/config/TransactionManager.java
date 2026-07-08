
package com.hrms.core.config;

import com.hrms.core.exception.technical.TransactionException;
import java.sql.*;

// Mấy hàm search, find thường ko cần Transaction Manager vì lấy dữ liệu thôi, 
// ko thay đổi nên ko cần cơ chế rollback đa service, trừ case cần độ chính xác cao.
// Mấy hàm search, find khi ko dùng Transaction Manager này thì thật ra 
// nó dùng cơ chế setAutoCommit(true) của JDBC.

public class TransactionManager {
    private TransactionManager() {}
    
    @FunctionalInterface
    public interface TransactionAction<T> {
        T execute() throws Exception;
    }

    public static <T> T doInTransaction(TransactionAction<T> action) {
        Connection conn = DBContext.getConnection();
        try {
            conn.setAutoCommit(false);
            T result = action.execute();
            conn.commit();
            return result;
        } catch (Exception e) {
            handleRollback(conn);
            throw new TransactionException("Transaction failed", e);
        }
        // Để TransactionFilter đảm nhiệm đóng transaction ở cuối mỗi request
    }

    public static void runInTransaction(Runnable action) {
        Connection conn = DBContext.getConnection();
        try {
            conn.setAutoCommit(false);
            action.run();
            conn.commit();
        } catch (Exception e) {
            handleRollback(conn);
            throw new TransactionException("Transaction failed", e);
        }
        // Để TransactionFilter đảm nhiệm đóng transaction ở cuối mỗi request
    }

    private static void handleRollback(Connection conn) {
        try {
            if (conn!=null && !conn.isClosed())
                conn.rollback();
        } catch (SQLException e) {
            throw new TransactionException("Rollback failed", e);
        }
    }
}
