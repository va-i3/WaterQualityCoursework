package com.water.dao;

import com.water.model.Measurement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MeasurementDao {

    // Створення (INSERT) нового вимірювання
    public void save(Measurement measurement) throws SQLException {
        String sql = "INSERT INTO measurements (sample_id, parameter_name, measured_value, norm_limit, is_critical) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, measurement.getWaterSampleId());
            pstmt.setString(2, "Загальний показник");
            pstmt.setDouble(3, measurement.getParameter1());
            pstmt.setDouble(4, 100.0);
            pstmt.setInt(5, 0);

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    measurement.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    // Отримання всіх вимірювань (SELECT ALL)
    public List<Measurement> findAll() throws SQLException {
        List<Measurement> measurements = new ArrayList<>();
        String sql = "SELECT id, sample_id, measured_value FROM measurements";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Measurement measurement = new Measurement(
                        rs.getInt("id"),
                        rs.getInt("sample_id"),
                        rs.getDouble("measured_value"),
                        0.0,
                        0.0,
                        0.0
                );
                measurements.add(measurement);
            }
        }
        return measurements;
    }

    // Оновлення (UPDATE) існуючого вимірювання за ID
    public void update(Measurement measurement) throws SQLException {
        String sql = "UPDATE measurements SET sample_id = ?, measured_value = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, measurement.getWaterSampleId());
            pstmt.setDouble(2, measurement.getParameter1());
            pstmt.setInt(3, measurement.getId());

            int updatedRows = pstmt.executeUpdate();
            if (updatedRows == 0) {
                throw new SQLException("Вимірювання з ID=" + measurement.getId() + " не знайдено для оновлення.");
            }
        }
    }

    // Видалення (DELETE) вимірювання за ID
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM measurements WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int deletedRows = pstmt.executeUpdate();
            if (deletedRows == 0) {
                throw new SQLException("Вимірювання з ID=" + id + " не знайдено для видалення.");
            }
        }
    }
}