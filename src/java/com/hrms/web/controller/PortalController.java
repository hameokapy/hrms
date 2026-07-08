
package com.hrms.web.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name="PortalController", urlPatterns={"/portal/*"})
public class PortalController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();
        if(path==null || path.equals("/")) {
            response.sendRedirect(request.getContextPath() + "/portal/dashboard");
            return;
        }
        String contentPage = "";
        String pageTitle = "HRMS Management";
        switch (path) {
            case "/dashboard" -> {
                contentPage = "/WEB-INF/views/portal/dashboard.jsp";
                pageTitle = "Dashboard";
                request.setAttribute("pageJS", "/assets/js/portal/dashboard.js");
            }
            // case default: con autho filter xử lý cho r
        }
        request.setAttribute("contentPage", contentPage);
        request.setAttribute("pageTitle", pageTitle);
        request.getRequestDispatcher("/WEB-INF/layouts/portal-layout.jsp").forward(request, response);
    }
}
