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
        Log.d("WeatherLocalDataSource", "Storing weather data for city: " + current.getCityName() + ". Temp: " + current.getTemperature());
        executorService.execute(() -> {
            weatherDAO.storeWeather(current);
            weatherDAO.storeHourlyWeather(hourly);
            weatherDAO.storeDailyWeather(daily);
        });
    }

    public LiveData<WeatherCurrentEntity> getLatestWeather(String cityName) {
        return weatherDAO.getLatestWeatherForCity(cityName);
    }

    public LiveData<List<WeatherHourlyEntity>> getHourlyEntities(String cityName) {
        return weatherDAO.getHourlyForecast(cityName);
    }

    public LiveData<List<WeatherDailyEntity>> getDailyEntities(String cityName) {
        return weatherDAO.getDailyForecast(cityName);
    }

    public void toggleCityFavourite(String cityName, boolean isFavourite) {
        executorService.execute(() -> cityDAO.setCityAsFavourite(cityName, isFavourite));
    }

    public void updateLastAccessedCity(String cityName, long lastUpdated) {
        executorService.execute(() -> cityDAO.updateLastAccessed(cityName, lastUpdated));
    }

    public LiveData<City> getCity(String cityName) {
        return cityDAO.getCity(cityName);
    }

    public LiveData<List<City>> getRecentCities(int limit) {
        return cityDAO.getCitiesByLastAccessed(limit);
    }

    public LiveData<List<City>> getFavouriteCities() {
        return cityDAO.getFavouriteCities();
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

