package com.water.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

public class CsvMeasurementImporter {
    private static final Logger LOG = Logger.getLogger(CsvMeasurementImporter.class.getName());
    private static final Path DEFAULT_CSV_PATH = Path.of("sample-data", "measurements.csv");

    private final WaterHttpClient client;

    public CsvMeasurementImporter(WaterHttpClient client) {
        this.client = client;
    }

    public int importDefaultCsv() throws IOException {
        return importFromFile(DEFAULT_CSV_PATH);
    }

    public int importFromFile(Path csvPath) throws IOException {
        if (!Files.exists(csvPath)) {
            throw new IOException("Файл не знайдено за відносним шляхом: " + csvPath);
        }

        List<String> lines = Files.readAllLines(csvPath, StandardCharsets.UTF_8);
        int imported = 0;

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split(";");
            if (parts.length != 5) {
                LOG.warning("Пропущено рядок " + (i + 1) + ": невірна кількість полів");
                continue;
            }

            try {
                int sampleId = Integer.parseInt(parts[0].trim());
                double param1 = Double.parseDouble(parts[1].trim());
                double param2 = Double.parseDouble(parts[2].trim());
                double param3 = Double.parseDouble(parts[3].trim());
                double wqi = Double.parseDouble(parts[4].trim());

                Measurement measurement = new Measurement(0, sampleId, param1, param2, param3, wqi);
                client.addMeasurement(measurement);
                imported++;
            } catch (Exception ex) {
                LOG.warning("Помилка у рядку " + (i + 1) + ": " + ex.getMessage());
            }
        }

        return imported;
    }
}