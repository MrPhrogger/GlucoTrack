package com.Tysr.glucotrack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private GlucoseAdapter adapter;
    private DatabaseReference userReadingsRef;
    private ValueEventListener readingsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
            return;
        }

        // Initialize Firebase Reference for this specific user
        userReadingsRef = FirebaseDatabase.getInstance().getReference("readings").child(user.getUid());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerViewReadings);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new GlucoseAdapter(this::showDeleteConfirmation);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabAddReading);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddReadingActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.buttonLogout).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
            finish();
        });

        loadReadingsFromFirebase();
    }

    private void loadReadingsFromFirebase() {
        readingsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Glucosereading> readings = new ArrayList<>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Glucosereading reading = postSnapshot.getValue(Glucosereading.class);
                    if (reading != null) {
                        readings.add(reading);
                    }
                }
                // Reverse list to show newest first
                java.util.Collections.reverse(readings);
                adapter.setReadings(readings);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load readings: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        userReadingsRef.addValueEventListener(readingsListener);
    }

    private void showDeleteConfirmation(Glucosereading reading) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Reading")
                .setMessage("Are you sure you want to delete this glucose reading?")
                .setPositiveButton("Delete", (dialog, which) -> deleteReadingFromFirebase(reading))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteReadingFromFirebase(Glucosereading reading) {
        if (reading.key != null) {
            userReadingsRef.child(reading.key).removeValue().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Toast.makeText(this, "Delete failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userReadingsRef != null && readingsListener != null) {
            userReadingsRef.removeEventListener(readingsListener);
        }
    }
}
