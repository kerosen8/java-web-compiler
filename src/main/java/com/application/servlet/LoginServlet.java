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
import lombok.extern.java.Log;

import java.io.IOException;
import java.util.Optional;

import static com.application.entity.Role.*;

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
        if (result.isValid()) {
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

}
