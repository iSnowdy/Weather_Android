package com.example.weebther.UI.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.weebther.Database.Local.Entity.City;
import com.example.weebther.Database.Remote.RemoteModels.WeatherResponse;
import com.example.weebther.R;
import com.example.weebther.UI.ViewModels.WeatherViewModel;

/**
 * Fragment displaying detailed weather information for a selected city.
 */
public class WeatherDetailsFragment extends Fragment {
    private WeatherViewModel weatherViewModel;
    private TextView cityName, temperature, minTemperature, maxTemperature, description;
    private City city;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_weather_details, container, false);
        setupUI(view);

        weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);

        // Retrieve city information from the bundle
        if (getArguments() != null) {
            city = (City) getArguments().getSerializable("city");

            if (city != null) {
                cityName.setText(city.getName());
                observeWeather();
            } else {
                Toast.makeText(requireContext(), "Error: City data not found", Toast.LENGTH_SHORT).show();
            }
        }
        return view;
    }

    /**
     * Initializes UI components.
     */
    private void setupUI(View view) {
        cityName = view.findViewById(R.id.cityName);
        temperature = view.findViewById(R.id.temperature);
        minTemperature = view.findViewById(R.id.minTemperature);
        maxTemperature = view.findViewById(R.id.maxTemperature);
        description = view.findViewById(R.id.description);
    }

    /**
     * Observes weather data for the selected city and updates the UI.
     */
    private void observeWeather() {
        weatherViewModel.getWeatherLiveData().observe(getViewLifecycleOwner(), weatherResponse -> {
            if (weatherResponse != null) {
                updateWeatherUI(weatherResponse);
            } else {
                Toast.makeText(requireContext(), "No weather data available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Updates the UI with weather information.
     *
     * @param weatherResponse The weather data retrieved from the API.
     */
    private void updateWeatherUI(WeatherResponse weatherResponse) {
        if (weatherResponse.current != null) {
            temperature.setText(String.format("%.1f°C", weatherResponse.current.temperature));
            description.setText(weatherResponse.current.weatherConditions.get(0).description);
        }

        if (weatherResponse.daily != null && !weatherResponse.daily.isEmpty()) {
            minTemperature.setText(String.format("%.1f°C", weatherResponse.daily.get(0).temperature.min));
            maxTemperature.setText(String.format("%.1f°C", weatherResponse.daily.get(0).temperature.max));
        }
    }
}
