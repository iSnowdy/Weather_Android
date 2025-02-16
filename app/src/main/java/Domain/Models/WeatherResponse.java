package Domain.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

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
}
