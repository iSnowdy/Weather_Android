package com.example.weebther.UI.ViewModels;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.weebther.Database.Local.Entity.City;
import com.example.weebther.Database.Local.Entity.WeatherCurrentEntity;
import com.example.weebther.Database.Local.Entity.WeatherDailyEntity;
import com.example.weebther.Database.Local.Entity.WeatherHourlyEntity;
import com.example.weebther.Database.Remote.GeoCodingCallBack;
import com.example.weebther.Database.Repository.GeoCodingRepository;
import com.example.weebther.Database.Repository.WeatherRepository;
import com.example.weebther.Exceptions.GeoLocatorException;
import com.example.weebther.R;
import com.example.weebther.Utils.SharedPreferencesManager;
import com.example.weebther.Utils.UnitSystem;

import java.util.List;

/**
 * ViewModel that manages weather data and geolocation.
 * It acts as a bridge between the UI and {@link WeatherRepository} and {@link GeoCodingRepository}.
 */
public class WeatherViewModel extends AndroidViewModel {
    private final WeatherRepository weatherRepository;
    private final GeoCodingRepository geoCodingRepository;
    private final int MAX_CITIES = 5;
    private final MutableLiveData<String> geoErrorLiveData = new MutableLiveData<>();

    public WeatherViewModel(Application application) {
        super(application);
        String API_KEY = application.getString(R.string.WEATHER_API_KEY);
        this.weatherRepository = new WeatherRepository(application, API_KEY);
        this.geoCodingRepository = new GeoCodingRepository(application);
    }

    // Changes the unit system of the API call
    public void changeUnitSystem(UnitSystem unitSystem) {
        SharedPreferencesManager.saveUnitPreference(getApplication(), unitSystem);
        Log.d("WeatherViewModel", "Unit system changed to: " + unitSystem);
        refreshAllCitiesWeather();
    }

    public void fetchWeatherByCity(String cityName) {
        geoCodingRepository.getCity(cityName, new GeoCodingCallBack() {
            @Override
            public void onSuccess(City city) {
                Log.d("WeatherViewModel", "City found: " + city.getName());

                // First, obtain weather data from cache (Room)
                weatherRepository.getCurrentWeather(city.getName()).observeForever(weatherData -> {
                    if (weatherData != null) {
                        Log.d("WeatherViewModel", "Using cached weather data for " + city.getName());
                    } else { // If there's no data, then we call the API
                        Log.d("WeatherViewModel", "No cached weather found. Calling API...");
                        weatherRepository.refreshWeather(city.getName(), city.getLatitude(), city.getLongitude());
                    }
                });
            }

            @Override
            public void onError(GeoLocatorException geoLocatorException, Throwable throwable) {
                Log.e("WeatherViewModel", "GeoCoding error: " + geoLocatorException.getMessage());
                geoErrorLiveData.postValue(geoLocatorException.getMessage());
            }
        });
    }


    private LiveData<WeatherCurrentEntity> fetchWeather(City city) {
        return weatherRepository.getCurrentWeather(city.getName());
    }

    public void refreshWeather(String cityName, double latitude, double longitude) {
        weatherRepository.refreshWeather(cityName, latitude, longitude);
    }

    public void refreshAllCitiesWeather() {
        weatherRepository.getRecentCities(MAX_CITIES).observeForever(cities -> {
            for (City city : cities) {
                refreshWeather(city.getName(), city.getLatitude(), city.getLongitude());
            }
        });
    }

    public LiveData<List<City>> getRecentCities() {
        return weatherRepository.getRecentCities(MAX_CITIES);
    }

    public LiveData<List<City>> getFavouriteCities() {
        return weatherRepository.getFavouriteCities();
    }

    public void toggleCityFavorite(String cityName, boolean isFavorite) {
        weatherRepository.toggleCityFavorite(cityName, isFavorite);
    }

    public void updateLastAccessed(String cityName) {
        weatherRepository.updateLastAccessed(cityName);
    }

    public void deleteCity(String cityName) {
        weatherRepository.deleteCityByName(cityName);
    }

    public boolean isInternetAvailable() {
        return weatherRepository.isInternetAvailable();
    }

    public LiveData<WeatherCurrentEntity> getWeatherLiveData(String cityName) {
        return weatherRepository.getCurrentWeather(cityName);
    }

    public LiveData<List<WeatherHourlyEntity>> getHourlyForecast(String cityName) {
        return weatherRepository.getHourlyForecast(cityName);
    }

    public LiveData<List<WeatherDailyEntity>> getDailyForecast(String cityName) {
        return weatherRepository.getDailyForecast(cityName);
    }


    public LiveData<String> getGeoErrorLiveData() {
        return geoErrorLiveData;
    }
}
