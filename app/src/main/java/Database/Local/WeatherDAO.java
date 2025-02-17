package Database.Local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import Database.Local.Entity.WeatherCurrentEntity;
import Database.Local.Entity.WeatherDailyEntity;
import Database.Local.Entity.WeatherHourlyEntity;

@Dao
public interface WeatherDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void storeWeather(WeatherCurrentEntity weather);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void storeHourlyWeather(List<WeatherHourlyEntity> hourly);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void storeDailyWeather(List<WeatherDailyEntity> daily);

    @Query("SELECT * FROM weather_current WHERE cityName = :cityName LIMIT 1")
    WeatherCurrentEntity getLatestWeatherForCity(String cityName);

    @Query("SELECT * FROM weather_hourly WHERE cityName = :cityName ORDER BY timestamp ASC")
    List<WeatherHourlyEntity> getHourlyForecast(String cityName);

    @Query("SELECT * FROM weather_daily WHERE cityName = :cityName ORDER BY timestamp ASC")
    List<WeatherDailyEntity> getDailyForecast(String cityName);
}
