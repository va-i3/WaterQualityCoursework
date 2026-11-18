package com.water.dao;

import com.water.model.ObservationPoint;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ObservationPointDao {

    // Створення (INSERT) пункту спостереження
    public void save(ObservationPoint point) throws SQLException {
        String sql = "INSERT INTO observation_points (name, location_description, coordinates) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, point.getName());
            pstmt.setString(2, point.getLocationDescription());
            pstmt.setString(3, point.getCoordinates());
            pstmt.executeUpdate();

            // Отримуємо згенерований ID
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    point.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    // Отримання всіх пунктів спостереження (SELECT ALL)
    public List<ObservationPoint> findAll() throws SQLException {
        List<ObservationPoint> points = new ArrayList<>();
        String sql = "SELECT id, name, location_description, coordinates FROM observation_points";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ObservationPoint point = new ObservationPoint(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("location_description"),
                        rs.getString("coordinates")
                );
                points.add(point);
            }
        }
        return points;
    }

    // Видалення за ID (DELETE)
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM observation_points WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
