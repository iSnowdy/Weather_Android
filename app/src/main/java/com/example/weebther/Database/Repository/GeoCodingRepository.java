package com.example.weebther.Database.Repository;

import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.weebther.Database.Local.CityDAO;
import com.example.weebther.Database.Local.DatabaseManager;
import com.example.weebther.Database.Local.Entity.City;
import com.example.weebther.Database.Remote.GeoCodingCallBack;
import com.example.weebther.Database.Remote.GeoCodingService;
import com.example.weebther.Database.Remote.RemoteModels.GeoCodingResponse;
import com.example.weebther.Exceptions.GeoLocatorException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Repository for handling reverse geolocation API calls using Nominatim via Retrofit.
 * <p>
 * Retrieves city coordinates and stores them in SQLite using Room.
 */

public class GeoCodingRepository {
    private static final String GEOCODING_BASE_URL = "https://nominatim.openstreetmap.org/";
    private final GeoCodingService geoCodingService;
    private final CityDAO cityDAO;
    private final ExecutorService executorService;

    /**
     * Constructor for GeoCodingRepository.
     *
     * @param context The application context required for accessing the database.
     */
    public GeoCodingRepository(Context context) {
        geoCodingService = RetrofitClient.getRetrofitInstance(GEOCODING_BASE_URL).create(GeoCodingService.class);
        DatabaseManager db = DatabaseManager.getInstance(context);
        cityDAO = db.cityDAO();
        executorService = Executors.newSingleThreadExecutor(); // Initialize ExecutorService
    }

    /**
     * Retrieves a city object from cache or API.
     *
     * @param cityName The name of the city.
     * @param callback The callback returning a {@link City} object or an error.
     */
    public void getCity(String cityName, GeoCodingCallBack callback) {
        executorService.execute(() -> {
            Optional<City> optionalCity = cityDAO.getCity(cityName);
            if (optionalCity.isPresent()) {
                Log.d("GeoCodingRepository", "Using cached city: " + cityName);
                callback.onSuccess(optionalCity.get());
            } else {
                fetchCityFromAPI(cityName, callback);
            }
        });
    }

    /**
     * Calls the GeoCoding API to fetch city data.
     *
     * @param cityName The name of the city.
     * @param callback The callback to return the city object or an error.
     */
    private void fetchCityFromAPI(String cityName, GeoCodingCallBack callback) {
        Log.d("GeoCodingRepository", "Fetching city from API: " + cityName);
        Call<List<GeoCodingResponse>> call = geoCodingService.getCoordinates(cityName, "json", 1);

        call.enqueue(new retrofit2.Callback<List<GeoCodingResponse>>() {
            @Override
            public void onResponse(Call<List<GeoCodingResponse>> call, Response<List<GeoCodingResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    GeoCodingResponse geoResponse = response.body().get(0);
                    City city = new City(cityName, "Unknown", geoResponse.getLatitude(), geoResponse.getLongitude(), false, System.currentTimeMillis());

                    storeCity(city); // Store city asynchronously

                    Log.d("GeoCodingRepository", "City stored: " + city.getName());
                    callback.onSuccess(city);
                } else {
                    handleAPIError(response, callback);
                }
            }

            @Override
            public void onFailure(Call<List<GeoCodingResponse>> call, Throwable t) {
                handleAPIFailure(t, callback);
            }
        });
    }

    /**
     * Stores a city in the database asynchronously.
     *
     * @param city The city object to store.
     */
    private void storeCity(City city) {
        executorService.execute(() -> {
            cityDAO.storeCity(city);
            Log.d("GeoCodingRepository", "City successfully stored in DB: " + city.getName());
            System.err.println("CITY LATITUDE: " + city.getLatitude() + ", CITY LONGITUDE: " + city.getLongitude());
        });
    }

    /**
     * Handles API errors.
     *
     * @param response The API response.
     * @param callback The callback to return an error.
     */
    private void handleAPIError(Response<List<GeoCodingResponse>> response, GeoCodingCallBack callback) {
        int errorCode = response.code();
        String errorMessage = response.message();

        GeoLocatorException exception;
        if (errorCode == 404) {
            exception = new GeoLocatorException("City not found (404)");
        } else if (errorCode == 500) {
            exception = new GeoLocatorException("Server error (500)");
        } else {
            exception = new GeoLocatorException("Unknown API error: " + errorMessage);
        }

        Log.e("GeoCodingRepository", "API error: " + errorMessage);
        callback.onError(exception, null);
    }

    /**
     * Handles API call failures (e.g., no internet connection).
     *
     * @param throwable The exception thrown during the API call.
     * @param callback  The callback to return an error.
     */
    private void handleAPIFailure(Throwable throwable, GeoCodingCallBack callback) {
        Log.e("GeoCodingRepository", "API call failed: " + throwable.getMessage(), throwable);
        callback.onError(new GeoLocatorException("Error fetching city data"), throwable);
    }
}
