package com.water.model;

public enum SampleStatus {
    REGISTERED("Зареєстровано"),
    IN_ANALYSIS("В дослідженні"),
    VERIFIED("Перевірено"),
    ARCHIVED("Архівовано");

    private final String ukrainianName;

    SampleStatus(String ukrainianName) {
        this.ukrainianName = ukrainianName;
    }

    public String getUkrainianName() {
        return ukrainianName;
    }
}
