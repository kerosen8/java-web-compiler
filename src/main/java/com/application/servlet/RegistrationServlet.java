package com.application.servlet;

import com.application.dto.CreateUserDTO;
import com.application.dto.SessionUserDTO;
import com.application.entity.Role;
import com.application.entity.User;
import com.application.service.UserService;
import com.application.validator.CreateUserValidator;
import com.application.validator.ValidationResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.*;

@WebServlet("/registration")
public class RegistrationServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/registration.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CreateUserDTO createUserDTO = CreateUserDTO
                .builder()
                .email(req.getParameter("email"))
                .password(req.getParameter("password"))
                .build();
        CreateUserValidator createUserValidator = new CreateUserValidator();
        ValidationResult result = createUserValidator.validate(createUserDTO);
        if (result.isValid()) {
            List<String> hash = hashPassword(createUserDTO.getPassword());
            createUserDTO.setPassword(hash.get(0));
            createUserDTO.setSalt(hash.get(1));
            SessionUserDTO user = userService.create(createUserDTO);
            req.getSession().setAttribute("user", user);
            resp.sendRedirect("/compiler");
        } else {
            req.setAttribute("errors", result.getErrors());
            req.getRequestDispatcher("/WEB-INF/jsp/registration.jsp").forward(req, resp);
        }
    }

    @SneakyThrows
    private List<String> hashPassword(String password) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
        messageDigest.update(salt);
        byte[] hashedPassword = messageDigest.digest(password.getBytes(UTF_8));
        StringBuilder hashedPasswordBuilder = new StringBuilder();
        for (byte b : hashedPassword) {
            hashedPasswordBuilder.append(String.format("%02x", b));
        }
        StringBuilder hashedSaltBuilder = new StringBuilder();
        for (byte b : salt) {
            hashedSaltBuilder.append(String.format("%02x", b));
        }
        return List.of(hashedPasswordBuilder.toString(), hashedSaltBuilder.toString());
    }
}
