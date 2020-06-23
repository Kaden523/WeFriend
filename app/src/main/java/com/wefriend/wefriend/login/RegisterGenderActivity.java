package com.wefriend.wefriend.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wefriend.wefriend.R;

import java.util.HashMap;
import java.util.Map;

public class RegisterGenderActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private boolean selectMale = true;
    private Button maleSelectionBtnView, femaleSelectionBtnView, continueGenderBtnView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_gender);

        maleSelectionBtnView = (Button) findViewById(R.id.maleSelectionButton);
        femaleSelectionBtnView = (Button) findViewById(R.id.femaleSelectionButton);
        continueGenderBtnView = findViewById(R.id.genderContinueButton);

        femaleSelectionBtnView.setAlpha(.5f);
        femaleSelectionBtnView.setBackgroundColor(Color.GRAY);

        maleSelectionBtnView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                maleSelectionBtnView.setBackgroundColor(Color.parseColor("#009688"));
                maleSelectionBtnView.setAlpha(1.0f);
                femaleSelectionBtnView.setAlpha(.5f);
                femaleSelectionBtnView.setBackgroundColor(Color.GRAY);
                selectMale = true;
            }
        });

        femaleSelectionBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                selectMale = false;
                femaleSelectionBtnView.setBackgroundColor(Color.parseColor("#009688"));
                femaleSelectionBtnView.setAlpha(1.0f);
                maleSelectionBtnView.setAlpha(.5f);
                maleSelectionBtnView.setBackgroundColor(Color.GRAY);
            }
        });

        continueGenderBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                final FirebaseUser user = mAuth.getCurrentUser();
                String UID = user.getUid();
                CollectionReference users = firebaseFirestore.collection("users");
                if (selectMale){
                    Map<String, Object> data = new HashMap<>();
                    data.put("Gender","male");
                    //data.put("connection","");
                    users.document(UID).set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("Register Gender", "DocumentSnapshot successfully written!");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Register Gender", "Error writing document", e);
                        }
                    });
                    users.document(UID).collection("connection").document("yes");
                    users.document(UID).collection("connection").document("no");
                }
                else {
                    Map<String, Object> data = new HashMap<>();
                    data.put("Gender","female");
                    //data.put("connection","");
                    users.document(UID).set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("Register Gender", "DocumentSnapshot successfully written!");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Register Gender", "Error writing document", e);
                        }
                    });
                    users.document(UID).collection("connection").document("yes");
                    users.document(UID).collection("connection").document("no");
                }
                Intent intent = new Intent(RegisterGenderActivity.this, RegisterNameActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}