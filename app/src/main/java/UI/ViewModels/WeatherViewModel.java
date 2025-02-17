package UI.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import Database.Local.Entity.WeatherCurrentEntity;
import Database.Repository.WeatherRepository;
import Domain.Models.WeatherResponse;

public class WeatherViewModel extends AndroidViewModel {
    private final WeatherRepository weatherRepository;
    private final MutableLiveData<WeatherResponse> weatherMutableLiveData = new MutableLiveData<>();

    public WeatherViewModel(Application application) {
        super(application);
        weatherRepository = new WeatherRepository(application);
    }

    // Obtain all the information of the weather
    public void fetchWeather(int cityID, double latitude, double longitude) {
        weatherRepository.getWeather(cityID, latitude, longitude).observeForever(weatherMutableLiveData::setValue);
    }

    // Shows the LiveData to the UI
    public LiveData<WeatherResponse> getWeatherLiveData() {
        return weatherMutableLiveData;
    }

    // Obtains the weather from the DB instead of the API
    public LiveData<WeatherCurrentEntity> getLatestWeatherForCity(int cityID) {
        return weatherRepository.getLatestWeatherForCity(cityID);
    }
}
