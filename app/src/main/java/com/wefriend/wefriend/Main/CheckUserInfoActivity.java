package com.wefriend.wefriend.Main;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.r0adkll.slidr.Slidr;
import com.wefriend.wefriend.R;
import com.wefriend.wefriend.account.Account;

public class CheckUserInfoActivity extends AppCompatActivity {
    private TextView userAboutMeTextView, userNameTextView, userMajorTextView,userInterestsTextView;
    private ImageView userProfileImageView;
    private ImageButton backBtn;
    static Account account;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_user_info);
        Slidr.attach(this);
        userAboutMeTextView = (TextView) findViewById(R.id.userInfo_aboutme);
        userInterestsTextView = (TextView) findViewById(R.id.checkInfo_interests);
        userNameTextView = (TextView) findViewById(R.id.checkInfo_name);
        userMajorTextView = (TextView) findViewById(R.id.checkInfo_major);
        userProfileImageView = (ImageView) findViewById(R.id.checkInfo_profileImage);
        backBtn = (ImageButton) findViewById(R.id.back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();;
            }
        });
        if(account == null){
            account = new Account(null,null,null,null,
                    null,null,null,null,null);
        }
        setScreen(account);
    }

    public void setScreen(Account account){
        String gender = null;

        //set name
        if(account.getFirst_name() != null || account.getLast_name()!=null){
            userNameTextView.setText(account.getFirst_name()+" " + account.getLast_name());
        }

        //set gender
        if(account.getGender()!=null){
            gender = account.getGender();
        }

        //set introduction
        if (account.getAboutMe() != null && !account.getAboutMe().equals("")){
            userAboutMeTextView.setText(account.getAboutMe());
        }
        else {
            if (gender.equals("male")){
                userAboutMeTextView.setText("He didn't write anything about himself.");
            }
            else if(gender.equals("female")){
                userAboutMeTextView.setText("She didn't write anything about herself.");
            }
        }
        //set major
        if (account.getMajor() !=null && !account.getMajor().equals("")){
            userMajorTextView.setText(account.getMajor());
        }

        //set profile image
        if(account.getProfileImage()!=null){
            Glide.with(this).load(account.getProfileImage())
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).fitCenter().into(userProfileImageView);
        }
        //set interests
    }

    public void setAccount(Account account){
        this.account = account;
    }
}