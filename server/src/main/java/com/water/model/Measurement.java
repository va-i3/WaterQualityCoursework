package com.water.model;

public class Measurement {
    private int id;
    private int waterSampleId;
    private double parameter1; // наприклад, вміст кисню / забруднювач F1
    private double parameter2; // F2
    private double parameter3; // F3
    private double waterQualityIndex; // Загальний індекс якості (наприклад, I = 0.40*F1 + 0.35*F2 + 0.25*F3)

    public Measurement(int id, int waterSampleId, double parameter1, double parameter2, double parameter3, double waterQualityIndex) {
        this.id = id;
        this.waterSampleId = waterSampleId;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
        this.parameter3 = parameter3;
        this.waterQualityIndex = waterQualityIndex;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getWaterSampleId() { return waterSampleId; }
    public void setWaterSampleId(int waterSampleId) { this.waterSampleId = waterSampleId; }

    public double getParameter1() { return parameter1; }
    public void setParameter1(double parameter1) { this.parameter1 = parameter1; }

    public double getParameter2() { return parameter2; }
    public void setParameter2(double parameter2) { this.parameter2 = parameter2; }

    public double getParameter3() { return parameter3; }
    public void setParameter3(double parameter3) { this.parameter3 = parameter3; }

    public double getWaterQualityIndex() { return waterQualityIndex; }
    public void setWaterQualityIndex(double waterQualityIndex) { this.waterQualityIndex = waterQualityIndex; }

    @Override
    public String toString() {
        return "Measurement{id=" + id + ", index=" + waterQualityIndex + "}";
    }
}