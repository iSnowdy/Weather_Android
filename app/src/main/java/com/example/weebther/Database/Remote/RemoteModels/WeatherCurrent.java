package com.example.weebther.Database.Remote.RemoteModels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherCurrent {
    @SerializedName("dt")
    public long timestamp;
    @SerializedName("temp")
    public double temperature;
    @SerializedName("feels_like")
    public double feelsLike;
    @SerializedName("pressure")
    public int pressure;
    @SerializedName("humidity")
    public int humidity;
    @SerializedName("uvi")
    public double uvi;
    @SerializedName("visibility")
    public int visibility;
    @SerializedName("wind_speed")
    public double windSpeed;
    @SerializedName("wind_deg")
    public int windDeg;
    @SerializedName("wind_gust")
    public double windGust;
    @SerializedName("weather")
    public List<WeatherCondition> weatherConditions;
}
