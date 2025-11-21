package com.example.attendanceqr.Admin;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.attendanceqr.R;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class ScheduleAttendanceActivity extends AppCompatActivity {


    Button selectStartTime, selectEndTime, saveSchedule;
    Calendar startCal = Calendar.getInstance();
    Calendar endCal = Calendar.getInstance();

    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_schedule_attendance);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        selectStartTime = findViewById(R.id.selectStartTime);
        selectEndTime = findViewById(R.id.selectEndTime);
        saveSchedule = findViewById(R.id.saveSchedule);

        selectStartTime.setOnClickListener(v -> showDateTimePicker(startCal, selectStartTime));
        selectEndTime.setOnClickListener(v -> showDateTimePicker(endCal, selectEndTime));

        saveSchedule.setOnClickListener(v -> {
            if (startCal.after(endCal)) {
                Toast.makeText(this, "End time must be after start time", Toast.LENGTH_SHORT).show();
                return;
            }

            HashMap<String, String> scheduleMap = new HashMap<>();
            scheduleMap.put("start", format.format(startCal.getTime()));
            scheduleMap.put("end", format.format(endCal.getTime()));

            FirebaseDatabase.getInstance().getReference("AttendanceSchedule")
                    .setValue(scheduleMap)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Schedule saved successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to save schedule", Toast.LENGTH_SHORT).show());
        });
    }

    private void showDateTimePicker(Calendar calendar, Button button) {
        DatePickerDialog datePicker = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    TimePickerDialog timePicker = new TimePickerDialog(this,
                            (view1, hourOfDay, minute) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                button.setText(format.format(calendar.getTime()));
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true);
                    timePicker.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }
}