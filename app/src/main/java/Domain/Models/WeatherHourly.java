package Domain.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherHourly {
    @SerializedName("dt")
    public long timestamp;
    @SerializedName("temp")
    public double temperature;
    @SerializedName("humidity")
    public int humidity;
    @SerializedName("weather")
    public List<WeatherCondition> weatherConditions;
}
