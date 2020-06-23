package com.wefriend.wefriend.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wefriend.wefriend.Main.BufferActivity;
import com.wefriend.wefriend.Main.MainActivity;
import com.wefriend.wefriend.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditAccountActivity extends AppCompatActivity {
    private ImageView avatarImageView;
    private Button saveButton;
    private Spinner selectSchoolSpinnerView;
    private EditText editAboutMeEditTextViiew, editPhoneEditTextView;
    private CheckBox sportCheckBox, travelCheckBox, musicCheckBox, readingCheckBox;
    private RadioButton maleRadioBtn, femaleRadioBtn;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Uri resultUri;
    private String userId, profileImageUri;
    private DocumentReference mDoc;
    private DatabaseReference mPhotoDB;
    private Account account;
    private ImageButton backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_account);
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        mDoc = FirebaseFirestore.getInstance().collection("users").document(userId);
        account = MainActivity.getAccount();
        avatarImageView = (ImageView) findViewById(R.id.profileImage);
        selectSchoolSpinnerView = (Spinner) findViewById(R.id.select_school);
        editAboutMeEditTextViiew = (EditText) findViewById(R.id.edit_aboutme);
        editPhoneEditTextView = (EditText) findViewById(R.id.edit_phone);
        sportCheckBox = (CheckBox) findViewById(R.id.checkbox_sports);
        travelCheckBox = (CheckBox) findViewById(R.id.checkbox_travel);
        musicCheckBox = (CheckBox) findViewById(R.id.checkbox_music);
        readingCheckBox = (CheckBox) findViewById(R.id.readingCheckBox);
        maleRadioBtn = (RadioButton) findViewById(R.id.maleSelction);
        femaleRadioBtn = (RadioButton) findViewById(R.id.femaleSelection);
        saveButton = (Button) findViewById(R.id.save_profile);
        backBtn = (ImageButton) findViewById(R.id.back);
        userId = mAuth.getInstance().getCurrentUser().getUid();
        mPhotoDB = FirebaseDatabase.getInstance().getReference().child("Profile_image").child(userId);
        if(account.getProfileImage()!=null){
            Glide.with(this).load(account.getProfileImage())
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).fitCenter().into(avatarImageView);
        }
        avatarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInfo();
                saveUserPhoto();
                Intent intent = new Intent(EditAccountActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
    private void saveUserPhoto() {
        if (resultUri != null) {
            final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("Profile_image").child(userId);
            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            final UploadTask uploadTask = filepath.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                }
            });

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Map userInfo = new HashMap();
                            userInfo.put("profileImageUrl", url);
                            mDoc.update(userInfo);
                        }
                    });
                    //Map userInfo = new HashMap();
                    //userInfo.put("profileImageUrl", downloadUrl.toString());
                    //mDoc.update(userInfo);
                    return;
                }
            });
        }
        Intent intent = new Intent(EditAccountActivity.this, BufferActivity.class);
        MainActivity.clearAllValues();
        startActivity(intent);
    }
    public void saveUserInfo(){
        String school = selectSchoolSpinnerView.getSelectedItem().toString();
        String defaulSchool = "Please select your college";
        String blankSchool = "-------------------------------------------------";
        String sex = null;
        String aboutMe = editAboutMeEditTextViiew.getText().toString();
        if (maleRadioBtn.isChecked()){
            sex = "male";
        }
        else if (femaleRadioBtn.isChecked()){
            sex = "female";
        }
        String phoneNumber = editPhoneEditTextView.getText().toString();
        Map userInfo = new HashMap();
        List<String> interests = new ArrayList<>();
        if (school != null && !school.equals(defaulSchool) && !school.equals(blankSchool)){
            userInfo.put("school",school);
        }
        if (sex != null){
            userInfo.put("Gender",sex);
        }
        if (aboutMe != ""){
            userInfo.put("introduction",aboutMe);
        }
        if (phoneNumber != ""){
            userInfo.put("phone_number",phoneNumber);
        }
        if(sportCheckBox.isChecked()){
            interests.add("sports");
        }
        if(readingCheckBox.isChecked()){
            interests.add("reading");
        }
        if (travelCheckBox.isChecked()){
            interests.add("travel");
        }
        if (musicCheckBox.isChecked()){
            interests.add("music");
        }
        if (interests.size()>0){
            userInfo.put("interests",interests);
        }
        if (userInfo != null && !userInfo.isEmpty()){
            mDoc.update(userInfo);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            avatarImageView.setImageURI(resultUri);
        }
    }
}
