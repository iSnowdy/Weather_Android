package com.example.weebther.UI.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.example.weebther.Utils.SharedPreferencesManager;
import com.example.weebther.Utils.UnitSystem;

import java.util.ArrayList;


public class MainFragment extends Fragment {
    private MenuItem menuItem; // Needed in order to change the unit system icon

    private WeatherViewModel weatherViewModel;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private CityAdapter cityAdapter;

    private boolean isFavouritesFilterActive = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
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

    // Toolbar stuff

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // This will allow the fragment to manage toolbar options
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        // Stores the unit menu item for later updates (unit system icon)
        menuItem = menu.findItem(R.id.action_units);
        String unit = SharedPreferencesManager.getUnitPreference(requireContext());
        UnitSystem newUnit = unit.equals("metric") ? UnitSystem.METRIC : UnitSystem.IMPERIAL;
        updateUnitIcon(menuItem, newUnit);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_favourites) {
            toggleFavouritesFilter();
            return true;
        } else if (item.getItemId() == R.id.action_units) {
            toggleUnitSystem();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleFavouritesFilter() {
        isFavouritesFilterActive = !isFavouritesFilterActive; // Switches the state

        // Removes all previous observers
        weatherViewModel.getFavouriteCities().removeObservers(getViewLifecycleOwner());
        weatherViewModel.getRecentCities().removeObservers(getViewLifecycleOwner());

        if (isFavouritesFilterActive) {
            weatherViewModel.getFavouriteCities().observe(getViewLifecycleOwner(), cities -> {
                cityAdapter.updateCitiesData(new ArrayList<>(cities));
            });
            Toast.makeText(requireContext(), "Showing favourite cities", Toast.LENGTH_SHORT).show();
        } else {
            weatherViewModel.getRecentCities().observe(getViewLifecycleOwner(), cities -> {
                cityAdapter.updateCitiesData(new ArrayList<>(cities));
            });
            Toast.makeText(requireContext(), "Showing recent cities", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleUnitSystem() {
        String currentUnit = SharedPreferencesManager.getUnitPreference(requireContext());
        UnitSystem newUnit = currentUnit.equals("metric") ? UnitSystem.IMPERIAL : UnitSystem.METRIC;

        SharedPreferencesManager.saveUnitPreference(requireContext(), newUnit);

        weatherViewModel.changeUnitSystem(newUnit);

        // Updates the UI with the new unit system icon
        updateUnitIcon(menuItem, newUnit);

        Toast.makeText(requireContext(), "Unit system changed to: " + newUnit.getValue(), Toast.LENGTH_SHORT).show();
    }

    private void updateUnitIcon(MenuItem item, UnitSystem unitSystem) {
        if (item != null) {
            int newIcon = unitSystem.equals(UnitSystem.METRIC) ? R.drawable.celsius : R.drawable.fahrenheit;
            item.setIcon(newIcon);
        }
    }
}
