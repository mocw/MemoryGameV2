package com.example.memorygamev2;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ShowImages extends AppCompatActivity  implements Serializable {


    // Creating RecyclerView.
    ListView listView;
    Toolbar mToolbar;
    Button clear;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_images);
        listView = findViewById(R.id.list);
        clear = findViewById(R.id.buttonClear);
        progressDialog = new ProgressDialog(ShowImages.this);

        if(DisplayImagesActivity.list.size()==0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Brak zdjęć!")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent i = new Intent(ShowImages.this,ProfileActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            startActivity(i);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        final GalleryAdapter galleryAdapter = new GalleryAdapter(ShowImages.this,
                R.layout.gallery, DisplayImagesActivity.list);
        listView.setAdapter(galleryAdapter);
        final Context context = this;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {
                final byte[] img=DisplayImagesActivity.list.get(position).getImage();
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                galleryAdapter.remove(galleryAdapter.getItem(position));
                                galleryAdapter.notifyDataSetChanged();
                                progressDialog.show();
                                progressDialog.setMessage("Usuwanie...");
                                final DatabaseReference mFirebaseDB = FirebaseDatabase.getInstance().getReference("UserPictures");
                                mFirebaseDB.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                                        {
                                            String currUserID = postSnapshot.child("UserID").getValue().toString();
                                            String imageName = postSnapshot.child("name").getValue().toString();
                                            if(ProfileActivity.UID.equals(currUserID)) {
                                                final StorageReference imagePath = FirebaseStorage
                                                        .getInstance().getReference().child("UserPictures")
                                                        .child(ProfileActivity.email).child(imageName);
                                                final long ONE_MEGABYTE = 1024 * 1024;
                                                final DatabaseReference postSnapshot2 = postSnapshot.getRef();
                                                imagePath.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                    @Override
                                                    public void onSuccess(byte[] bytes) {
                                                        if(Arrays.equals(bytes,img))
                                                        {
                                                            imagePath.delete();
                                                            postSnapshot2.removeValue();
                                                        }


                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception exception) {

                                                    }
                                                });

                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                Thread thread = new Thread(){
                                    @Override
                                    public void run() {
                                        try {
                                            sleep(1*1000);
                                            progressDialog.dismiss();
                                        }
                                        catch(Exception ex) {}

                                    }
                                };
                                thread.start();
                                if(galleryAdapter.isEmpty())
                                {
                                    progressDialog.dismiss();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setMessage("Brak zdjęć!")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    Intent i = new Intent(ShowImages.this,ProfileActivity.class);
                                                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                                    startActivity(i);
                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                }
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.dismiss();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Usunąć zdjęcie?").setPositiveButton("Tak", dialogClickListener)
                        .setNegativeButton("Nie", dialogClickListener).show();
            }
        });


        clear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                progressDialog.show();
                progressDialog.setMessage("Usuwanie...");
                final DatabaseReference mFirebaseDB = FirebaseDatabase.getInstance().getReference("UserPictures");
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
                                imagePath.delete();

                                //String key = postSnapshot.getKey();
                                //dataSnapshot.getRef().removeValue();
                                postSnapshot.getRef().removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                Thread thread = new Thread(){
                    @Override
                    public void run() {
                        try {
                            sleep(5*1000);
                            DisplayImagesActivity.list.clear();
                            Intent intent = new Intent(ShowImages.this, ProfileActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            progressDialog.dismiss();
                            startActivity(intent);
                        }
                        catch(Exception ex) {}

                    }
                };
                thread.start();
            }
        });
    }


}