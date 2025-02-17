package UI.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import Database.Local.Entity.City;
import UI.Adapters.CityAdapter;
import UI.ViewModels.WeatherViewModel;

public class CityListFragment extends Fragment {
    private WeatherViewModel weatherViewModel;
    private RecyclerView recyclerView;
    private CityAdapter cityAdapter;
    private SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_city_list, container, false);
        searchView = view.findViewById(R.id.searchView);
        recyclerView = view.findViewById(R.id.citiesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchCity(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);
        weatherViewModel.getCitiesLiveData().observe(getViewLifecycleOwner(), cities -> {
            cityAdapter = new CityAdapter(cities, city -> openWeatherDetails(city));
            recyclerView.setAdapter(cityAdapter);
        });

        return view;
    }

    private void searchCity(String cityName) {
        weatherViewModel.fetchCoordinates(cityName);

        weatherViewModel.getLatitude().observe(getViewLifecycleOwner(), latitude -> {
            if (latitude != null && weatherViewModel.getLongitude().getValue() != null) {
                double longitude = weatherViewModel.getLongitude().getValue();
                City city = new City(cityName.hashCode(), cityName, "Country Name");
                weatherViewModel.addCity(city);
                // Now that we have a City object, we can fetch the weather
                weatherViewModel.fetchWeather(cityName, latitude, longitude);
            }
            weatherViewModel.getLongitude().observe(getViewLifecycleOwner(), longitude -> {
                weatherViewModel.fetchWeather(cityName, latitude, longitude);
            });
        });

        weatherViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                // Handle the error?
                Toast.makeText(requireContext(), "City not found!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openWeatherDetails(City city) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("city", city);
        Navigation.findNavController(requireView()).navigate(R.id.action_cityListFragment_to_weatherDetailsFragment, bundle);
    }
}
