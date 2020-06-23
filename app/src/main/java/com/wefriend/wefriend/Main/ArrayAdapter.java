package com.wefriend.wefriend.Main;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.wefriend.wefriend.R;
import com.wefriend.wefriend.account.Account;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import java.util.List;

/**Created by Tianxin Chen on 6/15/2020.*/

public class ArrayAdapter extends android.widget.ArrayAdapter<Account> {
    Context context;

    public ArrayAdapter(Context context, int resourceID, List<Account> items){
        super(context,resourceID,items);
        this.context = context;
    }
    public View getView(int position, View convertView, ViewGroup parent){
        Account account = getItem(position);
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.items, parent, false);
        }
        if(account.getUserID()!=null) {
            TextView name = (TextView) convertView.findViewById(R.id.helloText);
            TextView major = (TextView) convertView.findViewById(R.id.card_major);
            ImageView avatar = (ImageView) convertView.findViewById(R.id.main_image);
            name.setText(account.getFirst_name() + " " + account.getLast_name());
            major.setText(account.getMajor());
            String profileImage = account.getProfileImage();
            if (profileImage != null && !profileImage.equals("")) {
                Glide.with(getContext()).load(profileImage).fitCenter()
                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).into(avatar);
            } else {
                avatar.setImageResource(R.drawable.avatar);
            }
        }
        else{
            TextView name = (TextView) convertView.findViewById(R.id.helloText);
            TextView major = (TextView) convertView.findViewById(R.id.card_major);
            ImageView avatar = (ImageView) convertView.findViewById(R.id.main_image);
            name.setText("Swipe left or right");
            major.setText("");
            avatar.setImageResource(R.drawable.swipe_to_start);
        }
        return convertView;
    }

}
