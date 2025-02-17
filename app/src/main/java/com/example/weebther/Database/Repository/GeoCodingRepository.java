package com.example.weebther.Database.Repository;

import android.util.Log;

import java.util.List;

import com.example.weebther.Database.Remote.GeoCodingCallBack;
import com.example.weebther.Database.Remote.GeoCodingService;
import com.example.weebther.Domain.Models.GeoCodingResponse;
import com.example.weebther.Exceptions.GeoLocatorException;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Reverse Geolocation
// Using Nominatim

public class GeoCodingRepository {
    private static final String API_URL = "https://nominatim.openstreetmap.org/";
    private final GeoCodingService geoCodingService;

    public GeoCodingRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        geoCodingService = retrofit.create(GeoCodingService.class);
    }


    // Calls the API and retrieves the information as a JSON object
    public void getCoordinates(String cityName, GeoCodingCallBack callback) {

        Log.d("GeoCodingRepository", "Calling API for City: " + cityName);

        Call<List<GeoCodingResponse>> call = geoCodingService.getCoordinates(cityName, "json", 1);
        call.enqueue(new retrofit2.Callback<List<GeoCodingResponse>>() {
            @Override
            public void onResponse(Call<List<GeoCodingResponse>> call, Response<List<GeoCodingResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    double latitude = response.body().get(0).getLatitude();
                    double longitude = response.body().get(0).getLongitude();

                    Log.d("GeoCodingRepository", "Latitude: " + latitude + " | Longitude: " + longitude);

                    callback.onSuccess(latitude, longitude);
                } else {

                    Log.e("GeoCodingRepository", "Error while trying to convert the city's name to coordinates");

                    callback.onError(new GeoLocatorException("Error while trying to convert the city's name to coordinates"), null);
                }
            }

            @Override
            public void onFailure(Call<List<GeoCodingResponse>> call, Throwable t) {

                Log.e("GeoCodingRepository", "Geolocator API call failed: " + t.getMessage(), t);

                callback.onError(new GeoLocatorException("Error while trying to convert the city's name to coordinates"), t);
            }
        });
    }
}
