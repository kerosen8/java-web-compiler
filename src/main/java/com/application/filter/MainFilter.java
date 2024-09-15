package com.application.filter;

import com.application.dto.SessionUserDTO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static com.application.entity.Role.*;

@WebFilter("/*")
public class MainFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (request.getSession().getAttribute("user") == null) {
            SessionUserDTO user = SessionUserDTO.builder().role(GUEST).build();
            request.getSession().setAttribute("user", user);
            filterChain.doFilter(servletRequest, servletResponse);
        }

        filterChain.doFilter(request, response);

    }
}
