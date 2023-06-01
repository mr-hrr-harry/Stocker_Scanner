package com.example.stockerioapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.HashMap;
import java.util.Map;


public class adderData extends AppCompatActivity {

    private Button scanGood, addBttn;
    private TextView barCodeText;
    private EditText mPdtName, mPdtPrice, mStockCt, mAlertCt, mDealerPNo;

    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;

    private FirebaseDatabase fbDB;
    private DatabaseReference ref;

    private String barCode="", pName, pPrice, stockCt, alertCt, dealerPNo, vendorID, vendPhNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adder_data);

        barCodeText = findViewById(R.id.barcodeTxt);
        addBttn = findViewById(R.id.addButton);

        mPdtName = findViewById(R.id.productName);
        mPdtPrice = findViewById(R.id.productPrice) ;
        mStockCt = findViewById(R.id.stockCount);
        mAlertCt = findViewById(R.id.alertCount);
        mDealerPNo = findViewById(R.id.dealerPhNo);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        vendorID = mAuth.getUid();

        DocumentReference docRef = fStore.collection("Vendors").document(vendorID);
        docRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                vendPhNo = documentSnapshot.getString("PhoneNo");
            }
        });


        scanGood = findViewById(R.id.scanGoodsButton);
        scanGood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scannerCode();
            }
        });

        addBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProductDB();
            }
        });
    }

    private void addProductDB() {

        if(barCode.equals("")){
            Toast.makeText(this, "Please scan the Product BarCode!", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            pName = mPdtName.getText().toString();
            if(pName.isEmpty()){
                Toast.makeText(this, "Please enter Product Name!", Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                pPrice = mPdtPrice.getText().toString();
                if (pPrice.isEmpty()) {
                    Toast.makeText(this, "Please enter Product Price!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    stockCt = mStockCt.getText().toString();
                    if(stockCt.isEmpty()){
                        Toast.makeText(this, "Please Enter Stock Count!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else {
                        alertCt = mAlertCt.getText().toString();
                        if(alertCt.isEmpty()){
                            Toast.makeText(this, "Please Enter Alert Count Value!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else {
                            dealerPNo = mDealerPNo.getText().toString();
                            if (dealerPNo.isEmpty()) {
                                Toast.makeText(this, "Please Enter Dealer Phone Number!", Toast.LENGTH_SHORT).show();
                                return;
                            } else if (dealerPNo.length() != 10 || !Patterns.PHONE.matcher(dealerPNo).matches()) {
                                Toast.makeText(this, "Please Enter a Valid Phone Number", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            else {

                                VendorProductTableDetails vendPdtTblDt = new VendorProductTableDetails(barCode, pName, pPrice, stockCt, alertCt, dealerPNo);

                                fbDB = FirebaseDatabase.getInstance();
                                ref = fbDB.getReference("Vendors").child(vendPhNo).child("ProductTable");

                                ref.child(barCode).setValue(vendPdtTblDt).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(adderData.this, pName + " added Successfully!", Toast.LENGTH_SHORT).show();
                                        mPdtName.setText("");
                                        mPdtPrice.setText("");
                                        mStockCt.setText("");
                                        mAlertCt.setText("");
                                        mDealerPNo.setText("");
                                        barCodeText.setText("BARCODE: ");
                                        startActivity(new Intent(adderData.this, adderData.class));
                                    }
                                });
                            }
                        }
                    }
                }
            }
        }
    }


    private void scannerCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("VolumeUp for Flash ON!");
        options.setBeepEnabled(true);
        options.setOrientationLocked(false);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
       if(result.getContents()!=null){
           barCode = result.getContents().toString();
           barCodeText.setText("BARCODE: " + barCode);
       }
    });
}