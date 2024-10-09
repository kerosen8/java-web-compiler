package com.application.dao;

import com.application.entity.Code;
import com.application.util.database.DBConnectorManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Statement.*;

public class CodeDAO {

    private final String CREATE_SQL = "INSERT INTO code (user_id, path, title) VALUES (?, ?, ?)";
    private final String FIND_BY_USER_ID_SQL = "SELECT * FROM code WHERE user_id = ?";
    private final String DELETE_BY_CODE_ID = "DELETE FROM code WHERE id = ?";

    public void create(Code code) {
        try (Connection connection = DBConnectorManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(CREATE_SQL, RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, code.getUserId());
            preparedStatement.setString(2, code.getPath());
            preparedStatement.setString(3, code.getTitle());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Code> findCodesByUserId(int userId) {
        List<Code> codes = new ArrayList<>();
        try (Connection connection = DBConnectorManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_USER_ID_SQL)) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Code code = Code
                        .builder()
                        .id(resultSet.getInt("id"))
                        .path(resultSet.getString("path"))
                        .title(resultSet.getString("title"))
                        .build();
                codes.add(code);
            }
            return codes;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteCodeById(int codeId) {
        try (Connection connection = DBConnectorManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BY_CODE_ID)) {
            preparedStatement.setInt(1, codeId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
