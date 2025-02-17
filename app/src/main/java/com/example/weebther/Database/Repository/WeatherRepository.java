package com.example.weebther.Database.Repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.weebther.Database.Local.CityDAO;
import com.example.weebther.Database.Local.DatabaseManager;
import com.example.weebther.Database.Local.Entity.City;
import com.example.weebther.Database.Local.Entity.WeatherCurrentEntity;
import com.example.weebther.Database.Local.Entity.WeatherDailyEntity;
import com.example.weebther.Database.Local.Entity.WeatherHourlyEntity;
import com.example.weebther.Database.Local.WeatherDAO;
import com.example.weebther.Database.Remote.WeatherApiClient;
import com.example.weebther.Database.Remote.WeatherService;
import com.example.weebther.Domain.Models.WeatherResponse;
import com.example.weebther.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

public class WeatherRepository {
    private final CityDAO cityDAO;
    private final WeatherDAO weatherDAO;
    private final WeatherService weatherService;
    private final String UNITS = "metric";
    private final String WEATHER_APP_STRING;

    private final ExecutorService executorService;

    public WeatherRepository(Context context) {
        DatabaseManager db = DatabaseManager.getInstance(context);
        this.cityDAO = db.cityDAO();
        this.weatherDAO = db.weatherDAO();
        this.weatherService = WeatherApiClient.getRetrofit().create(WeatherService.class);
        this.executorService = Executors.newSingleThreadExecutor();
        this.WEATHER_APP_STRING = context.getString(R.string.WEATHER_API_KEY);

        Log.d("WeatherRepository", "WeatherRepository created");
        Log.d("WeatherRepository", "Weather API Key: " + WEATHER_APP_STRING);

    }

    public LiveData<WeatherResponse> getWeather(String cityName, double latitude, double longitude) {
        MutableLiveData<WeatherResponse> weatherMutableLiveData = new MutableLiveData<>();

        Log.d("WeatherRepository", "Fetching weather for city: " + cityName + ". Latitude: " + latitude + ". Longitude: " + longitude);

        weatherService.getWeather(latitude, longitude, WEATHER_APP_STRING, UNITS).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();
                    weatherMutableLiveData.postValue(weatherResponse);

                    Log.d("WeatherRepository", "Weather fetched for city: " + cityName + ". Temperature: " + weatherResponse.current.temperature + "ºC .");

                    WeatherCurrentEntity weatherCurrentEntity = new WeatherCurrentEntity(
                            cityName,
                            (float) weatherResponse.current.temperature,
                            (float) weatherResponse.current.feelsLike,
                            weatherResponse.current.humidity,
                            (float) weatherResponse.current.windSpeed,
                            weatherResponse.current.weatherConditions.get(0).description,
                            weatherResponse.current.weatherConditions.get(0).icon,
                            weatherResponse.current.timestamp
                    );

                    // Stores the current weather in the DB
                    executorService.execute(() -> weatherDAO.storeWeather(weatherCurrentEntity));
                    // Stores the hourly forecast in the DB
                    executorService.execute(() -> weatherDAO.storeHourlyWeather(weatherResponse.getHourlyEntities(cityName)));
                    // Stores the daily forecast in the DB
                    executorService.execute(() -> weatherDAO.storeDailyWeather(weatherResponse.getDailyEntities(cityName)));
                } else {
                    Log.e("WeatherRepository", "Error while fetching weather for city: " + cityName);
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.e("WeatherRepository", "Error while fetching weather for city: " + cityName + ". Error: " + t.getMessage(), t);
                t.printStackTrace();
            }
        });

        return weatherMutableLiveData;
    }

    /*
        DB CRUD
     */

    // Retrieves the latest current weather from DB
    public LiveData<WeatherCurrentEntity> getLatestWeatherForCity(String cityName) {
        MutableLiveData<WeatherCurrentEntity> weatherMutableLiveData = new MutableLiveData<>();
        executorService.execute(() -> {
            WeatherCurrentEntity weather = weatherDAO.getLatestWeatherForCity(cityName);
            weatherMutableLiveData.postValue(weather);
        });
        return weatherMutableLiveData;
    }

    // Retrieves hourly forecast from DB
    public LiveData<List<WeatherHourlyEntity>> getHourlyForeCast(String cityName) {
        MutableLiveData<List<WeatherHourlyEntity>> hourlyMutableLiveData = new MutableLiveData<>();
        executorService.execute(() -> {
            List<WeatherHourlyEntity> hourly = weatherDAO.getHourlyForecast(cityName);
            hourlyMutableLiveData.postValue(hourly);
        });
        return hourlyMutableLiveData;
    }

    // Retrieves daily forecast from DB
    public LiveData<List<WeatherDailyEntity>> getDailyForeCast(String cityName) {
        MutableLiveData<List<WeatherDailyEntity>> dailyMutableLiveData = new MutableLiveData<>();
        executorService.execute(() -> {
            List<WeatherDailyEntity> daily = weatherDAO.getDailyForecast(cityName);
            dailyMutableLiveData.postValue(daily);
        });
        return dailyMutableLiveData;
    }

    public void addCity(City city) {
        executorService.execute(() -> cityDAO.storeCity(city));
    }

    public void deleteCity(City city) {
        executorService.execute(() -> cityDAO.deleteCity(city));
    }

    // We need to convert the List<City> to LiveData<List<City>> in order to be able to observe it
    // in real time. So if any new city is added or deleted, the UI will be updated automatically.
    public LiveData<List<City>> getCities() {
        MutableLiveData<List<City>> citiesMutableLiveData = new MutableLiveData<>();
        executorService.execute(() -> {
            List<City> cities = cityDAO.getCities();
            citiesMutableLiveData.postValue(cities);
        });
        return citiesMutableLiveData;
    }

    public void storeCurrentWeather(WeatherCurrentEntity weather) {
        executorService.execute(() -> weatherDAO.storeWeather(weather));
    }

    public void storeHourlyWeather(List<WeatherHourlyEntity> hourly) {
        executorService.execute(() -> weatherDAO.storeHourlyWeather(hourly));
    }

    public void storeDailyWeather(List<WeatherDailyEntity> daily) {
        executorService.execute(() -> weatherDAO.storeDailyWeather(daily));
    }
}
