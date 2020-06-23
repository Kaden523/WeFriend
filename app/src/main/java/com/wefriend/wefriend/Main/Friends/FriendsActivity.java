package com.wefriend.wefriend.Main.Friends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.r0adkll.slidr.Slidr;
import com.wefriend.wefriend.Main.MainActivity;
import com.wefriend.wefriend.Main.SearchDatabase;
import com.wefriend.wefriend.R;
import com.wefriend.wefriend.account.Account;


import java.util.ArrayList;
import java.util.Locale;

public class FriendsActivity extends AppCompatActivity {
    public static String UID;
    public static ArrayList<Account> friendsList;
    private ArrayList<Account> copyList;
    private ArrayList<Account> displayList;
    FriendsProfileAdapter mAdapter;
    private EditText search;
    FirebaseAuth mAuth;
    private ImageButton backBtn;
    String ChatID = "";
    ListView listView;
    DocumentReference documentReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        setDisplayList(friendsList);
        copyList = displayList;
        mAdapter = new FriendsProfileAdapter(FriendsActivity.this, R.layout.friends_items_layout, displayList);
        listView = (ListView) findViewById(R.id.matchList);
        listView.setAdapter(mAdapter);
        mAuth = FirebaseAuth.getInstance();
        Slidr.attach(this);
        backBtn = (ImageButton) findViewById(R.id.back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(FriendsActivity.this, MainActivity.class);
                //startActivity(intent);
                finish();
            }
        });

        //documentReference = FirebaseFirestore.getInstance().collection("users").document(UID)
        //        .collection("connection").document(userID);
        if (documentReference != null) {
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("Check ChatID", "DocumentSnapshot data: " + document.getData());
                            ChatID = document.get("ChatID").toString();
                        } else {
                            Log.d("Check ChatID", "No such document");
                        }
                    } else {
                        Log.d("Check ChatID", "get failed with ", task.getException());
                    }
                }
            });
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Account account = displayList.get(position);
                String userID = account.getUserID();
                String UID = mAuth.getCurrentUser().getUid();
                SearchDatabase mSearch = new SearchDatabase();
                ChatID = mSearch.checkChatID(UID, userID);
                String name = (account.getFirst_name()+" "+account.getLast_name());
                ChatActivity.chat = mSearch.getChat(UID,ChatID);
                final Intent intent = new Intent(FriendsActivity.this, ChatActivity.class);
                intent.putExtra("userID", userID);
                intent.putExtra("name",name);
                intent.putExtra("profileImage", account.getProfileImage());
                intent.putExtra("ChatID",ChatID);
                Log.d("Friends Activity", "onItemClick: The list has been clicked");
                startActivity(intent);
            }
        });
        search = (EditText) findViewById(R.id.searchBar);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                searchText();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchText();
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchText();
            }
        });
    }




    private void searchText() {
        String text = search.getText().toString().toLowerCase(Locale.getDefault());
        if (text.length() != 0) {
            if (displayList.size() != 0) {
                //mAdapter.clear();
                displayList.removeAll(displayList);
                mAdapter.notifyDataSetChanged();
                for (Account user : copyList) {
                    if (user.getLast_name().toLowerCase(Locale.getDefault()).contains(text) ||
                            user.getFirst_name().toLowerCase(Locale.getDefault()).contains(text)) {
                        displayList.add(user);
                        //mAdapter = new FriendsProfileAdapter(FriendsActivity.this, R.layout.friends_items_layout, displayList);
                        //listView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        } else {
            displayList.clear();
            for (Account user: copyList)
            displayList.add(user);
            //mAdapter = new FriendsProfileAdapter(FriendsActivity.this, R.layout.friends_items_layout, displayList);
            //listView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
        mAdapter.notifyDataSetChanged();
    }

    public void setDisplayList(ArrayList<Account> displayList) {
        this.displayList = displayList;
    }
}