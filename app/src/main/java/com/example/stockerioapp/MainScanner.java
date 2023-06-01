package com.example.stockerioapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import java.util.*;

import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class MainScanner extends AppCompatActivity {

    private Button mBillBtn;


    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;

//    private FirebaseDatabase fbDB;
    private DatabaseReference ref;

    private String barCode="", vendorID, vendPhNo, vendName, vendShop, pdtName, dealPhoneNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_scanner);

        mBillBtn = findViewById(R.id.mainScannerButton);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        vendorID = mAuth.getUid();

        DocumentReference docRef = fStore.collection("Vendors").document(vendorID);
        docRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                vendPhNo = documentSnapshot.getString("PhoneNo");
                vendName = documentSnapshot.getString("Name");
                vendShop = documentSnapshot.getString("ShopName");
            }
        });

        mBillBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateScan();
            }
        });
    }

    private void UpdateScan() {

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

            ref = FirebaseDatabase.getInstance().getReference("Vendors").child(vendPhNo).child("ProductTable").child(barCode);

            ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful()){
                        if(task.getResult().exists()){

                            DataSnapshot dataSnapshot = task.getResult();
                            pdtName = String.valueOf(dataSnapshot.child("productName").getValue());
                            int stockCt = Integer.parseInt(dataSnapshot.child("stockCount").getValue().toString());
                            int alertCt =  Integer.parseInt(dataSnapshot.child("alertCount").getValue().toString());
                            // double pdtPrice = Integer.parseInt(dataSnapshot.child("productPrice").getValue().toString())*(1.0);
                            dealPhoneNo = dataSnapshot.child("dealerPhoneNo").getValue().toString();

                            if(pdtName!=null) {

                                LayoutInflater li = LayoutInflater.from(MainScanner.this);
                                View promptsView = li.inflate(R.layout.prompts, null);

                                AlertDialog.Builder builder = new AlertDialog.Builder(MainScanner.this);

                                builder.setView(promptsView);
                                EditText eTemp = (EditText) promptsView.findViewById(R.id.promptCount);
                                builder.setCancelable(false)
                                        .setPositiveButton("ENTER", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                int sellCount = Integer.parseInt(eTemp.getText().toString());

                                                if( (stockCt-sellCount) < 0 ){
                                                    Toast.makeText(MainScanner.this, "Only " + stockCt + " of " + pdtName + " left!", Toast.LENGTH_SHORT).show();
                                                    new AlertDialog.Builder(MainScanner.this)
                                                            .setCancelable(false)
                                                            .setTitle("Alert!")
                                                            .setMessage( "Only " + stockCt + " of " + pdtName + " left\nPlease update your inventory!")
                                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    dialog.cancel();
                                                                    return;
                                                                }
                                                            })
                                                            .show();

                                                }
                                                else {

                                                    if ((stockCt - sellCount) <= alertCt) {

                                                        new AlertDialog.Builder(MainScanner.this)
                                                            .setTitle("Alert!")
                                                            .setMessage( "You are exceeding the alert count of " + alertCt + " for "+ pdtName + "\nNotify the Dealer ?")
                                                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                  if(ContextCompat.checkSelfPermission(MainScanner.this,  Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                                                                      notifyDealer();
                                                                  }
                                                                  else{
                                                                      ActivityCompat.requestPermissions(MainScanner.this, new String[]{Manifest.permission.SEND_SMS},100);
                                                                  }

                                                                }
                                                            })
                                                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    dialog.cancel();
                                                                }
                                                            })
                                                            .show();
                                                    }

                                                    HashMap updateMap = new HashMap();
                                                    updateMap.put("stockCount", (stockCt - sellCount)+"");

                                                    DatabaseReference tempRef =  FirebaseDatabase.getInstance().getReference("Vendors").child(vendPhNo).child("ProductTable").child(barCode);
                                                    tempRef.updateChildren(updateMap)
                                                        .addOnCompleteListener(new OnCompleteListener() {
                                                            @Override
                                                            public void onComplete(@NonNull Task task) {
                                                                if(task.isSuccessful()){
                                                                    Toast.makeText(MainScanner.this, pdtName + " left: " + (stockCt - sellCount), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                }
                                            }
                                        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });
                                builder.create();
                                builder.show();
                            }
                        }
                        else{
                            Toast.makeText(MainScanner.this, "No Such Product Exists! Please Add it!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    });

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==100 && grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            notifyDealer();
        }
        else{
            Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
        }
    }

    private void notifyDealer() {

        LayoutInflater li = LayoutInflater.from(MainScanner.this);
        View promptsView = li.inflate(R.layout.req_count, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainScanner.this);

        builder.setView(promptsView);
        EditText reqCount = (EditText) promptsView.findViewById(R.id.promptReqCount);
        builder.setCancelable(false)
                .setPositiveButton("SEND", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String reqCt = reqCount.getText().toString();
                        String message = ("Order request from " + vendName + " of " + vendShop + " for " + reqCt  +  " " + pdtName)+"";

                        if(!message.isEmpty() && !dealPhoneNo.isEmpty()){

                            try {
                                SmsManager sms = SmsManager.getDefault();
                                sms.sendTextMessage(dealPhoneNo.trim(), null, message, null, null);
                            }catch (IllegalArgumentException e){
                                Toast.makeText(MainScanner.this, "Message not sent due to invalid phone number/message format!", Toast.LENGTH_SHORT).show();
                            }catch (SecurityException e){
                                Toast.makeText(MainScanner.this, "Message not sent! due to denied permissions!", Toast.LENGTH_SHORT).show();
                            }catch (NullPointerException e){
                                Toast.makeText(MainScanner.this, "Message not sent! as no SMS instance found!", Toast.LENGTH_SHORT).show();
                            }catch (Exception e) {
                                Toast.makeText(MainScanner.this, "Notified Dealer Successfully!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(MainScanner.this, "Please update your Dealer Phone Number!", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.create();
        builder.show();

    }
}