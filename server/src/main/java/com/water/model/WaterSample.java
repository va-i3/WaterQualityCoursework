package com.water.model;

import java.time.LocalDateTime;

public class WaterSample {
    private int id;
    private int observationPointId;
    private LocalDateTime samplingDate;
    private String status;

    public WaterSample(int id, int observationPointId, LocalDateTime samplingDate, String status) {
        this.id = id;
        this.observationPointId = observationPointId;
        this.samplingDate = samplingDate;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getObservationPointId() { return observationPointId; }
    public void setObservationPointId(int observationPointId) { this.observationPointId = observationPointId; }

    public LocalDateTime getSamplingDate() { return samplingDate; }
    public void setSamplingDate(LocalDateTime samplingDate) { this.samplingDate = samplingDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "WaterSample{id=" + id + ", status='" + status + "'}";
    }
}