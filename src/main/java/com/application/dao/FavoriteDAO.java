package com.application.dao;

import com.application.entity.Favorite;
import com.application.util.DBConnectorManager;

import java.lang.ref.ReferenceQueue;
import java.sql.*;

import static java.sql.Statement.*;

public class FavoriteDAO {

    private final String CREATE_SQL = "INSERT INTO favorite (user_id, path, title) VALUES (?, ?, ?)";
    private final String FIND_BY_USER_ID_SQL = "SELECT * FROM favorite WHERE user_id = ?";

    public Favorite create(Favorite favorite) {
        try (Connection connection = DBConnectorManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(CREATE_SQL, RETURN_GENERATED_KEYS)) {
            ResultSet generatedKeys = preparedStatement.executeQuery();
            favorite.setId(generatedKeys.getInt(1));
            return favorite;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
