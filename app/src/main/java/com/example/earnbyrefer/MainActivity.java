package com.example.earnbyrefer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Access SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        boolean isFirstTime = sharedPreferences.getBoolean("isFirstTime", true);

//        if (isFirstTime) {
//
//            // 2. Direct the user to Activity One
//            Intent intent = new Intent(MainActivity.this, RegActivity.class);
//
//            startForResultLauncher.launch(intent);
//
//            // 3. Terminate ActivityTwo so it is removed from the back stack
//            finish();
//            return;
//        }

        // --- Normal Application Logic for Activity Two ---

        setContentView(R.layout.activity_main);
        Button checkout_btn = findViewById(R.id.checkout_acc);

        // Find the toolbar view and set it up
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Optional: Customize title programmatically
        if (getSupportActionBar() != null) {
            String appName = getString(R.string.app_name);
            getSupportActionBar().setTitle(appName);
        }
        checkout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //We start new
                CloseAccount();
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
            // Handle settings click
            Toast.makeText(this, "Contact WhatsApp:+91-9353205447", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void CloseAccount(){
        Toast.makeText(this,"Your Id will be Closed, Amount will be credited to your Account with in 3 working Days", Toast.LENGTH_SHORT).show();
    }

}