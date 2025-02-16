package Database.Local.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Represents "current" weather in the JSON file

@Entity(tableName = "weather_current")
public class WeatherCurrentEntity {
    // City ID is the PK for weather as well. This is done so each city has its own weather
    // Also, when setting it to false we will be replacing the old weather with the new one
    // If I did not do this, the DB would increase in size very fast
    @PrimaryKey(autoGenerate = false)
    private Integer cityID;
    private float temperature;
    private float feelsLike;
    private int pressure;
    private int humidity;
    private double uvi;
    private int visibility;
    private float windSpeed;
    private int windDeg;
    private String weatherDescription;
    private String weatherIcon;
    private long lastUpdated;


    public WeatherCurrentEntity(final Integer cityID, float temperature, float feelsLike,
                                int pressure, int humidity, double uvi, int visibility,
                                float windSpeed, int windDeg, String weatherDescription,
                                String weatherIcon, long lastUpdated) {
        this.cityID = cityID;
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.pressure = pressure;
        this.humidity = humidity;
        this.uvi = uvi;
        this.visibility = visibility;
        this.windSpeed = windSpeed;
        this.windDeg = windDeg;
        this.weatherDescription = weatherDescription;
        this.weatherIcon = weatherIcon;
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String toString() {
        return "\n-------------Weather Information-------------" +
                "\nCity ID: " + cityID +
                "\nTemperature: " + temperature + "°C" +
                "\nFeels Like: " + feelsLike + "°C" +
                "\nPressure: " + pressure + " hPa" +
                "\nHumidity: " + humidity + "%" +
                "\nUV Index: " + uvi +
                "\nVisibility: " + visibility + " m" +
                "\nWind Speed: " + windSpeed + " m/s" +
                "\nWind Direction: " + windDeg + "°" +
                "\nWeather Description: " + weatherDescription +
                "\nWeather Icon: " + weatherIcon +
                "\nLast Updated: " + lastUpdated +
                "\n-----------------------------------------";
    }

    // Getters y Setters
    public Integer getCityID() {
        return cityID;
    }

    public void setCityID(Integer cityID) {
        this.cityID = cityID;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getFeelsLike() {
        return feelsLike;
    }

    public void setFeelsLike(float feelsLike) {
        this.feelsLike = feelsLike;
    }

    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public double getUvi() {
        return uvi;
    }

    public void setUvi(double uvi) {
        this.uvi = uvi;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public float getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(float windSpeed) {
        this.windSpeed = windSpeed;
    }

    public int getWindDeg() {
        return windDeg;
    }

    public void setWindDeg(int windDeg) {
        this.windDeg = windDeg;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }

    public void setWeatherIcon(String weatherIcon) {
        this.weatherIcon = weatherIcon;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
