package Domain.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherDaily {
    @SerializedName("dt")
    public long timestamp;
    @SerializedName("temp")
    public Temperature temperature;
    @SerializedName("humidity")
    public int humidity;
    @SerializedName("weather")
    public List<WeatherCondition> weatherConditions;

    public class Temperature {
        @SerializedName("day")
        public double day;
        @SerializedName("min")
        public double min;
        @SerializedName("max")
        public double max;
    }

}
