package com.example.weebther.Database.Remote;

import com.example.weebther.Database.Remote.RemoteModels.WeatherResponse;
import com.example.weebther.Exceptions.OpenWeatherException;

public interface WeatherCallBack {
    void onSuccess(WeatherResponse weatherResponse);
    void onError(OpenWeatherException openWeatherException, Throwable throwable);
}
