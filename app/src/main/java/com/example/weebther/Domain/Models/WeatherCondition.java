package com.example.weebther.Domain.Models;

import com.google.gson.annotations.SerializedName;

// Will need: description | icon
// Parse icon symbology to mine (https://openweathermap.org/weather-conditions#How-to-get-icon-URL)

public class WeatherCondition {
    @SerializedName("id")
    public int id;
    @SerializedName("main")
    public String main;
    @SerializedName("description")
    public String description;
    @SerializedName("icon")
    public String icon;
}
