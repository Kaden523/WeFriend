package com.wefriend.wefriend.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wefriend.wefriend.R;
import com.wefriend.wefriend.login.LoginActivity;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText oldPasswordText, newPasswordText;
    private Button changePasswordBtn;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        mAuth = FirebaseAuth.getInstance();

        oldPasswordText = (EditText) findViewById(R.id.change_password);
        newPasswordText = (EditText) findViewById(R.id.new_password_confirm);
        changePasswordBtn = (Button) findViewById(R.id.btn_change_password);
        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FirebaseUser user = mAuth.getCurrentUser();

                AuthCredential credential = EmailAuthProvider
                        .getCredential(user.getEmail(), oldPasswordText.getText().toString());

                // Prompt the user to re-provide their sign-in credentials
                user.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Change Password", "User re-authenticated.");
                        Toast.makeText(ChangePasswordActivity.this, "Your password is successfully changed!",Toast.LENGTH_SHORT).show();;
                        user.updatePassword(newPasswordText.getText().toString());
                        mAuth.signOut();
                        Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                        //finish();
                        finishAffinity();
                    }
                });
            }
        });

    }
}