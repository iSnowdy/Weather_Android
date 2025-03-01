package com.example.weebther.UI.ViewModels;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.weebther.Database.Local.Entity.City;
import com.example.weebther.Database.Remote.GeoCodingCallBack;
import com.example.weebther.Database.Repository.GeoCodingRepository;
import com.example.weebther.Database.Repository.WeatherRepository;
import com.example.weebther.Database.Remote.RemoteModels.WeatherResponse;
import com.example.weebther.Exceptions.GeoLocatorException;
import com.example.weebther.R;

import java.util.List;

/**
 * ViewModel that manages weather data and geolocation.
 * It acts as a bridge between the UI and {@link WeatherRepository} and {@link GeoCodingRepository}.
 */
public class WeatherViewModel extends AndroidViewModel {
    private final WeatherRepository weatherRepository;
    private final GeoCodingRepository geoCodingRepository;
    private final LiveData<List<City>> recentCities;
    private final MutableLiveData<WeatherResponse> weatherLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> geoErrorLiveData = new MutableLiveData<>();
    private final int MAX_CITIES = 5;

    /**
     * Constructor for {@code WeatherViewModel}.
     *
     * @param application The application context
     */
    public WeatherViewModel(Application application) {
        super(application);
        final String API_KEY = application.getString(R.string.WEATHER_API_KEY);
        System.out.println("API_KEY: " + API_KEY);
        this.weatherRepository = new WeatherRepository(application, API_KEY);
        this.geoCodingRepository = new GeoCodingRepository(application);
        this.recentCities = weatherRepository.getRecentCities(MAX_CITIES); // Fetch last 5 searched cities
    }

    /**
     * Fetches weather data based on city name.
     * - If cached coordinates exist, fetch weather immediately.
     * - Otherwise, retrieve coordinates from the GeoCoding API.
     *
     * @param cityName The city name entered by the user
     */
    public void fetchWeatherByCity(String cityName) {
        geoCodingRepository.getCity(cityName, new GeoCodingCallBack() {
            @Override
            public void onSuccess(City city) {
                Log.d("WeatherViewModel", "City found: " + city.getName());
                fetchWeather(city);
            }

            @Override
            public void onError(GeoLocatorException geoLocatorException, Throwable throwable) {
                Log.e("WeatherViewModel", "GeoCoding error: " + geoLocatorException.getMessage());
                geoErrorLiveData.postValue(geoLocatorException.getMessage());
            }
        });
    }

    /**
     * Fetches weather using City object.
     */

    private void fetchWeather(City city) {
        LiveData<WeatherResponse> weatherData = weatherRepository.getWeather(city.getName(), city.getLatitude(), city.getLongitude());
        weatherData.observeForever(weatherLiveData::postValue);
    }


    /**
     * Forces a fresh weather update from OpenWeather API, bypassing cache.
     *
     * @param cityName  The city name
     * @param latitude  Latitude coordinate
     * @param longitude Longitude coordinate
     * @return LiveData containing fresh weather data
     */
    public LiveData<WeatherResponse> refreshWeather(String cityName, double latitude, double longitude) {
        return weatherRepository.refreshWeather(cityName, latitude, longitude);
    }

    /**
     * Retrieves a list of recently searched cities.
     *
     * @return LiveData containing a list of {@link City} objects
     */
    public LiveData<List<City>> getRecentCities() {
        return recentCities;
    }

    /**
     * Marks a city as favorite or removes it from favorites.
     *
     * @param cityName   The city name
     * @param isFavorite True if the city should be marked as favorite, false otherwise
     */
    public void toggleCityFavorite(String cityName, boolean isFavorite) {
        weatherRepository.toggleCityFavorite(cityName, isFavorite);
    }

    /**
     * Updates the last accessed timestamp for a city.
     *
     * @param cityName The city name
     */
    public void updateLastAccessed(String cityName) {
        weatherRepository.updateLastAccessed(cityName);
    }

    /**
     * Deletes a city and its associated weather data.
     *
     * @param cityName The city name to delete
     */
    public void deleteCity(String cityName) {
        weatherRepository.deleteCityByName(cityName);
    }

    /**
     * Checks if the device has an active internet connection.
     *
     * @return True if the internet is available, false otherwise
     */
    public boolean isInternetAvailable() {
        return weatherRepository.isInternetAvailable();
    }

    /**
     * Returns LiveData containing weather data.
     *
     * @return LiveData of WeatherResponse
     */
    public LiveData<WeatherResponse> getWeatherLiveData() {
        return weatherLiveData;
    }

    /**
     * Returns LiveData containing GeoCoding errors.
     *
     * @return LiveData of error messages
     */
    public LiveData<String> getGeoErrorLiveData() {
        return geoErrorLiveData;
    }
}
