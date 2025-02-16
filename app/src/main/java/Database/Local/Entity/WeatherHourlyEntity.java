package Database.Local.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "weather_hourly")
public class WeatherHourlyEntity {
    @PrimaryKey(autoGenerate = false)
    private int cityID;
    private long timestamp;
    private double temperature;
    private int humidity;
    private String weatherDescription;

    public WeatherHourlyEntity(int cityID, long timestamp, double temperature, int humidity, String weatherDescription) {
        this.cityID = cityID;
        this.timestamp = timestamp;
        this.temperature = temperature;
        this.humidity = humidity;
        this.weatherDescription = weatherDescription;
    }

    // Getters y Setters
    public int getCityID() { return cityID; }
    public void setCityID(int cityID) { this.cityID = cityID; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public int getHumidity() { return humidity; }
    public void setHumidity(int humidity) { this.humidity = humidity; }

    public String getWeatherDescription() { return weatherDescription; }
    public void setWeatherDescription(String weatherDescription) { this.weatherDescription = weatherDescription; }
}
