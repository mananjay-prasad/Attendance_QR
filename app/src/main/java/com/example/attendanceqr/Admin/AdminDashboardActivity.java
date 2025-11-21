package com.example.attendanceqr.Admin;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.attendanceqr.AdminReport.AdminReportActivity;
import com.example.attendanceqr.AttendanceRecord.ViewAttendanceActivity;
import com.example.attendanceqr.LoginActivity;
import com.example.attendanceqr.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AdminDashboardActivity extends AppCompatActivity {

    Button btnGenerateQR, btnLogout, btnViewAttendance;
    private Button selectDateButton, selectTimeButton, saveScheduleButton;
    private TextView selectedScheduleText;
    private Calendar startDateTime = Calendar.getInstance();
    private Calendar endDateTime = Calendar.getInstance();
    private DatabaseReference scheduleRef;
    private boolean isSelectingStartTime = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnGenerateQR = findViewById(R.id.btnGenerateQR);
        btnLogout = findViewById(R.id.btnLogout);
        btnViewAttendance = findViewById(R.id.btnViewAttendance);
        selectDateButton = findViewById(R.id.selectDateButton);
        selectTimeButton = findViewById(R.id.selectTimeButton);
        saveScheduleButton = findViewById(R.id.saveScheduleButton);
        selectedScheduleText = findViewById(R.id.selectedScheduleText);


        btnGenerateQR.setOnClickListener(v -> {
            startActivity(new Intent(this, GenerateQRActivity.class));
        });


        btnViewAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, ViewAttendanceActivity.class);
                startActivity(intent);
            }
        });


        findViewById(R.id.btnViewReport).setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminReportActivity.class));
        });


        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        scheduleRef = FirebaseDatabase.getInstance().getReference("AttendanceSchedule");

        selectDateButton.setOnClickListener(v -> {
            isSelectingStartTime = true;
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                startDateTime.set(Calendar.YEAR, year);
                startDateTime.set(Calendar.MONTH, month);
                startDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                    startDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    startDateTime.set(Calendar.MINUTE, minute);
                    updateText();
                }, startDateTime.get(Calendar.HOUR_OF_DAY), startDateTime.get(Calendar.MINUTE), false);
                timePickerDialog.show();
            }, startDateTime.get(Calendar.YEAR), startDateTime.get(Calendar.MONTH), startDateTime.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        selectTimeButton.setOnClickListener(v -> {
            isSelectingStartTime = false;
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                endDateTime.set(Calendar.YEAR, year);
                endDateTime.set(Calendar.MONTH, month);
                endDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                    endDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    endDateTime.set(Calendar.MINUTE, minute);
                    updateText();
                }, endDateTime.get(Calendar.HOUR_OF_DAY), endDateTime.get(Calendar.MINUTE), false);
                timePickerDialog.show();
            }, endDateTime.get(Calendar.YEAR), endDateTime.get(Calendar.MONTH), endDateTime.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        saveScheduleButton.setOnClickListener(v -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
            String startTime = sdf.format(startDateTime.getTime());
            String endTime = sdf.format(endDateTime.getTime());

            if (startDateTime.after(endDateTime)) {
                Toast.makeText(this, "End time must be after start time", Toast.LENGTH_SHORT).show();
                return;
            }

            HashMap<String, String> scheduleMap = new HashMap<>();
            scheduleMap.put("start", startTime);
            scheduleMap.put("end", endTime);

            scheduleRef.setValue(scheduleMap).addOnSuccessListener(aVoid ->
                    Toast.makeText(this, "Schedule saved successfully", Toast.LENGTH_SHORT).show()
            );
        });
    }
    private void updateText() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        selectedScheduleText.setText("Start: " + sdf.format(startDateTime.getTime()) +
                "\nEnd: " + sdf.format(endDateTime.getTime()));
    }

}