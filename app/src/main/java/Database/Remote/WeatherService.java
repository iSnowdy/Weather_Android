package Database.Remote;

import Database.Local.Entity.WeatherCurrentEntity;
import Domain.Models.WeatherResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {
    @GET("onecall") // What goes after the GET request
    Call<WeatherResponse> getWeather(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("appid") String apiKey,
            @Query("units") String units // "metric" = ºC | "imperial" = F
    );
}
