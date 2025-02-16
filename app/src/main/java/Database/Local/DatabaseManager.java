package Database.Local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import Database.Local.Entity.City;
import Database.Local.Entity.WeatherCurrentEntity;
import Database.Local.Entity.WeatherDailyEntity;
import Database.Local.Entity.WeatherHourlyEntity;

@Database(entities = {WeatherCurrentEntity.class, City.class, WeatherHourlyEntity.class, WeatherDailyEntity.class}, version = 1)
public abstract class DatabaseManager extends RoomDatabase {
    // Need a Singleton to avoid multiple instances of this class and volatile
    // to ensure that all changes to the DB are visible to multiple threads
    // This is important because we will be working with multiple threads
    private static volatile DatabaseManager databaseInstance;

    // This will allow us to access the methods inside the DAO without we having to instantiate them
    public abstract CityDAO cityDAO();

    public abstract WeatherDAO weatherDAO();

    public static DatabaseManager getInstance(Context context) {
        if (databaseInstance == null) {
            synchronized (DatabaseManager.class) {
                if (databaseInstance == null) {
                    databaseInstance =
                            Room.databaseBuilder(context.getApplicationContext(),
                                            DatabaseManager.class, "weather_database")
                                    .fallbackToDestructiveMigration() // This will allow room to destructively replace tables, if needed
                                    .build();
                }
            }
        }
        return databaseInstance;
    }


}
