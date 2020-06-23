package com.wefriend.wefriend.Main.Friends;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.r0adkll.slidr.Slidr;
import com.wefriend.wefriend.Main.ArrayAdapter;
import com.wefriend.wefriend.Main.MainActivity;
import com.wefriend.wefriend.R;
import com.wefriend.wefriend.account.Account;

import org.w3c.dom.Document;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    private EditText sendMessageEditTextView;
    private TextView userNameTextView;
    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference mReference;
    private ImageButton sendMessageBtn;
    private DatabaseReference mChatReference;
    private CollectionReference reference;
    //private StorageReference filepath,currentFilepath;
    private String profileImage = null;
    Intent intent;
    private List<Chat> mChat = new ArrayList<>();
    private ChatAdapter chatAdapter;
    private RecyclerView recyclerView;
    String imageUrl;
    public static String ChatID;
    String UID;
    public static List<Chat> chat;
    private ImageButton exitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        setFirebase();
        setChatAdapter();
        Slidr.attach(this);


        profileImageView = (CircleImageView) findViewById(R.id.chat_profile_image);
        sendMessageEditTextView = (EditText) findViewById(R.id.SendMessage);
        userNameTextView = (TextView) findViewById(R.id.chat_userName);
        toolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        sendMessageBtn = (ImageButton) findViewById(R.id.send_message_btn);
        exitBtn = (ImageButton) findViewById(R.id.exit_chat);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        final FirebaseUser mUser = mAuth.getCurrentUser();

        intent = getIntent();
        profileImage = intent.getStringExtra("profileImage");
        final String userID = intent.getStringExtra("userID");
        String name = intent.getStringExtra("name");
        String chatID = intent.getStringExtra("ChatID");
        if (chatID != null && !chatID.equals("")){
            ChatID = chatID;
        }

        CollectionReference collec = FirebaseFirestore.getInstance().collection("chats");

        StorageReference filepath = FirebaseStorage.getInstance().getReference().child("Profile_image")
                .child(mAuth.getCurrentUser().getUid());
        filepath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                imageUrl = task.getResult().toString();
            }
        });

        userNameTextView.setText(name);

        Glide.with(this).load(profileImage).into(profileImageView);
        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = sendMessageEditTextView.getText().toString();
                if (!text.equals("")){
                    sendMessage(mUser.getUid(), userID, text);
                }
                getChat(mUser.getUid(),ChatID);
                sendMessageEditTextView.setText("");
            }
        });

        if (MainActivity.chatIDList.containsKey(userID)){
            ChatID = MainActivity.chatIDList.get(userID).toString();
        }
        getChat(mUser.getUid(),ChatID);
        chatAdapter.notifyDataSetChanged();

    }


    public void sendMessage(String sender, String receiver, String text){

        Map chat = new HashMap();
        chat.put("sender",sender);
        chat.put("receiver",receiver);
        chat.put("message",text);
        chat.put("profileImageUrl",imageUrl);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        chat.put("time",timeStamp);
        reference.document(ChatID).collection(sender).document().set(chat);
        reference.document(ChatID).collection(receiver).document().set(chat);
        Map active = new HashMap();
        //active.put("last_active",timeStamp);
        //FirebaseFirestore.getInstance().collection("users").document(sender)
        //        .collection("connection").document(receiver).update(active);
        //FirebaseFirestore.getInstance().collection("users").document(receiver)
        //        .collection("connection").document(sender).update(active);
        //mChatReference.child("chats").push().setValue(chat);
    }

    public List<Chat> getChat(final String mUID, final String ChatID){
        final List<Chat>[] chatList = new List[]{new ArrayList<>()};
        String name = intent.getStringExtra("name");
        if(ChatID != null && !ChatID.equals("")){
            CollectionReference mColl =  reference.document(ChatID).collection(mUID);

            final List<Chat> finalChatList = chatList[0];
            mColl.orderBy("time").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    mChat = new ArrayList<>();
                    for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                Log.d("Search Chat", "New chat: " + dc.getDocument().getData().get("message"));
                                String sender = dc.getDocument().get("sender").toString();
                                String receiver = dc.getDocument().get("receiver").toString();
                                String message = dc.getDocument().get("message").toString();
                                if(dc.getDocument().get("profileImageUrl")!=null){
                                    String profileImageUrl = dc.getDocument().get("profileImageUrl").toString();
                                    Chat chat = new Chat(sender, receiver,message,profileImageUrl);
                                    mChat.add(chat);
                                    finalChatList.add(chat);
                                    chatAdapter.notifyDataSetChanged();
                                } else{
                                    Chat chat = new Chat(sender, receiver,message,null);
                                    mChat.add(chat);
                                    finalChatList.add(chat);
                                    chatAdapter.notifyDataSetChanged();
                                }
                                chatAdapter = new ChatAdapter(ChatActivity.this, mChat);
                                recyclerView.setAdapter(chatAdapter);
                                chatAdapter.notifyDataSetChanged();
                                break;
                            case MODIFIED:
                                Log.d("Search Chat", "Modified chat: " + dc.getDocument().getData());
                                break;
                            case REMOVED:
                                Log.d("Search Chat", "Removed chat: " + dc.getDocument().getData());
                                break;
                        }
                        chatList[0] = finalChatList;
                    }
                    chatAdapter.notifyDataSetChanged();
                }
            });
        }
        else{
            Log.d("Search Chat", "No ChatID");
        }
        chatAdapter.notifyDataSetChanged();
        return chatList[0];
    }

    private void setChatAdapter(){
        recyclerView = (RecyclerView) findViewById(R.id.chat_recycler_view);
        chatAdapter = new ChatAdapter(this, mChat);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(chatAdapter);
    }
    private void setFirebase(){
        mAuth = FirebaseAuth.getInstance();
        mChatReference = FirebaseDatabase.getInstance().getReference();
        UID = mAuth.getCurrentUser().getUid();
        reference = FirebaseFirestore.getInstance().collection("chats");
    }

}