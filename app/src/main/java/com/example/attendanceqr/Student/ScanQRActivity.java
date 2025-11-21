package com.example.attendanceqr.Student;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.attendanceqr.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ScanQRActivity extends AppCompatActivity {


    private Button scanButton;
    private DatabaseReference scheduleRef;
    private String scheduledTime = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scan_qractivity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        scanButton = findViewById(R.id.scanButton);

        DatabaseReference scheduleRef = FirebaseDatabase.getInstance().getReference("AttendanceSchedule");

        scheduleRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String startTime = snapshot.child("start").getValue(String.class);
                    String endTime = snapshot.child("end").getValue(String.class);

                    if (startTime != null && endTime != null) {
                        if (isWithinScheduledTime(startTime, endTime)) {
                            // Proceed to scan
                        } else {
                            Toast.makeText(ScanQRActivity.this, "Attendance not allowed at this time.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ScanQRActivity.this, "Schedule not available", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ScanQRActivity.this, "Failed to get schedule", Toast.LENGTH_SHORT).show();
            }
        });

        // Button click
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scheduledTime.isEmpty()) {
                    Toast.makeText(ScanQRActivity.this, "Schedule not loaded yet", Toast.LENGTH_SHORT).show();
                    return;
                }

//                if (isWithinScheduledTime()) {
//                    startQRScanner();
//                } else {
//                    Toast.makeText(ScanQRActivity.this, "Attendance not active now", Toast.LENGTH_SHORT).show();
//                }
            }
        });
    }

    private boolean isWithinScheduledTime(String startTime, String endTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());

            Date start = sdf.parse(startTime);
            Date end = sdf.parse(endTime);
            Date now = new Date();

            return now.after(start) && now.before(end);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void startQRScanner() {
        IntentIntegrator integrator = new IntentIntegrator(ScanQRActivity.this);
        integrator.setPrompt("Scan the QR Code");
        integrator.setOrientationLocked(true);
        integrator.setBeepEnabled(true);
        integrator.initiateScan();
    }
}