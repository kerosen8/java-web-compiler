package com.application.filter;

import com.application.dto.CreateUserDTO;
import com.application.entity.Role;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/*")
public class MainFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (request.getSession().getAttribute("user") == null) {
            request.getSession().setAttribute("user", CreateUserDTO.builder().role(Role.GUEST).build());
            filterChain.doFilter(servletRequest, servletResponse);
        }

        filterChain.doFilter(request, response);

    }
}
