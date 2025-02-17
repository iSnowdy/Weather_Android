package UI.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import Database.Local.Entity.City;
import UI.ViewModels.WeatherViewModel;

public class WeatherDetailsFragment extends Fragment {
    private WeatherViewModel weatherViewModel;
    private TextView
            cityName, temperature, minTemperature, maxTemperature, description;

    @SuppressLint("DefaultLocale")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_weather_details, container, false);

        cityName = view.findViewById(R.id.cityName);
        temperature = view.findViewById(R.id.temperature);
        minTemperature = view.findViewById(R.id.minTemperature);
        maxTemperature = view.findViewById(R.id.maxTemperature);
        description = view.findViewById(R.id.description);

        weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);

        // Retrieves the City information from the Bundle (deserializes it)
        if (getArguments() != null) {
            City city = (City) getArguments().getSerializable("city");
            cityName.setText(city.getName());

            weatherViewModel.getWeatherLiveData().observe(getViewLifecycleOwner(), weatherResponse -> {
                if (weatherResponse != null && !weatherResponse.daily.isEmpty()) {
                    temperature.setText(String.format("%.1f°C", weatherResponse.current.temperature));
                    minTemperature.setText(String.format("%.1f°C", weatherResponse.daily.get(0).temperature.min));
                    maxTemperature.setText(String.format("%.1f°C", weatherResponse.daily.get(0).temperature.max));
                    description.setText(weatherResponse.current.weatherConditions.get(0).description);
                }
            });
        }
        return view;
    }
}
