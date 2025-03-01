package com.example.weebther.Exceptions;

public class OpenWeatherException extends RuntimeException {
    public OpenWeatherException(String message) {
        super(message);
    }
    public OpenWeatherException(String message, Throwable throwable) {
      super(message, throwable);
    }
}
