package com.example.memorygamev2;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

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
import java.util.Iterator;
import java.util.List;

public class DisplayImagesActivity extends AppCompatActivity {

    // Creating DatabaseReference.
    DatabaseReference databaseReference;

    // Creating RecyclerView.
    ListView listView;

    // Creating RecyclerView.Adapter.
    RecyclerView.Adapter adapter ;

    // Creating Progress dialog
    ProgressDialog progressDialog;

    // Creating List of ImageUploadInfo class.
    public static List<ImageCT> list = new ArrayList<>();
    public static StorageReference imagePath = FirebaseStorage
            .getInstance().getReference().child("empty");
    public static DatabaseReference mFirebaseDB;
    private boolean empty=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_images);

        list.clear();
        // Assign id to RecyclerView.
        listView = findViewById(R.id.list);

        // Assign activity this to progress dialog.
        progressDialog = new ProgressDialog(DisplayImagesActivity.this);

        // Setting up message in Progress dialog.
        progressDialog.setMessage("Wczytywanie...");

        // Showing progress dialog.
        progressDialog.show();

        // Setting up Firebase image upload folder path in databaseReference.
        // The path is already defined in MainActivity.
        mFirebaseDB = FirebaseDatabase.getInstance().getReference("UserPictures");

        // Adding Add Value Event Listener to databaseReference.
        mFirebaseDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    if (items.hasNext())
                    {
                        items.next();
                    }
                    else empty=true;

                    String currUserID = postSnapshot.child("UserID").getValue().toString();
                    String imageName = postSnapshot.child("name").getValue().toString();

                    if(ProfileActivity.UID.equals(currUserID)) {
                        imagePath = FirebaseStorage
                                .getInstance().getReference().child("UserPictures")
                                .child(ProfileActivity.email).child(imageName);

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
                }

                // final GalleryAdapter galleryAdapter = new GalleryAdapter(DisplayImagesActivity.this,
                //        R.layout.gallery,list);
                //listView.setAdapter(galleryAdapter);

                Thread thread = new Thread(){
                    @Override
                    public void run() {
                        try {
                            sleep(8*1000);
                            imagePath = FirebaseStorage.getInstance().getReference().child("empty");
                            mFirebaseDB = FirebaseDatabase.getInstance().getReference().child("empty");
                            Intent intent = new Intent(DisplayImagesActivity.this, ShowImages.class);
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
                //Iterator errors
                progressDialog.dismiss();
            }
        });

    }
}
