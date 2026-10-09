package com.example.earnbyrefer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    private EditText etUserId, etPassword;
    private Button btnLogin;
    private TextView tvRegisterRedirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Find the toolbar view and set it up
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Optional: Customize title programmatically
        if (getSupportActionBar() != null) {
            String appName = getString(R.string.app_name);
            getSupportActionBar().setTitle(appName);
        }
        // Initialize UI Elements
        etUserId = findViewById(R.id.etID);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegisterRedirect = findViewById(R.id.tvRegisterRedirect);
// Handle Login Action
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUserId.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // Simple Input Validation
                if (TextUtils.isEmpty(username)) {
                    etUserId.setError("Username is required");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    etPassword.setError("Password is required");
                    return;
                }

                // Place authentication logic here (e.g., Firebase, SQLite, API calls)
                if (username.equals("XXXXX") && password.equals("1234")) {
                    // Intent to move to home screen:
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Handle Redirection to Registration Page for New Users
        tvRegisterRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigates to RegisterActivity
                Intent intent = new Intent(LoginActivity.this, RegActivity.class);
                startActivity(intent);
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
            // Handle settings click
            Toast.makeText(this, "Contact WhatsApp:+91-9353205447", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}