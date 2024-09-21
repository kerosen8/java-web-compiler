package com.application.service;

import com.application.dao.UserDAO;
import com.application.dto.CreateUserDTO;
import com.application.dto.SessionUserDTO;
import com.application.entity.User;
import com.application.util.SecurityUtil;
import lombok.SneakyThrows;

import java.util.Optional;

import static com.application.entity.Role.*;

public class UserService {

    private final UserDAO userDAO = new UserDAO();

    public SessionUserDTO create(CreateUserDTO createUserDTO) {
        User user = User
                .builder()
                .email(createUserDTO.getEmail())
                .password(createUserDTO.getPassword())
                .salt(createUserDTO.getSalt())
                .build();
        User addedUser = userDAO.create(user);
        return SessionUserDTO
                .builder()
                .userId(addedUser.getId())
                .role(USER)
                .build();
    }

    public Optional<User> findByEmail(String email) {
        return userDAO.findByEmail(email);
    }

    @SneakyThrows
    public boolean login(String email, String password) {
        Optional<User> userFromDb = findByEmail(email);
        if (userFromDb.isPresent()) {
            String storedSalt = userFromDb.get().getSalt();
            String storedHash = userFromDb.get().getPassword();
            return SecurityUtil.verifyHash(password, storedHash, storedSalt);
        }
        return false;
    }

}
