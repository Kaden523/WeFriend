package com.wefriend.wefriend.Main.Friends;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.wefriend.wefriend.R;
import com.wefriend.wefriend.account.Account;

import java.util.List;

public class FriendsProfileAdapter extends ArrayAdapter<Account> {
    private int resourceId;
    private Context mContext;

    public FriendsProfileAdapter(@NonNull Context context, int resource, @NonNull List<Account> objects) {
        super(context, resource, objects);
        resourceId = resource;
        this.mContext = context;
    }
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Account account = getItem(position);
        View view;
        ViewHolder viewHolder;
        String profileImageUrl;
        viewHolder = new ViewHolder();
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder.personPic = (ImageView) view.findViewById(R.id.friends_profile_image);
            viewHolder.personName = (TextView) view.findViewById(R.id.friend_name);
            viewHolder.imageButton = (ImageButton) view.findViewById(R.id.videoCalBtn);
        } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
        }
        profileImageUrl = account.getProfileImage();

        if (profileImageUrl != null && !profileImageUrl.equals("")) {
            try {
                Glide.with(mContext).load(profileImageUrl).into(viewHolder.personPic);
            }
            catch(Exception e){
                Log.d("Friend Profile Adapter", "Image Error");
            }
        } else {
            try {
                viewHolder.personPic.setImageResource(R.drawable.avatar);
            }
            catch(Exception e){
                Log.d("Friend Profile Adapter", "Image View Error");
            }
        }
            //case "defaultFemale":
            //Glide.with(mContext).load(R.drawable.default_woman).into(viewHolder.personPic);
            //break;
            //case "defaultMale":
            //Glide.with(mContext).load(R.drawable.default_man).into(viewHolder.personPic);
            //break;
            //default:
            //Glide.with(mContext).load(profileImageUrl).into(viewHolder.personPic);
            //break;
            //}
        try {
            viewHolder.personName.setText(account.getFirst_name() + " " + account.getLast_name());
            viewHolder.imageButton.setFocusable(false);
        }
        catch(Exception e){
            Log.d("Friend Profile Adapter", "Adapter Array Error");
        }

        return view;
    }


    class ViewHolder{
        ImageView personPic;
        TextView personName;
        ImageButton imageButton;
    }
}


