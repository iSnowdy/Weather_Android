package com.example.weebther.Database.Local.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "weather_daily")
public class WeatherDailyEntity {
    @PrimaryKey(autoGenerate = false)
    private long id;
    private String cityName;
    private long timestamp;
    private float tempMin;
    private float tempMax;
    private int humidity;
    private float probabilityOfPrecipitation;
    private float rain;
    private String weatherDescription;
    private String weatherIcon;

    public WeatherDailyEntity(String cityName, long timestamp, float tempMin, float tempMax,
                              int humidity, float probabilityOfPrecipitation, float rain, String weatherDescription, String weatherIcon) {
        this.cityName = cityName;
        this.timestamp = timestamp;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.humidity = humidity;
        this.probabilityOfPrecipitation = probabilityOfPrecipitation;
        this.rain = rain;
        this.weatherDescription = weatherDescription;
        this.weatherIcon = weatherIcon;

        this.id = (cityName + timestamp).hashCode();
        // Generates a unique ID based on the city and timestamp. Needed to have a combined PK to store historic data
    }

    // Getters y Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float getTempMin() {
        return tempMin;
    }

    public void setTempMin(float tempMin) {
        this.tempMin = tempMin;
    }

    public float getTempMax() {
        return tempMax;
    }

    public void setTempMax(float tempMax) {
        this.tempMax = tempMax;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public float getProbabilityOfPrecipitation() {
        return probabilityOfPrecipitation;
    }

    public void setProbabilityOfPrecipitation(float probabilityOfPrecipitation) {
        this.probabilityOfPrecipitation = probabilityOfPrecipitation;
    }

    public float getRain() {
        return rain;
    }

    public void setRain(float rain) {
        this.rain = rain;
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
}
