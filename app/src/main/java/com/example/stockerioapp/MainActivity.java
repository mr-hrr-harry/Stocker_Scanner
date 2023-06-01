package com.example.stockerioapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    ImageButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.logoButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Welcome to StockerIO 🤍!", Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }

    public void vendorLoginPg(View view){
        Intent intent = new Intent(MainActivity.this, VendorLogin.class);
        startActivity(intent);
    }

    public void dealerLoginPg(View view){
        Intent intent = new Intent(MainActivity.this, DealerLogin.class);
        startActivity(intent);
    }

}