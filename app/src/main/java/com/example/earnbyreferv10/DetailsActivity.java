package com.example.earnbyreferv10;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class DetailsActivity extends AppCompatActivity {

    private TextView tvTotalSubDealers, tvLevel1SubDealers;
    private Button btnViewGenealogicalTree;
    private String currentDealerId = "";
    private String fullHierarchyTreeText = "Loading sub-dealers network...";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_details);

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
        TextView tvUserName = findViewById(R.id.tvUserName);
        TextView tvUserId = findViewById(R.id.tvUserId);
        TextView tvIntroducerName = findViewById(R.id.tvIntroducerName);
        TextView tvIntroducerId = findViewById(R.id.tvIntroducerId);
        TextView tvAvailableBalance = findViewById(R.id.tvAvailableBalance);
        tvTotalSubDealers = findViewById(R.id.tvTotalSubDealers);
        tvLevel1SubDealers = findViewById(R.id.tvLevel1SubDealers);

        Button btnWithdraw = findViewById(R.id.btnWithdraw);
        Button btnPurchase = findViewById(R.id.btnPurchase);
        btnViewGenealogicalTree = findViewById(R.id.btn_V_g_tree);
        Button btnCloseApp = findViewById(R.id.btnCloseApp);

        // Populate intent extras if passed from LoginActivity
        Intent intent = getIntent();
        if (intent != null) {
            String userName = intent.getStringExtra("userName");
            String dealerId = intent.getStringExtra("dealerId");
            String introducerName = intent.getStringExtra("introducerName");
            String introducerId = intent.getStringExtra("introducerId");
            String phone = intent.getStringExtra("phone");

            if (dealerId != null && !dealerId.isEmpty()) {
                currentDealerId = dealerId;
                if (tvUserId != null) tvUserId.setText(dealerId);
            }
            if (userName != null && !userName.isEmpty() && tvUserName != null) tvUserName.setText(userName);
            if (introducerName != null && !introducerName.isEmpty() && tvIntroducerName != null) tvIntroducerName.setText(introducerName);
            if (introducerId != null && !introducerId.isEmpty() && tvIntroducerId != null) tvIntroducerId.setText(introducerId);
//            if (balance != null && !balance.isEmpty() && tvAvailableBalance != null) tvAvailableBalance.setText(balance);
        }

        // Load Sub-Dealers Network from Firebase Realtime Database
        if (!TextUtils.isEmpty(currentDealerId)) {
            loadSubDealersHierarchy(currentDealerId);
        } else {
            fullHierarchyTreeText = "Dealer ID not available.";
        }

        // Genealogical Tree action
        if (btnViewGenealogicalTree != null) {
            btnViewGenealogicalTree.setOnClickListener(v -> showGenealogicalTreeDialog());
        }

        // Withdraw button action
        if (btnWithdraw != null) {
            btnWithdraw.setOnClickListener(v ->
                Toast.makeText(DetailsActivity.this, R.string.withdraw_amount, Toast.LENGTH_SHORT).show()
            );
        }

        // Purchase Products button action
        if (btnPurchase != null) {
            btnPurchase.setOnClickListener(v -> {
                Intent purchaseIntent = new Intent(DetailsActivity.this, ItemPurchase.class);
                startActivity(purchaseIntent);
            });
        }

        // Close app button action
        if (btnCloseApp != null) {
            btnCloseApp.setOnClickListener(v -> finishAffinity());
        }
    }

    private void loadSubDealersHierarchy(String dealerIdKey) {
        DatabaseReference dealersRef = FirebaseDatabase.getInstance().getReference("Dealers");
        dealersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    fullHierarchyTreeText = "No dealers found in system.";
                    return;
                }

                DataSnapshot mySnapshot = findDealerSnapshot(snapshot, dealerIdKey);
                if (mySnapshot == null) {
                    fullHierarchyTreeText = "Dealer details not found in system.";
                    return;
                }

                int totalCount = 0;
                int level1Count = 0;
                StringBuilder hierarchyBuilder = new StringBuilder();

                List<DataSnapshot> currentLevel = new ArrayList<>();
                Set<String> visitedKeys = new HashSet<>();

                String myKey = mySnapshot.getKey();
                if (myKey != null) visitedKeys.add(myKey);

                // Collect Level 1 sub-dealers (sub_ref1, sub_ref2, sub_ref3)
                for (String refKey : new String[]{"sub_ref1", "sub_ref2", "sub_ref3"}) {
                    String subId = mySnapshot.child(refKey).getValue(String.class);
                    if (subId != null && !subId.trim().isEmpty()) {
                        DataSnapshot subSnap = findDealerSnapshot(snapshot, subId.trim());
                        if (subSnap != null) {
                            String k = subSnap.getKey();
                            if (k != null && !visitedKeys.contains(k)) {
                                currentLevel.add(subSnap);
                                visitedKeys.add(k);
                            }
                        }
                    }
                }

                level1Count = currentLevel.size();
                int level = 1;

                while (!currentLevel.isEmpty()) {
                    int levelSize = currentLevel.size();
                    totalCount += levelSize;

                    hierarchyBuilder.append("Level ").append(level)
                            .append(" (").append(levelSize).append(" dealer").append(levelSize > 1 ? "s" : "").append("):\n");

                    List<DataSnapshot> nextLevel = new ArrayList<>();
                    for (DataSnapshot dealerSnap : currentLevel) {
                        String name = dealerSnap.child("name").getValue(String.class);
                        String dId = dealerSnap.child("dealerId").getValue(String.class);
                        String phone = dealerSnap.child("phone").getValue(String.class);
                        String status = dealerSnap.child("status").getValue(String.class);

                        String idShow = (dId != null && !dId.isEmpty()) ? dId : (phone != null ? phone : "N/A");
                        String statusShow = (status != null && !status.isEmpty()) ? " [" + status + "]" : "";
                        String displayName = (name != null ? name : "Dealer") + " (ID: " + idShow + ")" + statusShow;

                        hierarchyBuilder.append("  • ").append(displayName).append("\n");

                        // Collect sub-dealers of this child for the next level
                        for (String refKey : new String[]{"sub_ref1", "sub_ref2", "sub_ref3"}) {
                            String subId = dealerSnap.child(refKey).getValue(String.class);
                            if (subId != null && !subId.trim().isEmpty()) {
                                DataSnapshot subSnap = findDealerSnapshot(snapshot, subId.trim());
                                if (subSnap != null) {
                                    String k = subSnap.getKey();
                                    if (k != null && !visitedKeys.contains(k)) {
                                        nextLevel.add(subSnap);
                                        visitedKeys.add(k);
                                    }
                                }
                            }
                        }
                    }
                    hierarchyBuilder.append("\n");
                    currentLevel = nextLevel;
                    level++;
                }

                if (tvTotalSubDealers != null) {
                    tvTotalSubDealers.setText(String.valueOf(totalCount));
                }
                if (tvLevel1SubDealers != null) {
                    tvLevel1SubDealers.setText(level1Count + " / 3");
                }

                if (hierarchyBuilder.length() > 0) {
                    fullHierarchyTreeText = "Total Sub-Dealers in Network: " + totalCount + "\n\n" + hierarchyBuilder.toString();
                } else {
                    fullHierarchyTreeText = "No sub-dealers registered under this dealer yet.";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DetailsActivity.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private DataSnapshot findDealerSnapshot(DataSnapshot allDealers, String targetId) {
        if (targetId == null || targetId.trim().isEmpty()) return null;
        for (DataSnapshot child : allDealers.getChildren()) {
            String key = child.getKey();
            String dId = child.child("dealerId").getValue(String.class);
            String phone = child.child("phone").getValue(String.class);

            if (Objects.equals(targetId, key) || Objects.equals(targetId, dId) || Objects.equals(targetId, phone)) {
                return child;
            }
        }
        return null;
    }

    private void showGenealogicalTreeDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Genealogical Tree Network")
                .setMessage(fullHierarchyTreeText)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
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
