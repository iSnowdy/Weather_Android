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
    @SerializedName("pop")
    public float probabilityOfPrecipitation;
    @SerializedName("rain")
    public float rain;

    // The static keyword allows us to instantiate Temperature without an instance of WeatherDaily
    public static class Temperature {
        @SerializedName("day")
        public float day;
        @SerializedName("min")
        public float min;
        @SerializedName("max")
        public float max;
    }
}
