package com.water.client;

import javafx.concurrent.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

public class CsvMeasurementImporter {

    private static final Logger LOG = Logger.getLogger(CsvMeasurementImporter.class.getName());
    public static final Path DEFAULT_CSV_PATH = Path.of("sample-data", "measurements.csv");
    private static final String EXPECTED_HEADER = "sampleId;param1;param2;param3;wqi";

    private final WaterHttpClient client;

    public CsvMeasurementImporter(WaterHttpClient client) {
        this.client = client;
    }

    public Task<Integer> createImportTask() {
        return createImportTask(DEFAULT_CSV_PATH);
    }

    public Task<Integer> createImportTask(Path csvPath) {
        return new Task<>() {
            @Override
            protected Integer call() throws Exception {
                LOG.info("Початок фонового CSV-імпорту з файлу: " + csvPath);
                updateMessage("Читання CSV-файлу...");
                updateProgress(0, 1);

                if (csvPath == null || !Files.exists(csvPath)) {
                    String msg = "CSV-файл не знайдено за відносним шляхом: " + csvPath;
                    LOG.warning(msg);
                    throw new IOException(msg);
                }

                List<String> lines = Files.readAllLines(csvPath, StandardCharsets.UTF_8);
                if (lines.isEmpty()) {
                    String msg = "CSV-файл порожній: " + csvPath;
                    LOG.warning(msg);
                    throw new IOException(msg);
                }

                String actualHeader = lines.get(0).replace("\uFEFF", "").trim();
                if (!EXPECTED_HEADER.equalsIgnoreCase(actualHeader)) {
                    String msg = "Невірний заголовок CSV. Очікувалося: '" + EXPECTED_HEADER
                            + "', отримано: '" + actualHeader + "'";
                    LOG.warning(msg);
                    throw new IOException(msg);
                }

                int totalDataLines = Math.max(0, lines.size() - 1);
                if (totalDataLines == 0) {
                    LOG.info("У CSV-файлі немає рядків даних для імпорту.");
                    updateProgress(1, 1);
                    updateMessage("Немає рядків даних для імпорту");
                    return 0;
                }

                int imported = 0;
                int skipped = 0;

                for (int i = 1; i < lines.size(); i++) {
                    if (isCancelled()) {
                        LOG.info("CSV-імпорт скасовано користувачем.");
                        break;
                    }

                    String rawLine = lines.get(i);
                    String line = rawLine == null ? "" : rawLine.trim();
                    int lineNumber = i + 1;

                    if (line.isEmpty()) {
                        skipped++;
                        LOG.warning("Пропущено порожній рядок " + lineNumber);
                        updateProgress(i, totalDataLines);
                        updateMessage("Оброблено рядків: " + i + " з " + totalDataLines + " (успішно: " + imported + ")");
                        continue;
                    }

                    String[] parts = line.split(";", -1);
                    if (parts.length != 5) {
                        skipped++;
                        LOG.warning("Пропущено рядок " + lineNumber + ": очікується 5 полів, отримано " + parts.length);
                        updateProgress(i, totalDataLines);
                        updateMessage("Оброблено рядків: " + i + " з " + totalDataLines + " (успішно: " + imported + ")");
                        continue;
                    }

                    try {
                        int sampleId = Integer.parseInt(parts[0].trim());
                        double p1 = Double.parseDouble(parts[1].trim());
                        double p2 = Double.parseDouble(parts[2].trim());
                        double p3 = Double.parseDouble(parts[3].trim());
                        double wqi = Double.parseDouble(parts[4].trim());

                        if (!Double.isFinite(p1) || !Double.isFinite(p2) || !Double.isFinite(p3) || !Double.isFinite(wqi)) {
                            throw new IllegalArgumentException("Нескінченне або NaN числове значення");
                        }

                        Measurement measurement = new Measurement(0, sampleId, p1, p2, p3, wqi);
                        client.addMeasurement(measurement);
                        imported++;
                    } catch (NumberFormatException ex) {
                        skipped++;
                        LOG.warning("Пропущено рядок " + lineNumber + ": некоректний формат числа (" + ex.getMessage() + ")");
                    } catch (Exception ex) {
                        skipped++;
                        LOG.warning("Помилка під час імпорту рядка " + lineNumber + ": " + ex.getMessage());
                    }

                    updateProgress(i, totalDataLines);
                    updateMessage("Імпортовано: " + imported + " (рядків оброблено: " + i + "/" + totalDataLines + ")");
                }

                LOG.info("Завершено CSV-імпорт. Успішно додано: " + imported + ", пропущено рядків: " + skipped);
                return imported;
            }
        };
    }
}