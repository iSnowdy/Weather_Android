package com.example.weebther.UI.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
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

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import com.example.weebther.Utils.WeatherFormatter;
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
    private TextView
            cityName,
            temperature, feelsLike,
            description,
            minTemp, maxTemp,
            humidity, uvi,
            windSpeed, rainProbability,
            rain;
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

    private void setupUI(View view) {
        cityName = view.findViewById(R.id.cityName);
        temperature = view.findViewById(R.id.temperature);
        feelsLike = view.findViewById(R.id.feelsLike);
        description = view.findViewById(R.id.description);

        minTemp = view.findViewById(R.id.minTemp);
        maxTemp = view.findViewById(R.id.maxTemp);
        humidity = view.findViewById(R.id.humidity);
        uvi = view.findViewById(R.id.uvi);
        windSpeed = view.findViewById(R.id.windSpeed);
        rainProbability = view.findViewById(R.id.rainProbability);
        rain = view.findViewById(R.id.rain);

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
                //updateDailyForecast(dailyData);
                updateDailyForecast(dailyData);
                setUpCharts(dailyData);
            }
        });
    }


    @SuppressLint("SetTextI18n")
    private void updateWeatherUI(WeatherCurrentEntity weatherCurrentEntity) {
        if (weatherCurrentEntity != null) {
            Log.d("WeatherDetailsFragment", "Updating weather UI current entity");

            temperature.setText("Temperature: " + WeatherFormatter.formatUnit(
                    requireContext(), weatherCurrentEntity.getTemperature(), WeatherFormatter.UnitType.TEMPERATURE));
            feelsLike.setText("Feels like: " + WeatherFormatter.formatUnit(
                    requireContext(), weatherCurrentEntity.getFeelsLike(), WeatherFormatter.UnitType.TEMPERATURE));
            description.setText(weatherCurrentEntity.getWeatherDescription());

            humidity.setText("Humidity: " + weatherCurrentEntity.getHumidity() + "%");
            uvi.setText(WeatherFormatter.formatUnit(
                    requireContext(), weatherCurrentEntity.getUvi(), WeatherFormatter.UnitType.UV_INDEX));
            windSpeed.setText("Wind: " + WeatherFormatter.formatUnit(
                    requireContext(), weatherCurrentEntity.getWindSpeed(), WeatherFormatter.UnitType.WIND_SPEED));
        }
    }

    private void updateDailyForecast(List<WeatherDailyEntity> dailyForecast) {
        Log.d("WeatherDetailsFragment", "Updating daily weather UI");

        if (dailyForecast == null || dailyForecast.isEmpty()) {
            Log.d("WeatherDetailsFragment", "No daily forecast data available.");
            return;
        }

        for (WeatherDailyEntity daily : dailyForecast) {
            if (findOutIfToday(daily)) {
                Log.d("WeatherDetailsFragment", "Today's forecast found. Updating UI.");

                minTemp.setText(WeatherFormatter.formatUnit(
                        requireContext(), daily.getTempMin(), WeatherFormatter.UnitType.TEMPERATURE));
                maxTemp.setText(WeatherFormatter.formatUnit(
                        requireContext(), daily.getTempMax(), WeatherFormatter.UnitType.TEMPERATURE));
                rainProbability.setText(WeatherFormatter.formatUnit(requireContext(), daily.getProbabilityOfPrecipitation(), WeatherFormatter.UnitType.PERCENTAGE));

                if (isRaining(daily)) {
                    rain.setText(WeatherFormatter.formatUnit(
                            requireContext(), daily.getRain(), WeatherFormatter.UnitType.RAIN_VOLUME));
                    rain.setVisibility(View.VISIBLE);
                } else {
                    rain.setVisibility(View.GONE); // Hides the TextView if there's no rain
                }

                break; // Early exit of the loop if we found today
            }
        }
    }

    private boolean findOutIfToday(WeatherDailyEntity daily) {
        LocalDate today = LocalDate.now();

        // Timestamp of the API -> LocalDate
        LocalDate forecastDate = Instant.ofEpochSecond(daily.getTimestamp())
                .atZone(ZoneId.systemDefault()) // System default zone
                .toLocalDate();

        return today.isEqual(forecastDate);
    }


    private boolean isRaining(WeatherDailyEntity daily) {
        // If the value of rain is anything other than 0.0, then it means it will probably rain
        return daily.getRain() != 0.0;
    }

    /*private void updateHourlyForecast(List<WeatherHourlyEntity> hourlyForecast) {
        StringBuilder hourlyText = new StringBuilder("Hourly Forecast:\n");
        for (WeatherHourlyEntity hourly : hourlyForecast) {
            hourlyText.append(String.format("%d:00 - %.1f°C, %s\n", hourly.getTimestamp(), hourly.getTemperature(), hourly.getWeatherDescription()));
        }
    }

    private void updateDailyForecast(List<WeatherDailyEntity> dailyForecast) {
        StringBuilder dailyText = new StringBuilder("Daily Forecast:\n");
        for (WeatherDailyEntity daily : dailyForecast) {
            dailyText.append(String.format("Day %d - Min: %.1f°C, Max: %.1f°C, %s\n",
                    daily.getTimestamp(), daily.getTempMin(), daily.getTempMax(), daily.getWeatherDescription()));
        }
    }*/

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
