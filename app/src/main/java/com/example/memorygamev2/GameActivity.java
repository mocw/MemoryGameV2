package com.example.memorygamev2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.os.Vibrator;
import android.support.v7.widget.GridLayout;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class GameActivity extends AppCompatActivity  implements View.OnClickListener{

    private GridLayout gridLayout;
    private int size;
    private int numbersOfButtons;
    private Card[] cards;
    private Card selectedCard;
    private Card secondSelectedCard;
    private int rows=0;
    private boolean isBusy = false;
    private long startTime;
    boolean isStock=false;
    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Intent intent = getIntent();
        size = intent.getIntExtra(ProfileActivity.SIZE,0);
        Context context = this;
        startTime = System.currentTimeMillis();
        isStock = intent.getExtras().getBoolean("isStock");

        if(EmptyGameActivity.imgGameList.size()==0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Brak zdjęć!")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent i = new Intent(GameActivity.this,SelectBoardActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        gridLayout = findViewById(R.id.gridLayout);
        init();
        createCards();
        shuffleImages();
    }

    private void init(){
        Intent intent = getIntent();
        size = intent.getIntExtra(ProfileActivity.SIZE,0) / 2;
        gridLayout.setColumnCount(size); //kolumny
        if(isStock) {
            rows = intent.getIntExtra("rows",0);
        }
        else rows = 2;
        gridLayout.setRowCount(rows); //wiersze
        numbersOfButtons = size * 2;
        cards = new Card[numbersOfButtons]; //ile obrazkow
    }

    private void createCards(){ //generowanie
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < size; j++){
                Card card = new Card(this,i,j);
                card.setId(View.generateViewId()); //l
                card.setOnClickListener(this);
                cards[i * size + j] = card; //tablica kart
                gridLayout.addView(card);
            }
        }
    }

    private void shuffleImages(){  //tasowanie zdjec
        Collections.shuffle(EmptyGameActivity.imgGameList);

        for (int n = 0; n<numbersOfButtons; n++){
            cards[n].setDefaulImage(EmptyGameActivity.imgGameList.get(n));  //pobieranie zdjec z listy
        }
    }

    @Override
    public void onClick(View v) {
        if(isBusy) {
            return;
        }

        Card card = (Card) v;

        if(card.isMatching()) {
            return;
        }

        if(selectedCard == null) {
            selectedCard = card;
            selectedCard.reverse();
            return;
        }

        if(selectedCard.getId() == card.getId()) {
            return;
        }

        if(Arrays.equals(selectedCard.getImage().getImage(), card.getImage().getImage())) {
            pairFound(card);
            if(shouldGameEnd()) {
                endGame();
            }
        } else {
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(200);
            pairNotFound(card);
        }
    }

    private void pairFound(Card card) {
        card.reverse();

        card.setMatched();

        card.setCorrect();
        selectedCard.setCorrect();
        selectedCard.setMatched();

        selectedCard.setEnabled(false);  //zmiana stanu karty, zeby znow na nia kliknac
        card.setEnabled(false);

        selectedCard = null;
    }

    private void pairNotFound(Card card) {
        secondSelectedCard = card;
        secondSelectedCard.reverse();
        isBusy = true;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                secondSelectedCard.reverse();
                selectedCard.reverse();
                secondSelectedCard = null;
                selectedCard = null;
                isBusy = false;
            }
        }, 500);
    }

    private void endGame() {

        long difference = System.currentTimeMillis() - startTime;
        final double timeResult = difference/1000.0;

        //Intent intent = getIntent();
        if(isStock) {

            FirebaseDatabase mFirebaseInstace = FirebaseDatabase.getInstance();
            final DatabaseReference mFirebaseDB = mFirebaseInstace.getReference("UserStats");

            mFirebaseDB.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                    {
                        String currUserID = postSnapshot.child("userID").getValue().toString();
                        String BoardSize = postSnapshot.child("BoardSize").getValue().toString();
                        String Time = postSnapshot.child("Time").getValue().toString();
                        double TimeL = Double.parseDouble(Time);
                        if(currUserID.equals(ProfileActivity.UID) && BoardSize.equals("2x5"))
                        {
                            if(Double.compare(timeResult,TimeL)<0)
                            {
                                DatabaseReference postSnapshotRef=postSnapshot.getRef().child("Time");
                                postSnapshotRef.setValue(timeResult);
                                Toast.makeText(GameActivity.this,
                                        "Gratulacje! Twój nowy rekord!", Toast.LENGTH_LONG).show();
                                return;
                            }
                            else return;
                        }
                    }

                    UserStats US = new UserStats(ProfileActivity.UID,"2x5",
                            ProfileActivity.email,Double.toString(timeResult));
                    mFirebaseDB.push().setValue(US);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        finish();
                        startActivity(getIntent().addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        Intent i = new Intent(GameActivity.this,SelectBoardActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        break;
                }
            }
        };

        Context context = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Twój czas: "+Double.toString(timeResult)+"s. Chcesz Powtórzyć?").setPositiveButton("Tak", dialogClickListener)
                .setNegativeButton("Nie", dialogClickListener).show();

    }

    private boolean shouldGameEnd() {
        List<Boolean> booleans = new LinkedList<>();
        for(Card card : cards) {
            booleans.add(card.isMatching());
        }
        return areAllTrue(booleans);
    }

    private boolean areAllTrue(List<Boolean> booleans) {
        for(boolean b : booleans) if(!b) return false;
        return true;
    }
}
