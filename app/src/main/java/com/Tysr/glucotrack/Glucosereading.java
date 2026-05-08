package com.Tysr.glucotrack;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
@Entity(tableName= "glucose_reading")
public class Glucosereading {
    @PrimaryKey(autoGenerate = true)
    public int id; // Kept for local Room compatibility if needed
    
    public String key; // Firebase unique key
    public String userId; // To identify which user owns this reading
    public float glucoseValue;
    public String timestamp;
    public String notes;
    public String status;
    public String aiTip;
    public String activitySuggestion;

    public Glucosereading() {
        // Default constructor required for calls to DataSnapshot.getValue(Glucosereading.class)
    }
}
