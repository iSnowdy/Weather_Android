package com.example.weebther;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.weebther.Database.CleanUpWorker;

import java.util.concurrent.TimeUnit;

/**
 * Main activity that sets up navigation, toolbar, and background cleanup tasks.
 */
public class MainActivity extends AppCompatActivity {
    private static final String CLEAN_UP_WORK_TAG = "CleanUpWorker";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //this.deleteDatabase("weather_database"); // BE CAREFUL!! This deletes ALL data in the DB

        setContentView(R.layout.activity_main);

        setupToolbar();
        setupNavigation();
        setupCleanUpWorker();
    }

    /**
     * Sets up the toolbar.
     */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     * Sets up Navigation Component.
     */
    private void setupNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupActionBarWithNavController(this, navController);
        } else {
            Log.e("MainActivity", "NavHostFragment is null!");
        }
    }

    /**
     * Schedules a periodic cleanup task using WorkManager.
     */
    private void setupCleanUpWorker() {
        PeriodicWorkRequest cleanUpRequest = new PeriodicWorkRequest.Builder(
                CleanUpWorker.class,
                1, TimeUnit.DAYS
        ).build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                CLEAN_UP_WORK_TAG,
                ExistingPeriodicWorkPolicy.KEEP,
                cleanUpRequest
        );

        Log.d("MainActivity", "Scheduled CleanUpWorker to run every 24 hours.");
    }

    /**
     * Handles navigation up actions.
     *
     * @return True if navigation was handled, false otherwise.
     */
    @Override
    public boolean onSupportNavigateUp() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            return navController.navigateUp() || super.onSupportNavigateUp();
        }
        return super.onSupportNavigateUp();
    }
}