package com.example.weebther.Database.Local.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "weather_hourly")
public class WeatherHourlyEntity {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    private String cityName;
    private long timestamp;
    private float temperature;
    private int humidity;
    private String weatherDescription;

    public void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }

    public WeatherHourlyEntity(String cityName, long timestamp, float temperature, int humidity, String weatherDescription) {
        this.cityName = cityName;
        this.timestamp = timestamp;
        this.temperature = temperature;
        this.humidity = humidity;
        this.weatherDescription = weatherDescription;
    }

    // Getters y Setters
    @NonNull
    public String getCityName() {
        return cityName;
    }

    public void setCityName(@NonNull String cityName) {
        this.cityName = cityName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }
}
