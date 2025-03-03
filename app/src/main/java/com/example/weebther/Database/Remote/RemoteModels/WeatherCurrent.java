package com.example.weebther.Database.Remote.RemoteModels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherCurrent {
    @SerializedName("dt")
    public long timestamp;
    @SerializedName("temp")
    public float temperature;
    @SerializedName("feels_like")
    public float feelsLike;
    @SerializedName("pressure")
    public int pressure;
    @SerializedName("humidity")
    public int humidity;
    @SerializedName("uvi")
    public float uvi;
    @SerializedName("visibility")
    public int visibility;
    @SerializedName("wind_speed")
    public float windSpeed;
    @SerializedName("wind_deg")
    public int windDeg;
    @SerializedName("wind_gust")
    public float windGust;
    @SerializedName("weather")
    public List<WeatherCondition> weatherConditions;
}
