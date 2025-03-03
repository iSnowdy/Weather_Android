package com.example.weebther.UI.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.weebther.Database.Local.Entity.City;
import com.example.weebther.Database.Local.Entity.WeatherCurrentEntity;
import com.example.weebther.Database.Local.Entity.WeatherHourlyEntity;
import com.example.weebther.Database.Local.Entity.WeatherDailyEntity;
import com.example.weebther.R;
import com.example.weebther.UI.ViewModels.WeatherViewModel;

import java.util.ArrayList;
import java.util.List;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

/**
 * Fragment displaying detailed weather information for a selected city.
 */
public class WeatherDetailsFragment extends Fragment {
    private WeatherViewModel weatherViewModel;
    private TextView cityName, temperature, description;
    private TextView hourlyForecastTextView, dailyForecastTextView;
    private LineChart temperatureChart;
    private BarChart rainChart;
    private Button refreshButton;
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

        // Refresh weather when button is clicked
        refreshButton.setOnClickListener(v -> {
            if (city != null) {
                Toast.makeText(requireContext(), "Updating weather...", Toast.LENGTH_SHORT).show();
                weatherViewModel.refreshWeather(city.getName(), city.getLatitude(), city.getLongitude());
            }
        });

        return view;
    }

    /**
     * Initializes UI components.
     */
    private void setupUI(View view) {
        cityName = view.findViewById(R.id.cityName);
        temperature = view.findViewById(R.id.temperature);
        description = view.findViewById(R.id.description);
        hourlyForecastTextView = view.findViewById(R.id.hourlyForecast);
        dailyForecastTextView = view.findViewById(R.id.dailyForecast);
        temperatureChart = view.findViewById(R.id.temperatureChart);
        rainChart = view.findViewById(R.id.rainChart);
        refreshButton = view.findViewById(R.id.refreshButton);
    }

    /**
     * Observes weather data for the selected city and updates the UI.
     */
    private void observeWeather() {

        // Current weather
        weatherViewModel.getWeatherLiveData(city.getName()).observe(getViewLifecycleOwner(), weatherData -> {
            if (weatherData != null) {
                updateWeatherUI(weatherData);
            } else {
                Toast.makeText(requireContext(), "No weather data available", Toast.LENGTH_SHORT).show();
            }
        });

        // Hourly weather
        /*weatherViewModel.getHourlyForecast(city.getName()).observe(getViewLifecycleOwner(), hourlyData -> {
            if (hourlyData != null) {
                updateHourlyForecast(hourlyData);
            }
        });*/

        // Daily weather
        weatherViewModel.getDailyForecast(city.getName()).observe(getViewLifecycleOwner(), dailyData -> {
            if (dailyData != null) {
                updateDailyForecast(dailyData);
                setUpCharts(dailyData);
            }
        });
    }

    /**
     * Updates the UI with weather information.
     */
    private void updateWeatherUI(WeatherCurrentEntity weatherCurrentEntity) {
        if (weatherCurrentEntity != null) {
            temperature.setText(String.format("%.1f°C", weatherCurrentEntity.getTemperature()));
            description.setText(weatherCurrentEntity.getWeatherDescription());
        }
    }

    /**
     * Updates the hourly forecast TextView.
     */
    private void updateHourlyForecast(List<WeatherHourlyEntity> hourlyForecast) {
        StringBuilder hourlyText = new StringBuilder("Hourly Forecast:\n");
        for (WeatherHourlyEntity hourly : hourlyForecast) {
            hourlyText.append(String.format("%d:00 - %.1f°C, %s\n", hourly.getTimestamp(), hourly.getTemperature(), hourly.getWeatherDescription()));
        }
        hourlyForecastTextView.setText(hourlyText.toString());
    }

    /**
     * Updates the daily forecast TextView.
     */
    private void updateDailyForecast(List<WeatherDailyEntity> dailyForecast) {
        StringBuilder dailyText = new StringBuilder("Daily Forecast:\n");
        for (WeatherDailyEntity daily : dailyForecast) {
            dailyText.append(String.format("Day %d - Min: %.1f°C, Max: %.1f°C, %s\n",
                    daily.getTimestamp(), daily.getTempMin(), daily.getTempMax(), daily.getWeatherDescription()));
        }
        dailyForecastTextView.setText(dailyText.toString());
    }

    private void setUpCharts(List<WeatherDailyEntity> dailyForecast) {
        setUpTemperatureChart(dailyForecast);
        setUpRainChart(dailyForecast);
    }

    private void setUpTemperatureChart(List<WeatherDailyEntity> dailyForecast) {
        List<Entry> minTempEntries = new ArrayList<>();
        List<Entry> maxTempEntries = new ArrayList<>();

        // Adds data to the entries lists from the daily forecast entity
        for (int i = 0; i < dailyForecast.size(); i++) {
            minTempEntries.add(new Entry(i, dailyForecast.get(i).getTempMin()));
            maxTempEntries.add(new Entry(i, dailyForecast.get(i).getTempMax()));
        }

        LineDataSet minTempDataSet = new LineDataSet(minTempEntries, "Min Temperature");
        LineDataSet maxTempDataSet = new LineDataSet(maxTempEntries, "Max Temperature");
        minTempDataSet.setColor(0xFF03A9F4); // Blue
        maxTempDataSet.setColor(0xFFFF5722); // Orange

        LineData lineData = new LineData(minTempDataSet, maxTempDataSet);
        temperatureChart.setData(lineData);
        temperatureChart.invalidate();
    }

    private void setUpRainChart(List<WeatherDailyEntity> dailyForecast) {
        List<BarEntry> rainEntries = new ArrayList<>();

        for (int i = 0; i < dailyForecast.size(); i++) {
            // Multiplied by 100 to get percentage
            rainEntries.add(new BarEntry(i, dailyForecast.get(i).getProbabilityOfPrecipitation() * 100));
        }

        BarDataSet dataSet = new BarDataSet(rainEntries, "Rain Probability (%)");
        dataSet.setColor(0xFF03A9F4); // Blue

        BarData barData = new BarData(dataSet);
        rainChart.setData(barData);
        rainChart.invalidate();
    }
}
