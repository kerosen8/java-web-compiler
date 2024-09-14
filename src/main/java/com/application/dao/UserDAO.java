package com.application.dao;

import com.application.dto.CreateUserDTO;
import com.application.entity.Role;
import com.application.entity.User;
import com.application.util.DBConnectorManager;

import java.sql.*;
import java.util.Optional;

import static java.sql.Statement.*;

public class UserDAO {

    private final String CREATE_SQL = "INSERT INTO users (email, password) VALUES (?, ?);";
    private final String FIND_BY_EMAIL_SQL = "SELECT * FROM users WHERE email = ?;";

    public User create(CreateUserDTO userDTO) {
        try (Connection connection = DBConnectorManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(CREATE_SQL, RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, userDTO.getEmail());
            preparedStatement.setString(2, userDTO.getPassword());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            return User.builder()
                    .id(resultSet.getInt(1))
                    .email(userDTO.getEmail())
                    .password(userDTO.getPassword())
                    .role(Role.USER)
                    .build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<User> findByEmail(String email) {
        try (Connection connection = DBConnectorManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_EMAIL_SQL)) {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                User user = User.builder()
                        .id(resultSet.getInt("id"))
                        .email(resultSet.getString("email"))
                        .password(resultSet.getString("password"))
                        .role(Role.USER)
                        .build();
                return Optional.of(user);
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
