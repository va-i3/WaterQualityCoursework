package com.water.model;

public class ObservationPoint {
    private int id;
    private String name;
    private String locationDescription;
    private String coordinates;

    public ObservationPoint(int id, String name, String locationDescription, String coordinates) {
        this.id = id;
        this.name = name;
        this.locationDescription = locationDescription;
        this.coordinates = coordinates;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocationDescription() { return locationDescription; }
    public void setLocationDescription(String locationDescription) { this.locationDescription = locationDescription; }

    public String getCoordinates() { return coordinates; }
    public void setCoordinates(String coordinates) { this.coordinates = coordinates; }

    @Override
    public String toString() {
        return "ObservationPoint{id=" + id + ", name='" + name + "'}";
    }
}
