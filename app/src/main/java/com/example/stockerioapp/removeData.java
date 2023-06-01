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

public class removeData extends AppCompatActivity {

    private Button mRemovePdtBtn;

    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;

    private FirebaseDatabase fbDB;
    private DatabaseReference ref;

    private String barCode="", vendorID, vendPhNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_data);

        mRemovePdtBtn = findViewById(R.id.removePdtScan);

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

        mRemovePdtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveScan();
            }
        });
    }

    private void RemoveScan() {
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

                            if(pdtName!=null){
                                new AlertDialog.Builder(removeData.this)
                                .setTitle("Confirm Removal!")
                                .setMessage("Would you like to remove " + pdtName + " from your inventory ?")
                                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                ref.removeValue();
                                                Toast.makeText(removeData.this, "Successfully removed " + pdtName + "!", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(removeData.this, removeData.class));
                                            }
                                        })
                                .show();
                            }
                        }
                        else{
                            Toast.makeText(removeData.this, "No Such Product Exists previously!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    });
}