package com.example.weebther.Database.Repository;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.weebther.Database.Local.Entity.City;
import com.example.weebther.Database.Local.Entity.WeatherCurrentEntity;
import com.example.weebther.Database.Remote.WeatherCallBack;
import com.example.weebther.Database.Remote.RemoteModels.WeatherCondition;
import com.example.weebther.Database.Remote.RemoteModels.WeatherCurrent;
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
    private final Handler mainHandler;
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
        this.mainHandler = new Handler(Looper.getMainLooper()); // Ensure we can run on main thread
        this.context = context;
    }

    /**
     * Fetches weather data with caching logic.
     * If cached data is available, it returns it. Otherwise, it fetches from the API and stores it in the database.
     *
     * @param cityName  The name of the city
     * @param latitude  Latitude coordinate of the city
     * @param longitude Longitude coordinate of the city
     * @return LiveData containing {@link WeatherResponse} with weather details
     */
    public LiveData<WeatherResponse> getWeather(String cityName, double latitude, double longitude) {
        MutableLiveData<WeatherResponse> liveData = new MutableLiveData<>();

        executorService.execute(() -> {
            LiveData<WeatherCurrentEntity> cachedWeatherLiveData = localDataSource.getLatestWeather(cityName);

            mainHandler.post(() -> cachedWeatherLiveData.observeForever(cachedWeather -> {
                if (cachedWeather != null) {

                    Log.d("WeatherRepository", "Using cached weather data for " + cityName);
                    System.err.println("CACHE: City Name: " + cityName + ", Latitude: " + latitude + ", Longitude: " + longitude);

                    // Use cached data if available
                    WeatherResponse cachedResponse = buildWeatherResponseFromCache(cityName, latitude, longitude, cachedWeather);
                    liveData.postValue(cachedResponse);
                } else {
                    Log.d("WeatherRepository", "No cached weather data for " + cityName);
                    System.err.println("NO CACHE: City Name: " + cityName + ", Latitude: " + latitude + ", Longitude: " + longitude);


                    // If no cache, check for internet connection and fetch from API
                    fetchWeatherFromAPI(cityName, latitude, longitude, liveData);
                }
            }));
        });

        return liveData;
    }

    /**
     * Forces a fresh weather update from OpenWeather API, bypassing cache.
     *
     * @param cityName  The name of the city
     * @param latitude  Latitude coordinate of the city
     * @param longitude Longitude coordinate of the city
     * @return LiveData containing fresh {@link WeatherResponse} data
     */
    public LiveData<WeatherResponse> refreshWeather(String cityName, double latitude, double longitude) {
        MutableLiveData<WeatherResponse> liveData = new MutableLiveData<>();

        executorService.execute(() -> fetchWeatherFromAPI(cityName, latitude, longitude, liveData));

        return liveData;
    }

    /**
     * Fetches weather data from OpenWeather API if internet is available.
     * Stores the retrieved data in the local database.
     *
     * @param cityName  The name of the city
     * @param latitude  Latitude coordinate
     * @param longitude Longitude coordinate
     * @param liveData  LiveData object to update the UI
     */
    private void fetchWeatherFromAPI(String cityName, double latitude, double longitude, MutableLiveData<WeatherResponse> liveData) {
        if (!isInternetAvailable()) {
            mainHandler.post(() -> liveData.setValue(null));
            return;
        }

        remoteDataSource.fetchWeather(latitude, longitude, apiKey, new WeatherCallBack() {
            @Override
            public void onSuccess(WeatherResponse response) {

                Log.d("WeatherRemoteDataSource", "OpenWeather API Response: "
                        + "Temp: " + response.current.temperature
                        + ", Humidity: " + response.current.humidity
                        + ", Weather: " + response.current.weatherConditions.get(0).description);

                mainHandler.post(() -> liveData.setValue(response));

                executorService.execute(() -> localDataSource.storeWeatherData(
                        response.getCurrentEntity(cityName),
                        response.getHourlyEntities(cityName),
                        response.getDailyEntities(cityName)
                ));
            }

            @Override
            public void onError(OpenWeatherException openWeatherException, Throwable throwable) {
                mainHandler.post(() -> liveData.setValue(null));
            }
        });
    }

    /**
     * Builds a {@link WeatherResponse} from cached Room data.
     *
     * @param cityName      The name of the city
     * @param latitude      Latitude coordinate
     * @param longitude     Longitude coordinate
     * @param cachedWeather The cached weather data from Room
     * @return A {@link WeatherResponse} containing the cached weather data
     */
    private WeatherResponse buildWeatherResponseFromCache(String cityName, double latitude, double longitude, WeatherCurrentEntity cachedWeather) {
        if (cachedWeather == null) {
            return null;
        }

        WeatherResponse cachedResponse = new WeatherResponse();
        cachedResponse.latitude = latitude;
        cachedResponse.longitude = longitude;
        cachedResponse.current = new WeatherCurrent();
        cachedResponse.current.temperature = cachedWeather.getTemperature();
        cachedResponse.current.feelsLike = cachedWeather.getFeelsLike();
        cachedResponse.current.humidity = cachedWeather.getHumidity();
        cachedResponse.current.windSpeed = cachedWeather.getWindSpeed();
        cachedResponse.current.weatherConditions = List.of(new WeatherCondition(cachedWeather.getWeatherDescription(), cachedWeather.getWeatherIcon()));

        return cachedResponse;
    }

    /**
     * Toggles the favorite status of a city.
     *
     * @param cityName   Name of the city
     * @param isFavorite True to mark as favorite, false otherwise
     */
    public void toggleCityFavorite(String cityName, boolean isFavorite) {
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
