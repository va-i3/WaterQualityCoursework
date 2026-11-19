package com.water.service;

import com.water.dao.MeasurementDao;
import com.water.model.Measurement;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class MeasurementService {
    private static final Logger logger = Logger.getLogger(MeasurementService.class.getName());
    private final MeasurementDao measurementDao;

    public MeasurementService() {
        this.measurementDao = new MeasurementDao();
    }

    // Розрахунок інтегрального показника: I = 0.40*F1 + 0.35*F2 + 0.25*F3
    public double calculateIntegralIndex(double p1, double p2, double p3) {
        validateFactor(p1, "Parameter 1");
        validateFactor(p2, "Parameter 2");
        validateFactor(p3, "Parameter 3");

        double index = 0.40 * p1 + 0.35 * p2 + 0.25 * p3;
        return Math.round(index * 100.0) / 100.0;
    }

    // Валідація нормованого діапазону 0-100
    private void validateFactor(double value, String factorName) {
        if (value < 0.0 || value > 100.0) {
            String errorMsg = "Помилка валідації: значення " + factorName +
                    " (" + value + ") виходить за межі допустимого діапазону [0..100].";
            logger.warning(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
    }

    private void validateMeasurementForSave(Measurement measurement) {
        if (measurement == null) {
            throw new IllegalArgumentException("Об'єкт вимірювання не може бути null.");
        }
        if (measurement.getWaterSampleId() <= 0) {
            throw new IllegalArgumentException("Некоректний ID проби води для вимірювання.");
        }
        double calculatedIndex = calculateIntegralIndex(
                measurement.getParameter1(),
                measurement.getParameter2(),
                measurement.getParameter3()
        );
        measurement.setWaterQualityIndex(calculatedIndex);
    }

    // Збереження вимірювання з автоматичним обчисленням індексу
    public void addMeasurement(Measurement measurement) throws SQLException {
        validateMeasurementForSave(measurement);
        measurementDao.save(measurement);
        logger.info("Успішно додано вимірювання для проби ID=" + measurement.getWaterSampleId() +
                ". Інтегральний показник I = " + measurement.getWaterQualityIndex());
    }

    // Оновлення вимірювання з перерахунком індексу
    public void updateMeasurement(Measurement measurement) throws SQLException {
        validateMeasurementForSave(measurement);
        if (measurement.getId() <= 0) {
            throw new IllegalArgumentException("Некоректний ID вимірювання для оновлення.");
        }
        measurementDao.update(measurement);
        logger.info("Успішно оновлено вимірювання ID=" + measurement.getId() +
                ". Інтегральний показник I = " + measurement.getWaterQualityIndex());
    }

    // Видалення вимірювання за ID
    public void deleteMeasurement(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Некоректний ID вимірювання для видалення.");
        }
        measurementDao.delete(id);
        logger.info("Успішно видалено вимірювання ID=" + id);
    }

    public List<Measurement> getAllMeasurements() throws SQLException {
        return measurementDao.findAll();
    }
}