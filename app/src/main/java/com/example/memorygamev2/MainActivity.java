package com.example.memorygamev2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    ProgressBar progressBar;
    EditText userEmail;
    EditText userPassword;
    Button userLogin;
    Button userSignUp;
    ImageView logo;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progressBar);
        userEmail = findViewById(R.id.etUserEmail);
        userPassword = findViewById(R.id.etUserPass);
        userLogin = findViewById(R.id.btnUserLogin);
        userSignUp=findViewById(R.id.buttonUserSignIp);
        logo = findViewById(R.id.logo);

        toolbar.setTitle("Zaloguj się");

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseApp.initializeApp(this);
        logo.setImageResource(R.drawable.logo);

        SharedPreferences loginData = getSharedPreferences("loginData", MODE_PRIVATE);
        userEmail.setText(loginData.getString("email",""));
        userPassword.setText(loginData.getString("password",""));


        userLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String textEmail = userEmail.getText().toString();
                final String textPassword = userPassword.getText().toString();

                if (textEmail.matches("") || textPassword.matches("")) {
                    Toast.makeText(MainActivity.this, "Brak loginu/hasła!",
                            Toast.LENGTH_LONG).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseAuth.signInWithEmailAndPassword(userEmail.getText().toString(),
                            userPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        SharedPreferences loginData = getSharedPreferences("loginData", MODE_PRIVATE);
                                        SharedPreferences.Editor e = loginData.edit();
                                        e.putString("email",textEmail);
                                        e.putString("password",textPassword);
                                        e.commit();
                                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                                    } else {
                                        Toast.makeText(MainActivity.this,
                                                task.getException().getMessage(), Toast.LENGTH_LONG).show();

                                    }
                                }
                            });
                }
            }
        });

        userSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String textEmail = userEmail.getText().toString();
                final String textPassword = userPassword.getText().toString();

                if (textEmail.matches("") || textPassword.matches("")) {
                    Toast.makeText(MainActivity.this, "Brak loginu/hasła!",
                            Toast.LENGTH_LONG).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseAuth.createUserWithEmailAndPassword(userEmail.getText().toString(),
                            userPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Zarejestrowano!",
                                                Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(MainActivity.this,
                                                task.getException().getMessage(), Toast.LENGTH_LONG).show();

                                    }
                                }
                            });
                }
            }
        });

    }
}
