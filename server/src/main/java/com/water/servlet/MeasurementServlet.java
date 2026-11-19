package com.water.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.water.model.Measurement;
import com.water.service.MeasurementService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class MeasurementServlet extends HttpServlet {
    private final MeasurementService measurementService = new MeasurementService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        try {
            List<Measurement> measurements = measurementService.getAllMeasurements();
            objectMapper.writeValue(resp.getWriter(), measurements);
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), Map.of("error", "Помилка бази даних: " + e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");
        String action = req.getParameter("action");
        if (action == null || action.isBlank()) {
            action = "add";
        }

        try {
            switch (action) {
                case "add" -> {
                    Measurement measurement = objectMapper.readValue(req.getInputStream(), Measurement.class);
                    measurementService.addMeasurement(measurement);
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                    objectMapper.writeValue(resp.getWriter(), measurement);
                }
                case "update" -> {
                    Measurement measurement = objectMapper.readValue(req.getInputStream(), Measurement.class);
                    measurementService.updateMeasurement(measurement);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    objectMapper.writeValue(resp.getWriter(), measurement);
                }
                case "delete" -> {
                    int id = resolveIdForDelete(req);
                    measurementService.deleteMeasurement(id);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    objectMapper.writeValue(resp.getWriter(), Map.of("deletedId", id));
                }
                default -> {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    objectMapper.writeValue(resp.getWriter(), Map.of("error", "Невідома дія: " + action));
                }
            }
        } catch (IllegalArgumentException | JsonProcessingException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), Map.of("error", e.getMessage()));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), Map.of("error", "Помилка бази даних: " + e.getMessage()));
        }
    }

    private int resolveIdForDelete(HttpServletRequest req) throws IOException {
        String idParam = req.getParameter("id");
        if (idParam != null && !idParam.isBlank()) {
            return Integer.parseInt(idParam.trim());
        }
        JsonNode node = objectMapper.readTree(req.getInputStream());
        if (node != null && node.has("id") && node.get("id").canConvertToInt()) {
            return node.get("id").asInt();
        }
        throw new IllegalArgumentException("Не вказано коректний ID для видалення.");
    }
}