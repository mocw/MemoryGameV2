package com.example.memorygamev2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import android.os.Handler;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class EmptyUserPicGameActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    public static List<ImageCT> list = new ArrayList<>();
    private StorageReference imagePath;
    private DatabaseReference mFirebaseDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_user_pic_game);
        list.clear();

        progressDialog = new ProgressDialog(EmptyUserPicGameActivity.this);
        progressDialog.setMessage("Wczytywanie");
        progressDialog.show();


        mFirebaseDB = FirebaseDatabase.getInstance().getReference("UserPictures");

        mFirebaseDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String currEmail = postSnapshot.child("imageAdress").getValue().toString();
                    String currImageName = postSnapshot.child("name").getValue().toString();
                    currEmail = currEmail.substring(0,currEmail.indexOf("/"));
                    imagePath=FirebaseStorage
                            .getInstance().getReference().child("UserPictures").child(currEmail)
                    .child(currImageName);


                    final long ONE_MEGABYTE = 1024 * 1024;
                    imagePath.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            ImageCT image = new ImageCT(bytes);
                            list.add(image);
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                        }
                    });
                }

                Thread thread = new Thread(){
                    @Override
                    public void run() {
                        try {
                            sleep(8*1000);
                            Intent intent = new Intent(EmptyUserPicGameActivity.this, ListWithCheckBoxActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            progressDialog.dismiss();
                            startActivity(intent);
                        }
                        catch(Exception ex) {}

                    }
                };
                thread.start();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
