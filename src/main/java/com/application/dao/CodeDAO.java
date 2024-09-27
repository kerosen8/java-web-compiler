package com.application.dao;

import com.application.entity.Code;
import com.application.util.DBConnectorManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Statement.*;

public class CodeDAO {

    private final String CREATE_SQL = "INSERT INTO code (user_id, path, title) VALUES (?, ?, ?)";
    private final String FIND_BY_USER_ID_SQL = "SELECT * FROM code WHERE user_id = ?";

    public Code create(Code favorite) {
        try (Connection connection = DBConnectorManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(CREATE_SQL, RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, favorite.getUserId());
            preparedStatement.setString(2, favorite.getPath());
            preparedStatement.setString(3, favorite.getTitle());
            preparedStatement.executeUpdate();
            ResultSet rs = preparedStatement.getGeneratedKeys();
            rs.next();
            favorite.setId(rs.getInt(1));
            return favorite;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Code> findByUserId(int userId) {
        List<Code> favorites = new ArrayList<>();
        try (Connection connection = DBConnectorManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_USER_ID_SQL)) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Code favorite = Code
                        .builder()
                        .path(resultSet.getString("path"))
                        .title(resultSet.getString("title"))
                        .build();
                favorites.add(favorite);
            }
            return favorites;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
