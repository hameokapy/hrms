
package com.hrms.web.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name="LoginController", urlPatterns={"/login"})
public class LoginController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getSession(false)!=null && request.getSession(false).getAttribute("sessionUser")!=null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        String error = request.getParameter("error");
        if (error != null) {
            request.setAttribute("error", error); 
        }
        request.getRequestDispatcher("/WEB-INF/views/common/login.jsp").forward(request, response);
    }
}
