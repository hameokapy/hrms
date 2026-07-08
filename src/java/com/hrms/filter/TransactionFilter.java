
package com.hrms.filter;

import com.hrms.core.config.DBContext;
import com.hrms.core.security.SecurityContext;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;

public class TransactionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } finally {
            DBContext.releaseConnection();
            SecurityContext.clear(); // Cuối request thì .clear() vì cơ chế Thread Pool của bọn Tomcat
        }
    }
    
}
