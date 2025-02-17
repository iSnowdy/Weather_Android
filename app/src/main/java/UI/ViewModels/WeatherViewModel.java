package UI.ViewModels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import Database.Local.Entity.City;
import Database.Local.Entity.WeatherCurrentEntity;
import Database.Local.Entity.WeatherDailyEntity;
import Database.Local.Entity.WeatherHourlyEntity;
import Database.Remote.GeoCodingCallBack;
import Database.Repository.GeoCodingRepository;
import Database.Repository.WeatherRepository;
import Domain.Models.WeatherResponse;
import Exceptions.GeoLocatorException;

/*
The ViewModel acts as an intermediary between the UI and the DB. This way we can keep the
Single Responsibility Principle. It will allow us, if we ever wanted to, change the database
and not affect the UI.
*/

public class WeatherViewModel extends AndroidViewModel {
    private final GeoCodingRepository geocodingRepository;
    private final WeatherRepository weatherRepository;

    private final MutableLiveData<Double> latitude = new MutableLiveData<>();
    private final MutableLiveData<Double> longitude = new MutableLiveData<>();
    private final MutableLiveData<GeoLocatorException> error = new MutableLiveData<>();
    private final MutableLiveData<WeatherResponse> weatherMutableLiveData = new MutableLiveData<>();

    public WeatherViewModel(Application application) {
        super(application);
        geocodingRepository = new GeoCodingRepository();
        weatherRepository = new WeatherRepository(application);
    }

    // Obtains a list of all cities inside the DB
    public LiveData<List<City>> getCitiesLiveData() {
        return weatherRepository.getCities();
    }

    public LiveData<Double> getLatitude() {
        return latitude;
    }
    public LiveData<Double> getLongitude() {
        return longitude;
    }

    public LiveData<GeoLocatorException> getError() {
        return error;
    }

    // Based on the city's name, it fetches the coordinates from the API
    public void fetchCoordinates(String cityName) {
        geocodingRepository.getCoordinates(cityName, new GeoCodingCallBack() {
            @Override
            public void onSuccess(double lat, double lon) {
                latitude.postValue(lat);
                longitude.postValue(lon);
            }

            @Override
            public void onError(GeoLocatorException geoLocatorException, Throwable throwable) {
                error.postValue(geoLocatorException);
            }
        });
    }

    // Obtains weather conditions from the API
    public void fetchWeather(String cityName, double latitude, double longitude) {
        weatherRepository.getWeather(cityName, latitude, longitude).observeForever(weatherMutableLiveData::setValue);
    }

    // Shows the LiveData to the UI so it can observe the changes in the weather
    public LiveData<WeatherResponse> getWeatherLiveData() {
        return weatherMutableLiveData;
    }

    // Obtains the weather from the DB instead of the API (previous method)
    public LiveData<WeatherCurrentEntity> getLatestWeatherForCity(String cityName) {
        return weatherRepository.getLatestWeatherForCity(cityName);
    }

    // Retrieves the hourly forecast from the DB
    public LiveData<List<WeatherHourlyEntity>> getHourlyForecast(String cityName) {
        return weatherRepository.getHourlyForeCast(cityName);
    }

    // Retrieves the daily forecast from the DB
    public LiveData<List<WeatherDailyEntity>> getDailyForecast(String cityName) {
        return weatherRepository.getDailyForeCast(cityName);
    }

    // Adds a new city to the DB
    public void addCity(City city) {
        weatherRepository.addCity(city);
    }

    // Deletes a city from the DB
    public void deleteCity(City city) {
        weatherRepository.deleteCity(city);
    }

    // Stores current weather conditions in the DB
    public void storeWeather(WeatherCurrentEntity weather) {
        weatherRepository.storeCurrentWeather(weather);
    }

    // Stores hourly forecast in the DB
    public void storeHourlyWeather(List<WeatherHourlyEntity> hourly) {
        weatherRepository.storeHourlyWeather(hourly);
    }

    // Stores daily forecast in the DB
    public void storeDailyWeather(List<WeatherDailyEntity> daily) {
        weatherRepository.storeDailyWeather(daily);
    }
}
