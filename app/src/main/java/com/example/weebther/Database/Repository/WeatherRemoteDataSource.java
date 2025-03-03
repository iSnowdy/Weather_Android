package com.example.weebther.Database.Repository;

import android.util.Log;

import com.example.weebther.Database.Remote.WeatherCallBack;
import com.example.weebther.Database.Remote.WeatherService;
import com.example.weebther.Database.Remote.RemoteModels.WeatherResponse;
import com.example.weebther.Exceptions.OpenWeatherException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherRemoteDataSource {
    private static final String OPEN_WEATHER_BASE_URL = "https://api.openweathermap.org/data/3.0/";
    private final WeatherService weatherService;

    public WeatherRemoteDataSource() {
        this.weatherService = RetrofitClient.getRetrofitInstance(OPEN_WEATHER_BASE_URL).create(WeatherService.class);
    }

    /**
     * Fetches weather data from OpenWeather API.
     *
     * @param latitude  Latitude of the city.
     * @param longitude Longitude of the city.
     * @param apiKey    OpenWeather API Key.
     * @param callback  Callback to return weather data or an error.
     */

    public void fetchWeather(double latitude, double longitude, String apiKey, WeatherCallBack callback) {
        weatherService.getWeather(latitude, longitude, apiKey, "metric").enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                    Log.d("WeatherRemoteDataSource", "Calling OpenWeather API for: " + latitude + ", " + longitude);

                } else {
                    handleAPIError(response, callback);
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                handleAPIFailure(t, callback);
            }
        });
    }

    /**
     * Handles API errors and passes appropriate error messages.
     *
     * @param response The API response.
     * @param callback The callback to return an error.
     */

    private void handleAPIError(Response<WeatherResponse> response, WeatherCallBack callback) {
        int errorCode = response.code();
        String errorMessage = response.message();

        OpenWeatherException exception;
        switch (errorCode) {
            case 401:
                exception = new OpenWeatherException("Unauthorized (401) - Invalid API Key");
                break;
            case 404:
                exception = new OpenWeatherException("City not found (404)");
                break;
            case 429:
                exception = new OpenWeatherException("Too many requests (429) - API limit reached");
                break;
            case 500:
                exception = new OpenWeatherException("Server error (500)");
                break;
            default:
                exception = new OpenWeatherException("Unknown API error (" + errorCode + "): " + errorMessage);
        }

        Log.e("WeatherRemoteDataSource", "API error: " + errorMessage);
        callback.onError(exception, null);
    }

    /**
     * Handles API call failures (e.g., no internet connection).
     *
     * @param throwable The exception thrown during the API call.
     * @param callback  The callback to return an error.
     */

    private void handleAPIFailure(Throwable throwable, WeatherCallBack callback) {
        Log.e("OpenWeatherRemoteDataSource", "API call failed: " + throwable.getMessage(), throwable);
        callback.onError(new OpenWeatherException("Error while converting the city's name to coordinates"), throwable);
    }
}



/*

Retrofit is a library that allows you to make HTTP requests in Android. It greatly simplifies
communication with APIs REST.

It will allow us to execute HTTP requests such as GET, POST, PUT and DELETE, handle HTTP responses
of JSON type automatically and convert them to Java objects.

Retrofit will act as a layer for us. Instead of using HttpURLConnection, native in Java, Retrofit
will handle the communication and convert the response to a Java object.

*/

/*

Retrofit has two ways of making HTTP requests to API:
    1. Synchronous calls. It means that the code execution will wait for the response from the API,
    blocking it. In our case, it means that the UI will be blocked while waiting for the response.
    This means that the user will not be able to interact with our application as long as the
    call is being made.
    For us we do not need the response from the API in order for the app to properly execute. It is
    "optional", so to speak. So blocking it can be perceived as lag or delay by the user, resulting
    in a poor user experience.
    2. Asynchronous calls. Asynchronous calls, on the other hand, do not block the code execution.
    Here Retrofit will execute the API call in a separate thread. When the response is received, the
    UI will be then updated.
    In order to make this kind of call, Retrofit needs a Callback.

    A Callback is an interface that is in charge of handling the response from the API. It will
    only be executed when the response from the API is received, and only then. The response
    can be successful or not, so we, as programmers, need to deal with that.
    If it was successful, then we can deal with the data it contains.

    Like all interfaces, Callback makes us implement 2 methods: onResponse and onFailure. Let's focus
    on onResponse(Call<T> call, Response<T> response).
        - Call<T>. It represents the request we are making to the API.
        - Response<T>. It represents the response we get from the API. In our case, it will contain
        the JSON holding all the data is created upon calling the API.

LiveData<T> is an observable class in Android that represents data that can change over time. It
is an object that is "observed" in the UI. So when the data changes, the UI will be updated with
the new data. By "observable" I mean just that; Fragments, Activities or ViewModels can observe
the data inside the LiveData object. When the data changes, the UI will be updated.

So let's say for example we have a LiveData<City>. If, for whatever reason, city.setCityName("New City")
is called, modifying the city object, the observer will be notified and the UI will be updated.

LiveData can only be modified from the ViewModel class that contains it or the Repository class. This
means that the user has no access to the LiveData class and therefore, modifying it.

MutableLivedata<T> is a subclass of LiveData that allows us to modify the data it contains through the
setValue() and postValue() methods. They both allow us to modify data from the object, but:
    - setValue(). We use it to modify data from the main thread. It will modify LiveData immediately and
    notify the observers of this change.
    - postValue(). We use it to modify data from a background thread. It will modify LiveData as well but
    on the background, and only notify the observers when is it safe.

Another important thing is that if we, for example, define a LiveData<T> in our ViewModel and modify
it in FragmentA and FragmentB, that same LiveData will be updated in both fragments. They share
the same instance, so to speak, of the object.

*/