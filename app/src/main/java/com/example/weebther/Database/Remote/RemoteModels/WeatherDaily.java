package com.example.weebther.Database.Remote.RemoteModels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

// I will need: dt | temp.min | temp.max | weather.icon

public class WeatherDaily {
    @SerializedName("dt")
    public long timestamp;
    @SerializedName("temp")
    public Temperature temperature;
    @SerializedName("humidity")
    public int humidity;
    @SerializedName("weather")
    public List<WeatherCondition> weatherConditions;

    // The static keyword allows us to instantiate Temperature without an instance of WeatherDaily
    public static class Temperature {
        @SerializedName("day")
        public double day;
        @SerializedName("min")
        public double min;
        @SerializedName("max")
        public double max;
    }
}
