package com.example.weebther.UI.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;

import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weebther.Database.Local.Entity.City;
import com.example.weebther.R;
import com.example.weebther.UI.Adapters.CityAdapter;
import com.example.weebther.UI.ViewModels.WeatherViewModel;

import java.util.ArrayList;

public class CityListFragment extends Fragment {
    private WeatherViewModel weatherViewModel;
    private RecyclerView recyclerView;
    private SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        Log.d("CityListFragment", "CityListFragment loaded");
        View view = inflater.inflate(R.layout.fragment_city_list, container, false);
        searchView = view.findViewById(R.id.searchView);
        recyclerView = view.findViewById(R.id.citiesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);

        setUpRecyclerView();
        setUpSearchView();

        return view;
    }

    // Creates the RecyclerView Adapter empty and adds the Callback function openWeatherDetails().
    // It represents a function that will be executed everytime a city is clicked inside the
    // RecyclerView.
    private void setUpRecyclerView() {
        CityAdapter cityAdapter = new CityAdapter(new ArrayList<>(), city -> openWeatherDetails(city));
        recyclerView.setAdapter(cityAdapter);

        weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);
        // Remove observers to avoid duplicate calls
        weatherViewModel.getCitiesLiveData().removeObservers(getViewLifecycleOwner());
        // TODO: Need to notify the adapter that the data has changed
        weatherViewModel.getCitiesLiveData().observe(getViewLifecycleOwner(), cities -> {
            cityAdapter.updateCitiesData(cities);
        });
    }

    private void openWeatherDetails(City city) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("city", city);
        Navigation.findNavController(requireView()).navigate(R.id.action_cityListFragment_to_weatherDetailsFragment, bundle);
    }

    private void setUpSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("CityListFragment", "Search query: " + query);
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
        weatherViewModel.fetchCoordinates(cityName);

        weatherViewModel.getLatitude().observe(getViewLifecycleOwner(), latitude -> {
            Double longitude = weatherViewModel.getLongitude().getValue();
            if (latitude != null && longitude != null) {

                Log.d("CityListFragment", "Latitude: " + latitude + " | Longitude: " + longitude);

                City city = new City(cityName.hashCode(), cityName, "Country Name");

                Log.d("CityListFragment", "City created: " + city);

                weatherViewModel.addCity(city);
                // Now that we have a City object, we can fetch the weather
                weatherViewModel.fetchWeather(cityName, latitude, longitude);
            }
        });

        weatherViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                // Handle the error?
                Log.e("CityListFragment", "Error: " + error.getMessage());
                Toast.makeText(requireContext(), "City not found!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
