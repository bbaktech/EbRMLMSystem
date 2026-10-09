package com.example.earnbyrefer;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegActivity2 extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 100;
    private ImageView imageView;
    private ActivityResultLauncher<Intent> cameraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg2);

        imageView = findViewById(R.id.imagePAYMENT);
        Button btnCapture = findViewById(R.id.btnUploadPaymentImage);
        Button regBtn = (Button) findViewById(R.id.btnRegester);

        // Find the toolbar view and set it up
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Optional: Customize title programmatically
        if (getSupportActionBar() != null) {
            String appName = getString(R.string.app_name);
            getSupportActionBar().setTitle(appName);
        }

        // 1. Register the ActivityResultLauncher to handle the camera response
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        if (extras != null) {
                            Bitmap imageBitmap = (Bitmap) extras.get("data");
                            imageView.setImageBitmap(imageBitmap);
                        }
                    } else {
                        Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // 2. Click listener to check permissions and open camera
        btnCapture.setOnClickListener(v -> checkPermissionAndOpenCamera());
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //We start new
                Intent intent = new Intent(RegActivity2.this, Acknowledge.class);
                startForResultLauncher.launch(intent);
                finish();
            }
        });

    }
    private final ActivityResultLauncher<Intent> startForResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // 2. Handle the callback result
            }
    );

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_abtus) {
            Toast.makeText(this, "Contact WhatsApp:+91-9353205447", Toast.LENGTH_SHORT).show();
            // Handle settings click
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            // Request runtime permission if not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(cameraIntent);
    }

    // Handle runtime permission response
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission required to take photos.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}

