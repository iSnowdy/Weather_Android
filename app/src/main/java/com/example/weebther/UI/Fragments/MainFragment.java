package com.example.weebther.UI.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weebther.Database.Local.Entity.City;
import com.example.weebther.Database.Remote.RemoteModels.WeatherResponse;
import com.example.weebther.R;
import com.example.weebther.UI.Adapters.CityAdapter;
import com.example.weebther.UI.ViewModels.WeatherViewModel;

import java.util.ArrayList;

/**
 * Main fragment that displays the search bar and RecyclerView with cities.
 * Handles user interaction for searching and refreshing weather data.
 */

public class MainFragment extends Fragment {
    private WeatherViewModel weatherViewModel;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private CityAdapter cityAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        Log.d("MainFragment", "MainFragment loaded");

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        searchView = view.findViewById(R.id.searchView);
        recyclerView = view.findViewById(R.id.citiesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);

        weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);

        setUpRecyclerView();
        setUpSearchView();

        return view;
    }

    /**
     * Sets up RecyclerView with CityAdapter.
     */
    private void setUpRecyclerView() {
        cityAdapter = new CityAdapter(new ArrayList<>(), this::openWeatherDetails, this::refreshWeather);
        recyclerView.setAdapter(cityAdapter);

        // Observe the list of recent cities
        weatherViewModel.getRecentCities().observe(getViewLifecycleOwner(), cities -> {
            if (cities != null) {
                cityAdapter.updateCitiesData(cities);
            }
        });
    }

    /**
     * Opens detailed weather view for a city.
     *
     * @param city The selected city.
     */
    private void openWeatherDetails(City city) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("city", city);
        Navigation.findNavController(requireView()).navigate(R.id.weatherDetailsFragment, bundle);
    }

    /**
     * Refreshes the weather data for a city.
     *
     * @param city The city to refresh.
     */

    private void refreshWeather(City city) {
        Log.d("MainFragment", "Refreshing weather for " + city.getName());

        weatherViewModel.refreshWeather(city.getName(), city.getLatitude(), city.getLongitude())
                .observe(getViewLifecycleOwner(), weatherResponse -> {
                    if (weatherResponse != null) {
                        Toast.makeText(requireContext(), "Weather updated for " + city.getName(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Failed to update weather!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Sets up the search bar to look for cities.
     */

    private void setUpSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("MainFragment", "Search query: " + query);
                searchCity(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    /**
     * Searches for a city using GeoCoding API and fetches weather data if found.
     */
    private void searchCity(String cityName) {
        weatherViewModel.fetchWeatherByCity(cityName);

        weatherViewModel.getWeatherLiveData().observe(getViewLifecycleOwner(), new Observer<WeatherResponse>() {
            @Override
            public void onChanged(WeatherResponse weatherResponse) {
                if (weatherResponse != null) {
                    Log.d("MainFragment", "Weather found for city: " + cityName);

                    // Already stored in DB by GeoCodingRepository, no need to re-create City.
                    weatherViewModel.updateLastAccessed(cityName);

                    // Remove observer to avoid repeated triggers
                    weatherViewModel.getWeatherLiveData().removeObserver(this);
                }
            }
        });

        // Observe errors
        weatherViewModel.getGeoErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Log.e("MainFragment", "Error: " + error);
                Toast.makeText(requireContext(), "City not found!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
