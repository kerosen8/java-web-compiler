package com.application.servlet;

import com.application.dto.LoginUserDTO;
import com.application.dto.SessionUserDTO;
import com.application.entity.User;
import com.application.service.UserService;
import com.application.util.annotation.CustomServlet;
import com.application.util.annotation.Inject;
import com.application.validator.LoginUserValidator;
import com.application.validator.ValidationResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

import static com.application.entity.Role.*;

@CustomServlet("/login")
public class LoginServlet extends HttpServlet {

    @Inject
    private UserService userService;
    @Inject
    private LoginUserValidator loginUserValidator;

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
        ValidationResult vr = loginUserValidator.validate(loginUserDTO);
        if (vr.isValid() && userService.authentication(loginUserDTO.getEmail(), loginUserDTO.getPassword())) {
            Optional<User> optionalUser = userService.findByEmail(loginUserDTO.getEmail());
            SessionUserDTO user = SessionUserDTO
                    .builder()
                    .userId(optionalUser.get().getId())
                    .role(USER)
                    .build();
            req.getSession().setAttribute("user", user);
        } else {
            req.setAttribute("errors", vr.getErrors());
            req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
        }
        resp.sendRedirect("/compiler");
    }

}
