package com.wefriend.wefriend.Main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.util.Size;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import com.squareup.okhttp.internal.http.HeaderParser;
import com.wefriend.wefriend.Main.Friends.FriendsActivity;
import com.wefriend.wefriend.R;
import com.wefriend.wefriend.account.Account;
import com.wefriend.wefriend.account.EditAccountActivity;
import com.wefriend.wefriend.introduction.Introduction;
import com.wefriend.wefriend.login.RegisterGenderActivity;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private Button likeButton, dislikeButton;
    private LinearLayout searchMoreLayout;
    static Account mAccount;
    static List<String> matchedID = new ArrayList<>();
    private ArrayAdapter arrayAdapter;
    private int i;
    private SwipeFlingAdapterView flingContainer;
    private ActionBarDrawerToggle toggle;
    private NavigationView drawerNavigationView;
    public static String college;
    private String UID;
    private List<Account> items;
    private DocumentReference userRef;
    private FirebaseAuth mAuth;
    public static Map chatIDList = new HashMap();
    public static List<Account> friendsList;
    ImageView searchMoreImageView;
    ImageView headerImage;
    String preference;
    TextView headerTextView, searchMoretextView;
    ProgressDialog TempDialog;
    CountDownTimer mCountDownTimer;
    int count = 0;
    static List<String> searchedID = new ArrayList<>();


    public MainActivity() {
    }

    public static Account getAccount() {
        return mAccount;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);

        chatIDList = new HashMap();

        searchMoreLayout = (LinearLayout) findViewById(R.id.search_more_layout);
        searchMoreLayout.setVisibility(View.INVISIBLE);
        likeButton =  (Button) findViewById(R.id.like);
        dislikeButton = (Button) findViewById(R.id.dislike);
        mAuth = FirebaseAuth.getInstance();
        UID = mAuth.getUid();
        userRef = FirebaseFirestore.getInstance().collection("users").document(UID);

        matchedID = BufferActivity.matchedID;
        searchedID = BufferActivity.searchedID;
        mAccount = BufferActivity.mAccount;


        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("Main Activity", "DocumentSnapshot data: " + document.getData());
                        college = document.get("school").toString();
                    } else {
                        Log.d("Main Activity", "No such document");
                    }
                } else {
                    Log.d("Main Activity", "get failed with ", task.getException());
                }
            }
        });


        SearchDatabase mSearch = new SearchDatabase();
        mSearch.resetArray();
        mSearch.searchCollege(college,UID);
        mSearch.getUserAccount(UID);
        mSearch.getMatched(UID);
        mSearch.getSearchedID(UID);
        mSearch.getMatchedID(UID);


        TempDialog = new ProgressDialog(MainActivity.this);
        TempDialog.setCancelable(false);
        TempDialog.setProgress(count);
        TempDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        TempDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
        TempDialog.show();
        new CountDownTimer(2000,1000) {
            @Override
            public void onTick(long millsUntilFinished) {
                TempDialog.setMessage("Loading...");
            }
            @Override
            public void onFinish() {
                TempDialog.dismiss();
            }
        }.start();



        //Log.d("User Account Debug",mAccount.getFirst_name());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (String s: matchedID){
            Log.d("Debug Matched",s);
        }
        Log.d("Debug Account",mAccount.getFirst_name());

        List<Account> initial_search = BufferActivity.initial_search;

        preference = BufferActivity.preference;
        if (preference == null || preference.equals("")){
            preference = "0";
        }

        final ArrayList<Account> friendsList = mSearch.getMatched(UID);

        items= searchFilter(initial_search);
        arrayAdapter = new ArrayAdapter(this, R.layout.items, items);

        drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);

        final LinearLayout header = findViewById(R.id.navigation_header);

        drawerNavigationView = (NavigationView) findViewById(R.id.drawer);
        searchMoreImageView  = (ImageView) findViewById(R.id.search_more_image);
        searchMoretextView = (TextView) findViewById(R.id.search_more_text);
        searchMoretextView.setClickable(true);
        searchMoretextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchFilterActivity.class);
                startActivity(intent);
            }
        });

        headerImage = (CircleImageView) header.findViewById(R.id.navigation_header_image);
        headerTextView = (TextView) header.findViewById(R.id.header_name);


        File file = new File("src/main/java/com/wefriend/wefriend/Data/radar_search.gif");
        Glide.with(this).asGif().load(R.drawable.radar_search).dontTransform().into(searchMoreImageView);

        Menu drawerMenu = drawerNavigationView.getMenu();
        MenuItem drawerMenuItem = drawerMenu.getItem(0);
        toggle = new ActionBarDrawerToggle(this, drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        drawerMenuItem.setChecked(true);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        drawerNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.ic_setting:
                        Intent intent= new Intent(MainActivity.this, SettingActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.ic_profile:
                        Intent intent1 = new Intent(MainActivity.this, EditAccountActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.ic_matched:
                        Intent intent2 = new Intent(MainActivity.this, FriendsActivity.class);
                        FriendsActivity.friendsList = friendsList;
                        FriendsActivity.UID = UID;
                        startActivity(intent2);
                        break;
                    case R.id.ic_search:
                        Intent intent3 = new Intent(MainActivity.this, SearchFilterActivity.class);
                        startActivity(intent3);
                        break;
                }
                return false;
            }
        });
        if(mAccount != null){
            if (mAccount.getProfileImage()!=null) {
                String mProfileUrl = mAccount.getProfileImage();
                Glide.with(this).load(mProfileUrl).fitCenter()
                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).into(headerImage);
            }
            if (mAccount.getLast_name()!=null){
                headerTextView.setText(mAccount.getFirst_name()+" "+mAccount.getLast_name());
            }
        }

        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);
        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                items.remove(0);
                arrayAdapter.notifyDataSetChanged();
                if (items.size() ==0){
                    searchMoreLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                Account account = (Account) dataObject;
                String userID = account.getUserID();
                String firstname = account.getFirst_name();
                DocumentReference documentReference = userRef.collection("connection").document(userID);
                documentReference.update("like","no");
                documentReference.update("matched","no");
                //documentReference.update("dislike",true);
                makeToast(MainActivity.this, "Dislike!");
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Account account = (Account) dataObject;
                final String userID = account.getUserID();
                String firstname = account.getFirst_name();
                if (userID!=null) {
                    final DocumentReference documentReference = userRef.collection("connection").document(userID);
                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Log.d("Right Swipe", "DocumentSnapshot data: " + document.getData());
                                    documentReference.update("like", "yes");
                                    isConnectionMatch(userID);

                                } else {
                                    Log.d("Right Swipe", "No such document");
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("like","yes");
                                    data.put("matched","no");
                                    documentReference.set(data);
                                }
                            }
                        }
                    });

                    final DocumentReference swipeDocumentReference = FirebaseFirestore.getInstance()
                            .collection("users").document(userID).collection("connection").document(UID);
                    swipeDocumentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Log.d("Right Swipe", "DocumentSnapshot data: " + document.getData());
                                    String isLiked = document.get("like").toString();
                                    if (isLiked.equals("yes")) {
                                        documentReference.update("matched", "yes");
                                        swipeDocumentReference.update("matched", "yes");
                                        try {
                                            Thread.sleep(1000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        isConnectionMatch(userID);
                                    } else {
                                        documentReference.update("matched", "no");
                                        swipeDocumentReference.update("matched", "no");
                                    }
                                } else {
                                    Log.d("Right Swipe", "No such document");
                                }
                            } else {
                                Log.d("Right Swipe", "get failed with ", task.getException());
                            }
                        }
                    });

                    makeToast(MainActivity.this, "Like!");
                }
                if (items.size() == 0){
                    likeButton.setVisibility(View.INVISIBLE);
                    dislikeButton.setVisibility(View.INVISIBLE);
                }
                else{
                    likeButton.setVisibility(View.VISIBLE);
                    dislikeButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
                //items.add(new Account("End",String.valueOf(i),null,null,null,null));
                //arrayAdapter.notifyDataSetChanged();
                Log.d("LIST", "notified");
                //searchMoreLayout.setVisibility(View.VISIBLE);
                //i++;
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Account thisAccount = (Account) dataObject;
                makeToast(MainActivity.this, "Clicked!");
                Intent intent = new Intent(MainActivity.this,CheckUserInfoActivity.class);
                CheckUserInfoActivity.account = thisAccount;
                startActivity(intent);
            }
        });

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //your method
                SearchDatabase mSearch = new SearchDatabase();
                mSearch.resetArray();
                mSearch.searchCollege(college,UID);
                mSearch.getUserAccount(UID);
                mSearch.getMatched(UID);
                mSearch.getSearchedID(UID);
                mSearch.getMatched(UID);
            }
        }, 0, 10000);

        if (arrayAdapter.isEmpty()){
            searchMoreLayout.setVisibility(View.VISIBLE);
            likeButton.setVisibility(View.INVISIBLE);
            dislikeButton.setVisibility(View.INVISIBLE);
        }


    }
    public void likeOnClick(View v) {
        //int id = blankFragment.getProjectId();
        Account account = items.get(0);
        final String userID = account.getUserID();
        String firstname = account.getFirst_name();
        if (userID!=null) {
            final DocumentReference documentReference = userRef.collection("connection").document(userID);
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("Right Swipe", "DocumentSnapshot data: " + document.getData());
                            documentReference.update("like", "yes");
                            isConnectionMatch(userID);

                        } else {
                            Log.d("Right Swipe", "No such document");
                            Map<String, Object> data = new HashMap<>();
                            data.put("like","yes");
                            data.put("matched","no");
                            documentReference.set(data);
                        }
                    }
                }
            });

            final DocumentReference swipeDocumentReference = FirebaseFirestore.getInstance()
                    .collection("users").document(userID).collection("connection").document(UID);
            swipeDocumentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("Right Swipe", "DocumentSnapshot data: " + document.getData());
                            String isLiked = document.get("like").toString();
                            if (isLiked.equals("yes")) {
                                documentReference.update("matched", "yes");
                                swipeDocumentReference.update("matched", "yes");
                            } else {
                                documentReference.update("matched", "no");
                                swipeDocumentReference.update("matched", "no");
                            }
                        } else {
                            Log.d("Right Swipe", "No such document");
                            swipeDocumentReference.update("matched","no");
                        }
                    } else {
                        Log.d("Right Swipe", "get failed with ", task.getException());
                    }
                }
            });

            makeToast(MainActivity.this, "Like!");
        }

        items.remove(0);
        arrayAdapter.notifyDataSetChanged();
        if (items.size() == 0){
            searchMoreLayout.setVisibility(View.VISIBLE);
            likeButton.setVisibility(View.INVISIBLE);
            dislikeButton.setVisibility(View.INVISIBLE);
        }
        else{
            likeButton.setVisibility(View.VISIBLE);
            dislikeButton.setVisibility(View.VISIBLE);
        }

    }

    public void dislikeOnClick(View v) {
        //int id = blankFragment.getProjectId();
        Account account = items.get(0);
        String userID = account.getUserID();
        String firstname = account.getFirst_name();
        DocumentReference documentReference = userRef.collection("connection").document(userID);
        documentReference.update("like","no");
        documentReference.update("matched","no");
        //documentReference.update("dislike",true);
        makeToast(MainActivity.this, "Dislike!");
        items.remove(0);
        arrayAdapter.notifyDataSetChanged();
        if (items.size() ==0){
            searchMoreLayout.setVisibility(View.VISIBLE);
            likeButton.setVisibility(View.INVISIBLE);
            dislikeButton.setVisibility(View.INVISIBLE);
        }
        else{
            searchMoreLayout.setVisibility(View.INVISIBLE);
            likeButton.setVisibility(View.VISIBLE);
            dislikeButton.setVisibility(View.VISIBLE);
        }
    }

    static void makeToast(Context ctx, String s){
        Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
    }

    public void setCollege(String college){
        this.college = college;
    }

    public void isConnectionMatch(final String userID) {
        final DocumentReference UserConnectionDb = FirebaseFirestore.getInstance().collection("users").document(userID)
                .collection("connection").document(UID);
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
                        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                        mColl.document(key).set(newChat);
                        Map chatID = new HashMap();
                        chatID.put("ChatID", key);
                        //chatID.put("last_active",timeStamp);
                        userRef.collection("connection").document(userID).update(chatID);
                        FirebaseFirestore.getInstance().collection("users").document(userID)
                                .collection("connection").document(UID).update(chatID);
                    }
                }
            }
        });
    }

    public void setPreference(String preference) {
        this.preference = preference;
    }

    public String getPreference() {
        return preference;
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
        arrayAdapter.notifyDataSetChanged();
    }


    private List<Account> searchFilter(List<Account> list){
        items = new ArrayList<>();
        switch (preference){
            case "0": //don't display matched users
                List<String> filter = matchedID;
                for(Account account: list){
                    if(account.getUserID()!=null && !filter.contains(account.getUserID()) && !items.contains(account)){
                        items.add(account);
                    }
                }

                return items;
            case "1": // don't display searched users
                List<String> filter1 = MainActivity.searchedID;
                for(Account account: list){
                    if(account.getUserID()!=null && !filter1.contains(account.getUserID())){
                        items.add(account);
                    }
                }
                return items;
            case "2": //display different gender only
                for(Account account: list){
                    if(!(account.getGender()).equals(mAccount.getGender()) &&
                            account.getUserID()!=null && !account.getUserID().equals("")){
                        items.add(account);
                    }
                }
                return items;

            case "3":
                for(Account account: list){
                    if((account.getGender()).equals(mAccount.getGender()) &&
                            account.getUserID()!=null && !account.getUserID().equals("")){
                        items.add(account);
                    }
                }
                return items;

            case "4":
                return list;
        }
        return items;
    }
    public static void clearAllValues(){
        MainActivity.mAccount = null;
        MainActivity.friendsList = null;
        MainActivity.matchedID = null;
        MainActivity.searchedID = null;
        MainActivity.college = null;
        MainActivity.chatIDList = null;
    }

}
