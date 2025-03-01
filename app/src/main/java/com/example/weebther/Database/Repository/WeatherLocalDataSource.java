package com.example.weebther.Database.Repository;


import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.weebther.Database.Local.CityDAO;
import com.example.weebther.Database.Local.DatabaseManager;
import com.example.weebther.Database.Local.Entity.City;
import com.example.weebther.Database.Local.Entity.WeatherCurrentEntity;
import com.example.weebther.Database.Local.Entity.WeatherDailyEntity;
import com.example.weebther.Database.Local.Entity.WeatherHourlyEntity;
import com.example.weebther.Database.Local.WeatherDAO;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeatherLocalDataSource {
    private final WeatherDAO weatherDAO;
    private final CityDAO cityDAO;
    private final ExecutorService executorService;

    public WeatherLocalDataSource(Context context) {
        DatabaseManager db = DatabaseManager.getInstance(context);
        cityDAO = db.cityDAO();
        weatherDAO = db.weatherDAO();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void storeWeatherData(WeatherCurrentEntity current, List<WeatherHourlyEntity> hourly, List<WeatherDailyEntity> daily) {
        executorService.execute(() -> {
            weatherDAO.storeWeather(current);
            weatherDAO.storeHourlyWeather(hourly);
            weatherDAO.storeDailyWeather(daily);
        });
    }

    public LiveData<WeatherCurrentEntity> getLatestWeather(String cityName) {
        MutableLiveData<WeatherCurrentEntity> liveData = new MutableLiveData<>();
        executorService.execute(() -> {
            Optional<WeatherCurrentEntity> optionalWeather = weatherDAO.getLatestWeatherForCity(cityName);
            if (optionalWeather.isPresent()) {
                liveData.postValue(optionalWeather.get());

            } else {
                Log.w("WeatherLocalDataSource", "No weather data found for city: " + cityName);
                // Instead of throwing an exception so that we don't potentially crash the app
                liveData.postValue(null); //
            }
        });
        return liveData;
    }

    public LiveData<List<WeatherHourlyEntity>> getHourlyEntities(String cityName) {
        MutableLiveData<List<WeatherHourlyEntity>> liveData = new MutableLiveData<>();
        executorService.execute(() -> {
            List<WeatherHourlyEntity> hourlyData = weatherDAO.getHourlyForecast(cityName);
            liveData.postValue(hourlyData);
        });
        return liveData;
    }

    public LiveData<List<WeatherDailyEntity>> getDailyEntities(String cityName) {
        MutableLiveData<List<WeatherDailyEntity>> liveData = new MutableLiveData<>();
        executorService.execute(() -> {
            List<WeatherDailyEntity> dailyData = weatherDAO.getDailyForecast(cityName);
            liveData.postValue(dailyData);
        });
        return liveData;
    }

    public void toggleCityFavourite(String cityName, boolean isFavourite) {
        executorService.execute(() -> cityDAO.setCityAsFavourite(cityName, isFavourite));
    }

    public void updateLastAccessedCity(String cityName, long lastUpdated) {
        executorService.execute(() -> cityDAO.updateLastAccessed(cityName, lastUpdated));
    }

    public LiveData<List<City>> getRecentCities(int limit) {
        MutableLiveData<List<City>> liveData = new MutableLiveData<>();

        executorService.execute(() -> {
            List<City> cities = cityDAO.getCitiesByLastAccessed(limit);
            liveData.postValue(cities);
        });

        return liveData;
    }


    public void deleteCity(String cityName) {
        executorService.execute(() -> {
            cityDAO.deleteCityByName(cityName);
        });
    }

    public void deleteWeatherData(String cityName) {
        executorService.execute(() -> {
            weatherDAO.deleteWeatherForCity(cityName);
            weatherDAO.deleteHourlyWeatherForCity(cityName);
            weatherDAO.deleteDailyWeatherForCity(cityName);
        });
    }
}

