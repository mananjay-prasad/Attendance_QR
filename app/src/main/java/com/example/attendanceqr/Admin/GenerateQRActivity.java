package com.example.attendanceqr.Admin;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.attendanceqr.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class GenerateQRActivity extends AppCompatActivity {

    ImageView imgQRCode;
    Button btnGenerateQR, btnEnableQR, btnDisableQR;
    TextView tvGeneratedInfo;
    String fixedQRData = "college_xyz_attendance"; // 🔐 FIXED QR CONTENT
    DatabaseReference qrStatusRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_generate_qractivity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imgQRCode = findViewById(R.id.imgQRCode);
        btnGenerateQR = findViewById(R.id.btnGenerateQR);
        btnEnableQR = findViewById(R.id.btnEnableQR);
        btnDisableQR = findViewById(R.id.btnDisableQR);
        tvGeneratedInfo = findViewById(R.id.tvGeneratedInfo);

        qrStatusRef = FirebaseDatabase.getInstance().getReference("qr_status");

        btnGenerateQR.setOnClickListener(v -> {
            try {
                Bitmap qrBitmap = generateQRCode(fixedQRData);
                imgQRCode.setImageBitmap(qrBitmap);
                tvGeneratedInfo.setText("QR Code:\n" + fixedQRData);
            } catch (WriterException e) {
                Toast.makeText(this, "QR Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private Bitmap generateQRCode(String text) throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();
        int width = 512, height = 512;

        BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, width, height);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? android.graphics.Color.BLACK : android.graphics.Color.WHITE);
            }
        }

        return bitmap;
    }
}