package com.wefriend.wefriend.introduction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Contacts;
import android.util.Log;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wefriend.wefriend.Main.BufferActivity;
import com.wefriend.wefriend.Main.MainActivity;
import com.wefriend.wefriend.Main.SearchDatabase;
import com.wefriend.wefriend.R;
import com.wefriend.wefriend.account.Account;
import com.wefriend.wefriend.login.LoginActivity;
import com.wefriend.wefriend.login.RegisterGenderActivity;

import java.util.List;

public class WelcomeScreenActivity extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private ProgressBar loadingProgressBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        mAuth = FirebaseAuth.getInstance();
        loadingProgressBarView = (ProgressBar) findViewById(R.id.welcome_progressBar);
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null && user.isEmailVerified()){
                    final String UID = user.getUid();
                    firebaseFirestore = FirebaseFirestore.getInstance();
                    final DocumentReference documentReference = firebaseFirestore.collection("users").document(UID);
                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Log.d("Intro check profile", "DocumentSnapshot data: " + document.getData());
                                    Intent intent = new Intent(WelcomeScreenActivity.this, BufferActivity.class);
                                    MainActivity.college = document.get("school").toString();
                                    BufferActivity.college = document.get("school").toString();
                                    intent.putExtra("SearchPreference", "0");
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Log.d("Intro check profile", "No such document");
                                    Intent intent = new Intent(WelcomeScreenActivity.this, RegisterGenderActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                Log.d("Intro check profile", "get failed with ", task.getException());
                                Intent intent = new Intent(WelcomeScreenActivity.this, Introduction.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
                else {
                    Intent intent = new Intent(WelcomeScreenActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }
}