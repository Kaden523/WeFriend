package com.wefriend.wefriend.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wefriend.wefriend.R;

public class RegisterActivity extends AppCompatActivity {
    private Button register;
    private EditText email, password, passwordConf;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null && !user.isEmailVerified()){
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };
        register = (Button) findViewById(R.id.btn_register);
        email = (EditText) findViewById(R.id.reg_email);
        password = (EditText)findViewById(R.id.reg_password);
        passwordConf = (EditText) findViewById(R.id.reg_password_confirm);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String newEmail = email.getText().toString();
                final String newPassword = password.getText().toString();
                final String newPasswordConf = passwordConf.getText().toString();
                if (newPassword.equals(newPasswordConf)){
                    mAuth.createUserWithEmailAndPassword(newEmail, newPassword).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this, "This Email address is already in used", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(RegisterActivity.this, "Please check your email address for verification", Toast.LENGTH_SHORT).show();
                                            email.setText("");
                                            password.setText("");
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(RegisterActivity.this, "Passwords not match", Toast.LENGTH_SHORT).show();
                    email.setText("");
                    password.setText("");
                }
            }
        });
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
