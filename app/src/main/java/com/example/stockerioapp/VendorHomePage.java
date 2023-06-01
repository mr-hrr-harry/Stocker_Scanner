package com.example.stockerioapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class VendorHomePage extends AppCompatActivity {

    private Button scannerButton, addGoodsButton, removeGoodsButton, updateGoodsButton;
    private TextView welcome;

    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;

    String vendorID, vendorName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_home_page);

        scannerButton = findViewById(R.id.goodsScan);
        addGoodsButton = findViewById(R.id.addGoods);
        removeGoodsButton = findViewById(R.id.removeGoods);
        updateGoodsButton = findViewById(R.id.updateGoods);
        welcome = findViewById(R.id.welcomeAddress);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        vendorID = mAuth.getUid();


        DocumentReference docRef = fStore.collection("Vendors").document(vendorID);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                vendorName = value.getString("Name");
                welcome.setText("Welcome " + vendorName + "!");
            }
        });



        scannerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VendorHomePage.this, MainScanner.class));
            }
        });

        addGoodsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VendorHomePage.this, adderData.class);
                startActivity(intent);
            }
        });

        removeGoodsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VendorHomePage.this, removeData.class);
                startActivity(intent);
            }
        });

        updateGoodsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VendorHomePage.this, updateData.class);
                startActivity(intent);
            }
        });

    }


    public void vendorLogout(){
        mAuth = FirebaseAuth.getInstance();

        mAuth.signOut();
        Intent intent  = new Intent(VendorHomePage.this, MainActivity.class);
        startActivity(intent);
        finish();

    }
}