package com.example.weebther.Database.Remote.RemoteModels;

import com.example.weebther.Database.Local.Entity.WeatherCurrentEntity;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import com.example.weebther.Database.Local.Entity.WeatherDailyEntity;
import com.example.weebther.Database.Local.Entity.WeatherHourlyEntity;

public class WeatherResponse {
    @SerializedName("lat")
    public double latitude;
    @SerializedName("long")
    public double longitude;
    @SerializedName("timezone")
    public String timezone;
    @SerializedName("timezone_offset")
    public int timezoneOffset;
    @SerializedName("current")
    public WeatherCurrent current;
    @SerializedName("hourly")
    public List<WeatherHourly> hourly;
    @SerializedName("daily")
    public List<WeatherDaily> daily;
    @SerializedName("alerts")
    public List<WeatherAlert> alerts;


    // Converts the API hourly forecast to data we can read; meaning we can store it in the DB as
    // entities we build ourselves: WeatherHourlyEntity for Room
    public List<WeatherHourlyEntity> getHourlyEntities(String cityName) {
        List<WeatherHourlyEntity> hourlyEntities = new ArrayList<>();
        if (hourly != null) {
            for (WeatherHourly weatherHoulyApi : hourly) {
                hourlyEntities.add(new WeatherHourlyEntity(
                        cityName,
                        weatherHoulyApi.timestamp,
                        weatherHoulyApi.temperature,
                        weatherHoulyApi.humidity,
                        weatherHoulyApi.probabilityOfPrecipitation,
                        weatherHoulyApi.weatherConditions.get(0).description
                ));
            }
        }
        return hourlyEntities;
    }

    // Same but with WeatherDailyEntity
    public List<WeatherDailyEntity> getDailyEntities(String cityName) {
        List<WeatherDailyEntity> dailyEntities = new ArrayList<>();
        if (daily != null) {
            for (WeatherDaily weatherDailyApi : daily) {
                dailyEntities.add(new WeatherDailyEntity(
                        cityName,
                        weatherDailyApi.timestamp,
                        weatherDailyApi.temperature.min,
                        weatherDailyApi.temperature.max,
                        weatherDailyApi.humidity,
                        weatherDailyApi.probabilityOfPrecipitation,
                        weatherDailyApi.rain,
                        weatherDailyApi.weatherConditions.get(0).description
                ));
            }

        }
        return dailyEntities;
    }

    // Converts this object to a WeatherCurrentEntity
    public WeatherCurrentEntity getCurrentEntity(String cityName) {
        return new WeatherCurrentEntity(
                cityName,
                current.temperature,
                current.feelsLike,
                current.humidity,
                current.windSpeed,
                current.weatherConditions.get(0).description,
                current.weatherConditions.get(0).icon,
                System.currentTimeMillis()
        );
    }
}
