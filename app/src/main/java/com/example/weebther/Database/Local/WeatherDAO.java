package com.example.weebther.Database.Local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;
import java.util.Optional;

import com.example.weebther.Database.Local.Entity.WeatherCurrentEntity;
import com.example.weebther.Database.Local.Entity.WeatherDailyEntity;
import com.example.weebther.Database.Local.Entity.WeatherHourlyEntity;

@Dao
public interface WeatherDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void storeWeather(WeatherCurrentEntity weather);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void storeHourlyWeather(List<WeatherHourlyEntity> hourly);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void storeDailyWeather(List<WeatherDailyEntity> daily);

    @Query("SELECT * FROM weather_current WHERE cityName = :cityName LIMIT 1")
    LiveData<WeatherCurrentEntity> getLatestWeatherForCity(String cityName);

    @Query("SELECT * FROM weather_hourly WHERE cityName = :cityName ORDER BY timestamp ASC")
    LiveData<List<WeatherHourlyEntity>> getHourlyForecast(String cityName);

    @Query("SELECT * FROM weather_daily WHERE cityName = :cityName ORDER BY timestamp ASC")
    LiveData<List<WeatherDailyEntity>> getDailyForecast(String cityName);

    // "Normal" DELETE methods
    @Query("DELETE FROM weather_current WHERE cityName = :cityName")
    void deleteWeatherForCity(String cityName);

    @Query("DELETE FROM weather_hourly WHERE cityName = :cityName")
    void deleteHourlyWeatherForCity(String cityName);

    @Query("DELETE FROM weather_daily WHERE cityName = :cityName")
    void deleteDailyWeatherForCity(String cityName);

    // DELETE methods for the Worker to use
    @Query("DELETE FROM weather_current WHERE lastUpdated < :timeStampToDelete")
    int deleteOldWeather(long timeStampToDelete);

    @Query("DELETE FROM weather_hourly WHERE timestamp < :timeStampToDelete")
    int deleteOldHourlyWeather(long timeStampToDelete);

    @Query("DELETE FROM weather_daily WHERE timestamp < :timeStampToDelete")
    int deleteOldDailyWeather(long timeStampToDelete);
}
