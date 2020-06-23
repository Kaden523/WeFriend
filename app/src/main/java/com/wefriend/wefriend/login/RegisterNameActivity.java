package com.wefriend.wefriend.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wefriend.wefriend.Main.MainActivity;
import com.wefriend.wefriend.R;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RegisterNameActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private EditText firstNameEditTextView, lastNameEditTextView;
    private DatePicker birthDatePicker;
    private Button continueBtn;
    private String firstName, lastName;
    private Date birthday;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_name);
        firstNameEditTextView = (EditText) findViewById(R.id.first_name);
        lastNameEditTextView = (EditText) findViewById(R.id.last_name);
        birthDatePicker = (DatePicker) findViewById(R.id.birthDate);
        continueBtn = (Button) findViewById(R.id.nameContinue);
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FirebaseUser user = mAuth.getCurrentUser();
                String UID = user.getUid();
                CollectionReference users = firebaseFirestore.collection("users");
                firstName = firstNameEditTextView.getText().toString();
                lastName = lastNameEditTextView.getText().toString();
                Calendar calender = Calendar.getInstance();
                calender.set(Calendar.MONTH, birthDatePicker.getMonth());
                calender.set(Calendar.DAY_OF_MONTH, birthDatePicker.getDayOfMonth());
                calender.set(Calendar.YEAR, birthDatePicker.getYear());
                birthday = calender.getTime();
                Map<String, Object> data = new HashMap<>();
                data.put("First_name",firstName);
                data.put("Last_name",lastName);
                data.put("birth_day", birthday);
                users.document(UID).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("Register Name", "DocumentSnapshot successfully written!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Register Name", "Error writing document", e);
                    }
                });
                Intent intent = new Intent(RegisterNameActivity.this, RegisterCollegeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}