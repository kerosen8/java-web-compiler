package com.application.servlet;

import com.application.dto.CreateUserDTO;
import com.application.dto.SessionUserDTO;
import com.application.service.UserService;
import com.application.util.annotation.CustomServlet;
import com.application.util.annotation.Inject;
import com.application.util.secutiry.SecurityUtil;
import com.application.validator.CreateUserValidator;
import com.application.validator.ValidationResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@CustomServlet("/registration")
public class RegistrationServlet extends HttpServlet {

    @Inject
    private UserService userService;
    @Inject
    private CreateUserValidator createUserValidator;

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
        ValidationResult vr = createUserValidator.validate(createUserDTO);
        if (vr.isValid()) {
            String salt = SecurityUtil.generateSalt();
            String hashedPassword = SecurityUtil.generateHash(createUserDTO.getPassword(), salt);
            createUserDTO.setPassword(hashedPassword);
            createUserDTO.setSalt(salt);
            SessionUserDTO user = userService.create(createUserDTO);
            req.getSession().setAttribute("user", user);
            resp.sendRedirect("/compiler");
        } else {
            req.setAttribute("errors", vr.getErrors());
            req.getRequestDispatcher("/WEB-INF/jsp/registration.jsp").forward(req, resp);
        }
    }

}
