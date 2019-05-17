package com.example.memorygamev2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EmptyGameActivity extends AppCompatActivity {

    private String received;
    public static List<ImageCT> imgGameList = new ArrayList<>();
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_empty);
        if(imgGameList!=null) imgGameList.clear();
        progressDialog = new ProgressDialog(EmptyGameActivity.this);
        progressDialog.setMessage("Wczytywanie.");
        Intent intent = getIntent();
        received = intent.getStringExtra("id");



        if(received.equals("GameActivity")) {
            progressDialog.show();
            final DatabaseReference mFirebaseDB = FirebaseDatabase.getInstance().getReference("UserPictures");
            Log.d(mFirebaseDB.toString(), "Logi");
            mFirebaseDB.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                    {
                        String currUserID = postSnapshot.child("UserID").getValue().toString();
                        String imageName = postSnapshot.child("name").getValue().toString();

                        if(ProfileActivity.UID.equals(currUserID)) {
                            StorageReference imagePath = FirebaseStorage
                                    .getInstance().getReference().child("UserPictures")
                                    .child(ProfileActivity.email).child(imageName);

                            final long ONE_MEGABYTE = 1024 * 1024;
                            imagePath.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    ImageCT image = new ImageCT(bytes);
                                    imgGameList.add(image);
                                    imgGameList.add(image);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {

                                }
                            });
                        }
                    }
                    Thread thread = new Thread(){
                        @Override
                        public void run() {
                            try {
                                sleep(8*1000);
                                int size = imgGameList.size();
                                Intent intent = new Intent(EmptyGameActivity.this, GameActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                intent.putExtra(ProfileActivity.SIZE,size);
                                intent.putExtra("isStock",false);
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

        if(received.equals("GameActivity2x5")) {

            List<Integer> numbers = new ArrayList<Integer>();
            for(int i=1;i<=15;i++)
            {
                numbers.add(i);
            }

            Collections.shuffle(numbers);
            progressDialog.show();

            for(int i=1;i<=6;i++)
            {
                StorageReference imagePath = FirebaseStorage
                        .getInstance().getReference().child("StockPictures").child("pic"+
                                numbers.get(i)+".png");
                final long ONE_MEGABYTE = 1024 * 1024;
                imagePath.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        ImageCT image = new ImageCT(bytes);
                        imgGameList.add(image);
                        imgGameList.add(image);

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
                        sleep(10*1000);
                        int size = imgGameList.size();
                        Intent intent = new Intent(EmptyGameActivity.this, GameActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.putExtra(ProfileActivity.SIZE,12);
                        intent.putExtra("rows",2);
                        intent.putExtra("isStock",true);
                        progressDialog.dismiss();
                        startActivity(intent);
                    }
                    catch(Exception ex) {}

                }
            };
            thread.start();

        }

        if(received.equals("UserPicsGame")) {
            progressDialog.show();
            for(int i=0;i<ListWithCheckBoxActivity.userListToGame.size();i++)
            {
                imgGameList.add(ListWithCheckBoxActivity.userListToGame.get(i));
                imgGameList.add(ListWithCheckBoxActivity.userListToGame.get(i));
            }

            Thread thread = new Thread(){
                @Override
                public void run() {
                    try {
                        sleep(4*1000);
                        int size = imgGameList.size();
                        Intent intent = new Intent(EmptyGameActivity.this, GameActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.putExtra(ProfileActivity.SIZE,size);
                        intent.putExtra("rows",2);
                        intent.putExtra("isStock",false);
                        progressDialog.dismiss();
                        startActivity(intent);
                    }
                    catch(Exception ex) {}

                }
            };
            thread.start();
        }


    }
}
