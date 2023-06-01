package com.example.stockerioapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DealerLogin extends AppCompatActivity {

    private EditText mEmail, mPass;
    private Button dloginButton;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer_login);


        mEmail = findViewById(R.id.dealerLoginEmail);
        mPass = findViewById(R.id.dealerLoginPassword);
        dloginButton = findViewById(R.id.dealerLoginButton);

        mAuth = FirebaseAuth.getInstance();

        dloginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginDealer();
            }
        });

    }

    private void loginDealer() {
        String email = mEmail.getText().toString();
        String pass = mPass.getText().toString();

        if(!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            if (!pass.isEmpty()){
                mAuth.signInWithEmailAndPassword(email, "Deal"+pass)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Toast.makeText(DealerLogin.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(DealerLogin.this, DealerHomePage.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(DealerLogin.this, "No Dealer Found!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            else{
                Toast.makeText(DealerLogin.this, "Please Enter Password!", Toast.LENGTH_SHORT).show();
                mPass.setError("Please Enter Password!");
            }
        }
        else if(email.isEmpty()){
            Toast.makeText(DealerLogin.this, "Please Enter Email!", Toast.LENGTH_SHORT).show();
            mEmail.setError("Please Enter Email!");
        }
        else{
            Toast.makeText(this, "Enter Valid Email!", Toast.LENGTH_SHORT).show();
            mEmail.setError("Enter Valid Email!");
        }
    }

    public void dealerSignupPg(View view){
        Intent intent = new Intent(DealerLogin.this, DealerSignup.class);
        startActivity(intent);
        mEmail.setText("");
        mPass.setText("");
    }
}