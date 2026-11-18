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
            // Використовуємо реальні поля з бази даних
            pstmt.setString(2, "Загальний показник"); // назва параметра за замовчуванням
            pstmt.setDouble(3, measurement.getParameter1()); // збережемо значення як measured_value
            pstmt.setDouble(4, 100.0); // нормативний ліміт
            pstmt.setInt(5, 0);       // чи критичне (0 - ні)

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
                        rs.getDouble("measured_value"), // передаємо виміряне значення
                        0.0,
                        0.0,
                        0.0
                );
                measurements.add(measurement);
            }
        }
        return measurements;
    }
}