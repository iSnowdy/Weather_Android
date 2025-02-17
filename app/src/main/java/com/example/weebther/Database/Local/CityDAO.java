package com.example.weebther.Database.Local;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import com.example.weebther.Database.Local.Entity.City;

// Room will generate the implementation for this interface
// We can only get long for the id and int or the affected tuples as a return signature

@Dao
public interface CityDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long storeCity(City city);
    @Update
    int updateCity(City city);
    @Delete
    int deleteCity(City city);
    @Query("SELECT * FROM cities WHERE id = :id")
    City getCity(Integer id);
    @Query("SELECT * FROM cities")
    List<City> getCities();
}
