package com.example.weebther.Database.Remote.RemoteModels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

// Use this for hourly forecast
// Will need: dt

public class WeatherHourly {
    @SerializedName("dt")
    public long timestamp;
    @SerializedName("temp")
    public float temperature;
    @SerializedName("humidity")
    public int humidity;
    @SerializedName("weather")
    public List<WeatherCondition> weatherConditions;
    @SerializedName("pop")
    public float probabilityOfPrecipitation;
}
