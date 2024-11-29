package com.example.eversafebaguio;

import java.util.List;

public class WeatherResponse {

    private String name;  // City name
    private Main main;    // Main weather data (humidity, pressure, etc.)
    private List<Weather> weather;  // List of weather information (description, etc.)

    // Getters for name, main, and weather
    public String getName() {
        return name;
    }

    public Main getMain() {
        return main;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    // Nested class for Main weather data
    public static class Main {
        private double temp;
        private float humidity;
        private int pressure;

        public double getTemp() {
            return temp;
        }

        public float getHumidity() {
            return humidity;
        }

        public int getPressure() {
            return pressure;
        }
    }

    // Nested class for Weather data
    public static class Weather {
        private String description;

        public String getDescription() {
            return description;
        }
    }
}

