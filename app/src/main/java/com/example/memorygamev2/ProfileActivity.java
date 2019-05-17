package com.example.memorygamev2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    TextView tvEmail;
    Button userLogOut;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    Button userSignUp;
    Button btnTakePicture;
    ImageView imageView;
    Button buttonAdd;
    Bitmap imageView2;
    Button btnGallery;
    Button btnPlay;
    StorageReference imagePath;
    ProgressDialog progressDialog;
    private DatabaseReference mFirebaseDB;
    private FirebaseDatabase mFirebaseInstance;
    public static String email;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final String SIZE = "ProfileActivity.SIZE";
    public static String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvEmail=findViewById(R.id.tvUserEmail);
        userLogOut=findViewById(R.id.signOut);
        userSignUp = findViewById(R.id.buttonUserSignIp);
        btnTakePicture = findViewById(R.id.buttonTakePicture);
        imageView = findViewById(R.id.imageView);
        buttonAdd = findViewById(R.id.buttonAdd);
        imageView2 = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        btnGallery = findViewById(R.id.buttonHistory);
        btnPlay = findViewById(R.id.buttonPlay);
        progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setMessage("Zapisywanie...");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        mFirebaseInstance=FirebaseDatabase.getInstance();
        mFirebaseDB = mFirebaseInstance.getReference("Users");

        email=firebaseUser.getEmail().toString().trim();
        tvEmail.setText(email);

        findInDB(email);


        userLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UID==null) nothing();
                else
                {
                    Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    if (bitmap.sameAs(imageView2)) {
                        Toast.makeText(ProfileActivity.this, "Brak zdjęcia!", Toast.LENGTH_SHORT).show();
                    } else {
                        savePicture();
                        imageView.setImageResource(R.drawable.ic_action_name);
                    }
                }
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UID==null) nothing();
                else
                {
                    Intent intent = new Intent(ProfileActivity.this, DisplayImagesActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UID==null) nothing();
                else
                {
                    Intent i = new Intent(ProfileActivity.this,SelectBoardActivity.class);
                    startActivity(i);
                }
            }
        });

    } // <-------- METODY

    private void nothing() {
        return;
    }


    private void savePicture() {
        progressDialog.show();
        DisplayImagesActivity.list.clear();
        byte[] image=getImageInBytes();
        imageView.setDrawingCacheEnabled(false);
        String filename = generateFileName();
        imagePath = FirebaseStorage.getInstance().getReference().child("UserPictures")
                .child(email).child(filename);
        mFirebaseDB = mFirebaseInstance.getReference("UserPictures");
        imagePath.putBytes(image); //DO STORAGE
        String path=email+"/"+imagePath.getName();
        String name=imagePath.getName();
        Image imageToDB = new Image(path,getUID(),name); // ZAPIS
        mFirebaseDB.push().setValue(imageToDB); //DO BAZY ZE ZDJECIAMI I ID UZYTKOWNIKA
        DisplayImagesActivity.imagePath = FirebaseStorage
                .getInstance().getReference().child("empty");
        imagePath = FirebaseStorage.getInstance().getReference().child("empty");
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(1000);
                    progressDialog.dismiss();
                    Toast.makeText(ProfileActivity.this, "Zapisano!",
                            Toast.LENGTH_LONG).show();
                }
                catch(Exception ex) {}

            }
        };
        thread.start();
    }

    private String generateFileName() {
        int count=10;
        final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }


    private String getUID() {
        return UID;
    }

    private byte[] getImageInBytes() {
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = imageView.getDrawingCache();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            imageView.setMaxWidth(200);
            imageView.setImageBitmap(thumbnail);
        }
    }

    public void addUser(String email) {
        User user = new User(email);
        mFirebaseDB.push().setValue(user);

        Toast.makeText(ProfileActivity.this, "Pierwsze logowanie!",
                Toast.LENGTH_LONG).show();
    }

    public void findInDB(final String _email) {
        final String email=_email;
        mFirebaseDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();
                while(items.hasNext()) {
                    DataSnapshot item = items.next();
                    String curremail = item.child("email").getValue().toString().trim();
                    if(email.equals(curremail)) {
                        UID=item.getKey(); //<--Klucz zalogowanego uzytkownika
                        return;
                    }
                }
                addUser(_email);
                findInDB(_email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
