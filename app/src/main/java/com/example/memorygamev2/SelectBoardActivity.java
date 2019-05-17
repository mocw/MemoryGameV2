package com.example.memorygamev2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelectBoardActivity extends AppCompatActivity {

    Button btnUsersGalleryGame;
    Button btnRank;
    Button btnRankGame;
    Button btnUserPicsGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_board);

        btnUsersGalleryGame = findViewById(R.id.UsersGallery);
        btnRankGame = findViewById(R.id.rankGame);
        btnUserPicsGame = findViewById(R.id.btnOwnGallery);
        btnRank = findViewById(R.id.buttonRank);

        btnRank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectBoardActivity.this, RankEmptyActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });

        btnRankGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectBoardActivity.this, EmptyGameActivity.class);
                intent.putExtra("id","GameActivity2x5");
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });


        btnUserPicsGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectBoardActivity.this, EmptyGameActivity.class);
                intent.putExtra("id","GameActivity");
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });

        btnUsersGalleryGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectBoardActivity.this, EmptyUserPicGameActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });
    }
}
