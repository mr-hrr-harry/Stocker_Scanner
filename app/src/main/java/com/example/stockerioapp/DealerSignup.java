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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DealerSignup extends AppCompatActivity {

    private EditText mDealerName, mPhone, mEmail, mPass, mConfPass;
    private Button dSignUPButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore cloudFSDB;
    private FirebaseDatabase realTimeDB;
    private DatabaseReference reference;

    private String name, phone, email, pass, cPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer_signup);

        mDealerName = findViewById(R.id.dealerName);
        mPhone = findViewById(R.id.delaerPhoneNo);
        mEmail = findViewById(R.id.dealerLoginEmail);
        mPass = findViewById(R.id.dealerLoginPassword);
        mConfPass = findViewById(R.id.dealerConfirmPassword);

        dSignUPButton = findViewById(R.id.dealerLoginButton);

        mAuth = FirebaseAuth.getInstance();

        dSignUPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });
    }

    private void createUser() {

        name = mDealerName.getText().toString();
        phone = mPhone.getText().toString();
        email = mEmail.getText().toString();
        pass = mPass.getText().toString();
        cPass = mConfPass.getText().toString();

        if(name.isEmpty()){
            Toast.makeText(this, "Please Enter Name!", Toast.LENGTH_SHORT).show();
            mDealerName.setError("Please Enter Name!");
            return;
        }

        if(phone.isEmpty()){
            Toast.makeText(this, "Please Enter Phone Number!", Toast.LENGTH_SHORT).show();
            mPhone.setError("Please Enter Phone Number!");
            return;
        }if(phone.length()!=10 || !Patterns.PHONE.matcher(phone).matches()){
            Toast.makeText(this, "Enter Valid Phone Number!", Toast.LENGTH_SHORT).show();
            mPhone.setError("Enter Valid Phone Number!");
            return;
        }

        if(email.isEmpty()) {
            Toast.makeText(this, "Please Enter Email ID!", Toast.LENGTH_SHORT).show();
            mEmail.setError("Please Enter Email ID!");
            return;
        }
        if(Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            if (pass.isEmpty()) {
                Toast.makeText(this, "Please Enter Password!", Toast.LENGTH_SHORT).show();
                mPass.setError("Please Enter Password!");
                return;
            }
            if (cPass.isEmpty()) {
                Toast.makeText(this, "Please Confirm Password!", Toast.LENGTH_SHORT).show();
                mConfPass.setError("Please Confirm Password!");
                return;
            }
            if (!pass.equals(cPass)) {
                mPass.setError("Password Doesn't match Confirm Password!");
                mConfPass.setError("Password Doesn't match Confirm Password!");
                Toast.makeText(this, "Password Doesn't match Confirm Password!", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, "Deal" + pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            addDealerFireStore();
                            addDealerRealTime();
                            Toast.makeText(DealerSignup.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                            Intent intent  = new Intent( DealerSignup.this, DealerLogin.class);
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

    private void addDealerFireStore() {
        cloudFSDB = FirebaseFirestore.getInstance();

        Map<String, String> dealer = new HashMap<>();
        dealer.put("Name", name);
        dealer.put("PhoneNo", phone);
        dealer.put("EmailID", email);

        cloudFSDB.collection("Dealers").document(FirebaseAuth.getInstance().getUid()).set(dealer);
    }


    private void addDealerRealTime() {
        DealerRealTimeData dealer = new DealerRealTimeData(name, phone);

        realTimeDB = FirebaseDatabase.getInstance();
        reference = realTimeDB.getReference("Dealers");

        reference.child(phone).setValue(dealer)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mDealerName.setText("");
                        mPhone.setText("");
                        mEmail.setText("");
                        mPass.setText("");
                        mConfPass.setText("");
                    }
                });

    }
}