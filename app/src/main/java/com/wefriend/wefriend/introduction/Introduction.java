package com.wefriend.wefriend.introduction;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.wefriend.wefriend.login.RegisterActivity;
import com.wefriend.wefriend.login.RegisterGenderActivity;

import java.util.List;


public class Introduction extends AppCompatActivity {

    private Button signupButton;
    private Button loginButton;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);
        mAuth = FirebaseAuth.getInstance();
        signupButton = (Button) findViewById(R.id.signup_button);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                openEmailAddressEntryPage();
            }
        });

        loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginPage();
            }
        });

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
                                    Intent intent = new Intent(Introduction.this, BufferActivity.class);
                                    MainActivity.college = document.get("school").toString();
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Log.d("Intro check profile", "No such document");
                                    Intent intent = new Intent(Introduction.this, RegisterGenderActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                Log.d("Intro check profile", "get failed with ", task.getException());
                            }
                        }
                    });


                }
            }
        };
    }

    public void openLoginPage(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void openEmailAddressEntryPage() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }
}

