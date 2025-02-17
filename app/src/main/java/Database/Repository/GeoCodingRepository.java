package Database.Repository;

import java.util.List;

import Database.Remote.GeoCodingCallBack;
import Database.Remote.GeoCodingService;
import Domain.Models.GeoCodingResponse;
import Exceptions.GeoLocatorException;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Reverse Geolocation
// Using Nominatim

public class GeoCodingRepository {
    private static final String API_URL = "https://nominatim.openstreetmap.org/search?q=%s&format=json&limit=1";
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
        Call<List<GeoCodingResponse>> call = geoCodingService.getCoordinates(cityName, "json", 1);
        call.enqueue(new retrofit2.Callback<List<GeoCodingResponse>>() {
            @Override
            public void onResponse(Call<List<GeoCodingResponse>> call, Response<List<GeoCodingResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    // lat | long
                    callback.onSuccess(response.body().get(0).getLatitude(), response.body().get(1).getLongitude());
                } else {
                    callback.onError(new GeoLocatorException("Error while trying to convert the city's name to coordinates"), null);
                }
            }

            @Override
            public void onFailure(Call<List<GeoCodingResponse>> call, Throwable t) {
                callback.onError(new GeoLocatorException("Error while trying to convert the city's name to coordinates"), t);
            }
        });
    }
}
