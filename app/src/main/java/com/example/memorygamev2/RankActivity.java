package com.example.memorygamev2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class RankActivity extends AppCompatActivity {
    public static ArrayList<String> arrayList;
    public static ArrayAdapter<String> adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        listView = (ListView) findViewById(R.id.rankList);
        listView.setAdapter(adapter);

        if(RankEmptyActivity.firstPlace) {
            final MediaPlayer mediaPlayer;
            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.clapping);
            mediaPlayer.start();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Gratulacje! Pierwsze miejsce!")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mediaPlayer.stop();
                            RankEmptyActivity.firstPlace=false;
                            return;
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

    }
}
