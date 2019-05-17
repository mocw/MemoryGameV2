package com.example.memorygamev2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ListWithCheckBoxActivity extends AppCompatActivity {
    ListView listView;
    Toolbar mToolbar;
    Button ready;
    ProgressDialog progressDialog;
    public static List<ImageCT> userListToGame = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_with_check_box);
        listView = findViewById(R.id.list);
        ready = findViewById(R.id.buttonReady);
        if(userListToGame!=null) userListToGame.clear();

        if(EmptyUserPicGameActivity.list.size()==0) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setMessage("Brak zdjęć!")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent i = new Intent(ListWithCheckBoxActivity.this,SelectBoardActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            startActivity(i);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("Wybierz zdjęcia, które chcesz dodać do gry")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        return;
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

        final GalleryAdapter galleryAdapter = new GalleryAdapter(ListWithCheckBoxActivity.this,
                R.layout.gallery, EmptyUserPicGameActivity.list);
        listView.setAdapter(galleryAdapter);
        final Context context = this;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                userListToGame.add(galleryAdapter.getItem(position));
                galleryAdapter.remove(galleryAdapter.getItem(position));
                galleryAdapter.notifyDataSetChanged();
                if(galleryAdapter.isEmpty())
                {
                    Intent intent = new Intent(ListWithCheckBoxActivity.this, EmptyGameActivity.class);
                    intent.putExtra("id","UserPicsGame");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        });

        ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userListToGame.size()==0)
                {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                    builder.setMessage("Nie wybrałeś żadnych zdjęć!")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    return;
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                    return;
                }
                Intent intent = new Intent(ListWithCheckBoxActivity.this, EmptyGameActivity.class);
                intent.putExtra("id","UserPicsGame");
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });


    }
}
