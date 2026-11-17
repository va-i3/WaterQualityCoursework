package org.example;

public class Main {
    public static void main(String[] args) {
        try {
            var connection = com.water.dao.DatabaseConnection.getConnection();
            if (connection != null && !connection.isClosed()) {
                System.out.println("Успішне підключення до бази даних water_monitoring_db!");
                connection.close();
            }
        } catch (Exception e) {
            System.err.println("Помилка підключення до бази даних:");
            e.printStackTrace();
        }
    }
}