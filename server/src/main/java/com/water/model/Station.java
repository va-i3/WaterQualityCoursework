package com.water.model;

public class Station {
    private int id;
    private String name;
    private String location;

    public Station(int id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    // Гетери та сетери
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}