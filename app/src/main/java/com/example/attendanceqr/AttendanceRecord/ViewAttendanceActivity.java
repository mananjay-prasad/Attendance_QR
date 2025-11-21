package com.example.attendanceqr.AttendanceRecord;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendanceqr.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ViewAttendanceActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    EditText etSearch;
    TextView tvDate;
    List<AttendanceRecord> attendanceList = new ArrayList<>();
    AttendanceAdapter adapter;
    String todayDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_attendance);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerAttendance);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        etSearch = findViewById(R.id.etSearch);
        tvDate = findViewById(R.id.tvDate);

        adapter = new AttendanceAdapter(attendanceList);
        recyclerView.setAdapter(adapter);

        todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        tvDate.setText("Date: " + todayDate);

        fetchAttendance(todayDate);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void fetchAttendance(String date) {
        FirebaseDatabase.getInstance().getReference("Attendance").child(date)
                .get().addOnSuccessListener(snapshot -> {
                    if (!snapshot.exists()) {
                        Toast.makeText(this, "No attendance found for today.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    attendanceList.clear();

                    for (DataSnapshot userSnap : snapshot.getChildren()) {
                        String uid = userSnap.getKey();
                        String time = userSnap.child("time").getValue(String.class);

                        FirebaseDatabase.getInstance().getReference("Students").child(uid)
                                .get().addOnSuccessListener(studentSnap -> {
                                    String name = studentSnap.child("name").getValue(String.class);
                                    attendanceList.add(new AttendanceRecord(date, uid, time, name));
                                    adapter.notifyDataSetChanged();
                                });
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load attendance", Toast.LENGTH_SHORT).show();
                });
    }

}