package Database.Local.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "weather_daily")
public class WeatherDailyEntity {
    @PrimaryKey(autoGenerate = false)
    private int cityID;
    private long timestamp;
    private double tempMin;
    private double tempMax;
    private int humidity;
    private String weatherDescription;

    public WeatherDailyEntity(int cityID, long timestamp, double tempMin, double tempMax, int humidity, String weatherDescription) {
        this.cityID = cityID;
        this.timestamp = timestamp;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.humidity = humidity;
        this.weatherDescription = weatherDescription;
    }

    // Getters y Setters
    public int getCityID() { return cityID; }
    public void setCityID(int cityID) { this.cityID = cityID; }

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
