package com.example.stockerioapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.HashMap;

public class updateData extends AppCompatActivity {

    private Button updateScanner, updateButton;
    private EditText stockCount, alertCount;
    private TextView productLabel;

    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;

    private FirebaseDatabase fbDB;
    private DatabaseReference ref;

    private String barCode="", vendorID, vendPhNo, stockCt, alertCt;
    private int dbStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_data);

        updateScanner = findViewById(R.id.updateGoodScan);
        updateButton = findViewById(R.id.updateButton);
        stockCount = findViewById(R.id.stockUpdateCount);
        alertCount = findViewById(R.id.alertUpdateCount);
        productLabel = findViewById(R.id.updateProductTxt);

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

        updateScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateScan();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDataBase();
            }
        });

    }

    private void updateDataBase(){
        if(barCode.equals("")){
            Toast.makeText(this, "Please scan the Product BarCode!", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            stockCt = stockCount.getText().toString();
            if(stockCt.isEmpty()){
                Toast.makeText(this, "Please enter Current Stock Count!", Toast.LENGTH_SHORT).show();
                return;
            }
            else{
                alertCt = alertCount.getText().toString();
                if(alertCt.isEmpty()){
                    Toast.makeText(this, "Please enter Current Stock Count!", Toast.LENGTH_SHORT).show();
                }
                else{

                    int stockCountInt = Integer.parseInt(stockCt);
                    int alertCountInt = Integer.parseInt(alertCt);

                    HashMap vendor = new HashMap();
                    vendor.put("stockCount", (stockCountInt+dbStock)+"" );
                    vendor.put("alertCount", alertCountInt+"");

                    DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference("Vendors").child(vendPhNo).child("ProductTable").child(barCode);
                    tempRef.updateChildren(vendor).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful()){
                                stockCount.setText("");
                                alertCount.setText("");
                                Toast.makeText(updateData.this, "Updated Successfully!", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(updateData.this, "No data updated!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }

        }
    }

    private void updateScan() {
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

            fbDB = FirebaseDatabase.getInstance();
            ref = fbDB.getReference("Vendors").child(vendPhNo).child("ProductTable").child(barCode);

            ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful()){

                        if(task.getResult().exists()){

                            DataSnapshot dataSnapshot = task.getResult();
                            String pdtName = String.valueOf(dataSnapshot.child("productName").getValue());
                            dbStock = Integer.valueOf(String.valueOf(dataSnapshot.child("stockCount").getValue()));
                            productLabel.setText("PRODUCT: "+ pdtName);

                        }
                        else{
                            Toast.makeText(updateData.this, "No Such Product Exists!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    });
}