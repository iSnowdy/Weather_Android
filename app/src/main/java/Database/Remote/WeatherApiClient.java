package Database.Remote;

/*

Retrofit is a library that allows you to make HTTP requests in Android. It greatly simplifies
communication with APIs REST.

It will allow us to execute HTTP requests such as GET, POST, PUT and DELETE, handle HTTP responses
of JSON type automatically and convert them to Java objects.

Retrofit will act as a layer for us. Instead of using HttpURLConnection, native in Java, Retrofit
will handle the communication and convert the response to a Java object.

*/

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherApiClient {
    private static final String BASE_API_URL = "https://api.openweathermap.org/data/3.0/";
    private static Retrofit retrofit = null;

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_API_URL)
                    .addConverterFactory(GsonConverterFactory.create()) // Transforms JSON -> Java Object
                    .build();
        }
        return retrofit;
    }
}
