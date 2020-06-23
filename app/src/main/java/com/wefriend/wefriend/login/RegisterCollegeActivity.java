package com.wefriend.wefriend.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wefriend.wefriend.Main.BufferActivity;
import com.wefriend.wefriend.Main.MainActivity;
import com.wefriend.wefriend.Main.SearchDatabase;
import com.wefriend.wefriend.R;
import com.wefriend.wefriend.account.Account;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterCollegeActivity extends AppCompatActivity {
    private EditText majorEditTextView;
    private Spinner selectSchoolRegisterSpinnerView;
    private Button schoolContinueBtn;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_college);
        majorEditTextView = (EditText) findViewById(R.id.register_major);
        selectSchoolRegisterSpinnerView = (Spinner) findViewById(R.id.select_school_register);
        schoolContinueBtn = (Button) findViewById(R.id.schoolContinueButton);
        schoolContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FirebaseUser user = mAuth.getCurrentUser();
                String UID = user.getUid();
                CollectionReference users = firebaseFirestore.collection("users");
                String major = majorEditTextView.getText().toString();
                String college = selectSchoolRegisterSpinnerView.getSelectedItem().toString();
                String default_college = "Please select your college";
                if(!college.equals(default_college)) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("school", college);
                    data.put("major", major);
                    users.document(UID).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("Register College", "DocumentSnapshot successfully written!");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Register College", "Error writing document", e);
                        }
                    });
                    Intent intent = new Intent(RegisterCollegeActivity.this, BufferActivity.class);
                    MainActivity.college = college;

                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(RegisterCollegeActivity.this, "Please select your School",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}