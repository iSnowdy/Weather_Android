package Database.Repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import Database.Local.DatabaseManager;
import Database.Local.Entity.WeatherCurrentEntity;
import Database.Local.WeatherDAO;
import Database.Remote.WeatherApiClient;
import Database.Remote.WeatherService;
import Domain.Models.WeatherResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherRepository {
    private final WeatherDAO weatherDAO;
    private final WeatherService weatherService;
    private final String UNITS = "metric";
    private final String WEATHER_APP_STRING = "6b8b4505e3072038c80bd7000d8c4202"; // TODO: Change this to private settings

    public WeatherRepository(Context context) {
        DatabaseManager db = DatabaseManager.getInstance(context);
        this.weatherDAO = db.weatherDAO();
        this.weatherService = WeatherApiClient.getRetrofit().create(WeatherService.class);
        //this.WEATHER_APP_STRING = context.getString(R.string.WEATHER_APP_STRING);
    }

    public LiveData<WeatherResponse> getWeather(String cityName, double latitude, double longitude) {
        MutableLiveData<WeatherResponse> weatherMutableLiveData = new MutableLiveData<>();

        weatherService.getWeather(latitude, longitude, WEATHER_APP_STRING, UNITS).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();
                    weatherMutableLiveData.postValue(weatherResponse);

                    WeatherCurrentEntity weatherCurrentEntity = new WeatherCurrentEntity(
                            cityName,
                            (float) weatherResponse.current.temperature,
                            (float) weatherResponse.current.feelsLike,
                            weatherResponse.current.humidity,
                            (float) weatherResponse.current.windSpeed,
                            weatherResponse.current.weatherConditions.get(0).description,
                            weatherResponse.current.weatherConditions.get(0).icon,
                            weatherResponse.current.timestamp
                    );

                    // Store the weather data in the local database in the background
                    new Thread(() -> weatherDAO.storeWeather(weatherCurrentEntity)).start();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });

        return weatherMutableLiveData;
    }

    public LiveData<WeatherCurrentEntity> getLatestWeatherForCity(String cityName) {
        MutableLiveData<WeatherCurrentEntity> weatherMutableLiveData = new MutableLiveData<>();
        new Thread(() -> {
            WeatherCurrentEntity weather = weatherDAO.getLatestWeatherForCity(cityName);
            weatherMutableLiveData.postValue(weather);
        }).start();
        return weatherMutableLiveData;
    }
}
