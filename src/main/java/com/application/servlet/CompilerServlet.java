package com.application.servlet;

import com.application.dto.CreateCodeDTO;
import com.application.dto.CodeDTO;
import com.application.dto.SessionUserDTO;
import com.application.service.CodeService;
import com.application.service.ServletService;
import com.application.util.annotation.Inject;
import com.application.util.annotation.CustomServlet;
import com.application.compiler.CompilationResult;
import com.application.compiler.Compiler;
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

@CustomServlet("/compiler")
public class CompilerServlet extends HttpServlet {

    @Inject
    private CodeService codeService;
    @Inject
    private ServletService servletService;
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
            if (servletService.recentCodePresent(recentCodes, recentCodeNumber)) {
                req.setAttribute("code", recentCodes.get(recentCodeNumber).getCode());
                req.setAttribute("result", recentCodes.get(recentCodeNumber).getResult());
            }
        }
        if (req.getParameter("savedCodeTitle") != null && user.getRole().name().equals("USER")) {
            String savedCodeTitle = req.getParameter("savedCodeTitle");
            List<CodeDTO> savedCodes = (List<CodeDTO>) req.getSession().getAttribute("savedCodes");
            Optional<CodeDTO> requiredCode = savedCodes.stream().filter(i -> i.getTitle().equals(savedCodeTitle)).findAny();
            requiredCode.ifPresent(codeDTO -> req.setAttribute("code", codeDTO.getCode()));
        }
        if (req.getParameter("deleteSavedCode") != null && user.getRole().name().equals("USER")) {
            int codeId = Integer.parseInt(req.getParameter("deleteSavedCode"));
            codeService.deleteCodeById(user.getUserId(), codeId);
            req.getSession().setAttribute("savedCodes", codeService.findCodesByUserId(user.getUserId()));
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
            int compilationNumber = Integer.parseInt(servletService.getCookieByName(cookies, "compilationNumber").get().getValue());
            if (compilationNumber <= 10) {
                servletService.addCookie(resp, cookies, "compilationNumber", "" + (compilationNumber + 1));
            } else {
                compilationNumber = 1;
                servletService.addCookie(resp, cookies, "compilationNumber", "" + compilationNumber);
            }
            CompilationResult compilationResult = Compiler.compile(code, input);
            recentCodes.put(compilationNumber, compilationResult);
            req.setAttribute("result", compilationResult.getResult().replaceAll(System.lineSeparator(), "<br>"));
            req.setAttribute("code", code);
            req.getSession().setAttribute("recentCodes", servletService.sortedRecentCodes(recentCodes));
            req.getRequestDispatcher("/WEB-INF/jsp/index.jsp").forward(req, resp);
        }
        if ("download".equals(action)) {
            resp.setContentType("application/octet-stream");
            resp.setHeader("Content-Disposition", "attachment; filename=" + servletService.getClassName(code) + ".java");
            OutputStream out = resp.getOutputStream();
            out.write(code.getBytes());
            out.flush();
            out.close();
            resp.sendRedirect("/compiler");
        }
        if ("save".equals(action)) {
            SessionUserDTO user = (SessionUserDTO) req.getSession().getAttribute("user");
            String fileName = SecurityUtil.generateFileName();
            Files.write(Path.of("favorite/" + fileName + ".java"), code.getBytes());
            CreateCodeDTO createCodeDTO = CreateCodeDTO
                    .builder()
                    .userId(user.getUserId())
                    .path("favorite/" + fileName + ".java")
                    .title(req.getParameter("savedCodeTitle"))
                    .build();
            codeService.create(createCodeDTO);
            req.setAttribute("code", code);
            req.getSession().setAttribute("savedCodes", codeService.findCodesByUserId(user.getUserId()));
            req.getRequestDispatcher("/WEB-INF/jsp/index.jsp").forward(req, resp);
        }
    }
}
