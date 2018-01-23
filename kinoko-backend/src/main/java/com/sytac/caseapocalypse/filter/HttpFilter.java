package com.sytac.caseapocalypse.filter;

import com.sytac.caseapocalypse.controllers.MainController;
import com.sytac.caseapocalypse.model.db.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HttpFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        HttpSession session = request.getSession();

        String path = request.getServletPath();

        if (path.equals("/login")) {
            response.sendRedirect("/");
        } else if ("/api/logout".equals(path)) {
            session.removeAttribute("user");
            response.sendRedirect("/");
        } else if ("/api/connection/test".equals(path) || "/api/login".equals(path) || "OPTIONS".equals(request.getMethod()) || (!path.contains("/api") && !path.contains("/admin"))) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else if (session == null) {
            LOGGER.error("Session is not found.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User must login");
        } else {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                LOGGER.error("Session is not found.");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User must login");
            } else {
                //candidate can't login
                if ("candidate".equals(user.getRole().getName())) {
                    LOGGER.error("User is not allowed");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User is not allowed");
                }
                //if an user is trying to access the admin api but it's not an admin
                if (path.contains("/admin") && !user.getRole().getName().equals("admin")) {
                    LOGGER.error("User is not allowed, he is not an admin");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User is not allowed, he is not an admin");
                } else {
                    filterChain.doFilter(servletRequest, servletResponse);
                }
            }
        }
    }

    @Override
    public void destroy() {

    }

}
