package com.example.attendanceqr.AdminReport;

import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.attendanceqr.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import com.itextpdf.layout.Document;


import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdminReportActivity extends AppCompatActivity {

    private Button btnExportPdf;
    private List<AttendanceModel> reportList = new ArrayList<>();
    private DatabaseReference attendanceRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_report);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnExportPdf = findViewById(R.id.btnExportPdf);

        attendanceRef = FirebaseDatabase.getInstance().getReference("Attendance");

        fetchAttendance();

        btnExportPdf.setOnClickListener(v -> {
            try {
                createPdfReport();
            } catch (Exception e) {
                Toast.makeText(this, "PDF Export Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAttendance() {
        attendanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reportList.clear();

                for (DataSnapshot dateSnap : snapshot.getChildren()) {
                    for (DataSnapshot studentSnap : dateSnap.getChildren()) {
                        AttendanceModel model = studentSnap.getValue(AttendanceModel.class);
                        reportList.add(model);
                    }
                }

                Toast.makeText(AdminReportActivity.this, "Attendance Loaded: " + reportList.size(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminReportActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createPdfReport() throws Exception {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
        File file = new File(path, "AttendanceReport.pdf");
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Attendance Report").setBold().setFontSize(18));
        document.add(new Paragraph("Generated on: " + new Date().toString()).setFontSize(12));
        document.add(new Paragraph("\n"));


        Table table = new Table(4);
        table.addCell("Date");
        table.addCell("Student Name");
        table.addCell("UID");
        table.addCell("Time");

        for (AttendanceModel record : reportList) {
            table.addCell(record.getDate());
            table.addCell(record.getName());
            table.addCell(record.getUid());
            table.addCell(record.getTime());
        }

        document.add(table);
        document.close();

        Toast.makeText(this, "PDF saved at: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
    }
}