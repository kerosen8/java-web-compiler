package com.application.filter;

import com.application.dto.SessionUserDTO;
import com.application.service.CodeService;
import com.application.util.ServletUtil;
import com.application.util.annotation.CustomFilter;
import com.application.util.annotation.Inject;
import com.application.util.compiler.CompilationResult;
import com.application.util.dicontainer.factory.BeanFactory;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Optional;

import static com.application.entity.Role.*;

//@WebFilter("/*")
@CustomFilter("/*")
public class MainFilter implements Filter {

    @Inject
    private CodeService codeService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        Cookie[] cookies = req.getCookies();

        if (req.getSession().getAttribute("user") == null) {
            SessionUserDTO user = SessionUserDTO.builder().role(GUEST).build();
            req.getSession().setAttribute("user", user);
        }
        if (!ServletUtil.checkCookiesContains(cookies, "compilationNumber")) {
            ServletUtil.addCookie(resp, cookies, "compilationNumber", "1");
        }
        if (req.getSession().getAttribute("recentCodes") == null) {
            req.getSession().setAttribute("recentCodes", new LinkedHashMap<Integer, CompilationResult>());
        }
        SessionUserDTO user = (SessionUserDTO) req.getSession().getAttribute("user");
        if (user.getRole().name().equals("USER")) {
            req.getSession().setAttribute("savedCodes", codeService.findByUserId(user.getUserId()));
        }
        filterChain.doFilter(req, resp);

    }
}
