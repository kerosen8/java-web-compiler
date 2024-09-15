package com.application.dao;

import com.application.dto.CreateUserDTO;
import com.application.entity.User;
import com.application.util.DBConnectorManager;

import java.sql.*;
import java.util.Optional;

import static com.application.entity.Role.*;
import static java.sql.Statement.*;

public class UserDAO {

    private final String CREATE_SQL = "INSERT INTO users (email, password) VALUES (?, ?);";
    private final String FIND_BY_EMAIL_SQL = "SELECT * FROM users WHERE email = ?;";

    public User create(User user) {
        try (Connection connection = DBConnectorManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(CREATE_SQL, RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            user.setId(resultSet.getInt(1));
            return user;
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
