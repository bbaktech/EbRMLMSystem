package com.example.earnbyreferv10;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button btnPayUpi, btnUploadScreenshot;

    private ImageView ivScreenshotPreview;
    private TextView tvOrderStatus;
    private Bitmap screenshotBitmap;

    // Change these values to match your specific bank details
    private final String UPI_ID = "7975714672@ybl";
    private final String MERCHANT_NAME = "BBAK Technologies";
    private final String TRANSACTION_NOTE = "#1024";
    private final String AMOUNT = "1.00";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Find the toolbar view and set it up
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Optional: Customize title programmatically
        if (getSupportActionBar() != null) {
            String appName = getString(R.string.app_name);
            getSupportActionBar().setTitle(appName);
        }

        btnPayUpi = findViewById(R.id.buttonPayViaUPI);
        btnUploadScreenshot = findViewById(R.id.buttonConformReg);
        ivScreenshotPreview = findViewById(R.id.iv_screenshot_preview);
//        tvOrderStatus = findViewById(R.id.tv_order_status);

        // 1. Launch UPI Deep Link
        btnPayUpi.setOnClickListener(v -> launchUPIIntent());

        // 2. Select Receipt Image
        btnUploadScreenshot.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);

        });
    }

    private void launchUPIIntent() {
        // Build standard UPI URL format
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", UPI_ID)
                .appendQueryParameter("pn", MERCHANT_NAME)
                .appendQueryParameter("tn", TRANSACTION_NOTE)
                .appendQueryParameter("am", AMOUNT)
                .appendQueryParameter("cu", "INR")
                .appendQueryParameter("tr", "1010")
                .appendQueryParameter("mc", "0000")
                .build();

        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

        // Show choice of available UPI apps (GPay, PhonePe, Paytm)
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with...");
        if (null != chooser.resolveActivity(getPackageManager())) {
            startActivity(chooser);
        } else {
            Toast.makeText(this, "No UPI app found on this device", Toast.LENGTH_SHORT).show();
        }
    }
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    try {
                        screenshotBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        ivScreenshotPreview.setImageBitmap(screenshotBitmap);
                        ivScreenshotPreview.setVisibility(View.VISIBLE);

                        // Proceed to Automated System Verification
//                        runSystemVerification(screenshotBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
    );

    // VERIFICATION 1: Automated System Check (OCR Scanner)
    private void runSystemVerification(Bitmap bitmap) {
        tvOrderStatus.setText("Status: Processing Automated Verification...");
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        recognizer.process(image)
                .addOnSuccessListener(visionText -> {
                    String fullText = visionText.getText().toLowerCase();

                    // Logic to see if the screenshot belongs to a transaction of your business
                    boolean containsMerchant = fullText.contains(MERCHANT_NAME.toLowerCase()) || fullText.contains("successfully");
                    boolean containsAmount = fullText.contains("499");

                    if (containsMerchant && containsAmount) {
                        // System pass, move to manual stage
                        tvOrderStatus.setText("Status: System Verified. Awaiting Manual Admin Release.");
                        tvOrderStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));

                        // TODO: Send imageUri and extracted Transaction metadata to your server/Firebase here
//                        uploadToAdminPanel();
                    } else {
                        // System fail
                        tvOrderStatus.setText("Status: Verification Failed. Invalid receipt text detected.");
                        tvOrderStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Failed to parse text from image.", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_abtus) {
            // Handle settings click
            Toast.makeText(this, "Contact WhatsApp:+91-9353205447", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
