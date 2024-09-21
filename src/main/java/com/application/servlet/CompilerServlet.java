package com.application.servlet;

import com.application.util.CompilationResult;
import com.application.util.Compiler;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/compiler")
public class CompilerServlet extends HttpServlet {

    private final String startCode = """
            public class Main {
                public static void main(String[] args) {
                    
                }
            }
            """;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Cookie[] cookies = req.getCookies();

        if (!checkCookiesContains(cookies, "compilationNumber")) {
            resp.addCookie(new Cookie("compilationNumber", String.valueOf(1)));
            req.getSession().setAttribute("latestCompilations", new LinkedHashMap<Integer, CompilationResult>());
        }

        if (req.getParameter("selectedCompilation") != null) {
            int selectedCompilation = Integer.parseInt(req.getParameter("selectedCompilation"));
            Map<Integer, CompilationResult> results = (LinkedHashMap<Integer, CompilationResult>) req.getSession().getAttribute("latestCompilations");
            req.setAttribute("result", results.get(selectedCompilation).getResult());
            req.setAttribute("code", results.get(selectedCompilation).getCode());
            req.setAttribute("latestCompilations", sortedLatestCompilations(results));
        } else {
            req.setAttribute("code", startCode);
        }

        req.getRequestDispatcher("/WEB-INF/jsp/index.jsp").forward(req, resp);
    }

    @Override
    @SneakyThrows
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Cookie[] cookies = req.getCookies();
        int compilationNumber = Integer.parseInt(getCookieByName(cookies, "compilationNumber").get().getValue());
        String code = req.getParameter("code");
        String action = req.getParameter("action");
        Map<Integer, CompilationResult> latestCompilations = (LinkedHashMap<Integer, CompilationResult>) req.getSession().getAttribute("latestCompilations");

        if ("compile".equals(action)) {
            if (compilationNumber <= 10) {
                resp.addCookie(new Cookie("compilationNumber", String.valueOf(compilationNumber + 1)));
            } else {
                compilationNumber = 1;
                resp.addCookie(new Cookie("compilationNumber", String.valueOf(compilationNumber)));
            }
            CompilationResult compilationResult = Compiler.compile(code);
            latestCompilations.put(compilationNumber, compilationResult);
            req.setAttribute("result", compilationResult.getResult());
            req.setAttribute("code", code);
            req.getSession().setAttribute("latestCompilations", sortedLatestCompilations(latestCompilations));
            req.getRequestDispatcher("/WEB-INF/jsp/index.jsp").forward(req, resp);
        }
        if ("download".equals(action)) {
            resp.setContentType("application/octet-stream");
            resp.setHeader("Content-Disposition", "attachment; filename=Main.java");
            OutputStream out = resp.getOutputStream();
            out.write(code.getBytes());
            out.flush();
            out.close();
            resp.sendRedirect("/compiler");
        }
        if ("save".equals(action)) {

        }
    }

    private Map<Integer, CompilationResult> sortedLatestCompilations(Map<Integer, CompilationResult> resultMap) throws IOException {
        return resultMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.comparing(CompilationResult::getCompilationTime).reversed()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e2,
                        LinkedHashMap::new
                ));
    }

    private boolean checkCookiesContains(Cookie[] cookies, String cookieName) {
        return Arrays.stream(cookies).anyMatch(cookie -> cookie.getName().equals(cookieName));
    }

    private Optional<Cookie> getCookieByName(Cookie[] cookies, String cookieName) {
        return Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(cookieName)).findAny();
    }

}