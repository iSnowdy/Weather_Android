package Database.Local.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "weather_daily")
public class WeatherDailyEntity {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    private String cityName;
    private long timestamp;
    private double tempMin;
    private double tempMax;
    private int humidity;
    private String weatherDescription;

    public WeatherDailyEntity(String cityName, long timestamp, double tempMin, double tempMax, int humidity, String weatherDescription) {
        this.cityName = cityName;
        this.timestamp = timestamp;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.humidity = humidity;
        this.weatherDescription = weatherDescription;
    }

    // Getters y Setters
    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public double getTempMin() { return tempMin; }
    public void setTempMin(double tempMin) { this.tempMin = tempMin; }

    public double getTempMax() { return tempMax; }
    public void setTempMax(double tempMax) { this.tempMax = tempMax; }

    public int getHumidity() { return humidity; }
    public void setHumidity(int humidity) { this.humidity = humidity; }

    public String getWeatherDescription() { return weatherDescription; }
    public void setWeatherDescription(String weatherDescription) { this.weatherDescription = weatherDescription; }
}
