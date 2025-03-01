package com.example.weebther.Database.Remote.RemoteModels;

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

    // Constructor required for manual instantiation
    public WeatherCondition(String description, String icon) {
        this.description = description;
        this.icon = icon;
    }

    // Default constructor required for Gson deserialization
    public WeatherCondition() {}

    // Returns the URL of the icon (API documentation specifies the format)
    public String getIconUrl() {
        return "https://openweathermap.org/img/wn/" + icon + "@2x.png";
    }
}
