package com.application.filter;

import com.application.dto.SessionUserDTO;
import com.application.service.CodeService;
import com.application.service.ServletService;
import com.application.util.annotation.CustomFilter;
import com.application.util.annotation.Inject;
import com.application.compiler.CompilationResult;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.LinkedHashMap;

import static com.application.entity.Role.*;

@CustomFilter("/*")
public class MainFilter implements Filter {

    @Inject
    private CodeService codeService;
    @Inject
    private ServletService servletService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        Cookie[] cookies = req.getCookies();

        if (req.getSession().getAttribute("user") == null) {
            SessionUserDTO user = SessionUserDTO.builder().role(GUEST).build();
            req.getSession().setAttribute("user", user);
        }
        if (!servletService.checkCookiesContains(cookies, "compilationNumber")) {
            servletService.addCookie(resp, cookies, "compilationNumber", "1");
        }
        if (req.getSession().getAttribute("recentCodes") == null) {
            req.getSession().setAttribute("recentCodes", new LinkedHashMap<Integer, CompilationResult>());
        }
        SessionUserDTO user = (SessionUserDTO) req.getSession().getAttribute("user");
        if (user.getRole().name().equals("USER")) {
            req.getSession().setAttribute("savedCodes", codeService.findCodesByUserId(user.getUserId()));
        }
        filterChain.doFilter(req, resp);

    }
}
