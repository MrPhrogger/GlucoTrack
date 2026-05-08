package com.Tysr.glucotrack;



import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Delete;
import androidx.room.Query;
import java.util.List;


@Dao
public interface GlucoseDao {
    @Insert
    void insert(Glucosereading reading);
    @Delete
    void delete(Glucosereading reading);

    @Query("SELECT * FROM glucose_reading")
    List<Glucosereading> getAllReadings();
    @Query("SELECT * FROM glucose_reading WHERE id= :id")
    Glucosereading getReadingById(int id);

}
