package com.example.weebther.Database.Local.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "weather_hourly")
public class WeatherHourlyEntity {
    @PrimaryKey(autoGenerate = false)
    private long id;
    private String cityName;
    private long timestamp;
    private float temperature;
    private int humidity;
    private float probabilityOfPrecipitation;
    private String weatherDescription;

    public WeatherHourlyEntity(String cityName, long timestamp, float temperature, int humidity, float probabilityOfPrecipitation, String weatherDescription) {
        this.cityName = cityName;
        this.timestamp = timestamp;
        this.temperature = temperature;
        this.humidity = humidity;
        this.probabilityOfPrecipitation = probabilityOfPrecipitation;
        this.weatherDescription = weatherDescription;

        // Generates a unique ID based on the city and timestamp. Needed to have a combined PK to store historic data
        this.id = (cityName + timestamp).hashCode();
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

    public float getProbabilityOfPrecipitation() {
        return probabilityOfPrecipitation;
    }

    public void setProbabilityOfPrecipitation(float probabilityOfPrecipitation) {
        this.probabilityOfPrecipitation = probabilityOfPrecipitation;
    }


    public String getWeatherDescription() {
        return weatherDescription;
    }

    public void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }
}
