package com.example.weebther.Database.Local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import java.util.Optional;

import com.example.weebther.Database.Local.Entity.City;

// Room will generate the implementation for this interface
// We can only get long for the id and int or the affected tuples as a return signature

@Dao
public interface CityDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long storeCity(City city);

    @Update
    int updateCity(City city);

    @Query("DELETE FROM cities WHERE name = :cityName")
    int deleteCityByName(String cityName);


    @Query("UPDATE cities SET isFavourite = :isFavourite WHERE name = :cityName")
    int setCityAsFavourite(String cityName, boolean isFavourite);


    @Query("UPDATE cities SET lastUpdated = :lastUpdated WHERE name = :cityName")
    int updateLastAccessed(String cityName, long lastUpdated);

    // Retrieves the last x amount of cities from the DB
    @Query("SELECT * FROM cities ORDER BY lastUpdated DESC LIMIT :limit")
    LiveData<List<City>> getCitiesByLastAccessed(int limit);

    @Query("SELECT * FROM cities WHERE name = :cityName")
    LiveData<City> getCity(String cityName);

    @Query("SELECT * FROM cities")
    LiveData<List<City>> getCities();
}
