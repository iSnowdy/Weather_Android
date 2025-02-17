package UI.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import Database.Local.Entity.WeatherCurrentEntity;
import Database.Remote.GeoCodingCallBack;
import Database.Repository.GeoCodingRepository;
import Database.Repository.WeatherRepository;
import Domain.Models.WeatherResponse;
import Exceptions.GeoLocatorException;

public class WeatherViewModel extends AndroidViewModel {
    private final GeoCodingRepository geocodingRepository;
    private final MutableLiveData<Double> latitude = new MutableLiveData<>();
    private final MutableLiveData<Double> longitude = new MutableLiveData<>();
    private final MutableLiveData<GeoLocatorException> error = new MutableLiveData<>();
    private final WeatherRepository weatherRepository;
    private final MutableLiveData<WeatherResponse> weatherMutableLiveData = new MutableLiveData<>();

    public WeatherViewModel(Application application) {
        super(application);
        geocodingRepository = new GeoCodingRepository();
        weatherRepository = new WeatherRepository(application);
    }

    public LiveData<Double> getLatitude() {
        return latitude;
    }

    public LiveData<Double> getLongitude() {
        return longitude;
    }

    public LiveData<GeoLocatorException> getError() {
        return error; // TODO ?
    }

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

    // Obtain all the information of the weather
    public void fetchWeather(String cityName, double latitude, double longitude) {
        weatherRepository.getWeather(cityName, latitude, longitude).observeForever(weatherMutableLiveData::setValue);
    }

    // Shows the LiveData to the UI
    public LiveData<WeatherResponse> getWeatherLiveData() {
        return weatherMutableLiveData;
    }

    // Obtains the weather from the DB instead of the API
    public LiveData<WeatherCurrentEntity> getLatestWeatherForCity(String cityName) {
        return weatherRepository.getLatestWeatherForCity(cityName);
    }
}
