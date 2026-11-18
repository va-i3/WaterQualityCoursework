package com.water.dao;

import com.water.model.WaterSample;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class WaterSampleDao {

    // Створення (INSERT) нової проби води
    public void save(WaterSample sample) throws SQLException {
        // Використовуємо station_id замість observation_point_id
        String sql = "INSERT INTO samples (station_id, sampling_date, status) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, sample.getObservationPointId());
            pstmt.setTimestamp(2, Timestamp.valueOf(sample.getSamplingDate()));
            pstmt.setString(3, sample.getStatus());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    sample.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    // Отримання всіх проб води (SELECT ALL)
    public List<WaterSample> findAll() throws SQLException {
        List<WaterSample> samples = new ArrayList<>();
        String sql = "SELECT id, station_id, sampling_date, status FROM samples";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                int pointId = rs.getInt("station_id");
                LocalDateTime samplingDate = rs.getTimestamp("sampling_date").toLocalDateTime();
                String status = rs.getString("status");

                WaterSample sample = new WaterSample(id, pointId, samplingDate, status);
                samples.add(sample);
            }
        }
        return samples;
    }

    // Оновлення статусу проби ( UPDATE lifecycle status )
    public void updateStatus(int sampleId, String newStatus) throws SQLException {
        String sql = "UPDATE samples SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newStatus);
            pstmt.setInt(2, sampleId);
            pstmt.executeUpdate();
        }
    }
}