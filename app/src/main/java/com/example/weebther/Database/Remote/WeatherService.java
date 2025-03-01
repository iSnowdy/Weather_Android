package com.example.weebther.Database.Remote;

import com.example.weebther.Database.Remote.RemoteModels.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/*
Retrofit interface that allows us to do HTTP requests to the API we are calling. In this case,
the OpenWeatherAPI 3.0.

The decoration will indicate what kind of request we are doing. In this case, it is a GET request.
After the GET decoration, inside the String, it will be the endpoint we are calling. By endpoint
I mean this:
    https://api.openweathermap.org/data/3.0/onecall
Endpoint is the last part of the URL we are calling. After all, parameters specific to the
API Rest are added to the URL.

So the onecall plus the query parameters will be added to the URL we built in the WeatherService
using Retrofit.Builder().
*/

public interface WeatherService {
    @GET("onecall")
    Call<WeatherResponse> getWeather(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("appid") String apiKey,
            @Query("units") String units // "metric" = ºC | "imperial" = F
    );
}
