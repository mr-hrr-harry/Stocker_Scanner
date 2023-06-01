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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VendorLogin extends AppCompatActivity {

    private EditText mEmail, mPass;
    private Button vloginButton;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_login);

        mEmail = findViewById(R.id.vendorLoginEmail);
        mPass = findViewById(R.id.vendorLoginPassword);
        vloginButton = findViewById(R.id.vendorLoginButton);

        mAuth = FirebaseAuth.getInstance();

        vloginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginVendor();
            }
        });

    }

    private void loginVendor() {
        String email = mEmail.getText().toString();
        String pass = mPass.getText().toString();

        if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (!pass.isEmpty()) {
               mAuth.signInWithEmailAndPassword(email, "Vend"+pass)
                       .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                           @Override
                           public void onSuccess(AuthResult authResult) {
                               Toast.makeText(VendorLogin.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                               Intent intent = new Intent(VendorLogin.this, VendorHomePage.class);
                               startActivity(intent);
                               finish();
                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Toast.makeText(VendorLogin.this, "No Vendor Found!", Toast.LENGTH_SHORT).show();
                           }
                       });
            }
            else{
                Toast.makeText(VendorLogin.this, "Please Enter Password!", Toast.LENGTH_SHORT).show();
                mPass.setError("Please Enter Password!");
            }
        }
        else if(email.isEmpty()){
            Toast.makeText(VendorLogin.this, "Please Enter Email!", Toast.LENGTH_SHORT).show();
            mEmail.setError("Please Enter Email!");
        }
        else{
            Toast.makeText(VendorLogin.this, "Enter Valid Email!", Toast.LENGTH_SHORT).show();
            mEmail.setError("Enter Valid Email!");
        }
    }

    public void vendorSignupPg(View view){
        Intent intent = new Intent(VendorLogin.this, VendorSignup.class);
        startActivity(intent);
        mEmail.setText("");
        mPass.setText("");
    }
}