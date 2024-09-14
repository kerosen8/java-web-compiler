package com.application.servlet;

import com.application.service.UserService;
import com.application.util.CompilerProcess;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

@WebServlet("/compiler")
public class MainServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String jSessionId = Arrays.stream(req.getCookies())
                .filter(cookie -> cookie.getName().equals("JSESSIONID")).findFirst().get().getValue();
        String currentSessionFolderPath = "resources/" + jSessionId + "/";
        Cookie[] cookies = req.getCookies();

        if (!checkCookieContains(cookies, "compilationNumber")) {
            resp.addCookie(new Cookie("compilationNumber", String.valueOf(1)));
        }

        if (req.getParameter("selectedCompilation") != null) {
            int selectedCompilation = Integer.parseInt(req.getParameter("selectedCompilation"));
            req.setAttribute("code", readAllBytesByStringFilePath("resources/" + jSessionId + "/" + selectedCompilation + "/Main.java"));
            req.setAttribute("lastCompiles", lastCompiles(currentSessionFolderPath));
        } else {
            req.setAttribute("code", readAllBytesByStringFilePath("resources/Main.java"));
        }

        req.getRequestDispatcher("/WEB-INF/jsp/index.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String jSessionId = Arrays.stream(req.getCookies())
                .filter(cookie -> cookie.getName().equals("JSESSIONID")).findFirst().get().getValue();
        String currentSessionFolderPath = "resources/" + jSessionId + "/";
        Cookie[] cookies = req.getCookies();
        int compilationNumber = Integer.parseInt(getCookieByName(cookies, "compilationNumber").get().getValue());
        String code = req.getParameter("code");

        if (compilationNumber > 10) {
            compilationNumber = 1;
            resp.addCookie(new Cookie("compilationNumber", String.valueOf(compilationNumber)));
        } else {
            resp.addCookie(new Cookie("compilationNumber", String.valueOf(compilationNumber + 1)));
        }

        Files.createDirectories(new File("resources/" + jSessionId + "/" + compilationNumber).toPath());
        Files.write(new File(currentSessionFolderPath + compilationNumber + "/Main.java").toPath(), code.getBytes());
        CompilerProcess.compile(jSessionId, compilationNumber);
        String result = readAllBytesByStringFilePath(currentSessionFolderPath + compilationNumber + "/output.txt");
        req.setAttribute("result", result);
        req.setAttribute("code", code);
        req.setAttribute("lastCompiles", lastCompiles(currentSessionFolderPath));
        req.getRequestDispatcher("/WEB-INF/jsp/index.jsp").forward(req, resp);
    }

    private Map<Integer, String> lastCompiles(String currentSessionFolderPath) throws IOException {
        Map<Integer, String> mapToBeSorted = new HashMap<>();
        for (int i = 1; i <= directoriesCount(new File(currentSessionFolderPath)); ++i) {
            mapToBeSorted.put(i, mapFromFileTimeToLocalDate(Files.readAttributes(new File(currentSessionFolderPath + i + "/Main.java").toPath(), BasicFileAttributes.class).lastModifiedTime()));
        }

        List<Map.Entry<Integer, String>> list = new ArrayList<>(mapToBeSorted.entrySet());
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        Map<Integer, String> result = new LinkedHashMap<>();
        for (Map.Entry<Integer, String> entry : list) result.put(entry.getKey(), entry.getValue());

        return result;
    }

    private String mapFromFileTimeToLocalDate(FileTime fileTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        Instant instant = fileTime.toInstant();
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());

        return zonedDateTime.toLocalDateTime().format(formatter);
    }

    @SneakyThrows
    private int directoriesCount(File file) throws IOException {
        try (Stream<Path> paths = Files.list(file.toPath())) {
            return (int) paths.filter(Files::isDirectory).count();
        }
    }

    private String readAllBytesByStringFilePath(String filePath) throws IOException {
        return new String(Files.readAllBytes(new File(filePath).toPath()));
    }

    private boolean checkCookieContains(Cookie[] cookies, String cookieName) {
        return Arrays.stream(cookies).anyMatch(cookie -> cookie.getName().equals(cookieName));
    }

    private Optional<Cookie> getCookieByName(Cookie[] cookies, String cookieName) {
        return Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(cookieName)).findAny();
    }

}
