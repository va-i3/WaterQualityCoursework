package com.water.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

public class WaterHttpClient {

    public static final String MEASUREMENTS_PATH = "/measurements";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public WaterHttpClient(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/")
                ? baseUrl.substring(0, baseUrl.length() - 1)
                : baseUrl;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public List<Measurement> getMeasurements() throws IOException, InterruptedException {
        return getMeasurements(MEASUREMENTS_PATH);
    }

    public List<Measurement> getMeasurements(String path) throws IOException, InterruptedException {
        String normalizedPath = path.startsWith("/") ? path : "/" + path;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + normalizedPath))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("HTTP помилка: " + response.statusCode() + " | Тіло: " + response.body());
        }

        return objectMapper.readValue(response.body(), new TypeReference<>() {});
    }

    public void addMeasurement(Measurement measurement) throws IOException, InterruptedException {
        String jsonBody = objectMapper.writeValueAsString(measurement);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + MEASUREMENTS_PATH))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("HTTP помилка: " + response.statusCode() + " | Тіло: " + response.body());
        }
    }

    public void updateMeasurement(Measurement measurement) throws IOException, InterruptedException {
        String jsonBody = objectMapper.writeValueAsString(measurement);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + MEASUREMENTS_PATH + "?action=update"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("HTTP помилка: " + response.statusCode() + " | Тіло: " + response.body());
        }
    }

    public void deleteMeasurement(int id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + MEASUREMENTS_PATH + "?action=delete&id=" + id))
                .timeout(Duration.ofSeconds(10))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("HTTP помилка: " + response.statusCode() + " | Тіло: " + response.body());
        }
    }
}