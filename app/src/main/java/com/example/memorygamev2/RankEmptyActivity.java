package com.example.memorygamev2;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RankEmptyActivity extends AppCompatActivity {
    private DatabaseReference mFirebaseDB;
    private FirebaseDatabase mFirebaseInstance;
    private ProgressDialog progressDialog;
    public static boolean firstPlace=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_rank);

        progressDialog = new ProgressDialog(RankEmptyActivity.this);
        progressDialog.setMessage("Wczytywanie.");
        progressDialog.show();

        RankActivity.arrayList=new ArrayList<>();
        mFirebaseInstance=FirebaseDatabase.getInstance();
        mFirebaseDB = mFirebaseInstance.getReference("UserStats");
        Query myTopPostsQuery = mFirebaseDB.orderByChild("Time").limitToLast(100);
        myTopPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int counter=0;
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    counter++;
                    if(!postSnapshot.child("email").exists()
                    || !postSnapshot.child("Time").exists())
                    {
                        DatabaseReference postSnapshot2 = postSnapshot.getRef();
                        postSnapshot2.removeValue();
                        RankActivity.arrayList.clear();
                        startActivity(getIntent());
                    }
                    String currEmail = postSnapshot.child("email").getValue().toString();
                    String Value = counter+ ". " +
                            postSnapshot.child("email").getValue().toString() +
                            " czas: " + postSnapshot.child("Time").getValue().toString() + "s.";
                    if(counter==1 && currEmail.equals(ProfileActivity.email))
                    {
                        firstPlace=true;
                    }

                    if(postSnapshot.child("email").getValue().toString().equals(ProfileActivity.email))
                    {
                        Value+="  <------------";
                    }
                    RankActivity.arrayList.add(Value);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        RankActivity.adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1
                ,RankActivity.arrayList);

        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(3*1000);
                    Intent intent = new Intent(RankEmptyActivity.this, RankActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    progressDialog.dismiss();
                    startActivity(intent);
                }
                catch(Exception ex) {}

            }
        };
        thread.start();



    }
}
