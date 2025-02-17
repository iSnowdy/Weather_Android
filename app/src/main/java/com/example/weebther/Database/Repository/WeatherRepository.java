package com.example.weebther.Database.Repository;

import android.content.Context;

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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherRepository {
    private final CityDAO cityDAO;
    private final WeatherDAO weatherDAO;
    private final WeatherService weatherService;
    private final String UNITS = "metric";
    private final String WEATHER_APP_STRING = "6b8b4505e3072038c80bd7000d8c4202"; // TODO: Change this to private settings

    private final ExecutorService executorService;

    public WeatherRepository(Context context) {
        DatabaseManager db = DatabaseManager.getInstance(context);
        this.cityDAO = db.cityDAO();
        this.weatherDAO = db.weatherDAO();
        this.weatherService = WeatherApiClient.getRetrofit().create(WeatherService.class);
        this.executorService = Executors.newSingleThreadExecutor();
        //this.WEATHER_APP_STRING = context.getString(R.string.WEATHER_APP_STRING);
    }

    public LiveData<WeatherResponse> getWeather(String cityName, double latitude, double longitude) {
        MutableLiveData<WeatherResponse> weatherMutableLiveData = new MutableLiveData<>();

        weatherService.getWeather(latitude, longitude, WEATHER_APP_STRING, UNITS).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();
                    weatherMutableLiveData.postValue(weatherResponse);

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
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
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
