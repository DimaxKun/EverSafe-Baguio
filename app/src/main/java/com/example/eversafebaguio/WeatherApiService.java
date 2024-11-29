package com.example.eversafebaguio;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApiService {

    // Current weather
    @GET("weather")
    Call<WeatherResponse> getWeather(
            @Query("q") String cityName,
            @Query("appid") String apiKey
    );

    // 5-day weather forecast
    @GET("forecast")
    Call<ForecastResponse> getWeatherForecast(
            @Query("q") String cityName,
            @Query("appid") String apiKey
    );
}
