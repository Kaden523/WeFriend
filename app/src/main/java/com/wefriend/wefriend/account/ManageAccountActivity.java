package com.wefriend.wefriend.account;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.wefriend.wefriend.Main.MainActivity;
import com.wefriend.wefriend.R;
import com.wefriend.wefriend.introduction.Introduction;

public class ManageAccountActivity extends AppCompatActivity {
    private Button changePasswordBtn, deleteAccountButton,confirmDeleteBtn, deferDeleteBtn;
    private TextView userNameTextView,userAccountTextView;
    private ImageView userProfileImageView;
    private ImageButton backBtn;
    FirebaseAuth mAuth;
    private Account mAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);
        mAuth = FirebaseAuth.getInstance();
        String email = mAuth.getCurrentUser().getEmail();
        userNameTextView = (TextView) findViewById(R.id.manage_account_name);
        userAccountTextView = (TextView) findViewById(R.id.manage_account_email);
        userProfileImageView = (ImageView) findViewById(R.id.manage_account_image);
        changePasswordBtn = (Button) findViewById(R.id.manage_account_password);
        deleteAccountButton = (Button) findViewById(R.id.delete_account);
        backBtn = (ImageButton) findViewById(R.id.back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        this.mAccount = MainActivity.getAccount();
        String name = mAccount.getFirst_name()+" "+mAccount.getLast_name();
        String profileImage = mAccount.getProfileImage();
        if(profileImage !=null && !profileImage.equals("")){
            Glide.with(this).load(profileImage).into(userProfileImageView);
        }
        userNameTextView.setText(name);
        userAccountTextView.setText(email);
        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageAccountActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });

        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //alertMessageLayout.setVisibility(View.VISIBLE);
                final AlertDialog.Builder alert = new AlertDialog.Builder(ManageAccountActivity.this);
                alert.setMessage("Are you sure you want to delete this account?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mAuth.getCurrentUser().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ManageAccountActivity.this, "Successfully delete the account!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ManageAccountActivity.this, Introduction.class);
                                MainActivity.clearAllValues();
                                startActivity(intent);
                                finishAffinity();
                                finish();
                            }
                        });

                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alert.create().show();
            }
        });
    }
}