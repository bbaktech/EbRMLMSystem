package com.example.earnbyreferv10;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
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

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity1 extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_CODE = 100;
    ShapeableImageView imageView;
    MaterialButton UpAadhaarBtn;
    private ActivityResultLauncher<Intent> cameraLauncher;

    private TextInputEditText edtName, edtPhone, edtAddress, edtAccountNo, edtBankName, edtIfsc, edtAadhaar, edtReferralId;
    private DatabaseReference dealersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register1);

        // Adjust layout padding when soft keyboard (IME) or system bars appear
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            int bottomPadding = Math.max(systemBars.bottom, ime.bottom);
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomPadding);
            return insets;
        });

        // Initialize Firebase Realtime Database reference
        dealersRef = FirebaseDatabase.getInstance().getReference("Dealers");

        // Initialize Views
        imageView = findViewById(R.id.iv_aadhaar_screenshot);
        UpAadhaarBtn = findViewById(R.id.btnUploadAadhaar);

        edtName = findViewById(R.id.edtRegisterName);
        edtPhone = findViewById(R.id.edtRegisterphone);
        edtAddress = findViewById(R.id.edtRegisterAddrsss);
        edtAccountNo = findViewById(R.id.editsb_accNo);
        edtBankName = findViewById(R.id.edit_bankName);
        edtIfsc = findViewById(R.id.editIFSCcode);
        edtAadhaar = findViewById(R.id.editaadheer);
        edtReferralId = findViewById(R.id.refralId);

        Button btnNext = findViewById(R.id.btnNext);

        // Camera Launcher setup
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

        UpAadhaarBtn.setOnClickListener(v -> checkPermissionAndOpenCamera());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            String appName = getString(R.string.app_name);
            getSupportActionBar().setTitle(appName);
        }

        // Save inputs to Firebase Realtime Database under "Dealers" node when Next button is clicked
        btnNext.setOnClickListener(v -> saveDealerDataToFirebase());
    }

    private void saveDealerDataToFirebase() {
        String name = edtName.getText() != null ? edtName.getText().toString().trim() : "";
        String phone = edtPhone.getText() != null ? edtPhone.getText().toString().trim() : "";
        String address = edtAddress.getText() != null ? edtAddress.getText().toString().trim() : "";
        String accountNo = edtAccountNo.getText() != null ? edtAccountNo.getText().toString().trim() : "";
        String bankName = edtBankName.getText() != null ? edtBankName.getText().toString().trim() : "";
        String ifscCode = edtIfsc.getText() != null ? edtIfsc.getText().toString().trim() : "";
        String aadhaarNo = edtAadhaar.getText() != null ? edtAadhaar.getText().toString().trim() : "";
        String referralId = edtReferralId.getText() != null ? edtReferralId.getText().toString().trim() : "";

        if (TextUtils.isEmpty(name)) {
            edtName.setError("Full name is required");
            edtName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            edtPhone.setError("Phone number is required");
            edtPhone.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(referralId)) {
            edtReferralId.setError("Referral Id is required");
            edtReferralId.requestFocus();
            return;
        }

        // Validate that referralId belongs to a previously registered dealer in Firebase
        dealersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isValidReferral = false;
                String refCount = "";
                String introducerName = "";
                String refral1 = "";
                String refral2 ="";
                String refral3 = "";
                String passDID = "";
                String passKey = "";
                String dealer_status = "";
                if (snapshot.exists()) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        String key = child.getKey();
                        String dId = child.child("dealerId").getValue(String.class);
                        String dealerPhone = child.child("phone").getValue(String.class);
                        dealer_status = child.child("status").getValue(String.class);

                        if (Objects.equals(referralId, key) || Objects.equals(referralId, dId) || Objects.equals(referralId, dealerPhone)) {
                            isValidReferral = true;
                            passKey = key;
//                            passDID = dId != null ? dId : key;
                            String refName = child.child("name").getValue(String.class);
                            introducerName = refName != null ? refName : "";

                            refral1 = child.child("sub_ref1").getValue(String.class);
                            refral2 = child.child("sub_ref2").getValue(String.class);
                            refral3 = child.child("sub_ref3").getValue(String.class);

                            if (TextUtils.isEmpty(refral1)) {
                                refCount = "sub_ref1";
                            } else if (TextUtils.isEmpty(refral2)) {
                                refCount = "sub_ref2";
                            } else if (TextUtils.isEmpty(refral3)) {
                                refCount = "sub_ref3";
                            } else {
                                refCount = "";
                            }
                            break;
                        }
                    }
                }

                if (!isValidReferral) {
                    edtReferralId.setError("Invalid Referral ID or Dealer not registered in the system");
                    edtReferralId.requestFocus();
                    return;
                }

                if (Objects.equals("", refCount)) {
                    edtReferralId.setError("Referral ID Dealer has already referred 3 dealers");
                    edtReferralId.requestFocus();
                    return;
                }

                if (!Objects.equals("JOINED", dealer_status)) {
                    edtReferralId.setError("Referral ID/Dealer has been not Approved");
                    edtReferralId.requestFocus();
                    return;
                }

                // Generate new dealer key
                String pushKey = dealersRef.push().getKey();
                String dealerId = (pushKey != null) ? pushKey : String.valueOf(System.currentTimeMillis());

                // Create dealer data map
                Map<String, Object> dealerMap = new HashMap<>();
//              you coud change dealer id value to phone No with index
                dealerMap.put("dealerId", dealerId);
                dealerMap.put("name", name);
                dealerMap.put("phone", phone);
                dealerMap.put("address", address);
                dealerMap.put("accountNo", accountNo);
                dealerMap.put("bankName", bankName);
                dealerMap.put("ifscCode", ifscCode);
                dealerMap.put("aadhaarNo", aadhaarNo);
                dealerMap.put("referralId", referralId);
                dealerMap.put("referred_by_name", introducerName);
                dealerMap.put("paymentimage", "");
                dealerMap.put("aadhaarimage", "");
                dealerMap.put("status", "JOINED");
                dealerMap.put("status1", "");
                dealerMap.put("sub_ref1", "");
                dealerMap.put("sub_ref2", "");
                dealerMap.put("sub_ref3", "");
                dealerMap.put("join_date", "");
                dealerMap.put("exit_date", "");
                dealerMap.put("item_ids", "");
                dealerMap.put("pass_word", "1234");

                final String finalPassKey = passKey;
                final String finalRefCount = refCount;
//                final String finalPassDID = passDID;

                // Save data under "Dealers" node-- you could use ...KEY also if dealer id changed to phone
                dealersRef.child(dealerId).setValue(dealerMap)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(RegisterActivity1.this, "Dealer registered successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity1.this, MainActivity.class);

                            // update referal record for sub dealers i.e pushkey to refered dealer sub
                            // dId and the refCount need to be added to interused account
                            //dealersRef.child(dealerId).setValue
                            dealersRef.child(finalPassKey).child(finalRefCount).setValue(dealerId);
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e ->
                            Toast.makeText(RegisterActivity1.this, "Failed to save: " + e.getMessage(), Toast.LENGTH_LONG).show()
                        );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RegisterActivity1.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(cameraIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_abtus) {
            Toast.makeText(this, "Contact WhatsApp:+91-9353205447", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

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