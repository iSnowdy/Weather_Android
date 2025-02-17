package Database.Local.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

// Must be Serializable so we can pass objects inside a Bundle

@Entity(tableName = "cities")
public class City implements Serializable {
    @PrimaryKey(autoGenerate = false)
    private final Integer id;
    private String name;
    private String country;

    public City(Integer id, String name, String country) {
        this.id = id;
        this.name = name;
        this.country = country;
    }



    @Override
    public String toString() {
        return  "\n-------------City Information-------------" +
                "\nCity ID: " + id +
                "\nCity Name: " + name +
                "\nCity Country: " + country +
                "\n-----------------------------------------";
    }


    // Getters and Setters
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }
}
