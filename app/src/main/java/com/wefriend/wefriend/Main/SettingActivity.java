package com.wefriend.wefriend.Main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.wefriend.wefriend.R;
import com.wefriend.wefriend.account.ManageAccountActivity;
import com.wefriend.wefriend.introduction.Introduction;

public class SettingActivity extends AppCompatActivity {
    private Button logoutBtn, manageAccountBtn;
    private ImageButton backBtn;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        logoutBtn = findViewById(R.id.log_out);
        backBtn = findViewById(R.id.back);
        manageAccountBtn = findViewById(R.id.setting_manage_account);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(SettingActivity.this, Introduction.class);
                MainActivity.clearAllValues();
                startActivity(intent);
                finishAffinity();
                finish();
            }
        });
        manageAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, ManageAccountActivity.class);
                startActivity(intent);
            }
        });
    }
}