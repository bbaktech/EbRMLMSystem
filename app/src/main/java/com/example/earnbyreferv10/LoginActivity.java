package com.example.earnbyreferv10;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText edtDealerId, edtPassword;
    private DatabaseReference dealersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            String appName = getString(R.string.app_name);
            getSupportActionBar().setTitle(appName);
        }

        dealersRef = FirebaseDatabase.getInstance().getReference("Dealers");

        edtDealerId = findViewById(R.id.edtdealerid);
        edtPassword = findViewById(R.id.edtPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvRegisterRedirect = findViewById(R.id.tvRegisterRedirect);

        btnLogin.setOnClickListener(v -> performLogin());

        tvRegisterRedirect.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity1.class);
            startActivity(intent);
        });
    }

    private void performLogin() {
        String dealerIdInput = edtDealerId.getText() != null ? edtDealerId.getText().toString().trim() : "";
        String passwordInput = edtPassword.getText() != null ? edtPassword.getText().toString().trim() : "";

        if (TextUtils.isEmpty(dealerIdInput)) {
            edtDealerId.setError("Dealer ID or Phone is required");
            edtDealerId.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(passwordInput)) {
            edtPassword.setError("Password is required");
            edtPassword.requestFocus();
            return;
        }

        // Validate credentials against Firebase "Dealers" node
        dealersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isUserFound = false;
                if (snapshot.exists()) {
                    for (DataSnapshot dealerSnapshot : snapshot.getChildren()) {
                        String key = dealerSnapshot.getKey();
                        String dId = dealerSnapshot.child("dealerId").getValue(String.class);
                        String phone = dealerSnapshot.child("phone").getValue(String.class);
                        String dbPassword = dealerSnapshot.child("pass_word").getValue(String.class);

                        boolean matchId = Objects.equals(dealerIdInput, key) || Objects.equals(dealerIdInput, dId) || Objects.equals(dealerIdInput, phone);
                        boolean matchPassword = Objects.equals(dbPassword, passwordInput);

                        if (matchId && matchPassword) {
                            isUserFound = true;
                            String name = dealerSnapshot.child("name").getValue(String.class);
                            String introducerName = dealerSnapshot.child("referred_by_name").getValue(String.class);
                            String introducerId = dealerSnapshot.child("referralId").getValue(String.class);

                            Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(LoginActivity.this, DetailsActivity.class);
                            intent.putExtra("dealerId", dId != null ? dId : key);
                            intent.putExtra("userName", name);
                            intent.putExtra("phone", phone);
                            intent.putExtra("introducerName", introducerName);
                            intent.putExtra("introducerId", introducerId);
                            startActivity(intent);
                            finish();
                            break;
                        }
                    }
                }

                if (!isUserFound) {
                    Toast.makeText(LoginActivity.this, "Invalid Dealer ID or Password", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
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
            Toast.makeText(this, "Contact WhatsApp:+91-9353205447", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}