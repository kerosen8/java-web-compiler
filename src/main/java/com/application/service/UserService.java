package com.application.service;

import com.application.dao.UserDAO;
import com.application.dto.CreateUserDTO;
import com.application.entity.User;

import java.util.Optional;

public class UserService {

    private final UserDAO userDAO = new UserDAO();

    public User createUser(CreateUserDTO createUserDTO) {
        return userDAO.create(createUserDTO);
    }

    public Optional<User> findByEmail(String email) {
        return userDAO.findByEmail(email);
    }

}
