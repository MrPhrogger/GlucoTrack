package com.Tysr.glucotrack;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddReadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_reading);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.add_reading_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextInputEditText editTextValue = findViewById(R.id.editTextGlucoseValue);
        TextInputEditText editTextNotes = findViewById(R.id.editTextNotes);
        TextInputEditText editTextStatus = findViewById(R.id.editTextStatus);
        Button buttonSave = findViewById(R.id.buttonSave);

        buttonSave.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                Toast.makeText(this, "Error: User not logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            String valueStr = editTextValue.getText().toString();
            if (valueStr.isEmpty()) {
                Toast.makeText(this, "Please enter a glucose value", Toast.LENGTH_SHORT).show();
                return;
            }

            Glucosereading reading = new Glucosereading();
            reading.userId = user.getUid();
            reading.glucoseValue = Float.parseFloat(valueStr);
            reading.notes = editTextNotes.getText().toString();
            reading.status = editTextStatus.getText().toString();
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            reading.timestamp = sdf.format(new Date());

            generateSmartTips(reading);

            // Save to Firebase Realtime Database
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("readings").child(user.getUid());
            String key = ref.push().getKey();
            reading.key = key;

            if (key != null) {
                ref.child(key).setValue(reading).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to save: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void generateSmartTips(Glucosereading reading) {
        float glucose = reading.glucoseValue;
        String status = reading.status.toLowerCase();
        String notes = reading.notes.toLowerCase();

        if (glucose < 70) {
            reading.aiTip = "Hypoglycemia (Low blood sugar) detected.";
            reading.activitySuggestion = "Follow the 15-15 rule: Eat 15g of fast carbs and recheck in 15 mins.";
            return;
        }

        if (status.contains("fasting")) {
            if (glucose < 100) {
                reading.aiTip = "Normal fasting range.";
                reading.activitySuggestion = "Your body is processing glucose well. Keep it up!";
            } else if (glucose < 126) {
                reading.aiTip = "Prediabetes range (Fasting).";
                reading.activitySuggestion = "Try to reduce refined carbs in your next meal.";
            } else {
                reading.aiTip = "Diabetes range (Fasting).";
                reading.activitySuggestion = "Consult your doctor. High fasting levels often need medical review.";
            }
        } else if (status.contains("after") || status.contains("meal") || status.contains("post")) {
            if (glucose < 140) {
                reading.aiTip = "Excellent post-meal control.";
                reading.activitySuggestion = "Your meal choices were great for your sugar levels.";
            } else if (glucose < 200) {
                reading.aiTip = "Elevated post-meal glucose.";
                reading.activitySuggestion = "Consider a 15-minute light walk to help lower sugar.";
            } else {
                reading.aiTip = "High post-meal glucose spike.";
                reading.activitySuggestion = "Monitor for symptoms and check your hydration.";
            }
        } else {
            if (glucose <= 140) {
                reading.aiTip = "Glucose is within a healthy target range.";
                reading.activitySuggestion = "Stay consistent with your routine.";
            } else {
                reading.aiTip = "Glucose is above normal targets.";
                reading.activitySuggestion = "Reflect on recent food intake or stress levels.";
            }
        }

        if (notes.contains("exercise") || notes.contains("gym") || notes.contains("run")) {
            if (glucose < 110) {
                reading.aiTip = "Low glucose for active exercise.";
                reading.activitySuggestion = "Eat a small snack to prevent a drop during your workout.";
            }
        }
        
        if (notes.contains("sick") || notes.contains("fever") || notes.contains("cold")) {
            reading.aiTip = "Illness can raise glucose levels.";
            reading.activitySuggestion = "Stay hydrated and follow your 'sick day' medical protocol.";
        }

        if (notes.contains("stress") || notes.contains("tired")) {
            reading.aiTip = "Cortisol from stress can spike sugar.";
            reading.activitySuggestion = "Try 5 minutes of deep breathing or a short rest.";
        }
    }
}
