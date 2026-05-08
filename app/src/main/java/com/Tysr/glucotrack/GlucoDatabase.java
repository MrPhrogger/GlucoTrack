package com.Tysr.glucotrack;

import androidx.room.RoomDatabase;
import androidx.room.Database;

import androidx.room.Room;
import android.content.Context;

@Database(entities = {Glucosereading.class}, version = 1, exportSchema = false)
public abstract class GlucoDatabase extends RoomDatabase {
    public abstract GlucoseDao glucoseDao();

    private static volatile GlucoDatabase INSTANCE;

    public static GlucoDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (GlucoDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    GlucoDatabase.class, "gluco_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
