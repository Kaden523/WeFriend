package com.wefriend.wefriend.Main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.wefriend.wefriend.Main.Friends.Chat;
import com.wefriend.wefriend.Main.Friends.ChatActivity;
import com.wefriend.wefriend.Main.Friends.FriendsActivity;
import com.wefriend.wefriend.R;
import com.wefriend.wefriend.account.Account;
import com.wefriend.wefriend.account.EditAccountActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BufferActivity extends AppCompatActivity {

    Map chatIDList;
    private Button likeButton, dislikeButton;
    private LinearLayout searchMoreLayout;
    static Account mAccount;
    static List<String> matchedID;
    static List<String> searchedID;
    static List<Account> initial_search;
    private ArrayAdapter arrayAdapter;
    private int i;
    private SwipeFlingAdapterView flingContainer;
    public static String college;
    private String UID;
    private DocumentReference userRef;
    private FirebaseAuth mAuth;
    ImageView searchMoreImageView;
    static String preference;
    ProgressDialog TempDialog;
    int count = 0;
    private DatabaseReference mDatabaseReference;
    private FirebaseFirestore mFirebaseFirestore;
    CollectionReference mCollectionReference;
    ArrayList<Account> search_result;
    StorageReference imageReference;
    FirebaseFirestoreSettings settings;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate (savedInstanceState);
        setContentView (R.layout.buffer_layout);

        chatIDList = new HashMap();
        search_result = new ArrayList<>();
        matchedID = new ArrayList<>();
        searchedID = new ArrayList<>();
        mAccount = new Account(null,null,null,null,null,
                null,null,null,null);
        initial_search = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        matchedID = new ArrayList<>();
        searchedID = new ArrayList<>();
        settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("school");
        //StorageReference filepath = FirebaseStorage.getInstance().getReference().child("Profile_image").child(userId);
        imageReference = FirebaseStorage.getInstance().getReference().child("Profile_image");
        //mFirebaseFirestore.setFirestoreSettings(settings);
        mCollectionReference = mFirebaseFirestore.collection("users");
        UID = mAuth.getUid();
        userRef = FirebaseFirestore.getInstance().collection("users").document(UID);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d("Buffer Activity", "DocumentSnapshot data: " + document.getData());
                    college = document.get("school").toString();
                } else {
                    Log.d("Buffer Activity", "No such document");
                }
            } else {
                Log.d("Buffer Activity", "get failed with ", task.getException());
                }
            }
        });
        SearchDatabase mSearch = new SearchDatabase();
        //mSearch.searchCollege(college,UID);
        //getUserAccount(UID);
        //getMatched(UID);
        //getSearchedID(UID);
        mSearch.searchCollege(college,UID);
        mSearch.getUserAccount(UID);
        mSearch.getMatchedID(UID);
        mSearch.getSearchedID(UID);
        mSearch.getUserAccount(UID);
        mSearch.resetArray();

        TempDialog = new ProgressDialog(BufferActivity.this);
        TempDialog.setMessage("Loading");
        TempDialog.setCancelable(false);
        TempDialog.setProgress(count);
        TempDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        TempDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));

        //ArrayList<Account> initial_search = getSearch_result();
        //Intent intent = getIntent();

        if (preference == null){
            preference = "0";
        }
        //preference = "2";

        //final ArrayList<Account> friendsList = getMatched(UID);
        //al.add(Integer.toString(search_name.size()));

        final List<Account> items = new ArrayList<>();
        items.add(new Account(null,null,null,null,null,
                null,null,null,null));
        arrayAdapter = new ArrayAdapter(this, R.layout.items, items);
        arrayAdapter.notifyDataSetChanged();

        TempDialog.show();
        new CountDownTimer(2000,1000) {
            @Override
            public void onTick(long millsUntilFinished) {
                TempDialog.setMessage("Loading...");
            }
            @Override
            public void onFinish() {
                TempDialog.dismiss();
                Intent intent = new Intent(BufferActivity.this, MainActivity.class);
                //MainActivity.matchedID = matchedID;
                //MainActivity.searchedID = searchedID;
                //MainActivity.mAccount = mAccount;
                startActivity(intent);
            }
        }.start();

    }

    static void makeToast(Context ctx, String s){
        Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
    }

    public void setCollege(String college){
        this.college = college;
    }

    public void isConnectionMatch(final String userID) {
        //final DocumentReference currentUserConnectionDb = userRef.collection("connection").document(userID);
        final DocumentReference documentReference = userRef.collection("connection").document(userID);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            String key = "";

            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.get("matched").toString().equals("yes")){
                    if(documentSnapshot.get("ChatID") == null){
                        Map newChat = new HashMap();
                        newChat.put("user_1",UID);
                        newChat.put("user_2",userID);
                        CollectionReference mColl = FirebaseFirestore.getInstance().collection("chats");
                        key = mColl.document().getId();
                        mColl.document(key).set(newChat);
                        Map chatID = new HashMap();
                        chatID.put("ChatID", key);
                        userRef.collection("connection").document(userID).update(chatID);
                        FirebaseFirestore.getInstance().collection("users").document(userID)
                                .collection("connection").document(UID).update(chatID);
                    }
                }
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //SearchDatabase mSearch = new SearchDatabase();
        //mSearch.resetArray();
        //mSearch.searchCollege(college,UID);
    }

}
