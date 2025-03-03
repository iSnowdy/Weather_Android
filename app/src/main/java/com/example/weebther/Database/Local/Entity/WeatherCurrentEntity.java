package com.example.weebther.Database.Local.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Represents "current" weather in the JSON file

@Entity(tableName = "weather_current")
public class WeatherCurrentEntity {
    // City ID is the PK for weather. This is done so each city has its own weather
    // Also, when setting it to false we will be replacing the old weather with the new one
    // If I did not do this, the DB would increase in size very fast
    @PrimaryKey(autoGenerate = false)
    @NonNull
    private String cityName;
    private float temperature;
    private float feelsLike;
    private int humidity;
    private float uvi;
    private float windSpeed;
    private String weatherDescription;
    private String weatherIcon;
    private long lastUpdated;

    public WeatherCurrentEntity(final String cityName, float temperature,
                                float feelsLike, int humidity, float uvi,
                                float windSpeed, String weatherDescription,
                                String weatherIcon, long lastUpdated) {
        this.cityName = cityName;
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.humidity = humidity;
        this.uvi = uvi;
        this.windSpeed = windSpeed;
        this.weatherDescription =
                weatherDescription.substring(0, 1).toUpperCase() + weatherDescription.substring(1);
        this.weatherIcon = weatherIcon;
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String toString() {
        return "\n-------------Weather Information-------------" +
                "\nCity: " + cityName +
                "\nTemperature: " + temperature + "°C" +
                "\nFeels Like: " + feelsLike + "°C" +
                "\nHumidity: " + humidity + "%" +
                "\nUV Index: " + uvi +
                "\nWind Speed: " + windSpeed + " m/s" +
                "\nWeather Description: " + weatherDescription +
                "\nWeather Icon: " + weatherIcon +
                "\nLast Updated: " + lastUpdated +
                "\n-----------------------------------------";
    }

    // Getters y Setters
    public String getCityName() {
        return cityName;
    }

    public void setCityID(String cityName) {
        this.cityName = cityName;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getFeelsLike() {
        return feelsLike;
    }

    public void setFeelsLike(float feelsLike) {
        this.feelsLike = feelsLike;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public float getUvi() {
        return uvi;
    }

    public void setUvi(float uvi) {
        this.uvi = uvi;
    }

    public float getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(float windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }

    public void setWeatherIcon(String weatherIcon) {
        this.weatherIcon = weatherIcon;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
