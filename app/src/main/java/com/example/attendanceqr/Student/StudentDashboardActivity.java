package com.example.attendanceqr.Student;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.attendanceqr.LoginActivity;
import com.example.attendanceqr.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StudentDashboardActivity extends AppCompatActivity {

    Button btnScanQR, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnScanQR = findViewById(R.id.btnScanQR);
        btnLogout = findViewById(R.id.btnLogout);

        btnScanQR.setOnClickListener(v -> {
            checkAttendanceWindow();
        });

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void checkAttendanceWindow() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("AttendanceStatus");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean isActive = snapshot.child("isActive").getValue(Boolean.class);
                Long start = snapshot.child("startTime").getValue(Long.class);
                Long end = snapshot.child("endTime").getValue(Long.class);
                long now = System.currentTimeMillis();

                if (isActive != null && isActive && start != null && end != null && now >= start && now <= end) {
                    // ✅ Valid time — start QR scanner
                    startActivity(new Intent(StudentDashboardActivity.this, ScanQRActivity.class));

                } else {
                    // ❌ Outside schedule
                    Toast.makeText(StudentDashboardActivity.this, "Attendance window is not active!", Toast.LENGTH_SHORT).show();
                    finish(); // optional: close activity
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

}