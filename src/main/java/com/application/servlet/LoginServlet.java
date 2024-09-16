package com.application.servlet;

import com.application.dto.LoginUserDTO;
import com.application.dto.SessionUserDTO;
import com.application.entity.Role;
import com.application.entity.User;
import com.application.service.UserService;
import com.application.validator.LoginUserValidator;
import com.application.validator.ValidationResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.java.Log;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static com.application.entity.Role.*;
import static java.nio.charset.StandardCharsets.UTF_8;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LoginUserDTO loginUserDTO = LoginUserDTO
                .builder()
                .email(req.getParameter("email"))
                .password(req.getParameter("password"))
                .build();
        LoginUserValidator loginUserValidator = new LoginUserValidator();
        ValidationResult result = loginUserValidator.validate(loginUserDTO);
        if (result.isValid() && authenticate(loginUserDTO.getEmail(), loginUserDTO.getPassword())) {
            Optional<User> userFromDb = userService.findByEmail(loginUserDTO.getEmail());
            SessionUserDTO user = SessionUserDTO
                    .builder()
                    .userId(userFromDb.get().getId())
                    .role(USER)
                    .build();
            req.getSession().setAttribute("user", user);
        } else {
            req.setAttribute("errors", result.getErrors());
            req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
        }
        resp.sendRedirect("/compiler");
    }

    @SneakyThrows
    private boolean authenticate(String email, String password) {
        Optional<User> userFromDb = userService.findByEmail(email);
        if (userFromDb.isPresent()) {
            byte[] storedSalt = hexStringToByteArray(userFromDb.get().getSalt());
            byte[] storedHash = hexStringToByteArray(userFromDb.get().getPassword());
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            messageDigest.update(storedSalt);
            byte[] hashedPassword = messageDigest.digest(password.getBytes(UTF_8));
            return MessageDigest.isEqual(hashedPassword, storedHash);
        }
        return false;
    }

    private byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));
        }
        return data;
    }

}
