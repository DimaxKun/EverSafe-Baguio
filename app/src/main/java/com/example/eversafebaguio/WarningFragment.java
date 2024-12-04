package com.example.eversafebaguio;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WarningFragment extends Fragment {

    private MediaPlayer mediaPlayer;

    // Declare TextViews for current weather and forecast
    private TextView weatherConditionTextView, tempTextView, descriptionTextView;
    private TextView forecastDay1TextView, forecastDay2TextView, forecastDay3TextView, forecastDay4TextView;
    private TextView day1DescriptionTextView, day2DescriptionTextView, day3DescriptionTextView, day4DescriptionTextView; // Added TextViews for forecast descriptions
    private ImageView weatherIconImageView, riskIconImageView, riskIconImageView1, riskIconImageView2, riskIconImageView3, riskIconImageView4;
    private ImageView weatherIconImageViewDay1, weatherIconImageViewDay2, weatherIconImageViewDay3, weatherIconImageViewDay4; // Added ImageViews for forecast icons

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_warning, container, false);

        // Handle back press for this fragment
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        // Show confirmation dialog
                        new AlertDialog.Builder(requireContext())
                                .setMessage("Are you sure you want to exit?")
                                .setCancelable(false) // Prevent closing dialog when touched outside
                                .setPositiveButton("Yes", (dialog, id) -> {
                                    // Exit the app
                                    requireActivity().finish(); // Close the activity if confirmed
                                })
                                .setNegativeButton("No", null) // Do nothing when "No" is clicked
                                .show();
                    }
                });

        ImageView userIcon = view.findViewById(R.id.icon_right);
        userIcon.setOnClickListener(v ->
                Toast.makeText(requireContext(), "User Profile", Toast.LENGTH_SHORT).show()
        );

        // Initialize TextViews for weather data and forecast
        weatherConditionTextView = view.findViewById(R.id.weatherConditionTextView);
        tempTextView = view.findViewById(R.id.tempTextView);
        descriptionTextView = view.findViewById(R.id.descriptionTextView);
        forecastDay1TextView = view.findViewById(R.id.forecastDay1TextView);
        forecastDay2TextView = view.findViewById(R.id.forecastDay2TextView);
        forecastDay3TextView = view.findViewById(R.id.forecastDay3TextView);
        forecastDay4TextView = view.findViewById(R.id.forecastDay4TextView);

        // Initialize TextViews for forecast descriptions
        day1DescriptionTextView = view.findViewById(R.id.day1DescriptionTextView);
        day2DescriptionTextView = view.findViewById(R.id.day2DescriptionTextView);
        day3DescriptionTextView = view.findViewById(R.id.day3DescriptionTextView);
        day4DescriptionTextView = view.findViewById(R.id.day4DescriptionTextView);

        // Initialize ImageViews for weather and risk icons
        weatherIconImageView = view.findViewById(R.id.weatherIconImageView);
        riskIconImageView = view.findViewById(R.id.riskIconImageView);
        riskIconImageView1 = view.findViewById(R.id.riskIconImageView1);
        riskIconImageView2 = view.findViewById(R.id.riskIconImageView2);
        riskIconImageView3 = view.findViewById(R.id.riskIconImageView3);
        riskIconImageView4 = view.findViewById(R.id.riskIconImageView4);
        weatherIconImageViewDay1 = view.findViewById(R.id.weatherIconImageViewDay1);
        weatherIconImageViewDay2 = view.findViewById(R.id.weatherIconImageViewDay2);
        weatherIconImageViewDay3 = view.findViewById(R.id.weatherIconImageViewDay3);
        weatherIconImageViewDay4 = view.findViewById(R.id.weatherIconImageViewDay4);

        // Fetch current weather and forecast data
        fetchWeatherData();
        fetchWeatherForecast();

        // Emergency button setup
//        Button emergencyButton = view.findViewById(R.id.emergencyButton);
//        emergencyButton.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), CallActivity.class);
//            startActivity(intent);
//        });

        return view;
    }

    private void fetchWeatherData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherApiService apiService = retrofit.create(WeatherApiService.class);
        String apiKey = "49e123927b724c4879e8ad72140bcbb6";
        Call<WeatherResponse> call = apiService.getWeather("Baguio City", apiKey);

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, retrofit2.Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    WeatherResponse weatherResponse = response.body();
                    if (weatherResponse != null) {
                        double tempInCelsius = weatherResponse.getMain().getTemp() - 273.15;
                        tempTextView.setText(String.format("%.2f °C", tempInCelsius));

                        String weatherCondition = categorizeWeatherCondition(weatherResponse.getWeather().get(0).getDescription());
                        weatherConditionTextView.setText(weatherCondition);
                        setWeatherDescription(weatherCondition);
                        updateWeatherIcon(weatherCondition);
                        updateRiskIcon(weatherCondition);
                    }
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.e("Weather", "API call failed", t);
            }
        });
    }

    private void fetchWeatherForecast() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherApiService apiService = retrofit.create(WeatherApiService.class);
        String apiKey = "49e123927b724c4879e8ad72140bcbb6";
        Call<ForecastResponse> call = apiService.getWeatherForecast("Baguio City", apiKey);

        call.enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, retrofit2.Response<ForecastResponse> response) {
                if (response.isSuccessful()) {
                    ForecastResponse forecastResponse = response.body();
                    if (forecastResponse != null) {
                        for (int i = 0; i < 4; i++) {
                            ForecastItem item = forecastResponse.getList().get(i);

                            // Create interval labels dynamically
                            String intervalLabel = "Next " + (3 * (i + 1)) + " Hours";

                            String weatherCondition = categorizeWeatherCondition(item.getWeather().get(0).getDescription());
                            double tempInCelsius = item.getMain().getTemp() - 273.15;

                            String forecastText = String.format("%s, %.2f°C", weatherCondition, tempInCelsius);

                            switch (i) {
                                case 0:
                                    forecastDay1TextView.setText(intervalLabel + ": " + forecastText);
                                    day1DescriptionTextView.setText(getWeatherDescription(weatherCondition));
                                    break;
                                case 1:
                                    forecastDay2TextView.setText(intervalLabel + ": " + forecastText);
                                    day2DescriptionTextView.setText(getWeatherDescription(weatherCondition));
                                    break;
                                case 2:
                                    forecastDay3TextView.setText(intervalLabel + ": " + forecastText);
                                    day3DescriptionTextView.setText(getWeatherDescription(weatherCondition));
                                    break;
                                case 3:
                                    forecastDay4TextView.setText(intervalLabel + ": " + forecastText);
                                    day4DescriptionTextView.setText(getWeatherDescription(weatherCondition));
                                    break;
                            }

                            updateForecastWeatherIcon(forecastDay1TextView, weatherIconImageViewDay1);
                            updateForecastWeatherIcon(forecastDay2TextView, weatherIconImageViewDay2);
                            updateForecastWeatherIcon(forecastDay3TextView, weatherIconImageViewDay3);
                            updateForecastWeatherIcon(forecastDay4TextView, weatherIconImageViewDay4);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {
                Log.e("Weather", "API call failed", t);
            }
        });
    }

    private String categorizeWeatherCondition(String description) {
        if (description.contains("clear")) {
            return "Sunny";
        } else if (description.contains("cloud")) {
            return "Cloudy";
        } else if (description.contains("rain")) {
            return "Rainy";
        } else if (description.contains("storm") || description.contains("thunderstorm")) {
            return "Stormy";
        } else if (description.contains("wind")) {
            return "Windy";
        } else {
            return "N/A";
        }
    }

    private String getWeatherDescription(String weatherCondition) {
        switch (weatherCondition) {
            case "Sunny":
                return "It's a sunny day! Stay hydrated and wear sunscreen.";
            case "Cloudy":
                return "It's cloudy, so it's a bit cooler.";
            case "Rainy":
                return "Expect rain. Don't forget your umbrella.";
            case "Stormy":
                return "Warning: Stormy weather ahead. Stay indoors.";
            case "Windy":
                return "Strong winds are expected. Be cautious of flying debris.";
            default:
                return "Weather data unavailable.";
        }
    }

    private void setWeatherDescription(String weatherCondition) {
        descriptionTextView.setText(getWeatherDescription(weatherCondition));
    }

    private void updateWeatherIcon(String weatherCondition) {
        switch (weatherCondition) {
            case "Sunny":
                weatherIconImageView.setImageResource(R.drawable.sunnyskies);
                break;
            case "Cloudy":
                weatherIconImageView.setImageResource(R.drawable.cloudyskies);
                break;
            case "Rainy":
                weatherIconImageView.setImageResource(R.drawable.rainyskies);
                break;
            case "Stormy":
                weatherIconImageView.setImageResource(R.drawable.stormyskies);
                break;
            case "Windy":
                weatherIconImageView.setImageResource(R.drawable.windyskies);
                break;
            default:
                weatherIconImageView.setImageResource(R.drawable.sunnyskies);
                break;
        }
    }

    private void updateRiskIcon(String weatherCondition) {
        switch (weatherCondition) {
            case "Sunny":
            case "Cloudy":
            case "Rainy":
            case "Windy":
                riskIconImageView.setImageResource(R.drawable.green_circle);
                riskIconImageView1.setImageResource(R.drawable.green_circle);
                riskIconImageView2.setImageResource(R.drawable.green_circle);
                riskIconImageView3.setImageResource(R.drawable.green_circle);
                riskIconImageView4.setImageResource(R.drawable.green_circle);
                break;
            case "Stormy":
                riskIconImageView.setImageResource(R.drawable.red_circle);
                riskIconImageView1.setImageResource(R.drawable.red_circle);
                riskIconImageView2.setImageResource(R.drawable.red_circle);
                riskIconImageView3.setImageResource(R.drawable.red_circle);
                riskIconImageView4.setImageResource(R.drawable.red_circle);
                break;
            default:
                riskIconImageView.setImageResource(R.drawable.green_circle);
                riskIconImageView1.setImageResource(R.drawable.green_circle);
                riskIconImageView2.setImageResource(R.drawable.green_circle);
                riskIconImageView3.setImageResource(R.drawable.green_circle);
                riskIconImageView4.setImageResource(R.drawable.green_circle);
                break;
        }
    }

    private void updateForecastWeatherIcon(TextView textView, ImageView imageView) {
        String forecastText = textView.getText().toString();
        if (forecastText.contains("Sunny")) {
            imageView.setImageResource(R.drawable.sunnyskies);
        } else if (forecastText.contains("Cloudy")) {
            imageView.setImageResource(R.drawable.cloudyskies);
        } else if (forecastText.contains("Rainy")) {
            imageView.setImageResource(R.drawable.rainyskies);
        } else if (forecastText.contains("Stormy")) {
            imageView.setImageResource(R.drawable.stormyskies);
        } else if (forecastText.contains("Windy")) {
            imageView.setImageResource(R.drawable.windyskies);
        } else {
            imageView.setImageResource(R.drawable.sunnyskies);
        }
    }
}
