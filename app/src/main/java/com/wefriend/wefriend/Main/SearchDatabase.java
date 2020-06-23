package com.wefriend.wefriend.Main;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
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
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.wefriend.wefriend.Main.Friends.Chat;
import com.wefriend.wefriend.Main.Friends.ChatActivity;
import com.wefriend.wefriend.Main.Friends.ChatAdapter;
import com.wefriend.wefriend.R;
import com.wefriend.wefriend.account.Account;
import com.wefriend.wefriend.login.LoginActivity;
import com.wefriend.wefriend.login.RegisterGenderActivity;

import java.util.ArrayList;
import java.util.List;

public class SearchDatabase {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionReference;
    private ArrayList<Account> search_result;
    private String UID;
    private String college;
    private Account mAccount;
    StorageReference imageReference;
    FirebaseFirestoreSettings settings;
    private ArrayList<String> searchedList;
    private ArrayList<String> matchedList;
    public SearchDatabase(){
        mAuth = FirebaseAuth.getInstance();
        settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("school");
        //StorageReference filepath = FirebaseStorage.getInstance().getReference().child("Profile_image").child(userId);
        imageReference = FirebaseStorage.getInstance().getReference().child("Profile_image");
        //mFirebaseFirestore.setFirestoreSettings(settings);
        mCollectionReference = mFirebaseFirestore.collection("users");
        searchedList = new ArrayList<>();
        matchedList = new ArrayList<>();
        mAccount = new Account(null,null,null,null,null,
                null,null,null,null);
    }
    public void resetArray(){
        search_result=new ArrayList<>();
    }


    public void searchCollege(String college, String UID){
        this.UID = UID;
        this.college = college;
        Query query = mCollectionReference.whereEqualTo("school", college);
        searchCollegeHelper(query);
    }

    public Account getAccount() {
        return mAccount;
    }

    public void setAccount(Account mAccount) {
        this.mAccount = mAccount;
    }

    public void searchCollegeHelper(Query query){
        final String Uid = UID;

        //search_result.add(new Account(null,null,null,null,null,null,null,null));
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("Search College", "listen:error", e);
                    return;
                }
                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            Log.d("Search College", "New college: " + dc.getDocument().getData().get("Last_name"));
                            String first_name = dc.getDocument().get("First_name").toString();
                            String last_name = dc.getDocument().get("Last_name").toString();
                            String school = dc.getDocument().get("school").toString();
                            String gender = dc.getDocument().get("Gender").toString();
                            String major = dc.getDocument().get("major").toString();
                            String aboutMe = null;
                            if(dc.getDocument().get("introduction") != null){
                                aboutMe = dc.getDocument().get("introduction").toString();
                            }
                            String baseId = dc.getDocument().getId();
                            String profileImage = null;
                            if(dc.getDocument().get("profileImageUrl") != null){
                                profileImage = dc.getDocument().get("profileImageUrl").toString();
                            }
                            Timestamp birthday = dc.getDocument().getTimestamp("birth_day");
                            if (!Uid.equals(baseId)){
                                Account account = new Account(null,null,
                                        null,null,null,null,
                                        null,null,null);
                                account.setBirth_day(birthday);
                                account.setFirst_name(first_name);
                                account.setLast_name(last_name);
                                account.setGender(gender);
                                account.setMajor(major);
                                account.setSchool(school);
                                account.setUserID(baseId);
                                account.setProfileImage(profileImage);
                                account.setAboutMe(aboutMe);
                                search_result.add(account);
                                BufferActivity.initial_search = search_result;
                            }
                            break;
                        case MODIFIED:
                            Log.d("Search College", "Modified college: " + dc.getDocument().getData());
                            break;
                        case REMOVED:
                            Log.d("Search College", "Removed college: " + dc.getDocument().getData());
                            break;
                    }
                }
            }
        });
    }
    public ArrayList<Account> getMatched(String UID){
        final String Uid = UID;
        CollectionReference mCollectionReference = mFirebaseFirestore.collection("users").document(UID).collection("connection");
        Query query = mCollectionReference.whereEqualTo("matched","yes");
        final ArrayList<Account> matchedList = new ArrayList<>();
        final List<String> mMatchedId = new ArrayList<>();
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("Search Matchde", "listen:error", e);
                    return;
                }
                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            Log.d("Search College", "New college: " + dc.getDocument().getData());
                            String id = dc.getDocument().getId();

                            mMatchedId.add(id);
                            MainActivity.matchedID=mMatchedId;

                            if(dc.getDocument().get("ChatID") != null){
                                String chatID = dc.getDocument().get("ChatID").toString();
                                MainActivity.chatIDList.put(id,chatID);
                            }
                            DocumentReference documentReference = mFirebaseFirestore.collection("users").document(id);
                            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Log.d("Search Match", "DocumentSnapshot data: " + document.getData());
                                            String first_name = document.get("First_name").toString();
                                            String last_name = document.get("Last_name").toString();
                                            String school = document.get("school").toString();
                                            String gender = document.get("Gender").toString();
                                            String major = document.get("major").toString();
                                            String baseId = document.getId();
                                            String aboutMe = null;
                                            if(document.get("introduction") != null){
                                                aboutMe = document.get("introduction").toString();
                                            }
                                            String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                            Timestamp birthday = document.getTimestamp("birth_day");
                                            String profileImage = null;
                                            if(document.get("profileImageUrl") != null){
                                                profileImage = document.get("profileImageUrl").toString();
                                            }

                                            Account account = new Account(null,
                                                    null,null,null,
                                                    null,null,null,null,null);
                                            account.setBirth_day(birthday);
                                            account.setFirst_name(first_name);
                                            account.setLast_name(last_name);
                                            account.setGender(gender);
                                            account.setMajor(major);
                                            account.setSchool(school);
                                            account.setUserID(baseId);
                                            account.setProfileImage(profileImage);
                                            //setAccount(account);
                                            matchedList.add(account);
                                            BufferActivity.matchedID.add(baseId);
                                            }else{
                                            Log.d("Search Match", "No Such Document");
                                        }
                                    } else {
                                        Log.d("Search Match", "get failed with ", task.getException());
                                    }
                                }
                            });



                            break;
                        case MODIFIED:
                            Log.d("Search College", "Modified college: " + dc.getDocument().getData());
                            break;
                        case REMOVED:
                            Log.d("Search College", "Removed college: " + dc.getDocument().getData());
                            break;
                    }
                }
            }
        });
        return matchedList;
    }


    public ArrayList<Account> getSearch_result() {
        //search_result.remove(0);
        return search_result;
    }


    public String checkChatID(String UID, String userID) {
        final String[] ChatID = new String[1];
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(UID)
                .collection("connection").document(userID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("Check ChatID", "DocumentSnapshot data: " + document.getData());
                        ChatActivity.ChatID = document.get("ChatID").toString();
                        ChatID[0] = document.get("ChatID").toString();
                    } else {
                        Log.d("Check ChatID", "No such document");
                    }
                } else {
                    Log.d("Check ChatID", "get failed with ", task.getException());
                }
            }
        });
        return ChatID[0];
    }

    public List<Chat> getChat(final String mUID, final String ChatID){
        CollectionReference reference = FirebaseFirestore.getInstance().collection("chats");
        final List<Chat>[] chatList = new List[]{new ArrayList<>()};
        if(ChatID != null && !ChatID.equals("")){
            CollectionReference mColl =  reference.document(ChatID).collection(mUID);

            final List<Chat> finalChatList = chatList[0];
            mColl.orderBy("time").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
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
                                    finalChatList.add(chat);
                                } else{
                                    Chat chat = new Chat(sender, receiver,message,null);
                                    finalChatList.add(chat);
                                }
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

                }
            });
        }
        return chatList[0];
    }

    public void getMatchedID(String Uid){
        final String UID = Uid;
        CollectionReference mCollectionReference = mFirebaseFirestore.collection("users").document(UID).collection("connection");
        Query query = mCollectionReference.whereEqualTo("matched","yes");
        final ArrayList<String> matchedListID = new ArrayList<>();
        matchedListID.add("");
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("Search Matched", "listen:error", e);
                    return;
                }
                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            Log.d("Search Matched ID", "Matched: " + dc.getDocument().getData());
                            String id = dc.getDocument().getId();
                            matchedListID.add(id);
                            MainActivity.matchedID = matchedListID;
                            BufferActivity.matchedID = matchedListID;
                            matchedList = matchedListID;
                    }
                }
            }
        });
    }

    public ArrayList<String> getMatchedList() {
        return matchedList;
    }

    public void getSearchedID(String Uid){
        final String UID = Uid;
        searchedList.add("");
        CollectionReference mCollectionReference = mFirebaseFirestore.collection("users").document(UID).collection("connection");
        mCollectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("Get Searched ID", document.getId() + " => " + document.getData());
                        searchedList.add(document.getId());
                        MainActivity.searchedID = searchedList;
                        BufferActivity.searchedID=searchedList;
                    }
                } else {
                    Log.d("Get Searched ID", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public ArrayList<String> getSearchedList() {
        return searchedList;
    }

    public Account getUserAccount(String UID){
        DocumentReference mAccount = mCollectionReference.document(UID);
        final Account account = new Account(null,null,null,null,
                null,null,null,null,null);
        mAccount.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("My Account", "DocumentSnapshot data: " + document.getData());
                        String first_name = document.get("First_name").toString();
                        String last_name = document.get("Last_name").toString();
                        String school = document.get("school").toString();
                        String gender = document.get("Gender").toString();
                        String major = document.get("major").toString();
                        String baseId = document.getId();
                        String aboutMe = null;
                        if(document.get("introduction") != null){
                            aboutMe = document.get("introduction").toString();
                        }
                        String profileImage = null;
                        if(document.get("profileImageUrl") != null){
                            profileImage = document.get("profileImageUrl").toString();
                        }
                        Timestamp birthday = document.getTimestamp("birth_day");
                        account.setBirth_day(birthday);
                        account.setFirst_name(first_name);
                        account.setLast_name(last_name);
                        account.setGender(gender);
                        account.setMajor(major);
                        account.setSchool(school);
                        account.setUserID(baseId);
                        account.setProfileImage(profileImage);
                        account.setAboutMe(aboutMe);
                        setAccount(account);
                        MainActivity.mAccount = account;
                        BufferActivity.mAccount = account;
                    } else {
                        Log.d("My Account", "No such document");
                    }
                }
            }
        });
        return account;
    }


}
