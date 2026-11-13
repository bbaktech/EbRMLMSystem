package com.example.earnbyreferv10;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class ItemPurchase extends AppCompatActivity {


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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_item_purchase);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            String appName = getString(R.string.app_name);
            getSupportActionBar().setTitle(appName);
        }


        // Initialize Views
        TextView tvMrp1 = findViewById(R.id.tvMrp1);
        TextView tvMrp2 = findViewById(R.id.tvMrp2);
        TextView tvMrp3 = findViewById(R.id.tvMrp3);
        TextView tvMrp4 = findViewById(R.id.tvMrp4);

        // Add strikethrough effect to MRP texts
        if (tvMrp1 != null) tvMrp1.setPaintFlags(tvMrp1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        if (tvMrp2 != null) tvMrp2.setPaintFlags(tvMrp2.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        if (tvMrp3 != null) tvMrp3.setPaintFlags(tvMrp3.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        if (tvMrp4 != null) tvMrp4.setPaintFlags(tvMrp4.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        Button btnBuy1 = findViewById(R.id.btnBuy1);
        Button btnBuy2 = findViewById(R.id.btnBuy2);
        Button btnBuy3 = findViewById(R.id.btnBuy3);
        Button btnBuy4 = findViewById(R.id.btnBuy4);
        Button btnPurchaseAll = findViewById(R.id.btnPurchaseAll);
        Button btnClose = findViewById(R.id.btnClose);

        // Click actions for individual item buy buttons
        if (btnBuy1 != null) {
            btnBuy1.setOnClickListener(v -> Toast.makeText(this, "Product 1 selected", Toast.LENGTH_SHORT).show());
        }
        if (btnBuy2 != null) {
            btnBuy2.setOnClickListener(v -> Toast.makeText(this, "Product 2 selected", Toast.LENGTH_SHORT).show());
        }
        if (btnBuy3 != null) {
            btnBuy3.setOnClickListener(v -> Toast.makeText(this, "Product 3 selected", Toast.LENGTH_SHORT).show());
        }
        if (btnBuy4 != null) {
            btnBuy4.setOnClickListener(v -> Toast.makeText(this, "Product 4 selected", Toast.LENGTH_SHORT).show());
        }

        // Action for main purchase button
        if (btnPurchaseAll != null) {
            btnPurchaseAll.setOnClickListener(v -> Toast.makeText(this, "Proceeding to checkout", Toast.LENGTH_SHORT).show());
        }

        // Action for close button
        if (btnClose != null) {
            btnClose.setOnClickListener(v -> finish());
        }
    }
}