package com.wefriend.wefriend.Main.Friends;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wefriend.wefriend.Main.Friends.Chat;
import com.wefriend.wefriend.R;
import com.wefriend.wefriend.account.Account;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context context;
    List<Chat> mChat;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public ChatAdapter(@NonNull Context context, @NonNull List<Chat> mChat) {
        this.context = context;
        this.mChat = mChat;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_LEFT){
            View view = LayoutInflater.from(context).inflate(R.layout.chat_items_left, parent, false);
            return (new ChatAdapter.ViewHolder(view));
        }
        else if(viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.chat_items_right, parent, false);
            return (new ChatAdapter.ViewHolder(view));
        }
        else{
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = mChat.get(position);
        holder.messageTextView.setText(chat.getMessage());
        if (chat.getMessage()==null || chat.getMessage() == ""){
            holder.messageTextView.setText("Null");
        }
        if (chat.getProfileImageUrl() == null){
            holder.profilePic.setImageResource(R.drawable.avatar);
        }
        else{
            Glide.with(context).load(chat.getProfileImageUrl()).into(holder.profilePic);
        }
    }



    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView profilePic;
        public TextView messageTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = (ImageView) itemView.findViewById(R.id.chat_profile_image_view);
            messageTextView = (TextView)itemView.findViewById(R.id.chat_message);
        }
    }

    @Override
    public int getItemViewType(int position) {
        String currentUser = mAuth.getCurrentUser().getUid();
        if (currentUser.equals(mChat.get(position).getSender())){
            return MSG_TYPE_RIGHT;
        }
        else{
            return MSG_TYPE_LEFT;
        }
    }
}
