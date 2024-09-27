package com.application.filter;

import com.application.dto.SessionUserDTO;
import com.application.service.CodeService;
import com.application.util.CompilationResult;
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

@WebFilter("/*")
public class MainFilter implements Filter {

    private final CodeService codeService = new CodeService();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        Cookie[] cookies = req.getCookies();

        if (req.getSession().getAttribute("user") == null) {
            SessionUserDTO user = SessionUserDTO.builder().role(GUEST).build();
            req.getSession().setAttribute("user", user);
        }
        if (!checkCookiesContains(cookies, "compilationNumber")) {
            addCookie(resp, cookies, "compilationNumber", "1");
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

    private Optional<Cookie> getCookieByName(Cookie[] cookies, String cookieName) {
        return Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(cookieName)).findAny();
    }

    private void addCookie(HttpServletResponse response, Cookie[] cookies, String name, String value) {
        if (getCookieByName(cookies, name).isPresent()) {
            Cookie cookie = getCookieByName(cookies, name).get();
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(3600);
        response.addCookie(cookie);
    }

    private boolean checkCookiesContains(Cookie[] cookies, String cookieName) {
        return Arrays.stream(cookies).anyMatch(cookie -> cookie.getName().equals(cookieName));
    }
}
