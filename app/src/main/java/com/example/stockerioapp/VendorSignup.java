package com.example.stockerioapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class VendorSignup extends AppCompatActivity {

    private EditText mVendorName, mShopName, mLocation, mPhone, mEmail, mPass, mConfPass;
    private Button vSignUpButton;
    
    private FirebaseAuth mAuth;
    private FirebaseFirestore  cloudFSDB;
    private FirebaseDatabase realTimedDB;
    private DatabaseReference reference;

    private String name, shopName, loc, phone, email, pass, cPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_signup);

        mVendorName = findViewById(R.id.vendorName);
        mShopName = findViewById(R.id.storeName);
        mLocation = findViewById(R.id.vendorLocationAddress);
        mPhone = findViewById(R.id.vendorPhoneNo);
        mEmail = findViewById(R.id.vendorLoginEmail);
        mPass = findViewById(R.id.vendorLoginPassword);
        mConfPass = findViewById(R.id.vendorConfirmPassword);

        vSignUpButton = findViewById(R.id.vendorLoginButton);


        mAuth = FirebaseAuth.getInstance();

        vSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });
    }


    private void createUser() {

        name = mVendorName.getText().toString();
        shopName = mShopName.getText().toString();
        loc = mLocation.getText().toString();
        phone = mPhone.getText().toString();
        email = mEmail.getText().toString();
        pass = mPass.getText().toString();
        cPass = mConfPass.getText().toString();

        if(name.isEmpty()){
            Toast.makeText(VendorSignup.this, "Please Enter Name!", Toast.LENGTH_SHORT).show();
            mVendorName.setError("Please Enter Name!");
            return;
        }if(shopName.isEmpty()){
            Toast.makeText(VendorSignup.this, "Please Enter Shop Name!", Toast.LENGTH_SHORT).show();
            mShopName.setError("Please Enter Shop name!");
            return;
        }if(loc.isEmpty()){
            Toast.makeText(VendorSignup.this, "Please Enter Location address!", Toast.LENGTH_SHORT).show();
            mLocation.setError("Please Enter Location address!");
            return;
        }

        //Phone No. Validation
        if(phone.isEmpty()){
            Toast.makeText(VendorSignup.this, "Please Enter Phone Number!", Toast.LENGTH_SHORT).show();
            mPhone.setError("Please Enter Phone Number!");
            return;
        }if(phone.length()!=10 || !Patterns.PHONE.matcher(phone).matches()){
            Toast.makeText(this, "Enter Valid Phone Number!", Toast.LENGTH_SHORT).show();
            mPhone.setError("Enter Valid Phone Number!");
            return;
        }

        //Email validation
        if(email.isEmpty()) {
            Toast.makeText(VendorSignup.this, "Please Enter Email ID!", Toast.LENGTH_SHORT).show();
            mEmail.setError("Please Enter Email ID!");
            return;
        }
        if(Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            if(pass.isEmpty()){
                Toast.makeText(VendorSignup.this, "Please Enter Password!", Toast.LENGTH_SHORT).show();
                mPass.setError("Please Enter Password!");
                return;
            }if(cPass.isEmpty()){
                Toast.makeText(VendorSignup.this, "Please Confirm Password!", Toast.LENGTH_SHORT).show();
                mConfPass.setError("Please Confirm Password!");
                return;
            }if(!pass.equals(cPass)){
                mPass.setError("Password Doesn't match Confirm Password!");
                mConfPass.setError("Password Doesn't match Confirm Password!");
                Toast.makeText(this, "Password Doesn't match Confirm Password!", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, "Vend"+pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            addVendorFirestore();
                            addVendorRealTime();
                            Toast.makeText(VendorSignup.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(VendorSignup.this, VendorLogin.class);
                            startActivity(intent);
                            finish();
                        }
                    });
        }
        else{
            Toast.makeText(this, "Enter Valid Email!", Toast.LENGTH_SHORT).show();
            mEmail.setError("Enter Valid Email!");
        }

    }

    private void addVendorFirestore() {
        cloudFSDB = FirebaseFirestore.getInstance();

        Map <String, String> vendor = new HashMap<>();
        vendor.put("Name", name);
        vendor.put("ShopName", shopName);
        vendor.put("Location", loc);
        vendor.put("PhoneNo", phone);
        vendor.put("EmailID", email);

        cloudFSDB.collection("Vendors").document(FirebaseAuth.getInstance().getUid()).set(vendor);
    }

    private void addVendorRealTime() {

        VendorRealTimeData vendor = new VendorRealTimeData(name, phone);

        realTimedDB = FirebaseDatabase.getInstance();
        reference = realTimedDB.getReference("Vendors");

        reference.child(phone).setValue(vendor)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mVendorName.setText("");
                        mShopName.setText("");
                        mLocation.setText("");
                        mPhone.setText("");
                        mEmail.setText("");
                        mPass.setText("");
                        mConfPass.setText("");
                    }
                });
    }
}