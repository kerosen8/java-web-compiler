package com.application.servlet;

import com.application.dto.CreateUserDTO;
import com.application.entity.Role;
import com.application.entity.User;
import com.application.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/registration")
public class RegistrationServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/registration.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CreateUserDTO userToBeAdded = CreateUserDTO
                .builder()
                .email(req.getParameter("email"))
                .password(req.getParameter("password"))
                .role(Role.GUEST)
                .build();
        User user = userService.createUser(userToBeAdded);
        req.getSession().setAttribute("user", user);
        resp.sendRedirect("/compiler");
    }
}
