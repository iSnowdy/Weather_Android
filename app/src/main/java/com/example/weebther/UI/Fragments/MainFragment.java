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

    private void setUpRecyclerView() {
        cityAdapter = new CityAdapter(weatherViewModel, new ArrayList<>(), this::openWeatherDetails, this::refreshWeather, getViewLifecycleOwner());
        recyclerView.setAdapter(cityAdapter);

        weatherViewModel.getRecentCities().observe(getViewLifecycleOwner(), cities -> {
            if (cities != null) {
                Log.d("MainFragment", "UI received " + cities.size() + " cities.");
                cityAdapter.updateCitiesData(new ArrayList<>(cities));
            }
        });
    }

    // This method is passed as an OnClickListener to the CityAdapter so that when the user
    // clicks on a city, the details fragment is opened
    private void openWeatherDetails(City city) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("city", city);
        Navigation.findNavController(requireView()).navigate(R.id.weatherDetailsFragment, bundle);
    }

    private void refreshWeather(City city) {
        weatherViewModel.refreshWeather(city.getName(), city.getLatitude(), city.getLongitude());
        Toast.makeText(requireContext(), "Weather updating for " + city.getName(), Toast.LENGTH_SHORT).show();
    }

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

    private void searchCity(String cityName) {
        weatherViewModel.fetchWeatherByCity(cityName);
        weatherViewModel.getWeatherLiveData(cityName).removeObservers(getViewLifecycleOwner());
        weatherViewModel.getWeatherLiveData(cityName).observe(getViewLifecycleOwner(), weatherResponse -> {
            if (weatherResponse != null) {
                Log.d("MainFragment", "Weather found for city: " + cityName);
                weatherViewModel.updateLastAccessed(cityName);
            }
        });
    }
}
