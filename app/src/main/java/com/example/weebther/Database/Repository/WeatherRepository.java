package com.example.weebther.Database.Repository;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.weebther.Database.Local.Entity.City;
import com.example.weebther.Database.Local.Entity.WeatherCurrentEntity;
import com.example.weebther.Database.Local.Entity.WeatherDailyEntity;
import com.example.weebther.Database.Local.Entity.WeatherHourlyEntity;
import com.example.weebther.Database.Remote.WeatherCallBack;
import com.example.weebther.Database.Remote.RemoteModels.WeatherResponse;
import com.example.weebther.Exceptions.OpenWeatherException;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository class that handles weather data retrieval from both local storage (Room) and remote API (OpenWeather).
 * Ensures caching, API calls optimization, and network checking.
 */
public class WeatherRepository {
    private final WeatherRemoteDataSource remoteDataSource;
    private final WeatherLocalDataSource localDataSource;
    private final String apiKey;
    private final ExecutorService executorService;
    private final Context context;

    /**
     * Constructor for the Weather Repository.
     *
     * @param context Application context
     * @param apiKey  OpenWeather API key
     */
    public WeatherRepository(Context context, String apiKey) {
        this.remoteDataSource = new WeatherRemoteDataSource();
        this.localDataSource = new WeatherLocalDataSource(context);
        this.apiKey = apiKey;
        this.executorService = Executors.newSingleThreadExecutor();
        this.context = context;
    }

    /**
     * Retrieves weather data from cache first. If not available, fetches from API.
     *
     * @param cityName The name of the city
     * @return LiveData containing {@link WeatherCurrentEntity} with weather details
     */
    public LiveData<WeatherCurrentEntity> getCurrentWeather(String cityName) {
        return localDataSource.getLatestWeather(cityName);
    }

    public LiveData<List<WeatherHourlyEntity>> getHourlyForecast(String cityName) {
        return localDataSource.getHourlyEntities(cityName);
    }

    public LiveData<List<WeatherDailyEntity>> getDailyForecast(String cityName) {
        return localDataSource.getDailyEntities(cityName);
    }

    /**
     * Forces a fresh weather update from OpenWeather API, bypassing cache.
     *
     * @param cityName  The name of the city
     * @param latitude  Latitude coordinate of the city
     * @param longitude Longitude coordinate of the city
     */
    public void refreshWeather(String cityName, double latitude, double longitude) {
        executorService.execute(() -> fetchWeatherFromAPI(cityName, latitude, longitude));
    }

    /**
     * Fetches weather data from OpenWeather API if internet is available.
     * Stores the retrieved data in the local database.
     *
     * @param cityName  The name of the city
     * @param latitude  Latitude coordinate
     * @param longitude Longitude coordinate
     */
    private void fetchWeatherFromAPI(String cityName, double latitude, double longitude) {
        if (!isInternetAvailable()) {
            Log.w("WeatherRepository", "No internet connection. Skipping API call.");
            return;
        }

        remoteDataSource.fetchWeather(latitude, longitude, apiKey, new WeatherCallBack() {
            @Override
            public void onSuccess(WeatherResponse response) {
                Log.d("WeatherRepository", "Weather data fetched from API for " + cityName);

                // Store the weather data in Room
                executorService.execute(() -> localDataSource.storeWeatherData(
                        response.getCurrentEntity(cityName),
                        response.getHourlyEntities(cityName),
                        response.getDailyEntities(cityName)
                ));
            }

            @Override
            public void onError(OpenWeatherException openWeatherException, Throwable throwable) {
                Log.e("WeatherRepository", "Error fetching weather data: " + openWeatherException.getMessage());
            }
        });
    }

    /**
     * Toggles the favorite status of a city.
     *
     * @param cityName   Name of the city
     * @param isFavorite True to mark as favorite, false otherwise
     */
    public void toggleCityFavorite(String cityName, boolean isFavorite) {
        Log.d("WeatherRepository", "Toggling city favorite: " + cityName + " to " + isFavorite);
        executorService.execute(() -> localDataSource.toggleCityFavourite(cityName, isFavorite));
    }

    /**
     * Updates the last accessed timestamp of a city.
     *
     * @param cityName Name of the city
     */
    public void updateLastAccessed(String cityName) {
        executorService.execute(() -> {
            long now = System.currentTimeMillis();
            localDataSource.updateLastAccessedCity(cityName, now);
        });
    }

    /**
     * Retrieves a city from the database.
     *
     * @param cityName Name of the city
     * @return LiveData containing the city
     */
    public LiveData<City> getCity(String cityName) {
        return localDataSource.getCity(cityName);
    }

    /**
     * Retrieves a list of recently accessed cities.
     *
     * @param limit Maximum number of recent cities to return
     * @return LiveData list of recent cities
     */
    public LiveData<List<City>> getRecentCities(int limit) {
        return localDataSource.getRecentCities(limit);
    }

    /**
     * Deletes a city and all its associated weather data.
     *
     * @param cityName Name of the city to delete
     */
    public void deleteCityByName(String cityName) {
        executorService.execute(() -> {
            localDataSource.deleteWeatherData(cityName);
            localDataSource.deleteCity(cityName);
        });
    }

    /**
     * Checks if the device has an active internet connection.
     *
     * @return True if internet is available, false otherwise
     */
    public boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
