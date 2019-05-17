package com.example.memorygamev2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatButton;
import android.widget.GridLayout;

public class Card extends AppCompatButton {

    boolean isClicked = false;
    boolean isMatching = false;

    private ImageCT front;
    private final Drawable back;
    private final Drawable correct;

    public Card(Context context , int cloumns , int rows) {
        super(context);
        this.back = context.getDrawable(R.drawable.crystal_clear_action_apply); //obrazek z pliku
        this.correct = context.getDrawable(R.drawable.correct);

        setBackground(back); //tlo

        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(GridLayout.spec(rows),
                GridLayout.spec(cloumns));
        layoutParams.width = (int) getResources().getDisplayMetrics().density * 100; //rozmiar szer
        layoutParams.height = (int) getResources().getDisplayMetrics().density * 150; //rozmiar wys
        setLayoutParams(layoutParams);
    }

    boolean isMatching() {
        return isMatching; //czy dopasowane
    }

    void setMatched() {
        isMatching = true; //zaznaczone 2 obrazki
    }

    ImageCT getImage() {

        return front;
    }

    public void setDefaulImage(ImageCT image) {
        front = image;
    }

    public void setCorrect()
    {
        setBackground(correct);
    }

    void reverse() {
        if(isMatching) {
            return;
        }
        if(isClicked) {
            setBackground(back);
            isClicked = false;
        } else {
            Bitmap bitmap = BitmapFactory.decodeByteArray(front.getImage(), //ciag bajtow na bitmape
                    0,front.getImage().length);
            Drawable drawable = new BitmapDrawable(getResources(),bitmap);
            setBackground(drawable);
            isClicked = true;
        }
    }
}
