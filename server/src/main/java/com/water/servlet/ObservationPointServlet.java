package com.water.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.water.dao.ObservationPointDao;
import com.water.model.ObservationPoint;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ObservationPointServlet extends HttpServlet {
    private final ObservationPointDao pointDao = new ObservationPointDao();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        try {
            List<ObservationPoint> points = pointDao.findAll();
            objectMapper.writeValue(resp.getWriter(), points);
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), Map.of("error", "Помилка бази даних: " + e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");
        try {
            ObservationPoint point = objectMapper.readValue(req.getInputStream(), ObservationPoint.class);
            if (point == null || point.getName() == null || point.getName().isBlank()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getWriter(), Map.of("error", "Поле 'name' є обов'язковим"));
                return;
            }

            pointDao.save(point);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(resp.getWriter(), point);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), Map.of("error", "Некоректний JSON: " + e.getOriginalMessage()));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), Map.of("error", "Помилка бази даних: " + e.getMessage()));
        }
    }
}