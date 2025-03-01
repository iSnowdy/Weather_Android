package com.example.weebther.Database;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.weebther.Database.Local.DatabaseManager;
import com.example.weebther.Database.Local.WeatherDAO;

/*

WorkManager is an Android API to schedule periodic tasks in background.

It is supposed to keep executing even if the app is closed or the device restarts. The worker is
an asynchronous type of task that runs in the background.

We can add some constraints to it:
    - Only Wi-Fi to execute it (if needed).
    - Only when the device is not low on battery.

// TODO: Test this somehow?

*/

public class CleanUpWorker extends Worker {

    public CleanUpWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            DatabaseManager db = DatabaseManager.getInstance(getApplicationContext());
            WeatherDAO weatherDAO = db.weatherDAO();

            // Delete all weather data older than two weeks
            long twoWeeksAgoTimeStamp = System.currentTimeMillis() - (14 * 24 * 60 * 60 * 1000);
            weatherDAO.deleteOldWeather(twoWeeksAgoTimeStamp);
            weatherDAO.deleteOldHourlyWeather(twoWeeksAgoTimeStamp);
            weatherDAO.deleteOldDailyWeather(twoWeeksAgoTimeStamp);

            return Result.success();
        } catch (Exception e) {
            // Upon failure, attempt to retry it later on
            return Result.retry();
        }

    }
}