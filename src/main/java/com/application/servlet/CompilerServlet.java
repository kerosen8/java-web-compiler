package com.application.servlet;

import com.application.dto.CreateCodeDTO;
import com.application.dto.CodeDTO;
import com.application.dto.SessionUserDTO;
import com.application.service.CodeService;
import com.application.util.ServletUtil;
import com.application.util.annotation.Inject;
import com.application.util.annotation.CustomServlet;
import com.application.util.compiler.CompilationResult;
import com.application.util.compiler.Compiler;
import com.application.util.secutiry.SecurityUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@CustomServlet("/compiler")
public class CompilerServlet extends HttpServlet {

    @Inject
    private CodeService codeService;
    private final String startCode = """
            public class Main {
                public static void main(String[] args) {
                    
                }
            }
            """;

    @Override
    @SuppressWarnings("unchecked")
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SessionUserDTO user = (SessionUserDTO) req.getSession().getAttribute("user");
        req.setAttribute("code", startCode);
        if (req.getParameter("recentCodeNumber") != null) {
            int recentCodeNumber = Integer.parseInt(req.getParameter("recentCodeNumber"));
            Map<Integer, CompilationResult> recentCodes = (LinkedHashMap<Integer, CompilationResult>) req.getSession().getAttribute("recentCodes");
            if (recentCodePresent(recentCodes, recentCodeNumber)) {
                req.setAttribute("code", recentCodes.get(recentCodeNumber).getCode());
                req.setAttribute("result", recentCodes.get(recentCodeNumber).getResult());
                req.setAttribute("recentCodes", sortedRecentCodes(recentCodes));
            }
        }
        if (req.getParameter("title") != null && user.getRole().name().equals("USER")) {
            String title = req.getParameter("title");
            List<CodeDTO> savedCodes = (List<CodeDTO>) req.getSession().getAttribute("savedCodes");
            Optional<CodeDTO> requiredCode = savedCodes.stream().filter(i -> i.getTitle().equals(title)).findAny();
            requiredCode.ifPresent(codeDTO -> req.setAttribute("code", codeDTO.getCode()));
        }
        req.getRequestDispatcher("/WEB-INF/jsp/index.jsp").forward(req, resp);
    }

    @Override
    @SneakyThrows
    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter("code");
        String action = req.getParameter("action");

        if ("compile".equals(action)) {
            Cookie[] cookies = req.getCookies();
            String input = req.getParameter("input");
            Map<Integer, CompilationResult> recentCodes = (LinkedHashMap<Integer, CompilationResult>) req.getSession().getAttribute("recentCodes");
            int compilationNumber = Integer.parseInt(ServletUtil.getCookieByName(cookies, "compilationNumber").get().getValue());
            if (compilationNumber <= 10) {
                ServletUtil.addCookie(resp, cookies, "compilationNumber", "" + (compilationNumber + 1));
            } else {
                compilationNumber = 1;
                ServletUtil.addCookie(resp, cookies, "compilationNumber", "" + compilationNumber);
            }
            CompilationResult compilationResult = Compiler.compile(code, input);
            recentCodes.put(compilationNumber, compilationResult);
            req.setAttribute("result", compilationResult.getResult().replaceAll(System.lineSeparator(), "<br>"));
            req.setAttribute("code", code);
            req.getSession().setAttribute("recentCodes", sortedRecentCodes(recentCodes));
            req.getRequestDispatcher("/WEB-INF/jsp/index.jsp").forward(req, resp);
        }
        if ("download".equals(action)) {
            resp.setContentType("application/octet-stream");
            resp.setHeader("Content-Disposition", "attachment; filename=" + getClassName(code) + ".java");
            OutputStream out = resp.getOutputStream();
            out.write(code.getBytes());
            out.flush();
            out.close();
            resp.sendRedirect("/compiler");
        }
        if ("save".equals(action)) {
            SessionUserDTO user = (SessionUserDTO) req.getSession().getAttribute("user");
            String pathName = SecurityUtil.generateFilePath();
            Files.write(Path.of("favorite/" + pathName + ".java"), code.getBytes());
            CreateCodeDTO createCodeDTO = CreateCodeDTO
                    .builder()
                    .userId(user.getUserId())
                    .path("favorite/" + pathName + ".java")
                    .title(req.getParameter("title"))
                    .build();
            codeService.create(createCodeDTO);
            req.setAttribute("code", code);
            req.getSession().setAttribute("savedCodes", codeService.findByUserId(user.getUserId()));
            req.getRequestDispatcher("/WEB-INF/jsp/index.jsp").forward(req, resp);
        }
    }

    private Map<Integer, CompilationResult> sortedRecentCodes(Map<Integer, CompilationResult> resultMap) throws IOException {
        return resultMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.comparing(CompilationResult::getCompilationTime).reversed()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e2,
                        LinkedHashMap::new
                ));
    }

    private String getClassName(String code) {
        String target = "public class";
        int startIndex = code.indexOf(target) + target.length() + 1;
        StringBuilder result = new StringBuilder();
        for (int i = startIndex; ; i++) {
            char c = code.charAt(i);
            if (c != ' ') {
                result.append(code.charAt(i));
            } else break;
        }
        return result.toString();
    }

    private boolean recentCodePresent(Map<Integer, CompilationResult> results, int compilationNumber) {
        for (Integer entry : results.keySet()) {
            if (entry == compilationNumber) {
                return true;
            }
        }
        return false;
    }

}
