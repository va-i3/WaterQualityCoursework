package org.example;

import com.water.dao.ObservationPointDao;
import com.water.dao.WaterSampleDao;
import com.water.dao.MeasurementDao;
import com.water.model.ObservationPoint;
import com.water.model.WaterSample;
import com.water.model.Measurement;
import com.water.model.SampleStatus;
import com.water.service.MeasurementService;
import com.water.service.WaterSampleService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== ТЕСТУВАННЯ СИСТЕМИ МОНІТОРИНГУ ЯКОСТІ ВОДИ ===");

        ObservationPointDao pointDao = new ObservationPointDao();
        WaterSampleDao sampleDao = new WaterSampleDao();
        MeasurementDao measurementDao = new MeasurementDao();

        try {
            // 1. Створюємо новий тестовий пункт спостереження
            ObservationPoint newPoint = new ObservationPoint(
                    0,
                    "Станція №1 (Центр)",
                    "Поблизу центрального мосту",
                    "50.4501, 30.5234"
            );

            pointDao.save(newPoint);
            System.out.println("Успішно збережено пункт з ID: " + newPoint.getId());

            // 2. Створюємо пробу води (використовуємо фіксований station_id = 1, який точно є в базі)
            WaterSample newSample = new WaterSample(
                    0,
                    1, // Використовуємо існуючий ID станції
                    LocalDateTime.now(),
                    "ЗАРЕЄСТРОВАНО"
            );
            sampleDao.save(newSample);
            System.out.println("Успішно збережено пробу води з ID: " + newSample.getId());

            // 3. Отримуємо список усіх пунктів з бази
            List<ObservationPoint> points = pointDao.findAll();
            System.out.println("Усі пункти спостереження в базі:");
            for (ObservationPoint p : points) {
                System.out.println(" -> " + p);
            }

            // 4. Тестування сервісу вимірювань та розрахунку індексу (I = 0.40*P1 + 0.35*P2 + 0.25*P3)
            MeasurementService measurementService = new MeasurementService();
            Measurement measurement = new Measurement(0, newSample.getId(), 80.0, 90.0, 70.0, 0.0);

            measurementService.addMeasurement(measurement);
            measurementDao.save(measurement);
            System.out.println("Розрахований інтегральний показник води та збережено в БД: " + measurement.getWaterQualityIndex());

            // 5. Тестування життєвого циклу проби води
            WaterSampleService sampleService = new WaterSampleService();
            SampleStatus currentStatus = SampleStatus.REGISTERED;
            System.out.println("Початковий статус проби: " + currentStatus.getUkrainianName());

            // Перехід у статус "В дослідженні"
            currentStatus = sampleService.changeSampleStatus(currentStatus, SampleStatus.IN_ANALYSIS);
            System.out.println("Новий статус проби: " + currentStatus.getUkrainianName());

        } catch (SQLException e) {
            System.err.println("Помилка при роботі з базою даних:");
            e.printStackTrace();
        }
    }
}